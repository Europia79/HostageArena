package mc.euro.extraction.nms.v1_5_R2;

import java.util.ArrayList;
import java.util.List;
import mc.alk.arena.executors.CustomCommandExecutor;
import mc.alk.arena.executors.MCCommand;
import mc.alk.arena.objects.arenas.Arena;
import mc.alk.arena.util.SerializerUtil;
import mc.euro.extraction.debug.*;
import mc.euro.extraction.commands.Command;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Nikolai
 */
public class HostageExecutor extends CustomCommandExecutor {
    
    JavaPlugin plugin;
    DebugInterface debug;
    
    public HostageExecutor() {
        this.plugin = (JavaPlugin) Bukkit.getServer().getPluginManager().getPlugin("HostageArena");
        boolean b = plugin.getConfig().getBoolean("Debug");
        if (b == true) {
            this.debug = new DebugOn(plugin);
        } else {
            this.debug = new DebugOff(plugin);
        }
    }
    
    @MCCommand(cmds={"setextraction"}, perm="hostagearena.vips.extraction")
    public boolean setExtractionPoint(Player sender, Arena arena) {
        // path = arenas.{arena}.extractionpoints.{index}
        String path = "arenas." + arena.getName();
        Location loc = sender.getLocation();
        String sloc = SerializerUtil.getLocString(loc);
        List<String> locations = new ArrayList<String>();
        locations.add(sloc);
        plugin.getConfig().set(path, locations);
        plugin.saveConfig();
        return true;
    }
    
    @MCCommand(cmds={"setspawn"}, perm="hostagearena.vips.spawn")
    public boolean setHostageSpawn(Player sender, Arena arena) {
        debug.log("arena = " + arena.getName());
        int matchTime = arena.getParams().getMatchTime();
        
        debug.log("setHostageSpawn() MatchTime = " + matchTime);
        
        String selectArena = "aa select " + arena.getName();
        
        plugin.getServer().dispatchCommand(sender, selectArena);
        
        World w = sender.getWorld();
        double x = sender.getLocation().getX();
        double y = sender.getLocation().getY();
        double z = sender.getLocation().getZ();
        float yaw = sender.getLocation().getYaw();
        float pitch = sender.getLocation().getPitch();
        
        Location location = new Location(w, (x - 1), y, z, yaw, pitch);
        sender.teleport(location);
        plugin.getServer().dispatchCommand(sender, 
                Command.addspawn(matchTime, 1));
        
        location = new Location(w, x, y, (z - 1), yaw, pitch);
        sender.teleport(location);
        plugin.getServer().dispatchCommand(sender, 
                Command.addspawn(matchTime, 2));
        
        location = new Location(w, x, y, z, yaw, pitch);
        sender.teleport(location);
        plugin.getServer().dispatchCommand(sender, 
                Command.addspawn(matchTime, 3));
        
        sender.sendMessage("The hostage spawn point for " + arena.getName() + " has been set!");
        
        return true;
    }
    
    @MCCommand(cmds={"spawn"})
    public boolean spawnHostage(Player sender) {
        
        return true;
    }
    
}
