package me.wolfyscript.customcrafting.data.cache.potions;

import me.wolfyscript.customcrafting.data.TestCache;
import org.bukkit.potion.PotionEffect;

public interface ApplyPotionEffect {

    void applyPotionEffect(PotionEffectCache potionEffectCache, TestCache cache, PotionEffect potionEffect);
}
