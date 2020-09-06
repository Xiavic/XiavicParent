package com.github.xiavic.essentials.Utils.warp;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.permissions.Permissible;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Represents a warp which would be used in /pw, /privatewarp
 */
public class PrivateWarp extends Warp {

    private UUID owner;
    private Collection<UUID> whitelisted = new HashSet<>();
    private Collection<UUID> blacklisted = new HashSet<>();

    public PrivateWarp(@NotNull final String name, @NotNull Location location,
                       @NotNull final UUID owner) {
        super(name, location);
        this.owner = owner;
    }

    public PrivateWarp(@NotNull final String name, @NotNull Location location,
                       @NotNull final UUID owner, @Nullable final Collection<UUID> whitelisted,
                       @Nullable final Collection<UUID> blacklisted) {
        this(name, location, owner);
        if (whitelisted != null) {
            this.whitelisted = new HashSet<>(whitelisted);
        }
        if (blacklisted != null) {
            this.blacklisted = new HashSet<>(blacklisted);
        }
    }

    public PrivateWarp (@NotNull final PrivateWarp privateWarp) {
        super(privateWarp);
        this.whitelisted = new HashSet<>(privateWarp.whitelisted);
        this.blacklisted = privateWarp.blacklisted;
    }

    @NotNull
    public Collection<UUID> getWhitelisted() {
        return new ArrayList<>(this.whitelisted);
    }

    @NotNull
    public Collection<UUID> getBlacklisted() {
        return new ArrayList<>(blacklisted);
    }

    public boolean isWhitelisted(final UUID uuid) {
        return whitelisted.contains(uuid);
    }

    public boolean isBlacklisted(final UUID uuid) {
        return blacklisted.contains(uuid);
    }

    @NotNull
    public UUID getOwner() {
        return this.owner;
    }

    @NotNull
    public PrivateWarp setOwner(final UUID owner) {
        this.owner = owner;
        return this;
    }

    /**
     * Adds a player to the whitelist and if the player is
     * blacklisted, they are removed from the blacklist.
     *
     * @param player The UniqueID of the player.
     */
    public void addToWhitelist(final UUID player) {
        whitelisted.remove(player);
        whitelisted.add(player);
        blacklisted.remove(player);
    }

    @Override
    public boolean canBeAccessedBy(final Permissible permissible) {
        if (permissible instanceof Entity) {
            Entity entity = (Entity) permissible;
            final UUID uuid = entity.getUniqueId();
            boolean bool = !isBlacklisted(uuid);
            bool = isEnabled() && whitelisted.isEmpty() ? bool : isWhitelisted(uuid);
            return bool;
        }
        return false;
    }

    @Override
    public String getPermission() {
        return null;
    }

    @Override
    public boolean hasPermission() {
        return false;
    }

    @Override
    public Warp setPermission(final @Nullable String permission) {
        return this;
    }

    /**
     * Adds a player to the blacklist and if the player is
     * blacklisted, they are removed from the whitelist.
     *
     * @param player The UniqueID of the player.
     */
    public void addToBlacklist(final UUID player) {
        blacklisted.remove(player);
        blacklisted.add(player);
        whitelisted.remove(player);
    }

    public void removeFromWhitelist(final UUID player) {
        whitelisted.remove(player);
    }

    public void removeFromBlacklist(final UUID player) {
        blacklisted.remove(player);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        if (!super.equals(o))
            return false;
        PrivateWarp that = (PrivateWarp) o;
        if (!Objects.equals(whitelisted, that.whitelisted)) {
            return false;
        }
        if (!Objects.equals(owner, that.owner)) {
            return false;
        }
        final boolean value = Objects.equals(blacklisted, that.blacklisted);
        return value;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (whitelisted != null ? whitelisted.hashCode() : 0);
        result = 31 * result + (blacklisted != null ? blacklisted.hashCode() : 0);
        result = 31 * result + owner.hashCode();
        return result;
    }
}
