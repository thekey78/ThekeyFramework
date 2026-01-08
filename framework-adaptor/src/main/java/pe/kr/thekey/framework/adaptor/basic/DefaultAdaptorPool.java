package pe.kr.thekey.framework.adaptor.basic;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import pe.kr.thekey.framework.adaptor.AdaptorPool;
import pe.kr.thekey.framework.adaptor.exception.ExternalException;
import pe.kr.thekey.framework.adaptor.util.AdaptorConverter;
import pe.kr.thekey.framework.adaptor.util.ConnectionObject;
import pe.kr.thekey.framework.adaptor.util.ConnectionObjectFactory;
import pe.kr.thekey.framework.core.config.ErrorCode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static pe.kr.thekey.framework.adaptor.util.AdaptorProperties.*;

@Slf4j
public class DefaultAdaptorPool implements AdaptorPool {
    @Getter
    private boolean failover;
    @Getter
    private boolean loadBalance;
    @Getter
    private boolean permanent;
    @Getter
    private boolean async;
    @Getter
    private DataType dataType;
    @Getter
    private ConnectType connectType;
    @Getter
    private String encoding;

    private final AdaptorConverter converter;


    private List<HostInfo> hosts;

    protected List<ConnectionObject> connections = new ArrayList<>();

    protected AtomicInteger currentIndex = new AtomicInteger(0);

    public DefaultAdaptorPool(AdaptorConfigInfo configInfo, AdaptorConverter converter) {
        this.converter = converter;
        reload(configInfo);
    }


    public void reload(AdaptorConfigInfo configInfo) {
        this.failover = configInfo.isFailover();
        this.loadBalance = configInfo.isLoadBalance();
        this.dataType = configInfo.getDataType();
        this.encoding = configInfo.getEncoding();
        this.hosts = configInfo.getHosts();
        this.permanent = configInfo.isPermanent();
        this.async = configInfo.isAsync();
        this.dataType = configInfo.getDataType();
        this.connectType = configInfo.getConnectType();
        this.encoding = configInfo.getEncoding();
        init();
    }

    protected void init() {
        makeAdaptorPool();
    }

    @PostConstruct
    protected void makeAdaptorPool() {
        for (HostInfo host : this.hosts) {
            ConnectionObject socketConnectionObject = ConnectionObjectFactory.createConnectionObject(connectType, dataType, encoding, host);
            connect(isPermanent(), socketConnectionObject);
            connections.add(socketConnectionObject);
        }
    }

    @Override
    public byte[] execute(String uuid, String channelId, byte[] data) {
        ConnectionObject connectionObject = getConnectionObject();

        connect(!isPermanent(), connectionObject);
        // Executes transactional I/O; disconnects if not permanent
        try {
            return connectionObject.writeRead(uuid, channelId, data);
        } catch (IOException e) {
            log.error(ErrorCode.FEX013.getMessage(), e);
            throw new ExternalException(ErrorCode.FEX013, e);
        } finally {
            if (!isPermanent()) {
                try {
                    connectionObject.disconnect();
                } catch (IOException e) {
                    log.error("Failed to disconnect adaptor connection", e);
                }
            }
        }
    }

    @Override
    public <T> T execute(String uuid, String channelId, Object data, Class<T> responseType) throws ExternalException {
        try {
            byte[] requestData = converter.convertToBytes(data, dataType, encoding);
            byte[] responseData = execute(uuid, channelId, requestData);
            return (T) converter.convertFromBytes(responseData, responseType, dataType, encoding);
        } catch (Exception e) {
            log.error("Failed to execute adaptor with conversion", e);
            throw new ExternalException(ErrorCode.FEX013, e);
        }
    }

    private ConnectionObject getConnectionObject() {
        ConnectionObject socketConnectionObject;
        // Selects connection object based on load balancing
        if (isLoadBalance()) {
            socketConnectionObject = connections.get(currentIndex.getAndIncrement());
            if (currentIndex.get() >= hosts.size()) {
                currentIndex.set(0);
            }
        }
        else {
            socketConnectionObject = connections.get(currentIndex.get());
        }
        return socketConnectionObject;
    }

    private void connect(boolean isConnect, ConnectionObject connectionObject) {
        if (isConnect) {
            try {
                connectionObject.connect();
            } catch (IOException e) {
                log.error(ErrorCode.FEX012.getMessage(), e);
                throw new ExternalException(ErrorCode.FEX012, e);
            }
        }
    }
}
