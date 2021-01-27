package me.wolfyscript.customcrafting.gui.elite_crafting;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.EliteWorkbench;
import me.wolfyscript.customcrafting.gui.CCWindow;
import me.wolfyscript.customcrafting.utils.PlayerUtil;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.nms.inventory.GUIInventory;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public abstract class CraftingWindow extends CCWindow {

    public CraftingWindow(GuiCluster<CCCache> cluster, String namespace, int size, CustomCrafting customCrafting) {
        super(cluster, namespace, size, customCrafting);
        setForceSyncUpdate(true);
    }

    @Override
    public void onUpdateAsync(GuiUpdate<CCCache> update) {
        //Prevent super class from rendering
    }

    @Override
    public void onUpdateSync(GuiUpdate<CCCache> event) {
        for (int i = 0; i < getSize() - 1; i++) {
            event.setButton(i, new NamespacedKey("none", "glass_black"));
        }
        event.setButton(getSize() - 1, PlayerUtil.getStore(event.getPlayer()).isDarkMode() ? "texture_dark" : "texture_light");
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
        eliteWorkbench.setCurrentGridSize(0);
        return false;
    }

}
