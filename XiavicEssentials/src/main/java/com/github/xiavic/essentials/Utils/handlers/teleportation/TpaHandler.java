package com.github.xiavic.essentials.Utils.handlers.teleportation;

import com.github.xiavic.essentials.Main;
import com.github.xiavic.essentials.Utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class TpaHandler implements ITeleportRequestHandler, Runnable {

    private TeleportationHandler tpHandler = new TeleportationHandler();

    private final int requestTimeout;
    private final int teleportTime;
    private final int tpaCooldown;

    private List<TpaRequest> requests = new ArrayList<>();
    private Map<TpaRequest, Long> teleports = new HashMap<>();
    private Map<UUID, Long> cooldowns = new HashMap<>();
    private List<TpaRequest> deadTeleports = new ArrayList<>();
    private List<TpaRequest> deadRequests = new ArrayList<>();
    private List<UUID> deadCooldowns = new ArrayList<>();

    public TpaHandler() {
        requestTimeout = Main.mainConfig.getInt("TpaHandling.TpaTimeout");
        teleportTime = Main.mainConfig.getInt("TpaHandling.TpaDelay");
        tpaCooldown = Main.mainConfig.getInt("TpaHandling.TpaCooldown");
    }

    @Override
    public boolean loadTeleportHandler() { return false; }

    @Override
    public void startCooldown(Player player) {
        cooldowns.put(player.getUniqueId(), System.currentTimeMillis());
    }

    @Override
    public boolean canTpa(Player player) {
        if (cooldowns.containsKey(player.getUniqueId())) {

            int remaining = (int) (tpaCooldown - ((System.currentTimeMillis() - cooldowns.get(player.getUniqueId())) / 1000));
            Utils.sendMessage(player, "teleport.teleport-cooldown", "%time%", String.valueOf(remaining));
            return false;
        }
        return true;
    }

    @Override
    public void parseRequest(Player player, boolean accepted) {
        for (TpaRequest request : requests) {
            if (Bukkit.getPlayer(request.getTarget()) == player) {

                Utils.debugLog(Bukkit.getPlayer(request.getTarget()).getDisplayName() + " | " + Bukkit.getPlayer(request.getOrigin()).getDisplayName());

                Utils.sendMessage(
                        Bukkit.getPlayer(request.getOrigin()), accepted ?
                                "teleport.teleport-request-acccepted" : "teleport.teleport-request-denied", "%target%",
                        Objects.requireNonNull(Bukkit.getPlayer(request.getTarget())).getDisplayName(), "%time%", String.valueOf(teleportTime)
                );

                Utils.sendMessage(
                        Bukkit.getPlayer(request.getTarget()), accepted ?
                                "teleport.teleport-accepted" : "teleport.teleport-denied", "%sender%",
                        Objects.requireNonNull(Bukkit.getPlayer(request.getOrigin())).getDisplayName()
                );

                if (accepted) {
                    teleports.put(request, System.currentTimeMillis());
                }

                requests.remove(request);
                return;

            } else {
                Utils.debugLog("Well Shit Brother, That didn't work!");
            }
        }
    }


    @Override
    public ETeleportResults addRequest(Player origin, Player target) {
        for (TpaRequest request : requests) {
            if (request.getOrigin() == origin.getUniqueId()) {
                Utils.sendMessage(origin, "teleport.teleport-pending");
                return ETeleportResults.PENDING;
            }
        }

        // Check if player has teleportation disabled.
        if (tpHandler.checkTPDisabled(origin)) return ETeleportResults.P1DISABLED;
        if (tpHandler.checkTPDisabled(target)) return ETeleportResults.P2DISABLED;

        requests.add(new TpaRequest(origin, target));
        startCooldown(origin);
        return ETeleportResults.SUCCESS;
    }

    @Override
    public void doChecks() {}

    private void checkRequests() {
        for (TpaRequest request : requests) {
            if (request.isDead(requestTimeout)) {
                deadRequests.add(request);
            }
        }
        requests.removeAll(deadRequests);
        deadRequests.clear();
    }

    private void checkTeleports() {
        for (Map.Entry<TpaRequest, Long> teleport : teleports.entrySet()) {
            if ((System.currentTimeMillis() - teleport.getValue()) / 1000 > teleportTime) {
                TpaRequest request = teleport.getKey();
                tpHandler.teleportToPlayer(Bukkit.getPlayer(request.getOrigin()), Bukkit.getPlayer(request.getTarget()), false);
                deadTeleports.add(request);
            }
        }
        for (TpaRequest tpr : deadTeleports) {
            teleports.remove(tpr);
            requests.remove(tpr);
        }
    }

    private void checkCooldowns() {
        for (Map.Entry<UUID, Long> cooldown : cooldowns.entrySet()) {
            if ((System.currentTimeMillis() - cooldown.getValue()) / 1000 > tpaCooldown) {
                deadCooldowns.add(cooldown.getKey());
            }
        }
        for (UUID player : deadCooldowns) {
            cooldowns.remove(player);
        }
    }

    @Override
    public void run() {
        checkRequests();
        checkTeleports();
        checkCooldowns();
    }
}
