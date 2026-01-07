package pe.kr.thekey.framework.adaptor;

import lombok.Getter;
import pe.kr.thekey.framework.adaptor.util.AdaptorProperties;

public class AdaptorPoolFactory {
    @Getter
    private AdaptorProperties properties;
    public AdaptorPoolFactory(AdaptorProperties properties) {
         this.properties = properties;
    }

    public AdaptorPool getAdaptorPool() {
        return null;
    }

    protected void makeAdaptorPool() {

    }
}
