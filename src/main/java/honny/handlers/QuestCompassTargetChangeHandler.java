package honny.handlers;

import honny.HonnyCompass;
import honny.controllers.PlayerCompass;
import org.betonquest.betonquest.api.QuestCompassTargetChangeEvent;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.database.PlayerData;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Optional;

public class QuestCompassTargetChangeHandler implements Listener {
    @EventHandler
    public void QuestCompassTargetChangeEvent(QuestCompassTargetChangeEvent event) {
        HonnyCompass honnyCompass = HonnyCompass.getInstance();

        Optional<PlayerCompass> optionalPlayerCompass = honnyCompass.getCompass(event.getProfile());
        optionalPlayerCompass.ifPresent(playerCompass -> playerCompass.setTargetLocation(event.getLocation()));
    }
}
