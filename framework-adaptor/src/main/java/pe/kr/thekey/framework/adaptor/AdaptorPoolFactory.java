package pe.kr.thekey.framework.adaptor;

import lombok.Getter;
import pe.kr.thekey.framework.adaptor.basic.DefaultAdaptorPool;
import pe.kr.thekey.framework.adaptor.util.AdaptorConverter;
import pe.kr.thekey.framework.adaptor.util.AdaptorProperties;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AdaptorPoolFactory {
    @Getter
    private final AdaptorProperties properties;
    private final AdaptorConverter converter;
    private final Map<String, AdaptorPool> poolMap = new ConcurrentHashMap<>();

    public AdaptorPoolFactory(AdaptorProperties properties, AdaptorConverter converter) {
         this.properties = properties;
         this.converter = converter;
         makeAdaptorPool();
    }

    public AdaptorPool getAdaptorPool(String channelId) {
        return poolMap.get(channelId);
    }

    protected void makeAdaptorPool() {
        properties.getConfigs().forEach((key, config) -> {
            poolMap.put(key, new DefaultAdaptorPool(config, converter));
        });
    }
}
