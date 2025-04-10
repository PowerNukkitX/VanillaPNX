package org.powernukkitx.listener;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerLoginEvent;
import org.powernukkitx.VanillaPNX;

public class PlayerLoginListener implements Listener {

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        if(VanillaPNX.get().getWrapper().getSocket() == null) {
            event.setCancelled();
            event.setKickMessage("Vanilla Generator is not ready yet!");
        }
    }

}
