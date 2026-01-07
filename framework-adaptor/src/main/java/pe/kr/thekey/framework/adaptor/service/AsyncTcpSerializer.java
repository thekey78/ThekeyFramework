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
        int read;
        do {
            read = inputStream.read(buffer);
            dataOutputStream.write(buffer, 0, read);
        } while (read > 0);
        return dataOutputStream.toByteArray();
    }

    @Override
    public void serialize(byte[] object, OutputStream outputStream) throws IOException {
        log.debug("Serializing message to output stream");
        outputStream.write(object);
    }
}
