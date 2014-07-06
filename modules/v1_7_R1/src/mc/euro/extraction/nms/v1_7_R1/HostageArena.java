package mc.euro.extraction.nms.v1_7_R1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mc.alk.arena.competition.match.Match;
import mc.alk.arena.objects.arenas.Arena;
import mc.alk.arena.objects.events.ArenaEventHandler;
import mc.alk.arena.objects.events.EventPriority;
import mc.alk.arena.objects.spawns.TimedSpawn;
import mc.euro.extraction.debug.*;
import mc.euro.extraction.util.Attributes;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_7_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;
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
public class HostageArena extends Arena {
    JavaPlugin plugin;
    public static List<Hostage> allhostages = new ArrayList<Hostage>();
    List<Hostage> hlist = new ArrayList<Hostage>();
    Map<Integer, List<Villager>> hostages = new HashMap<Integer, List<Villager>>();
    DebugInterface debug;
    int taskID;
    int counter;
    int desiredHP;
    Map<Integer, HostageTracker> trackers;
    
    public HostageArena() {
        this.plugin = (JavaPlugin) Bukkit.getServer().getPluginManager().getPlugin("HostageArena");
        this.debug = new DebugOn(this.plugin);
        trackers = new HashMap<Integer, HostageTracker>();
        this.counter = 0;
        this.desiredHP = plugin.getConfig().getInt("HostageHP", 3);
    }
    
    // @ArenaEventHandler (priority=EventPriority.HIGHEST,entityMethod="getEntity")
    @ArenaEventHandler(priority = EventPriority.HIGHEST, needsPlayer = false)
    public void onSpawn(CreatureSpawnEvent e) {
        if (e.getEntity().getType() != EntityType.VILLAGER) return;
        if (e.getEntity() instanceof Hostage) return;
        this.debug.log("CreatureSpawnEvent has detected a Villager spawn.");

        Villager v = (Villager) e.getEntity();
        v.setCustomName(Attributes.getName(plugin));
        v.setProfession(Attributes.getType(plugin));

    }
    
    @ArenaEventHandler (priority=EventPriority.HIGHEST)
    public void onInvOpen(InventoryOpenEvent e) {
        if (e.getInventory().getType() != InventoryType.MERCHANT) return;
        e.setCancelled(true);
    }
    
    @ArenaEventHandler (priority=EventPriority.HIGHEST)
    public void onHostageInteract(PlayerInteractEntityEvent e) {
        this.debug.log("onHostageInteract() has been called.");
        if (e.getRightClicked().getType() != EntityType.VILLAGER) return;
        
        Entity E = e.getRightClicked();
        Hostage h;
        try {
            h = (Hostage) ((CraftEntity)E).getHandle();
        } catch (ClassCastException ex) {
            // Caused by baby villager or a non-Hostage Villager.
            this.debug.log("onHostageInteract() ClassCastException: most likely "
                    + "caused by a baby villager or a Villager that is not a Hostage.");
            Villager v = (Villager) e.getRightClicked();
            double HP = v.getHealth();
            Profession p = v.getProfession();
            String name = v.getCustomName();
            Hostage hostage = new Hostage(((CraftWorld) v.getWorld()).getHandle(), 
                    Attributes.getType(plugin).getId(), e.getPlayer().getName());
            hostage.setLocation(v.getLocation().getX(), v.getLocation().getY(), v.getLocation().getZ(), 
                    v.getLocation().getYaw(), v.getLocation().getPitch());
            ((CraftWorld) v.getWorld()).getHandle().removeEntity(((CraftEntity) e.getRightClicked()).getHandle());
            ((CraftWorld) v.getWorld()).getHandle().addEntity(hostage);
            hostage.setHealth((float) HP);
            hostage.setProfession(p.getId());
            hostage.setCustomName(name);
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
    }
    
    @ArenaEventHandler (priority=EventPriority.HIGH)
    public void onDamageEvent(EntityDamageByEntityEvent e) {
        if (e.getEntity().getType() != EntityType.VILLAGER) return;
        if (!(e.getDamager() instanceof Player)) return;
        Player player = (Player) e.getDamager();
        this.debug.sendMessage(player, "Hostage has been damaged");
        // dmg = (TotalHP + 0.01) / DesiredHP;
        Villager v = (Villager) e.getEntity();
        double dmg = (v.getMaxHealth() + 0.01) / this.desiredHP;
        this.debug.log("Hostage took " + dmg + " damage");
        e.setDamage(dmg);

    }
    
    @ArenaEventHandler (priority=EventPriority.HIGH)
    public void onHostageDeath(EntityDeathEvent e) {
        if (e.getEntity().getType() != EntityType.VILLAGER) return;
        
        // document which team killed the hostage.
        // End the match if they killed 2 of 3 hostages
        Player killer = e.getEntity().getKiller();
        debug.sendMessage(killer, "You have killed a hostage.");
    }
    
    /**
     * Order: onStart(), onComplete(), onFinish() 
     */
    @Override
    public void onStart() {
        super.onStart();
        debug.msgArenaPlayers(getMatch().getPlayers(), "onStart() has been called.");
        int matchID = getMatch().getID();
        trackers.put(matchID, new HostageTracker(getMatch()));
        this.taskID = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, trackers.get(matchID), 0L, 20L);
    }
    
    /**
     * Order: onStart(), onComplete(), onFinish() 
     */
    @Override
    public void onComplete() {
        debug.log("onComplete() has been called");
    }
    
    /**
     * Order: onStart(), onComplete(), onFinish() 
     */
    @Override
    public void onFinish() {
        debug.log("onFinish() has been called");
        plugin.getServer().getScheduler().cancelTask(taskID);
    }
    
    public List getHostages() {
        Match m = getMatch();
        Arena arena = m.getArena();
        List tlist = new ArrayList();
        Map<Long, TimedSpawn> vmap = arena.getTimedSpawns();
        for (Entity en : vmap.get(1L).getSpawn().getLocation().getChunk().getEntities()) {
            if (en instanceof Villager) tlist.add(en);
        }
        return tlist;
    }
    
    public void setDebug(boolean b) {
        
    }
}
