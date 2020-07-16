package com.github.xiavic.essentials.commands.player.Essential.Teleport;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.BukkitCommandManager;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import com.github.xiavic.essentials.Utils.handlers.teleportation.ETeleportResults;
import com.github.xiavic.essentials.Utils.handlers.teleportation.TeleportationHandler;
import com.github.xiavic.essentials.Utils.handlers.teleportation.TpaHandler;
import com.github.xiavic.essentials.Utils.messages.TeleportationMessages;
import com.github.xiavic.essentials.Main;
import com.github.xiavic.essentials.Utils.LocationUtils;
import com.github.xiavic.essentials.Utils.Utils;
import com.github.xiavic.essentials.Utils.messages.Messages;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

@SuppressWarnings("unused")
public class TeleportationCommandHandler extends BaseCommand {

    private static final Messages messages = Messages.INSTANCE;
    private static final TeleportationMessages tpMessages = TeleportationMessages.INSTANCE;
    private final Main plugin;
    private final TeleportationHandler teleportHandler;
    private final TpaHandler tpaHandler;

    public TeleportationCommandHandler(final BukkitCommandManager commandManager,
        @NotNull final TeleportationHandler teleportHandler, @NotNull final TpaHandler tpaHandler) {
        commandManager.registerCommand(this);
        this.plugin = (Main) commandManager.getPlugin();
        this.teleportHandler = teleportHandler;
        this.tpaHandler = tpaHandler;
    }

    @Default @CommandAlias("back") @CommandPermission("Xiavic.player.back")
    public void doBack(final Player player) {
        final Location last = teleportHandler.getLastLocation(player);
        if (last != null) {
            teleportHandler.teleportToLocation(player, last);
            Utils.sendMessage(player, "teleport.teleporting-previous");
        } else {
            Utils.sendMessage(player, "teleport.no-previous-location");
        }
    }

    @Default @CommandAlias("randomteleport|rtp") @CommandPermission("Xiavic.player.rtp")
    public void doRandomTeleport(final Player player) {
        final double distance = plugin.getConfig().getDouble("RTPDistance");
        double randomX = (ThreadLocalRandom.current().nextDouble() * (distance * 2)) - distance;
        double randomZ = (ThreadLocalRandom.current().nextDouble() * (distance * 2)) - distance;
        double randomY = player.getWorld().getHighestBlockYAt((int) Math.round(randomX), (int) Math.round(randomZ)) + 1.5;

        Location rtp = new Location(player.getWorld(), randomX, randomY, randomZ);
        teleportHandler.teleportToLocation(player, rtp);
        Utils.sendMessage(player, "teleport.random-teleport");

        Block block = rtp.getBlock().getRelative(0, -1, 0);
        if (block.getType().equals(Material.WATER) || block.getType().equals(Material.LAVA)) {
            block.setType(Material.DIRT);
        }
    }

    @Default @CommandAlias("spawn") @CommandPermission("Xiavic.player.spawn") @CommandCompletion("@Players")
    public void doSpawnTeleport(Player player, @Optional OnlinePlayer otherPlayer) {
        if (otherPlayer == null) {
            final Location spawn = LocationUtils.getLocation("SpawnSystem.Spawn");
            teleportHandler.teleportToLocation(player, spawn);
            Utils.sendMessage(player, "teleport.teleported-spawn");

        } else {
            if (player.hasPermission("Xiavic.staff.spawnothers")) {
                final Location spawn = LocationUtils.getLocation("SpawnSystem.Spawn");
                teleportHandler.teleportToLocation(otherPlayer.player, spawn);
                Utils.sendMessage(otherPlayer.player, "teleport.teleported-spawn");
            } else {
                Utils.sendMessage(player, "messages.no-permission");
            }
        }
    }

    @Default @CommandAlias("tpa") @CommandPermission("Xiavic.player.tpa") @CommandCompletion("@players")
    public void doTeleportRequest(Player sender, OnlinePlayer otherPlayer) {
        if (sender == otherPlayer.player) {
            Utils.sendMessage(sender, "teleport.teleport-self");
            return;
        }

        ETeleportResults result = tpaHandler.addRequest(sender, otherPlayer.player);

        if (result == ETeleportResults.P2DISABLED) {
            Utils.sendMessage(sender, "teleport.teleport-disabled", "%target%", otherPlayer.player.getDisplayName());
        }

    }

    @Default @CommandAlias("tpaccept") @CommandPermission("Xiavic.player.tpaccept")
    public void acceptTeleportRequest(final Player sender) {
        tpaHandler.parseRequest(sender, true);
    }

    @Default @CommandAlias("tpdeny") @CommandPermission("Xiavic.player.tpdeny")
    public void denyTeleportRequest(final Player sender) {
        tpaHandler.parseRequest(sender, false);
    }
}
