package com.github.xiavic.essentials.commands.player;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.BukkitCommandManager;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import com.github.xiavic.essentials.utils.Utils;
import org.bukkit.command.CommandSender;

public class LinksCommandHandler extends BaseCommand {

    public LinksCommandHandler(BukkitCommandManager commandManager) {
        commandManager.registerCommand(this);
    }

    @CommandAlias("discord") @CommandPermission("xiavic.links.discord")
    public void showDiscord(CommandSender sender) {
        Utils.sendMessage(sender, "links.discord");
    }

    @CommandAlias("forums") @CommandPermission("xiavic.links.forums")
    public void showForums(CommandSender sender) {
        Utils.sendMessage(sender, "links.forums");
    }

    @CommandAlias("website") @CommandPermission("xiavic.links.website")
    public void showWebsite(CommandSender sender) {
        Utils.sendMessage(sender, "links.website");
    }

    @CommandAlias("twitter") @CommandPermission("xiavic.links.twitter")
    public void showTwitter(CommandSender sender) {
        Utils.sendMessage(sender, "links.twitter");
    }

    @CommandAlias("youtube") @CommandPermission("xiavic.links.youtube")
    public void showYoutube(CommandSender sender) {
        Utils.sendMessage(sender, "links.youtube");
    }


}
