package me.wolfyscript.customcrafting.gui.main_gui;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.gui.ExtendedGuiWindow;
import me.wolfyscript.customcrafting.gui.main_gui.buttons.PatronButton;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.GuiUpdateEvent;
import me.wolfyscript.utilities.api.inventory.InventoryAPI;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.buttons.ActionButton;
import me.wolfyscript.utilities.api.utils.chat.ClickData;
import me.wolfyscript.utilities.api.utils.chat.ClickEvent;
import me.wolfyscript.utilities.api.utils.chat.HoverEvent;
import org.bukkit.event.EventHandler;

public class PatronsMenu extends ExtendedGuiWindow {

    public PatronsMenu(InventoryAPI inventoryAPI, CustomCrafting customCrafting) {
        super("patrons_menu", inventoryAPI, 27, customCrafting);
    }

    @Override
    public void onInit() {
        registerButton(new ActionButton("back", new ButtonState("none", "back", WolfyUtilities.getCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODY0Zjc3OWE4ZTNmZmEyMzExNDNmYTY5Yjk2YjE0ZWUzNWMxNmQ2NjllMTljNzVmZDFhN2RhNGJmMzA2YyJ9fX0="), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            guiHandler.openPreviousInv();
            return true;
        })));
        registerButton(new ActionButton("patreon", new ButtonState("patreon", WolfyUtilities.getSkullViaURL("5693b66a595f78af3f51f4efa4c13375b1b958e6f4c507a47c4fe565cc275"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            api.openBook(player, false,
                    new ClickData[]{
                            new ClickData("&8[&cBecome a Patron&8]\n\n\n\n\n", null, new HoverEvent(HoverEvent.Action.SHOW_TEXT, "§7Goto WolfyScript's Patreon"), new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.patreon.com/wolfyscript")),
                            new ClickData("\n&8[&cBack&8]\n", null, new HoverEvent(HoverEvent.Action.SHOW_TEXT, "§7Back to Patreon Menu"), new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cc")),
                    }
            );
            return true;
        })));
        registerButton(new PatronButton("Apprehentice", "Foxtrot200", "db61eab0-7fb1-48db-986f-125e73787976"));
        registerButton(new PatronButton("Alex", "LeftAlex", "af1ef7e4-acc3-44a1-8323-8f50b92be2c9"));
        registerButton(new PatronButton("Vincent Deniau", "VinceTheWolf", "a307c2b3-463a-4db6-8a6a-07419909af72"));
        registerButton(new PatronButton("Nat R", "1Jack", "956faa3f-df9e-402b-bc13-39c03d4b4a5b"));
    }

    @EventHandler
    public void onUpdate(GuiUpdateEvent event) {
        if (event.verify(this)) {
            event.setButton(0, "back");

            event.setButton(10, "patron.apprehentice");
            event.setButton(12, "patron.alex");
            event.setButton(14, "patron.vincent_deniau");
            event.setButton(16, "patron.nat_r");
        }
    }

}
