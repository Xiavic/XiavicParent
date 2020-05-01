package Commands.UserCmds.Essential;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import static Main.mainClass.permissions;

public class WorkbenchCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] strings) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.hasPermission(permissions.getString("Workbench"))) {
                Inventory i = Bukkit.createInventory(player, InventoryType.WORKBENCH);
                player.openInventory(i);
                return true;
            }
        }
        return false;
    }
}
