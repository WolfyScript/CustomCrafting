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

package me.wolfyscript.customcrafting.utils;

import com.wolfyscript.jackson.dataformat.hocon.HoconMapper;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.lib.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.lib.com.fasterxml.jackson.databind.ObjectMapper;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.chat.ClickData;
import me.wolfyscript.utilities.util.json.jackson.JacksonUtil;
import me.wolfyscript.utilities.util.version.WUVersion;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateChecker {

    private static final long CHECK_DELAY = 1000L * 60L * 20L;
    private static final String REQUEST_URL = "https://api.spigotmc.org/simple/0.2/index.php?action=getResource&id=";
    private static final String RESOURCE_URL = "https://www.spigotmc.org/resources/";
    private final CustomCrafting plugin;
    private final WolfyUtilities api;
    private final int id;
    private boolean outdated;
    private long lastCheck;
    private WUVersion version;
    private final ObjectMapper objectMapper;

    public UpdateChecker(CustomCrafting plugin, int id) {
        this.outdated = false;
        this.id = id;
        this.plugin = plugin;
        this.api = plugin.getApi();
        this.objectMapper = new HoconMapper();
    }

    protected void check(@Nullable Player player) {
        try {
            HttpURLConnection con = (HttpURLConnection) new URL(REQUEST_URL + id).openConnection();
            con.setReadTimeout(2000);
            JsonNode node = objectMapper.readTree(new BufferedReader(new InputStreamReader(con.getInputStream())));
            version = WUVersion.parse(node.path("current_version").asText(plugin.getVersion().getVersion()));
            outdated = version.isAfter(plugin.getVersion());
            lastCheck = System.currentTimeMillis();
            if (outdated) {
                api.getConsole().warn("$msg.startup.outdated$");
                sendOutdatedMsg(player);
            }
        } catch (Exception ex) {
            api.getConsole().warn("$msg.startup.update_check_fail$");
        }
    }

    public void run(@Nullable Player player) {
        if (outdated) {
            sendOutdatedMsg(player);
        } else if (System.currentTimeMillis() - lastCheck > CHECK_DELAY) {
            new Thread(() -> check(player), "CC-update-check").start();
        }
    }

    protected void sendOutdatedMsg(@Nullable Player player) {
        if (player != null) {
            api.getChat().sendMessage(player, "$msg.player.outdated.msg$");
            api.getChat().sendActionMessage(player,
                    new ClickData("$msg.player.outdated.msg2$", null),
                    new ClickData("$msg.player.outdated.link$", null,
                            new me.wolfyscript.utilities.api.chat.ClickEvent(ClickEvent.Action.OPEN_URL, RESOURCE_URL + id)));
        }
    }

    public boolean isOutdated() {
        return outdated;
    }

    @Nullable
    public WUVersion getVersion() {
        return version;
    }
}
