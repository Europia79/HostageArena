package mc.euro.extraction.timers;

import java.util.LinkedHashSet;
import java.util.Set;

import mc.alk.arena.competition.match.Match;
import mc.alk.arena.controllers.PlayerController;
import mc.alk.arena.objects.ArenaPlayer;
import mc.alk.arena.objects.arenas.Arena;
import mc.euro.extraction.events.ExtractionTimerEvent;
import mc.euro.extraction.events.HostageExtractedEvent;
import mc.euro.extraction.nms.Hostage;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

/**
 *
 * @author Nikolai
 */
public class ExtractionTimer implements Runnable {
    
    Plugin plugin;
    int duration;
    boolean started;
    BukkitTask task;
    Set<Hostage> extractionZone = new LinkedHashSet<Hostage>();
    
    Arena arena;
    Match match;
    
    public ExtractionTimer(Arena arena) {
        this.plugin = Bukkit.getServer().getPluginManager().getPlugin("HostageArena");
        this.duration = plugin.getConfig().getInt("ExtractionTimer", 30) + 1;
        this.started = false;
        this.arena = arena;
        this.match = arena.getMatch();
    }
    
    public ExtractionTimer(Arena arena, int time) {
        this.plugin = Bukkit.getServer().getPluginManager().getPlugin("HostageArena");
        this.duration = time;
        this.started = false;
        this.arena = arena;
        this.match = arena.getMatch();
    }
    
    public int start() {
        if (started == false) {
            started = true;
            task = Bukkit.getScheduler().runTaskTimer(plugin, this, 20L, 20L);
        }
        return task.getTaskId();
    }
    
    public void stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }
    
    public boolean hasStarted() {
        return this.started;
    }
    
    @Override
    public void run() {
        this.duration = duration - 1;
        ExtractionTimerEvent timerEvent = new ExtractionTimerEvent(arena, this);
        Bukkit.getServer().getPluginManager().callEvent(timerEvent);
        
        Set<Hostage> clonedExtractionZone = new LinkedHashSet<Hostage>(extractionZone);
        
        if (duration <= 0 && !extractionZone.isEmpty()) {
            
            for (Hostage h : clonedExtractionZone) {
                ArenaPlayer rescuer = PlayerController.toArenaPlayer(h.getRescuer());
                HostageExtractedEvent rescuedEvent = new HostageExtractedEvent(h, rescuer);
                Bukkit.getServer().getPluginManager().callEvent(rescuedEvent);
                h.removeEntity();
                extractionZone.remove(h);
            }
        }
    }
    
    public int getTime() {
        return this.duration;
    }
    
    public Arena getArena() {
        return this.arena;
    }
    
    public Match getMatch() {
        return this.match;
    }
    
    public synchronized void setExtractionZone(Set<Hostage> hostagesAtExtractionZone) {
        this.extractionZone = hostagesAtExtractionZone;
    }
}
