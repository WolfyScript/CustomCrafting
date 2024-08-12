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

package me.wolfyscript.customcrafting.data.patreon;

import com.google.gson.JsonParser;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.utilities.util.RandomCollection;
import me.wolfyscript.utilities.util.inventory.PlayerHeadUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Collections;

public class Patron {

    private static final RandomCollection<ItemStack> HEADS = new RandomCollection<>();

    static {
        HEADS.add(1, PlayerHeadUtils.getViaURL("adc6429cfabacf211dd3db26c5ca7b5942dd82599fbb1d537cf72e4952e2c7b"));
        HEADS.add(2, PlayerHeadUtils.getViaURL("deb41e309d82952f5f4a908bf168e848c8176e5f0487e576bc3219ed0644e6e7"));
        HEADS.add(1, PlayerHeadUtils.getViaURL("15481d610ec06a543840f76b8cc9dc51fd80200a5fa894fdf4e9e301904af48e"));
        HEADS.add(1, PlayerHeadUtils.getViaURL("d359537c15534f61c1cd886bc118774ed22280e7cdab6613870160aad4ca39"));
        HEADS.add(1, PlayerHeadUtils.getViaURL("3d9d2c75785f1238987bb11a442972a86daec9662ac56bbfea2d83db962e1ac3"));
    }

    private final String name;
    private ItemStack head;

    public Patron(String name, String uuid, @NotNull ItemStack head, Patreon.Tier tier) {
        this.head = head;
        //https://sessionserver.mojang.com/session/minecraft/profile/db61eab07fb148db986f125e73787976?unsigned=false
        Bukkit.getScheduler().runTaskAsynchronously(CustomCrafting.inst(), () -> {
            try {
                if (!uuid.isEmpty()) {
                    var url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.replace("-", "") + "?unsigned=false");
                    var reader = new InputStreamReader(url.openStream());
                    var jsonObject = new JsonParser().parse(reader).getAsJsonObject();
                    var minecraftName = jsonObject.get("name").getAsString();
                    var properties = jsonObject.get("properties").getAsJsonArray().get(0).getAsJsonObject();
                    this.head = PlayerHeadUtils.getViaValue(properties.get("value").getAsString());
                    ItemMeta meta = this.head.getItemMeta();
                    if (!minecraftName.isEmpty()) {
                        meta.setLore(Collections.singletonList("§7aka. " + minecraftName));
                    }
                    this.head.setItemMeta(meta);
                }
            } catch (IOException e) {
                Bukkit.getLogger().info("Could not get skin data from session servers!");
            }
            ItemMeta meta = this.head.getItemMeta();
            meta.setDisplayName(tier.getColor() + "§l" + name);
            this.head.setItemMeta(meta);
        });
        this.name = name;
    }

    public Patron(String name, String uuid, Patreon.Tier tier) {
        this(name, uuid, new ItemStack(Material.PLAYER_HEAD), tier);
    }

    public Patron(String name, Patreon.Tier tier) {
        this(name, "", HEADS.next().clone(), tier);
    }

    public Patron(String name) {
        this(name, "", HEADS.next().clone(), Patreon.Tier.WOLFRAM);
    }

    public ItemStack getHead() {
        return head;
    }

    public String getName() {
        return name;
    }
}
