package com.github.xiavic.lib.teleport;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

public interface ITeleportHandler {

    void processPTeleport(Player player);
    void processPToggle(Player player);

    CompletableFuture<Boolean> teleportPLocation(Player player, Location location);

    // change - if true: teleport player2 to player1 else teleport player1 to player2
    CompletableFuture<Boolean> teleportPPlayer(Player player, Player otherPlayer, boolean reverse);

    // 0 - teleport successful
    // 1 - player1 disabled
    // 2 - player2 disabled
    int remoteTp(Player p1, Player p2);

    Location getLastLocation(Player player);

    boolean isDisabled(Player player);




}
