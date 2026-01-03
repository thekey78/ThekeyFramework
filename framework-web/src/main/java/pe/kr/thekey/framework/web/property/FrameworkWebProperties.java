package pe.kr.thekey.framework.web.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "thekey.framework.web")
@Getter
public class FrameworkWebProperties {
    @Setter
    private boolean enabled = true;

    @Setter
    private SessionKey sessionKey;

    @Getter
    @Setter
    public static class SessionKey {
        private boolean enabled = false;
        private List<String> keys;
    }
}
