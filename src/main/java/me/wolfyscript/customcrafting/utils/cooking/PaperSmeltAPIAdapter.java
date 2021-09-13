package me.wolfyscript.customcrafting.utils.cooking;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.data.CookingRecipeData;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.Pair;
import org.bukkit.Keyed;
import org.bukkit.block.Block;
import org.bukkit.block.Furnace;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.ItemStack;

public class PaperSmeltAPIAdapter extends SmeltAPIAdapter {

    public PaperSmeltAPIAdapter(CustomCrafting customCrafting, CookingManager manager) {
        super(customCrafting, manager);
    }

    @Override
    public Pair<CookingRecipeData<?>, Boolean> process(FurnaceSmeltEvent event, Block block, Furnace furnace) {
        var recipe = event.getRecipe();
        if (recipe != null && recipe.getKey().getNamespace().equals(NamespacedKeyUtils.NAMESPACE)) {
            return processRecipe(event.getSource(), NamespacedKeyUtils.toInternal(NamespacedKey.fromBukkit(recipe.getKey())), block);
        }
        return new Pair<>(null, false);
    }
}
