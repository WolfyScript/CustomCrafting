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
import me.wolfyscript.customcrafting.recipes.CustomRecipeAnvil;
import me.wolfyscript.customcrafting.recipes.data.AnvilData;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import me.wolfyscript.utilities.util.inventory.item_builder.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

public class RepairTaskDefault extends RepairTask {

    public static final NamespacedKey KEY = new NamespacedKey(NamespacedKeyUtils.NAMESPACE, "default");

    public RepairTaskDefault() {
        super(KEY, CustomRecipeAnvil.Mode.NONE);
    }

    protected RepairTaskDefault(NamespacedKey key, CustomRecipeAnvil.Mode mode) {
        super(key, mode);
    }

    @Override
    public StackReference compute(CustomRecipeAnvil recipe, PrepareAnvilEvent event, AnvilData anvilData, Player player, ItemStack inputLeft, ItemStack inputRight) {
        AnvilInventory inventory = event.getInventory();
        ItemBuilder stackBuilder;
        if (ItemUtils.isAirOrNull(event.getResult())) {
            stackBuilder = new ItemBuilder(inputLeft.clone());
            if (!recipe.isBlockRename() && inventory.getRenameText() != null && !inventory.getRenameText().isEmpty()) {
                stackBuilder.setDisplayName(inventory.getRenameText());
            }
        } else {
            stackBuilder = new ItemBuilder(event.getResult());
            ItemStack resultStack = event.getResult();
            if (stackBuilder.hasItemMeta()) {
                //Further recipe options to block features.
                if (recipe.isBlockEnchant() && resultStack.hasItemMeta() && stackBuilder.getItemMeta().hasEnchants()) {
                    //Block Enchants
                    resultStack.getEnchantments().keySet().forEach(stackBuilder::removeEnchantment);
                    if (inputLeft != null) {
                        inputLeft.getEnchantments().forEach(stackBuilder::addUnsafeEnchantment);
                    }
                }
                if (recipe.isBlockRename()) {
                    //Block Renaming
                    if (inputLeft != null && inputLeft.hasItemMeta() && inputLeft.getItemMeta().hasDisplayName()) {
                        stackBuilder.setDisplayName(inputLeft.getItemMeta().getDisplayName());
                    } else {
                        stackBuilder.setDisplayName(null);
                    }
                }
                if (recipe.isBlockRepair() && stackBuilder.getItemMeta() instanceof Damageable resultDamageable) {
                    //Block Repairing
                    if (inputLeft != null && inputLeft.hasItemMeta() && inputLeft.getItemMeta() instanceof Damageable damageable) {
                        resultDamageable.setDamage(damageable.getDamage());
                    }
                    stackBuilder.setItemMeta(resultDamageable);
                }
            }
        }
        return StackReference.of(stackBuilder.create());
    }
}
