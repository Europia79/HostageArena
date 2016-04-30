package mc.euro.extraction.util;

import java.util.LinkedHashMap;
import java.util.Map;

import mc.alk.arena.objects.arenas.Arena;
import mc.alk.arena.objects.spawns.EntitySpawn;
import mc.alk.arena.objects.spawns.SpawnInstance;
import mc.alk.arena.objects.spawns.TimedSpawn;

/**
 *
 * @author Nikolai
 */
public class ArenaUtil {

    public static Map<Long, EntitySpawn> getHostageSpawns(Arena arena) {
        Map<Long, TimedSpawn> smap = arena.getTimedSpawns();
        Map<Long, EntitySpawn> emap = new LinkedHashMap<Long, EntitySpawn>();
        Long key = 0L;
        for (TimedSpawn ts : smap.values()) {
            key = key + 1L;
            SpawnInstance si = ts.getSpawn();
            if (si instanceof EntitySpawn) {
                EntitySpawn es = (EntitySpawn) si;
                if (es.getEntityString().equalsIgnoreCase("VILLAGER")) {
                    emap.put(key, es);
                }
            }
        }
        return emap;
    }

}
