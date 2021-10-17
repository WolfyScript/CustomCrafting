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

package me.wolfyscript.customcrafting.gui.item_creator.tabs;

import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.items.Items;
import me.wolfyscript.customcrafting.gui.item_creator.ButtonOption;
import me.wolfyscript.customcrafting.gui.item_creator.ButtonParticleEffectSelect;
import me.wolfyscript.customcrafting.gui.item_creator.MenuItemCreator;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.DummyButton;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.particles.ParticleLocation;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class TabParticleEffects extends ItemCreatorTab {

    public static final String KEY = "particle_effects";

    public TabParticleEffects() {
        super(new NamespacedKey(NamespacedKeyUtils.NAMESPACE, KEY));
    }

    @Override
    public void register(MenuItemCreator creator, WolfyUtilities api) {
        creator.registerButton(new ButtonOption(Material.FIREWORK_ROCKET, this));
        /* TODO
        creator.registerButton(new DummyButton<>("particle_effects.head", Material.IRON_HELMET));
        creator.registerButton(new ButtonParticleEffectSelect(ParticleLocation.HEAD));
        creator.registerButton(new DummyButton<>("particle_effects.chest", Material.IRON_CHESTPLATE));
        creator.registerButton(new ButtonParticleEffectSelect(ParticleLocation.CHEST));
        creator.registerButton(new DummyButton<>("particle_effects.legs", Material.IRON_LEGGINGS));
        creator.registerButton(new ButtonParticleEffectSelect(ParticleLocation.LEGS));
        creator.registerButton(new DummyButton<>("particle_effects.feet", Material.IRON_BOOTS));
        creator.registerButton(new ButtonParticleEffectSelect(ParticleLocation.FEET));
        creator.registerButton(new DummyButton<>("particle_effects.hand", Material.IRON_SWORD));
        creator.registerButton(new ButtonParticleEffectSelect(ParticleLocation.HAND));
        creator.registerButton(new DummyButton<>("particle_effects.off_hand", Material.SHIELD));
        creator.registerButton(new ButtonParticleEffectSelect(ParticleLocation.OFF_HAND));
        creator.registerButton(new DummyButton<>("particle_effects.block", Material.GRASS_BLOCK));
        creator.registerButton(new ButtonParticleEffectSelect(ParticleLocation.BLOCK));

         */
    }

    @Override
    public void render(GuiUpdate<CCCache> update, CCCache cache, Items items, CustomItem customItem, ItemStack item) {
        update.setButton(28, "particle_effects.head");
        update.setButton(29, "particle_effects.chest");
        update.setButton(30, "particle_effects.legs");
        update.setButton(31, "particle_effects.feet");
        update.setButton(32, "particle_effects.hand");
        update.setButton(33, "particle_effects.off_hand");
        update.setButton(34, "particle_effects.block");

        update.setButton(37, "particle_effects.head.input");
        update.setButton(38, "particle_effects.chest.input");
        update.setButton(39, "particle_effects.legs.input");
        update.setButton(40, "particle_effects.feet.input");
        update.setButton(41, "particle_effects.hand.input");
        update.setButton(42, "particle_effects.off_hand.input");
        update.setButton(43, "particle_effects.block.input");
    }
}
