package mc.euro.extraction;

import mc.euro.extraction.nms.v1_7_R1.HostageArena;
import mc.euro.extraction.debug.*;
import mc.euro.extraction.nms.v1_7_R1.CustomEntityType;
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
        getServer().getPluginManager().registerEvents(new HostageArena(), this);
        CustomEntityType.registerEntities();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        CustomEntityType.unregisterEntities();
    }

}
