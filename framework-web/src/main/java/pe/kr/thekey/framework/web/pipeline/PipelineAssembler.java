package pe.kr.thekey.framework.web.pipeline;

import pe.kr.thekey.framework.core.pipeline.Phase;
import pe.kr.thekey.framework.core.pipeline.Stage;

import java.util.*;
import static java.util.Comparator.comparingInt;

public final class PipelineAssembler {
    private final StageRegistry registry;
    private final Map<String, StageDefinition> definitions; // id -> def
    private final PipelinePlan plan;

    public PipelineAssembler(StageRegistry registry, Map<String, StageDefinition> definitions, PipelinePlan plan) {
        this.registry = registry;
        this.definitions = definitions;
        this.plan = plan;
    }

    public List<ExecutableStage> assemble(Phase phase) {
        List<String> ids = (phase == Phase.INPUT) ? plan.inputOrder() : plan.outputOrder();
        List<ExecutableStage> out = new ArrayList<>();
        for (String id : ids) {
            var stage = registry.getRequired(id);
            var def = definitions.getOrDefault(id, Defaults.defaultDefFor(stage));
            if (def.phase() != phase) {
                throw new IllegalStateException("Stage phase mismatch: " + id + " def=" + def.phase() + " stage=" + stage.phase());
            }
            out.add(new ExecutableStage(stage, def));
        }
        // order는 def 기준으로 재정렬 가능(Plan+order 혼합 운영 방지하려면 여기서 강제)
        out.sort(comparingInt(es -> es.def().order()));
        return out;
    }

    static final class Defaults {
        static StageDefinition defaultDefFor(Stage s) {
            return new StageDefinition(
                    s.id(), s.phase(), true, 1000,
                    ctx -> true,
                    StageDefinition.FailurePolicy.FAIL_CLOSE
            );
        }
    }

}
