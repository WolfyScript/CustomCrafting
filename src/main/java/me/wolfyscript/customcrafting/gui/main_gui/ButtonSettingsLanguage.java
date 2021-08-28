package me.wolfyscript.customcrafting.gui.main_gui;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.utils.ChatUtils;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.List;

class ButtonSettingsLanguage extends ActionButton<CCCache> {

    public static final String KEY = "language";

    ButtonSettingsLanguage(List<String> availableLangs, WolfyUtilities api, CustomCrafting customCrafting) {
        super(KEY, new ButtonState<>(KEY, Material.BOOKSHELF, (cache, guiHandler, player, inventory, slot, event) -> {
            int index = availableLangs.indexOf(customCrafting.getConfigHandler().getConfig().getLanguage());
            int nextIndex = index;
            if (event instanceof InventoryClickEvent clickEvent) {
                if (clickEvent.isLeftClick() && !clickEvent.isShiftClick()) {
                    nextIndex = (index + 1 < availableLangs.size()) ? index + 1 : 0;
                } else if (clickEvent.isRightClick() && !clickEvent.isShiftClick()) {
                    nextIndex = index - 1 >= 0 ? index - 1 : availableLangs.size() - 1;
                } else if (clickEvent.isShiftClick()) {
                    if (ChatUtils.checkPerm(player, "customcrafting.cmd.reload")) {
                        api.getChat().sendMessage(player, "&eReloading Inventories and Languages!");
                        customCrafting.getApi().getLanguageAPI().unregisterLanguages();
                        customCrafting.getConfigHandler().getConfig().reload();
                        customCrafting.getConfigHandler().loadLang();
                        customCrafting.getApi().getInventoryAPI().reset();
                        api.getChat().sendMessage(player, "&aReload complete! Reloaded GUIs and languages");
                        guiHandler.close();
                        return true;
                    }
                    return true;
                }
            }
            customCrafting.getConfigHandler().getConfig().setLanguage(availableLangs.get(nextIndex));
            return true;
        }, (hashMap, cache, guiHandler, player, inventory, itemStack, slot, b) -> {
            int index = availableLangs.indexOf(customCrafting.getConfigHandler().getConfig().getLanguage());
            List<String> displayLangs = new ArrayList<>();
            displayLangs.addAll(availableLangs.subList(index, availableLangs.size()));
            displayLangs.addAll(availableLangs.subList(0, index));
            for (int i = 0; i < 5; i++) {
                if (i < displayLangs.size()) {
                    hashMap.put("%lang" + i + "%", displayLangs.get(i));
                } else {
                    hashMap.put("%lang" + i + "%", "");
                }
            }
            return itemStack;
        }));
    }
}
