package mc.euro.extraction.arenas;

import mc.alk.arena.objects.ArenaPlayer;
import mc.euro.extraction.api.ExtractionPlugin;
import mc.euro.extraction.nms.NPCFactory;


/**
 * VipArena: Either team can rescue/capture the VIPs.
 * 
 * Game can end in a tie.
 * 
 * @author Nikolai
 */
public class VipArena extends ExtractionArena {
    
    /**
     * Pre-BattleArena v3.9.8 constructor to support backwards compatibility.
     */
    public VipArena() {
        super();
    }
    
    /**
     * This constructor requires BattleArena v3.9.8+.
     */
    public VipArena(ExtractionPlugin plugin, NPCFactory npcFactory) {
        super(plugin, npcFactory);
    }
    
    @Override
    public boolean canInteract(ArenaPlayer ap) {
        return true;
    }
    
    @Override
    public boolean canRescue(ArenaPlayer ap) {
        return true;
    }

}
