package me.wolfyscript.customcrafting.data.cache.potions;

import me.wolfyscript.customcrafting.data.TestCache;
import org.bukkit.potion.PotionEffectType;

public interface ApplyPotionEffectType {

    void applyPotionEffect(TestCache cache, PotionEffectType potionEffectType);
}
