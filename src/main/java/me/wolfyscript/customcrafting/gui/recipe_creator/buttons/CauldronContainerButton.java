package me.wolfyscript.customcrafting.gui.recipe_creator.buttons;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.PlayerCache;
import me.wolfyscript.customcrafting.recipes.types.anvil.AnvilConfig;
import me.wolfyscript.customcrafting.recipes.types.cauldron.CauldronConfig;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
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

public class CauldronContainerButton extends ItemInputButton {

    public CauldronContainerButton(int inputSlot) {
        super("cauldron.container_" + inputSlot, new ButtonState("", Material.AIR, new ButtonActionRender() {
            @Override
            public boolean run(GuiHandler guiHandler, Player player, Inventory inventory, int slot, InventoryClickEvent event) {
                PlayerCache cache = CustomCrafting.getPlayerCache(player);
                CauldronConfig cauldronConfig = cache.getCauldronConfig();
                if (event.isRightClick() && event.isShiftClick()) {
                    List<CustomItem> variants = new ArrayList<>();
                    if(inputSlot == 0 && cauldronConfig.getIngredients() != null){
                        variants = cauldronConfig.getIngredients();
                    }else if(cauldronConfig.getResult() != null){
                        variants = cauldronConfig.getResult();
                    }
                    cache.getVariantsData().setSlot(inputSlot);
                    cache.getVariantsData().setVariants(variants);
                    guiHandler.changeToInv("variants");
                    return true;
                } else {
                    Bukkit.getScheduler().runTask(CustomCrafting.getInst(), () -> {
                        CustomItem input = inventory.getItem(slot) != null && !inventory.getItem(slot).getType().equals(Material.AIR) ? CustomItem.getByItemStack(inventory.getItem(slot)) : new CustomItem(Material.AIR);
                        List<CustomItem> inputs = inputSlot == 0 ? cauldronConfig.getIngredients() : cauldronConfig.getResult();
                        if (inputs.size() > 0) {
                            inputs.set(0, input);
                        } else {
                            inputs.add(input);
                        }
                        if(inputSlot == 0){
                            cauldronConfig.setIngredients(inputs);
                        }else{
                            cauldronConfig.setResult(inputs);
                        }
                    });
                }
                return false;
            }

            @Override
            public ItemStack render(HashMap<String, Object> hashMap, GuiHandler guiHandler, Player player, ItemStack item, int i, boolean b) {
                CauldronConfig cauldronConfig = CustomCrafting.getPlayerCache(player).getCauldronConfig();
                List<CustomItem> items = inputSlot == 0 ? cauldronConfig.getIngredients() : cauldronConfig.getResult();
                if (items != null && !items.isEmpty()) {
                    item = items.get(0);
                }
                return item;
            }
        }));
    }
}
