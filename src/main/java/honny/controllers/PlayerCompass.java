package honny.controllers;

import honny.HonnyCompass;
import honny.MainConfigManager;
import honny.dependings.betonquest.CompassLocations;
import honny.utils.AngleUtil;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;

public class PlayerCompass {
    private final BossBar bossBarCompass;
    private final BossBar bossBarMessage;

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

    public void update(Player player) {
        HonnyCompass instance = HonnyCompass.getInstance();
        MainConfigManager mainConfig = instance.getMainConfig();

        // All compass is 40 sections length
        int yawPerSection = 9; // 360 / 40 = 9

        // integer division; 2 / 9 = 0
        double yaw = player.getLocation().getYaw();
        if (yaw < 0) yaw += 360;
        int currentYaw = ((int) yaw / yawPerSection) + 20;

        this.compassList = new ArrayList<>(mainConfig.getOriginCompass());
        String targetName = null;

        if (instance.getOptionalBetonQuest().isPresent()) {
            for (CompassLocations.CompassLocation compassLocation : HonnyCompass.getInstance().getCompassLocations().getLocations(player)) {

                if (player.getLocation().getWorld() != null) {
                    if (!player.getLocation().getWorld().equals(compassLocation.location.getWorld())) continue;

                    int pointYaw = AngleUtil.computeAngle(player, compassLocation.location) / yawPerSection + 20;

                    if (pointYaw == currentYaw) {
                        targetName = compassLocation.name;
                    }
                    // below
                    if (player.getLocation().getY() - compassLocation.location.getY() > mainConfig.getYDifferenceIcons()) {
                        if (pointYaw == currentYaw) {
                            this.compassList.set(pointYaw, mainConfig.getCompassTargetSelectedBelow());
                        } else {
                            this.compassList.set(pointYaw, mainConfig.getCompassTargetBelow());
                        }
                    }
                    // above
                    else if (compassLocation.location.getY() - player.getLocation().getY() > mainConfig.getYDifferenceIcons()) {
                        if (pointYaw == currentYaw) {
                            this.compassList.set(pointYaw, mainConfig.getCompassTargetSelectedAbove());
                        } else {
                            this.compassList.set(pointYaw, mainConfig.getCompassTargetAbove());
                        }
                    }
                    // at player level
                    else {
                        if (pointYaw == currentYaw) {
                            this.compassList.set(pointYaw, mainConfig.getCompassTargetSelected());
                        } else {
                            this.compassList.set(pointYaw, mainConfig.getCompassTarget());
                        }
                    }
                }
            }
        }

        this.compassList = this.compassList.subList(currentYaw - 10, currentYaw + 11);

        String compass = mainConfig.getSymbolStart() + StringUtils.join(this.compassList, "") + mainConfig.getSymbolEnd();
        bossBarCompass.setTitle(compass);

        if (targetName != null) {
            bossBarMessage.setTitle(mainConfig.getTitlePrefix() + targetName);
            bossBarMessage.setVisible(true);
        } else {
            bossBarMessage.setVisible(false);
        }
    }
}
