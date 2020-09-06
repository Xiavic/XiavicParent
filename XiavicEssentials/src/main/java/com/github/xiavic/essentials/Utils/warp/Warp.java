package com.github.xiavic.essentials.Utils.warp;

import io.papermc.lib.PaperLib;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.permissions.Permissible;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Warp {

    private final UUID uniqueID;
    private boolean enabled;

    private String name;
    private String permission;
    private Location location;

    public Warp(@NotNull final String name, final Location location) {
        this.uniqueID = UUID.randomUUID();
        this.name = name;
        this.location = location.clone();
    }

    public Warp(@NotNull final Warp warp) {
        this.name = warp.name;
        this.permission = warp.permission;
        this.location = warp.location;
        this.enabled = warp.enabled;
        this.uniqueID = warp.uniqueID;
    }

    public final boolean isBaseWarp() {
        return this.getClass() == Warp.class;
    }

    @NotNull public String getName() {
        return this.name;
    }

    public Warp setName(@NotNull final String name) {
        this.name = name;
        return this;
    }

    @NotNull public UUID getUniqueID() {
        return uniqueID;
    }

    public boolean hasPermission() {
        return permission != null && !permission.isEmpty();
    }

    public String getPermission() {
        return this.permission;
    }

    public Warp setPermission(@Nullable final String permission) {
        this.permission = permission;
        return this;
    }

    public @NotNull Location getLocation() {
        return this.location.clone();
    }

    public Warp setLocation(@NotNull final Location location) {
        this.location = location;
        return this;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Warp setEnabled(final boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public boolean canBeAccessedBy(final Permissible permissible) {
        return !hasPermission() || permissible.hasPermission(permission);
    }

    public CompletableFuture<Boolean> teleport(final Entity entity) {
        if (!canBeAccessedBy(entity )|| !isEnabled()) {
            return CompletableFuture.completedFuture(false);
        }
        return PaperLib.teleportAsync(entity, location);
    }

    @Override public boolean equals(final Object o) {

        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Warp warp = (Warp) o;
        if (!Objects.equals(uniqueID, warp.uniqueID)) {
            return false;
        }
        if (!Objects.equals(name, warp.name)) {
            return false;
        }
        if (!Objects.equals(permission, warp.permission)) {
            return false;
        }
        return Objects.equals(location, warp.location);
    }

    @Override public int hashCode() {
        int result = uniqueID.hashCode();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (permission != null ? permission.hashCode() : 0);
        result = 31 * result + (location != null ? location.hashCode() : 0);
        return result;
    }
}
