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
public class HostageExtractedEvent extends Event {
    
    private static final HandlerList handlers = new HandlerList();
    Hostage hostage;
    ArenaPlayer rescuer;
    
    public HostageExtractedEvent(Hostage h, ArenaPlayer rescuer) {
        this.hostage = h;
        this.rescuer = rescuer;
    }
    
    public Hostage getHostage() {
        return this.hostage;
    }
    
    public ArenaPlayer getRescuer() {
        return this.rescuer;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
