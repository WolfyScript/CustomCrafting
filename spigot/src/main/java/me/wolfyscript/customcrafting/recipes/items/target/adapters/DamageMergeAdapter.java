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

package me.wolfyscript.customcrafting.recipes.items.target.adapters;

import com.wolfyscript.utilities.bukkit.world.items.reference.StackReference;
import io.th0rgal.oraxen.OraxenPlugin;
import io.th0rgal.oraxen.api.OraxenItems;
import io.th0rgal.oraxen.mechanics.provided.gameplay.durability.DurabilityMechanicFactory;
import me.wolfyscript.customcrafting.recipes.data.IngredientData;
import me.wolfyscript.customcrafting.recipes.data.RecipeData;
import me.wolfyscript.customcrafting.recipes.items.target.MergeAdapter;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.utilities.api.WolfyUtilCore;
import me.wolfyscript.utilities.compatibility.plugins.ItemsAdderIntegration;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DamageMergeAdapter extends MergeAdapter {

    private final List<DamagePluginAdapter> adapters = new ArrayList<>();
    private boolean repairBonus = false;
    private int additionalDamage = 0;
    private final WolfyUtilCore core;

    public DamageMergeAdapter() {
        super(new NamespacedKey(NamespacedKeyUtils.NAMESPACE, "damage"));
        this.core = WolfyUtilCore.getInstance();
        initAdapters();
    }

    public DamageMergeAdapter(DamageMergeAdapter adapter) {
        super(adapter);
        this.additionalDamage = adapter.additionalDamage;
        this.repairBonus = adapter.repairBonus;
        this.core = WolfyUtilCore.getInstance();
        initAdapters();
    }

    private void initAdapters() {
        adapters.clear();
        core.getCompatibilityManager().getPlugins().runIfAvailable("Oraxen", intgrtn -> adapters.add(new OraxenAdapter()));
        core.getCompatibilityManager().getPlugins().runIfAvailable("ItemsAdder", intgrtn -> adapters.add(new ItemsAdderAdapter()));
        core.getCompatibilityManager().getPlugins().runIfAvailable("eco", intgratn -> adapters.add(new EcoArmorAdapter()));
    }

    public int getAdditionalDamage() {
        return additionalDamage;
    }

    public void setAdditionalDamage(int additionalDamage) {
        this.additionalDamage = additionalDamage;
    }

    @Override
    public ItemStack merge(RecipeData<?> recipeData, @Nullable Player player, @Nullable Block block, StackReference resultReference, ItemStack result) {
        return tryApplyToPluginItem(recipeData, result).orElseGet(() -> {
            var meta = result.getItemMeta();
            ((Damageable) meta).setDamage(calculateDamage(recipeData, result.getType().getMaxDurability()));
            result.setItemMeta(meta);
            return result;
        });
    }

    private int calculateDamage(RecipeData<?> recipeData, final int maxDur) {
        int totalDurability = 0;
        for (IngredientData data : recipeData.getBySlots(slots)) {
            totalDurability += maxDur - tryGetDamageFromPlugins(data.itemStack()).orElseGet(() -> data.itemStack().getItemMeta() instanceof Damageable damageable ? damageable.getDamage() : 0);
        }
        return Math.min(
                Math.max(
                        (maxDur - totalDurability) + additionalDamage - (repairBonus ? (int) Math.floor(maxDur / 20d) : 0),
                        0
                ),
                maxDur
        );
    }

    private Optional<Integer> tryGetDamageFromPlugins(ItemStack itemStack) {
        for (DamagePluginAdapter adapter : adapters) {
            Optional<Integer> damage = adapter.getDamage(itemStack);
            if (damage.isPresent()) return damage;
        }
        return Optional.empty();
    }

    private Optional<ItemStack> tryApplyToPluginItem(RecipeData<?> data, ItemStack result) {
        for (DamagePluginAdapter adapter : adapters) {
            Optional<ItemStack> damage = adapter.tryToApplyDamage(data, result);
            if (damage.isPresent()) return damage;
        }
        return Optional.empty();
    }

    @Override
    public MergeAdapter clone() {
        return new DamageMergeAdapter(this);
    }


    private interface DamagePluginAdapter {

        Optional<Integer> getDamage(ItemStack stack);

        Optional<ItemStack> tryToApplyDamage(RecipeData<?> recipeData, ItemStack result);

    }

    private class ItemsAdderAdapter implements DamagePluginAdapter {

        private final ItemsAdderIntegration integration;

        ItemsAdderAdapter() {
            this.integration = core.getCompatibilityManager().getPlugins().getIntegration("ItemsAdder", ItemsAdderIntegration.class);
        }

        @Override
        public Optional<Integer> getDamage(ItemStack stack) {
            return integration.getStackByItemStack(stack).map(customStack -> customStack.getMaxDurability() - customStack.getDurability());
        }

        @Override
        public Optional<ItemStack> tryToApplyDamage(RecipeData<?> recipeData, ItemStack result) {
            return integration.getStackByItemStack(result).map(customStack -> {
                final int maxDur = customStack.getMaxDurability();
                customStack.setDurability(maxDur - calculateDamage(recipeData, maxDur));
                return result;
            });
        }
    }

    private class OraxenAdapter implements DamagePluginAdapter {

        public static final org.bukkit.NamespacedKey DURABILITY_KEY = new org.bukkit.NamespacedKey(OraxenPlugin.get(), "durability");

        @Override
        public Optional<Integer> getDamage(ItemStack stack) {
            PersistentDataContainer persistentDataContainer = stack.getItemMeta().getPersistentDataContainer();
            Integer dur = persistentDataContainer.get(DURABILITY_KEY, PersistentDataType.INTEGER);
            var durabilityMechanic = DurabilityMechanicFactory.get().getMechanic(OraxenItems.getIdByItem(stack));
            if (dur != null && durabilityMechanic != null) {
                return Optional.of(durabilityMechanic.getItemMaxDurability() - dur);
            }
            return Optional.empty();
        }

        @Override
        public Optional<ItemStack> tryToApplyDamage(RecipeData<?> recipeData, ItemStack result) {
            var durabilityMechanic = DurabilityMechanicFactory.get().getMechanic(OraxenItems.getIdByItem(result));
            if (durabilityMechanic != null) {
                durabilityMechanic.changeDurability(result, calculateDamage(recipeData, durabilityMechanic.getItemMaxDurability()));
                return Optional.of(result);
            }
            return Optional.empty();
        }
    }

    private class EcoArmorAdapter implements DamagePluginAdapter {

        private static final org.bukkit.NamespacedKey EFFECTIVE_DURABILITY = new org.bukkit.NamespacedKey("ecoarmor", "effective-durability");

        @Override
        public Optional<Integer> getDamage(ItemStack stack) {
            return getEffectiveMaxDur(stack).map(effectiveMaxDur -> {
                if (stack.getItemMeta() instanceof Damageable damageable) {
                    int damage = damageable.getDamage();
                    double dmgPercent = (double) damage / stack.getType().getMaxDurability();
                    return (int) Math.floor(effectiveMaxDur.doubleValue() * dmgPercent);
                }
                return null;
            });
        }

        @Override
        public Optional<ItemStack> tryToApplyDamage(RecipeData<?> recipeData, ItemStack result) {
            return getEffectiveMaxDur(result).map(effectiveMaxDur -> {
                int dmg = calculateDamage(recipeData, effectiveMaxDur);
                if (result.getItemMeta() instanceof Damageable damageable) {
                    damageable.setDamage(dmg);
                    result.setItemMeta(damageable);
                }
                return result;
            });
        }

        private Optional<Integer> getEffectiveMaxDur(ItemStack stack) {
            PersistentDataContainer persistentDataContainer = stack.getItemMeta().getPersistentDataContainer();
            return Optional.ofNullable(persistentDataContainer.get(EFFECTIVE_DURABILITY, PersistentDataType.INTEGER));
        }
    }

}
