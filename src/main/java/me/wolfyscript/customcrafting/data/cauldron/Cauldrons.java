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

package me.wolfyscript.customcrafting.data.cauldron;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.listeners.customevents.CauldronCookEvent;
import me.wolfyscript.customcrafting.recipes.CustomRecipeCauldron;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.util.version.MinecraftVersions;
import me.wolfyscript.utilities.util.version.ServerVersion;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Levelled;
import org.bukkit.block.data.type.Campfire;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.util.Vector;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Cauldrons {

    private final CustomCrafting customCrafting;
    private final WolfyUtilities api;
    //Hashmap of all the locations of the valid cauldrons. The Key is the Location. The Value is the current active recipe, which is going to be saved on server shutdown.
    private final Map<Location, List<Cauldron>> cauldrons = new HashMap<>();

    public Cauldrons(CustomCrafting customCrafting) {
        this.customCrafting = customCrafting;
        this.api = customCrafting.getApi();
        load();
    }

    public void addCauldron(Location location) {
        synchronized (cauldrons) {
            cauldrons.computeIfAbsent(location, l -> new ArrayList<>());
        }
    }

    public void removeCauldron(Location location) {
        synchronized (cauldrons) {
            cauldrons.remove(location);
        }
    }

    public synchronized Map<Location, List<Cauldron>> getCauldrons() {
        return cauldrons;
    }

    public synchronized boolean isCauldron(Location location) {
        return cauldrons.containsKey(location);
    }

    public static boolean isCauldron(Material type) {
        return type.equals(Material.CAULDRON) || (ServerVersion.isAfterOrEq(MinecraftVersions.v1_17) && (type.equals(Material.WATER_CAULDRON) || type.equals(Material.LAVA_CAULDRON)));
    }

    public static int getLevel(Block block) {
        return block.getBlockData() instanceof Levelled levelled ? levelled.getLevel() : block.getType().equals(Material.LAVA_CAULDRON) ? 3 : 0;
    }


    private Location stringToLocation(String loc) {
        String[] args = loc.split(";");
        try {
            var uuid = UUID.fromString(args[0]);
            var world = Bukkit.getWorld(uuid);
            if (world != null) {
                return new Location(world, Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]));
            }
        } catch (IllegalArgumentException e) {
            api.getConsole().warn("Couldn't find world " + args[0]);
        }
        return null;
    }

    public void load() {
        api.getConsole().info("Loading Cauldrons");
        var file = new File(customCrafting.getDataFolder() + File.separator + "cauldrons.dat");
        if (file.exists()) {
            try (var fis = new FileInputStream(file); var ois = new BukkitObjectInputStream(fis)) {
                var object = ois.readObject();
                this.cauldrons.clear();
                Map<String, List<String>> loadMap = (Map<String, List<String>>) object;
                for (Map.Entry<String, List<String>> entry : loadMap.entrySet()) {
                    var location = stringToLocation(entry.getKey());
                    if (location != null) {
                        this.cauldrons.put(location, entry.getValue() == null ? new ArrayList<>() : entry.getValue().stream().map(Cauldron::fromString).filter(Objects::nonNull).collect(Collectors.toList()));
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                api.getConsole().warn("Couldn't load cauldrons. No data found");
            }
        }
    }
}
