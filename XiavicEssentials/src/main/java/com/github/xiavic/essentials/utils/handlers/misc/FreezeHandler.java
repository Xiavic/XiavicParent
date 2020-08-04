package com.github.xiavic.essentials.utils.handlers.misc;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FreezeHandler {

    public static List<UUID> frozenPlayers = new ArrayList<>();

    public static void freezePlayer(Player target) { frozenPlayers.add(target.getUniqueId()); }
    public static void unFreezePlayer(Player target) { frozenPlayers.remove(target.getUniqueId()); }
    public static boolean isFrozen(Player player) {
        return frozenPlayers.contains(player.getUniqueId());
    }



}
