package mc.euro.extraction.nms.v1_7_R1;

import java.lang.reflect.Field;
import net.minecraft.server.v1_7_R1.Entity;
import net.minecraft.server.v1_7_R1.EntityOwnable;
import net.minecraft.server.v1_7_R1.EntityVillager;
import net.minecraft.server.v1_7_R1.PathfinderGoalSelector;
import net.minecraft.server.v1_7_R1.World;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R1.util.UnsafeList;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Nikolai
 */
public class Hostage extends EntityVillager implements EntityOwnable {
    
    String owner;
    int ownerID;
    
    public Hostage(World w) {
        super(w);
        clearPathfinders();
        this.goalSelector.a(10, new PathfinderGoalFollowPlayer(this, 1.0D, 2.0F, 2.0F));
    }
    
    public Hostage(World w, int profession) {
        super(w, profession);
        clearPathfinders();
        this.goalSelector.a(10, new PathfinderGoalFollowPlayer(this, 1.0D, 2.0F, 2.0F));
    }
    
    public Hostage(World w, int profession, String p) {
        super(w, profession);
        this.owner = p;
        clearPathfinders();
        this.goalSelector.a(10, new PathfinderGoalFollowPlayer(this, 1.0D, 2.0F, 2.0F));
    }
    
    private void clearPathfinders() {
        try {
            Field bField = PathfinderGoalSelector.class.getDeclaredField("b"); // List.add() List.iterator()
            bField.setAccessible(true);
            Field cField = PathfinderGoalSelector.class.getDeclaredField("c"); // List.contains() List.remove()
            cField.setAccessible(true);
            bField.set(goalSelector, new UnsafeList<PathfinderGoalSelector>());
            bField.set(targetSelector, new UnsafeList<PathfinderGoalSelector>());
            cField.set(goalSelector, new UnsafeList<PathfinderGoalSelector>());
            cField.set(targetSelector, new UnsafeList<PathfinderGoalSelector>());
        } catch (Exception ex) {
            ex.printStackTrace();
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
        follow(p.getName());
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

    @Override
    public String getOwnerName() {
        return this.owner;
    }

    @Override
    public Entity getOwner() {
        if (this.owner == null) return null;
        Player player = (Player) Bukkit.getPlayer(this.owner);
        int id = player.getEntityId();
        Entity E = (Entity) this.world.getEntity(id);
        return E;
        // return this.world.getEntity(this.ownerID);
    }
    
    public Location getLocation() {
        Villager v = (Villager) this;
        return v.getLocation();
    }


}
