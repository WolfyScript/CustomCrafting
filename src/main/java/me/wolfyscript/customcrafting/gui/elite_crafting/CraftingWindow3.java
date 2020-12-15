package me.wolfyscript.customcrafting.gui.elite_crafting;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.data.cache.EliteWorkbench;
import me.wolfyscript.customcrafting.gui.elite_crafting.buttons.CraftingSlotButton;
import me.wolfyscript.customcrafting.gui.elite_crafting.buttons.ResultSlotButton;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.DummyButton;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class CraftingWindow3 extends CraftingWindow {

    public CraftingWindow3(GuiCluster<TestCache> cluster, CustomCrafting customCrafting) {
        super(cluster, "crafting_grid3", 27, customCrafting);
    }

    @Override
    public void onInit() {
        for (int i = 0; i < 9; i++) {
            registerButton(new CraftingSlotButton(i, customCrafting));
        }
        registerButton(new ResultSlotButton(customCrafting));
        registerButton(new DummyButton("texture_dark", new ButtonState("none", "background", Material.BLACK_STAINED_GLASS_PANE, 9013)));
        registerButton(new DummyButton("texture_light", new ButtonState("none", "background", Material.BLACK_STAINED_GLASS_PANE, 9003)));
    }

    @Override
    public void onUpdateSync(GuiUpdate event) {
        super.onUpdateSync(event);

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
