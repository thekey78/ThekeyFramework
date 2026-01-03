package pe.kr.thekey.framework.web.condition;

import org.springframework.util.AntPathMatcher;
import pe.kr.thekey.framework.core.pipeline.StageContext;

public final class PrimitiveFunctions {
    private final AntPathMatcher ant = new AntPathMatcher();

    public boolean path(StageContext c, String antPattern) {
        return ant.match(antPattern, c.request().getRequestURI());
    }

    public boolean methodIn(StageContext c, String... methods) {
        String m = c.request().getMethod();
        for (String x : methods) if (x.equalsIgnoreCase(m)) return true;
        return false;
    }

    public boolean headerExists(StageContext c, String name) {
        return c.request().getHeader(name) != null;
    }

    public String header(StageContext c, String name) {
        return c.request().getHeader(name);
    }

    public boolean channel(StageContext c, String channel) {
        return channel.equalsIgnoreCase(c.requestContext().channel());
    }

    public boolean principal(StageContext c) {
        String p = c.requestContext().principalId();
        return p != null && !p.isBlank();
    }
}
