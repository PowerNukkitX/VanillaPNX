package org.powernukkitx.vanillagen.server;

import cn.nukkit.Server;
import cn.nukkit.utils.Utils;
import lombok.Getter;
import lombok.SneakyThrows;
import org.powernukkitx.vanillagen.VanillaPNX;
import org.powernukkitx.vanillagen.packet.ClientHelloPacket;
import org.powernukkitx.vanillagen.server.socket.PNXNettyImpl;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
public class PaperWrapper {

    public static final String jarName = "paper.jar";

    private Process process;
    private ProcessBuilder processBuilder;

    private PNXNettyImpl socket;

    private final File operationFolder;

    public PaperWrapper(File operationFolder) {
        this.operationFolder = operationFolder;
        operationFolder.mkdir();
    }

    protected File getServerJar() {
        return new File(operationFolder, jarName);
    }

    public void start() {
        checkServerInstallation();
        processBuilder = new ProcessBuilder("java", "-jar", jarName, "nogui").directory(operationFolder);

        try {
            process = processBuilder.start();
            VanillaPNX.get().getLogger().info("Starting paper server...");
        } catch (Exception e) {
            e.printStackTrace();
        }
        process.onExit().thenRun(this::onExit);

        Thread outThread = new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if(line.contains("PNXSocketPortInfo=")) {
                        Pattern pattern = Pattern.compile("PNXSocketPortInfo=([0-9]*);");
                        Matcher matcher = pattern.matcher(line);
                        if(matcher.find()) {
                            int port = VanillaPNX.get().getServer().getPort();
                            this.socket = new PNXNettyImpl(port, Integer.parseInt(matcher.group(1)));
                            this.socket.send(new ClientHelloPacket(port, ProcessHandle.current().pid()));
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        outThread.start();

        Thread errThread = new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.err.println("ERR: " + line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        errThread.start();
    }

    public void stop() {
        writeConsole("stop");
    }

    protected void onExit() {
        VanillaPNX.get().getLogger().info("Paper Server stopped.. Stopping PowerNukkitX...");
        process.destroy();
        Server.getInstance().shutdown();
    }

    public void writeConsole(String command) {
        PrintWriter stdin = new PrintWriter(
                new BufferedWriter(
                        new OutputStreamWriter(process.getOutputStream())), true);

        stdin.println(command);
    }

    @SneakyThrows
    protected void checkServerInstallation() {
        File file = new File(getOperationFolder(), "eula.txt");
        if(file.exists()) file.delete();
        if(file.createNewFile()) {
            try(FileWriter writer = new FileWriter(file)) {
                writer.write("eula=true");
                writer.flush();
            }
        }
        File serverJar = getServerJar();
        if(!serverJar.exists()) {
            downloadPaper();
        }

        File pluginsFolder = new File(getOperationFolder(), "plugins");
        pluginsFolder.mkdir();
        String bridgePluginName = "paper-bridge-1.0-SNAPSHOT.jar";
        File bridgePlugin = new File(pluginsFolder, bridgePluginName);
        bridgePlugin.createNewFile();
        try (InputStream inputStream = this.getClass().getModule().getResourceAsStream(bridgePluginName)) {
            assert inputStream != null;
            Utils.writeFile(bridgePlugin, inputStream);
        }
    }

    protected boolean downloadPaper() {
        try {
            File file = new File(getOperationFolder(), jarName);
            InputStream in = new URL("https://api.papermc.io/v2/projects/paper/versions/1.21.8/builds/25/downloads/paper-1.21.8-25.jar").openStream();
            Files.copy(in , file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            VanillaPNX.get().getLogger().info("Successfully downloaded paper jar.");
            return true;
        } catch (Exception e) {
            VanillaPNX.get().getLogger().error("Failed to download Paper jar", e);
            return false;
        }
    }

}
