package pe.kr.thekey.framework.adaptor.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.restclient.autoconfigure.service.HttpServiceClientAutoConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.http.HttpClient;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.time.Duration;

@Slf4j
@RequiredArgsConstructor
public class HttpConnectionObject implements ConnectionObject {
    @Getter
    private final AdaptorProperties.HostInfo hostInfo;

    @Getter
    private final AdaptorProperties.DataType dataType;

    @Getter
    protected RestClient connection;

    protected byte[] response;

    @Override
    public void connect() throws IOException {
        if (connection != null) {
            return;
        }
        HttpComponentsClientHttpRequestFactory factory =
                new HttpComponentsClientHttpRequestFactory();
        factory.setReadTimeout(hostInfo.getReadTimeout());
        factory.setConnectionRequestTimeout(hostInfo.getConnectionTimeout());

        connection = RestClient.builder()
                .requestFactory(factory)
                .build();
    }

    @Override
    public void disconnect() throws IOException {
        if (connection != null) {
            log.info("Disconnecting from {}", hostInfo.getUrl());
            connection = null;
        }
    }

    @Override
    public byte[] writeRead(String uuid, String channelId, byte[] data) throws IOException {
        write(uuid, channelId, data);
        return read(1024 * 8);
    }

    @Override
    public void write(String uuid, String channelId, byte[] data) throws IOException {
        if (connection == null) {
            throw new IllegalStateException("Socket is not connected");
        }

        writeLog(uuid, channelId, data);
        response = connection.post()
                .uri(hostInfo.getUrl())
                .contentLength(data.length)
                .contentType(switch (dataType){
                    case JSON -> MediaType.APPLICATION_JSON;
                    case XML -> MediaType.APPLICATION_XML;
                    default -> MediaType.TEXT_PLAIN;
                })
                .body(outputStream -> {
                    outputStream.write(data);
                })
                .retrieve()
                .body(byte[].class);
    }

    @Override
    public byte[] read(int length) throws IOException {
        return response;
    }

    @Override
    public boolean isConnected() {
        return connection != null;
    }

    protected void writeLog(String uuid, String channelId, byte[] data) {
        log.info("Writing data to socket for UUID: {}, Channel: {}, Data: {}", uuid, channelId, new String(data));
    }
}
