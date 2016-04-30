package mc.euro.extraction.api;

import mc.euro.extraction.debug.DebugInterface;

import org.bukkit.plugin.Plugin;

/**
 *
 * @author Nikolai
 */
public interface ExtractionPlugin extends Plugin {
    
    public DebugInterface debug();
    public boolean toggleDebug();
    public void setDebugging(boolean enable);
    
}
