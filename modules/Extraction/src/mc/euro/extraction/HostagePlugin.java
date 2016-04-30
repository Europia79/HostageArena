package mc.euro.extraction;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import mc.alk.arena.BattleArena;
import mc.alk.arena.serializers.ArenaSerializer;
import mc.euro.extraction.api.ExtractionPlugin;
import mc.euro.extraction.appljuze.ConfigManager;
import mc.euro.extraction.appljuze.CustomConfig;
import mc.euro.extraction.arenas.HostageArena;
import mc.euro.extraction.arenas.VipArena;
import mc.euro.extraction.commands.HostageExecutor;
import mc.euro.extraction.commands.VipExecutor;
import mc.euro.extraction.debug.DebugInterface;
import mc.euro.extraction.debug.DebugOff;
import mc.euro.extraction.debug.DebugOn;
import mc.euro.extraction.factory.HostageArenaFactory;
import mc.euro.extraction.factory.VipArenaFactory;
import mc.euro.extraction.v2.Version;
import mc.euro.extraction.v2.VersionFactory;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Nikolai
 */
public class HostagePlugin extends JavaPlugin implements ExtractionPlugin {
    
    final String NMS = VersionFactory.getNmsPackage();
    
    ConfigManager manager;
    DebugInterface debug;

    @Override
    public void onEnable() {
        
        if (!isServerCompatible()) {
            String className = "mc.euro.extraction.nms." + NMS + ".CraftHostage";
            getLogger().log(Level.WARNING, "HostageArena is not compatible with your server.");
            getLogger().log(Level.WARNING, "IMPLEMENTATION NOT FOUND: ");
            getLogger().log(Level.WARNING, className);
            disableHostageArena();
            return;
        }
        
        Version<Plugin> ba = VersionFactory.getPluginVersion("BattleArena");
        if (!ba.isCompatible("3.9.7.3")) {
            getLogger().info("HostageArena requires BattleArena v3.9.7.3+");
            disableHostageArena();
            return;
        }
        
        /**
         * Writes config.yml if it doesn't exist.
         * Updates an old config.yml with new nodes.
         */
        setupConfigYml();
        
        loadConfigYml();
        
        fixHostageArenaConfigYml();
        
        if (ba.isCompatible("3.9.8")) {
            VipArenaFactory.registerCompetition(this, "VipArena", "vips", VipArena.class, new VipExecutor(this));
            HostageArenaFactory.registerCompetition(this, "HostageArena", "hostage", HostageArena.class, new HostageExecutor(this));
        } else {
            BattleArena.registerCompetition(this, "VipArena", "vips", VipArena.class, new VipExecutor(this));
            BattleArena.registerCompetition(this, "HostageArena", "hostage", HostageArena.class, new HostageExecutor(this));
        }
        
        registerEntites(); // CustomEntityType.registerEntities();
    } // End of onEnable()
    
    @Override
    public void onDisable() {
        super.onDisable();
        if (isServerCompatible()) {
            unregisterEntities(); // CustomEntityType.unregisterEntities();
        }
        updateConfigYml();
        saveAllArenas();
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
    
    private void disableHostageArena() {
        Bukkit.getPluginManager().disablePlugin(this);
    }
    
    private void updateConfigYml() {
        if (debug instanceof DebugOn) {
            getConfig().set("Debug", true);
        } else {
            getConfig().set("Debug", false);
        }
        saveConfig();
    }
    
    private void saveAllArenas() {
        boolean verbose = (debug instanceof DebugOn);
        ArenaSerializer.saveAllArenas(verbose);
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
    
    /**
     * Node "HostageArena.command" must be "hostage".
     * 
     * Originally, HostageArena only supported HostageArena...
     * Then it was changed to VipArena with VipArenaConfig.yml
     * But the problem is if the old HostageArenaConfig.yml is still there.
     * Then the node will incorrectly be "vips".
     */
    private void fixHostageArenaConfigYml() {
        File hfile = new File(this.getDataFolder(), "HostageArenaConfig.yml");
        if (hfile.exists()) {
            FileConfiguration hconfig = YamlConfiguration.loadConfiguration(hfile);
            if (hconfig.getString("HostageArena.command", "").equals("vips")) {
                hconfig.set("HostageArena.command", "hostage");
            }
            try {
                hconfig.save(hfile);
            } catch (IOException ex) {
                Logger.getLogger(HostagePlugin.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    } // End of fixHostageArenaConfigYml()
    
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
    
    public Class<?> getNmsClass(String clazz) throws ClassNotFoundException {
        return Class.forName("mc.euro.extraction.nms." + NMS + "." + clazz);
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
    
    public CustomConfig getConfig(String fileName) {
        return manager.getNewConfig(fileName);
    }
}
