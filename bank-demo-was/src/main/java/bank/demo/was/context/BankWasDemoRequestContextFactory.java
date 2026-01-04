package bank.demo.was.context;

import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import pe.kr.thekey.framework.core.context.RequestContext;
import pe.kr.thekey.framework.core.context.RequestContextFactory;
import pe.kr.thekey.framework.web.servlet.impl.ThekeyHttpServletRequestWrapper;

public class BankWasDemoRequestContextFactory implements RequestContextFactory {
    @Override
    public RequestContext create(@NonNull HttpServletRequest request) {
        if (request instanceof ThekeyHttpServletRequestWrapper) {
            return new BankWasDemoRequestContext((ThekeyHttpServletRequestWrapper) request);
        }
        return new BankWasDemoRequestContext(new ThekeyHttpServletRequestWrapper(request));
    }
}
