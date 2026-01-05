package pe.kr.thekey.framework.messenger.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pe.kr.thekey.framework.core.config.FrameworkCoreConfig;
import pe.kr.thekey.framework.messenger.config.MessengerConfig;
import pe.kr.thekey.framework.messenger.dto.MessageDto;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
@SpringBootTest(classes = {MessengerConfig.class, FrameworkCoreConfig.class}, properties = {"thekey.framework.messenger.file-change-on-load=false"})
public class MessengerServiceManualReloadTest {
    @Autowired
    private MessengerService messengerService;

    @Test
    public void testReload_fileChangeReload() throws Exception {
        // Update properties manually for test
        // In real scenario, this would be in application.yml or updated via external call
        // Here we just test the loadMappings functionality
        Path tempDir = Paths.get("C:\\workspace\\ThekeyFramework\\framework-messenger\\target\\test-classes\\messages\\body");
        log.info("Created temporary directory for testing: {}", tempDir.toAbsolutePath().toFile().getAbsolutePath());
        Path mappingFile = tempDir.resolve("dynamic-mapping.xml");

        String mappingContent = """
                <beanio xmlns="http://www.beanio.org/2012/03"
                        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                        xsi:schemaLocation="http://www.beanio.org/2012/03 http://www.beanio.org/2012/03/mapping.xsd">
                
                    <stream name="dynamicStream" format="fixedlength">
                        <record name="testRecord" class="pe.kr.thekey.framework.messenger.dto.MessageDto">
                            <!-- Header 영역: MessageDto의 header(Map)에 매핑 -->
                            <segment name="header" class="map">
                                <field name="id" length="10" />
                            </segment>
                
                            <!-- Body 영역: MessageDto의 data(Map)에 매핑 -->
                            <segment name="data" class="map">
                                <field name="content" length="20" />
                            </segment>
                
                            <!-- Footer 영역: MessageDto의 footer(Map)에 매핑 -->
                            <segment name="footer" class="map">
                                <field name="checksum" length="5" />
                            </segment>
                        </record>
                    </stream>
                </beanio>
                """;

        Files.write(mappingFile, mappingContent.getBytes());
        messengerService.reload();

        MessageDto messageDto = new MessageDto();
        messageDto.setHeader(Map.of("id", "1234567890"));
        messageDto.setData(Map.of("content", "Hello, World!"));
        messageDto.setFooter(Map.of("checksum", "12345"));
        String marshal = messengerService.marshal("dynamicStream", messageDto);
        assertEquals("1234567890Hello, World!       12345", marshal);

        Files.delete(mappingFile);
        messengerService.reload();
        assertThrows(IllegalArgumentException.class,  () -> messengerService.marshal("dynamicStream", messageDto));
    }
}
