package me.wolfyscript.customcrafting.gui.item_creator.tabs;

import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.items.Items;
import me.wolfyscript.customcrafting.gui.item_creator.ButtonOption;
import me.wolfyscript.customcrafting.gui.item_creator.MenuItemCreator;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ChatInputButton;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.Pair;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;

public class TabEnchants extends ItemCreatorTabVanilla {

    public static final String KEY = "enchantments";

    public TabEnchants() {
        super(new NamespacedKey(NamespacedKeyUtils.NAMESPACE, KEY));
    }

    @Override
    public void register(MenuItemCreator creator, WolfyUtilities api) {
        creator.registerButton(new ButtonOption(Material.ENCHANTED_BOOK, this));
        creator.registerButton(new ChatInputButton<>(KEY + ".add", Material.ENCHANTED_BOOK, (guiHandler, player, s, args) -> {
            if (args.length > 1) {
                int level;
                try {
                    level = Integer.parseInt(args[args.length - 1]);
                } catch (NumberFormatException ex) {
                    creator.sendMessage(player, "enchant.invalid_lvl");
                    return true;
                }
                var enchantment = Enchantment.getByKey(org.bukkit.NamespacedKey.minecraft(args[0].toLowerCase(Locale.ROOT).replace(' ', '_')));
                if (enchantment != null) {
                    guiHandler.getCustomCache().getItems().getItem().addUnsafeEnchantment(enchantment, level);
                } else {
                    creator.sendMessage(player, "enchant.invalid_enchant", new Pair<>("%ENCHANT%", args[0]));
                    return true;
                }
            } else {
                creator.sendMessage(player, "enchant.no_lvl");
                return true;
            }
            return false;
        }));
        creator.registerButton(new ChatInputButton<>(KEY + ".remove", Material.RED_CONCRETE, (guiHandler, player, s, args) -> {
            var enchantment = Enchantment.getByKey(org.bukkit.NamespacedKey.minecraft(args[0].toLowerCase(Locale.ROOT).replace(' ', '_')));
            if (enchantment != null) {
                guiHandler.getCustomCache().getItems().getItem().removeEnchantment(enchantment);
            } else {
                creator.sendMessage(player, "enchant.invalid_enchant", new Pair<>("%ENCHANT%", args[0]));
                return true;
            }
            return false;
        }));
    }

    @Override
    public void render(GuiUpdate<CCCache> update, CCCache cache, Items items, CustomItem customItem, ItemStack item) {
        update.setButton(30, KEY + ".add");
        update.setButton(32, KEY + ".remove");
        update.setButton(36, "meta_ignore.wolfyutilities.enchant");
    }
}
