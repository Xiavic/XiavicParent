package com.github.xiavic.essentials.Commands.staff.noncheat.teleport;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.BukkitCommandManager;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import com.github.xiavic.essentials.Main;
import com.github.xiavic.essentials.Utils.Teleportation.TeleportationHandler;
import com.github.xiavic.essentials.Utils.Utils;
import com.github.xiavic.essentials.Utils.messages.CommandMessages;
import com.github.xiavic.essentials.Utils.messages.TeleportationMessages;
import com.github.xiavic.lib.teleport.ITeleportHandler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

@SuppressWarnings("unused") public class StaffTeleportCommandHandler extends BaseCommand {

    private static final TeleportationMessages teleportationMessages =
        TeleportationMessages.INSTANCE;

    private static final CommandMessages commandMessages = CommandMessages.INSTANCE;

    private final TeleportationHandler teleportHandler;

    public StaffTeleportCommandHandler(@NotNull final BukkitCommandManager commandManager,
        @NotNull final TeleportationHandler teleportHandler) {
        commandManager.registerCommand(this);
        this.teleportHandler = teleportHandler;
    }

    @CommandAlias("goto") @CommandPermission("Xiavic.staff.tp") @CommandCompletion("@players")
    public void doTeleport(Player sender, OnlinePlayer target) {
        teleportHandler.teleportToPlayer(sender, target.player, false);
        Utils.sendMessage(sender, teleportationMessages.messageTeleported, "has", "have",
            "%target1%", "You", "%target2%", target.player.getDisplayName());
    }

    @CommandAlias("teleport|tp") @CommandPermission("Xiavic.staff.tp.other") @CommandCompletion("@players")
    public void doTeleport(CommandSender sender, OnlinePlayer toTeleport, OnlinePlayer target) {
        switch (teleportHandler.teleportRemote(toTeleport.player, target.player)) {
            case SUCCESS:
                Utils.sendMessage(sender, teleportationMessages.messageTeleported, "%target1%",
                    toTeleport.player.getDisplayName(), "%target2%", target.player.getDisplayName());
                break;
            case P1DISABLED:
                Utils.sendMessage(sender, teleportationMessages.messageTeleportationDisabled,
                    "%target%", toTeleport.player.getDisplayName());
                break;
            case P2DISABLED:
                Utils.sendMessage(sender, teleportationMessages.messageTeleportationDisabled,
                    "%target%", target.player.getDisplayName());
        }
    }

    @CommandAlias("teleportall|tpall") @CommandPermission("Xiavic.staff.tpall")
    public void doMassTeleport(final Player sender) {
        // TODO send mesage
        long immunity = Main.mainConfig.getLong("TeleportAll.Immunity");
        if (immunity != 0) {
            sender.setInvulnerable(true);
        }
        final boolean senderInvulnerable = sender.isInvulnerable();
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p == sender) {
                continue;
            }
            boolean invulnerable = sender.isInvulnerable();
            if (immunity != 0) {
                sender.setInvulnerable(true);
            }

            teleportHandler.teleportToPlayer(sender, p, true).thenAccept(
                    (unused) -> Bukkit.getScheduler().runTaskLater(Main.getPlugin(Main.class),
                            () -> p.setInvulnerable(invulnerable), Utils.toTicks(immunity, TimeUnit.SECONDS))
            );
        }
        Bukkit.getScheduler().runTaskLater(Main.getPlugin(Main.class),
            () -> sender.setInvulnerable(senderInvulnerable),
            Utils.toTicks(immunity, TimeUnit.SECONDS)); //Revert invulnerability
    }

    @CommandAlias("teleporthere|tphere") @CommandPermission("Xiavic.staff.tphere") @CommandCompletion("@players")
    public void doTeleportHere(Player sender, OnlinePlayer otherPlayer) {
        Utils.sendMessage(otherPlayer.player, teleportationMessages.messageForceTeleported);
        teleportHandler.teleportToPlayer(sender, otherPlayer.player, true);
    }

    @Subcommand("teleportposition|tppos") @CommandPermission("Xiavic.staff.tppos")
    public void doTeleportPosition(Player sender, double x, double y, double z) {
        World world = sender.getWorld();
        teleportHandler.teleportToLocation(sender, new Location(world, x, y, z));
    }

}
