package honny.handlers;

import honny.HonnyCompass;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitHandler implements Listener {
    @EventHandler
    public void PlayerQuit(PlayerQuitEvent event) {
        HonnyCompass.getInstance().deleteCompass(event.getPlayer());
    }
}
