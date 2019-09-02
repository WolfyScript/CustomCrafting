package me.wolfyscript.customcrafting.gui.main_gui.buttons;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.PlayerCache;
import me.wolfyscript.customcrafting.data.cache.Stonecutter;
import me.wolfyscript.customcrafting.items.CustomItem;
import me.wolfyscript.utilities.api.inventory.GuiHandler;
import me.wolfyscript.utilities.api.inventory.button.ButtonActionRender;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.buttons.ItemInputButton;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StonecutterContainerButton extends ItemInputButton {

    public StonecutterContainerButton(int inputSlot) {
        super("stonecutter.container_" + inputSlot, new ButtonState("", Material.AIR, new ButtonActionRender() {
            @Override
            public boolean run(GuiHandler guiHandler, Player player, Inventory inventory, int slot, InventoryClickEvent event) {
                PlayerCache cache = CustomCrafting.getPlayerCache(player);
                Stonecutter stonecutter = cache.getStonecutter();
                if (inputSlot == 1) {
                    //RESULT STUFF
                } else {
                    if (event.isRightClick() && event.isShiftClick()) {
                        List<CustomItem> variants = new ArrayList<>();
                        if (stonecutter.getSource() != null) {
                            variants = stonecutter.getSource();
                        }
                        cache.getVariantsData().setSlot(inputSlot);
                        cache.getVariantsData().setVariants(variants);
                        guiHandler.changeToInv("variants");
                        return true;
                    } else {
                        Bukkit.getScheduler().runTask(CustomCrafting.getInst(), () -> stonecutter.setSource(0, inventory.getItem(slot) != null && !inventory.getItem(slot).getType().equals(Material.AIR) ? CustomItem.getByItemStack(inventory.getItem(slot)) : new CustomItem(Material.AIR)));
                    }
                }
                return false;
            }

            @Override
            public ItemStack render(HashMap<String, Object> hashMap, GuiHandler guiHandler, Player player, ItemStack itemStack, int i, boolean b) {
                Stonecutter stonecutter = CustomCrafting.getPlayerCache(player).getStonecutter();
                if (inputSlot == 1) {
                    //RESULT STUFF
                } else {
                    if (stonecutter.getSource() != null && !stonecutter.getSource().isEmpty()) {
                        itemStack = stonecutter.getSource().get(0);
                    }
                }
                return itemStack;
            }
        }));
    }
}
