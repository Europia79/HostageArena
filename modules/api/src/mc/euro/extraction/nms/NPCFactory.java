package mc.euro.extraction.nms;

import java.lang.reflect.Constructor;
import mc.euro.extraction.api.IHostagePlugin;
import mc.euro.version.VersionFactory;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Villager.Profession;

/**
 * 
 * 
 * @author Nikolai
 */
public abstract class NPCFactory {
    
    protected IHostagePlugin plugin;
    protected static String NMS;
    
    /**
     * The NPCFactory has a different implementation for each version of Minecraft.
     */
    public static NPCFactory newInstance(IHostagePlugin plugin) {
        NMS = VersionFactory.getNmsPackage();
        Class<?>[] args = {IHostagePlugin.class};
        Constructor con = null;
        NPCFactory factory = null;
        try {
            con = getNmsClass("HostageFactory").getConstructor(args);
            factory = (NPCFactory) con.newInstance(plugin);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return factory;
    }
    
    public static Class<?> getNmsClass(String clazz) throws Exception {
        return Class.forName("mc.euro.extraction.nms." + NMS + "." + clazz);
    }
    
    /**
     * The factory made it, so the factory should know if it's a Hostage or not.
     */
    public abstract boolean isHostage(Entity entity);
    /**
     * If the Entity is not a hostage, then it will get replaced with one.
     */
    public abstract Hostage getHostage(Entity entity);
    public abstract Hostage spawnHostage(Location loc);
    public abstract Hostage spawnHostage(Location loc, Profession prof);
    
}
