package pe.kr.thekey.framework.adaptor.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.ip.tcp.connection.AbstractClientConnectionFactory;
import org.springframework.integration.ip.tcp.connection.AbstractServerConnectionFactory;
import org.springframework.integration.ip.tcp.connection.TcpNioClientConnectionFactory;
import org.springframework.integration.ip.tcp.connection.TcpNioServerConnectionFactory;
import org.springframework.integration.ip.tcp.inbound.TcpInboundGateway;
import org.springframework.messaging.MessageChannel;
import pe.kr.thekey.framework.adaptor.service.AsyncTcpSerializer;
import pe.kr.thekey.framework.adaptor.util.AdaptorProperties;

import java.util.*;

@RequiredArgsConstructor
@AutoConfiguration
@EnableConfigurationProperties({AdaptorProperties.class})
@ConditionalOnProperty(prefix="thekey.framework.adaptor", name="enable", havingValue="true", matchIfMissing=true)
public class AdaptorConfig {
    private final AdaptorProperties properties;



    @Bean
    public AsyncTcpSerializer serializer() {
        return new AsyncTcpSerializer();
    }

    @Bean
    public Map<String, List<AbstractClientConnectionFactory>> clientConnectionFactoryMap(AdaptorProperties properties) {
        Map<String, List<AbstractClientConnectionFactory>> connectionFactoryMap = new HashMap<>();
        properties.getConfigs().forEach((k,v) -> {
            connectionFactoryMap.put(k, clientConnectionFactories(v.getHosts(), serializer()));
        });

        return connectionFactoryMap;
    }

    @Bean
    public List<AbstractClientConnectionFactory> clientConnectionFactories(List<AdaptorProperties.HostInfo> hosts, AsyncTcpSerializer serializer) {
        List<AbstractClientConnectionFactory> connectionFactoryList = new ArrayList<>();
        hosts.forEach(host -> {
            connectionFactoryList.add(makeTcpNioClientConnectionFactory(host, serializer));
        });
        return connectionFactoryList;
    }

    @Bean
    public AbstractClientConnectionFactory makeTcpNioClientConnectionFactory(AdaptorProperties.HostInfo host, AsyncTcpSerializer serializer) {
        TcpNioClientConnectionFactory connectionFactory = new TcpNioClientConnectionFactory(host.getIp(), host.getPort());
        connectionFactory.setSerializer(serializer);
        connectionFactory.setDeserializer(serializer);
        connectionFactory.setSingleUse(true);
        return connectionFactory;
    }


    @Bean
    public Map<String, AbstractServerConnectionFactory> serverConnectionFactoryMap(AdaptorProperties properties) {
        Map<String, AbstractServerConnectionFactory> connectionFactoryMap = new HashMap<>();
        properties.getConfigs().forEach((k,v) -> {
            connectionFactoryMap.put(k, serverConnectionFactory(serializer(), v.getAsyncReceiveInfo()));
        });

        return connectionFactoryMap;
    }

    @Bean
    public AbstractServerConnectionFactory serverConnectionFactory(AsyncTcpSerializer serializer, AdaptorProperties.AsyncReceiveInfo receiveInfo) {
        TcpNioServerConnectionFactory connectionFactory = new TcpNioServerConnectionFactory(receiveInfo.getPort());
        connectionFactory.setSerializer(serializer);
        connectionFactory.setDeserializer(serializer);
        connectionFactory.setSingleUse(true);
        return connectionFactory;
    }

    @Bean
    public MessageChannel inboundChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel replyChannel() {
        return new DirectChannel();
    }

    @Bean
    public TcpInboundGateway inboundGateway(AbstractServerConnectionFactory connectionFactory, MessageChannel inboundChannel, MessageChannel replyChannel) {
        TcpInboundGateway tcpInboundGateway = new TcpInboundGateway();
        tcpInboundGateway.setConnectionFactory(connectionFactory);
        tcpInboundGateway.setRequestChannel(inboundChannel);
        tcpInboundGateway.setReplyChannel(replyChannel);
        return tcpInboundGateway;
    }
}
