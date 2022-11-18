package honny.dependings.betonquest;

import honny.HonnyCompass;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.database.PlayerData;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompassLocations {
    public static class CompassLocation {
        public final Location location;
        public final String name;

        public CompassLocation(Location location, String name) {
            this.location = location;
            this.name = name;
        }
    }

    private final Map<String, CompassLocation> locations = new HashMap<>();

    public void reload() {
        locations.clear();

        for (final QuestPackage pack : Config.getPackages().values()) {
            final String packName = pack.getQuestPath();
            // loop all compass locations
            final ConfigurationSection section = pack.getConfig().getConfigurationSection("compass");
            if (section != null) {
                for (final String key : section.getKeys(false)) {
                    final String location = pack.getString("compass." + key + ".location");
                    String name;
                    if (section.isConfigurationSection(key + ".name")) {
                        name = pack.getString("compass." + key + ".name." + Config.getLanguage());
                        if (name == null) {
                            name = pack.getString("compass." + key + ".name.en");
                        }
                    } else {
                        name = pack.getString("compass." + key + ".name");
                    }
                    if (name == null) {
                        HonnyCompass.getInstance().getLogger().warning("Name not defined in a compass pointer in " + packName + " package: " + key);
                        continue;
                    }
                    if (location == null) {
                        HonnyCompass.getInstance().getLogger().warning("Location not defined in a compass pointer in " + packName + " package: " + key);
                        continue;
                    }
                    // if the tag is present, continue
                    final String[] parts = location.split(";");
                    if (parts.length != 4) {
                        HonnyCompass.getInstance().getLogger().warning("Could not parse location in a compass pointer in " + packName + " package: " + key);
                        continue;
                    }
                    final World world = Bukkit.getWorld(parts[3]);
                    if (world == null) {
                        HonnyCompass.getInstance().getLogger().warning("World does not exist in a compass pointer in " + packName + " package: " + key);
                    }
                    final int locX;
                    final int locY;
                    final int locZ;
                    try {
                        locX = Integer.parseInt(parts[0]);
                        locY = Integer.parseInt(parts[1]);
                        locZ = Integer.parseInt(parts[2]);
                    } catch (final NumberFormatException e) {
                        HonnyCompass.getInstance().getLogger().warning("Could not parse location coordinates in a compass pointer in " + packName + " package: " + key);
                        return;
                    }
                    final Location loc = new Location(world, locX, locY, locZ);
                    String bqKey = packName + ".compass-" + key;
                    this.locations.put(bqKey, new CompassLocation(loc, name));
                }
            }
        }

        HonnyCompass.getInstance().getLogger().info("§dBetonQuest compass locations loaded: " + this.locations.size());
    }

    public List<CompassLocation> getLocations(Player player) {
        List<CompassLocation> compassLocations = new ArrayList<>();
        BetonQuest betonQuest = HonnyCompass.getInstance().getOptionalBetonQuest().get();

        Profile profile = PlayerConverter.getID(player);
        PlayerData playerData = betonQuest.getPlayerData(profile);

        for (Map.Entry<String, CompassLocation> entry : this.locations.entrySet()) {
            if (playerData.hasTag(entry.getKey())) {
                compassLocations.add(entry.getValue());
            }
        }
        return compassLocations;
    }
}
