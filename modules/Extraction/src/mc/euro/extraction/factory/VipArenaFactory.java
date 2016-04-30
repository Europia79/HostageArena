package mc.euro.extraction.factory;

import mc.alk.arena.BattleArena;
import mc.alk.arena.executors.CustomCommandExecutor;
import mc.alk.arena.objects.arenas.Arena;
import mc.alk.arena.objects.arenas.ArenaFactory;
import mc.euro.extraction.api.ExtractionPlugin;
import mc.euro.extraction.arenas.VipArena;
import mc.euro.extraction.nms.NPCFactory;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * 
 * 
 * @author Nikolai
 */
public class VipArenaFactory implements ArenaFactory {
    
    ExtractionPlugin plugin;
    NPCFactory npcFactory;
    
    public VipArenaFactory(ExtractionPlugin reference) {
        this.plugin = reference;
        this.npcFactory = NPCFactory.newInstance(plugin);
    }
    
    public VipArenaFactory(ExtractionPlugin reference, NPCFactory npcFactory) {
        this.plugin = reference;
        this.npcFactory = npcFactory;
    }

    @Override
    public Arena newArena() {
        Arena arena = new VipArena(plugin, npcFactory);
        return arena;
    }
    
    /**
     * Wrapper method to allow servers to use older versions of BattleArena.
     * Works by shielding other classes from the ArenaFactory import.
     * Any classes that have this import would break on old BA versions.
     * This class is invoked at runtime only if a newer version of BA is installed.
     */
    public static void registerCompetition(JavaPlugin jplugin, String name, String cmd, Class<? extends VipArena> clazz, CustomCommandExecutor executor) {
        ArenaFactory factory = new VipArenaFactory((ExtractionPlugin) jplugin);
        BattleArena.registerCompetition(jplugin, name, cmd, factory, executor);
    }
    
}
