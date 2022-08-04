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

package me.wolfyscript.customcrafting.compatibility;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.compatibility.protocollib.ProtocolLib;
import me.wolfyscript.customcrafting.placeholderapi.PlaceHolder;
import me.wolfyscript.utilities.api.WolfyUtilities;
import org.bukkit.Bukkit;

public class OtherPlugins {

    private final CustomCrafting plugin;

    private ProtocolLib protocolLib = null;

    public OtherPlugins(CustomCrafting plugin) {
        this.plugin = plugin;
    }

    public void init() {
        if (Bukkit.getPluginManager().getPlugin("ProtocolLib") != null) {
            plugin.getLogger().info("Detected ProtocolLib... initiating additional features.");
            this.protocolLib = new ProtocolLib(plugin);
        }
        if (WolfyUtilities.hasPlugin("PlaceholderAPI")) {
            plugin.getApi().getConsole().info("$msg.startup.placeholder$");
            new PlaceHolder(plugin).register();
        }

    }
}
