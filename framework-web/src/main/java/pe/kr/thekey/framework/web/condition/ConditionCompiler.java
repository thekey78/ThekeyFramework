package pe.kr.thekey.framework.web.condition;

import pe.kr.thekey.framework.core.pipeline.StageContext;
import pe.kr.thekey.framework.web.pipeline.StageDefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * DSL -> StageDefinition.Condition 컴파일러
 * <p>
 * 지원:
 *  - &&, ||, !, (), ==, !=
 *  - path('...'), methodIn('A','B'), header('X'), headerValue('X'), channel('APP'), principal()
 * </p>
 */
public final class ConditionCompiler {

    private final PrimitiveFunctions f;

    public ConditionCompiler(PrimitiveFunctions f) {
        this.f = Objects.requireNonNull(f);
    }

    public StageDefinition.Condition compile(String expr) {
        if (expr == null || expr.isBlank()) return ctx -> true;
        return new SimpleParser(f, expr).parse();
    }

    // -----------------------------
    // Parser
    // Grammar (recursive descent):
    //   expr        := orExpr
    //   orExpr      := andExpr ( '||' andExpr )*
    //   andExpr     := unaryExpr ( '&&' unaryExpr )*
    //   unaryExpr   := '!' unaryExpr | primary
    //   primary     := '(' expr ')' | predicate
    //   predicate   := boolCall ( ( '==' | '!=' ) stringLiteral )?
    //               | valueCall ( ( '==' | '!=' ) stringLiteral )   // valueCall must be compared
    //
    //   boolCall    := path(...) | methodIn(...) | header(...) | channel(...) | principal()
    //   valueCall   := headerValue(...)
    // -----------------------------
    static final class SimpleParser {
        private final PrimitiveFunctions f;
        private final List<Token> tokens;
        private int pos = 0;

        SimpleParser(PrimitiveFunctions f, String input) {
            this.f = f;
            this.tokens = new Lexer(input).lex();
        }

        StageDefinition.Condition parse() {
            Node root = parseExpr();
            expect(TokenType.EOF);
            return root::eval;
        }

        private Node parseExpr() {
            return parseOr();
        }

        private Node parseOr() {
            Node left = parseAnd();
            while (match(TokenType.OR_OR)) {
                Node right = parseAnd();
                left = new Or(left, right);
            }
            return left;
        }

        private Node parseAnd() {
            Node left = parseUnary();
            while (match(TokenType.AND_AND)) {
                Node right = parseUnary();
                left = new And(left, right);
            }
            return left;
        }

        private Node parseUnary() {
            if (match(TokenType.BANG)) {
                return new Not(parseUnary());
            }
            return parsePrimary();
        }

        private Node parsePrimary() {
            if (match(TokenType.LPAREN)) {
                Node inner = parseExpr();
                expect(TokenType.RPAREN);
                return inner;
            }
            return parsePredicate();
        }

        private Node parsePredicate() {
            // 함수 호출
            Call call = parseCall();

            // 비교 연산이 붙는지 확인
            if (peekType(TokenType.EQ_EQ) || peekType(TokenType.BANG_EQ)) {
                Token op = advance();
                String rhs = parseStringLiteral();

                if (call.kind == CallKind.BOOL_CALL) {
                    // boolCall == 'Y' 같은 형태는 허용하지 않음 (오타 방지)
                    throw error("Boolean function cannot be compared with string. Use headerValue(...) for value comparisons.");
                }

                ValueExpr left = (ValueExpr) call.expr;
                boolean equals = (op.type == TokenType.EQ_EQ);
                return new Compare(left, rhs, equals);
            }

            // 비교가 없으면 boolCall만 허용
            if (call.kind == CallKind.VALUE_CALL) {
                throw error("Value function must be compared. Example: headerValue('X') == 'Y'");
            }

            return (Node) call.expr;
        }

        private Call parseCall() {
            Token name = expect(TokenType.IDENT);
            expect(TokenType.LPAREN);

            String fn = name.text;

            // principal()은 인자 없음
            if ("principal".equals(fn)) {
                expect(TokenType.RPAREN);
                return new Call(CallKind.BOOL_CALL, (Node) (f::principal));
            }

            // 나머지는 문자열 인자들
            List<String> args = new ArrayList<>();
            if (!peekType(TokenType.RPAREN)) {
                args.add(parseStringLiteral());
                while (match(TokenType.COMMA)) {
                    args.add(parseStringLiteral());
                }
            }
            expect(TokenType.RPAREN);

            // 함수 매핑
            return switch (fn) {
                case "path" -> {
                    requireArgCount(fn, args, 1);
                    yield new Call(CallKind.BOOL_CALL, (Node) (ctx -> f.path(ctx, args.get(0))));
                }
                case "methodIn" -> {
                    requireMinArgCount(fn, args, 1);
                    yield new Call(CallKind.BOOL_CALL, (Node) (ctx -> f.methodIn(ctx, args.toArray(new String[0]))));
                }
                case "header" -> {
                    requireArgCount(fn, args, 1);
                    yield new Call(CallKind.BOOL_CALL, (Node) (ctx -> f.headerExists(ctx, args.get(0))));
                }
                case "channel" -> {
                    requireArgCount(fn, args, 1);
                    yield new Call(CallKind.BOOL_CALL, (Node) (ctx -> f.channel(ctx, args.get(0))));
                }
                case "headerValue" -> {
                    requireArgCount(fn, args, 1);
                    yield new Call(CallKind.VALUE_CALL, (ValueExpr) (ctx -> f.header(ctx, args.get(0))));
                }
                default -> throw error("Unknown function: " + fn);
            };
        }

        private static void requireArgCount(String fn, List<String> args, int n) {
            if (args.size() != n) throw new IllegalArgumentException(fn + " requires " + n + " argument(s).");
        }

        private static void requireMinArgCount(String fn, List<String> args, int n) {
            if (args.size() < n) throw new IllegalArgumentException(fn + " requires at least " + n + " argument(s).");
        }

        private String parseStringLiteral() {
            Token t = expect(TokenType.STRING);
            return t.text;
        }

        // -------- token helpers --------
        private boolean match(TokenType t) {
            if (peekType(t)) {
                pos++;
                return true;
            }
            return false;
        }

        private boolean peekType(TokenType t) {
            return tokens.get(pos).type == t;
        }

        private Token advance() {
            return tokens.get(pos++);
        }

        private Token expect(TokenType t) {
            Token tok = tokens.get(pos);
            if (tok.type != t) {
                throw error("Expected " + t + " but found " + tok.type + " at position " + tok.pos);
            }
            pos++;
            return tok;
        }

        private RuntimeException error(String msg) {
            Token tok = tokens.get(Math.min(pos, tokens.size() - 1));
            return new IllegalArgumentException(msg + " (near '" + tok.raw + "' @ " + tok.pos + ")");
        }
    }

    // -----------------------------
    // AST
    // -----------------------------
    @FunctionalInterface
    interface Node { boolean eval(StageContext ctx); }

    @FunctionalInterface
    interface ValueExpr { String eval(StageContext ctx); }

    record And(Node l, Node r) implements Node {
        public boolean eval(StageContext ctx) { return l.eval(ctx) && r.eval(ctx); }
    }

    record Or(Node l, Node r) implements Node {
        public boolean eval(StageContext ctx) { return l.eval(ctx) || r.eval(ctx); }
    }

    record Not(Node n) implements Node {
        public boolean eval(StageContext ctx) { return !n.eval(ctx); }
    }

    record Compare(ValueExpr left, String right, boolean equals) implements Node {
        public boolean eval(StageContext ctx) {
            String lv = left.eval(ctx);
            boolean eq = Objects.equals(lv, right);
            return equals == eq;
        }
    }

    enum CallKind { BOOL_CALL, VALUE_CALL }
    record Call(CallKind kind, Object expr) {}

    // -----------------------------
    // Lexer
    // -----------------------------
    enum TokenType {
        IDENT, STRING,
        LPAREN, RPAREN, COMMA,
        AND_AND, OR_OR, BANG,
        EQ_EQ, BANG_EQ,
        EOF
    }

    static final class Token {
        final TokenType type;
        final String text; // IDENT name or STRING content (unquoted)
        final int pos;
        final String raw;  // raw lexeme for error

        Token(TokenType type, String text, int pos, String raw) {
            this.type = type;
            this.text = text;
            this.pos = pos;
            this.raw = raw;
        }
    }

    static final class Lexer {
        private final String s;
        private final int n;
        private int i = 0;

        Lexer(String s) {
            this.s = s;
            this.n = s.length();
        }

        List<Token> lex() {
            List<Token> out = new ArrayList<>();
            while (true) {
                skipWs();
                if (i >= n) break;

                int start = i;
                char c = s.charAt(i);

                // Operators / punctuation
                if (c == '(') { out.add(tok(TokenType.LPAREN, "(", start, "(")); i++; continue; }
                if (c == ')') { out.add(tok(TokenType.RPAREN, ")", start, ")")); i++; continue; }
                if (c == ',') { out.add(tok(TokenType.COMMA, ",", start, ",")); i++; continue; }

                if (c == '!') {
                    if (peekNext('=')) { out.add(tok(TokenType.BANG_EQ, "!=", start, "!=")); i += 2; }
                    else { out.add(tok(TokenType.BANG, "!", start, "!")); i++; }
                    continue;
                }

                if (c == '=') {
                    if (peekNext('=')) { out.add(tok(TokenType.EQ_EQ, "==", start, "==")); i += 2; continue; }
                    throw new IllegalArgumentException("Unexpected '=' at " + start + ". Use '=='.");
                }

                if (c == '&') {
                    if (peekNext('&')) { out.add(tok(TokenType.AND_AND, "&&", start, "&&")); i += 2; continue; }
                    throw new IllegalArgumentException("Unexpected '&' at " + start + ". Use '&&'.");
                }

                if (c == '|') {
                    if (peekNext('|')) { out.add(tok(TokenType.OR_OR, "||", start, "||")); i += 2; continue; }
                    throw new IllegalArgumentException("Unexpected '|' at " + start + ". Use '||'.");
                }

                // String literal: '...'
                if (c == '\'') {
                    out.add(readString());
                    continue;
                }

                // Ident: [A-Za-z_][A-Za-z0-9_]*
                if (isIdentStart(c)) {
                    out.add(readIdent());
                    continue;
                }

                throw new IllegalArgumentException("Unexpected char '" + c + "' at " + start);
            }

            out.add(new Token(TokenType.EOF, "", n, "<eof>"));
            return out;
        }

        private Token readIdent() {
            int start = i;
            i++; // first char
            while (i < n && isIdentPart(s.charAt(i))) i++;
            String raw = s.substring(start, i);
            return new Token(TokenType.IDENT, raw, start, raw);
        }

        private Token readString() {
            int start = i;
            i++; // skip opening '
            StringBuilder sb = new StringBuilder();
            while (i < n) {
                char c = s.charAt(i);
                if (c == '\\') {
                    if (i + 1 >= n) throw new IllegalArgumentException("Unterminated escape in string at " + i);
                    char nx = s.charAt(i + 1);
                    // minimal escapes
                    if (nx == '\'' || nx == '\\') { sb.append(nx); i += 2; continue; }
                    // keep unknown escape as literal next char
                    sb.append(nx);
                    i += 2;
                    continue;
                }
                if (c == '\'') {
                    i++; // closing
                    String raw = s.substring(start, i);
                    return new Token(TokenType.STRING, sb.toString(), start, raw);
                }
                sb.append(c);
                i++;
            }
            throw new IllegalArgumentException("Unterminated string literal at " + start);
        }

        private void skipWs() {
            while (i < n) {
                char c = s.charAt(i);
                if (c == ' ' || c == '\t' || c == '\n' || c == '\r') i++;
                else break;
            }
        }

        private boolean peekNext(char expected) {
            return (i + 1 < n) && s.charAt(i + 1) == expected;
        }

        private static boolean isIdentStart(char c) {
            return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || c == '_';
        }

        private static boolean isIdentPart(char c) {
            return isIdentStart(c) || (c >= '0' && c <= '9');
        }

        private static Token tok(TokenType t, String text, int pos, String raw) {
            return new Token(t, text, pos, raw);
        }
    }
}
