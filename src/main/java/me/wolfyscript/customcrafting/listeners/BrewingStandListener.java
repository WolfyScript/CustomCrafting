package me.wolfyscript.customcrafting.listeners;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.Types;
import me.wolfyscript.customcrafting.recipes.types.brewing.BrewingRecipe;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.nms.NMSUtil;
import me.wolfyscript.utilities.api.nms.block.NMSBrewingStand;
import me.wolfyscript.utilities.util.Pair;
import me.wolfyscript.utilities.util.RandomCollection;
import me.wolfyscript.utilities.util.inventory.InventoryUtils;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
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
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class BrewingStandListener implements Listener {

    private final CustomCrafting customCrafting;
    private final WolfyUtilities wolfyUtilities;
    private final NMSUtil nmsUtil;
    private final Map<Location, Pair<BukkitTask, Map<BrewingRecipe, CustomItem>>> activeBrewingStands = new HashMap<>();

    public BrewingStandListener(WolfyUtilities wolfyUtilities, CustomCrafting customCrafting) {
        this.wolfyUtilities = wolfyUtilities;
        this.nmsUtil = wolfyUtilities.getNmsUtil();
        this.customCrafting = customCrafting;
    }

    @EventHandler
    public void onTest(BrewingStandFuelEvent event) {
        //For later use of custom Brewing Stand fuel
    }

    @EventHandler
    public void onInv(InventoryClickEvent event) {
        if (event.getClickedInventory() instanceof BrewerInventory) {
            BrewerInventory inventory = (BrewerInventory) event.getClickedInventory();
            Player player = (Player) event.getWhoClicked();
            Location location = inventory.getLocation();

            if (event.getSlot() != 4) {
                final ItemStack cursor = event.getCursor(); //The item in the cursor
                final ItemStack currentItem = event.getCurrentItem(); //The item in the slot
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
                        } else if (currentItem.isSimilar(cursor)) {
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
                        //Placing items
                        if (!ItemUtils.isAirOrNull(currentItem)) {
                            if (currentItem.isSimilar(cursor)) {
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
                        } else if (cursor != null) {
                            ItemStack itemStack = new ItemStack(cursor);
                            event.setCancelled(true);
                            inventory.setItem(event.getSlot(), itemStack);
                            event.getView().setCursor(new ItemStack(Material.AIR));
                        }
                        player.updateInventory();//And we update the inventory
                    }
                }
            }

            Bukkit.getScheduler().runTaskLater(customCrafting, () -> {
                final ItemStack ingredient = inventory.getItem(3);
                if (ItemUtils.isAirOrNull(ingredient)) {
                    return;
                }
                //Recipe Checker!
                BrewingStand brewingStand = inventory.getHolder();
                if (brewingStand != null) {

                    NMSBrewingStand nmsBrewingStand = nmsUtil.getBlockUtil().getNmsBrewingStand(brewingStand);

                    if (nmsBrewingStand != null) {
                        int fuelLevel = nmsBrewingStand.getFuelLevel();
                        //Check if recipe is correct
                        Map<BrewingRecipe, CustomItem> brewingRecipeList = new HashMap<>();
                        //Check if at least one slot contains an item
                        if (!ItemUtils.isAirOrNull(inventory.getItem(0)) || !ItemUtils.isAirOrNull(inventory.getItem(1)) || !ItemUtils.isAirOrNull(inventory.getItem(2))) {
                            //Check for possible recipes and add them to the map
                            customCrafting.getRecipeHandler().getAvailableRecipes(Types.BREWING_STAND, player).stream().filter(recipe -> fuelLevel >= recipe.getFuelCost()).forEach(recipe -> {
                                for (CustomItem customItem : recipe.getIngredients()) {
                                    if (customItem.isSimilar(ingredient, recipe.isExactMeta())) {
                                        //Ingredient is valid
                                        //Checking for valid item in the bottom 3 slots of the brewing inventory
                                        if (recipe.getAllowedItems().isEmpty()
                                                ||
                                                recipe.getAllowedItems().stream().anyMatch(item1 -> {
                                                    for (int i = 0; i < 3; i++) {
                                                        ItemStack itemStack = inventory.getItem(i);
                                                        if (!ItemUtils.isAirOrNull(itemStack) && item1.isSimilar(itemStack, recipe.isExactMeta())) {
                                                            return true;
                                                        }
                                                    }
                                                    return false;
                                                })) {
                                            //Brewing Inventory contains a valid item for that recipe
                                            brewingRecipeList.put(recipe, customItem);
                                        }
                                        break;
                                    }
                                }
                            });
                        }
                        //System.out.println("Current valid recipes: " + brewingRecipeList);
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
                            Map.Entry<BrewingRecipe, CustomItem> firstEntry = brewingRecipeList.entrySet().stream().findFirst().get();
                            nmsBrewingStand.setBrewingTime(400);
                            nmsBrewingStand.setFuelLevel(fuelLevel - 1);
                            final CustomItem finalIngredient = firstEntry.getValue();
                            //Set the tick multiplier that is used for the progress bar
                            int multiplier = -1; //* (400 / firstEntry.getKey().getBrewTime());

                            if (brewingStand.getFuelLevel() > 0) {
                                AtomicInteger tick = new AtomicInteger(400);
                                BukkitRunnable runnable = new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        if (activeBrewingStands.containsKey(location)) {
                                            if (tick.get() > 0) {
                                                Bukkit.getScheduler().runTask(customCrafting, () -> {
                                                    NMSBrewingStand nmsBrewingStand = nmsUtil.getBlockUtil().getNmsBrewingStand(brewingStand);
                                                    if (nmsBrewingStand != null) {
                                                        nmsBrewingStand.setBrewingTime(tick.addAndGet(multiplier));
                                                    } else {
                                                        activeBrewingStands.remove(location);
                                                        cancel();
                                                    }
                                                });
                                                return;
                                            }
                                            Bukkit.getScheduler().runTask(customCrafting, () -> {
                                                List<Integer> processedSlots = new ArrayList<>();
                                                for (BrewingRecipe recipe : activeBrewingStands.get(location).getValue().keySet()) {
                                                    if (processedSlots.size() >= 3) break;
                                                    BrewerInventory brewerInventory = brewingStand.getInventory();
                                                    finalIngredient.consumeItem(brewerInventory.getItem(3), 1, player.getInventory());
                                                    for (int i = 0; i < 3; i++) {
                                                        if (processedSlots.contains(i)) {
                                                            continue;
                                                        }//Make sure the slot isn't processed twice by multiple recipes
                                                        ItemStack inputItem = brewerInventory.getItem(i);
                                                        if (!ItemUtils.isAirOrNull(inputItem)) {//is slot not empty?
                                                            //Check if item is contained in recipe before trying to process it
                                                            if (recipe.getAllowedItems().isEmpty() || recipe.getAllowedItems().parallelStream().anyMatch(item1 -> item1.isSimilar(inputItem, recipe.isExactMeta()))) {
                                                                //Input in that slot is valid, so marking slot as processed
                                                                processedSlots.add(i);
                                                                //Process the item in the slot
                                                                PotionMeta potionMeta = (PotionMeta) inputItem.getItemMeta();
                                                                if (potionMeta != null) {
                                                                    if (!InventoryUtils.isCustomItemsListEmpty(recipe.getResults())) {
                                                                        //Result available. Replace the items with a random result from the list. (Percentages of items are used)
                                                                        if (recipe.getResults().size() > 1) {
                                                                            RandomCollection<CustomItem> items = recipe.getResults().parallelStream().collect(RandomCollection.getCollector((rdmC, customItem) -> rdmC.add(customItem.getRarityPercentage(), customItem)));
                                                                            if (!items.isEmpty()) {
                                                                                brewerInventory.setItem(i, items.next().create());
                                                                            }
                                                                        } else if (recipe.getResult() != null) {
                                                                            brewerInventory.setItem(i, recipe.getResult().create());
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
                                            });
                                        }
                                        cancel();
                                    }
                                };
                                BukkitTask task = runnable.runTaskTimerAsynchronously(customCrafting, 2, 1);
                                activeBrewingStands.put(location, new Pair<>(task, brewingRecipeList));
                            }
                        } else {
                            //Put new brewing recipes to map, but keep current active task
                            activeBrewingStands.put(location, new Pair<>(activeBrewingStands.get(location).getKey(), brewingRecipeList));
                        }
                    }
                }
            }, 2);

        }
    }


}
