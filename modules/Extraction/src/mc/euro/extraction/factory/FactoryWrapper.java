package mc.euro.extraction.factory;

import mc.alk.arena.BattleArena;
import mc.alk.arena.executors.CustomCommandExecutor;
import mc.euro.extraction.HostageArena;
import mc.euro.extraction.api.IHostagePlugin;
import mc.euro.extraction.nms.NPCFactory;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * ArenaFactory may not exist at runtime.
 * 
 * @author Nikolai
 */
public class FactoryWrapper {
    
    IHostagePlugin plugin;
    NPCFactory npcFactory;
    int hitpoints;
    
    public FactoryWrapper(IHostagePlugin reference, NPCFactory npcFactory, int vipHitpoints) {
        this.plugin = reference;
        this.npcFactory = npcFactory;
        this.hitpoints = vipHitpoints;
    }
    
    /**
     * Wrapper method to allow servers to use older versions of BattleArena.
     * Works by shielding other classes from the ArenaFactory import.
     * Any classes that have this import would break on old BA versions.
     * This class is invoked at runtime only if a newer version of BA is installed.
     */
    public void registerCompetition(JavaPlugin jplugin, String name, String cmd, Class<? extends HostageArena> clazz, CustomCommandExecutor executor) {
        HostageArenaFactory arenaFactory = new HostageArenaFactory(plugin, npcFactory, hitpoints);
        BattleArena.registerCompetition(jplugin, name, cmd, arenaFactory, executor);
    }
    
}
