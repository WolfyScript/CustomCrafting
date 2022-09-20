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

package me.wolfyscript.customcrafting.recipes;

import com.google.common.collect.Streams;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.main_gui.ClusterMain;
import me.wolfyscript.customcrafting.gui.recipebook.ButtonContainerIngredient;
import me.wolfyscript.customcrafting.recipes.brewing.EffectAddition;
import me.wolfyscript.customcrafting.recipes.brewing.EffectSettingsRequired;
import me.wolfyscript.customcrafting.recipes.brewing.EffectSettingsUpgrade;
import me.wolfyscript.customcrafting.recipes.items.Ingredient;
import me.wolfyscript.customcrafting.recipes.items.Result;
import me.wolfyscript.customcrafting.utils.ItemLoader;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JacksonInject;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonCreator;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonGetter;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonIgnore;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonProperty;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonSetter;
import me.wolfyscript.lib.com.fasterxml.jackson.core.JsonGenerator;
import me.wolfyscript.lib.com.fasterxml.jackson.core.type.TypeReference;
import me.wolfyscript.lib.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.lib.com.fasterxml.jackson.databind.SerializerProvider;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.GuiWindow;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.Pair;
import me.wolfyscript.utilities.util.chat.ChatColor;
import me.wolfyscript.utilities.util.inventory.PotionUtils;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class CustomRecipeBrewing extends CustomRecipe<CustomRecipeBrewing> {

    private static final CustomItem placeHolderPotion = new CustomItem(Material.POTION).setDisplayName(ChatColor.convert("&6&lAny kind of potion!"));

    Ingredient allowedItems; //The CustomItems that can be used. Needs to be a potion of course.
    private Ingredient ingredients; //The top ingredient of the recipe. Always required.
    private int fuelCost; //The fuel cost of recipe
    private int brewTime; //The brew time in ticks

    //These options are for general changes made to the potions, if advanced features are not required, or you want to edit all effects before editing them further in detail.
    private int durationChange; //added to the Duration. if <0 it will be subtracted
    private int amplifierChange; //added to the Amplifier. if <0 it will be subtracted
    private boolean resetEffects; //If true resets all the effects
    private Color effectColor; //Alternative to colorChange

    //These options are more precise, and you can specify the exact effect you want to edit.
    //Instead of all these options you can use a set result.
    private List<PotionEffectType> effectRemovals; //These effects will be removed from the potions
    private List<EffectAddition> effectAdditions; //These effects will be added with an option if they should be replaced if they are already present
    private List<EffectSettingsUpgrade> effectUpgrades; //These effects will be added to the existing potion effects. Meaning that the values of these PotionEffects will add to the existing effects and boolean values will be replaced.

    //Conditions for the Potions inside the 3 slots at the bottom
    private List<EffectSettingsRequired> requiredEffects; //The effects that are required with the current Duration and amplitude. Integer values == 0 will be ignored and any value will be allowed.

    //Indexed values
    @JsonIgnore
    private Map<PotionEffectType, EffectSettingsUpgrade> effectUpgradesByEffectType;
    @JsonIgnore
    private Map<PotionEffectType, EffectSettingsRequired> requiredEffectsByEffectType;

    public CustomRecipeBrewing(NamespacedKey namespacedKey, JsonNode node) {
        super(namespacedKey, node);
        this.ingredients = ItemLoader.loadIngredient(node.path("ingredients"));
        this.result = ItemLoader.loadResult(node.path("results"), this.customCrafting);
        this.fuelCost = node.path("fuel_cost").asInt(1);
        this.brewTime = node.path("brew_time").asInt(80);
        this.allowedItems = ItemLoader.loadIngredient(node.path("allowed_items"));

        setDurationChange(node.path("duration_change").asInt());
        setAmplifierChange(node.path("amplifier_change").asInt());
        setResetEffects(node.path("reset_effects").asBoolean(false));
        setEffectColor(node.has("color") ? mapper.convertValue(node.path("color"), Color.class) : null);

        setEffectRemovals(Streams.stream(node.path("effect_removals").elements()).map(n -> mapper.convertValue(n, PotionEffectType.class)).toList());
        setEffectAdditions(mapper.convertValue(node.path("effect_additions"), new TypeReference<List<EffectAddition>>() {}));
        setEffectUpgrades(mapper.convertValue(node.path("effect_upgrades"), new TypeReference<List<EffectSettingsUpgrade>>() {}));
        setRequiredEffects(mapper.convertValue(node.path("required_effects"), new TypeReference<List<EffectSettingsRequired>>() {}));
    }

    @JsonCreator
    public CustomRecipeBrewing(@JsonProperty("key") @JacksonInject("key") NamespacedKey key, @JacksonInject("customcrafting") CustomCrafting customCrafting) {
        super(key, customCrafting, RecipeType.BREWING_STAND);
        this.ingredients = new Ingredient();
        this.fuelCost = 1;
        this.brewTime = 400;
        this.allowedItems = new Ingredient();

        this.durationChange = 0;
        this.amplifierChange = 0;
        this.resetEffects = false;
        this.effectColor = null;
        this.effectRemovals = new ArrayList<>();
        this.effectAdditions = new ArrayList<>();
        this.effectUpgradesByEffectType = new HashMap<>();
        this.result = new Result();
        this.requiredEffectsByEffectType = new HashMap<>();
    }

    @Deprecated
    public CustomRecipeBrewing(NamespacedKey key) {
        this(key, CustomCrafting.inst());
    }

    private CustomRecipeBrewing(CustomRecipeBrewing customRecipeBrewing) {
        super(customRecipeBrewing);
        this.ingredients = customRecipeBrewing.getIngredient();
        this.fuelCost = customRecipeBrewing.getFuelCost();
        this.brewTime = customRecipeBrewing.getBrewTime();

        this.allowedItems = customRecipeBrewing.getAllowedItems();

        this.durationChange = customRecipeBrewing.getDurationChange();
        this.amplifierChange = customRecipeBrewing.getAmplifierChange();
        this.resetEffects = customRecipeBrewing.isResetEffects();
        this.effectColor = customRecipeBrewing.getEffectColor();
        this.effectRemovals = customRecipeBrewing.getEffectRemovals();
        setEffectAdditions(customRecipeBrewing.getEffectAdditionSettings());
        setEffectUpgrades(customRecipeBrewing.getEffectUpgradeSettings());
        setRequiredEffects(customRecipeBrewing.getRequiredEffectSettings());
    }

    @Override
    public RecipeType<CustomRecipeBrewing> getRecipeType() {
        return RecipeType.BREWING_STAND;
    }

    @Override
    public Ingredient getIngredient(int slot) {
        return slot == 0 ? this.ingredients : this.allowedItems;
    }

    @JsonIgnore
    public Ingredient getIngredient() {
        return getIngredient(0);
    }

    private void setIngredient(int slot, Ingredient ingredient) {
        if (slot == 0) {
            this.ingredients = ingredient;
        } else {
            this.allowedItems = ingredient;
        }
    }

    @JsonIgnore
    public void setIngredient(Ingredient ingredient) {
        setIngredient(0, ingredient);
    }

    @Override
    public void setResult(@NotNull Result result) {
        Objects.requireNonNull(result, "Invalid result! Result must not be null!");
        this.result = result;
    }

    public Ingredient getAllowedItems() {
        return allowedItems;
    }

    public void setAllowedItems(Ingredient allowedItems) {
        this.allowedItems = allowedItems;
    }

    public int getFuelCost() {
        return fuelCost;
    }

    public void setFuelCost(int fuelCost) {
        this.fuelCost = fuelCost;
    }

    public int getBrewTime() {
        return brewTime;
    }

    public void setBrewTime(int brewTime) {
        this.brewTime = brewTime;
    }

    public int getDurationChange() {
        return durationChange;
    }

    public void setDurationChange(int durationChange) {
        this.durationChange = durationChange;
    }

    public int getAmplifierChange() {
        return amplifierChange;
    }

    public void setAmplifierChange(int amplifierChange) {
        this.amplifierChange = amplifierChange;
    }

    public boolean isResetEffects() {
        return resetEffects;
    }

    public void setResetEffects(boolean resetEffects) {
        this.resetEffects = resetEffects;
    }

    public Color getEffectColor() {
        return effectColor;
    }

    public void setEffectColor(Color effectColor) {
        this.effectColor = effectColor;
    }

    public List<PotionEffectType> getEffectRemovals() {
        return effectRemovals;
    }

    public void setEffectRemovals(List<PotionEffectType> effectRemovals) {
        this.effectRemovals = effectRemovals;
    }

    @JsonIgnore
    @Deprecated
    public Map<PotionEffect, Boolean> getEffectAdditions() {
        return effectAdditions.stream().collect(Collectors.toMap(EffectAddition::getEffect, EffectAddition::isReplace));
    }

    @JsonIgnore
    @Deprecated
    public void setEffectAdditions(Map<PotionEffect, Boolean> effectAdditions) {
        this.effectAdditions = effectAdditions.entrySet().stream().map(entry -> new EffectAddition(entry.getKey(), entry.getValue())).collect(Collectors.toList());
    }

    /**
     * Sets the effects that are added to the potions in the brewing stand.
     *
     * @param effectAdditions A list of the effect additions.
     */
    @JsonSetter("effectAdditions")
    public void setEffectAdditions(List<EffectAddition> effectAdditions) {
        this.effectAdditions = effectAdditions;
    }

    @JsonGetter("effectAdditions")
    public List<EffectAddition> getEffectAdditionSettings() {
        return effectAdditions;
    }

    /**
     * @deprecated
     */
    @JsonIgnore
    @Deprecated
    public Map<PotionEffectType, Pair<Integer, Integer>> getEffectUpgrades() {
        return effectUpgradesByEffectType.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> new Pair<>(entry.getValue().getAmplifier(), entry.getValue().getDuration())));
    }

    /**
     * @deprecated The upgrades are using their own object now. This method converts it to the new format. Use {@link #setEffectUpgrades(List)} instead!
     */
    @JsonIgnore
    @Deprecated
    public void setEffectUpgrades(Map<PotionEffectType, Pair<Integer, Integer>> effectUpgrades) {
        this.effectUpgradesByEffectType = new HashMap<>();
        this.effectUpgrades = new ArrayList<>();
        for (var entry : effectUpgrades.entrySet()) {
            var upgrade = new EffectSettingsUpgrade(entry.getKey(), entry.getValue().getKey(), entry.getValue().getValue());
            this.effectUpgrades.add(upgrade);
            this.effectUpgradesByEffectType.put(entry.getKey(), upgrade);
        }
    }

    /**
     * Sets the upgrade settings, that are applied to the existing potion effects.
     *
     * @param effectUpgrades The effect type upgrades.
     */
    @JsonSetter("effectUpgrades")
    public void setEffectUpgrades(List<EffectSettingsUpgrade> effectUpgrades) {
        this.effectUpgrades = Objects.requireNonNullElseGet(effectUpgrades, ArrayList::new);
        this.effectUpgradesByEffectType = this.effectUpgrades.stream().collect(Collectors.toMap(EffectSettingsUpgrade::getEffectType, settings -> settings));
    }

    /**
     * Gets the upgrades that are applied to the potion effect types.
     *
     * @return The effect type upgrades
     */
    @JsonGetter("effectUpgrades")
    public List<EffectSettingsUpgrade> getEffectUpgradeSettings() {
        return effectUpgrades;
    }

    @JsonIgnore
    @Deprecated
    public Map<PotionEffectType, Pair<Integer, Integer>> getRequiredEffects() {
        return requiredEffectsByEffectType.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> new Pair<>(entry.getValue().getAmplifier(), entry.getValue().getDuration())));
    }

    @JsonIgnore
    @Deprecated
    public void setRequiredEffects(Map<PotionEffectType, Pair<Integer, Integer>> requiredEffects) {
        this.requiredEffectsByEffectType = new HashMap<>();
        this.requiredEffects = new ArrayList<>();
        for (var entry : requiredEffects.entrySet()) {
            var required = new EffectSettingsRequired(entry.getKey(), entry.getValue().getKey(), entry.getValue().getValue());
            this.requiredEffects.add(required);
            this.requiredEffectsByEffectType.put(entry.getKey(), required);
        }
    }

    /**
     * Sets the settings for the required effects, that the potions must have.
     *
     * @param requiredEffects A list of the settings for the required effects.
     */
    @JsonSetter("requiredEffects")
    public void setRequiredEffects(List<EffectSettingsRequired> requiredEffects) {
        this.requiredEffects = Objects.requireNonNullElseGet(requiredEffects, ArrayList::new);
        this.requiredEffectsByEffectType = this.requiredEffects.stream().collect(Collectors.toMap(EffectSettingsRequired::getEffectType, settings -> settings));
    }

    /**
     * Gets the settings for the required effects, that the potions must have.
     *
     * @return A list of the settings for the required effects.
     */
    @JsonGetter("requiredEffects")
    public List<EffectSettingsRequired> getRequiredEffectSettings() {
        return requiredEffects;
    }

    @Override
    public CustomRecipeBrewing clone() {
        return new CustomRecipeBrewing(this);
    }

    @Override
    public void writeToJson(JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        super.writeToJson(gen, serializerProvider);

        gen.writeObjectField("ingredients", ingredients);

        gen.writeNumberField("fuel_cost", fuelCost);
        gen.writeNumberField("brew_time", brewTime);

        gen.writeObjectField("allowed_items", allowedItems);

        gen.writeObjectField("results", result);

        //Load options
        gen.writeNumberField("duration_change", durationChange);
        gen.writeNumberField("amplifier_change", amplifierChange);
        gen.writeBooleanField("reset_effects", resetEffects);
        gen.writeObjectField("color", effectColor);

        //Load advanced options
        gen.writeArrayFieldStart("effect_removals");
        for (PotionEffectType effectRemoval : effectRemovals) {
            gen.writeObject(effectRemoval);
        }
        gen.writeEndArray();
        gen.writeObjectField("effect_additions", effectAdditions);
        gen.writeObjectField("effect_upgrades", effectUpgrades);
        gen.writeObjectField("required_effects", requiredEffects);
    }

    @Override
    public boolean findResultItem(ItemStack result) {
        if (this.getResult().isEmpty()) {
            if (getAllowedItems().isEmpty()) {
                return result.getType().equals(Material.POTION);
            }
            return getAllowedItems().getChoices().stream().anyMatch(customItem -> customItem.create().isSimilar(result));
        }
        return super.findResultItem(result);
    }

    @Override
    public List<CustomItem> getRecipeBookItems() {
        if (this.getResult().isEmpty()) {
            if (getAllowedItems().isEmpty()) {
                return Collections.singletonList(placeHolderPotion);
            }
            return getIngredient().getChoices();
        }
        return this.getResult().getChoices();
    }

    @Override
    public void prepareMenu(GuiHandler<CCCache> guiHandler, GuiCluster<CCCache> cluster) {
        ((ButtonContainerIngredient) cluster.getButton(ButtonContainerIngredient.key(3))).setVariants(guiHandler, getIngredient());
        if (!getAllowedItems().isEmpty()) {
            ((ButtonContainerIngredient) cluster.getButton(ButtonContainerIngredient.key(0))).setVariants(guiHandler, getAllowedItems());
        } else {
            ((ButtonContainerIngredient) cluster.getButton(ButtonContainerIngredient.key(0))).setVariants(guiHandler, Collections.singletonList(placeHolderPotion));
        }
        if (!this.getResult().isEmpty()) {
            ((ButtonContainerIngredient) cluster.getButton(ButtonContainerIngredient.key(1))).setVariants(guiHandler, this.getResult());
        } else {
            CustomItem modifications = new CustomItem(Material.POTION).setDisplayName(ChatColor.convert("&6&lResulting Potion"));
            modifications.addLoreLine("");
            if (resetEffects) {
                modifications.addLoreLine("&4All effects will be removed!");
            } else {
                if (durationChange != 0 || amplifierChange != 0) {
                    modifications.addLoreLine(ChatColor.convert("&eOverall Modifications:"));
                    if (durationChange != 0) {
                        modifications.addLoreLine(ChatColor.convert("&7Duration: " + (durationChange > 0 ? "+ " : " ") + durationChange));
                    }
                    if (amplifierChange != 0) {
                        modifications.addLoreLine(ChatColor.convert("&7Amplifier: " + (amplifierChange > 0 ? "+ " : " ") + amplifierChange));
                    }
                    modifications.addLoreLine("");
                }
                if (!effectUpgradesByEffectType.isEmpty()) {
                    modifications.addLoreLine(ChatColor.convert("&eEffect Modifications:"));
                    for (Map.Entry<PotionEffectType, EffectSettingsUpgrade> entry : effectUpgradesByEffectType.entrySet()) {
                        modifications.addLoreLine(PotionUtils.getPotionEffectLore(entry.getValue().getAmplifier(), entry.getValue().getDuration(), entry.getKey()));
                    }
                    modifications.addLoreLine("");
                }
                if (!effectAdditions.isEmpty()) {
                    modifications.addLoreLine(ChatColor.convert("&eEffect Additions:"));
                    for (EffectAddition addition : effectAdditions) {
                        PotionEffect effect = addition.getEffect();
                        modifications.addLoreLine(PotionUtils.getPotionEffectLore(effect.getAmplifier(), effect.getDuration(), effect.getType()));
                    }
                    modifications.addLoreLine("");
                }
                if (!effectRemovals.isEmpty()) {
                    modifications.addLoreLine(ChatColor.convert("&4Removed:"));
                    for (PotionEffectType type : effectRemovals) {
                        modifications.addLoreLine(ChatColor.convert("&9" + PotionUtils.getPotionName(type)));
                    }
                }
            }
            ((ButtonContainerIngredient) cluster.getButton(ButtonContainerIngredient.key(1))).setVariants(guiHandler, Collections.singletonList(modifications));
        }
    }

    @Override
    public void renderMenu(GuiWindow<CCCache> guiWindow, GuiUpdate<CCCache> event) {
        var cluster = guiWindow.getCluster();
        event.setButton(12, ButtonContainerIngredient.key(cluster, 3));
        event.setButton(20, new NamespacedKey(cluster.getId(), "brewing.icon"));
        event.setButton(21, ClusterMain.GLASS_GREEN);

        event.setButton(30, ButtonContainerIngredient.key(cluster, 0));
        event.setButton(31, ClusterMain.GLASS_GREEN);
        event.setButton(32, ClusterMain.GLASS_GREEN);
        event.setButton(33, ButtonContainerIngredient.key(cluster, 1));
    }

}
