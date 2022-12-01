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

package me.wolfyscript.customcrafting.placeholderapi;

import com.wolfyscript.utilities.NamespacedKey;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCPlayerData;
import me.wolfyscript.customcrafting.recipes.CraftingRecipe;
import me.wolfyscript.customcrafting.recipes.CustomRecipe;
import me.wolfyscript.customcrafting.recipes.conditions.Conditions;
import me.wolfyscript.customcrafting.utils.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class PlaceHolder extends PlaceholderExpansion {

    private final CustomCrafting customCrafting;

    public PlaceHolder(CustomCrafting customCrafting) {
        this.customCrafting = customCrafting;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    /**
     * The placeholder identifier should go here.
     * <br>This is what tells PlaceholderAPI to call our onRequest
     * method to obtain a value if a placeholder starts with our
     * identifier.
     * <br>This must be unique and can not contain % or _
     *
     * @return The identifier in {@code %<identifier>_<value>%} as String.
     */
    @Override
    public String getIdentifier() {
        return "customcrafting";
    }

    @Override
    public String getAuthor() {
        return customCrafting.getDescription().getAuthors().toString();
    }

    @Override
    public String getVersion() {
        return customCrafting.getDescription().getVersion();
    }

    @Override
    public String onRequest(OfflinePlayer p, String identifier) {
        if (p == null) {
            return "";
        }
        CCPlayerData playerData = PlayerUtil.getStore(p.getUniqueId());
        String[] args = identifier.split("_");
        switch (args[0]) {
            case "crafts":
                return String.valueOf(playerData.getTotalCrafts());
            case "recipes":
                if (args.length > 1) {
                    switch (args[1]) {
                        case "vanilla":
                            return String.valueOf(customCrafting.getDataHandler().getMinecraftRecipes().size());
                        case "custom":
                            return String.valueOf(customCrafting.getRegistries().getRecipes().size());
                        case "available":
                            if (p.isOnline()) {
                                Player player = Bukkit.getPlayer(p.getUniqueId());
                                return String.valueOf(customCrafting.getRegistries().getRecipes().getAvailable(player).size());
                            }
                            break;
                        default:
                            //No type
                    }
                }
                break;
            case "recipe":
                if (args.length > 2) {
                    StringBuilder recipeID = new StringBuilder(args[2]);
                    for (int i = 3; i < args.length; i++) {
                        recipeID.append(args[i]);
                    }
                    NamespacedKey recipeKey = customCrafting.getApi().getIdentifiers().getNamespaced(recipeID.toString());
                    CustomRecipe<?> recipe = customCrafting.getRegistries().getRecipes().get(recipeKey);
                    switch (args[1]) {
                        case "type":
                            return recipe.getRecipeType().toString();
                        case "crafts":
                            return String.valueOf(playerData.getRecipeCrafts(recipeKey));
                        case "advanced":
                            if (recipe instanceof CraftingRecipe) {
                                return String.valueOf(recipe.getConditions().getByID("advanced_workbench").getOption().equals(Conditions.Option.EXACT));
                            }
                            break;
                        case "permission":
                            if (recipe instanceof CraftingRecipe) {
                                return String.valueOf(recipe.getConditions().getByID("permission").getOption().equals(Conditions.Option.EXACT));
                            }
                            break;
                        case "available":
                            if (recipe instanceof CraftingRecipe) {
                                if (p.isOnline()) {
                                    return String.valueOf(recipe.checkCondition("permission", Conditions.Data.of(Bukkit.getPlayer(p.getUniqueId()))));
                                }
                            }
                            break;
                        default:
                    }
                }
                break;
            default:
                //No command
        }
        return "";
    }
}
