package me.wolfyscript.customcrafting.gui.potion_creator;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.potions.PotionEffects;
import me.wolfyscript.customcrafting.gui.CCWindow;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ChatInputButton;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.DummyButton;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ToggleButton;
import me.wolfyscript.utilities.util.inventory.PlayerHeadUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;

import java.util.Locale;

public class PotionCreator extends CCWindow {

    public PotionCreator(GuiCluster<CCCache> cluster, CustomCrafting customCrafting) {
        super(cluster, "potion_creator", 54, customCrafting);
    }

    @Override
    public void onInit() {
        registerButton(new ActionButton<>("back", new ButtonState<>("none", "back", PlayerHeadUtils.getViaValue("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODY0Zjc3OWE4ZTNmZmEyMzExNDNmYTY5Yjk2YjE0ZWUzNWMxNmQ2NjllMTljNzVmZDFhN2RhNGJmMzA2YyJ9fX0="), (cache, guiHandler, player, inventory, slot, event) -> {
            if (cache.getPotionEffectCache().isRecipePotionEffect()) {
                guiHandler.openCluster("recipe_creator");
            } else {
                guiHandler.openCluster("item_creator");
            }
            return true;
        })));

        registerButton(new ActionButton<>("cancel", Material.BARRIER, (cache, guiHandler, player, inventory, slot, event) -> {
            if (cache.getPotionEffectCache().isRecipePotionEffect()) {
                guiHandler.openCluster("recipe_creator");
            } else {
                guiHandler.openCluster("item_creator");
            }
            return true;
        }));

        registerButton(new ActionButton<>("apply", Material.LIME_CONCRETE, (cache, guiHandler, player, inventory, slot, event) -> {
            PotionEffects potionEffectCache = cache.getPotionEffectCache();
            potionEffectCache.applyPotionEffect(cache);
            if (potionEffectCache.isRecipePotionEffect()) {
                guiHandler.openCluster("recipe_creator");
            } else {
                guiHandler.openCluster("item_creator");
            }
            return true;
        }));

        registerButton(new DummyButton<>("preview", Material.POTION, (hashMap, cache, guiHandler, player, inventory, oldItem, i, b) -> {
            PotionEffects potionEffectCache = guiHandler.getCustomCache().getPotionEffectCache();
            ItemStack itemStack = new ItemStack(Material.POTION);
            PotionMeta itemMeta = (PotionMeta) itemStack.getItemMeta();
            itemMeta.setDisplayName("§7 - - - - - ");
            if (potionEffectCache.getType() != null) {
                itemMeta.addCustomEffect(new PotionEffect(potionEffectCache.getType(), potionEffectCache.getDuration(), potionEffectCache.getAmplifier(), potionEffectCache.isAmbient(), potionEffectCache.isParticles(), potionEffectCache.isIcon()), true);
            }
            itemStack.setItemMeta(itemMeta);
            return itemStack;
        }));

        registerButton(new ActionButton<>("potion_effect_type", new ButtonState<>("potion_effect_type", Material.BOOKSHELF, (cache, guiHandler, player, inventory, i, event) -> {
            PotionEffects potionEffectCache = cache.getPotionEffectCache();
            potionEffectCache.setApplyPotionEffectType((cache1, potionEffect) -> potionEffectCache.setType(potionEffect));
            potionEffectCache.setOpenedFrom("potion_creator", "potion_creator");
            guiHandler.openWindow("potion_effect_type_selection");
            return true;
        }, (values, cache, guiHandler, player, inventory, itemStack, i, b) -> {
            PotionEffects potionEffectCache = guiHandler.getCustomCache().getPotionEffectCache();
            values.put("%effect_type%", potionEffectCache.getType() != null ? StringUtils.capitalize(potionEffectCache.getType().getName().replace("_", " ").toLowerCase(Locale.ROOT)) : "&cnone");
            return itemStack;
        })));

        registerButton(new ChatInputButton<>("duration", Material.CLOCK, (hashMap, cache, guiHandler, player, inventory, itemStack, i, b) -> {
            hashMap.put("%duration%", guiHandler.getCustomCache().getPotionEffectCache().getDuration());
            return itemStack;
        }, (guiHandler, player, s, args) -> {
            try {
                guiHandler.getCustomCache().getPotionEffectCache().setDuration(Integer.parseInt(args[0]));
                return false;
            } catch (NumberFormatException e) {
                api.getChat().sendPlayerMessage(player, "item_creator", "main_menu", "potion.error_number");
            }
            return true;
        }));
        registerButton(new ChatInputButton<>("amplifier", Material.IRON_SWORD, (hashMap, cache, guiHandler, player, inventory, itemStack, i, b) -> {
            hashMap.put("%amplifier%", guiHandler.getCustomCache().getPotionEffectCache().getAmplifier());
            return itemStack;
        }, (guiHandler, player, s, args) -> {
            try {
                guiHandler.getCustomCache().getPotionEffectCache().setAmplifier(Integer.parseInt(args[0]));
                return false;
            } catch (NumberFormatException e) {
                api.getChat().sendPlayerMessage(player, "item_creator", "main_menu", "potion.error_number");
            }
            return true;
        }));
        registerButton(new ToggleButton<>("ambient", new ButtonState<>("ambient.enabled", Material.BLAZE_POWDER, (cache, guiHandler, player, inventory, slot, event) -> {
            cache.getPotionEffectCache().setAmbient(false);
            return true;
        }), new ButtonState<>("ambient.disabled", Material.BLAZE_POWDER, (cache, guiHandler, player, inventory, slot, event) -> {
            cache.getPotionEffectCache().setAmbient(true);
            return true;
        })));
        registerButton(new ToggleButton<>("particles", new ButtonState<>("particles.enabled", Material.FIREWORK_ROCKET, (cache, guiHandler, player, inventory, slot, event) -> {
            cache.getPotionEffectCache().setParticles(false);
            return true;
        }), new ButtonState<>("particles.disabled", Material.FIREWORK_ROCKET, (cache, guiHandler, player, inventory, slot, event) -> {
            cache.getPotionEffectCache().setParticles(true);
            return true;
        })));
        registerButton(new ToggleButton<>("icon", new ButtonState<>("icon.enabled", Material.ITEM_FRAME, (cache, guiHandler, player, inventory, slot, event) -> {
            cache.getPotionEffectCache().setIcon(false);
            return true;
        }), new ButtonState<>("icon.disabled", Material.ITEM_FRAME, (cache, guiHandler, player, inventory, slot, event) -> {
            cache.getPotionEffectCache().setIcon(true);
            return true;
        })));

    }

    @Override
    public void onUpdateSync(GuiUpdate<CCCache> guiUpdate) {

    }

    @Override
    public void onUpdateAsync(GuiUpdate<CCCache> update) {
        super.onUpdateAsync(update);
        GuiHandler<CCCache> guiHandler = update.getGuiHandler();
        PotionEffects potionEffectCache = guiHandler.getCustomCache().getPotionEffectCache();
        update.setButton(0, "back");
        update.setButton(11, "apply");
        update.setButton(13, "preview");
        update.setButton(15, "cancel");

        update.setButton(28, "potion_effect_type");
        update.setButton(30, "duration");
        update.setButton(32, "amplifier");
        ((ToggleButton<CCCache>) getButton("ambient")).setState(guiHandler, potionEffectCache.isAmbient());
        ((ToggleButton<CCCache>) getButton("particles")).setState(guiHandler, potionEffectCache.isParticles());
        ((ToggleButton<CCCache>) getButton("icon")).setState(guiHandler, potionEffectCache.isIcon());
        update.setButton(34, "ambient");
        update.setButton(38, "particles");
        update.setButton(42, "icon");
    }
}
