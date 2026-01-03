package bank.demo.common.util;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import pe.kr.thekey.framework.core.properties.FrameworkCoreProperties;
import pe.kr.thekey.framework.core.utils.ApplicationContextHolder;
import pe.kr.thekey.framework.web.property.FrameworkWebProperties;

@ConfigurationProperties(prefix = "bank.demo")
@ToString
public class DemoProperties {
    @Getter
    @Setter
    private RunningMode runningMode = RunningMode.DEVELOPMENT;

    public enum RunningMode {
        DEVELOPMENT, PRODUCTION, STAGING, LOCAL, TEST
    }

    public FrameworkCoreProperties getCoreProperties() {
        ApplicationContext applicationContext = ApplicationContextHolder.getApplicationContext();
        if (applicationContext != null) {
            return applicationContext.getBean(FrameworkCoreProperties.class);
        }
        return null;
    }

    public FrameworkWebProperties getWebProperties() {
        ApplicationContext applicationContext = ApplicationContextHolder.getApplicationContext();
        if (applicationContext != null) {
            return applicationContext.getBean(FrameworkWebProperties.class);
        }
        return null;
    }
}
