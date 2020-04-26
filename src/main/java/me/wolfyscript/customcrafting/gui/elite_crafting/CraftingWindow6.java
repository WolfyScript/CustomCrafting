package me.wolfyscript.customcrafting.gui.elite_crafting;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.data.cache.EliteWorkbench;
import me.wolfyscript.customcrafting.gui.ExtendedGuiWindow;
import me.wolfyscript.customcrafting.gui.elite_crafting.buttons.CraftingSlotButton;
import me.wolfyscript.customcrafting.gui.elite_crafting.buttons.ResultSlotButton;
import me.wolfyscript.utilities.api.inventory.GuiUpdateEvent;
import me.wolfyscript.utilities.api.inventory.InventoryAPI;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.buttons.DummyButton;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

public class CraftingWindow6 extends ExtendedGuiWindow {

    public CraftingWindow6(InventoryAPI inventoryAPI, CustomCrafting customCrafting) {
        super("crafting_grid6", inventoryAPI, 54, customCrafting);
    }

    @Override
    public void onInit() {
        for (int i = 0; i < 36; i++) {
            registerButton(new CraftingSlotButton(i, customCrafting));
        }
        registerButton(new ResultSlotButton(customCrafting));
        registerButton(new DummyButton("texture_light", new ButtonState("none", "background", Material.BLACK_STAINED_GLASS_PANE, 9006, null)));
        registerButton(new DummyButton("texture_dark", new ButtonState("none", "background", Material.BLACK_STAINED_GLASS_PANE, 9016, null)));
    }

    @EventHandler
    public void onUpdate(GuiUpdateEvent event) {
        if (event.verify(this)) {
            TestCache cache = (TestCache) event.getGuiHandler().getCustomCache();
            EliteWorkbench eliteWorkbench = cache.getEliteWorkbench();
            if (eliteWorkbench.getContents() == null || eliteWorkbench.getCurrentGridSize() <= 0) {
                eliteWorkbench.setCurrentGridSize(6);
                eliteWorkbench.setContents(new ItemStack[36]);
            }

            event.setButton(16, "crafting", "knowledge_book");
            int slot;
            for (int i = 0; i < 36; i++) {
                slot = i + (i / 6) * 3;
                event.setButton(slot, "crafting.slot_" + i);
            }
            event.setButton(43, "result_slot");


        }
    }
}
