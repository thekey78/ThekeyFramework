package bank.demo.was.config;

import bank.demo.was.context.BankWasDemoRequestContextFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pe.kr.thekey.framework.core.context.RequestContextFactory;

@Configuration
public class BankWasDemoConfiguration {
    @Bean
    public RequestContextFactory requestContextFactory() {
        return new BankWasDemoRequestContextFactory();
    }
}
