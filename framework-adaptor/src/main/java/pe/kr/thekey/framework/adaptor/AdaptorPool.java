package pe.kr.thekey.framework.adaptor;

import pe.kr.thekey.framework.adaptor.exception.ExternalException;

public interface AdaptorPool {
    byte[] execute(String uuid, String channelId, byte[] data) throws ExternalException;
}
