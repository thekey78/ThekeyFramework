package pe.kr.thekey.framework.adaptor.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import pe.kr.thekey.framework.adaptor.util.AdaptorProperties;
import pe.kr.thekey.framework.core.utils.ApplicationContextHolder;
import pe.kr.thekey.framework.core.utils.FrameworkCoreProperties;

import java.util.Objects;

@RequiredArgsConstructor
public abstract class AbstractAsyncReceiver {
    private final AdaptorProperties.AsyncReceiveInfo asyncReceiveInfo;

    @PostConstruct
    public void init() {
        FrameworkCoreProperties coreProperties = ApplicationContextHolder.getApplicationContext().getBean(FrameworkCoreProperties.class);
        String wasIdName = coreProperties.getWasIdName();

        if (Objects.equals(System.getProperty(wasIdName), asyncReceiveInfo.getReceiveWasId())) {
            if(asyncReceiveInfo.getReceiveConnectType() == AdaptorProperties.ConnectType.SOCKET) {
                makeReceiveSocket();
            }
        }
    }

    private void makeReceiveSocket() {

    }
}
