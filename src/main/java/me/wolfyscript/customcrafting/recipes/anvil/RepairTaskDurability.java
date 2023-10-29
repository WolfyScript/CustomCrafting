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

import com.wolfyscript.utilities.bukkit.world.items.reference.StackReference;
import com.wolfyscript.utilities.bukkit.world.items.reference.WolfyUtilsStackIdentifier;
import me.wolfyscript.customcrafting.recipes.CustomRecipeAnvil;
import me.wolfyscript.customcrafting.recipes.data.AnvilData;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.utilities.api.WolfyUtilCore;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.compatibility.plugins.ItemsAdderIntegration;
import me.wolfyscript.utilities.compatibility.plugins.itemsadder.CustomStack;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.inventory.item_builder.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

public class RepairTaskDurability extends RepairTaskDefault {

    public static final NamespacedKey KEY = new NamespacedKey(NamespacedKeyUtils.NAMESPACE, "durability");

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
    public StackReference compute(CustomRecipeAnvil recipe, PrepareAnvilEvent event, AnvilData anvilData, Player player, ItemStack inputLeft, ItemStack inputRight) {
        StackReference resultItem = super.compute(recipe, event, anvilData, player, inputLeft, inputRight);
        //Durability mode is set.
        ItemsAdderIntegration iAIntegration = WolfyUtilCore.getInstance().getCompatibilityManager().getPlugins().getIntegration("ItemsAdder", ItemsAdderIntegration.class);
        if (iAIntegration != null) {
            //Set the new durability using the ItemsAdder method.
            CustomStack customStack = iAIntegration.getByItemStack(resultItem.originalStack());
            if (customStack != null) {
                customStack.setDurability(Math.min(customStack.getMaxDurability(), customStack.getDurability() + durability));
                return resultItem;
            }
        }

        var stackBuilder = new ItemBuilder(resultItem.referencedStack());
        if (stackBuilder.hasCustomDurability()) {
            stackBuilder.setCustomDamage(Math.max(0, stackBuilder.getCustomDamage() - durability));
        } else if (stackBuilder.getItemMeta() instanceof Damageable damageable) {
            damageable.setDamage(damageable.getDamage() - durability);
            stackBuilder.setItemMeta(damageable);
        }
        return resultItem;
    }
}
