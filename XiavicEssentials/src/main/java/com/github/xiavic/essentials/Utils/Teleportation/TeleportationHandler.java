package com.github.xiavic.essentials.Utils.Teleportation;


import com.github.xiavic.essentials.Utils.Utils;
import io.papermc.lib.PaperLib;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class TeleportationHandler implements ITeleportHandler, Listener {

    private static List<UUID> disabledPlayers = new ArrayList<>();
    private static Map<UUID, Location> lastLocations = new HashMap<>();


    @Override
    public void processPTeleport(Player player) { lastLocations.put(player.getUniqueId(), player.getLocation()); }

    @Override
    public void processPToggle(Player player) {
        if (disabledPlayers.contains(player.getUniqueId())) {
            disabledPlayers.remove(player.getUniqueId());
            Utils.sendMessage(player, "teleport.teleport-toggled-off");
        } else {
            disabledPlayers.add(player.getUniqueId());
            Utils.sendMessage(player, "teleport.teleport-toggled-on");
        }
    }

    @Override
    public CompletableFuture<Boolean> teleportToLocation(Player player, Location location) {
        processPTeleport(player);
        return player.teleportAsync(location);
    }

    @Override
    public CompletableFuture<Boolean> teleportToPlayer(Player player, Player otherPlayer, boolean reverse) {
        if (reverse) {
            if (!disabledPlayers.contains(player.getUniqueId())) {
                processPTeleport(otherPlayer);
                return otherPlayer.teleportAsync(player.getLocation());
            }
        } else {
            if (!disabledPlayers.contains(otherPlayer.getUniqueId())) {
                processPTeleport(player);
                return player.teleportAsync(otherPlayer.getLocation());
            }
        }
        return CompletableFuture.completedFuture(false);
    }

    @Override
    public ETeleportResults teleportRemote(Player origin, Player target) {
        if (disabledPlayers.contains(origin.getUniqueId())) return ETeleportResults.P1DISABLED; // Error Messages?
        if (disabledPlayers.contains(target.getUniqueId())) return ETeleportResults.P2DISABLED;

        processPTeleport(origin);
        PaperLib.teleportAsync(origin, target.getLocation());
        return ETeleportResults.SUCCESS;
    }

    @Override
    public Location getLastLocation(Player player) {
        if (!lastLocations.containsKey(player.getUniqueId())) return null;
        return lastLocations.get(player.getUniqueId());
    }

    @Override
    public boolean checkTPDisabled(Player player) {
        return disabledPlayers.contains(player.getUniqueId());
    }

    @EventHandler
    public static void onDeath(PlayerDeathEvent e) {
        lastLocations.put(e.getEntity().getUniqueId(), e.getEntity().getLocation());
        // Cancel TpaRequest Here - TODO
    }

}
