package org.powernukkitx.vanillagen;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.Getter;
import lombok.SneakyThrows;
import org.powernukkitx.vanillagen.netty.HandleByteBuf;
import org.powernukkitx.vanillagen.packet.*;

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
            Socket inputSocket = socket.accept();
            new Thread(() -> {
                try {
                    BufferedInputStream bufferedStream = new BufferedInputStream(inputSocket.getInputStream());
                    ByteBuf byteBuf = Unpooled.wrappedBuffer(bufferedStream.readAllBytes());
                    Packet packet = decode(byteBuf);
                    onPacket(packet);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }
    }

    public void send(Packet packet) {
        try {
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
        } catch (Exception e) {
            System.out.println("Connection to Paper Server timed out! If this spams your console, reboot the server!");
            e.printStackTrace();
        }
    }

    protected final Packet decode(ByteBuf byteBuf) {
        int pid = byteBuf.readByte();
        Packet packet = switch (pid) {
            case ProtocolInfo.CLIENT_HELLO -> new ClientHelloPacket();
            case ProtocolInfo.SERVER_HELLO -> new ServerHelloPacket();
            case ProtocolInfo.WORLD_INFO -> new WorldInfoPacket();
            case ProtocolInfo.LEVEL_ACKNOWLEDGED -> new LevelAcknowledgedPacket();
            case ProtocolInfo.CHUNK_REQUEST -> new ChunkRequestPacket();
            case ProtocolInfo.CHUNK_TERRAIN_DATA -> new ChunkTerrainDataPacket();
            case ProtocolInfo.CHUNK_THROWAWAY -> new ChunkThrowawayPacket();
            case ProtocolInfo.PLAYER_POSITION_UPDATE -> new PlayerPositionUpdatePacket();
            case ProtocolInfo.POPULATION -> new PopulationPacket();
            case ProtocolInfo.CLIENT_HEARTBEAT -> new ClientHeartbeatPacket();
            case ProtocolInfo.BLOCK_ENTITY_DATA -> new BlockEntityDataPacket();
            default -> new UnknownPacket(pid);
        };
        packet.decode(HandleByteBuf.of(byteBuf));
        return packet;
    }

    protected abstract void onPacket(Packet packet);

}
