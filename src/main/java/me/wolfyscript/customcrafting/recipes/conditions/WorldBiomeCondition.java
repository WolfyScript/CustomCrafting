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

import com.wolfyscript.utilities.NamespacedKey;
import com.wolfyscript.utilities.bukkit.BukkitNamespacedKey;
import com.wolfyscript.utilities.bukkit.gui.callback.CallbackButtonRender;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import me.wolfyscript.customcrafting.recipes.CustomRecipe;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Material;
import org.bukkit.block.Biome;

public class WorldBiomeCondition extends Condition<WorldBiomeCondition> {

    public static final NamespacedKey KEY = new BukkitNamespacedKey(NamespacedKeyUtils.NAMESPACE, "world_biome");

    private final List<String> biomes;

    public WorldBiomeCondition() {
        super(KEY);
        setAvailableOptions(Conditions.Option.EXACT);
        this.biomes = new ArrayList<>();
    }

    @Override
    public boolean check(CustomRecipe<?> recipe, Conditions.Data data) {
        if (data.getBlock() != null) {
            return biomes.contains(data.getBlock().getBiome().toString());
        }
        return false;
    }

    public void addBiome(String biome) {
        this.biomes.add(biome);
    }

    public List<String> getBiomes() {
        return biomes;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder(option.toString());
        stringBuilder.append(";");
        for (String eliteWorkbench : biomes) {
            stringBuilder.append(eliteWorkbench).append(",");
        }
        return stringBuilder.toString();
    }

    public static class GUIComponent extends FunctionalGUIComponent<WorldBiomeCondition> {

        private static final String PARENT_LANG = "conditions.world_biome";
        private static final String ADD = PARENT_LANG + ".add";
        private static final String LIST = PARENT_LANG + ".list";
        private static final String REMOVE = PARENT_LANG + ".remove";

        public GUIComponent() {
            super(Material.SAND, getLangKey(KEY.getKey(), "name"), getLangKey(KEY.getKey(), "description"),
                    (menu, api) -> {
                        var bB = menu.getButtonBuilder();
                        bB.action(REMOVE).state(state -> state.icon(Material.RED_CONCRETE).action((cache, guiHandler, player, guiInventory, btn, slot, inventoryInteractEvent) -> {
                            var conditions = cache.getRecipeCreatorCache().getRecipeCache().getConditions();
                            if (!conditions.getByType(WorldBiomeCondition.class).getBiomes().isEmpty()) {
                                conditions.getByType(WorldBiomeCondition.class).getBiomes().remove(conditions.getByType(WorldBiomeCondition.class).getBiomes().size() - 1);
                            }
                            return true;
                        })).register();
                        bB.dummy(LIST).state(state -> state.icon(Material.BOOK).render((cache, guiHandler, player, guiInventory, btn, itemStack, slot) -> {
                            WorldBiomeCondition condition = guiHandler.getCustomCache().getRecipeCreatorCache().getRecipeCache().getConditions().getByType(WorldBiomeCondition.class);
                            var tagResBuilder = TagResolver.builder();
                            tagResBuilder.resolver(Placeholder.unparsed("mode", condition.getOption().getDisplayString(api)));
                            for (int i = 0; i < 4; i++) {
                                if (i < condition.getBiomes().size()) {
                                    tagResBuilder.resolver(Placeholder.unparsed("var"+i, condition.getBiomes().get(i)));
                                } else {
                                    tagResBuilder.resolver(Placeholder.unparsed("var"+i, "..."));
                                }
                            }
                            return CallbackButtonRender.UpdateResult.of(tagResBuilder.build());
                        })).register();
                        bB.chatInput(ADD).state(state -> state.icon(Material.GREEN_CONCRETE)).inputAction((guiHandler, player, s, strings) -> {
                            if (!s.isEmpty()) {
                                try {
                                    var biome = Biome.valueOf(s.toUpperCase(Locale.ROOT));
                                    var conditions = guiHandler.getCustomCache().getRecipeCreatorCache().getRecipeCache().getConditions();
                                    WorldBiomeCondition condition = conditions.getByType(WorldBiomeCondition.class);
                                    if (condition.getBiomes().contains(biome.toString())) {
                                        menu.sendMessage(guiHandler, menu.translatedMsgKey("already_existing"));
                                        return true;
                                    }
                                    conditions.getByType(WorldBiomeCondition.class).addBiome(biome.toString());
                                    return false;
                                } catch (IllegalArgumentException ex) {
                                    menu.sendMessage(guiHandler, menu.translatedMsgKey("invalid_biome"));
                                }
                            }
                            return true;
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
