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

package me.wolfyscript.customcrafting.recipes;

import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JacksonInject;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonCreator;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonProperty;
import me.wolfyscript.lib.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.api.nms.RecipeUtil;
import me.wolfyscript.utilities.api.nms.item.crafting.FunctionalCampfireRecipe;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.Material;
import org.bukkit.inventory.CampfireRecipe;

public class CustomRecipeCampfire extends CustomRecipeCooking<CustomRecipeCampfire, CampfireRecipe> {

    public CustomRecipeCampfire(NamespacedKey namespacedKey, JsonNode node) {
        super(namespacedKey, node);
    }

    @JsonCreator
    public CustomRecipeCampfire(@JsonProperty("key") @JacksonInject("key") NamespacedKey key) {
        super(key);
    }

    public CustomRecipeCampfire(CustomRecipeCampfire customRecipeCampfire) {
        super(customRecipeCampfire);
    }

    @Override
    public CampfireRecipe getVanillaRecipe() {
        if (!getSource().isEmpty()) {
            RecipeUtil recipeUtil = api.getNmsUtil().getRecipeUtil();
            FunctionalCampfireRecipe campfireRecipe = recipeUtil.campfireRecipe(getNamespacedKey(), getGroup(), getResult().getItemStack(), getRecipeChoice(), getExp(), getCookingTime(), (inventory, world) -> getSource().test(inventory.getItem(0), isCheckNBT()));
            campfireRecipe.setAssembler(inventory -> java.util.Optional.ofNullable(getResult().getItemStack()));
            recipeUtil.registerCookingRecipe(campfireRecipe);
        }
        return null;
    }

    @Override
    public CustomRecipeCampfire clone() {
        return new CustomRecipeCampfire(this);
    }

    @Override
    public boolean validType(Material material) {
        return material.equals(Material.CAMPFIRE);
    }
}
