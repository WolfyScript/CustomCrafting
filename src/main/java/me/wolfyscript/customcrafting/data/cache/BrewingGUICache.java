package me.wolfyscript.customcrafting.data.cache;

import me.wolfyscript.utilities.api.utils.Pair;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class BrewingGUICache {

    private String option;

    int page;

    private PotionEffect potionEffectAddition;
    private boolean replacePotionEffectAddition;

    private PotionEffectType upgradePotionEffectType;
    private Pair<Integer, Integer> upgradeValues;

    public BrewingGUICache() {
        this.page = 0;
        this.option = "";

        this.potionEffectAddition = null;
        this.replacePotionEffectAddition = false;

        this.upgradePotionEffectType = null;
        this.upgradeValues = new Pair<>(0, 0);
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public boolean isReplacePotionEffectAddition() {
        return replacePotionEffectAddition;
    }

    public void setReplacePotionEffectAddition(boolean replacePotionEffectAddition) {
        this.replacePotionEffectAddition = replacePotionEffectAddition;
    }

    public PotionEffect getPotionEffectAddition() {
        return potionEffectAddition;
    }

    public void setPotionEffectAddition(PotionEffect potionEffectAddition) {
        this.potionEffectAddition = potionEffectAddition;
    }

    public Pair<Integer, Integer> getUpgradeValues() {
        return upgradeValues;
    }

    public void setUpgradeValues(Pair<Integer, Integer> upgradeValues) {
        this.upgradeValues = upgradeValues;
    }

    public PotionEffectType getUpgradePotionEffectType() {
        return upgradePotionEffectType;
    }

    public void setUpgradePotionEffectType(PotionEffectType upgradePotionEffectType) {
        this.upgradePotionEffectType = upgradePotionEffectType;
    }
}
