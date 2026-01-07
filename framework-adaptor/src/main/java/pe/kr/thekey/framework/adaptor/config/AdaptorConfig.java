package pe.kr.thekey.framework.adaptor.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.context.IntegrationContextUtils;
import org.springframework.integration.ftp.inbound.FtpInboundFileSynchronizer;
import org.springframework.integration.ip.tcp.connection.AbstractClientConnectionFactory;
import org.springframework.integration.ip.tcp.connection.AbstractServerConnectionFactory;
import org.springframework.integration.ip.tcp.connection.TcpNioClientConnectionFactory;
import org.springframework.integration.ip.tcp.connection.TcpNioServerConnectionFactory;
import org.springframework.integration.ip.tcp.inbound.TcpInboundGateway;
import org.springframework.integration.ftp.inbound.FtpInboundFileSynchronizingMessageSource;
import org.springframework.integration.ftp.session.DefaultFtpSessionFactory;
import org.springframework.integration.sftp.inbound.SftpInboundFileSynchronizer;
import org.springframework.integration.sftp.inbound.SftpInboundFileSynchronizingMessageSource;
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;
import org.springframework.messaging.MessageChannel;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import pe.kr.thekey.framework.adaptor.service.AsyncTcpSerializer;
import pe.kr.thekey.framework.adaptor.util.AdaptorProperties;
import pe.kr.thekey.framework.adaptor.util.AdaptorProperties.*;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@AutoConfiguration
@ComponentScan("pe.kr.thekey.framework.adaptor")
@EnableConfigurationProperties({AdaptorProperties.class})
@ConditionalOnProperty(prefix = "thekey.framework.adaptor", name = "enable", havingValue = "true", matchIfMissing = true)
public class AdaptorConfig {
    private final ApplicationContext context;
    private final AdaptorProperties properties;

    @Bean(name = IntegrationContextUtils.TASK_SCHEDULER_BEAN_NAME)
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(5); // 적절한 풀 사이즈 설정
        scheduler.setThreadNamePrefix("adapter-task-scheduler-");
        scheduler.initialize();
        return scheduler;
    }

    @Bean
    public AsyncTcpSerializer serializer() {
        return new AsyncTcpSerializer();
    }

    @Bean
    public Map<String, List<AbstractClientConnectionFactory>> clientConnectionFactoryMap() {
        Map<String, List<AbstractClientConnectionFactory>> connectionFactoryMap = new HashMap<>();
        properties.getConfigs().forEach((k, v) -> connectionFactoryMap.put(k, clientConnectionFactories(v.getHosts(), serializer())));

        return connectionFactoryMap;
    }

    public List<AbstractClientConnectionFactory> clientConnectionFactories(List<HostInfo> hosts, AsyncTcpSerializer serializer) {
        return hosts.stream()
                .map(host -> makeTcpNioClientConnectionFactory(host, serializer))
                .collect(Collectors.toList());
    }

    public AbstractClientConnectionFactory makeTcpNioClientConnectionFactory(HostInfo host, AsyncTcpSerializer serializer) {
        TcpNioClientConnectionFactory connectionFactory = new TcpNioClientConnectionFactory(host.getIp(), host.getPort());
        connectionFactory.setSerializer(serializer);
        connectionFactory.setDeserializer(serializer);
        connectionFactory.setSingleUse(true);
        return connectionFactory;
    }

    @Bean
    public Map<String, AbstractServerConnectionFactory> serverConnectionFactoryMap(AsyncTcpSerializer serializer) {
        Map<String, AbstractServerConnectionFactory> factoryMap = new HashMap<>();

        properties.getConfigs().forEach((key, configInfo) -> {
            AsyncReceiveInfo asyncInfo = configInfo.getAsyncReceiveInfo();

            // AsyncReceiveInfo가 설정되어 있고 enable이 true인 경우에만 생성
            if (asyncInfo != null && asyncInfo.isEnable() && asyncInfo.getTcpIp() != null) {
                AbstractServerConnectionFactory factory = makeServerConnectionFactory(serializer, asyncInfo.getTcpIp());
                ConfigurableListableBeanFactory beanFactory = ((ConfigurableApplicationContext) context).getBeanFactory();
                factory.setBeanFactory(beanFactory);
                factory.setApplicationContext(context);
                factory.setApplicationEventPublisher(context);
                factory.afterPropertiesSet();
                log.debug("Registered server connection factory for key: {}", key);
                beanFactory.registerSingleton(key + factory.getClass().getName(), factory);
                factoryMap.put(key, factory);
            }
        });

        return factoryMap;
    }

    private AbstractServerConnectionFactory makeServerConnectionFactory(AsyncTcpSerializer serializer, TcpIpInfo tcpIpInfo) {
        if (tcpIpInfo.getPort() < 0 || tcpIpInfo.getPort() > 65535)
            throw new IllegalArgumentException("Invalid port number: " + tcpIpInfo.getPort());
        TcpNioServerConnectionFactory connectionFactory = new TcpNioServerConnectionFactory(tcpIpInfo.getPort());
        connectionFactory.setSerializer(serializer);
        connectionFactory.setDeserializer(serializer);
        connectionFactory.setSingleUse(true);
        connectionFactory.setSoTimeout(tcpIpInfo.getReadTimeout());
        return connectionFactory;
    }

    @Bean
    public Map<String, DefaultFtpSessionFactory> ftpSessionFactoryMap() {
        Map<String, DefaultFtpSessionFactory> factoryMap = new HashMap<>();

        properties.getConfigs().forEach((key, configInfo) -> {
            AsyncReceiveInfo asyncInfo = configInfo.getAsyncReceiveInfo();

            if (asyncInfo != null && asyncInfo.isEnable()
                    && asyncInfo.getReceiveConnectType() == ConnectType.FTP
                    && asyncInfo.getFtp() != null) {
                DefaultFtpSessionFactory factory = makeFtpSessionFactory(asyncInfo.getFtp());
                log.debug("Registered FTP server connection factory for key: {}", key);
                ((ConfigurableApplicationContext) context).getBeanFactory().registerSingleton(key + factory.getClass().getName(), factory);

                factoryMap.put(key, factory);
            }
        });

        return factoryMap;
    }

    private DefaultFtpSessionFactory makeFtpSessionFactory(FtpInfo ftpInfo) {
        DefaultFtpSessionFactory factory = new DefaultFtpSessionFactory();
        factory.setHost(ftpInfo.getHost());
        factory.setPort(ftpInfo.getPort());
        factory.setUsername(ftpInfo.getUsername());
        factory.setPassword(ftpInfo.getPassword());
        factory.setClientMode(ftpInfo.isPassiveMode() ? 2 : 1); // 2 = PASSIVE, 1 = ACTIVE
        factory.setConnectTimeout(ftpInfo.getConnectionTimeout());
        factory.setDataTimeout(ftpInfo.getDataTimeout());
        return factory;
    }

    @Bean("sftpSessionFactoryMap")
    public Map<String, DefaultSftpSessionFactory> sftpSessionFactoryMap() {
        Map<String, DefaultSftpSessionFactory> factoryMap = new HashMap<>();

        properties.getConfigs().forEach((key, configInfo) -> {
            AsyncReceiveInfo asyncInfo = configInfo.getAsyncReceiveInfo();

            if (asyncInfo != null && asyncInfo.isEnable()
                    && asyncInfo.getReceiveConnectType() == ConnectType.SFTP
                    && asyncInfo.getSftp() != null) {
                DefaultSftpSessionFactory factory = makeSftpSessionFactory(asyncInfo.getSftp());
                log.debug("Registered SFTP server connection factory for key: {}", key);
                ((ConfigurableApplicationContext) context).getBeanFactory().registerSingleton(key + factory.getClass().getName(), factory);

                factoryMap.put(key, factory);
            }
        });

        return factoryMap;
    }

    private DefaultSftpSessionFactory makeSftpSessionFactory(SftpInfo sftpInfo) {
        DefaultSftpSessionFactory factory = new DefaultSftpSessionFactory();
        factory.setHost(sftpInfo.getHost());
        factory.setPort(sftpInfo.getPort());
        factory.setUser(sftpInfo.getUsername());

        if (sftpInfo.getPrivateKey() != null && !sftpInfo.getPrivateKey().isEmpty()) {
            factory.setPrivateKey(new org.springframework.core.io.FileSystemResource(sftpInfo.getPrivateKey()));
            if (sftpInfo.getPrivateKeyPassphrase() != null) {
                factory.setPrivateKeyPassphrase(sftpInfo.getPrivateKeyPassphrase());
            }
        } else {
            factory.setPassword(sftpInfo.getPassword());
        }

        factory.setTimeout(sftpInfo.getConnectionTimeout());
        factory.setAllowUnknownKeys(true);
        return factory;
    }

    @Bean
    public Map<String, FtpInboundFileSynchronizingMessageSource> ftpInboundAdapterMap(Map<String, DefaultFtpSessionFactory> ftpSessionFactoryMap) {
        Map<String, FtpInboundFileSynchronizingMessageSource> adapterMap = new HashMap<>();

        properties.getConfigs().forEach((key, configInfo) -> {
            AsyncReceiveInfo asyncInfo = configInfo.getAsyncReceiveInfo();
            if (asyncInfo != null && asyncInfo.isEnable()
                    && asyncInfo.getReceiveConnectType() == ConnectType.FTP
                    && ftpSessionFactoryMap.containsKey(key)) {

                FtpInfo ftpInfo = asyncInfo.getFtp();
                DefaultFtpSessionFactory sessionFactory = ftpSessionFactoryMap.get(key);

                FtpInboundFileSynchronizingMessageSource adapter = new FtpInboundFileSynchronizingMessageSource(makeFtpInboundSynchronizer(sessionFactory, ftpInfo));
                adapter.setLocalDirectory(new File(ftpInfo.getLocalDirectory()));
                adapter.setAutoCreateLocalDirectory(true);
                log.debug("Registered FTP inbound adapter for key: {}", key);
                ((ConfigurableApplicationContext) context).getBeanFactory().registerSingleton(key + adapter.getClass().getName(), adapter);

                adapterMap.put(key, adapter);
            }
        });

        return adapterMap;
    }

    private FtpInboundFileSynchronizer makeFtpInboundSynchronizer(DefaultFtpSessionFactory sessionFactory, FtpInfo ftpInfo) {
        FtpInboundFileSynchronizer synchronizer = new FtpInboundFileSynchronizer(sessionFactory);
        synchronizer.setRemoteDirectory(ftpInfo.getRemoteDirectory());
        synchronizer.setDeleteRemoteFiles(false);
        return synchronizer;
    }

    @Bean
    public Map<String, SftpInboundFileSynchronizingMessageSource> sftpInboundAdapterMap(
            Map<String, DefaultSftpSessionFactory> sftpSessionFactoryMap) {
        Map<String, SftpInboundFileSynchronizingMessageSource> adapterMap = new HashMap<>();

        properties.getConfigs().forEach((key, configInfo) -> {
            AsyncReceiveInfo asyncInfo = configInfo.getAsyncReceiveInfo();
            if (asyncInfo != null && asyncInfo.isEnable()
                    && asyncInfo.getReceiveConnectType() == ConnectType.SFTP
                    && sftpSessionFactoryMap.containsKey(key)) {

                SftpInfo sftpInfo = asyncInfo.getSftp();
                DefaultSftpSessionFactory sessionFactory = sftpSessionFactoryMap.get(key);

                SftpInboundFileSynchronizingMessageSource adapter = new SftpInboundFileSynchronizingMessageSource(makeSftpInboundSynchronizer(sessionFactory, sftpInfo));
                adapter.setLocalDirectory(new File(sftpInfo.getLocalDirectory()));
                adapter.setAutoCreateLocalDirectory(true);
                log.debug("Registered SFTP inbound adapter for key: {}", key);
                ((ConfigurableApplicationContext) context).getBeanFactory().registerSingleton(key + adapter.getClass().getName(), adapter);

                adapterMap.put(key, adapter);
            }
        });

        return adapterMap;
    }

    private SftpInboundFileSynchronizer makeSftpInboundSynchronizer(DefaultSftpSessionFactory sessionFactory, SftpInfo sftpInfo) {
        SftpInboundFileSynchronizer synchronizer = new SftpInboundFileSynchronizer(sessionFactory);
        synchronizer.setRemoteDirectory(sftpInfo.getRemoteDirectory());
        synchronizer.setDeleteRemoteFiles(false);
        return synchronizer;
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
    public Map<String, TcpInboundGateway> inboundGatewayMap(
            Map<String, AbstractServerConnectionFactory> serverConnectionFactoryMap,
            MessageChannel inboundChannel,
            MessageChannel replyChannel) {
        Map<String, TcpInboundGateway> gatewayMap = new HashMap<>();

        serverConnectionFactoryMap.forEach((key, connectionFactory) -> {
            TcpInboundGateway tcpInboundGateway = tcpInboundGateway(connectionFactory, inboundChannel, replyChannel);
            log.debug("Registered TCP inbound gateway for key: {}", key);
            ((ConfigurableApplicationContext) context).getBeanFactory().registerSingleton(key + tcpInboundGateway.getClass().getName(), tcpInboundGateway);
            gatewayMap.put(key, tcpInboundGateway);
        });

        return gatewayMap;
    }

    public TcpInboundGateway tcpInboundGateway(AbstractServerConnectionFactory connectionFactory,
                                               MessageChannel inboundChannel,
                                               MessageChannel replyChannel) {
        TcpInboundGateway tcpInboundGateway = new TcpInboundGateway();
        tcpInboundGateway.setConnectionFactory(connectionFactory);
        tcpInboundGateway.setRequestChannel(inboundChannel);
        tcpInboundGateway.setReplyChannel(replyChannel);
        return tcpInboundGateway;
    }
}
