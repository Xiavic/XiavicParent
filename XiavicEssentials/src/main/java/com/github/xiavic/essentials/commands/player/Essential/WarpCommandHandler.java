package com.github.xiavic.essentials.commands.player.Essential;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.github.xiavic.essentials.Utils.Utils;
import com.github.xiavic.essentials.Utils.handlers.warps.Warp;
import com.github.xiavic.essentials.Utils.handlers.warps.WarpManager;
import org.bukkit.entity.Player;

@CommandAlias("warp|warps")
public class WarpCommandHandler extends BaseCommand {

    @Default
    @Subcommand("list") @CommandPermission("xiavic.warps.list")
    public void listWarps(Player player) {
        Utils.sendMessage(player, "warps.list", "%list%", WarpManager.getAllWarps());
    }

    @Subcommand("create") @CommandPermission("xiavic.warps.create")
    public void createWarp(Player player, String warpName, String warpPermNode, boolean enabled) {
        Warp newWarp = new Warp(warpName, warpPermNode, enabled, player.getLocation());
        switch (WarpManager.registerWarp(newWarp)) {
            case ALREADY_EXISTS:
                Utils.sendMessage(player, "warps.already-exists");
                break;
            case REGISTERED:
                Utils.sendMessage(player, "warps.successfully-created"); // Add Name & Pos to Message
                break;
        }
    }




}
