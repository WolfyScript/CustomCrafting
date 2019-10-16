package me.wolfyscript.customcrafting.utils;

import org.bukkit.Location;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.json.simple.JSONObject;

import java.util.Locale;

public class LivingEntitySerialization {

    /**
     * Serialize a LivingEntity into a JSONObject. If the given LivingEntity is a Player,
     * it is cast as such and redirected to PlayerSerialization.
     *
     * @param entity
     * @return
     */
    public static JSONObject serializeEntity(LivingEntity entity) {
            JSONObject root = new JSONObject();
            root.put("age", ((Ageable) entity).getAge());
            root.put("health", entity.getHealth());
            root.put("name", entity.getCustomName());
            root.put("type", entity.getType().getTypeId());
            return root;

    }

    /**
     * Serialize a LivingEntity into a String.
     *
     * @param entity       The LivingEntity to serialize
     * @return The serialization string
     */
    public static String serializeEntityAsString(LivingEntity entity) {
        return serializeEntity(entity).toString();
    }

    /**
     * Spawn a LivingEntity in a desired Location with the given stats.
     *
     * @param location Where the entity should be spawned
     * @param stats    The stats of the entity
     * @return The LivingEntity spawned
     */
    public static LivingEntity spawnEntity(Location location, JSONObject stats) {
        if (stats.get("type") == null) {
            throw new IllegalArgumentException("The type of the entity cannot be determined");
        } else {
            LivingEntity entity = (LivingEntity) location.getWorld().spawnEntity(location, EntityType.valueOf(((String) stats.get("type")).toUpperCase(Locale.ROOT)));
            ((Ageable) entity).setAge((Integer) stats.get("age"));
            entity.setHealth((Double) stats.get("health"));
            entity.setCustomName((String) stats.get("name"));


            return entity;
        }

    }

}
