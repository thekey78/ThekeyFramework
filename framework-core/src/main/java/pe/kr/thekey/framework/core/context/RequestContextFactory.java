package pe.kr.thekey.framework.core.context;

import jakarta.servlet.http.HttpServletRequest;

public interface RequestContextFactory {
    RequestContext create(HttpServletRequest request);
}
