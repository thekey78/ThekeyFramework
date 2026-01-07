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

    @Getter
    @Setter
    @ToString
    public static class AdaptorConfigInfo {
        private boolean enable;
        private boolean failover;
        private boolean loadBalance;
        private boolean permanent;
        private boolean async;
        private DataType dataType;
        private ConnectType connectType;
        private List<HostInfo> hosts = new ArrayList<>();
        private AsyncReceiveInfo asyncReceiveInfo;
    }

    @Getter
    @Setter
    @ToString
    public static class HostInfo {
        private String url;
        private String ip;
        private int port;
        private int connectionTimeout = 5000;
        private int readTimeout = 60000;
        private int order = 0;
    }

    @Getter
    @Setter
    @ToString
    public static class AsyncReceiveInfo {
        private boolean enable;
        private ConnectType receiveConnectType;
        private TcpIpInfo tcpIp;
        private FtpInfo ftp;
        private SftpInfo sftp;
        private GrpcInfo grpc;
        private KafkaInfo kafka;
    }

    @Getter
    @Setter
    @ToString
    public static class TcpIpInfo {
        private String host;
        private int port;
        private int readTimeout = 60000;
        private String receiveWasId;
    }

    @Getter
    @Setter
    @ToString
    public static class FtpInfo {
        private String host;
        private int port = 21;
        private String username;
        private String password;
        private String remoteDirectory;
        private String localDirectory;
        private int connectionTimeout = 30000;
        private int dataTimeout = 60000;
        private boolean passiveMode = true;
    }

    @Getter
    @Setter
    @ToString
    public static class SftpInfo {
        private String host;
        private int port = 22;
        private String username;
        private String password;
        private String privateKey;
        private String privateKeyPassphrase;
        private String remoteDirectory;
        private String localDirectory;
        private int connectionTimeout = 30000;
        private int sessionTimeout = 60000;
    }

    @Getter
    @Setter
    @ToString
    public static class GrpcInfo {
        private String host;
        private int port;
        private boolean useTls = false;
        private String certChainFile;
        private String privateKeyFile;
        private String trustCertCollection;
        private int maxInboundMessageSize = 4194304; // 4MB
        private int keepAliveTime = 300; // seconds
        private int keepAliveTimeout = 20; // seconds
    }

    @Getter
    @Setter
    @ToString
    public static class KafkaInfo {
        private String bootstrapServers;
        private String topic;
        private String groupId;
        private String consumerGroup;
        private int sessionTimeout = 30000;
        private int pollTimeout = 1000;
        private String keyDeserializer = "org.apache.kafka.common.serialization.StringDeserializer";
        private String valueDeserializer = "org.apache.kafka.common.serialization.StringDeserializer";
        private String autoOffsetReset = "latest";
        private boolean enableAutoCommit = true;
    }

    public enum DataType {
        XML, CSV, BINARY, DELIMITED, JSON;

        public static boolean useMessenger(@NonNull DataType dataType) {
            return dataType != JSON;
        }

        public boolean useMessenger() {
            return useMessenger(this);
        }
    }

    public enum ConnectType {
        SOCKET, FTP, SFTP, GRPC, KAFKA, HTTP
    }
}

