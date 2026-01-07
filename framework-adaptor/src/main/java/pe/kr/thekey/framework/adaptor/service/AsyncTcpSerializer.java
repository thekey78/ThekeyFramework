package pe.kr.thekey.framework.adaptor.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.ip.tcp.serializer.AbstractPooledBufferByteArraySerializer;

import java.io.*;

@Slf4j
public class AsyncTcpSerializer extends AbstractPooledBufferByteArraySerializer {
    @Override
    protected byte[] doDeserialize(InputStream inputStream, byte[] buffer) throws IOException {
        log.debug("Deserializing message from input stream");
        ByteArrayOutputStream dataOutputStream = new ByteArrayOutputStream();
        // 첫 번째 read는 데이터가 올 때까지 블로킹됩니다.
        int read = inputStream.read(buffer);
        if (read <= 0) {
            return null;
        }
        dataOutputStream.write(buffer, 0, read);

        // 추가 데이터가 바로 가용하다면 더 읽고, 아니면 지금까지 읽은 것만 반환합니다.
        // 이는 데이터가 한 번에 다 들어온다는 가정(테스트 환경)에서 작동합니다.
        while (inputStream.available() > 0 && (read = inputStream.read(buffer)) > 0) {
            dataOutputStream.write(buffer, 0, read);
        }
        return dataOutputStream.toByteArray();
    }

    @Override
    public void serialize(byte[] object, OutputStream outputStream) throws IOException {
        log.debug("Serializing message to output stream");
        outputStream.write(object);
    }
}
