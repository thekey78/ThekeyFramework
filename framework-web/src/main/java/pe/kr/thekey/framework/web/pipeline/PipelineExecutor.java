package pe.kr.thekey.framework.web.pipeline;

import lombok.extern.slf4j.Slf4j;
import pe.kr.thekey.framework.core.error.StandardException;
import pe.kr.thekey.framework.core.pipeline.StageContext;

import java.util.List;

@Slf4j
public final class PipelineExecutor {
    public void execute(List<ExecutableStage> chain, StageContext ctx) {
        for (ExecutableStage es : chain) {
            StageDefinition def = es.def();
            if (!def.enable()) continue;
            if (!def.condition().matches(ctx)) continue;

            try {
                es.stage().execute(ctx);
            } catch (Exception ex) {
                if (def.failurePolicy() == StageDefinition.FailurePolicy.FAIL_OPEN) {
                    // TODO: log/audit 후 continue
                    log.error(ex.getMessage(), ex);
                    continue;
                }
                if (ex instanceof StandardException)
                    throw ex;
                throw new StandardException("PIPELINE_EXECUTION_ERROR", ex.getMessage(), ex);
            }
        }
    }
}
