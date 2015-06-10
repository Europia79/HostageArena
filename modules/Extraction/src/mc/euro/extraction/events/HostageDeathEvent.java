package mc.euro.extraction.events;

import mc.alk.arena.objects.ArenaPlayer;
import mc.euro.extraction.nms.Hostage;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * 
 * 
 * @author Nikolai
 */
public class HostageDeathEvent extends Event {
    
    private static final HandlerList handlers = new HandlerList();
    Hostage hostage;
    ArenaPlayer killer;
    
    public HostageDeathEvent(Hostage hostage, ArenaPlayer killer) {
        this.hostage = hostage;
        this.killer = killer;
    }
    
    public Hostage getHostage() {
        return this.hostage;
    }
    
    public ArenaPlayer getKiller() {
        return this.killer;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
    
}
