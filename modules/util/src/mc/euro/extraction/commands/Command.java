package mc.euro.extraction.commands;

import mc.euro.extraction.util.BukkitVersion;

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

    public static String addspawn(int duration, int index) {
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
        BukkitVersion v = BukkitVersion.getVersion("BattleArena");
        String cmd = v.isCompatible("3.9.6.2") ? cmd1 : cmd2;
        return cmd;
    }
    
    /**
     * Not yet implemented in BattleArena, hence why it's private. <br/><br/>
     * @param duration The match duration.
     * @return 
     */
    private static String addSpawn(int duration) {
        String cmd3 = "aa addspawn VILLAGER"
                + " fs=0"
                + " rs=0"
                + " ds=" + duration;
        return cmd3;
    }
}
