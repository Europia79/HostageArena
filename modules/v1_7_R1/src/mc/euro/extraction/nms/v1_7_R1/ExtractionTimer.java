package mc.euro.extraction.nms.v1_7_R1;

import java.util.ArrayList;
import java.util.List;
import mc.alk.arena.competition.match.Match;
import mc.euro.extraction.api.SuperPlugin;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Nikolai
 */
public class ExtractionTimer extends BukkitRunnable {
    
    SuperPlugin plugin;
    int duration;
    Match match;
    List<CraftHostage> hostages = new ArrayList<CraftHostage>();
    
    public ExtractionTimer(Match m) {
        this.plugin = (SuperPlugin) Bukkit.getServer().getPluginManager().getPlugin("HostageArena");
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
