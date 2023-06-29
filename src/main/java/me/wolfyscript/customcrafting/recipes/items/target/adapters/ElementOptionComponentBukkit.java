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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ElementOptionComponentBukkit extends ElementOption<String, String> {

    public boolean isEqual(String value, EvalContext evalContext, MiniMessage miniMessage, TagResolver... tagResolvers) {
        boolean exclude = exclude().map(shouldExclude -> shouldExclude.evaluate(evalContext)).orElse(false);
        for (ValueProvider<String> valueProvider : values()) {
            if (Objects.equals(miniMessage.deserialize(valueProvider.getValue(evalContext), tagResolvers), BukkitComponentSerializer.legacy().deserialize(value))) {
                return !exclude;
            }
        }
        return exclude;
    }

    @Override
    public boolean isEqual(String value, EvalContext evalContext) {
        return isEqual(value, evalContext, CustomCrafting.inst().getApi().getChat().getMiniMessage(), TagResolver.empty());
    }

    public List<String> readFromSource(List<String> source, EvalContext evalContext, MiniMessage miniMessage, TagResolver... tagResolvers) {
        List<String> result = new ArrayList<>();
        if (condition().map(boolOperator -> boolOperator.evaluate(evalContext)).orElse(true)) {
            index().ifPresentOrElse(indexProvider -> {
                int index = indexProvider.getValue(evalContext);
                if (index < 0) {
                    index = source.size() + (index % source.size()); //Convert the negative index to a positive reverted index, that starts from the end.
                }
                index = index % source.size(); //Prevent out of bounds
                if (source.size() > index) {
                    String targetValue = source.get(index);
                    if (isEqual(targetValue, evalContext, miniMessage, tagResolvers)) {
                        result.add(targetValue);
                    }
                }
            }, () -> {
                if (values().isEmpty()) {
                    result.addAll(source);
                    return;
                }
                for (String targetValue : source) {
                    if (isEqual(targetValue, evalContext, miniMessage, tagResolvers)) {
                        result.add(targetValue);
                    }
                }
            });
        }
        return result;
    }
}
