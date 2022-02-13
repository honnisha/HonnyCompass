package honny.commands;

import honny.HonnyCompass;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CommandsHandler implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            if (sender instanceof Player && !(sender.hasPermission("honnycompass.reload") || sender.isOp())) {
                sender.sendMessage("§cYou don't have permissions.");
                return true;
            }
            HonnyCompass.getInstance().reloadMainConfig();
            sender.sendMessage("§e[HonnyCompass] Plugin reloaded");
            return true;
        }
        return true;
    }
}
