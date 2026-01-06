package pe.kr.thekey.framework.adaptor.util;

import java.io.IOException;

public interface ConnectionObject {
    void connect() throws IOException;

    void disconnect() throws IOException;

    byte[] writeRead(String uuid, String channelId, byte[] data) throws IOException;

    void write(String uuid, String channelId, byte[] data) throws IOException;

    byte[] read(int length) throws IOException;

    boolean isConnected();
}
