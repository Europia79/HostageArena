package mc.euro.extraction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import mc.alk.arena.competition.match.Match;
import mc.alk.arena.util.SerializerUtil;
import mc.euro.extraction.api.IHostagePlugin;
import mc.euro.extraction.appljuze.CustomConfig;
import mc.euro.extraction.nms.Hostage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Villager;

/**
 *
 * @author Nikolai
 */
public class HostageTracker implements Runnable {
    
    public static List<Hostage> allhostages;
    public List<Hostage> hostages;
    
    IHostagePlugin plugin;
    Match match;
    HostageArena arena;
    List<Location> extractions;
    World world;
    int taskID;
    
    public HostageTracker(HostageArena a) {
        this.plugin = (IHostagePlugin) Bukkit.getPluginManager().getPlugin("HostageArena");
        this.arena = a;
        this.extractions = arena.getExtractionPoints();
        this.world = extractions.get(0).getWorld();
    }
    public HostageTracker(Match m, List<Location> epoints) {
        this.plugin = (IHostagePlugin) Bukkit.getPluginManager().getPlugin("HostageArena");
        this.match = m;
        this.extractions = epoints;
        this.world = extractions.get(0).getWorld();
    }

    @Override
    public void run() {
        if (match.isFinished()) {
            plugin.getServer().getScheduler().cancelTask(taskID);
        }
        if (world != null && world.getEntitiesByClass(Villager.class) == null) {
            plugin.debug().log("No hostages have been found.");
            return;
        }
        
        Collection collection = world.getEntitiesByClass(Villager.class);
        plugin.debug().log("" + collection.size() + " hostages have been found.");
        plugin.debug().msgArenaPlayers(match.getPlayers(),"" + collection.size()
                + " hostages have been found.");
        
        
    }
    
}
