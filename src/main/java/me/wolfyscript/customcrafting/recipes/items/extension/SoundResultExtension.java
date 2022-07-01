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
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SoundResultExtension extends ResultExtension {

    private Sound sound;
    private float volume = 1.0F;
    private float pitch = 1.0F;
    private SoundCategory soundCategory = SoundCategory.BLOCKS;
    private boolean forPlayer = false;
    private boolean nearPlayer = false;
    private boolean nearWorkstation = false;
    private boolean onBlock = true;

    public SoundResultExtension() {
        super(new NamespacedKey(CustomCrafting.inst(), "sound"));
    }

    public SoundResultExtension(SoundResultExtension extension) {
        super(extension);
        this.sound = extension.sound;
        this.volume = extension.volume;
        this.pitch = extension.pitch;
        this.soundCategory = extension.soundCategory;
        this.forPlayer = extension.forPlayer;
        this.nearPlayer = extension.nearPlayer;
        this.nearWorkstation = extension.nearWorkstation;
        this.onBlock = extension.onBlock;
    }

    public SoundResultExtension(Sound sound) {
        this();
        this.sound = sound;
    }

    public SoundResultExtension(Sound sound, float volume, float pitch, SoundCategory soundCategory) {
        this();
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
        this.soundCategory = soundCategory;
    }

    public SoundResultExtension(Sound sound, float volume, float pitch, SoundCategory soundCategory, boolean forPlayer, boolean nearPlayer, boolean nearWorkstation, boolean onBlock) {
        this();
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
        this.soundCategory = soundCategory;
        this.forPlayer = forPlayer;
        this.nearPlayer = nearPlayer;
        this.nearWorkstation = nearWorkstation;
        this.onBlock = onBlock;
    }

    @Override
    public void onWorkstation(Block block, @Nullable Player player) {
        if (onBlock && sound != null) {
            block.getWorld().playSound(block.getLocation(), sound, soundCategory, volume, pitch);
        }
    }

    @Override
    public void onLocation(Location location, @Nullable Player player) {
        if (sound != null && ((player != null && nearPlayer) || nearWorkstation)) {
            getEntitiesInRange(Player.class, location, getOuterRadius(), getInnerRadius()).forEach(player1 -> player1.playSound(location, sound, soundCategory, volume, pitch));
        }
    }

    @Override
    public void onPlayer(@NotNull Player player, Location location) {
        if (forPlayer && sound != null) {
            player.playSound(location, sound, soundCategory, volume, pitch);
        }
    }

    @Override
    public ResultExtension clone() {
        return new SoundResultExtension(this);
    }

    public Sound getSound() {
        return sound;
    }

    public void setSound(Sound sound) {
        this.sound = sound;
    }

    public float getVolume() {
        return volume;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public SoundCategory getSoundCategory() {
        return soundCategory;
    }

    public void setSoundCategory(SoundCategory soundCategory) {
        this.soundCategory = soundCategory;
    }

    public boolean isForPlayer() {
        return forPlayer;
    }

    public void setForPlayer(boolean forPlayer) {
        this.forPlayer = forPlayer;
    }

    public boolean isNearPlayer() {
        return nearPlayer;
    }

    public void setNearPlayer(boolean nearPlayer) {
        this.nearPlayer = nearPlayer;
    }

    public boolean isNearWorkstation() {
        return nearWorkstation;
    }

    public void setNearWorkstation(boolean nearWorkstation) {
        this.nearWorkstation = nearWorkstation;
    }

    public boolean isOnBlock() {
        return onBlock;
    }

    public void setOnBlock(boolean onBlock) {
        this.onBlock = onBlock;
    }
}
