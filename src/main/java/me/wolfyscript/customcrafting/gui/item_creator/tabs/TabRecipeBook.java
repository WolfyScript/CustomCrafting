package me.wolfyscript.customcrafting.gui.item_creator.tabs;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.custom_data.RecipeBookData;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.items.Items;
import me.wolfyscript.customcrafting.data.cache.items.ItemsButtonAction;
import me.wolfyscript.customcrafting.gui.item_creator.ButtonOption;
import me.wolfyscript.customcrafting.gui.item_creator.MenuItemCreator;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ToggleButton;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class TabRecipeBook extends ItemCreatorTab {

    public static final String KEY = "knowledge_book";

    public TabRecipeBook() {
        super(new NamespacedKey(NamespacedKeyUtils.NAMESPACE, KEY));
    }

    @Override
    public void register(MenuItemCreator creator, WolfyUtilities api) {
        creator.registerButton(new ButtonOption(Material.KNOWLEDGE_BOOK, this));
        creator.registerButton(new ToggleButton<>("knowledge_book.toggle", (cache, guiHandler, player, guiInventory, i) -> ((RecipeBookData) cache.getItems().getItem().getCustomData(CustomCrafting.RECIPE_BOOK)).isEnabled(), new ButtonState<>("knowledge_book.toggle.enabled", Material.GREEN_CONCRETE, (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
            ((RecipeBookData) items.getItem().getCustomData(CustomCrafting.RECIPE_BOOK)).setEnabled(false);
            return true;
        }), new ButtonState<>("knowledge_book.toggle.disabled", Material.RED_CONCRETE, (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
            ((RecipeBookData) items.getItem().getCustomData(CustomCrafting.RECIPE_BOOK)).setEnabled(true);
            return true;
        })));
    }

    @Override
    public void render(GuiUpdate<CCCache> update, CCCache cache, Items items, CustomItem customItem, ItemStack item) {
        update.setButton(31, "knowledge_book.toggle");
    }
}
