package me.wolfyscript.customcrafting.gui.potion_creator;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.data.cache.potions.PotionEffects;
import me.wolfyscript.customcrafting.gui.ExtendedGuiWindow;
import me.wolfyscript.utilities.api.inventory.GuiHandler;
import me.wolfyscript.utilities.api.inventory.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.InventoryAPI;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.buttons.ActionButton;
import me.wolfyscript.utilities.api.inventory.button.buttons.ChatInputButton;
import me.wolfyscript.utilities.api.inventory.button.buttons.DummyButton;
import me.wolfyscript.utilities.api.inventory.button.buttons.ToggleButton;
import me.wolfyscript.utilities.api.utils.inventory.PlayerHeadUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;

import java.util.Locale;

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

        registerButton(new ActionButton("cancel", Material.BARRIER, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            if (((TestCache) guiHandler.getCustomCache()).getPotionEffectCache().isRecipePotionEffect()) {
                guiHandler.openCluster("recipe_creator");
            } else {
                guiHandler.openCluster("item_creator");
            }
            return true;
        }));

        registerButton(new ActionButton("apply", Material.LIME_CONCRETE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            PotionEffects potionEffectCache = ((TestCache) guiHandler.getCustomCache()).getPotionEffectCache();
            potionEffectCache.applyPotionEffect(((TestCache) guiHandler.getCustomCache()));
            if (potionEffectCache.isRecipePotionEffect()) {
                guiHandler.openCluster("recipe_creator");
            } else {
                guiHandler.openCluster("item_creator");
            }
            return true;
        }));

        registerButton(new DummyButton("preview", Material.POTION, (hashMap, guiHandler, player, oldItem, i, b) -> {
            PotionEffects potionEffectCache = ((TestCache) guiHandler.getCustomCache()).getPotionEffectCache();
            ItemStack itemStack = new ItemStack(Material.POTION);
            PotionMeta itemMeta = (PotionMeta) itemStack.getItemMeta();
            itemMeta.setDisplayName("§7 - - - - - ");
            if (potionEffectCache.getType() != null) {
                itemMeta.addCustomEffect(new PotionEffect(potionEffectCache.getType(), potionEffectCache.getDuration(), potionEffectCache.getAmplifier(), potionEffectCache.isAmbient(), potionEffectCache.isParticles(), potionEffectCache.isIcon()), true);
            }
            itemStack.setItemMeta(itemMeta);
            return itemStack;
        }));

        registerButton(new ActionButton("potion_effect_type", new ButtonState("potion_effect_type", Material.BOOKSHELF, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            PotionEffects potionEffectCache = ((TestCache) guiHandler.getCustomCache()).getPotionEffectCache();
            potionEffectCache.setApplyPotionEffectType((cache, potionEffect) -> potionEffectCache.setType(potionEffect));
            potionEffectCache.setOpenedFrom("potion_creator", "potion_creator");
            guiHandler.changeToInv("potion_effect_type_selection");
            return true;
        }, (values, guiHandler, player, itemStack, i, b) -> {
            PotionEffects potionEffectCache = ((TestCache) guiHandler.getCustomCache()).getPotionEffectCache();
            values.put("%effect_type%", potionEffectCache.getType() != null ? StringUtils.capitalize(potionEffectCache.getType().getName().replace("_", " ").toLowerCase(Locale.ROOT)) : "&cnone");
            return itemStack;
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
        registerButton(new ToggleButton("ambient", new ButtonState("ambient.enabled", Material.BLAZE_POWDER, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).getPotionEffectCache().setAmbient(false);
            return true;
        }), new ButtonState("ambient.disabled", Material.BLAZE_POWDER, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).getPotionEffectCache().setAmbient(true);
            return true;
        })));
        registerButton(new ToggleButton("particles", new ButtonState("particles.enabled", Material.FIREWORK_ROCKET, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).getPotionEffectCache().setParticles(false);
            return true;
        }), new ButtonState("particles.disabled", Material.FIREWORK_ROCKET, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).getPotionEffectCache().setParticles(true);
            return true;
        })));
        registerButton(new ToggleButton("icon", new ButtonState("icon.enabled", Material.ITEM_FRAME, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).getPotionEffectCache().setIcon(false);
            return true;
        }), new ButtonState("icon.disabled", Material.ITEM_FRAME, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).getPotionEffectCache().setIcon(true);
            return true;
        })));

    }

    @Override
    public void onUpdateAsync(GuiUpdate update) {
        super.onUpdateAsync(update);
        GuiHandler<TestCache> guiHandler = update.getGuiHandler(TestCache.class);
        PotionEffects potionEffectCache = guiHandler.getCustomCache().getPotionEffectCache();
        update.setButton(0, "back");
        update.setButton(11, "apply");
        update.setButton(13, "preview");
        update.setButton(15, "cancel");

        update.setButton(28, "potion_effect_type");
        update.setButton(30, "duration");
        update.setButton(32, "amplifier");
        ((ToggleButton) getButton("ambient")).setState(guiHandler, potionEffectCache.isAmbient());
        ((ToggleButton) getButton("particles")).setState(guiHandler, potionEffectCache.isParticles());
        ((ToggleButton) getButton("icon")).setState(guiHandler, potionEffectCache.isIcon());
        update.setButton(34, "ambient");
        update.setButton(38, "particles");
        update.setButton(42, "icon");
    }
}
