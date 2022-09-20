/*
 *       ____ _  _ ____ ___ ____ _  _ ____ ____ ____ ____ ___ _ _  _ ____
 *       |    |  | [__   |  |  | |\/| |    |__/ |__| |___  |  | |\ | | __
 *       |___ |__| ___]  |  |__| |  | |___ |  \ |  | |     |  | | \| |__]
 *
 *       CustomCrafting Recipe creation and management tool for Minecraft
 *                      Copyright (C) 2021  WolfyScript
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.wolfyscript.customcrafting.listeners;

import com.wolfyscript.lib.nbt.nbtapi.NBTTileEntity;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.CustomRecipeBrewing;
import me.wolfyscript.customcrafting.recipes.RecipeType;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.nms.NMSUtil;
import me.wolfyscript.utilities.util.Pair;
import me.wolfyscript.utilities.util.inventory.InventoryUtils;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
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
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class BrewingStandListener implements Listener {

    private final CustomCrafting customCrafting;
    private final WolfyUtilities wolfyUtilities;
    private final Map<Location, Pair<BukkitTask, Map<CustomRecipeBrewing, CustomItem>>> activeBrewingStands = new HashMap<>();

    public BrewingStandListener(WolfyUtilities wolfyUtilities, CustomCrafting customCrafting) {
        this.wolfyUtilities = wolfyUtilities;
        this.customCrafting = customCrafting;
    }

    @EventHandler
    public void onTest(BrewingStandFuelEvent event) {
        //For later use of custom Brewing Stand fuel
    }

    @EventHandler
    public void onInv(InventoryClickEvent event) {
        if (event.getClickedInventory() instanceof BrewerInventory inventory && customCrafting.getConfigHandler().getConfig().isBrewingRecipes()) {
            var player = (Player) event.getWhoClicked();
            var location = inventory.getLocation();
            if (event.getSlot() != 4) {
                //Place items
                if (event.getClickedInventory() == null) return;
                if (event.getClickedInventory().getType() != InventoryType.BREWING) return;
                if (event.getSlot() == 3) {
                    //Make it possible to place in everything into the ingredient slot
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
                    }
                    InventoryUtils.calculateClickedSlot(event);
                }
            }

            Bukkit.getScheduler().runTaskLater(customCrafting, () -> {
                final ItemStack ingredient = inventory.getItem(3);
                if (ItemUtils.isAirOrNull(ingredient)) {
                    return;
                }
                //Recipe Checker!
                var brewingStand = inventory.getHolder();
                if (brewingStand != null) {
                    NBTTileEntity brewingStandEntity = new NBTTileEntity(brewingStand);
                    int fuelLevel = brewingStandEntity.getInteger("Fuel");
                    //Check if recipe is correct
                    Map<CustomRecipeBrewing, CustomItem> brewingRecipeList = new HashMap<>();
                    //Check if at least one slot contains an item
                    if (!ItemUtils.isAirOrNull(inventory.getItem(0)) || !ItemUtils.isAirOrNull(inventory.getItem(1)) || !ItemUtils.isAirOrNull(inventory.getItem(2))) {
                        //Check for possible recipes and add them to the map
                        customCrafting.getRegistries().getRecipes().getAvailable(RecipeType.BREWING_STAND, player).stream().filter(recipe -> fuelLevel >= recipe.getFuelCost()).forEach(recipe -> {
                            Optional<CustomItem> optional = recipe.getIngredient().check(ingredient, recipe.isCheckNBT());
                            if (optional.isPresent()) {
                                //Ingredient is valid
                                //Checking for valid item in the bottom 3 slots of the brewing inventory
                                boolean valid = true;
                                if (!recipe.getAllowedItems().isEmpty()) {
                                    for (int i = 0; i < 3; i++) {
                                        ItemStack itemStack = inventory.getItem(i);
                                        if (!ItemUtils.isAirOrNull(itemStack) && !recipe.getAllowedItems().test(itemStack, recipe.isCheckNBT())) {
                                            valid = false;
                                            break;
                                        }
                                    }
                                }
                                if (valid) {
                                    //Brewing Inventory contains a valid item for that recipe
                                    brewingRecipeList.put(recipe, optional.get());
                                }
                            }
                        });
                    }
                    //Check if the current state of recipes is empty
                    if (brewingRecipeList.isEmpty()) {
                        //list of recipes empty
                        if (activeBrewingStands.containsKey(location)) {
                            //Cancel current running tasks and removing the brewing operation from the location
                            activeBrewingStands.get(location).getKey().cancel();
                            activeBrewingStands.remove(location);
                        }
                    } else if (!activeBrewingStands.containsKey(location)) {
                        //Using the first recipe to set the brew time, fuel Level cost and ingredient.
                        //Because there can be multiple recipes for one ingredient
                        Map.Entry<CustomRecipeBrewing, CustomItem> firstEntry = brewingRecipeList.entrySet().stream().findFirst().get();
                        brewingStandEntity.setInteger("BrewTime", 400);
                        brewingStandEntity.setInteger("Fuel", fuelLevel - 1);
                        final CustomItem finalIngredient = firstEntry.getValue();
                        //Set the tick multiplier that is used for the progress bar
                        int multiplier = -1;
                        if (brewingStand.getFuelLevel() > 0) {
                            var tick = new AtomicInteger(400);
                            BukkitRunnable runnable = new BukkitRunnable() {
                                @Override
                                public void run() {
                                    if (!activeBrewingStands.containsKey(location)) {
                                        cancel();
                                        return;
                                    }
                                    if (tick.get() > 0) {
                                        if (!location.getBlock().getType().equals(Material.BREWING_STAND)) {
                                            activeBrewingStands.remove(location);
                                            cancel();
                                            return;
                                        }
                                        new NBTTileEntity(brewingStand).setInteger("BrewTime", tick.addAndGet(multiplier));
                                        return;
                                    }
                                    List<Integer> processedSlots = new LinkedList<>();
                                    for (CustomRecipeBrewing recipe : activeBrewingStands.get(location).getValue().keySet()) {
                                        if (processedSlots.size() >= 3) break;
                                        var brewerInventory = brewingStand.getInventory();
                                        finalIngredient.remove(brewerInventory.getItem(3), 1, player.getInventory());
                                        for (int i = 0; i < 3; i++) {
                                            if (processedSlots.contains(i)) {
                                                continue; //Make sure the slot isn't processed twice by multiple recipes
                                            }
                                            ItemStack inputItem = brewerInventory.getItem(i);
                                            if (!ItemUtils.isAirOrNull(inputItem)) { //is slot not empty?
                                                //Check if item is contained in recipe before trying to process it
                                                if (recipe.getAllowedItems().isEmpty() || recipe.getAllowedItems().test(inputItem, recipe.isCheckNBT())) {
                                                    //Input in that slot is valid, so marking slot as processed
                                                    processedSlots.add(i);
                                                    //Process the item in the slot
                                                    var potionMeta = (PotionMeta) inputItem.getItemMeta();
                                                    if (potionMeta != null) {
                                                        if (!recipe.getResult().isEmpty()) {
                                                            //Result available. Replace the items with a random result from the list. (Percentages of items are used)
                                                            Optional<CustomItem> item = recipe.getResult().getItem(player);
                                                            if (item.isPresent()) {
                                                                brewerInventory.setItem(i, item.get().create());
                                                            }
                                                        } else {
                                                            //No result available
                                                            if (recipe.isResetEffects()) {
                                                                potionMeta.clearCustomEffects();
                                                            } else {
                                                                //remove the effects that are configured
                                                                recipe.getEffectRemovals().forEach(potionMeta::removeCustomEffect);
                                                                //Go through all the effects that are left
                                                                for (PotionEffect effect : potionMeta.getCustomEffects()) {
                                                                    //Add the global effect changes
                                                                    int duration = effect.getDuration() + recipe.getDurationChange();
                                                                    int amplifier = effect.getAmplifier() + recipe.getAmplifierChange();
                                                                    if (recipe.getEffectUpgrades().containsKey(effect.getType())) {
                                                                        //Add the effect specific upgrades
                                                                        Pair<Integer, Integer> values = recipe.getEffectUpgrades().get(effect.getType());
                                                                        amplifier = amplifier + values.getKey();
                                                                        duration = duration + values.getValue();
                                                                    }
                                                                    potionMeta.addCustomEffect(new PotionEffect(effect.getType(), duration, amplifier, effect.isAmbient(), effect.hasParticles(), effect.hasIcon()), true);
                                                                }
                                                                recipe.getEffectAdditions().forEach(potionMeta::addCustomEffect);
                                                            }
                                                            if (recipe.getEffectColor() != null) {
                                                                potionMeta.setColor(recipe.getEffectColor());
                                                            }
                                                            inputItem.setItemMeta(potionMeta);
                                                            brewerInventory.setItem(i, inputItem);
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    activeBrewingStands.remove(location);
                                    cancel();
                                }
                            };
                            BukkitTask task = runnable.runTaskTimer(customCrafting, 2, 1);
                            activeBrewingStands.put(location, new Pair<>(task, brewingRecipeList));
                        }
                    } else {
                        //Put new brewing recipes to map, but keep current active task
                        activeBrewingStands.put(location, new Pair<>(activeBrewingStands.get(location).getKey(), brewingRecipeList));
                    }
                }
            }, 2);

        }
    }


}
