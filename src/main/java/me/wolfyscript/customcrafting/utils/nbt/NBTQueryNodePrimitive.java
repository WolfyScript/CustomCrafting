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

package me.wolfyscript.customcrafting.utils.nbt;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.wolfyscript.lib.nbt.nbtapi.NBTCompound;
import com.wolfyscript.lib.nbt.nbtapi.NBTType;
import me.wolfyscript.lib.com.fasterxml.jackson.databind.JsonNode;

import java.util.Optional;

public class NBTQueryNodePrimitive<T extends NBTType, V> extends NBTQueryNode<T> {

    private final V value;

    public NBTQueryNodePrimitive(String key, T type, V value) {
        super(key, type);
        this.value = value;
    }

    @Override
    public boolean check(String key, NBTType type, NBTCompound parent) {
        return false;
    }

    public static Optional<NBTQueryNodePrimitive<?, ?>> construct(String key, JsonNode node, JsonParser p, DeserializationContext ctxt) {
        if (node.isTextual()) {
            var text = node.asText();
            if (!text.isBlank()) {
                char identifier = text.charAt(text.length() - 1);
                String value = text.substring(0, text.length() - 1);
                return Optional.of(switch (identifier) {
                    case 'b','B' -> new NBTQueryNodePrimitive<>(key, NBTType.NBTTagByte, Byte.parseByte(value));
                    case 's','S' -> new NBTQueryNodePrimitive<>(key, NBTType.NBTTagShort, Short.parseShort(value));
                    case 'i','I' -> new NBTQueryNodePrimitive<>(key, NBTType.NBTTagInt, Integer.parseInt(value));
                    case 'l','L' -> new NBTQueryNodePrimitive<>(key, NBTType.NBTTagLong, Long.parseLong(value));
                    case 'f','F' -> new NBTQueryNodePrimitive<>(key, NBTType.NBTTagFloat, Float.parseFloat(value));
                    case 'd','D' -> new NBTQueryNodePrimitive<>(key, NBTType.NBTTagDouble, Double.parseDouble(value));
                    default -> new NBTQueryNodePrimitive<>(key, NBTType.NBTTagString, Double.parseDouble(value));
                });
            }
        } else {
            NBTQueryNodePrimitive<?, ?> result = null;
            if(node.isInt()) {
                result = new NBTQueryNodePrimitive<>(key, NBTType.NBTTagInt, node.asInt(0));
            } else if (node.isDouble()) {
                result = new NBTQueryNodePrimitive<>(key, NBTType.NBTTagDouble, node.asDouble(0d));
            }
            return Optional.ofNullable(result);
        }
        return Optional.empty();
    }
}
