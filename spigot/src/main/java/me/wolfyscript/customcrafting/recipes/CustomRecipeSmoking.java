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

import com.wolfyscript.utilities.verification.Verifier;
import com.wolfyscript.utilities.verification.VerifierBuilder;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JacksonInject;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonCreator;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonProperty;
import me.wolfyscript.lib.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.SmokingRecipe;

public class CustomRecipeSmoking extends CustomRecipeCooking<CustomRecipeSmoking, SmokingRecipe> {

    static {
        final Verifier<CustomRecipeSmoking> VERIFIER = VerifierBuilder.<CustomRecipeSmoking>object(RecipeType.SMOKER.getNamespacedKey(), CustomRecipeCooking.validator())
                .name(container -> "Smoking Recipe" + container.value().map(customRecipeSmithing -> " [" + customRecipeSmithing.getNamespacedKey() + "]").orElse(""))
                .build();
        CustomCrafting.inst().getRegistries().getVerifiers().register(VERIFIER);
    }

    public CustomRecipeSmoking(NamespacedKey namespacedKey, JsonNode node) {
        super(namespacedKey, node);
    }

    @JsonCreator
    public CustomRecipeSmoking(@JsonProperty("key") @JacksonInject("key") NamespacedKey key, @JacksonInject("customcrafting") CustomCrafting customCrafting) {
        super(key, customCrafting);
    }

    @Deprecated
    public CustomRecipeSmoking(NamespacedKey key) {
        this(key, CustomCrafting.inst());
    }

    private CustomRecipeSmoking(CustomRecipeSmoking customRecipeSmoking) {
        super(customRecipeSmoking);
    }

    @Override
    public CustomRecipeSmoking clone() {
        return new CustomRecipeSmoking(this);
    }

    @Override
    public boolean validType(Material material) {
        return material.equals(Material.SMOKER);
    }

    @Override
    public SmokingRecipe getVanillaRecipe() {
        if (!getSource().isEmpty()) {
            SmokingRecipe placeholderRecipe = new SmokingRecipe(ICustomVanillaRecipe.toPlaceholder(getNamespacedKey()).bukkit(), getResult().getItemStack(), getMaterialSourceChoice(), getExp(), getCookingTime());
            if(Bukkit.getRecipe(placeholderRecipe.getKey())!=null)
                Bukkit.removeRecipe(placeholderRecipe.getKey());
            Bukkit.addRecipe(placeholderRecipe);
            return new SmokingRecipe(ICustomVanillaRecipe.toDisplayKey(getNamespacedKey()).bukkit(), getResult().getItemStack(), getSourceChoice(), getExp(), getCookingTime());
        }
        return null;
    }
}
