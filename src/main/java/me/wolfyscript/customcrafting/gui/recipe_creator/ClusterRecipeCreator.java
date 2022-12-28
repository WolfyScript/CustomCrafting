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

package me.wolfyscript.customcrafting.gui.recipe_creator;

import com.wolfyscript.utilities.NamespacedKey;
import com.wolfyscript.utilities.bukkit.BukkitNamespacedKey;
import com.wolfyscript.utilities.bukkit.gui.InventoryAPI;
import com.wolfyscript.utilities.bukkit.gui.callback.CallbackButtonAction;
import com.wolfyscript.utilities.bukkit.gui.callback.CallbackButtonRender;
import com.wolfyscript.utilities.bukkit.world.inventory.PlayerHeadUtils;
import com.wolfyscript.utilities.common.gui.ButtonInteractionResult;
import com.wolfyscript.utilities.common.gui.GUIClickInteractionDetails;
import com.wolfyscript.utilities.common.gui.GUIInteractionDetails;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.recipe_creator.RecipeCache;
import me.wolfyscript.customcrafting.gui.CCCluster;
import me.wolfyscript.customcrafting.recipes.RecipePriority;
import me.wolfyscript.customcrafting.utils.ChatUtils;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.util.StringUtil;

public class ClusterRecipeCreator extends CCCluster {

    public static final String KEY = "recipe_creator";

    //Buttons
    public static final NamespacedKey CONDITIONS = new BukkitNamespacedKey(KEY, "conditions");
    public static final NamespacedKey GROUP = new BukkitNamespacedKey(KEY, "group");
    public static final NamespacedKey TAGS = new BukkitNamespacedKey(KEY, "tags");
    public static final NamespacedKey SAVE = new BukkitNamespacedKey(KEY, "save");
    public static final NamespacedKey SAVE_AS = new BukkitNamespacedKey(KEY, "save_as");
    public static final NamespacedKey PRIORITY = new BukkitNamespacedKey(KEY, "priority");
    public static final NamespacedKey EXACT_META = new BukkitNamespacedKey(KEY, "exact_meta");
    public static final NamespacedKey HIDDEN = new BukkitNamespacedKey(KEY, "hidden");
    public static final NamespacedKey VANILLA_BOOK = new BukkitNamespacedKey(KEY, "vanilla_book");
    public static final String SHAPELESS = "crafting.shapeless";
    public static final String MIRROR_VERTICAL = "crafting.mirror_vertical";
    public static final String MIRROR_HORIZONTAL = "crafting.mirror_horizontal";
    public static final String MIRROR_ROTATION = "crafting.mirror_rotation";
    //Language Keys
    private static final String ENABLED = ".enabled";
    public static final NamespacedKey EXACT_META_ENABLED = enabledKey(EXACT_META.getKey());
    public static final NamespacedKey HIDDEN_ENABLED = enabledKey(HIDDEN.getKey());
    public static final NamespacedKey VANILLA_BOOK_ENABLED = enabledKey(VANILLA_BOOK.getKey());
    public static final NamespacedKey SHAPELESS_ENABLED = enabledKey(SHAPELESS);
    public static final NamespacedKey MIRROR_VERTICAL_ENABLED = enabledKey(MIRROR_VERTICAL);
    public static final NamespacedKey MIRROR_HORIZONTAL_ENABLED = enabledKey(MIRROR_HORIZONTAL);
    public static final NamespacedKey MIRROR_ROTATION_ENABLED = enabledKey(MIRROR_ROTATION);
    private static final String DISABLED = ".disabled";
    public static final NamespacedKey EXACT_META_DISABLED = disabledKey(EXACT_META.getKey());
    public static final NamespacedKey HIDDEN_DISABLED = disabledKey(HIDDEN.getKey());
    public static final NamespacedKey VANILLA_BOOK_DISABLED = disabledKey(VANILLA_BOOK.getKey());
    public static final NamespacedKey SHAPELESS_DISABLED = disabledKey(SHAPELESS);
    public static final NamespacedKey MIRROR_VERTICAL_DISABLED = disabledKey(MIRROR_VERTICAL);
    public static final NamespacedKey MIRROR_HORIZONTAL_DISABLED = disabledKey(MIRROR_HORIZONTAL);
    public static final NamespacedKey MIRROR_ROTATION_DISABLED = disabledKey(MIRROR_ROTATION);

    //Window keys
    public static final NamespacedKey ITEM_EDITOR = new BukkitNamespacedKey(KEY, "item_editor");

    private static NamespacedKey enabledKey(String key) {
        return new BukkitNamespacedKey(KEY, key + ENABLED);
    }

    private static NamespacedKey disabledKey(String key) {
        return new BukkitNamespacedKey(KEY, key + DISABLED);
    }

    public ClusterRecipeCreator(InventoryAPI<CCCache> inventoryAPI, CustomCrafting customCrafting) {
        super(inventoryAPI, KEY, customCrafting);
    }

    @Override
    public void onInit() {
        registerGuiWindow(new RecipeCreatorCrafting(this, customCrafting));
        registerGuiWindow(new RecipeCreatorCooking(this, customCrafting));
        registerGuiWindow(new RecipeCreatorAnvil(this, customCrafting));
        registerGuiWindow(new RecipeCreatorCauldron(this, customCrafting));
        registerGuiWindow(new RecipeCreatorStonecutter(this, customCrafting));
        registerGuiWindow(new RecipeCreatorGrindstone(this, customCrafting));
        registerGuiWindow(new RecipeCreatorCraftingElite(this, customCrafting));
        registerGuiWindow(new RecipeCreatorCraftingEliteSettings(this, customCrafting));
        registerGuiWindow(new RecipeCreatorBrewing(this, customCrafting));
        registerGuiWindow(new RecipeCreatorSmithing(this, customCrafting));
        //Other Menus
        registerGuiWindow(new MenuConditions(this, customCrafting));
        registerGuiWindow(new MenuConditionsAdd(this, customCrafting));
        registerGuiWindow(new MenuResult(this, customCrafting));
        registerGuiWindow(new MenuIngredient(this, customCrafting));
        //Tags
        registerGuiWindow(new MenuTagSettings(this, customCrafting));
        registerGuiWindow(new MenuTagChooseList(this, customCrafting));
        registerGuiWindow(new MenuItemEditor(this, customCrafting));

        getButtonBuilder().action(CONDITIONS.getKey()).state(state -> state.icon(Material.CYAN_CONCRETE_POWDER).action((holder, cache, btn, slot, details) -> {
            holder.getGuiHandler().openWindow("conditions");
            return ButtonInteractionResult.cancel(true);
        })).register();
        getButtonBuilder().action(TAGS.getKey()).state(state -> state.icon(Material.NAME_TAG).action((holder, cache, btn, slot, details) -> {
            holder.getGuiHandler().openWindow("tag_settings");
            return ButtonInteractionResult.cancel(true);
        })).register();
        getButtonBuilder().chatInput(GROUP.getKey()).state(state -> state.icon(Material.BOOKSHELF).action((holder, cache, btn, slot, details) -> {
                    if (details instanceof GUIClickInteractionDetails clickDetails && clickDetails.isRightClick()) {
                        cache.getRecipeCreatorCache().getRecipeCache().setGroup("");
                        return ButtonInteractionResult.cancel(false);
                    }
                    return ButtonInteractionResult.cancel(true);
                }).render((holder, cache, btn, slot, itemStack) -> CallbackButtonRender.Result.of(Placeholder.parsed("group", cache.getRecipeCreatorCache().getRecipeCache().getGroup()))))
                .inputAction((guiHandler, player, s, args) -> {
                    if (args.length > 0) {
                        guiHandler.getCustomCache().getRecipeCreatorCache().getRecipeCache().setGroup(args[0]);
                    }
                    return false;
                }).tabComplete((guiHandler, player, args) -> {
                    List<String> results = new ArrayList<>();
                    StringUtil.copyPartialMatches(args[0], customCrafting.getRegistries().getRecipes().groups(), results);
                    return results;
                }).register();

        getButtonBuilder().multiChoice(VANILLA_BOOK.getKey())
                // State: vanilla = true, auto_discover = true
                .addState(state -> state.subKey("vanilla_book_discover").icon(Material.GRASS_BLOCK).action((holder, cache, btn, slot, details) -> {
                    cache.getRecipeCreatorCache().getRecipeCache().setVanillaBook(true);
                    cache.getRecipeCreatorCache().getRecipeCache().setAutoDiscover(false);
                    return ButtonInteractionResult.cancel(true);
                }))
                // State: vanilla = true, auto_discover = false
                .addState(state -> state.subKey("vanilla_book_no_discover").icon(Material.GRASS_BLOCK).action((holder, cache, btn, slot, details) -> {
                    cache.getRecipeCreatorCache().getRecipeCache().setVanillaBook(false);
                    cache.getRecipeCreatorCache().getRecipeCache().setAutoDiscover(false);
                    return ButtonInteractionResult.cancel(true);
                }))
                // State: vanilla = false, auto_discover = false
                .addState(state -> state.subKey("no_vanilla_book").icon(Material.GRASS_BLOCK).action((holder, cache, btn, slot, details) -> {
                    cache.getRecipeCreatorCache().getRecipeCache().setVanillaBook(true);
                    cache.getRecipeCreatorCache().getRecipeCache().setAutoDiscover(true);
                    return ButtonInteractionResult.cancel(true);
                }))
                .stateFunction((holder, cache, button, slot) -> {
                    RecipeCache<?> recipeCache = cache.getRecipeCreatorCache().getRecipeCache();
                    if (recipeCache.isVanillaBook() && recipeCache.isAutoDiscover()) {
                        return 0;
                    }
                    return recipeCache.isVanillaBook() ? 1 : 2;
                }).register();

        getButtonBuilder().toggle(ClusterRecipeCreator.EXACT_META.getKey()).stateFunction((holder, cache, slot) -> cache.getRecipeCreatorCache().getRecipeCache().isCheckNBT())
                .enabledState(state -> state.subKey("enabled").icon(Material.ITEM_FRAME).action((holder, cache, btn, slot, details) -> {
                    cache.getRecipeCreatorCache().getRecipeCache().setCheckNBT(false);
                    return ButtonInteractionResult.cancel(true);
                })).disabledState(state -> state.subKey("disabled").icon(Material.PAPER).action((holder, cache, btn, slot, details) -> {
                    cache.getRecipeCreatorCache().getRecipeCache().setCheckNBT(true);
                    return ButtonInteractionResult.cancel(true);
                })).register();
        getButtonBuilder().toggle(ClusterRecipeCreator.HIDDEN.getKey()).stateFunction((holder, cache, slot) -> cache.getRecipeCreatorCache().getRecipeCache().isHidden())
                .enabledState(state -> state.subKey("enabled").icon(PlayerHeadUtils.getViaURL("85e5bf255d5d7e521474318050ad304ab95b01a4af0bae15e5cd9c1993abcc98")).action((holder, cache, btn, slot, details) -> {
                    cache.getRecipeCreatorCache().getRecipeCache().setHidden(false);
                    return ButtonInteractionResult.cancel(true);
                })).disabledState(state -> state.subKey("disabled").icon(PlayerHeadUtils.getViaURL("ce9d49dd09ecee2a4996965514d6d301bf12870c688acb5999b6658e1dfdff85")).action((holder, cache, btn, slot, details) -> {
                    cache.getRecipeCreatorCache().getRecipeCache().setHidden(true);
                    return ButtonInteractionResult.cancel(true);
                })).register();

        getButtonBuilder().action(ClusterRecipeCreator.PRIORITY.getKey()).state(state -> state.icon(PlayerHeadUtils.getViaURL("b8ea57c7551c6ab33b8fed354b43df523f1e357c4b4f551143c34ddeac5b6c8d")).action((holder, cache, btn, slot, details) -> {
            RecipePriority priority = cache.getRecipeCreatorCache().getRecipeCache().getPriority();
            int order;
            order = priority.getOrder();
            if (order < 2) {
                order++;
            } else {
                order = -2;
            }
            cache.getRecipeCreatorCache().getRecipeCache().setPriority(RecipePriority.getByOrder(order));
            return ButtonInteractionResult.cancel(true);
        }).render((holder, cache, btn, slot, itemStack) -> {
            RecipePriority priority = cache.getRecipeCreatorCache().getRecipeCache().getPriority();
            if (priority != null) {
                return CallbackButtonRender.Result.of(Placeholder.parsed("pri", priority.name()));
            }
            return CallbackButtonRender.Result.of();
        })).register();
        registerSaveButtons();
    }

    private void registerSaveButtons() {
        getButtonBuilder().action(SAVE.getKey()).state(state -> state.icon(Material.WRITABLE_BOOK).action((holder, cache, btn, slot, details) -> {
            if (holder.getGuiHandler().getWindow() instanceof RecipeCreator && !cache.getRecipeCreatorCache().getRecipeCache().save(customCrafting, holder.getPlayer(), holder.getGuiHandler())) {
                getChat().sendMessage(holder.getPlayer(), translatedMsgKey("save.empty"));
            }
            return ButtonInteractionResult.cancel(true);
        }).render((holder, cache, btn, slot, itemStack) -> {
            NamespacedKey namespacedKey = cache.getRecipeCreatorCache().getRecipeCache().getKey();
            if (namespacedKey != null) {
                return CallbackButtonRender.Result.of(itemStack, Placeholder.unparsed("recipe_folder", namespacedKey.getKeyComponent().getFolder()), Placeholder.unparsed("recipe_key", namespacedKey.getKeyComponent().getObject()));
            }
            return CallbackButtonRender.Result.of(itemStack);
        })).register();
        getButtonBuilder().action(SAVE_AS.getKey()).state(state -> state.icon(Material.WRITABLE_BOOK).action((holder, cache, btn, slot, details) -> {
            if (holder.getGuiHandler().getWindow() instanceof RecipeCreator) {
                final var guiHandler = holder.getGuiHandler();
                final var player = holder.getPlayer();
                guiHandler.setChatTabComplete((guiHandler1, player1, args) -> {
                    List<String> results = new ArrayList<>();
                    if (args.length > 0) {
                        var registryRecipes = customCrafting.getRegistries().getRecipes();
                        if (args.length == 1) {
                            results.add("<folder>");
                            StringUtil.copyPartialMatches(args[0], registryRecipes.folders(NamespacedKeyUtils.NAMESPACE), results);
                        } else if (args.length == 2) {
                            results.add("<recipe_name>");
                            StringUtil.copyPartialMatches(args[1], registryRecipes.get(NamespacedKeyUtils.NAMESPACE, args[0]).stream().filter(recipe -> cache.getRecipeCreatorCache().getRecipeType().isInstance(recipe)).map(recipe -> NamespacedKeyUtils.getRelativeKeyObjPath(recipe.getNamespacedKey())).toList(), results);
                        }
                    }
                    Collections.sort(results);
                    return results;
                });
                openChat(guiHandler, translatedMsgKey("save.input"), (guiHandler1, player1, s, args) -> {
                    var namespacedKey = ChatUtils.getNamespacedKey(player1, s, args);
                    if (namespacedKey != null && !namespacedKey.getNamespace().equalsIgnoreCase("minecraft")) {
                        cache.getRecipeCreatorCache().getRecipeCache().setKey(namespacedKey);
                        if (!cache.getRecipeCreatorCache().getRecipeCache().save(customCrafting, player, guiHandler)) {
                            getChat().sendMessage(player, translatedMsgKey("save.empty"));
                            return false;
                        }
                    } else {
                        getChat().sendMessage(player, translatedMsgKey("save.key.invalid"));
                        return false;
                    }
                    return true;
                });
                getChat().sendMessage(player, getChat().translated("msg.input.wui_command"));
            }
            return ButtonInteractionResult.cancel(true);
        })).register();
    }
}
