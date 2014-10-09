package mc.euro.extraction.timers;

import java.util.ArrayList;
import java.util.List;
import mc.alk.arena.competition.match.Match;
import mc.euro.extraction.api.IHostagePlugin;
import mc.euro.extraction.nms.Hostage;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Nikolai
 */
public class ExtractionTimer extends BukkitRunnable {
    
    IHostagePlugin plugin;
    int duration;
    Match match;
    List<Hostage> hostages = new ArrayList<Hostage>();
    
    public ExtractionTimer(Match m) {
        this.plugin = (IHostagePlugin) Bukkit.getServer().getPluginManager().getPlugin("HostageArena");
        this.duration = plugin.getConfig().getInt("ExtractionTimer", 15) + 1;
        this.match = m;
    }

    @Override
    public void run() {
        this.duration = duration - 1;
        if (duration >= 0) match.sendMessage("" + duration);
        
        if (duration <= 0) {
            
        }
    }
}
