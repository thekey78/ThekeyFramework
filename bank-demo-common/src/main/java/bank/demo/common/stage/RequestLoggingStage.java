package bank.demo.common.stage;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import pe.kr.thekey.framework.core.pipeline.Phase;
import pe.kr.thekey.framework.core.pipeline.Stage;
import pe.kr.thekey.framework.core.pipeline.StageContext;

import java.util.Enumeration;

@Slf4j
public class RequestLoggingStage implements Stage {
    @Override
    public String id() {
        return "request-logging";
    }

    @Override
    public Phase phase() {
        return Phase.INPUT;
    }

    @Override
    public void execute(StageContext ctx) {
        log.debug("Executing Request Logging Stage");
        HttpServletRequest request = ctx.request();
        Enumeration<String> names = request.getParameterNames();
        names.asIterator().forEachRemaining(name -> log.debug("Parameter {}:{}", name, request.getParameter(name)));
    }
}
