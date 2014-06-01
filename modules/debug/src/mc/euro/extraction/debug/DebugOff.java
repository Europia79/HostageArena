package mc.euro.extraction.debug;

import java.util.Set;
import mc.alk.arena.objects.ArenaPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * debug = new DebugOff(); will toggle debugging mode OFF.
 */
public class DebugOff implements DebugInterface {
    
    JavaPlugin plugin;

    public DebugOff() {
    }

    public DebugOff(JavaPlugin reference) {
        this.plugin = reference;
    }
    
    @Override
    public void log(String m) {
        // Doesn't log because Debugging is OFF.
    }

    @Override
    public void sendMessage(Player p, String m) {
    }

    @Override
    public void msgArenaPlayers(Set<ArenaPlayer> players, String string) {
        
    }
    
}
