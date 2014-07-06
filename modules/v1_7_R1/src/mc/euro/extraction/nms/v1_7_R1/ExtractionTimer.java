package mc.euro.extraction.nms.v1_7_R1;

import java.util.ArrayList;
import java.util.List;
import mc.euro.extraction.util.ConfigManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Nikolai
 */
public class ExtractionTimer extends BukkitRunnable {
    
    JavaPlugin plugin;
    List<Hostage> hostages = new ArrayList<Hostage>();
    ConfigManager manager;
    
    public ExtractionTimer(List<Hostage> H) {
        hostages = H;
    }

    @Override
    public void run() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
