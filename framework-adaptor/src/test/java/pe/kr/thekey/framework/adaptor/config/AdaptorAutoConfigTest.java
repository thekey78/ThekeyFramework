package pe.kr.thekey.framework.adaptor.config;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.integration.ftp.session.DefaultFtpSessionFactory;
import pe.kr.thekey.framework.adaptor.util.AdaptorConverter;
import pe.kr.thekey.framework.adaptor.util.AdaptorProperties;
import pe.kr.thekey.framework.core.config.FrameworkCoreConfig;
import pe.kr.thekey.framework.messenger.config.MessengerConfig;

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
@SpringBootConfiguration
@SpringBootTest(useMainMethod = SpringBootTest.UseMainMethod.NEVER,
        classes = {AdaptorAutoConfig.class, SocketAdaptorAutoConfig.class, FrameworkCoreConfig.class, MessengerConfig.class},
        properties = {
                "logging.level.root=info",
                "logging.level.pe.kr.thekey.framework=debug",
                "logging.level.org.springframework.integration=debug",
                "thekey.framework.adaptor.enable=true",
                "thekey.framework.adaptor.configs.EAI.enable=true",
                "thekey.framework.adaptor.configs.EAI.async=true",
                "thekey.framework.adaptor.configs.EAI.dataType=binary",
                "thekey.framework.adaptor.configs.EAI.connectType=socket",
                "thekey.framework.adaptor.configs.EAI.hosts[0].ip=127.0.0.1",
                "thekey.framework.adaptor.configs.EAI.asyncReceiveInfo.enable=true",
                "thekey.framework.adaptor.configs.EAI.asyncReceiveInfo.receiveConnectType=socket",
                "thekey.framework.adaptor.configs.EAI.asyncReceiveInfo.tcpIp.port=9898"
        }
)
public class AdaptorAutoConfigTest {

    @Autowired
    private AdaptorAutoConfig adaptorAutoConfig;

    @Autowired
    private AdaptorProperties adaptorProperties;

    @Autowired
    private AdaptorConverter adaptorConverter;


    @Test
    public void testJsonConversion() throws Exception {
        Map<String, String> data = new HashMap<>();
        data.put("key", "value");
        data.put("name", "홍길동");
        
        byte[] converted = adaptorConverter.convertToBytes(data, AdaptorProperties.DataType.JSON, "UTF-8");
        assertNotNull(converted);
        
        Map result = (Map) adaptorConverter.convertFromBytes(converted, Map.class, AdaptorProperties.DataType.JSON, "UTF-8");
        assertEquals("value", result.get("key"));
        assertEquals("홍길동", result.get("name"));
    }

    @Test
    public void testXmlConversion() throws Exception {
        TestVo vo = new TestVo();
        vo.setName("테스트");
        vo.setAge(20);
        
        byte[] converted = adaptorConverter.convertToBytes(vo, AdaptorProperties.DataType.XML, "UTF-8");
        assertNotNull(converted);
        
        TestVo result = (TestVo) adaptorConverter.convertFromBytes(converted, TestVo.class, AdaptorProperties.DataType.XML, "UTF-8");
        assertEquals("테스트", result.getName());
        assertEquals(20, result.getAge());
    }

    public static class TestVo {
        private String name;
        private int age;
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public int getAge() { return age; }
        public void setAge(int age) { this.age = age; }
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

        Map<String, AdaptorProperties.AdaptorConfigInfo> mockConfigs = new HashMap<>();
        mockConfigs.put("testKey", configInfo);
        adaptorProperties.setConfigs(mockConfigs);

        // Get the ftpSessionFactoryMap bean
        Map<String, DefaultFtpSessionFactory> factoryMap = adaptorAutoConfig.ftpSessionFactoryMap();

        // Verify the map is not null and contains the mock configuration key
        assertNotNull(factoryMap, "The ftpSessionFactoryMap should not be null");
        assertNotNull(factoryMap.get("testKey"), "The ftpSessionFactory should be created for the configuration key");
    }

    @Test
    public void testFtpSessionFactoryMapWithoutConfigurations() {
        adaptorProperties.setConfigs(new HashMap<>());

        // Get the ftpSessionFactoryMap bean
        Map<String, DefaultFtpSessionFactory> factoryMap = adaptorAutoConfig.ftpSessionFactoryMap();

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
            byte[] content = "Test data".getBytes();
            byte[] dataToSend = new byte[content.length + 1];
            System.arraycopy(content, 0, dataToSend, 0, content.length);

            outputStream.write(dataToSend);
            outputStream.flush();
//            socket.shutdownOutput();

            log.debug("Sent data to server: Test data");
            byte[] readData = new byte[1024];
            int bytesRead = inputStream.read(readData);
            assertTrue(bytesRead > 0, "Expected data to be received from server");
            assertEquals("Test data", new String(readData, 0, bytesRead-1), "Received data should match sent data");
        }
    }

    @Test
    public void testByteCode() {
        log.debug("{}, {}", (byte) -1, (byte) 255);
    }
}