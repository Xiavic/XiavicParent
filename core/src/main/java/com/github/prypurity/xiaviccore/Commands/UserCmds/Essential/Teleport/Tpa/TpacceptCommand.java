package com.github.prypurity.xiaviccore.Commands.UserCmds.Essential.Teleport.Tpa;

import com.github.prypurity.xiaviccore.Main;
import com.github.prypurity.xiaviccore.Utils.Tpa.TpaHandler;
import com.github.prypurity.xiaviccore.Utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpacceptCommand implements CommandExecutor {
    TpaHandler tpaHandler;

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] strings) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            if (player.hasPermission(Main.permissions.getString("TpAccept")) || player.isOp()) {
                TpaHandler.parseRequest(player, true);
            } else {
                Utils.chat(player, Main.messages.getString("NoPerms"));
            }
            return true;
        } else {
            Utils.chat(commandSender, Main.messages.getString("SenderNotPlayer"));
        }
        return false;
    }
}