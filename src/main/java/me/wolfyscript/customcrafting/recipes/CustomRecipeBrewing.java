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

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.google.common.collect.Streams;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.main_gui.ClusterMain;
import me.wolfyscript.customcrafting.gui.recipebook.ButtonContainerIngredient;
import me.wolfyscript.customcrafting.recipes.items.Ingredient;
import me.wolfyscript.customcrafting.recipes.items.Result;
import me.wolfyscript.customcrafting.utils.ItemLoader;
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
import java.util.*;

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
    private List<PotionEffectType> effectRemovals; //These effects will be removed from the potions
    private Map<PotionEffect, Boolean> effectAdditions; //These effects will be added with an option if they should be replaced if they are already present
    private Map<PotionEffectType, Pair<Integer, Integer>> effectUpgrades; //These effects will be added to the existing potion effects. Meaning that the values of these PotionEffects will add to the existing effects and boolean values will be replaced.
    //Instead of all these options you can use a set result.

    //Conditions for the Potions inside the 3 slots at the bottom
    private Map<PotionEffectType, Pair<Integer, Integer>> requiredEffects; //The effects that are required with the current Duration and amplitude. Integer values == 0 will be ignored and any value will be allowed.

    public CustomRecipeBrewing(NamespacedKey namespacedKey, JsonNode node) {
        super(namespacedKey, node);
        this.ingredients = ItemLoader.loadIngredient(node.path("ingredients"));
        this.result = ItemLoader.loadResult(node.path("results"));
        this.fuelCost = node.path("fuel_cost").asInt(1);
        this.brewTime = node.path("brew_time").asInt(80);
        this.allowedItems = ItemLoader.loadIngredient(node.path("allowed_items"));

        setDurationChange(node.path("duration_change").asInt());
        setAmplifierChange(node.path("amplifier_change").asInt());
        setResetEffects(node.path("reset_effects").asBoolean(false));
        setEffectColor(node.has("color") ? mapper.convertValue(node.path("color"), Color.class) : null);

        setEffectRemovals(Streams.stream(node.path("effect_removals").elements()).map(n -> mapper.convertValue(n, PotionEffectType.class)).toList());
        Map<PotionEffect, Boolean> effectAdditions = new HashMap<>();
        node.path("effect_additions").elements().forEachRemaining(n -> {
            var potionEffect = mapper.convertValue(n.path("effect"), PotionEffect.class);
            if (potionEffect != null) {
                effectAdditions.put(potionEffect, n.path("replace").asBoolean());
            }
        });
        setEffectAdditions(effectAdditions);

        Map<PotionEffectType, Pair<Integer, Integer>> effectUpgrades = new HashMap<>();
        node.path("effect_upgrades").elements().forEachRemaining(n -> {
            PotionEffectType potionEffect = mapper.convertValue(n.path("effect_type"), PotionEffectType.class);
            if (potionEffect != null) {
                effectUpgrades.put(potionEffect, new Pair<>(n.get("amplifier").asInt(), n.path("duration").asInt()));
            }
        });
        setEffectUpgrades(effectUpgrades);

        Map<PotionEffectType, Pair<Integer, Integer>> requiredEffects = new HashMap<>();
        node.path("required_effects").elements().forEachRemaining(n -> {
            PotionEffectType potionEffect = mapper.convertValue(n.path("type"), PotionEffectType.class);
            if (potionEffect != null) {
                requiredEffects.put(potionEffect, new Pair<>(n.get("amplifier").asInt(), n.path("duration").asInt()));
            }
        });
        setRequiredEffects(requiredEffects);
    }

    public CustomRecipeBrewing(NamespacedKey key) {
        super(key);
        this.ingredients = new Ingredient();
        this.fuelCost = 1;
        this.brewTime = 400;
        this.allowedItems = new Ingredient();

        this.durationChange = 0;
        this.amplifierChange = 0;
        this.resetEffects = false;
        this.effectColor = null;
        this.effectRemovals = new ArrayList<>();
        this.effectAdditions = new HashMap<>();
        this.effectUpgrades = new HashMap<>();
        this.result = new Result();
        this.requiredEffects = new HashMap<>();
    }

    public CustomRecipeBrewing(CustomRecipeBrewing customRecipeBrewing) {
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
        this.effectAdditions = customRecipeBrewing.getEffectAdditions();
        this.effectUpgrades = customRecipeBrewing.getEffectUpgrades();
        this.requiredEffects = customRecipeBrewing.getRequiredEffects();
    }

    @Override
    public RecipeType<CustomRecipeBrewing> getRecipeType() {
        return RecipeType.BREWING_STAND;
    }

    @Override
    public Ingredient getIngredient(int slot) {
        return slot == 0 ? this.ingredients : this.allowedItems;
    }

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

    public Map<PotionEffect, Boolean> getEffectAdditions() {
        return effectAdditions;
    }

    public void setEffectAdditions(Map<PotionEffect, Boolean> effectAdditions) {
        this.effectAdditions = effectAdditions;
    }

    public Map<PotionEffectType, Pair<Integer, Integer>> getEffectUpgrades() {
        return effectUpgrades;
    }

    public void setEffectUpgrades(Map<PotionEffectType, Pair<Integer, Integer>> effectUpgrades) {
        this.effectUpgrades = effectUpgrades;
    }

    public Map<PotionEffectType, Pair<Integer, Integer>> getRequiredEffects() {
        return requiredEffects;
    }

    public void setRequiredEffects(Map<PotionEffectType, Pair<Integer, Integer>> requiredEffects) {
        this.requiredEffects = requiredEffects;
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
        gen.writeArrayFieldStart("effect_additions");
        for (Map.Entry<PotionEffect, Boolean> entry : effectAdditions.entrySet()) {
            if (entry.getKey() != null) {
                gen.writeStartObject();
                gen.writeObjectField("effect", entry.getKey());
                gen.writeBooleanField("replace", entry.getValue());
                gen.writeEndObject();
            }
        }
        gen.writeEndArray();
        gen.writeArrayFieldStart("effect_upgrades");
        for (Map.Entry<PotionEffectType, Pair<Integer, Integer>> entry : effectUpgrades.entrySet()) {
            if (entry.getKey() != null) {
                gen.writeStartObject();
                gen.writeObjectField("effect_type", entry.getKey());
                gen.writeNumberField("amplifier", entry.getValue().getKey());
                gen.writeNumberField("duration", entry.getValue().getValue());
                gen.writeEndObject();
            }
        }
        gen.writeEndArray();

        //Load input condition options
        gen.writeArrayFieldStart("required_effects");
        for (Map.Entry<PotionEffectType, Pair<Integer, Integer>> entry : requiredEffects.entrySet()) {
            if (entry.getKey() != null) {
                gen.writeStartObject();
                gen.writeObjectField("effect_type", entry.getKey());
                gen.writeNumberField("amplifier", entry.getValue().getKey());
                gen.writeNumberField("duration", entry.getValue().getValue());
                gen.writeEndObject();
            }
        }
        gen.writeEndArray();
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
                if (!effectUpgrades.isEmpty()) {
                    modifications.addLoreLine(ChatColor.convert("&eEffect Modifications:"));
                    for (Map.Entry<PotionEffectType, Pair<Integer, Integer>> entry : effectUpgrades.entrySet()) {
                        modifications.addLoreLine(PotionUtils.getPotionEffectLore(entry.getValue().getKey(), entry.getValue().getValue(), entry.getKey()));
                    }
                    modifications.addLoreLine("");
                }
                if (!effectAdditions.isEmpty()) {
                    modifications.addLoreLine(ChatColor.convert("&eEffect Additions:"));
                    for (Map.Entry<PotionEffect, Boolean> entry : effectAdditions.entrySet()) {
                        PotionEffect effect = entry.getKey();
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
        event.setButton(12, ButtonContainerIngredient.namespacedKey(3));
        event.setButton(20, new NamespacedKey("recipe_book", "brewing.icon"));
        event.setButton(21, ClusterMain.GLASS_GREEN);

        event.setButton(30, ButtonContainerIngredient.namespacedKey(0));
        event.setButton(31, ClusterMain.GLASS_GREEN);
        event.setButton(32, ClusterMain.GLASS_GREEN);
        event.setButton(33, ButtonContainerIngredient.namespacedKey(1));


    }
}
