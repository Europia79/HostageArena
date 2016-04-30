package mc.euro.extraction.factory;

import mc.alk.arena.BattleArena;
import mc.alk.arena.executors.CustomCommandExecutor;
import mc.alk.arena.objects.arenas.Arena;
import mc.alk.arena.objects.arenas.ArenaFactory;
import mc.euro.extraction.api.ExtractionPlugin;
import mc.euro.extraction.arenas.HostageArena;
import mc.euro.extraction.nms.NPCFactory;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * 
 * 
 * @author Nikolai
 */
public class HostageArenaFactory implements ArenaFactory {
    
    ExtractionPlugin plugin;
    NPCFactory npcFactory;
    
    public HostageArenaFactory(ExtractionPlugin reference) {
        this.plugin = reference;
        this.npcFactory = NPCFactory.newInstance(plugin);
    }
    
    public HostageArenaFactory(ExtractionPlugin reference, NPCFactory npcFactory) {
        this.plugin = reference;
        this.npcFactory = npcFactory;
    }

    @Override
    public Arena newArena() {
        Arena arena = new HostageArena(plugin, npcFactory);
        return arena;
    }
    
    /**
     * Wrapper method to allow servers to use older versions of BattleArena.
     * Works by shielding other classes from the ArenaFactory import.
     * Any classes that have this import would break on old BA versions.
     * This class is invoked at runtime only if a newer version of BA is installed.
     */
    public static void registerCompetition(JavaPlugin jplugin, String name, String cmd, Class<? extends HostageArena> clazz, CustomCommandExecutor executor) {
        ArenaFactory factory = new HostageArenaFactory((ExtractionPlugin) jplugin);
        BattleArena.registerCompetition(jplugin, name, cmd, factory, executor);
    }
    
}
