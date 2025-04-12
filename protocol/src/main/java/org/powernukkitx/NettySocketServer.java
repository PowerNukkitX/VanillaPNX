package org.powernukkitx;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.Getter;
import lombok.SneakyThrows;
import org.powernukkitx.netty.HandleByteBuf;
import org.powernukkitx.packet.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public abstract class NettySocketServer {

    protected int destinationPort;

    protected ServerSocket socket;
    protected Thread socketThread;

    @Getter
    private boolean alive = false;

    @SneakyThrows
    public NettySocketServer(int port, int destinationPort) {
        this.socket = new ServerSocket(port);
        this.socketThread = new Thread(() -> {
            loop();
        });
        this.socketThread.start();
        this.destinationPort = destinationPort;
    }

    @SneakyThrows
    protected void loop() {
        alive = true;
        while (isAlive()) {
            long time = System.currentTimeMillis();
            Socket inputSocket = socket.accept();
            BufferedInputStream bufferedStream = new BufferedInputStream(inputSocket.getInputStream());
            ByteBuf byteBuf = Unpooled.wrappedBuffer(bufferedStream.readAllBytes());
            Packet packet = decode(byteBuf);
            onPacket(packet);
        }
    }

    public void send(Packet packet) {
        HandleByteBuf buf = HandleByteBuf.of(Unpooled.buffer());
        buf.writeByte(packet.getPid());
        packet.encode(buf);
        byte[] data = new byte[buf.readableBytes()];
        buf.readBytes(data);
        try (Socket socket = new Socket("127.0.0.1", destinationPort); OutputStream out = socket.getOutputStream()) {
             out.write(data);
             out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected final Packet decode(ByteBuf byteBuf) {
        int pid = byteBuf.readByte();
        Packet packet = switch (pid) {
            case ProtocolInfo.CLIENT_HELLO -> new ClientHello();
            case ProtocolInfo.SERVER_HELLO -> new ServerHello();
            case ProtocolInfo.CLIENT_HEARTBEAT -> new ClientHeartbeat();
            case ProtocolInfo.WORLD_INFO -> new WorldInfo();
            case ProtocolInfo.LEVEL_ACKNOWLEDGED -> new LevelAcknowledged();
            case ProtocolInfo.CHUNK_COMPLETION -> new ChunkCompletion();
            case ProtocolInfo.CHUNK_REQUEST -> new ChunkRequest();
            case ProtocolInfo.PLAYER_POSITION_UPDATE -> new PlayerPositionUpdate();
            case ProtocolInfo.CHUNK_THROWAWAY -> new ChunkThrowaway();
            default -> new UnknownPacket(pid);
        };
        packet.decode(HandleByteBuf.of(byteBuf));
        return packet;
    }

    protected abstract void onPacket(Packet packet);

}
