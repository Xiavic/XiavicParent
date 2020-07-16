package com.github.xiavic.essentials.Utils.events;

import com.github.xiavic.essentials.Utils.handlers.afkhandlers.AFKHandler;
import com.github.xiavic.essentials.Utils.handlers.afkhandlers.AFKState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.player.*;

public class AFKEvents implements Listener {

//    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
//    public void onChat(final AsyncPlayerChatEvent event) {
//        Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
//            if (event.getMessage().startsWith("/afk")) return;
//            AFKHandler.toggleAFK(event.getPlayer());
//        });
//    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMove(final PlayerMoveEvent event) {
        if (AFKHandler.isPlayerAFK(event.getPlayer()) == AFKState.NOTAFK) return;
        AFKHandler.toggleAFK(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInteract(final PlayerInteractEvent event) {
        if (AFKHandler.isPlayerAFK(event.getPlayer()) == AFKState.NOTAFK) return;
        AFKHandler.toggleAFK(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTeleport(final PlayerTeleportEvent event) {
        if (AFKHandler.isPlayerAFK(event.getPlayer()) == AFKState.NOTAFK) return;
        AFKHandler.toggleAFK(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEat(final PlayerRespawnEvent event) {
        if (AFKHandler.isPlayerAFK(event.getPlayer()) == AFKState.NOTAFK) return;
        AFKHandler.toggleAFK(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onItemUse(final PlayerItemConsumeEvent event) {
        if (AFKHandler.isPlayerAFK(event.getPlayer()) == AFKState.NOTAFK) return;
        AFKHandler.toggleAFK(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onRespawn(final PlayerRespawnEvent event) {
        if (AFKHandler.isPlayerAFK(event.getPlayer()) == AFKState.NOTAFK) return;
        AFKHandler.toggleAFK(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDisconnect(final PlayerQuitEvent event) {
        AFKHandler.forceRemovePlayer(event.getPlayer());
    }

}
