package pe.kr.thekey.framework.adaptor.util;

public class ConnectionObjectFactory {
    public static ConnectionObject createConnectionObject(AdaptorProperties.ConnectType connectType, AdaptorProperties.DataType dataType, String encoding, AdaptorProperties.HostInfo hostInfo) {
        return switch (connectType) {
            case SOCKET -> new SocketConnectionObject(hostInfo, encoding);
            case HTTP -> new HttpConnectionObject(hostInfo, dataType, encoding);
            default -> null;
        };
    }
}
