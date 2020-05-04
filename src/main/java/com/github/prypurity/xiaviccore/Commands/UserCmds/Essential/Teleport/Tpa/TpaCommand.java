package com.github.prypurity.xiaviccore.Commands.UserCmds.Essential.Teleport.Tpa;

import com.github.prypurity.xiaviccore.Main;
import com.github.prypurity.xiaviccore.Utils.Tpa.TpaHandler;
import com.github.prypurity.xiaviccore.Utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpaCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] strings) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            if (player.hasPermission(Main.permissions.getString("Tpa")) || player.isOp()) {
                if (strings.length == 1) {
                    if (!strings[0].equalsIgnoreCase(player.getName())) {
                        if (TpaHandler.canTpa(player)) {
                            try {
                                Player target = Bukkit.getPlayer(strings[0]);
                                int result = TpaHandler.addRequest(player, target);
                                if (result == 2) {
                                    Utils.chat(player, Main.messages.getString("TpDisabled").replace("%target%", target.getDisplayName()));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                Utils.chat(player, Main.messages.getString("PlayerNotFound"));
                            }
                        }
                    } else {
                        Utils.chat(player, Main.messages.getString("TpSelf"));
                    }
                    return true;
                } else {
                    Utils.chat(player, "You must specify a target!");
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
