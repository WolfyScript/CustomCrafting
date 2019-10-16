package me.wolfyscript.customcrafting.gui.main_gui;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.PlayerCache;
import me.wolfyscript.customcrafting.gui.ExtendedGuiWindow;
import me.wolfyscript.customcrafting.gui.main_gui.buttons.VariantContainerButton;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.GuiUpdateEvent;
import me.wolfyscript.utilities.api.inventory.InventoryAPI;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.buttons.ActionButton;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;

import java.util.List;

public class VariantMenu extends ExtendedGuiWindow {

    public VariantMenu(InventoryAPI inventoryAPI) {
        super("variants", inventoryAPI, 54);
    }

    @Override
    public void onInit() {
        for (int i = 0; i < 45; i++) {
            registerButton(new VariantContainerButton(i));
        }
        registerButton(new ActionButton("back", new ButtonState("none", "back", WolfyUtilities.getCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODY0Zjc3OWE4ZTNmZmEyMzExNDNmYTY5Yjk2YjE0ZWUzNWMxNmQ2NjllMTljNzVmZDFhN2RhNGJmMzA2YyJ9fX0="), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            PlayerCache cache = CustomCrafting.getPlayerCache(player);
            switch (cache.getSetting()) {
                case WORKBENCH:
                    if (cache.getVariantsData().getSlot() == 9) {
                        List<CustomItem> items = cache.getVariantsData().getVariants();
                        items.removeIf(item -> item.getType().equals(Material.AIR));
                        System.out.println("List: " + items);
                        cache.getWorkbench().setResult(items);
                    } else {
                        cache.getWorkbench().setIngredients(cache.getVariantsData().getSlot(), cache.getVariantsData().getVariants());
                    }
                    break;
                case ANVIL:

                    List<CustomItem> items = cache.getVariantsData().getVariants();
                    items.removeIf(item -> item.getType().equals(Material.AIR));

                    cache.getAnvil().setIngredient(cache.getVariantsData().getSlot(), cache.getVariantsData().getVariants());

                    break;
                case STONECUTTER:
                    if (cache.getVariantsData().getSlot() != 1) {
                        cache.getStonecutter().setSource(cache.getVariantsData().getVariants());
                    }
                    break;
                case FURNACE:
                case SMOKER:
                case BLAST_FURNACE:
                case CAMPFIRE:
                    if (cache.getVariantsData().getSlot() != 1) {
                        cache.getCookingData().setSource(cache.getVariantsData().getVariants());
                    }
                    break;
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
