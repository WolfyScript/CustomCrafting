package me.wolfyscript.customcrafting.gui.recipe_creator.recipe_creators;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.CCPlayerData;
import me.wolfyscript.customcrafting.data.cache.BrewingGUICache;
import me.wolfyscript.customcrafting.data.cache.potions.PotionEffects;
import me.wolfyscript.customcrafting.gui.recipe_creator.buttons.BrewingContainerButton;
import me.wolfyscript.customcrafting.gui.recipe_creator.buttons.BrewingOptionButton;
import me.wolfyscript.customcrafting.gui.recipe_creator.buttons.ExactMetaButton;
import me.wolfyscript.customcrafting.gui.recipe_creator.buttons.PriorityButton;
import me.wolfyscript.customcrafting.recipes.types.brewing.BrewingRecipe;
import me.wolfyscript.customcrafting.utils.PlayerUtil;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ChatInputButton;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.DummyButton;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ToggleButton;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.Pair;
import me.wolfyscript.utilities.util.inventory.InventoryUtils;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BrewingCreator extends RecipeCreator {

    public BrewingCreator(GuiCluster<CCCache> cluster, CustomCrafting customCrafting) {
        super(cluster, "brewing_stand", 54, customCrafting);
    }

    @Override
    public void onInit() {
        super.onInit();

        registerButton(new ExactMetaButton());
        registerButton(new PriorityButton());

        registerButton(new DummyButton<>("brewing_stand", Material.BREWING_STAND));
        registerButton(new BrewingContainerButton(0, customCrafting));
        registerButton(new BrewingContainerButton(1, customCrafting));

        registerButton(new DummyButton<>("allowed_items", Material.POTION));

        //Initialize simple option buttons
        registerButton(new ActionButton<>("duration_change", Material.LINGERING_POTION, (cache, guiHandler, player, guiInventory, i, event) ->  {
            BrewingRecipe brewingRecipe = cache.getBrewingRecipe();
            if(event instanceof InventoryClickEvent){
                if (((InventoryClickEvent) event).getClick().isRightClick()) {
                    //Change Mode
                    brewingRecipe.setDurationChange(0);
                    return true;
                }
                //Change Value
                openChat("duration_change", guiHandler, (guiHandler1, player1, s, strings) -> {
                    try {
                        int value = Integer.parseInt(s);
                        brewingRecipe.setDurationChange(value);
                    } catch (NumberFormatException ex) {
                        api.getChat().sendPlayerMessage(player1, "recipe_creator", "valid_number");
                    }
                    return false;
                });
            }
            return true;
        }, (hashMap, cache, guiHandler, player, inventory, itemStack, i, b) -> {
            hashMap.put("%value%", guiHandler.getCustomCache().getBrewingRecipe().getDurationChange());
            return itemStack;
        }));
        registerButton(new ActionButton<>("amplifier_change", Material.IRON_SWORD, (cache, guiHandler, player, inventory, i, event) -> {
            BrewingRecipe brewingRecipe = cache.getBrewingRecipe();
            if(event instanceof InventoryClickEvent){
                if (((InventoryClickEvent) event).getClick().isRightClick()) {
                    //Change Mode
                    brewingRecipe.setDurationChange(0);
                    return true;
                }
                //Change Value
                openChat("amplifier_change", guiHandler, (guiHandler1, player1, s, strings) -> {
                    try {
                        int value = Integer.parseInt(s);
                        brewingRecipe.setAmplifierChange(value);
                    } catch (NumberFormatException ex) {
                        api.getChat().sendPlayerMessage(player1, "recipe_creator", "valid_number");
                    }
                    return false;
                });
            }
            return true;
        }, (values, cache, guiHandler, player, inventory, itemStack, i, b) -> {
            values.put("%value%", cache.getBrewingRecipe().getAmplifierChange());
            return itemStack;
        }));

        registerButton(new ToggleButton<>("reset_effects", new ButtonState<>("reset_effects.enabled", Material.BARRIER, (cache, guiHandler, player, inventory, i, event) -> {
            cache.getBrewingRecipe().setResetEffects(false);
            return true;
        }), new ButtonState<>("reset_effects.disabled", Material.BARRIER, (cache, guiHandler, player, inventory, i, event) -> {
            cache.getBrewingRecipe().setResetEffects(true);
            return true;
        })));
        registerButton(new ActionButton<>("effect_color", Material.RED_DYE, (cache, guiHandler, player, inventory, i, event) -> {
            BrewingRecipe brewingRecipe = cache.getBrewingRecipe();
            if(event instanceof InventoryClickEvent){
                if (((InventoryClickEvent) event).getClick().isRightClick()) {
                    //Change Mode
                    brewingRecipe.setEffectColor(null);
                    return true;
                }
                //Change Value
                openChat("effect_color", guiHandler, (guiHandler1, player1, s, args) -> {
                    if (args.length > 2) {
                        try {
                            int red = Integer.parseInt(args[0]);
                            int green = Integer.parseInt(args[1]);
                            int blue = Integer.parseInt(args[2]);
                            brewingRecipe.setEffectColor(Color.fromRGB(red, green, blue));
                        } catch (NumberFormatException ex) {
                            api.getChat().sendPlayerMessage(player1, "recipe_creator", "valid_number");
                        }
                    }
                    return false;
                });
            }
            return true;
        }, (hashMap, cache, guiHandler, player, inventory, itemStack, i, b) -> {
            hashMap.put("%value%", cache.getBrewingRecipe().getEffectColor());
            return itemStack;
        }));

        registerButton(new BrewingOptionButton(Material.BARRIER, "effect_removals"));
        registerButton(new DummyButton<>("effect_removals.info", Material.POTION, (hashMap, cache, guiHandler, player, inventory, unused, i, b) -> {
            ItemStack itemStack = new ItemStack(Material.POTION);
            PotionMeta meta = (PotionMeta) itemStack.getItemMeta();
            cache.getBrewingRecipe().getEffectRemovals().forEach(potionEffectType -> meta.addCustomEffect(new PotionEffect(potionEffectType, 0, 0), true));
            ItemMeta unusedItemMeta = unused.getItemMeta();
            meta.setDisplayName(unusedItemMeta.getDisplayName());
            meta.setLore(unusedItemMeta.getLore());
            itemStack.setItemMeta(meta);
            return itemStack;
        }));
        registerButton(new ActionButton<>("effect_removals.add_type", Material.GREEN_CONCRETE, (cache, guiHandler, player, inventory, i, event) -> {
            BrewingRecipe brewingRecipe = cache.getBrewingRecipe();
            PotionEffects potionEffectCache = cache.getPotionEffectCache();
            potionEffectCache.setApplyPotionEffectType((cache1, potionEffectType) -> {
                if (!brewingRecipe.getEffectRemovals().contains(potionEffectType)) {
                    brewingRecipe.getEffectRemovals().add(potionEffectType);
                }
            });
            potionEffectCache.setOpenedFrom("recipe_creator", "brewing_stand");
            guiHandler.openWindow(new NamespacedKey("potion_creator", "potion_effect_type_selection"));
            return true;
        }));
        registerButton(new ActionButton<>("effect_removals.remove_type", Material.RED_CONCRETE, (cache, guiHandler, player, inventory, i, event) -> {
            BrewingRecipe brewingRecipe = cache.getBrewingRecipe();
            PotionEffects potionEffectCache = cache.getPotionEffectCache();
            potionEffectCache.setApplyPotionEffectType((cache1, potionEffectType) -> brewingRecipe.getEffectRemovals().remove(potionEffectType));
            potionEffectCache.setOpenedFrom("recipe_creator", "brewing_stand");
            guiHandler.openWindow(new NamespacedKey("potion_creator", "potion_effect_type_selection"));
            return true;
        }));

        registerButton(new BrewingOptionButton(Material.ITEM_FRAME, "result"));
        registerButton(new ActionButton<>("result.info", Material.BOOK));
        registerButton(new BrewingContainerButton(2, customCrafting));

        registerButton(new BrewingOptionButton(Material.ANVIL, "effect_additions"));
        registerButton(new DummyButton<>("effect_additions.info", Material.LINGERING_POTION, (hashMap, cache, guiHandler, player, inventory, unused, i, b) -> {
            ItemStack itemStack = new ItemStack(Material.LINGERING_POTION);
            PotionMeta meta = (PotionMeta) itemStack.getItemMeta();
            guiHandler.getCustomCache().getBrewingRecipe().getEffectAdditions().forEach((potionEffect, aBoolean) -> meta.addCustomEffect(potionEffect, true));
            ItemMeta unusedItemMeta = unused.getItemMeta();
            meta.setDisplayName(unusedItemMeta.getDisplayName());
            meta.setLore(unusedItemMeta.getLore());
            itemStack.setItemMeta(meta);
            return itemStack;
        }));
        registerButton(new ActionButton<>("effect_additions.potion_effect", Material.POTION, (cache, guiHandler, player, inventory, i, inventoryClickEvent) -> {
            PotionEffects potionEffectCache = cache.getPotionEffectCache();
            potionEffectCache.setApplyPotionEffect((potionEffectCache1, cache1, potionEffect) -> cache1.getBrewingGUICache().setPotionEffectAddition(potionEffect));
            potionEffectCache.setRecipePotionEffect(true);
            guiHandler.openWindow(new NamespacedKey("potion_creator", "potion_creator"));
            return true;
        }, (hashMap, cache, guiHandler, player, inventory, unused, i, b) -> {
            ItemStack itemStack = new ItemStack(Material.POTION);
            PotionMeta meta = (PotionMeta) itemStack.getItemMeta();
            ItemMeta unusedItemMeta = unused.getItemMeta();
            BrewingGUICache brewingGUICache = guiHandler.getCustomCache().getBrewingGUICache();
            if (brewingGUICache.getPotionEffectAddition() != null) {
                meta.addCustomEffect(brewingGUICache.getPotionEffectAddition(), true);
            }
            meta.setDisplayName(unusedItemMeta.getDisplayName());
            meta.setLore(unusedItemMeta.getLore());
            itemStack.setItemMeta(meta);
            return itemStack;
        }));
        registerButton(new ToggleButton<>("effect_additions.replace", new ButtonState<>("effect_additions.replace.enabled", Material.GREEN_CONCRETE, (cache, guiHandler, player, inventory, slot, event) -> {
            cache.getBrewingGUICache().setReplacePotionEffectAddition(false);
            return true;
        }), new ButtonState<>("effect_additions.replace.disabled", Material.RED_CONCRETE, (cache, guiHandler, player, inventory, slot, event) -> {
            cache.getBrewingGUICache().setReplacePotionEffectAddition(true);
            return true;
        })));
        registerButton(new ActionButton<>("effect_additions.apply", Material.BOOK, (cache, guiHandler, player, inventory, slot, event) -> {
            BrewingRecipe brewingRecipe = cache.getBrewingRecipe();
            BrewingGUICache brewingGUICache = cache.getBrewingGUICache();
            if (brewingGUICache.getPotionEffectAddition() != null) {
                PotionEffect potionEffectAddition = brewingGUICache.getPotionEffectAddition();
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
        }));
        registerButton(new ActionButton<>("effect_additions.remove", Material.RED_CONCRETE, (cache, guiHandler, player, inventory, i, event) -> {
            BrewingRecipe brewingRecipe = cache.getBrewingRecipe();
            PotionEffects potionEffectCache = cache.getPotionEffectCache();
            potionEffectCache.setApplyPotionEffectType((cache1, potionEffectType) -> brewingRecipe.getEffectAdditions().remove(potionEffectType));
            potionEffectCache.setOpenedFrom("recipe_creator", "brewing_stand");
            guiHandler.openWindow(new NamespacedKey("potion_creator", "potion_effect_type_selection"));
            return true;
        }));

        registerButton(new BrewingOptionButton(Material.ENCHANTED_BOOK, "effect_upgrades"));
        registerButton(new DummyButton<>("effect_upgrades.info", Material.LINGERING_POTION, (values, cache, guiHandler, player, inventory, unused, i, b) -> {
            ItemStack itemStack = new ItemStack(Material.LINGERING_POTION);
            PotionMeta meta = (PotionMeta) itemStack.getItemMeta();
            List<String> upgrades = new ArrayList<>();
            guiHandler.getCustomCache().getBrewingRecipe().getEffectUpgrades().forEach((effectType, pair) -> {
                meta.addCustomEffect(new PotionEffect(effectType, pair.getValue(), pair.getKey()), true);
                upgrades.add("§6" + effectType.getName() + " §7- §6a: §7" + pair.getKey() + "§6 d: §7" + pair.getValue());
            });
            ItemMeta unusedItemMeta = unused.getItemMeta();
            meta.setDisplayName(unusedItemMeta.getDisplayName());
            meta.setLore(unusedItemMeta.getLore());
            itemStack.setItemMeta(meta);
            values.put("%values%", upgrades);
            return itemStack;
        }));
        registerButton(new ActionButton<>("effect_upgrades.add_type", Material.POTION, (cache, guiHandler, player, inventory, i, event) -> {
            BrewingRecipe brewingRecipe = cache.getBrewingRecipe();
            BrewingGUICache guiCache = cache.getBrewingGUICache();
            PotionEffects potionEffectCache = cache.getPotionEffectCache();
            potionEffectCache.setApplyPotionEffectType((cache1, potionEffectType) -> {
                if (!brewingRecipe.getEffectUpgrades().containsKey(potionEffectType)) {
                    guiCache.setUpgradePotionEffectType(potionEffectType);
                }
            });
            potionEffectCache.setOpenedFrom("recipe_creator", "brewing_stand");
            guiHandler.openWindow(new NamespacedKey("potion_creator", "potion_effect_type_selection"));
            return true;
        }, (hashMap, cache, guiHandler, player, inventory, unused, i, b) -> {
            ItemStack itemStack = new ItemStack(Material.POTION);
            PotionMeta meta = (PotionMeta) itemStack.getItemMeta();
            ItemMeta unusedItemMeta = unused.getItemMeta();
            BrewingGUICache brewingGUICache = guiHandler.getCustomCache().getBrewingGUICache();
            if (brewingGUICache.getUpgradePotionEffectType() != null) {
                PotionEffectType type = brewingGUICache.getUpgradePotionEffectType();
                int amplifier = brewingGUICache.getUpgradeValues().getKey();
                int duration = brewingGUICache.getUpgradeValues().getValue();
                meta.addCustomEffect(new PotionEffect(type, duration, amplifier), true);
            }
            meta.setDisplayName(unusedItemMeta.getDisplayName());
            meta.setLore(unusedItemMeta.getLore());
            itemStack.setItemMeta(meta);
            return itemStack;
        }));
        registerButton(new ChatInputButton<>("effect_upgrades.amplifier", Material.BLAZE_POWDER, (hashMap, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            hashMap.put("%value%", guiHandler.getCustomCache().getBrewingGUICache().getUpgradeValues().getKey());
            return itemStack;
        }, (guiHandler, player, s, args) -> {
            int value;
            try {
                value = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                api.getChat().sendPlayerMessage(player, "recipe_creator", "valid_number");
                return true;
            }
            guiHandler.getCustomCache().getBrewingGUICache().getUpgradeValues().setKey(value);
            return false;
        }));
        registerButton(new ChatInputButton<>("effect_upgrades.duration", Material.BLAZE_POWDER, (hashMap, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            hashMap.put("%value%", guiHandler.getCustomCache().getBrewingGUICache().getUpgradeValues().getValue());
            return itemStack;
        }, (guiHandler, player, s, args) -> {
            int value;
            try {
                value = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                api.getChat().sendPlayerMessage(player, "recipe_creator", "valid_number");
                return true;
            }
            guiHandler.getCustomCache().getBrewingGUICache().getUpgradeValues().setValue(value);
            return false;
        }));
        registerButton(new ActionButton<>("effect_upgrades.apply", Material.BOOK, (cache, guiHandler, player, inventory, slot, event) -> {
            BrewingRecipe brewingRecipe = cache.getBrewingRecipe();
            BrewingGUICache brewingGUICache = cache.getBrewingGUICache();
            if (brewingGUICache.getUpgradePotionEffectType() != null) {
                PotionEffectType potionEffectAddition = brewingGUICache.getUpgradePotionEffectType();
                brewingRecipe.getEffectUpgrades().put(potionEffectAddition, brewingGUICache.getUpgradeValues());
            }
            brewingGUICache.setUpgradePotionEffectType(null);
            brewingGUICache.setUpgradeValues(new Pair<>(0, 0));
            return true;
        }));
        registerButton(new ActionButton<>("effect_upgrades.remove", Material.RED_CONCRETE, (cache, guiHandler, player, inventory, i, event) -> {
            BrewingRecipe brewingRecipe = cache.getBrewingRecipe();
            PotionEffects potionEffectCache = cache.getPotionEffectCache();
            potionEffectCache.setApplyPotionEffectType((cache1, potionEffectType) -> brewingRecipe.getEffectUpgrades().remove(potionEffectType));
            potionEffectCache.setOpenedFrom("recipe_creator", "brewing_stand");
            guiHandler.openWindow(new NamespacedKey("potion_creator", "potion_effect_type_selection"));
            return true;
        }));


        registerButton(new BrewingOptionButton(Material.BOOKSHELF, "required_effects"));

    }

    @Override
    public void onUpdateSync(GuiUpdate<CCCache> guiUpdate) {

    }


    @Override
    public void onUpdateAsync(GuiUpdate<CCCache> update) {
        super.onUpdateAsync(update);
        CCPlayerData data = PlayerUtil.getStore(update.getPlayer());
        update.setButton(0, "back");
        CCCache cache = update.getGuiHandler().getCustomCache();
        BrewingGUICache brewingGUICache = cache.getBrewingGUICache();
        BrewingRecipe brewingRecipe = cache.getBrewingRecipe();

        ((ToggleButton) getButton("hidden")).setState(update.getGuiHandler(), brewingRecipe.isHidden());

        update.setButton(1, "hidden");
        update.setButton(3, new NamespacedKey("recipe_creator", "conditions"));
        update.setButton(5, "priority");
        update.setButton(7, "exact_meta");

        update.setButton(9, "brewing.container_0");
        update.setButton(10, "brewing_stand");

        update.setButton(36, "brewing.container_1");
        update.setButton(37, "allowed_items");

        //Simple Options
        update.setButton(11, "duration_change");
        update.setButton(20, "amplifier_change");
        update.setButton(29, "effect_color");
        update.setButton(38, "reset_effects");
        NamespacedKey gray = new NamespacedKey("none", data.isDarkMode() ? "glass_gray" : "glass_white");
        update.setButton(12, gray);
        update.setButton(21, gray);
        update.setButton(30, gray);
        update.setButton(39, gray);


        update.setButton(13, "effect_removals.option");
        update.setButton(14, "result.option");
        update.setButton(15, "effect_additions.option");
        update.setButton(16, "effect_upgrades.option");
        update.setButton(17, "required_effects.option");

        switch (brewingGUICache.getOption()) {
            case "result":
                update.setButton(32, "brewing.container_2");
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


        }
        //requiredEffects
        //effectRemovals
        //effectAdditions
        //effectUpgrades
        //Result Items

        if (brewingRecipe.hasNamespacedKey()) {
            update.setButton(52, "save");
        }
        update.setButton(53, "save_as");
    }

    @Override
    public boolean validToSave(CCCache cache) {
        return !InventoryUtils.isCustomItemsListEmpty(cache.getBrewingRecipe().getIngredients());
    }
}
