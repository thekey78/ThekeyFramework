package pe.kr.thekey.framework.messenger.config;

import org.beanio.StreamFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import pe.kr.thekey.framework.messenger.service.MessengerService;
import pe.kr.thekey.framework.messenger.util.MessengerProperties;

@AutoConfiguration
@EnableConfigurationProperties({MessengerProperties.class})
@ConditionalOnProperty(prefix="thekey.framework.messenger", name="enable", havingValue="true", matchIfMissing=true)
public class MessengerConfig {
    @Bean
    public MessengerService messengerService(MessengerProperties properties) {
        return new MessengerService(properties);
    }
}
