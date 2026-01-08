package pe.kr.thekey.framework.adaptor.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.core.serializer.Deserializer;
import org.springframework.core.serializer.Serializer;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.context.IntegrationContextUtils;
import org.springframework.integration.ip.tcp.connection.AbstractClientConnectionFactory;
import org.springframework.integration.ip.tcp.connection.AbstractServerConnectionFactory;
import org.springframework.integration.ip.tcp.connection.TcpNioClientConnectionFactory;
import org.springframework.integration.ip.tcp.connection.TcpNioServerConnectionFactory;
import org.springframework.integration.ip.tcp.inbound.TcpInboundGateway;
import org.springframework.integration.ip.tcp.serializer.ByteArraySingleTerminatorSerializer;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import pe.kr.thekey.framework.adaptor.service.MessageService;
import pe.kr.thekey.framework.adaptor.util.AdaptorProperties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@AutoConfiguration
@ConditionalOnProperty(prefix = "thekey.framework.adaptor", name = "enable", havingValue = "true", matchIfMissing = true)
public class SocketAdaptorAutoConfig {
    private final ApplicationContext context;
    private final AdaptorProperties properties;
    private final ConfigurableListableBeanFactory beanFactory;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Bean(name = IntegrationContextUtils.TASK_SCHEDULER_BEAN_NAME)
    @ConditionalOnMissingBean
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(5); // 적절한 풀 사이즈 설정
        scheduler.setThreadNamePrefix("adapter-task-scheduler-");
        scheduler.initialize();
        return scheduler;
    }

    @Bean
    @ConditionalOnMissingBean
    public Serializer<?> serializer() {
        return new ByteArraySingleTerminatorSerializer((byte)0x03);
    }

    @Bean
    @ConditionalOnMissingBean
    public Deserializer<?> deserializer() {
        return new ByteArraySingleTerminatorSerializer((byte)0x03);
    }

    @Bean
    public Map<String, List<AbstractClientConnectionFactory>> clientConnectionFactoryMap(Serializer<?> serializer, Deserializer<?> deserializer) {
        Map<String, List<AbstractClientConnectionFactory>> connectionFactoryMap = new HashMap<>();
        properties.getConfigs().forEach((k, v) -> connectionFactoryMap.put(k, clientConnectionFactories(v.getHosts(), serializer, deserializer)));

        return connectionFactoryMap;
    }

    public List<AbstractClientConnectionFactory> clientConnectionFactories(List<AdaptorProperties.HostInfo> hosts, Serializer<?> serializer, Deserializer<?> deserializer) {
        return hosts.stream()
                .map(host -> makeTcpNioClientConnectionFactory(host, serializer, deserializer))
                .collect(Collectors.toList());
    }

    public AbstractClientConnectionFactory makeTcpNioClientConnectionFactory(AdaptorProperties.HostInfo host, Serializer<?> serializer, Deserializer<?> deserializer) {
        TcpNioClientConnectionFactory connectionFactory = new TcpNioClientConnectionFactory(host.getIp(), host.getPort());
        connectionFactory.setSerializer(serializer);
        connectionFactory.setDeserializer(deserializer);
        connectionFactory.setSingleUse(true);
        return connectionFactory;
    }

    @Bean("inboundChannel")
    @ConditionalOnMissingBean
    public MessageChannel inboundChannel() {
        return new DirectChannel();
    }

    @Bean("replyChannel")
    @ConditionalOnMissingBean
    public MessageChannel replyChannel() {
        return new DirectChannel();
    }

    @Bean("inboundHandler")
    @ConditionalOnMissingBean
    @ServiceActivator(inputChannel = "inboundChannel")
    public MessageHandler inboundHandler() {
        return message -> {
            log.info("Received message: {}", message.getPayload());
        };
    }

    @Bean("replyHandler")
    @ConditionalOnMissingBean
    @ServiceActivator(outputChannel = "replyChannel")
    public MessageHandler replyHandler() {
        return message -> log.info("Replay message: {}", message.getPayload());
    }

    @Bean
    public MessageService messageService() {
        return message -> {
            log.info("Receive message: {}", new String(message));
            return message;
        };
    }

    @Bean
    public Map<String, AbstractServerConnectionFactory> serverConnectionFactoryMap(Serializer<?> serializer, Deserializer<?> deserializer, MessageChannel inboundChannel, MessageChannel replyChannel) {
        Map<String, AbstractServerConnectionFactory> factoryMap = new HashMap<>();

        properties.getConfigs().forEach((key, configInfo) -> {
            AdaptorProperties.AsyncReceiveInfo asyncInfo = configInfo.getAsyncReceiveInfo();

            // AsyncReceiveInfo가 설정되어 있고 enable이 true인 경우에만 생성
            if (asyncInfo != null && asyncInfo.isEnable() && asyncInfo.getTcpIp() != null) {

                AbstractServerConnectionFactory factory = makeServerConnectionFactory(serializer, deserializer, asyncInfo.getTcpIp());
                TcpInboundGateway tcpInboundGateway = makeTcpInboundGateway(factory, inboundChannel, replyChannel);
                tcpInboundGateway.start(); // 게이트웨이 활성화

                log.debug("Registered server connection factory for key: {}", key + "." + factory.getClass().getName());
                beanFactory.registerSingleton(key + "." + factory.getClass().getSimpleName(), factory);
                factoryMap.put(key, factory);
            }
        });

        return factoryMap;
    }

    private AbstractServerConnectionFactory makeServerConnectionFactory(Serializer<?> serializer, Deserializer<?> deserializer, AdaptorProperties.TcpIpInfo tcpIpInfo) {
        if (tcpIpInfo.getPort() < 0 || tcpIpInfo.getPort() > 65535)
            throw new IllegalArgumentException("Invalid port number: " + tcpIpInfo.getPort());
        TcpNioServerConnectionFactory factory = new TcpNioServerConnectionFactory(tcpIpInfo.getPort());
        factory.setSerializer(serializer);
        factory.setDeserializer(deserializer);
        factory.setSoTimeout(tcpIpInfo.getReadTimeout());
        if (this.applicationEventPublisher != null)
            factory.setApplicationEventPublisher(this.applicationEventPublisher);
        factory.setApplicationContext(context);
        factory.setBeanFactory(beanFactory);
        factory.afterPropertiesSet();
        return factory;
    }

    private TcpInboundGateway makeTcpInboundGateway(AbstractServerConnectionFactory connectionFactory,
                                                    MessageChannel inboundChannel,
                                                    MessageChannel replyChannel) {
        TcpInboundGateway tcpInboundGateway = new TcpInboundGateway();
        tcpInboundGateway.setConnectionFactory(connectionFactory);
        tcpInboundGateway.setRequestChannel(inboundChannel);
        tcpInboundGateway.setReplyChannel(replyChannel);
        tcpInboundGateway.setApplicationContext(context);
        // 게이트웨이를 빈으로 등록하고 초기화 및 시작 처리를 합니다.
        tcpInboundGateway.setBeanFactory(beanFactory);
        tcpInboundGateway.afterPropertiesSet();
        return tcpInboundGateway;
    }

}
