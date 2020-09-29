package me.wolfyscript.customcrafting.gui.potion_creator;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.gui.ExtendedGuiWindow;
import me.wolfyscript.utilities.api.inventory.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.InventoryAPI;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.buttons.ActionButton;
import me.wolfyscript.utilities.api.inventory.button.buttons.ChatInputButton;
import me.wolfyscript.utilities.api.inventory.button.buttons.ToggleButton;
import me.wolfyscript.utilities.api.utils.inventory.PlayerHeadUtils;
import org.bukkit.Material;

public class PotionCreator extends ExtendedGuiWindow {

    public PotionCreator(InventoryAPI inventoryAPI, CustomCrafting customCrafting) {
        super("potion_creator", inventoryAPI, 54, customCrafting);
    }

    @Override
    public void onInit() {
        registerButton(new ActionButton("back", new ButtonState("none", "back", PlayerHeadUtils.getViaValue("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODY0Zjc3OWE4ZTNmZmEyMzExNDNmYTY5Yjk2YjE0ZWUzNWMxNmQ2NjllMTljNzVmZDFhN2RhNGJmMzA2YyJ9fX0="), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            if (((TestCache) guiHandler.getCustomCache()).getPotionEffectCache().isRecipePotionEffect()) {
                guiHandler.openCluster("recipe_creator");
            } else {
                guiHandler.openCluster("item_creator");
            }
            return true;
        })));
        

        registerButton(new ActionButton("potion_effect_type", new ButtonState("potion_effect_type", Material.BOOKSHELF, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            guiHandler.changeToInv("potion_effect_type_selection");
            return true;
        })));

        registerButton(new ChatInputButton("duration", Material.CLOCK, (hashMap, guiHandler, player, itemStack, i, b) -> {
            hashMap.put("%duration%", ((TestCache) guiHandler.getCustomCache()).getPotionEffectCache().getDuration());
            return itemStack;
        }, (guiHandler, player, s, args) -> {
            try {
                ((TestCache) guiHandler.getCustomCache()).getPotionEffectCache().setDuration(Integer.parseInt(args[0]));
                return false;
            } catch (NumberFormatException e) {
                api.sendPlayerMessage(player, "item_creator", "main_menu", "potion.error_number");
            }
            return true;
        }));
        registerButton(new ChatInputButton("amplifier", Material.IRON_SWORD, (hashMap, guiHandler, player, itemStack, i, b) -> {
            hashMap.put("%amplifier%", ((TestCache) guiHandler.getCustomCache()).getPotionEffectCache().getAmplifier());
            return itemStack;
        }, (guiHandler, player, s, args) -> {
            try {
                ((TestCache) guiHandler.getCustomCache()).getPotionEffectCache().setAmplifier(Integer.parseInt(args[0]));
                return false;
            } catch (NumberFormatException e) {
                api.sendPlayerMessage(player, "item_creator", "main_menu", "potion.error_number");
            }
            return true;
        }));
        registerButton(new ToggleButton("ambient", new ButtonState("ambient.enabled", Material.BARREL, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).getPotionEffectCache().setAmbient(false);
            return true;
        }), new ButtonState("ambient.disabled", Material.BARREL, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).getPotionEffectCache().setAmbient(true);
            return true;
        })));
        registerButton(new ToggleButton("particles", new ButtonState("particles.enabled", Material.BARREL, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).getPotionEffectCache().setParticles(false);
            return true;
        }), new ButtonState("particles.disabled", Material.BARREL, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).getPotionEffectCache().setParticles(true);
            return true;
        })));
        registerButton(new ToggleButton("icon", new ButtonState("icon.enabled", Material.BARREL, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).getPotionEffectCache().setIcon(false);
            return true;
        }), new ButtonState("icon.disabled", Material.BARREL, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).getPotionEffectCache().setIcon(true);
            return true;
        })));

    }

    @Override
    public void onUpdateAsync(GuiUpdate update) {
        update.setButton(0, "back");

        update.setButton(19, "potion_effect_type");

        update.setButton(21, "duration");
        update.setButton(23, "amplifier");
        update.setButton(25, "ambient");
        update.setButton(29, "particles");
        update.setButton(33, "icon");
    }
}
