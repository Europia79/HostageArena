package mc.euro.extraction.commands;

import mc.euro.extraction.api.IHostagePlugin;
import mc.euro.extraction.nms.Hostage;
import mc.euro.extraction.nms.NPCFactory;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEntityEvent;

/**
 * This gives Hostage behavior (stay, follow, & HP) to individual NPCs outside of the arena. <br/><br/>
 * 
 * Used to spawn Hostages outside of arenas and test out their behavior. <br/>
 * 
 * @author Nikolai
 */
public class HostageListener implements Listener {
    
    IHostagePlugin plugin;
    Hostage hostage;
    int maxHP;
    
    public HostageListener(Hostage h, int hp) {
        this.plugin = (IHostagePlugin) Bukkit.getPluginManager().getPlugin("HostageArena");
        this.hostage = h;
        this.maxHP = hp;
    }
    
    @EventHandler (priority=EventPriority.HIGHEST)
    public void onInvOpen(InventoryOpenEvent e) {
        if (e.getInventory().getType() != InventoryType.MERCHANT) return;
        double distance = e.getPlayer().getLocation().distance(hostage.getLocation());
        if (distance <= 5) {
            e.setCancelled(true);
        }
    }
    
    @EventHandler (priority=EventPriority.HIGHEST)
    public void onHostageInteract(PlayerInteractEntityEvent e) {
        plugin.debug().log("onHostageInteract() has been called.");
        if (e.getRightClicked().getType() != EntityType.VILLAGER) return;
        
        Entity entity = e.getRightClicked();
        
        if (!entity.equals(hostage)) {
            plugin.debug().log("The clicked Villager does NOT equal HostageListeners VIP");
            return;
        };
        
        Player p = (Player) e.getPlayer();
        
        if (hostage.isFollowing()) {
            plugin.debug().log("Hostage was following " + hostage.getOwnerName());
            plugin.debug().log("Hostage is now staying.");
            hostage.stay();
        } else if (hostage.isStopped()) {
            hostage.follow(p.getName());
            plugin.debug().log("Hostage was staying.");
            plugin.debug().log("Hostage is now following " + hostage.getOwnerName());
        }
    }
    
    @EventHandler (priority=EventPriority.HIGH)
    public void onDamageEvent(EntityDamageByEntityEvent e) {
        if (e.getEntity().getType() != EntityType.VILLAGER) return;
        if (!e.getEntity().equals(hostage)) return;
        if (!(e.getDamager() instanceof Player)) return;
        Player player = (Player) e.getDamager();
        plugin.debug().sendMessage(player, "Hostage has been damaged");
        // dmg = (TotalHP + 0.01) / DesiredHP;
        Villager v = (Villager) e.getEntity();
        double dmg = (v.getMaxHealth() + 0.01) / this.maxHP;
        plugin.debug().log("Hostage took " + dmg + " damage");
        e.setDamage(dmg);

    }
    
    @EventHandler (priority=EventPriority.HIGHEST)
    public void onDamagedEvent(EntityDamageEvent e) {
        if (e.getEntity().getType() != EntityType.VILLAGER) return;
        if (e instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) e;
            if (!(event.getDamager() instanceof Player)) {
                e.setCancelled(true);
            }
        } else {
            e.setCancelled(true);
        }
    }
    
    @EventHandler (priority=EventPriority.HIGH)
    public void onHostageDeath(EntityDeathEvent e) {
        if (e.getEntity().getType() != EntityType.VILLAGER) return;
        Entity died = e.getEntity();
        if (died.equals(hostage)) {
            HandlerList.unregisterAll(this);
            plugin.debug().log("HostageListener removed due to EntityDeathEvent");
            Player killer = e.getEntity().getKiller();
            killer.sendMessage("You have killed a hostage.");
        }
    }
    
}
