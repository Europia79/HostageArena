package mc.euro.extraction.nms.v1_7_R1;

import mc.euro.extraction.debug.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_7_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
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
    DebugInterface debug;
    
    public NpcListener() {
        this.plugin = (JavaPlugin) Bukkit.getServer().getPluginManager().getPlugin("HostageArena");
        if (plugin.getConfig().getBoolean("Debug", true)) {
            debug = new DebugOn(plugin);
        } else {
            debug = new DebugOff(plugin);
        }
    }
    
    @EventHandler (priority=EventPriority.HIGHEST)
    public void onSpawn(CreatureSpawnEvent e) {
        if (e.getEntity().getType() != EntityType.VILLAGER) return;
        this.debug.log("CreatureSpawnEvent has detected a Villager spawn.");

            Villager v = (Villager) e.getEntity();
            CraftHostage hostage = new CraftHostage(((CraftWorld) v.getWorld()).getHandle());
            hostage.setLocation(v.getLocation().getX(), v.getLocation().getY(), v.getLocation().getZ(), 
                    v.getLocation().getYaw(), v.getLocation().getPitch());
            ((CraftWorld) v.getWorld()).getHandle().removeEntity(((CraftEntity) e.getEntity()).getHandle());
            // ((CraftWorld) v.getWorld()).getHandle().addEntity(hostage);
            
            hostage.setCustomName("Hostage");
    }
    
    @EventHandler (priority=EventPriority.HIGHEST)
    public void onInvOpen(InventoryOpenEvent e) {
        if (e.getInventory().getType() != InventoryType.MERCHANT) return;
        e.setCancelled(true);
    }
    
    /*
    @EventHandler (priority=EventPriority.HIGHEST)
    public void onHostageInteract(PlayerInteractEntityEvent e) {
        this.debug.sendMessage(e.getPlayer(), "onHostageInteract() has been called.");
        if (e.getRightClicked().getType() != EntityType.VILLAGER) return;
        
        Entity E = e.getRightClicked();
        Hostage h;
        try {
            h = (Hostage) ((CraftEntity)E).getHandle();
        } catch (ClassCastException ex) {
            // Caused by baby villager
            this.debug.log("onHostageInteract() ClassCastException: most likely "
                    + "caused by a baby villager or a Villager that is not a Hostage.");
            return;
        }
        
        
        Player p = (Player) e.getPlayer();
        
        if (h.isFollowing()) {
            this.debug.log("Hostage was following " + h.getOwnerName());
            this.debug.log("Hostage is now staying.");
            h.stay();
        } else if (h.isStopped()) {
            h.follow(p.getName());
            this.debug.log("Hostage was staying.");
            this.debug.log("Hostage is now following " + h.getOwnerName());
        }
    } */
    
    @EventHandler (priority=EventPriority.HIGH)
    public void onDamageEvent(EntityDamageByEntityEvent e) {
        if (e.getEntity().getType() != EntityType.VILLAGER) return;
        if (!(e.getDamager() instanceof Player)) return;
        Player player = (Player) e.getDamager();
        this.debug.sendMessage(player, "Hostage has been damaged");
        
        e.setDamage(7.7);

    }
    
    @EventHandler (priority=EventPriority.HIGH)
    public void onHostageDeath(EntityDeathEvent e) {
        if (e.getEntity().getType() != EntityType.VILLAGER) return;
        
        // document which team killed the hostage.
        // End the match if they killed 2 of 3 hostages
        Player killer = e.getEntity().getKiller();
        debug.sendMessage(killer, "You have killed a hostage.");
    }
    
}
