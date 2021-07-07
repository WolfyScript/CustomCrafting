package me.wolfyscript.customcrafting.gui.item_creator.tabs;

import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.items.Items;
import me.wolfyscript.customcrafting.data.cache.items.ItemsButtonAction;
import me.wolfyscript.customcrafting.gui.item_creator.buttons.OptionButton;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ToggleButton;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class TabVanilla extends ItemCreatorTab {

    public static final String KEY = "vanilla";

    public TabVanilla() {
        super(new NamespacedKey(NamespacedKeyUtils.NAMESPACE, KEY));
    }

    @Override
    public void register() {
        creator.registerButton(new OptionButton(Material.GRASS_BLOCK, this));
        creator.registerButton(new ToggleButton<>("vanilla.block_recipes", (cache, guiHandler, player, guiInventory, i) -> cache.getItems().getItem().isBlockVanillaRecipes(), new ButtonState<>("vanilla.block_recipes.enabled", Material.GREEN_CONCRETE, (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
            items.getItem().setBlockVanillaRecipes(false);
            return true;
        }), new ButtonState<>("vanilla.block_recipes.disabled", Material.RED_CONCRETE, (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
            items.getItem().setBlockVanillaRecipes(true);
            return true;
        })));
        creator.registerButton(new ToggleButton<>("vanilla.block_placement", new ButtonState<>("vanilla.block_placement.enabled", Material.GREEN_CONCRETE, (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
            items.getItem().setBlockPlacement(false);
            return true;
        }), new ButtonState<>("vanilla.block_placement.disabled", Material.RED_CONCRETE, (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
            items.getItem().setBlockPlacement(true);
            return true;
        })));
    }

    @Override
    public void render(GuiUpdate<CCCache> update, CCCache cache, Items items, CustomItem customItem, ItemStack item) {
        update.setButton(29, "vanilla.block_recipes");
        if (item.getType().isBlock()) {
            update.setButton(31, "vanilla.block_placement");
        }
    }
}
