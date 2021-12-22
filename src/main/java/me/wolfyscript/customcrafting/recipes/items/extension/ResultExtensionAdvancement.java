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

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import me.wolfyscript.customcrafting.CustomCrafting;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class ResultExtensionAdvancement extends ResultExtension {

    private NamespacedKey advancement;
    private boolean revoke = false;
    private String criteria = null;
    private boolean nearPlayer = false;
    private boolean nearWorkstation = false;

    public ResultExtensionAdvancement() {
        super(new me.wolfyscript.utilities.util.NamespacedKey(CustomCrafting.inst(), "advancement"));
    }

    public ResultExtensionAdvancement(ResultExtensionAdvancement extension) {
        super(extension);
        this.advancement = extension.advancement;
        this.revoke = extension.revoke;
        this.criteria = extension.criteria;
        this.nearPlayer = extension.nearPlayer;
        this.nearWorkstation = extension.nearWorkstation;
    }

    public ResultExtensionAdvancement(org.bukkit.NamespacedKey advancement, boolean revoke, @Nullable String criteria, boolean nearPlayer, boolean nearWorkstation) {
        this();
        this.advancement = Objects.requireNonNull(advancement, "Invalid Extension! Key of Advancement cannot be null!");
        this.revoke = revoke;
        this.criteria = criteria;
        this.nearPlayer = nearPlayer;
        this.nearWorkstation = nearWorkstation;
    }

    @Override
    public void onWorkstation(Block block, @Nullable Player player) {
    }

    @Override
    public void onLocation(Location location, @Nullable Player player) {
        if ((player != null && nearPlayer) || nearWorkstation) {
            this.getEntitiesInRange(Player.class, location, getOuterRadius(), getInnerRadius()).forEach(this::applyAdvancementChanges);
        }
    }

    @Override
    public void onPlayer(@NotNull Player player, Location location) {
        applyAdvancementChanges(player);
    }

    @Override
    public ResultExtensionAdvancement clone() {
        return new ResultExtensionAdvancement(this);
    }

    @JsonGetter
    private String getAdvancement() {
        return this.advancement.toString();
    }

    @JsonSetter
    private void setAdvancement(String advancement) {
        this.advancement = Objects.requireNonNull(org.bukkit.NamespacedKey.fromString(advancement), "Invalid Extension! Key of Advancement cannot be null!");
    }

    protected void applyAdvancementChanges(Player player) {
        Advancement advancementObj = Bukkit.getAdvancement(advancement);
        if (advancementObj != null) {
            AdvancementProgress progress = player.getAdvancementProgress(advancementObj);
            if (criteria != null && !criteria.isEmpty()) {
                if (revoke) {
                    progress.revokeCriteria(criteria);
                } else {
                    progress.awardCriteria(criteria);
                }
            } else {
                if (revoke) {
                    progress.getAwardedCriteria().forEach(progress::revokeCriteria);
                } else {
                    progress.getRemainingCriteria().forEach(progress::awardCriteria);
                }
            }
        }
    }

    public enum Method {

        EVERYTHING, FROM, ONLY, UNTIL, THROUGH

    }


}
