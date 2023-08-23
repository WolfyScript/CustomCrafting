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

import me.wolfyscript.utilities.util.Pair;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class DataSettings {

    private static final String PRINT_PENDING = "print_pending";
    private static final String PRINT_INVALID = "print_invalid";
    private static final String TIMEOUT_LOADING = "timeout.loading";
    private static final String TIMEOUT_PENDING = "timeout.pending";
    private static final String PRINT_STACKTRACE = "print_stacktrace";
    private static final String BUKKIT_VERSION = "bukkit_version";
    private static final String CONFIG_VERSION = "version";

    private final ConfigurationSection section;

    public DataSettings(ConfigurationSection section) {
        this.section = section;
    }

    private void setTimeout(String key, Pair<Long, TimeUnit> value, Map<String, Object> result) {
        result.put(key + ".value", value.getKey());
        result.put(key + ".unit", value.getValue().toString());
    }

    private Pair<Long, TimeUnit> getTimeout(ConfigurationSection section, String key) {
        return new Pair<>(section.getLong(key + ".value"),
                Objects.requireNonNullElse(TimeUnit.valueOf(section.getString(key + ".unit")), TimeUnit.SECONDS));
    }

    public boolean isPrintPending() {
        return section.getBoolean(PRINT_PENDING);
    }

    public boolean isPrintInvalid() {
        return section.getBoolean(PRINT_INVALID);
    }

    public boolean isPrintStackTrace() {
        return section.getBoolean(PRINT_STACKTRACE);
    }

    public int getBukkitVersion() {
        return section.getInt(BUKKIT_VERSION);
    }

    public int getConfigVersion() {
        return section.getInt(CONFIG_VERSION);
    }

    public Pair<Long, TimeUnit> getTimeoutPending() {
        return getTimeout(section, TIMEOUT_PENDING);
    }

    public Pair<Long, TimeUnit> getTimeoutLoading() {
        return getTimeout(section, TIMEOUT_LOADING);
    }

    public void setBukkitVersion(int bukkitVersion) {
        section.set(BUKKIT_VERSION, bukkitVersion);
    }

    public void setConfigVersion(int configVersion) {
        section.set(CONFIG_VERSION, configVersion);
    }

    public void setPrintInvalid(boolean printFailed) {
        section.set(PRINT_INVALID, printFailed);
    }

    public void setPrintPending(boolean printPending) {
        section.set(PRINT_PENDING, printPending);
    }

}
