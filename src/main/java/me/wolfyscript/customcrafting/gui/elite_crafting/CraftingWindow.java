package me.wolfyscript.customcrafting.gui.elite_crafting;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.data.cache.EliteWorkbench;
import me.wolfyscript.customcrafting.gui.ExtendedGuiWindow;
import me.wolfyscript.utilities.api.inventory.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.InventoryAPI;
import me.wolfyscript.utilities.api.inventory.events.GuiCloseEvent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

public abstract class CraftingWindow extends ExtendedGuiWindow {

    public CraftingWindow(String namespace, int size, InventoryAPI inventoryAPI, CustomCrafting customCrafting) {
        super(namespace, inventoryAPI, size, customCrafting);
        setForceSyncUpdate(true);
    }

    @Override
    public void onUpdateAsync(GuiUpdate update) {
        //Prevent super class from rendering
    }

    @Override
    public void onUpdateSync(GuiUpdate event) {
        for (int i = 0; i < event.getGuiHandler().getCurrentInv().getSize() - 1; i++) {
            event.setButton(i, "none", "glass_black");
        }
        event.setButton(event.getGuiHandler().getCurrentInv().getSize() - 1, CustomCrafting.getPlayerStatistics(event.getPlayer()).getDarkMode() ? "texture_dark" : "texture_light");
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
