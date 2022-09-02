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

package me.wolfyscript.customcrafting.recipes.conditions;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.CustomRecipe;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonCreator;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonIgnore;
import me.wolfyscript.lib.com.fasterxml.jackson.core.type.TypeReference;
import me.wolfyscript.lib.com.fasterxml.jackson.databind.InjectableValues;
import me.wolfyscript.lib.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.lib.com.fasterxml.jackson.databind.node.ObjectNode;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.json.jackson.JacksonUtil;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class Conditions {

    @JsonIgnore
    private final Map<NamespacedKey, Condition<?>> valuesMap;
    private final CustomCrafting customCrafting;

    //Conditions initialization
    public Conditions(CustomCrafting customCrafting) {
        this.valuesMap = new HashMap<>();
        this.customCrafting = customCrafting;
    }

    @JsonCreator
    private Conditions(JsonNode node) {
        this.customCrafting = CustomCrafting.inst(); //TODO: Dependency Injection
        var injectableValues = new InjectableValues.Std();
        injectableValues.addValue("customcrafting", this.customCrafting);
        var jsonReader = customCrafting.getApi().getJacksonMapperUtil().getGlobalMapper().reader(injectableValues);
        if (node.isArray()) {
            //Required for backwards compatibility with previous configs.
            this.valuesMap = new HashMap<>();
            node.elements().forEachRemaining(element -> {
                ((ObjectNode) element).put("key", String.valueOf(new NamespacedKey(NamespacedKeyUtils.NAMESPACE, element.path("id").asText())));
                try {
                    var condition = jsonReader.readValue(element, Condition.class);
                    if (!condition.getOption().equals(Option.IGNORE)) {
                        valuesMap.put(condition.getNamespacedKey(), condition);
                    }
                } catch (IOException ex) {
                    this.customCrafting.getApi().getConsole().getLogger().log(Level.SEVERE, "Failed to deserialize condition! \"" + element + "\"!");
                    ex.printStackTrace();
                }
            });
        } else {
            Map<NamespacedKey, Condition<?>> values = new HashMap<>();
            try {
                Set<Condition<?>> conditions = jsonReader.forType(new TypeReference<Set<Condition<?>>>() {}).readValue(node.path("values"));
                values = conditions.stream().collect(Collectors.toMap(Condition::getNamespacedKey, condition -> condition));
            } catch (IOException e) {
                this.customCrafting.getApi().getConsole().getLogger().log(Level.SEVERE, "Failed to deserialize conditions!");
                e.printStackTrace();
            }
            this.valuesMap = values;
        }
    }

    public boolean check(String id, CustomRecipe<?> customRecipe, Data data) {
        var condition = getByID(id);
        return condition == null || condition.check(customRecipe, data);
    }

    public boolean checkConditions(CustomRecipe<?> customRecipe, Data data) {
        return valuesMap.values().stream().allMatch(condition -> condition.check(customRecipe, data));
    }

    @JsonIgnore
    public EliteWorkbenchCondition getEliteCraftingTableCondition() {
        return (EliteWorkbenchCondition) getByID("elite_crafting_table");
    }

    public <C extends Condition<C>> C getByType(Class<C> type) {
        return valuesMap.values().stream().filter(type::isInstance).map(type::cast).findFirst().orElse(null);
    }

    public Condition<?> getByKey(NamespacedKey key) {
        return valuesMap.get(key);
    }

    public boolean has(NamespacedKey key) {
        return valuesMap.containsKey(key);
    }

    public void setCondition(Condition<?> condition) {
        valuesMap.put(condition.getNamespacedKey(), condition);
    }

    public void removeCondition(Condition<?> condition) {
        valuesMap.remove(condition.getNamespacedKey());
    }

    public void removeCondition(NamespacedKey key) {
        valuesMap.remove(key);
    }

    public Collection<Condition<?>> getValues() {
        return valuesMap.values();
    }

    public Set<NamespacedKey> keySet() {
        return valuesMap.keySet();
    }

    @Deprecated
    public Condition<?> getByID(String id) {
        return valuesMap.get(new NamespacedKey(NamespacedKeyUtils.NAMESPACE, id));
    }

    public enum Option {
        EXACT,
        @Deprecated /*Ignore is no longer used! Previous conditions using it will no longer be loaded!*/ IGNORE,
        HIGHER,
        HIGHER_EXACT,
        LOWER,
        LOWER_EXACT,
        HIGHER_LOWER;

        private final String displayString;

        Option() {
            this.displayString = "$inventories.recipe_creator.conditions.mode_names." + this.toString().toLowerCase(Locale.ROOT) + "$";
        }

        public String getDisplayString() {
            return displayString;
        }

        public String getDisplayString(WolfyUtilities api) {
            return api.getLanguageAPI().replaceKeys(displayString);
        }
    }

    public static class Data {

        private Player player;
        private Block block;
        private InventoryView inventoryView;

        @Deprecated
        public Data(@Nullable Player player, Block block, @Nullable InventoryView inventoryView) {
            this.player = player;
            this.block = block;
            this.inventoryView = inventoryView;
        }

        @Deprecated
        public Data(Player player, Block block) {
            this(player, block, null);
        }

        @Deprecated
        public Data(Player player) {
            this(player, null, null);
        }

        public static Data of(@Nullable Player player, Block block, @Nullable InventoryView inventoryView) {
            return new Data(player, block, inventoryView);
        }

        public static Data of(Block block, @Nullable InventoryView inventoryView) {
            return new Data(null, block, inventoryView);
        }

        public static Data of(Player player) {
            return new Data(player);
        }

        public static Data of(@Nullable Player player, @Nullable InventoryView inventoryView) {
            Block block = null;
            if (inventoryView != null) {
                Location topInvLoc = inventoryView.getTopInventory().getLocation();
                if (topInvLoc != null) {
                    block = topInvLoc.getBlock();
                }
            } else if (player != null) {
                //Previously the player#getTargetedBlock method was used. But this performs better (no raytracing)
                block = player.getLocation().getBlock();
            }
            return new Data(player, block, inventoryView);
        }

        @Nullable
        public Player getPlayer() {
            return player;
        }

        public void setPlayer(@Nullable Player player) {
            this.player = player;
        }

        public Block getBlock() {
            return block;
        }

        public void setBlock(Block block) {
            this.block = block;
        }

        @Nullable
        public InventoryView getInventoryView() {
            return inventoryView;
        }

        public void setInventoryView(@Nullable InventoryView inventoryView) {
            this.inventoryView = inventoryView;
        }
    }

}
