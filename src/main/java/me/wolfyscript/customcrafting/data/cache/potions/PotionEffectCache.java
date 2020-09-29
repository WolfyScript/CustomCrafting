package me.wolfyscript.customcrafting.data.cache.potions;

import org.bukkit.potion.PotionEffectType;

public class PotionEffectCache {

    boolean recipePotionEffect;

    private int amplifier;
    private int duration;
    private PotionEffectType type;
    private boolean ambient;
    private boolean particles;
    private boolean icon;

    public PotionEffectCache() {
        this.amplifier = 0;
        this.duration = 0;
        this.type = null;
        this.ambient = false;
        this.particles = true;
        this.icon = true;
        this.recipePotionEffect = false;
    }

    public boolean isRecipePotionEffect() {
        return recipePotionEffect;
    }

    public void setRecipePotionEffect(boolean recipePotionEffect) {
        this.recipePotionEffect = recipePotionEffect;
    }

    public int getAmplifier() {
        return amplifier;
    }

    public void setAmplifier(int amplifier) {
        this.amplifier = amplifier;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public PotionEffectType getType() {
        return type;
    }

    public void setType(PotionEffectType type) {
        this.type = type;
    }

    public boolean isAmbient() {
        return ambient;
    }

    public void setAmbient(boolean ambient) {
        this.ambient = ambient;
    }

    public boolean isParticles() {
        return particles;
    }

    public void setParticles(boolean particles) {
        this.particles = particles;
    }

    public boolean isIcon() {
        return icon;
    }

    public void setIcon(boolean icon) {
        this.icon = icon;
    }
}
