package me.wolfyscript.customcrafting.utils.recipe_item.extension;

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
    private boolean playForPlayer = false;
    private boolean playForNearPlayer = false;
    private boolean playForNearWorkstation = false;
    private boolean playOnBlock = true;

    public SoundResultExtension() {
        super(new NamespacedKey("customcrafting", "sound"));
    }

    public SoundResultExtension(SoundResultExtension extension) {
        super(extension);
        this.sound = extension.sound;
        this.volume = extension.volume;
        this.pitch = extension.pitch;
        this.soundCategory = extension.soundCategory;
        this.playForPlayer = extension.playForPlayer;
        this.playForNearPlayer = extension.playForNearPlayer;
        this.playForNearWorkstation = extension.playForNearWorkstation;
        this.playOnBlock = extension.playOnBlock;
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

    public SoundResultExtension(Sound sound, float volume, float pitch, SoundCategory soundCategory, boolean playForPlayer, boolean playForNearPlayer, boolean playForNearWorkstation, boolean playOnBlock) {
        this();
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
        this.soundCategory = soundCategory;
        this.playForPlayer = playForPlayer;
        this.playForNearPlayer = playForNearPlayer;
        this.playForNearWorkstation = playForNearWorkstation;
        this.playOnBlock = playOnBlock;
    }

    @Override
    public void onWorkstation(Block block, @Nullable Player player) {
        if (playOnBlock && sound != null) {
            block.getWorld().playSound(block.getLocation(), sound, soundCategory, volume, pitch);
        }
    }

    @Override
    public void onLocation(Location location, @Nullable Player player) {
        if (sound != null && ((player != null && playForNearPlayer) || playForNearWorkstation)) {
            getEntitiesInRange(Player.class, location, outerRadius, innerRadius).forEach(player1 -> player1.playSound(location, sound, soundCategory, volume, pitch));
        }
    }

    @Override
    public void onPlayer(@NotNull Player player, Location location) {
        if (playForPlayer && sound != null) {
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

    public boolean isPlayForPlayer() {
        return playForPlayer;
    }

    public void setPlayForPlayer(boolean playForPlayer) {
        this.playForPlayer = playForPlayer;
    }

    public boolean isPlayForNearPlayer() {
        return playForNearPlayer;
    }

    public void setPlayForNearPlayer(boolean playForNearPlayer) {
        this.playForNearPlayer = playForNearPlayer;
    }

    public boolean isPlayForNearWorkstation() {
        return playForNearWorkstation;
    }

    public void setPlayForNearWorkstation(boolean playForNearWorkstation) {
        this.playForNearWorkstation = playForNearWorkstation;
    }

    public boolean isPlayOnBlock() {
        return playOnBlock;
    }

    public void setPlayOnBlock(boolean playOnBlock) {
        this.playOnBlock = playOnBlock;
    }
}
