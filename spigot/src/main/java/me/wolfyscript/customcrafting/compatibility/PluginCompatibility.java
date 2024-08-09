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
import me.wolfyscript.utilities.util.version.WUVersion;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class PluginCompatibility {

    private final CustomCrafting plugin;

    private ProtocolLib protocolLib = null;

    public PluginCompatibility(CustomCrafting plugin) {
        this.plugin = plugin;
    }

    public void init() {
        Plugin protocolLibPlugin = Bukkit.getPluginManager().getPlugin("ProtocolLib");
        if (protocolLibPlugin != null) {
            String verString = protocolLibPlugin.getDescription().getVersion();
            WUVersion version = WUVersion.parse(verString);
            if (version.getMajor() <= 4) {
                plugin.getLogger().severe("");
                plugin.getLogger().severe("[!] ------------------- [Attention!] ------------------- [!]");
                plugin.getLogger().severe("Running Incompatible ProtocolLib version!");
                plugin.getLogger().severe("Please update to the latest version of ProtocolLib!");
                plugin.getLogger().severe("https://ci.dmulloy2.net/job/ProtocolLib/lastSuccessfulBuild/");
                plugin.getLogger().severe("You are running " + verString + " !");
                plugin.getLogger().severe("Minimum requirement is at least 5.0.0-SNAPSHOT !");
                plugin.getLogger().severe("[!] ------------------- [Attention!] ------------------- [!]");
                plugin.getLogger().severe("");
                return;
            }
            plugin.getLogger().info("Detected ProtocolLib... initiating additional features.");
            this.protocolLib = new ProtocolLib(plugin);
        }
        if (WolfyUtilities.hasPlugin("PlaceholderAPI")) {
            plugin.getApi().getConsole().info("$msg.startup.placeholder$");
            new PlaceHolder(plugin).register();
        }

    }
}
