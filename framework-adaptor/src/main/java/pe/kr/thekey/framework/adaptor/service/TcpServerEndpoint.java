package pe.kr.thekey.framework.adaptor.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;

@Slf4j
@RequiredArgsConstructor
@MessageEndpoint
public class TcpServerEndpoint {
    private final MessageService messageService;

    @ServiceActivator(inputChannel = "inboundChannel", outputChannel = "replyChannel", autoStartup = "true")
    public byte[] handleMessage(byte[] message) {
        log.debug("Received message: {}", new String(message));
        return messageService.processMessage(message);
    }
}