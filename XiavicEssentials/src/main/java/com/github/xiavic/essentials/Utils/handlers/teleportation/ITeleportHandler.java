package com.github.xiavic.essentials.Utils.handlers.teleportation;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import java.util.concurrent.CompletableFuture;

public interface ITeleportHandler {

    void processPTeleport(Player player);
    void processPToggle(Player player);

    CompletableFuture<Boolean> teleportToLocation(Player player, Location location);
    CompletableFuture<Boolean> teleportToPlayer(Player player, Player otherPlayer, boolean reverse);
    ETeleportResults teleportRemote(Player origin, Player target);

    Location getLastLocation(Player player);
    boolean checkTPDisabled(Player player);

}
