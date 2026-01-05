package pe.kr.thekey.framework.adaptor.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

@Slf4j
@RequiredArgsConstructor
public class ConnectionObject {
    @Getter
    private final AdaptorProperties.HostInfo hostInfo;

    @Getter
    protected SocketChannel socket;

    public void connect() throws IOException {
        if (socket != null && socket.isOpen()) {
            return;
        }

        log.info("Connecting to {}:{}...", hostInfo.getIp(), hostInfo.getPort());
        try {
            socket = SocketChannel.open();
            socket.configureBlocking(true);
            socket.socket().connect(new InetSocketAddress(hostInfo.getIp(), hostInfo.getPort()), hostInfo.getConnectionTimeout());
            socket.socket().setSoTimeout(hostInfo.getReadTimeout());
            log.info("Successfully connected to {}:{}", hostInfo.getIp(), hostInfo.getPort());
        } catch (IOException e) {
            log.error("Failed to connect to {}:{}", hostInfo.getIp(), hostInfo.getPort(), e);
            throw e;
        }
    }

    public void disconnect() throws IOException {
        if (socket != null && socket.isOpen()) {
            log.info("Disconnecting from {}:{}...", hostInfo.getIp(), hostInfo.getPort());
            socket.close();
        }
    }
    public void execute(String uuid, String channelId, byte[] data) throws IOException {
        if (socket == null || !socket.isOpen()) {
            throw new IllegalStateException("Socket is not connected");
        }

        writeLog(uuid, channelId, data);
        socket.write(ByteBuffer.wrap(data));
    }

    public byte[] read(int length) throws IOException {
        if (socket == null || !socket.isOpen()) {
            throw new IllegalStateException("Socket is not connected");
        }

        ByteBuffer responseBuffer = ByteBuffer.allocate(length);
        socket.read(responseBuffer);
        responseBuffer.flip();
        return responseBuffer.array();
    }

    public boolean isConnected() {
        return socket != null && socket.isOpen();
    }

    protected void writeLog(String uuid, String channelId, byte[] data) {

    }
}
