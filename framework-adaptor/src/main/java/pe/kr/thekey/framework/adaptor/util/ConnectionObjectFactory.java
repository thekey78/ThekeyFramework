package pe.kr.thekey.framework.adaptor.util;

public class ConnectionObjectFactory {
    public static ConnectionObject createConnectionObject(AdaptorProperties.ConnectType connectType, AdaptorProperties.DataType dataType, AdaptorProperties.HostInfo hostInfo) {
        return switch (connectType) {
            case SOCKET -> new SocketConnectionObject(hostInfo);
            case HTTP -> new HttpConnectionObject(hostInfo, dataType);
        };
    }
}
