package com.github.xiavic.essentials.Utils.events;

import com.github.xiavic.essentials.Utils.Utils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatEvent implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (event.getPlayer().hasPermission("Xiavic.chat.rgbcolors")) {
            event.setMessage(Utils.convertRGBColor(event.getMessage()));
        }
    }

}
