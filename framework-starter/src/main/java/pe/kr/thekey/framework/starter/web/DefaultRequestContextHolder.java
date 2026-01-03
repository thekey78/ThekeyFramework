package pe.kr.thekey.framework.starter.web;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;
import pe.kr.thekey.framework.core.context.RequestContext;
import pe.kr.thekey.framework.core.context.RequestContextHolder;

import java.util.Objects;

public class DefaultRequestContextHolder implements RequestContextHolder {
    /**
     * HttpServletRequest attribute key
     */
    public static final String REQ_ATTR_KEY = RequestContext.class.getName();

    private static final ThreadLocal<RequestContext> TL = new ThreadLocal<>();

    private final RequestAttributesProvider requestAttributesProvider;

    public DefaultRequestContextHolder(RequestAttributesProvider requestAttributesProvider) {
        this.requestAttributesProvider = Objects.requireNonNull(requestAttributesProvider);
    }

    @Override
    public RequestContext getRequired() {
        RequestContext ctx = getOrNull();
        if (ctx == null) throw new IllegalStateException("RequestContext is not available in current scope.");
        return ctx;
    }

    @Override
    public RequestContext getOrNull() {
        // 1) Fast path: ThreadLocal
        RequestContext ctx = TL.get();
        if (ctx != null) return ctx;

        // 2) Fallback: request attribute (if present)
        RequestAttributes ra = requestAttributesProvider.getOrNull();
        if (ra instanceof ServletRequestAttributes sra) {
            Object v = sra.getRequest().getAttribute(REQ_ATTR_KEY);
            if (v instanceof RequestContext rc) return rc;
        }
        return null;
    }

    @Override
    public void set(RequestContext ctx) {
        Objects.requireNonNull(ctx, "ctx must not be null");

        // ThreadLocal set
        TL.set(ctx);

        // Request attribute set (if request scope exists)
        RequestAttributes ra = requestAttributesProvider.getOrNull();
        if (ra instanceof ServletRequestAttributes sra) {
            sra.getRequest().setAttribute(REQ_ATTR_KEY, ctx);
        }
    }

    @Override
    public void clear() {
        // ThreadLocal clear
        TL.remove();

        // Request attribute clear (if request scope exists)
        RequestAttributes ra = requestAttributesProvider.getOrNull();
        if (ra instanceof ServletRequestAttributes sra) {
            sra.getRequest().removeAttribute(REQ_ATTR_KEY);
        }
    }

    /**
     * RequestAttributes access abstraction.
     * - Spring의 org.springframework.web.context.request.RequestContextHolder에 직접 의존하지 않게 분리
     * - 테스트 용이
     */
    @FunctionalInterface
    public interface RequestAttributesProvider {
        RequestAttributes getOrNull();
    }
}
