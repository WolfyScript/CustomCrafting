package me.wolfyscript.customcrafting.gui.item_creator;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.custom_data.EliteWorkbenchData;
import me.wolfyscript.customcrafting.configs.custom_data.RecipeBookData;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.CCPlayerData;
import me.wolfyscript.customcrafting.data.cache.items.Items;
import me.wolfyscript.customcrafting.data.cache.items.ItemsButtonAction;
import me.wolfyscript.customcrafting.data.cache.potions.PotionEffects;
import me.wolfyscript.customcrafting.gui.CCWindow;
import me.wolfyscript.customcrafting.gui.item_creator.buttons.*;
import me.wolfyscript.customcrafting.utils.ChatUtils;
import me.wolfyscript.customcrafting.utils.PlayerUtil;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.custom_items.meta.MetaSettings;
import me.wolfyscript.utilities.api.inventory.custom_items.references.*;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.*;
import me.wolfyscript.utilities.util.Pair;
import me.wolfyscript.utilities.util.Registry;
import me.wolfyscript.utilities.util.chat.ChatColor;
import me.wolfyscript.utilities.util.inventory.PlayerHeadUtils;
import me.wolfyscript.utilities.util.inventory.item_builder.ItemBuilder;
import me.wolfyscript.utilities.util.particles.ParticleLocation;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class ItemCreator extends CCWindow {

    private static final MetaSettings dummyMetaSettings = new MetaSettings();

    public ItemCreator(GuiCluster<CCCache> cluster, CustomCrafting customCrafting) {
        super(cluster, "main_menu", 54, customCrafting);
    }

    @Override
    public void onInit() {
        registerButton(new ActionButton<>("back", new ButtonState<>("none", "back", PlayerHeadUtils.getViaValue("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODY0Zjc3OWE4ZTNmZmEyMzExNDNmYTY5Yjk2YjE0ZWUzNWMxNmQ2NjllMTljNzVmZDFhN2RhNGJmMzA2YyJ9fX0="),
                (cache, guiHandler, player, inventory, i, event) -> {
                    if (cache.getItems().isRecipeItem()) {
                        guiHandler.openCluster("recipe_creator");
                    } else {
                        guiHandler.openCluster("none");
                    }
                    return true;
                })));
        registerButton(new ItemInputButton<>("item_input", new ButtonState<>("", Material.AIR,
                (cache, guiHandler, player, inventory, slot, event) -> false,
                (cache, guiHandler, player, inventory, item, slot, event) -> {
                    Items items = cache.getItems();
                    //-------------TODO: Experimental
                    /*
                    if (event.getAction().name().startsWith("PICKUP") || event.getAction().equals(InventoryAction.COLLECT_TO_CURSOR) || event.getAction().equals(InventoryAction.CLONE_STACK)) {
                        ItemStack cursor = event.getView().getCursor();
                        if (!ItemUtils.isAirOrNull(cursor) && items.isSaved()) {
                            CustomItem customItem = CustomItems.getCustomItem(items.getNamespacedKey());
                            if (!ItemUtils.isAirOrNull(customItem)) {
                                event.getView().setCursor(customItem.create(cursor.getAmount()));
                            }
                        }
                    }
                    //*/
                    //---------------------------------------
                    CustomItem customItem = CustomItem.getReferenceByItemStack(item != null ? item : new ItemStack(Material.AIR));
                    items.setItem(customItem);
                    ((ToggleButton<CCCache>) guiHandler.getWindow().getButton("unbreakable")).setState(guiHandler, (item != null && !item.getType().equals(Material.AIR)) && item.getItemMeta().isUnbreakable());
                }, null,
                (hashMap, cache, guiHandler, player, guiInventory, itemStack, i, b) -> guiHandler.getCustomCache().getItems().getItem().getItemStack())));

        registerButton(new ActionButton<>("save_item", Material.WRITABLE_BOOK, (cache, guiHandler, player, inventory, i, event) -> {
            Items items = cache.getItems();
            if (!items.getItem().getItemStack().getType().equals(Material.AIR)) {
                sendMessage(player, "save.input.line1");
                openChat("save.input.line2", guiHandler, (guiHandler1, player1, s, args) -> {
                    me.wolfyscript.utilities.util.NamespacedKey namespacedKey = ChatUtils.getNamespacedKey(player1, s, args);
                    if (namespacedKey != null) {
                        CustomItem customItem = items.getItem();
                        if (customItem.getApiReference() instanceof WolfyUtilitiesRef && ((WolfyUtilitiesRef) customItem.getApiReference()).getNamespacedKey().equals(namespacedKey)) {
                            api.getChat().sendMessage(player, "&cError saving item! Cannot override original CustomItem &4" + namespacedKey + "&c! Save it under another NamespacedKey or Edit the original!");
                            return true;
                        }
                        customCrafting.saveItem(namespacedKey, items.getItem());
                        items.setSaved(true);
                        items.setNamespacedKey(namespacedKey);
                        sendMessage(player, "save.success");
                        api.getChat().sendMessage(player1, "&6" + namespacedKey.getNamespace() + "/items/" + namespacedKey.getKey());
                        return false;
                    }
                    return true;
                });
            }
            return true;
        }));

        registerButton(new ActionButton<>("apply_item", Material.GREEN_CONCRETE, (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
            if (!items.getItem().getItemStack().getType().equals(Material.AIR)) {
                CustomItem customItem = cache.getItems().getItem();
                if (items.isSaved()) {
                    customCrafting.saveItem(items.getNamespacedKey(), customItem);
                    customItem = Registry.CUSTOM_ITEMS.get(items.getNamespacedKey());
                }
                cache.applyItem(customItem);
                guiHandler.openCluster("recipe_creator");
            }
            return true;
        }));

        registerButton(new ActionButton<>("page_next", PlayerHeadUtils.getViaURL("c86185b1d519ade585f184c34f3f3e20bb641deb879e81378e4eaf209287"), (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
            items.setPage(items.getPage() + 1);
            return true;
        }));
        registerButton(new ActionButton<>("page_previous", PlayerHeadUtils.getViaURL("ad73cf66d31b83cd8b8644c15958c1b73c8d97323b801170c1d8864bb6a846d"), (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
            if (items.getPage() > 0) {
                items.setPage(items.getPage() - 1);
            }
            return true;
        }));

        registerButton(new DummyButton<>("reference.wolfyutilities", Material.CRAFTING_TABLE, (hashMap, cache, guiHandler, player, inventory, itemStack, i, b) -> {
            hashMap.put("%item_key%", ((WolfyUtilitiesRef) guiHandler.getCustomCache().getItems().getItem().getApiReference()).getNamespacedKey().toString());
            return itemStack;
        }));
        registerButton(new DummyButton<>("reference.oraxen", Material.DIAMOND, (hashMap, cache, guiHandler, player, inventory, itemStack, i, b) -> {
            hashMap.put("%item_key%", ((OraxenRef) guiHandler.getCustomCache().getItems().getItem().getApiReference()).getItemID());
            return itemStack;
        }));
        registerButton(new DummyButton<>("reference.itemsadder", Material.GRASS_BLOCK, (hashMap, cache, guiHandler, player, inventory, itemStack, i, b) -> {
            hashMap.put("%item_key%", ((ItemsAdderRef) guiHandler.getCustomCache().getItems().getItem().getApiReference()).getItemName());
            return itemStack;
        }));
        registerButton(new DummyButton<>("reference.mythicmobs", Material.WITHER_SKELETON_SKULL, (hashMap, cache, guiHandler, player, inventory, itemStack, i, b) -> {
            hashMap.put("%item_key%", ((ItemsAdderRef) guiHandler.getCustomCache().getItems().getItem().getApiReference()).getItemName());
            return itemStack;
        }));

        //DISPLAY NAME SETTINGS
        registerButton(new OptionButton(Material.NAME_TAG, "display_name"));
        registerButton(new ChatInputButton<>("display_name.set", Material.GREEN_CONCRETE, (guiHandler, player, s, strings) -> {
            guiHandler.getCustomCache().getItems().getItem().setDisplayName(ChatColor.convert(s));
            return false;
        }));
        registerButton(new ActionButton<>("display_name.remove", Material.RED_CONCRETE, (cache, guiHandler, player, inventory, i, event) -> {
            guiHandler.getCustomCache().getItems().getItem().setDisplayName(null);
            return true;
        }));

        //ENCHANT SETTINGS
        registerButton(new OptionButton(Material.ENCHANTED_BOOK, "enchantments"));
        {
            registerButton(new ChatInputButton<>("enchantments.add", Material.ENCHANTED_BOOK, (guiHandler, player, s, args) -> {
                if (args.length > 1) {
                    int level;
                    try {
                        level = Integer.parseInt(args[args.length - 1]);
                    } catch (NumberFormatException ex) {
                        sendMessage(player, "enchant.invalid_lvl");
                        return true;
                    }
                    Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(args[0].toLowerCase(Locale.ROOT).replace(' ', '_')));
                    if (enchantment != null) {
                        guiHandler.getCustomCache().getItems().getItem().addUnsafeEnchantment(enchantment, level);
                    } else {
                        api.getChat().sendKey(player, new me.wolfyscript.utilities.util.NamespacedKey("none", "item_creator"), "enchant.invalid_enchant", new Pair<>("%ENCHANT%", args[0]));
                        return true;
                    }
                } else {
                    sendMessage(player, "enchant.no_lvl");
                    return true;
                }
                return false;
            }));
            registerButton(new ChatInputButton<>("enchantments.remove", Material.RED_CONCRETE, (guiHandler, player, s, args) -> {
                Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(args[0].toLowerCase(Locale.ROOT).replace(' ', '_')));
                if (enchantment != null) {
                    guiHandler.getCustomCache().getItems().getItem().removeEnchantment(enchantment);
                } else {
                    api.getChat().sendKey(player, new me.wolfyscript.utilities.util.NamespacedKey("none", "item_creator"), "enchant.invalid_enchant", new Pair<>("%ENCHANT%", args[0]));
                    return true;
                }
                return false;
            }));
        }

        //LORE SETTINGS
        registerButton(new OptionButton(Material.WRITABLE_BOOK, "lore"));
        {
            registerButton(new ChatInputButton<>("lore.add", Material.WRITABLE_BOOK, (guiHandler, player, s, strings) -> {
                guiHandler.getCustomCache().getItems().getItem().addLoreLine(s.equals("&empty") ? "" : ChatColor.convert(s));
                return false;
            }));
            registerButton(new ActionButton<>("lore.remove", Material.WRITTEN_BOOK, (cache, guiHandler, player, inventory, i, event) -> {
                ChatUtils.sendLoreManager(player);
                guiHandler.close();
                return true;
            }));
        }

        //FLAGS SETTINGS
        registerButton(new OptionButton(Material.WRITTEN_BOOK, "flags"));
        {
            registerButton(new ItemFlagsToggleButton("enchants", ItemFlag.HIDE_ENCHANTS, Material.ENCHANTING_TABLE));
            registerButton(new ItemFlagsToggleButton("attributes", ItemFlag.HIDE_ATTRIBUTES, Material.ENCHANTED_GOLDEN_APPLE));
            registerButton(new ItemFlagsToggleButton("unbreakable", ItemFlag.HIDE_UNBREAKABLE, Material.BEDROCK));
            registerButton(new ItemFlagsToggleButton("destroys", ItemFlag.HIDE_DESTROYS, Material.TNT));
            registerButton(new ItemFlagsToggleButton("placed_on", ItemFlag.HIDE_PLACED_ON, Material.GRASS_BLOCK));
            registerButton(new ItemFlagsToggleButton("potion_effects", ItemFlag.HIDE_POTION_EFFECTS, Material.POTION));
        }

        //attributes_modifiers SETTINGS
        registerButton(new OptionButton(Material.ENCHANTED_GOLDEN_APPLE, "attribute"));
        {
            registerButton(new AttributeCategoryButton("generic_max_health", Material.ENCHANTED_GOLDEN_APPLE));
            registerButton(new AttributeCategoryButton("generic_follow_range", Material.ENDER_EYE));
            registerButton(new AttributeCategoryButton("generic_knockback_resistance", Material.STICK));
            registerButton(new AttributeCategoryButton("generic_movement_speed", Material.IRON_BOOTS));
            registerButton(new AttributeCategoryButton("generic_flying_speed", Material.FIREWORK_ROCKET));
            registerButton(new AttributeCategoryButton("generic_attack_damage", Material.DIAMOND_SWORD));
            registerButton(new AttributeCategoryButton("generic_attack_speed", Material.DIAMOND_AXE));
            registerButton(new AttributeCategoryButton("generic_armor", Material.CHAINMAIL_CHESTPLATE));
            registerButton(new AttributeCategoryButton("generic_armor_toughness", Material.DIAMOND_CHESTPLATE));
            registerButton(new AttributeCategoryButton("generic_luck", Material.NETHER_STAR));
            registerButton(new AttributeCategoryButton("horse_jump_strength", Material.DIAMOND_HORSE_ARMOR));
            registerButton(new AttributeCategoryButton("zombie_spawn_reinforcements", Material.ZOMBIE_HEAD));

            registerButton(new AttributeModeButton(AttributeModifier.Operation.ADD_NUMBER, "60b55f74681c68283a1c1ce51f1c83b52e2971c91ee34efcb598df3990a7e7"));
            registerButton(new AttributeModeButton(AttributeModifier.Operation.ADD_SCALAR, "57b1791bdc46d8a5c51729e8982fd439bb40513f64b5babee93294efc1c7"));
            registerButton(new AttributeModeButton(AttributeModifier.Operation.MULTIPLY_SCALAR_1, "a9f27d54ec5552c2ed8f8e1917e8a21cb98814cbb4bc3643c2f561f9e1e69f"));

            registerButton(new AttributeSlotButton(EquipmentSlot.HAND, Material.IRON_SWORD));
            registerButton(new AttributeSlotButton(EquipmentSlot.OFF_HAND, Material.SHIELD));
            registerButton(new AttributeSlotButton(EquipmentSlot.FEET, Material.IRON_BOOTS));
            registerButton(new AttributeSlotButton(EquipmentSlot.LEGS, Material.IRON_LEGGINGS));
            registerButton(new AttributeSlotButton(EquipmentSlot.CHEST, Material.IRON_CHESTPLATE));
            registerButton(new AttributeSlotButton(EquipmentSlot.HEAD, Material.IRON_HELMET));
            registerButton(new ChatInputButton<>("attribute.set_amount", PlayerHeadUtils.getViaURL("461c8febcac21b9f63d87f9fd933589fe6468e93aa81cfcf5e52a4322e16e6"), (values, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
                values.put("%NUMBER%", guiHandler.getCustomCache().getItems().getAttribAmount());
                return itemStack;
            }, (guiHandler, player, s, args) -> {
                try {
                    guiHandler.getCustomCache().getItems().setAttribAmount(Double.parseDouble(args[0]));
                } catch (NumberFormatException e) {
                    api.getChat().sendKey(player, new me.wolfyscript.utilities.util.NamespacedKey("item_creator", "main_menu"), "attribute.amount.error");
                    return true;
                }
                return false;
            }));
            registerButton(new ChatInputButton<>("attribute.set_name", Material.NAME_TAG, (values, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
                values.put("%NAME%", guiHandler.getCustomCache().getItems().getAttributeName());
                return itemStack;
            }, (guiHandler, player, s, strings) -> {
                guiHandler.getCustomCache().getItems().setAttributeName(strings[0]);
                return false;
            }));
            registerButton(new ChatInputButton<>("attribute.set_uuid", Material.TRIPWIRE_HOOK, (values, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
                values.put("%UUID%", guiHandler.getCustomCache().getItems().getAttributeUUID());
                return itemStack;
            }, (guiHandler, player, s, strings) -> {
                try {
                    UUID uuid = UUID.fromString(strings[0]);
                    guiHandler.getCustomCache().getItems().setAttributeUUID(uuid.toString());
                } catch (IllegalArgumentException ex) {
                    api.getChat().sendKey(player, getNamespacedKey(), "attribute.uuid.error.line1", new Pair<>("%UUID%", strings[0]));
                    api.getChat().sendKey(player, getNamespacedKey(), "attribute.uuid.error.line2");
                    return true;
                }
                return false;
            }));
            registerButton(new ActionButton<>("attribute.save", Material.GREEN_CONCRETE, (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
                ItemMeta itemMeta = items.getItem().getItemMeta();
                itemMeta.addAttributeModifier(Attribute.valueOf(cache.getSubSetting().split("\\.")[1].toUpperCase(Locale.ROOT)), items.getAttributeModifier());
                items.getItem().setItemMeta(itemMeta);
                return true;
            }));
            registerButton(new ActionButton<>("attribute.delete", Material.RED_CONCRETE, (cache, guiHandler, player, inventory, i, event) -> {
                ChatUtils.sendAttributeModifierManager(player);
                guiHandler.close();
                return true;
            }));
        }

        //PLAYER_HEAD SETTINGS
        registerButton(new OptionButton(Material.PLAYER_HEAD, "player_head"));
        {
            registerButton(new ItemInputButton<>("player_head.texture.input", new ButtonState<>("", Material.AIR, (cache, guiHandler, player, inventory, i, event) -> {
                if(event instanceof InventoryClickEvent){
                    return ((InventoryClickEvent) event).getCurrentItem().getType().equals(Material.PLAYER_HEAD);
                }
                return true;
            })));
            registerButton(new ActionButton<>("player_head.texture.apply", Material.GREEN_CONCRETE, (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
                if (inventory.getItem(38) != null && inventory.getItem(38).getType().equals(Material.PLAYER_HEAD)) {
                    items.getItem().setPlayerHeadValue(new ItemBuilder(inventory.getItem(38)).getPlayerHeadValue());
                }
                return true;
            }));
            registerButton(new ChatInputButton<>("player_head.owner", Material.NAME_TAG, (guiHandler, player, s, args) -> {
                ItemMeta itemMeta = guiHandler.getCustomCache().getItems().getItem().getItemMeta();
                if (!(itemMeta instanceof SkullMeta)) {
                    return true;
                }
                try {
                    UUID uuid = UUID.fromString(args[0]);
                    ((SkullMeta) itemMeta).setOwningPlayer(Bukkit.getOfflinePlayer(uuid));
                    guiHandler.getCustomCache().getItems().getItem().setItemMeta(itemMeta);
                } catch (IllegalArgumentException e) {
                    return true;
                }
                return false;
            }));
        }

        //POTION SETTINGS
        registerButton(new OptionButton(Material.POTION, "potion"));
        {
            registerButton(new ActionButton<>("potion.add", PlayerHeadUtils.getViaURL("9a2d891c6ae9f6baa040d736ab84d48344bb6b70d7f1a280dd12cbac4d777"), (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
                cache.getPotionEffectCache().setApplyPotionEffect((potionEffectCache1, cache1, potionEffect) -> {
                    ItemMeta itemMeta = items.getItem().getItemMeta();
                    if (itemMeta instanceof PotionMeta) {
                        ((PotionMeta) itemMeta).addCustomEffect(potionEffect, true);
                    }
                    items.getItem().setItemMeta(itemMeta);
                });
                cache.getPotionEffectCache().setRecipePotionEffect(true);
                guiHandler.openWindow(new me.wolfyscript.utilities.util.NamespacedKey("potion_creator", "potion_creator"));
                return true;
            }));
            registerButton(new ActionButton<>("potion.remove", Material.RED_CONCRETE, (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
                PotionEffects potionEffectCache = cache.getPotionEffectCache();
                potionEffectCache.setApplyPotionEffectType((cache1, type) -> {
                    ItemMeta itemMeta = items.getItem().getItemMeta();
                    if (itemMeta instanceof PotionMeta) {
                        ((PotionMeta) itemMeta).removeCustomEffect(type);
                    }
                    items.getItem().setItemMeta(itemMeta);
                });
                potionEffectCache.setOpenedFrom("item_creator", "main_menu");
                guiHandler.openWindow(new me.wolfyscript.utilities.util.NamespacedKey("potion_creator", "potion_effect_type_selection"));
                return true;
            }));
        }

        //Unbreakable Setting
        registerButton(new ToggleButton<>("unbreakable", new ButtonState<>("unbreakable.enabled", Material.BEDROCK, (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
            ItemMeta itemMeta = items.getItem().getItemMeta();
            itemMeta.setUnbreakable(false);
            items.getItem().setItemMeta(itemMeta);
            return true;
        }), new ButtonState<>("unbreakable.disabled", Material.GLASS, (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
            ItemMeta itemMeta = items.getItem().getItemMeta();
            itemMeta.setUnbreakable(true);
            items.getItem().setItemMeta(itemMeta);
            return true;
        })));

        //DAMAGE Settings
        registerButton(new OptionButton(Material.IRON_SWORD, "damage"));
        {
            registerButton(new ChatInputButton<>("damage.set", Material.GREEN_CONCRETE, (guiHandler, player, s, strings) -> {
                ItemMeta itemMeta = guiHandler.getCustomCache().getItems().getItem().getItemMeta();
                if (!(itemMeta instanceof Damageable)) {
                    return true;
                }
                try {
                    int value = Integer.parseInt(s);
                    ((Damageable) itemMeta).setDamage(value);
                    guiHandler.getCustomCache().getItems().getItem().setItemMeta(itemMeta);
                    api.getChat().sendKey(player, getNamespacedKey(), "damage.value_success", new Pair<>("%VALUE%", String.valueOf(value)));
                } catch (NumberFormatException e) {
                    api.getChat().sendKey(player, getNamespacedKey(), "damage.invalid_value", new Pair<>("%VALUE%", s));
                    return true;
                }
                return false;
            }));
            registerButton(new ActionButton<>("damage.reset", Material.RED_CONCRETE, (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
                ItemMeta itemMeta = items.getItem().getItemMeta();
                if (itemMeta instanceof Damageable) {
                    ((Damageable) itemMeta).setDamage(0);
                }
                items.getItem().setItemMeta(itemMeta);
                return true;
            }));
        }

        //REPAIR_COST Settings
        registerButton(new OptionButton(Material.EXPERIENCE_BOTTLE, "repair_cost"));
        {
            registerButton(new ChatInputButton<>("repair_cost.set", Material.GREEN_CONCRETE, (guiHandler, player, s, strings) -> {
                ItemMeta itemMeta = guiHandler.getCustomCache().getItems().getItem().getItemMeta();
                try {
                    int value = Integer.parseInt(s);
                    ((Repairable) itemMeta).setRepairCost(value);
                    guiHandler.getCustomCache().getItems().getItem().setItemMeta(itemMeta);
                    api.getChat().sendKey(player, getNamespacedKey(), "repair_cost.value_success", new Pair<>("%VALUE%", String.valueOf(value)));
                } catch (NumberFormatException e) {
                    api.getChat().sendKey(player, getNamespacedKey(), "repair_cost.invalid_value", new Pair<>("%VALUE%", s));
                    return true;
                }
                return false;
            }));
            registerButton(new ActionButton<>("repair_cost.reset", Material.RED_CONCRETE, (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
                ItemMeta itemMeta = items.getItem().getItemMeta();
                if (itemMeta instanceof Repairable) {
                    ((Repairable) itemMeta).setRepairCost(0);
                }
                items.getItem().setItemMeta(itemMeta);
                return true;
            }));
        }

        //CUSTOM_MODEL_DATA Settings
        registerButton(new OptionButton(Material.REDSTONE, "custom_model_data"));
        {
            registerButton(new ChatInputButton<>("custom_model_data.set", Material.GREEN_CONCRETE, (hashMap, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
                Items items = guiHandler.getCustomCache().getItems();
                hashMap.put("%VAR%", (items.getItem().hasItemMeta() && items.getItem().getItemMeta().hasCustomModelData() ? items.getItem().getItemMeta().getCustomModelData() : "&7&l/") + "");
                return itemStack;
            }, (guiHandler, player, s, strings) -> {
                ItemMeta itemMeta = guiHandler.getCustomCache().getItems().getItem().getItemMeta();
                if (!(itemMeta instanceof Repairable)) {
                    return true;
                }
                try {
                    int value = Integer.parseInt(s);
                    itemMeta.setCustomModelData(value);
                    guiHandler.getCustomCache().getItems().getItem().setItemMeta(itemMeta);
                    api.getChat().sendKey(player, getNamespacedKey(), "custom_model_data.success", new Pair<>("%VALUE%", String.valueOf(value)));
                } catch (NumberFormatException e) {
                    api.getChat().sendKey(player, getNamespacedKey(), "custom_model_data.invalid_value", new Pair<>("%VALUE%", s));
                    return true;
                }
                return false;
            }));
            registerButton(new ActionButton<>("custom_model_data.reset", Material.RED_CONCRETE, (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
                ItemMeta itemMeta = items.getItem().getItemMeta();
                itemMeta.setCustomModelData(null);
                items.getItem().setItemMeta(itemMeta);
                return true;
            }));
        }

        //CONSUME SETTINGS
        registerButton(new OptionButton(Material.ITEM_FRAME, "consume"));
        {
            registerButton(new ChatInputButton<>("consume.durability_cost.enabled", Material.DROPPER, (hashMap, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
                hashMap.put("%VAR%", guiHandler.getCustomCache().getItems().getItem().getDurabilityCost());
                return itemStack;
            }, (guiHandler, player, s, strings) -> {
                try {
                    int value = Integer.parseInt(s);
                    guiHandler.getCustomCache().getItems().getItem().setDurabilityCost(value);
                    api.getChat().sendKey(player, getNamespacedKey(), "consume.valid", new Pair<>("%VALUE%", String.valueOf(value)));
                } catch (NumberFormatException e) {
                    api.getChat().sendKey(player, getNamespacedKey(), "consume.invalid", new Pair<>("%VALUE%", s));
                    return true;
                }
                return false;
            }));
            registerButton(new DummyButton<>("consume.durability_cost.disabled", Material.DROPPER));

            registerButton(new ToggleButton<>("consume.consume_item", new ButtonState<>("consume.consume_item.enabled", Material.GREEN_CONCRETE, (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
                items.getItem().setConsumed(false);
                return true;
            }), new ButtonState<>("consume.consume_item.disabled", Material.RED_CONCRETE, (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
                items.getItem().setConsumed(true);
                return true;
            })));

            registerButton(new DummyButton<>("consume.replacement.enabled", Material.GREEN_CONCRETE));
            registerButton(new DummyButton<>("consume.replacement.disabled", Material.RED_CONCRETE));

            registerButton(new ItemInputButton<>("consume.replacement", new ButtonState<>("", Material.AIR, (ItemsButtonAction) (cache, items, guiHandler, player, inventory, slot, event) -> {
                Bukkit.getScheduler().runTask(customCrafting, () -> {
                    ItemStack replacement = inventory.getItem(slot);
                    if (replacement != null) {
                        items.getItem().setReplacement(CustomItem.getReferenceByItemStack(replacement).getApiReference());
                    } else {
                        items.getItem().setReplacement(null);
                    }
                });
                return false;
            }, (hashMap, cache, guiHandler, player, inventory, itemStack, i, b) -> guiHandler.getCustomCache().getItems().getItem().hasReplacement() ? new CustomItem(cache.getItems().getItem().getReplacement()).create() : new ItemStack(Material.AIR))));
        }

        //FUEL Settings
        registerButton(new OptionButton(Material.COAL, "fuel"));
        {
            registerButton(new ChatInputButton<>("fuel.burn_time.set", Material.GREEN_CONCRETE, (values, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
                values.put("%VAR%", guiHandler.getCustomCache().getItems().getItem().getBurnTime());
                return itemStack;
            }, (guiHandler, player, s, strings) -> {
                try {
                    int value = Integer.parseInt(s);
                    guiHandler.getCustomCache().getItems().getItem().setBurnTime(value);
                    api.getChat().sendKey(player, getNamespacedKey(), "fuel.value_success", new Pair<>("%VALUE%", String.valueOf(value)));
                } catch (NumberFormatException e) {
                    api.getChat().sendKey(player, getNamespacedKey(), "fuel.invalid_value", new Pair<>("%VALUE%", s));
                    return true;
                }
                return false;
            }));
            registerButton(new ActionButton<>("fuel.burn_time.reset", Material.RED_CONCRETE, (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
                items.getItem().setBurnTime(0);
                return true;
            }));
            registerButton(new FurnaceFuelToggleButton("furnace", Material.FURNACE));
            registerButton(new FurnaceFuelToggleButton("blast_furnace", Material.BLAST_FURNACE));
            registerButton(new FurnaceFuelToggleButton("smoker", Material.SMOKER));
        }

        //CUSTOM_DURABILITY_COST Settings
        registerButton(new OptionButton(Material.DIAMOND_SWORD, "custom_durability"));
        {
            registerButton(new ActionButton<>("custom_durability.remove", new ButtonState<>("custom_durability.remove", Material.RED_CONCRETE_POWDER, (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
                items.getItem().removeCustomDurability();
                return true;
            })));
            registerButton(new ChatInputButton<>("custom_durability.set_durability", Material.GREEN_CONCRETE, (values, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
                values.put("%VAR%", guiHandler.getCustomCache().getItems().getItem().getCustomDurability());
                return itemStack;
            }, (guiHandler, player, s, strings) -> {
                try {
                    guiHandler.getCustomCache().getItems().getItem().setCustomDurability(Integer.parseInt(strings[0]));
                } catch (NumberFormatException ex) {
                    return true;
                }
                guiHandler.openCluster();
                return false;
            }));
            registerButton(new ChatInputButton<>("custom_durability.set_damage", Material.RED_CONCRETE, (values, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
                Items items = guiHandler.getCustomCache().getItems();
                values.put("%VAR%", items.getItem().getCustomDamage());
                return itemStack;
            }, (guiHandler, player, s, strings) -> {
                try {
                    guiHandler.getCustomCache().getItems().getItem().setCustomDamage(Integer.parseInt(strings[0]));
                } catch (NumberFormatException ex) {
                    return true;
                }
                guiHandler.openCluster();
                return false;
            }));
            registerButton(new ChatInputButton<>("custom_durability.set_tag", Material.NAME_TAG, (values, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
                Items items = guiHandler.getCustomCache().getItems();
                values.put("%VAR%", items.getItem().getCustomDurabilityTag());
                return itemStack;
            }, (guiHandler, player, s, strings) -> {
                try {
                    guiHandler.getCustomCache().getItems().getItem().setCustomDurabilityTag("&r" + s);
                } catch (NumberFormatException ex) {
                    return true;
                }
                guiHandler.openCluster();
                return false;
            }));
        }

        //LOCALIZED_NAME Settings
        registerButton(new OptionButton(Material.NAME_TAG, "localized_name"));
        {
            registerButton(new ChatInputButton<>("localized_name.set", Material.NAME_TAG, (hashMap, cache, guiHandler, player, inventory, itemStack, i, b) -> {
                hashMap.put("%VAR%", guiHandler.getCustomCache().getItems().getItem().getItemMeta().getLocalizedName());
                return itemStack;
            }, (guiHandler, player, s, strings) -> {
                ItemMeta itemMeta = guiHandler.getCustomCache().getItems().getItem().getItemMeta();
                itemMeta.setLocalizedName(ChatColor.convert(s));
                guiHandler.getCustomCache().getItems().getItem().setItemMeta(itemMeta);
                return false;
            }));
            registerButton(new ActionButton<>("localized_name.remove", Material.NAME_TAG, (cache, guiHandler, player, inventory, i, inventoryClickEvent) -> {
                ItemMeta itemMeta = guiHandler.getCustomCache().getItems().getItem().getItemMeta();
                itemMeta.setLocalizedName(null);
                guiHandler.getCustomCache().getItems().getItem().setItemMeta(itemMeta);
                return true;
            }));
        }

        //Permission
        registerButton(new OptionButton(Material.BARRIER, "permission"));
        {
            registerButton(new ChatInputButton<>("permission.set", Material.GREEN_CONCRETE, (hashMap, cache, guiHandler, player, inventory, itemStack, i, b) -> {
                String perm = guiHandler.getCustomCache().getItems().getItem().getPermission();
                hashMap.put("%VAR%", perm.isEmpty() ? "none" : perm);
                return itemStack;
            }, (guiHandler, player, s, strings) -> {
                guiHandler.getCustomCache().getItems().getItem().setPermission(s.replace(" ", "."));
                return false;
            }));
            registerButton(new ActionButton<>("permission.remove", Material.RED_CONCRETE_POWDER, (cache, guiHandler, player, inventory, i, event) -> {
                guiHandler.getCustomCache().getItems().getItem().setPermission("");
                return true;
            }));
        }

        //Rarity Percentage
        registerButton(new OptionButton(Material.DIAMOND, "rarity"));
        {
            registerButton(new ChatInputButton<>("rarity.set", Material.GREEN_CONCRETE, (hashMap, cache, guiHandler, player, inventory, itemStack, i, b) -> {
                hashMap.put("%VAR%", guiHandler.getCustomCache().getItems().getItem().getRarityPercentage() + "§8(§7" + (cache.getItems().getItem().getRarityPercentage() * 100) + "%§8)");
                return itemStack;
            }, (guiHandler, player, s, strings) -> {
                try {
                    guiHandler.getCustomCache().getItems().getItem().setRarityPercentage(Double.parseDouble(s));
                } catch (NumberFormatException ex) {
                    return true;
                }
                return false;
            }));
            registerButton(new ActionButton<>("rarity.reset", Material.RED_CONCRETE_POWDER, (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
                items.getItem().setRarityPercentage(1.0d);
                return true;
            }));
        }

        registerButton(new OptionButton(Material.BOOKSHELF, "persistent_data"));
        {

        }

        //Elite Workbench Settings
        registerButton(new OptionButton(Material.CRAFTING_TABLE, "elite_workbench"));
        {
            registerButton(new ActionButton<>("elite_workbench.particles", Material.FIREWORK_ROCKET, (cache, guiHandler, player, inventory, i, event) -> {
                cache.setSubSetting("particle_effects");
                return true;
            }));
            registerButton(new MultipleChoiceButton<>("elite_workbench.grid_size",
                    new ButtonState<>("elite_workbench.grid_size.size_3", PlayerHeadUtils.getViaURL("9e95293acbcd4f55faf5947bfc5135038b275a7ab81087341b9ec6e453e839"), (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
                        ((EliteWorkbenchData) items.getItem().getCustomData(CustomCrafting.ELITE_CRAFTING_TABLE)).setGridSize(4);
                        return true;
                    }),
                    new ButtonState<>("elite_workbench.grid_size.size_4", PlayerHeadUtils.getViaURL("cbfb41f866e7e8e593659986c9d6e88cd37677b3f7bd44253e5871e66d1d424"), (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
                        ((EliteWorkbenchData) items.getItem().getCustomData(CustomCrafting.ELITE_CRAFTING_TABLE)).setGridSize(5);
                        return true;
                    }),
                    new ButtonState<>("elite_workbench.grid_size.size_5", PlayerHeadUtils.getViaURL("14d844fee24d5f27ddb669438528d83b684d901b75a6889fe7488dfc4cf7a1c"), (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
                        ((EliteWorkbenchData) items.getItem().getCustomData(CustomCrafting.ELITE_CRAFTING_TABLE)).setGridSize(6);
                        return true;
                    }),
                    new ButtonState<>("elite_workbench.grid_size.size_6", PlayerHeadUtils.getViaURL("faff2eb498e5c6a04484f0c9f785b448479ab213df95ec91176a308a12add70"), (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
                        ((EliteWorkbenchData) items.getItem().getCustomData(CustomCrafting.ELITE_CRAFTING_TABLE)).setGridSize(3);
                        return true;
                    })));
            registerButton(new ToggleButton<>("elite_workbench.toggle", new ButtonState<>("elite_workbench.toggle.enabled", Material.GREEN_CONCRETE, (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
                ((EliteWorkbenchData) items.getItem().getCustomData(CustomCrafting.ELITE_CRAFTING_TABLE)).setEnabled(false);
                return true;
            }), new ButtonState<>("elite_workbench.toggle.disabled", Material.RED_CONCRETE, (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
                ((EliteWorkbenchData) items.getItem().getCustomData(CustomCrafting.ELITE_CRAFTING_TABLE)).setEnabled(true);
                return true;
            })));
            registerButton(new ToggleButton<>("elite_workbench.advanced_recipes", new ButtonState<>("elite_workbench.advanced_recipes.enabled", Material.GREEN_CONCRETE, (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
                ((EliteWorkbenchData) items.getItem().getCustomData(CustomCrafting.ELITE_CRAFTING_TABLE)).setAdvancedRecipes(false);
                return true;
            }), new ButtonState<>("elite_workbench.advanced_recipes.disabled", Material.RED_CONCRETE, (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
                ((EliteWorkbenchData) items.getItem().getCustomData(CustomCrafting.ELITE_CRAFTING_TABLE)).setAdvancedRecipes(true);
                return true;
            })));
        }

        //Advanced Knowledgebook Settings
        registerButton(new OptionButton(Material.KNOWLEDGE_BOOK, "knowledge_book"));
        {
            me.wolfyscript.utilities.util.NamespacedKey knowledgeBook = new me.wolfyscript.utilities.util.NamespacedKey("customcrafting","knowledge_book");
            registerButton(new ToggleButton<>("knowledge_book.toggle", new ButtonState<>("knowledge_book.toggle.enabled", Material.GREEN_CONCRETE, (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
                ((RecipeBookData) items.getItem().getCustomData(knowledgeBook)).setEnabled(false);
                return true;
            }), new ButtonState<>("knowledge_book.toggle.disabled", Material.RED_CONCRETE, (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
                ((RecipeBookData) items.getItem().getCustomData(knowledgeBook)).setEnabled(true);
                return true;
            })));
        }

        registerButton(new OptionButton(Material.IRON_HELMET, "armor_slots"));
        {
            registerButton(new ArmorSlotToggleButton(EquipmentSlot.HEAD, Material.DIAMOND_HELMET));
            registerButton(new ArmorSlotToggleButton(EquipmentSlot.CHEST, Material.DIAMOND_CHESTPLATE));
            registerButton(new ArmorSlotToggleButton(EquipmentSlot.LEGS, Material.DIAMOND_LEGGINGS));
            registerButton(new ArmorSlotToggleButton(EquipmentSlot.FEET, Material.DIAMOND_BOOTS));
        }

        registerButton(new OptionButton(Material.FIREWORK_ROCKET, "particle_effects"));
        {
            registerButton(new DummyButton<>("particle_effects.head", Material.IRON_HELMET));
            registerButton(new ParticleEffectSelectButton(ParticleLocation.HEAD));
            registerButton(new DummyButton<>("particle_effects.chest", Material.IRON_CHESTPLATE));
            registerButton(new ParticleEffectSelectButton(ParticleLocation.CHEST));
            registerButton(new DummyButton<>("particle_effects.legs", Material.IRON_LEGGINGS));
            registerButton(new ParticleEffectSelectButton(ParticleLocation.LEGS));
            registerButton(new DummyButton<>("particle_effects.feet", Material.IRON_BOOTS));
            registerButton(new ParticleEffectSelectButton(ParticleLocation.FEET));
            registerButton(new DummyButton<>("particle_effects.hand", Material.IRON_SWORD));
            registerButton(new ParticleEffectSelectButton(ParticleLocation.HAND));
            registerButton(new DummyButton<>("particle_effects.off_hand", Material.SHIELD));
            registerButton(new ParticleEffectSelectButton(ParticleLocation.OFF_HAND));
            registerButton(new DummyButton<>("particle_effects.block", Material.GRASS_BLOCK));
            registerButton(new ParticleEffectSelectButton(ParticleLocation.BLOCK));
        }

        registerButton(new OptionButton(Material.GRASS_BLOCK, "vanilla"));
        {
            registerButton(new ToggleButton<>("vanilla.block_recipes", new ButtonState<>("vanilla.block_recipes.enabled", Material.GREEN_CONCRETE, (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
                items.getItem().setBlockVanillaRecipes(false);
                return true;
            }), new ButtonState<>("vanilla.block_recipes.disabled", Material.RED_CONCRETE, (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
                items.getItem().setBlockVanillaRecipes(true);
                return true;
            })));
            registerButton(new ToggleButton<>("vanilla.block_placement", new ButtonState<>("vanilla.block_placement.enabled", Material.GREEN_CONCRETE, (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
                items.getItem().setBlockPlacement(false);
                return true;
            }), new ButtonState<>("vanilla.block_placement.disabled", Material.RED_CONCRETE, (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
                items.getItem().setBlockPlacement(true);
                return true;
            })));
        }

        for (String meta : dummyMetaSettings.getMetas()) {
            registerButton(new MetaIgnoreButton(meta));
        }
    }

    @Override
    public void onUpdateSync(GuiUpdate<CCCache> guiUpdate) {

    }

    @Override
    public void onUpdateAsync(GuiUpdate<CCCache> event) {
        super.onUpdateAsync(event);
        GuiHandler<CCCache> guiHandler = event.getGuiHandler();
        CCCache cache = guiHandler.getCustomCache();
        Items items = cache.getItems();
        CustomItem customItem = items.getItem();
        ItemStack item = customItem.create();

        event.setButton(0, "back");
        event.setButton(13, "item_input");

        CCPlayerData data = PlayerUtil.getStore(event.getPlayer());
        me.wolfyscript.utilities.util.NamespacedKey gray = new me.wolfyscript.utilities.util.NamespacedKey("none", data.isDarkMode() ? "glass_gray" : "glass_white");
        event.setButton(4, gray);
        event.setButton(12, gray);
        event.setButton(14, gray);
        event.setButton(22, gray);

        if (items.isRecipeItem()) {
            event.setButton(2, "apply_item");
        }
        event.setButton(3, "save_item");

        List<String> options = new ArrayList<>();
        if (customItem.getApiReference() instanceof VanillaRef) {
            options.add("display_name.option");
            options.add("localized_name.option");
            options.add("lore.option");
            options.add("enchantments.option");
            options.add("flags.option");
            options.add("attribute.option");
            if (items.getItem() != null && !item.getType().equals(Material.AIR)) {
                ((ToggleButton<CCCache>) getButton("unbreakable")).setState(event.getGuiHandler(), item.getItemMeta().isUnbreakable());
                options.add("unbreakable");
            }
            options.add("repair_cost.option");
            if (items.getItem() != null && item.hasItemMeta() && item.getItemMeta() instanceof PotionMeta) {
                options.add("potion.option");
            }
            options.add("damage.option");
            if (items.getItem() != null && item.getType().equals(Material.PLAYER_HEAD)) {
                options.add("player_head.option");
            }
        } else {
            if (customItem.getApiReference() instanceof WolfyUtilitiesRef) {
                event.setButton(5, "reference.wolfyutilites");
            } else if (customItem.getApiReference() instanceof OraxenRef) {
                event.setButton(5, "reference.oraxen");
            } else if (customItem.getApiReference() instanceof ItemsAdderRef) {
                event.setButton(5, "reference.itemsadder");
            } else if (customItem.getApiReference() instanceof MythicMobsRef) {
                event.setButton(5, "reference.mythicmobs");
            }
        }
        options.add("fuel.option");
        options.add("consume.option");
        options.add("permission.option");
        options.add("rarity.option");

        options.add("custom_durability.option");
        options.add("custom_model_data.option");
        //options.add("persistent_data.option");
        options.add("armor_slots.option");
        options.add("particle_effects.option");

        if (item.getType().isBlock()) {
            options.add("elite_workbench.option");
        }
        options.add("knowledge_book.option");
        options.add("vanilla.option");

        int maxPages = options.size() / 14 + (options.size() % 14 > 0 ? 1 : 0);
        if (items.getPage() >= maxPages) {
            items.setPage(maxPages - 1);
        }
        if (items.getPage() > 0) {
            event.setButton(5, "page_previous");
        }
        if (items.getPage() + 1 < maxPages) {
            event.setButton(5, "page_next");
        }

        int slot = 9;
        int j = 14 * items.getPage();
        for (int i = 0; i < 14; i++) {
            if (i == 3) {
                slot = 12;
            } else if (i == 10) {
                slot = 13;
            }
            if (j < options.size()) {
                event.setButton(slot + i, options.get(j));
                j++;
            } else {
                event.setButton(slot + i, gray);
            }
        }

        if (!item.getType().equals(Material.AIR)) {
            //DRAW Sections
            switch (cache.getSubSetting()) {
                case "display_name":
                    event.setButton(39, "display_name.set");
                    event.setButton(41, "display_name.remove");
                    event.setButton(45, "meta_ignore.name");
                    break;
                case "enchantments":
                    event.setButton(39, "enchantments.add");
                    event.setButton(41, "enchantments.remove");
                    event.setButton(45, "meta_ignore.enchant");
                    break;
                case "lore":
                    event.setButton(39, "lore.add");
                    event.setButton(41, "lore.remove");
                    event.setButton(45, "meta_ignore.lore");
                    break;
                case "flags":
                    ((ToggleButton<CCCache>) getButton("flags.attributes")).setState(guiHandler, item.getItemMeta().hasItemFlag(ItemFlag.HIDE_ATTRIBUTES));
                    ((ToggleButton<CCCache>) getButton("flags.unbreakable")).setState(guiHandler, item.getItemMeta().hasItemFlag(ItemFlag.HIDE_UNBREAKABLE));
                    ((ToggleButton<CCCache>) getButton("flags.destroys")).setState(guiHandler, item.getItemMeta().hasItemFlag(ItemFlag.HIDE_DESTROYS));
                    ((ToggleButton<CCCache>) getButton("flags.placed_on")).setState(guiHandler, item.getItemMeta().hasItemFlag(ItemFlag.HIDE_PLACED_ON));
                    ((ToggleButton<CCCache>) getButton("flags.potion_effects")).setState(guiHandler, item.getItemMeta().hasItemFlag(ItemFlag.HIDE_POTION_EFFECTS));
                    ((ToggleButton<CCCache>) getButton("flags.enchants")).setState(guiHandler, item.getItemMeta().hasItemFlag(ItemFlag.HIDE_ENCHANTS));
                    event.setButton(37, "flags.attributes");
                    event.setButton(39, "flags.unbreakable");
                    event.setButton(41, "flags.destroys");
                    event.setButton(43, "flags.placed_on");
                    event.setButton(47, "flags.potion_effects");
                    event.setButton(51, "flags.enchants");
                    event.setButton(45, "meta_ignore.flags");
                    break;
                case "attribute":
                    event.setButton(36, "attribute.generic_max_health");
                    event.setButton(37, "attribute.generic_follow_range");
                    event.setButton(38, "attribute.generic_knockback_resistance");
                    event.setButton(39, "attribute.generic_movement_speed");
                    event.setButton(40, "attribute.generic_flying_speed");
                    event.setButton(41, "attribute.generic_attack_damage");
                    event.setButton(42, "attribute.generic_attack_speed");
                    event.setButton(43, "attribute.generic_armor");
                    event.setButton(44, "attribute.generic_armor_toughness");
                    event.setButton(48, "attribute.generic_luck");
                    event.setButton(49, "attribute.horse_jump_strength");
                    event.setButton(50, "attribute.zombie_spawn_reinforcements");
                    event.setButton(45, "meta_ignore.attributes_modifiers");
                    break;
                case "player_head":
                    if (items.getItem() != null && item.getType().equals(Material.PLAYER_HEAD)) {
                        event.setButton(38, "player_head.texture.input");
                        event.setButton(39, "player_head.texture.apply");
                        event.setButton(41, "player_head.owner");
                        event.setButton(45, "meta_ignore.playerHead");
                    }
                    break;
                case "potion":
                    if (items.getItem() != null && item.hasItemMeta() && item.getItemMeta() instanceof PotionMeta) {
                        event.setButton(39, "potion.add");
                        event.setButton(40, "potion_beta.add");
                        event.setButton(41, "potion.remove");
                        event.setButton(45, "meta_ignore.potion");
                    }
                    break;
                case "damage":
                    event.setButton(39, "damage.set");
                    event.setButton(41, "damage.reset");
                    event.setButton(45, "meta_ignore.damage");
                    break;
                case "repair_cost":
                    event.setButton(39, "repair_cost.set");
                    event.setButton(41, "repair_cost.reset");
                    event.setButton(45, "meta_ignore.repairCost");
                    break;
                case "fuel":
                    event.setButton(39, "fuel.burn_time.set");
                    event.setButton(41, "fuel.burn_time.reset");
                    ((ToggleButton<CCCache>) getButton("fuel.furnace")).setState(event.getGuiHandler(), customItem.getAllowedBlocks().contains(Material.FURNACE));
                    ((ToggleButton<CCCache>) getButton("fuel.blast_furnace")).setState(event.getGuiHandler(), customItem.getAllowedBlocks().contains(Material.BLAST_FURNACE));
                    ((ToggleButton<CCCache>) getButton("fuel.smoker")).setState(event.getGuiHandler(), customItem.getAllowedBlocks().contains(Material.SMOKER));
                    event.setButton(47, "fuel.furnace");
                    event.setButton(49, "fuel.blast_furnace");
                    event.setButton(51, "fuel.smoker");
                    break;
                case "custom_model_data":
                    event.setButton(39, "custom_model_data.set");
                    event.setButton(41, "custom_model_data.reset");
                    event.setButton(45, "meta_ignore.customModelData");
                    break;
                case "consume":
                    ((ToggleButton<CCCache>) getButton("consume.consume_item")).setState(event.getGuiHandler(), customItem.isConsumed());
                    event.setButton(31, "consume.consume_item");
                    event.setButton(38, "consume.replacement");
                    event.setButton(39, items.getItem().hasReplacement() ? "consume.replacement.enabled" : "consume.replacement.disabled");
                    if (customItem.hasReplacement() || item.getMaxStackSize() > 1) {
                        event.setButton(41, "consume.durability_cost.disabled");
                    } else {
                        event.setButton(41, "consume.durability_cost.enabled");
                    }
                    break;
                case "custom_durability":
                    event.setButton(38, "custom_durability.set_damage");
                    event.setButton(40, "custom_durability.set_tag");
                    event.setButton(42, "custom_durability.set_durability");
                    event.setButton(49, "custom_durability.remove");
                    event.setButton(45, "meta_ignore.custom_damage");
                    event.setButton(53, "meta_ignore.custom_durability");
                    break;
                case "localized_name":
                    event.setButton(39, "localized_name.set");
                    event.setButton(41, "localized_name.remove");
                    break;
                case "permission":
                    event.setButton(39, "permission.set");
                    event.setButton(41, "permission.remove");
                    break;
                case "rarity":
                    event.setButton(39, "rarity.set");
                    event.setButton(41, "rarity.reset");
                    break;
                case "elite_workbench":
                    if (item.getType().isBlock()) {
                        ((MultipleChoiceButton<CCCache>) getButton("elite_workbench.grid_size")).setState(event.getGuiHandler(), ((EliteWorkbenchData) customItem.getCustomData(CustomCrafting.ELITE_CRAFTING_TABLE)).getGridSize() - 3);
                        ((ToggleButton<CCCache>) getButton("elite_workbench.toggle")).setState(event.getGuiHandler(), ((EliteWorkbenchData) customItem.getCustomData(CustomCrafting.ELITE_CRAFTING_TABLE)).isEnabled());
                        ((ToggleButton<CCCache>) getButton("elite_workbench.advanced_recipes")).setState(event.getGuiHandler(), ((EliteWorkbenchData) customItem.getCustomData(CustomCrafting.ELITE_CRAFTING_TABLE)).isAdvancedRecipes());
                        event.setButton(37, "elite_workbench.particles");
                        event.setButton(39, "elite_workbench.grid_size");
                        event.setButton(41, "elite_workbench.toggle");
                        event.setButton(43, "elite_workbench.advanced_recipes");
                    }
                    break;
                case "knowledge_book":
                    ((ToggleButton<CCCache>) getButton("elite_workbench.toggle")).setState(event.getGuiHandler(), ((RecipeBookData) customItem.getCustomData(CustomCrafting.RECIPE_BOOK)).isEnabled());
                    event.setButton(40, "knowledge_book.toggle");
                    break;
                case "armor_slots":
                    ((ToggleButton<CCCache>) getButton("armor_slots.head")).setState(event.getGuiHandler(), customItem.hasEquipmentSlot(EquipmentSlot.HEAD));
                    ((ToggleButton<CCCache>) getButton("armor_slots.chest")).setState(event.getGuiHandler(), customItem.hasEquipmentSlot(EquipmentSlot.CHEST));
                    ((ToggleButton<CCCache>) getButton("armor_slots.legs")).setState(event.getGuiHandler(), customItem.hasEquipmentSlot(EquipmentSlot.LEGS));
                    ((ToggleButton<CCCache>) getButton("armor_slots.feet")).setState(event.getGuiHandler(), customItem.hasEquipmentSlot(EquipmentSlot.FEET));
                    event.setButton(37, "armor_slots.head");
                    event.setButton(39, "armor_slots.chest");
                    event.setButton(41, "armor_slots.legs");
                    event.setButton(43, "armor_slots.feet");
                    break;
                case "particle_effects":
                    event.setButton(37, "particle_effects.head");
                    event.setButton(38, "particle_effects.chest");
                    event.setButton(39, "particle_effects.legs");
                    event.setButton(40, "particle_effects.feet");
                    event.setButton(41, "particle_effects.hand");
                    event.setButton(42, "particle_effects.off_hand");
                    event.setButton(43, "particle_effects.block");

                    event.setButton(46, "particle_effects.head.input");
                    event.setButton(47, "particle_effects.chest.input");
                    event.setButton(48, "particle_effects.legs.input");
                    event.setButton(49, "particle_effects.feet.input");
                    event.setButton(50, "particle_effects.hand.input");
                    event.setButton(51, "particle_effects.off_hand.input");
                    event.setButton(52, "particle_effects.block.input");
                    break;
                case "vanilla":
                    ((ToggleButton<CCCache>) getButton("vanilla.block_recipes")).setState(event.getGuiHandler(), customItem.isBlockVanillaRecipes());
                    event.setButton(38, "vanilla.block_recipes");
                    if (item.getType().isBlock()) {
                        event.setButton(40, "vanilla.block_placement");
                    }

            }
            if (cache.getSubSetting().startsWith("attribute.generic") || cache.getSubSetting().startsWith("attribute.horse") || cache.getSubSetting().startsWith("attribute.zombie")) {
                event.setButton(36, "attribute.slot_head");
                event.setButton(45, "attribute.slot_chest");
                event.setButton(37, "attribute.slot_legs");
                event.setButton(46, "attribute.slot_feet");
                event.setButton(38, "attribute.slot_hand");
                event.setButton(47, "attribute.slot_off_hand");
                event.setButton(42, "attribute.multiply_scalar_1");
                event.setButton(43, "attribute.add_scalar");
                event.setButton(44, "attribute.add_number");
                event.setButton(51, "attribute.set_amount");
                event.setButton(52, "attribute.set_name");
                event.setButton(53, "attribute.set_uuid");
                event.setButton(40, "attribute.save");
                event.setButton(49, "attribute.delete");
            }
        }
    }
}
