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

    private boolean subtract = false;
    private int additionalDamage = 0;

    public DamageMergeAdapter() {
        super(NamespacedKey.wolfyutilties("durability"));
    }

    public DamageMergeAdapter(DamageMergeAdapter adapter) {
        super(adapter);
    }

    /**
     * 110 dur + 110 dur = 220 dur (max 131)
     * <p>
     * 21 + 21 = 42 dmg
     * <p>
     * 42 - 131 = -89
     * <p>
     * 131 maxdur - (-89) = 220 dur (max 131)
     *
     * @param craftingData The {@link CraftingData} containing all the info of the grid state.
     * @param player       The player that crafted the item.
     * @param customResult The {@link CustomItem} of the crafted item.
     * @param result       The actual manipulable result ItemStack. (Previous adapters might have already manipulated this item!)
     * @return
     */
    @Override
    public ItemStack mergeCrafting(CraftingData craftingData, Player player, CustomItem customResult, ItemStack result) {
        int totalDamage = 0;
        for (IngredientData data : craftingData.getBySlots(slots)) {
            if (data.itemStack().getItemMeta() instanceof Damageable damageable) {
                totalDamage += damageable.getDamage();
            }
        }
        int maxDur = result.getType().getMaxDurability();
        totalDamage = Math.max(totalDamage - maxDur + additionalDamage, 0);
        if (result instanceof Damageable damageable) {
            damageable.setDamage(totalDamage);
        }
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

    public boolean isSubtract() {
        return subtract;
    }

    public void setSubtract(boolean subtract) {
        this.subtract = subtract;
    }

    @Override
    public MergeAdapter clone() {
        return new DamageMergeAdapter(this);
    }
}
