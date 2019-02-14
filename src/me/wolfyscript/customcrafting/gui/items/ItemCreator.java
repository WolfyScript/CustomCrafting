package me.wolfyscript.customcrafting.gui.items;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.custom_configs.ItemConfig;
import me.wolfyscript.customcrafting.gui.PlayerCache;
import me.wolfyscript.customcrafting.gui.Setting;
import me.wolfyscript.customcrafting.items.CustomItem;
import me.wolfyscript.customcrafting.items.ItemUtils;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.*;
import me.wolfyscript.utilities.api.utils.Legacy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class ItemCreator extends GuiWindow {

    private WolfyUtilities api = CustomCrafting.getApi();

    public ItemCreator(InventoryAPI inventoryAPI) {
        super("item_creator", inventoryAPI, 54);
    }

    @Override
    public void onInit() {
        createItem("save_item", Material.WRITABLE_BOOK);
        createItem("apply_item", Material.GREEN_CONCRETE);

        createItem("item_name", Material.NAME_TAG);
        createItem("item_enchantments", Material.ENCHANTED_BOOK);
        createItem("item_lore", Material.WRITABLE_BOOK);
        createItem("item_flags", Material.WRITTEN_BOOK);
        createItem("attributes_modifiers", Material.ENCHANTED_GOLDEN_APPLE);
        createItem("unbreakable_on", Material.BEDROCK);
        createItem("unbreakable_off", Material.GLASS);
        createItem("skull_setting_on", Material.PLAYER_HEAD);
        createItem("skull_setting_off", Material.PLAYER_HEAD);
        createItem("potion_effects", Material.POTION);
        createItem("destroys", Material.BARRIER);
        createItem("placed_on", Material.GRASS_BLOCK);

        createItem("potion_add", Material.GREEN_CONCRETE);
        createItem("potion_remove", Material.RED_CONCRETE);

        createItem("enchant_add", Material.ENCHANTED_BOOK);
        createItem("enchant_remove", Material.ENCHANTED_BOOK);
        createItem("lore_add", Material.WRITABLE_BOOK);
        createItem("lore_remove", Material.WRITTEN_BOOK);

        createItem("destroys_add", Material.GREEN_CONCRETE);
        createItem("destroys_remove", Material.RED_CONCRETE);
        createItem("placed_on_add", Material.GREEN_CONCRETE);
        createItem("placed_on_remove", Material.RED_CONCRETE);

        createItem("set_displayname", Material.NAME_TAG);
        createItem("allow_displayname_on", Material.NAME_TAG);
        createItem("allow_displayname_off", Material.NAME_TAG);

        createItem("skull_texture", Material.GREEN_CONCRETE);
        createItem("skull_owner", Material.NAME_TAG);

        createItem("flag_enchants", Material.ENCHANTING_TABLE);
        createItem("flag_attributes", Material.ENCHANTED_GOLDEN_APPLE);
        createItem("flag_unbreakable", Material.BEDROCK);
        createItem("flag_destroys", Material.TNT);
        createItem("flag_placed_on", Material.GRASS_BLOCK);
        createItem("flag_potion_effects", Material.POTION);

        createItem("attribute_max_health", Material.ENCHANTED_GOLDEN_APPLE);
        createItem("attribute_follow_range", Material.ENDER_EYE);
        createItem("attribute_knockback_resistance", Material.STICK);
        createItem("attribute_movement_speed", Material.IRON_BOOTS);
        createItem("attribute_flying_speed", Material.FIREWORK_ROCKET);
        createItem("attribute_attack_damage", Material.DIAMOND_SWORD);
        createItem("attribute_attack_speed", Material.DIAMOND_AXE);
        createItem("attribute_armor", Material.CHAINMAIL_CHESTPLATE);
        createItem("attribute_armor_toughness", Material.DIAMOND_CHESTPLATE);
        createItem("attribute_luck", Material.NETHER_STAR);
        createItem("attribute_horse_jump_strength", Material.DIAMOND_HORSE_ARMOR);
        createItem("attribute_zombie_spawn_reinforcements", Material.ZOMBIE_HEAD);

        createItem("operation_add_number", WolfyUtilities.getSkullByValue("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjBiNTVmNzQ2ODFjNjgyODNhMWMxY2U1MWYxYzgzYjUyZTI5NzFjOTFlZTM0ZWZjYjU5OGRmMzk5MGE3ZTcifX19"));
        createItem("operation_add_scalar", WolfyUtilities.getSkullByValue("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTdiMTc5MWJkYzQ2ZDhhNWM1MTcyOWU4OTgyZmQ0MzliYjQwNTEzZjY0YjViYWJlZTkzMjk0ZWZjMWM3In19fQ=="));
        createItem("operation_multiply_scalar_1", WolfyUtilities.getSkullByValue("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTlmMjdkNTRlYzU1NTJjMmVkOGY4ZTE5MTdlOGEyMWNiOTg4MTRjYmI0YmMzNjQzYzJmNTYxZjllMWU2OWYifX19"));

        createItem("slot_hand", Material.IRON_SWORD);
        createItem("slot_off_hand", Material.SHIELD);
        createItem("slot_feet", Material.IRON_BOOTS);
        createItem("slot_legs", Material.IRON_LEGGINGS);
        createItem("slot_chest", Material.IRON_CHESTPLATE);
        createItem("slot_head", Material.IRON_HELMET);

        createItem("attribute_save", Material.GREEN_CONCRETE);
        createItem("attribute_delete", Material.RED_CONCRETE);
        createItem("set_amount", WolfyUtilities.getSkullByValue("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDYxYzhmZWJjYWMyMWI5ZjYzZDg3ZjlmZDkzMzU4OWZlNjQ2OGU5M2FhODFjZmNmNWU1MmE0MzIyZTE2ZTYifX19"));

    }

    @EventHandler
    public void onUpdate(GuiUpdateEvent event) {
        if (event.verify(this)) {
            PlayerCache cache = CustomCrafting.getPlayerCache(event.getPlayer());
            event.setItem(4, "none", "glass_white");
            event.setItem(12, "none", "glass_white");
            event.setItem(13, cache.getCustomItem());
            event.setItem(14, "none", "glass_white");
            event.setItem(22, "none", "glass_white");

            if (!cache.getItemTag().isEmpty()) {
                event.setItem(3, "apply_item");
            }
            event.setItem(5, "save_item");

            event.setItem(9, "item_name");
            event.setItem(10, "item_lore");

            event.setItem(18, "item_enchantments");
            event.setItem(19, "item_flags");

            event.setItem(20, "unbreakable_off");
            if (cache.getCustomItem().hasItemMeta() && cache.getCustomItem().getItemMeta().isUnbreakable()) {
                event.setItem(20, "unbreakable_on");
            }

            event.setItem(16, "potion_effects");
            event.setItem(17, "attributes_modifiers");

            event.setItem(24, "placed_on");
            event.setItem(25, "destroys");
            event.setItem(26, "skull_setting_on");

            ItemStack itemStack = cache.getCustomItem();
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (!itemStack.getType().equals(Material.AIR)) {
                //DRAW Sections
                switch (cache.getSubSetting()) {
                    case "item_name":
                        if (cache.isItemNameEnabled()) {
                            event.setItem(39, "set_displayname");
                            event.setItem(41, "allow_displayname_on");
                        } else {
                            event.setItem(40, "allow_displayname_off");
                        }
                        break;
                    case "item_enchantments":
                        event.setItem(39, "enchant_add");
                        event.setItem(41, "enchant_remove");
                        break;
                    case "item_lore":
                        event.setItem(39, "lore_add");
                        event.setItem(41, "lore_remove");
                        break;
                    case "item_flags":
                        event.setItem(37, event.getItem("flag_attributes", "%C%", itemMeta.hasItemFlag(ItemFlag.HIDE_ATTRIBUTES) ? "§3" : "§4"));
                        event.setItem(39, event.getItem("flag_unbreakable", "%C%", itemMeta.hasItemFlag(ItemFlag.HIDE_UNBREAKABLE) ? "§3" : "§4"));
                        event.setItem(41, event.getItem("flag_destroys", "%C%", itemMeta.hasItemFlag(ItemFlag.HIDE_DESTROYS) ? "§3" : "§4"));
                        event.setItem(43, event.getItem("flag_placed_on", "%C%", itemMeta.hasItemFlag(ItemFlag.HIDE_PLACED_ON) ? "§3" : "§4"));
                        event.setItem(47, event.getItem("flag_potion_effects", "%C%", itemMeta.hasItemFlag(ItemFlag.HIDE_POTION_EFFECTS) ? "§3" : "§4"));
                        event.setItem(51, event.getItem("flag_enchants", "%C%", itemMeta.hasItemFlag(ItemFlag.HIDE_ENCHANTS) ? "§3" : "§4"));
                        break;
                    case "attributes_modifiers":
                        event.setItem(36, "attribute_max_health");
                        event.setItem(37, "attribute_follow_range");
                        event.setItem(38, "attribute_knockback_resistance");
                        event.setItem(39, "attribute_movement_speed");
                        event.setItem(40, "attribute_flying_speed");
                        event.setItem(41, "attribute_attack_damage");
                        event.setItem(42, "attribute_attack_speed");
                        event.setItem(43, "attribute_armor");
                        event.setItem(44, "attribute_armor_toughness");
                        event.setItem(48, "attribute_luck");
                        event.setItem(49, "attribute_horse_jump_strength");
                        event.setItem(50, "attribute_zombie_spawn_reinforcements");
                        break;
                    case "skull_setting_on":
                        event.setItem(38, new ItemStack(Material.AIR));
                        event.setItem(39, "skull_texture");
                        event.setItem(41, "skull_owner");
                        break;
                    case "potion_effects":
                        if (itemMeta instanceof PotionMeta) {
                            event.setItem(39, "potion_add");
                            event.setItem(41, "potion_remove");
                        } else {
                            event.setItem(40, new ItemStack(Material.BARRIER));
                        }
                        break;
                    case "destroys":
                        event.setItem(31, "destroys");
                        event.setItem(38, "destroys_add");
                        event.setItem(39, "none", "glass_white");
                        event.setItem(40, new ItemStack(Material.AIR));
                        event.setItem(41, "none", "glass_white");
                        event.setItem(42, "destroys_remove");
                        break;
                    case "placed_on":
                        event.setItem(31, "placed_on");
                        event.setItem(38, "placed_on_add");
                        event.setItem(39, "none", "glass_white");
                        event.setItem(40, new ItemStack(Material.AIR));
                        event.setItem(41, "none", "glass_white");
                        event.setItem(42, "placed_on_remove");
                        break;
                }
                if (cache.getSubSetting().startsWith("attribute_")) {
                    event.setItem(36, "slot_head");
                    event.setItem(45, "slot_chest");
                    event.setItem(37, "slot_legs");
                    event.setItem(46, "slot_feet");
                    event.setItem(38, "slot_hand");
                    event.setItem(47, "slot_off_hand");

                    event.setItem(42, "operation_add_number");
                    event.setItem(43, "operation_add_scalar");
                    event.setItem(44, "operation_multiply_scalar_1");

                    event.setItem(52, "set_amount");

                    event.setItem(40, "attribute_save");
                    event.setItem(49, "attribute_delete");


                }

            }


        }
    }

    @Override
    public boolean onAction(GuiAction guiAction) {
        String action = guiAction.getAction();
        Player player = guiAction.getPlayer();
        PlayerCache cache = CustomCrafting.getPlayerCache(guiAction.getPlayer());
        if (action.equals("back")) {
            guiAction.getGuiHandler().openLastInv();
        } else if (action.equals("save_item") && !cache.getCustomItem().getType().equals(Material.AIR)) {
            if(cache.getItemTag(0).equals("items") && CustomCrafting.getRecipeHandler().getCustomItem(cache.getItemTag(2)) != null){
                ItemUtils.saveItem(cache.getItemTag(2), cache.getCustomItem());
                api.sendPlayerMessage(player, "&aItem saved to &6" + cache.getItemTag(2).split(":")[0] + "/items/" + cache.getItemTag(2).split(":")[1]);
            }else{
                runChat(0, "&3Type in the name of the folder and item! &6e.g. example your_item", guiAction.getGuiHandler());
            }
        } else if (action.equals("apply") && !cache.getCustomItem().getType().equals(Material.AIR)) {
            ItemStack itemStack = cache.getCustomItem();
            if(cache.getItemTag(1).equals("saved")) {
                ItemUtils.saveItem(cache.getItemTag(2), CustomCrafting.getRecipeHandler().getCustomItem(cache.getItemTag(2)));
                itemStack = CustomCrafting.getRecipeHandler().getCustomItem(cache.getItemTag(2)).getIDItem();
            }
            ItemUtils.applyItem(itemStack, cache);
            guiAction.getGuiHandler().changeToInv("recipe_creator");
        } else {
            ItemStack itemStack = cache.getCustomItem();
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (!itemStack.getType().equals(Material.AIR) && itemMeta != null) {
                switch (action) {
                    //TOP Section
                    case "item_name":
                    case "item_lore":
                    case "item_flags":
                    case "attributes_modifiers":
                    case "skull_setting_on":
                    case "item_enchantments":
                    case "potion_effects":
                    case "destroys":
                    case "placed_on":
                        cache.setSubSetting(action);
                        break;
                    case "unbreakable_off":
                        itemMeta.setUnbreakable(true);
                        break;
                    case "unbreakable_on":
                        itemMeta.setUnbreakable(false);
                        break;

                    //Display name section
                    case "allow_displayname_on":
                    case "allow_displayname_off":
                        cache.setItemName(!cache.isItemNameEnabled());
                        break;
                    case "set_displayname":
                        runChat(1, "&2Type in the name! Use &6& &2for formatting codes!", guiAction.getGuiHandler());
                        break;

                    //Enchantments
                    case "enchant_add":
                        runChat(2, "&2Type in the enchantment and level! e.g. &6unbreaking 5", guiAction.getGuiHandler());
                        break;
                    case "enchant_remove":
                        runChat(3, "&2Type in the enchantment! e.g. &6unbreaking", guiAction.getGuiHandler());
                        break;

                    //LORE
                    case "lore_add":
                        runChat(4, "&2Type in the lore! Use &6& &2for formatting codes!", guiAction.getGuiHandler());
                        break;
                    case "lore_remove":
                        runChat(5, "&2Type in the line of the lore you want to remove!", guiAction.getGuiHandler());
                        break;

                    //Potion
                    case "potion_add":
                        runChat(6, "&2Type in the Potion name, level, time in ticks! e.g. &6speed 5 1200", guiAction.getGuiHandler());
                        break;
                    case "potion_remove":
                        runChat(7, "&2Type in the Potion name you want to remove!", guiAction.getGuiHandler());
                        break;

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
                } else if (action.startsWith("attribute_")) {
                    cache.setSubSetting(action);
                }


                itemStack.setItemMeta(itemMeta);
                cache.setCustomItem(itemStack);
                update(guiAction.getGuiHandler());

            }

        }
        return true;
    }

    @Override
    public boolean onClick(GuiClick guiClick) {
        PlayerCache cache = CustomCrafting.getPlayerCache(guiClick.getPlayer());
        Bukkit.getScheduler().runTaskLater(CustomCrafting.getInst(), () -> {
            ItemStack item = guiClick.getPlayer().getOpenInventory().getTopInventory().getItem(13);
            cache.setCustomItem(item != null ? item : new ItemStack(Material.AIR));
            update(guiClick.getGuiHandler());
        }, 1);

        return false;
    }

    @Override
    public boolean parseChatMessage(int id, String message, GuiHandler guiHandler) {
        Player player = guiHandler.getPlayer();
        PlayerCache cache = CustomCrafting.getPlayerCache(player);
        String[] args = message.split(" ");
        ItemMeta itemMeta = cache.getCustomItem().getItemMeta();
        List<String> lore;
        PotionEffectType type;
        switch (id) {
            case 0:
                if (args.length > 1) {
                    ItemUtils.saveItem(args[0]+":"+args[1], cache.getCustomItem());
                    api.sendPlayerMessage(player, "&aItem saved to &6" + args[0] + "/items/" + args[1]);
                } else {
                    return true;
                }
                break;
            //DisplayName
            case 1:
                itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', message));
                cache.getCustomItem().setItemMeta(itemMeta);
                break;
            //Enchantments ADD
            case 2:
                if (args.length > 1) {
                    int level = 1;
                    try {
                        level = Integer.parseInt(args[1]);
                    } catch (NumberFormatException ex) {
                        api.sendPlayerMessage(player, "&cInvalid level! It's not a number!");
                        return true;
                    }
                    Enchantment enchantment = Legacy.getEnchantment(args[0]);
                    if (enchantment != null) {
                        cache.getCustomItem().addUnsafeEnchantment(enchantment, level);
                    } else {
                        api.sendPlayerMessage(player, "&cInvalid Enchantment! &4" + args[0] + " &c does not exist!");
                        return true;
                    }
                } else {
                    api.sendPlayerMessage(player, "&cNo level set!");
                    return true;
                }
                break;
            //REMOVE
            case 3:
                Enchantment enchantment = Legacy.getEnchantment(args[0]);
                if (enchantment != null) {
                    cache.getCustomItem().removeEnchantment(enchantment);
                } else {
                    api.sendPlayerMessage(player, "&cInvalid Enchantment! &4" + args[0] + " &c does not exist!");
                    return true;
                }
                break;
            //Lore ADD
            case 4:
                lore = itemMeta.hasLore() ? itemMeta.getLore() : new ArrayList<>();
                lore.add(ChatColor.translateAlternateColorCodes('&', message));
                itemMeta.setLore(lore);
                cache.getCustomItem().setItemMeta(itemMeta);
                break;
            //REMOVE
            case 5:
                if (!itemMeta.hasLore()) {
                    return false;
                }
                int index;
                try {
                    index = Integer.parseInt(args[0]) - 1;
                } catch (NumberFormatException e) {
                    api.sendPlayerMessage(player, "&cError getting the line! Is it a number? It should be!");
                    return true;
                }
                lore = itemMeta.getLore();
                if (lore.size() > index && !(index < 0)) {
                    lore.remove(index);
                } else {
                    api.sendPlayerMessage(player, "&cError the line &4" + args[0] + "&c does not exist!");
                    return true;
                }
                itemMeta.setLore(lore);
                cache.getCustomItem().setItemMeta(itemMeta);
                api.sendPlayerMessage(player, "&aRemoved Lore in line &6" + args[0]);
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
                        api.sendPlayerMessage(player, "&cError parsing Numbers! &4<name> <duration> <level> [<ambient> <particles>]");
                        return true;
                    }
                }
                if (type != null) {
                    PotionEffect potionEffect = new PotionEffect(type, duration, amplifier, ambient, particles);
                    ((PotionMeta) itemMeta).addCustomEffect(potionEffect, true);
                    api.sendPlayerMessage(player, "&aAdded Potion Effect &6" + type.getName() + ", " + duration + ", " + amplifier + ", " + ambient + ", " + particles);
                    cache.getCustomItem().setItemMeta(itemMeta);
                    return false;

                }
                api.sendPlayerMessage(player, "&cWrong arguments or invalid name! &4<name> <duration> <level> [<ambient> <particles>]");
                return true;

            //REMOVE
            case 7:
                if (!(itemMeta instanceof PotionMeta)) {
                    return true;
                }
                type = Legacy.getPotion(args[0]);
                if (type != null) {
                    ((PotionMeta) itemMeta).removeCustomEffect(type);
                    cache.getCustomItem().setItemMeta(itemMeta);
                    return false;
                }
                api.sendPlayerMessage(player, "&cInvalid name! &4" + args[0] + "&c does not exist!");
                return true;
        }
        return false;
    }
}
