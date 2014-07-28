package mc.euro.extraction.nms.v1_7_R1;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import mc.alk.arena.competition.match.Match;
import mc.alk.arena.util.SerializerUtil;
import mc.euro.extraction.api.SuperPlugin;
import mc.euro.extraction.appljuze.CustomConfig;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Villager;

/**
 *
 * @author Nikolai
 */
public class HostageTracker implements Runnable {
    
    public static List<Hostage> allhostages;
    public List<Hostage> hostages;
    
    SuperPlugin plugin;
    Match match;
    Location extraction;
    List<Location> extractions;
    int taskID;
    
    public HostageTracker(Match m) {
        this.plugin = (SuperPlugin) Bukkit.getPluginManager().getPlugin("HostageArena");
        this.match = m;
        loadExtractionPoints();
        
    }

    private void loadExtractionPoints() {
        // path = arenas.{arena}.extractionpoints
        CustomConfig config = plugin.getConfig("arenas.yml");
        String path = "arenas." + match.getArena().getName() + ".extractionpoints";
        if (config.getStringList(path) == null) {
            plugin.getLogger().severe("No Extraction points found for arena: " + match.getArena().getName());
            plugin.getLogger().severe("Match is being canceled because the arena is mis-configured.");
            match.cancelMatch();
            return;
        }
        List<String> listLocations = config.getStringList(path);
        this.extractions = new ArrayList<Location>();
        for (String s : listLocations) {
            Location t = SerializerUtil.getLocation(s);
            this.extraction = t;
            this.extractions.add(extraction);
        }
    }

    @Override
    public void run() {
        if (match.isFinished()) {
            plugin.getServer().getScheduler().cancelTask(taskID);
        }
        if (this.extraction.getWorld().getEntitiesByClass(Villager.class) == null) {
            plugin.debug().log("No hostages have been found.");
            return;
        }
        
        Collection collection = extraction.getWorld().getEntitiesByClass(Villager.class);
        plugin.debug().log("" + collection.size() + " hostages have been found.");
        plugin.debug().msgArenaPlayers(match.getPlayers(),"" + collection.size()
                + " hostages have been found.");
        
    }
    
}
