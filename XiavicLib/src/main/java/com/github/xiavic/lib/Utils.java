package com.github.xiavic.lib;

import com.github.xiavic.lib.teleport.ITeleportHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;

public class Utils {

    //EZ Chat Colors

    private static ITeleportHandler handler;

    public static String colorize(final String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static @NotNull String[] colorize(final String... strings) {
        if (strings == null) {
            return new String[0];
        }
        final String[] arr = new String[strings.length];
        int index = 0;
        for (final String s : strings) {
            arr[index++] = colorize(s);
        }
        return arr;
    }

    public static @NotNull String capitalise(@NotNull final String s) {
        if (s.isEmpty()) {
            return s;
        }
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    public static @NotNull String[] capitalise(final String... strings) {
        if (strings == null) {
            return new String[0];
        }
        final String[] arr = new String[strings.length];
        int index = 0;
        for (final String s : strings) {
            arr[index++] = capitalise(s);
        }
        return arr;
    }

    // Sends messages to a player directly and makes the 'chat' name make more sense
    public static void chat(@NotNull final Player player, final String... string) {
        if (string == null) {
            return;
        }
        StringJoiner joiner = new StringJoiner("\n");
        for (final String s : colorize(string)) {
            joiner.add(s);
        }
        player.sendMessage(joiner.toString());
    }

    // An overload so you can do the same thing when you need to send a message to console
    // from inside a command class
    public static void chat(final CommandSender sender, final String... string) {
        if (string == null) {
            return;
        }
        StringJoiner joiner = new StringJoiner("\n");
        for (final String s : colorize(string)) {
            joiner.add(s);
        }
        sender.sendMessage(joiner.toString());
    }

    /**
     * Attempt to replace the currently held item to a new one. The old item will be moved to
     * the next empty slot.
     *
     * @param player    The player's {@link Player} instance.
     * @param itemStack The {@link ItemStack} to replace - if null, air will be set instead.
     * @return Returns whether or not the swap was successful.
     */
    public static boolean placeInCursorSlot(@NotNull final Player player,
        @Nullable final ItemStack itemStack) {
        final PlayerInventory inventory = player.getInventory();
        final int firstEmpty = inventory.firstEmpty();
        if (firstEmpty == -1) {
            return false;
        }
        final int held = inventory.getHeldItemSlot();
        inventory.setItem(firstEmpty, inventory.getItem(held));
        inventory.setItem(held, itemStack == null ? null : itemStack.clone());
        return true;
    }

    public static long toTicks(final long duration, @NotNull final TimeUnit timeUnit) {
        return TimeUnit.MILLISECONDS.convert(duration, timeUnit) * 50; //Each tick is 50ms
    }

    public static long fromTicks(final long ticks, @NotNull final TimeUnit timeUnit) {
        return timeUnit.convert(ticks / 50, timeUnit);
    }

    public static @NotNull String parseNMSVersion() {
        final Server server = Bukkit.getServer();
        return server.getClass().getPackage().getName().replace("org.bukkit.craftbukkit", "");
    }

}
