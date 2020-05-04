package com.github.prypurity.xiaviccore.Commands.UserCmds.Fun.Links;

import com.github.prypurity.xiaviccore.Main;
import com.github.prypurity.xiaviccore.Utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class YoutubeCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        Player player = (Player) sender;
        if (player.hasPermission(Main.permissions.getString("Youtube")) || player.isOp()) {
            player.sendMessage(Utils.chat(Main.messages.getString("Youtube")));
        } else {
            player.sendMessage(Utils.chat(Main.messages.getString("NoPerms")));
        }
        return false;
    }
}