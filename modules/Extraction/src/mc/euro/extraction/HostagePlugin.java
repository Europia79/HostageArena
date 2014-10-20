package mc.euro.extraction;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import mc.alk.arena.BattleArena;
import mc.alk.arena.executors.CustomCommandExecutor;
import mc.euro.extraction.api.IHostagePlugin;
import mc.euro.extraction.appljuze.ConfigManager;
import mc.euro.extraction.appljuze.CustomConfig;
import mc.euro.extraction.commands.HostageExecutor;
import mc.euro.extraction.debug.*;
import mc.euro.extraction.util.Version;
import mc.euro.extraction.util.VersionFactory;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Nikolai
 */
public class HostagePlugin extends JavaPlugin implements IHostagePlugin {
    
    DebugInterface debug;
    Version server;
    public static final String MAX = "1.7.10-R9.9-SNAPSHOT";
    public static final String MIN = "1.2.5";
    String NMS;
    
    ConfigManager manager;

    @Override
    public void onEnable() {
        NMS = VersionFactory.getNmsVersion().toString();
        server = VersionFactory.getServerVersion();
        if (!server.isSupported(MAX) || !server.isCompatible(MIN)) {
            getLogger().info("VirtualPlayers is not compatible with your server.");
            getLogger().info("The maximum supported version is " + MAX);
            getLogger().info("The minimum capatible version is " + MIN);
            Bukkit.getServer().getPluginManager().disablePlugin(this);
            return;
        }
        saveDefaultConfig();
        manager = new ConfigManager(this);
        
        debug = new DebugOn(this);

        debug.log("HostageNames = " + getConfig().getStringList("HostageNames").toString());
        debug.log("HostageTypes = " + getConfig().getStringList("HostageTypes").toString());
        debug.log("HostageHP = " + getConfig().getInt("HostageHP", 3));
        debug.log("ExtractionTimer = " + getConfig().getInt("ExtractionTimer", 30));
        
        boolean b = getConfig().getBoolean("Debug");
        if (b) {
            debug = new DebugOn(this);
        } else {
            debug = new DebugOff(this);
        }
        // getServer().getPluginManager().registerEvents(new HostageArena(), this);
        // registerListeners();
        // registerArena();
        CustomCommandExecutor cmd = new HostageExecutor(this);
        BattleArena.registerCompetition(this, "HostageArena", "vips", HostageArena.class, cmd);
        // CustomEntityType.registerEntities();
        registerEntites();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        // CustomEntityType.unregisterEntities();
        unregisterEntities();
    }
    
    public Class<?> getNmsClass(String clazz) throws ClassNotFoundException {
        return Class.forName("mc.euro.extraction.nms." + NMS + "." + clazz);
    }
    
    private void registerArena() {
        try {
            Class arenaClass = getNmsClass("HostageArena");
            CustomCommandExecutor cmd = getCustomCommandExecutor();
            debug.log("registering HostageArena class: " + arenaClass.toString());
            BattleArena.registerCompetition(this, "HostageArena", "vips", 
                    arenaClass,
                    cmd);
        } catch (Exception ex) {
            Logger.getLogger(HostagePlugin.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private CustomCommandExecutor getCustomCommandExecutor() {
        try {
            Class cmdClass = getNmsClass("HostageExecutor");
            return ((CustomCommandExecutor) cmdClass
                    .getConstructor(new Class[]{IHostagePlugin.class}).newInstance(this));
        } catch (ClassNotFoundException ex) {
            getLogger().log(Level.SEVERE, null, ex);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(HostagePlugin.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(HostagePlugin.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            getLogger().log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            getLogger().log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            getLogger().log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            getLogger().log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            getLogger().log(Level.SEVERE, null, ex);
        }
        getLogger().info("[HostageArena] method getCustomCommandExecutor() has disabled HostageArena");
        disableHostageArena();
        return null;
    }
    
    private void registerListeners() {
        try {
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
    
    private void disableHostageArena() {
        Bukkit.getPluginManager().disablePlugin(this);
    }

    @Override
    public DebugInterface debug() {
        return this.debug;
    }

    @Override
    public boolean toggleDebug() {
        if (debug instanceof DebugOn) {
            debug = new DebugOff(this);
            return false;
        } else {
            debug = new DebugOn(this);
        }
        return true;
    }

    @Override
    public void setDebugging(boolean enable) {
        if (enable == true) {
            debug = new DebugOn(this);
        } else {
            debug = new DebugOff(this);
        }
    }
    
    @Override
    public CustomConfig getConfig(String fileName) {
        return manager.getNewConfig(fileName);
    }
}
