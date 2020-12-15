package me.wolfyscript.customcrafting.gui.recipe_creator;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.gui.ExtendedGuiWindow;
import me.wolfyscript.customcrafting.gui.recipe_creator.buttons.VariantContainerButton;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import me.wolfyscript.utilities.util.inventory.PlayerHeadUtils;
import org.bukkit.Material;

import java.util.List;
import java.util.stream.Collectors;

public class VariantMenu extends ExtendedGuiWindow {

    public VariantMenu(GuiCluster<TestCache> cluster, CustomCrafting customCrafting) {
        super(cluster, "variants", 54, customCrafting);
    }

    @Override
    public void onInit() {
        for (int i = 0; i < 45; i++) {
            registerButton(new VariantContainerButton(i, customCrafting));
        }
        registerButton(new ActionButton("back", new ButtonState("none", "back", PlayerHeadUtils.getViaValue("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODY0Zjc3OWE4ZTNmZmEyMzExNDNmYTY5Yjk2YjE0ZWUzNWMxNmQ2NjllMTljNzVmZDFhN2RhNGJmMzA2YyJ9fX0="), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            TestCache cache = (TestCache) guiHandler.getCustomCache();
            int resultSlot = 9;
            switch (cache.getRecipeType().getType()) {
                case ELITE_WORKBENCH:
                    resultSlot = 36;
                case WORKBENCH:
                    if (cache.getVariantsData().getSlot() == resultSlot) {
                        List<CustomItem> items = cache.getVariantsData().getVariants();
                        items.removeIf(item -> item == null || item.getItemStack().getType().equals(Material.AIR));
                        cache.getCraftingRecipe().setResult(items);
                    } else {
                        cache.getCraftingRecipe().setIngredients(cache.getVariantsData().getSlot(), cache.getVariantsData().getVariants());
                    }
                    break;
                case ANVIL:
                    List<CustomItem> items = cache.getVariantsData().getVariants();
                    items.removeIf(item -> item == null || item.getItemStack().getType().equals(Material.AIR));
                    cache.getAnvilRecipe().setInput(cache.getVariantsData().getSlot(), cache.getVariantsData().getVariants());
                    break;
                case STONECUTTER:
                    if (cache.getVariantsData().getSlot() != 1) {
                        cache.getStonecutterRecipe().setSource(cache.getVariantsData().getVariants());
                    }
                    break;
                case FURNACE:
                case SMOKER:
                case BLAST_FURNACE:
                case CAMPFIRE:
                    if (cache.getVariantsData().getSlot() != 1) {
                        cache.getCookingRecipe().setSource(cache.getVariantsData().getVariants());
                    } else {
                        cache.getCookingRecipe().setResult(cache.getVariantsData().getVariants());
                    }
                    break;
                case CAULDRON: {
                    List<CustomItem> variants = cache.getVariantsData().getVariants().stream().filter(item -> !ItemUtils.isAirOrNull(item)).collect(Collectors.toList());
                    if (cache.getVariantsData().getSlot() == 0) {
                        cache.getCauldronRecipe().setIngredients(variants);
                    } else {
                        cache.getCauldronRecipe().setResult(variants);
                    }
                }
                break;
                case BREWING_STAND:
                    List<CustomItem> variants = cache.getVariantsData().getVariants().stream().filter(item -> !ItemUtils.isAirOrNull(item)).collect(Collectors.toList());
                    switch (cache.getVariantsData().getSlot()) {
                        case 0:
                            cache.getBrewingRecipe().setIngredients(variants);
                            break;
                        case 1:
                            cache.getBrewingRecipe().setAllowedItems(variants);
                            break;
                        case 2:
                            cache.getBrewingRecipe().setResult(variants);
                            break;
                    }

            }
            guiHandler.openPreviousInv();
            return true;
        })));
    }

    @Override
    public void onUpdateAsync(GuiUpdate<TestCache> event) {
        super.onUpdateAsync(event);
        event.setButton(0, "back");
        for (int i = 0; i < 45; i++) {
            event.setButton(9 + i, "variant_container_" + i);
        }
    }
}
