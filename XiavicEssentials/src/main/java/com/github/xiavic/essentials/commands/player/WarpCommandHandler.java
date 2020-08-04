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
    @Subcommand("use") @CommandPermission("xiavic.warps.use") @CommandCompletion("@enabledPublicWarps")
    public void onWarpuse(Player player, String warpName) {
        if (warpName == null) { Utils.sendMessage(player, "warps.invalid-warp"); return; }
        if (!WarpManager.getAllWarpNames().contains(warpName)) { Utils.sendMessage(player, "warp.invalid-warp"); return; }

        Warp localWarp = WarpManager.getWarp(warpName);
        assert localWarp != null;
        if (!localWarp.isEnabled()) { Utils.sendMessage(player, "warps.warp-disabled"); return; }
        if (!player.hasPermission("xiavic.warps." + localWarp.getPermission())) { Utils.sendMessage(player, "warps.no-permission"); return; }
        TeleportationHandler.processPTeleport(player);
        player.teleportAsync(localWarp.getLocation());
        // Send TP Message
        Utils.sendMessage(player, "warps.successful-teleport", "%warp%", localWarp.getName().toUpperCase());


    }

    @Subcommand("delete|remove") @CommandPermission("xiavic.warps.delete") @CommandCompletion("@publicwarps")
    public void onDeleteWarp(Player player, String warpName) {
        if (warpName == null) { Utils.sendMessage(player, "warps.invalid-warp"); return; }
        if (!WarpManager.getAllWarpNames().contains(warpName)) { Utils.sendMessage(player, "warps.invalid-warp"); return; }

        Warp localWarp = WarpManager.getWarp(warpName);
        assert localWarp != null;
        switch (WarpManager.unregisterWarp(localWarp)) {
            case FAILED:
                // sendFailedMessage\
                Utils.sendMessage(player, "warps.error-occurred");
                break;
            case UNREGISTERED:
                // send success message
                Utils.sendMessage(player, "warps.warp-removed", "%warp%", localWarp.getName().toUpperCase());
                break;
        }

    }

    @Subcommand("info") @CommandPermission("xiavic.warps.info") @CommandCompletion("@publicwarps")
    public void onWarpInfo(Player player, String warpName) {
        if (warpName == null) { Utils.sendMessage(player, "warps.invalid-warp"); return; }
        if (!WarpManager.getAllWarpNames().contains(warpName)) { Utils.sendMessage(player, "warps.invalid-warp"); return; }

        Warp localWarp = WarpManager.getWarp(warpName);
        assert localWarp != null;

        // Send the player information about the warp.
        Utils.sendMessage(player, "warps.info", "%warpName%", Utils.capitializeString(localWarp.getName()),
                "%permnode%", localWarp.getPermission(),
                "%warpEnabled%", String.valueOf(localWarp.isEnabled()),
                "%warpWorld%", localWarp.getLocation().getWorld().getName(),
                "%warpX%", String.valueOf(localWarp.getLocation().getX()),
                "%warpY%", String.valueOf(localWarp.getLocation().getY()),
                "%warpZ%", String.valueOf(localWarp.getLocation().getZ())
        );

    }

    @Subcommand("save|savewarps") @CommandPermission("xiavic.warps.saveall")
    public void onWarpSave (Player player) {
        WarpManager.saveAllWarps();
        player.sendMessage("RIP CHECK RESOURCES");
    }

    @Subcommand("load|loadwarps") @CommandPermission("xiavic.warps.loadall")
    public void onWarpLoad (Player player) {
        WarpManager.loadWarpsFromFile();
        player.sendMessage("RIP NEW WARPS ADDED");
    }

    @Subcommand("edit") @CommandPermission("xiavic.warps.loadall") @CommandCompletion("@publicwarps @editTypes *")
    public void onEditWarp (Player player, String warpName, String node, @Optional String value) {
        Warp localWarp = WarpManager.getWarp(warpName);
        assert localWarp != null;
        switch (node) {
            case "NAME":
                if (value == null) { Utils.sendMessage(player, "warps.edit-failed"); break; }
                localWarp.setName(value);
                Utils.sendMessage(player, "warps.edit-success");
                break;

            case "PERM":
                if (value == null) { Utils.sendMessage(player, "warps.edit-failed"); break; }
                localWarp.setPermission(value);
                Utils.sendMessage(player, "warps.edit-success");
                break;

            case "ENABLED":
                if (value == null) { Utils.sendMessage(player, "warps.edit-failed"); break; }
                localWarp.setEnabled(Boolean.parseBoolean(value));
                Utils.sendMessage(player, "warps.edit-success");
                break;

            case "LOCATION":
                localWarp.setLocation(player.getLocation().clone());
                Utils.sendMessage(player, "warps.edit-success");
                break;
        }

    }

}
