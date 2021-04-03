package me.wolfyscript.customcrafting.gui.recipe_creator;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.TagSettingsCache;
import me.wolfyscript.customcrafting.data.cache.items.ItemsButtonAction;
import me.wolfyscript.customcrafting.gui.CCWindow;
import me.wolfyscript.customcrafting.gui.MainCluster;
import me.wolfyscript.customcrafting.gui.recipe_creator.buttons.TagChooseButton;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.util.inventory.PlayerHeadUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Tag;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class TagChooseList extends CCWindow {

    private static final List<Tag<Material>> ITEM_TAGS;

    static {
        ITEM_TAGS = StreamSupport.stream(Bukkit.getTags("items", Material.class).spliterator(), false).sorted(Comparator.comparing(o -> o.getKey().toString())).collect(Collectors.toList());
    }

    public TagChooseList(GuiCluster<CCCache> guiCluster, CustomCrafting customCrafting) {
        super(guiCluster, "tag_list", 54, customCrafting);
    }

    @Override
    public void onInit() {
        registerButton(new ActionButton<>("next_page", PlayerHeadUtils.getViaURL("c86185b1d519ade585f184c34f3f3e20bb641deb879e81378e4eaf209287"), (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
            int page = cache.getTagSettingsCache().getChooseListPage();
            int maxPages = ITEM_TAGS.size() / 45 + ITEM_TAGS.size() % 45 > 0 ? 1 : 0;
            if (page < maxPages) {
                cache.getTagSettingsCache().setChooseListPage(++page);
            }
            return true;
        }));
        registerButton(new ActionButton<>("previous_page", PlayerHeadUtils.getViaURL("ad73cf66d31b83cd8b8644c15958c1b73c8d97323b801170c1d8864bb6a846d"), (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
            int page = cache.getTagSettingsCache().getChooseListPage();
            if (page > 0) {
                cache.getTagSettingsCache().setChooseListPage(--page);
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
        CCCache cache = update.getGuiHandler().getCustomCache();
        TagSettingsCache tagsCache = cache.getTagSettingsCache();
        int page = tagsCache.getChooseListPage();
        int maxPages = ITEM_TAGS.size() / 45 + (ITEM_TAGS.size() % 45 > 0 ? 1 : 0);
        if (page > maxPages) {
            tagsCache.setChooseListPage(maxPages);
        }

        for (int i = 45 * page, invSlot = 0; i < ITEM_TAGS.size() && invSlot < getSize() - 9; i++, invSlot++) {
            TagChooseButton button = new TagChooseButton(ITEM_TAGS.get(i));
            registerButton(button);
            update.setButton(invSlot, button);
        }
        update.setButton(49, MainCluster.BACK_BOTTOM);

        if (page > 0) {
            update.setButton(48, "previous_page");
        }
        if (page + 1 < maxPages) {
            update.setButton(50, "next_page");
        }
    }
}
