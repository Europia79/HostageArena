package mc.euro.extraction.nms;

import java.lang.reflect.Constructor;
import mc.euro.extraction.api.SuperPlugin;
import mc.euro.version.VersionFactory;
import org.bukkit.entity.Entity;

/**
 * 
 * 
 * @author Nikolai
 */
public abstract class NPCFactory {
    
    protected SuperPlugin plugin;
    protected static String NMS;
    
    public static NPCFactory newInstance(SuperPlugin plugin) {
        NMS = VersionFactory.getNmsVersion().toString();
        Class<?>[] args = {SuperPlugin.class};
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
    
    public abstract Hostage getHostage(Entity E);
    
}
