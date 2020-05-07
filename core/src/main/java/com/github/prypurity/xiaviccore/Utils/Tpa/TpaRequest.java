package com.github.prypurity.xiaviccore.Utils.Tpa;

import com.github.prypurity.xiaviccore.Main;
import com.github.prypurity.xiaviccore.Utils.Utils;
import org.bukkit.entity.Player;

public class TpaRequest {

    // private FileConfiguration m = Messages.get();

    private Player origin;
    private Player target;
    private long requestTime;

    public TpaRequest(Player origin, Player target) {
        this.origin = origin;
        this.target = target;
        this.requestTime = System.currentTimeMillis();
        sendRequest();
    }

    public void sendRequest() {
        this.origin.sendMessage(Utils.chat(Main.messages.getString("Tpa").replace("%target%", target.getDisplayName())));
        this.target.sendMessage(Utils.chat(Main.messages.getString("Tpa_Request").replace("%sender%", origin.getDisplayName())));
    }

    public Player getOrigin() {
        return this.origin;
    }

    public Player getTarget() {
        return this.target;
    }

    public boolean isDead(int duration) {
        long currentTime = System.currentTimeMillis();
        return (currentTime - requestTime) / 1000 > duration;
    }

}
