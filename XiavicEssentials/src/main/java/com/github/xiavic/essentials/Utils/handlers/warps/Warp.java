package com.github.xiavic.essentials.Utils.handlers.warps;

import org.bukkit.Location;
import org.bukkit.permissions.Permissible;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

public class Warp {

    private UUID uid;
    private boolean enabled;
    private transient volatile Thread locker;

    private String name;
    private String perm_node;
    private Location location;

    public Warp(String name, String warpPerm, boolean enabled, Location location) {
        this.uid = UUID.randomUUID();
        this.name = name;
        this.perm_node = warpPerm;
        this.location = location.clone();
        this.enabled = enabled;
    }

    public Warp(Warp warp) {
        this.name = warp.name;
        this.perm_node = warp.perm_node;
        this.location = warp.location;
        this.enabled = warp.enabled;
        this.uid = warp.uid;
    }

    public String getName() {
        return this.name;
    }

    public Warp setName(String name) {
        this.name = name;
        return this;
    }

    @NotNull public UUID getUniqueID() {
        return uid;
    }

    public boolean hasPermission() {
        boolean result = perm_node != null && !perm_node.isEmpty();
        return result;
    }

    public String getPermission() {
        final String permission = this.perm_node;
        return permission;
    }

    public Warp setPermission(String permission) {
        this.perm_node = permission;
        return this;
    }

    public Location getLocation() {
        final Location cloned = this.location.clone();
        return cloned;
    }

    public Warp setLocation(Location location) {
        this.location = location;
        return this;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Warp setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public boolean canBeAccessedBy(Permissible permissible) {
        return !hasPermission() || permissible.hasPermission(perm_node);
    }


}
