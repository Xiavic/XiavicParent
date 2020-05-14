package com.github.xiavic.lib.teleport;

import org.bukkit.entity.Player;

public interface ITeleportRequestHandler {

    boolean loadTeleportHandler();

    void startCooldown(Player player);

    boolean canTpa(Player player);

    void parseRequest(Player player, boolean accepted);

    // 0 - success
    // 1 - tpa already pending
    // 2 - tpa disabled
    int addRequest(Player origin, Player target);

    void doChecks();
}
