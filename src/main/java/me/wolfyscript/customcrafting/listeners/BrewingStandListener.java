package me.wolfyscript.customcrafting.listeners;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.types.RecipeType;
import me.wolfyscript.customcrafting.recipes.types.brewing.BrewingRecipe;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.utils.NamespacedKey;
import me.wolfyscript.utilities.api.utils.RandomCollection;
import me.wolfyscript.utilities.api.utils.Reflection;
import me.wolfyscript.utilities.api.utils.inventory.ItemUtils;
import me.wolfyscript.utilities.api.utils.inventory.item_builder.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Color;
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

    private final Method getTileEntity;
    private final Field brewTime;
    private final Field fuelLevelField;

    {
        Class<?> craftBrewingStand = Reflection.getOBC("block.CraftBlockEntityState");
        Class<?> tileEntityBrewingStand = Reflection.getNMS("TileEntityBrewingStand");

        getTileEntity = Reflection.getDeclaredMethod(craftBrewingStand, "getTileEntity");
        brewTime = Reflection.getField(tileEntityBrewingStand, "brewTime");
        fuelLevelField = Reflection.getField(tileEntityBrewingStand, "fuelLevel");
    }

    public BrewingStandListener(CustomCrafting customCrafting) {
        this.customCrafting = customCrafting;
        this.getTileEntity.setAccessible(true);
        this.brewTime.setAccessible(true);
        this.fuelLevelField.setAccessible(true);
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
                            currentItem.setAmount(currentItem.getAmount() + (Math.min(cursor.getAmount(), possibleAmount)));
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

                    if (brewingStand != null && !isBrewingStandEmpty(inventory)) {
                        try {
                            Object tileEntityObj = getTileEntity.invoke(brewingStand);
                            if (tileEntityObj != null) {
                                int fuelLevel = fuelLevelField.getInt(tileEntityObj);
                                //Check if recipe is correct
                                BrewingRecipe brewingRecipe = null;
                                CustomItem item = null;
                                for (BrewingRecipe recipe : customCrafting.getRecipeHandler().getAvailableRecipes(RecipeType.BREWING_STAND, player)) {
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

                                            Bukkit.getScheduler().runTaskTimerAsynchronously(customCrafting, task -> {
                                                if (activeBrewingStands.containsKey(location)) {
                                                    if (tick.get() > 0) {
                                                        try {
                                                            Object tileEntity = getTileEntity.invoke(brewingStand);
                                                            if (tileEntity != null) {
                                                                brewTime.setInt(tileEntityObj, tick.getAndAdd(multiplier));
                                                            } else {
                                                                activeBrewingStands.remove(location);
                                                                task.cancel();
                                                            }
                                                        } catch (IllegalAccessException | InvocationTargetException e) {
                                                            e.printStackTrace();
                                                            activeBrewingStands.remove(location);
                                                            task.cancel();
                                                        }
                                                    } else {
                                                        BrewingRecipe recipe = (BrewingRecipe) customCrafting.getRecipeHandler().getRecipe(activeBrewingStands.get(location));
                                                        BrewerInventory brewerInventory = brewingStand.getInventory();
                                                        finalIngredient.consumeItem(brewerInventory.getItem(3), 1, player.getInventory());

                                                        for (int i = 0; i < 3; i++) {
                                                            ItemStack inputItem = brewerInventory.getItem(i);
                                                            ItemBuilder input = new ItemBuilder(inputItem);
                                                            if (!recipe.getGlobalOptions().getResult().isEmpty()) {
                                                                //Result available. Replace the items with a random result from the list. (Percentages of items are used)
                                                                if (recipe.getGlobalOptions().getResult().size() > 1) {
                                                                    RandomCollection<CustomItem> items = new RandomCollection<>();
                                                                    recipe.getGlobalOptions().getResult().forEach(customItem -> items.add(customItem.getRarityPercentage(), customItem));
                                                                    if (!items.isEmpty()) {
                                                                        if (!ItemUtils.isAirOrNull(inputItem))
                                                                            brewerInventory.setItem(i, items.next().create());
                                                                    }
                                                                } else if (recipe.getResult() != null) {
                                                                    if (!ItemUtils.isAirOrNull(inputItem))
                                                                        brewerInventory.setItem(i, recipe.getResult().create());
                                                                }
                                                            } else {
                                                                //No result available
                                                                if (recipe.getGlobalOptions().isResetEffects()) {
                                                                    resetPotionEffects(input);
                                                                } else {
                                                                    increasePotionDuration(input, recipe.getGlobalOptions().getDurationChange());
                                                                    increasePotionAmplifier(input, recipe.getGlobalOptions().getAmplifierChange());
                                                                }
                                                                if (recipe.getGlobalOptions().getEffectColor() != null) {
                                                                    setPotionRGB(input, recipe.getGlobalOptions().getEffectColor());
                                                                }
                                                                brewerInventory.setItem(i, input.create());
                                                            }

                                                        }
                                                        activeBrewingStands.remove(location);
                                                        task.cancel();
                                                    }
                                                } else {
                                                    task.cancel();
                                                }
                                            }, 2, 1);
                                        }
                                        activeBrewingStands.put(location, brewingRecipe.getNamespacedKey());
                                    }
                                }
                            }
                        } catch (IllegalAccessException | InvocationTargetException ignored) {
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

    public void setPotionRGB(ItemBuilder itemBuilder, Color color) {
        if (itemBuilder != null && itemBuilder.getItemMeta() instanceof PotionMeta) {
            PotionMeta potionMeta = (PotionMeta) itemBuilder.getItemMeta();
            potionMeta.setColor(color);
            itemBuilder.setItemMeta(potionMeta);
        }
    }

    public void resetPotionEffects(ItemBuilder itemBuilder) {
        if (itemBuilder != null && itemBuilder.getItemMeta() instanceof PotionMeta) {
            PotionMeta potionMeta = (PotionMeta) itemBuilder.getItemMeta();
            potionMeta.clearCustomEffects();
            itemBuilder.setItemMeta(potionMeta);
        }
    }


}
