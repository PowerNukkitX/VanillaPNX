package org.powernukkitx;

import cn.nukkit.plugin.PluginBase;
import cn.nukkit.plugin.PluginManager;
import cn.nukkit.registry.RegisterException;
import cn.nukkit.registry.Registries;
import org.powernukkitx.generator.EntityDamageListener;
import org.powernukkitx.generator.VanillaGenerateStage;
import org.powernukkitx.generator.VanillaGenerator;
import org.powernukkitx.listener.ChunkLoadListener;
import org.powernukkitx.listener.ChunkUnloadListener;
import org.powernukkitx.listener.LevelLoadListener;
import org.powernukkitx.listener.PlayerLoginListener;
import org.powernukkitx.server.PaperWrapper;

import java.io.File;

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
}
