package mc.euro.extraction.events;

import mc.euro.extraction.nms.Hostage;
import org.bukkit.entity.Player;
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
    
    public HostageExtractedEvent(Hostage h) {
        this.hostage = h;
    }
    
    public Hostage getHostage() {
        return this.hostage;
    }
    
    public Player getRescuer() {
        return this.hostage.getRescuer();
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
