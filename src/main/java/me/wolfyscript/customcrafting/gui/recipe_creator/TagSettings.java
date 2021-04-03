package me.wolfyscript.customcrafting.gui.recipe_creator;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.TagSettingsCache;
import me.wolfyscript.customcrafting.data.cache.items.ItemsButtonAction;
import me.wolfyscript.customcrafting.gui.CCWindow;
import me.wolfyscript.customcrafting.gui.MainCluster;
import me.wolfyscript.customcrafting.gui.recipe_creator.buttons.TagContainerButton;
import me.wolfyscript.customcrafting.utils.recipe_item.RecipeItemStack;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.inventory.PlayerHeadUtils;
import org.bukkit.Material;

public class TagSettings extends CCWindow {

    public TagSettings(GuiCluster<CCCache> guiCluster, CustomCrafting customCrafting) {
        super(guiCluster, "tag_settings", 54, customCrafting);
    }

    @Override
    public void onInit() {
        registerButton(new ActionButton<>("add_tag_list", Material.NAME_TAG, (cache, guiHandler, player, guiInventory, i, inventoryInteractEvent) -> {
            guiHandler.openWindow("tag_list");
            return true;
        }));
        registerButton(new ActionButton<>("next_page", PlayerHeadUtils.getViaURL("c86185b1d519ade585f184c34f3f3e20bb641deb879e81378e4eaf209287"), (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
            int page = cache.getTagSettingsCache().getListPage();
            cache.getTagSettingsCache().setListPage(++page);
            return true;
        }));
        registerButton(new ActionButton<>("previous_page", PlayerHeadUtils.getViaURL("ad73cf66d31b83cd8b8644c15958c1b73c8d97323b801170c1d8864bb6a846d"), (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
            int page = cache.getTagSettingsCache().getListPage();
            if (page > 0) {
                cache.getTagSettingsCache().setListPage(--page);
            }
            return true;
        }));
    }

    @Override
    public void onUpdateSync(GuiUpdate<CCCache> guiUpdate) {
    }

    @Override
    public void onUpdateAsync(GuiUpdate<CCCache> update) {
        super.onUpdateAsync(update);
        TagSettingsCache tagsCache = update.getGuiHandler().getCustomCache().getTagSettingsCache();
        RecipeItemStack recipeItemStack = tagsCache.getRecipeItemStack();
        update.setButton(0, MainCluster.BACK);
        if (recipeItemStack != null) {
            NamespacedKey[] tags = recipeItemStack.getTags().toArray(new NamespacedKey[0]);

            int page = tagsCache.getListPage();
            int maxPages = tags.length / 45 + (tags.length % 45 > 0 ? 1 : 0);
            if (page > maxPages) {
                tagsCache.setListPage(maxPages);
            }

            if (page > 0) {
                update.setButton(2, "previous_page");
            }
            if (page + 1 < maxPages) {
                update.setButton(4, "next_page");
            }

            for (int i = 45 * page, invSlot = 9; i < tags.length && invSlot < getSize() - 9; i++, invSlot++) {
                TagContainerButton button = new TagContainerButton(tags[i]);
                registerButton(button);
                update.setButton(invSlot, button);
            }
        }

        update.setButton(49, "add_tag_list");


    }
}
