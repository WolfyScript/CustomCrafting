package me.wolfyscript.customcrafting.utils.cooking;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.data.CookingRecipeData;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.utilities.api.nms.inventory.RecipeType;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.Pair;
import org.bukkit.Keyed;
import org.bukkit.block.Block;
import org.bukkit.block.Furnace;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.inventory.CookingRecipe;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import java.util.Iterator;

public class BukkitSmeltAPIAdapter extends SmeltAPIAdapter {

    public BukkitSmeltAPIAdapter(CustomCrafting customCrafting, CookingManager manager) {
        super(customCrafting, manager);
    }

    @Override
    public Pair<CookingRecipeData<?>, Boolean> process(FurnaceSmeltEvent event, Block block, Furnace furnace) {
        Iterator<Recipe> recipeIterator = customCrafting.getApi().getNmsUtil().getRecipeUtil().recipeIterator(switch (furnace.getType()) {
            case BLAST_FURNACE -> RecipeType.BLASTING;
            case SMOKER -> RecipeType.SMOKING;
            default -> RecipeType.SMELTING;
        });
        boolean customRecipe = false;
        while (recipeIterator.hasNext()) {
            if (recipeIterator.next() instanceof CookingRecipe<?> recipe && recipe.getKey().getNamespace().equals(NamespacedKeyUtils.NAMESPACE) && recipe.getResult().isSimilar(event.getResult())) {
                customRecipe = true;
                Pair<CookingRecipeData<?>, Boolean> data = processRecipe(event.getSource(), NamespacedKeyUtils.toInternal(NamespacedKey.fromBukkit(recipe.getKey())), block);
                if (data.getKey() != null) {
                    return data;
                }
            }
        }
        return new Pair<>(null, customRecipe);
    }
}
