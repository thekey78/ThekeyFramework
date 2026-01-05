package pe.kr.thekey.framework.web.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import pe.kr.thekey.framework.web.property.FrameworkWebProperties;

@AutoConfiguration
@EnableConfigurationProperties(FrameworkWebProperties.class)
@ConditionalOnProperty(prefix="thekey.framework.web", name="enable", havingValue="true", matchIfMissing=true)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class WebConfiguration {
}
