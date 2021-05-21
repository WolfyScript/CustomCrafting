package me.wolfyscript.customcrafting.listeners;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.Registry;
import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.Types;
import me.wolfyscript.customcrafting.recipes.types.grindstone.GrindstoneData;
import me.wolfyscript.customcrafting.recipes.types.grindstone.GrindstoneRecipe;
import me.wolfyscript.customcrafting.utils.recipe_item.Ingredient;
import me.wolfyscript.customcrafting.utils.recipe_item.Result;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.util.Pair;
import me.wolfyscript.utilities.util.inventory.InventoryUtils;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
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
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class GrindStoneListener implements Listener {

    private static final HashMap<UUID, GrindstoneData> preCraftedRecipes = new HashMap<>();
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
            event.setCancelled(true);
            GrindstoneData grindstoneData = preCraftedRecipes.get(player.getUniqueId());

            //Result taken out and placed on cursor or into the inventory.
            ItemStack result = event.getCurrentItem();
            ItemStack cursor = event.getCursor();

            if (event.isShiftClick()) {
                if (InventoryUtils.hasInventorySpace(player, result)) {
                    player.getInventory().addItem(result);
                } else {
                    return;
                }
            } else if (ItemUtils.isAirOrNull(cursor) || (result.isSimilar(cursor) && cursor.getAmount() + result.getAmount() <= cursor.getMaxStackSize())) {
                if (ItemUtils.isAirOrNull(cursor)) {
                    event.setCursor(result);
                } else {
                    cursor.setAmount(cursor.getAmount() + result.getAmount());
                }
            } else {
                return;
            }

            if (grindstoneData.getRecipe().getXp() > 0) { //Spawn xp
                ExperienceOrb orb = (ExperienceOrb) player.getLocation().getWorld().spawnEntity(player.getLocation(), EntityType.EXPERIENCE_ORB);
                orb.setExperience(grindstoneData.getRecipe().getXp());
            }
            if (grindstoneData.getResult().isPresent()) { //Check if there is a Result available & execute extensions
                grindstoneData.getResult().get().executeExtensions(inventory.getLocation() != null ? inventory.getLocation() : player.getLocation(), inventory.getLocation() != null, player);
            }

            //Custom Recipe
            final ItemStack itemTop = inventory.getItem(0) == null ? null : inventory.getItem(0).clone();
            final ItemStack itemBottom = inventory.getItem(1) == null ? null : inventory.getItem(1).clone();

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
            player.updateInventory();

            //Remove crafted recipe.
            preCraftedRecipes.remove(player.getUniqueId());

            //Check for new recipe!
            Bukkit.getScheduler().runTask(customCrafting, () -> {
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

            ItemStack calculatedCursor = cursor;
            ItemStack calculatedCurrentItem = currentItem;

            //Place item when the item is valid
            event.setCancelled(true);
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
                } else if (currentItem.isSimilar(cursor) && currentItem.getAmount() < currentItem.getMaxStackSize() && cursor.getAmount() > 0) {
                    calculatedCurrentItem = currentItem.clone();
                    calculatedCurrentItem.setAmount(currentItem.getAmount() + 1);
                    calculatedCursor = cursor.clone();
                    calculatedCursor.setAmount(cursor.getAmount() - 1);
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
    }

    public Pair<CustomItem, GrindstoneData> checkRecipe(ItemStack item, ItemStack itemOther, int slot, Player player, InventoryView inventoryView) {
        CustomItem finalInputTop = null;
        CustomItem finalInputBottom = null;

        preCraftedRecipes.remove(player.getUniqueId());

        GrindstoneRecipe foundRecipe = null;
        boolean validItem = false;

        for (GrindstoneRecipe grindstoneRecipe : Registry.RECIPES.getAvailable(Types.GRINDSTONE, player).stream().filter(grindstoneRecipe -> grindstoneRecipe.checkConditions(new Conditions.Data(player, player.getTargetBlock(null, 5), inventoryView))).collect(Collectors.toList())) {
            Ingredient input = slot == 0 ? grindstoneRecipe.getInputTop() : grindstoneRecipe.getInputBottom();
            Ingredient otherInput = slot == 0 ? grindstoneRecipe.getInputBottom() : grindstoneRecipe.getInputTop();
            Optional<CustomItem> optional = input.check(item, grindstoneRecipe.isExactMeta());
            if (!optional.isPresent()) {
                //Item is invalid! Go to next recipe!
                continue;
            }
            if (!ItemUtils.isAirOrNull(itemOther)) {
                //Another item exists in the other slot! Check if current and other item are a valid recipe
                Optional<CustomItem> optionalOther = otherInput.check(itemOther, grindstoneRecipe.isExactMeta());
                if (!optionalOther.isPresent()) {
                    //Other existing Item is invalid!
                    continue;
                }
                if (slot == 0) {
                    finalInputBottom = optionalOther.get();
                } else {
                    finalInputTop = optionalOther.get();
                }
            } else if (!otherInput.isEmpty()) { //Other slot is empty! check if current item is in a recipe
                //Recipe has other input! This recipe is not yet valid!
                validItem = true;
                break;
            }
            validItem = true;
            if (slot == 0) {
                finalInputTop = optional.get();
            } else {
                finalInputBottom = optional.get();
            }
            foundRecipe = grindstoneRecipe;
            break;
        }
        Result<?> result = null;
        CustomItem resultItem = new CustomItem(Material.AIR);
        if (foundRecipe != null) {
            result = foundRecipe.getResult().get(inventoryView.getTopInventory().getStorageContents());
            resultItem = result.getItem(player).orElse(new CustomItem(Material.AIR));
        }
        return new Pair<>(resultItem, new GrindstoneData(foundRecipe, result, validItem, finalInputTop, finalInputBottom));
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
            case ENCHANTED_BOOK:
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
