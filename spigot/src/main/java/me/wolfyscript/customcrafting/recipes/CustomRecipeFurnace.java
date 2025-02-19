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
import org.bukkit.inventory.FurnaceRecipe;

public class CustomRecipeFurnace extends CustomRecipeCooking<CustomRecipeFurnace, FurnaceRecipe> {

    static {
        final Verifier<CustomRecipeFurnace> VERIFIER = VerifierBuilder.<CustomRecipeFurnace>object(RecipeType.FURNACE.getNamespacedKey(), CustomRecipeCooking.validator())
                .name(container -> "Furnace Recipe" + container.value().map(customRecipeSmithing -> " [" + customRecipeSmithing.getNamespacedKey() + "]").orElse(""))
                .build();
        CustomCrafting.inst().getRegistries().getVerifiers().register(VERIFIER);
    }

    public CustomRecipeFurnace(NamespacedKey namespacedKey, JsonNode node) {
        super(namespacedKey, node);
    }

    @JsonCreator
    public CustomRecipeFurnace(@JsonProperty("key") @JacksonInject("key") NamespacedKey key, @JacksonInject("customcrafting") CustomCrafting customCrafting) {
        super(key, customCrafting);
    }

    @Deprecated
    public CustomRecipeFurnace(NamespacedKey key) {
        this(key, CustomCrafting.inst());
    }

    public CustomRecipeFurnace(CustomRecipeFurnace customRecipeFurnace) {
        super(customRecipeFurnace);
    }

    @Override
    public FurnaceRecipe getVanillaRecipe() {
        if (!getSource().isEmpty()) {
            FurnaceRecipe placeholderRecipe = new FurnaceRecipe(ICustomVanillaRecipe.toPlaceholder(getNamespacedKey()).bukkit(), getResult().getItemStack(), getMaterialSourceChoice(), getExp(), getCookingTime());
            if(Bukkit.getRecipe(placeholderRecipe.getKey())!=null)
                Bukkit.removeRecipe(placeholderRecipe.getKey());
            Bukkit.addRecipe(placeholderRecipe);
            //registerRecipeIntoMinecraft(new FunctionalRecipeBuilderSmelting(getNamespacedKey(), getResult().getItemStack(), getRecipeChoice()));
            return new FurnaceRecipe(ICustomVanillaRecipe.toDisplayKey(getNamespacedKey()).bukkit(), getResult().getItemStack(), getSourceChoice(), getExp(), getCookingTime());
        }
        return null;
    }

    @Override
    public CustomRecipeFurnace clone() {
        return new CustomRecipeFurnace(this);
    }

    @Override
    public boolean validType(Material material) {
        return material.equals(Material.FURNACE);
    }
}
