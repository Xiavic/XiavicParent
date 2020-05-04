package com.github.prypurity.xiaviccore;

import com.github.prypurity.xiaviccore.Commands.StaffCmds.cheats.CheatArmor;
import com.github.prypurity.xiaviccore.Commands.StaffCmds.cheats.CheatEXP;
import com.github.prypurity.xiaviccore.Commands.StaffCmds.noncheat.*;
import com.github.prypurity.xiaviccore.Commands.StaffCmds.noncheat.teleport.TPPosCommand;
import com.github.prypurity.xiaviccore.Commands.StaffCmds.noncheat.teleport.TPhereCommand;
import com.github.prypurity.xiaviccore.Commands.StaffCmds.noncheat.teleport.TeleportCommand;
import com.github.prypurity.xiaviccore.Commands.StaffCmds.noncheat.teleport.TpallCommand;
import com.github.prypurity.xiaviccore.Commands.UserCmds.Essential.*;
import com.github.prypurity.xiaviccore.Commands.UserCmds.Essential.Teleport.BackCommand;
import com.github.prypurity.xiaviccore.Commands.UserCmds.Essential.Teleport.RandomTPCommand;
import com.github.prypurity.xiaviccore.Commands.UserCmds.Essential.Teleport.SpawnCommand;
import com.github.prypurity.xiaviccore.Commands.UserCmds.Essential.Teleport.Tpa.TpaCommand;
import com.github.prypurity.xiaviccore.Commands.UserCmds.Essential.Teleport.Tpa.TpacceptCommand;
import com.github.prypurity.xiaviccore.Commands.UserCmds.Essential.Teleport.Tpa.TpdenyCommand;
import com.github.prypurity.xiaviccore.Commands.UserCmds.Fun.*;
import com.github.prypurity.xiaviccore.Commands.UserCmds.Fun.Links.*;
import com.github.prypurity.xiaviccore.Utils.EquipAnything.EquipEvents;
import com.github.prypurity.xiaviccore.Utils.Files.Messages;
import com.github.prypurity.xiaviccore.Utils.Files.Permissions;
import com.github.prypurity.xiaviccore.Utils.Listeners.AFKHandler;
import com.github.prypurity.xiaviccore.Utils.Listeners.JoinQuit;
import com.github.prypurity.xiaviccore.Utils.Listeners.RespawnEvent;
import com.github.prypurity.xiaviccore.Utils.Listeners.TeleportHandler;
import com.github.prypurity.xiaviccore.Utils.Tpa.TpaHandler;
import de.leonhard.storage.Json;
import de.leonhard.storage.LightningBuilder;
import de.leonhard.storage.Yaml;
import de.leonhard.storage.internal.settings.ConfigSettings;
import de.leonhard.storage.internal.settings.DataType;
import de.leonhard.storage.internal.settings.ReloadSettings;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;


public final class Main extends JavaPlugin {

    //public static FileConfiguration mainConfig;
    //public static FileConfiguration messages;
    //public static FileConfiguration permissions;
    public static Yaml permissions;
    public static Yaml messages;
    public static Yaml mainConfig;
    public static Yaml commands;
    public static TpaHandler tpaHandler;
    public static TeleportHandler teleportHandler;
    private static Main instance;

    // Handle Instance of plugin in multiple classes.
    public static Main getInstance() {
        return instance;
    }

    public void onEnable() {
        instance = this;
        setupStorage(); // Loads or Copy Default Configuration
        // loadshit();
        Bukkit.getConsoleSender().sendMessage(" ");
        Bukkit.getConsoleSender().sendMessage(" XIAVIC CORE IS ACTIVATED... ");
        Bukkit.getConsoleSender().sendMessage(" ");
        Bukkit.getConsoleSender().sendMessage("Xiavic Network's Amazing Core");
        Bukkit.getConsoleSender().sendMessage("     In Development by the Xiavic Dev Team ");
        Bukkit.getConsoleSender().sendMessage(" ");
        registerShit();
        registerListeners();
        registerCommands();
        Bukkit.getScheduler().runTaskTimer(this, tpaHandler, 0, 20);
    }

    public void onDisable() {

    }

    // TODO: Check if Object is not Null so we can catch exceptions!
    private void registerCommands() {
        //getCommand("fireball").setExecutor(new FireBallCommand());
        getCommand("argh").setExecutor(new ArghCommand());
        getCommand("afk").setExecutor(new AFKCommand());
        getCommand("back").setExecutor(new BackCommand());
        getCommand("cartography").setExecutor(new CartographyCommand());
        getCommand("cheatarmor").setExecutor(new CheatArmor());
        getCommand("cheatexp").setExecutor(new CheatEXP());
        getCommand("clear").setExecutor(new ClearCommand());
        getCommand("clearall").setExecutor(new ClearAllCommand());
        //getCommand("coinflip").setExecutor(new CoinFlipCommand());
        getCommand("coreconfigupdate").setExecutor(new ConfigReloadCommand());
        getCommand("coreversion").setExecutor(new CoreVersionCommand());
        getCommand("discord").setExecutor(new DiscordCommand());
        getCommand("dispose").setExecutor(new DisposeCommand());
        getCommand("ec").setExecutor(new EnderChestCommand());
        getCommand("ext").setExecutor(new ExtinguishCommand());
        getCommand("feed").setExecutor(new FeedCommand());
        getCommand("fly").setExecutor(new FlyCommand());
        getCommand("flyspeed").setExecutor(new FlySpeedCommand());
        getCommand("forums").setExecutor(new ForumsCommand());
        getCommand("gamemode").setExecutor(new GamemodeCommand());
        getCommand("god").setExecutor(new GodCommand());
        getCommand("grindstone").setExecutor(new GrindstoneCommand());
        getCommand("heal").setExecutor(new HealCommand());
        getCommand("head").setExecutor(new HeadCommand());
        getCommand("info").setExecutor(new ItemInfoCommand());
        getCommand("loom").setExecutor(new LoomCommand());
        getCommand("more").setExecutor(new MoreCommand());
        getCommand("near").setExecutor(new NearCommand());
        getCommand("pony").setExecutor(new PonyCommand());
        getCommand("rtp").setExecutor(new RandomTPCommand());
        getCommand("repair").setExecutor(new RepairCommand());
        getCommand("setfirstspawn").setExecutor(new FirstSpawnSetCommand());
        getCommand("setspawn").setExecutor(new SpawnSetCommand());
        getCommand("spawn").setExecutor(new SpawnCommand());
        getCommand("stonecutter").setExecutor(new StonecutterCommand());
        getCommand("sudo").setExecutor(new SudoCommand());
        getCommand("suicide").setExecutor(new SuicideCommand());
        getCommand("top").setExecutor(new TopCommand());
        getCommand("tp").setExecutor(new TeleportCommand());
        getCommand("tpa").setExecutor(new TpaCommand());
        getCommand("tpaccept").setExecutor(new TpacceptCommand());
        getCommand("tpall").setExecutor(new TpallCommand());
        getCommand("tpdeny").setExecutor(new TpdenyCommand());
        getCommand("tphere").setExecutor(new TPhereCommand());
        getCommand("tppos").setExecutor(new TPPosCommand());
        getCommand("twitter").setExecutor(new TwitterCommand());
        getCommand("walkspeed").setExecutor(new WalkSpeedCommand());
        getCommand("website").setExecutor(new WebsiteCommand());
        getCommand("whois").setExecutor(new WhoIsCommand());
        getCommand("world").setExecutor(new WorldCommand());
        getCommand("workbench").setExecutor(new WorkbenchCommand());
        getCommand("youtube").setExecutor(new YoutubeCommand());
    }

    private void registerListeners() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new JoinQuit(), this);
        pm.registerEvents(new EquipEvents(), this);
        pm.registerEvents(new RespawnEvent(), this);
        pm.registerEvents(teleportHandler, this);
        AFKHandler.INSTANCE.registerTicker();
    }

    // Use this function for creating new shit
    private void registerShit() {
        tpaHandler = new TpaHandler();
        teleportHandler = new TeleportHandler();
    }

    // Handling of configuration file with Json, Yaml, and Toml
    private void setupStorage() {

        ////////////////
        // permissions.yml
        ////////////////
        permissions = LightningBuilder
                .fromFile(new File("plugins/XiavicCore/Resources/permissions"))
                .addInputStreamFromResource("permissions.yml")
                .setConfigSettings(ConfigSettings.PRESERVE_COMMENTS)
                .setReloadSettings(ReloadSettings.AUTOMATICALLY)
                .setDataType(DataType.SORTED)
                .createConfig();

        ////////////////
        // messages.yml
        ////////////////
        messages = LightningBuilder
                .fromFile(new File("plugins/XiavicCore/Resources/messages"))
                .addInputStreamFromResource("messages.yml")
                .setConfigSettings(ConfigSettings.PRESERVE_COMMENTS)
                .setReloadSettings(ReloadSettings.AUTOMATICALLY)
                .setDataType(DataType.SORTED)
                .createConfig();

        ////////////////
        // config.yml
        ////////////////
        mainConfig = LightningBuilder
                .fromFile(new File("plugins/XiavicCore/config"))
                .addInputStreamFromResource("config.yml")
                .setConfigSettings(ConfigSettings.PRESERVE_COMMENTS)
                .setReloadSettings(ReloadSettings.AUTOMATICALLY)
                .setDataType(DataType.SORTED)
                .createConfig();

        ////////////////
        // commands.yml
        ////////////////
        mainConfig = LightningBuilder
                .fromFile(new File("plugins/XiavicCore/commands"))
                .addInputStreamFromResource("commands.yml")
                .setConfigSettings(ConfigSettings.PRESERVE_COMMENTS)
                .setReloadSettings(ReloadSettings.AUTOMATICALLY)
                .setDataType(DataType.SORTED)
                .createConfig();

        ////////////////
        // players.json
        ////////////////
        Json players = new Json("players", Bukkit.getServer().getWorldContainer() + "/plugins/XiavicCore/Resources");
        players.set("players." + "NightPotato.Rank", "ThatNewGuy!");


    }

    private void loadshit() {
        saveResource("resources/permissions.yml", false);
        saveResource("resources/messages.yml", false);
        saveResource("config.yml", false);
        Permissions.setup();
        Messages.setup();
        //mainConfig = getConfig();
        //permissions = Permissions.get();
        //messages = Messages.get();
    }

    // I am using this function for updating the configs from the files inside the current
    // build of the plugin and preserves the spawn location in the mainConfig
    public void updateShit() {
        String firstspawnLocation = mainConfig.getString("FirstSpawn");
        String spawnLocation = mainConfig.getString("Spawn");
        saveResource("Resources/permissions.yml", true);
        saveResource("Resources/messages.yml", true);
        saveResource("config.yml", true);
        Permissions.setup();
        Messages.setup();
        //mainConfig = getConfig();
        //permissions = Permissions.get();
        // messages = Messages.get();
        mainConfig.set("FirstSpawn", firstspawnLocation);
        mainConfig.set("Spawn", spawnLocation);
        saveConfig();
    }

}
