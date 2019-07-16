package me.wolfyscript.customcrafting.gui.items;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.PlayerCache;
import me.wolfyscript.customcrafting.data.cache.Items;
import me.wolfyscript.customcrafting.gui.ExtendedGuiWindow;
import me.wolfyscript.customcrafting.items.CustomItem;
import me.wolfyscript.customcrafting.items.Meta;
import me.wolfyscript.customcrafting.items.MetaSettings;
import me.wolfyscript.customcrafting.utils.ChatUtils;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.*;
import me.wolfyscript.utilities.api.utils.Legacy;
import me.wolfyscript.utilities.api.utils.chat.ClickData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class ItemCreator extends ExtendedGuiWindow {

    private final int pages = 1;

    public ItemCreator(InventoryAPI inventoryAPI) {
        super("item_creator", inventoryAPI, 54);
    }

    @Override
    public void onInit() {
        createItem("save_item", Material.WRITABLE_BOOK);
        createItem("apply_item", Material.GREEN_CONCRETE);

        createItem("invalid_type", Material.BARRIER);
        createItem("invalid_version", Material.BARRIER);

        createItem("page_next", WolfyUtilities.getSkullViaURL("c86185b1d519ade585f184c34f3f3e20bb641deb879e81378e4eaf209287"));
        createItem("page_previous", WolfyUtilities.getSkullViaURL("ad73cf66d31b83cd8b8644c15958c1b73c8d97323b801170c1d8864bb6a846d"));

        createItem("item_name", Material.NAME_TAG);
        createItem("item_enchantments", Material.ENCHANTED_BOOK);
        createItem("item_lore", Material.WRITABLE_BOOK);
        createItem("item_flags", Material.WRITTEN_BOOK);
        createItem("attributes_modifiers", Material.ENCHANTED_GOLDEN_APPLE);
        createItem("unbreakable_on", Material.BEDROCK);
        createItem("unbreakable_off", Material.GLASS);
        createItem("skull_setting", Material.PLAYER_HEAD);
        createItem("potion_effects", Material.POTION);
        createItem("variants", Material.BOOKSHELF);

        createItem("set_displayname", Material.GREEN_CONCRETE);
        createItem("remove_displayname", Material.RED_CONCRETE);

        createItem("meta_ignore", Material.CYAN_CONCRETE);

        createItem("potion_add", Material.GREEN_CONCRETE);
        createItem("potion_remove", Material.RED_CONCRETE);

        createItem("enchant_add", Material.ENCHANTED_BOOK);
        createItem("enchant_remove", Material.ENCHANTED_BOOK);
        createItem("lore_add", Material.WRITABLE_BOOK);
        createItem("lore_remove", Material.WRITTEN_BOOK);

        createItem("variant_add", Material.GREEN_CONCRETE);
        createItem("variant_remove", Material.RED_CONCRETE);
        createItem("variants_list", Material.BOOKSHELF);

        createItem("item_damage", Material.DIAMOND_SWORD);
        createItem("damage_set", Material.GREEN_CONCRETE);
        createItem("damage_reset", Material.RED_CONCRETE);

        createItem("repair_cost", Material.EXPERIENCE_BOTTLE);
        createItem("repair_set", Material.GREEN_CONCRETE);
        createItem("repair_reset", Material.RED_CONCRETE);

        createItem("custom_model_data", Material.REDSTONE);
        createItem("set_custom_model_data", Material.GREEN_CONCRETE);
        createItem("reset_custom_model_data", Material.RED_CONCRETE);

        createItem("replacement_durability", Material.ITEM_FRAME);
        createItem("durability_cost", Material.DROPPER);
        createItem("durability_cost_disabled", Material.DROPPER);
        createItem("consume_item_enabled", Material.GREEN_CONCRETE);
        createItem("consume_item_disabled", Material.RED_CONCRETE);
        createItem("replacement", Material.GREEN_CONCRETE);
        createItem("replacement_disabled", Material.RED_CONCRETE);

        createItem("furnace.fuel", Material.COAL);
        createItem("furnace.burn_time", Material.GREEN_CONCRETE);
        createItem("furnace.burn_time_reset", Material.RED_CONCRETE);
        createItem("furnace.furnace", Material.FURNACE);
        if (WolfyUtilities.hasVillagePillageUpdate()) {
            createItem("furnace.blast_furnace", Material.BLAST_FURNACE);
            createItem("furnace.smoker", Material.SMOKER);
        }

        createItem("skull_texture", Material.GREEN_CONCRETE);
        createItem("skull_owner", Material.NAME_TAG);

        createItem("flag_enchants", Material.ENCHANTING_TABLE);
        createItem("flag_attributes", Material.ENCHANTED_GOLDEN_APPLE);
        createItem("flag_unbreakable", Material.BEDROCK);
        createItem("flag_destroys", Material.TNT);
        createItem("flag_placed_on", Material.GRASS_BLOCK);
        createItem("flag_potion_effects", Material.POTION);

        createItem("attribute.generic_max_health", Material.ENCHANTED_GOLDEN_APPLE);
        createItem("attribute.generic_follow_range", Material.ENDER_EYE);
        createItem("attribute.generic_knockback_resistance", Material.STICK);
        createItem("attribute.generic_movement_speed", Material.IRON_BOOTS);
        createItem("attribute.generic_flying_speed", Material.FIREWORK_ROCKET);
        createItem("attribute.generic_attack_damage", Material.DIAMOND_SWORD);
        createItem("attribute.generic_attack_speed", Material.DIAMOND_AXE);
        createItem("attribute.generic_armor", Material.CHAINMAIL_CHESTPLATE);
        createItem("attribute.generic_armor_toughness", Material.DIAMOND_CHESTPLATE);
        createItem("attribute.generic_luck", Material.NETHER_STAR);
        createItem("attribute.horse_jump_strength", Material.DIAMOND_HORSE_ARMOR);
        createItem("attribute.zombie_spawn_reinforcements", Material.ZOMBIE_HEAD);

        createItem("attribute.add_number", WolfyUtilities.getSkullByValue("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjBiNTVmNzQ2ODFjNjgyODNhMWMxY2U1MWYxYzgzYjUyZTI5NzFjOTFlZTM0ZWZjYjU5OGRmMzk5MGE3ZTcifX19"));
        createItem("attribute.add_scalar", WolfyUtilities.getSkullByValue("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTdiMTc5MWJkYzQ2ZDhhNWM1MTcyOWU4OTgyZmQ0MzliYjQwNTEzZjY0YjViYWJlZTkzMjk0ZWZjMWM3In19fQ=="));
        createItem("attribute.multiply_scalar_1", WolfyUtilities.getSkullByValue("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTlmMjdkNTRlYzU1NTJjMmVkOGY4ZTE5MTdlOGEyMWNiOTg4MTRjYmI0YmMzNjQzYzJmNTYxZjllMWU2OWYifX19"));

        createItem("attribute.slot_hand", Material.IRON_SWORD);
        createItem("attribute.slot_off_hand", Material.SHIELD);
        createItem("attribute.slot_feet", Material.IRON_BOOTS);
        createItem("attribute.slot_legs", Material.IRON_LEGGINGS);
        createItem("attribute.slot_chest", Material.IRON_CHESTPLATE);
        createItem("attribute.slot_head", Material.IRON_HELMET);

        createItem("attribute.save", Material.GREEN_CONCRETE);
        createItem("attribute.delete", Material.RED_CONCRETE);
        createItem("attribute.set_amount", WolfyUtilities.getSkullByValue("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDYxYzhmZWJjYWMyMWI5ZjYzZDg3ZjlmZDkzMzU4OWZlNjQ2OGU5M2FhODFjZmNmNWU1MmE0MzIyZTE2ZTYifX19"));
        createItem("attribute.set_name", Material.NAME_TAG);
        createItem("attribute.set_uuid", Material.TRIPWIRE_HOOK);
    }

    @EventHandler
    public void onUpdate(GuiUpdateEvent event) {
        if (event.verify(this)) {
            PlayerCache cache = CustomCrafting.getPlayerCache(event.getPlayer());
            Items items = cache.getItems();

            event.setItem(4, "none", "glass_white");
            event.setItem(12, "none", "glass_white");
            event.setItem(13, items.getItem());
            event.setItem(14, "none", "glass_white");
            event.setItem(22, "none", "glass_white");

            if (!items.getType().equals("items")) {
                event.setItem(3, "apply_item");
            }
            event.setItem(5, "save_item");
            if (items.getPage() > 0) {
                event.setItem(6, "page_previous");
            }
            if (items.getPage() + 1 < pages) {
                event.setItem(7, "page_next");
            }

            switch (items.getPage()) {
                case 0:
                    event.setItem(9, "item_name");
                    event.setItem(10, "item_lore");
                    event.setItem(11, "attributes_modifiers");

                    event.setItem(18, "item_enchantments");
                    event.setItem(19, "item_flags");
                    event.setItem(20, "custom_model_data");
                    event.setItem(21, "unbreakable_off");
                    if (items.getItem() != null && items.getItem().hasItemMeta() && items.getItem().getItemMeta().isUnbreakable()) {
                        event.setItem(21, "unbreakable_on");
                    }

                    event.setItem(15, "repair_cost");
                    event.setItem(16, "potion_effects");
                    event.setItem(17, event.getItem("furnace.fuel", "%C%", items.getItem().getBurnTime() > 0 ? "§a" : "§4"));

                    event.setItem(23, "replacement_durability");
                    event.setItem(24, "item_damage");
                    event.setItem(25, "variants");
                    event.setItem(26, "skull_setting");
                    break;
                case 1:

                    break;
            }


            CustomItem itemStack = items.getItem();
            ItemMeta itemMeta = itemStack.getItemMeta();
            MetaSettings metaSettings = items.getItem().getMetaSettings();
            if (!itemStack.getType().equals(Material.AIR)) {
                //DRAW Sections
                switch (cache.getSubSetting()) {
                    case "item_name":
                        event.setItem(39, "set_displayname");
                        event.setItem(41, "remove_displayname");
                        event.setItem(45, event.getItem("meta_ignore", "%VAR%", metaSettings.getNameMeta().getOption().toString()));
                        break;
                    case "item_enchantments":
                        event.setItem(39, "enchant_add");
                        event.setItem(41, "enchant_remove");
                        event.setItem(45, event.getItem("meta_ignore", "%VAR%", metaSettings.getEnchantMeta().getOption().toString()));
                        break;
                    case "item_lore":
                        event.setItem(39, "lore_add");
                        event.setItem(41, "lore_remove");
                        event.setItem(45, event.getItem("meta_ignore", "%VAR%", metaSettings.getLoreMeta().getOption().toString()));
                        break;
                    case "item_flags":
                        event.setItem(37, event.getItem("flag_attributes", "%C%", itemMeta.hasItemFlag(ItemFlag.HIDE_ATTRIBUTES) ? "§3" : "§4"));
                        event.setItem(39, event.getItem("flag_unbreakable", "%C%", itemMeta.hasItemFlag(ItemFlag.HIDE_UNBREAKABLE) ? "§3" : "§4"));
                        event.setItem(41, event.getItem("flag_destroys", "%C%", itemMeta.hasItemFlag(ItemFlag.HIDE_DESTROYS) ? "§3" : "§4"));
                        event.setItem(43, event.getItem("flag_placed_on", "%C%", itemMeta.hasItemFlag(ItemFlag.HIDE_PLACED_ON) ? "§3" : "§4"));
                        event.setItem(47, event.getItem("flag_potion_effects", "%C%", itemMeta.hasItemFlag(ItemFlag.HIDE_POTION_EFFECTS) ? "§3" : "§4"));
                        event.setItem(51, event.getItem("flag_enchants", "%C%", itemMeta.hasItemFlag(ItemFlag.HIDE_ENCHANTS) ? "§3" : "§4"));
                        event.setItem(45, event.getItem("meta_ignore", "%VAR%", metaSettings.getFlagsMeta().getOption().toString()));
                        break;
                    case "attributes_modifiers":
                        event.setItem(36, "attribute.generic_max_health");
                        event.setItem(37, "attribute.generic_follow_range");
                        event.setItem(38, "attribute.generic_knockback_resistance");
                        event.setItem(39, "attribute.generic_movement_speed");
                        event.setItem(40, "attribute.generic_flying_speed");
                        event.setItem(41, "attribute.generic_attack_damage");
                        event.setItem(42, "attribute.generic_attack_speed");
                        event.setItem(43, "attribute.generic_armor");
                        event.setItem(44, "attribute.generic_armor_toughness");
                        event.setItem(48, "attribute.generic_luck");
                        event.setItem(49, "attribute.horse_jump_strength");
                        event.setItem(50, "attribute.zombie_spawn_reinforcements");
                        break;
                    case "skull_setting":
                        if (items.getItem().getType().equals(Material.PLAYER_HEAD)) {
                            event.setItem(38, items.getSkullSetting());
                            event.setItem(39, "skull_texture");
                            event.setItem(41, "skull_owner");
                            event.setItem(45, event.getItem("meta_ignore", "%VAR%", metaSettings.getPlayerHeadMeta().getOption().toString()));
                        } else {
                            event.setItem(40, "invalid_type");
                        }
                        break;
                    case "potion_effects":
                        if (itemMeta instanceof PotionMeta) {
                            event.setItem(39, "potion_add");
                            event.setItem(41, "potion_remove");
                            event.setItem(45, event.getItem("meta_ignore", "%VAR%", metaSettings.getPotionMeta().getOption().toString()));
                        } else {
                            event.setItem(40, "invalid_type");
                        }
                        break;
                    case "variants":
                        //TODO: COMPATIBILITY WITH FURNACE RECIPES!
                        //|| items.getType().equals("source")
                        if (items.getType().equals("ingredient")) {
                            //TODO: GET corresponding item
                            event.setItem(37, "variant_add");
                            event.setItem(38, items.getVariantItem());
                            event.setItem(40, "variant_remove");
                            ItemStack listItem = event.getItem("variants_list");
                            ItemMeta listItemMeta = listItem.getItemMeta();
                            String row = WolfyUtilities.translateColorCodes(api.getLanguageAPI().getActiveLanguage().replaceKey("items.item_creator.variants_list.lore").get(0));
                            List<String> lore = new ArrayList<>();
                            List<CustomItem> ingredients = cache.getWorkbench().getIngredients(items.getCraftSlot());
                            for (int i = 1; i < ingredients.size(); i++) {
                                CustomItem customItem = ingredients.get(i);
                                String line = row.replace("%ITEM%", customItem.hasID() ? customItem.getId() : customItem.getType().getKey().toString()).replace("%NUM%", "" + i);
                                lore.add(line);
                            }
                            listItemMeta.setLore(lore);
                            listItem.setItemMeta(listItemMeta);
                            event.setItem(42, listItem);
                        } else {
                            event.setItem(40, "invalid_type");
                        }
                        break;
                    case "item_damage":
                        if (itemMeta instanceof Damageable) {
                            event.setItem(39, "damage_set");
                            event.setItem(41, "damage_reset");
                            event.setItem(45, event.getItem("meta_ignore", "%VAR%", metaSettings.getDamageMeta().getOption().toString()));
                        } else {
                            event.setItem(40, "invalid_type");
                        }
                        break;
                    case "repair_cost":
                        if (itemMeta instanceof Repairable) {
                            event.setItem(39, "repair_set");
                            event.setItem(41, "repair_reset");
                            event.setItem(45, event.getItem("meta_ignore", "%VAR%", metaSettings.getRepairCostMeta().getOption().toString()));
                        } else {
                            event.setItem(40, "invalid_type");
                        }
                        break;
                    case "furnace.fuel":
                        event.setItem(39, event.getItem("furnace.burn_time", "%VAR%", items.getItem().getBurnTime() + ""));
                        event.setItem(41, "furnace.burn_time_reset");

                        if (WolfyUtilities.hasVillagePillageUpdate()) {
                            event.setItem(47, event.getItem("furnace.furnace", "%C%", items.getItem().getAllowedBlocks().contains(Material.FURNACE) ? "§a" : "§c"));
                            event.setItem(49, event.getItem("furnace.blast_furnace", "%C%", items.getItem().getAllowedBlocks().contains(Material.BLAST_FURNACE) ? "§a" : "§c"));
                            event.setItem(51, event.getItem("furnace.smoker", "%C%", items.getItem().getAllowedBlocks().contains(Material.SMOKER) ? "§a" : "§c"));
                        }
                        break;
                    case "custom_model_data":
                        if (WolfyUtilities.hasVillagePillageUpdate()) {
                            event.setItem(39, event.getItem("set_custom_model_data", "%VAR%", (items.getItem().hasItemMeta() && items.getItem().getItemMeta().hasCustomModelData() ? items.getItem().getItemMeta().getCustomModelData() : WolfyUtilities.translateColorCodes(api.getLanguageAPI().getActiveLanguage().replaceKeys("$msg.gui.item_creator.custom_model_data.disabled$"))) + ""));
                            event.setItem(41, "reset_custom_model_data");
                            event.setItem(45, event.getItem("meta_ignore", "%VAR%", metaSettings.getCustomModelDataMeta().getOption().toString()));
                        } else {
                            event.setItem(40, "invalid_version");
                        }
                        break;
                    case "replacement_durability":
                        event.setItem(31, items.getItem().isConsumed() ? "consume_item_enabled" : "consume_item_disabled");

                        event.setItem(38, items.getItem().hasReplacement() ? items.getItem().getReplacement() : new ItemStack(Material.AIR));
                        event.setItem(39, items.getItem().hasReplacement() ? "replacement" : "replacement_disabled");

                        if (items.getItem().hasReplacement() || items.getItem().getMaxStackSize() > 1) {
                            event.setItem(41, "durability_cost_disabled");
                        } else {
                            event.setItem(41, event.getItem("durability_cost", "%VAR%", "" + items.getItem().getDurabilityCost()));
                        }

                        break;
                }
                if (cache.getSubSetting().startsWith("GENERIC_") || cache.getSubSetting().startsWith("HORSE_") || cache.getSubSetting().startsWith("ZOMBIE_")) {
                    event.setItem(36, "attribute.slot_head");
                    event.setItem(45, "attribute.slot_chest");
                    event.setItem(37, "attribute.slot_legs");
                    event.setItem(46, "attribute.slot_feet");
                    event.setItem(38, "attribute.slot_hand");
                    event.setItem(47, "attribute.slot_off_hand");
                    if (items.getAttributeSlot() != null) {
                        ItemStack slotItem = event.getItem("attribute.slot_" + items.getAttributeSlot().name().toLowerCase());
                        slotItem.addUnsafeEnchantment(Enchantment.FIRE_ASPECT, 1);
                        ItemMeta slotMeta = slotItem.getItemMeta();
                        slotMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                        slotItem.setItemMeta(slotMeta);
                        switch (items.getAttributeSlot()) {
                            case HEAD:
                                event.setItem(36, slotItem);
                                break;
                            case CHEST:
                                event.setItem(45, slotItem);
                                break;
                            case LEGS:
                                event.setItem(37, slotItem);
                                break;
                            case FEET:
                                event.setItem(46, slotItem);
                                break;
                            case HAND:
                                event.setItem(38, slotItem);
                                break;
                            case OFF_HAND:
                                event.setItem(47, slotItem);
                        }
                    }
                    event.setItem(42, event.getItem("attribute.add_number", "%C%", items.getAttribOperation().equals(AttributeModifier.Operation.ADD_NUMBER) ? "§a" : "§4"));
                    event.setItem(43, event.getItem("attribute.add_scalar", "%C%", items.getAttribOperation().equals(AttributeModifier.Operation.ADD_SCALAR) ? "§a" : "§4"));
                    event.setItem(44, event.getItem("attribute.multiply_scalar_1", "%C%", items.getAttribOperation().equals(AttributeModifier.Operation.MULTIPLY_SCALAR_1) ? "§a" : "§4"));
                    event.setItem(51, event.getItem("attribute.set_amount", "%NUMBER%", String.valueOf(items.getAttribAmount())));
                    event.setItem(52, event.getItem("attribute.set_name", "%NAME%", items.getAttributeName()));
                    event.setItem(53, event.getItem("attribute.set_uuid", "%UUID%", items.getAttributeUUID()));
                    event.setItem(40, "attribute.save");
                    event.setItem(49, "attribute.delete");
                }
            }
        }
    }

    @Override
    public boolean onAction(GuiAction guiAction) {
        if (!super.onAction(guiAction)) {
            String action = guiAction.getAction();
            Player player = guiAction.getPlayer();
            PlayerCache cache = CustomCrafting.getPlayerCache(guiAction.getPlayer());
            Items items = cache.getItems();
            if (action.equals("back")) {
                guiAction.getGuiHandler().openLastInv();
            } else if (action.equals("save_item") && !items.getItem().getType().equals(Material.AIR)) {
                if (items.getType().equals("items") && CustomCrafting.getRecipeHandler().getCustomItem(items.getId()) != null) {
                    CustomItem.saveItem(cache, items.getId(), items.getItem());
                    api.sendPlayerMessage(player, "$msg.gui.item_creator.save.success$");
                    api.sendPlayerMessage(player, "&6" + items.getId().split(":")[0] + "/items/" + items.getId().split(":")[1]);
                } else {
                    //TODO ITEM LIST
                    api.sendPlayerMessage(player, "$msg.gui.item_creator.save.input.line1$");
                    runChat(0, "$msg.gui.item_creator.save.input.line2$", guiAction.getGuiHandler());
                }
            } else if (action.equals("apply_item") && !items.getItem().getType().equals(Material.AIR)) {
                CustomItem customItem = items.getItem();
                if (items.isSaved()) {
                    CustomItem.saveItem(cache, items.getId(), customItem);
                    customItem = CustomCrafting.getRecipeHandler().getCustomItem(items.getId());
                }
                CustomItem.applyItem(customItem, cache);
                guiAction.getGuiHandler().changeToInv("recipe_creator");
            } else if (action.equals("meta_ignore")) {
                Meta meta = items.getItem().getMetaSettings().getMetaByCache(cache);
                List<MetaSettings.Option> options = meta.getAvailableOptions();
                int i = options.indexOf(meta.getOption())+1;
                if(i >= options.size()){
                    i = 0;
                }
                meta.setOption(options.get(i));
            } else {
                CustomItem itemStack = items.getItem();
                ItemMeta itemMeta = itemStack.getItemMeta();
                if (!itemStack.getType().equals(Material.AIR) && itemMeta != null) {
                    switch (action) {
                        //TOP Section
                        case "skull_setting":
                        case "item_name":
                        case "item_lore":
                        case "item_flags":
                        case "attributes_modifiers":
                        case "item_enchantments":
                        case "potion_effects":
                        case "item_damage":
                        case "repair_cost":
                        case "furnace.fuel":
                        case "custom_model_data":
                        case "variants":
                        case "replacement_durability":
                            cache.setSubSetting(action);
                            break;
                        case "unbreakable_off":
                            itemMeta.setUnbreakable(true);
                            break;
                        case "unbreakable_on":
                            itemMeta.setUnbreakable(false);
                            break;

                        //Display name section
                        case "remove_displayname":
                            itemMeta.setDisplayName(null);
                            break;
                        case "set_displayname":
                            runChat(1, "$msg.gui.item_creator.display_name$", guiAction.getGuiHandler());
                            break;
                        //Enchantments
                        case "enchant_add":
                            runChat(2, "$msg.gui.item_creator.enchant.add$", guiAction.getGuiHandler());
                            break;
                        case "enchant_remove":
                            runChat(3, "$msg.gui.item_creator.enchant.remove$", guiAction.getGuiHandler());
                            break;

                        //LORE
                        //TODO: LORE MANAGER!
                        case "lore_add":
                            runChat(4, "$msg.gui.item_creator.lore.add$", guiAction.getGuiHandler());
                            break;
                        case "lore_remove":
                            ChatUtils.sendLoreManager(player);
                            guiAction.getGuiHandler().close();
                            //runChat(5, "$msg.gui.item_creator.lore.remove$", guiAction.getGuiHandler());
                            break;

                        //Potion
                        case "potion_add":
                            runChat(6, "$msg.gui.item_creator.potion.add$", guiAction.getGuiHandler());
                            break;
                        case "potion_remove":
                            runChat(7, "$msg.gui.item_creator.potion.remove$", guiAction.getGuiHandler());
                            break;

                        //Attribute Settings
                        case "attribute.slot_head":
                        case "attribute.slot_chest":
                        case "attribute.slot_legs":
                        case "attribute.slot_feet":
                        case "attribute.slot_hand":
                        case "attribute.slot_off_hand":
                            EquipmentSlot equipmentSlot = EquipmentSlot.valueOf(action.substring("attribute.slot_".length()).toUpperCase());
                            if (items.getAttributeSlot() == null) {
                                items.setAttributeSlot(equipmentSlot);
                            } else {
                                items.setAttributeSlot(items.getAttributeSlot().equals(equipmentSlot) ? null : equipmentSlot);
                            }
                            break;
                        case "attribute.add_number":
                        case "attribute.add_scalar":
                        case "attribute.multiply_scalar_1":
                            items.setAttribOperation(AttributeModifier.Operation.valueOf(action.split("\\.")[1].toUpperCase()));
                            break;
                        case "attribute.set_amount":
                            runChat(8, "$msg.gui.item_creator.attribute.amount.input$", guiAction.getGuiHandler());
                            break;
                        case "attribute.set_name":
                            runChat(9, "$msg.gui.item_creator.attribute.name.input$", guiAction.getGuiHandler());
                            break;
                        case "attribute.set_uuid":
                            runChat(10, "$msg.gui.item_creator.attribute.uuid.input$", guiAction.getGuiHandler());
                            break;
                        case "attribute.save":
                            itemMeta.addAttributeModifier(Attribute.valueOf(cache.getSubSetting()), items.getAttributeModifier());
                            break;
                        case "attribute.delete":
                            ChatUtils.sendAttributeModifierManager(player);
                            guiAction.getGuiHandler().close();
                            break;
                        //PLAYER SKULL SETTINGS
                        case "skull_texture":
                            if (guiAction.getClickedInventory().getItem(38) != null && guiAction.getClickedInventory().getItem(38).getType().equals(Material.PLAYER_HEAD)) {
                                ItemStack inputHead = guiAction.getClickedInventory().getItem(38);
                                itemMeta = WolfyUtilities.migrateSkullTexture((SkullMeta) inputHead.getItemMeta(), itemStack);
                            }
                            break;
                        case "skull_owner":
                            runChat(11, "$msg.gui.item_creator.skull_owner.input$", guiAction.getGuiHandler());
                            break;
                        //VARIANTS
                        case "variant_add":
                            if (items.getVariantItem() != null) {
                                cache.getWorkbench().getIngredients(items.getCraftSlot()).add(items.getVariantItem());
                            }
                            break;
                        case "variant_remove":
                            api.sendPlayerMessage(player, "$msg.gui.item_creator.variant.remove.msg$");
                            guiAction.getGuiHandler().close();
                            String line = WolfyUtilities.translateColorCodes(api.getLanguageAPI().getActiveLanguage().replaceKeys("$msg.gui.item_creator.variant.remove.line$"));
                            List<CustomItem> ingredients = cache.getWorkbench().getIngredients(items.getCraftSlot());
                            for (int i = 1; i < ingredients.size(); i++) {
                                int finalI = i;
                                CustomItem customItem = ingredients.get(i);
                                String id = customItem.hasID() ? customItem.getId() : customItem.getType().getKey().toString();
                                api.sendActionMessage(player, new ClickData(line.replace("%NUM%", "" + i).replace("%ITEM%", id), (wolfyUtilities, player1) -> {
                                    ingredients.remove(finalI);
                                    api.sendPlayerMessage(player, "$msg.gui.item_creator.variant.remove.success$", new String[]{"%ITEM%", id});
                                    guiAction.getGuiHandler().openLastInv();
                                }));
                            }
                            return true;
                        //DAMAGE
                        case "damage_set":
                            if (itemMeta instanceof Damageable) {
                                runChat(20, "$msg.gui.item_creator.damage.set$", guiAction.getGuiHandler());
                            } else {
                                api.sendPlayerMessage(player, "$msg.gui.item_creator.damage.invalid_type$");
                            }
                            break;
                        case "damage_reset":
                            if (itemMeta instanceof Damageable) {
                                ((Damageable) itemMeta).setDamage(0);
                            }
                            break;
                        //REPAIR COST
                        case "repair_set":
                            if (itemMeta instanceof Repairable) {
                                runChat(30, "$msg.gui.item_creator.repair.set$", guiAction.getGuiHandler());
                            } else {
                                api.sendPlayerMessage(player, "$msg.gui.item_creator.repair.invalid_type$");
                            }
                            break;
                        case "repair_reset":
                            if (itemMeta instanceof Repairable) {
                                ((Repairable) itemMeta).setRepairCost(0);
                            }
                            break;
                        //FUEL
                        case "furnace.burn_time":
                            runChat(40, "$msg.gui.item_creator.fuel.set$", guiAction.getGuiHandler());
                            break;
                        case "furnace.burn_time_reset":
                            itemStack.setBurnTime(0);
                            break;
                        case "furnace.furnace":
                        case "furnace.blast_furnace":
                        case "furnace.smoker":
                            Material material = Material.matchMaterial(action.split("\\.")[1]);
                            if (items.getItem().getAllowedBlocks().contains(material)) {
                                items.getItem().getAllowedBlocks().remove(material);
                            } else {
                                items.getItem().getAllowedBlocks().add(material);
                            }
                            break;
                        //CUSTOM MODEL DATA
                        case "set_custom_model_data":
                            runChat(50, "$msg.gui.item_creator.custom_model_data.set$", guiAction.getGuiHandler());
                            break;
                        case "reset_custom_model_data":
                            itemMeta.setCustomModelData(null);
                            break;
                        case "durability_cost":
                            runChat(60, "", guiAction.getGuiHandler());
                            break;
                        case "consume_item_enabled":
                            items.getItem().setConsumed(false);
                            break;
                        case "consume_item_disabled":
                            items.getItem().setConsumed(true);

                    }

                    //Flag and attribute section
                    if (action.startsWith("flag_")) {
                        String attribute = action.split("_", 2)[1];
                        ItemFlag itemFlag = ItemFlag.valueOf("HIDE_" + attribute.toUpperCase());
                        if (!itemMeta.hasItemFlag(itemFlag)) {
                            itemMeta.addItemFlags(itemFlag);
                        } else {
                            itemMeta.removeItemFlags(itemFlag);
                        }
                    } else if (action.startsWith("attribute.generic") || action.startsWith("attribute.horse_") || action.startsWith("attribute.zombie_")) {
                        cache.setSubSetting(action.split("\\.")[1].toUpperCase());
                    }
                    itemStack.setItemMeta(itemMeta);
                    items.setItem(itemStack);
                    update(guiAction.getGuiHandler());
                }
            }
        }
        return true;
    }

    @Override
    public boolean onClick(GuiClick guiClick) {
        PlayerCache cache = CustomCrafting.getPlayerCache(guiClick.getPlayer());
        Bukkit.getScheduler().runTaskLater(CustomCrafting.getInst(), () -> {
            if (guiClick.getClickedSlot() == 13) {
                ItemStack item = guiClick.getPlayer().getOpenInventory().getTopInventory().getItem(13);
                cache.getItems().setItem(new CustomItem(item != null ? item : new ItemStack(Material.AIR)));
            } else {
                switch (cache.getSubSetting()) {
                    case "skull_setting":
                        ItemStack skull = guiClick.getPlayer().getOpenInventory().getTopInventory().getItem(38);
                        if (skull != null) {
                            if (!skull.equals(guiClick.getGuiHandler().getItem("none", "glass_gray"))) {
                                cache.getItems().setSkullSetting(skull);
                            }
                        } else {
                            cache.getItems().setSkullSetting(new ItemStack(Material.AIR));
                        }
                        break;
                    case "variants":
                        ItemStack item = guiClick.getPlayer().getOpenInventory().getTopInventory().getItem(38);
                        if (item != null) {
                            cache.getItems().setVariantItem(CustomItem.getByItemStack(item));
                        } else {
                            cache.getItems().setVariantItem(new CustomItem(Material.AIR));
                        }
                        break;
                    case "replacement_durability":
                        ItemStack replacement = guiClick.getPlayer().getOpenInventory().getTopInventory().getItem(38);
                        if (replacement != null) {
                            cache.getItems().getItem().setReplacement(CustomItem.getByItemStack(replacement));
                        } else {
                            cache.getItems().getItem().setReplacement(null);
                        }
                        break;
                }
            }
            update(guiClick.getGuiHandler());
        }, 1);
        return false;
    }

    @Override
    public boolean parseChatMessage(int id, String message, GuiHandler guiHandler) {
        Player player = guiHandler.getPlayer();
        PlayerCache cache = CustomCrafting.getPlayerCache(player);
        Items items = cache.getItems();
        String[] args = message.split(" ");
        CustomItem customItem = items.getItem();
        ItemMeta itemMeta = items.getItem().getItemMeta();
        List<String> lore;
        PotionEffectType type;
        switch (id) {
            case 0:
                if (args.length > 1) {
                    String namespace = args[0].toLowerCase(Locale.ROOT).replace(" ", "_");
                    String key = args[1].toLowerCase(Locale.ROOT).replace(" ", "_");
                    if(!CustomCrafting.VALID_NAMESPACE.matcher(namespace).matches()){
                        api.sendPlayerMessage(player, "&cInvalid Namespace! Namespaces may only contain lowercase alphanumeric characters, periods, underscores, and hyphens!");
                        return true;
                    }
                    if(!CustomCrafting.VALID_KEY.matcher(key).matches()){
                        api.sendPlayerMessage(player, "&cInvalid key! Keys may only contain lowercase alphanumeric characters, periods, underscores, and hyphens!");
                        return true;
                    }

                    CustomItem.saveItem(cache, namespace + ":" + key, items.getItem());
                    api.sendPlayerMessage(player, "$msg.gui.item_creator.save.success$");
                    api.sendPlayerMessage(player, "&6" + namespace + "/items/" + key);
                } else {
                    return true;
                }
                break;
            //DisplayName
            case 1:
                itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', message));
                items.getItem().setItemMeta(itemMeta);
                break;
            //Enchantments ADD
            case 2:
                if (args.length > 1) {
                    int level;
                    try {
                        level = Integer.parseInt(args[args.length - 1]);
                    } catch (NumberFormatException ex) {
                        api.sendPlayerMessage(player, "$msg.gui.item_creator.enchant.invalid_lvl$");
                        return true;
                    }
                    Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(args[0].toLowerCase(Locale.ROOT).replace(' ', '_')));
                    if (enchantment != null) {
                        items.getItem().addUnsafeEnchantment(enchantment, level);
                    } else {
                        api.sendPlayerMessage(player, "$msg.gui.item_creator.enchant.invalid_enchant$", new String[]{"%ENCHANT%", args[0]});
                        return true;
                    }
                } else {
                    api.sendPlayerMessage(player, "$msg.gui.item_creator.enchant.no_lvl$");
                    return true;
                }
                break;
            //REMOVE
            case 3:
                Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(args[0].toLowerCase(Locale.ROOT).replace(' ', '_')));
                if (enchantment != null) {
                    items.getItem().removeEnchantment(enchantment);
                } else {
                    api.sendPlayerMessage(player, "$msg.gui.item_creator.enchant.invalid_enchant$", new String[]{"%ENCHANT%", args[0]});
                    return true;
                }
                break;
            //Lore ADD
            case 4:
                lore = itemMeta.hasLore() ? itemMeta.getLore() : new ArrayList<>();
                if (message.equals("&empty")) {
                    lore.add("");
                } else {
                    lore.add(WolfyUtilities.translateColorCodes(message));
                }
                itemMeta.setLore(lore);
                items.getItem().setItemMeta(itemMeta);
                break;
            //REMOVE LORE
            case 5:
                if (!itemMeta.hasLore()) {
                    return false;
                }
                int index;
                try {
                    index = Integer.parseInt(args[0]) - 1;
                } catch (NumberFormatException e) {
                    api.sendPlayerMessage(player, "$msg.gui.item_creator.lore.no_number$");
                    return true;
                }
                lore = itemMeta.getLore();
                if (lore.size() > index && !(index < 0)) {
                    lore.remove(index);
                } else {
                    api.sendPlayerMessage(player, "$msg.gui.item_creator.lore.error$", new String[]{"%NUM%", args[0]});
                    return true;
                }
                itemMeta.setLore(lore);
                items.getItem().setItemMeta(itemMeta);
                api.sendPlayerMessage(player, "$msg.gui.item_creator.lore.success$", new String[]{"%NUM%", args[0]});
                return false;
            //Potion
            case 6:
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
                        api.sendPlayerMessage(player, "$msg.gui.item_creator.potion.error_number$");
                        return true;
                    }
                }
                if (type != null) {
                    PotionEffect potionEffect = new PotionEffect(type, duration, amplifier, ambient, particles);
                    ((PotionMeta) itemMeta).addCustomEffect(potionEffect, true);

                    api.sendPlayerMessage(player, "$msg.gui.item_creator.potion.success$", new String[]{"%TYPE%", type.getName()}, new String[]{"%DUR%", String.valueOf(duration)}, new String[]{"%AMP%", String.valueOf(amplifier)}, new String[]{"%AMB%", String.valueOf(ambient)}, new String[]{"%PAR%", String.valueOf(particles)});
                    items.getItem().setItemMeta(itemMeta);
                    return false;

                }
                api.sendPlayerMessage(player, "$msg.gui.item_creator.potion.wrong_args$");
                return true;

            //REMOVE
            case 7:
                if (!(itemMeta instanceof PotionMeta)) {
                    return true;
                }
                type = Legacy.getPotion(args[0]);
                if (type != null) {
                    ((PotionMeta) itemMeta).removeCustomEffect(type);
                    items.getItem().setItemMeta(itemMeta);
                    return false;
                }
                api.sendPlayerMessage(player, "$msg.gui.item_creator.potion.invalid_name$", new String[]{"%NAME%", args[0]});
                return true;
            case 8:
                try {
                    items.setAttribAmount(Double.parseDouble(args[0]));
                } catch (NumberFormatException e) {
                    api.sendPlayerMessage(player, "$msg.gui.item_creator.attribute.amount.error$");
                    return true;
                }
                return false;
            case 9:
                items.setAttributeName(args[0]);
                return false;
            case 10:
                try {
                    UUID uuid = UUID.fromString(args[0]);
                    items.setAttributeUUID(uuid.toString());
                } catch (IllegalArgumentException ex) {
                    api.sendPlayerMessage(player, "$msg.gui.item_creator.attribute.uuid.error.line1$", new String[]{"%UUID%", args[0]});
                    api.sendPlayerMessage(player, "$msg.gui.item_creator.attribute.uuid.error.line2$");
                    return true;
                }
                return false;
            case 11:
                if (!(itemMeta instanceof SkullMeta)) {
                    return true;
                }
                try {
                    UUID uuid = UUID.fromString(args[0]);
                    ((SkullMeta) itemMeta).setOwningPlayer(Bukkit.getOfflinePlayer(uuid));
                } catch (IllegalArgumentException e) {
                    ((SkullMeta) itemMeta).setOwningPlayer(Bukkit.getOfflinePlayer(args[0]));
                }
                items.getItem().setItemMeta(itemMeta);
                return false;
            case 20:
                if (!(itemMeta instanceof Damageable)) {
                    return true;
                }
                try {
                    int value = Integer.parseInt(message);
                    ((Damageable) itemMeta).setDamage(value);
                    items.getItem().setItemMeta(itemMeta);
                    api.sendPlayerMessage(player, "$msg.gui.item_creator.damage.value_success$", new String[]{"%VALUE%", String.valueOf(value)});
                } catch (NumberFormatException e) {
                    api.sendPlayerMessage(player, "$msg.gui.item_creator.damage.invalid_value$", new String[]{"%VALUE%", message});
                    return true;
                }
                return false;
            case 30:
                if (!(itemMeta instanceof Repairable)) {
                    return true;
                }
                try {
                    int value = Integer.parseInt(message);
                    ((Repairable) itemMeta).setRepairCost(value);
                    items.getItem().setItemMeta(itemMeta);
                    api.sendPlayerMessage(player, "$msg.gui.item_creator.repair.value_success$", new String[]{"%VALUE%", String.valueOf(value)});
                } catch (NumberFormatException e) {
                    api.sendPlayerMessage(player, "$msg.gui.item_creator.repair.invalid_value$", new String[]{"%VALUE%", message});
                    return true;
                }
                return false;
            case 40:
                try {
                    int value = Integer.parseInt(message);
                    customItem.setBurnTime(value);
                    api.sendPlayerMessage(player, "$msg.gui.item_creator.fuel.value_success$", new String[]{"%VALUE%", String.valueOf(value)});
                } catch (NumberFormatException e) {
                    api.sendPlayerMessage(player, "$msg.gui.item_creator.fuel.invalid_value$", new String[]{"%VALUE%", message});
                    return true;
                }
                return false;
            case 50:
                try {
                    int value = Integer.parseInt(message);
                    itemMeta.setCustomModelData(value);
                    customItem.setItemMeta(itemMeta);
                    api.sendPlayerMessage(player, "$msg.gui.item_creator.custom_model_data.success$", new String[]{"%VALUE%", String.valueOf(value)});
                } catch (NumberFormatException e) {
                    api.sendPlayerMessage(player, "$msg.gui.item_creator.custom_model_data.invalid_value$", new String[]{"%VALUE%", message});
                    return true;
                }
                return false;
            case 60:
                try {
                    int value = Integer.parseInt(message);
                    items.getItem().setDurabilityCost(value);
                    //api.sendPlayerMessage(player, "", new String[]{"%VALUE%", String.valueOf(value)});
                } catch (NumberFormatException e) {
                    //api.sendPlayerMessage(player, "", new String[]{"%VALUE%", message});
                    return true;
                }
                return false;
        }
        return false;
    }
}
