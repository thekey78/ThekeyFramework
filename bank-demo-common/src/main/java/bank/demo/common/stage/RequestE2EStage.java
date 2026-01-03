package bank.demo.common.stage;

import lombok.extern.slf4j.Slf4j;
import pe.kr.thekey.framework.core.pipeline.Phase;
import pe.kr.thekey.framework.core.pipeline.Stage;
import pe.kr.thekey.framework.core.pipeline.StageContext;

@Slf4j
public class RequestE2EStage implements Stage {
    @Override
    public String id() {
        return "e2e-stage";
    }

    @Override
    public Phase phase() {
        return Phase.INPUT;
    }

    @Override
    public void execute(StageContext ctx) {
        log.debug("Executing E2E Stage");
    }
}
