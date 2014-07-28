package mc.euro.extraction.api;

import mc.euro.extraction.appljuze.CustomConfig;
import mc.euro.extraction.debug.DebugInterface;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author Nikolai
 */
public interface SuperPlugin extends Plugin {
    
    public DebugInterface debug();
    public boolean toggleDebug();
    public void setDebugging(boolean enable);
    
    public CustomConfig getConfig(String fileName);
    
}
