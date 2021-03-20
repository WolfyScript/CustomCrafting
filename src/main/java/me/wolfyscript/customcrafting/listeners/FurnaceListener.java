package me.wolfyscript.customcrafting.listeners;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.types.CustomCookingRecipe;
import me.wolfyscript.customcrafting.recipes.types.CustomRecipe;
import me.wolfyscript.customcrafting.recipes.types.blast_furnace.CustomBlastRecipe;
import me.wolfyscript.customcrafting.recipes.types.furnace.CustomFurnaceRecipe;
import me.wolfyscript.customcrafting.recipes.types.smoker.CustomSmokerRecipe;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.Registry;
import me.wolfyscript.utilities.util.inventory.InventoryUtils;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Furnace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class FurnaceListener implements Listener {

    private final CustomCrafting customCrafting;
    private final List<InventoryType> invs = Arrays.asList(InventoryType.FURNACE, InventoryType.BLAST_FURNACE, InventoryType.SMOKER);

    public FurnaceListener(CustomCrafting customCrafting) {
        this.customCrafting = customCrafting;
    }

    @EventHandler
    public void onInvClick(InventoryClickEvent event) {
        if (event.getClickedInventory() != null && invs.contains(event.getClickedInventory().getType())) {
            if (event.getSlotType().equals(InventoryType.SlotType.FUEL)) {
                Material material = Material.FURNACE;
                Location location = event.getInventory().getLocation();
                if (location != null) {
                    material = location.getBlock().getType();
                }
                if (event.getCursor() == null) return;
                Optional<CustomItem> fuelItem = Registry.CUSTOM_ITEMS.values().parallelStream().filter(customItem -> customItem.getBurnTime() > 0 && customItem.isSimilar(event.getCursor())).findFirst();
                if (fuelItem.isPresent()) {
                    if (fuelItem.get().getAllowedBlocks().contains(material)) {
                        InventoryUtils.calculateClickedSlot(event);
                    } else {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onBurn(FurnaceBurnEvent event) {
        ItemStack input = event.getFuel();
        for (CustomItem customItem : Registry.CUSTOM_ITEMS.values()) {
            if (customItem.getBurnTime() > 0) {
                if (customItem.isSimilar(input)) {
                    if (customItem.getAllowedBlocks().contains(event.getBlock().getType())) {
                        event.setCancelled(false);
                        event.setBurning(true);
                        event.setBurnTime(customItem.getBurnTime());
                        break;
                    }
                }
            }
        }
    }

    @EventHandler
    public void onSmelt(FurnaceSmeltEvent event) {
        Furnace furnace = (Furnace) event.getBlock().getState();
        FurnaceInventory inventory = furnace.getInventory();
        ItemStack currentResultItem = furnace.getInventory().getResult();
        final Class<? extends Recipe> type;
        switch (furnace.getType()) {
            case BLAST_FURNACE:
                type = BlastingRecipe.class;
                break;
            case SMOKER:
                type = SmokingRecipe.class;
                break;
            default:
                type = FurnaceRecipe.class;
        }
        List<Recipe> recipes = Bukkit.getRecipesFor(event.getResult()).stream().filter(recipe -> type.isInstance(recipe) && recipe.getResult().isSimilar(event.getResult())).collect(Collectors.toList());
        for (Recipe recipe : recipes) {
            if (!(recipe instanceof Keyed)) continue;
            NamespacedKey namespacedKey = NamespacedKey.of(((Keyed) recipe).getKey());
            if (customCrafting.getRecipeHandler().getDisabledRecipes().contains(namespacedKey)) {
                event.setCancelled(true);
                continue;
            }
            CustomCookingRecipe<?, ?> customRecipe = (CustomCookingRecipe<?, ?>) me.wolfyscript.customcrafting.Registry.RECIPES.get(namespacedKey);
            if (isRecipeValid(event.getBlock().getType(), customRecipe)) {
                if (customRecipe.getConditions().checkConditions(customRecipe, new Conditions.Data(null, event.getBlock(), null))) {
                    event.setCancelled(false);
                    if (customRecipe.getResult().size() > 1) {
                        CustomItem item = customRecipe.getResult().getItem().orElse(new CustomItem(Material.AIR));
                        if (currentResultItem == null) {
                            event.setResult(item.create());
                            break;
                        }
                        int nextAmount = currentResultItem.getAmount() + item.getAmount();
                        if ((item.isSimilar(currentResultItem)) && nextAmount <= currentResultItem.getMaxStackSize() && !ItemUtils.isAirOrNull(inventory.getSmelting())) {
                            inventory.getSmelting().setAmount(inventory.getSmelting().getAmount() - 1);
                            currentResultItem.setAmount(nextAmount);
                        }
                        event.setCancelled(true);
                    }
                    break;
                } else {
                    event.setCancelled(true);
                }
            }
        }
    }

    private boolean isRecipeValid(Material furnaceType, CustomRecipe<?, ?> recipe) {
        if (recipe instanceof CustomCookingRecipe) {
            switch (furnaceType) {
                case BLAST_FURNACE:
                    return recipe instanceof CustomBlastRecipe;
                case SMOKER:
                    return recipe instanceof CustomSmokerRecipe;
                case FURNACE:
                    return recipe instanceof CustomFurnaceRecipe;
            }
        }
        return false;
    }


}
