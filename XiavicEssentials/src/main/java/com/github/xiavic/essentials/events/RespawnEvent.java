package com.github.xiavic.essentials.events;

import com.github.xiavic.essentials.utils.LocationUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class RespawnEvent implements Listener {

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        if (event.isBedSpawn() | event.isAnchorSpawn()) return;
        event.setRespawnLocation(LocationUtils.getLocation("SpawnSystem.Spawn"));
    }

}
