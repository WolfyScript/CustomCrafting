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

package me.wolfyscript.customcrafting.recipes.items.extension;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.utilities.api.WolfyUtilCore;
import me.wolfyscript.utilities.compatibility.plugins.MythicMobsIntegration;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ThreadLocalRandom;

public class MythicMobResultExtension extends ResultExtension {

    private String mobName;
    private int mobLevel;
    private Vector offset = new Vector(0.5, 1, 0.5);

    public MythicMobResultExtension() {
        super(new NamespacedKey(CustomCrafting.inst(), "mythicmobs/mob_spawn"));
    }

    public MythicMobResultExtension(MythicMobResultExtension extension) {
        super(extension);
        this.mobName = extension.mobName;
        this.mobLevel = extension.mobLevel;
        this.offset = extension.offset;
    }

    public MythicMobResultExtension(String mobName, int mobLevel) {
        this();
        this.mobName = mobName;
        this.mobLevel = mobLevel;
    }

    public MythicMobResultExtension(String mobName, int mobLevel, Vector offset) {
        this();
        this.mobName = mobName;
        this.mobLevel = mobLevel;
        this.offset = offset;
    }

    @Override
    public void onWorkstation(Block block, @Nullable Player player) {
        spawnMob(block.getLocation());
    }

    @Override
    public void onLocation(Location location, @Nullable Player player) {

    }

    @Override
    public void onPlayer(@NotNull Player player, Location location) {

    }

    @Override
    public MythicMobResultExtension clone() {
        return new MythicMobResultExtension(this);
    }

    protected void spawnMob(Location origin) {
        WolfyUtilCore.getInstance().getCompatibilityManager().getPlugins().runIfAvailable("MythicMobs", MythicMobsIntegration.class, integration -> {
            ThreadLocalRandom random = ThreadLocalRandom.current();
            Vector innerRange = getInnerRadius();
            Vector outerRange = getOuterRadius();
            double x = (random.nextBoolean() ? 1 : -1) * random.nextDouble(innerRange.getX(), outerRange.getX());
            double y = (random.nextBoolean() ? 1 : -1) * random.nextDouble(innerRange.getY(), outerRange.getY());
            double z = (random.nextBoolean() ? 1 : -1) * random.nextDouble(innerRange.getZ(), outerRange.getZ());
            origin.add(x, y, z);
            origin.add(offset);
            integration.spawnMob(mobName, origin, mobLevel);
        });
    }
}
