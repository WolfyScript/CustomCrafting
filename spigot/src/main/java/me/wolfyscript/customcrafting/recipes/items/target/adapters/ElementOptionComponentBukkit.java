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

package me.wolfyscript.customcrafting.recipes.items.target.adapters;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.lib.net.kyori.adventure.platform.bukkit.BukkitComponentSerializer;
import me.wolfyscript.lib.net.kyori.adventure.text.minimessage.MiniMessage;
import me.wolfyscript.lib.net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import me.wolfyscript.utilities.util.eval.context.EvalContext;
import me.wolfyscript.utilities.util.eval.value_providers.ValueProvider;

import java.util.List;
import java.util.Objects;

public class ElementOptionComponentBukkit extends ElementOption<String, String> {

    @Deprecated(forRemoval = true)
    public boolean isEqual(String value, EvalContext evalContext, MiniMessage miniMessage, TagResolver... tagResolvers) {
        return isComponentEqual(value, evalContext, miniMessage, tagResolvers);
    }

    public boolean isComponentEqual(String value, EvalContext evalContext, MiniMessage miniMessage, TagResolver... tagResolvers) {
        for (ValueProvider<String> valueProvider : values()) {
            if (Objects.equals(miniMessage.deserialize(valueProvider.getValue(evalContext), tagResolvers), BukkitComponentSerializer.legacy().deserialize(value))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isEqual(String value, EvalContext evalContext) {
        return isComponentEqual(value, evalContext, CustomCrafting.inst().getApi().getChat().getMiniMessage(), TagResolver.empty());
    }

    public List<String> readFromSource(List<String> source, EvalContext evalContext, MiniMessage miniMessage, TagResolver... tagResolvers) {
        return readFromSource(source, s -> isComponentEqual(s, evalContext, miniMessage, tagResolvers), evalContext);
    }
}
