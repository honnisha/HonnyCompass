package honny.controllers;

import honny.HonnyCompass;
import honny.MainConfigManager;
import honny.dependings.betonquest.CompassLocations;
import honny.utils.AngleUtil;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PlayerCompass {
    private final BossBar bossBarCompass;
    private final BossBar bossBarMessage;
    @Setter private Location targetLocation;

    public PlayerCompass(Player player) {
        MainConfigManager mainConfig = HonnyCompass.getInstance().getMainConfig();
        bossBarCompass = Bukkit.createBossBar("", mainConfig.getBarColor(), mainConfig.getBarStyle());
        bossBarCompass.addPlayer(player);

        bossBarMessage = Bukkit.createBossBar("", mainConfig.getBarColor(), mainConfig.getBarStyle());
        bossBarMessage.addPlayer(player);
        bossBarMessage.setVisible(false);

        this.update(player);
    }

    public void deleteBossBar() {
        bossBarCompass.removeAll();
        bossBarMessage.removeAll();
    }

    List<String> compassList;
    List<CompassLocations.CompassLocation> compassLocations = null;

    public void updateCompassLocations(Player player) {
        this.compassLocations = HonnyCompass.getInstance().getCompassLocations().getLocations(player);
    }

    public @NotNull List<CompassLocations.CompassLocation> getCompassLocations(Player player) {
        if (compassLocations == null) updateCompassLocations(player);
        return this.compassLocations;
    }

    public void update(Player player) {
        HonnyCompass instance = HonnyCompass.getInstance();
        MainConfigManager mainConfig = instance.getMainConfig();

        // All compass is 20 + 40 + 20 sections length
        int yawPerSection = 9; // 360 / 40 = 9

        // integer division; 2 / 9 = 0
        double yaw = player.getLocation().getYaw();
        if (yaw < 0) yaw += 360;
        int currentYaw = ((int) yaw / yawPerSection) + 20;

        this.compassList = new ArrayList<>(mainConfig.getOriginCompass());
        String targetName = null;
        boolean targetSelected = false;
        double distance = 0;

        if (instance.getOptionalBetonQuest().isPresent()) {
            for (CompassLocations.CompassLocation compassLocation : getCompassLocations(player)) {

                if (player.getLocation().getWorld() != null) {
                    if (!player.getLocation().getWorld().equals(compassLocation.location.getWorld())) continue;

                    int pointYaw = AngleUtil.computeAngle(player, compassLocation.location) / yawPerSection + 20;

                    boolean selected = this.targetLocation != null && this.targetLocation.equals(compassLocation.location);

                    if (pointYaw == currentYaw) {
                        targetName = compassLocation.name;
                        targetSelected = selected;
                        distance = player.getLocation().distance(compassLocation.location);
                    }

                    String point;
                    if (selected) {
                        // below
                        if (player.getLocation().getY() - compassLocation.location.getY() > mainConfig.getYDifferenceIcons()) {
                            point = pointYaw == currentYaw ? mainConfig.getSelectedCompassTargetSelectedBelow() : mainConfig.getSelectedCompassTargetBelow();
                        }
                        // above
                        else if (compassLocation.location.getY() - player.getLocation().getY() > mainConfig.getYDifferenceIcons()) {
                            point = pointYaw == currentYaw ? mainConfig.getSelectedCompassTargetSelectedAbove() : mainConfig.getSelectedCompassTargetAbove();
                        }
                        // at player level
                        else {
                            point = pointYaw == currentYaw ? mainConfig.getSelectedCompassTargetSelected() : mainConfig.getSelectedCompassTarget();
                        }
                    }
                    else {
                        // below
                        if (player.getLocation().getY() - compassLocation.location.getY() > mainConfig.getYDifferenceIcons()) {
                            point = pointYaw == currentYaw ? mainConfig.getCompassTargetSelectedBelow() : mainConfig.getCompassTargetBelow();
                        }
                        // above
                        else if (compassLocation.location.getY() - player.getLocation().getY() > mainConfig.getYDifferenceIcons()) {
                            point = pointYaw == currentYaw ? mainConfig.getCompassTargetSelectedAbove() : mainConfig.getCompassTargetAbove();
                        }
                        // at player level
                        else {
                            point = pointYaw == currentYaw ? mainConfig.getCompassTargetSelected() : mainConfig.getCompassTarget();
                        }
                    }

                    this.compassList.set(pointYaw, point);
                    if (pointYaw > 40)
                        this.compassList.set(pointYaw - 40, point);
                    if (pointYaw < 40)
                        this.compassList.set(pointYaw + 40, point);
                }
            }
        }

        this.compassList = this.compassList.subList(currentYaw - 10, currentYaw + 11);

        String currentItem = this.compassList.get(10);
        if (mainConfig.getReplacers().containsKey(currentItem)) this.compassList.set(10, mainConfig.getReplacers().get(currentItem));

        String compass = mainConfig.getSymbolStart() + StringUtils.join(this.compassList, "") + mainConfig.getSymbolEnd();
        bossBarCompass.setTitle(compass);

        if (targetName != null) {
            if (targetSelected)
                bossBarMessage.setTitle(mainConfig.getTitleMessageSelected().replace(
                        "{name}", targetName
                ).replace(
                        "{distance}", Double.toString(Math.round(distance))
                ));
            else
                bossBarMessage.setTitle(mainConfig.getTitleMessage().replace(
                        "{name}", targetName
                ).replace(
                        "{distance}", Double.toString(Math.round(distance))
                ));
            bossBarMessage.setVisible(true);
        } else {
            bossBarMessage.setVisible(false);
        }
    }
}
