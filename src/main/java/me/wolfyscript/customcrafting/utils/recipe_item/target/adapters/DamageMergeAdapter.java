package me.wolfyscript.customcrafting.utils.recipe_item.target.adapters;

import me.wolfyscript.customcrafting.recipes.types.workbench.CraftingData;
import me.wolfyscript.customcrafting.recipes.types.workbench.IngredientData;
import me.wolfyscript.customcrafting.utils.recipe_item.target.MergeAdapter;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.jetbrains.annotations.Nullable;

public class DamageMergeAdapter extends MergeAdapter {

    private boolean repairBonus = false;
    private int additionalDamage = 0;

    public DamageMergeAdapter() {
        super(NamespacedKey.wolfyutilties("damage"));
    }

    public DamageMergeAdapter(DamageMergeAdapter adapter) {
        super(adapter);
        this.additionalDamage = adapter.additionalDamage;
        this.repairBonus = adapter.repairBonus;
    }

    /**
     *
     *
     * @param craftingData The {@link CraftingData} containing all the info of the grid state.
     * @param player       The player that crafted the item.
     * @param customResult The {@link CustomItem} of the crafted item.
     * @param result       The actual manipulable result ItemStack. (Previous adapters might have already manipulated this item!)
     * @return
     */
    @Override
    public ItemStack mergeCrafting(CraftingData craftingData, Player player, CustomItem customResult, ItemStack result) {
        int damage = 0;
        int maxDur = result.getType().getMaxDurability();
        for (IngredientData data : craftingData.getBySlots(slots)) {
            if (data.itemStack().getItemMeta() instanceof Damageable damageable) {
                if (damage <= 0) {
                    damage += damageable.getDamage();
                } else {
                    damage = Math.max(damage + damageable.getDamage() - maxDur, maxDur);
                }
            }
        }
        var meta = result.getItemMeta();
        int totalDamage = damage + additionalDamage;
        if (repairBonus) {
            totalDamage += Math.floor(maxDur / 20d);
        }
        ((Damageable) meta).setDamage(Math.min(totalDamage, maxDur));
        result.setItemMeta(meta);
        return result;
    }

    @Override
    public ItemStack merge(ItemStack[] ingredients, @Nullable Player player, CustomItem customResult, ItemStack result) {
        return null;
    }

    public int getAdditionalDamage() {
        return additionalDamage;
    }

    public void setAdditionalDamage(int additionalDamage) {
        this.additionalDamage = additionalDamage;
    }

    @Override
    public MergeAdapter clone() {
        return new DamageMergeAdapter(this);
    }
}
