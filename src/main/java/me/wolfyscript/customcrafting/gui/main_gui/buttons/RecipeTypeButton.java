package me.wolfyscript.customcrafting.gui.main_gui.buttons;

import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.gui.Setting;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.buttons.ActionButton;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class RecipeTypeButton extends ActionButton {

    public RecipeTypeButton(String id, Setting recipeType, ItemStack icon) {
        super(id, new ButtonState(id, icon, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).setSetting(recipeType);
            guiHandler.changeToInv("recipe_editor");
            return true;
        }));
    }

    public RecipeTypeButton(String id, Setting recipeType, Material icon) {
        this(id, recipeType, new ItemStack(icon));
    }
}
