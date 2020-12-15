package me.wolfyscript.customcrafting.gui.recipe_creator.buttons;

import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class BrewingOptionButton extends ActionButton<TestCache> {

    public BrewingOptionButton(String id, Material material, String option) {
        super(id, material, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            guiHandler.getCustomCache().getBrewingGUICache().setOption(option);
            return true;
        });
    }

    public BrewingOptionButton(Material material, String option) {
        super(option + ".option", material, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            guiHandler.getCustomCache().getBrewingGUICache().setOption(option);
            return true;
        });
    }

    public BrewingOptionButton(ItemStack itemStack, String option) {
        super(option + ".option", itemStack, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            guiHandler.getCustomCache().getBrewingGUICache().setOption(option);
            return true;
        });
    }
}
