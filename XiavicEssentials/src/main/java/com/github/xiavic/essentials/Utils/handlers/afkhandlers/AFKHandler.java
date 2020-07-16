package com.github.xiavic.essentials.Utils.handlers.afkhandlers;

import com.github.xiavic.essentials.Utils.Utils;
import org.bukkit.entity.Player;

import java.util.*;

public class AFKHandler {

    private static Collection<UUID> AFKPlayers = new HashSet<>();

    public static void registerHandler() {}

    public static AFKState toggleAFK(Player player) {
        if (AFKPlayers.contains(player.getUniqueId())) {
            AFKPlayers.remove(player.getUniqueId());
            Utils.sendMessage(player, "messages.player-not-afk", "%player%", player.getDisplayName());
            return AFKState.NOTAFK;
        } else {
            AFKPlayers.add(player.getUniqueId());
            Utils.sendMessage(player, "messages.player-afk", "%player%", player.getDisplayName());
            return AFKState.AFK;
        }
    }

    public static AFKState isPlayerAFK(Player player) {
        if (AFKPlayers.contains(player.getUniqueId())) return AFKState.AFK;
        return AFKState.NOTAFK;
    }

    public static void forceRemovePlayer(Player player) { AFKPlayers.remove(player.getUniqueId()); }

}
