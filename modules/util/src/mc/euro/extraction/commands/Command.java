package mc.euro.extraction.commands;

import mc.euro.extraction.util.VersionFormat;

/**
 * This class handles backwards compatibility for different 
 * versions of BattleArena commands. <br/><br/>
 * 
 * <pre>
 * 
 * Version - Syntax
 * 
 * +396200 - /aa addspawn {block} fs=1 rs=500 ds=500 index=1
 * -395850 - /aa addspawn {block} fs=1 rs=500 ds=500 1
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
                + " rs=" + duration
                + " ds=" + duration
                + " index=" + index;
        String cmd2 = "aa addspawn VILLAGER"
                + " fs=0"
                + " rs=" + duration
                + " ds=" + duration
                + " " + index;
        String cmd = (getVersion() >= 396000) ? cmd1 : cmd2;
        return cmd;
    }
    
    private static int getVersion() {
        return VersionFormat.getBAversion();
    }
    

}
