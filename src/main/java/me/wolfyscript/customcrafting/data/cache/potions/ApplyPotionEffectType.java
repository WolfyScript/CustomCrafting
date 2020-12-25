package me.wolfyscript.customcrafting.data.cache.potions;

import me.wolfyscript.customcrafting.data.CCCache;
import org.bukkit.potion.PotionEffectType;

public interface ApplyPotionEffectType {

    void applyPotionEffect(CCCache cache, PotionEffectType potionEffectType);
}
