package pe.kr.thekey.framework.core.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import pe.kr.thekey.framework.core.properties.FrameworkCoreProperties;
import pe.kr.thekey.framework.core.utils.ApplicationContextHolder;
import pe.kr.thekey.framework.core.utils.file.WatcherForDirectory;
import pe.kr.thekey.framework.core.utils.file.WatcherForFile;

@AutoConfiguration
@EnableConfigurationProperties({FrameworkCoreProperties.class})
@ConditionalOnProperty(prefix="thekey.framework.core", name="enable", havingValue="true", matchIfMissing=true)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class FrameworkCoreConfig {
    @Bean
    public WatcherForFile watcherForFile() {
        return new WatcherForFile();
    }

    @Bean
    public WatcherForDirectory watcherForDirectory() {
        return new WatcherForDirectory();
    }

    @Bean
    public ApplicationContextAware applicationContextAware() {
        return new ApplicationContextHolder() {};
    }
}
