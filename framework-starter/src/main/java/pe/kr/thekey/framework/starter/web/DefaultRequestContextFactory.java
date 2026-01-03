package pe.kr.thekey.framework.starter.web;

import jakarta.servlet.http.HttpServletRequest;
import pe.kr.thekey.framework.core.context.RequestContext;
import pe.kr.thekey.framework.core.context.RequestContextFactory;
import pe.kr.thekey.framework.web.servlet.impl.ThekeyHttpServletRequestWrapper;

public class DefaultRequestContextFactory implements RequestContextFactory {

    @Override
    public RequestContext create(HttpServletRequest request) {
        return new DefaultRequestContext(new ThekeyHttpServletRequestWrapper(request));
    }
}
