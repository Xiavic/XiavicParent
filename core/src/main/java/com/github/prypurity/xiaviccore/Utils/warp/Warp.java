package com.github.prypurity.xiaviccore.Utils.warp;

import com.github.prypurity.xiaviccore.Utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class Warp {

    private final String name;
    private final UUID creator;
    private final UUID uniqueID;
    private final Location location;
    private final String permission;
    private Collection<UUID> whitelist = new HashSet<>();

    /**
     * Constructs a new warp.
     *
     * @param creator    The UUID of the creator.
     * @param name       The name of this warp.
     * @param location   The location of this warp.
     * @param permission The permission to use this warp - may be null.
     */
    public Warp(@NotNull final UUID creator, @NotNull final String name,
        @NotNull final Location location, @Nullable final String permission) {
        this.creator = creator;
        this.name = Utils.chat(name);
        this.location = location;
        Objects.requireNonNull(location.getWorld());
        this.permission = permission;
        this.uniqueID = UUID.randomUUID();
    }

    /**
     * Warp to reconstruct from a serial Map
     *
     * @param serial The serial map.
     * @see #serialize()
     */
    public Warp(final Map<String, String> serial) {
        this.uniqueID = UUID.fromString(serial.get("UniqueID"));
        this.creator = UUID.fromString(serial.get("Creator"));
        this.name = serial.get("Name");
        this.permission = serial.get("Permission");
        final double x, y, z;
        final float yaw, pitch;
        final String[] rawLoc = serial.get("LocationXYZ").split(",");
        final World world = Bukkit.getWorld(serial.get("World"));
        x = Double.parseDouble(rawLoc[0]);
        y = Double.parseDouble(rawLoc[1]);
        z = Double.parseDouble(rawLoc[2]);
        yaw = Float.parseFloat(serial.get("Yaw"));
        pitch = Float.parseFloat(serial.get("Pitch"));
        final Location location = new Location(world, x, y, z);
        location.setYaw(yaw);
        location.setPitch(pitch);
        this.location = location;
        final String rawData = serial.get("Whitelist");
        final String[] dataArray = rawData.split(",");
        this.whitelist = new HashSet<>(dataArray.length);
        for (final String uuid : dataArray) {
            if (!uuid.isEmpty()) {
                addToWhitelist(UUID.fromString(uuid));
            }
        }
    }

    public boolean isVisibleTo(final UUID uuid) {
        return this.whitelist.contains(uuid);
    }

    public void addToWhitelist(final UUID player) {
        removeFromWhitelist(player);
        this.whitelist.add(player);
    }

    public void removeFromWhitelist(final UUID player) {
        this.whitelist.remove(player);
    }

    public void setWhitelist(final Collection<UUID> whitelist) {
        this.whitelist = new HashSet<>(whitelist);
    }


    public Collection<UUID> getWhitelisted() {
        return new HashSet<>(this.whitelist);
    }

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return location.clone();
    }

    public boolean hasPermission() {
        return permission != null && !permission.isEmpty();
    }

    public String getPermission() {
        return permission;
    }

    /**
     * Serialize this map into a String, String representation of this class.
     */
    public Map<String, String> serialize() {
        final Map<String, String> ret = new HashMap<>(5);
        ret.put("UniqueID", uniqueID.toString());
        ret.put("Creator", creator.toString());
        ret.put("Name", name);
        ret.put("Permission", permission);
        ret.put("World", location.getWorld().getName());
        ret.put("LocationXYZ", location.getX() + "," + location.getY() + "," + location.getZ());
        ret.put("Yaw", String.valueOf(location.getYaw()));
        ret.put("Pitch", String.valueOf(location.getPitch()));
        StringBuilder builder = new StringBuilder();
        for (final UUID uuid : whitelist) {
            builder.append(uuid.toString()).append(",");
        }
        builder.delete(builder.length(), builder.length()); //Remove last ","
        ret.put("Whitelist", builder.toString());
        return ret;
    }
}
