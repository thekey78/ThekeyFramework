package pe.kr.thekey.framework.adaptor.config;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.integration.ftp.session.DefaultFtpSessionFactory;
import org.springframework.integration.ip.tcp.connection.AbstractServerConnectionFactory;
import pe.kr.thekey.framework.adaptor.service.AsyncTcpSerializer;
import pe.kr.thekey.framework.adaptor.util.AdaptorProperties;
import pe.kr.thekey.framework.core.config.FrameworkCoreConfig;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for AdaptorConfig, focusing on the serializer method.
 * The goal is to ensure that the serializer Bean is properly configured and instantiated.
 */
@Slf4j
@SpringBootTest(useMainMethod = SpringBootTest.UseMainMethod.NEVER,
        classes = {AdaptorConfig.class, FrameworkCoreConfig.class},
        properties = {
                "logging.level.root=info",
                "logging.level.pe.kr.thekey.framework=debug",
                "thekey.framework.adaptor.enable=true",
                "thekey.framework.adaptor.configs.EAI.enable=true",
                "thekey.framework.adaptor.configs.EAI.async=true",
                "thekey.framework.adaptor.configs.EAI.dataType=BINARY",
                "thekey.framework.adaptor.configs.EAI.connectType=SOCKET",
                "thekey.framework.adaptor.configs.EAI.hosts[0].ip=127.0.0.1",
                "thekey.framework.adaptor.configs.EAI.asyncReceiveInfo.enable=true",
                "thekey.framework.adaptor.configs.EAI.asyncReceiveInfo.receiveConnectType=SOCKET",
                "thekey.framework.adaptor.configs.EAI.asyncReceiveInfo.tcpIp.port=9898"
        }
)
public class AdaptorConfigTest {

    @Autowired
    private AdaptorConfig adaptorConfig;

    @Autowired
    private AdaptorProperties adaptorProperties;

    @Test
    public void testSerializerBean() {
        // Verify that the serializer bean is created and not null
        AsyncTcpSerializer serializer = adaptorConfig.serializer();
        assertNotNull(serializer, "The serializer bean should not be null");
    }

    @Test
    public void testFtpSessionFactoryMapWithConfigurations() {
        // Mock a configuration
        AdaptorProperties.FtpInfo ftpInfo = new AdaptorProperties.FtpInfo();
        ftpInfo.setHost("test.ftp.server");
        ftpInfo.setPort(21);
        ftpInfo.setUsername("testUser");
        ftpInfo.setPassword("testPassword");
        ftpInfo.setPassiveMode(true);
        ftpInfo.setConnectionTimeout(5000);
        ftpInfo.setDataTimeout(3000);

        AdaptorProperties.AsyncReceiveInfo asyncReceiveInfo = new AdaptorProperties.AsyncReceiveInfo();
        asyncReceiveInfo.setEnable(true);
        asyncReceiveInfo.setReceiveConnectType(AdaptorProperties.ConnectType.FTP);
        asyncReceiveInfo.setFtp(ftpInfo);

        AdaptorProperties.AdaptorConfigInfo configInfo = new AdaptorProperties.AdaptorConfigInfo();
        configInfo.setAsyncReceiveInfo(asyncReceiveInfo);

        Map<String, AdaptorProperties.AdaptorConfigInfo> mockConfigs = Map.of("testKey", configInfo);
        adaptorProperties.setConfigs(mockConfigs);

        // Get the ftpSessionFactoryMap bean
        Map<String, DefaultFtpSessionFactory> factoryMap = adaptorConfig.ftpSessionFactoryMap();

        // Verify the map is not null and contains the mock configuration key
        assertNotNull(factoryMap, "The ftpSessionFactoryMap should not be null");
        assertNotNull(factoryMap.get("testKey"), "The ftpSessionFactory should be created for the configuration key");
    }

    @Test
    public void testServerConnectionFactoryMapWithConfigurations() {
        // Mock a configuration
        AdaptorProperties.TcpIpInfo tcpIpInfo = new AdaptorProperties.TcpIpInfo();
        tcpIpInfo.setPort(9090);
        tcpIpInfo.setReadTimeout(10000);

        AdaptorProperties.AsyncReceiveInfo asyncReceiveInfo = new AdaptorProperties.AsyncReceiveInfo();
        asyncReceiveInfo.setEnable(true);
        asyncReceiveInfo.setTcpIp(tcpIpInfo);

        AdaptorProperties.AdaptorConfigInfo configInfo = new AdaptorProperties.AdaptorConfigInfo();
        configInfo.setAsyncReceiveInfo(asyncReceiveInfo);

        Map<String, AdaptorProperties.AdaptorConfigInfo> mockConfigs = Map.of("testKey", configInfo);
        adaptorProperties.setConfigs(mockConfigs);

        // Get the serverConnectionFactoryMap bean
        Map<String, AbstractServerConnectionFactory> factoryMap = adaptorConfig.serverConnectionFactoryMap(new AsyncTcpSerializer());

        // Verify the map is not null and contains the mock configuration key
        assertNotNull(factoryMap, "The serverConnectionFactoryMap should not be null");
        assertNotNull(factoryMap.get("testKey"), "The serverConnectionFactory should be created for the configuration key");
        assertEquals(9090, factoryMap.get("testKey").getPort(), "The port of the server connection factory should match the configuration");
    }

    @Test
    public void testServerConnectionFactoryMapWithInvalidConfiguration() {
        // Create invalid configuration
        AdaptorProperties.TcpIpInfo tcpIpInfo = new AdaptorProperties.TcpIpInfo();
        tcpIpInfo.setPort(-1); // Invalid port number

        AdaptorProperties.AsyncReceiveInfo asyncReceiveInfo = new AdaptorProperties.AsyncReceiveInfo();
        asyncReceiveInfo.setEnable(true);
        asyncReceiveInfo.setTcpIp(tcpIpInfo);

        AdaptorProperties.AdaptorConfigInfo configInfo = new AdaptorProperties.AdaptorConfigInfo();
        configInfo.setAsyncReceiveInfo(asyncReceiveInfo);

        Map<String, AdaptorProperties.AdaptorConfigInfo> mockConfigs = Map.of("invalidKey", configInfo);
        adaptorProperties.setConfigs(mockConfigs);

        // Get the serverConnectionFactoryMap bean
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                adaptorConfig.serverConnectionFactoryMap(new AsyncTcpSerializer()));
        assertTrue(exception.getMessage().contains("Invalid port number"));
    }

    @Test
    public void testServerConnectionFactoryMapWithMultipleConfigurations() {
        // Mock multiple configurations
        AdaptorProperties.TcpIpInfo firstTcpIpInfo = new AdaptorProperties.TcpIpInfo();
        firstTcpIpInfo.setPort(8080);
        firstTcpIpInfo.setReadTimeout(5000);

        AdaptorProperties.AsyncReceiveInfo firstAsyncReceiveInfo = new AdaptorProperties.AsyncReceiveInfo();
        firstAsyncReceiveInfo.setEnable(true);
        firstAsyncReceiveInfo.setTcpIp(firstTcpIpInfo);

        AdaptorProperties.AdaptorConfigInfo firstConfigInfo = new AdaptorProperties.AdaptorConfigInfo();
        firstConfigInfo.setAsyncReceiveInfo(firstAsyncReceiveInfo);

        AdaptorProperties.TcpIpInfo secondTcpIpInfo = new AdaptorProperties.TcpIpInfo();
        secondTcpIpInfo.setPort(7070);
        secondTcpIpInfo.setReadTimeout(8000);

        AdaptorProperties.AsyncReceiveInfo secondAsyncReceiveInfo = new AdaptorProperties.AsyncReceiveInfo();
        secondAsyncReceiveInfo.setEnable(true);
        secondAsyncReceiveInfo.setTcpIp(secondTcpIpInfo);

        AdaptorProperties.AdaptorConfigInfo secondConfigInfo = new AdaptorProperties.AdaptorConfigInfo();
        secondConfigInfo.setAsyncReceiveInfo(secondAsyncReceiveInfo);

        Map<String, AdaptorProperties.AdaptorConfigInfo> mockConfigs = Map.of(
                "firstKey", firstConfigInfo,
                "secondKey", secondConfigInfo
        );
        adaptorProperties.setConfigs(mockConfigs);

        // Get the serverConnectionFactoryMap bean
        Map<String, AbstractServerConnectionFactory> factoryMap = adaptorConfig.serverConnectionFactoryMap(new AsyncTcpSerializer());

        // Verify the map is not null and contains both keys
        assertNotNull(factoryMap, "The serverConnectionFactoryMap should not be null");
        assertEquals(2, factoryMap.size(), "The serverConnectionFactoryMap should contain two entries");
        assertEquals(8080, factoryMap.get("firstKey").getPort(), "The port of the first server connection factory should match the configuration");
        assertEquals(7070, factoryMap.get("secondKey").getPort(), "The port of the second server connection factory should match the configuration");
    }

    @Test
    public void testServerConnectionFactoryMapWithoutConfigurations() {
        // Mock an empty configuration
//        when(adaptorProperties.getConfigs()).thenReturn(Map.of());
        adaptorProperties.setConfigs(Map.of());

        // Get the serverConnectionFactoryMap bean
        Map<String, AbstractServerConnectionFactory> factoryMap = adaptorConfig.serverConnectionFactoryMap(new AsyncTcpSerializer());

        // Verify the map is not null and empty
        assertNotNull(factoryMap, "The serverConnectionFactoryMap should not be null");
        assertTrue(factoryMap.isEmpty(), "The serverConnectionFactoryMap should be empty when there are no configurations");
    }

    @Test
    public void testFtpSessionFactoryMapWithoutConfigurations() {
        // Mock an empty configuration
//        when(adaptorProperties.getConfigs()).thenReturn(Map.of());
        adaptorProperties.setConfigs(Map.of());

        // Get the ftpSessionFactoryMap bean
        Map<String, DefaultFtpSessionFactory> factoryMap = adaptorConfig.ftpSessionFactoryMap();

        // Verify the map is not null and empty
        assertNotNull(factoryMap, "The ftpSessionFactoryMap should not be null");
        assertTrue(factoryMap.isEmpty(), "The ftpSessionFactoryMap should be empty when there are no configurations");
    }


    @Test
    public void testServerConnectionFactoryWithSendData() throws IOException {
        log.debug("adaptorProperties: {}", adaptorProperties);
        try(
                Socket socket = new Socket("127.0.0.1", 9898);
                OutputStream outputStream = socket.getOutputStream();
                InputStream inputStream = socket.getInputStream()
        ) {
            log.debug("Connected to server: {} {}", socket.getInetAddress(), socket.getPort());
            outputStream.write("Test data".getBytes());
            outputStream.flush();
            // 데이터 송신 완료를 알림 (서버의 read()가 -1 또는 0을 반환할 수 있게 함)
            socket.shutdownOutput();

            log.debug("Sent data to server: Test data");
            byte[] readData = new byte[1024];
            int bytesRead = inputStream.read(readData);
            if(bytesRead > 0)
                log.debug("Received data from server: {}", new String(readData, 0, bytesRead));
            else
                log.debug("No data received from server");
        }
    }
}