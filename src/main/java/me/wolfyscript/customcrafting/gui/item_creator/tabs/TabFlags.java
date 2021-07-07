package me.wolfyscript.customcrafting.gui.item_creator.tabs;

import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.items.Items;
import me.wolfyscript.customcrafting.gui.item_creator.buttons.ItemFlagsToggleButton;
import me.wolfyscript.customcrafting.gui.item_creator.buttons.OptionButton;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.version.MinecraftVersion;
import me.wolfyscript.utilities.util.version.ServerVersion;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public class TabFlags extends ItemCreatorTab {

    public static final String KEY = "flags";

    public TabFlags() {
        super(new NamespacedKey(NamespacedKeyUtils.NAMESPACE, KEY));
    }

    @Override
    public void register() {
        creator.registerButton(new OptionButton(Material.WRITTEN_BOOK, this));
        creator.registerButton(new ItemFlagsToggleButton("enchants", ItemFlag.HIDE_ENCHANTS, Material.ENCHANTING_TABLE));
        creator.registerButton(new ItemFlagsToggleButton("attributes", ItemFlag.HIDE_ATTRIBUTES, Material.ENCHANTED_GOLDEN_APPLE));
        creator.registerButton(new ItemFlagsToggleButton("unbreakable", ItemFlag.HIDE_UNBREAKABLE, Material.BEDROCK));
        creator.registerButton(new ItemFlagsToggleButton("destroys", ItemFlag.HIDE_DESTROYS, Material.TNT));
        creator.registerButton(new ItemFlagsToggleButton("placed_on", ItemFlag.HIDE_PLACED_ON, Material.GRASS_BLOCK));
        creator.registerButton(new ItemFlagsToggleButton("potion_effects", ItemFlag.HIDE_POTION_EFFECTS, Material.POTION));
        if (ServerVersion.isAfterOrEq(MinecraftVersion.of(1, 16, 2))) {
            creator.registerButton(new ItemFlagsToggleButton("dye", ItemFlag.HIDE_DYE, Material.YELLOW_DYE));
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
