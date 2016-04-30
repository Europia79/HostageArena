package mc.euro.extraction.arenas;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import mc.alk.arena.BattleArena;
import mc.alk.arena.controllers.PlayerController;
import mc.alk.arena.events.matches.MatchStartEvent;
import mc.alk.arena.objects.ArenaPlayer;
import mc.alk.arena.objects.MatchResult;
import mc.alk.arena.objects.arenas.Arena;
import mc.alk.arena.objects.events.ArenaEventHandler;
import mc.alk.arena.objects.events.EventPriority;
import mc.alk.arena.objects.spawns.EntitySpawn;
import mc.alk.arena.objects.teams.ArenaTeam;
import mc.alk.arena.serializers.Persist;
import mc.euro.extraction.api.ExtractionPlugin;
import mc.euro.extraction.events.ExtractionTimerEvent;
import mc.euro.extraction.events.HostageDeathEvent;
import mc.euro.extraction.events.HostageExtractedEvent;
import mc.euro.extraction.nms.Hostage;
import mc.euro.extraction.nms.NPCFactory;
import mc.euro.extraction.sound.SoundAdapter;
import mc.euro.extraction.timers.ExtractionTimer;
import mc.euro.extraction.util.ArenaUtil;
import mc.euro.extraction.util.Villagers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.scheduler.BukkitTask;

/**
 * ExtractionArena is the parent for HostageArena & VipArena. </br></br>
 * 
 * <pre>
 * ExtractionArena contains all the fields & methods common to both arenas.
 * 
 * https://en.wikipedia.org/wiki/Extraction_(military)
 * 
 * Altho HostageArena & VipArena are very similar (they both involve extracting 
 * hostages or VIPs) each has their own rule-set.
 * 
 * For VipArena, either team can rescue/capture the VIPs.
 * 
 * For HostageArena, one team guards the hostages while the other attempts to rescue them.
 * </pre>
 * 
 * @author Nikolai
 */
public abstract class ExtractionArena extends Arena implements Runnable {
    
    protected ExtractionPlugin plugin;
    protected NPCFactory npcFactory;
    protected Collection<Hostage> vips = new ArrayList<Hostage>();
    protected BukkitTask tracker; // HostageTracker task
    protected ExtractionTimer timer;
    protected Set<Hostage> extractionZone = new LinkedHashSet<Hostage>();
    
    /**
     * key = teamID, value = total number of hostages killed.
     */
    protected Map<Integer, Integer> kills = new HashMap<Integer, Integer>();
    protected Map<Integer, Integer> rescued = new HashMap<Integer, Integer>();
    
    @Persist public boolean AllowPlayersToKillHostages;
    @Persist protected List<Location> epoints = new CopyOnWriteArrayList<Location>();
    @Persist protected int HOSTAGES_2WIN;
    @Persist protected int HOSTAGES_2LOSE; // ZERO COULD OPEN UP POSSIBLE EXPLOIT
    protected int EXTRACTION_ZONE_RADIUS;
    protected int HOSTAGE_HP;
    
    /**
     * Pre-BattleArena v3.9.8 constructor to support backwards compatibility.
     */
    public ExtractionArena() {
        this.plugin = (ExtractionPlugin) Bukkit.getServer().getPluginManager().getPlugin("HostageArena");
        this.npcFactory = NPCFactory.newInstance(plugin);
        this.HOSTAGE_HP = plugin.getConfig().getInt("HostageHP", 3);
        this.EXTRACTION_ZONE_RADIUS = plugin.getConfig().getInt("ExtractionZoneRadius", 12);
    }
    
    /**
     * This constructor requires BattleArena v3.9.8+.
     */
    public ExtractionArena(ExtractionPlugin plugin, NPCFactory npcFactory) {
        this.plugin = plugin;
        this.npcFactory = npcFactory;
        this.HOSTAGE_HP = this.plugin.getConfig().getInt("HostageHP", 3);
        this.EXTRACTION_ZONE_RADIUS = plugin.getConfig().getInt("ExtractionZoneRadius", 12);
    }
    
    public abstract boolean canInteract(ArenaPlayer ap);
    public abstract boolean canRescue(ArenaPlayer ap);
    
    public boolean canInteract(Player p) {
        return canInteract(BattleArena.toArenaPlayer(p));
    }
    
    public boolean canRescue(Player p) {
        return canInteract(BattleArena.toArenaPlayer(p));
    }
    
    @Override
    public void run() {
        if (getMatch().isFinished() || vips.isEmpty()) {
            stop();
            return;
        }
        if (zoneContainsHostage() && (!timer.hasStarted())) {
            timer.start();
        }
        if (timer.hasStarted()) {
            timer.setExtractionZone(extractionZone);
        }
    }
    
    /**
     * If the extractionZone is NOT empty, then it contains hostages. <br/>
     */
    public boolean zoneContainsHostage() {
        extractionZone.clear();
        for (Hostage h : getHostages()) {
            if (h.getRescuer() == null) continue;
            if (!canRescue(h.getRescuer())) continue;
            
            for (Location loc : getExtractionPoints()) {
                double distance = loc.distance(h.getLocation());
                if (distance <= EXTRACTION_ZONE_RADIUS) {
                    extractionZone.add(h);
                }
            }
        }
        return (!extractionZone.isEmpty());
    }
    
    public void stop() {
        if (timer.hasStarted()) timer.stop();
        tracker.cancel();
    }
    
    @ArenaEventHandler
    public void matchStartEvent(MatchStartEvent e) {
        plugin.debug().log("MatchStartEvent called");
    }
    
    @ArenaEventHandler(priority = EventPriority.HIGHEST, needsPlayer = false)
    public void onHostageSpawn(CreatureSpawnEvent e) {
        if (getMatch().getPlayers().isEmpty()) return;
        if (e.getEntity().getType() != EntityType.VILLAGER) return;
        // if (e.getEntity() instanceof Hostage) return;
        plugin.debug().log("CreatureSpawnEvent has detected a Villager spawn.");
        plugin.debug().log("getHostages().size() = " + getHostages().size());
        plugin.debug().log("getHostageSpawns().size() = " + ArenaUtil.getHostageSpawns(this).size());
        String isEmpty = "" + getName() + " PLAYERS IN MATCH = " + getMatch().getPlayers();
        plugin.debug().log(isEmpty);
        
        if (getHostages().size() >= ArenaUtil.getHostageSpawns(this).size()) {
            e.setCancelled(true);
            return;
        }
        
        /**
         * BattleArena will spawn the Villagers BEFORE onStart().
         * If the respawn time is greater than zero, then 
         * BattleArena will immediately respawn the villagers again 
         * AFTER onStart(). Creating a duplication glitch.
         */
        if (!npcFactory.isHostage(e.getEntity())) {
            plugin.debug().log("Villager is NOT of type Hostage.");
            e.setCancelled(true);
            plugin.debug().log("CreatureSpawnEvent CANCELLED");
            
            Hostage hostage = npcFactory.spawnHostage(e.getEntity().getLocation());
            hostage.setCustomName(Villagers.getName());
            hostage.setProfessionType(Villagers.getType());
            vips.add(hostage);
            hostage.stay();
        } else {
            plugin.debug().log("CreatureSpawnEvent detected a Hostage spawn");
        }
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
        if (!e.getEventName().equals("PlayerInteractEntityEvent")) return;
        
        Entity entity = e.getRightClicked();
        Hostage h = npcFactory.getHostage(entity);
        Player p = (Player) e.getPlayer();
        
        if (h == null) return;
        if (!canInteract(p)) return;
        
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
        if (!(e.getDamager() instanceof Player) || !AllowPlayersToKillHostages) {
            e.setCancelled(true);
            return;
        }
        Player player = (Player) e.getDamager();
        plugin.debug().sendMessage(player, "Hostage has been damaged");
        // dmg = (TotalHP + 0.01) / DesiredHP;
        Villager v = (Villager) e.getEntity();
        double dmg = (v.getMaxHealth() + 0.01) / this.HOSTAGE_HP;
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
        assert AllowPlayersToKillHostages : "Players are not allowed to kill hostages.";
        Hostage h = npcFactory.getHostage(e.getEntity());
        if (vips.contains(h)) {
            vips.remove(h);
        }
        // document which team killed the hostage.
        ArenaPlayer killer = PlayerController.getArenaPlayer(e.getEntity().getKiller());
        HostageDeathEvent deathEvent = new HostageDeathEvent(h, killer);
        Bukkit.getServer().getPluginManager().callEvent(deathEvent);
        killer.sendMessage("You have killed a hostage.");
        // document which team killed the hostage.
        int teamID = getTeam(killer).getId();
        int k = kills.get(teamID);
        k = k + 1;
        // End the match if they killed 2 of 3 hostages
        if (k >= HOSTAGES_2LOSE) {
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
    
    @ArenaEventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player player = e.getEntity();
        String name = player.getName();
        for (Hostage h : vips) {
            if (h.getOwnerName() != null && h.getOwnerName().equalsIgnoreCase(name)) {
                h.stay();
            }
        }
    }
    
    @ArenaEventHandler (needsPlayer = false)
    public void onTimerTick(ExtractionTimerEvent e) {
        if (getMatch().getPlayers().isEmpty()) return;
        plugin.debug().log("" + getName() + " ExtractionTimerEvent called.");
        int time = e.getTime();
        if (time >= 0) {
            getMatch().sendMessage("" + time);
            String s = plugin.getConfig().getString("TimerSound");
            Sound sound = SoundAdapter.getSound(s);
            float volume = (float) plugin.getConfig().getDouble("TimerRange", 256) / 16;
            float pitch = (float) plugin.getConfig().getDouble("TimerPitch", 1);
            for (ArenaPlayer ap : getMatch().getPlayers()) {
                ap.getPlayer().playSound(ap.getLocation(), sound, volume, pitch);
            }
        }
    }
    
    @ArenaEventHandler
    public void onHostageRescuedEvent(HostageExtractedEvent e) {
        if (getMatch().getPlayers().isEmpty()) return;
        
        int teamID = e.getRescuer().getTeam().getId();
        int x = rescued.get(teamID);
        x = x + 1;
        rescued.put(teamID, x);
        Hostage h = e.getHostage();
        if (vips.contains(h)) {
            vips.remove(h);
        }
        getMatch().sendMessage("Hostage Extracted !");
        plugin.debug().log(getName() + " Total rescued = " + this.rescued);
        int k = 0;
        for (Integer i : kills.values()) {
            k = k + i;
        }
        plugin.debug().log(getName() + " Total killed = " + k);
        plugin.debug().log(getName() + " vips.size() = " + vips.size());
        if (rescued.get(teamID) == HOSTAGES_2WIN) {
            MatchResult result = new MatchResult();
            ArenaTeam winners = getTeam(e.getRescuer());
            result.setVictor(winners);
            ArenaTeam losers = getOtherTeam(winners);
            result.addLoser(losers);
            losers.sendMessage("" + losers.getTeamChatColor() 
                    + "You lost! The other team rescued the hostages.");
            winners.sendMessage("" + winners.getTeamChatColor()
                    + "You won! You have rescued the hostages.");
            getMatch().endMatchWithResult(result);
        }
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
    
    @Override
    protected void onPrestart() {
        plugin.debug().log("onPrestart() called.");
    }
    
    /**
     * Order: onBegin(), onStart(), onComplete(), onFinish(). 
     */
    @Override
    public void onBegin() {
        plugin.debug().log("onBeing() called.");
        validateExtractionArena();
    }
    
    private void validateExtractionArena() {
        validateTeams();
        validateExtractionPoints();
        validateHostageSpawns();
    }
    
    private void validateTeams() {
        if (getTeams().size() != 2) {
            plugin.getLogger().warning("VipArena & HostageArena require exactly 2 teams.");
            plugin.getLogger().warning("Match is being cancelled.");
            getMatch().cancelMatch();
        }
    }
    
    private void validateExtractionPoints() {
        if (epoints.isEmpty()) {
            String WARNING = "There are no extraction points configured for this arena.";
            plugin.debug().msgArenaPlayers(getMatch().getPlayers(), WARNING);
            getMatch().cancelMatch();
        }
    }
    
    private void validateHostageSpawns() {
        Map<Long, EntitySpawn> emap = ArenaUtil.getHostageSpawns(this);
        if (emap.isEmpty()) {
            plugin.getLogger().warning("There are no hostage spawns configured for this arena.");
            getMatch().cancelMatch();
            return;
        }
        if (HOSTAGES_2WIN == 0) {
            HOSTAGES_2WIN = 1;
        }
        if (HOSTAGES_2WIN > emap.size()) {
            String type_name = getMatch().getName() + " " + getName();
            plugin.getLogger().warning(type_name + " was mis-configured.");
            plugin.getLogger().warning("The number of hostages needed to win "
                    + "was set higher than the number of hostage spawns.");
            plugin.getLogger().warning("Changing the number of hostages need to win "
                    + "to the max: " + emap.size());
            HOSTAGES_2WIN = emap.size();
        }
    }
    
    /**
     * Order: onBegin(), onStart(), onComplete(), onFinish(). 
     */
    @Override
    public void onStart() {
        plugin.debug().log("ExtractionArena onStart() called.");
        this.kills.clear();
        this.rescued.clear();
        this.timer = new ExtractionTimer(getMatch().getArena());
        this.tracker = Bukkit.getScheduler().runTaskTimer(plugin, this, 20L, 20L);
        
        for (ArenaTeam t : getMatch().getTeams()) {
            int teamID = t.getId();
            kills.put(teamID, 0);
            rescued.put(teamID, 0);
        }
    }
    
    /**
     * Order: onBegin(), onStart(), onComplete(), onFinish(). 
     */
    @Override
    public void onComplete() {
        plugin.debug().log("onComplete() has been called");
    }
    
    /**
     * Order: onBegin(), onStart(), onComplete(), onFinish(). 
     */
    @Override
    public void onFinish() {
        plugin.debug().log("onFinish() has been called");
        kills.clear();
        tracker.cancel();
        timer.stop();
        removeHostages();
    }
    
    private void removeHostages() {
        for (Hostage h : vips) {
            h.removeEntity();
        }
        vips.clear();
    }
    
    public Collection<Hostage> getHostages() {
        return this.vips;
    }
    
    public void addExtractionPoint(Location loc) {
        epoints.add(loc);
    }
    
    public void clearExtractionPoints() {
        epoints.clear();
    }
    
    public List<Location> getExtractionPoints() {
        return epoints;
    }
    
    public void setNumberOfHostagesNeededToWin(int x) {
        HOSTAGES_2WIN = x;
    }
    
    public void setNumberOfHostageDeathsToLose(int x) {
        HOSTAGES_2LOSE = x;
    }
}
