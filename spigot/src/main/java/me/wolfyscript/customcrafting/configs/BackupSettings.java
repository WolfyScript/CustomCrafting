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

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class BackupSettings {

    private static final String ENABLED = "enabled";
    private static final String KEEP_FOR = "keep_for";

    private final ConfigurationSection section;

    public BackupSettings(ConfigurationSection section) {
        this.section = section;
    }

    public boolean enabled() {
        return section.getBoolean(ENABLED, true);
    }

    public Duration keepFor() {
        var unit = Objects.requireNonNullElse(TimeUnit.valueOf(section.getString(KEEP_FOR + ".unit", "SECONDS")), TimeUnit.SECONDS);
        var value = section.getLong(KEEP_FOR + ".value");

        return Duration.of(value, unit.toChronoUnit());
    }

}
