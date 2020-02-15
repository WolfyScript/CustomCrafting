package me.wolfyscript.customcrafting.listeners;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.types.grindstone.GrindstoneData;
import me.wolfyscript.customcrafting.recipes.types.grindstone.GrindstoneRecipe;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.utils.ItemUtils;
import me.wolfyscript.utilities.api.utils.RandomCollection;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

public class GrindStoneListener implements Listener {

    private static HashMap<UUID, GrindstoneData> preCraftedRecipes = new HashMap<>();
    private static HashMap<UUID, HashMap<String, CustomItem>> precraftedItems = new HashMap<>();
    private WolfyUtilities api;

    public GrindStoneListener(WolfyUtilities api) {
        this.api = api;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;
        if (WolfyUtilities.hasVillagePillageUpdate()) {
            if (event.getClickedInventory().getType().equals(InventoryType.GRINDSTONE)) {
                Player player = (Player) event.getWhoClicked();
                InventoryAction action = event.getAction();
                Inventory inventory = event.getClickedInventory();
                if (event.getSlot() == 2 && !ItemUtils.isAirOrNull(inventory.getItem(2)) && (action.toString().startsWith("PICKUP_") || action.equals(InventoryAction.COLLECT_TO_CURSOR) || action.equals(InventoryAction.MOVE_TO_OTHER_INVENTORY))) {
                    //Take out item!
                    if (preCraftedRecipes.get(player.getUniqueId()) != null) {
                        //Custom Recipe
                        GrindstoneData grindstoneData = preCraftedRecipes.get(player.getUniqueId());
                        GrindstoneRecipe recipe = grindstoneData.getRecipe();
                        CustomItem inputTop = grindstoneData.getInputTop();
                        CustomItem inputBottom = grindstoneData.getInputBottom();

                        if (inputTop != null) {
                            inputTop.consumeItem(inventory.getItem(0), 1, inventory);
                        }
                        if (inputBottom != null) {
                            inputBottom.consumeItem(inventory.getItem(1), 1, inventory);
                        }

                    } else {
                        //Vanilla Recipe

                    }
                } else {
                    //Place in items and click empty result slot
                    if (event.getSlot() < 2) {
                        ItemStack cursor = event.getCursor();
                        ItemStack currentItem = event.getCurrentItem();
                        if (cursor != null) {
                            if (event.isLeftClick()) {
                                if (!ItemUtils.isAirOrNull(currentItem)) {
                                    if (currentItem.isSimilar(cursor) || cursor.isSimilar(currentItem)) {
                                        event.setCancelled(true);
                                        int possibleAmount = currentItem.getMaxStackSize() - currentItem.getAmount();
                                        currentItem.setAmount(currentItem.getAmount() + (cursor.getAmount() < possibleAmount ? cursor.getAmount() : possibleAmount));
                                        cursor.setAmount(cursor.getAmount() - possibleAmount);
                                        player.updateInventory();
                                    } else {
                                        if (!ItemUtils.isAirOrNull(cursor)) {
                                            event.setCancelled(true);
                                            ItemStack itemStack = new ItemStack(cursor);
                                            event.getView().setCursor(currentItem);
                                            inventory.setItem(event.getSlot(), itemStack);
                                            player.updateInventory();
                                        }
                                    }
                                } else if (!event.getAction().equals(InventoryAction.PICKUP_ALL)) {
                                    ItemStack itemStack = new ItemStack(cursor);
                                    Bukkit.getScheduler().runTask(CustomCrafting.getInst(), () -> {
                                        inventory.setItem(event.getSlot(), itemStack);
                                        event.getView().setCursor(new ItemStack(Material.AIR));
                                    });
                                }
                            } else if (event.isRightClick()) {
                                if (!ItemUtils.isAirOrNull(currentItem)) {
                                    if (currentItem.isSimilar(cursor) || cursor.isSimilar(currentItem)) {
                                        if (currentItem.getAmount() < currentItem.getMaxStackSize()) {
                                            if (cursor.getAmount() > 0) {
                                                event.setCancelled(true);
                                                currentItem.setAmount(currentItem.getAmount() + 1);
                                                cursor.setAmount(cursor.getAmount() - 1);
                                                player.updateInventory();
                                            }
                                        }
                                    } else if (!event.getAction().toString().startsWith("PICKUP")) {
                                        Bukkit.getScheduler().runTask(CustomCrafting.getInst(), () -> {
                                            inventory.setItem(event.getSlot(), cursor);
                                            event.getView().setCursor(currentItem);
                                        });
                                    }
                                } else {
                                    event.setCancelled(true);
                                    cursor.setAmount(cursor.getAmount() - 1);
                                    ItemStack itemStack = new ItemStack(cursor);
                                    Bukkit.getScheduler().runTaskLater(CustomCrafting.getInst(), () -> {
                                        itemStack.setAmount(1);
                                        inventory.setItem(event.getSlot(), itemStack);
                                        event.getWhoClicked().setItemOnCursor(cursor);
                                    }, 1);
                                }
                            }
                        } else {
                            event.setCancelled(true);
                        }
                    }
                }

                //Updates the result
                Bukkit.getScheduler().runTask(CustomCrafting.getInst(), () -> {
                    ItemStack inputTop = inventory.getItem(0);
                    ItemStack inputBottom = inventory.getItem(1);
                    preCraftedRecipes.put(player.getUniqueId(), null);
                    for (GrindstoneRecipe grindstoneRecipe : CustomCrafting.getRecipeHandler().getGrindstoneRecipes()) {
                        CustomItem finalInputTop = null;
                        CustomItem finalInputBottom = null;
                        if (grindstoneRecipe.getInputTop() != null && !grindstoneRecipe.getInputTop().isEmpty()) {
                            if (ItemUtils.isAirOrNull(inputTop)) {
                                continue;
                            }
                            for (CustomItem customItem : grindstoneRecipe.getInputTop()) {
                                if (customItem.isSimilar(inputTop, grindstoneRecipe.isExactMeta())) {
                                    finalInputTop = customItem.clone();
                                    break;
                                }
                            }
                            if (finalInputTop == null) {
                                continue;
                            }
                        } else if (!ItemUtils.isAirOrNull(inputTop)) {
                            continue;
                        }
                        if (grindstoneRecipe.getInputBottom() != null && !grindstoneRecipe.getInputBottom().isEmpty()) {
                            if (ItemUtils.isAirOrNull(inputBottom)) {
                                continue;
                            }
                            for (CustomItem customItem : grindstoneRecipe.getInputBottom()) {
                                if (customItem.isSimilar(inputBottom, grindstoneRecipe.isExactMeta())) {
                                    finalInputBottom = customItem.clone();
                                    break;
                                }
                            }
                            if (finalInputBottom == null) {
                                continue;
                            }
                        } else if (!ItemUtils.isAirOrNull(inputBottom)) {
                            continue;
                        }
                        if (!grindstoneRecipe.getConditions().checkConditions(grindstoneRecipe, new Conditions.Data(player, player.getTargetBlock(null, 5), event.getView()))) {
                            continue;
                        }
                        RandomCollection<CustomItem> items = new RandomCollection<>();
                        for (CustomItem customItem : grindstoneRecipe.getCustomResults()) {
                            if (!customItem.hasPermission() || player.hasPermission(customItem.getPermission())) {
                                items.add(customItem.getRarityPercentage(), customItem.clone());
                            }
                        }
                        HashMap<String, CustomItem> precraftedItem = precraftedItems.getOrDefault(player, new HashMap<>());
                        CustomItem result = new CustomItem(Material.AIR);
                        if (precraftedItem.get(grindstoneRecipe.getId()) == null) {
                            if (!items.isEmpty()) {
                                result = items.next();
                                precraftedItem.put(grindstoneRecipe.getId(), result);
                                precraftedItems.put(player.getUniqueId(), precraftedItem);
                            }
                        } else {
                            result = precraftedItem.get(grindstoneRecipe.getId());
                        }
                        preCraftedRecipes.put(player.getUniqueId(), new GrindstoneData(grindstoneRecipe, finalInputTop, finalInputBottom));
                        inventory.setItem(2, result.getRealItem());
                        break;
                    }
                });
            }
        }
    }
}
