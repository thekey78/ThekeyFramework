package bank.demo.common.stage;

import lombok.extern.slf4j.Slf4j;
import pe.kr.thekey.framework.core.pipeline.Phase;
import pe.kr.thekey.framework.core.pipeline.Stage;
import pe.kr.thekey.framework.core.pipeline.StageContext;

@Slf4j
public class RequestXssStage implements Stage {
    @Override
    public String id() {
        return "xss-validation";
    }

    @Override
    public Phase phase() {
        return Phase.INPUT;
    }

    @Override
    public void execute(StageContext ctx) {
        //ctx.request().setE2E(true);
        log.debug("Executing XSS validation stage");
    }
}
