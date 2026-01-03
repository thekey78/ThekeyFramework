package bank.demo.was.context;

import pe.kr.thekey.framework.starter.web.DefaultRequestContext;
import pe.kr.thekey.framework.web.servlet.ThekeyHttpServletRequest;

import java.time.Instant;
import java.util.Enumeration;

public class BankWasDemoRequestContext extends DefaultRequestContext {
    public BankWasDemoRequestContext(ThekeyHttpServletRequest request) {
        super(request);
    }

    public boolean e2eDecryptionApplied() {
        for(Enumeration<String> parameterNames = request.getParameterNames(); parameterNames.hasMoreElements();) {
            String parameterName = parameterNames.nextElement();
            if(parameterName.startsWith("E2E_")) {
                return true;
            }
        }
        return false;
    }

    public boolean signatureVerified() {
        for(Enumeration<String> parameterNames = request.getParameterNames(); parameterNames.hasMoreElements();) {
            String parameterName = parameterNames.nextElement();
            if(parameterName.startsWith("SIGNATURE_")) {
                return true;
            }
        }
        return false;
    }

    public TokenStatus ddosTokenStatus() {
        Object ddosTokenStatus = request.getAttribute("DdosTokenStatus");
        if(ddosTokenStatus == null) {
            return TokenStatus.NONE;
        }
        return TokenStatus.valueOf(ddosTokenStatus.toString());
    }

    public Instant startTime() {
        return Instant.ofEpochMilli(System.currentTimeMillis());
    }

}
