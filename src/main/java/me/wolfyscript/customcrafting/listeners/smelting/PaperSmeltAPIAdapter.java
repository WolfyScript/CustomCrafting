package me.wolfyscript.customcrafting.listeners.smelting;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.Furnace;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.ItemStack;

public class PaperSmeltAPIAdapter extends SmeltAPIAdapter {

    public PaperSmeltAPIAdapter(CustomCrafting customCrafting) {
        super(customCrafting);
    }

    @Override
    public void process(FurnaceSmeltEvent event, Block block, Furnace furnace, FurnaceInventory inventory, ItemStack currentResultItem) {
        var recipe = event.getRecipe();
        if (recipe != null) {
            var namespacedKey = NamespacedKey.fromBukkit(recipe.getKey());
            if (!customCrafting.getDisableRecipesHandler().getRecipes().contains(namespacedKey)) {
                processRecipe(event, NamespacedKeyUtils.toInternal(namespacedKey), block, inventory, currentResultItem);
            } else {
                event.setCancelled(true);
            }
        }
    }
}
