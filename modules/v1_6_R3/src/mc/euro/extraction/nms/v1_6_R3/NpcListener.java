package mc.euro.extraction.nms.v1_6_R3;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Nikolai
 */
public class NpcListener implements Listener {
    
    JavaPlugin plugin;
    
    public NpcListener() {
        this.plugin = (JavaPlugin) Bukkit.getServer().getPluginManager().getPlugin("HostageArena");
    }
    
    @EventHandler (priority=EventPriority.HIGHEST)
    public void onSpawn(CreatureSpawnEvent e) {
        if (e.getEntity().getType() != EntityType.VILLAGER) return;
        
        Villager v = (Villager) e.getEntity();
        v.setCustomName("Hostage");
        
    }
    
    @EventHandler (priority=EventPriority.HIGHEST)
    public void onHostageInteract(PlayerInteractEntityEvent e) {
        if (e.getRightClicked().getType() != EntityType.VILLAGER) return;
        
        Entity E = e.getRightClicked();
        Hostage h = (Hostage) ((CraftEntity)E).getHandle();
        
        Player p = (Player) e.getPlayer();
        
        if (h.isFollowing()) {
            h.stay();
        } else if (h.isStopped()) {
            h.follow(p.getName());
        }
    }
    
    @EventHandler (priority=EventPriority.HIGHEST)
    public void onInvOpen(InventoryOpenEvent e) {
        if (e.getInventory().getType() != InventoryType.MERCHANT) return;
        e.setCancelled(true);
    }
    
}
