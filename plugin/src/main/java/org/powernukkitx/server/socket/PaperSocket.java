package org.powernukkitx.server.socket;

import cn.nukkit.Server;
import cn.nukkit.level.Level;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import lombok.Getter;
import lombok.SneakyThrows;
import org.powernukkitx.generator.GenerationQueue;
import org.powernukkitx.generator.VanillaGenerator;
import org.powernukkitx.VanillaPNX;
import org.powernukkitx.listener.ChunkLoadListener;
import org.powernukkitx.listener.LevelLoadListener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

public class PaperSocket {

    @Getter
    protected final int paperPort;

    protected ServerSocket socket;
    protected Thread socketThread;

    @Getter
    private boolean serverHello = false;

    @SneakyThrows
    public PaperSocket(int paperPort) {
        this.paperPort = paperPort;
        this.socket = new ServerSocket(VanillaPNX.get().getServer().getPort());
        this.socketThread = new Thread(() -> {
            loop();
        });
        this.socketThread.start();
    }

    @SneakyThrows
    protected void loop() {
        while (VanillaPNX.get().getWrapper().getProcess().isAlive()) {
            Socket inputSocket = socket.accept();
            BufferedReader in = new BufferedReader(new InputStreamReader(inputSocket.getInputStream()));
            JsonArray object = JsonParser.parseString(in.readLine()).getAsJsonArray();
            new Thread(() -> {
                switch (object.get(0).getAsString()) {
                    case "ServerHello" -> {
                        VanillaPNX.get().getServer().getLevels().values().forEach(LevelLoadListener::sendLevelInfo);
                        Server.getInstance().getScheduler().scheduleRepeatingTask(() -> {
                            send("ClientHeartbeat");
                        }, 20);
                        try {
                            TimeUnit.MILLISECONDS.sleep(100);
                            this.serverHello = true;
                            VanillaGenerator.getQueue().init();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    case "LevelAcknowledged" -> {
                        GenerationQueue.acknowledged(object.get(1).getAsString());
                    }
                    case "ChunkCompletion" -> {
                        String levelname = object.get(1).getAsString();
                        Long chunkHash = object.get(2).getAsLong();
                        GenerationQueue.addToReceived(levelname, chunkHash);
                        VanillaGenerator.applyData(VanillaPNX.get().getServer().getLevelByName(levelname).getChunk(Level.getHashX(chunkHash), Level.getHashZ(chunkHash)), object.get(3).getAsJsonArray());
                    }
                }
            }).start();
        }
    }

    public void send(String... values) {
        JsonArray message = new JsonArray();
        for(String value : values) message.add(value);
        send(message);
    }

    public void send(JsonArray array) {
        array.add(VanillaPNX.get().getServer().getPort());
        try {
            Socket socket = new Socket("127.0.0.1", getPaperPort());
            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);
            writer.println(array);
            socket.close();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public JsonArray receive(String... values) {
        try {
            JsonArray message = new JsonArray();
            for(String value : values) message.add(value);
            message.add(VanillaPNX.get().getServer().getPort());
            Socket socket = new Socket("127.0.0.1", getPaperPort());
            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);
            writer.println(message);
            final BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            final String response = in.readLine();
            socket.close();
            return JsonParser.parseString(response).getAsJsonArray();
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
