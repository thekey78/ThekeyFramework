package pe.kr.thekey.framework.starter.web;

import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import pe.kr.thekey.framework.core.context.RequestContext;
import pe.kr.thekey.framework.core.context.RequestContextFactory;
import pe.kr.thekey.framework.web.servlet.impl.ThekeyHttpServletRequestWrapper;

public class DefaultRequestContextFactory implements RequestContextFactory {

    @Override
    public RequestContext create(@NonNull HttpServletRequest request) {
        if (request instanceof ThekeyHttpServletRequestWrapper) {
            return new DefaultRequestContext((ThekeyHttpServletRequestWrapper) request);
        }
        return new DefaultRequestContext(new ThekeyHttpServletRequestWrapper(request));
    }
}
