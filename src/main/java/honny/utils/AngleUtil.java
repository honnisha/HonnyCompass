package honny.utils;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class AngleUtil {
    public static int computeAngle(Player player, Location targetLocation) {
        Location playerLoc = player.getLocation().clone();

        Vector target = targetLocation.toVector();
        playerLoc.setDirection(target.subtract(playerLoc.toVector()));
        float playerYaw = playerLoc.getYaw();
        return Math.round(playerYaw);
    }
}
