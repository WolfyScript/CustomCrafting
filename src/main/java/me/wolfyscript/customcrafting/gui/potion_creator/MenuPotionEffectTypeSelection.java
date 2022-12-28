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
import com.wolfyscript.utilities.bukkit.gui.GuiCluster;
import com.wolfyscript.utilities.bukkit.gui.GuiUpdate;
import com.wolfyscript.utilities.bukkit.gui.button.ButtonAction;
import com.wolfyscript.utilities.bukkit.gui.button.ButtonState;
import com.wolfyscript.utilities.bukkit.world.inventory.PlayerHeadUtils;
import com.wolfyscript.utilities.common.gui.ButtonInteractionResult;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.potions.PotionEffects;
import me.wolfyscript.customcrafting.gui.CCWindow;
import me.wolfyscript.customcrafting.gui.main_gui.ClusterMain;
import org.bukkit.potion.PotionEffectType;

public class MenuPotionEffectTypeSelection extends CCWindow {

    public MenuPotionEffectTypeSelection(GuiCluster<CCCache> cluster, CustomCrafting customCrafting) {
        super(cluster, ClusterPotionCreator.POTION_EFFECT_TYPE_SELECTION.getKey(), 54, customCrafting);
    }

    @Override
    public void onInit() {

        getButtonBuilder().action("back").state(state -> state.key(ClusterMain.BACK).icon(PlayerHeadUtils.getViaURL("864f779a8e3ffa231143fa69b96b14ee35c16d669e19c75fd1a7da4bf306c")).action((holder, cache, btn, slot, details) -> {
            PotionEffects potionEffectCache = holder.getGuiHandler().getCustomCache().getPotionEffectCache();
            if (!potionEffectCache.getOpenedFromCluster().isEmpty()) {
                holder.getGuiHandler().openWindow(new BukkitNamespacedKey(potionEffectCache.getOpenedFromCluster(), potionEffectCache.getOpenedFromWindow()));
            } else {
                holder.getGuiHandler().openWindow(potionEffectCache.getOpenedFromWindow());
            }
            return ButtonInteractionResult.cancel(true);
        })).register();

        for (PotionEffectType type : PotionEffectType.values()) {
            if (type != null) { // Required for servers running mods along side spigot.
                ButtonPotionEffectTypeSelect.register(getButtonBuilder(), type);
            }
        }
    }

    @Override
    public void onUpdateAsync(GuiUpdate<CCCache> update) {
        super.onUpdateAsync(update);
        update.setButton(0, "back");
        for (int i = 0; i < PotionEffectType.values().length; i++) {
            update.setButton(9 + i, "potion_effect_type_" + PotionEffectType.values()[i].getName().toLowerCase());
        }
    }
}
