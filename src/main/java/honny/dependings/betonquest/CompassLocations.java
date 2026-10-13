package honny.dependings.betonquest;

import honny.HonnyCompass;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
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
        File bqFolder = new File(HonnyCompass.getInstance().getDataFolder().getParentFile(), "BetonQuest");
        if (!bqFolder.exists()) return;

        List<File> ymlFiles = new ArrayList<>();
        findYmlFiles(bqFolder, ymlFiles);

        for (File file : ymlFiles) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            ConfigurationSection section = config.getConfigurationSection("compass");
            if (section != null) {
                String packName = file.getParentFile().getName();
                if (packName.equals("BetonQuest")) packName = "default";

                for (final String key : section.getKeys(false)) {
                    final String locationStr = config.getString("compass." + key + ".location");
                    String name = config.getString("compass." + key + ".name.en");
                    if (name == null) name = config.getString("compass." + key + ".name");
                    if (config.isConfigurationSection("compass." + key + ".name")) {
                        for (String lang : config.getConfigurationSection("compass." + key + ".name").getKeys(false)) {
                            name = config.getString("compass." + key + ".name." + lang);
                            break;
                        }
                    }

                    if (name == null || locationStr == null) continue;

                    final String[] parts = locationStr.split(";");
                    if (parts.length != 4) continue;

                    final World world = Bukkit.getWorld(parts[3]);
                    if (world == null) continue;

                    try {
                        final int locX = Integer.parseInt(parts[0]);
                        final int locY = Integer.parseInt(parts[1]);
                        final int locZ = Integer.parseInt(parts[2]);
                        final Location loc = new Location(world, locX, locY, locZ);

                        String bqKey = packName + ";" + key;
                        this.locations.put(bqKey, new CompassLocation(loc, name));
                    } catch (final NumberFormatException ignored) {}
                }
            }
        }
    }

    private void findYmlFiles(File dir, List<File> list) {
        File[] files = dir.listFiles();
        if (files == null) return;
        for (File f : files) {
            if (f.isDirectory()) {
                findYmlFiles(f, list);
            } else if (f.getName().endsWith(".yml")) {
                list.add(f);
            }
        }
    }

    public List<CompassLocation> getLocations(Player player) {
        List<CompassLocation> compassLocations = new ArrayList<>();

        Location vanillaTarget = player.getCompassTarget();

        if (vanillaTarget != null) {
            for (CompassLocation cl : this.locations.values()) {
                if (cl.location.getWorld() != null && cl.location.getWorld().equals(vanillaTarget.getWorld())) {
                    if (cl.location.distance(vanillaTarget) < 3.0) {
                        compassLocations.add(cl);
                    }
                }
            }
        }
        return compassLocations;
    }
}