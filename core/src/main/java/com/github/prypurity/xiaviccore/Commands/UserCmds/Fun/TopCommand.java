package com.github.prypurity.xiaviccore.Commands.UserCmds.Fun;

import com.github.prypurity.xiaviccore.Main;
import com.github.prypurity.xiaviccore.Utils.Listeners.TeleportHandler;
import com.github.prypurity.xiaviccore.Utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TopCommand implements CommandExecutor {
    TeleportHandler teleportHandler;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.hasPermission(Main.permissions.getString("Top"))) {
                TeleportHandler.teleport(player, player.getWorld().getHighestBlockAt(player.getLocation()).getLocation().add(0, 1.5, 0));
                return true;
            } else {
                Utils.chat(player, Main.messages.getString("NoPerms"));
            }
        } else {
            Utils.chat(sender, Main.messages.getString("SenderNotPlayer"));
        }
        return false;
    }
}
//Found an easier way to do the command, Thanks to @Lokka30! <3
