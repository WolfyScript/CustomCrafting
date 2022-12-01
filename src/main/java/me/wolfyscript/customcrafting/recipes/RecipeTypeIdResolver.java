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

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsontype.impl.TypeIdResolverBase;
import com.fasterxml.jackson.databind.type.TypeFactory;
import me.wolfyscript.customcrafting.CustomCrafting;

public class RecipeTypeIdResolver extends TypeIdResolverBase {

    private JavaType superType;

    @Override
    public void init(JavaType baseType) {
        super.init(baseType);
        this.superType = baseType;
    }

    @Override
    public String idFromValue(Object value) {
        return getKey(value);
    }

    @Override
    public String idFromValueAndType(Object value, Class<?> aClass) {
        return getKey(value);
    }

    private String getKey(Object value) {
        if (value instanceof CustomRecipe<?> customRecipe) {
            return customRecipe.type.toString();
        } else {
            throw new IllegalArgumentException(String.format("Object %s is not of type CustomRecipe!", value.getClass().getName()));
        }
    }

    @Override
    public JavaType typeFromId(DatabindContext context, String id) {
        RecipeType<?> recipeType = CustomCrafting.inst().getRegistries().getRecipeTypes().get(CustomCrafting.inst().getApi().getIdentifiers().getNamespaced(id));
        if (recipeType != null) {
            Class<?> clazz = recipeType.getRecipeClass();
            return clazz != null ? context.constructSpecializedType(this.superType, clazz) : TypeFactory.unknownType();
        }
        return TypeFactory.unknownType();
    }

    @Override
    public JsonTypeInfo.Id getMechanism() {
        return JsonTypeInfo.Id.CUSTOM;
    }
}
