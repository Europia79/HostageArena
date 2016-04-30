package mc.euro.extraction.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static mc.alk.arena.executors.BaseExecutor.sendMessage;
import mc.alk.arena.executors.CustomCommandExecutor;
import mc.alk.arena.executors.MCCommand;
import mc.alk.arena.objects.arenas.Arena;
import mc.alk.arena.objects.spawns.EntitySpawn;
import mc.alk.arena.objects.spawns.TimedSpawn;
import mc.alk.arena.serializers.ArenaSerializer;
import mc.euro.extraction.api.ExtractionPlugin;
import mc.euro.extraction.arenas.ExtractionArena;
import mc.euro.extraction.debug.*;
import mc.euro.extraction.nms.Hostage;
import mc.euro.extraction.nms.NPCFactory;
import mc.euro.extraction.util.ArenaUtil;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Nikolai
 */
public abstract class ExtractionExecutor extends CustomCommandExecutor {
    
    protected ExtractionPlugin plugin;
    
    public ExtractionExecutor() {
        this.plugin = (ExtractionPlugin) Bukkit.getPluginManager().getPlugin("HostageArena");
    }
    
    public ExtractionExecutor(ExtractionPlugin plugin) {
        this.plugin = plugin;
    }
    
    @MCCommand(cmds={"list"},subCmds={"extractionpoint","extractionpoints"}, perm="vips.extractionpoints.list")
    public boolean listExtractionPoints(CommandSender sender, Arena a) {
        if (!(a instanceof ExtractionArena)) {
            sender.sendMessage("" + a.getName() + " is not an ExtractionArena");
            return false;
        }
        ExtractionArena arena = (ExtractionArena) a;
        List<Location> locs = arena.getExtractionPoints();
        if (locs.isEmpty()) {
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

    @MCCommand(cmds={"set"}, subCmds={"extractionpoint"}, perm="vips.extractionpoints.set")
    public boolean setExtractionPoint(Player sender, Arena arena) {
        clearExtractionPoints(sender, arena);
        addExtractionPoint(sender, arena);
        return true;
    }
    
    @MCCommand(cmds={"clear"}, subCmds={"extractionpoints"}, perm="vips.extractionpoints.clear")
    public boolean clearExtractionPoints(CommandSender sender, Arena a) {
        // path = arenas.{arenaName}.persistables.epoints
        if (!(a instanceof ExtractionArena)) {
            sender.sendMessage("" + a.getName() + " is not a valid ExtractionArena.");
            return false;
        }
        ExtractionArena arena = (ExtractionArena) a;
        arena.clearExtractionPoints();
        String msg = "All extraction points for arena " + a.getName() + " have been deleted.";
        sender.sendMessage(msg);
        ac.updateArena(arena);
        saveAllArenas();
        return true;
    }

    @MCCommand(cmds = {"add"}, subCmds = {"extractionpoint"}, perm="vips.extractionpoints.add")
    public boolean addExtractionPoint(Player sender, Arena a) {
        
        if (!(a instanceof ExtractionArena)) {
            sender.sendMessage("Arena must be a valid ExtractionArena.");
            return false;
        }
        ExtractionArena arena = (ExtractionArena) a;
        Location loc = sender.getLocation();
        arena.addExtractionPoint(loc);
        sender.sendMessage("Extraction Point added to arena: " + a.getName());
        ac.updateArena(arena);
        saveAllArenas();
        return true;
    }
    
    public boolean addExtractionZone(Player sender, Arena a) {
        /*
        ExtractionArena arena = (ExtractionArena) arena;
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
    
    @MCCommand(cmds = {"list"}, subCmds = {"allspawns"}, perm = "vips.spawns.list")
    public boolean listAllSpawns(CommandSender sender, Arena arena) {
        sendMessage(sender, ChatColor.GREEN + "All spawns for &6" + arena.getName());
        Map<Long, TimedSpawn> spawns = arena.getTimedSpawns();
        if (spawns == null || spawns.isEmpty()) {
            return sendMessage(sender, ChatColor.RED + "Arena has no spawns");
        }
        List<Long> keys = new ArrayList<Long>(spawns.keySet());
        Collections.sort(keys);
        for (Long k : keys) {
            sendMessage(sender, "&5" + k + "&e: " + spawns.get(k).getDisplayName());
        }
        return true;
    }
    
    @MCCommand(cmds={"list"}, subCmds={"spawns", "hostageSpawns", "hostages"}, perm="vips.spawns.list")
    public boolean listHostageSpawns(CommandSender sender, Arena arena) {
        sendMessage(sender, ChatColor.GREEN + "All hostage spawns for &6" + arena.getName());
        Map<Long, EntitySpawn> spawns = ArenaUtil.getHostageSpawns(arena);
        if (spawns == null || spawns.isEmpty()) {
            return sendMessage(sender, ChatColor.RED + "Arena has no spawns");
        }
        List<Long> keys = new ArrayList<Long>(spawns.keySet());
        Collections.sort(keys);
        for (Long k : keys) {
            sendMessage(sender, "&5" + k + "&e: " + spawns.get(k).getEntityString());
        }
        return true;
    }
    
    @MCCommand(cmds={"set"}, subCmds={"spawn"}, perm="vips.spawns.set")
    public boolean setHostageSpawn(Player sender, Arena arena) {
        if (clearHostageSpawns(sender, arena)) {
            return addHostageSpawn(sender, arena);
        }
        return false;
    }
    
    /**
     * An Arena may have Spawns for 1,2,3,4,5: But only some of these might
     * actually be Hostage spawns. So, we just wanna clear only the Villager spawns.
     * 
     * - get the Arena's timedSpawns map
     * - remove hostages from the map
     * - make a copy of the timedSpawns map
     * - clear the timedSpawns map
     * - add the copy back to timedSpawns
     * 
     * This will ensure that if we remove index 3, 
     * then the map will be re-ordered 1,2,3,4 instead of 1,2,4,5.
     */
    @MCCommand(cmds={"clear"}, subCmds={"spawns", "hostageSpawns"}, perm="vips.spawns.clear")
    public boolean clearHostageSpawns(CommandSender sender, Arena a) {
        if (!(a instanceof ExtractionArena)) {
            sender.sendMessage(a.getName() + " is not a HostageArena or VipArena.");
            return false;
        }
        Map<Long, TimedSpawn> timedSpawns = a.getTimedSpawns();
        Map<Long, EntitySpawn> vipSpawns = ArenaUtil.getHostageSpawns(a);
        
        for (Long key : vipSpawns.keySet()) {
            timedSpawns.remove(key);
        }
        Map<Long, TimedSpawn> copy = new LinkedHashMap<Long, TimedSpawn>(timedSpawns);
        timedSpawns.clear();
        
        long index = 1L; // start at index 1
        for (Long k : copy.keySet()) {
            timedSpawns.put(index, copy.get(k));
            index = index + 1L;
        }
        ac.updateArena(a);
        saveAllArenas();
        return true;
    }
    
    @MCCommand(cmds={"add"}, subCmds={"spawn","hostageSpawn", "hs"}, perm="vips.spawns.add")
    public boolean addHostageSpawn(Player sender, Arena a) {
        if (!(a instanceof ExtractionArena)) {
            sender.sendMessage("Not a valid ExtractionArena.");
            sender.sendMessage("Arena must be a HostageArena or VipArena.");
            return false;
        }
        ExtractionArena arena = (ExtractionArena) a;
        Map<Long, TimedSpawn> smap = arena.getTimedSpawns();
        int index = smap.size() + 1;
        
        String selectArena = "aa select " + arena.getName();
        plugin.getServer().dispatchCommand(sender, selectArena);
        plugin.getServer().dispatchCommand(sender, Command.addspawn(index));
        
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
    
    @MCCommand(cmds={"set"}, subCmds={"hostages2win"}, perm="vips.set.hostages2win")
    public boolean setHostagesToWin(CommandSender sender, Arena a, Integer x) {
        if (!(a instanceof ExtractionArena)) {
            sender.sendMessage("Arena " + a.getName() + " is not a HostageArena or VipArena");
            return false;
        }
        ExtractionArena arena = (ExtractionArena) a;
        arena.setNumberOfHostagesNeededToWin(x);
        sender.sendMessage("Arena " + a.getName() + " now requires " + x + " hostages rescued to win.");
        ac.updateArena(arena);
        saveAllArenas();
        return true;
    }
    
    @MCCommand(cmds={"set"}, subCmds={"hostages2lose"}, perm="vips.set.hostages2lose")
    public boolean setHostagesToLose(CommandSender sender, Arena a, Integer x) {
        if (!(a instanceof ExtractionArena)) {
            sender.sendMessage("Arena " + a.getName() + " is not a HostageArena or VipArena.");
            return false;
        }
        ExtractionArena arena = (ExtractionArena) a;
        arena.setNumberOfHostageDeathsToLose(x);
        sender.sendMessage("Arena " + a.getName() + " now requires " + x + " hostage deaths to lose.");
        ac.updateArena(arena);
        saveAllArenas();
        return true;
    }
    
    @MCCommand(cmds={"set"}, subCmds={"hostageVulnerability","hostageDamage","hdmg"}, perm="vips.set.hostagedamage")
    public boolean setHostageVulnerability(CommandSender sender, Arena a, Boolean b) {
        if (!(a instanceof ExtractionArena)) {
            sender.sendMessage("Arena " + a.getName() + " is not a HostageArena or VipArena.");
            return false;
        }
        ExtractionArena arena = (ExtractionArena) a;
        arena.AllowPlayersToKillHostages = b;
        if (b) {
            sender.sendMessage("Players are now able to kill hostages in arena " + a.getName());
        } else {
            sender.sendMessage("Players are now unable to kill hostages in arena " + a.getName());
        }
        ac.updateArena(arena);
        saveAllArenas();
        return true;
    }
    
    /**
     * Toggles debug mode ON / OFF. <br/>
     * Usage: /bomb debug
     */
    @MCCommand(cmds={"debug"}, perm="vips.debug", usage="debug")
    public boolean toggleDebug(CommandSender sender) {
        if (plugin.debug() instanceof DebugOn) {
            plugin.setDebugging(false);
            sender.sendMessage("Debugging mode for HostageArenas has been turned off.");
            return true;
        } else {
            plugin.setDebugging(true);
            sender.sendMessage("Debugging mode for HostageArenas has been turned on.");
            return true;
        }
    }
    
    /**
     * DebugOn = verbose.
     * DebugOff = silent.
     */
    protected void saveAllArenas() {
        boolean verbose = plugin.debug() instanceof DebugOn;
        ArenaSerializer.saveAllArenas(verbose);
    }
    
}
