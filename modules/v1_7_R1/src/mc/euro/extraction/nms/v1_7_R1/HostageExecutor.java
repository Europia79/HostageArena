package mc.euro.extraction.nms.v1_7_R1;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import mc.alk.arena.executors.CustomCommandExecutor;
import mc.alk.arena.executors.MCCommand;
import mc.alk.arena.objects.arenas.Arena;
import mc.alk.arena.objects.spawns.SpawnLocation;
import mc.alk.arena.util.SerializerUtil;
import mc.euro.extraction.debug.*;
import mc.euro.extraction.commands.Command;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
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
    
    @MCCommand(cmds={"set","add"},subCmds={"extractionpoint"}, op=true)
    public boolean setExtractionPoint(Player sender, Arena arena) {
        // path = arenas.{arena}.extractionpoints
        String path = "arenas." + arena.getName() + ".extractionpoints";
        List<String> locations = new ArrayList<String>();
        if (plugin.getConfig().getStringList(path) != null) {
            locations = plugin.getConfig().getStringList(path);
        }
        Location loc = sender.getLocation();
        String stringLocation = SerializerUtil.getLocString(loc);
        locations.add(stringLocation);
        plugin.getConfig().set(path, locations);
        plugin.getConfig().set("arenas." + arena.getName() + ".path", path);
        plugin.saveConfig();
        sender.sendMessage("Extraction point set!");
        return true;
    }
    
    @MCCommand(cmds={"clear"}, subCmds={"extractionpoints"},op=true)
    public boolean clearExtractionPoints(CommandSender sender, Arena arena) {
        // path = arenas.{arena}.extractionpoints
        String path = "arenas." + arena.getName() + ".extractionpoints";
        plugin.getConfig().set(path, null);
        plugin.saveConfig();
        sender.sendMessage("All extraction points have been deleted.");
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
    
    /**
     * Toggles debug mode ON / OFF.
     * Usage: /bomb debug
     */
    @MCCommand(cmds={"debug"}, perm="bombarena.debug", usage="debug")
    public boolean toggleDebug(CommandSender sender) {
        if (plugin.debug instanceof DebugOn) {
            plugin.debug = new DebugOff(plugin);
            plugin.getConfig().set("Debug", false);
            plugin.saveConfig();
            sender.sendMessage("Debugging mode for the BombArena has been turned off.");
            return true;
        } else if (plugin.debug instanceof DebugOff) {
            plugin.debug = new DebugOn(plugin);
            plugin.getConfig().set("Debug", true);
            plugin.saveConfig();
            sender.sendMessage("Debugging mode for the BombArena has been turned on.");
            return true;
        }
        return false;
    }
    
}
