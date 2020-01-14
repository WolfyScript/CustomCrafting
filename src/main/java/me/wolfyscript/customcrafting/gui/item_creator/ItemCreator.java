package me.wolfyscript.customcrafting.gui.item_creator;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.custom_data.EliteWorkbenchData;
import me.wolfyscript.customcrafting.data.PlayerCache;
import me.wolfyscript.customcrafting.data.cache.items.Items;
import me.wolfyscript.customcrafting.gui.ExtendedGuiWindow;
import me.wolfyscript.customcrafting.gui.item_creator.buttons.MetaIgnoreButton;
import me.wolfyscript.customcrafting.utils.ChatUtils;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.custom_items.CustomItems;
import me.wolfyscript.utilities.api.custom_items.ItemConfig;
import me.wolfyscript.utilities.api.custom_items.MetaSettings;
import me.wolfyscript.utilities.api.inventory.GuiHandler;
import me.wolfyscript.utilities.api.inventory.GuiUpdateEvent;
import me.wolfyscript.utilities.api.inventory.GuiWindow;
import me.wolfyscript.utilities.api.inventory.InventoryAPI;
import me.wolfyscript.utilities.api.inventory.button.ButtonActionRender;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.buttons.*;
import me.wolfyscript.utilities.api.utils.ItemUtils;
import me.wolfyscript.utilities.api.utils.Legacy;
import me.wolfyscript.utilities.api.utils.item_builder.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
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

    public ItemCreator(InventoryAPI inventoryAPI) {
        super("main_menu", inventoryAPI, 54);
    }

    @Override
    public void onInit() {
        registerButton(new ActionButton("back", new ButtonState("none", "back", WolfyUtilities.getCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODY0Zjc3OWE4ZTNmZmEyMzExNDNmYTY5Yjk2YjE0ZWUzNWMxNmQ2NjllMTljNzVmZDFhN2RhNGJmMzA2YyJ9fX0="), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            if (CustomCrafting.getPlayerCache(player).getItems().isRecipeItem()) {
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
                Bukkit.getScheduler().runTaskLater(CustomCrafting.getInst(), () -> {
                    ItemStack item = inventory.getItem(i);
                    CustomCrafting.getPlayerCache(player).getItems().setItem(new CustomItem(item != null ? item : new ItemStack(Material.AIR)));
                    ((ToggleButton) guiWindow.getButton("unbreakable")).setState(guiHandler, (item != null && !item.getType().equals(Material.AIR)) && item.getItemMeta().isUnbreakable());
                }, 1);
                return false;
            }

            @Override
            public ItemStack render(HashMap<String, Object> hashMap, GuiHandler guiHandler, Player player, ItemStack itemStack, int slot, boolean help) {
                itemStack = CustomCrafting.getPlayerCache(player).getItems().getItem();
                return itemStack;
            }
        })));
        registerButton(new ActionButton("save_item", new ButtonState("save_item", Material.WRITABLE_BOOK, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            Items items = CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems();
            if (!items.getItem().getType().equals(Material.AIR)) {
                String id = items.getId();
                //TODO ITEM LIST
                sendMessage(player, "save.input.line1");
                openChat("save.input.line2", guiHandler, (guiHandler1, player1, s, args) -> {
                    if (args.length > 1) {
                        String namespace = args[0].toLowerCase(Locale.ROOT).replace(" ", "_");
                        String key = args[1].toLowerCase(Locale.ROOT).replace(" ", "_");
                        if (!CustomCrafting.VALID_NAMESPACEKEY.matcher(namespace).matches()) {
                            api.sendPlayerMessage(player1, "&cInvalid Namespace! Namespaces may only contain lowercase alphanumeric characters, periods, underscores, and hyphens!");
                            return true;
                        }
                        if (!CustomCrafting.VALID_NAMESPACEKEY.matcher(key).matches()) {
                            api.sendPlayerMessage(player1, "&cInvalid key! Keys may only contain lowercase alphanumeric characters, periods, underscores, and hyphens!");
                            return true;
                        }
                        saveItem(CustomCrafting.getPlayerCache(player1), namespace + ":" + key, items.getItem());
                        sendMessage(player, "save.success");
                        api.sendPlayerMessage(player1, "&6" + namespace + "/items/" + key);
                        return false;
                    }
                    return true;
                });
            }
            return true;
        })));

        registerButton(new ActionButton("apply_item", new ButtonState("apply_item", Material.GREEN_CONCRETE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            PlayerCache cache = CustomCrafting.getPlayerCache(guiHandler.getPlayer());
            if (!cache.getItems().getItem().getType().equals(Material.AIR)) {
                CustomItem customItem = cache.getItems().getItem();
                if (cache.getItems().isSaved()) {
                    saveItem(cache, cache.getItems().getId(), customItem);
                    customItem = CustomItems.getCustomItem(cache.getItems().getId());
                }
                cache.applyItem(customItem);
                guiHandler.openCluster("recipe_creator");
            }
            return true;
        })));

        registerButton(new ActionButton("page_next", new ButtonState("page_next", WolfyUtilities.getSkullViaURL("c86185b1d519ade585f184c34f3f3e20bb641deb879e81378e4eaf209287"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems().setPage(CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems().getPage() + 1);
            return true;
        })));
        registerButton(new ActionButton("page_previous", new ButtonState("page_previous", WolfyUtilities.getSkullViaURL("ad73cf66d31b83cd8b8644c15958c1b73c8d97323b801170c1d8864bb6a846d"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            if (CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems().getPage() > 0) {
                CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems().setPage(CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems().getPage() - 1);
            }
            return true;
        })));

        //DISPLAY NAME SETTINGS
        registerButton(new ActionButton("display_name.option", new ButtonState("display_name.option", Material.NAME_TAG, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            CustomCrafting.getPlayerCache(guiHandler.getPlayer()).setSubSetting("display_name");
            return true;
        })));
        registerButton(new ChatInputButton("display_name.set", new ButtonState("display_name.set", Material.GREEN_CONCRETE), (guiHandler, player, s, strings) -> {
            CustomItem itemStack = CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems().getItem();
            ItemMeta itemMeta = itemStack.getItemMeta();
            /*
            if (s.startsWith("&f")) {
                s = "&r" + s;
            }
            */
            itemMeta.setDisplayName(WolfyUtilities.translateColorCodes(s));
            itemStack.setItemMeta(itemMeta);
            return false;
        }));
        registerButton(new ActionButton("display_name.remove", new ButtonState("display_name.remove", Material.RED_CONCRETE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            CustomItem itemStack = CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems().getItem();
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(null);
            itemStack.setItemMeta(itemMeta);
            return true;
        })));

        //ENCHANT SETTINGS
        registerButton(new ActionButton("enchantments.option", new ButtonState("enchantments.option", Material.ENCHANTED_BOOK, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            CustomCrafting.getPlayerCache(guiHandler.getPlayer()).setSubSetting("enchantments");
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
                        CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems().getItem().addUnsafeEnchantment(enchantment, level);
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
                    CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems().getItem().removeEnchantment(enchantment);
                } else {
                    api.sendPlayerMessage(player, "none", "item_creator", "enchant.invalid_enchant", new String[]{"%ENCHANT%", args[0]});
                    return true;
                }
                return false;
            }));
        }

        //LORE SETTINGS
        registerButton(new ActionButton("lore.option", new ButtonState("lore.option", Material.WRITABLE_BOOK, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            CustomCrafting.getPlayerCache(guiHandler.getPlayer()).setSubSetting("lore");
            return true;
        })));
        {
            registerButton(new ChatInputButton("lore.add", new ButtonState("lore.add", Material.WRITABLE_BOOK), (guiHandler, player, s, strings) -> {
                CustomItem itemStack = CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems().getItem();
                ItemMeta itemMeta = itemStack.getItemMeta();
                List<String> lore = itemMeta.hasLore() ? itemMeta.getLore() : new ArrayList<>();
                if (s.equals("&empty")) {
                    lore.add("");
                } else {
                    lore.add(WolfyUtilities.translateColorCodes(s));
                }
                itemMeta.setLore(lore);
                CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems().getItem().setItemMeta(itemMeta);
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
            CustomCrafting.getPlayerCache(guiHandler.getPlayer()).setSubSetting("flags");
            return true;
        })));
        {
            registerButton(new ToggleButton("flags.enchants", new ButtonState("flags.enchants.enabled", Material.ENCHANTING_TABLE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                new ItemBuilder(CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems().getItem()).removeItemFlags(ItemFlag.HIDE_ENCHANTS);
                return true;
            }), new ButtonState("flags.enchants.disabled", Material.ENCHANTING_TABLE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                new ItemBuilder(CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems().getItem()).addItemFlags(ItemFlag.HIDE_ENCHANTS);
                return true;
            })));
            registerButton(new ToggleButton("flags.attributes", new ButtonState("flags.attributes.enabled", Material.ENCHANTED_GOLDEN_APPLE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                new ItemBuilder(CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems().getItem()).removeItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                return true;
            }), new ButtonState("flags.attributes.disabled", Material.ENCHANTED_GOLDEN_APPLE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                new ItemBuilder(CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems().getItem()).addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                return true;
            })));
            registerButton(new ToggleButton("flags.unbreakable", new ButtonState("flags.unbreakable.enabled", Material.BEDROCK, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                new ItemBuilder(CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems().getItem()).removeItemFlags(ItemFlag.HIDE_UNBREAKABLE);
                return true;
            }), new ButtonState("flags.unbreakable.disabled", Material.BEDROCK, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                new ItemBuilder(CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems().getItem()).addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
                return true;
            })));
            registerButton(new ToggleButton("flags.destroys", new ButtonState("flags.destroys.enabled", Material.TNT, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                new ItemBuilder(CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems().getItem()).removeItemFlags(ItemFlag.HIDE_DESTROYS);
                return true;
            }), new ButtonState("flags.destroys.disabled", Material.TNT, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                new ItemBuilder(CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems().getItem()).addItemFlags(ItemFlag.HIDE_DESTROYS);
                return true;
            })));
            registerButton(new ToggleButton("flags.placed_on", new ButtonState("flags.placed_on.enabled", Material.GRASS_BLOCK, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                new ItemBuilder(CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems().getItem()).removeItemFlags(ItemFlag.HIDE_PLACED_ON);
                return true;
            }), new ButtonState("flags.placed_on.disabled", Material.GRASS_BLOCK, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                new ItemBuilder(CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems().getItem()).addItemFlags(ItemFlag.HIDE_PLACED_ON);
                return true;
            })));
            registerButton(new ToggleButton("flags.potion_effects", new ButtonState("flags.potion_effects.enabled", Material.POTION, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                new ItemBuilder(CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems().getItem()).removeItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
                return true;
            }), new ButtonState("flags.potion_effects.disabled", Material.POTION, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                new ItemBuilder(CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems().getItem()).addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
                return true;
            })));
        }

        //attributes_modifiers SETTINGS
        registerButton(new ActionButton("attribute.option", new ButtonState("attribute.option", Material.ENCHANTED_GOLDEN_APPLE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            CustomCrafting.getPlayerCache(guiHandler.getPlayer()).setSubSetting("attribute");
            return true;
        })));
        {
            registerButton(new ActionButton("attribute.generic_max_health", new ButtonState("attribute.generic_max_health", Material.ENCHANTED_GOLDEN_APPLE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                CustomCrafting.getPlayerCache(guiHandler.getPlayer()).setSubSetting("attribute.generic_max_health");
                return true;
            })));
            registerButton(new ActionButton("attribute.generic_follow_range", new ButtonState("attribute.generic_follow_range", Material.ENDER_EYE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                CustomCrafting.getPlayerCache(guiHandler.getPlayer()).setSubSetting("attribute.generic_follow_range");
                return true;
            })));
            registerButton(new ActionButton("attribute.generic_knockback_resistance", new ButtonState("attribute.generic_knockback_resistance", Material.STICK, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                CustomCrafting.getPlayerCache(guiHandler.getPlayer()).setSubSetting("attribute.generic_knockback_resistance");
                return true;
            })));
            registerButton(new ActionButton("attribute.generic_movement_speed", new ButtonState("attribute.generic_movement_speed", Material.IRON_BOOTS, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                CustomCrafting.getPlayerCache(guiHandler.getPlayer()).setSubSetting("attribute.generic_movement_speed");
                return true;
            })));
            registerButton(new ActionButton("attribute.generic_flying_speed", new ButtonState("attribute.generic_flying_speed", Material.FIREWORK_ROCKET, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                CustomCrafting.getPlayerCache(guiHandler.getPlayer()).setSubSetting("attribute.generic_flying_speed");
                return true;
            })));
            registerButton(new ActionButton("attribute.generic_attack_damage", new ButtonState("attribute.generic_attack_damage", Material.DIAMOND_SWORD, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                CustomCrafting.getPlayerCache(guiHandler.getPlayer()).setSubSetting("attribute.generic_attack_damage");
                return true;
            })));
            registerButton(new ActionButton("attribute.generic_attack_speed", new ButtonState("attribute.generic_attack_speed", Material.DIAMOND_AXE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                CustomCrafting.getPlayerCache(guiHandler.getPlayer()).setSubSetting("attribute.generic_attack_speed");
                return true;
            })));
            registerButton(new ActionButton("attribute.generic_armor", new ButtonState("attribute.generic_armor", Material.CHAINMAIL_CHESTPLATE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                CustomCrafting.getPlayerCache(guiHandler.getPlayer()).setSubSetting("attribute.generic_armor");
                return true;
            })));
            registerButton(new ActionButton("attribute.generic_armor_toughness", new ButtonState("attribute.generic_armor_toughness", Material.DIAMOND_CHESTPLATE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                CustomCrafting.getPlayerCache(guiHandler.getPlayer()).setSubSetting("attribute.generic_armor_toughness");
                return true;
            })));
            registerButton(new ActionButton("attribute.generic_luck", new ButtonState("attribute.generic_luck", Material.NETHER_STAR, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                CustomCrafting.getPlayerCache(guiHandler.getPlayer()).setSubSetting("attribute.generic_luck");
                return true;
            })));
            registerButton(new ActionButton("attribute.horse_jump_strength", new ButtonState("attribute.horse_jump_strength", Material.DIAMOND_HORSE_ARMOR, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                CustomCrafting.getPlayerCache(guiHandler.getPlayer()).setSubSetting("attribute.horse_jump_strength");
                return true;
            })));
            registerButton(new ActionButton("attribute.zombie_spawn_reinforcements", new ButtonState("attribute.zombie_spawn_reinforcements", Material.ZOMBIE_HEAD, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                CustomCrafting.getPlayerCache(guiHandler.getPlayer()).setSubSetting("attribute.zombie_spawn_reinforcements");
                return true;
            })));
            registerButton(new ActionButton("attribute.add_number", new ButtonState("attribute.add_number", WolfyUtilities.getSkullViaURL("60b55f74681c68283a1c1ce51f1c83b52e2971c91ee34efcb598df3990a7e7"), new ButtonActionRender() {
                @Override
                public boolean run(GuiHandler guiHandler, Player player, Inventory inventory, int i, InventoryClickEvent inventoryClickEvent) {
                    CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems().setAttribOperation(AttributeModifier.Operation.ADD_NUMBER);
                    return true;
                }

                @Override
                public ItemStack render(HashMap<String, Object> replacements, GuiHandler guiHandler, Player player, ItemStack itemStack, int slot, boolean help) {
                    replacements.put("%C%", CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems().getAttribOperation().equals(AttributeModifier.Operation.ADD_NUMBER) ? "§a" : "§4");
                    return itemStack;
                }
            })));
            registerButton(new ActionButton("attribute.add_scalar", new ButtonState("attribute.add_scalar", WolfyUtilities.getSkullViaURL("57b1791bdc46d8a5c51729e8982fd439bb40513f64b5babee93294efc1c7"), new ButtonActionRender() {
                @Override
                public boolean run(GuiHandler guiHandler, Player player, Inventory inventory, int i, InventoryClickEvent inventoryClickEvent) {
                    CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems().setAttribOperation(AttributeModifier.Operation.ADD_SCALAR);
                    return true;
                }

                @Override
                public ItemStack render(HashMap<String, Object> replacements, GuiHandler guiHandler, Player player, ItemStack itemStack, int slot, boolean help) {
                    replacements.put("%C%", CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems().getAttribOperation().equals(AttributeModifier.Operation.ADD_SCALAR) ? "§a" : "§4");
                    return itemStack;
                }
            })));
            registerButton(new ActionButton("attribute.multiply_scalar_1", new ButtonState("attribute.multiply_scalar_1", WolfyUtilities.getSkullViaURL("a9f27d54ec5552c2ed8f8e1917e8a21cb98814cbb4bc3643c2f561f9e1e69f"), new ButtonActionRender() {
                @Override
                public boolean run(GuiHandler guiHandler, Player player, Inventory inventory, int i, InventoryClickEvent inventoryClickEvent) {
                    CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems().setAttribOperation(AttributeModifier.Operation.MULTIPLY_SCALAR_1);
                    return true;
                }

                @Override
                public ItemStack render(HashMap<String, Object> replacements, GuiHandler guiHandler, Player player, ItemStack itemStack, int slot, boolean help) {
                    replacements.put("%C%", CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems().getAttribOperation().equals(AttributeModifier.Operation.MULTIPLY_SCALAR_1) ? "§a" : "§4");
                    return itemStack;
                }
            })));
            registerButton(new ActionButton("attribute.slot_hand", new ButtonState("attribute.slot_hand", Material.IRON_SWORD, new ButtonActionRender() {
                @Override
                public boolean run(GuiHandler guiHandler, Player player, Inventory inventory, int i, InventoryClickEvent inventoryClickEvent) {
                    Items items = CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems();
                    items.setAttributeSlot(items.getAttributeSlot() == null ? EquipmentSlot.HAND : (items.getAttributeSlot().equals(EquipmentSlot.HAND) ? null : EquipmentSlot.HAND));
                    return true;
                }

                @Override
                public ItemStack render(HashMap<String, Object> hashMap, GuiHandler guiHandler, Player player, ItemStack itemStack, int slot, boolean help) {
                    if (CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems().isAttributeSlot(EquipmentSlot.HAND)) {
                        itemStack.setItemMeta(ItemUtils.setEnchantEffect(Objects.requireNonNull(itemStack.getItemMeta()), true));
                    }
                    return itemStack;
                }
            })));
            registerButton(new ActionButton("attribute.slot_off_hand", new ButtonState("attribute.slot_off_hand", Material.SHIELD, new ButtonActionRender() {
                @Override
                public boolean run(GuiHandler guiHandler, Player player, Inventory inventory, int i, InventoryClickEvent inventoryClickEvent) {
                    Items items = CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems();
                    items.setAttributeSlot(items.getAttributeSlot() == null ? EquipmentSlot.OFF_HAND : (items.getAttributeSlot().equals(EquipmentSlot.OFF_HAND) ? null : EquipmentSlot.OFF_HAND));
                    return true;
                }

                @Override
                public ItemStack render(HashMap<String, Object> hashMap, GuiHandler guiHandler, Player player, ItemStack itemStack, int slot, boolean help) {
                    if (CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems().isAttributeSlot(EquipmentSlot.OFF_HAND)) {
                        itemStack.setItemMeta(ItemUtils.setEnchantEffect(Objects.requireNonNull(itemStack.getItemMeta()), true));
                    }
                    return itemStack;
                }
            })));
            registerButton(new ActionButton("attribute.slot_feet", new ButtonState("attribute.slot_feet", Material.IRON_BOOTS, new ButtonActionRender() {
                @Override
                public boolean run(GuiHandler guiHandler, Player player, Inventory inventory, int i, InventoryClickEvent inventoryClickEvent) {
                    Items items = CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems();
                    items.setAttributeSlot(items.getAttributeSlot() == null ? EquipmentSlot.FEET : (items.getAttributeSlot().equals(EquipmentSlot.FEET) ? null : EquipmentSlot.FEET));
                    return true;
                }

                @Override
                public ItemStack render(HashMap<String, Object> hashMap, GuiHandler guiHandler, Player player, ItemStack itemStack, int slot, boolean help) {
                    if (CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems().isAttributeSlot(EquipmentSlot.FEET)) {
                        itemStack.setItemMeta(ItemUtils.setEnchantEffect(Objects.requireNonNull(itemStack.getItemMeta()), true));
                    }
                    return itemStack;
                }
            })));
            registerButton(new ActionButton("attribute.slot_legs", new ButtonState("attribute.slot_legs", Material.IRON_LEGGINGS, new ButtonActionRender() {
                @Override
                public boolean run(GuiHandler guiHandler, Player player, Inventory inventory, int i, InventoryClickEvent inventoryClickEvent) {
                    Items items = CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems();
                    items.setAttributeSlot(items.getAttributeSlot() == null ? EquipmentSlot.LEGS : (items.getAttributeSlot().equals(EquipmentSlot.LEGS) ? null : EquipmentSlot.LEGS));
                    return true;
                }

                @Override
                public ItemStack render(HashMap<String, Object> hashMap, GuiHandler guiHandler, Player player, ItemStack itemStack, int slot, boolean help) {
                    if (CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems().isAttributeSlot(EquipmentSlot.LEGS)) {
                        itemStack.setItemMeta(ItemUtils.setEnchantEffect(Objects.requireNonNull(itemStack.getItemMeta()), true));
                    }
                    return itemStack;
                }
            })));
            registerButton(new ActionButton("attribute.slot_chest", new ButtonState("attribute.slot_chest", Material.IRON_CHESTPLATE, new ButtonActionRender() {
                @Override
                public boolean run(GuiHandler guiHandler, Player player, Inventory inventory, int i, InventoryClickEvent inventoryClickEvent) {
                    Items items = CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems();
                    items.setAttributeSlot(items.getAttributeSlot() == null ? EquipmentSlot.CHEST : (items.getAttributeSlot().equals(EquipmentSlot.CHEST) ? null : EquipmentSlot.CHEST));
                    return true;
                }

                @Override
                public ItemStack render(HashMap<String, Object> hashMap, GuiHandler guiHandler, Player player, ItemStack itemStack, int slot, boolean help) {
                    if (CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems().isAttributeSlot(EquipmentSlot.CHEST)) {
                        itemStack.setItemMeta(ItemUtils.setEnchantEffect(Objects.requireNonNull(itemStack.getItemMeta()), true));
                    }
                    return itemStack;
                }
            })));
            registerButton(new ActionButton("attribute.slot_head", new ButtonState("attribute.slot_head", Material.IRON_HELMET, new ButtonActionRender() {
                @Override
                public boolean run(GuiHandler guiHandler, Player player, Inventory inventory, int i, InventoryClickEvent inventoryClickEvent) {
                    Items items = CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems();
                    items.setAttributeSlot(items.getAttributeSlot() == null ? EquipmentSlot.HEAD : (items.getAttributeSlot().equals(EquipmentSlot.HEAD) ? null : EquipmentSlot.HEAD));
                    return true;
                }

                @Override
                public ItemStack render(HashMap<String, Object> hashMap, GuiHandler guiHandler, Player player, ItemStack itemStack, int slot, boolean help) {
                    if (CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems().isAttributeSlot(EquipmentSlot.HEAD)) {
                        itemStack.setItemMeta(ItemUtils.setEnchantEffect(Objects.requireNonNull(itemStack.getItemMeta()), true));
                    }
                    return itemStack;
                }
            })));
            registerButton(new ChatInputButton("attribute.set_amount", new ButtonState("attribute.set_amount", WolfyUtilities.getSkullViaURL("461c8febcac21b9f63d87f9fd933589fe6468e93aa81cfcf5e52a4322e16e6"), (hashMap, guiHandler, player, itemStack, slot, help) -> {
                hashMap.put("%NUMBER%", CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems().getAttribAmount());
                return itemStack;
            }), (guiHandler, player, s, args) -> {
                try {
                    CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems().setAttribAmount(Double.parseDouble(args[0]));
                } catch (NumberFormatException e) {
                    api.sendPlayerMessage(player, "item_creator", "main_menu","attribute.amount.error");
                    return true;
                }
                return false;
            }));
            registerButton(new ChatInputButton("attribute.set_name", new ButtonState("attribute.set_name", Material.NAME_TAG, (hashMap, guiHandler, player, itemStack, slot, help) -> {
                hashMap.put("%NAME%", CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems().getAttributeName());
                return itemStack;
            }), (guiHandler, player, s, strings) -> {
                CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems().setAttributeName(strings[0]);
                return false;
            }));
            registerButton(new ChatInputButton("attribute.set_uuid", new ButtonState("attribute.set_uuid", Material.TRIPWIRE_HOOK, (hashMap, guiHandler, player, itemStack, slot, help) -> {
                hashMap.put("%UUID%", CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems().getAttributeUUID());
                return itemStack;
            }), (guiHandler, player, s, strings) -> {
                try {
                    UUID uuid = UUID.fromString(strings[0]);
                    CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems().setAttributeUUID(uuid.toString());
                } catch (IllegalArgumentException ex) {
                    api.sendPlayerMessage(player, "item_creator", "main_menu", "attribute.uuid.error.line1", new String[]{"%UUID%", strings[0]});
                    api.sendPlayerMessage(player, "item_creator", "main_menu", "attribute.uuid.error.line2");
                    return true;
                }
                return false;
            }));
            registerButton(new ActionButton("attribute.save", new ButtonState("attribute.save", Material.GREEN_CONCRETE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                ItemMeta itemMeta = CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems().getItem().getItemMeta();
                itemMeta.addAttributeModifier(Attribute.valueOf(CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getSubSetting().split("\\.")[1].toUpperCase(Locale.ROOT)), CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems().getAttributeModifier());
                CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems().getItem().setItemMeta(itemMeta);
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
            CustomCrafting.getPlayerCache(guiHandler.getPlayer()).setSubSetting("player_head");
            return true;
        })));
        {
            registerButton(new ItemInputButton("player_head.texture.input", new ButtonState("", Material.AIR)));

            registerButton(new ActionButton("player_head.texture.apply", new ButtonState("player_head.texture.apply", Material.GREEN_CONCRETE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                if (inventory.getItem(38) != null && inventory.getItem(38).getType().equals(Material.PLAYER_HEAD)) {
                    CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems().getItem().setItemMeta(WolfyUtilities.migrateSkullTexture((SkullMeta) inventory.getItem(38).getItemMeta(), CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems().getItem()));
                }
                return true;
            })));
            registerButton(new ChatInputButton("player_head.owner", new ButtonState("player_head.owner", Material.NAME_TAG), (guiHandler, player, s, args) -> {
                ItemMeta itemMeta = CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems().getItem().getItemMeta();
                if (!(itemMeta instanceof SkullMeta)) {
                    return true;
                }
                try {
                    UUID uuid = UUID.fromString(args[0]);
                    ((SkullMeta) itemMeta).setOwningPlayer(Bukkit.getOfflinePlayer(uuid));
                } catch (IllegalArgumentException e) {
                    ((SkullMeta) itemMeta).setOwningPlayer(Bukkit.getOfflinePlayer(args[0]));
                }
                CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems().getItem().setItemMeta(itemMeta);
                return false;
            }));
        }

        //POTION SETTINGS
        registerButton(new ActionButton("potion.option", new ButtonState("potion.option", Material.POTION, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            CustomCrafting.getPlayerCache(guiHandler.getPlayer()).setSubSetting("potion");
            return true;
        })));
        {
            registerButton(new ChatInputButton("potion.add", new ButtonState("potion.add", Material.GREEN_CONCRETE), (guiHandler, player, s, args) -> {
                ItemMeta itemMeta = CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems().getItem().getItemMeta();
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
                        type = Legacy.getPotion(args[0]);
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
                    CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems().getItem().setItemMeta(itemMeta);
                    return false;

                }
                api.sendPlayerMessage(player, "item_creator", "main_menu", "potion.wrong_args");
                return true;
            }));
            registerButton(new ChatInputButton("potion.remove", new ButtonState("potion.remove", Material.RED_CONCRETE), (guiHandler, player, s, args) -> {
                ItemMeta itemMeta = CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems().getItem().getItemMeta();
                PotionEffectType type;
                if (!(itemMeta instanceof PotionMeta)) {
                    return true;
                }
                type = Legacy.getPotion(args[0]);
                if (type != null) {
                    ((PotionMeta) itemMeta).removeCustomEffect(type);
                    CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems().getItem().setItemMeta(itemMeta);
                    return false;
                }
                api.sendPlayerMessage(player, "item_creator", "main_menu", "potion.invalid_name", new String[]{"%NAME%", args[0]});
                return true;
            }));
        }

        //Unbreakable Setting
        registerButton(new ToggleButton("unbreakable", new ButtonState("unbreakable.enabled", Material.BEDROCK, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            CustomItem itemStack = CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems().getItem();
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setUnbreakable(false);
            itemStack.setItemMeta(itemMeta);
            return true;
        }), new ButtonState("unbreakable.disabled", Material.GLASS, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            CustomItem itemStack = CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems().getItem();
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setUnbreakable(true);
            itemStack.setItemMeta(itemMeta);
            return true;
        })));

        //DAMAGE Settings
        registerButton(new ActionButton("damage.option", new ButtonState("damage.option", Material.IRON_SWORD, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            CustomCrafting.getPlayerCache(guiHandler.getPlayer()).setSubSetting("damage");
            return true;
        })));
        {
            registerButton(new ChatInputButton("damage.set", new ButtonState("damage.set", Material.GREEN_CONCRETE), (guiHandler, player, s, strings) -> {
                ItemMeta itemMeta = CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems().getItem().getItemMeta();
                if (!(itemMeta instanceof Damageable)) {
                    return true;
                }
                try {
                    int value = Integer.parseInt(s);
                    ((Damageable) itemMeta).setDamage(value);
                    CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems().getItem().setItemMeta(itemMeta);
                    api.sendPlayerMessage(player, "item_creator", "main_menu", "damage.value_success", new String[]{"%VALUE%", String.valueOf(value)});
                } catch (NumberFormatException e) {
                    api.sendPlayerMessage(player, "item_creator", "main_menu", "damage.invalid_value", new String[]{"%VALUE%", s});
                    return true;
                }
                return false;
            }));
            registerButton(new ActionButton("damage.reset", new ButtonState("damage.reset", Material.RED_CONCRETE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                ItemMeta itemMeta = CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems().getItem().getItemMeta();
                if (itemMeta instanceof Damageable) {
                    ((Damageable) itemMeta).setDamage(0);
                }
                CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems().getItem().setItemMeta(itemMeta);
                return true;
            })));
        }

        //REPAIR_COST Settings
        registerButton(new ActionButton("repair_cost.option", new ButtonState("repair_cost.option", Material.EXPERIENCE_BOTTLE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            CustomCrafting.getPlayerCache(guiHandler.getPlayer()).setSubSetting("repair_cost");
            return true;
        })));
        {
            registerButton(new ChatInputButton("repair_cost.set", new ButtonState("repair_cost.set", Material.GREEN_CONCRETE), (guiHandler, player, s, strings) -> {
                ItemMeta itemMeta = CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems().getItem().getItemMeta();
                try {
                    int value = Integer.parseInt(s);
                    ((Repairable) itemMeta).setRepairCost(value);
                    CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems().getItem().setItemMeta(itemMeta);
                    api.sendPlayerMessage(player, "item_creator", "main_menu", "repair_cost.value_success", new String[]{"%VALUE%", String.valueOf(value)});
                } catch (NumberFormatException e) {
                    api.sendPlayerMessage(player, "item_creator", "main_menu", "repair_cost.invalid_value", new String[]{"%VALUE%", s});
                    return true;
                }
                return false;
            }));
            registerButton(new ActionButton("repair_cost.reset", new ButtonState("repair_cost.reset", Material.RED_CONCRETE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                ItemMeta itemMeta = CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems().getItem().getItemMeta();
                if (itemMeta instanceof Repairable) {
                    ((Repairable) itemMeta).setRepairCost(0);
                }
                CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems().getItem().setItemMeta(itemMeta);
                return true;
            })));
        }

        //CUSTOM_MODEL_DATA Settings
        registerButton(new ActionButton("custom_model_data.option", new ButtonState("custom_model_data.option", Material.REDSTONE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            CustomCrafting.getPlayerCache(guiHandler.getPlayer()).setSubSetting("custom_model_data");
            return true;
        })));
        {
            registerButton(new ChatInputButton("custom_model_data.set", new ButtonState("custom_model_data.set", Material.GREEN_CONCRETE, (hashMap, guiHandler, player, itemStack, slot, help) -> {
                Items items = CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems();
                hashMap.put("%VAR%", (items.getItem().hasItemMeta() && items.getItem().getItemMeta().hasCustomModelData() ? items.getItem().getItemMeta().getCustomModelData() : "&7&l/") + "");
                return itemStack;
            }), (guiHandler, player, s, strings) -> {
                ItemMeta itemMeta = CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems().getItem().getItemMeta();
                if (!(itemMeta instanceof Repairable)) {
                    return true;
                }
                try {
                    int value = Integer.parseInt(s);
                    itemMeta.setCustomModelData(value);
                    CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems().getItem().setItemMeta(itemMeta);
                    api.sendPlayerMessage(player, "item_creator", "main_menu", "custom_model_data.success", new String[]{"%VALUE%", String.valueOf(value)});
                } catch (NumberFormatException e) {
                    api.sendPlayerMessage(player, "item_creator", "main_menu", "custom_model_data.invalid_value", new String[]{"%VALUE%", s});
                    return true;
                }
                return false;
            }));
            registerButton(new ActionButton("custom_model_data.reset", new ButtonState("custom_model_data.reset", Material.RED_CONCRETE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                ItemMeta itemMeta = CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems().getItem().getItemMeta();
                itemMeta.setCustomModelData(null);
                CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems().getItem().setItemMeta(itemMeta);
                return true;
            })));
        }

        //CONSUME SETTINGS
        registerButton(new ActionButton("consume.option", new ButtonState("consume.option", Material.ITEM_FRAME, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            CustomCrafting.getPlayerCache(guiHandler.getPlayer()).setSubSetting("consume");
            return true;
        })));
        {
            registerButton(new ChatInputButton("consume.durability_cost.enabled", new ButtonState("consume.durability_cost.enabled", Material.DROPPER, (hashMap, guiHandler, player, itemStack, slot, help) -> {
                hashMap.put("%VAR%", CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems().getItem().getDurabilityCost());
                return itemStack;
            }), (guiHandler, player, s, strings) -> {
                try {
                    int value = Integer.parseInt(s);
                    CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems().getItem().setDurabilityCost(value);
                    api.sendPlayerMessage(player, "item_creator", "main_menu", "consume.valid", new String[]{"%VALUE%", String.valueOf(value)});
                } catch (NumberFormatException e) {
                    api.sendPlayerMessage(player, "item_creator", "main_menu", "consume.invalid", new String[]{"%VALUE%", s});
                    return true;
                }
                return false;
            }));
            registerButton(new DummyButton("consume.durability_cost.disabled", new ButtonState("consume.durability_cost.disabled", Material.DROPPER)));

            registerButton(new ToggleButton("consume.consume_item", new ButtonState("consume.consume_item.enabled", Material.GREEN_CONCRETE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems().getItem().setConsumed(false);
                return true;
            }), new ButtonState("consume.consume_item.disabled", Material.RED_CONCRETE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems().getItem().setConsumed(true);
                return true;
            })));

            registerButton(new DummyButton("consume.replacement.enabled", new ButtonState("consume.replacement.enabled", Material.GREEN_CONCRETE, null)));
            registerButton(new DummyButton("consume.replacement.disabled", new ButtonState("consume.replacement.disabled", Material.RED_CONCRETE, null)));

            registerButton(new ItemInputButton("consume.replacement", new ButtonState("", Material.AIR, new ButtonActionRender() {
                @Override
                public boolean run(GuiHandler guiHandler, Player player, Inventory inventory, int slot, InventoryClickEvent inventoryClickEvent) {
                    PlayerCache cache = CustomCrafting.getPlayerCache(player);
                    Bukkit.getScheduler().runTask(CustomCrafting.getInst(), () -> {
                        ItemStack replacement = inventory.getItem(slot);
                        if (replacement != null) {
                            cache.getItems().getItem().setReplacement(CustomItem.getByItemStack(replacement));
                        } else {
                            cache.getItems().getItem().setReplacement(null);
                        }
                    });
                    return false;
                }

                @Override
                public ItemStack render(HashMap<String, Object> values, GuiHandler guiHandler, Player player, ItemStack itemStack, int slot, boolean help) {
                    return CustomCrafting.getPlayerCache(player).getItems().getItem().hasReplacement() ? CustomCrafting.getPlayerCache(player).getItems().getItem().getReplacement() : new ItemStack(Material.AIR);
                }
            })));
        }

        //FUEL Settings
        registerButton(new ActionButton("fuel.option", new ButtonState("fuel.option", Material.COAL, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            CustomCrafting.getPlayerCache(guiHandler.getPlayer()).setSubSetting("fuel");
            return true;
        })));
        {
            registerButton(new ChatInputButton("fuel.burn_time.set", new ButtonState("fuel.burn_time.set", Material.GREEN_CONCRETE, (hashMap, guiHandler, player, itemStack, slot, help) -> {
                hashMap.put("%VAR%", CustomCrafting.getPlayerCache(player).getItems().getItem().getBurnTime());
                return itemStack;
            }), (guiHandler, player, s, strings) -> {
                try {
                    int value = Integer.parseInt(s);
                    CustomCrafting.getPlayerCache(player).getItems().getItem().setBurnTime(value);
                    api.sendPlayerMessage(player, "item_creator", "main_menu", "fuel.value_success", new String[]{"%VALUE%", String.valueOf(value)});
                } catch (NumberFormatException e) {
                    api.sendPlayerMessage(player, "item_creator", "main_menu", "fuel.invalid_value", new String[]{"%VALUE%", s});
                    return true;
                }
                return false;
            }));
            registerButton(new ActionButton("fuel.burn_time.reset", new ButtonState("fuel.burn_time.reset", Material.RED_CONCRETE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                CustomCrafting.getPlayerCache(player).getItems().getItem().setBurnTime(0);
                return true;
            })));

            registerButton(new ToggleButton("fuel.furnace", new ButtonState("fuel.furnace.enabled", Material.FURNACE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                CustomCrafting.getPlayerCache(player).getItems().getItem().getAllowedBlocks().remove(Material.FURNACE);
                return true;
            }), new ButtonState("fuel.furnace.disabled", Material.FURNACE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                CustomCrafting.getPlayerCache(player).getItems().getItem().getAllowedBlocks().add(Material.FURNACE);
                return true;
            })));

            if (WolfyUtilities.hasVillagePillageUpdate()) {
                registerButton(new ToggleButton("fuel.blast_furnace", new ButtonState("fuel.blast_furnace.enabled", Material.BLAST_FURNACE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                    CustomCrafting.getPlayerCache(player).getItems().getItem().getAllowedBlocks().remove(Material.BLAST_FURNACE);
                    return true;
                }), new ButtonState("fuel.blast_furnace.disabled", Material.BLAST_FURNACE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                    CustomCrafting.getPlayerCache(player).getItems().getItem().getAllowedBlocks().add(Material.BLAST_FURNACE);
                    return true;
                })));
                registerButton(new ToggleButton("fuel.smoker", new ButtonState("fuel.smoker.enabled", Material.SMOKER, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                    CustomCrafting.getPlayerCache(player).getItems().getItem().getAllowedBlocks().remove(Material.SMOKER);
                    return true;
                }), new ButtonState("fuel.smoker.disabled", Material.SMOKER, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                    CustomCrafting.getPlayerCache(player).getItems().getItem().getAllowedBlocks().add(Material.SMOKER);
                    return true;
                })));
            }
        }

        //CUSTOM_DURABILITY_COST Settings
        registerButton(new ActionButton("custom_durability.option", new ButtonState("custom_durability.option", Material.DIAMOND_SWORD, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            CustomCrafting.getPlayerCache(guiHandler.getPlayer()).setSubSetting("custom_durability");
            return true;
        })));
        {
            registerButton(new ActionButton("custom_durability.remove", new ButtonState("custom_durability.remove", Material.RED_CONCRETE_POWDER, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                CustomItem.removeCustomDurability(CustomCrafting.getPlayerCache(player).getItems().getItem());
                return true;
            })));
            registerButton(new ChatInputButton("custom_durability.set_durability", new ButtonState("custom_durability.set_durability", Material.GREEN_CONCRETE, (values, guiHandler, player, itemStack, slot, help) -> {
                Items items = CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems();
                values.put("%VAR%", CustomItem.getCustomDurability(items.getItem()));
                return itemStack;
            }), (guiHandler, player, s, strings) -> {
                try {
                    CustomItem.setCustomDurability(CustomCrafting.getPlayerCache(player).getItems().getItem(), Integer.parseInt(strings[0]));
                } catch (NumberFormatException ex) {
                    return true;
                }
                guiHandler.openCluster();
                return false;
            }));
            registerButton(new ChatInputButton("custom_durability.set_damage", new ButtonState("custom_durability.set_damage", Material.RED_CONCRETE, (values, guiHandler, player, itemStack, slot, help) -> {
                Items items = CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems();
                values.put("%VAR%", CustomItem.getCustomDamage(items.getItem()));
                return itemStack;
            }), (guiHandler, player, s, strings) -> {
                try {
                    CustomItem.setCustomDamage(CustomCrafting.getPlayerCache(player).getItems().getItem(), Integer.parseInt(strings[0]));
                } catch (NumberFormatException ex) {
                    return true;
                }
                guiHandler.openCluster();
                return false;
            }));
            registerButton(new ChatInputButton("custom_durability.set_tag", new ButtonState("custom_durability.set_tag", Material.NAME_TAG, (values, guiHandler, player, itemStack, slot, help) -> {
                Items items = CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems();
                values.put("%VAR%", CustomItem.getCustomDurabilityTag(items.getItem()));
                return itemStack;
            }), (guiHandler, player, s, strings) -> {
                try {
                    CustomItem.setCustomDurabilityTag(CustomCrafting.getPlayerCache(player).getItems().getItem(), "&r" + s);
                } catch (NumberFormatException ex) {
                    return true;
                }
                guiHandler.openCluster();
                return false;
            }));
        }

        //LOCALIZED_NAME Settings
        registerButton(new ActionButton("localized_name.option", new ButtonState("localized_name.option", Material.NAME_TAG, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            CustomCrafting.getPlayerCache(guiHandler.getPlayer()).setSubSetting("localized_name");
            return true;
        })));
        {
            registerButton(new ChatInputButton("localized_name.set", new ButtonState("localized_name.set", Material.NAME_TAG, (hashMap, guiHandler, player, itemStack, i, b) -> {
                hashMap.put("%VAR%", CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems().getItem().getItemMeta().getLocalizedName());
                return itemStack;
            }), (guiHandler, player, s, strings) -> {
                ItemMeta itemMeta = CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems().getItem().getItemMeta();
                itemMeta.setLocalizedName(WolfyUtilities.translateColorCodes(s));
                CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems().getItem().setItemMeta(itemMeta);
                return false;
            }));
            registerButton(new ActionButton("localized_name.remove", new ButtonState("localized_name.remove", Material.NAME_TAG, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                ItemMeta itemMeta = CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems().getItem().getItemMeta();
                itemMeta.setLocalizedName(null);
                CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems().getItem().setItemMeta(itemMeta);
                return true;
            })));
        }

        //Permission
        registerButton(new ActionButton("permission.option", new ButtonState("permission.option", Material.BARRIER, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            CustomCrafting.getPlayerCache(guiHandler.getPlayer()).setSubSetting("permission");
            return true;
        })));
        {
            registerButton(new ChatInputButton("permission.set", new ButtonState("permission.set", Material.GREEN_CONCRETE, (hashMap, guiHandler, player, itemStack, i, b) -> {
                String perm = CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems().getItem().getPermission();
                hashMap.put("%VAR%", perm.isEmpty() ? "none" : perm);
                return itemStack;
            }), (guiHandler, player, s, strings) -> {
                CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems().getItem().setPermission(s.replace(" ", "."));
                return false;
            }));
            registerButton(new ActionButton("permission.remove", new ButtonState("permission.remove", Material.RED_CONCRETE_POWDER, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems().getItem().setPermission("");
                return true;
            })));
        }

        //Rarity Percentage
        registerButton(new ActionButton("rarity.option", new ButtonState("rarity.option", Material.DIAMOND, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            CustomCrafting.getPlayerCache(guiHandler.getPlayer()).setSubSetting("rarity");
            return true;
        })));
        {
            registerButton(new ChatInputButton("rarity.set", new ButtonState("rarity.set", Material.GREEN_CONCRETE, (hashMap, guiHandler, player, itemStack, i, b) -> {
                hashMap.put("%VAR%", CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems().getItem().getRarityPercentage() + "§8(§7" + (CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems().getItem().getRarityPercentage() * 100) + "%§8)");
                return itemStack;
            }), (guiHandler, player, s, strings) -> {
                try {
                    CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems().getItem().setRarityPercentage(Double.parseDouble(s));
                } catch (NumberFormatException ex) {
                    return true;
                }
                return false;
            }));
            registerButton(new ActionButton("rarity.reset", new ButtonState("rarity.reset", Material.RED_CONCRETE_POWDER, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems().getItem().setRarityPercentage(1.0d);
                return true;
            })));
        }

        registerButton(new ActionButton("persistent_data.option", new ButtonState("persistent_data.option", Material.BOOKSHELF, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            CustomCrafting.getPlayerCache(guiHandler.getPlayer()).setSubSetting("persistent_data");
            return true;
        })));
        {

        }

        //Elite Workbench Settings
        registerButton(new ActionButton("elite_workbench.option", new ButtonState("elite_workbench.option", Material.CRAFTING_TABLE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            CustomCrafting.getPlayerCache(guiHandler.getPlayer()).setSubSetting("elite_workbench");
            return true;
        })));
        {
            registerButton(new ActionButton("elite_workbench.particles", new ButtonState("elite_workbench.particles", Material.BARRIER)));
            registerButton(new MultipleChoiceButton("elite_workbench.grid_size",
                    new ButtonState("elite_workbench.grid_size.size_3", WolfyUtilities.getSkullViaURL("9e95293acbcd4f55faf5947bfc5135038b275a7ab81087341b9ec6e453e839"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                        ((EliteWorkbenchData) CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems().getItem().getCustomData("elite_workbench")).setGridSize(4);
                        return true;
                    }),
                    new ButtonState("elite_workbench.grid_size.size_4", WolfyUtilities.getSkullViaURL("cbfb41f866e7e8e593659986c9d6e88cd37677b3f7bd44253e5871e66d1d424"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                        ((EliteWorkbenchData) CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems().getItem().getCustomData("elite_workbench")).setGridSize(5);
                        return true;
                    }),
                    new ButtonState("elite_workbench.grid_size.size_5", WolfyUtilities.getSkullViaURL("14d844fee24d5f27ddb669438528d83b684d901b75a6889fe7488dfc4cf7a1c"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                        ((EliteWorkbenchData) CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems().getItem().getCustomData("elite_workbench")).setGridSize(6);
                        return true;
                    }),
                    new ButtonState("elite_workbench.grid_size.size_6", WolfyUtilities.getSkullViaURL("faff2eb498e5c6a04484f0c9f785b448479ab213df95ec91176a308a12add70"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                        ((EliteWorkbenchData) CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems().getItem().getCustomData("elite_workbench")).setGridSize(3);
                        return true;
                    })));
            registerButton(new ToggleButton("elite_workbench.toggle", new ButtonState("elite_workbench.toggle.enabled", Material.GREEN_CONCRETE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                ((EliteWorkbenchData) CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems().getItem().getCustomData("elite_workbench")).setEnabled(false);
                return true;
            }), new ButtonState("elite_workbench.toggle.disabled", Material.RED_CONCRETE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                ((EliteWorkbenchData) CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems().getItem().getCustomData("elite_workbench")).setEnabled(true);
                return true;
            })));

            registerButton(new ToggleButton("elite_workbench.advanced_recipes", new ButtonState("elite_workbench.advanced_recipes.enabled", Material.GREEN_CONCRETE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                ((EliteWorkbenchData) CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems().getItem().getCustomData("elite_workbench")).setAdvancedRecipes(false);
                return true;
            }), new ButtonState("elite_workbench.advanced_recipes.disabled", Material.RED_CONCRETE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                ((EliteWorkbenchData) CustomCrafting.getPlayerCache(guiHandler.getPlayer()).getItems().getItem().getCustomData("elite_workbench")).setAdvancedRecipes(true);
                return true;
            })));
        }


        for (String meta : dummyMetaSettings.getMetas()) {
            registerButton(new MetaIgnoreButton(meta));
        }
    }

    @EventHandler
    public void onUpdate(GuiUpdateEvent event) {
        if (event.verify(this)) {
            GuiHandler guiHandler = event.getGuiHandler();
            PlayerCache cache = CustomCrafting.getPlayerCache(event.getPlayer());
            Items items = cache.getItems();
            event.setButton(0, "back");
            if (!WolfyUtilities.getVersion().equals("1.3.1.0") && WolfyUtilities.getVersionNumber() >= 1320) {
                event.setButton(13, "item_input");
            }
            event.setButton(4, "none", cache.getDarkMode() ? "glass_gray" : "glass_white");
            event.setButton(12, "none", cache.getDarkMode() ? "glass_gray" : "glass_white");
            event.setButton(14, "none", cache.getDarkMode() ? "glass_gray" : "glass_white");
            event.setButton(22, "none", cache.getDarkMode() ? "glass_gray" : "glass_white");

            if (!items.isRecipeItem()) {
                event.setButton(3, "save_item");
            } else {
                event.setButton(2, "apply_item");
                event.setButton(3, "save_item");
            }


            List<String> options = new ArrayList<>();
            options.add("display_name.option");
            options.add("localized_name.option");
            options.add("lore.option");
            options.add("enchantments.option");
            options.add("flags.option");
            options.add("attribute.option");
            if (items.getItem() != null && !items.getItem().getType().equals(Material.AIR)) {
                ((ToggleButton) event.getGuiWindow().getButton("unbreakable")).setState(event.getGuiHandler(), items.getItem().getItemMeta().isUnbreakable());
                options.add("unbreakable");
            }
            options.add("repair_cost.option");
            if (items.getItem() != null && items.getItem().hasItemMeta() && items.getItem().getItemMeta() instanceof PotionMeta) {
                options.add("potion.option");
            }
            options.add("fuel.option");
            options.add("consume.option");
            options.add("damage.option");
            if (items.getItem() != null && items.getItem().getType().equals(Material.PLAYER_HEAD)) {
                options.add("player_head.option");
            }
            options.add("permission.option");
            options.add("rarity.option");
            if (WolfyUtilities.hasVillagePillageUpdate()) {
                options.add("custom_durability.option");
                options.add("custom_model_data.option");
                //options.add("persistent_data.option");
                options.add("elite_workbench.option");
            }
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
                if(j < options.size()){
                    event.setButton(slot + i, options.get(j));
                    j++;
                }else{
                    event.setButton(slot + i, "none", cache.getDarkMode() ? "glass_gray" : "glass_white");
                }
            }
            if (!items.getItem().getType().equals(Material.AIR)) {
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
                        ((ToggleButton) event.getGuiWindow().getButton("flags.attributes")).setState(guiHandler, items.getItem().getItemMeta().hasItemFlag(ItemFlag.HIDE_ATTRIBUTES));
                        ((ToggleButton) event.getGuiWindow().getButton("flags.unbreakable")).setState(guiHandler, items.getItem().getItemMeta().hasItemFlag(ItemFlag.HIDE_UNBREAKABLE));
                        ((ToggleButton) event.getGuiWindow().getButton("flags.destroys")).setState(guiHandler, items.getItem().getItemMeta().hasItemFlag(ItemFlag.HIDE_DESTROYS));
                        ((ToggleButton) event.getGuiWindow().getButton("flags.placed_on")).setState(guiHandler, items.getItem().getItemMeta().hasItemFlag(ItemFlag.HIDE_PLACED_ON));
                        ((ToggleButton) event.getGuiWindow().getButton("flags.potion_effects")).setState(guiHandler, items.getItem().getItemMeta().hasItemFlag(ItemFlag.HIDE_POTION_EFFECTS));
                        ((ToggleButton) event.getGuiWindow().getButton("flags.enchants")).setState(guiHandler, items.getItem().getItemMeta().hasItemFlag(ItemFlag.HIDE_ENCHANTS));
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
                        event.setButton(38, "player_head.texture.input");
                        event.setButton(39, "player_head.texture.apply");
                        event.setButton(41, "player_head.owner");
                        event.setButton(45, "meta_ignore.playerHead");
                        break;
                    case "potion":
                        event.setButton(39, "potion.add");
                        event.setButton(41, "potion.remove");
                        event.setButton(45, "meta_ignore.potion");
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
                        if (WolfyUtilities.hasVillagePillageUpdate()) {
                            ((ToggleButton) event.getGuiWindow().getButton("fuel.furnace")).setState(event.getGuiHandler(), items.getItem().getAllowedBlocks().contains(Material.FURNACE));
                            ((ToggleButton) event.getGuiWindow().getButton("fuel.blast_furnace")).setState(event.getGuiHandler(), items.getItem().getAllowedBlocks().contains(Material.BLAST_FURNACE));
                            ((ToggleButton) event.getGuiWindow().getButton("fuel.smoker")).setState(event.getGuiHandler(), items.getItem().getAllowedBlocks().contains(Material.SMOKER));
                            event.setButton(47, "fuel.furnace");
                            event.setButton(49, "fuel.blast_furnace");
                            event.setButton(51, "fuel.smoker");
                        }
                        break;
                    case "custom_model_data":
                        event.setButton(39, "custom_model_data.set");
                        event.setButton(41, "custom_model_data.reset");
                        event.setButton(45, "meta_ignore.customModelData");
                        break;
                    case "consume":
                        ((ToggleButton) event.getGuiWindow().getButton("consume.consume_item")).setState(event.getGuiHandler(), items.getItem().isConsumed());
                        event.setButton(31, "consume.consume_item");
                        event.setButton(38, "consume.replacement");
                        event.setButton(39, items.getItem().hasReplacement() ? "consume.replacement.enabled" : "consume.replacement.disabled");
                        if (items.getItem().hasReplacement() || items.getItem().getMaxStackSize() > 1) {
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
                        ((MultipleChoiceButton) event.getGuiWindow().getButton("elite_workbench.grid_size")).setState(event.getGuiHandler(), ((EliteWorkbenchData) items.getItem().getCustomData("elite_workbench")).getGridSize() - 3);
                        ((ToggleButton) event.getGuiWindow().getButton("elite_workbench.toggle")).setState(event.getGuiHandler(), ((EliteWorkbenchData) items.getItem().getCustomData("elite_workbench")).isEnabled());
                        event.setButton(37, "elite_workbench.particles");
                        event.setButton(39, "elite_workbench.grid_size");
                        event.setButton(41, "elite_workbench.toggle");
                        event.setButton(43, "elite_workbench.advanced_recipes");

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

    public static void saveItem(PlayerCache cache, String id, CustomItem customItem) {
        ItemConfig config;
        String namespace = id.split(":")[0];
        String key = id.split(":")[1];
        
        if (CustomCrafting.hasDataBaseHandler()) {
            config = new ItemConfig(CustomCrafting.getApi().getConfigAPI(), namespace, key);
        } else {
            config = new ItemConfig(CustomCrafting.getApi().getConfigAPI(), namespace, CustomCrafting.getInst().getDataFolder() + "/recipes/" + namespace + "/items", key, true, "json");
        }
        config.setCustomItem(customItem);
        if (CustomItems.getCustomItem(id) != null) {
            CustomItems.removeCustomItem(id);
        }
        if (CustomCrafting.hasDataBaseHandler()) {
            CustomCrafting.getDataBaseHandler().updateItem(config);
        } else {
            config.reload(CustomCrafting.getConfigHandler().getConfig().isPrettyPrinting());
        }
        CustomItem customItem1 = new CustomItem(config);
        cache.getItems().setItem(customItem1);
        CustomItems.addCustomItem(customItem1);
    }
}
