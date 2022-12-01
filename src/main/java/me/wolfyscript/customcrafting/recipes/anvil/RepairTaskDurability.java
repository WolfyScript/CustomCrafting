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

package me.wolfyscript.customcrafting.recipes.anvil;

import com.wolfyscript.utilities.NamespacedKey;
import com.wolfyscript.utilities.bukkit.BukkitNamespacedKey;
import com.wolfyscript.utilities.bukkit.WolfyCoreBukkit;
import com.wolfyscript.utilities.bukkit.world.items.CustomItem;
import me.wolfyscript.customcrafting.recipes.CustomRecipeAnvil;
import me.wolfyscript.customcrafting.recipes.data.AnvilData;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.utilities.compatibility.plugins.ItemsAdderIntegration;
import me.wolfyscript.utilities.compatibility.plugins.itemsadder.CustomStack;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

public class RepairTaskDurability extends RepairTaskDefault {

    public static final NamespacedKey KEY = new BukkitNamespacedKey(NamespacedKeyUtils.NAMESPACE, "durability");

    private int durability;

    public RepairTaskDurability() {
        super(KEY, CustomRecipeAnvil.Mode.DURABILITY);
    }

    public void setDurability(int durability) {
        this.durability = durability;
    }

    public int getDurability() {
        return durability;
    }

    @Override
    public CustomItem computeResult(CustomRecipeAnvil recipe, PrepareAnvilEvent event, AnvilData anvilData, Player player, ItemStack inputLeft, ItemStack inputRight) {
        CustomItem resultItem = super.computeResult(recipe, event, anvilData, player, inputLeft, inputRight);
        //Durability mode is set.
        ItemsAdderIntegration iAIntegration = WolfyCoreBukkit.getInstance().getCompatibilityManager().getPlugins().getIntegration("ItemsAdder", ItemsAdderIntegration.class);
        if (iAIntegration != null) {
            //Set the new durability using the ItemsAdder method.
            CustomStack customStack = iAIntegration.getByItemStack(resultItem.getItemStack());
            if (customStack != null) {
                customStack.setDurability(Math.min(customStack.getMaxDurability(), customStack.getDurability() + durability));
                return resultItem;
            }
        }
        if (resultItem.hasCustomDurability()) {
            resultItem.setCustomDamage(Math.max(0, resultItem.getCustomDamage() - durability));
        } else if (resultItem.getItemMeta() instanceof Damageable damageable) {
            damageable.setDamage(damageable.getDamage() - durability);
            resultItem.setItemMeta(damageable);
        }
        return resultItem;
    }
}
