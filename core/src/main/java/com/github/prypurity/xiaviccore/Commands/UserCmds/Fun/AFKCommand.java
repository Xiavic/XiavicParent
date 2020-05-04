package com.github.prypurity.xiaviccore.Commands.UserCmds.Fun;

import com.github.prypurity.xiaviccore.Utils.Listeners.AFKHandler;
import com.github.prypurity.xiaviccore.Utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import static com.github.prypurity.xiaviccore.Main.messages;
import static com.github.prypurity.xiaviccore.Main.permissions;

public class AFKCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull final CommandSender sender, @NotNull final Command command,
        @NotNull final String label, @NotNull final String[] args) {
        if (!sender.hasPermission(permissions.getString("Afk"))) {
            Utils.chat(sender, messages.getString("NoPerms"));
            return true;
        }
        if (!(sender instanceof Player)) {
            Utils.chat(sender, messages.getString("SenderNotPlayer"));
            return true;
        }
        final UUID player = ((Player) sender).getUniqueId();
        final boolean currentlyAfk = AFKHandler.INSTANCE.isAFK(player);
        AFKHandler.INSTANCE.setAFK(player, !currentlyAfk);
        //TODO send message?
        return true;
    }
}
