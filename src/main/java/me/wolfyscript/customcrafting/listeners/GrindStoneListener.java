package me.wolfyscript.customcrafting.listeners;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.types.grindstone.GrindstoneData;
import me.wolfyscript.customcrafting.recipes.types.grindstone.GrindstoneRecipe;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.utils.RandomCollection;
import me.wolfyscript.utilities.api.utils.inventory.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class GrindStoneListener implements Listener {

    private static final HashMap<UUID, GrindstoneData> preCraftedRecipes = new HashMap<>();
    private static final HashMap<UUID, HashMap<String, CustomItem>> precraftedItems = new HashMap<>();
    private final CustomCrafting customCrafting;

    public GrindStoneListener(CustomCrafting customCrafting) {
        this.customCrafting = customCrafting;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;

        if (!event.getClickedInventory().getType().equals(InventoryType.GRINDSTONE)) return;
        Player player = (Player) event.getWhoClicked();
        InventoryAction action = event.getAction();
        Inventory inventory = event.getClickedInventory();
        if (event.getSlot() == 2 && !ItemUtils.isAirOrNull(inventory.getItem(2)) && (action.toString().startsWith("PICKUP_") || action.equals(InventoryAction.COLLECT_TO_CURSOR) || action.equals(InventoryAction.MOVE_TO_OTHER_INVENTORY))) {
            //Take out item!
            if (preCraftedRecipes.get(player.getUniqueId()) == null) {
                //Vanilla Recipe
                return;
            }
            //Custom Recipe
            final ItemStack itemTop = inventory.getItem(0) == null ? null : inventory.getItem(0).clone();
            final ItemStack itemBottom = inventory.getItem(1) == null ? null : inventory.getItem(1).clone();
            Bukkit.getScheduler().runTaskLater(customCrafting, () -> {
                GrindstoneData grindstoneData = preCraftedRecipes.get(player.getUniqueId());
                CustomItem inputTop = grindstoneData.getInputTop();
                CustomItem inputBottom = grindstoneData.getInputBottom();
                if (inputTop != null) {
                    inputTop.consumeItem(itemTop, 1, inventory);
                    inventory.setItem(0, itemTop);
                }
                if (inputBottom != null) {
                    inputBottom.consumeItem(itemBottom, 1, inventory);
                    inventory.setItem(1, itemBottom);
                }
                preCraftedRecipes.remove(player.getUniqueId());
            }, 1);
        } else if (event.getSlot() != 2) {
            //Place in items and click empty result slot
            final ItemStack cursor = event.getCursor(); //And the item in the cursor
            final ItemStack currentItem = event.getCurrentItem(); //We want to get the item in the slot

            if (event.getAction().toString().startsWith("PICKUP_") || action.equals(InventoryAction.COLLECT_TO_CURSOR) || action.equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)) {
                return;
            }
            event.setCancelled(true);

            ItemStack calculatedCursor = cursor;
            ItemStack calculatedCurrentItem = currentItem;

            //Place item when the item is valid
            if (event.getClickedInventory() == null) return;
            if (event.getClickedInventory().getType() != InventoryType.GRINDSTONE) return;
            if (event.isRightClick()) {
                //Dropping one item or pick up half
                if (event.getAction().equals(InventoryAction.PICKUP_HALF) || event.getAction().equals(InventoryAction.PICKUP_SOME)) {
                    return;
                }
                //Dropping one item
                if (ItemUtils.isAirOrNull(currentItem)) {
                    calculatedCurrentItem = cursor.clone();
                    calculatedCurrentItem.setAmount(1);
                    calculatedCursor = cursor.clone();
                    calculatedCursor.setAmount(cursor.getAmount() - 1);
                } else if (currentItem.isSimilar(cursor)) {
                    if (currentItem.getAmount() < currentItem.getMaxStackSize()) {
                        if (cursor.getAmount() > 0) {
                            calculatedCurrentItem = currentItem.clone();
                            calculatedCurrentItem.setAmount(currentItem.getAmount() + 1);
                            calculatedCursor = cursor.clone();
                            calculatedCursor.setAmount(cursor.getAmount() - 1);
                        }
                    }
                }
            } else {
                //Placing an item
                if (ItemUtils.isAirOrNull(cursor)) {
                    return; //Make sure cursor contains item
                }
                if (!ItemUtils.isAirOrNull(currentItem)) {
                    if (currentItem.isSimilar(cursor) || cursor.isSimilar(currentItem)) {
                        int possibleAmount = currentItem.getMaxStackSize() - currentItem.getAmount();
                        calculatedCurrentItem = currentItem.clone();
                        calculatedCurrentItem.setAmount(currentItem.getAmount() + (Math.min(cursor.getAmount(), possibleAmount)));
                        calculatedCursor = cursor.clone();
                        calculatedCursor.setAmount(cursor.getAmount() - possibleAmount);
                    } else {
                        if (!ItemUtils.isAirOrNull(cursor)) {
                            calculatedCursor = currentItem.clone();
                            calculatedCurrentItem = cursor.clone();
                        }
                    }
                } else {
                    calculatedCursor = null;
                    calculatedCurrentItem = cursor.clone();
                }
            }

            AtomicReference<CustomItem> finalInputTop = new AtomicReference<>();
            AtomicReference<CustomItem> finalInputBottom = new AtomicReference<>();

            List<GrindstoneRecipe> allowedRecipes = customCrafting.getRecipeHandler().getAvailableGrindstoneRecipes(player).stream().filter(grindstoneRecipe -> grindstoneRecipe.getConditions().checkConditions(grindstoneRecipe, new Conditions.Data(player, player.getTargetBlock(null, 5), event.getView()))).collect(Collectors.toList());

            ItemStack item = calculatedCurrentItem;
            ItemStack itemOther = event.getSlot() == 0 ? inventory.getItem(1) : inventory.getItem(0);

            preCraftedRecipes.remove(player.getUniqueId());

            GrindstoneRecipe foundRecipe = null;
            boolean validItem = false;

            for (GrindstoneRecipe grindstoneRecipe : allowedRecipes) {
                List<CustomItem> input = grindstoneRecipe.getInputBottom();
                List<CustomItem> otherInput = grindstoneRecipe.getInputTop();
                if (event.getSlot() == 0) {
                    input = grindstoneRecipe.getInputTop();
                    otherInput = grindstoneRecipe.getInputBottom();
                }
                Optional<CustomItem> optional = input.stream().filter(customItem -> customItem.isSimilar(item, grindstoneRecipe.isExactMeta())).findFirst();
                if (!optional.isPresent()) {
                    continue;
                }
                if (!ItemUtils.isAirOrNull(itemOther)) {
                    //Another item exists in the other slot! Check if current and other item are a valid recipe
                    Optional<CustomItem> optionalOther = otherInput.stream().filter(customItem -> customItem.isSimilar(itemOther, grindstoneRecipe.isExactMeta())).findFirst();
                    if (!optionalOther.isPresent()) {
                        continue;
                    }
                    if (event.getSlot() == 0) {
                        finalInputTop.set(optional.get());
                        finalInputBottom.set(optionalOther.get());
                    } else {
                        finalInputBottom.set(optional.get());
                        finalInputTop.set(optionalOther.get());
                    }
                } else {
                    //Other slot is empty! check if current item is in a recipe
                    validItem = true;
                    break;
                }
                validItem = true;
                foundRecipe = grindstoneRecipe;
                break;
            }

            if (validItem) {
                event.setCurrentItem(calculatedCurrentItem);
                event.getWhoClicked().setItemOnCursor(calculatedCursor);
            }

            if (foundRecipe == null) {
                if (ItemUtils.isAirOrNull(cursor) || allowedInGrindstone(cursor.getType())) {
                    event.setCancelled(false);
                }
                return; //Returns and uses Vanilla recipe instead
            }

            RandomCollection<CustomItem> items = new RandomCollection<>();
            foundRecipe.getCustomResults().stream().filter(cI -> !cI.hasPermission() || player.hasPermission(cI.getPermission())).forEach(cI -> items.add(cI.getRarityPercentage(), cI.clone()));
            HashMap<String, CustomItem> precraftedItem = precraftedItems.getOrDefault(player.getUniqueId(), new HashMap<>());
            CustomItem result = new CustomItem(Material.AIR);
            if (precraftedItem.get(foundRecipe.getNamespacedKey().toString()) == null) {
                if (!items.isEmpty()) {
                    result = items.next();
                    precraftedItem.put(foundRecipe.getNamespacedKey().toString(), result);
                    precraftedItems.put(player.getUniqueId(), precraftedItem);
                }
            } else {
                result = precraftedItem.get(foundRecipe.getNamespacedKey().toString());
            }
            preCraftedRecipes.put(player.getUniqueId(), new GrindstoneData(foundRecipe, finalInputTop.get(), finalInputBottom.get()));
            inventory.setItem(2, result.getItemStack());
            player.updateInventory();
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (event.getInventory() == null) return;

        if (!event.getInventory().getType().equals(InventoryType.GRINDSTONE)) return;
        if (event.getInventorySlots().isEmpty()) return;
        event.setCancelled(true);

        //TODO: DRAG ITEMS INTO GRINDSTONE!

            /* Player player = (Player) event.getWhoClicked();
            Inventory inventory = event.getInventory();

            //Place in items and click empty result slot

            final ItemStack cursor = event.getCursor(); //And the item in the cursor
            final ItemStack currentItem = event.getCurrentItem(); //We want to get the item in the slot
            event.setCancelled(true);
            boolean validItem = false;
            if (event.getSlot() == 0) {
                for (GrindstoneRecipe grindstoneRecipe : CustomCrafting.getRecipeHandler().getGrindstoneRecipes()) {
                    CustomItem finalInputTop = null;
                    if (grindstoneRecipe.getInputTop() != null && !grindstoneRecipe.getInputTop().isEmpty()) {
                        if (ItemUtils.isAirOrNull(cursor)) {
                            continue;
                        }
                        for (CustomItem customItem : grindstoneRecipe.getInputTop()) {
                            if (customItem.isSimilar(cursor, grindstoneRecipe.isExactMeta())) {
                                finalInputTop = customItem.clone();
                                break;
                            }
                        }
                        if (finalInputTop == null) {
                            continue;
                        }
                    } else if (!ItemUtils.isAirOrNull(cursor)) {
                        continue;
                    }
                    validItem = true;
                    break;
                }
            } else {
                for (GrindstoneRecipe grindstoneRecipe : CustomCrafting.getRecipeHandler().getGrindstoneRecipes()) {
                    CustomItem finalInputBottom = null;
                    if (grindstoneRecipe.getInputBottom() != null && !grindstoneRecipe.getInputBottom().isEmpty()) {
                        if (ItemUtils.isAirOrNull(cursor)) {
                            continue;
                        }
                        for (CustomItem customItem : grindstoneRecipe.getInputBottom()) {
                            if (customItem.isSimilar(cursor, grindstoneRecipe.isExactMeta())) {
                                finalInputBottom = customItem.clone();
                                break;
                            }
                        }
                        if (finalInputBottom == null) {
                            continue;
                        }
                    } else if (!ItemUtils.isAirOrNull(cursor)) {
                        continue;
                    }
                    if (!grindstoneRecipe.getConditions().checkConditions(grindstoneRecipe, new Conditions.Data(player, player.getTargetBlock(null, 5), event.getView()))) {
                        continue;
                    }
                    validItem = true;
                    break;
                }
            }
            System.out.println("Activated After item: " + validItem);
            if (!validItem) {
                if (ItemUtils.isAirOrNull(cursor) || allowedInGrindstone(cursor.getType())) {
                    event.setCancelled(false);
                }
                return;
            }
            //Check the opposite item depending on which slot was clicked
            preCraftedRecipes.put(player.getUniqueId(), null);
            GrindstoneRecipe foundRecipe = null;
            CustomItem finalInputTop = null;
            CustomItem finalInputBottom = null;
            for (GrindstoneRecipe grindstoneRecipe : CustomCrafting.getRecipeHandler().getGrindstoneRecipes()) {
                if (event.getSlot() == 0) {
                    ItemStack input = inventory.getItem(1);
                    if (grindstoneRecipe.getInputBottom() != null && !grindstoneRecipe.getInputBottom().isEmpty()) {
                        if (ItemUtils.isAirOrNull(input)) {
                            continue;
                        }
                        for (CustomItem customItem : grindstoneRecipe.getInputBottom()) {
                            if (customItem.isSimilar(input, grindstoneRecipe.isExactMeta())) {
                                finalInputBottom = customItem.clone();
                                break;
                            }
                        }
                        if (finalInputBottom == null) {
                            continue;
                        }
                    } else if (!ItemUtils.isAirOrNull(input)) {
                        continue;
                    }
                } else {
                    ItemStack input = inventory.getItem(0);
                    if (grindstoneRecipe.getInputTop() != null && !grindstoneRecipe.getInputTop().isEmpty()) {
                        if (ItemUtils.isAirOrNull(input)) {
                            continue;
                        }
                        for (CustomItem customItem : grindstoneRecipe.getInputTop()) {
                            if (customItem.isSimilar(input, grindstoneRecipe.isExactMeta())) {
                                finalInputTop = customItem.clone();
                                break;
                            }
                        }
                        if (finalInputTop == null) {
                            continue;
                        }
                    } else if (!ItemUtils.isAirOrNull(input)) {
                        continue;
                    }
                }
                if (!grindstoneRecipe.getConditions().checkConditions(grindstoneRecipe, new Conditions.Data(player, player.getTargetBlock(null, 5), event.getView()))) {
                    continue;
                }
                foundRecipe = grindstoneRecipe;
                break;
            }
            System.out.println("Activated After recipe: " + foundRecipe);
            if (foundRecipe == null) {
                if (ItemUtils.isAirOrNull(cursor) || allowedInGrindstone(cursor.getType())) {
                    event.setCancelled(false);
                }
                return; //Returns and uses Vanilla recipe instead
            }
            RandomCollection<CustomItem> items = new RandomCollection<>();
            for (CustomItem customItem : foundRecipe.getCustomResults()) {
                if (!customItem.hasPermission() || player.hasPermission(customItem.getPermission())) {
                    items.add(customItem.getRarityPercentage(), customItem.clone());
                }
            }
            HashMap<String, CustomItem> precraftedItem = precraftedItems.getOrDefault(player, new HashMap<>());
            CustomItem result = new CustomItem(Material.AIR);
            if (precraftedItem.get(foundRecipe.getId()) == null) {
                if (!items.isEmpty()) {
                    result = items.next();
                    precraftedItem.put(foundRecipe.getId(), result);
                    precraftedItems.put(player.getUniqueId(), precraftedItem);
                }
            } else {
                result = precraftedItem.get(foundRecipe.getId());
            }
            preCraftedRecipes.put(player.getUniqueId(), new GrindstoneData(foundRecipe, finalInputTop, finalInputBottom));
            inventory.setItem(2, result.getRealItem());
             */

    }

    private boolean isTool(Material material) {
        switch (material) {
            case WOODEN_AXE:
            case WOODEN_HOE:
            case WOODEN_SWORD:
            case WOODEN_SHOVEL:
            case WOODEN_PICKAXE:
            case GOLDEN_AXE:
            case GOLDEN_HOE:
            case GOLDEN_SWORD:
            case GOLDEN_SHOVEL:
            case GOLDEN_PICKAXE:
            case STONE_AXE:
            case STONE_HOE:
            case STONE_SWORD:
            case STONE_SHOVEL:
            case STONE_PICKAXE:
            case IRON_AXE:
            case IRON_HOE:
            case IRON_SWORD:
            case IRON_SHOVEL:
            case IRON_PICKAXE:
            case DIAMOND_AXE:
            case DIAMOND_HOE:
            case DIAMOND_SWORD:
            case DIAMOND_SHOVEL:
            case DIAMOND_PICKAXE:
                return true;
            default:
                return false;
        }
    }

    private boolean allowedInGrindstone(Material material) {
        if (isTool(material)) return true;
        switch (material) {
            case LEATHER_BOOTS:
            case LEATHER_HELMET:
            case LEATHER_LEGGINGS:
            case LEATHER_CHESTPLATE:
            case IRON_BOOTS:
            case IRON_HELMET:
            case IRON_LEGGINGS:
            case IRON_CHESTPLATE:
            case GOLDEN_BOOTS:
            case GOLDEN_HELMET:
            case GOLDEN_LEGGINGS:
            case GOLDEN_CHESTPLATE:
            case CHAINMAIL_BOOTS:
            case CHAINMAIL_HELMET:
            case CHAINMAIL_LEGGINGS:
            case CHAINMAIL_CHESTPLATE:
            case DIAMOND_BOOTS:
            case DIAMOND_HELMET:
            case DIAMOND_LEGGINGS:
            case DIAMOND_CHESTPLATE:
            case BOW:
            case CROSSBOW:
            case TRIDENT:
            case SHIELD:
            case TURTLE_HELMET:
            case ELYTRA:
            case CARROT_ON_A_STICK:
            case FISHING_ROD:
            case SHEARS:
            case FLINT_AND_STEEL:
                return true;
            default:
                return false;
        }
    }

}
