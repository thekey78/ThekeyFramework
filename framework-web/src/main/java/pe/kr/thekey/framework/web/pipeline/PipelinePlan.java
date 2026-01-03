package pe.kr.thekey.framework.web.pipeline;

import java.util.List;

public record PipelinePlan(List<String> inputOrder, List<String> outputOrder) {}