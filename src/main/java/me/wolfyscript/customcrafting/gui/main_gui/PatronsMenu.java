package me.wolfyscript.customcrafting.gui.main_gui;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.gui.ExtendedGuiWindow;
import me.wolfyscript.customcrafting.gui.main_gui.buttons.PatronButton;
import me.wolfyscript.utilities.api.inventory.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.InventoryAPI;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.buttons.ActionButton;
import me.wolfyscript.utilities.api.utils.chat.ClickData;
import me.wolfyscript.utilities.api.utils.chat.ClickEvent;
import me.wolfyscript.utilities.api.utils.chat.HoverEvent;
import me.wolfyscript.utilities.api.utils.inventory.PlayerHeadUtils;

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
        registerButton(new PatronButton("Apprehentice", "Foxtrot200", "db61eab0-7fb1-48db-986f-125e73787976"));
        registerButton(new PatronButton("Nat R", "1Jack", "956faa3f-df9e-402b-bc13-39c03d4b4a5b"));
        registerButton(new PatronButton("Alex", "LeftAlex", "af1ef7e4-acc3-44a1-8323-8f50b92be2c9"));
        registerButton(new PatronButton("Vincent Deniau", "VinceTheWolf", "a307c2b3-463a-4db6-8a6a-07419909af72"));
        registerButton(new PatronButton("gizmonster", "gizmonster", "e502d121-de9d-4f5d-b7e5-0da747c4e2e8"));

        registerButton(new PatronButton("Arthur Neumann"));
        registerButton(new PatronButton("TheDutchRuben"));
        registerButton(new PatronButton("Junye Zhou"));
        registerButton(new PatronButton("Eli2t"));
        registerButton(new PatronButton("Beng701"));

        registerButton(new PatronButton("Ananass Me", "Honakura", "a599af6e-7f60-4050-9854-92026e29d4d1"));
        registerButton(new PatronButton("CypherPhyre"));
        registerButton(new PatronButton("Thomas Texier"));
        registerButton(new PatronButton("르 미"));
        registerButton(new PatronButton("John"));

        registerButton(new PatronButton("Cameron R"));
        registerButton(new PatronButton("HittmanA"));
        registerButton(new PatronButton("Fluk Rocker"));
        registerButton(new PatronButton("Nick coburn"));
        registerButton(new PatronButton("Ethonion"));

        registerButton(new PatronButton("Gamer430"));
    }

    @Override
    public void onUpdateAsync(GuiUpdate event) {
        super.onUpdateAsync(event);
        event.setButton(0, "back");

        event.setButton(9, "patron.apprehentice");
        event.setButton(11, "patron.nat_r");
        event.setButton(13, "patron.alex");
        event.setButton(15, "patron.vincent_deniau");
        event.setButton(17, "patron.gizmonster");

        event.setButton(18, "patron.arthur_neumann");
        event.setButton(20, "patron.thedutchruben");
        event.setButton(22, "patron.junye_zhou");
        event.setButton(24, "patron.eli2t");
        event.setButton(26, "patron.beng701");

        event.setButton(27, "patron.ananass_me");
        event.setButton(29, "patron.cypherphyre");
        event.setButton(30, "patron.thomas_texier");
        event.setButton(31, "patron.르_미");
        event.setButton(32, "patron.john");
        event.setButton(33, "patron.cameron_r");
        event.setButton(35, "patron.hittmana");

        event.setButton(37, "patron.fluk_rocker");
        event.setButton(39, "patron.nick_coburn");
        event.setButton(41, "patron.ethonion");
        event.setButton(43, "patron.gamer430");

    }

}
