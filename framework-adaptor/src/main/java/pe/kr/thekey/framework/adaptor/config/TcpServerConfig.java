package pe.kr.thekey.framework.adaptor.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.ip.tcp.connection.AbstractServerConnectionFactory;
import org.springframework.integration.ip.tcp.connection.TcpNioClientConnectionFactory;
import org.springframework.integration.ip.tcp.connection.TcpNioServerConnectionFactory;
import org.springframework.integration.ip.tcp.inbound.TcpInboundGateway;
import org.springframework.messaging.MessageChannel;
import pe.kr.thekey.framework.adaptor.service.AsyncTcpSerializer;
import pe.kr.thekey.framework.adaptor.util.AdaptorProperties;

@AutoConfiguration
@EnableIntegration
@RequiredArgsConstructor
public class TcpServerConfig {
    private final AdaptorProperties properties;

    @Bean
    public AsyncTcpSerializer serializer() {
        return new AsyncTcpSerializer();
    }

    @Bean
    public TcpNioClientConnectionFactory clientConnectionFactory() {
        TcpNioClientConnectionFactory connectionFactory = new TcpNioClientConnectionFactory();
        connectionFactory.setSerializer(serializer());
        connectionFactory.setDeserializer(serializer());
        connectionFactory.setSingleUse(true);
        return connectionFactory;
    }

    @Bean
    public AbstractServerConnectionFactory connectionFactory(AsyncTcpSerializer serializer) {
        TcpNioServerConnectionFactory connectionFactory = new TcpNioServerConnectionFactory(port);
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
