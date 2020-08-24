package me.wolfyscript.customcrafting.gui.item_creator;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.custom_data.EliteWorkbenchData;
import me.wolfyscript.customcrafting.configs.custom_data.KnowledgeBookData;
import me.wolfyscript.customcrafting.data.PlayerStatistics;
import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.data.cache.items.Items;
import me.wolfyscript.customcrafting.gui.ExtendedGuiWindow;
import me.wolfyscript.customcrafting.gui.item_creator.buttons.*;
import me.wolfyscript.customcrafting.utils.ChatUtils;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.custom_items.CustomItems;
import me.wolfyscript.utilities.api.custom_items.MetaSettings;
import me.wolfyscript.utilities.api.custom_items.api_references.ItemsAdderRef;
import me.wolfyscript.utilities.api.custom_items.api_references.OraxenRef;
import me.wolfyscript.utilities.api.custom_items.api_references.VanillaRef;
import me.wolfyscript.utilities.api.custom_items.api_references.WolfyUtilitiesRef;
import me.wolfyscript.utilities.api.inventory.GuiHandler;
import me.wolfyscript.utilities.api.inventory.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.GuiWindow;
import me.wolfyscript.utilities.api.inventory.InventoryAPI;
import me.wolfyscript.utilities.api.inventory.button.ButtonActionRender;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.buttons.*;
import me.wolfyscript.utilities.api.utils.inventory.PlayerHeadUtils;
import me.wolfyscript.utilities.api.utils.inventory.item_builder.ItemBuilder;
import me.wolfyscript.utilities.api.utils.particles.ParticleEffect;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class ItemCreator extends ExtendedGuiWindow {

    private static final MetaSettings dummyMetaSettings = new MetaSettings();

    public ItemCreator(InventoryAPI inventoryAPI, CustomCrafting customCrafting) {
        super("main_menu", inventoryAPI, 54, customCrafting);
    }

    @Override
    public void onInit() {
        registerButton(new ActionButton("back", new ButtonState("none", "back", PlayerHeadUtils.getViaValue("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODY0Zjc3OWE4ZTNmZmEyMzExNDNmYTY5Yjk2YjE0ZWUzNWMxNmQ2NjllMTljNzVmZDFhN2RhNGJmMzA2YyJ9fX0="), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            if (((TestCache) guiHandler.getCustomCache()).getItems().isRecipeItem()) {
                guiHandler.openCluster("recipe_creator");
            } else {
                guiHandler.openCluster("none");
            }
            return true;
        })));
        registerButton(new ItemInputButton("item_input", new ButtonState("", Material.AIR, new ButtonActionRender() {
            @Override
            public boolean run(GuiHandler guiHandler, Player player, Inventory inventory, int i, InventoryClickEvent event) {
                GuiWindow guiWindow = guiHandler.getCurrentInv();
                Bukkit.getScheduler().runTaskLater(customCrafting, () -> {
                    ItemStack item = inventory.getItem(i);
                    CustomItem customItem = CustomItem.getReferenceByItemStack(item != null ? item : new ItemStack(Material.AIR));
                    ((TestCache) guiHandler.getCustomCache()).getItems().setItem(customItem);
                    ((ToggleButton) guiWindow.getButton("unbreakable")).setState(guiHandler, (item != null && !item.getType().equals(Material.AIR)) && item.getItemMeta().isUnbreakable());
                }, 1);
                return false;
            }

            @Override
            public ItemStack render(HashMap<String, Object> hashMap, GuiHandler guiHandler, Player player, ItemStack itemStack, int slot, boolean help) {
                return ((TestCache) guiHandler.getCustomCache()).getItems().getItem().getItemStack();
            }
        })));
        registerButton(new ActionButton("save_item", new ButtonState("save_item", Material.WRITABLE_BOOK, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            Items items = ((TestCache) guiHandler.getCustomCache()).getItems();
            if (!items.getItem().getItemStack().getType().equals(Material.AIR)) {
                sendMessage(player, "save.input.line1");
                openChat("save.input.line2", guiHandler, (guiHandler1, player1, s, args) -> {
                    if (args.length > 1) {
                        try {
                            me.wolfyscript.utilities.api.utils.NamespacedKey namespacedKey = new me.wolfyscript.utilities.api.utils.NamespacedKey(args[0].toLowerCase(Locale.ROOT).replace(" ", "_"), args[1].toLowerCase(Locale.ROOT).replace(" ", "_"));
                            CustomItem customItem = items.getItem();
                            if (customItem.getApiReference() instanceof WolfyUtilitiesRef && ((WolfyUtilitiesRef) customItem.getApiReference()).getNamespacedKey().equals(namespacedKey)) {
                                api.sendPlayerMessage(player, "&cError saving item! Cannot override original CustomItem &4" + namespacedKey + "&c! Save it under another NamespacedKey or Edit the original!");
                                return true;
                            }
                            customCrafting.saveItem(namespacedKey, items.getItem());
                            sendMessage(player, "save.success");
                            api.sendPlayerMessage(player1, "&6" + namespacedKey.getNamespace() + "/items/" + namespacedKey.getKey());
                            Bukkit.getScheduler().runTask(api.getPlugin(), (Runnable) guiHandler::openCluster);
                        } catch (IllegalArgumentException e) {
                            api.sendPlayerMessage(player1, e.getMessage());
                        }
                        return false;
                    }
                    return true;
                });
            }
            return true;
        })));

        registerButton(new ActionButton("apply_item", new ButtonState("apply_item", Material.GREEN_CONCRETE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            TestCache cache = ((TestCache) guiHandler.getCustomCache());
            if (!cache.getItems().getItem().getItemStack().getType().equals(Material.AIR)) {
                CustomItem customItem = cache.getItems().getItem();
                if (cache.getItems().isSaved()) {
                    customCrafting.saveItem(cache.getItems().getNamespacedKey(), customItem);
                    customItem = CustomItems.getCustomItem(cache.getItems().getNamespacedKey());
                }
                cache.applyItem(customItem);
                guiHandler.openCluster("recipe_creator");
            }
            return true;
        })));

        registerButton(new ActionButton("page_next", new ButtonState("page_next", PlayerHeadUtils.getViaURL("c86185b1d519ade585f184c34f3f3e20bb641deb879e81378e4eaf209287"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).getItems().setPage(((TestCache) guiHandler.getCustomCache()).getItems().getPage() + 1);
            return true;
        })));
        registerButton(new ActionButton("page_previous", new ButtonState("page_previous", PlayerHeadUtils.getViaURL("ad73cf66d31b83cd8b8644c15958c1b73c8d97323b801170c1d8864bb6a846d"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            if (((TestCache) guiHandler.getCustomCache()).getItems().getPage() > 0) {
                ((TestCache) guiHandler.getCustomCache()).getItems().setPage(((TestCache) guiHandler.getCustomCache()).getItems().getPage() - 1);
            }
            return true;
        })));

        registerButton(new DummyButton("reference.wolfyutilites", new ButtonState("reference.wolfyutilities", Material.CRAFTING_TABLE, (hashMap, guiHandler, player, itemStack, i, b) -> {
            hashMap.put("%item_key%", ((WolfyUtilitiesRef) ((TestCache) guiHandler.getCustomCache()).getItems().getItem().getApiReference()).getNamespacedKey().toString());
            return itemStack;
        })));
        registerButton(new DummyButton("reference.oraxen", new ButtonState("reference.oraxen", Material.DIAMOND, (hashMap, guiHandler, player, itemStack, i, b) -> {
            hashMap.put("%item_key%", ((OraxenRef) ((TestCache) guiHandler.getCustomCache()).getItems().getItem().getApiReference()).getItemID());
            return itemStack;
        })));
        registerButton(new DummyButton("reference.itemsadder", new ButtonState("reference.itemsadder", Material.GRASS_BLOCK, (hashMap, guiHandler, player, itemStack, i, b) -> {
            hashMap.put("%item_key%", ((ItemsAdderRef) ((TestCache) guiHandler.getCustomCache()).getItems().getItem().getApiReference()).getItemName());
            return itemStack;
        })));

        //DISPLAY NAME SETTINGS
        registerButton(new ActionButton("display_name.option", new ButtonState("display_name.option", Material.NAME_TAG, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).setSubSetting("display_name");
            return true;
        })));
        registerButton(new ChatInputButton("display_name.set", new ButtonState("display_name.set", Material.GREEN_CONCRETE), (guiHandler, player, s, strings) -> {
            ((TestCache) guiHandler.getCustomCache()).getItems().getItem().setDisplayName(WolfyUtilities.translateColorCodes(s));
            return false;
        }));
        registerButton(new ActionButton("display_name.remove", new ButtonState("display_name.remove", Material.RED_CONCRETE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).getItems().getItem().setDisplayName(null);
            return true;
        })));

        //ENCHANT SETTINGS
        registerButton(new ActionButton("enchantments.option", new ButtonState("enchantments.option", Material.ENCHANTED_BOOK, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).setSubSetting("enchantments");
            return true;
        })));
        {
            registerButton(new ChatInputButton("enchantments.add", new ButtonState("enchantments.add", Material.ENCHANTED_BOOK), (guiHandler, player, s, args) -> {
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
                        ((TestCache) guiHandler.getCustomCache()).getItems().getItem().addUnsafeEnchantment(enchantment, level);
                    } else {
                        api.sendPlayerMessage(player, "none", "item_creator", "enchant.invalid_enchant", new String[]{"%ENCHANT%", args[0]});
                        return true;
                    }
                } else {
                    sendMessage(player, "enchant.no_lvl");
                    return true;
                }
                return false;
            }));
            registerButton(new ChatInputButton("enchantments.remove", new ButtonState("enchantments.remove", Material.RED_CONCRETE), (guiHandler, player, s, args) -> {
                Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(args[0].toLowerCase(Locale.ROOT).replace(' ', '_')));
                if (enchantment != null) {
                    ((TestCache) guiHandler.getCustomCache()).getItems().getItem().removeEnchantment(enchantment);
                } else {
                    api.sendPlayerMessage(player, "none", "item_creator", "enchant.invalid_enchant", new String[]{"%ENCHANT%", args[0]});
                    return true;
                }
                return false;
            }));
        }

        //LORE SETTINGS
        registerButton(new ActionButton("lore.option", new ButtonState("lore.option", Material.WRITABLE_BOOK, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).setSubSetting("lore");
            return true;
        })));
        {
            registerButton(new ChatInputButton("lore.add", new ButtonState("lore.add", Material.WRITABLE_BOOK), (guiHandler, player, s, strings) -> {
                ((TestCache) guiHandler.getCustomCache()).getItems().getItem().addLoreLine(s.equals("&empty") ? "" : WolfyUtilities.translateColorCodes(s));
                return false;
            }));
            registerButton(new ActionButton("lore.remove", new ButtonState("lore.remove", Material.WRITTEN_BOOK, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                ChatUtils.sendLoreManager(player);
                guiHandler.close();
                return true;
            })));
        }

        //FLAGS SETTINGS
        registerButton(new ActionButton("flags.option", new ButtonState("flags.option", Material.WRITTEN_BOOK, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).setSubSetting("flags");
            return true;
        })));
        {
            registerButton(new ItemFlagsToggleButton("enchants", ItemFlag.HIDE_ENCHANTS, Material.ENCHANTING_TABLE));
            registerButton(new ItemFlagsToggleButton("attributes", ItemFlag.HIDE_ATTRIBUTES, Material.ENCHANTED_GOLDEN_APPLE));
            registerButton(new ItemFlagsToggleButton("unbreakable", ItemFlag.HIDE_UNBREAKABLE, Material.BEDROCK));
            registerButton(new ItemFlagsToggleButton("destroys", ItemFlag.HIDE_DESTROYS, Material.TNT));
            registerButton(new ItemFlagsToggleButton("placed_on", ItemFlag.HIDE_PLACED_ON, Material.GRASS_BLOCK));
            registerButton(new ItemFlagsToggleButton("potion_effects", ItemFlag.HIDE_POTION_EFFECTS, Material.POTION));
        }

        //attributes_modifiers SETTINGS
        registerButton(new ActionButton("attribute.option", new ButtonState("attribute.option", Material.ENCHANTED_GOLDEN_APPLE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).setSubSetting("attribute");
            return true;
        })));
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
            registerButton(new ChatInputButton("attribute.set_amount", new ButtonState("attribute.set_amount", PlayerHeadUtils.getViaURL("461c8febcac21b9f63d87f9fd933589fe6468e93aa81cfcf5e52a4322e16e6"), (hashMap, guiHandler, player, itemStack, slot, help) -> {
                hashMap.put("%NUMBER%", ((TestCache) guiHandler.getCustomCache()).getItems().getAttribAmount());
                return itemStack;
            }), (guiHandler, player, s, args) -> {
                try {
                    ((TestCache) guiHandler.getCustomCache()).getItems().setAttribAmount(Double.parseDouble(args[0]));
                } catch (NumberFormatException e) {
                    api.sendPlayerMessage(player, "item_creator", "main_menu", "attribute.amount.error");
                    return true;
                }
                return false;
            }));
            registerButton(new ChatInputButton("attribute.set_name", new ButtonState("attribute.set_name", Material.NAME_TAG, (hashMap, guiHandler, player, itemStack, slot, help) -> {
                hashMap.put("%NAME%", ((TestCache) guiHandler.getCustomCache()).getItems().getAttributeName());
                return itemStack;
            }), (guiHandler, player, s, strings) -> {
                ((TestCache) guiHandler.getCustomCache()).getItems().setAttributeName(strings[0]);
                return false;
            }));
            registerButton(new ChatInputButton("attribute.set_uuid", new ButtonState("attribute.set_uuid", Material.TRIPWIRE_HOOK, (hashMap, guiHandler, player, itemStack, slot, help) -> {
                hashMap.put("%UUID%", ((TestCache) guiHandler.getCustomCache()).getItems().getAttributeUUID());
                return itemStack;
            }), (guiHandler, player, s, strings) -> {
                try {
                    UUID uuid = UUID.fromString(strings[0]);
                    ((TestCache) guiHandler.getCustomCache()).getItems().setAttributeUUID(uuid.toString());
                } catch (IllegalArgumentException ex) {
                    api.sendPlayerMessage(player, "item_creator", "main_menu", "attribute.uuid.error.line1", new String[]{"%UUID%", strings[0]});
                    api.sendPlayerMessage(player, "item_creator", "main_menu", "attribute.uuid.error.line2");
                    return true;
                }
                return false;
            }));
            registerButton(new ActionButton("attribute.save", new ButtonState("attribute.save", Material.GREEN_CONCRETE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                ItemMeta itemMeta = ((TestCache) guiHandler.getCustomCache()).getItems().getItem().getItemMeta();
                itemMeta.addAttributeModifier(Attribute.valueOf(((TestCache) guiHandler.getCustomCache()).getSubSetting().split("\\.")[1].toUpperCase(Locale.ROOT)), ((TestCache) guiHandler.getCustomCache()).getItems().getAttributeModifier());
                ((TestCache) guiHandler.getCustomCache()).getItems().getItem().setItemMeta(itemMeta);
                return true;
            })));
            registerButton(new ActionButton("attribute.delete", new ButtonState("attribute.delete", Material.RED_CONCRETE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                ChatUtils.sendAttributeModifierManager(player);
                guiHandler.close();
                return true;
            })));
        }

        //PLAYER_HEAD SETTINGS
        registerButton(new ActionButton("player_head.option", new ButtonState("player_head.option", Material.PLAYER_HEAD, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).setSubSetting("player_head");
            return true;
        })));
        {
            registerButton(new ItemInputButton("player_head.texture.input", new ButtonState("", Material.AIR, (guiHandler, player, inventory, i, event) -> event.getCurrentItem().getType().equals(Material.PLAYER_HEAD))));
            registerButton(new ActionButton("player_head.texture.apply", new ButtonState("player_head.texture.apply", Material.GREEN_CONCRETE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                if (inventory.getItem(38) != null && inventory.getItem(38).getType().equals(Material.PLAYER_HEAD)) {
                    ((TestCache) guiHandler.getCustomCache()).getItems().getItem().setPlayerHeadValue(new ItemBuilder(inventory.getItem(38)).getPlayerHeadValue());
                }
                return true;
            })));
            registerButton(new ChatInputButton("player_head.owner", new ButtonState("player_head.owner", Material.NAME_TAG), (guiHandler, player, s, args) -> {
                ItemMeta itemMeta = ((TestCache) guiHandler.getCustomCache()).getItems().getItem().getItemMeta();
                if (!(itemMeta instanceof SkullMeta)) {
                    return true;
                }
                try {
                    UUID uuid = UUID.fromString(args[0]);
                    ((SkullMeta) itemMeta).setOwningPlayer(Bukkit.getOfflinePlayer(uuid));
                    ((TestCache) guiHandler.getCustomCache()).getItems().getItem().setItemMeta(itemMeta);
                } catch (IllegalArgumentException e) {
                    return true;
                }
                return false;
            }));
        }

        //POTION SETTINGS
        registerButton(new ActionButton("potion.option", new ButtonState("potion.option", Material.POTION, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).setSubSetting("potion");
            return true;
        })));
        {
            registerButton(new ChatInputButton("potion.add", new ButtonState("potion.add", Material.GREEN_CONCRETE), (guiHandler, player, s, args) -> {
                ItemMeta itemMeta = ((TestCache) guiHandler.getCustomCache()).getItems().getItem().getItemMeta();
                PotionEffectType type;
                if (!(itemMeta instanceof PotionMeta)) {
                    return true;
                }
                type = null;
                int duration = 0;
                int amplifier = 1;
                boolean ambient = true;
                boolean particles = true;
                if (args.length >= 3) {
                    try {
                        type = PotionEffectType.getByName(args[0]);
                        duration = Integer.parseInt(args[1]);
                        amplifier = Integer.parseInt(args[2]);
                        if (args.length == 5) {
                            ambient = Boolean.valueOf(args[3].toLowerCase());
                            particles = Boolean.valueOf(args[4].toLowerCase());
                        }
                    } catch (NumberFormatException e) {
                        api.sendPlayerMessage(player, "item_creator", "main_menu", "potion.error_number");
                        return true;
                    }
                }
                if (type != null) {
                    PotionEffect potionEffect = new PotionEffect(type, duration, amplifier, ambient, particles);
                    ((PotionMeta) itemMeta).addCustomEffect(potionEffect, true);

                    api.sendPlayerMessage(player, "item_creator", "main_menu", "potion.success", new String[]{"%TYPE%", type.getName()}, new String[]{"%DUR%", String.valueOf(duration)}, new String[]{"%AMP%", String.valueOf(amplifier)}, new String[]{"%AMB%", String.valueOf(ambient)}, new String[]{"%PAR%", String.valueOf(particles)});
                    ((TestCache) guiHandler.getCustomCache()).getItems().getItem().setItemMeta(itemMeta);
                    return false;

                }
                api.sendPlayerMessage(player, "item_creator", "main_menu", "potion.wrong_args");
                return true;
            }));
            registerButton(new ChatInputButton("potion.remove", new ButtonState("potion.remove", Material.RED_CONCRETE), (guiHandler, player, s, args) -> {
                ItemMeta itemMeta = ((TestCache) guiHandler.getCustomCache()).getItems().getItem().getItemMeta();
                PotionEffectType type;
                if (!(itemMeta instanceof PotionMeta)) {
                    return true;
                }
                type = PotionEffectType.getByName(args[0]);
                if (type != null) {
                    ((PotionMeta) itemMeta).removeCustomEffect(type);
                    ((TestCache) guiHandler.getCustomCache()).getItems().getItem().setItemMeta(itemMeta);
                    return false;
                }
                api.sendPlayerMessage(player, "item_creator", "main_menu", "potion.invalid_name", new String[]{"%NAME%", args[0]});
                return true;
            }));
        }

        //Unbreakable Setting
        registerButton(new ToggleButton("unbreakable", new ButtonState("unbreakable.enabled", Material.BEDROCK, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ItemMeta itemMeta = ((TestCache) guiHandler.getCustomCache()).getItems().getItem().getItemMeta();
            itemMeta.setUnbreakable(false);
            ((TestCache) guiHandler.getCustomCache()).getItems().getItem().setItemMeta(itemMeta);
            return true;
        }), new ButtonState("unbreakable.disabled", Material.GLASS, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ItemMeta itemMeta = ((TestCache) guiHandler.getCustomCache()).getItems().getItem().getItemMeta();
            itemMeta.setUnbreakable(true);
            ((TestCache) guiHandler.getCustomCache()).getItems().getItem().setItemMeta(itemMeta);
            return true;
        })));

        //DAMAGE Settings
        registerButton(new ActionButton("damage.option", new ButtonState("damage.option", Material.IRON_SWORD, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).setSubSetting("damage");
            return true;
        })));
        {
            registerButton(new ChatInputButton("damage.set", new ButtonState("damage.set", Material.GREEN_CONCRETE), (guiHandler, player, s, strings) -> {
                ItemMeta itemMeta = ((TestCache) guiHandler.getCustomCache()).getItems().getItem().getItemMeta();
                if (!(itemMeta instanceof Damageable)) {
                    return true;
                }
                try {
                    int value = Integer.parseInt(s);
                    ((Damageable) itemMeta).setDamage(value);
                    ((TestCache) guiHandler.getCustomCache()).getItems().getItem().setItemMeta(itemMeta);
                    api.sendPlayerMessage(player, "item_creator", "main_menu", "damage.value_success", new String[]{"%VALUE%", String.valueOf(value)});
                } catch (NumberFormatException e) {
                    api.sendPlayerMessage(player, "item_creator", "main_menu", "damage.invalid_value", new String[]{"%VALUE%", s});
                    return true;
                }
                return false;
            }));
            registerButton(new ActionButton("damage.reset", new ButtonState("damage.reset", Material.RED_CONCRETE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                ItemMeta itemMeta = ((TestCache) guiHandler.getCustomCache()).getItems().getItem().getItemMeta();
                if (itemMeta instanceof Damageable) {
                    ((Damageable) itemMeta).setDamage(0);
                }
                ((TestCache) guiHandler.getCustomCache()).getItems().getItem().setItemMeta(itemMeta);
                return true;
            })));
        }

        //REPAIR_COST Settings
        registerButton(new ActionButton("repair_cost.option", new ButtonState("repair_cost.option", Material.EXPERIENCE_BOTTLE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).setSubSetting("repair_cost");
            return true;
        })));
        {
            registerButton(new ChatInputButton("repair_cost.set", new ButtonState("repair_cost.set", Material.GREEN_CONCRETE), (guiHandler, player, s, strings) -> {
                ItemMeta itemMeta = ((TestCache) guiHandler.getCustomCache()).getItems().getItem().getItemMeta();
                try {
                    int value = Integer.parseInt(s);
                    ((Repairable) itemMeta).setRepairCost(value);
                    ((TestCache) guiHandler.getCustomCache()).getItems().getItem().setItemMeta(itemMeta);
                    api.sendPlayerMessage(player, "item_creator", "main_menu", "repair_cost.value_success", new String[]{"%VALUE%", String.valueOf(value)});
                } catch (NumberFormatException e) {
                    api.sendPlayerMessage(player, "item_creator", "main_menu", "repair_cost.invalid_value", new String[]{"%VALUE%", s});
                    return true;
                }
                return false;
            }));
            registerButton(new ActionButton("repair_cost.reset", new ButtonState("repair_cost.reset", Material.RED_CONCRETE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                ItemMeta itemMeta = ((TestCache) guiHandler.getCustomCache()).getItems().getItem().getItemMeta();
                if (itemMeta instanceof Repairable) {
                    ((Repairable) itemMeta).setRepairCost(0);
                }
                ((TestCache) guiHandler.getCustomCache()).getItems().getItem().setItemMeta(itemMeta);
                return true;
            })));
        }

        //CUSTOM_MODEL_DATA Settings
        registerButton(new ActionButton("custom_model_data.option", new ButtonState("custom_model_data.option", Material.REDSTONE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).setSubSetting("custom_model_data");
            return true;
        })));
        {
            registerButton(new ChatInputButton("custom_model_data.set", new ButtonState("custom_model_data.set", Material.GREEN_CONCRETE, (hashMap, guiHandler, player, itemStack, slot, help) -> {
                Items items = ((TestCache) guiHandler.getCustomCache()).getItems();
                hashMap.put("%VAR%", (items.getItem().hasItemMeta() && items.getItem().getItemMeta().hasCustomModelData() ? items.getItem().getItemMeta().getCustomModelData() : "&7&l/") + "");
                return itemStack;
            }), (guiHandler, player, s, strings) -> {
                ItemMeta itemMeta = ((TestCache) guiHandler.getCustomCache()).getItems().getItem().getItemMeta();
                if (!(itemMeta instanceof Repairable)) {
                    return true;
                }
                try {
                    int value = Integer.parseInt(s);
                    itemMeta.setCustomModelData(value);
                    ((TestCache) guiHandler.getCustomCache()).getItems().getItem().setItemMeta(itemMeta);
                    api.sendPlayerMessage(player, "item_creator", "main_menu", "custom_model_data.success", new String[]{"%VALUE%", String.valueOf(value)});
                } catch (NumberFormatException e) {
                    api.sendPlayerMessage(player, "item_creator", "main_menu", "custom_model_data.invalid_value", new String[]{"%VALUE%", s});
                    return true;
                }
                return false;
            }));
            registerButton(new ActionButton("custom_model_data.reset", new ButtonState("custom_model_data.reset", Material.RED_CONCRETE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                ItemMeta itemMeta = ((TestCache) guiHandler.getCustomCache()).getItems().getItem().getItemMeta();
                itemMeta.setCustomModelData(null);
                ((TestCache) guiHandler.getCustomCache()).getItems().getItem().setItemMeta(itemMeta);
                return true;
            })));
        }

        //CONSUME SETTINGS
        registerButton(new ActionButton("consume.option", new ButtonState("consume.option", Material.ITEM_FRAME, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).setSubSetting("consume");
            return true;
        })));
        {
            registerButton(new ChatInputButton("consume.durability_cost.enabled", new ButtonState("consume.durability_cost.enabled", Material.DROPPER, (hashMap, guiHandler, player, itemStack, slot, help) -> {
                hashMap.put("%VAR%", ((TestCache) guiHandler.getCustomCache()).getItems().getItem().getDurabilityCost());
                return itemStack;
            }), (guiHandler, player, s, strings) -> {
                try {
                    int value = Integer.parseInt(s);
                    ((TestCache) guiHandler.getCustomCache()).getItems().getItem().setDurabilityCost(value);
                    api.sendPlayerMessage(player, "item_creator", "main_menu", "consume.valid", new String[]{"%VALUE%", String.valueOf(value)});
                } catch (NumberFormatException e) {
                    api.sendPlayerMessage(player, "item_creator", "main_menu", "consume.invalid", new String[]{"%VALUE%", s});
                    return true;
                }
                return false;
            }));
            registerButton(new DummyButton("consume.durability_cost.disabled", new ButtonState("consume.durability_cost.disabled", Material.DROPPER)));

            registerButton(new ToggleButton("consume.consume_item", new ButtonState("consume.consume_item.enabled", Material.GREEN_CONCRETE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                ((TestCache) guiHandler.getCustomCache()).getItems().getItem().setConsumed(false);
                return true;
            }), new ButtonState("consume.consume_item.disabled", Material.RED_CONCRETE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                ((TestCache) guiHandler.getCustomCache()).getItems().getItem().setConsumed(true);
                return true;
            })));

            registerButton(new DummyButton("consume.replacement.enabled", new ButtonState("consume.replacement.enabled", Material.GREEN_CONCRETE, null)));
            registerButton(new DummyButton("consume.replacement.disabled", new ButtonState("consume.replacement.disabled", Material.RED_CONCRETE, null)));

            registerButton(new ItemInputButton("consume.replacement", new ButtonState("", Material.AIR, new ButtonActionRender() {
                @Override
                public boolean run(GuiHandler guiHandler, Player player, Inventory inventory, int slot, InventoryClickEvent inventoryClickEvent) {
                    TestCache cache = ((TestCache) guiHandler.getCustomCache());
                    Bukkit.getScheduler().runTask(customCrafting, () -> {
                        ItemStack replacement = inventory.getItem(slot);
                        if (replacement != null) {
                            cache.getItems().getItem().setReplacement(CustomItem.getReferenceByItemStack(replacement).getApiReference());
                        } else {
                            cache.getItems().getItem().setReplacement(null);
                        }
                    });
                    return false;
                }

                @Override
                public ItemStack render(HashMap<String, Object> values, GuiHandler guiHandler, Player player, ItemStack itemStack, int slot, boolean help) {
                    return ((TestCache) guiHandler.getCustomCache()).getItems().getItem().hasReplacement() ? new CustomItem(((TestCache) guiHandler.getCustomCache()).getItems().getItem().getReplacement()).create() : new ItemStack(Material.AIR);
                }
            })));
        }

        //FUEL Settings
        registerButton(new ActionButton("fuel.option", new ButtonState("fuel.option", Material.COAL, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).setSubSetting("fuel");
            return true;
        })));
        {
            registerButton(new ChatInputButton("fuel.burn_time.set", new ButtonState("fuel.burn_time.set", Material.GREEN_CONCRETE, (hashMap, guiHandler, player, itemStack, slot, help) -> {
                hashMap.put("%VAR%", ((TestCache) guiHandler.getCustomCache()).getItems().getItem().getBurnTime());
                return itemStack;
            }), (guiHandler, player, s, strings) -> {
                try {
                    int value = Integer.parseInt(s);
                    ((TestCache) guiHandler.getCustomCache()).getItems().getItem().setBurnTime(value);
                    api.sendPlayerMessage(player, "item_creator", "main_menu", "fuel.value_success", new String[]{"%VALUE%", String.valueOf(value)});
                } catch (NumberFormatException e) {
                    api.sendPlayerMessage(player, "item_creator", "main_menu", "fuel.invalid_value", new String[]{"%VALUE%", s});
                    return true;
                }
                return false;
            }));
            registerButton(new ActionButton("fuel.burn_time.reset", new ButtonState("fuel.burn_time.reset", Material.RED_CONCRETE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                ((TestCache) guiHandler.getCustomCache()).getItems().getItem().setBurnTime(0);
                return true;
            })));
            registerButton(new ToggleButton("fuel.furnace", new ButtonState("fuel.furnace.enabled", Material.FURNACE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                ((TestCache) guiHandler.getCustomCache()).getItems().getItem().getAllowedBlocks().remove(Material.FURNACE);
                return true;
            }), new ButtonState("fuel.furnace.disabled", Material.FURNACE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                ((TestCache) guiHandler.getCustomCache()).getItems().getItem().getAllowedBlocks().add(Material.FURNACE);
                return true;
            })));
            registerButton(new ToggleButton("fuel.blast_furnace", new ButtonState("fuel.blast_furnace.enabled", Material.BLAST_FURNACE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                ((TestCache) guiHandler.getCustomCache()).getItems().getItem().getAllowedBlocks().remove(Material.BLAST_FURNACE);
                return true;
            }), new ButtonState("fuel.blast_furnace.disabled", Material.BLAST_FURNACE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                ((TestCache) guiHandler.getCustomCache()).getItems().getItem().getAllowedBlocks().add(Material.BLAST_FURNACE);
                return true;
            })));
            registerButton(new ToggleButton("fuel.smoker", new ButtonState("fuel.smoker.enabled", Material.SMOKER, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                ((TestCache) guiHandler.getCustomCache()).getItems().getItem().getAllowedBlocks().remove(Material.SMOKER);
                return true;
            }), new ButtonState("fuel.smoker.disabled", Material.SMOKER, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                ((TestCache) guiHandler.getCustomCache()).getItems().getItem().getAllowedBlocks().add(Material.SMOKER);
                return true;
            })));
        }

        //CUSTOM_DURABILITY_COST Settings
        registerButton(new ActionButton("custom_durability.option", new ButtonState("custom_durability.option", Material.DIAMOND_SWORD, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).setSubSetting("custom_durability");
            return true;
        })));
        {
            registerButton(new ActionButton("custom_durability.remove", new ButtonState("custom_durability.remove", Material.RED_CONCRETE_POWDER, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                ((TestCache) guiHandler.getCustomCache()).getItems().getItem().removeCustomDurability();
                return true;
            })));
            registerButton(new ChatInputButton("custom_durability.set_durability", new ButtonState("custom_durability.set_durability", Material.GREEN_CONCRETE, (values, guiHandler, player, itemStack, slot, help) -> {
                values.put("%VAR%", ((TestCache) guiHandler.getCustomCache()).getItems().getItem().getCustomDurability());
                return itemStack;
            }), (guiHandler, player, s, strings) -> {
                try {
                    ((TestCache) guiHandler.getCustomCache()).getItems().getItem().setCustomDurability(Integer.parseInt(strings[0]));
                } catch (NumberFormatException ex) {
                    return true;
                }
                guiHandler.openCluster();
                return false;
            }));
            registerButton(new ChatInputButton("custom_durability.set_damage", new ButtonState("custom_durability.set_damage", Material.RED_CONCRETE, (values, guiHandler, player, itemStack, slot, help) -> {
                Items items = ((TestCache) guiHandler.getCustomCache()).getItems();
                values.put("%VAR%", items.getItem().getCustomDamage());
                return itemStack;
            }), (guiHandler, player, s, strings) -> {
                try {
                    ((TestCache) guiHandler.getCustomCache()).getItems().getItem().setCustomDamage(Integer.parseInt(strings[0]));
                } catch (NumberFormatException ex) {
                    return true;
                }
                guiHandler.openCluster();
                return false;
            }));
            registerButton(new ChatInputButton("custom_durability.set_tag", new ButtonState("custom_durability.set_tag", Material.NAME_TAG, (values, guiHandler, player, itemStack, slot, help) -> {
                Items items = ((TestCache) guiHandler.getCustomCache()).getItems();
                values.put("%VAR%", items.getItem().getCustomDurabilityTag());
                return itemStack;
            }), (guiHandler, player, s, strings) -> {
                try {
                    ((TestCache) guiHandler.getCustomCache()).getItems().getItem().setCustomDurabilityTag("&r" + s);
                } catch (NumberFormatException ex) {
                    return true;
                }
                guiHandler.openCluster();
                return false;
            }));
        }

        //LOCALIZED_NAME Settings
        registerButton(new ActionButton("localized_name.option", new ButtonState("localized_name.option", Material.NAME_TAG, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).setSubSetting("localized_name");
            return true;
        })));
        {
            registerButton(new ChatInputButton("localized_name.set", new ButtonState("localized_name.set", Material.NAME_TAG, (hashMap, guiHandler, player, itemStack, i, b) -> {
                hashMap.put("%VAR%", ((TestCache) guiHandler.getCustomCache()).getItems().getItem().getItemMeta().getLocalizedName());
                return itemStack;
            }), (guiHandler, player, s, strings) -> {
                ItemMeta itemMeta = ((TestCache) guiHandler.getCustomCache()).getItems().getItem().getItemMeta();
                itemMeta.setLocalizedName(WolfyUtilities.translateColorCodes(s));
                ((TestCache) guiHandler.getCustomCache()).getItems().getItem().setItemMeta(itemMeta);
                return false;
            }));
            registerButton(new ActionButton("localized_name.remove", new ButtonState("localized_name.remove", Material.NAME_TAG, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                ItemMeta itemMeta = ((TestCache) guiHandler.getCustomCache()).getItems().getItem().getItemMeta();
                itemMeta.setLocalizedName(null);
                ((TestCache) guiHandler.getCustomCache()).getItems().getItem().setItemMeta(itemMeta);
                return true;
            })));
        }

        //Permission
        registerButton(new ActionButton("permission.option", new ButtonState("permission.option", Material.BARRIER, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).setSubSetting("permission");
            return true;
        })));
        {
            registerButton(new ChatInputButton("permission.set", new ButtonState("permission.set", Material.GREEN_CONCRETE, (hashMap, guiHandler, player, itemStack, i, b) -> {
                String perm = ((TestCache) guiHandler.getCustomCache()).getItems().getItem().getPermission();
                hashMap.put("%VAR%", perm.isEmpty() ? "none" : perm);
                return itemStack;
            }), (guiHandler, player, s, strings) -> {
                ((TestCache) guiHandler.getCustomCache()).getItems().getItem().setPermission(s.replace(" ", "."));
                return false;
            }));
            registerButton(new ActionButton("permission.remove", new ButtonState("permission.remove", Material.RED_CONCRETE_POWDER, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                ((TestCache) guiHandler.getCustomCache()).getItems().getItem().setPermission("");
                return true;
            })));
        }

        //Rarity Percentage
        registerButton(new ActionButton("rarity.option", new ButtonState("rarity.option", Material.DIAMOND, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).setSubSetting("rarity");
            return true;
        })));
        {
            registerButton(new ChatInputButton("rarity.set", new ButtonState("rarity.set", Material.GREEN_CONCRETE, (hashMap, guiHandler, player, itemStack, i, b) -> {
                hashMap.put("%VAR%", ((TestCache) guiHandler.getCustomCache()).getItems().getItem().getRarityPercentage() + "§8(§7" + (((TestCache) guiHandler.getCustomCache()).getItems().getItem().getRarityPercentage() * 100) + "%§8)");
                return itemStack;
            }), (guiHandler, player, s, strings) -> {
                try {
                    ((TestCache) guiHandler.getCustomCache()).getItems().getItem().setRarityPercentage(Double.parseDouble(s));
                } catch (NumberFormatException ex) {
                    return true;
                }
                return false;
            }));
            registerButton(new ActionButton("rarity.reset", new ButtonState("rarity.reset", Material.RED_CONCRETE_POWDER, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                ((TestCache) guiHandler.getCustomCache()).getItems().getItem().setRarityPercentage(1.0d);
                return true;
            })));
        }

        registerButton(new ActionButton("persistent_data.option", new ButtonState("persistent_data.option", Material.BOOKSHELF, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).setSubSetting("persistent_data");
            return true;
        })));
        {

        }

        //Elite Workbench Settings
        registerButton(new ActionButton("elite_workbench.option", new ButtonState("elite_workbench.option", Material.CRAFTING_TABLE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).setSubSetting("elite_workbench");
            return true;
        })));
        {
            registerButton(new ActionButton("elite_workbench.particles", new ButtonState("elite_workbench.particles", Material.FIREWORK_ROCKET, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                ((TestCache) guiHandler.getCustomCache()).setSubSetting("particle_effects");
                return true;
            })));
            registerButton(new MultipleChoiceButton("elite_workbench.grid_size",
                    new ButtonState("elite_workbench.grid_size.size_3", PlayerHeadUtils.getViaURL("9e95293acbcd4f55faf5947bfc5135038b275a7ab81087341b9ec6e453e839"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                        ((EliteWorkbenchData) ((TestCache) guiHandler.getCustomCache()).getItems().getItem().getCustomData("elite_workbench")).setGridSize(4);
                        return true;
                    }),
                    new ButtonState("elite_workbench.grid_size.size_4", PlayerHeadUtils.getViaURL("cbfb41f866e7e8e593659986c9d6e88cd37677b3f7bd44253e5871e66d1d424"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                        ((EliteWorkbenchData) ((TestCache) guiHandler.getCustomCache()).getItems().getItem().getCustomData("elite_workbench")).setGridSize(5);
                        return true;
                    }),
                    new ButtonState("elite_workbench.grid_size.size_5", PlayerHeadUtils.getViaURL("14d844fee24d5f27ddb669438528d83b684d901b75a6889fe7488dfc4cf7a1c"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                        ((EliteWorkbenchData) ((TestCache) guiHandler.getCustomCache()).getItems().getItem().getCustomData("elite_workbench")).setGridSize(6);
                        return true;
                    }),
                    new ButtonState("elite_workbench.grid_size.size_6", PlayerHeadUtils.getViaURL("faff2eb498e5c6a04484f0c9f785b448479ab213df95ec91176a308a12add70"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                        ((EliteWorkbenchData) ((TestCache) guiHandler.getCustomCache()).getItems().getItem().getCustomData("elite_workbench")).setGridSize(3);
                        return true;
                    })));
            registerButton(new ToggleButton("elite_workbench.toggle", new ButtonState("elite_workbench.toggle.enabled", Material.GREEN_CONCRETE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                ((EliteWorkbenchData) ((TestCache) guiHandler.getCustomCache()).getItems().getItem().getCustomData("elite_workbench")).setEnabled(false);
                return true;
            }), new ButtonState("elite_workbench.toggle.disabled", Material.RED_CONCRETE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                ((EliteWorkbenchData) ((TestCache) guiHandler.getCustomCache()).getItems().getItem().getCustomData("elite_workbench")).setEnabled(true);
                return true;
            })));
            registerButton(new ToggleButton("elite_workbench.advanced_recipes", new ButtonState("elite_workbench.advanced_recipes.enabled", Material.GREEN_CONCRETE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                ((EliteWorkbenchData) ((TestCache) guiHandler.getCustomCache()).getItems().getItem().getCustomData("elite_workbench")).setAdvancedRecipes(false);
                return true;
            }), new ButtonState("elite_workbench.advanced_recipes.disabled", Material.RED_CONCRETE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                ((EliteWorkbenchData) ((TestCache) guiHandler.getCustomCache()).getItems().getItem().getCustomData("elite_workbench")).setAdvancedRecipes(true);
                return true;
            })));
        }

        //Advanced Knowledgebook Settings
        registerButton(new ActionButton("knowledge_book.option", new ButtonState("knowledge_book.option", Material.KNOWLEDGE_BOOK, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).setSubSetting("knowledge_book");
            return true;
        })));
        {
            registerButton(new ToggleButton("knowledge_book.toggle", new ButtonState("knowledge_book.toggle.enabled", Material.GREEN_CONCRETE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                ((KnowledgeBookData) ((TestCache) guiHandler.getCustomCache()).getItems().getItem().getCustomData("knowledge_book")).setEnabled(false);
                return true;
            }), new ButtonState("knowledge_book.toggle.disabled", Material.RED_CONCRETE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                ((KnowledgeBookData) ((TestCache) guiHandler.getCustomCache()).getItems().getItem().getCustomData("knowledge_book")).setEnabled(true);
                return true;
            })));
        }

        registerButton(new ActionButton("armor_slots.option", new ButtonState("armor_slots.option", Material.IRON_HELMET, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).setSubSetting("armor_slots");
            return true;
        })));
        {
            registerButton(new ArmorSlotToggleButton(EquipmentSlot.HEAD, Material.DIAMOND_HELMET));
            registerButton(new ArmorSlotToggleButton(EquipmentSlot.CHEST, Material.DIAMOND_CHESTPLATE));
            registerButton(new ArmorSlotToggleButton(EquipmentSlot.LEGS, Material.DIAMOND_LEGGINGS));
            registerButton(new ArmorSlotToggleButton(EquipmentSlot.FEET, Material.DIAMOND_BOOTS));
        }

        registerButton(new ActionButton("particle_effects.option", new ButtonState("particle_effects.option", Material.FIREWORK_ROCKET, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).setSubSetting("particle_effects");
            return true;
        })));
        {
            registerButton(new DummyButton("particle_effects.head", new ButtonState("particle_effects.head", Material.IRON_HELMET)));
            registerButton(new ParticleEffectSelectButton(ParticleEffect.Action.HEAD));
            registerButton(new DummyButton("particle_effects.chest", new ButtonState("particle_effects.chest", Material.IRON_CHESTPLATE)));
            registerButton(new ParticleEffectSelectButton(ParticleEffect.Action.CHEST));
            registerButton(new DummyButton("particle_effects.legs", new ButtonState("particle_effects.legs", Material.IRON_LEGGINGS)));
            registerButton(new ParticleEffectSelectButton(ParticleEffect.Action.LEGS));
            registerButton(new DummyButton("particle_effects.feet", new ButtonState("particle_effects.feet", Material.IRON_BOOTS)));
            registerButton(new ParticleEffectSelectButton(ParticleEffect.Action.FEET));
            registerButton(new DummyButton("particle_effects.hand", new ButtonState("particle_effects.hand", Material.IRON_SWORD)));
            registerButton(new ParticleEffectSelectButton(ParticleEffect.Action.HAND));
            registerButton(new DummyButton("particle_effects.off_hand", new ButtonState("particle_effects.off_hand", Material.SHIELD)));
            registerButton(new ParticleEffectSelectButton(ParticleEffect.Action.OFF_HAND));
            registerButton(new DummyButton("particle_effects.block", new ButtonState("particle_effects.block", Material.GRASS_BLOCK)));
            registerButton(new ParticleEffectSelectButton(ParticleEffect.Action.BLOCK));
        }

        for (String meta : dummyMetaSettings.getMetas()) {
            registerButton(new MetaIgnoreButton(meta));
        }
    }

    @Override
    public void onUpdateAsync(GuiUpdate event) {
        GuiHandler guiHandler = event.getGuiHandler();
        TestCache cache = ((TestCache) guiHandler.getCustomCache());
        Items items = cache.getItems();
        CustomItem customItem = items.getItem();
        ItemStack item = customItem.create();

        event.setButton(0, "back");
        event.setButton(13, "item_input");

        PlayerStatistics playerStatistics = CustomCrafting.getPlayerStatistics(event.getPlayer());
        event.setButton(4, "none", playerStatistics.getDarkMode() ? "glass_gray" : "glass_white");
        event.setButton(12, "none", playerStatistics.getDarkMode() ? "glass_gray" : "glass_white");
        event.setButton(14, "none", playerStatistics.getDarkMode() ? "glass_gray" : "glass_white");
        event.setButton(22, "none", playerStatistics.getDarkMode() ? "glass_gray" : "glass_white");

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
                ((ToggleButton) event.getGuiWindow().getButton("unbreakable")).setState(event.getGuiHandler(), item.getItemMeta().isUnbreakable());
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
                event.setButton(slot + i, "none", playerStatistics.getDarkMode() ? "glass_gray" : "glass_white");
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
                    ((ToggleButton) event.getGuiWindow().getButton("flags.attributes")).setState(guiHandler, item.getItemMeta().hasItemFlag(ItemFlag.HIDE_ATTRIBUTES));
                    ((ToggleButton) event.getGuiWindow().getButton("flags.unbreakable")).setState(guiHandler, item.getItemMeta().hasItemFlag(ItemFlag.HIDE_UNBREAKABLE));
                    ((ToggleButton) event.getGuiWindow().getButton("flags.destroys")).setState(guiHandler, item.getItemMeta().hasItemFlag(ItemFlag.HIDE_DESTROYS));
                    ((ToggleButton) event.getGuiWindow().getButton("flags.placed_on")).setState(guiHandler, item.getItemMeta().hasItemFlag(ItemFlag.HIDE_PLACED_ON));
                    ((ToggleButton) event.getGuiWindow().getButton("flags.potion_effects")).setState(guiHandler, item.getItemMeta().hasItemFlag(ItemFlag.HIDE_POTION_EFFECTS));
                    ((ToggleButton) event.getGuiWindow().getButton("flags.enchants")).setState(guiHandler, item.getItemMeta().hasItemFlag(ItemFlag.HIDE_ENCHANTS));
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
                    ((ToggleButton) event.getGuiWindow().getButton("fuel.furnace")).setState(event.getGuiHandler(), customItem.getAllowedBlocks().contains(Material.FURNACE));
                    ((ToggleButton) event.getGuiWindow().getButton("fuel.blast_furnace")).setState(event.getGuiHandler(), customItem.getAllowedBlocks().contains(Material.BLAST_FURNACE));
                    ((ToggleButton) event.getGuiWindow().getButton("fuel.smoker")).setState(event.getGuiHandler(), customItem.getAllowedBlocks().contains(Material.SMOKER));
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
                    ((ToggleButton) event.getGuiWindow().getButton("consume.consume_item")).setState(event.getGuiHandler(), customItem.isConsumed());
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
                        ((MultipleChoiceButton) event.getGuiWindow().getButton("elite_workbench.grid_size")).setState(event.getGuiHandler(), ((EliteWorkbenchData) customItem.getCustomData("elite_workbench")).getGridSize() - 3);
                        ((ToggleButton) event.getGuiWindow().getButton("elite_workbench.toggle")).setState(event.getGuiHandler(), ((EliteWorkbenchData) customItem.getCustomData("elite_workbench")).isEnabled());
                        ((ToggleButton) event.getGuiWindow().getButton("elite_workbench.advanced_recipes")).setState(event.getGuiHandler(), ((EliteWorkbenchData) customItem.getCustomData("elite_workbench")).isAdvancedRecipes());
                        event.setButton(37, "elite_workbench.particles");
                        event.setButton(39, "elite_workbench.grid_size");
                        event.setButton(41, "elite_workbench.toggle");
                        event.setButton(43, "elite_workbench.advanced_recipes");
                    }
                    break;
                case "knowledge_book":
                    ((ToggleButton) event.getGuiWindow().getButton("elite_workbench.toggle")).setState(event.getGuiHandler(), ((KnowledgeBookData) customItem.getCustomData("knowledge_book")).isEnabled());
                    event.setButton(40, "knowledge_book.toggle");
                    break;
                case "armor_slots":
                    ((ToggleButton) event.getGuiWindow().getButton("armor_slots.head")).setState(event.getGuiHandler(), customItem.hasEquipmentSlot(EquipmentSlot.HEAD));
                    ((ToggleButton) event.getGuiWindow().getButton("armor_slots.chest")).setState(event.getGuiHandler(), customItem.hasEquipmentSlot(EquipmentSlot.CHEST));
                    ((ToggleButton) event.getGuiWindow().getButton("armor_slots.legs")).setState(event.getGuiHandler(), customItem.hasEquipmentSlot(EquipmentSlot.LEGS));
                    ((ToggleButton) event.getGuiWindow().getButton("armor_slots.feet")).setState(event.getGuiHandler(), customItem.hasEquipmentSlot(EquipmentSlot.FEET));
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
