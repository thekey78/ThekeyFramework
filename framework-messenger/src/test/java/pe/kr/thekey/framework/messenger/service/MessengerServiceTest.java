package pe.kr.thekey.framework.messenger.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pe.kr.thekey.framework.core.config.FrameworkCoreConfig;
import pe.kr.thekey.framework.messenger.config.MessengerConfig;
import pe.kr.thekey.framework.messenger.dto.MessageDto;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest(classes = {MessengerConfig.class, FrameworkCoreConfig.class})
public class MessengerServiceTest {
    @Autowired
    private MessengerService messengerService;

    /**
     * Tests message parsing and marshaling via service
     */
    @Test
    public void testParseAndMarshal() {
        String message = "ID00000001CONTENT             CHECK";
        Object result = messengerService.parse("testStream", message);

        assertInstanceOf(MessageDto.class, result);
        MessageDto dto = (MessageDto) result;

        assertEquals("ID00000001", dto.getHeader().get("id"));
        assertEquals("CONTENT", dto.getData().get("content"));
        assertEquals("CHECK", dto.getFooter().get("checksum"));

        String marshaled = messengerService.marshal("testStream", dto);
        assertEquals(message, marshaled);
    }
}
