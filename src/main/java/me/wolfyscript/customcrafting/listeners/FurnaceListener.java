package me.wolfyscript.customcrafting.listeners;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.listeners.smelting.BukkitSmeltAPIAdapter;
import me.wolfyscript.customcrafting.listeners.smelting.PaperSmeltAPIAdapter;
import me.wolfyscript.customcrafting.listeners.smelting.SmeltAPIAdapter;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.util.inventory.InventoryUtils;
import org.bukkit.Material;
import org.bukkit.block.Furnace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class FurnaceListener implements Listener {

    private final CustomCrafting customCrafting;
    private final SmeltAPIAdapter smeltAPIAdapter;
    private final List<InventoryType> invs = Arrays.asList(InventoryType.FURNACE, InventoryType.BLAST_FURNACE, InventoryType.SMOKER);

    public FurnaceListener(CustomCrafting customCrafting) {
        this.customCrafting = customCrafting;
        this.smeltAPIAdapter = customCrafting.isPaper() ? new PaperSmeltAPIAdapter(customCrafting) : new BukkitSmeltAPIAdapter(customCrafting);
    }

    @EventHandler
    public void onInvClick(InventoryClickEvent event) {
        if (event.getClickedInventory() != null && invs.contains(event.getClickedInventory().getType()) && event.getSlotType().equals(InventoryType.SlotType.FUEL)) {
            var location = event.getInventory().getLocation();
            if (event.getCursor() == null) return;
            Optional<CustomItem> fuelItem = me.wolfyscript.utilities.util.Registry.CUSTOM_ITEMS.values().parallelStream().filter(customItem -> customItem.getFuelSettings().getBurnTime() > 0 && customItem.isSimilar(event.getCursor())).findFirst();
            if (fuelItem.isPresent()) {
                if (fuelItem.get().getFuelSettings().getAllowedBlocks().contains(location != null ? location.getBlock().getType() : Material.FURNACE)) {
                    InventoryUtils.calculateClickedSlot(event);
                } else {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onBurn(FurnaceBurnEvent event) {
        ItemStack input = event.getFuel();
        for (CustomItem customItem : me.wolfyscript.utilities.util.Registry.CUSTOM_ITEMS.values()) {
            var fuelSettings = customItem.getFuelSettings();
            if (fuelSettings.getBurnTime() > 0 && customItem.isSimilar(input) && fuelSettings.getAllowedBlocks().contains(event.getBlock().getType())) {
                event.setCancelled(false);
                event.setBurning(true);
                event.setBurnTime(fuelSettings.getBurnTime());
                break;
            }
        }
    }

    @EventHandler
    public void onSmelt(FurnaceSmeltEvent event) {
        var block = event.getBlock();
        var furnace = (Furnace) block.getState();
        FurnaceInventory inventory = furnace.getInventory();
        ItemStack currentResultItem = inventory.getResult();
        smeltAPIAdapter.process(event, block, furnace, inventory, currentResultItem);
    }

}
