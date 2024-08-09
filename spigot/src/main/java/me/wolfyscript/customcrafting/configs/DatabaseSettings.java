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
import org.bukkit.util.NumberConversions;
import org.jetbrains.annotations.NotNull;

public class DatabaseSettings  implements ConfigurationSerializable {

    private static final String ENABLED = "enabled";
    private static final String HOST = "host";
    private static final String PORT = "port";
    private static final String SCHEMA = "schema";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";

    private final boolean enabled;
    private final String host;
    private final int port;
    private final String schema;
    private final String username;
    private final String password;

    public DatabaseSettings() {
        enabled = false;
        host = "localhost";
        port = 0;
        schema = "mc_plugins";
        username = "minecraft";
        password = "";
    }

    public DatabaseSettings(Map<String, Object> values) {
        this.enabled = values.get(ENABLED) instanceof Boolean bool && bool;
        this.host = (String) values.getOrDefault(HOST, "localhost");
        Object portVal = values.get(PORT);
        this.port = portVal instanceof Number ? NumberConversions.toInt(portVal) : 3306;
        this.schema = values.getOrDefault(SCHEMA, "mc_plugins").toString();
        this.username = values.getOrDefault(USERNAME, "minecraft").toString();
        this.password = values.getOrDefault(PASSWORD, "").toString();
    }

    public DatabaseSettings(ConfigurationSection section) {
        this.enabled = section.getBoolean(ENABLED);
        this.host = section.getString(HOST, "localhost");
        this.port = section.getInt(PORT, 3306);
        this.schema = section.getString(SCHEMA, "mc_plugins");
        this.username = section.getString(USERNAME, "minecraft");
        this.password = section.getString(PASSWORD, "");
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> result = new HashMap<>();
        result.put(ENABLED, enabled);
        result.put(HOST, host);
        result.put(PORT, port);
        result.put(SCHEMA, schema);
        result.put(USERNAME, username);
        result.put(PASSWORD, password);
        return result;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getSchema() {
        return schema;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
