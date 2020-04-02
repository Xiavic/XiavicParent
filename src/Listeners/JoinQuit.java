package Listeners;

import Main.mainClass;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import utils.Files.messages;
import utils.utils;

public class JoinQuit implements Listener {

    private static mainClass plugin;

    FileConfiguration m = messages.get();

    @EventHandler
    private void onJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        String name = p.getDisplayName();
        if (!(p.hasPlayedBefore())) {
            event.setJoinMessage(utils.chat(m.getString("FirstJoin_Message").replace("%player%", name)));
            p.sendMessage(utils.chat("&6Welcome " + p.getName()));
            Location spawn = new Location(p.getWorld(), 548.0f, 97.0f, -438.0f);
            p.teleport(spawn);
        } else {
            event.setJoinMessage(utils.chat(m.getString("ReJoin_Message").replace("%player%", name)));
        }

    }

    @EventHandler
    private void onQuit(PlayerQuitEvent event) {
        Player p = event.getPlayer();
        String name = p.getDisplayName();
        event.setQuitMessage(utils.chat(m.getString("Quit_Message").replace("%player%", name)));
    }
}
