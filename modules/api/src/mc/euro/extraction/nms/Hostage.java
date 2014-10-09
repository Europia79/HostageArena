package mc.euro.extraction.nms;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager.Profession;

/**
 * 
 * 
 * @author Nikolai
 */
public interface Hostage {
    
    public void stay();
    public boolean isStopped();
    public boolean isFollowing();
    public void follow(Player p);
    public void follow(String p);
    public void setOwner(Player p);
    public void setOwner(String name);
    public String getOwnerName();
    public Location getLocation();
    public void setLocation(Location loc);
    public void removeEntity();
    
    public Profession getProfessionType();
    public void setProfessionType(Profession x);
    public String getCustomName();
    public void setCustomName(String name);
    
    public void setHealth(double health);
    
}
