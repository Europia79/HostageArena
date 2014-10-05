package mc.euro.extraction.nms.v1_7_R1;

import mc.euro.extraction.api.SuperPlugin;
import mc.euro.extraction.nms.Hostage;
import mc.euro.extraction.nms.NPCFactory;
import mc.euro.extraction.util.Attributes;
import org.bukkit.craftbukkit.v1_7_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Villager;

/**
 * 
 * 
 * @author Nikolai
 */
public class HostageFactory extends NPCFactory {
    
    public HostageFactory(SuperPlugin p) {
        this.plugin = p;
    }

    @Override
    public Hostage getHostage(Entity E) {
        Hostage h;
        try {
            h = (Hostage) ((CraftEntity)E).getHandle();
        } catch (ClassCastException ex) {
            // Caused by baby villager or a non-Hostage Villager.
            plugin.debug().log("onHostageInteract() ClassCastException: most likely "
                    + "caused by a baby villager or a Villager that is not a Hostage.");
            Villager v = (Villager) E;
            double HP = v.getHealth();
            Villager.Profession type = v.getProfession();
            String customName = v.getCustomName();
            CraftHostage hostage = new CraftHostage(((CraftWorld) v.getWorld()).getHandle(), 
                    Attributes.getType(plugin).getId());
            hostage.setLocation(v.getLocation());
            ((CraftWorld) v.getWorld()).getHandle().removeEntity(((CraftEntity)E).getHandle());
            ((CraftWorld) v.getWorld()).getHandle().addEntity(hostage);
            hostage.setHealth((float) HP);
            hostage.setType(type);
            hostage.setCustomName(customName);
            h = (Hostage) hostage;
        }
        return h;
    }

}
