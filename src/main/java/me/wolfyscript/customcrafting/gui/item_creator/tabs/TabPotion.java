package me.wolfyscript.customcrafting.gui.item_creator.tabs;

import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.items.Items;
import me.wolfyscript.customcrafting.data.cache.items.ItemsButtonAction;
import me.wolfyscript.customcrafting.data.cache.potions.PotionEffects;
import me.wolfyscript.customcrafting.gui.item_creator.ItemCreator;
import me.wolfyscript.customcrafting.gui.item_creator.buttons.OptionButton;
import me.wolfyscript.customcrafting.gui.potion_creator.ClusterPotionCreator;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.inventory.PlayerHeadUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;

public class TabPotion extends ItemCreatorTabVanilla {

    public static final String KEY = "potion";

    public TabPotion() {
        super(new NamespacedKey(NamespacedKeyUtils.NAMESPACE, KEY));
    }

    @Override
    public void register(ItemCreator creator, WolfyUtilities api) {
        creator.registerButton(new OptionButton(Material.POTION, this));
        creator.registerButton(new ActionButton<>("potion.add", PlayerHeadUtils.getViaURL("9a2d891c6ae9f6baa040d736ab84d48344bb6b70d7f1a280dd12cbac4d777"), (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
            cache.getPotionEffectCache().setApplyPotionEffect((potionEffectCache1, cache1, potionEffect) -> {
                var itemMeta = items.getItem().getItemMeta();
                if (itemMeta instanceof PotionMeta) {
                    ((PotionMeta) itemMeta).addCustomEffect(potionEffect, true);
                }
                items.getItem().setItemMeta(itemMeta);
            });
            cache.getPotionEffectCache().setRecipePotionEffect(false);
            guiHandler.openWindow(ClusterPotionCreator.POTION_CREATOR);
            return true;
        }));
        creator.registerButton(new ActionButton<>("potion.remove", Material.RED_CONCRETE, (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
            PotionEffects potionEffectCache = cache.getPotionEffectCache();
            potionEffectCache.setApplyPotionEffectType((cache1, type) -> {
                var itemMeta = items.getItem().getItemMeta();
                if (itemMeta instanceof PotionMeta) {
                    ((PotionMeta) itemMeta).removeCustomEffect(type);
                }
                items.getItem().setItemMeta(itemMeta);
            });
            potionEffectCache.setOpenedFrom("item_creator", "main_menu");
            guiHandler.openWindow(ClusterPotionCreator.POTION_EFFECT_TYPE_SELECTION);
            return true;
        }));
    }

    @Override
    public boolean shouldRender(GuiUpdate<CCCache> update, CCCache cache, Items items, CustomItem customItem, ItemStack item) {
        return super.shouldRender(update, cache, items, customItem, item) && items.getItem() != null && item.hasItemMeta() && item.getItemMeta() instanceof PotionMeta;
    }

    @Override
    public void render(GuiUpdate<CCCache> update, CCCache cache, Items items, CustomItem customItem, ItemStack item) {
        update.setButton(30, "potion.add");
        update.setButton(31, "potion_beta.add");
        update.setButton(32, "potion.remove");
        update.setButton(36, "meta_ignore.wolfyutilities.potion");
    }
}
