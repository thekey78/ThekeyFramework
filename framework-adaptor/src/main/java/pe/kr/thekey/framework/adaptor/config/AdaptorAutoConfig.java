package pe.kr.thekey.framework.adaptor.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.integration.ftp.inbound.FtpInboundFileSynchronizer;
import org.springframework.integration.ftp.inbound.FtpInboundFileSynchronizingMessageSource;
import org.springframework.integration.ftp.session.DefaultFtpSessionFactory;
import org.springframework.integration.sftp.inbound.SftpInboundFileSynchronizer;
import org.springframework.integration.sftp.inbound.SftpInboundFileSynchronizingMessageSource;
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;
import pe.kr.thekey.framework.adaptor.AdaptorPoolFactory;
import pe.kr.thekey.framework.adaptor.util.AdaptorConverter;
import pe.kr.thekey.framework.adaptor.util.AdaptorProperties;
import pe.kr.thekey.framework.adaptor.util.AdaptorProperties.*;
import pe.kr.thekey.framework.messenger.service.MessengerService;

import java.io.File;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@AutoConfiguration
@ComponentScan("pe.kr.thekey.framework.adaptor")
@EnableConfigurationProperties({AdaptorProperties.class})
@ConditionalOnProperty(prefix = "thekey.framework.adaptor", name = "enable", havingValue = "true", matchIfMissing = true)
public class AdaptorAutoConfig {
    private final ApplicationContext context;
    private final AdaptorProperties properties;

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
    public RestTemplateBuilder restTemplateBuilder() {
        return new RestTemplateBuilder();
    }

    @Bean
    public AdaptorPoolFactory adaptorPoolFactory(AdaptorProperties properties, AdaptorConverter converter) {
        return new AdaptorPoolFactory(properties, converter);
    }

    @Bean
    public AdaptorConverter adaptorConverter(MessengerService messengerService) {
        return new AdaptorConverter(messengerService);
    }
}
