package mc.euro.extraction.nms.v1_7_R1;

import java.util.ArrayList;
import java.util.List;
import mc.alk.arena.BattleArena;
import mc.alk.arena.controllers.BattleArenaController;
import mc.alk.arena.executors.CustomCommandExecutor;
import mc.alk.arena.executors.MCCommand;
import mc.alk.arena.objects.arenas.Arena;
import mc.alk.arena.util.SerializerUtil;
import mc.euro.extraction.api.SuperPlugin;
import mc.euro.extraction.appljuze.CustomConfig;
import mc.euro.extraction.debug.*;
import mc.euro.extraction.commands.Command;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Nikolai
 */
public class HostageExecutor extends CustomCommandExecutor {
    
    SuperPlugin plugin;
    
    public HostageExecutor(SuperPlugin p) {
        this.plugin = p;
    }
    
    @MCCommand(cmds={"list"},subCmds={"extractionpoint","extractionpoints"}, op=true)
    public boolean listExtractionPoints(CommandSender sender, Arena arena) {
        return true;
    }
    
    @MCCommand(cmds={"set"},subCmds={"extractionpoint"}, op=true)
    public boolean setExtractionPoint(Player sender, Arena arena) {
        clearExtractionPoints(sender, arena);
        addExtractionPoint(sender, arena);
        return true;
    }
    @MCCommand(cmds={"add"},subCmds={"extractionpoint"}, op=true)
    public boolean addExtractionPoint(Player sender, Arena arena) {
        // path = arenas.{arena}.extractionpoints
        String path = "arenas." + arena.getName() + ".extractionpoints";
        List<String> locations = new ArrayList<String>();
        CustomConfig config = plugin.getConfig("arenas.yml");
        if (config.getStringList(path) != null) {
            locations = plugin.getConfig("arenas.yml").getStringList(path);
        }
        Location loc = sender.getLocation();
        String stringLocation = SerializerUtil.getLocString(loc);
        locations.add(stringLocation);
        config.set(path, locations);
        config.saveConfig();
        BattleArena.saveArenas(plugin);
        sender.sendMessage("Extraction point set!");
        return true;
    }
    
    @MCCommand(cmds={"clear"}, subCmds={"extractionpoints"},op=true)
    public boolean clearExtractionPoints(CommandSender sender, Arena arena) {
        // path = arenas.{arena}.extractionpoints
        CustomConfig config = plugin.getConfig("arenas.yml");
        String path = "arenas." + arena.getName() + ".extractionpoints";
        config.set(path, null);
        config.saveConfig();
        sender.sendMessage("All extraction points for this arena have been deleted.");
        return true;
    }
    
    @MCCommand(cmds={"setspawn"}, perm="hostagearena.vips.spawn")
    public boolean setHostageSpawn(Player sender, Arena arena) {
        plugin.debug().log("arena = " + arena.getName());
        int matchTime = arena.getParams().getMatchTime();
        
        plugin.debug().log("setHostageSpawn() MatchTime = " + matchTime);
        
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
        sender.sendMessage("Command not yet implemented.");
        return true;
    }
    
    /**
     * Toggles debug mode ON / OFF.
     * Usage: /bomb debug
     */
    @MCCommand(cmds={"debug"}, perm="bombarena.debug", usage="debug")
    public boolean toggleDebug(CommandSender sender) {
        if (plugin.debug() instanceof DebugOn) {
            plugin.setDebugging(false);
            sender.sendMessage("Debugging mode for the HostageArena has been turned off.");
            return true;
        } else {
            plugin.setDebugging(true);
            sender.sendMessage("Debugging mode for the HostageArena has been turned on.");
            return true;
        }
    }
    
}
