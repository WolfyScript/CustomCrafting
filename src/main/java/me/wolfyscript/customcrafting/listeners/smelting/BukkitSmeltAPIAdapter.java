package me.wolfyscript.customcrafting.listeners.smelting;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.utilities.api.nms.inventory.RecipeType;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.Keyed;
import org.bukkit.block.Block;
import org.bukkit.block.Furnace;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import java.util.Iterator;

public class BukkitSmeltAPIAdapter extends SmeltAPIAdapter {

    public BukkitSmeltAPIAdapter(CustomCrafting customCrafting) {
        super(customCrafting);
    }

    @Override
    public void process(FurnaceSmeltEvent event, Block block, Furnace furnace, FurnaceInventory inventory, ItemStack currentResultItem) {
        final RecipeType type = switch (furnace.getType()) {
            case BLAST_FURNACE -> RecipeType.BLASTING;
            case SMOKER -> RecipeType.SMOKING;
            default -> RecipeType.SMELTING;
        };
        Iterator<Recipe> recipeIterator = customCrafting.getApi().getNmsUtil().getRecipeUtil().recipeIterator(type);
        while (recipeIterator.hasNext()) {
            var recipe = recipeIterator.next();
            if (recipe instanceof Keyed keyed && recipe.getResult().isSimilar(event.getResult())) {
                var namespacedKey = NamespacedKey.fromBukkit(keyed.getKey());
                if (!customCrafting.getDisableRecipesHandler().getRecipes().contains(namespacedKey)) {
                    if (processRecipe(event, NamespacedKeyUtils.toInternal(namespacedKey), block, inventory, currentResultItem)) {
                        break;
                    }
                } else {
                    event.setCancelled(true);
                }
            }
        }
    }
}
