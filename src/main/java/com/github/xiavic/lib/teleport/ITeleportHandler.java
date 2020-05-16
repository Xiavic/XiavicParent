package com.github.xiavic.lib.teleport;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface ITeleportHandler {
    void processPlayerTeleport(Player player);

    void processPlayerToggle(Player player);

    void teleport(Player player, Location location);

    // change - if true: teleport player2 to player1 else teleport player1 to player2
    boolean teleport(Player p1, Player p2, boolean change);

    boolean remoteTp(Player player, Location location);

    // 0 - teleport successful
    // 1 - player1 disabled
    // 2 - player2 disabled
    int remoteTp(Player p1, Player p2);

    Location getLastLocation(Player player) throws Exception;

    boolean isDisabled(Player player);

}
