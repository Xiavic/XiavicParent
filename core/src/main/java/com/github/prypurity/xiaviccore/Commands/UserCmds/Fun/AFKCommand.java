package com.github.prypurity.xiaviccore.Commands.UserCmds.Fun;

import com.github.prypurity.xiaviccore.Utils.Listeners.AFKHandler;
import com.github.prypurity.xiaviccore.Utils.Utils;
import com.github.prypurity.xiaviccore.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class AFKCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull final CommandSender sender, @NotNull final Command command,
        @NotNull final String label, @NotNull final String[] args) {
        if (sender.hasPermission(Main.permissions.getString("Afk"))) {
            if (sender instanceof Player) {
                final UUID player = ((Player) sender).getUniqueId();
                final boolean currentlyAfk = AFKHandler.INSTANCE.isAFK(player);
                AFKHandler.INSTANCE.setAFK(player, !currentlyAfk, true);
            } else {
                Utils.chat(sender, Main.messages.getString("SenderNotPlayer"));
                return true;
            }
        } else {
            Utils.chat(sender, Main.messages.getString("NoPerms"));
        }
        return true;
    }
}
