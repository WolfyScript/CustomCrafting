package me.wolfyscript.customcrafting.gui.item_creator.tabs;

import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.items.Items;
import me.wolfyscript.customcrafting.gui.item_creator.ButtonItemFlagsToggle;
import me.wolfyscript.customcrafting.gui.item_creator.ButtonOption;
import me.wolfyscript.customcrafting.gui.item_creator.MenuItemCreator;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.version.MinecraftVersion;
import me.wolfyscript.utilities.util.version.ServerVersion;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public class TabFlags extends ItemCreatorTabVanilla {

    public static final String KEY = "flags";

    public TabFlags() {
        super(new NamespacedKey(NamespacedKeyUtils.NAMESPACE, KEY));
    }

    @Override
    public void register(MenuItemCreator creator, WolfyUtilities api) {
        creator.registerButton(new ButtonOption(Material.WRITTEN_BOOK, this));
        creator.registerButton(new ButtonItemFlagsToggle("enchants", ItemFlag.HIDE_ENCHANTS, Material.ENCHANTING_TABLE));
        creator.registerButton(new ButtonItemFlagsToggle("attributes", ItemFlag.HIDE_ATTRIBUTES, Material.ENCHANTED_GOLDEN_APPLE));
        creator.registerButton(new ButtonItemFlagsToggle("unbreakable", ItemFlag.HIDE_UNBREAKABLE, Material.BEDROCK));
        creator.registerButton(new ButtonItemFlagsToggle("destroys", ItemFlag.HIDE_DESTROYS, Material.TNT));
        creator.registerButton(new ButtonItemFlagsToggle("placed_on", ItemFlag.HIDE_PLACED_ON, Material.GRASS_BLOCK));
        creator.registerButton(new ButtonItemFlagsToggle("potion_effects", ItemFlag.HIDE_POTION_EFFECTS, Material.POTION));
        if (ServerVersion.isAfterOrEq(MinecraftVersion.of(1, 16, 2))) {
            creator.registerButton(new ButtonItemFlagsToggle("dye", ItemFlag.HIDE_DYE, Material.YELLOW_DYE));
        }
    }

    @Override
    public void render(GuiUpdate<CCCache> update, CCCache cache, Items items, CustomItem customItem, ItemStack item) {
        update.setButton(28, "flags.attributes");
        update.setButton(30, "flags.unbreakable");
        update.setButton(32, "flags.destroys");
        update.setButton(34, "flags.placed_on");
        update.setButton(38, "flags.potion_effects");
        if (ServerVersion.isAfterOrEq(MinecraftVersion.of(1, 16, 2))) {
            update.setButton(40, "flags.dye");
        }
        update.setButton(42, "flags.enchants");
        update.setButton(36, "meta_ignore.wolfyutilities.flags");
    }
}
