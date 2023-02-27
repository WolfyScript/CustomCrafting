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

import com.google.common.base.Preconditions;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.customitem.EliteCraftingTableSettings;
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
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
        this.customCrafting = CustomCrafting.inst(); //TODO: Dependency Injection (v5)
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
                Set<Condition<?>> conditions = jsonReader.forType(new TypeReference<Set<Condition<?>>>() {
                }).readValue(node.path("values"));
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
        private EliteCraftingTableSettings eliteCraftingTableSettings;

        private Data(Player player) {
            Preconditions.checkNotNull(player);
            this.player = player;
        }

        private Data(Block block) {
            Preconditions.checkNotNull(block);
            this.block = block;
        }

        public static Data of(@NotNull Player player) {
            return new Data(player);
        }

        public static Data of(@NotNull Block block) {
            return new Data(block);
        }

        @Deprecated
        public static Data of(@Nullable Player player, @NotNull Block block, @Nullable InventoryView inventoryView) {
            Data data = player == null ? of(block) : of(player);
            if (inventoryView != null) data.setInventoryView(inventoryView);
            return data;
        }

        @Deprecated
        public static Data of(@NotNull Block block, @Nullable InventoryView inventoryView) {
            return inventoryView == null ? of(block) : of(block).setInventoryView(inventoryView);
        }

        public static Data of(@Nullable Player player, @Nullable InventoryView inventoryView) {
            Data data;
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
            if (block != null) {
                data = of(block);
            } else if (player != null) {
                data = of(player);
            } else {
                throw new IllegalArgumentException("Condition Data must at least have a player or block!");
            }
            if (inventoryView != null)
                return data.setInventoryView(inventoryView);
            return data;
        }

        @Nullable
        public Player getPlayer() {
            return player;
        }

        public Block getBlock() {
            return block;
        }

        @Nullable
        public InventoryView getInventoryView() {
            return inventoryView;
        }

        public Optional<Block> block() {
            return Optional.ofNullable(block);
        }

        public Optional<Player> player() {
            return Optional.ofNullable(player);
        }

        public Optional<InventoryView> inventoryView() {
            return Optional.ofNullable(inventoryView);
        }

        public Optional<EliteCraftingTableSettings> eliteCraftingTableSettings() {
            return Optional.ofNullable(eliteCraftingTableSettings);
        }

        public Data setPlayer(Player player) {
            if (player != null) {
                this.player = player;
            }
            return this;
        }

        public Data setBlock(Block block) {
            if (block != null) {
                this.block = block;
            }
            return this;
        }

        public Data setInventoryView(InventoryView inventoryView) {
            if (inventoryView != null) {
                this.inventoryView = inventoryView;
            }
            return this;
        }

        public Data setEliteCraftingTableSettings(EliteCraftingTableSettings eliteCraftingTableSettings) {
            if (eliteCraftingTableSettings != null) {
                this.eliteCraftingTableSettings = eliteCraftingTableSettings;
            }
            return this;
        }
    }

}
