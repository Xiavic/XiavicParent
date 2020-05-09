package com.github.xiavic.lib.teleport;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractTeleportRequest {

    // private FileConfiguration m = Messages.get();

    private final Player origin;
    private final Player target;
    private final long requestTime;

    public AbstractTeleportRequest(@NotNull final Player origin, @NotNull final Player target) {
        this.origin = origin;
        this.target = target;
        this.requestTime = System.currentTimeMillis();
    }

    public abstract void sendRequest();

    public @NotNull Player getOrigin() {
        return this.origin;
    }

    public @NotNull Player getTarget() {
        return this.target;
    }

    public boolean isDead(int duration) {
        long currentTime = System.currentTimeMillis();
        return (currentTime - requestTime) / 1000 > duration;
    }

}
