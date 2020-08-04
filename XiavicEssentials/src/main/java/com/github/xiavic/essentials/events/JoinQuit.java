package com.github.xiavic.essentials.events;

import com.github.xiavic.essentials.Main;
import com.github.xiavic.essentials.commands.staff.noncheat.StaffCommandHandler;
import com.github.xiavic.essentials.utils.LocationUtils;
import com.github.xiavic.essentials.utils.Misc.everythingElse;
import com.github.xiavic.essentials.utils.Utils;
import com.github.xiavic.essentials.utils.handlers.misc.FreezeHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;


public class JoinQuit implements Listener {

    @EventHandler
    private void onJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();
        if (!player.hasPlayedBefore()) {
            event.setJoinMessage(Utils.convertRGBColor(Main.messages.getString("message.first-join").replace("%player%", player.getDisplayName())));
            if (!player.isOp()) { player.teleportAsync(LocationUtils.getLocation("SpawnSystem.FirstSpawn")); }
        } else {
            if (!StaffCommandHandler.vanishedPlayers.contains(player.getUniqueId())) {
                event.setJoinMessage(Utils.convertRGBColor(Main.messages.getString("messages.rejoin").replace("%player%", player.getDisplayName())));
                if (FreezeHandler.isFrozen(player)) { Utils.sendMessage(player, "messages.player-frozen"); }
            } else {
                event.setJoinMessage(null);
                for (Player target : Bukkit.getOnlinePlayers()) {
                    target.hidePlayer(player);
                }
            }
        }

    }

    @EventHandler
    private void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (!StaffCommandHandler.vanishedPlayers.contains(player.getUniqueId())) {
            event.setQuitMessage(Utils.convertRGBColor(Main.messages.getString("messages.quit").replace("%player%", player.getDisplayName())));
        } else {
            event.setQuitMessage(null);
        }
    }
}
