package com.github.xiavic.essentials.utils;

import com.github.xiavic.essentials.Main;
import com.github.xiavic.essentials.utils.messages.Message;
import com.github.xiavic.essentials.utils.messages.Messages;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.bungeecord.BungeeCordComponentSerializer;
import net.md_5.bungee.api.chat.TextComponent;
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

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    private static final Messages messages = Messages.INSTANCE;

    /**
     * Send a message to a recipient.
     * Taken from: https://github.com/Sauilitired/Hyperverse
     *
     * @param recipient    Receiver of the message
     * @param message      Message to send
     * @param replacements Replacements
     * @see #format(String, String...) for information about string replacements
     */
    @Deprecated
    public static void sendPrefixedMessage(@NotNull final CommandSender recipient,
        @NotNull final Message message, @NotNull final String... replacements) {
        Objects.requireNonNull(recipient);
        final String replacedMessage = format(message.toString(), replacements);
        if (replacedMessage.isEmpty()) {
            return;
        }
        if (replacedMessage.contains("<") && replacedMessage.contains(">")) {
            if (replacedMessage.contains(ChatColor.COLOR_CHAR + "")) {
                final String prefixedMessage = ChatColor.translateAlternateColorCodes('&',
                    messages.messagePrefix.toString() + replacedMessage);
                final Component fixedMessage = BungeeCordComponentSerializer.legacy()
                    .deserialize(TextComponent.fromLegacyText(prefixedMessage));
                recipient.spigot()
                    .sendMessage(BungeeCordComponentSerializer.legacy().serialize(fixedMessage));
            } else {
                final String prefixedMessage = messages.messagePrefix.toString() + replacedMessage;
                final Component component = BungeeCordComponentSerializer.legacy()
                    .deserialize(TextComponent.fromLegacyText(prefixedMessage));
                recipient.spigot()
                    .sendMessage(BungeeCordComponentSerializer.legacy().serialize(component));
            }
        } else {
            final String prefixedMessage = ChatColor.translateAlternateColorCodes('&',
                messages.messagePrefix.toString() + replacedMessage);
            recipient.sendMessage(prefixedMessage);
        }
    }

    @Deprecated
    public static void sendMessage(@NotNull final CommandSender recipient,
        @NotNull final Message message, @NotNull final String... replacements) {
        Objects.requireNonNull(recipient);
        final String replacedMessage = format(message.toString(), replacements);
        if (replacedMessage.isEmpty()) {
            return;
        }
        if (replacedMessage.contains("<") && replacedMessage.contains(">")) {
            if (replacedMessage.contains(ChatColor.COLOR_CHAR + "")) {
                final Component fixedMessage = BungeeCordComponentSerializer.legacy()
                    .deserialize(TextComponent.fromLegacyText(replacedMessage));
                recipient.spigot()
                    .sendMessage(BungeeCordComponentSerializer.legacy().serialize(fixedMessage));
            } else {
                recipient.spigot().sendMessage(BungeeCordComponentSerializer.legacy().serialize(
                    BungeeCordComponentSerializer.legacy()
                        .deserialize(TextComponent.fromLegacyText(replacedMessage))));
            }
        } else {
            recipient.sendMessage(ChatColor.translateAlternateColorCodes('&',
                replacedMessage));
        }
    }

    /**
     * Format a string. Replacements come in pairs of two, where
     * the first value is the string to be replaced, and the second
     * value is the replacement, example:
     * %key1%, value1, %key2%, value2
     * Taken from: https://github.com/Sauilitired/Hyperverse
     *
     * @param message      String to format
     * @param replacements Replacements, needs to be a multiple of 2
     * @return The formatted string
     */
    @NotNull public static String format(@NotNull final String message,
        @NotNull final String... replacements) {
        if (replacements.length % 2 != 0) {
            throw new IllegalArgumentException("Replacement length must be a multiple of two");
        }
        String replacedMessage = Objects.requireNonNull(message);
        for (int i = 0; i < replacements.length; i += 2) {
            replacedMessage = replacedMessage.replace(replacements[i], replacements[i + 1]);
        }
        return replacedMessage;
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

    public static long toTicks(final long duration, final TimeUnit timeUnit) {
        return TimeUnit.MILLISECONDS.convert(duration, timeUnit) * 50; //Each tick is 50ms
    }

    public static long fromTicks(final long ticks, final TimeUnit timeUnit) {
        return timeUnit.convert(ticks / 50, timeUnit);
    }

    public static String parseNMSVersion() {
        final Server server = Bukkit.getServer();
        return server.getClass().getPackage().getName().replace("org.bukkit.craftbukkit", "");
    }


    /*
        This is not a Internationalization (I18N) / Locales capitalize friendly way of hanlding
        messages, if we want to support a translatable languiage file, then we should stick to
        a method of using Internationalization (I18N) / Locales API from ACF to best ensure we
        have muiltiple language support.

        Personally we should worry about this when the plugin is more public and has more
        customimizable features. For now let contributors provide translations.
    */

    public static void sendMessage(CommandSender recipient, String messagesKey, String... replacements) {
        String replacedMessage = format(Main.messages.getString(messagesKey), replacements);
        if (replacedMessage.isEmpty()) {
            Main.getPlugin(Main.class).getLogger().log(Level.WARNING, "Formatting of Message, has failed!");
            Main.getPlugin(Main.class).getLogger().log(Level.SEVERE, messagesKey);
        }

        recipient.sendMessage(convertRGBColor(replacedMessage));

    }

    public static void debugLog(Object object) {
        Main.getPlugin(Main.class).getLogger().log(Level.WARNING, object.toString());;
    }

    /*
        Location Utils

            This section of the utils will hanlde all Locations based
            methods used inside of commands.

     */
    public static boolean areCordsEqual(Location primary, Location secondary) {
        return primary.getWorld() == secondary.getWorld() && primary.getX() == secondary.getX()
                && primary.getY() == secondary.getY() && primary.getZ() == secondary.getZ();
    }


    /*
       Minecraft Chat Based Utils

           This will handle the formatting and displaying of the chat based
           portion of our plugin. Most of the functions here should be standard
           to the Utils.
    */

    public static String convertLegacyCColor(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static String convertRGBColor(String message) {
        if (!Bukkit.getVersion().contains("1.16")) return convertLegacyCColor(message);
        if (message == null) { debugLog("Sorry, RGB Support failed in Utils."); return ""; }

        Pattern pattern = Pattern.compile("(#[a-fA-F0-9]{6})");
        Matcher match = pattern.matcher(message);
        while (match.find()) {
            String color = message.substring(match.start(), match.end());
            message = message.replace(color, net.md_5.bungee.api.ChatColor.of(color) + "");
            match = pattern.matcher(message);
        }

        return convertLegacyCColor(message);
    }

    public static String capitializeString(String message) {
        if (message.isEmpty()) { return message; }
        return message.substring(0, 1).toUpperCase() + message.substring(1);
    }

    public static String toTitleCase(String delimiter, String message) {
        if (message.isEmpty()) return message;
        String[] words = message.split(delimiter);
        if (words.length == 0) return message;

        StringBuilder builder = new StringBuilder(delimiter);
        for (String word : words) {
            builder.append(capitializeString(word));
        }
        return builder.toString();
    }

    @Deprecated
    public static void chat(CommandSender player, String... strings) {
        sendLegacyMessage(player, strings);
    }

    @Deprecated
    public static void sendLegacyMessage(CommandSender player, String... strings) {
        for (String string : strings) {
            player.sendMessage(convertLegacyCColor(string));
        }
    }

    @Deprecated
    public static String titleCase(@NotNull final String delimiter, @NotNull final String s) {
        return toTitleCase(delimiter, s);
    }


}
