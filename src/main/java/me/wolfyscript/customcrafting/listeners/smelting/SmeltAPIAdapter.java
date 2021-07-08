package me.wolfyscript.customcrafting.listeners.smelting;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.Registry;
import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.types.CustomCookingRecipe;
import me.wolfyscript.customcrafting.utils.recipe_item.Result;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Furnace;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.ItemStack;

public abstract class SmeltAPIAdapter {

    protected CustomCrafting customCrafting;

    protected SmeltAPIAdapter(CustomCrafting customCrafting) {
        this.customCrafting = customCrafting;
    }

    public abstract void process(FurnaceSmeltEvent event, Block block, Furnace furnace, FurnaceInventory inventory, ItemStack currentResultItem);

    protected boolean processRecipe(FurnaceSmeltEvent event, NamespacedKey recipeKey, Block block, FurnaceInventory inventory, ItemStack currentResultItem) {
        if (Registry.RECIPES.get(recipeKey) instanceof CustomCookingRecipe<?, ?> cookingRecipe && cookingRecipe.validType(block.getType())) {
            if (cookingRecipe.checkConditions(new Conditions.Data(null, block, null))) {
                event.setCancelled(false);
                Result<?> result = cookingRecipe.getResult();
                if (result.size() > 1) {
                    CustomItem item = result.getItem(block).orElse(new CustomItem(Material.AIR));
                    if (currentResultItem != null) {
                        int nextAmount = currentResultItem.getAmount() + item.getAmount();
                        if (nextAmount <= currentResultItem.getMaxStackSize() && !ItemUtils.isAirOrNull(inventory.getSmelting())) {
                            ItemStack clonedCurrentItem = currentResultItem.clone();
                            clonedCurrentItem.setAmount(item.getAmount());
                            if (item.isSimilar(clonedCurrentItem)) {
                                inventory.getSmelting().setAmount(inventory.getSmelting().getAmount() - 1);
                                currentResultItem.setAmount(nextAmount);
                                result.executeExtensions(block.getLocation(), true, null);
                                result.removeCachedItem(block);
                            }
                        }
                        event.setCancelled(true);
                    } else {
                        event.setResult(item.create());
                        result.executeExtensions(block.getLocation(), true, null);
                        result.removeCachedItem(block);
                    }
                }
                return true;
            }
            event.setCancelled(true);
        }
        return false;
    }

}
