package mc.euro.extraction.nms.v1_4_5;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import net.minecraft.server.v1_4_5.BiomeBase;
import net.minecraft.server.v1_4_5.BiomeMeta;
import net.minecraft.server.v1_4_5.EntityLiving;
import net.minecraft.server.v1_4_5.EntityTypes;
import net.minecraft.server.v1_4_5.EntityVillager;

import org.bukkit.entity.EntityType;

/**
 * http://forums.bukkit.org/threads/nms-tutorial-how-to-override-default-minecraft-mobs.216788/
 *
 * @author TeeePeee
 */
public enum CustomEntityType {

    HOSTAGE("Villager", 120, EntityType.VILLAGER, EntityVillager.class, CraftHostage.class);

    public String author = "TeeePeee";
    public String source = "http://forums.bukkit.org/threads/nms-tutorial-how-to-override-default-minecraft-mobs.216788/";

    private String name;
    private int id;
    private EntityType entityType;
    private Class<? extends EntityLiving> nmsClass;
    private Class<? extends EntityLiving> customClass;

    private CustomEntityType(String name, int id, EntityType entityType,
            Class<? extends EntityLiving> nmsClass,
            Class<? extends EntityLiving> customClass) {
        this.name = name;
        this.id = id;
        this.entityType = entityType;
        this.nmsClass = nmsClass;
        this.customClass = customClass;
    }

    public String getName() {
        return name;
    }

    public int getID() {
        return id;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public Class<? extends EntityLiving> getNMSClass() {
        return nmsClass;
    }

    public Class<? extends EntityLiving> getCustomClass() {
        return customClass;
    }

    /**
     * Register our entities.
     */
    public static void registerEntities() {
        for (CustomEntityType entity : values()) {
            a(entity.getCustomClass(), entity.getName(), entity.getID());
        }

// BiomeBase#biomes became private.
        BiomeBase[] biomes;
        try {
            biomes = (BiomeBase[]) getPrivateStatic(BiomeBase.class, "biomes");
        } catch (Exception ex) {
// Unable to fetch.
            ex.printStackTrace();
            return;
        }
        for (BiomeBase biomeBase : biomes) {
            if (biomeBase == null) {
                break;
            }

// This changed names from J, K, L and M.
            for (String field : new String[]{"J", "K", "L", "M"}) {
                try {
                    Field list = BiomeBase.class.getDeclaredField(field);
                    list.setAccessible(true);
                    @SuppressWarnings("unchecked")
                    List<BiomeMeta> mobList = (List<BiomeMeta>) list.get(biomeBase);

// Write in our custom class.
                    for (BiomeMeta meta : mobList) {
                        for (CustomEntityType entity : values()) {
                            if (entity.getNMSClass().equals(meta.b)) {
                                meta.b = entity.getCustomClass();
                            }
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    /**
     * Unregister our entities to prevent memory leaks. Call on disable.
     */
    public static void unregisterEntities() {
        for (CustomEntityType entity : values()) {
// Remove our class references.
            try {
                ((Map) getPrivateStatic(EntityTypes.class, "c")).remove(entity.getCustomClass());
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            try {
                ((Map) getPrivateStatic(EntityTypes.class, "e")).remove(entity.getCustomClass());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        for (CustomEntityType entity : values()) {
            try {
// Unregister each entity by writing the NMS back in place of the custom class.
                a(entity.getNMSClass(), entity.getName(), entity.getID());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

// Biomes#biomes was made private so use reflection to get it.
        BiomeBase[] biomes;
        try {
            biomes = (BiomeBase[]) getPrivateStatic(BiomeBase.class, "biomes");
        } catch (Exception ex) {
// Unable to fetch.
            ex.printStackTrace();
            return;
        }
        for (BiomeBase biomeBase : biomes) {
            if (biomeBase == null) {
                break;
            }

// The list fields changed names but update the meta regardless.
            for (String field : new String[]{"J", "K", "L", "M"}) {
                try {
                    Field list = BiomeBase.class.getDeclaredField(field);
                    list.setAccessible(true);
                    @SuppressWarnings("unchecked")
                    List<BiomeMeta> mobList = (List<BiomeMeta>) list.get(biomeBase);

// Make sure the NMS class is written back over our custom class.
                    for (BiomeMeta meta : mobList) {
                        for (CustomEntityType entity : values()) {
                            if (entity.getCustomClass().equals(meta.b)) {
                                meta.b = entity.getNMSClass();
                            }
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    /**
     * A convenience method.
     *
     * @param clazz The class.
     * @param f The string representation of the private static field.
     * @return The object found
     * @throws Exception if unable to get the object.
     */
    private static Object getPrivateStatic(Class clazz, String f) throws Exception {
        Field field = clazz.getDeclaredField(f);
        field.setAccessible(true);
        return field.get(null);
    }

    /*
     * Since 1.7.2 added a check in their entity registration, simply bypass it and write to the maps ourself.
     */
    private static void a(Class paramClass, String paramString, int paramInt) {
        try {
            ((Map) getPrivateStatic(EntityTypes.class, "b")).put(paramString, paramClass);
            ((Map) getPrivateStatic(EntityTypes.class, "c")).put(paramClass, paramString);
            ((Map) getPrivateStatic(EntityTypes.class, "d")).put(Integer.valueOf(paramInt), paramClass);
            ((Map) getPrivateStatic(EntityTypes.class, "e")).put(paramClass, Integer.valueOf(paramInt));
            ((Map) getPrivateStatic(EntityTypes.class, "f")).put(paramString, Integer.valueOf(paramInt));
        } catch (Exception ex) {
// Unable to register the new class.
            ex.printStackTrace();
        }
    }
}
