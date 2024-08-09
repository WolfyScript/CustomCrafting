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

package me.wolfyscript.customcrafting.configs;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class CookingSettings implements ConfigurationSerializable {

    private static final String MATCH_VANILLA_RECIPES = "match_vanilla_recipes";

    private final boolean matchVanillaRecipes;

    public CookingSettings(Map<String, Object> values) {
        this.matchVanillaRecipes = values.get(MATCH_VANILLA_RECIPES) instanceof Boolean bool && bool;
    }

    public CookingSettings(ConfigurationSection section) {
        this.matchVanillaRecipes = section.getBoolean(MATCH_VANILLA_RECIPES, true);
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> result = new HashMap<>();
        result.put(MATCH_VANILLA_RECIPES, matchVanillaRecipes);
        return result;
    }

    public boolean isMatchVanillaRecipes() {
        return matchVanillaRecipes;
    }

}
