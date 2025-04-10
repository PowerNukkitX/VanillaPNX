package org.powernukkitx.server;

import cn.nukkit.utils.Utils;
import com.google.gson.JsonArray;
import com.oracle.truffle.regex.tregex.nodes.dfa.Matchers;
import lombok.Getter;
import lombok.SneakyThrows;
import org.iq80.leveldb.fileenv.FileUtils;
import org.powernukkitx.VanillaPNX;
import org.powernukkitx.server.socket.PaperSocket;

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

    private PaperSocket socket;

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
                            this.socket = new PaperSocket(Integer.parseInt(matcher.group(1)));
                            this.socket.send("ClientHello");
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        outThread.start();

// Lies stderr
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
        getSocket().send("ClientBye");
        writeConsole("stop");
    }

    protected void onExit() {
        VanillaPNX.get().getLogger().info("Paper Server stopped!");
        process.destroy();
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
            InputStream in = new URL("https://api.papermc.io/v2/projects/paper/versions/1.21.4/builds/224/downloads/paper-1.21.4-224.jar").openStream();
            Files.copy(in , file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            VanillaPNX.get().getLogger().info("Successfully downloaded paper jar.");
            return true;
        } catch (Exception e) {
            VanillaPNX.get().getLogger().error("Failed to download Paper jar", e);
            return false;
        }
    }

}
