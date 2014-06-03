package mc.euro.extraction;

import java.util.logging.Level;
import java.util.logging.Logger;
import mc.alk.arena.BattleArena;
import mc.euro.extraction.debug.*;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Nikolai
 */
public class HostagePlugin extends JavaPlugin {
    
    DebugInterface debug;

    @Override
    public void onEnable() {
        debug = new DebugOn(this);
        // getServer().getPluginManager().registerEvents(new HostageArena(), this);
        registerArena();
        // CustomEntityType.registerEntities();
        registerEntites();
        
    }

    @Override
    public void onDisable() {
        super.onDisable();
        // CustomEntityType.unregisterEntities();
        unregisterEntities();
    }
    
    public Class<?> getNmsClass(String clazz) throws Exception {
        String mcVersion = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        return Class.forName("mc.euro.extraction.nms." + mcVersion + "." + clazz);
    }
    
    private void registerArena() {
        try {
            // BattleArena.registerCompetition(this, "HostageArena", "vips", getNmsClass("HostageArena"), new BombExecutor());
            getServer().getPluginManager().registerEvents(
                    (Listener) getNmsClass("NpcListener").getConstructor().newInstance(), this);
        } catch (Exception ex) {
            Logger.getLogger(HostagePlugin.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void registerEntites() {
        try {
            getNmsClass("CustomEntityType").getDeclaredMethod("registerEntities").invoke(null);
        } catch (Exception ex) {
            Logger.getLogger(HostagePlugin.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void unregisterEntities() {
        try {
            getNmsClass("CustomEntityType").getDeclaredMethod("unregisterEntities").invoke(null);
        } catch (Exception ex) {
            Logger.getLogger(HostagePlugin.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
