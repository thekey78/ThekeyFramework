package pe.kr.thekey.framework.adaptor.service;

import org.springframework.integration.ip.tcp.serializer.AbstractPooledBufferByteArraySerializer;

import java.io.*;

public class AsyncTcpSerializer extends AbstractPooledBufferByteArraySerializer {
    @Override
    protected byte[] doDeserialize(InputStream inputStream, byte[] buffer) throws IOException {
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
        outputStream.write(object);
    }
}
