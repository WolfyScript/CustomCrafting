package me.wolfyscript.customcrafting.gui.recipe_creator.buttons;

import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

public class TagContainerButton extends ActionButton<CCCache> {

    public TagContainerButton(NamespacedKey namespacedKey) {
        super("tag." + namespacedKey.toString("."), new ButtonState<>("tag_container", Material.NAME_TAG, (cache, guiHandler, player, guiInventory, slot, event) -> {
            if (event instanceof InventoryClickEvent clickEvent && clickEvent.getClick().equals(ClickType.SHIFT_RIGHT)) {
                var recipeItemStack = cache.getRecipeCreatorCache().getTagSettingsCache().getRecipeItemStack();
                if (recipeItemStack != null) {
                    recipeItemStack.getTags().remove(namespacedKey);
                }
            }
            return true;
        }, (values, cache, guiHandler, player, guiInventory, itemStack, i, b) -> {
            values.put("%namespaced_key%", namespacedKey.toString());
            return itemStack;
        }));
    }
}
