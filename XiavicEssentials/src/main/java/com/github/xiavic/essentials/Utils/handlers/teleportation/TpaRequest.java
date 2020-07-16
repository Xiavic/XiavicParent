package com.github.xiavic.essentials.Utils.handlers.teleportation;

import com.github.xiavic.essentials.Utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TpaRequest {

    private final UUID origin;
    private final UUID target;
    private final long requestTime;

    public TpaRequest(Player origin, Player target) {
        this.origin = origin.getUniqueId();
        this.target = target.getUniqueId();
        this.requestTime = System.currentTimeMillis();
        sendRequest();
    }

    public void sendRequest() {
        Utils.sendMessage(Bukkit.getPlayer(this.origin), "teleport.teleport-request-sent", "%target%", Bukkit.getPlayer(this.target).getDisplayName());
        Utils.sendMessage(Bukkit.getPlayer(this.target), "teleport.teleport-requested", "%sender%", Bukkit.getPlayer(this.origin).getDisplayName());
    }

    public UUID getOrigin() {
        return this.origin;
    }

    public UUID getTarget() {
        return this.target;
    }

    public boolean isDead(int duration) {
        long currentTime = System.currentTimeMillis();
        return (currentTime - requestTime) / 1000 > duration;
    }

}
