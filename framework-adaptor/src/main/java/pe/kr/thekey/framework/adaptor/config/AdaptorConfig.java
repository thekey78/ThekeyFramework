package pe.kr.thekey.framework.adaptor.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import pe.kr.thekey.framework.adaptor.util.AdaptorProperties;

import java.time.Duration;

@RequiredArgsConstructor
@AutoConfiguration
@EnableConfigurationProperties({AdaptorProperties.class})
@ConditionalOnProperty(prefix="thekey.framework.adaptor", name="enable", havingValue="true", matchIfMissing=true)
public class AdaptorConfig {

    @Bean
    @ConditionalOnMissingBean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .setConnectTimeout(Duration.ofMillis(5000))
                .setReadTimeout(Duration.ofMillis(5000))
                .build();
    }

    @Bean
    @ConditionalOnMissingBean
    public RestTemplateBuilder restTemplateBuilder() {
        return new RestTemplateBuilder();
    }
}
