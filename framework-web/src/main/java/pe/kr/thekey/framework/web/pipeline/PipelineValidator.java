package pe.kr.thekey.framework.web.pipeline;

import java.util.List;

public final class PipelineValidator {
    private final List<String> requiredStageIds;

    public PipelineValidator(List<String> requiredStageIds) {
        this.requiredStageIds = requiredStageIds;
    }

    public void validate(PipelinePlan plan, StageRegistry registry) {
        // Plan에 있는데 Bean이 없으면 실패
        for (String id : plan.inputOrder()) if (!registry.contains(id)) throw new IllegalStateException("Missing stage bean: " + id);
        for (String id : plan.outputOrder()) if (!registry.contains(id)) throw new IllegalStateException("Missing stage bean: " + id);

        // 필수 단계 누락 실패(공통팀 기준)
        for (String id : requiredStageIds) {
            if (!plan.inputOrder().contains(id) && !plan.outputOrder().contains(id)) {
                throw new IllegalStateException("Required stage missing in plan: " + id);
            }
        }
    }

}
