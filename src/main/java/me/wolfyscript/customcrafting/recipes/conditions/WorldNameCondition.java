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

import me.wolfyscript.customcrafting.recipes.CustomRecipe;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonProperty;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ChatInputButton;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.DummyButton;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WorldNameCondition extends Condition<WorldNameCondition> {

    public static final NamespacedKey KEY = new NamespacedKey(NamespacedKeyUtils.NAMESPACE, "world_name");

    private static final String PARENT_LANG = "conditions.world_name";
    private static final String ADD = PARENT_LANG + ".add";
    private static final String LIST = PARENT_LANG + ".list";
    private static final String REMOVE = PARENT_LANG + ".remove";

    @JsonProperty("names")
    private final List<String> worldNames;

    public WorldNameCondition() {
        super(KEY);
        setAvailableOptions(Conditions.Option.EXACT);
        this.worldNames = new ArrayList<>();
    }

    @Override
    public boolean check(CustomRecipe<?> recipe, Conditions.Data data) {
        if (data.getBlock() != null) {
            return worldNames.contains(data.getBlock().getLocation().getWorld().getName());
        }
        return false;
    }

    public void addWorldName(String worldName) {
        this.worldNames.add(worldName);
    }

    public List<String> getWorldNames() {
        return worldNames;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder(option.toString());
        stringBuilder.append(";");
        for (String eliteWorkbench : worldNames) {
            stringBuilder.append(eliteWorkbench).append(",");
        }
        return stringBuilder.toString();
    }

    public static class GUIComponent extends FunctionalGUIComponent<WorldNameCondition> {

        public GUIComponent() {
            super(Material.GRASS_BLOCK, getLangKey(KEY.getKey(), "name"), getLangKey(KEY.getKey(), "description"),
                    (menu, api) -> {
                        menu.registerButton(new ActionButton<>(REMOVE, Material.RED_CONCRETE, (cache, guiHandler, player, guiInventory, slot, inventoryInteractEvent) -> {
                            var conditions = cache.getRecipeCreatorCache().getRecipeCache().getConditions();
                            if (!conditions.getByType(WorldNameCondition.class).getWorldNames().isEmpty()) {
                                conditions.getByType(WorldNameCondition.class).getWorldNames().remove(conditions.getByType(WorldNameCondition.class).getWorldNames().size() - 1);
                            }
                            return true;
                        }));
                        menu.registerButton(new DummyButton<>(LIST, Material.BOOK, (hashMap, cache, guiHandler, player, guiInventory, itemStack, slot, b) -> {
                            var condition = cache.getRecipeCreatorCache().getRecipeCache().getConditions().getByType(WorldNameCondition.class);
                            hashMap.put("%MODE%", condition.getOption().getDisplayString(api));
                            for (int i = 0; i < 4; i++) {
                                if (i < condition.getWorldNames().size()) {
                                    hashMap.put("%var" + i + "%", condition.getWorldNames().get(i));
                                } else {
                                    hashMap.put("%var" + i + "%", "...");
                                }
                            }
                            return itemStack;
                        }));
                        menu.registerButton(new ChatInputButton<>(ADD, Material.GREEN_CONCRETE, (guiHandler, player, s, strings) -> {
                            if (!s.isEmpty()) {
                                var world = Bukkit.getWorld(s);
                                if (world == null) {
                                    menu.sendMessage(guiHandler, menu.translatedMsgKey("missing_world"));
                                    return true;
                                }
                                var conditions = guiHandler.getCustomCache().getRecipeCreatorCache().getRecipeCache().getConditions();
                                var condition = conditions.getByType(WorldNameCondition.class);
                                if (condition.getWorldNames().contains(s)) {
                                    menu.sendMessage(guiHandler, menu.translatedMsgKey("already_existing"));
                                    return true;
                                }
                                conditions.getByType(WorldNameCondition.class).addWorldName(s);
                                return false;
                            }
                            return true;
                        }, (guiHandler, player, args) -> {
                            if (args.length > 0) {
                                return StringUtil.copyPartialMatches(args[0], Bukkit.getWorlds().stream().map(World::getName).toList(), Collections.emptyList());
                            }
                            return Collections.emptyList();
                        }));
                    },
                    (update, cache, condition, recipe) -> {
                        update.setButton(29, ADD);
                        update.setButton(31, LIST);
                        update.setButton(33, REMOVE);
                    });
        }
    }
}
