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
import com.wolfyscript.utilities.bukkit.gui.GuiCluster;
import com.wolfyscript.utilities.bukkit.gui.GuiMenuComponent;
import com.wolfyscript.utilities.bukkit.gui.GuiUpdate;
import com.wolfyscript.utilities.bukkit.gui.callback.CallbackButtonRender;
import com.wolfyscript.utilities.tuple.Pair;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.CCPlayerData;
import me.wolfyscript.customcrafting.data.cache.BrewingGUICache;
import me.wolfyscript.customcrafting.data.cache.potions.PotionEffects;
import me.wolfyscript.customcrafting.data.cache.recipe_creator.RecipeCacheBrewing;
import me.wolfyscript.customcrafting.gui.potion_creator.ClusterPotionCreator;
import me.wolfyscript.customcrafting.utils.PlayerUtil;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class RecipeCreatorBrewing extends RecipeCreator {

    private static final String ALLOWED_ITEMS = "allowed_items";
    private static final String DURATION_CHANGE = "duration_change";
    private static final String AMPLIFIER_CHANGE = "amplifier_change";

    public RecipeCreatorBrewing(GuiCluster<CCCache> cluster, CustomCrafting customCrafting) {
        super(cluster, "brewing_stand", 54, customCrafting);
    }

    private static void registerBrewingOption(GuiMenuComponent.ButtonBuilder<CCCache> bB, Material material, String option) {
        bB.action(option + ".option").state(state -> state.icon(material).action((cache, guiHandler, player, guiInventory, button, i, event) -> {
            guiHandler.getCustomCache().getBrewingGUICache().setOption(option);
            return true;
        })).register();
    }

    @Override
    public void onInit() {
        super.onInit();
        var btnBld = getButtonBuilder();
        btnBld.dummy("brewing_stand").state(s -> s.icon(Material.BREWING_STAND)).register();
        registerButton(new ButtonRecipeIngredient(0));
        registerButton(new ButtonRecipeIngredient(1));

        ButtonBuilder<CCCache> bB = getButtonBuilder();

        bB.dummy(ALLOWED_ITEMS).state(state -> state.icon(Material.POTION)).register();

        registerBrewingOption(bB, Material.BARRIER, "effect_removals");
        registerBrewingOption(bB, Material.ITEM_FRAME, "result");
        registerBrewingOption(bB, Material.ANVIL, "effect_additions");
        registerBrewingOption(bB, Material.ENCHANTED_BOOK, "effect_upgrades");
        registerBrewingOption(bB, Material.BOOKSHELF, "required_effects");

        //Initialize simple option buttons
        bB.action(DURATION_CHANGE).state(state -> state.icon(Material.LINGERING_POTION).action((cache, guiHandler, player, guiInventory, button, i, event) -> {
            var brewingRecipe = cache.getRecipeCreatorCache().getBrewingCache();
            if (event instanceof InventoryClickEvent clickEvent) {
                if (clickEvent.getClick().isRightClick()) {
                    //Change Mode
                    brewingRecipe.setDurationChange(0);
                    return true;
                }
                //Change Value
                openChat(DURATION_CHANGE, guiHandler, (guiHandler1, player1, s, strings) -> {
                    try {
                        int value = Integer.parseInt(s);
                        brewingRecipe.setDurationChange(value);
                    } catch (NumberFormatException ex) {
                        api.getChat().sendKey(player1, getCluster(), "valid_number");
                    }
                    return false;
                });
            }
            return true;
        }).render((cache, guiHandler, player, guiInventory, button, itemStack, i) -> CallbackButtonRender.UpdateResult.of(Placeholder.unparsed("value", String.valueOf(cache.getRecipeCreatorCache().getBrewingCache().getDurationChange()))))).register();
        bB.action(AMPLIFIER_CHANGE).state(state -> state.icon(Material.IRON_SWORD).action((cache, guiHandler, player, guiInventory, button, i, event) -> {
            var brewingRecipe = cache.getRecipeCreatorCache().getBrewingCache();
            if (event instanceof InventoryClickEvent clickEvent) {
                if (clickEvent.getClick().isRightClick()) {
                    //Change Mode
                    brewingRecipe.setDurationChange(0);
                    return true;
                }
                //Change Value
                openChat(AMPLIFIER_CHANGE, guiHandler, (guiHandler1, player1, s, strings) -> {
                    try {
                        int value = Integer.parseInt(s);
                        brewingRecipe.setAmplifierChange(value);
                    } catch (NumberFormatException ex) {
                        api.getChat().sendKey(player1, getCluster(), "valid_number");
                    }
                    return false;
                });
            }
            return true;
        }).render((cache, guiHandler, player, inventory, itemStack, i, b) -> CallbackButtonRender.UpdateResult.of(Placeholder.unparsed("value", String.valueOf(cache.getRecipeCreatorCache().getBrewingCache().getAmplifierChange()))))).register();

        bB.toggle("reset_effects").enabledState(state -> state.subKey("enabled").icon(Material.BARRIER).action((cache, guiHandler, player, guiInventory, button, i, event) -> {
            cache.getRecipeCreatorCache().getBrewingCache().setResetEffects(false);
            return true;
        })).disabledState(state -> state.subKey("disabled").icon(Material.BARRIER).action((cache, guiHandler, player, guiInventory, button, i, event) -> {
            cache.getRecipeCreatorCache().getBrewingCache().setResetEffects(true);
            return true;
        })).register();
        bB.action("effect_color").state(state -> state.icon( Material.RED_DYE).action((cache, guiHandler, player, guiInventory, button, i, event) -> {
            var customRecipeBrewing = cache.getRecipeCreatorCache().getBrewingCache();
            if (event instanceof InventoryClickEvent clickEvent) {
                if (clickEvent.getClick().isRightClick()) {
                    //Change Mode
                    customRecipeBrewing.setEffectColor(null);
                    return true;
                }
                //Change Value
                openChat("effect_color", guiHandler, (guiHandler1, player1, s, args) -> {
                    if (args.length > 2) {
                        try {
                            int red = Integer.parseInt(args[0]);
                            int green = Integer.parseInt(args[1]);
                            int blue = Integer.parseInt(args[2]);
                            customRecipeBrewing.setEffectColor(Color.fromRGB(red, green, blue));
                        } catch (NumberFormatException ex) {
                            api.getChat().sendKey(player1, getCluster(), "valid_number");
                        }
                    }
                    return false;
                });
            }
            return true;
        }).render((cache, guiHandler, player, inventory, itemStack, i, b) -> CallbackButtonRender.UpdateResult.of(Placeholder.unparsed("value", cache.getRecipeCreatorCache().getBrewingCache().getEffectColor().toString())))).register();

        bB.dummy("effect_removals.info").state(state -> state.icon(Material.POTION).render((cache, guiHandler, player, inventory, btn, stack, b) -> {
            var itemStack = new ItemStack(Material.POTION);
            PotionMeta meta = (PotionMeta) itemStack.getItemMeta();
            cache.getRecipeCreatorCache().getBrewingCache().getEffectRemovals().forEach(potionEffectType -> meta.addCustomEffect(new PotionEffect(potionEffectType, 0, 0), true));
            var unusedItemMeta = stack.getItemMeta();
            meta.setDisplayName(unusedItemMeta.getDisplayName());
            meta.setLore(unusedItemMeta.getLore());
            itemStack.setItemMeta(meta);
            return CallbackButtonRender.UpdateResult.of(itemStack);
        })).register();
        bB.action("effect_removals.add_type").state(state -> state.icon(Material.GREEN_CONCRETE).action((cache, guiHandler, player, guiInventory, button, i, event) -> {
            var brewingRecipe = cache.getRecipeCreatorCache().getBrewingCache();
            PotionEffects potionEffectCache = cache.getPotionEffectCache();
            potionEffectCache.setApplyPotionEffectType((cache1, potionEffectType) -> {
                if (!brewingRecipe.getEffectRemovals().contains(potionEffectType)) {
                    brewingRecipe.getEffectRemovals().add(potionEffectType);
                }
            });
            potionEffectCache.setOpenedFrom(ClusterRecipeCreator.KEY, "brewing_stand");
            guiHandler.openWindow(ClusterPotionCreator.POTION_EFFECT_TYPE_SELECTION);
            return true;
        })).register();

        bB.action("effect_removals.remove_type").state(state -> state.icon(Material.RED_CONCRETE).action((cache, guiHandler, player, guiInventory, button, i, event) -> {
            var brewingRecipe = cache.getRecipeCreatorCache().getBrewingCache();
            PotionEffects potionEffectCache = cache.getPotionEffectCache();
            potionEffectCache.setApplyPotionEffectType((cache1, potionEffectType) -> brewingRecipe.getEffectRemovals().remove(potionEffectType));
            potionEffectCache.setOpenedFrom(ClusterRecipeCreator.KEY, "brewing_stand");
            guiHandler.openWindow(ClusterPotionCreator.POTION_EFFECT_TYPE_SELECTION);
            return true;
        })).register();

        bB.dummy("result.info").state(state -> state.icon(Material.BOOK)).register();
        registerButton(new ButtonRecipeResult());

        bB.dummy("effect_additions.info").state(state -> state.icon(Material.LINGERING_POTION).render((cache, guiHandler, player, guiInventory, button, stack, i) -> {
            var itemStack = new ItemStack(Material.LINGERING_POTION);
            PotionMeta meta = (PotionMeta) itemStack.getItemMeta();
            cache.getRecipeCreatorCache().getBrewingCache().getEffectAdditions().forEach((potionEffect, aBoolean) -> meta.addCustomEffect(potionEffect, true));
            var unusedItemMeta = stack.getItemMeta();
            meta.setDisplayName(unusedItemMeta.getDisplayName());
            meta.setLore(unusedItemMeta.getLore());
            itemStack.setItemMeta(meta);
            return CallbackButtonRender.UpdateResult.of(itemStack);
        })).register();
        bB.action("effect_additions.potion_effect").state(state -> state.icon(Material.POTION).action((cache, guiHandler, player, guiInventory, button, i, event) -> {
            PotionEffects potionEffectCache = cache.getPotionEffectCache();
            potionEffectCache.setApplyPotionEffect((potionEffectCache1, cache1, potionEffect) -> cache1.getBrewingGUICache().setPotionEffectAddition(potionEffect));
            potionEffectCache.setRecipePotionEffect(true);
            guiHandler.openWindow(ClusterPotionCreator.POTION_CREATOR);
            return true;
        }).render((cache, guiHandler, player, inventory, btn, stack, b) -> {
            var itemStack = new ItemStack(Material.POTION);
            PotionMeta meta = (PotionMeta) itemStack.getItemMeta();
            var unusedItemMeta = stack.getItemMeta();
            var brewingGUICache = guiHandler.getCustomCache().getBrewingGUICache();
            if (brewingGUICache.getPotionEffectAddition() != null) {
                meta.addCustomEffect(brewingGUICache.getPotionEffectAddition(), true);
            }
            meta.setDisplayName(unusedItemMeta.getDisplayName());
            meta.setLore(unusedItemMeta.getLore());
            itemStack.setItemMeta(meta);
            return CallbackButtonRender.UpdateResult.of(itemStack);
        })).register();
        bB.toggle("effect_additions.replace").enabledState(state -> state.subKey("enabled").icon(Material.GREEN_CONCRETE).action((cache, guiHandler, player, guiInventory, button, i, event) -> {
            cache.getBrewingGUICache().setReplacePotionEffectAddition(false);
            return true;
        })).disabledState(state -> state.subKey("disabled").icon(Material.RED_CONCRETE).action((cache, guiHandler, player, guiInventory, button, i, event) -> {
            cache.getBrewingGUICache().setReplacePotionEffectAddition(true);
            return true;
        })).register();
        bB.action("effect_additions.apply").state(state -> state.icon(Material.BOOK).action((cache, guiHandler, player, guiInventory, button, i, event) -> {
            var brewingRecipe = cache.getRecipeCreatorCache().getBrewingCache();
            var brewingGUICache = cache.getBrewingGUICache();
            if (brewingGUICache.getPotionEffectAddition() != null) {
                var potionEffectAddition = brewingGUICache.getPotionEffectAddition();
                Map<PotionEffect, Boolean> additions = new HashMap<>(brewingRecipe.getEffectAdditions());
                brewingRecipe.getEffectAdditions().keySet().forEach(potionEffect -> {
                    if (potionEffectAddition.getType().equals(potionEffect.getType())) {
                        additions.remove(potionEffect);
                    }
                });
                additions.put(brewingGUICache.getPotionEffectAddition(), brewingGUICache.isReplacePotionEffectAddition());
                brewingRecipe.setEffectAdditions(additions);
            }
            brewingGUICache.setPotionEffectAddition(null);
            brewingGUICache.setReplacePotionEffectAddition(false);
            return true;
        })).register();
        bB.action("effect_additions.remove").state(state -> state.icon(Material.RED_CONCRETE).action((cache, guiHandler, player, guiInventory, button, i, event) -> {
            var brewingRecipe = cache.getRecipeCreatorCache().getBrewingCache();
            var potionEffectCache = cache.getPotionEffectCache();
            potionEffectCache.setApplyPotionEffectType((cache1, potionEffectType) -> brewingRecipe.getEffectAdditions().keySet().removeIf(potionEffect -> potionEffect.getType().equals(potionEffectType)));
            potionEffectCache.setOpenedFrom(ClusterRecipeCreator.KEY, "brewing_stand");
            guiHandler.openWindow(ClusterPotionCreator.POTION_EFFECT_TYPE_SELECTION);
            return true;
        })).register();

        bB.dummy("effect_upgrades.info").state(state -> state.icon(Material.LINGERING_POTION).render((cache, guiHandler, player, inventory, btn, stack, b) -> {
            var itemStack = new ItemStack(Material.LINGERING_POTION);
            PotionMeta meta = (PotionMeta) itemStack.getItemMeta();
            List<String> upgrades = new ArrayList<>();
            cache.getRecipeCreatorCache().getBrewingCache().getEffectUpgrades().forEach((effectType, pair) -> {
                meta.addCustomEffect(new PotionEffect(effectType, pair.getValue(), pair.getKey()), true);
                upgrades.add("§6" + effectType.getName() + " §7- §6a: §7" + pair.getKey() + "§6 d: §7" + pair.getValue());
            });
            var unusedItemMeta = stack.getItemMeta();
            meta.setDisplayName(unusedItemMeta.getDisplayName());
            meta.setLore(unusedItemMeta.getLore());
            itemStack.setItemMeta(meta);
            // TODO: values.put("%values%", upgrades);
            return CallbackButtonRender.UpdateResult.of(itemStack);
        })).register();
        bB.action("effect_upgrades.add_type").state(state -> state.icon(Material.POTION).action((cache, guiHandler, player, guiInventory, button, i, event) -> {
            var brewingRecipe = cache.getRecipeCreatorCache().getBrewingCache();
            var guiCache = cache.getBrewingGUICache();
            var potionEffectCache = cache.getPotionEffectCache();
            potionEffectCache.setApplyPotionEffectType((cache1, potionEffectType) -> {
                if (!brewingRecipe.getEffectUpgrades().containsKey(potionEffectType)) {
                    guiCache.setUpgradePotionEffectType(potionEffectType);
                }
            });
            potionEffectCache.setOpenedFrom(ClusterRecipeCreator.KEY, "brewing_stand");
            guiHandler.openWindow(ClusterPotionCreator.POTION_EFFECT_TYPE_SELECTION);
            return true;
        }).render((cache, guiHandler, player, inventory, btn, stack, b) -> {
            var itemStack = new ItemStack(Material.POTION);
            PotionMeta meta = (PotionMeta) itemStack.getItemMeta();
            var unusedItemMeta = stack.getItemMeta();
            var brewingGUICache = cache.getBrewingGUICache();
            if (brewingGUICache.getUpgradePotionEffectType() != null) {
                PotionEffectType type = brewingGUICache.getUpgradePotionEffectType();
                int amplifier = brewingGUICache.getUpgradeValues().getKey();
                int duration = brewingGUICache.getUpgradeValues().getValue();
                meta.addCustomEffect(new PotionEffect(type, duration, amplifier), true);
            }
            meta.setDisplayName(unusedItemMeta.getDisplayName());
            meta.setLore(unusedItemMeta.getLore());
            itemStack.setItemMeta(meta);
            return CallbackButtonRender.UpdateResult.of(itemStack);
        })).register();
        bB.chatInput("effect_upgrades.amplifier").state(state -> state.icon(Material.BLAZE_POWDER).render((cache, guiHandler, player, inventory, btn, stack, slot) -> {
            return CallbackButtonRender.UpdateResult.of(Placeholder.unparsed("value", String.valueOf(guiHandler.getCustomCache().getBrewingGUICache().getUpgradeValues().getKey())));
        })).inputAction((guiHandler, player, s, args) -> {
            int value;
            try {
                value = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                api.getChat().sendKey(player, getCluster(), "valid_number");
                return true;
            }
            guiHandler.getCustomCache().getBrewingGUICache().getUpgradeValues().setKey(value);
            return false;
        }).register();
        bB.chatInput("effect_upgrades.duration").state(state -> state.icon(Material.BLAZE_POWDER).render((cache, guiHandler, player, inventory, btn, stack, slot) -> {
            return CallbackButtonRender.UpdateResult.of(Placeholder.unparsed("value", String.valueOf(guiHandler.getCustomCache().getBrewingGUICache().getUpgradeValues().getValue())));
        })).inputAction((guiHandler, player, s, args) -> {
            int value;
            try {
                value = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                api.getChat().sendKey(player, getCluster(), "valid_number");
                return true;
            }
            guiHandler.getCustomCache().getBrewingGUICache().getUpgradeValues().setValue(value);
            return false;
        }).register();
        bB.action("effect_upgrades.apply").state(state -> state.icon(Material.BOOK).action((cache, guiHandler, player, guiInventory, button, i, event) -> {
            var brewingRecipe = cache.getRecipeCreatorCache().getBrewingCache();
            var brewingGUICache = cache.getBrewingGUICache();
            if (brewingGUICache.getUpgradePotionEffectType() != null) {
                var potionEffectAddition = brewingGUICache.getUpgradePotionEffectType();
                brewingRecipe.getEffectUpgrades().put(potionEffectAddition, brewingGUICache.getUpgradeValues());
            }
            brewingGUICache.setUpgradePotionEffectType(null);
            brewingGUICache.setUpgradeValues(new Pair<>(0, 0));
            return true;
        })).register();
        bB.action("effect_upgrades.remove").state(state -> state.icon(Material.RED_CONCRETE).action((cache, guiHandler, player, guiInventory, button, i, event) -> {
            var brewingRecipe = cache.getRecipeCreatorCache().getBrewingCache();
            PotionEffects potionEffectCache = cache.getPotionEffectCache();
            potionEffectCache.setApplyPotionEffectType((cache1, potionEffectType) -> brewingRecipe.getEffectUpgrades().remove(potionEffectType));
            potionEffectCache.setOpenedFrom(ClusterRecipeCreator.KEY, "brewing_stand");
            guiHandler.openWindow(ClusterPotionCreator.POTION_EFFECT_TYPE_SELECTION);
            return true;
        })).register();
    }

    @Override
    public void onUpdateAsync(GuiUpdate<CCCache> update) {
        super.onUpdateAsync(update);
        CCPlayerData data = PlayerUtil.getStore(update.getPlayer());
        update.setButton(0, BACK);
        CCCache cache = update.getGuiHandler().getCustomCache();
        var brewingGUICache = cache.getBrewingGUICache();
        var brewingRecipe = cache.getRecipeCreatorCache().getBrewingCache();

        update.setButton(1, ClusterRecipeCreator.HIDDEN);
        update.setButton(3, ClusterRecipeCreator.CONDITIONS);
        update.setButton(5, ClusterRecipeCreator.PRIORITY);
        update.setButton(7, ClusterRecipeCreator.EXACT_META);

        update.setButton(9, "recipe.ingredient_0");
        update.setButton(10, "brewing_stand");

        update.setButton(36, "recipe.ingredient_1");
        update.setButton(37, ALLOWED_ITEMS);

        //Simple Options
        update.setButton(11, DURATION_CHANGE);
        update.setButton(20, AMPLIFIER_CHANGE);
        update.setButton(29, "effect_color");
        update.setButton(38, "reset_effects");
        NamespacedKey gray = data.getLightBackground();
        update.setButton(12, gray);
        update.setButton(21, gray);
        update.setButton(30, gray);
        update.setButton(39, gray);

        update.setButton(13, "effect_removals.option");
        update.setButton(14, "result.option");
        update.setButton(15, "effect_additions.option");
        update.setButton(16, "effect_upgrades.option");
        update.setButton(17, "required_effects.option");

        switch (getOption(brewingGUICache, brewingRecipe)) {
            case "result":
                update.setButton(32, "recipe.result");
                update.setButton(34, "result.info");
                break;
            case "effect_removals":
                update.setButton(32, "effect_removals.info");
                update.setButton(34, "effect_removals.add_type");
                update.setButton(35, "effect_removals.remove_type");
                break;
            case "effect_additions":
                update.setButton(22, "effect_additions.info");
                update.setButton(32, "effect_additions.potion_effect");
                update.setButton(34, "effect_additions.replace");
                update.setButton(40, "effect_additions.apply");
                update.setButton(41, "effect_additions.remove");
                break;
            case "effect_upgrades":
                update.setButton(22, "effect_upgrades.info");
                update.setButton(33, "effect_upgrades.add_type");
                update.setButton(34, "effect_upgrades.amplifier");
                update.setButton(35, "effect_upgrades.duration");
                update.setButton(40, "effect_upgrades.apply");
                update.setButton(41, "effect_upgrades.remove");
                break;
            default:
                //No sub-menu selected!
        }
        //requiredEffects
        //effectRemovals
        //effectAdditions
        //effectUpgrades
        //Result Items

        if (brewingRecipe.isSaved()) {
            update.setButton(52, ClusterRecipeCreator.SAVE);
        }
        update.setButton(53, ClusterRecipeCreator.SAVE_AS);
    }

    private static String parseOption(RecipeCacheBrewing brewingRecipe) {
        if (!brewingRecipe.getResult().isEmpty()) {
            return "result";
        }

        if (!brewingRecipe.getEffectRemovals().isEmpty()) {
            return "effect_removals";
        }

        if (!brewingRecipe.getEffectAdditions().isEmpty()) {
            return "effect_additions";
        }

        if (!brewingRecipe.getEffectUpgrades().isEmpty()) {
            return "effect_upgrades";
        }

        if (!brewingRecipe.getRequiredEffects().isEmpty()) {
            return "required_effects";
        }

        return "";
    }

    private String getOption(BrewingGUICache brewingGUICache, RecipeCacheBrewing brewingRecipe) {
        // Ignore cache and parse option from the saved recipe if we're loading it fresh
        if (brewingRecipe.isSaved() && brewingGUICache.getParsedOptionKey() != brewingRecipe.getKey()) {
            String option = parseOption(brewingRecipe);

            brewingGUICache.setOption(option);
            brewingGUICache.setParsedOptionKey(brewingRecipe.getKey());

            return option;
        }

        return brewingGUICache.getOption();
    }
}
