package com.github.xiavic.essentials.commands.player;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.github.xiavic.essentials.utils.Utils;
import com.github.xiavic.essentials.utils.handlers.teleportation.TeleportationHandler;
import com.github.xiavic.essentials.utils.handlers.warps.Warp;
import com.github.xiavic.essentials.utils.handlers.warps.WarpManager;
import org.bukkit.entity.Player;

@CommandAlias("warp|warps")
public class WarpCommandHandler extends BaseCommand {

    @Subcommand("list") @CommandPermission("xiavic.warps.list")
    public void listWarps(Player player) {
        Utils.sendMessage(player, "warps.list", "%list%", Utils.capitializeString(WarpManager.getAllWarps()));
    }

    @Subcommand("create") @CommandPermission("xiavic.warps.create")
    public void createWarp(Player player, String warpName, String warpPermNode, boolean enabled) {
        Warp newWarp = new Warp(warpName.toLowerCase(), warpPermNode.toLowerCase(), enabled, player.getLocation());
        switch (WarpManager.registerWarp(newWarp)) {
            case ALREADY_EXISTS:
                Utils.sendMessage(player, "warps.already-exists");
                break;
            case REGISTERED:
                Utils.sendMessage(player, "warps.successfully-created"); // Add Name & Pos to Message
                break;
        }
    }

    @Default
    @Subcommand("use") @CommandPermission("xiavic.warps.use") @CommandCompletion("@publicwarps")
    public void onWarpuse(Player player, String warpName) {
        if (warpName == null) { Utils.sendMessage(player, "warp.invalid-warp"); return; }
        if (!WarpManager.getAllWarpNames().contains(warpName)) { Utils.sendMessage(player, "warp.invalid-warp"); return; }

        Warp localWarp = WarpManager.getWarp(warpName);
        assert localWarp != null;
        if (!localWarp.isEnabled()) { Utils.sendMessage(player, "warp.warp-disabled"); return; }
        if (!player.hasPermission("xiavic.warps." + localWarp.getPermission())) { Utils.sendMessage(player, "warp.no-permission"); return; }
        TeleportationHandler.processPTeleport(player);
        player.teleportAsync(localWarp.getLocation());
        // Send TP Message
        Utils.sendMessage(player, "warp.successful-teleport");


    }

    @Subcommand("delete|remove") @CommandPermission("xiavic.warps.delete")
    public void onDeleteWarp(Player player, String warpName) {
        if (warpName == null) { Utils.sendMessage(player, "warp.invalid-warp"); return; }
        if (!WarpManager.getAllWarpNames().contains(warpName)) { Utils.sendMessage(player, "warp.invalid-warp"); return; }

        Warp localWarp = WarpManager.getWarp(warpName);
        assert localWarp != null;
        switch (WarpManager.unregisterWarp(localWarp)) {
            case FAILED:
                // sendFailedMessage
                player.sendMessage("Yay! You failed again devon!");
                break;
            case UNREGISTERED:
                // send success message
                player.sendMessage("Well Then! You didn't fail devon!");
                break;
        }

    }

    @Subcommand("info") @CommandPermission("xiavic.warps.info") @CommandCompletion("@publicwarps")
    public void onWarpInfo(Player player, String warpName) {
        if (warpName == null) { Utils.sendMessage(player, "warp.invalid-warp"); return; }
        if (!WarpManager.getAllWarpNames().contains(warpName)) { Utils.sendMessage(player, "warp.invalid-warp"); return; }

        Warp localWarp = WarpManager.getWarp(warpName);
        assert localWarp != null;

        // Print message of info
        player.sendMessage(localWarp.getName() + " : " + localWarp.getPermission());

    }

    @Subcommand("save|savewarps") @CommandPermission("xiavic.warps.saveall")
    public void onWarpSave (Player player) {
        // TODO: Save to File with Format and Ability to parse that format back into plugin.
    }

}
