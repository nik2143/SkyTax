package it.nik2143.skytax;

import de.leonhard.storage.Yaml;
import it.nik2143.skytax.commands.SkyTaxCommand;
import it.nik2143.skytax.hooks.ASkyBlock;
import it.nik2143.skytax.hooks.IslandsMethods;
import it.nik2143.skytax.hooks.SuperiorSkyblock2;
import it.nik2143.skytax.listeners.PlayerJoinListener;
import it.nik2143.skytax.placeholders.ClipPlaceholderAPI;
import it.nik2143.skytax.tasks.PlayersCheckTask;
import it.nik2143.skytax.utils.FilesUpdater;
import it.nik2143.skytax.utils.UpdateChecker;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Getter
public class SkyTax extends JavaPlugin {

    @Getter private static SkyTax skyTax;
    private boolean outdated;
    private IslandsMethods islandsMethods;
    private Economy econ = null;
    private Permission perms = null;
    private Yaml configuration;
    private Yaml language;
    private String newVersion;
    private HashMap<String,TaxUser> users = new HashMap<>();
    private DataManager dataManager;

    @Override
    public void onEnable() {
        skyTax = this;
        try {
            configuration = new Yaml("config", this.getDataFolder().getAbsolutePath(),this.getResource("config.yml"));
            language = new Yaml("language", this.getDataFolder().getAbsolutePath(),this.getResource("language.yml"));
            new FilesUpdater(this,configuration.getFile(),configuration.getName()).checkUpdate(configuration.getInt("FileVersion"),3);
            new FilesUpdater(this,language.getFile(),language.getName()).checkUpdate(language.getInt("FileVersion"),2);
            dataManager = new DataManager();
        } catch (Exception e){
            e.printStackTrace();
            Bukkit.getServer().getConsoleSender().sendMessage(Utils.color( "&c&lERROR: &f[SkyTax] An error occurred while loading configuration files, a backup of data file will be created. The plugin will be disabled"));
            try {
                Files.copy(new File(getDataFolder()+"/data.json").toPath(),new File(getDataFolder()+"/data-broken-"+System.currentTimeMillis()+".json").toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException ioException) {
                Bukkit.getServer().getConsoleSender().sendMessage(Utils.color( "&c&lERROR: &f[SkyTax] An error occured while backup of data"));
                dataManager.setShouldsave(false);
            }
            Bukkit.getServer().getPluginManager().disablePlugin(this);
            return;
        }
        CompletableFuture<List<TaxUser>> cf = dataManager.loadData();
        cf.thenAccept((List<TaxUser> userslist)->{
            users = new HashMap<>();
            userslist.forEach(user-> users.put(user.uuid,user));
        });
        if (Bukkit.getPluginManager().isPluginEnabled("ASkyBlock")) islandsMethods = new ASkyBlock();
        else if (Bukkit.getPluginManager().isPluginEnabled("SuperiorSkyblock2")) islandsMethods = new SuperiorSkyblock2();
        if (islandsMethods == null){
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&lERROR: &f[SkyTax] No supported Skyblock plugin found, install ASkyBlock or Superior Skyblock 2. The plugin will be disabled"));
            Bukkit.getServer().getPluginManager().disablePlugin(this);
            return;
        }
        getLogger().info("Version " + getDescription().getVersion()  + " - Using "+islandsMethods.getClass().getSimpleName());
        SkyTaxCommand skyTaxCommand = new SkyTaxCommand();
        Objects.requireNonNull(this.getCommand("skytax")).setExecutor(skyTaxCommand);
        Objects.requireNonNull(this.getCommand("skytax")).setTabCompleter(skyTaxCommand);
        registerEvents(islandsMethods);
        if (!setupEconomy()) {
            Bukkit.getServer().getConsoleSender().sendMessage(Utils.color("&c&lERROR: &f[SkyTax] Economy Manager Plugin not found, but is required to use this plugin so plugin will be disabled "));
            Bukkit.getServer().getPluginManager().disablePlugin(this);
            return;
        }
        if (!setupPermissions()) {
            Bukkit.getServer().getConsoleSender().sendMessage(Utils.color("&c&lERROR: &f[SkyTax] Permissions Manager Plugin not found, but is required to use this plugin so plugin will be disabled"));
            Bukkit.getServer().getPluginManager().disablePlugin(this);
            return;
        }
        registerPlaceholders();
        new Metrics(this, 9777);
        new PlayersCheckTask().runTaskTimerAsynchronously(this,1L, configuration.getLong("TimeToCheck") * 20L);
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> getDataManager().saveData(),configuration.getLong("save-data-time") * 20L,configuration.getLong("save-data-time") * 20L);
        checkUpdate();
    }

    @Override
    public void onDisable() {
        dataManager.saveData();
        dataManager.close();
        configuration.getInputStream().ifPresent(inputStream -> {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        language.getInputStream().ifPresent(inputStream -> {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        getLogger().info("Version - " + getDescription().getVersion() + " Disabled");
    }


    private void checkUpdate(){
        UpdateChecker.init(this,81159).requestUpdateCheck().whenComplete(((result, e) -> {
            outdated = result.requiresUpdate();
            newVersion = result.getNewestVersion();
            if (outdated){
                getLogger().warning("[SkyTax] New update available (" + newVersion + ")");
            }
        }));
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return true;
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        if (rsp == null) {
            return false;
        }
        perms = rsp.getProvider();
        return true;
    }

    private void registerEvents(IslandsMethods islandsMethods) {
        Bukkit.getServer().getPluginManager().registerEvents(new JoinEvent(islandsMethods),this);
        if (Bukkit.getServer().getPluginManager().getPlugin("ASkyBlock")!=null) {
            Bukkit.getServer().getPluginManager().registerEvents(new IslandTpEventASky(), this);
        } else if (Bukkit.getServer().getPluginManager().getPlugin("SuperiorSkyblock2")!=null){
            Bukkit.getServer().getPluginManager().registerEvents(new IslandTpEventSuperior(), this);
        }
        Bukkit.getServer().getPluginManager().registerEvents(new WorldSave(),this);
    }

    private void registerPlaceholders(){
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")){
            new ClipPlaceholderAPI().register();
        }
    }

}

