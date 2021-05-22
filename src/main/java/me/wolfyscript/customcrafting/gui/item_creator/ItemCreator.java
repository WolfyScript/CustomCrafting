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
import me.wolfyscript.customcrafting.gui.MainCluster;
import me.wolfyscript.customcrafting.gui.PotionCreatorCluster;
import me.wolfyscript.customcrafting.gui.item_creator.buttons.*;
import me.wolfyscript.customcrafting.utils.ChatUtils;
import me.wolfyscript.customcrafting.utils.ItemLoader;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.customcrafting.utils.PlayerUtil;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.custom_items.references.*;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.*;
import me.wolfyscript.utilities.util.Pair;
import me.wolfyscript.utilities.util.Registry;
import me.wolfyscript.utilities.util.chat.ChatColor;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import me.wolfyscript.utilities.util.inventory.PlayerHeadUtils;
import me.wolfyscript.utilities.util.inventory.item_builder.ItemBuilder;
import me.wolfyscript.utilities.util.particles.ParticleLocation;
import me.wolfyscript.utilities.util.version.MinecraftVersion;
import me.wolfyscript.utilities.util.version.ServerVersion;
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

    private static final String UNBREAKABLE = "unbreakable";
    private static final String BACK = "back";
    private static final String SAVE_ITEM = "save_item";
    private static final String APPLY_ITEM = "apply_item";
    private static final String ITEM_INPUT = "item_input";
    private static final String PAGE_NEXT = "page_next";
    private static final String PAGE_PREVIOUS = "page_previous";

    private static final String REFERENCE_WOLFYUTILITIES = "reference.wolfyutilities";
    private static final String REFERENCE_ORAXEN = "reference.oraxen";
    private static final String REFERENCE_ITEMSADDER = "reference.itemsadder";
    private static final String REFERENCE_MYTHICMOBS = "reference.mythicmobs";

    private static final String DISPLAY_NAME = "display_name";
    private static final String LORE = "lore";
    private static final String ENCHANTS = "enchantments";
    private static final String FLAGS = "flags";
    private static final String PARTICLE_EFFECTS = "particle_effects";
    private static final String ATTRIBUTE = "attribute";
    private static final String POTION = "potion";
    private static final String PLAYER_HEAD = "player_head";
    private static final String DAMAGE = "damage";
    private static final String REPAIR_COST = "repair_cost";
    private static final String FUEL = "fuel";
    private static final String CUSTOM_MODEL_DATA = "custom_model_data";
    private static final String CONSUME = "consume";
    private static final String CUSTOM_DURABILITY = "custom_durability";
    private static final String LOCALIZED_NAME = "localized_name";
    private static final String PERMISSION = "permission";
    private static final String RARITY = "rarity";
    private static final String ELITE_CRAFTING_TABLE = "elite_workbench";
    private static final String RECIPE_BOOK = "knowledge_book";
    private static final String ARMOR_SLOTS = "armor_slots";
    private static final String VANILLA = "vanilla";

    public ItemCreator(GuiCluster<CCCache> cluster, CustomCrafting customCrafting) {
        super(cluster, "main_menu", 54, customCrafting);
    }

    @Override
    public void onInit() {
        registerButton(new ActionButton<>(BACK, new ButtonState<>(MainCluster.BACK, PlayerHeadUtils.getViaURL("864f779a8e3ffa231143fa69b96b14ee35c16d669e19c75fd1a7da4bf306c"), (cache, guiHandler, player, inventory, i, event) -> {
            if (cache.getItems().isRecipeItem()) {
                guiHandler.openCluster("recipe_creator");
            } else {
                guiHandler.openCluster("none");
            }
            return true;
        })));
        registerButton(new ItemInputButton<>(ITEM_INPUT, new ButtonState<>("", Material.AIR, (cache, guiHandler, player, inventory, slot, event) -> false, (cache, guiHandler, player, inventory, item, slot, event) -> {
            Items items = cache.getItems();
            items.setItem(CustomItem.getReferenceByItemStack(item != null ? item : ItemUtils.AIR));
        }, null, (hashMap, cache, guiHandler, player, guiInventory, itemStack, i, b) -> guiHandler.getCustomCache().getItems().getItem().getItemStack())));
        registerButton(new ActionButton<>(SAVE_ITEM, Material.WRITABLE_BOOK, (cache, guiHandler, player, inventory, i, event) -> {
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
                        ItemLoader.saveItem(namespacedKey, items.getItem());
                        items.setSaved(true);
                        items.setNamespacedKey(namespacedKey);
                        sendMessage(player, "save.success");
                        me.wolfyscript.utilities.util.NamespacedKey internalKey = NamespacedKeyUtils.toInternal(namespacedKey);
                        api.getChat().sendMessage(player1, "&6" + internalKey.getNamespace() + "/items/" + internalKey.getKey());
                        return false;
                    }
                    return true;
                });
            }
            return true;
        }));

        registerButton(new ActionButton<>(APPLY_ITEM, Material.GREEN_CONCRETE, (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
            if (!items.getItem().getItemStack().getType().equals(Material.AIR)) {
                CustomItem customItem = cache.getItems().getItem();
                if (items.isSaved()) {
                    ItemLoader.saveItem(items.getNamespacedKey(), customItem);
                    customItem = Registry.CUSTOM_ITEMS.get(items.getNamespacedKey());
                }
                cache.applyItem(customItem);
                guiHandler.openCluster("recipe_creator");
            }
            return true;
        }));

        registerButton(new ActionButton<>(PAGE_NEXT, PlayerHeadUtils.getViaURL("c86185b1d519ade585f184c34f3f3e20bb641deb879e81378e4eaf209287"), (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
            items.setPage(items.getPage() + 1);
            return true;
        }));
        registerButton(new ActionButton<>(PAGE_PREVIOUS, PlayerHeadUtils.getViaURL("ad73cf66d31b83cd8b8644c15958c1b73c8d97323b801170c1d8864bb6a846d"), (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
            if (items.getPage() > 0) {
                items.setPage(items.getPage() - 1);
            }
            return true;
        }));
        registerReferences();
        registerDisplayName();
        registerEnchants();
        registerLore();
        registerItemFlags();
        registerAttributes();
        registerPlayerHead();
        registerPotion();
        registerUnbreakable();
        registerDamage();
        registerRepairCost();
        registerCustomModelData();
        registerConsume();
        registerFuel();
        registerCustomDurability();
        registerLocalizedName();
        registerPermission();
        registerRarity();
        registerRecipeBook();
        registerEliteCraftingTable();
        registerArmorSlots();
        registerParticleEffects();
        registerVanilla();
        registerButton(new OptionButton(Material.BOOKSHELF, "persistent_data"));
        Registry.META_PROVIDER.keySet().forEach(namespacedKey -> registerButton(new MetaIgnoreButton(namespacedKey)));
    }

    @Override
    public void onUpdateAsync(GuiUpdate<CCCache> event) {
        super.onUpdateAsync(event);
        GuiHandler<CCCache> guiHandler = event.getGuiHandler();
        CCCache cache = guiHandler.getCustomCache();
        Items items = cache.getItems();
        CustomItem customItem = items.getItem();
        ItemStack item = customItem.create();

        event.setButton(0, BACK);
        event.setButton(13, ITEM_INPUT);

        CCPlayerData data = PlayerUtil.getStore(event.getPlayer());
        me.wolfyscript.utilities.util.NamespacedKey gray = data.getLightBackground();
        event.setButton(4, gray);
        event.setButton(12, gray);
        event.setButton(14, gray);
        event.setButton(22, gray);

        if (items.isRecipeItem()) {
            event.setButton(2, APPLY_ITEM);
        }
        event.setButton(3, SAVE_ITEM);

        List<String> options = new ArrayList<>();
        if (customItem.getApiReference() instanceof VanillaRef) {
            options.add("display_name.option");
            options.add("localized_name.option");
            options.add("lore.option");
            options.add("enchantments.option");
            options.add("flags.option");
            options.add("attribute.option");
            if (!ItemUtils.isAirOrNull(items.getItem())) {
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
                event.setButton(5, REFERENCE_WOLFYUTILITIES);
            } else if (customItem.getApiReference() instanceof OraxenRef) {
                event.setButton(5, REFERENCE_ORAXEN);
            } else if (customItem.getApiReference() instanceof ItemsAdderRef) {
                event.setButton(5, REFERENCE_ITEMSADDER);
            } else if (customItem.getApiReference() instanceof MythicMobsRef) {
                event.setButton(5, REFERENCE_MYTHICMOBS);
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
            event.setButton(5, PAGE_PREVIOUS);
        }
        if (items.getPage() + 1 < maxPages) {
            event.setButton(5, PAGE_NEXT);
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
                case DISPLAY_NAME:
                    event.setButton(39, DISPLAY_NAME + ".set");
                    event.setButton(41, DISPLAY_NAME + ".remove");
                    event.setButton(45, "meta_ignore.wolfyutilities.name");
                    break;
                case ENCHANTS:
                    event.setButton(39, ENCHANTS + ".add");
                    event.setButton(41, ENCHANTS + ".remove");
                    event.setButton(45, "meta_ignore.wolfyutilities.enchant");
                    break;
                case LORE:
                    event.setButton(39, LORE + ".add");
                    event.setButton(41, LORE + ".remove");
                    event.setButton(45, "meta_ignore.wolfyutilities.lore");
                    break;
                case FLAGS:
                    event.setButton(28, "flags.attributes");
                    event.setButton(30, "flags.unbreakable");
                    event.setButton(32, "flags.destroys");
                    event.setButton(34, "flags.placed_on");
                    event.setButton(38, "flags.potion_effects");
                    if (ServerVersion.isAfterOrEq(MinecraftVersion.of(1, 16, 2))) {
                        event.setButton(40, "flags.dye");
                    }
                    event.setButton(42, "flags.enchants");
                    event.setButton(45, "meta_ignore.wolfyutilities.flags");
                    break;
                case ATTRIBUTE:
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
                    event.setButton(45, "meta_ignore.wolfyutilities.attributes_modifiers");
                    break;
                case PLAYER_HEAD:
                    if (items.getItem() != null && item.getType().equals(Material.PLAYER_HEAD)) {
                        event.setButton(38, "player_head.texture.input");
                        event.setButton(39, "player_head.texture.apply");
                        event.setButton(41, "player_head.owner");
                        event.setButton(45, "meta_ignore.wolfyutilities.playerHead");
                    }
                    break;
                case POTION:
                    if (items.getItem() != null && item.hasItemMeta() && item.getItemMeta() instanceof PotionMeta) {
                        event.setButton(39, "potion.add");
                        event.setButton(40, "potion_beta.add");
                        event.setButton(41, "potion.remove");
                        event.setButton(45, "meta_ignore.wolfyutilities.potion");
                    }
                    break;
                case DAMAGE:
                    event.setButton(39, "damage.set");
                    event.setButton(41, "damage.reset");
                    event.setButton(45, "meta_ignore.wolfyutilities.damage");
                    break;
                case REPAIR_COST:
                    event.setButton(39, "repair_cost.set");
                    event.setButton(41, "repair_cost.reset");
                    event.setButton(45, "meta_ignore.wolfyutilities.repairCost");
                    break;
                case FUEL:
                    event.setButton(39, "fuel.burn_time.set");
                    event.setButton(41, "fuel.burn_time.reset");
                    event.setButton(47, "fuel.furnace");
                    event.setButton(49, "fuel.blast_furnace");
                    event.setButton(51, "fuel.smoker");
                    break;
                case CUSTOM_MODEL_DATA:
                    event.setButton(39, "custom_model_data.set");
                    event.setButton(41, "custom_model_data.reset");
                    event.setButton(45, "meta_ignore.wolfyutilities.customModelData");
                    break;
                case CONSUME:
                    event.setButton(31, "consume.consume_item");
                    event.setButton(38, "consume.replacement");
                    event.setButton(39, items.getItem().hasReplacement() ? "consume.replacement.enabled" : "consume.replacement.disabled");
                    if (customItem.hasReplacement() || item.getMaxStackSize() > 1) {
                        event.setButton(41, "consume.durability_cost.disabled");
                    } else {
                        event.setButton(41, "consume.durability_cost.enabled");
                    }
                    break;
                case CUSTOM_DURABILITY:
                    event.setButton(38, "custom_durability.set_damage");
                    event.setButton(40, "custom_durability.set_tag");
                    event.setButton(42, "custom_durability.set_durability");
                    event.setButton(49, "custom_durability.remove");
                    event.setButton(45, "meta_ignore.wolfyutilities.custom_damage");
                    event.setButton(53, "meta_ignore.wolfyutilities.custom_durability");
                    break;
                case LOCALIZED_NAME:
                    event.setButton(39, "localized_name.set");
                    event.setButton(41, "localized_name.remove");
                    break;
                case PERMISSION:
                    event.setButton(39, "permission.set");
                    event.setButton(41, "permission.remove");
                    break;
                case RARITY:
                    event.setButton(39, "rarity.set");
                    event.setButton(41, "rarity.reset");
                    break;
                case ELITE_CRAFTING_TABLE:
                    if (!item.getType().isBlock()) break;
                    event.setButton(37, "elite_workbench.particles");
                    event.setButton(39, "elite_workbench.grid_size");
                    event.setButton(41, "elite_workbench.toggle");
                    event.setButton(43, "elite_workbench.advanced_recipes");
                    break;
                case RECIPE_BOOK:
                    event.setButton(40, "knowledge_book.toggle");
                    break;
                case ARMOR_SLOTS:
                    event.setButton(37, "armor_slots.head");
                    event.setButton(39, "armor_slots.chest");
                    event.setButton(41, "armor_slots.legs");
                    event.setButton(43, "armor_slots.feet");
                    break;
                case PARTICLE_EFFECTS:
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
                case VANILLA:
                    event.setButton(38, "vanilla.block_recipes");
                    if (!item.getType().isBlock()) break;
                    event.setButton(40, "vanilla.block_placement");
                    break;
                default:
                    //NONE selected
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

    private void registerReferences() {
        registerButton(new DummyButton<>(REFERENCE_WOLFYUTILITIES, Material.CRAFTING_TABLE, (hashMap, cache, guiHandler, player, inventory, itemStack, i, b) -> {
            hashMap.put("%item_key%", ((WolfyUtilitiesRef) guiHandler.getCustomCache().getItems().getItem().getApiReference()).getNamespacedKey().toString());
            return itemStack;
        }));
        registerButton(new DummyButton<>(REFERENCE_ORAXEN, Material.DIAMOND, (hashMap, cache, guiHandler, player, inventory, itemStack, i, b) -> {
            hashMap.put("%item_key%", ((OraxenRef) guiHandler.getCustomCache().getItems().getItem().getApiReference()).getItemID());
            return itemStack;
        }));
        registerButton(new DummyButton<>(REFERENCE_ITEMSADDER, Material.GRASS_BLOCK, (hashMap, cache, guiHandler, player, inventory, itemStack, i, b) -> {
            hashMap.put("%item_key%", ((ItemsAdderRef) guiHandler.getCustomCache().getItems().getItem().getApiReference()).getItemID());
            return itemStack;
        }));
        registerButton(new DummyButton<>(REFERENCE_MYTHICMOBS, Material.WITHER_SKELETON_SKULL));
    }

    private void registerArmorSlots() {
        registerButton(new OptionButton(Material.IRON_HELMET, ARMOR_SLOTS));
        registerButton(new ArmorSlotToggleButton(EquipmentSlot.HEAD, Material.DIAMOND_HELMET));
        registerButton(new ArmorSlotToggleButton(EquipmentSlot.CHEST, Material.DIAMOND_CHESTPLATE));
        registerButton(new ArmorSlotToggleButton(EquipmentSlot.LEGS, Material.DIAMOND_LEGGINGS));
        registerButton(new ArmorSlotToggleButton(EquipmentSlot.FEET, Material.DIAMOND_BOOTS));
    }

    private void registerRecipeBook() {
        registerButton(new OptionButton(Material.KNOWLEDGE_BOOK, RECIPE_BOOK));
        registerButton(new ToggleButton<>("knowledge_book.toggle", (cache, guiHandler, player, guiInventory, i) -> ((RecipeBookData) cache.getItems().getItem().getCustomData(CustomCrafting.RECIPE_BOOK)).isEnabled(), new ButtonState<>("knowledge_book.toggle.enabled", Material.GREEN_CONCRETE, (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
            ((RecipeBookData) items.getItem().getCustomData(CustomCrafting.RECIPE_BOOK)).setEnabled(false);
            return true;
        }), new ButtonState<>("knowledge_book.toggle.disabled", Material.RED_CONCRETE, (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
            ((RecipeBookData) items.getItem().getCustomData(CustomCrafting.RECIPE_BOOK)).setEnabled(true);
            return true;
        })));
    }

    private void registerRarity() {
        registerButton(new OptionButton(Material.DIAMOND, RARITY));
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

    private void registerPermission() {
        registerButton(new OptionButton(Material.BARRIER, PERMISSION));
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

    private void registerCustomDurability() {
        registerButton(new OptionButton(Material.DIAMOND_SWORD, CUSTOM_DURABILITY));
        registerButton(new ActionButton<>("custom_durability.remove", Material.RED_CONCRETE_POWDER, (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
            items.getItem().removeCustomDurability();
            return true;
        }));
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

    private void registerFuel() {
        registerButton(new OptionButton(Material.COAL, FUEL));
        registerButton(new ChatInputButton<>("fuel.burn_time.set", Material.GREEN_CONCRETE, (values, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            values.put("%VAR%", guiHandler.getCustomCache().getItems().getItem().getBurnTime());
            return itemStack;
        }, (guiHandler, player, s, strings) -> {
            try {
                int value = Integer.parseInt(s);
                guiHandler.getCustomCache().getItems().getItem().setBurnTime(value);
                sendMessage(player, "fuel.value_success", new Pair<>("%VALUE%", String.valueOf(value)));
            } catch (NumberFormatException e) {
                sendMessage(player, "fuel.invalid_value", new Pair<>("%VALUE%", s));
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

    private void registerConsume() {
        registerButton(new OptionButton(Material.ITEM_FRAME, CONSUME));
        registerButton(new ChatInputButton<>(CONSUME + ".durability_cost.enabled", Material.DROPPER, (hashMap, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            hashMap.put("%VAR%", guiHandler.getCustomCache().getItems().getItem().getDurabilityCost());
            return itemStack;
        }, (guiHandler, player, s, strings) -> {
            try {
                int value = Integer.parseInt(s);
                guiHandler.getCustomCache().getItems().getItem().setDurabilityCost(value);
                sendMessage(player, "consume.valid", new Pair<>("%VALUE%", String.valueOf(value)));
            } catch (NumberFormatException e) {
                sendMessage(player, "consume.invalid", new Pair<>("%VALUE%", s));
                return true;
            }
            return false;
        }));
        registerButton(new DummyButton<>(CONSUME + ".durability_cost.disabled", Material.DROPPER));

        registerButton(new ToggleButton<>(CONSUME + ".consume_item", (cache, guiHandler, player, guiInventory, i) -> cache.getItems().getItem().isConsumed(), new ButtonState<>("consume.consume_item.enabled", Material.GREEN_CONCRETE, (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
            items.getItem().setConsumed(false);
            return true;
        }), new ButtonState<>(CONSUME + ".consume_item.disabled", Material.RED_CONCRETE, (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
            items.getItem().setConsumed(true);
            return true;
        })));

        registerButton(new DummyButton<>(CONSUME + ".replacement.enabled", Material.GREEN_CONCRETE));
        registerButton(new DummyButton<>(CONSUME + ".replacement.disabled", Material.RED_CONCRETE));

        registerButton(new ItemInputButton<>(CONSUME + ".replacement", Material.AIR, (ItemsButtonAction) (cache, items, guiHandler, player, inventory, slot, event) -> {
            Bukkit.getScheduler().runTask(customCrafting, () -> {
                ItemStack replacement = inventory.getItem(slot);
                if (replacement != null) {
                    items.getItem().setReplacement(CustomItem.getReferenceByItemStack(replacement).getApiReference());
                } else {
                    items.getItem().setReplacement(null);
                }
            });
            return false;
        }, (hashMap, cache, guiHandler, player, inventory, itemStack, i, b) -> guiHandler.getCustomCache().getItems().getItem().hasReplacement() ? CustomItem.with(cache.getItems().getItem().getReplacement()).create() : new ItemStack(Material.AIR)));
    }

    private void registerCustomModelData() {
        registerButton(new OptionButton(Material.REDSTONE, CUSTOM_MODEL_DATA));
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
                sendMessage(player, "custom_model_data.success", new Pair<>("%VALUE%", String.valueOf(value)));
            } catch (NumberFormatException e) {
                sendMessage(player, "custom_model_data.invalid_value", new Pair<>("%VALUE%", s));
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

    private void registerPotion() {
        registerButton(new OptionButton(Material.POTION, POTION));
        registerButton(new ActionButton<>("potion.add", PlayerHeadUtils.getViaURL("9a2d891c6ae9f6baa040d736ab84d48344bb6b70d7f1a280dd12cbac4d777"), (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
            cache.getPotionEffectCache().setApplyPotionEffect((potionEffectCache1, cache1, potionEffect) -> {
                ItemMeta itemMeta = items.getItem().getItemMeta();
                if (itemMeta instanceof PotionMeta) {
                    ((PotionMeta) itemMeta).addCustomEffect(potionEffect, true);
                }
                items.getItem().setItemMeta(itemMeta);
            });
            cache.getPotionEffectCache().setRecipePotionEffect(false);
            guiHandler.openWindow(PotionCreatorCluster.POTION_CREATOR);
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
            guiHandler.openWindow(PotionCreatorCluster.POTION_EFFECT_TYPE_SELECTION);
            return true;
        }));
    }

    private void registerUnbreakable() {
        registerButton(new ToggleButton<>(UNBREAKABLE, (cache, guiHandler, player, guiInventory, i) -> {
            CustomItem item = cache.getItems().getItem();
            return !ItemUtils.isAirOrNull(item) && item.getItemMeta().isUnbreakable();
        }, new ButtonState<>(UNBREAKABLE + ".enabled", Material.BEDROCK, (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
            ItemMeta itemMeta = items.getItem().getItemMeta();
            itemMeta.setUnbreakable(false);
            items.getItem().setItemMeta(itemMeta);
            return true;
        }), new ButtonState<>(UNBREAKABLE + ".disabled", Material.GLASS, (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
            ItemMeta itemMeta = items.getItem().getItemMeta();
            itemMeta.setUnbreakable(true);
            items.getItem().setItemMeta(itemMeta);
            return true;
        })));
    }

    private void registerPlayerHead() {
        registerButton(new OptionButton(Material.PLAYER_HEAD, PLAYER_HEAD));
        registerButton(new ItemInputButton<>("player_head.texture.input", Material.AIR, (cache, guiHandler, player, inventory, i, event) -> {
            if (event instanceof InventoryClickEvent) {
                return ((InventoryClickEvent) event).getCurrentItem().getType().equals(Material.PLAYER_HEAD);
            }
            return true;
        }));
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

    private void registerDamage() {
        registerButton(new OptionButton(Material.IRON_SWORD, DAMAGE));
        registerButton(new ChatInputButton<>("damage.set", Material.GREEN_CONCRETE, (guiHandler, player, s, strings) -> {
            ItemMeta itemMeta = guiHandler.getCustomCache().getItems().getItem().getItemMeta();
            if (!(itemMeta instanceof Damageable)) {
                return true;
            }
            try {
                int value = Integer.parseInt(s);
                ((Damageable) itemMeta).setDamage(value);
                guiHandler.getCustomCache().getItems().getItem().setItemMeta(itemMeta);
                sendMessage(player, "damage.value_success", new Pair<>("%VALUE%", String.valueOf(value)));
            } catch (NumberFormatException e) {
                sendMessage(player, "damage.invalid_value", new Pair<>("%VALUE%", s));
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

    private void registerRepairCost() {
        registerButton(new OptionButton(Material.EXPERIENCE_BOTTLE, REPAIR_COST));
        registerButton(new ChatInputButton<>("repair_cost.set", Material.GREEN_CONCRETE, (guiHandler, player, s, strings) -> {
            ItemMeta itemMeta = guiHandler.getCustomCache().getItems().getItem().getItemMeta();
            try {
                int value = Integer.parseInt(s);
                ((Repairable) itemMeta).setRepairCost(value);
                guiHandler.getCustomCache().getItems().getItem().setItemMeta(itemMeta);
                sendMessage(player, "repair.value_success", new Pair<>("%VALUE%", String.valueOf(value)));
            } catch (NumberFormatException e) {
                sendMessage(player, "repair.invalid_value", new Pair<>("%VALUE%", s));
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

    private void registerLocalizedName() {
        registerButton(new OptionButton(Material.NAME_TAG, LOCALIZED_NAME));
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

    private void registerEliteCraftingTable() {
        registerButton(new OptionButton(Material.CRAFTING_TABLE, ELITE_CRAFTING_TABLE));
        registerButton(new ActionButton<>("elite_workbench.particles", Material.FIREWORK_ROCKET, (cache, guiHandler, player, inventory, i, event) -> {
            cache.setSubSetting(PARTICLE_EFFECTS);
            return true;
        }));
        registerButton(new MultipleChoiceButton<>("elite_workbench.grid_size", (cache, guiHandler, player, guiInventory, i) -> ((EliteWorkbenchData) cache.getItems().getItem().getCustomData(CustomCrafting.ELITE_CRAFTING_TABLE)).getGridSize() - 3,
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
        registerButton(new ToggleButton<>("elite_workbench.toggle", (cache, guiHandler, player, guiInventory, i) -> ((EliteWorkbenchData) cache.getItems().getItem().getCustomData(CustomCrafting.ELITE_CRAFTING_TABLE)).isEnabled(), new ButtonState<>("elite_workbench.toggle.enabled", Material.GREEN_CONCRETE, (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
            ((EliteWorkbenchData) items.getItem().getCustomData(CustomCrafting.ELITE_CRAFTING_TABLE)).setEnabled(false);
            return true;
        }), new ButtonState<>("elite_workbench.toggle.disabled", Material.RED_CONCRETE, (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
            ((EliteWorkbenchData) items.getItem().getCustomData(CustomCrafting.ELITE_CRAFTING_TABLE)).setEnabled(true);
            return true;
        })));
        registerButton(new ToggleButton<>("elite_workbench.advanced_recipes", (cache, guiHandler, player, guiInventory, i) -> ((EliteWorkbenchData) cache.getItems().getItem().getCustomData(CustomCrafting.ELITE_CRAFTING_TABLE)).isAdvancedRecipes(), new ButtonState<>("elite_workbench.advanced_recipes.enabled", Material.GREEN_CONCRETE, (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
            ((EliteWorkbenchData) items.getItem().getCustomData(CustomCrafting.ELITE_CRAFTING_TABLE)).setAdvancedRecipes(false);
            return true;
        }), new ButtonState<>("elite_workbench.advanced_recipes.disabled", Material.RED_CONCRETE, (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
            ((EliteWorkbenchData) items.getItem().getCustomData(CustomCrafting.ELITE_CRAFTING_TABLE)).setAdvancedRecipes(true);
            return true;
        })));
    }

    private void registerAttributes() {
        registerButton(new OptionButton(Material.ENCHANTED_GOLDEN_APPLE, ATTRIBUTE));
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
                sendMessage(player, "attribute.amount.error");
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

    private void registerParticleEffects() {
        registerButton(new OptionButton(Material.FIREWORK_ROCKET, PARTICLE_EFFECTS));
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

    private void registerVanilla() {
        registerButton(new OptionButton(Material.GRASS_BLOCK, VANILLA));
        registerButton(new ToggleButton<>("vanilla.block_recipes", (cache, guiHandler, player, guiInventory, i) -> cache.getItems().getItem().isBlockVanillaRecipes(), new ButtonState<>("vanilla.block_recipes.enabled", Material.GREEN_CONCRETE, (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
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

    private void registerDisplayName() {
        registerButton(new OptionButton(Material.NAME_TAG, DISPLAY_NAME));
        registerButton(new ChatInputButton<>(DISPLAY_NAME + ".set", Material.GREEN_CONCRETE, (guiHandler, player, s, strings) -> {
            guiHandler.getCustomCache().getItems().getItem().setDisplayName(ChatColor.convert(s));
            return false;
        }));
        registerButton(new ActionButton<>(DISPLAY_NAME + ".remove", Material.RED_CONCRETE, (cache, guiHandler, player, inventory, i, event) -> {
            guiHandler.getCustomCache().getItems().getItem().setDisplayName(null);
            return true;
        }));
    }

    private void registerLore() {
        registerButton(new OptionButton(Material.WRITABLE_BOOK, LORE));
        registerButton(new ChatInputButton<>(LORE + ".add", Material.WRITABLE_BOOK, (guiHandler, player, s, strings) -> {
            guiHandler.getCustomCache().getItems().getItem().addLoreLine(s.equals("&empty") ? "" : ChatColor.convert(s));
            return false;
        }));
        registerButton(new ActionButton<>(LORE + ".remove", Material.WRITTEN_BOOK, (cache, guiHandler, player, inventory, i, event) -> {
            ChatUtils.sendLoreManager(player);
            guiHandler.close();
            return true;
        }));
    }

    private void registerEnchants() {
        registerButton(new OptionButton(Material.ENCHANTED_BOOK, ENCHANTS));
        registerButton(new ChatInputButton<>(ENCHANTS + ".add", Material.ENCHANTED_BOOK, (guiHandler, player, s, args) -> {
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
                    sendMessage(player, "enchant.invalid_enchant", new Pair<>("%ENCHANT%", args[0]));
                    return true;
                }
            } else {
                sendMessage(player, "enchant.no_lvl");
                return true;
            }
            return false;
        }));
        registerButton(new ChatInputButton<>(ENCHANTS + ".remove", Material.RED_CONCRETE, (guiHandler, player, s, args) -> {
            Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(args[0].toLowerCase(Locale.ROOT).replace(' ', '_')));
            if (enchantment != null) {
                guiHandler.getCustomCache().getItems().getItem().removeEnchantment(enchantment);
            } else {
                sendMessage(player, "enchant.invalid_enchant", new Pair<>("%ENCHANT%", args[0]));
                return true;
            }
            return false;
        }));
    }

    private void registerItemFlags() {
        registerButton(new OptionButton(Material.WRITTEN_BOOK, FLAGS));
        registerButton(new ItemFlagsToggleButton("enchants", ItemFlag.HIDE_ENCHANTS, Material.ENCHANTING_TABLE));
        registerButton(new ItemFlagsToggleButton("attributes", ItemFlag.HIDE_ATTRIBUTES, Material.ENCHANTED_GOLDEN_APPLE));
        registerButton(new ItemFlagsToggleButton("unbreakable", ItemFlag.HIDE_UNBREAKABLE, Material.BEDROCK));
        registerButton(new ItemFlagsToggleButton("destroys", ItemFlag.HIDE_DESTROYS, Material.TNT));
        registerButton(new ItemFlagsToggleButton("placed_on", ItemFlag.HIDE_PLACED_ON, Material.GRASS_BLOCK));
        registerButton(new ItemFlagsToggleButton("potion_effects", ItemFlag.HIDE_POTION_EFFECTS, Material.POTION));
        if (ServerVersion.isAfterOrEq(MinecraftVersion.of(1, 16, 2))) {
            registerButton(new ItemFlagsToggleButton("dye", ItemFlag.HIDE_DYE, Material.YELLOW_DYE));
        }
    }
}