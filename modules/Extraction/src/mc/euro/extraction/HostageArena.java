package mc.euro.extraction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import mc.alk.arena.competition.match.Match;
import mc.alk.arena.events.matches.MatchResultEvent;
import mc.alk.arena.objects.MatchResult;
import mc.alk.arena.objects.arenas.Arena;
import mc.alk.arena.objects.events.ArenaEventHandler;
import mc.alk.arena.objects.events.EventPriority;
import mc.alk.arena.objects.spawns.TimedSpawn;
import mc.alk.arena.objects.teams.ArenaTeam;
import mc.alk.arena.serializers.Persist;
import mc.euro.extraction.api.HostageRoom;
import mc.euro.extraction.api.IHostagePlugin;
import mc.euro.extraction.nms.Hostage;
import mc.euro.extraction.nms.NPCFactory;
import mc.euro.extraction.util.Attributes;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEntityEvent;

/**
 *
 * @author Nikolai
 */
public class HostageArena extends Arena {
    IHostagePlugin plugin;
    NPCFactory factory;
    Set<Hostage> vips = new LinkedHashSet<Hostage>(6);
    List<Hostage> hostages = new ArrayList<Hostage>(6);
    int desiredHP;
    HostageTracker tracker;
    int taskID;
    /**
     * {@literal <pre>Map<teamID, kills></pre>}
     */
    Map<Integer, Integer> kills = new HashMap<Integer, Integer>();
    
    @Persist
    List<Location> epoints = new CopyOnWriteArrayList<Location>();
    
    public HostageArena() {
        this.plugin = (IHostagePlugin) Bukkit.getServer().getPluginManager().getPlugin("HostageArena");
        // trackers = new HashMap<Integer, HostageTracker>();
        this.desiredHP = plugin.getConfig().getInt("HostageHP", 3);
        this.factory = NPCFactory.newInstance(plugin);
    }
    
    public void addExtractionPoint(Location loc) {
        epoints.add(loc);
    }
    
    public void clearExtractionPoints() {
        epoints.clear();
    }
    
    public List<Location> getExtractionPoints() {
        return new ArrayList<Location>(epoints);
    }
    
    // @ArenaEventHandler (priority=EventPriority.HIGHEST,entityMethod="getEntity")
    @ArenaEventHandler(priority = EventPriority.HIGHEST, needsPlayer = false)
    public void onSpawn(CreatureSpawnEvent e) {
        if (e.getEntity().getType() != EntityType.VILLAGER) return;
        if (e.getEntity() instanceof Hostage) return;
        plugin.debug().log("CreatureSpawnEvent has detected a Villager spawn.");

        Villager v = (Villager) e.getEntity();
        v.setCustomName(Attributes.getName(plugin));
        v.setProfession(Attributes.getType(plugin));
        
        // getHostage() is glitchy when located inside CreatureSpawnEvent
        /*
        Hostage hostage = factory.getHostage(v);
        
        if (!vips.contains(hostage)) {
            vips.add(hostage);
        }
        */
    }
    
    @ArenaEventHandler (priority=EventPriority.HIGHEST)
    public void onInvOpen(InventoryOpenEvent e) {
        if (e.getInventory().getType() != InventoryType.MERCHANT) return;
        e.setCancelled(true);
    }
    
    @ArenaEventHandler (priority=EventPriority.HIGHEST)
    public void onHostageInteract(PlayerInteractEntityEvent e) {
        plugin.debug().log("onHostageInteract() has been called.");
        if (e.getRightClicked().getType() != EntityType.VILLAGER) return;
        
        Entity E = e.getRightClicked();
        
        NPCFactory factory = NPCFactory.newInstance(plugin);
        Hostage h = factory.getHostage(E);
        
        Player p = (Player) e.getPlayer();
        
        if (h.isFollowing()) {
            plugin.debug().log("Hostage was following " + h.getOwnerName());
            plugin.debug().log("Hostage is now staying.");
            h.stay();
        } else if (h.isStopped()) {
            h.follow(p.getName());
            plugin.debug().log("Hostage was staying.");
            plugin.debug().log("Hostage is now following " + h.getOwnerName());
        }
    }
    
    @ArenaEventHandler (priority=EventPriority.HIGH)
    public void onDamageEvent(EntityDamageByEntityEvent e) {
        if (e.getEntity().getType() != EntityType.VILLAGER) return;
        if (!(e.getDamager() instanceof Player)) return;
        Player player = (Player) e.getDamager();
        plugin.debug().sendMessage(player, "Hostage has been damaged");
        // dmg = (TotalHP + 0.01) / DesiredHP;
        Villager v = (Villager) e.getEntity();
        double dmg = (v.getMaxHealth() + 0.01) / this.desiredHP;
        plugin.debug().log("Hostage took " + dmg + " damage");
        e.setDamage(dmg);

    }
    
    @ArenaEventHandler (priority=EventPriority.HIGHEST)
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
    
    @ArenaEventHandler (priority=EventPriority.HIGH)
    public void onHostageDeath(EntityDeathEvent e) {
        if (e.getEntity().getType() != EntityType.VILLAGER) return;
        Hostage h = null;
        try {
            h = (Hostage) e.getEntity();
        } catch (ClassCastException ex) {
            
        } finally {
            if (vips.contains(h)) {
                vips.remove(h);
            }
        }
        // document which team killed the hostage.
        // End the match if they killed 2 of 3 hostages
        Player killer = e.getEntity().getKiller();
        killer.sendMessage("You have killed a hostage.");
        int teamID = getTeam(killer).getId();
        int k = kills.get(teamID);
        k = k + 1;
        if (k >= 2) {
            MatchResult result = new MatchResult();
            ArenaTeam losers = getTeam(killer);
            result.addLoser(losers);
            ArenaTeam winners = getOtherTeam(losers);
            result.setVictor(winners);
            losers.sendMessage("" + losers.getTeamChatColor() 
                    + "You lost! You have killed too many hostages.");
            winners.sendMessage("" + winners.getTeamChatColor()
                    + "You won! The other team killed too many hostages.");
            getMatch().endMatchWithResult(result);
        }
        kills.put(teamID, k);
    }
    
    public ArenaTeam getOtherTeam(ArenaTeam team1) {
        ArenaTeam otherTeam = null;
        for (ArenaTeam team2 : getMatch().getTeams()) {
            if (team2.getId() != team1.getId()) {
                otherTeam = team2;
                break;
            }
        }
        return otherTeam;
    }
    
    /**
     * Order: onStart(), onComplete(), onFinish() 
     */
    @Override
    public void onStart() {
        super.onStart();
        plugin.debug().msgArenaPlayers(getMatch().getPlayers(), "onStart() has been called.");
        
        if (this.epoints != null && !epoints.isEmpty()) {
            this.tracker = new HostageTracker(getMatch(), getExtractionPoints());
            this.taskID = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, tracker, 0L, 20L);
        } else {
            String WARNING = "There are no extraction points configured for this arena.";
            plugin.debug().msgArenaPlayers(getMatch().getPlayers(), WARNING);
        }
        
        for (ArenaTeam t : getMatch().getTeams()) {
            int teamID = t.getId();
            kills.put(teamID, 0);
        }
    }
    
    /**
     * Order: onStart(), onComplete(), onFinish() 
     */
    @Override
    public void onComplete() {
        plugin.debug().log("onComplete() has been called");
    }
    
    @ArenaEventHandler
    public void onMatchResult(MatchResultEvent e) {
        for (Hostage h : vips) {
            h.removeEntity();
        }
    }
    
    /**
     * Order: onStart(), onComplete(), onFinish() 
     */
    @Override
    public void onFinish() {
        plugin.debug().log("onFinish() has been called");
        plugin.getServer().getScheduler().cancelTask(taskID);
        kills.clear();
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
    
    public List getHostageList() {
        return new ArrayList(vips);
    }
    
    public Set getHostageSet() {
        return vips;
    }
}
