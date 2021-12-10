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
import me.wolfyscript.customcrafting.configs.custom_data.EliteWorkbenchData;
import me.wolfyscript.customcrafting.recipes.CustomRecipe;
import me.wolfyscript.customcrafting.recipes.RecipeType;
import me.wolfyscript.customcrafting.utils.ChatUtils;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.custom_items.references.WolfyUtilitiesRef;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ChatInputButton;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.DummyButton;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.Registry;
import org.bukkit.Material;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class EliteWorkbenchCondition extends Condition<EliteWorkbenchCondition> {

    public static final NamespacedKey KEY = new NamespacedKey(NamespacedKeyUtils.NAMESPACE, "elite_crafting_table");

    private static final String PARENT_LANG = "conditions.elite_crafting_table";
    private static final String ADD = PARENT_LANG + ".add";
    private static final String LIST = PARENT_LANG + ".list";
    private static final String REMOVE = PARENT_LANG + ".remove";

    @JsonAlias({"elite_crafting_tables", "elite_workbenches"})
    @JsonProperty
    private final List<NamespacedKey> eliteWorkbenches;

    public EliteWorkbenchCondition() {
        super(KEY);
        setAvailableOptions(Conditions.Option.EXACT);
        this.eliteWorkbenches = new ArrayList<>();
    }

    @Override
    public boolean isApplicable(CustomRecipe<?> recipe) {
        return RecipeType.Container.ELITE_CRAFTING.isInstance(recipe);
    }

    @Override
    public boolean check(CustomRecipe<?> recipe, Conditions.Data data) {
        if (RecipeType.Container.ELITE_CRAFTING.isInstance(recipe)) {
            if (data.getBlock() != null) {
                CustomItem customItem = NamespacedKeyUtils.getCustomItem(data.getBlock());
                if (customItem != null && customItem.getApiReference() instanceof WolfyUtilitiesRef wolfyUtilsRef) {
                    return eliteWorkbenches.contains(wolfyUtilsRef.getNamespacedKey()) && ((EliteWorkbenchData) customItem.getCustomData(CustomCrafting.ELITE_CRAFTING_TABLE)).isEnabled();
                }
            }
            return false;
        }
        return true;
    }

    public void addEliteWorkbenches(NamespacedKey eliteWorkbenches) {
        if (!this.eliteWorkbenches.contains(eliteWorkbenches)) {
            this.eliteWorkbenches.add(eliteWorkbenches);
        }
    }

    @JsonIgnore
    public List<NamespacedKey> getEliteWorkbenches() {
        return eliteWorkbenches;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder(option.toString());
        stringBuilder.append(";");
        for (NamespacedKey eliteWorkbench : eliteWorkbenches) {
            stringBuilder.append(eliteWorkbench.toString()).append(",");
        }
        return stringBuilder.toString();
    }

    public static class GUIComponent extends FunctionalGUIComponent<EliteWorkbenchCondition> {

        public GUIComponent() {
            super(Material.CRAFTING_TABLE, getLangKey(KEY.getKey(), "name"), List.of(getLangKey(KEY.getKey(), "description")),
                    (menu, api) -> {
                        menu.registerButton(new ChatInputButton<>(ADD, Material.GREEN_CONCRETE, (guiHandler, player, s, args) -> {
                            if (args.length > 1) {
                                var namespacedKey = ChatUtils.getNamespacedKey(player, "", args);
                                if (namespacedKey != null) {
                                    var condition = guiHandler.getCustomCache().getRecipeCreatorCache().getRecipeCache().getConditions().getByType(EliteWorkbenchCondition.class);
                                    if (condition.getEliteWorkbenches().contains(namespacedKey)) {
                                        menu.sendMessage(player, "already_existing");
                                        return true;
                                    }
                                    var customItem = Registry.CUSTOM_ITEMS.get(namespacedKey);
                                    if (customItem == null) {
                                        menu.sendMessage(player, "error");
                                        return true;
                                    }
                                    EliteWorkbenchData data = (EliteWorkbenchData) customItem.getCustomData(CustomCrafting.ELITE_CRAFTING_TABLE);
                                    if (data != null && !data.isEnabled()) {
                                        menu.sendMessage(player, "not_elite_workbench");
                                        return true;
                                    }
                                    condition.addEliteWorkbenches(namespacedKey);
                                    return false;
                                }
                            }
                            menu.sendMessage(player, "no_name");
                            return true;
                        }, (guiHandler, player, args) -> {
                            Set<NamespacedKey> entries = Registry.CUSTOM_ITEMS.entrySet().stream().filter(entry -> {
                                EliteWorkbenchData data = (EliteWorkbenchData) entry.getValue().getCustomData(CustomCrafting.ELITE_CRAFTING_TABLE);
                                return data != null && data.isEnabled();
                            }).map(entry -> NamespacedKeyUtils.toInternal(entry.getKey())).collect(Collectors.toSet());
                            List<String> results = new ArrayList<>();
                            if (args.length > 0) {
                                if (args.length == 1) {
                                    StringUtil.copyPartialMatches(args[0], entries.stream().map(NamespacedKey::getNamespace).distinct().toList(), results);
                                } else if (args.length == 2) {
                                    StringUtil.copyPartialMatches(args[1], entries.stream().filter(key -> key.getNamespace().equals(args[0])).map(NamespacedKey::getKey).distinct().toList(), results);
                                }
                                return results;
                            }
                            return results;
                        }));
                        menu.registerButton(new DummyButton<>(LIST, Material.BOOK, (hashMap, cache, guiHandler, player, guiInventory, itemStack, slot, b) -> {
                            var condition = cache.getRecipeCreatorCache().getRecipeCache().getConditions().getEliteCraftingTableCondition();
                            for (int i = 0; i < 4; i++) {
                                if (i < condition.getEliteWorkbenches().size()) {
                                    hashMap.put("%var" + i + "%", condition.getEliteWorkbenches().get(i));
                                } else {
                                    hashMap.put("%var" + i + "%", "...");
                                }
                            }
                            return itemStack;
                        }));
                        menu.registerButton(new ActionButton<>(REMOVE, Material.RED_CONCRETE, (cache, guiHandler, player, guiInventory, i, inventoryInteractEvent) -> {
                            var condition = cache.getRecipeCreatorCache().getRecipeCache().getConditions().getByType(EliteWorkbenchCondition.class);
                            if (!condition.getEliteWorkbenches().isEmpty()) {
                                condition.getEliteWorkbenches().remove(condition.getEliteWorkbenches().size() - 1);
                            }
                            return true;
                        }));
                    },
                    (update, cache, condition, recipe) -> {
                        update.setButton(29, ADD);
                        update.setButton(31, LIST);
                        update.setButton(33, REMOVE);
                    });
        }

        @Override
        public boolean shouldRender(RecipeType<?> type) {
            return RecipeType.Container.ELITE_CRAFTING.has(type);
        }
    }
}
