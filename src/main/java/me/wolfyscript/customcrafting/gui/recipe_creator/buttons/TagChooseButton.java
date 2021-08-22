package me.wolfyscript.customcrafting.gui.recipe_creator.buttons;

import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.Material;
import org.bukkit.Tag;

public class TagChooseButton extends ActionButton<CCCache> {

    public TagChooseButton(Tag<Material> tag) {
        super("tag." + NamespacedKey.fromBukkit(tag.getKey()).toString("."), new ButtonState<>("tag", Material.NAME_TAG, (cache, guiHandler, player, guiInventory, slot, event) -> {
            var recipeItemStack = cache.getRecipeCreatorCache().getTagSettingsCache().getRecipeItemStack();
            if (recipeItemStack != null) {
                recipeItemStack.getTags().add(NamespacedKey.fromBukkit(tag.getKey()));
            }
            guiHandler.openPreviousWindow();
            return true;
        }, (values, cache, guiHandler, player, guiInventory, itemStack, i, b) -> {
            values.put("%namespaced_key%", NamespacedKey.fromBukkit(tag.getKey()).toString());
            itemStack.setType(tag.getValues().stream().findFirst().orElse(Material.NAME_TAG));
            return itemStack;
        }));
    }
}
