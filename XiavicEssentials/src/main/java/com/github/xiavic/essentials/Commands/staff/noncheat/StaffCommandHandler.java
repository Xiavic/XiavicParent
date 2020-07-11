package com.github.xiavic.essentials.Commands.staff.noncheat;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.BukkitCommandManager;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import com.github.xiavic.essentials.Main;
import com.github.xiavic.essentials.Utils.Utils;
import com.github.xiavic.essentials.Utils.messages.CommandMessages;
import com.github.xiavic.essentials.Utils.messages.Messages;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused") public class StaffCommandHandler extends BaseCommand {

    private static final CommandMessages commandMessages = CommandMessages.INSTANCE;
    private static final Messages messages = Messages.INSTANCE;

    public StaffCommandHandler(@NotNull final BukkitCommandManager commandManager) {
        commandManager.registerCommand(this);
        new GameModeCommandHandler(commandManager);
    }

    @Default @CommandAlias("clear|ci") @CommandPermission("Xiavic.staff.clear") @CommandCompletion("@players")
    public void clearInventory(Player player, @Optional String otherPlayer) {

        if (otherPlayer != null) {
            if (player.hasPermission("Xiavic.staff.clearothers")) {

                Player otherPlayerObj = Bukkit.getPlayer(otherPlayer);
                assert otherPlayerObj != null;

                otherPlayerObj.getInventory().clear();
                Utils.sendMessage(otherPlayerObj, commandMessages.messageInventoryCleared);


            }
        } else {

            player.getInventory().clear();
            Utils.sendMessage(player, commandMessages.messageInventoryCleared);

        }

    }

    @Default @CommandAlias("coreconfigupdate|ccu") @CommandPermission("Xiavic.staff.config.update")
    public void reloadConfiguration(final CommandSender sender) {
        Main.mainConfig.forceReload();
        Main.messages.forceReload();
        Main.permissions.forceReload();
        Utils.sendMessage(sender, messages.messageConfigUpdated);
    }

    @Default @CommandAlias("coreversion") @CommandPermission("Xiavic.staff.version")
    public void showVersion(final CommandSender sender) {
        Utils.sendMessage(sender, messages.messageShowPluginVersion, "%version%",
            Main.getPlugin(Main.class).getDescription().getVersion());
    }

    @Default @CommandAlias("feed") @CommandPermission("Xiavic.staff.feed") @CommandCompletion("@players")
    public void feed(final Player player, @Optional OnlinePlayer otherPlayer) {

        if (otherPlayer != null) {

            if (player.hasPermission("Xiavic.staff.feedothers")) {

                otherPlayer.player.setFoodLevel(20);
                otherPlayer.player.setSaturation(20);
                Utils.sendMessage(player, commandMessages.messagePlayerFedOther, "%target%", otherPlayer.getPlayer().getDisplayName());
                Utils.sendMessage(otherPlayer.player, commandMessages.messagePlayerFed);
            }

        } else {

            player.setFoodLevel(20);
            player.setSaturation(20);
            Utils.sendMessage(player, commandMessages.messagePlayerFed);

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
        Utils.sendMessage(player, commandMessages.messageSetFirstJoinSpawnPoint);
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
        Utils.sendMessage(player, commandMessages.messageSetWorldSpawn);
    }

    @Default @CommandAlias("fly") @CommandPermission("Xiavic.staff.fly")
    public void toggleFly(final Player player, @Optional OnlinePlayer otherplayer) {
        if (otherplayer != null) {
            if (player.hasPermission("Xiavic.staff.flyothers")) {
                otherplayer.player.setAllowFlight(!otherplayer.player.getAllowFlight());
                otherplayer.player.setFlying(!otherplayer.player.isFlying());
                Utils.sendMessage(otherplayer.player, commandMessages.messagePlayerFlyOther, "%target%", otherplayer.player.getDisplayName(), "%mode%", otherplayer.player.getAllowFlight() ? "&cenabled" : "&cdisabled");
            }
        } else {
            player.setAllowFlight(!player.getAllowFlight());
            player.setFlying(!player.isFlying());
            Utils.sendMessage(player, commandMessages.messagePlayerFly, "%mode%", player.getAllowFlight() ? "&cenabled" : "&cdisabled");
        }
    }

    @Default @CommandAlias("flyspeed") @CommandCompletion("1 2 3 4 5 6 7 8 9 10")
    @CommandPermission("Xiavic.staff.flyspeed")
    public void toggleFlySpeed(final Player player, int speed) {
        player.setFlySpeed(speed / 10f);
        Utils.sendMessage(player, commandMessages.messagePlayerChangeFlySpeed, "%amount%",
            String.valueOf(speed));
    }

    @Default @CommandAlias("god") @CommandCompletion("@players true|false")
    @CommandPermission("Xiavic.staff.god")
    public void toggleGod(Player player, @Optional String otherPlayer, @Optional Boolean enabled) {

        if (otherPlayer != null) {

            if (player.hasPermission("Xiavic.staff.godothers")) {

                Player otherPlayerObj =  Bukkit.getPlayer(otherPlayer);
                assert otherPlayerObj != null;

                // Do Something
                enabled = enabled == null ? !otherPlayerObj.isInvulnerable() : enabled;
                otherPlayerObj.setInvulnerable(enabled);
                Utils.sendMessage(otherPlayerObj, commandMessages.messagePlayerChangeGodMode, "%mode%", enabled.toString());
                Utils.sendMessage(player, commandMessages.messagePlayerChangeGodMode, "%mode%", enabled.toString());

            }

        } else {

            enabled = enabled == null ? !player.isInvulnerable() : enabled;
            player.setInvulnerable(enabled);
            Utils.sendMessage(player, commandMessages.messagePlayerChangeGodMode, "%mode%", enabled.toString());

        }

    }

    @Default @CommandAlias("heal") @CommandPermission("Xiavic.staff.heal") @CommandCompletion("@players")
    public void doHeal(final Player player, @Optional OnlinePlayer otherplayer) {
        if (otherplayer != null) {
            if (player.hasPermission("Xiavic.staff.healothers")) {

                otherplayer.player.setHealth(20);
                otherplayer.player.setSaturation(20);
                otherplayer.player.setFoodLevel(20);
                Utils.sendMessage(otherplayer.player, commandMessages.messagePlayerHealed);
                Utils.sendMessage(player, "commands.heal-other");

            }
        } else {

            player.setHealth(20);
            player.setSaturation(20);
            player.setFoodLevel(20);
            Utils.sendMessage(player, commandMessages.messagePlayerHealed);

        }
    }


    @Default @CommandAlias("healall") @CommandPermission("Xiavic.staff.healall")
    public void doMassHeal(final CommandSender sender) {
        for (final Player player : Bukkit.getOnlinePlayers()) {
            player.setHealth(20);
            player.setSaturation(20);
            player.setFoodLevel(20);
            Utils.sendMessage(player, commandMessages.messagePlayerHealed);
        }
        Utils.sendMessage(sender, commandMessages.messageAllPlayersHealed);
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
        Utils.sendMessage(player, commandMessages.messagePlayerChangeWalkSpeed, "%amount%",
            String.valueOf(speed));
    }

    // Rewrite This Crap
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
}
