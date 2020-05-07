package com.github.prypurity.xiaviccore.Commands.UserCmds.Essential.Teleport;

import com.github.prypurity.xiaviccore.Main;
import com.github.prypurity.xiaviccore.Utils.Listeners.TeleportHandler;
import com.github.prypurity.xiaviccore.Utils.LocationUtils;
import com.github.prypurity.xiaviccore.Utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpawnCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        Player player = (Player) sender;
        Location l = LocationUtils.getLocation("Spawn");
        if (args.length == 1) {
            if (player.hasPermission(Main.permissions.getString("SpawnOthers")) || player.isOp()) {
                Player target = Bukkit.getPlayer(args[0]);
                if (target != null) {
                    TeleportHandler.teleport(target, l);
                    return true;
                } else {
                    Utils.chat(player, Main.messages.getString("PlayerNotFound"));
                }
            }
            Utils.chat(player, Main.messages.getString("NoPerms"));
        } else {
            if (player.hasPermission(Main.permissions.getString("Spawn")) || player.isOp()) {
                TeleportHandler.teleport(player, l);
                return true;
            }
            Utils.chat(player, Main.messages.getString("NoPerms"));
        }
        return false;
    }
}
