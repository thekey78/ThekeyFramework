package pe.kr.thekey.framework.starter.config;

import jakarta.servlet.Filter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import pe.kr.thekey.framework.core.context.RequestContextFactory;
import pe.kr.thekey.framework.core.context.RequestContextHolder;
import pe.kr.thekey.framework.core.error.ErrorMapper;
import pe.kr.thekey.framework.core.pipeline.Stage;
import pe.kr.thekey.framework.core.utils.FrameworkCoreProperties;
import pe.kr.thekey.framework.starter.web.DefaultErrorMapper;
import pe.kr.thekey.framework.starter.web.DefaultRequestContextFactory;
import pe.kr.thekey.framework.starter.web.DefaultRequestContextHolder;
import pe.kr.thekey.framework.starter.web.SpringRequestAttributesProvider;
import pe.kr.thekey.framework.web.condition.ConditionCompiler;
import pe.kr.thekey.framework.web.condition.PrimitiveFunctions;
import pe.kr.thekey.framework.web.filter.FrameworkGatewayFilter;
import pe.kr.thekey.framework.web.pipeline.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@AutoConfiguration
public class FrameworkAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public PrimitiveFunctions primitiveFunctions() {
        return new PrimitiveFunctions();
    }

    @Bean
    @ConditionalOnMissingBean
    public ConditionCompiler conditionCompiler(PrimitiveFunctions f) {
        return new ConditionCompiler(f);
    }

    @Bean
    public StageRegistry stageRegistry(ApplicationContext ac) {
        Collection<Stage> stages = ac.getBeansOfType(Stage.class).values();
        return new StageRegistry(stages);
    }

    @Bean
    public PipelinePlan pipelinePlan(FrameworkCoreProperties frameworkCoreProperties) {
        return new PipelinePlan(frameworkCoreProperties.getPipeline().getInputOrder(), frameworkCoreProperties.getPipeline().getOutputOrder());
    }

    @Bean
    public Map<String, StageDefinition> stageDefinitions(FrameworkCoreProperties props, StageRegistry registry, ConditionCompiler cc) {
        Map<String, StageDefinition> defs = new HashMap<>();
        for (var e : props.getStages().entrySet()) {
            String id = e.getKey();
            var sp = e.getValue();
            var stage = registry.getRequired(id);

            var cond = cc.compile(sp.getCondition());
            var fp = StageDefinition.FailurePolicy.valueOf(sp.getFailurePolicy());

            defs.put(id, new StageDefinition(id, stage.phase(), sp.isEnable(), sp.getOrder(), cond, fp));
        }
        return defs;
    }

    @Bean
    public PipelineValidator pipelineValidator(FrameworkCoreProperties props) {
        return new PipelineValidator(props.getPipeline().getRequiredStageIds());
    }

    @Bean
    public PipelineAssembler pipelineAssembler(StageRegistry registry, Map<String, StageDefinition> defs, PipelinePlan plan, PipelineValidator validator) {
        validator.validate(plan, registry);
        return new PipelineAssembler(registry, defs, plan);
    }

    @Bean
    @ConditionalOnMissingBean
    public PipelineExecutor pipelineExecutor() {
        return new PipelineExecutor();
    }

    @Bean
    @ConditionalOnMissingBean
    public FilterRegistrationBean<Filter> frameworkGatewayFilter(
            RequestContextFactory ctxFactory,
            RequestContextHolder ctxHolder,
            PipelineAssembler assembler,
            PipelineExecutor executor,
            ErrorMapper errorMapper
    ) {
        FilterRegistrationBean<Filter> frb = new FilterRegistrationBean<>();
        frb.setFilter(new FrameworkGatewayFilter(ctxFactory, ctxHolder, assembler, executor, errorMapper));
        frb.addUrlPatterns("/*");
        frb.setOrder(-1000);
        return frb;
    }

    @Bean
    @ConditionalOnMissingBean
    public RequestContextFactory requestContextFactory() {
        return new DefaultRequestContextFactory();
    }

    @Bean
    @ConditionalOnMissingBean
    public DefaultRequestContextHolder.RequestAttributesProvider requestAttributesProvider() {
        return new SpringRequestAttributesProvider();
    }


    @Bean
    @ConditionalOnMissingBean
    public RequestContextHolder requestContextHolder(DefaultRequestContextHolder.RequestAttributesProvider provider) {
        return new DefaultRequestContextHolder(provider);
    }

    @Bean
    @ConditionalOnMissingBean
    public ErrorMapper errorMapper() {
        return new DefaultErrorMapper();
    }
}
