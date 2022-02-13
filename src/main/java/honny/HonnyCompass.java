package honny;

import fr.xephi.authme.api.v3.AuthMeApi;
import honny.commands.CommandsComplete;
import honny.commands.CommandsHandler;
import honny.controllers.PlayerCompass;
import honny.dependings.betonquest.CompassLocations;
import honny.handlers.PlayerQuitHandler;
import honny.tasks.CompassUpdater;
import lombok.Getter;
import org.betonquest.betonquest.BetonQuest;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public final class HonnyCompass extends JavaPlugin {

    @Getter private static HonnyCompass instance;
    @Getter private MainConfigManager mainConfig;
    @Getter private final CompassLocations compassLocations = new CompassLocations();

    @Getter private Optional<AuthMeApi> optionalAuthMeApi = Optional.empty();
    @Getter private Optional<BetonQuest> optionalBetonQuest = Optional.empty();

    private final Map<UUID, PlayerCompass> compasses = new HashMap<>();

    @Override
    public void onEnable() {
        instance = this;

        // Cybermedium
        this.getLogger().info("§d_  _ ____ _  _ _  _ _   _ ____ ____ _  _ ___  ____ ____ ____ ");
        this.getLogger().info("§d|__| |  | |\\ | |\\ |  \\_/  |    |  | |\\/| |__] |__| [__  [__  ");
        this.getLogger().info("§d|  | |__| | \\| | \\|   |   |___ |__| |  | |    |  | ___] ___] ");
        this.getLogger().info("§d                                                             ");

        Objects.requireNonNull(this.getCommand("honnycompass")).setExecutor(new CommandsHandler());
        Objects.requireNonNull(this.getCommand("honnycompass")).setTabCompleter(new CommandsComplete());

        if (Bukkit.getPluginManager().getPlugin("AuthMe") != null) {
            optionalAuthMeApi = Optional.of(AuthMeApi.getInstance());
            this.getLogger().info("§dAuthMe plugin hooked.");
        } else {
            this.getLogger().info("§dAuthMe not found.");
        }

        if (Bukkit.getPluginManager().getPlugin("BetonQuest") != null) {
            optionalBetonQuest = Optional.of(BetonQuest.getInstance());
            this.getLogger().info("§dBetonQuest plugin hooked.");
        } else {
            this.getLogger().info("§dBetonQuest not found.");
        }

        this.saveDefaultConfig();
        this.reloadMainConfig();

        getServer().getPluginManager().registerEvents(new PlayerQuitHandler(), this);

        CompassUpdater compassUpdater = new CompassUpdater();
        compassUpdater.runTaskTimer(this, 20, 1);

        this.getLogger().info("§dPlugin loaded");
    }

    public void reloadMainConfig() {
        for (PlayerCompass value : this.compasses.values()) {
            value.deleteBossBar();
        }
        this.compasses.clear();
        this.reloadConfig();

        mainConfig = new MainConfigManager(
                this.getConfig()
        );
        if (optionalBetonQuest.isPresent()) {
            compassLocations.reload();
        }
        this.getLogger().info("§dConfig loaded");
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
