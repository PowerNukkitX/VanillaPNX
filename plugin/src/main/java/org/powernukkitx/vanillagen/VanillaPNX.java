package org.powernukkitx.vanillagen;

import cn.nukkit.plugin.PluginBase;
import cn.nukkit.plugin.PluginManager;
import cn.nukkit.registry.RegisterException;
import cn.nukkit.registry.Registries;
import cn.nukkit.utils.Config;
import org.powernukkitx.vanillagen.listener.*;
import org.powernukkitx.vanillagen.generator.VanillaGenerateStage;
import org.powernukkitx.vanillagen.generator.VanillaGenerator;
import org.powernukkitx.vanillagen.server.PaperWrapper;

import java.io.File;
import java.io.IOException;

public class VanillaPNX extends PluginBase {

    private static VanillaPNX instance;

    private PaperWrapper wrapper;

    @Override
    public void onLoad() {
        instance = this;
        getDataFolder().mkdir();
        wrapper = new PaperWrapper(new File(getDataFolder(), "paper"));
        wrapper.start();
        try {
            Registries.GENERATE_STAGE.register(VanillaGenerateStage.NAME, VanillaGenerateStage.class);
            Registries.GENERATOR.register(VanillaGenerator.NAME, VanillaGenerator.class);
        } catch (RegisterException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onEnable() {
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new LevelLoadListener(), this);
        pluginManager.registerEvents(new ChunkLoadListener(), this);
        pluginManager.registerEvents(new PlayerLoginListener(), this);
        pluginManager.registerEvents(new ChunkUnloadListener(), this);
        pluginManager.registerEvents(new EntityDamageListener(), this);
        pluginManager.registerEvents(new ChunkSendManager(), this);
    }

    @Override
    public void onDisable() {
        wrapper.stop();
    }

    public static VanillaPNX get() {
        return instance;
    }

    public PaperWrapper getWrapper() {
        return wrapper;
    }

    @Override
    public void saveDefaultConfig() {
        File file = new File(getDataFolder(), "config.yml");
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        Config config = new Config(file);
        if(!config.exists("save-chunks")) config.set("save-chunks", true);
        config.save();
    }
}
