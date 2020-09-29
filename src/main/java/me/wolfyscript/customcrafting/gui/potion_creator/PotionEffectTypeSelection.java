package me.wolfyscript.customcrafting.gui.potion_creator;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.gui.ExtendedGuiWindow;
import me.wolfyscript.customcrafting.gui.potion_creator.buttons.PotionEffectTypeSelectButton;
import me.wolfyscript.utilities.api.inventory.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.InventoryAPI;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.buttons.ActionButton;
import me.wolfyscript.utilities.api.utils.inventory.PlayerHeadUtils;
import org.bukkit.potion.PotionEffectType;

public class PotionEffectTypeSelection extends ExtendedGuiWindow {

    public PotionEffectTypeSelection(InventoryAPI inventoryAPI, CustomCrafting customCrafting) {
        super("potion_effect_type_selection", inventoryAPI, 54, customCrafting);
    }

    @Override
    public void onInit() {
        registerButton(new ActionButton("back", new ButtonState("none", "back", PlayerHeadUtils.getViaValue("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODY0Zjc3OWE4ZTNmZmEyMzExNDNmYTY5Yjk2YjE0ZWUzNWMxNmQ2NjllMTljNzVmZDFhN2RhNGJmMzA2YyJ9fX0="), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            guiHandler.changeToInv("potion_creator");
            return true;
        })));

        for (PotionEffectType type : PotionEffectType.values()) {
            registerButton(new PotionEffectTypeSelectButton(type));
        }
    }

    @Override
    public void onUpdateAsync(GuiUpdate update) {
        update.setButton(0, "back");
        for (int i = 0; i < PotionEffectType.values().length; i++) {
            update.setButton(9 + i, "potion_effect_type_" + PotionEffectType.values()[i].getName().toLowerCase());
        }
    }
}
