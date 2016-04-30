package mc.euro.extraction.commands;

import mc.euro.extraction.v2.Version;
import mc.euro.extraction.v2.VersionFactory;

import org.bukkit.plugin.Plugin;

/**
 * This class handles backwards compatibility for different 
 * versions of BattleArena commands. <br/><br/>
 * 
 * <pre>
 * 
 * Version - Syntax
 * 
 * +3.9.6.2.0 - /aa addspawn {block} fs=1 rs=500 ds=500 index=1
 * -3.9.5.8.5 - /aa addspawn {block} fs=1 rs=500 ds=500 1
 * 
 * </pre>
 * 
 * @author Nikolai
 */
public abstract class Command {
    
    /**
     * Not yet implemented in BattleArena, hence why it's private. <br/><br/>
     * @return 
     */
    private static String addspawn() {
        String cmd3 = "aa addspawn VILLAGER"
                + " fs=0"
                + " rs=0"
                + " ds=-1";
        return cmd3;
    }
    
    public static String addspawn(int index) {
        return addspawn(index, -1);
    }
    
    public static String addspawn(int index, int duration) {
        // /aa addspawn VILLAGER 3 fs=1 rs=500 ds=500 index=1
        // /aa addspawn VILLAGER 3 fs=1 rs=500 ds=500 1
        String cmd1 = "aa addspawn VILLAGER"
                + " fs=0"
                + " rs=0"
                + " ds=" + duration
                + " index=" + index;
        String cmd2 = "aa addspawn VILLAGER"
                + " fs=0"
                + " rs=0"
                + " ds=" + duration
                + " " + index;
        Version<Plugin> v = VersionFactory.getPluginVersion("BattleArena");
        String cmd = v.isCompatible("3.9.6.2") ? cmd1 : cmd2;
        return cmd;
    }
    
}
