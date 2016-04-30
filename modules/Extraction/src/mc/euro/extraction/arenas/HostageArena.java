package mc.euro.extraction.arenas;

import java.util.List;
import java.util.Map;

import mc.alk.arena.events.matches.MatchResultEvent;
import mc.alk.arena.objects.ArenaPlayer;
import mc.alk.arena.objects.CompetitionResult;
import mc.alk.arena.objects.MatchResult;
import mc.alk.arena.objects.events.ArenaEventHandler;
import mc.alk.arena.objects.spawns.TimedSpawn;
import mc.alk.arena.objects.teams.ArenaTeam;
import mc.alk.arena.serializers.Persist;
import mc.euro.extraction.api.ExtractionPlugin;
import mc.euro.extraction.nms.NPCFactory;

import org.bukkit.Location;

/**
 * HostageArena: One team guards the hostages, while the other team rescues them.
 * 
 * Game cannot end in a tie:
 * If time expires, the guards win.
 * 
 * @author Nikolai
 */
public class HostageArena extends ExtractionArena {
    
    ArenaTeam attackers; // The team trying to rescue the hostages.
    ArenaTeam defenders; // The team trying to guard the hostages.
    
    @Persist public boolean AllowGuardsToInteractWithHostages = true;
    
    /**
     * Pre-BattleArena v3.9.8 constructor to support backwards compatibility.
     */
    public HostageArena() {
        super();
    }
    
    /**
     * This constructor requires BattleArena v3.9.8+.
     */
    public HostageArena(ExtractionPlugin plugin, NPCFactory npcFactory) {
        super(plugin, npcFactory);
    }
    
    @Override
    public boolean canInteract(ArenaPlayer ap) {
        if (!isGuard(ap)) {
            return true;
        } else {
            return AllowGuardsToInteractWithHostages;
        }
    }
    
    private boolean isGuard(ArenaPlayer ap) {
        return ap.getTeam().getId() == defenders.getId();
    }
    
    @Override
    public boolean canRescue(ArenaPlayer ap) {
        return ap.getTeam().getId() == attackers.getId();
    }
    
    @ArenaEventHandler (needsPlayer = false)
    public void onMatchResult(MatchResultEvent e) {
        plugin.debug().log("MatchResultEvent called.");
        CompetitionResult result = e.getMatchResult();
        if (result.isDraw()) {
            MatchResult newResult = new MatchResult();
            newResult.addLoser(attackers);
            newResult.setVictor(defenders);
            getMatch().setMatchResult(newResult);
        }
    }
    
    @Override
    public void onStart() {
        super.onStart();
        plugin.debug().log("HostageArena onStart() called.");
        assignTeams(getTeams());
    }
    
    /**
     * This is called from onStart() and assigns teams to be attackers/defenders.
     * <br/><br/>
     */
    public void assignTeams(List<ArenaTeam> bothTeams) {
        
        Map<Long, TimedSpawn> spawns = getTimedSpawns();
        Location hostageSpawnLoc = spawns.get(1L).getSpawn().getLocation();
        
        ArenaTeam team1 = null;
        ArenaTeam team2;
        for (ArenaTeam t : bothTeams) {
            team1 = t;
            break;
        }
        team2 = getOtherTeam(team1);
        
        double distance1 = getMatch().getTeamSpawn(team1, false).getLocation().distance(hostageSpawnLoc);
        double distance2 = getMatch().getTeamSpawn(team2, false).getLocation().distance(hostageSpawnLoc);

        if (distance1 < distance2) {
            this.defenders = team1;
            this.attackers = team2;
        } else {
            this.defenders = team2;
            this.attackers = team1;
        }
        this.defenders.sendMessage("Guard the hostages to win!");
        this.attackers.sendMessage("Rescue the hostages to win!");
    }  // END OF assignTeams()

}
