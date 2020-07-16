package com.github.xiavic.essentials.Utils.handlers.teleportation;

import org.bukkit.entity.Player;

public interface ITeleportRequestHandler {

    boolean loadTeleportHandler();

    void startCooldown(Player player);

    boolean canTpa(Player player);

    void parseRequest(Player player, boolean accepted);
    ETeleportResults addRequest(Player origin, Player target);

    void doChecks();

}
