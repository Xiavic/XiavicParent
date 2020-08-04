package com.github.xiavic.essentials.utils.handlers.privatewarps;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

public class PrivateWarp {

    private UUID uid;
    private String name;
    private UUID warpOwner;
    private Location location;
    private boolean enabled;
    private ArrayList<UUID> allowedPlayers;


    public PrivateWarp(String name, boolean enabled, Player player) {
        this.uid = UUID.randomUUID();
        this.name = name;
        this.warpOwner = player.getUniqueId();
        this.location = player.getLocation().clone();
        this.enabled = enabled;
        this.allowedPlayers = new ArrayList<>();
    }


    public UUID getUID() { return this.uid; }
    public String getName() { return this.name; }
    public UUID getWarpOwner() { return this.warpOwner; }
    public Location getLocation() { return this.location; }
    public boolean isEnabled() { return this.enabled;}
    public ArrayList<UUID> getAllowedPlayers() { return this.allowedPlayers; }

    public void setName(String name) { this.name = name; }
    public void setLocation(Location newLocation) { this.location = newLocation; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public void addPlayerToWarp(UUID playerUID) { this.allowedPlayers.add(playerUID); }
    public void removePlayerFromWarp(UUID playerUID) { this.allowedPlayers.remove(playerUID); }


}
