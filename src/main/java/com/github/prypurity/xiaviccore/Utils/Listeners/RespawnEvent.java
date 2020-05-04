package com.github.prypurity.xiaviccore.Utils.Listeners;

import com.github.prypurity.xiaviccore.Utils.LocationUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class RespawnEvent implements Listener {

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        if (!e.isBedSpawn()) {
            e.setRespawnLocation(LocationUtils.getLocation("Spawn"));
        }
    }

}
