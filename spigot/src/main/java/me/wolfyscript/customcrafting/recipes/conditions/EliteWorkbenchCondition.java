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

import com.wolfyscript.utilities.bukkit.items.CustomItemBlockData;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.custom_data.EliteWorkbenchData;
import me.wolfyscript.customcrafting.configs.customitem.EliteCraftingTableSettings;
import me.wolfyscript.customcrafting.recipes.CustomRecipe;
import me.wolfyscript.customcrafting.recipes.RecipeType;
import me.wolfyscript.customcrafting.utils.ChatUtils;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonAlias;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonIgnore;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonProperty;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.DummyButton;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.util.StringUtil;

import java.util.*;
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
            Block block = data.getBlock();
            return customCrafting.getApi().getCore().getPersistentStorage().getOrCreateWorldStorage(block.getWorld()).getBlock(block.getLocation())
                    .flatMap(blockStorage -> blockStorage.getData(CustomItemBlockData.ID, CustomItemBlockData.class))
                    .flatMap(CustomItemBlockData::getCustomItem)
                    .flatMap(customItem -> customItem.getData(EliteCraftingTableSettings.class)
                            .map(eliteCraftingTableSettings -> eliteCraftingTableSettings.isEnabled() && eliteWorkbenches.contains(customItem.getNamespacedKey()))
                    ).orElse(false);
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
            super(Material.CRAFTING_TABLE, getLangKey(KEY.getKey(), "name"), getLangKey(KEY.getKey(), "description"),
                    (menu, api) -> {
                        menu.getButtonBuilder().chatInput(ADD)
                                .state(builder -> builder
                                        .icon(Material.GREEN_CONCRETE)
                                        .action((cache, guiHandler, player, guiInventory, i, inventoryInteractEvent) -> {
                                            var chat = guiInventory.getWindow().getChat();
                                            chat.sendMessage(player, chat.translated("msg.input.wui_command"));
                                            return true;
                                        }))
                                .inputAction((guiHandler, player, s, args) -> {
                                    if (args.length > 1) {
                                        var namespacedKey = ChatUtils.getNamespacedKey(player, "", args);
                                        if (namespacedKey != null) {
                                            var condition = guiHandler.getCustomCache().getRecipeCreatorCache().getRecipeCache().getConditions().getByType(EliteWorkbenchCondition.class);
                                            if (condition.getEliteWorkbenches().contains(namespacedKey)) {
                                                menu.sendMessage(guiHandler, menu.translatedMsgKey("already_existing"));
                                                return true;
                                            }
                                            var customItem = api.getRegistries().getCustomItems().get(namespacedKey);
                                            if (customItem == null) {
                                                menu.sendMessage(guiHandler, menu.translatedMsgKey("error"));
                                                return true;
                                            }
                                            Optional<EliteCraftingTableSettings> settings = customItem.getData(EliteCraftingTableSettings.class);
                                            if (settings.isPresent() && settings.get().isEnabled()) {
                                                condition.addEliteWorkbenches(namespacedKey);
                                                return false;
                                            }
                                            // Try the old elite crafting table settings
                                            EliteWorkbenchData data = (EliteWorkbenchData) customItem.getCustomData(CustomCrafting.ELITE_CRAFTING_TABLE_DATA);
                                            if (data != null && !data.isEnabled()) {
                                                menu.sendMessage(guiHandler, menu.translatedMsgKey("not_elite_workbench"));
                                                return true;
                                            }
                                            condition.addEliteWorkbenches(namespacedKey);
                                            return false;
                                        }
                                    }
                                    menu.sendMessage(guiHandler, menu.translatedMsgKey("no_name"));
                                    return true;
                                })
                                .tabComplete((guiHandler, player, args) -> {
                                    Set<NamespacedKey> entries = api.getRegistries().getCustomItems().entrySet().stream()
                                            .filter(entry -> entry.getValue().getData(EliteCraftingTableSettings.class).map(EliteCraftingTableSettings::isEnabled)
                                                    // Read old elite crafting table data
                                                    .orElseGet(() -> {
                                                        EliteWorkbenchData data = (EliteWorkbenchData) entry.getValue().getCustomData(CustomCrafting.ELITE_CRAFTING_TABLE_DATA);
                                                        return data != null && data.isEnabled();
                                                    })).map(Map.Entry::getKey).collect(Collectors.toSet());
                                    if (args.length == 2) {
                                        return StringUtil.copyPartialMatches(args[1], entries.stream()
                                                .filter(key -> key.getKeyComponent().getFolder().equals(args[0]))
                                                .map(namespacedKey -> namespacedKey.getKeyComponent().getObject())
                                                .distinct().toList(), new ArrayList<>());
                                    }
                                    if (args.length >= 1) {
                                        return StringUtil.copyPartialMatches(args[0], entries.stream().map(namespacedKey -> namespacedKey.getKeyComponent().getFolder()).distinct().toList(), new ArrayList<>());
                                    }
                                    return Collections.emptyList();
                                })
                                .register();
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
