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

import com.wolfyscript.lib.nbt.nbtapi.NBTCompound;
import com.wolfyscript.lib.nbt.nbtapi.NBTContainer;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonCreator;
import me.wolfyscript.lib.com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Map;

public class NBTQuery {

    private Map<String, NBTQueryNode> nodes;

    @JsonCreator
    public NBTQuery(ObjectNode node) {

        node.fields().forEachRemaining(entry -> {

        });

    }



    public NBTCompound find() {
        NBTContainer container = new NBTContainer();






        return (NBTCompound) new NBTContainer();
    }


}
