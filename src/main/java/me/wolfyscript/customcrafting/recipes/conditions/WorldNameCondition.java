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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wolfyscript.utilities.NamespacedKey;
import com.wolfyscript.utilities.bukkit.BukkitNamespacedKey;
import com.wolfyscript.utilities.bukkit.gui.callback.CallbackButtonRender;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import me.wolfyscript.customcrafting.recipes.CustomRecipe;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.util.StringUtil;

public class WorldNameCondition extends Condition<WorldNameCondition> {

    public static final NamespacedKey KEY = new BukkitNamespacedKey(NamespacedKeyUtils.NAMESPACE, "world_name");

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
                        var bB = menu.getButtonBuilder();
                        bB.action(REMOVE).state(state -> state.icon(Material.RED_CONCRETE).action((cache, guiHandler, player, guiInventory, btn, slot, event) -> {
                            var conditions = cache.getRecipeCreatorCache().getRecipeCache().getConditions();
                            if (!conditions.getByType(WorldNameCondition.class).getWorldNames().isEmpty()) {
                                conditions.getByType(WorldNameCondition.class).getWorldNames().remove(conditions.getByType(WorldNameCondition.class).getWorldNames().size() - 1);
                            }
                            return true;
                        })).register();
                        bB.dummy(LIST).state(state -> state.icon(Material.BOOK).render((cache, guiHandler, player, guiInventory, btn, stack, slot) -> {
                            var condition = cache.getRecipeCreatorCache().getRecipeCache().getConditions().getByType(WorldNameCondition.class);
                            var tagResBuilder = TagResolver.builder();
                            tagResBuilder.resolver(Placeholder.unparsed("mode", condition.getOption().getDisplayString(api)));
                            for (int i = 0; i < 4; i++) {
                                if (i < condition.getWorldNames().size()) {
                                    tagResBuilder.resolver(Placeholder.unparsed("var"+i, condition.getWorldNames().get(i)));
                                } else {
                                    tagResBuilder.resolver(Placeholder.unparsed("var"+i,  "..."));
                                }
                            }
                            return CallbackButtonRender.UpdateResult.of();
                        })).register();
                        bB.chatInput(ADD).state(state -> state.icon(Material.GREEN_CONCRETE)).inputAction((guiHandler, player, s, strings) -> {
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
                        }).tabComplete((guiHandler, player, args) -> {
                            if (args.length > 0) {
                                return StringUtil.copyPartialMatches(args[0], Bukkit.getWorlds().stream().map(World::getName).toList(), Collections.emptyList());
                            }
                            return Collections.emptyList();
                        }).register();
                    },
                    (update, cache, condition, recipe) -> {
                        update.setButton(29, ADD);
                        update.setButton(31, LIST);
                        update.setButton(33, REMOVE);
                    });
        }
    }
}
