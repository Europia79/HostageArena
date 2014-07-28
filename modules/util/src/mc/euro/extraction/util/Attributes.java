package mc.euro.extraction.util;

import java.util.List;
import java.util.Random;
import mc.euro.extraction.api.SuperPlugin;
import org.bukkit.entity.Villager.Profession;

/**
 *
 * @author Nikolai
 */
public class Attributes {
    
    private static int tCounter = 0;
    private static int nCounter = 0;
    
    public static Profession getType(SuperPlugin plugin) {
        return getType(plugin, "HostageTypes");
    }
    
    private static Profession getType(SuperPlugin plugin, String path) {
        Profession p = Profession.FARMER;
        Attributes.tCounter = Attributes.tCounter + 1;
        List<String> professions = plugin.getConfig().getStringList(path);
        if (professions != null && professions.size() > 0 && Attributes.tCounter <= professions.size()) {
            try {
                p = Profession.valueOf(professions.get(tCounter - 1));
            } catch (IllegalArgumentException ex) {
                p = getRandomType();
            }
            if (Attributes.tCounter >= professions.size()) Attributes.tCounter = 0;
        }
        return p;
    }
    
    private static Profession getRandomType() {
        return Profession.getProfession(new Random().nextInt(5));
    }
    
    public static String getName(SuperPlugin plugin) {
        return getName(plugin, "HostageNames");
    }
    
    private static String getName(SuperPlugin plugin, String path) {
        String name = "VIP";
        Attributes.nCounter = Attributes.nCounter + 1;
        List<String> names = plugin.getConfig().getStringList(path);
        if (names != null && names.size() > 0 && Attributes.nCounter <= names.size()) {
            name = names.get(nCounter - 1);
            if (Attributes.nCounter >= names.size()) Attributes.nCounter = 0;
        }
        return name;
    }
}
