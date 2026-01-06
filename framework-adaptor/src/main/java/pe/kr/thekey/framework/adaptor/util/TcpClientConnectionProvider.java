package pe.kr.thekey.framework.adaptor.util;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TcpClientConnectionProvider {
    private final AdaptorProperties properties;

    private AdaptorProperties.AsyncReceiveInfo getAsyncReceiverInfo(String key) {
        AdaptorProperties.AdaptorConfigInfo adaptorConfigInfo = properties.getConfigs().get(key);
        if(adaptorConfigInfo != null) {
            return adaptorConfigInfo.getAsyncReceiveInfo();
        }
        return null;
    }
}
