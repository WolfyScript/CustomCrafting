package me.wolfyscript.customcrafting.gui.recipe_creator.buttons;

import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.inventory.item_builder.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.Tag;

public class TagChooseButton extends ActionButton<CCCache> {

    public TagChooseButton(Tag<Material> tag) {
        super("tag." + NamespacedKey.fromBukkit(tag.getKey()).toString("."), Material.NAME_TAG, (cache, guiHandler, player, guiInventory, slot, event) -> {


            return true;
        }, (hashMap, cache, guiHandler, player, guiInventory, itemStack, i, b) -> {
            ItemBuilder itemBuilder = new ItemBuilder(tag.getValues().stream().findFirst().orElse(Material.NAME_TAG));
            itemBuilder.setDisplayName("§6" + tag.getKey().toString());
            return itemBuilder.create();
        });
    }
}
