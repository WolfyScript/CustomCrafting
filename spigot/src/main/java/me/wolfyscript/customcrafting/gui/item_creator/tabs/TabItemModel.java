package me.wolfyscript.customcrafting.gui.item_creator.tabs;

import com.wolfyscript.utilities.bukkit.world.items.reference.BukkitStackIdentifier;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.items.Items;
import me.wolfyscript.customcrafting.gui.item_creator.ButtonOption;
import me.wolfyscript.customcrafting.gui.item_creator.MenuItemCreator;
import me.wolfyscript.customcrafting.utils.ChatUtils;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ChatInputButton;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.DummyButton;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.Pair;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class TabItemModel extends ItemCreatorTabVanilla {

    public static final String KEY = "item_model";

    public TabItemModel() {
        super(new NamespacedKey(NamespacedKeyUtils.NAMESPACE, KEY));
    }

    @Override
    public void register(MenuItemCreator creator, WolfyUtilities api) {
        creator.registerButton(new ButtonOption(Material.GLOWSTONE, this));

        creator.registerButton(new DummyButton<>(KEY + ".info", Material.NAME_TAG, ((hashMap, ccCache, guiHandler, player, guiInventory, itemStack, i, b) -> {
            CustomItem item = guiHandler.getCustomCache().getItems().getItem();
            Pair<org.bukkit.NamespacedKey, String> key = !item.hasItemMeta() ? new Pair<>(null, "&7&l/")
                    : !item.getItemMeta().hasItemModel() ? new Pair<>(null, "&7&l/")
                    : item.getItemMeta().getItemModel() == null ? new Pair<>(null, "&7&l/")
                    : new Pair<>(item.getItemMeta().getItemModel(), null);
            hashMap.put("%VAR%", key.getKey()==null?key.getValue():key.getKey().getNamespace()+":"+key.getKey().getKey());
            return itemStack;
        })));

        new ChatInputButton.Builder<>(creator,KEY+".set")
                .state(s->s.icon(Material.GREEN_CONCRETE))
                .inputAction(((guiHandler, player, s, args) -> {
                    NamespacedKey key = ChatUtils.getInternalNamespacedKey(player,"",args);
                    if(key==null){
                        creator.sendMessage(player,KEY+".invalid_namespace",new Pair<>("%VAL%",String.join(" ",args)));
                        return true;
                    }
                    BukkitStackIdentifier identifier = guiHandler.getCustomCache().getItems().asBukkitIdentifier().orElse(null);
                    if(identifier==null)
                        return true;
                    guiHandler.getCustomCache().getItems().modifyOriginalStack(stack->{
                        ItemMeta meta = stack.getItemMeta();
                        meta.setItemModel(key.bukkit());
                        stack.setItemMeta(meta);
                    });
                    creator.sendMessage(player,KEY+".set",new Pair<>("%VALUE%",key.getNamespace()+":"+key.getKey()));
                    return false;
                }))
                .register();
        creator.registerButton(new ActionButton<>(KEY+".reset",Material.RED_CONCRETE,(ccCache, guiHandler, player, guiInventory, i, inventoryInteractEvent) -> {
            guiHandler.getCustomCache().getItems().modifyOriginalStack(stack->{
               ItemMeta meta = stack.getItemMeta();
               meta.setItemModel(null);
               stack.setItemMeta(meta);
            });
            creator.sendMessage(player,KEY+".reset");
            return true;
        }));
    }

    @Override
    public void render(GuiUpdate<CCCache> update, CCCache cache, Items items, CustomItem customItem, ItemStack item) {
        update.setButton(29,KEY+".set");
        update.setButton(31,KEY+".info");
        update.setButton(33,KEY+".reset");
    }
}
