package honny;

import lombok.Getter;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MainConfigManager {
    @Getter private final String north;
    @Getter private final String east;
    @Getter private final String south;
    @Getter private final String west;

    @Getter private final String fill;
    @Getter private final String fillCenter;

    @Getter private final String select;

    @Getter private final String compassTarget;
    @Getter private final String compassTargetSelected;
    @Getter private final String compassTargetAbove;
    @Getter private final String compassTargetSelectedAbove;
    @Getter private final String compassTargetBelow;
    @Getter private final String compassTargetSelectedBelow;

    @Getter private final String selectedCompassTarget;
    @Getter private final String selectedCompassTargetSelected;
    @Getter private final String selectedCompassTargetAbove;
    @Getter private final String selectedCompassTargetSelectedAbove;
    @Getter private final String selectedCompassTargetBelow;
    @Getter private final String selectedCompassTargetSelectedBelow;

    @Getter private final String symbolStart;
    @Getter private final String symbolEnd;

    @Getter private final String titlePrefix;

    @Getter private final BarColor barColor;
    @Getter private final BarStyle barStyle;

    @Getter private final int ticksUpdateCompass;
    @Getter private final double yDifferenceIcons;

    @Getter private final List<String> originCompass;

    public MainConfigManager(FileConfiguration config) {
        south = getString(config, "south", "&e&lS");
        west = getString(config, "west", "&e&lW");
        north = getString(config, "north", "&e&lN");
        east = getString(config, "east", "&e&lE");

        fill = getString(config, "fill", "&7═");
        fillCenter = getString(config, "fill-center", "&7╪");

        select = getString(config, "select", "&7╪");

        compassTarget = getString(config, "default.compass-target", "&2□");
        compassTargetSelected = getString(config, "default.compass-target-selected", "&a■");

        compassTargetAbove = getString(config, "default.compass-target-above", "&2△");
        compassTargetSelectedAbove = getString(config, "default.compass-target-selected-above", "&a▲");

        compassTargetBelow = getString(config, "default.compass-target-below", "&2▽");
        compassTargetSelectedBelow = getString(config, "default.compass-target-selected-below", "&a▼");

        selectedCompassTarget = getString(config, "selected.compass-target", "&2□");
        selectedCompassTargetSelected = getString(config, "selected.compass-target-selected", "&a■");

        selectedCompassTargetAbove = getString(config, "selected.compass-target-above", "&2△");
        selectedCompassTargetSelectedAbove = getString(config, "selected.compass-target-selected-above", "&a▲");

        selectedCompassTargetBelow = getString(config, "selected.compass-target-below", "&2▽");
        selectedCompassTargetSelectedBelow = getString(config, "selected.compass-target-selected-below", "&a▼");

        symbolStart = getString(config, "symbol-start", "&e&l╠");
        symbolEnd = getString(config, "symbol-end", "&e&l╠");

        titlePrefix = getString(config, "title-prefix", "&6");

        barColor = BarColor.valueOf(getString(config, "bar-color", "white").toUpperCase(Locale.ROOT));
        barStyle = BarStyle.valueOf(getString(config, "bar-style", "solid").toUpperCase(Locale.ROOT));

        ticksUpdateCompass = config.getInt("ticks-update-compass", 20);
        yDifferenceIcons = config.getDouble("y-difference-icons", 10);

        originCompass = formatOriginCompass();
    }

    private List<String> formatOriginCompass() {
        List<String> allSection = new ArrayList<>();

        // Length 9
        List<String> fillSection = Arrays.asList(this.fill, this.fill, this.fill, this.fill, this.fillCenter, this.fill, this.fill, this.fill, this.fill);

        // offset 20 slots
        allSection.add(this.north);
        allSection.addAll(fillSection);
        allSection.add(this.east);
        allSection.addAll(fillSection);

        // starts 0
        allSection.add(this.south);
        allSection.addAll(fillSection);
        allSection.add(this.west);
        allSection.addAll(fillSection);
        allSection.add(this.north);
        allSection.addAll(fillSection);
        allSection.add(this.east);
        allSection.addAll(fillSection);

        // 20 slots after
        allSection.add(this.south);
        allSection.addAll(fillSection);
        allSection.add(this.west);
        allSection.addAll(fillSection);

        return allSection;
    }

    private static String getString(FileConfiguration config, String path, String defaultValue) {
        String value = config.getString(path);
        if (value == null) return defaultValue;
        return value.replace("&", "§");
    }

    private static String getString(FileConfiguration config, String path) {
        String value = config.getString(path);
        if (value == null) {
            HonnyCompass.getInstance().getLogger().warning("§4Config string \"" + path + "\" not found");
            return null;
        }
        return value.replace("&", "§");
    }
}
