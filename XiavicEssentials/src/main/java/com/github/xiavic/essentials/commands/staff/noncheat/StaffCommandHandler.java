package com.github.xiavic.essentials.commands.staff.noncheat;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.BukkitCommandManager;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import com.github.xiavic.essentials.Main;
import com.github.xiavic.essentials.utils.Misc.everythingElse;
import com.github.xiavic.essentials.utils.Utils;
import com.github.xiavic.essentials.utils.handlers.misc.FreezeHandler;
import com.github.xiavic.essentials.utils.messages.CommandMessages;
import com.github.xiavic.essentials.utils.messages.Messages;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("unused") public class StaffCommandHandler extends BaseCommand {

    private static final CommandMessages commandMessages = CommandMessages.INSTANCE;
    private static final Messages messages = Messages.INSTANCE;
    public static List<UUID> vanishedPlayers = new ArrayList<>();

    public StaffCommandHandler(@NotNull final BukkitCommandManager commandManager) {
        commandManager.registerCommand(this);
        new GameModeCommandHandler(commandManager);
    }

    @Default @CommandAlias("clear|ci") @CommandPermission("Xiavic.staff.clear") @CommandCompletion("@players")
    public void clearInventory(Player player, @Optional OnlinePlayer otherPlayer) {

        if (otherPlayer != null) {
            if (player.hasPermission("Xiavic.staff.clearothers")) {
                otherPlayer.player.getInventory().clear();
                Utils.sendMessage(otherPlayer.player, "commands.inventory.clear");
                Utils.sendMessage(player, "commands.inventory-clear-other");


            }
        } else {

            player.getInventory().clear();
            Utils.sendMessage(player, "commands.inventory.clear");

        }

    }

    @Default @CommandAlias("coreconfigupdate|ccu") @CommandPermission("Xiavic.staff.config.update")
    public void reloadConfiguration(final CommandSender sender) {
        Main.mainConfig.forceReload();
        Main.messages.forceReload();
//        Main.permissions.forceReload();
        Utils.sendMessage(sender, "messages.config-updated");
    }

    @Default @CommandAlias("coreversion") @CommandPermission("Xiavic.staff.version")
    public void showVersion(final CommandSender sender) {
        Utils.sendMessage(sender, "messages.config-version", "%version%", Main.getPlugin(Main.class).getDescription().getVersion());
    }

    @Default @CommandAlias("feed") @CommandPermission("Xiavic.staff.feed") @CommandCompletion("@players")
    public void feed(final Player player, @Optional OnlinePlayer otherPlayer) {
        if (otherPlayer != null) {
            if (player.hasPermission("Xiavic.staff.feedothers")) {
                otherPlayer.player.setFoodLevel(20);
                otherPlayer.player.setSaturation(20);
                Utils.sendMessage(player, "commands.feed-other", "%target%", otherPlayer.getPlayer().getDisplayName());
                Utils.sendMessage(otherPlayer.player, "commands.feed");
            }
        } else {
            player.setFoodLevel(20);
            player.setSaturation(20);
            Utils.sendMessage(player, "commands.feed");
        }
    }

    @Default @CommandAlias("setfirstspawn") @CommandPermission("Xiavic.staff.setfirstspawn")
    public void setFirstSpawn(final Player player) {
        final Location loc = player.getLocation();
        final World world = loc.getWorld();
        final double x = loc.getX(), y = loc.getY(), z = loc.getZ();
        final float yaw = loc.getYaw(), pitch = loc.getPitch();
        final String output =
            world.getName() + "," + x + "," + y + "," + z + "," + yaw + "," + pitch;
        Main.mainConfig.set("SpawnSystem.FirstSpawn", output);
        Utils.sendMessage(player, "commands.set-first-join");
    }

    @Default @CommandAlias("setspawn") @CommandPermission("Xiavic.staff.setspawn")
    public void setSpawn(final Player player) {
        final Location loc = player.getLocation();
        final World world = loc.getWorld();
        final double x = loc.getX(), y = loc.getY(), z = loc.getZ();
        final float yaw = loc.getYaw(), pitch = loc.getPitch();
        final String output =
                world.getName() + "," + x + "," + y + "," + z + "," + yaw + "," + pitch;
        Main.mainConfig.set("SpawnSystem.Spawn", output);
        Utils.sendMessage(player, "commands.set-spawn");
    }

    @Default @CommandAlias("fly") @CommandPermission("Xiavic.staff.fly") @CommandCompletion("@players")
    public void toggleFly(final Player player, @Optional OnlinePlayer otherplayer) {
        if (otherplayer != null) {
            if (player.hasPermission("Xiavic.staff.flyothers")) {
                otherplayer.player.setAllowFlight(!otherplayer.player.getAllowFlight());
                Utils.sendMessage(player, "commands.fly-other", "%target%", otherplayer.player.getDisplayName(), "%mode%", otherplayer.player.getAllowFlight() ? "&cenabled" : "&cdisabled");
                Utils.sendMessage(otherplayer.player, "commands.fly");
            }
        } else {
            player.setAllowFlight(!player.getAllowFlight());
            Utils.sendMessage(player, "commands.fly", "%mode%", player.getAllowFlight() ? "&cenabled" : "&cdisabled");
        }
    }

    @Default @CommandAlias("flyspeed") @CommandCompletion("1 2 3 4 5 6 7 8 9 10")
    @CommandPermission("Xiavic.staff.flyspeed")
    public void toggleFlySpeed(final Player player, int speed) {
        player.setFlySpeed(speed / 10f);
        Utils.sendMessage(player, "commands.flyspeed", "%amount%", String.valueOf(speed));
    }

    @Default @CommandAlias("god") @CommandCompletion("@players true|false")
    @CommandPermission("Xiavic.staff.god")
    public void toggleGod(Player player, @Optional OnlinePlayer otherPlayer, @Optional Boolean enabled) {
        if (otherPlayer != null) {
            if (player.hasPermission("Xiavic.staff.godothers")) {
                enabled = enabled == null ? !otherPlayer.player.isInvulnerable() : enabled;
                otherPlayer.player.setInvulnerable(enabled);
                Utils.sendMessage(otherPlayer.player, "commands.god-other", "%mode%", enabled.toString());
                Utils.sendMessage(player, "commands.god", "%mode%", enabled.toString());
            }
        } else {
            enabled = enabled == null ? !player.isInvulnerable() : enabled;
            player.setInvulnerable(enabled);
            Utils.sendMessage(player, "commands.god", "%mode%", enabled.toString());
        }
    }

    @Default @CommandAlias("heal") @CommandPermission("Xiavic.staff.heal") @CommandCompletion("@players")
    public void doHeal(final Player player, @Optional OnlinePlayer otherplayer) {
        if (otherplayer != null) {
            if (player.hasPermission("Xiavic.staff.healothers")) {
                otherplayer.player.setHealth(20);
                otherplayer.player.setSaturation(20);
                otherplayer.player.setFoodLevel(20);
                Utils.sendMessage(otherplayer.player, "commands.heal");
                Utils.sendMessage(player, "commands.heal-other");
            }
        } else {
            player.setHealth(20);
            player.setSaturation(20);
            player.setFoodLevel(20);
            Utils.sendMessage(player, "commands.heal");
        }
    }


    @Default @CommandAlias("healall") @CommandPermission("Xiavic.staff.healall")
    public void doMassHeal(final CommandSender sender) {
        for (final Player player : Bukkit.getOnlinePlayers()) {
            player.setHealth(20);
            player.setSaturation(20);
            player.setFoodLevel(20);
            Utils.sendMessage(player, "commands.heal");
        }
        Utils.sendMessage(sender, "commands.heal-all");
    }

    @Default @CommandAlias("more") @CommandPermission("Xiavic.staff.more")
    public void giveMaxStack(final Player player) {
        final ItemStack inHand = player.getInventory().getItemInMainHand();
        final int maxSize = inHand.getMaxStackSize();
        inHand.setAmount(maxSize);
        player.getInventory().setItemInMainHand(inHand);
    }

    @Default @CommandAlias("walkspeed") @CommandPermission("Xiavic.staff.walkspeed")
    @CommandCompletion("1 2 3 4 5 6 7 8 9 10")
    public void toggleWalkSpeed(final Player player, final int speed) {
        player.setWalkSpeed(speed / 10f);
        Utils.sendMessage(player, "commands.walkspeed", "%amount%", String.valueOf(speed));
    }

    @Default @CommandAlias("whois") @CommandPermission("Xiavic.player.realname")
    public void showRealName(final CommandSender sender, final OnlinePlayer otherplayer) {

        Utils.sendMessage(sender, commandMessages.messageWhoIsPlayer, "%nickname%", otherplayer.player.getDisplayName(), "%username%", otherplayer.player.getName());
        final Location loc = otherplayer.player.getLocation();
        Utils.chat(sender,
                "&6Player UUID: &9" + otherplayer.player.getUniqueId(),
                "&6Exp: &9" + otherplayer.player.getTotalExperience() + "&6, Next Level: &9" + otherplayer.player.getExpToLevel(),
                "&6Health: &9" + otherplayer.player.getHealth() + "&6, Food: &9" + otherplayer.player.getFoodLevel(),
                "&6Time: &9" + otherplayer.player.getPlayerTime(),
                "&6Location: &9" + otherplayer.player.getWorld().getName().toUpperCase() + " | &cX &9" + loc.getBlockX() + " | &cY &9" + loc.getBlockY() + " | &cZ &9" + loc.getBlockZ(),
                "&6Gamemode: &9" + otherplayer.player.getGameMode() + "&6, Can Fly: &9" + otherplayer.player.getAllowFlight(),
                "&6First Joined: &9" + otherplayer.player.getFirstPlayed() + "&6, Last Played: &9" + otherplayer.player.getLastSeen());
    }

    @Default @CommandAlias("vanish") @CommandPermission("Xiavic.staff.vanish") @SuppressWarnings("deprecation")
    public void toggleVanish(Player player) {
        if (vanishedPlayers.contains(player.getUniqueId())) {
            vanishedPlayers.remove(player.getUniqueId());
            for (Player target : Bukkit.getOnlinePlayers()) {
                target.showPlayer(player);
            }
            Utils.sendMessage(player, "messages.vanish-disabled");
        } else {
            vanishedPlayers.add(player.getUniqueId());
            for (Player target : Bukkit.getOnlinePlayers()) {
                target.hidePlayer(player);
            }
            Utils.sendMessage(player, "messages.vanish-enabled");
        }
    }

    @Default @CommandAlias("freeze") @CommandPermission("Xiavic.staff.freeze") @CommandCompletion("@players")
    public void toggleFreeze(Player player, OnlinePlayer target) {
        if (!FreezeHandler.frozenPlayers.contains(target.player.getUniqueId())) {
            FreezeHandler.freezePlayer(target.player);
            Utils.sendMessage(player, "freeze.froze-player", "%target%", target.player.getDisplayName());
            Utils.sendMessage(target.player, "freeze.been-frozen", "%staffMember%", player.getDisplayName());
        } else {
            FreezeHandler.unFreezePlayer(target.player);
            Utils.sendMessage(player, "freeze.unfroze-player", "%target%", target.player.getDisplayName());
            Utils.sendMessage(target.player, "freeze.been-unfrozen", "%staffMember%", player.getDisplayName());
        }
    }



}
