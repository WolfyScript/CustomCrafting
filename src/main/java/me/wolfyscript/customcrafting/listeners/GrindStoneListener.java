package me.wolfyscript.customcrafting.listeners;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.types.RecipeType;
import me.wolfyscript.customcrafting.recipes.types.grindstone.GrindstoneData;
import me.wolfyscript.customcrafting.recipes.types.grindstone.GrindstoneRecipe;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.utils.NamespacedKey;
import me.wolfyscript.utilities.api.utils.Pair;
import me.wolfyscript.utilities.api.utils.RandomCollection;
import me.wolfyscript.utilities.api.utils.inventory.InventoryUtils;
import me.wolfyscript.utilities.api.utils.inventory.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class GrindStoneListener implements Listener {

    private static final HashMap<UUID, GrindstoneData> preCraftedRecipes = new HashMap<>();
    private static final HashMap<UUID, HashMap<NamespacedKey, CustomItem>> precraftedItems = new HashMap<>();
    private final CustomCrafting customCrafting;

    public GrindStoneListener(CustomCrafting customCrafting) {
        this.customCrafting = customCrafting;
    }

    @EventHandler
    public void onTakeOutResult(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;
        if (event.getAction().equals(InventoryAction.NOTHING)) return;
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

            Bukkit.getScheduler().runTask(customCrafting, () -> {
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

                if(grindstoneData.getRecipe().getXp() > 0){
                    ExperienceOrb orb = (ExperienceOrb) player.getLocation().getWorld().spawnEntity(player.getLocation(), EntityType.EXPERIENCE_ORB);
                    orb.setExperience(grindstoneData.getRecipe().getXp());
                }

                preCraftedRecipes.remove(player.getUniqueId());
                Pair<CustomItem, GrindstoneData> checkResult = checkRecipe(inventory.getItem(0), inventory.getItem(1), 0, player, event.getView());
                GrindstoneRecipe foundRecipe = checkResult.getValue().getRecipe();
                if (foundRecipe == null) {
                    return; //Returns and uses Vanilla recipe instead
                }
                preCraftedRecipes.put(player.getUniqueId(), checkResult.getValue());
                inventory.setItem(2, checkResult.getKey().create());
            });
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;
        if (event.getAction().equals(InventoryAction.NOTHING)) return;
        if (!event.getClickedInventory().getType().equals(InventoryType.GRINDSTONE)) return;
        Player player = (Player) event.getWhoClicked();
        InventoryAction action = event.getAction();
        Inventory inventory = event.getClickedInventory();
        if (event.getSlot() != 2) {
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

            Pair<CustomItem, GrindstoneData> checkResult = checkRecipe(calculatedCurrentItem, inventory.getItem(event.getSlot() == 0 ? 1 : 0), event.getSlot(), player, event.getView());

            boolean validItem = checkResult.getValue().isValidItem();
            GrindstoneRecipe foundRecipe = checkResult.getValue().getRecipe();

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
            preCraftedRecipes.put(player.getUniqueId(), checkResult.getValue());
            inventory.setItem(2, checkResult.getKey().create());
            player.updateInventory();
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (!event.getInventory().getType().equals(InventoryType.GRINDSTONE)) return;
        if (event.getInventorySlots().isEmpty()) return;
        event.setCancelled(true);

        //TODO: DRAG ITEMS INTO GRINDSTONE!

    }

    public Pair<CustomItem, GrindstoneData> checkRecipe(ItemStack item, ItemStack itemOther, int slot, Player player, InventoryView inventoryView) {
        AtomicReference<CustomItem> finalInputTop = new AtomicReference<>();
        AtomicReference<CustomItem> finalInputBottom = new AtomicReference<>();

        List<GrindstoneRecipe> allowedRecipes = customCrafting.getRecipeHandler().getAvailableRecipes(RecipeType.GRINDSTONE, player).stream().filter(grindstoneRecipe -> grindstoneRecipe.getConditions().checkConditions(grindstoneRecipe, new Conditions.Data(player, player.getTargetBlock(null, 5), inventoryView))).collect(Collectors.toList());

        preCraftedRecipes.remove(player.getUniqueId());

        GrindstoneRecipe foundRecipe = null;
        boolean validItem = false;

        for (GrindstoneRecipe grindstoneRecipe : allowedRecipes) {
            List<CustomItem> input = grindstoneRecipe.getInputBottom();
            List<CustomItem> otherInput = grindstoneRecipe.getInputTop();
            if (slot == 0) {
                input = grindstoneRecipe.getInputTop();
                otherInput = grindstoneRecipe.getInputBottom();
            }
            Optional<CustomItem> optional = input.stream().filter(customItem -> customItem.isSimilar(item, grindstoneRecipe.isExactMeta())).findFirst();
            if (!optional.isPresent()) {
                //Item is invalid! Go to next recipe!
                continue;
            }
            if (!ItemUtils.isAirOrNull(itemOther)) {
                //Another item exists in the other slot! Check if current and other item are a valid recipe
                Optional<CustomItem> optionalOther = otherInput.stream().filter(customItem -> customItem.isSimilar(itemOther, grindstoneRecipe.isExactMeta())).findFirst();
                if (!optionalOther.isPresent()) {
                    //Other exiting Item is invalid!
                    continue;
                }
                if (slot == 0) {
                    finalInputBottom.set(optionalOther.get());
                } else {
                    finalInputTop.set(optionalOther.get());
                }
            } else if (!InventoryUtils.isCustomItemsListEmpty(otherInput)) { //Other slot is empty! check if current item is in a recipe
                //Recipe has other input! This recipe is not yet valid!
                validItem = true;
                break;
            }
            validItem = true;
            if (slot == 0) {
                finalInputTop.set(optional.get());
            } else {
                finalInputBottom.set(optional.get());
            }
            foundRecipe = grindstoneRecipe;
            break;
        }

        GrindstoneData grindstoneData = new GrindstoneData(foundRecipe, validItem, finalInputTop.get(), finalInputBottom.get());
        CustomItem result = new CustomItem(Material.AIR);
        if (foundRecipe != null) {
            RandomCollection<CustomItem> items = new RandomCollection<>();
            foundRecipe.getResults().stream().filter(cI -> !cI.hasPermission() || player.hasPermission(cI.getPermission())).forEach(cI -> items.add(cI.getRarityPercentage(), cI.clone()));
            HashMap<NamespacedKey, CustomItem> precraftedItem = precraftedItems.getOrDefault(player.getUniqueId(), new HashMap<>());
            if (precraftedItem.get(foundRecipe.getNamespacedKey()) == null) {
                if (!items.isEmpty()) {
                    result = items.next();
                    precraftedItem.put(foundRecipe.getNamespacedKey(), result);
                    precraftedItems.put(player.getUniqueId(), precraftedItem);
                }
            } else {
                result = precraftedItem.get(foundRecipe.getNamespacedKey());
            }
        }
        return new Pair<>(result, grindstoneData);
    }

    private boolean isTool(Material material) {
        String name = material.name();
        return name.endsWith("AXE") || name.endsWith("HOE") || name.endsWith("SWORD") || name.endsWith("SHOVEL") || name.endsWith("PICKAXE");
    }

    private boolean allowedInGrindstone(Material material) {
        if (isTool(material)) return true;
        String name = material.name();
        if (name.endsWith("BOOTS") || name.endsWith("HELMET") || name.endsWith("LEGGINGS") || name.endsWith("CHESTPLATE") || name.endsWith("_ON_A_STICK")) {
            return true;
        }
        switch (material) {
            case BOW:
            case CROSSBOW:
            case TRIDENT:
            case SHIELD:
            case ELYTRA:
            case FISHING_ROD:
            case SHEARS:
            case FLINT_AND_STEEL:
                return true;
            default:
                return false;
        }
    }

}
