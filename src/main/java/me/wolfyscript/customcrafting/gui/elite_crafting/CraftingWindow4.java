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

public class CraftingWindow4 extends ExtendedGuiWindow {

    public CraftingWindow4(InventoryAPI inventoryAPI, CustomCrafting customCrafting) {
        super("crafting_grid4", inventoryAPI, 36, customCrafting);
    }

    @Override
    public void onInit() {
        for (int i = 0; i < 16; i++) {
            registerButton(new CraftingSlotButton(i, customCrafting));
        }
        registerButton(new ResultSlotButton(customCrafting));
        registerButton(new DummyButton("texture_light", new ButtonState("none", "background", Material.BLACK_STAINED_GLASS_PANE, 9004, null)));
        registerButton(new DummyButton("texture_dark", new ButtonState("none", "background", Material.BLACK_STAINED_GLASS_PANE, 9014, null)));
    }

    @EventHandler
    public void onUpdate(GuiUpdateEvent event) {
        if (event.verify(this)) {
            TestCache cache = (TestCache) event.getGuiHandler().getCustomCache();
            EliteWorkbench eliteWorkbench = cache.getEliteWorkbench();
            if (eliteWorkbench.getContents() == null || eliteWorkbench.getCurrentGridSize() <= 0) {
                eliteWorkbench.setCurrentGridSize(4);
                eliteWorkbench.setContents(new ItemStack[16]);
            }

            event.setButton(15, "crafting", "knowledge_book");
            int slot;
            for (int i = 0; i < 16; i++) {
                slot = 1 + i + (i / 4) * 5;
                event.setButton(slot, "crafting.slot_" + i);
            }
            event.setButton(25, "result_slot");
        }
    }
}
