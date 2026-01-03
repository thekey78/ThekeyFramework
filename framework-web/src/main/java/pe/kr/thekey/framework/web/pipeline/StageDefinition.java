package pe.kr.thekey.framework.web.pipeline;

import pe.kr.thekey.framework.core.pipeline.Phase;
import pe.kr.thekey.framework.core.pipeline.StageContext;

public record StageDefinition(String id, Phase phase, boolean enabled, int order, Condition condition,
                              FailurePolicy failurePolicy) {
    public interface Condition {
        boolean matches(StageContext ctx);
    }

    public enum FailurePolicy {FAIL_CLOSE, FAIL_OPEN}

}
