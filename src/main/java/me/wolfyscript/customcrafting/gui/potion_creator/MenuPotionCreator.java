/*
 *       ____ _  _ ____ ___ ____ _  _ ____ ____ ____ ____ ___ _ _  _ ____
 *       |    |  | [__   |  |  | |\/| |    |__/ |__| |___  |  | |\ | | __
 *       |___ |__| ___]  |  |__| |  | |___ |  \ |  | |     |  | | \| |__]
 *
 *       CustomCrafting Recipe creation and management tool for Minecraft
 *                      Copyright (C) 2021  WolfyScript
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.wolfyscript.customcrafting.gui.potion_creator;

import com.wolfyscript.utilities.bukkit.BukkitNamespacedKey;
import com.wolfyscript.utilities.bukkit.gui.GuiCluster;
import com.wolfyscript.utilities.bukkit.gui.GuiHandler;
import com.wolfyscript.utilities.bukkit.gui.GuiUpdate;
import com.wolfyscript.utilities.bukkit.gui.button.ButtonAction;
import com.wolfyscript.utilities.bukkit.gui.button.ButtonChatInput;
import com.wolfyscript.utilities.bukkit.gui.button.ButtonDummy;
import com.wolfyscript.utilities.bukkit.gui.button.ButtonState;
import com.wolfyscript.utilities.bukkit.gui.button.ButtonToggle;
import com.wolfyscript.utilities.bukkit.world.inventory.PlayerHeadUtils;
import java.util.Locale;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.potions.PotionEffects;
import me.wolfyscript.customcrafting.gui.CCWindow;
import me.wolfyscript.customcrafting.gui.main_gui.ClusterMain;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;

public class MenuPotionCreator extends CCWindow {

    public MenuPotionCreator(GuiCluster<CCCache> cluster, CustomCrafting customCrafting) {
        super(cluster, ClusterPotionCreator.POTION_CREATOR.getKey(), 54, customCrafting);
    }

    @Override
    public void onInit() {
        registerButton(new ButtonAction<>("back", new ButtonState<>(ClusterMain.BACK, PlayerHeadUtils.getViaURL("864f779a8e3ffa231143fa69b96b14ee35c16d669e19c75fd1a7da4bf306c"), (cache, guiHandler, player, inventory, slot, event) -> {
            if (cache.getPotionEffectCache().isRecipePotionEffect()) {
                guiHandler.openCluster("recipe_creator");
            } else {
                guiHandler.openCluster("item_creator");
            }
            return true;
        })));

        registerButton(new ButtonAction<>("cancel", Material.BARRIER, (cache, guiHandler, player, inventory, slot, event) -> {
            if (cache.getPotionEffectCache().isRecipePotionEffect()) {
                guiHandler.openCluster("recipe_creator");
            } else {
                guiHandler.openCluster("item_creator");
            }
            return true;
        }));

        registerButton(new ButtonAction<>("apply", Material.LIME_CONCRETE, (cache, guiHandler, player, inventory, slot, event) -> {
            PotionEffects potionEffectCache = cache.getPotionEffectCache();
            potionEffectCache.applyPotionEffect(cache);
            if (potionEffectCache.isRecipePotionEffect()) {
                guiHandler.openCluster("recipe_creator");
            } else {
                guiHandler.openCluster("item_creator");
            }
            return true;
        }));

        registerButton(new ButtonDummy<>("preview", Material.POTION, (hashMap, cache, guiHandler, player, inventory, oldItem, i, b) -> {
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

        registerButton(new ButtonAction<>("potion_effect_type", new ButtonState<>("potion_effect_type", Material.BOOKSHELF, (cache, guiHandler, player, inventory, btn, i, event) -> {
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

        registerButton(new ButtonChatInput<>("duration", Material.CLOCK, (hashMap, cache, guiHandler, player, inventory, itemStack, i, b) -> {
            hashMap.put("%duration%", guiHandler.getCustomCache().getPotionEffectCache().getDuration());
            return itemStack;
        }, (guiHandler, player, s, args) -> {
            try {
                guiHandler.getCustomCache().getPotionEffectCache().setDuration(Integer.parseInt(args[0]));
                return false;
            } catch (NumberFormatException e) {
                api.getChat().sendKey(player, new BukkitNamespacedKey("item_creator", "main_menu"), "potion.error_number");
            }
            return true;
        }));
        registerButton(new ButtonChatInput<>("amplifier", Material.IRON_SWORD, (hashMap, cache, guiHandler, player, inventory, itemStack, i, b) -> {
            hashMap.put("%amplifier%", guiHandler.getCustomCache().getPotionEffectCache().getAmplifier());
            return itemStack;
        }, (guiHandler, player, s, args) -> {
            try {
                guiHandler.getCustomCache().getPotionEffectCache().setAmplifier(Integer.parseInt(args[0]));
                return false;
            } catch (NumberFormatException e) {
                api.getChat().sendKey(player, new BukkitNamespacedKey("item_creator", "main_menu"), "potion.error_number");
            }
            return true;
        }));
        registerButton(new ButtonToggle<>("ambient", new ButtonState<>("ambient.enabled", Material.BLAZE_POWDER, (cache, guiHandler, player, inventory, slot, event) -> {
            cache.getPotionEffectCache().setAmbient(false);
            return true;
        }), new ButtonState<>("ambient.disabled", Material.BLAZE_POWDER, (cache, guiHandler, player, inventory, slot, event) -> {
            cache.getPotionEffectCache().setAmbient(true);
            return true;
        })));
        registerButton(new ButtonToggle<>("particles", new ButtonState<>("particles.enabled", Material.FIREWORK_ROCKET, (cache, guiHandler, player, inventory, slot, event) -> {
            cache.getPotionEffectCache().setParticles(false);
            return true;
        }), new ButtonState<>("particles.disabled", Material.FIREWORK_ROCKET, (cache, guiHandler, player, inventory, slot, event) -> {
            cache.getPotionEffectCache().setParticles(true);
            return true;
        })));
        registerButton(new ButtonToggle<>("icon", new ButtonState<>("icon.enabled", Material.ITEM_FRAME, (cache, guiHandler, player, inventory, slot, event) -> {
            cache.getPotionEffectCache().setIcon(false);
            return true;
        }), new ButtonState<>("icon.disabled", Material.ITEM_FRAME, (cache, guiHandler, player, inventory, slot, event) -> {
            cache.getPotionEffectCache().setIcon(true);
            return true;
        })));

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
        ((ButtonToggle<CCCache>) getButton("ambient")).setState(guiHandler, potionEffectCache.isAmbient());
        ((ButtonToggle<CCCache>) getButton("particles")).setState(guiHandler, potionEffectCache.isParticles());
        ((ButtonToggle<CCCache>) getButton("icon")).setState(guiHandler, potionEffectCache.isIcon());
        update.setButton(34, "ambient");
        update.setButton(38, "particles");
        update.setButton(42, "icon");
    }
}
