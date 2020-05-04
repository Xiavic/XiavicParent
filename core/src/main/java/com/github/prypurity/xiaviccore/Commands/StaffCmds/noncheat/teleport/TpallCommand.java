package com.github.prypurity.xiaviccore.Commands.StaffCmds.noncheat.teleport;

import com.github.prypurity.xiaviccore.Main;
import com.github.prypurity.xiaviccore.Utils.Listeners.TeleportHandler;
import com.github.prypurity.xiaviccore.Utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpallCommand implements CommandExecutor {
    TeleportHandler teleportHandler;

    // TODO probably ought to create messages in the config files but it can stay like this for now

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            if (player.hasPermission(Main.permissions.getString("Tpall")) || player.isOp()) {
                if (strings.length == 1) {
                    Player target1 = Bukkit.getPlayer(strings[0]);
                    if (target1 != null) {
                        if (TeleportHandler.isDisabled(target1)) {
                            Utils.chat(player,
                                    "Teleporting all players to %target%, I'm sure they'll be happy about it"
                                            .replace("%target%", target1.getDisplayName()));
                            for (Player target : Bukkit.getOnlinePlayers()) {
                                if (target != target1) {
                                    boolean result =
                                            TeleportHandler.teleport(target1, target, true);
                                    if (result)
                                        Utils.chat(target, "You are being teleported!");
                                }
                            }
                        }
                    }
                } else {
                    Utils.chat(player,
                            "Teleporting all players to you, I'm sure they'll be happy about it");
                    for (Player target : Bukkit.getOnlinePlayers()) {
                        if (target != player) {
                            boolean result = TeleportHandler.teleport(player, target, true);
                            if (result)
                                Utils.chat(target, "You are being teleported!");
                        }
                    }
                }
                return true;
            }
        }
        Utils.chat(commandSender, Main.messages.getString("SenderNotPlayer"));
        return false;
    }
}
