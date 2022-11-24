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

package me.wolfyscript.customcrafting.gui.item_creator;

import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.items.Items;
import me.wolfyscript.customcrafting.data.cache.items.ItemsButtonAction;
import com.wolfyscript.utilities.bukkit.gui.button.ButtonState;
import com.wolfyscript.utilities.bukkit.gui.button.ButtonAction;
import me.wolfyscript.utilities.util.particles.ParticleLocation;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Locale;

public class ButtonParticleEffectSelect extends ButtonAction<CCCache> {

    public ButtonParticleEffectSelect(ParticleLocation action) {
        super("particle_effects." + action.toString().toLowerCase(Locale.ROOT) + ".input", new ButtonState<>("particle_effects.input", Material.BARRIER, (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
            if (event instanceof InventoryClickEvent clickEvent) {
                if (clickEvent.getClick().isShiftClick()) {
                    //TODO: new particle system. items.getItem().getParticleContent().set(action);
                } else {
                    cache.getParticleCache().setAction(action);
                    guiHandler.openCluster("particle_creator");
                }
            }
            return true;
        }, (hashMap, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            Items items = guiHandler.getCustomCache().getItems();
            /*
            ParticleEffect particleEffect = Registry.PARTICLE_EFFECTS.get(items.getItem().getParticleContent().getParticleEffect(action));
            if (particleEffect != null) {
                itemStack.setType(particleEffect.getIcon());
                hashMap.put("%effect_name%", particleEffect.getName());
                hashMap.put("%effect_description%", particleEffect.getDescription());
            } else {
                itemStack.setType(Material.AIR);
            }

             */
            return itemStack;
        }));
    }
}
