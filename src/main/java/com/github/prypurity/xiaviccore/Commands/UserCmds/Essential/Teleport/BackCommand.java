package com.github.prypurity.xiaviccore.Commands.UserCmds.Essential.Teleport;

import com.github.prypurity.xiaviccore.Main;
import com.github.prypurity.xiaviccore.Utils.Listeners.TeleportHandler;
import com.github.prypurity.xiaviccore.Utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BackCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            if (player.hasPermission(Main.permissions.getString("Back")) || player.isOp()) {
                try {
                    TeleportHandler.teleport(player, TeleportHandler.getLastLocation(player));
                } catch (Exception e) {
                    Utils.chat(player, Main.messages.getString("BackNone"));
                }
            } else {
                Utils.chat(player, Main.messages.getString("NoPerms"));
            }
            return true;
        }
        Utils.chat(commandSender, Main.messages.getString("SenderNotPlayer"));
        return false;
    }
}
