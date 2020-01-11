package YourPluginName.Main;

import InviteRewards.Main.InviteRewards;
import YourPluginName.Commands.ExampleCommand;
import YourPluginName.Listeners.LogInListener;
import YourPluginName.Rewards.RewardConfig;
import YourPluginName.Storage.DatabaseFunctions;
import YourPluginName.Storage.SQLPool;
import YourPluginName.Triggers.TimeRewardTrigger;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Main extends JavaPlugin {

    private static JavaPlugin plugin;
    private static VertXLogger logger;
    private static Economy economy;
    private static Permission perms;
    private static Chat chat;
    private static ChatManager chatManager;
    private static DatabaseFunctions dataHandler;
    private static boolean dataReady;

    public static JavaPlugin getPlugin() {
        return plugin;
    }

    public static VertXLogger getLog() {
        return logger;
    }

    public static ChatManager getChatManager() { return chatManager; }

    public static DatabaseFunctions getDataHandler() throws Exception {
        if (dataReady)
            return dataHandler;
        throw new Exception("Data is not loaded yet");
    }

    @Override
    public void onEnable() {
        plugin = this;
        dataReady = false;
        logger = new VertXLogger("VertXExamplePlugin");
        chatManager = new ChatManager();
        ConfigurationSerialization.registerClass(RewardConfig.class, "RewardConfig");

        createConfig();

        try {
            dataHandler = new DatabaseFunctions();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Database could not be activated");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        dataHandler.loadData().thenAccept((success)-> {
            if (success) {
                dataReady = true;
                Main.getLog().log("Successfully loaded data");
            } else {
                getServer().getPluginManager().disablePlugin(this);
            }
        });

        InviteRewards.setPlugin(this);
        TimeRewardTrigger trigger = new TimeRewardTrigger();

        //register listeners
        getServer().getPluginManager().registerEvents(new LogInListener(), this);

        //register commands
        getCommand("example-command").setExecutor(new ExampleCommand("example-command" , ""));

        setupPermissions();
        setupChat();
        getChat();
    }

    private void createConfig() {

        ConfigurationSerialization.registerClass(RewardConfig.class);

        try {
            if (!getDataFolder().exists()) {
                getDataFolder().mkdirs();
            }
            File file = new File(getDataFolder(), "config.yml");
            if (!file.exists()) {
                getLogger().info("config.yml not found, creating!");
                saveDefaultConfig();
            } else {
                getLogger().info("config.yml found, loading!");

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private boolean setupChat() {
        RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
        chat = rsp.getProvider();
        return chat != null;
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }

    public static Permission getPermissions() {
        return perms;
    }

    public static Chat getChat() {
        return chat;
    }

    @Override
    public void onDisable() {
        SQLPool.close();
    }
}
