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

package me.wolfyscript.customcrafting.gui.cauldron;

import com.wolfyscript.utilities.NamespacedKey;
import com.wolfyscript.utilities.bukkit.BukkitNamespacedKey;
import com.wolfyscript.utilities.bukkit.gui.InventoryAPI;
import com.wolfyscript.utilities.bukkit.gui.callback.CallbackButtonAction;
import com.wolfyscript.utilities.common.gui.ButtonInteractionResult;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.CCCluster;
import me.wolfyscript.customcrafting.gui.recipebook.ButtonContainerIngredient;
import me.wolfyscript.customcrafting.utils.PlayerUtil;
import org.bukkit.Material;

public class CauldronWorkstationCluster extends CCCluster {

    public static final String KEY = "cauldron";
    public static final NamespacedKey CAULDRON_MAIN = new BukkitNamespacedKey(KEY, "cauldron");
    public static final NamespacedKey CAULDRON_RESULT = new BukkitNamespacedKey(KEY, "result");

    public CauldronWorkstationCluster(InventoryAPI<CCCache> inventoryAPI, CustomCrafting customCrafting) {
        super(inventoryAPI, KEY, customCrafting);
    }

    @Override
    public void onInit() {
        setEntry(CAULDRON_MAIN);
        
        registerGuiWindow(new CauldronWorkstationMenu(this, customCrafting));

        getButtonBuilder().action(CAULDRON_MAIN.getKey()).state(state -> state.icon(Material.KNOWLEDGE_BOOK).action((holder, cache, btn, slot, details) -> {
            ButtonContainerIngredient.resetButtons(holder.getGuiHandler());
            PlayerUtil.openRecipeBook(holder.getPlayer());
            return ButtonInteractionResult.cancel(true);
        })).register();
    }
}
