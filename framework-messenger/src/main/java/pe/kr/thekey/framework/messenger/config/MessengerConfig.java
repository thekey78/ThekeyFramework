package pe.kr.thekey.framework.messenger.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import pe.kr.thekey.framework.core.utils.file.WatcherForDirectory;
import pe.kr.thekey.framework.messenger.service.MessengerService;
import pe.kr.thekey.framework.messenger.service.impl.MessengerServiceImpl;
import pe.kr.thekey.framework.messenger.util.MessengerProperties;

@RequiredArgsConstructor
@AutoConfiguration
@EnableConfigurationProperties({MessengerProperties.class})
@ConditionalOnProperty(prefix="thekey.framework.messenger", name="enable", havingValue="true", matchIfMissing=true)
public class MessengerConfig {
    private final MessengerProperties properties;
    private final WatcherForDirectory watcherForDirectory;

    @Bean
    public MessengerService messengerService() {
        return new MessengerServiceImpl(properties, watcherForDirectory);
    }
}
