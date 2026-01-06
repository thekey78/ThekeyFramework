package pe.kr.thekey.framework.adaptor.service;

import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;

@MessageEndpoint
public class TcpServerEndpoint {
    private final MessageService messageService;

    public TcpServerEndpoint(MessageService messageService) {
        this.messageService = messageService;
    }

    @ServiceActivator(inputChannel = "inboundChannel", outputChannel = "replyChannel", autoStartup = "true")
    public byte[] handleMessage(byte[] message) {
        return messageService.processMessage(message);
    }
}
