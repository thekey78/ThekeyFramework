package bank.demo.common.stage;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import pe.kr.thekey.framework.core.error.StandardException;
import pe.kr.thekey.framework.core.pipeline.Phase;
import pe.kr.thekey.framework.core.pipeline.Stage;
import pe.kr.thekey.framework.core.pipeline.StageContext;

@Slf4j
public class RequestDdosTokenStage implements Stage {
    @Override
    public String id() {
        return "token-validation";
    }

    @Override
    public Phase phase() {
        return Phase.INPUT;
    }

    @Override
    public void execute(StageContext ctx) {
        log.debug("Executing DDOS Token Validation Stage");

        HttpServletRequest request = ctx.request();
        HttpSession session = request.getSession();
        String requestToken = request.getHeader(Constants.DDOS_TOKEN_NAME);
        if (session.getAttribute(Constants.DDOS_TOKEN_NAME) != null) {
            Object attribute = session.getAttribute(Constants.DDOS_TOKEN_NAME);
            if(!attribute.equals(requestToken)) {
                throw new StandardException("F0000", "Invalid token");
            }
        }
    }
}
