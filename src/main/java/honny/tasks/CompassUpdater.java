package honny.tasks;

import honny.HonnyCompass;
import honny.MainConfigManager;
import honny.controllers.PlayerCompass;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Optional;

public class CompassUpdater extends BukkitRunnable {
    int count = 0;

    @Override
    public void run() {
        HonnyCompass honnyCompass = HonnyCompass.getInstance();
        MainConfigManager mainConfig = honnyCompass.getMainConfig();

        count++;
        if (count < mainConfig.getTicksUpdateCompass()) return;
        count = 0;

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (honnyCompass.getOptionalAuthMeApi().isPresent()) {
                if (!honnyCompass.getOptionalAuthMeApi().get().isAuthenticated(player)) continue;
            }

            Optional<PlayerCompass> optionalPlayerCompass = honnyCompass.getCompass(player);
            if (optionalPlayerCompass.isEmpty()) {
                honnyCompass.createCompass(player);
            } else {
                optionalPlayerCompass.get().update(player);
            }
        }
    }
}
