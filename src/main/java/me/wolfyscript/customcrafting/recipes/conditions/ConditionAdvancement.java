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

import com.google.common.collect.Streams;
import me.wolfyscript.customcrafting.recipes.CustomRecipe;
import me.wolfyscript.customcrafting.recipes.RecipeType;
import me.wolfyscript.customcrafting.utils.ChatUtils;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ChatInputButton;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.DummyButton;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.advancement.Advancement;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ConditionAdvancement extends Condition<ConditionAdvancement> {

    public static final NamespacedKey KEY = new NamespacedKey(NamespacedKeyUtils.NAMESPACE, "player_advancements");

    private static final String PARENT_LANG = "conditions.player_advancements";
    private static final String ADD = PARENT_LANG + ".add";
    private static final String LIST = PARENT_LANG + ".list";
    private static final String REMOVE = PARENT_LANG + ".remove";

    private List<NamespacedKey> advancements;

    public ConditionAdvancement() {
        super(KEY);
        this.advancements = new ArrayList<>();
        setAvailableOptions(Conditions.Option.EXACT);
    }

    @Override
    public boolean isApplicable(CustomRecipe<?> recipe) {
        return RecipeType.Container.CRAFTING.isInstance(recipe) || RecipeType.Container.ELITE_CRAFTING.isInstance(recipe) || switch (recipe.getRecipeType().getType()) {
            case BREWING_STAND, GRINDSTONE -> true;
            default -> false;
        };
    }

    @Override
    public boolean check(CustomRecipe<?> recipe, Conditions.Data data) {
        if (data.getPlayer() != null) {
            return advancements.stream().allMatch(key -> {
                Advancement advancement = Bukkit.getAdvancement(new org.bukkit.NamespacedKey(key.getNamespace(), key.getKey()));
                return advancement == null || data.getPlayer().getAdvancementProgress(advancement).isDone();
            });
        }
        return true;
    }

    public List<NamespacedKey> getAdvancements() {
        return advancements;
    }

    public void setAdvancements(List<NamespacedKey> advancements) {
        this.advancements = advancements;
    }

    public static class GUIComponent extends FunctionalGUIComponent<ConditionAdvancement> {

        public GUIComponent() {
            super(Material.EXPERIENCE_BOTTLE, getLangKey(KEY.getKey(), "name"), getLangKey(KEY.getKey(), "description"),
                    (menu, api) -> {
                        menu.registerButton(new ChatInputButton<>(ADD, Material.GREEN_CONCRETE, (guiHandler, player, s, args) -> {
                            if (args.length > 1) {
                                var key = ChatUtils.getInternalNamespacedKey(player, "", args);
                                if (key != null) {
                                    var condition = guiHandler.getCustomCache().getRecipeCreatorCache().getRecipeCache().getConditions().getByType(ConditionAdvancement.class);
                                    if (condition.advancements.contains(key)) {
                                        menu.sendMessage(guiHandler, menu.translatedMsgKey("already_existing"));
                                        return true;
                                    }
                                    if (Bukkit.getAdvancement(new org.bukkit.NamespacedKey(key.getNamespace(), key.getKey())) == null) {
                                        menu.sendMessage(guiHandler, menu.translatedMsgKey("advancement_not_found"));
                                        return true;
                                    }
                                    condition.advancements.add(key);
                                    return false;
                                }
                            }
                            menu.sendMessage(guiHandler, menu.translatedMsgKey("no_name"));
                            return true;
                        }, (guiHandler, player, args) -> {
                            Set<NamespacedKey> entries = Streams.stream(Bukkit.advancementIterator()).map(advancement -> NamespacedKey.fromBukkit(advancement.getKey())).collect(Collectors.toSet());
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
                            var condition = cache.getRecipeCreatorCache().getRecipeCache().getConditions().getByType(ConditionAdvancement.class);
                            for (int i = 0; i < 4; i++) {
                                if (i < condition.advancements.size()) {
                                    hashMap.put("%var" + i + "%", condition.advancements.get(i));
                                } else {
                                    hashMap.put("%var" + i + "%", "...");
                                }
                            }
                            return itemStack;
                        }));
                        menu.registerButton(new ActionButton<>(REMOVE, Material.RED_CONCRETE, (cache, guiHandler, player, guiInventory, i, inventoryInteractEvent) -> {
                            var condition = cache.getRecipeCreatorCache().getRecipeCache().getConditions().getByType(ConditionAdvancement.class);
                            if (!condition.advancements.isEmpty()) {
                                condition.advancements.remove(condition.advancements.size() - 1);
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
            return RecipeType.Container.CRAFTING.has(type) || RecipeType.Container.ELITE_CRAFTING.has(type) || type == RecipeType.BREWING_STAND || type == RecipeType.GRINDSTONE;
        }
    }
}
