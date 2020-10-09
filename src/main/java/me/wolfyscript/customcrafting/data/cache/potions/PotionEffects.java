package me.wolfyscript.customcrafting.data.cache.potions;

import me.wolfyscript.customcrafting.data.TestCache;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PotionEffects {

    boolean recipePotionEffect;
    private String openedFromWindow;
    private String openedFromCluster;

    private ApplyPotionEffect applyPotionEffect;
    private ApplyPotionEffectType applyPotionEffectType;

    private int amplifier;
    private int duration;
    private PotionEffectType type;
    private boolean ambient;
    private boolean particles;
    private boolean icon;

    public PotionEffects() {
        this.amplifier = 0;
        this.duration = 0;
        this.type = null;
        this.ambient = false;
        this.particles = true;
        this.icon = true;
        this.recipePotionEffect = false;
        this.openedFromCluster = "";
        this.openedFromWindow = "";
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

    public ApplyPotionEffect getApplyPotionEffect() {
        return applyPotionEffect;
    }

    public void setApplyPotionEffect(ApplyPotionEffect applyPotionEffect) {
        this.applyPotionEffect = applyPotionEffect;
    }

    public void applyPotionEffect(TestCache cache) {
        if (getType() != null) {
            PotionEffect potionEffect = new PotionEffect(getType(), getDuration(), getAmplifier(), isAmbient(), isParticles(), isIcon());
            if (applyPotionEffect != null) {
                applyPotionEffect.applyPotionEffect(this, cache, potionEffect);
                applyPotionEffect = null;
            }
        }
    }

    public ApplyPotionEffectType getApplyPotionEffectType() {
        return applyPotionEffectType;
    }

    public void setApplyPotionEffectType(ApplyPotionEffectType applyPotionEffectType) {
        this.applyPotionEffectType = applyPotionEffectType;
    }

    public String getOpenedFromWindow() {
        return openedFromWindow;
    }

    public void setOpenedFromWindow(String openedFromWindow) {
        this.openedFromWindow = openedFromWindow;
    }

    public String getOpenedFromCluster() {
        return openedFromCluster;
    }

    public void setOpenedFromCluster(String openedFromCluster) {
        this.openedFromCluster = openedFromCluster;
    }

    public void setOpenedFrom(String openedFromCluster, String openedFromWindow) {
        this.openedFromCluster = openedFromCluster;
        this.openedFromWindow = openedFromWindow;
    }
}
