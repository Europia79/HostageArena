package mc.euro.extraction.nms.v1_5_R2;

import java.lang.reflect.Field;
import net.minecraft.server.v1_5_R2.Entity;
import net.minecraft.server.v1_5_R2.EntityVillager;
import net.minecraft.server.v1_5_R2.PathfinderGoalSelector;
import net.minecraft.server.v1_5_R2.World;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_5_R2.entity.CraftVillager;
import org.bukkit.craftbukkit.v1_5_R2.util.UnsafeList;
import org.bukkit.entity.Player;

/**
 *
 * @author Nikolai
 */
public class CraftHostage extends EntityVillager {
    
    String owner;
    int ownerID;
    World world2;
    
    public CraftHostage(World w) {
        super(w);
        this.world2 = w;
        clearPathfinders();
        this.goalSelector.a(10, new PathfinderGoalFollowPlayer(this, 1.0F, 2.0F, 2.0F));
        ((CraftVillager) super.getBukkitEntity()).setMaxHealth(20);
        this.setHealth(20);
    }
    
    public CraftHostage(World w, int profession) {
        super(w, profession);
        this.world2 = w;
        clearPathfinders();
        this.goalSelector.a(10, new PathfinderGoalFollowPlayer(this, 1.0F, 2.0F, 2.0F));
        ((CraftVillager) super.getBukkitEntity()).setMaxHealth(20);
        this.setHealth(20);
    }
    
    private void clearPathfinders() {
        try {
            Field bField = PathfinderGoalSelector.class.getDeclaredField("b");
            bField.setAccessible(true);
            Field cField = PathfinderGoalSelector.class.getDeclaredField("c");
            cField.setAccessible(true);
            bField.set(goalSelector, new UnsafeList<PathfinderGoalSelector>());
            bField.set(targetSelector, new UnsafeList<PathfinderGoalSelector>());
            cField.set(goalSelector, new UnsafeList<PathfinderGoalSelector>());
            cField.set(targetSelector, new UnsafeList<PathfinderGoalSelector>());
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }
    
    public void stay() {
        this.owner = null;
    }
    
    public boolean isStopped() {
        if (this.owner == null) return true;
        return false;
    }
    
    public boolean isFollowing() {
        if (this.owner == null) return false;
        return true;
    }
    
    public void follow(Player p) {
        this.owner = p.getName();
    }
    
    public void follow(String p) {
        this.owner = p;
    }
    
    public void setOwner(Player p) {
        this.owner = p.getName();
        this.ownerID = p.getEntityId();
    }
    
    public void setOwner(String name) {
        this.owner = name;
    }

    public String getOwnerName() {
        return this.owner;
    }

    public Entity getOwner() {
        if (this.owner == null) return null;
        Player player = (Player) Bukkit.getPlayer(this.owner);
        int id = player.getEntityId();
        Entity E = (Entity) this.world.getEntity(id);
        return E;
        // return this.world.getEntity(this.ownerID);
    }

}
