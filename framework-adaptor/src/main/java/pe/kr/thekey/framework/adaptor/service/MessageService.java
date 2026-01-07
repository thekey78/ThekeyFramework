package pe.kr.thekey.framework.adaptor.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MessageService {

    public byte[] processMessage(byte[] message) {
        log.debug("Received message: {}", new String(message));
        return message;
    }
}
