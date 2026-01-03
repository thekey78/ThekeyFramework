package bank.demo.was.context;

import jakarta.servlet.http.HttpServletRequest;
import pe.kr.thekey.framework.core.context.RequestContext;
import pe.kr.thekey.framework.core.context.RequestContextFactory;
import pe.kr.thekey.framework.web.servlet.impl.ThekeyHttpServletRequestWrapper;

public class BankWasDemoRequestContextFactory implements RequestContextFactory {
    @Override
    public RequestContext create(HttpServletRequest request) {
        return new BankWasDemoRequestContext(new ThekeyHttpServletRequestWrapper(request));
    }
}
