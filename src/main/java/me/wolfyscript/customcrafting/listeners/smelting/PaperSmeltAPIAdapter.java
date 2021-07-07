package me.wolfyscript.customcrafting.listeners.smelting;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.Registry;
import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.types.CustomCookingRecipe;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
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

public class PaperSmeltAPIAdapter extends SmeltAPIAdapter {

    public PaperSmeltAPIAdapter(CustomCrafting customCrafting) {
        super(customCrafting);
    }

    @Override
    public void process(FurnaceSmeltEvent event, Block block, Furnace furnace, FurnaceInventory inventory, ItemStack currentResultItem) {
        //TODO: Replace with custom recipe calculation thanks to event.getSource()!
        var recipe = event.getRecipe();
        if (recipe != null) {
            var namespacedKey = NamespacedKey.fromBukkit(recipe.getKey());
            if (!customCrafting.getDataHandler().getDisabledRecipes().contains(namespacedKey)) {
                var internalKey = NamespacedKeyUtils.toInternal(namespacedKey);
                if (Registry.RECIPES.get(internalKey) instanceof CustomCookingRecipe<?, ?> cookingRecipe && cookingRecipe.validType(block.getType())) {
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
                        return;
                    }
                    event.setCancelled(true);
                }
            } else {
                event.setCancelled(true);
            }
        }
    }
}
