package me.wolfyscript.customcrafting.listeners;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.types.brewing.BrewingRecipe;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.utils.NamespacedKey;
import me.wolfyscript.utilities.api.utils.Reflection;
import me.wolfyscript.utilities.api.utils.inventory.ItemUtils;
import me.wolfyscript.utilities.api.utils.inventory.item_builder.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BrewingStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.BrewingStandFuelEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class BrewingStandListener implements Listener {

    private final CustomCrafting customCrafting;
    private final Map<Location, NamespacedKey> activeBrewingStands = new HashMap<>();

    public BrewingStandListener(CustomCrafting customCrafting) {
        this.customCrafting = customCrafting;
    }


    @EventHandler
    public void onTest(BrewingStandFuelEvent event) {

    }

    @EventHandler
    public void onInv(InventoryClickEvent event) {
        if (event.getClickedInventory() instanceof BrewerInventory) {
            BrewerInventory inventory = (BrewerInventory) event.getClickedInventory();
            Player player = (Player) event.getWhoClicked();
            Location location = inventory.getLocation();

            if (event.getSlot() != 4) {
                final ItemStack cursor = event.getCursor(); //And the item in the cursor
                final ItemStack currentItem = event.getCurrentItem(); //We want to get the item in the slot

                /* DEBUG STUFF
                System.out.println("Action: "+event.getAction());
                System.out.println("Cursor: "+cursor);
                System.out.println("CurrentItem: "+currentItem);
                 */

                //Place items
                if (event.getClickedInventory() == null) return;
                if (event.getClickedInventory().getType() != InventoryType.BREWING) return;

                if (event.isRightClick()) {
                    //Dropping one item or pick up half
                    if (event.getAction().equals(InventoryAction.PICKUP_HALF) || event.getAction().equals(InventoryAction.PICKUP_SOME)) {
                        Bukkit.getScheduler().runTask(customCrafting, () -> {
                            if (ItemUtils.isAirOrNull(inventory.getItem(3))) {
                                activeBrewingStands.remove(location);
                            }
                        });
                        return;
                    }
                    //Dropping one item
                    if (ItemUtils.isAirOrNull(currentItem)) {
                        event.setCancelled(true);
                        ItemStack itemStack = cursor.clone();
                        cursor.setAmount(cursor.getAmount() - 1);
                        Bukkit.getScheduler().runTaskLater(customCrafting, () -> {
                            itemStack.setAmount(1);
                            inventory.setItem(event.getSlot(), itemStack);
                            event.getWhoClicked().setItemOnCursor(cursor);
                        }, 1);
                    } else if (currentItem.isSimilar(cursor) || cursor.isSimilar(currentItem)) {
                        if (currentItem.getAmount() < currentItem.getMaxStackSize()) {
                            if (cursor.getAmount() > 0) {
                                event.setCancelled(true);
                                currentItem.setAmount(currentItem.getAmount() + 1);
                                cursor.setAmount(cursor.getAmount() - 1);
                                player.updateInventory();
                            }
                        }
                    }
                } else {
                    if (event.getAction().equals(InventoryAction.PICKUP_ALL) || ItemUtils.isAirOrNull(event.getCursor()) || event.getAction().equals(InventoryAction.COLLECT_TO_CURSOR)) {
                        //Make sure cursor contains item and the item isn't picked up
                        Bukkit.getScheduler().runTask(customCrafting, () -> {
                            if (ItemUtils.isAirOrNull(inventory.getItem(3))) {
                                activeBrewingStands.remove(location);
                            }
                        });
                        return;
                    }
                    //Placing an item
                    if (!ItemUtils.isAirOrNull(currentItem)) {
                        if (currentItem.isSimilar(cursor) || cursor.isSimilar(currentItem)) {
                            event.setCancelled(true);
                            int possibleAmount = currentItem.getMaxStackSize() - currentItem.getAmount();
                            currentItem.setAmount(currentItem.getAmount() + (cursor.getAmount() < possibleAmount ? cursor.getAmount() : possibleAmount));
                            cursor.setAmount(cursor.getAmount() - possibleAmount);
                        } else {
                            if (!ItemUtils.isAirOrNull(cursor)) {
                                event.setCancelled(true);
                                ItemStack itemStack = new ItemStack(cursor);
                                event.getView().setCursor(currentItem);
                                inventory.setItem(event.getSlot(), itemStack);
                            }
                        }
                    } else {
                        ItemStack itemStack = new ItemStack(cursor);
                        Bukkit.getScheduler().runTask(customCrafting, () -> {
                            inventory.setItem(event.getSlot(), itemStack);
                            event.getView().setCursor(new ItemStack(Material.AIR));
                        });
                    }
                    player.updateInventory();//And we update the inventory
                }
            }

            if (event.getSlot() == 3) {
                Bukkit.getScheduler().runTaskLater(customCrafting, () -> {
                    //Recipe Checker!
                    final ItemStack ingredient = inventory.getItem(3);

                    BrewingStand brewingStand = inventory.getHolder();

                    if (!isBrewingStandEmpty(inventory)) {
                        Method getTileEntity = Reflection.getMethod(Reflection.getOBC("block.CraftBrewingStand"), "getTileEntity");
                        Field brewTime = Reflection.getField(Reflection.getNMS("TileEntityBrewingStand"), "brewTime");
                        Field fuelLevelField = Reflection.getField(Reflection.getNMS("TileEntityBrewingStand"), "fuelLevel");
                        getTileEntity.setAccessible(true);
                        brewTime.setAccessible(true);
                        fuelLevelField.setAccessible(true);

                        try {
                            Object tileEntityObj = getTileEntity.invoke(brewingStand);
                            if (tileEntityObj != null) {
                                int fuelLevel = fuelLevelField.getInt(tileEntityObj);
                                //Check if recipe is correct
                                BrewingRecipe brewingRecipe = null;
                                CustomItem item = null;
                                for (BrewingRecipe recipe : customCrafting.getRecipeHandler().getAvailableBrewingRecipes(player)) {
                                    item = null;
                                    for (CustomItem customItem : recipe.getIngredients()) {
                                        if (customItem.isSimilar(ingredient, recipe.isExactMeta())) {
                                            if (fuelLevel >= recipe.getFuelCost()) {
                                                item = customItem;
                                            }
                                            break;
                                        }
                                    }
                                    if (item == null) continue;
                                    brewingRecipe = recipe;
                                }
                                if (brewingRecipe != null) {
                                    brewTime.setInt(tileEntityObj, brewingRecipe.getBrewTime());
                                    fuelLevelField.setInt(tileEntityObj, fuelLevel - brewingRecipe.getFuelCost());

                                    if (!activeBrewingStands.containsKey(location)) {
                                        if (brewingStand.getFuelLevel() > 0) {

                                            AtomicInteger tick = new AtomicInteger(400);
                                            int multiplier = -1 * (400 / brewingRecipe.getBrewTime());

                                            final CustomItem finalIngredient = item;
                                            new BukkitRunnable() {
                                                @Override
                                                public void run() {
                                                    if (activeBrewingStands.containsKey(location)) {
                                                        if (tick.get() > 0) {
                                                            try {
                                                                Object tileEntityObj = getTileEntity.invoke(brewingStand);
                                                                if (tileEntityObj != null) {
                                                                    brewTime.setInt(tileEntityObj, tick.getAndAdd(multiplier));
                                                                } else {
                                                                    activeBrewingStands.remove(location);
                                                                    cancel();
                                                                }
                                                            } catch (IllegalAccessException | InvocationTargetException e) {
                                                                e.printStackTrace();
                                                                activeBrewingStands.remove(location);
                                                                cancel();
                                                            }
                                                        } else {
                                                            BrewingRecipe recipe = (BrewingRecipe) customCrafting.getRecipeHandler().getRecipe(activeBrewingStands.get(location));

                                                            BrewerInventory brewerInventory = brewingStand.getInventory();
                                                            final ItemBuilder input0 = new ItemBuilder(brewerInventory.getItem(0));
                                                            final ItemBuilder input1 = new ItemBuilder(brewerInventory.getItem(1));
                                                            final ItemBuilder input2 = new ItemBuilder(brewerInventory.getItem(2));

                                                            finalIngredient.consumeItem(brewerInventory.getItem(3), 1, player.getInventory());

                                                            if (recipe.getDurationChange() != 0) {
                                                                int i = recipe.getDurationChange();
                                                                increasePotionDuration(input0, i);
                                                                increasePotionDuration(input1, i);
                                                                increasePotionDuration(input2, i);
                                                            }
                                                            if (recipe.getAmplifierChange() != 0) {
                                                                int i = recipe.getAmplifierChange();
                                                                increasePotionAmplifier(input0, i);
                                                                increasePotionAmplifier(input1, i);
                                                                increasePotionAmplifier(input2, i);
                                                            }

                                                            brewerInventory.setItem(0, input0.create());
                                                            brewerInventory.setItem(1, input1.create());
                                                            brewerInventory.setItem(2, input2.create());

                                                            activeBrewingStands.remove(location);
                                                            cancel();
                                                        }
                                                    } else {
                                                        cancel();
                                                    }
                                                }
                                            }.runTaskTimerAsynchronously(customCrafting, 2, 1);
                                        }
                                        activeBrewingStands.put(location, brewingRecipe.getNamespacedKey());
                                    }
                                }
                            }
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            return;
                        }
                    }
                }, 2);
            }
        }
    }

    private boolean isBrewingStandEmpty(BrewerInventory inventory) {
        return ItemUtils.isAirOrNull(inventory.getItem(0)) && ItemUtils.isAirOrNull(inventory.getItem(1)) && ItemUtils.isAirOrNull(inventory.getItem(2));
    }

    private void increasePotionDuration(ItemBuilder itemBuilder, int durationChange) {
        if (itemBuilder != null && itemBuilder.getItemMeta() instanceof PotionMeta) {
            PotionMeta potionMeta = (PotionMeta) itemBuilder.getItemMeta();
            List<PotionEffect> effects = new ArrayList<>(potionMeta.getCustomEffects());
            for (PotionEffect effect : effects) {
                potionMeta.removeCustomEffect(effect.getType());
                potionMeta.addCustomEffect(new PotionEffect(effect.getType(), effect.getDuration() + durationChange, effect.getAmplifier(), effect.isAmbient(), effect.hasParticles(), effect.hasIcon()), true);
            }
            itemBuilder.setItemMeta(potionMeta);
        }
    }

    public void increasePotionAmplifier(ItemBuilder itemBuilder, int amplifierChange) {
        if (itemBuilder != null && itemBuilder.getItemMeta() instanceof PotionMeta) {
            PotionMeta potionMeta = (PotionMeta) itemBuilder.getItemMeta();
            List<PotionEffect> effects = new ArrayList<>(potionMeta.getCustomEffects());
            for (PotionEffect effect : effects) {
                potionMeta.removeCustomEffect(effect.getType());
                potionMeta.addCustomEffect(new PotionEffect(effect.getType(), effect.getDuration(), effect.getAmplifier() + amplifierChange, effect.isAmbient(), effect.hasParticles(), effect.hasIcon()), true);
            }
            itemBuilder.setItemMeta(potionMeta);
        }
    }


}
