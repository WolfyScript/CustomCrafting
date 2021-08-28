package me.wolfyscript.customcrafting.gui.elite_crafting;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.EliteWorkbench;
import me.wolfyscript.customcrafting.gui.CCWindow;
import me.wolfyscript.customcrafting.gui.main_gui.ClusterMain;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.DummyButton;
import me.wolfyscript.utilities.api.nms.inventory.GUIInventory;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

abstract class CraftingWindow extends CCWindow {

    protected static final String RESULT = "result_slot";
    protected final int gridSize;

    protected CraftingWindow(GuiCluster<CCCache> cluster, String namespace, int size, CustomCrafting customCrafting, int gridSize) {
        super(cluster, namespace, size, customCrafting);
        setForceSyncUpdate(true);
        this.gridSize = gridSize;
    }

    @Override
    public void onInit() {
        for (int i = 0; i < gridSize * gridSize; i++) {
            registerButton(new ButtonSlotCrafting(i, customCrafting));
        }
        registerButton(new ButtonSlotResult(customCrafting));
        registerButton(new DummyButton<>("texture_dark", new ButtonState<>(ClusterMain.BACKGROUND, Material.BLACK_STAINED_GLASS_PANE)));
        registerButton(new DummyButton<>("texture_light", new ButtonState<>(ClusterMain.BACKGROUND, Material.BLACK_STAINED_GLASS_PANE)));
    }

    @Override
    public void onUpdateAsync(GuiUpdate<CCCache> update) {
        //Prevent super class from rendering
    }

    @Override
    public void onUpdateSync(GuiUpdate<CCCache> event) {
        for (int i = 0; i < getSize(); i++) {
            event.setButton(i, ClusterMain.GLASS_BLACK);
        }
        CCCache cache = event.getGuiHandler().getCustomCache();
        EliteWorkbench eliteWorkbench = cache.getEliteWorkbench();
        if (eliteWorkbench.getContents() == null || eliteWorkbench.getCurrentGridSize() <= 0) {
            eliteWorkbench.setCurrentGridSize((byte) gridSize);
            eliteWorkbench.setContents(new ItemStack[gridSize * gridSize]);
        }
        int slot;
        for (int i = 0; i < gridSize * gridSize; i++) {
            slot = getGridX() + i + (i / gridSize) * (9 - gridSize);
            event.setButton(slot, "crafting.slot_" + i);
        }
    }

    public abstract int getGridX();

    @Override
    public boolean onClose(GuiHandler<CCCache> guiHandler, GUIInventory<CCCache> guiInventory, InventoryView transaction) {
        Player player = guiHandler.getPlayer();
        CCCache cache = guiHandler.getCustomCache();
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
        eliteWorkbench.setCurrentGridSize((byte) 0);
        return false;
    }

}
