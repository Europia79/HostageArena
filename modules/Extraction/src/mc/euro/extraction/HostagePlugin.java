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
import mc.euro.extraction.factory.FactoryWrapper;
import mc.euro.extraction.nms.NPCFactory;
import mc.euro.extraction.util.Version;
import mc.euro.extraction.util.VersionFactory;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Nikolai
 */
public class HostagePlugin extends JavaPlugin implements IHostagePlugin {
    
    @Deprecated final String MAX = "1.8.8-R9.9-SNAPSHOT"; // not used
    @Deprecated final String MIN = "1.6.0"; // invisibility bug for 1.5.x & below
    final Version<Server> server = VersionFactory.getServerVersion();
    final String NMS = VersionFactory.getNmsVersion().toString();
    
    ConfigManager manager;
    DebugInterface debug;

    @Override
    public void onEnable() {
        
        if (!isServerCompatible()) {
            String className = "mc.euro.extraction.nms." + NMS + ".CraftHostage";
            getLogger().log(Level.WARNING, "HostageArena is not compatible with your server.");
            getLogger().log(Level.WARNING, "IMPLEMENTATION NOT FOUND: ");
            getLogger().log(Level.WARNING, className);
            Bukkit.getServer().getPluginManager().disablePlugin(this);
            return;
        }
        
        Version<Plugin> ba = VersionFactory.getPluginVersion("BattleArena");
        if (!ba.isCompatible("3.9.7.3")) {
            getLogger().info("HostageArena requires BattleArena v3.9.7.3+");
            Bukkit.getServer().getPluginManager().disablePlugin(this);
            return;
        }
        
        /**
         * Writes config.yml if it doesn't exist.
         * Updates an old config.yml with new nodes.
         */
        setupConfigYml();
        
        loadConfigYml();
        
        // getServer().getPluginManager().registerEvents(new HostageArena(), this);
        // registerListeners();
        // registerArena();
        CustomCommandExecutor cmd = new HostageExecutor(this);
        if (ba.isCompatible("3.9.8")) {
            int hitpoints = getConfig().getInt("HostageHP", 3);
            NPCFactory npcFactory = NPCFactory.newInstance(this);
            FactoryWrapper wrapper = new FactoryWrapper(this, npcFactory, hitpoints);
            wrapper.registerCompetition(this, "VipArena", "vips", HostageArena.class, cmd);
        } else {
            BattleArena.registerCompetition(this, "VipArena", "vips", HostageArena.class, cmd);
        }
        // CustomEntityType.registerEntities();
        registerEntites();
    } // End of onEnable()
    
    @Override
    public void onDisable() {
        super.onDisable();
        if (isServerCompatible()) {
            unregisterEntities(); // CustomEntityType.unregisterEntities();
        }
        updateConfigYml();
    }
    
    private boolean isServerCompatible() {
        String className = "mc.euro.extraction.nms." + NMS + ".CraftHostage";
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException ex) {
            return false;
        }
    }
    
    private void updateConfigYml() {
        if (debug instanceof DebugOn) {
            getConfig().set("Debug", true);
        } else {
            getConfig().set("Debug", false);
        }
        saveConfig();
    }
    
    /**
     * Writes config.yml if it doesn't exist. 
     * Updates an old config.yml with new nodes.
     */
    private void setupConfigYml() {
        saveDefaultConfig(); // Save the default config.yml if it doesn't exist
        getConfig().options().copyHeader(true); // update comment section
        getConfig().options().copyDefaults(true); // append
        saveConfig();
    }
    
    public void loadConfigYml() {
        manager = new ConfigManager(this);
        
        boolean b = getConfig().getBoolean("Debug");
        if (b) {
            debug = new DebugOn(this);
        } else {
            debug = new DebugOff(this);
        }

        try {
            debug.log("HostageNames = " + getConfig().getStringList("HostageNames").toString());
            debug.log("HostageTypes = " + getConfig().getStringList("HostageTypes").toString());
            debug.log("HostageHP = " + getConfig().getInt("HostageHP", 3));
            debug.log("ExtractionTimer = " + getConfig().getInt("ExtractionTimer", 30));
        } catch (NullPointerException ignored) {
            
        }
    } // End of loadConfigYml()
    
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
        updateConfigYml();
    }
    
    @Override
    public CustomConfig getConfig(String fileName) {
        return manager.getNewConfig(fileName);
    }
}
