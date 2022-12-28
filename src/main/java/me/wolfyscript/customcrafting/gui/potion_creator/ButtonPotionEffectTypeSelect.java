/*
 *       ____ _  _ ____ ___ ____ _  _ ____ ____ ____ ____ ___ _ _  _ ____
 *       |    |  | [__   |  |  | |\/| |    |__/ |__| |___  |  | |\ | | __
 *       |___ |__| ___]  |  |__| |  | |___ |  \ |  | |     |  | | \| |__]
 *
 *       CustomCrafting Recipe creation and management tool for Minecraft
 *                      Copyright (C) 2021  WolfyScript
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.wolfyscript.customcrafting.gui.potion_creator;

import com.wolfyscript.utilities.bukkit.BukkitNamespacedKey;
import com.wolfyscript.utilities.bukkit.gui.GuiMenuComponent;
import com.wolfyscript.utilities.bukkit.gui.callback.CallbackButtonRender;
import com.wolfyscript.utilities.common.gui.ButtonInteractionResult;
import java.util.Locale;
import java.util.Random;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.potions.PotionEffects;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ButtonPotionEffectTypeSelect {

    private static final Random random = new Random(System.currentTimeMillis());

    static void register(GuiMenuComponent.ButtonBuilder<CCCache> buttonBuilder, PotionEffectType effectType) {
        buttonBuilder.action("potion_effect_type_" + effectType.getName().toLowerCase()).state(state -> state.key("effect_type").icon(Material.POTION).action((holder, cache, btn, slot, details) -> {
            PotionEffects potionEffectCache = holder.getGuiHandler().getCustomCache().getPotionEffectCache();
            if (potionEffectCache.getApplyPotionEffectType() != null) {
                potionEffectCache.getApplyPotionEffectType().applyPotionEffect(holder.getGuiHandler().getCustomCache(), effectType);
            }
            if (!potionEffectCache.getOpenedFromCluster().isEmpty()) {
                holder.getGuiHandler().openWindow(new BukkitNamespacedKey(potionEffectCache.getOpenedFromCluster(), potionEffectCache.getOpenedFromWindow()));
            } else {
                holder.getGuiHandler().openWindow(potionEffectCache.getOpenedFromWindow());
            }
            return ButtonInteractionResult.cancel(true);
        }).render((holder, cache, btn, slot, itemStack) -> CallbackButtonRender.Result.of(getPotionEffectTypeItem(effectType)))).register();
    }

    private static ItemStack getPotionEffectTypeItem(PotionEffectType type) {
        ItemStack itemStack = new ItemStack(Material.POTION);
        PotionMeta itemMeta = (PotionMeta) itemStack.getItemMeta();
        itemMeta.setDisplayName("§7§l » §r§7" + StringUtils.capitalize(type.getName().replace("_", " ").toLowerCase(Locale.ROOT)));
        itemMeta.addCustomEffect(new PotionEffect(type, 1, 1), true);
        itemMeta.setColor(Color.fromRGB(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}
