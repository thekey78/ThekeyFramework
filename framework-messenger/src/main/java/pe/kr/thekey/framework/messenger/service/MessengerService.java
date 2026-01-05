package pe.kr.thekey.framework.messenger.service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

public interface MessengerService {
    @PostConstruct
    void init();

    @PreDestroy
    void destroy();

    Object parse(String streamName, String message);

    String marshal(String streamName, Object bean);

    void reload();
}
