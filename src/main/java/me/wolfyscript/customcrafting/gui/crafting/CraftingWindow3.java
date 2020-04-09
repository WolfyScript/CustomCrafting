package me.wolfyscript.customcrafting.gui.crafting;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.data.cache.EliteWorkbench;
import me.wolfyscript.customcrafting.gui.ExtendedGuiWindow;
import me.wolfyscript.customcrafting.gui.crafting.buttons.CraftingSlotButton;
import me.wolfyscript.customcrafting.gui.crafting.buttons.ResultSlotButton;
import me.wolfyscript.utilities.api.inventory.GuiUpdateEvent;
import me.wolfyscript.utilities.api.inventory.InventoryAPI;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.buttons.DummyButton;
import me.wolfyscript.utilities.api.inventory.events.GuiCloseEvent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;

public class CraftingWindow3 extends ExtendedGuiWindow {

    public CraftingWindow3(InventoryAPI inventoryAPI, CustomCrafting customCrafting) {
        super("crafting_grid3", inventoryAPI, 27, customCrafting);
    }

    @Override
    public void onInit() {
        for (int i = 0; i < 9; i++) {
            registerButton(new CraftingSlotButton(i));
        }
        registerButton(new ResultSlotButton());

        registerButton(new DummyButton("texture_dark", new ButtonState("none", "background", Material.BLACK_STAINED_GLASS_PANE, 9013, null)));
        registerButton(new DummyButton("texture_light", new ButtonState("none", "background", Material.BLACK_STAINED_GLASS_PANE, 9003, null)));
    }

    @EventHandler
    public void onUpdate(GuiUpdateEvent event) {
        if (event.verify(this)) {
            TestCache cache = (TestCache) event.getGuiHandler().getCustomCache();
            EliteWorkbench eliteWorkbench = cache.getEliteWorkbench();
            if (eliteWorkbench.getContents() == null || eliteWorkbench.getCurrentGridSize() <= 0) {
                eliteWorkbench.setCurrentGridSize(3);
                eliteWorkbench.setContents(new ItemStack[9]);
            }
            event.setButton(9, "crafting", "knowledge_book");
            int slot;
            for (int i = 0; i < 9; i++) {
                slot = 2 + i + (i / 3) * 6;
                event.setButton(slot, "crafting.slot_" + i);
            }
            event.setButton(16, "result_slot");
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onUpdateMain(GuiUpdateEvent event) {
        if (event.getGuiHandler().getCurrentInv() != null) {
            if (event.getGuiHandler().getCurrentInv().getNamespace().startsWith("crafting_grid")) {
                for (int i = 0; i < event.getGuiHandler().getCurrentInv().getSize() - 1; i++) {
                    event.setButton(i, "none", "glass_black");
                }
                event.setButton(event.getGuiHandler().getCurrentInv().getSize() - 1, CustomCrafting.getPlayerStatistics(event.getPlayer()).getDarkMode() ? "texture_dark" : "texture_light");
            }
        }
    }

    @EventHandler
    public void onUpdate(GuiCloseEvent event) {
        if (event.getGuiCluster().equals("crafting")) {
            Player player = (Player) event.getPlayer();
            TestCache cache = (TestCache) event.getGuiHandler().getCustomCache();
            EliteWorkbench eliteWorkbench = cache.getEliteWorkbench();
            if (eliteWorkbench.getContents() != null) {
                for (ItemStack itemStack : eliteWorkbench.getContents()) {
                    if (itemStack != null && !itemStack.getType().equals(Material.AIR)) {
                        player.getInventory().addItem(itemStack);
                    }
                }
            }
            eliteWorkbench.setEliteWorkbenchData(null);
            eliteWorkbench.setResult(new ItemStack(Material.AIR));
            eliteWorkbench.setContents(null);
            eliteWorkbench.setCurrentGridSize(0);
        }
    }

}
