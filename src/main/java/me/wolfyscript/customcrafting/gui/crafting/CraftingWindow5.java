package me.wolfyscript.customcrafting.gui.crafting;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.PlayerCache;
import me.wolfyscript.customcrafting.data.cache.EliteWorkbench;
import me.wolfyscript.customcrafting.gui.ExtendedGuiWindow;
import me.wolfyscript.customcrafting.gui.crafting.buttons.CraftingSlotButton;
import me.wolfyscript.customcrafting.gui.crafting.buttons.ResultSlotButton;
import me.wolfyscript.utilities.api.inventory.GuiUpdateEvent;
import me.wolfyscript.utilities.api.inventory.InventoryAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

public class CraftingWindow5 extends ExtendedGuiWindow {

    public CraftingWindow5(InventoryAPI inventoryAPI) {
        super("crafting_grid5", inventoryAPI, 45);
    }

    @Override
    public void onInit() {
        for (int i = 0; i < 25; i++) {
            registerButton(new CraftingSlotButton(i));
        }
        registerButton(new ResultSlotButton());
    }

    @EventHandler
    public void onUpdate(GuiUpdateEvent event) {
        if (event.verify(this)) {
            PlayerCache cache = CustomCrafting.getPlayerCache(event.getPlayer());
            EliteWorkbench eliteWorkbench = cache.getEliteWorkbench();
            if (eliteWorkbench.getContents() == null || eliteWorkbench.getCurrentGridSize() <= 0) {
                eliteWorkbench.setCurrentGridSize(5);
                eliteWorkbench.setContents(new ItemStack[25]);
            }
            event.setButton(18, "crafting", "knowledge_book");
            int slot;
            for (int i = 0; i < 25; i++) {
                slot = 1 + i + (i / 5) * 4;
                event.setButton(slot, "crafting.slot_" + i);
            }
            event.setButton(25, "result_slot");
        }
    }


}
