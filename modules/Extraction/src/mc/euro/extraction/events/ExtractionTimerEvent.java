package mc.euro.extraction.events;

import mc.euro.extraction.timers.ExtractionTimer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * 
 * 
 * @author Nikolai
 */
public class ExtractionTimerEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final ExtractionTimer timer;
    
    public ExtractionTimerEvent(ExtractionTimer etimer) {
        this.timer = etimer;
    }
    
    public int getTime() {
        return timer.getTime();
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
    
}
