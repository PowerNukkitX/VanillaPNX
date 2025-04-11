package org.powernukkitx.socket;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.powernukkitx.PaperBridge;
import org.powernukkitx.utils.WorldInfo;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class PNXSocket {

    @Getter
    private static Long heartbeatTime = System.currentTimeMillis();

    @Getter
    protected final Int2ObjectArrayMap<PNXServer> servers = new Int2ObjectArrayMap<>();

    protected ServerSocket socket;
    protected Thread socketThread;

    @SneakyThrows
    public PNXSocket() {
        for(int port = Bukkit.getPort()+1; port < 0xFFFF; port++) {
            try {
                this.socket = new ServerSocket(port);
                PaperBridge.get().getLogger().info("PNXSocketPortInfo=" + port + ";");
                break;
            } catch (Exception e){}
        }
        this.socketThread = new Thread(() -> {
            loop();
        });
        this.socketThread.start();
    }

    @SneakyThrows
    protected void loop() {
        while (!Bukkit.isStopping()) {
            Socket inputSocket = socket.accept();
            BufferedReader in = new BufferedReader(new InputStreamReader(inputSocket.getInputStream()));
            JsonArray object = JsonParser.parseString(in.readLine()).getAsJsonArray();
            new Thread(() ->{
                int port = object.get(object.size()-1).getAsInt();
                switch (object.get(0).getAsString()) {
                    case "ClientHello" -> {
                        PNXServer server = new PNXServer(port);
                        JsonArray response = new JsonArray();
                        response.add("ServerHello");
                        servers.put(port, server);
                        send(server, response);
                    }
                    case "ClientBye" -> {
                        servers.remove(port);
                    }
                    case "ClientHeartbeat" -> {
                        heartbeatTime = System.currentTimeMillis();
                    }
                    case "WorldInfo" -> {
                        PNXServer server = servers.get(port);
                        server.addWorldInfo(new WorldInfo(
                                object.get(1).getAsString(),
                                Long.parseLong(object.get(2).getAsString()),
                                Integer.parseInt(object.get(3).getAsString()),
                                server));
                    }
                    case "RequestChunk" -> {
                        PNXServer server = servers.get(port);
                        String world = object.get(1).getAsString();
                        server.getWorlds().get(world).queueChunk(Long.parseLong(object.get(2).getAsString()));
                    }
                }
            }).start();
        }
    }

    public static void send(PNXServer server, JsonArray message) {
        try {
            final Socket socket = new Socket("localhost", server.port);
            final OutputStream output = socket.getOutputStream();
            final PrintWriter writer = new PrintWriter(output, true);
            writer.println(message);
            socket.close();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
