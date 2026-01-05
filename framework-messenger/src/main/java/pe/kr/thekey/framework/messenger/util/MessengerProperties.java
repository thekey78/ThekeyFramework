package pe.kr.thekey.framework.messenger.util;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@Getter
@ConfigurationProperties(prefix = "thekey.framework.messenger")
public class MessengerProperties {
    @Setter
    private boolean enable;

    private final List<String> filePaths = new ArrayList<>();

    @Setter
    private String fileEncoding = "UTF-8";

    @Setter
    private String requestEncoding = "UTF-8";

    @Setter
    private String responseEncoding = "UTF-8";

    @Setter
    private boolean fileChangeOnLoad;

    private final List<String> mapping = new ArrayList<>();
}
