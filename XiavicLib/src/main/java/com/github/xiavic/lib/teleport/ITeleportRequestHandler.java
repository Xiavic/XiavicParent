package com.github.xiavic.lib.teleport;

import org.bukkit.entity.Player;

public interface ITeleportRequestHandler {

    boolean loadTeleportHandler();

    void startCooldown(Player player);

    boolean canTpa(Player player);

    /**
     * Parse and return the most recent teleport request of the given player.
     * @param player The player to parse.
     * @param accepted Whether the request should be accepted.
     * @return Returns the origin player.
     */
    Player parseRequest(Player player, boolean accepted);

    // 0 - success
    // 1 - tpa already pending
    // 2 - tpa disabled
    int addRequest(Player origin, Player target);

    void doChecks();
}
