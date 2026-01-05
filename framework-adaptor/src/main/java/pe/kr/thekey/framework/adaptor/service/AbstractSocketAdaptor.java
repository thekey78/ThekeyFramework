package pe.kr.thekey.framework.adaptor.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.kr.thekey.framework.adaptor.util.AdaptorProperties;
import pe.kr.thekey.framework.adaptor.util.ConnectionObject;
import pe.kr.thekey.framework.core.utils.ApplicationContextHolder;
import pe.kr.thekey.framework.messenger.service.MessengerService;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractSocketAdaptor {
    protected final AdaptorProperties.AdaptorConfigInfo configInfo;
    protected boolean useMessenger;

    protected MessengerService messengerService;

    protected List<ConnectionObject> connections = new ArrayList<>();

    @PostConstruct
    public void init() {
        this.useMessenger = configInfo.getDataType().useMessenger();
        if (this.useMessenger) {
            messengerService = ApplicationContextHolder.getApplicationContext().getBean(MessengerService.class);
        }
    }

    public void createSocket() {
        for (AdaptorProperties.HostInfo host : configInfo.getHosts()) {
            try {
                ConnectionObject connection = new ConnectionObject(host);
                connection.connect();
                connections.add(connection);
            } catch (Exception e) {
                log.error("Error creating connection for host: {}:{}", host.getIp(), host.getPort(), e);
            }
        }
    }

}
