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

import java.util.HashMap;
import java.util.Map;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

public class LocalStorageSettings  implements ConfigurationSerializable {

    private static final String LOAD = "load";
    private static final String BEFORE_DATABASE = "before_database";
    private static final String OVERRIDE = "override";

    private final boolean load;
    private final boolean beforeDatabase;
    private final boolean override;

    public LocalStorageSettings(Map<String, Object> values) {
        this.load = values.get(LOAD) instanceof Boolean bool && bool;
        this.beforeDatabase = values.get(BEFORE_DATABASE) instanceof Boolean bool && bool;
        this.override = values.get(OVERRIDE) instanceof Boolean bool && bool;
    }

    public LocalStorageSettings(ConfigurationSection section) {
        this.load = section.getBoolean(LOAD);
        this.beforeDatabase = section.getBoolean(BEFORE_DATABASE);
        this.override = section.getBoolean(OVERRIDE);
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> result = new HashMap<>();
        result.put(LOAD, load);
        result.put(BEFORE_DATABASE, beforeDatabase);
        result.put(OVERRIDE, override);
        return result;
    }

    public boolean isEnabled() {
        return load;
    }

    public boolean isBeforeDatabase() {
        return beforeDatabase;
    }

    public boolean isOverride() {
        return override;
    }
}
