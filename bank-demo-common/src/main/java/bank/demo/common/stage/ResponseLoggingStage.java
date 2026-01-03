package bank.demo.common.stage;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import pe.kr.thekey.framework.core.pipeline.Phase;
import pe.kr.thekey.framework.core.pipeline.Stage;
import pe.kr.thekey.framework.core.pipeline.StageContext;

@Slf4j
public class ResponseLoggingStage implements Stage {
    @Override
    public String id() {
        return "response-logging";
    }

    @Override
    public Phase phase() {
        return Phase.OUTPUT;
    }

    @Override
    public void execute(StageContext ctx) {
        HttpServletResponse response = ctx.response();
        int status = response.getStatus();
        String businessToken = response.getHeader(Constants.BUSINESS_TOKEN_NAME);
        log.info("Response {}:{}", businessToken, status);
    }
}
