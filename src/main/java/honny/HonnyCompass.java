package honny;

import fr.xephi.authme.api.v3.AuthMeApi;
import honny.commands.CommandsComplete;
import honny.commands.CommandsHandler;
import honny.controllers.PlayerCompass;
import honny.dependings.betonquest.CompassLocations;
import honny.handlers.PlayerQuitHandler;
import honny.tasks.CompassUpdater;
import honny.tasks.PlayerCompassLocationsUpdater;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public final class HonnyCompass extends JavaPlugin {

    @Getter private static HonnyCompass instance;
    @Getter private MainConfigManager mainConfig;
    @Getter private final CompassLocations compassLocations = new CompassLocations();

    @Getter private Optional<AuthMeApi> optionalAuthMeApi = Optional.empty();
    @Getter private Optional<Object> optionalBetonQuest = Optional.empty();

    private final Map<UUID, PlayerCompass> compasses = new HashMap<>();

    @Override
    public void onEnable() {
        instance = this;
        this.getLogger().info("§d_  _ ____ _  _ _  _ _   _ ____ ____ _  _ ___  ____ ____ ____ ");
        this.getLogger().info("§d|__| |  | |\\ | |\\ |  \\_/  |    |  | |\\/| |__] |__| [__  [__  ");
        this.getLogger().info("§d|  | |__| | \\| | \\|   |   |___ |__| |  | |    |  | ___] ___] ");
        this.getLogger().info("§d_  _ ____ _  _ _  _ _   _ ____ ____ _  _ ___  ____ ____ ____ ");
        this.getLogger().info("§d   Update 1.21.x by jpx1209 , betonquest 3.3 dev api new :                                                          ");
        this.getLogger().info("§d     _____             ____  _       _       _____                                                                      ");
        this.getLogger().info("§d      |_   _|_ _  ___   / ___|(_)_ __ | |__   |_   _|__  _                                                        ");
        this.getLogger().info("§d        | |/ _` |/ _ \\  \\___ \\| | '_ \\| '_ \\    | |/ _ \\| '_ \\                                                          ");
        this.getLogger().info("§d        | | (_| | (_) |  ___) | | | | | | | |   | | (_) | | | |                                                         ");
        this.getLogger().info("§d        |_|\\__,_|\\___/  |____/|_|_| |_|_| |_|   |_|\\___/|_| |_|                                                         ");
        this.getLogger().info("§d                                                             ");
        Objects.requireNonNull(this.getCommand("honnycompass")).setExecutor(new CommandsHandler());
        Objects.requireNonNull(this.getCommand("honnycompass")).setTabCompleter(new CommandsComplete());

        if (Bukkit.getPluginManager().getPlugin("AuthMe") != null) {
            optionalAuthMeApi = Optional.of(AuthMeApi.getInstance());
        }

        if (Bukkit.getPluginManager().getPlugin("BetonQuest") != null) {
            optionalBetonQuest = Optional.of(new Object());
            this.getLogger().info("§dBetonQuest plugin hooked natively.");
        }

        this.saveDefaultConfig();
        this.reloadMainConfig();

        getServer().getPluginManager().registerEvents(new PlayerQuitHandler(), this);

        CompassUpdater compassUpdater = new CompassUpdater();
        compassUpdater.runTaskTimer(this, 20, 1);

        PlayerCompassLocationsUpdater playerCompassLocationsUpdater = new PlayerCompassLocationsUpdater();
        playerCompassLocationsUpdater.runTaskTimerAsynchronously(this, 20, 20);
    }

    public void reloadMainConfig() {
        for (PlayerCompass value : this.compasses.values()) {
            value.deleteBossBar();
        }
        this.compasses.clear();
        this.reloadConfig();
        mainConfig = new MainConfigManager(this.getConfig());
        if (optionalBetonQuest.isPresent()) {
            compassLocations.reload();
        }
    }

    public PlayerCompass createCompass(Player player) {
        PlayerCompass playerCompass = new PlayerCompass(player);
        this.compasses.put(player.getUniqueId(), playerCompass);
        return playerCompass;
    }

    public Optional<PlayerCompass> getCompass(Player player) {
        if (!compasses.containsKey(player.getUniqueId())) return Optional.empty();
        return Optional.of(compasses.get(player.getUniqueId()));
    }

    public void deleteCompass(Player player) {
        compasses.remove(player.getUniqueId());
    }

    @Override
    public void onDisable() {
        for (PlayerCompass value : this.compasses.values()) {
            value.deleteBossBar();
        }
    }
}