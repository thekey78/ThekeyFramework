package pe.kr.thekey.framework.adaptor.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

@Slf4j
@RequiredArgsConstructor
public class HttpConnectionObject implements ConnectionObject {
    @Getter
    private final AdaptorProperties.HostInfo hostInfo;

    @Getter
    private final AdaptorProperties.DataType dataType;

    @Getter
    private final String encoding;

    @Getter
    protected RestClient connection;

    protected byte[] response;

    @Override
    public void connect() throws IOException {
        if (connection != null) {
            return;
        }
        
        connection = RestClient.builder()
                .requestFactory(new HttpComponentsClientHttpRequestFactory())
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
            connect();
        }

        writeLog(uuid, channelId, data);
        
        MediaType contentType = switch (dataType) {
            case JSON -> MediaType.APPLICATION_JSON;
            case XML -> MediaType.APPLICATION_XML;
            default -> MediaType.TEXT_PLAIN;
        };
        
        // 인코딩 적용을 위해 Charset 추가
        try {
            Charset charset = Charset.forName(encoding);
            contentType = new MediaType(contentType, charset);
        } catch (UnsupportedCharsetException e) {
            log.warn("Unsupported encoding: {}, using default", encoding);
        }

        response = connection.post()
                .uri(hostInfo.getUrl())
                .contentType(contentType)
                .body(data)
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
        try {
            log.info("Writing data to HTTP for UUID: {}, Channel: {}, Data: {}", uuid, channelId, new String(data, encoding));
        } catch (java.io.UnsupportedEncodingException e) {
            log.error("Unsupported encoding: {}", encoding, e);
            log.info("Writing data to HTTP for UUID: {}, Channel: {}, Data: {}", uuid, channelId, new String(data));
        }
    }
}
