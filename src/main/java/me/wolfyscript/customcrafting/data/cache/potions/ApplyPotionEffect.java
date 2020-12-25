package me.wolfyscript.customcrafting.data.cache.potions;

import me.wolfyscript.customcrafting.data.CCCache;
import org.bukkit.potion.PotionEffect;

public interface ApplyPotionEffect {

    void applyPotionEffect(PotionEffects potionEffectCache, CCCache cache, PotionEffect potionEffect);
}
