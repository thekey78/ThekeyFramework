package pe.kr.thekey.framework.web.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "thekey.framework.web")
@Getter
public class FrameworkWebProperties {
    @Setter
    private boolean enable = true;

    @Setter
    private SessionKey sessionKey;

    @Getter
    @Setter
    public static class SessionKey {
        private boolean enable = false;
        private List<String> keys;
    }
}
