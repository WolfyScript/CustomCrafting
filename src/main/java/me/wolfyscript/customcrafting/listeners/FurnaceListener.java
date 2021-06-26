package me.wolfyscript.customcrafting.listeners;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.Registry;
import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.types.CustomCookingRecipe;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.customcrafting.utils.recipe_item.Result;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.nms.inventory.RecipeType;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.inventory.InventoryUtils;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.block.Furnace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class FurnaceListener implements Listener {

    private final CustomCrafting customCrafting;
    private final List<InventoryType> invs = Arrays.asList(InventoryType.FURNACE, InventoryType.BLAST_FURNACE, InventoryType.SMOKER);

    public FurnaceListener(CustomCrafting customCrafting) {
        this.customCrafting = customCrafting;
    }

    @EventHandler
    public void onInvClick(InventoryClickEvent event) {
        if (event.getClickedInventory() != null && invs.contains(event.getClickedInventory().getType()) && event.getSlotType().equals(InventoryType.SlotType.FUEL)) {
            var location = event.getInventory().getLocation();
            if (event.getCursor() == null) return;
            Optional<CustomItem> fuelItem = me.wolfyscript.utilities.util.Registry.CUSTOM_ITEMS.values().parallelStream().filter(customItem -> customItem.getBurnTime() > 0 && customItem.isSimilar(event.getCursor())).findFirst();
            if (fuelItem.isPresent()) {
                if (fuelItem.get().getAllowedBlocks().contains(location != null ? location.getBlock().getType() : Material.FURNACE)) {
                    InventoryUtils.calculateClickedSlot(event);
                } else {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onBurn(FurnaceBurnEvent event) {
        ItemStack input = event.getFuel();
        for (CustomItem customItem : me.wolfyscript.utilities.util.Registry.CUSTOM_ITEMS.values()) {
            if (customItem.getBurnTime() > 0 && customItem.isSimilar(input) && customItem.getAllowedBlocks().contains(event.getBlock().getType())) {
                event.setCancelled(false);
                event.setBurning(true);
                event.setBurnTime(customItem.getBurnTime());
                break;
            }
        }
    }

    @EventHandler
    public void onSmelt(FurnaceSmeltEvent event) {
        var block = event.getBlock();
        var furnace = (Furnace) block.getState();
        FurnaceInventory inventory = furnace.getInventory();
        ItemStack currentResultItem = furnace.getInventory().getResult();
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
                if (!customCrafting.getDataHandler().getDisabledRecipes().contains(namespacedKey)) {
                    var internalKey = NamespacedKeyUtils.toInternal(namespacedKey);
                    if (Registry.RECIPES.has(internalKey) && Registry.RECIPES.get(internalKey) instanceof CustomCookingRecipe<?, ?> cookingRecipe && cookingRecipe.validType(block.getType())) {
                        if (cookingRecipe.checkConditions(new Conditions.Data(null, block, null))) {
                            event.setCancelled(false);
                            Result<?> result = cookingRecipe.getResult();
                            if (result.size() > 1) {
                                CustomItem item = result.getItem(block).orElse(new CustomItem(Material.AIR));
                                if (currentResultItem != null) {
                                    int nextAmount = currentResultItem.getAmount() + item.getAmount();
                                    if ((item.isSimilar(currentResultItem)) && nextAmount <= currentResultItem.getMaxStackSize() && !ItemUtils.isAirOrNull(inventory.getSmelting())) {
                                        inventory.getSmelting().setAmount(inventory.getSmelting().getAmount() - 1);
                                        currentResultItem.setAmount(nextAmount);
                                        result.executeExtensions(block.getLocation(), true, null);
                                        result.removeCachedItem(block);
                                    }
                                    event.setCancelled(true);
                                } else {
                                    event.setResult(item.create());
                                    result.executeExtensions(block.getLocation(), true, null);
                                    result.removeCachedItem(block);
                                }
                            }
                            break;
                        }
                        event.setCancelled(true);
                    }
                } else {
                    event.setCancelled(true);
                }
            }
        }
    }
}
