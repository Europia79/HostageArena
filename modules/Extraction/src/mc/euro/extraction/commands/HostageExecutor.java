package mc.euro.extraction.commands;

import java.util.List;
import mc.alk.arena.BattleArena;
import mc.alk.arena.executors.CustomCommandExecutor;
import mc.alk.arena.executors.MCCommand;
import mc.alk.arena.objects.arenas.Arena;
import mc.euro.extraction.HostageArena;
import mc.euro.extraction.api.IHostagePlugin;
import mc.euro.extraction.debug.*;
import mc.euro.extraction.nms.Hostage;
import mc.euro.extraction.nms.NPCFactory;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

/**
 *
 * @author Nikolai
 */
public class HostageExecutor extends CustomCommandExecutor {
    
    IHostagePlugin plugin;
    
    public HostageExecutor(IHostagePlugin p) {
        this.plugin = p;
    }
    
    @MCCommand(cmds={"list"},subCmds={"extractionpoint","extractionpoints"}, op=true)
    public boolean listExtractionPoints(CommandSender sender, Arena a) {
        if (!(a instanceof HostageArena)) {
            sender.sendMessage("" + a.getName() + " is not a HostageArena");
            return false;
        }
        HostageArena arena = (HostageArena) a;
        List<Location> locs = arena.getExtractionPoints();
        if (locs.size() == 0) {
            sender.sendMessage("This arena (" + a.getName() + ") does not have any extraction points");
            return true;
        }
        sender.sendMessage("Extraction points for arena " + a.getName());
        for (int i = 0; i < locs.size(); i++) {
            String msg = i + ". " + locs.get(i).toString();
            sender.sendMessage(msg);
        }
        return true;
    }

    @MCCommand(cmds={"set"}, subCmds={"extractionpoint"}, op = true)
    public boolean setExtractionPoint(Player sender, Arena arena) {
        clearExtractionPoints(sender, arena);
        addExtractionPoint(sender, arena);
        return true;
    }
    
    @MCCommand(cmds={"clear"}, subCmds={"extractionpoints"},op=true)
    public boolean clearExtractionPoints(CommandSender sender, Arena a) {
        // path = arenas.{arenaName}.persistables.epoints
        if (!(a instanceof HostageArena)) {
            sender.sendMessage("" + a.getName() + " is not a valid HostageArena.");
            return false;
        }
        HostageArena arena = (HostageArena) a;
        arena.clearExtractionPoints();
        BattleArena.saveArenas(plugin);
        String msg = "All extraction points for arena " + a.getName() + " have been deleted.";
        return true;
    }

    @MCCommand(cmds = {"add"}, subCmds = {"extractionpoint"}, op = true)
    public boolean addExtractionPoint(Player sender, Arena a) {
        
        if (!(a instanceof HostageArena)) {
            sender.sendMessage("Arena must be a valid HostageArena.");
            return false;
        }
        HostageArena arena = (HostageArena) a;
        Location loc = sender.getLocation();
        arena.addExtractionPoint(loc);
        BattleArena.saveArenas(plugin);
        sender.sendMessage("Extraction Point added to arena: " + a.getName());
        return true;
    }
    
    public boolean addExtractionZone(Player sender, Arena a) {
        /*
        HostageArena arena = (HostageArena) arena;
        if (Bukkit.getPluginManager().isPluginEnabled("WorldEdit")) {
            WorldEditPlugin wePlugin = (WorldEditPlugin) Bukkit
                    .getPluginManager().getPlugin("WorldEdit");
            Selection selection = wePlugin.getSelection(player);
            if (selection != null) {
                ExtractionZone zone = new ExtractionZone(
                        selection.getMinimumPoint(),
                        selection.getMaximumPoint(), sender.getLocation());
                arena.addExtractionZone(zone);
                BattleArena.saveArenas(plugin);
                String message = ChatColor.GREEN
                        + "Extraction zone has been added for arena "
                        + arena.getDisplayName() + "!";
                sender.sendMessage(message);
                return true;
            } else {
                sender.sendMessage(ChatColor.RED
                        + "You must have a WorldEdit selection with the two corners selected!");
            }
        } else {
            sender.sendMessage(ChatColor.RED
                    + "You must have WorldEdit enabled to use this feature!");
        }
        */
        sender.sendMessage("Command not implemented");
        return false;
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
    
    @MCCommand(cmds={"spawn"}, op=true)
    public boolean spawnHostage(Player sender) {
        return spawnHostage(sender, "VIP");
    }
    
    @MCCommand(cmds={"spawn"}, op=true)
    public boolean spawnHostage(Player sender, String name) {
        Location loc = sender.getLocation();
        NPCFactory factory = NPCFactory.newInstance(plugin);
        Hostage hostage = factory.spawnHostage(loc);
        hostage.setCustomName(name);
        sender.sendMessage("Hostage named " + name + " has been spawned.");
        return true;
    }
    
    /**
     * Toggles debug mode ON / OFF. <br/>
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
