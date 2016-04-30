package mc.euro.extraction.events;

import mc.alk.arena.competition.match.Match;
import mc.alk.arena.objects.arenas.Arena;
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
    private final Arena arena;
    private final Match match;
    
    public ExtractionTimerEvent(Arena arena, ExtractionTimer etimer) {
        this.arena = arena;
        this.match = this.arena.getMatch();
        this.timer = etimer;
    }
    
    public int getTime() {
        return timer.getTime();
    }
    
    public Arena getArena() {
        return this.arena;
    }
    
    public Match getMatch() {
        return this.match;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
    
}
