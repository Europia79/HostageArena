package mc.euro.extraction.timers;

import java.util.LinkedHashSet;
import java.util.Set;

import mc.alk.arena.controllers.PlayerController;
import mc.alk.arena.objects.ArenaPlayer;
import mc.euro.extraction.events.ExtractionTimerEvent;
import mc.euro.extraction.events.HostageExtractedEvent;
import mc.euro.extraction.nms.Hostage;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

/**
 *
 * @author Nikolai
 */
public class ExtractionTimer extends BukkitRunnable {
    
    Plugin plugin;
    int duration = 31;
    boolean started;
    BukkitTask task;
    Set<Hostage> extractionZone = new LinkedHashSet<Hostage>();
    
    public ExtractionTimer() {
        this.plugin = Bukkit.getServer().getPluginManager().getPlugin("HostageArena");
        this.duration = plugin.getConfig().getInt("ExtractionTimer", 30) + 1;
        this.started = false;
    }
    
    public ExtractionTimer(int time) {
        this.plugin = Bukkit.getServer().getPluginManager().getPlugin("HostageArena");
        this.duration = time;
        this.started = false;
    }
    
    public int start() {
        if (started == false) {
            started = true;
            task = runTaskTimer(plugin, 20L, duration);
        }
        return task.getTaskId();
    }
    
    public boolean hasStarted() {
        return this.started;
    }
    
    @Override
    public void run() {
        this.duration = duration - 1;
        ExtractionTimerEvent timerEvent = new ExtractionTimerEvent(this);
        Bukkit.getServer().getPluginManager().callEvent(timerEvent);
        
        Set<Hostage> clonedExtractionZone = new LinkedHashSet<Hostage>(extractionZone);
        
        if (duration > 0) {
            
        } else if (duration <= 0 && !extractionZone.isEmpty()) {
            
            for (Hostage h : clonedExtractionZone) {
                ArenaPlayer ap = PlayerController.getArenaPlayer(h.getRescuer());
                HostageExtractedEvent rescuedEvent = new HostageExtractedEvent(h, ap);
                Bukkit.getServer().getPluginManager().callEvent(rescuedEvent);
                h.removeEntity();
            }
        }
    }
    
    public int getTime() {
        return this.duration;
    }
    
    public void setExtractionZone(Set<Hostage> hostagesAtExtractionZone) {
        this.extractionZone = hostagesAtExtractionZone;
    }
}
