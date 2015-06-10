package mc.euro.extraction.factory;

import mc.alk.arena.objects.arenas.Arena;
import mc.alk.arena.objects.arenas.ArenaFactory;
import mc.euro.extraction.HostageArena;
import mc.euro.extraction.api.IHostagePlugin;
import mc.euro.extraction.nms.NPCFactory;

/**
 * 
 * 
 * @author Nikolai
 */
public class HostageArenaFactory implements ArenaFactory {
    
    IHostagePlugin plugin;
    NPCFactory factory;
    int hitpoints;
    
    public HostageArenaFactory(IHostagePlugin reference, NPCFactory npcFactory, int vipHitpoints) {
        this.plugin = reference;
        this.factory = npcFactory;
        this.hitpoints = vipHitpoints;
    }

    @Override
    public Arena newArena() {
        Arena arena = new HostageArena(plugin, factory, hitpoints);
        return arena;
    }
    
}
