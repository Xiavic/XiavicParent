package com.github.prypurity.xiaviccore.Utils.Tpa;

import com.github.prypurity.xiaviccore.Main;
import com.github.prypurity.xiaviccore.Utils.Listeners.TeleportHandler;
import com.github.prypurity.xiaviccore.Utils.Utils;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TpaHandler implements Runnable {

    // private FileConfiguration m = Messages.get();

    static TeleportHandler teleportHandler;
    private static ArrayList<TpaRequest> requests = new ArrayList<>();
    private static HashMap<TpaRequest, Long> teleports = new HashMap<>();
    private static HashMap<Player, Long> cooldowns = new HashMap<>();
    private static int requestTimeout;
    private static int teleportTime;
    private static int tpaCooldown;
    private static ArrayList<TpaRequest> deadTeleports = new ArrayList<>();
    private static ArrayList<TpaRequest> deadRequests = new ArrayList<>();
    private static ArrayList<Player> deadCooldowns = new ArrayList<>();

    public TpaHandler() {
        requestTimeout = Main.mainConfig.getInt("TpaTimeout");
        teleportTime = Main.mainConfig.getInt("TpaDelay");
        tpaCooldown = Main.mainConfig.getInt("TpaCooldown");
    }

    public static void startCooldown(Player player) {
        cooldowns.put(player, System.currentTimeMillis());
    }

    public static boolean canTpa(Player player) {
        if (cooldowns.containsKey(player)) {
            int remaining = (int) (tpaCooldown - ((System.currentTimeMillis() - cooldowns.get(player)) / 1000));
            player.sendMessage(Utils.chat(Main.messages.getString("TpaCooldown").replace("%time%", String.valueOf(remaining))));
            return false;
        }
        return true;
    }

    public static void parseRequest(Player player, boolean accepted) {
        for (TpaRequest request : requests) {
            if (request.getTarget() == player) {
                if (accepted) {
                    request.getOrigin().sendMessage(Utils.chat(Main.messages.getString("Tpa_Accept").replace("%target%", request.getTarget().getDisplayName()).replace("%time%", String.valueOf(teleportTime))));
                    request.getTarget().sendMessage(Utils.chat(Main.messages.getString("Tpa_Accept1").replace("%sender%", request.getOrigin().getDisplayName())));
                    teleports.put(request, System.currentTimeMillis());
                } else {
                    request.getOrigin().sendMessage(Utils.chat(Main.messages.getString("Tpa_Deny").replace("%target%", request.getTarget().getDisplayName()).replace("%time%", String.valueOf(teleportTime))));
                    request.getTarget().sendMessage(Utils.chat(Main.messages.getString("Tpa_Deny1").replace("%sender%", request.getOrigin().getDisplayName())));
                }
                requests.remove(request);
                return;
            }
        }
        player.sendMessage(Utils.chat(Main.messages.getString("NoRequest")));
    }

    // 0 - success
    // 1 - tpa already pending
    // 2 - tpa disabled
    public static int addRequest(Player origin, Player target) {
        for (TpaRequest tpr : requests) {
            if (tpr.getOrigin() == origin) {
                origin.sendMessage(Utils.chat(Main.messages.getString("TpaPending")));
                return 1;
            }
        }
        if (TeleportHandler.isDisabled(target)) return 2;
        requests.add(new TpaRequest(origin, target));
        startCooldown(origin);
        return 0;
    }

    private static void checkRequests() {
        for (TpaRequest request : requests) {
            if (request.isDead(requestTimeout)) {
                System.out.println("Teleport request timed out");
                deadRequests.add(request);
            }
        }
        requests.removeAll(deadRequests);
        deadRequests.clear();
    }

    private static void checkTeleports() {
        for (Map.Entry<TpaRequest, Long> teleport : teleports.entrySet()) {
            if ((System.currentTimeMillis() - teleport.getValue()) / 1000 > teleportTime) {
                System.out.println("Teleport request fulfilled");
                TpaRequest request = teleport.getKey();
                TeleportHandler.teleport(request.getOrigin(), request.getTarget(), false);
                deadTeleports.add(request);
            }
        }
        for (TpaRequest tpr : deadTeleports) {
            teleports.remove(tpr);
            requests.remove(tpr);
        }
    }

    private static void checkCooldowns() {
        for (Map.Entry<Player, Long> cooldown : cooldowns.entrySet()) {
            if ((System.currentTimeMillis() - cooldown.getValue()) / 1000 > tpaCooldown) {
                System.out.println("Cooldown Expired");
                deadCooldowns.add(cooldown.getKey());
            }
        }
        for (Player player : deadCooldowns) {
            cooldowns.remove(player);
        }
    }

    //@Override
    public void run() {
        checkRequests();
        checkTeleports();
        checkCooldowns();
    }

}
