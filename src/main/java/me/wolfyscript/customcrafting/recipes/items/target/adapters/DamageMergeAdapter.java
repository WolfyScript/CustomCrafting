package me.wolfyscript.customcrafting.recipes.items.target.adapters;

import me.wolfyscript.customcrafting.recipes.data.IngredientData;
import me.wolfyscript.customcrafting.recipes.data.RecipeData;
import me.wolfyscript.customcrafting.recipes.items.target.MergeAdapter;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.jetbrains.annotations.Nullable;

public class DamageMergeAdapter extends MergeAdapter {

    private boolean repairBonus = false;
    private int additionalDamage = 0;

    public DamageMergeAdapter() {
        super(new NamespacedKey(NamespacedKeyUtils.NAMESPACE, "damage"));
    }

    public DamageMergeAdapter(DamageMergeAdapter adapter) {
        super(adapter);
        this.additionalDamage = adapter.additionalDamage;
        this.repairBonus = adapter.repairBonus;
    }

    public int getAdditionalDamage() {
        return additionalDamage;
    }

    public void setAdditionalDamage(int additionalDamage) {
        this.additionalDamage = additionalDamage;
    }

    @Override
    public ItemStack merge(RecipeData<?> recipeData, @Nullable Player player, @Nullable Block block, CustomItem customResult, ItemStack result) {
        int totalDurability = 0;
        int maxDur = result.getType().getMaxDurability();
        for (IngredientData data : recipeData.getBySlots(slots)) {
            if (data.itemStack().getItemMeta() instanceof Damageable damageable) {
                totalDurability += maxDur - damageable.getDamage();
            }
        }
        var meta = result.getItemMeta();
        int totalDamage = Math.max((maxDur - totalDurability) + additionalDamage - (repairBonus ? (int) Math.floor(maxDur / 20d) : 0), 0);
        ((Damageable) meta).setDamage(Math.min(totalDamage, maxDur));
        result.setItemMeta(meta);
        return result;
    }

    @Override
    public MergeAdapter clone() {
        return new DamageMergeAdapter(this);
    }
}
