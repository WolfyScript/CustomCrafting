package me.wolfyscript.customcrafting.gui.main_gui;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.patreon.Patron;
import me.wolfyscript.customcrafting.gui.ExtendedGuiWindow;
import me.wolfyscript.utilities.api.inventory.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.InventoryAPI;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.buttons.ActionButton;
import me.wolfyscript.utilities.api.utils.chat.ClickData;
import me.wolfyscript.utilities.api.utils.chat.ClickEvent;
import me.wolfyscript.utilities.api.utils.chat.HoverEvent;
import me.wolfyscript.utilities.api.utils.inventory.PlayerHeadUtils;

import java.util.List;

public class PatronsMenu extends ExtendedGuiWindow {

    public PatronsMenu(InventoryAPI inventoryAPI, CustomCrafting customCrafting) {
        super("patrons_menu", inventoryAPI, 54, customCrafting);
    }

    @Override
    public void onInit() {
        registerButton(new ActionButton("back", new ButtonState("none", "back", PlayerHeadUtils.getViaValue("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODY0Zjc3OWE4ZTNmZmEyMzExNDNmYTY5Yjk2YjE0ZWUzNWMxNmQ2NjllMTljNzVmZDFhN2RhNGJmMzA2YyJ9fX0="), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            guiHandler.openPreviousInv();
            return true;
        })));
        registerButton(new ActionButton("patreon", PlayerHeadUtils.getViaURL("5693b66a595f78af3f51f4efa4c13375b1b958e6f4c507a47c4fe565cc275"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            api.openBook(player, false,
                    new ClickData[]{
                            new ClickData("&8[&cBecome a Patron&8]\n\n\n\n\n", null, new HoverEvent(HoverEvent.Action.SHOW_TEXT, "§7Goto WolfyScript's Patreon"), new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.patreon.com/wolfyscript")),
                            new ClickData("\n&8[&cBack&8]\n", null, new HoverEvent(HoverEvent.Action.SHOW_TEXT, "§7Back to Patreon Menu"), new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cc")),
                    }
            );
            return true;
        }));
    }

    @Override
    public void onUpdateAsync(GuiUpdate event) {
        super.onUpdateAsync(event);
        event.setButton(0, "back");
        List<Patron> patronList = customCrafting.getPatreonObj().getPatronList();

        int j = 9;
        for (int i = 0; i < patronList.size() && j < getSize(); i++) {
            event.setItem(j, patronList.get(i).getHead());
            j += 2;
        }
    }

}
