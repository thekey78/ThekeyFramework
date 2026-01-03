package pe.kr.thekey.framework.web.pipeline;

import pe.kr.thekey.framework.core.pipeline.Stage;

public record ExecutableStage(Stage stage, StageDefinition def) {
}
