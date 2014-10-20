package mc.euro.extraction;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import mc.alk.arena.competition.match.Match;
import mc.euro.extraction.api.IHostagePlugin;
import mc.euro.extraction.nms.Hostage;
import mc.euro.extraction.timers.ExtractionTimer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Nikolai
 */
public class HostageTracker extends BukkitRunnable {
    
    IHostagePlugin plugin;
    Match match;
    Set<Hostage> hostages;
    List<Location> extractions;
    ExtractionTimer timer;
    Set<Hostage> extractionZone;
    
    public HostageTracker(Match m, Set<Hostage> hSet, List<Location> epoints) {
        this.plugin = (IHostagePlugin) Bukkit.getPluginManager().getPlugin("HostageArena");
        this.match = m;
        this.hostages = hSet;
        this.extractions = epoints; // extraction points
        this.extractionZone = new LinkedHashSet<Hostage>();
        this.timer = new ExtractionTimer();
    }

    @Override
    public void run() {
        if (match.isFinished()) {
            stop();
        }
        if (zoneContainsHostage()) {
            if (!timer.hasStarted()) {
                timer.start();
            } else {
                timer.setExtractionZone(extractionZone);
            }
        }
        
    }

    /**
     * If the extractionZone is NOT empty, then it contains hostages. <br/>
     */
    private boolean zoneContainsHostage() {
        extractionZone.clear();
        Set<Hostage> vips = new LinkedHashSet<Hostage>(hostages);
        for (Hostage h : vips) {
            for (Location loc : extractions) {
                double distance = loc.distance(h.getLocation());
                plugin.debug().log("distance = " + distance);
                if (distance <= 12) {
                    plugin.debug().log("Hostaged added");
                    extractionZone.add(h);
                }
            }
        }
        plugin.debug().log("extractionZone.isEmpty() = " + extractionZone.isEmpty());
        return (!extractionZone.isEmpty());
    }
    
    public void stop() {
        timer.cancel();
        this.cancel();
    }
    
}
