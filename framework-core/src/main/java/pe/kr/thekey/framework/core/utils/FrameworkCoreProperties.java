package pe.kr.thekey.framework.core.utils;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@ConfigurationProperties(prefix = "thekey.framework.core")
public class FrameworkCoreProperties {
    @Setter
    private boolean enable = true;

    @Setter
    private String wasIdName;

    private final Pipeline pipeline = new Pipeline();

    private final Map<String, StageProps> stages = new HashMap<>();

    @Setter
    @Getter
    public static class Pipeline {
        private List<String> inputOrder = List.of();
        private List<String> outputOrder = List.of();
        private List<String> requiredStageIds = List.of(); // 공통팀 기준

    }

    @Setter
    @Getter
    public static class StageProps {
        private boolean enable = true;
        private int order = 1000;
        private String condition = ""; // DSL
        private String failurePolicy = "FAIL_CLOSED"; // FAIL_CLOSED|FAIL_OPEN

    }
}
