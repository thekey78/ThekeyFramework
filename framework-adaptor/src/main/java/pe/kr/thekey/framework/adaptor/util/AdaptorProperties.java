package pe.kr.thekey.framework.adaptor.util;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ConfigurationProperties(prefix = "thekey.framework.adaptor")
@Getter
@ToString
public class AdaptorProperties {
    @Setter
    private boolean enable = true;

    @Setter
    private Map<String, AdaptorConfigInfo> configs = new ConcurrentHashMap<>();

    @Data
    public static class AdaptorConfigInfo {
        private boolean enable;
        private boolean failover;
        private boolean loadBalance;
        private boolean permanent;
        private boolean async;
        private DataType dataType;
        private ConnectType connectType;
        private List<HostInfo> hosts = new ArrayList<>();
        private int receivePort;
        private AsyncReceiveInfo asyncReceiveInfo;
    }

    @Data
    public static class HostInfo {
        private String url;
        private String ip;
        private int port;
        private int connectionTimeout = 5000;
        private int readTimeout = 60000;
        private int order = 0;
    }

    @Data
    public static class AsyncReceiveInfo {
        private boolean enable;
        private String receiveWasId;
        private ConnectType receiveConnectType;
        private String receiveWasUrl;
        private int port;
        private int readTimeout = 60000;
        private int order = 0;
    }

    public enum DataType {
        XML, CSV, FIXED, DELIMITED, JSON;

        public static boolean useMessenger(@NonNull DataType dataType) {
            return dataType != JSON;
        }

        public boolean useMessenger() {
            return useMessenger(this);
        }
    }

    public enum ConnectType {
        SOCKET, HTTP;
    }
}

