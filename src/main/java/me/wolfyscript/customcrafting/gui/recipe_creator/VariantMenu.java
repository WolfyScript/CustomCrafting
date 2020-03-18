package me.wolfyscript.customcrafting.gui.recipe_creator;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.gui.ExtendedGuiWindow;
import me.wolfyscript.customcrafting.gui.recipe_creator.buttons.VariantContainerButton;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.GuiUpdateEvent;
import me.wolfyscript.utilities.api.inventory.InventoryAPI;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.buttons.ActionButton;
import me.wolfyscript.utilities.api.utils.ItemUtils;
import org.bukkit.event.EventHandler;

import java.util.List;

public class VariantMenu extends ExtendedGuiWindow {

    public VariantMenu(InventoryAPI inventoryAPI, CustomCrafting customCrafting) {
        super("variants", inventoryAPI, 54, customCrafting);
    }

    @Override
    public void onInit() {
        for (int i = 0; i < 45; i++) {
            registerButton(new VariantContainerButton(i));
        }
        registerButton(new ActionButton("back", new ButtonState("none", "back", WolfyUtilities.getCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODY0Zjc3OWE4ZTNmZmEyMzExNDNmYTY5Yjk2YjE0ZWUzNWMxNmQ2NjllMTljNzVmZDFhN2RhNGJmMzA2YyJ9fX0="), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            TestCache cache = (TestCache) guiHandler.getCustomCache();
            int resultSlot = 9;
            switch (cache.getSetting()) {
                case ELITE_WORKBENCH:
                    resultSlot = 36;
                case WORKBENCH:
                    if (cache.getVariantsData().getSlot() == resultSlot) {
                        List<CustomItem> items = cache.getVariantsData().getVariants();
                        items.removeIf(item -> ItemUtils.isAirOrNull(item));
                        cache.getCraftConfig().setResult(items);
                    } else {
                        cache.getCraftConfig().setIngredients(cache.getVariantsData().getSlot(), cache.getVariantsData().getVariants());
                    }
                    break;
                case ANVIL:
                    List<CustomItem> items = cache.getVariantsData().getVariants();
                    items.removeIf(item -> ItemUtils.isAirOrNull(item));
                    cache.getAnvilConfig().setInput(cache.getVariantsData().getSlot(), cache.getVariantsData().getVariants());
                    break;
                case STONECUTTER:
                    if (cache.getVariantsData().getSlot() != 1) {
                        cache.getStonecutterConfig().setSource(cache.getVariantsData().getVariants());
                    }
                    break;
                case FURNACE:
                case SMOKER:
                case BLAST_FURNACE:
                case CAMPFIRE:
                    if (cache.getVariantsData().getSlot() != 1) {
                        cache.getCookingConfig().setSource(cache.getVariantsData().getVariants());
                    }else{
                        cache.getCookingConfig().setResult(cache.getVariantsData().getVariants());
                    }
                    break;
                case CAULDRON:
                    List<CustomItem> variants = cache.getVariantsData().getVariants();
                    variants.removeIf(item -> ItemUtils.isAirOrNull(item));
                    if (cache.getVariantsData().getSlot() == 0) {
                        cache.getCauldronConfig().setIngredients(variants);
                    } else {
                        cache.getCauldronConfig().setResult(variants);
                    }

            }
            guiHandler.openPreviousInv();
            return true;
        })));
    }

    @EventHandler
    public void onUpdate(GuiUpdateEvent event) {
        if (event.verify(this)) {
            event.setButton(0, "back");
            for (int i = 0; i < 45; i++) {
                event.setButton(9 + i, "variant_container_" + i);
            }
        }
    }
}
