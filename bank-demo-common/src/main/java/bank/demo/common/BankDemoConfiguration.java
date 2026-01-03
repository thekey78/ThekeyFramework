package bank.demo.common;

import bank.demo.common.stage.*;
import bank.demo.common.util.DemoProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import pe.kr.thekey.framework.core.pipeline.Stage;

@AutoConfiguration
@EnableConfigurationProperties(DemoProperties.class)
public class BankDemoConfiguration {
    @Bean
    public Stage requestXssStage() {
        return new RequestXssStage();
    }

    @Bean
    public Stage requestDdosTokenStage() {
        return new RequestDdosTokenStage();
    }

    @Bean
    public Stage responseLoggingStage() {
        return new ResponseLoggingStage();
    }

    @Bean
    public Stage requestLoggingStage() {
        return new RequestLoggingStage();
    }

    @Bean
    public Stage responseDdosTokenStage() {
        return new ResponseDdosTokenStage();
    }

    @Bean
    public Stage requestE2EStage() {
        return new RequestE2EStage();
    }
}
