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

package me.wolfyscript.customcrafting.data.cache;

import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.Pair;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class BrewingGUICache {

    private String option;
    private NamespacedKey parsedOptionKey;

    int page;

    private PotionEffect potionEffectAddition;
    private boolean replacePotionEffectAddition;

    private PotionEffectType upgradePotionEffectType;
    private Pair<Integer, Integer> upgradeValues;

    public BrewingGUICache() {
        this.page = 0;
        this.option = "";
        this.parsedOptionKey = null;
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

    public NamespacedKey getParsedOptionKey() {
        return parsedOptionKey;
    }

    public void setParsedOptionKey(NamespacedKey parsedOptionKey) {
        this.parsedOptionKey = parsedOptionKey;
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
