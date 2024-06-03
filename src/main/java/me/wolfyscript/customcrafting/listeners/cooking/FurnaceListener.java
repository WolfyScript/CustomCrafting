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

package me.wolfyscript.customcrafting.listeners.cooking;

import com.wolfyscript.utilities.bukkit.world.items.reference.StackReference;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.*;
import me.wolfyscript.customcrafting.recipes.conditions.Conditions;
import me.wolfyscript.customcrafting.recipes.data.*;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.registry.RegistryCustomItem;
import me.wolfyscript.utilities.util.inventory.InventoryUtils;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.Furnace;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.CookingRecipe;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Collection;
import java.util.Optional;

public class FurnaceListener implements Listener {

    public static final NamespacedKey RECIPES_USED_KEY = new NamespacedKey(NamespacedKeyUtils.NAMESPACE, "recipes_used");

    protected final CustomCrafting customCrafting;
    protected final WolfyUtilities api;
    protected final CookingManager manager;
    protected final RegistryCustomItem registryCustomItem;

    public FurnaceListener(CustomCrafting customCrafting, CookingManager manager) {
        this.manager = manager;
        this.customCrafting = customCrafting;
        this.api = customCrafting.getApi();
        this.registryCustomItem = api.getRegistries().getCustomItems();
    }

    @EventHandler
    public void placeItemIntoFurnace(InventoryClickEvent event) {
        if (!(event.getClickedInventory() instanceof FurnaceInventory)) return;
        var slotType = event.getSlotType();
        if (slotType.equals(InventoryType.SlotType.FUEL)) {
            if (event.getCursor() == null) return;
            Optional<CustomItem> fuelItem = registryCustomItem.values().stream().filter(customItem -> customItem.getFuelSettings().getBurnTime() > 0 && customItem.isSimilar(event.getCursor())).findFirst();
            if (fuelItem.isPresent()) {
                var location = event.getInventory().getLocation();
                if (fuelItem.get().getFuelSettings().getAllowedBlocks().contains(location != null ? location.getBlock().getType() : Material.FURNACE)) {
                    InventoryUtils.calculateClickedSlot(event);
                } else {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onBurn(FurnaceBurnEvent event) {
        ItemStack input = event.getFuel();
        registryCustomItem.getByItemStack(input).ifPresent(customItem -> {
            var fuelSettings = customItem.getFuelSettings();
            if (fuelSettings.getBurnTime() > 0 && customItem.isSimilar(input) && fuelSettings.getAllowedBlocks().contains(event.getBlock().getType())) {
                event.setCancelled(false);
                event.setBurning(true);
                event.setBurnTime(fuelSettings.getBurnTime());
            }
        });
    }

    @EventHandler
    public void onStartSmelt(FurnaceStartSmeltEvent event) {
        manager.clearCache(event.getBlock());

        final CookingRecipe<?> bukkitRecipe = event.getRecipe();
        final ItemStack source = event.getSource();
        final Block block = event.getBlock();
        // We want to notify the SmeltEvent if the original recipe was a vanilla recipe, and if so let it use that instead when no custom recipe matches.
        final boolean customBackingRecipe = ICustomVanillaRecipe.isPlaceholderOrDisplayRecipe(bukkitRecipe.getKey());

        // Are there any cached recipes, that were previously matched with that bukkit recipe present?
        Collection<me.wolfyscript.utilities.util.NamespacedKey> cachedRecipes = manager.getAssociatedCustomRecipes(bukkitRecipe.getKey());
        for (me.wolfyscript.utilities.util.NamespacedKey recipeKey : cachedRecipes) {
            CustomRecipeCooking<?, ?> customRecipe = (CustomRecipeCooking<?, ?>) customCrafting.getRegistries().getRecipes().get(recipeKey);
            if (customRecipe == null) {
                continue;
            }
            var dataOptional = processRecipe(customRecipe, source, block);
            if (dataOptional.isEmpty()) {
                continue;
            }
            manager.cacheRecipeData(event.getBlock(), new CookingRecipeCache(dataOptional.get(), customBackingRecipe));
            return; // Found valid cached custom recipe
        }

        // No cached recipe or no valid custom recipe
        customCrafting.getRegistries().getRecipes().get((RecipeType<? extends CustomRecipeCooking<?, ?>>) switch (event.getBlock().getType()) {
                    case BLAST_FURNACE -> RecipeType.BLAST_FURNACE;
                    case SMOKER -> RecipeType.SMOKER;
                    default -> RecipeType.FURNACE;
                }).stream()
                .sorted()
                .filter(recipe -> !recipe.isDisabled() && !cachedRecipes.contains(recipe.getNamespacedKey())) // Do not include disabled or already checked recipes
                .map(recipe1 -> processRecipe(recipe1, source, block))
                .filter(Optional::isPresent)
                .findFirst()
                .ifPresentOrElse(dataOptional -> {
                    // Let's remember this recipe and check it first next time the same bukkit recipe is present
                    manager.cacheCustomBukkitRecipeAssociation(bukkitRecipe.getKey(), dataOptional.get().getRecipe().getNamespacedKey());
                    // Finally cache recipe data and notify the SmeltEvent
                    manager.cacheRecipeData(block, new CookingRecipeCache(dataOptional.get(), customBackingRecipe));
                }, () -> {
                    // No custom recipe was found, but may still want to cancel the smelting when the original recipe is a custom recipe
                    manager.cacheRecipeData(block, new CookingRecipeCache(null, customBackingRecipe));
                });
    }

    private Optional<CookingRecipeData<?>> processRecipe(CustomRecipeCooking<?,?> cookingRecipe, ItemStack source, Block block) {
        if (cookingRecipe.validType(block.getType())) {
            Optional<StackReference> customSource = cookingRecipe.getSource().checkChoices(source, cookingRecipe.isCheckNBT());
            if (customSource.isPresent()) {
                if (cookingRecipe.checkConditions(Conditions.Data.of(block))) {
                    var data = new IngredientData(0, 0, cookingRecipe.getSource(), customSource.get(), source);
                    return Optional.ofNullable(switch (cookingRecipe.getRecipeType().getType()) {
                        case FURNACE -> new FurnaceRecipeData((CustomRecipeFurnace) cookingRecipe, data);
                        case SMOKER -> new SmokerRecipeData((CustomRecipeSmoking) cookingRecipe, data);
                        case BLAST_FURNACE -> new BlastingRecipeData((CustomRecipeBlasting) cookingRecipe, data);
                        default -> null;
                    });
                }
            }
        }
        return Optional.empty();
    }

    @EventHandler
    public void onSmelt(FurnaceSmeltEvent event) {
        //Check for and handle the custom recipe.
        Optional<CookingRecipeCache> cacheOptional = manager.getCustomRecipeCache(event);
        if (cacheOptional.isPresent()) {
            CookingRecipeCache cache = cacheOptional.get();
            CookingRecipeData<?> data = cache.data();
            if (data != null) {
                updateRecipeExperience(event.getBlock(), data.getRecipe().getNamespacedKey());
                applyResult(event);
                return;
            }
            if (cache.cancelSmelting()) {
                event.setCancelled(true); // FurnaceStartSmeltEvent told us to cancel if no custom recipe was found
            }
        }

        // Check if the CustomItem is allowed in Vanilla recipes
        CustomItem customItem = CustomItem.getByItemStack(event.getSource());
        if (customItem != null && customItem.isBlockVanillaRecipes()) {
            event.setCancelled(true); //Cancel the process if it is.
        }
        Bukkit.getScheduler().runTask(customCrafting, () -> manager.clearCache(event.getBlock()));
    }

    /**
     * Applies the result to the furnace and clears the cached data afterwards.<br>
     * <p>
     * If there is no actively cached data for the specified block it won't do anything!
     *
     * @param event The event to set result for.
     */
    public void applyResult(FurnaceSmeltEvent event) {
        var block = event.getBlock();
        manager.getCustomRecipeCache(block).ifPresent(cache -> {
            FurnaceInventory inventory = ((Furnace) event.getBlock().getState()).getInventory();
            ItemStack smelting = inventory.getSmelting();
            if (ItemUtils.isAirOrNull(smelting)) return;

            var data = cache.data();
            Bukkit.getScheduler().runTaskLater(customCrafting, () -> manager.clearCache(block), 1); //Clearing the cached data after 1 tick (event should be done).
            if (data == null) return;
            var result = data.getResult();
            var currentResultItem = inventory.getResult();

            ItemStack itemResult = result.item(data, null, block);
            //Need to set the result to air to bypass the vanilla result computation (See net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity#burn).
            event.setResult(new ItemStack(Material.AIR));
            if (currentResultItem != null) {
                if (!itemResult.isSimilar(currentResultItem)) {
                    event.setCancelled(true);
                    return;
                }
                int nextAmount = currentResultItem.getAmount() + itemResult.getAmount();
                if (nextAmount > currentResultItem.getMaxStackSize()) {
                    event.setCancelled(true);
                    return;
                }
                currentResultItem.setAmount(nextAmount);
            } else {
                inventory.setResult(itemResult);
            }

            data.bySlot(0).ifPresent(ingredientData -> {
                ItemStack shrunken = ingredientData.reference().shrink(smelting, 1, ingredientData.ingredient().isReplaceWithRemains(), null, null, block.getLocation());
                shrunken.setAmount(shrunken.getAmount());
                inventory.setSmelting(shrunken);
            });

            result.executeExtensions(block.getLocation(), true, null);
            result.removeCachedReference(block);
            block.getState().update(); // Update the state of the block. Just in case!
        });
    }

    /**
     * Listening to this event is required, because the exp events are not called. This is because the recipes are not saved in the vanilla NBT tags and therefor the furnace doesn't contain any xp to drop.
     */
    @EventHandler
    public void onResultCollect(InventoryClickEvent event) {
        if (!(event.getClickedInventory() instanceof FurnaceInventory inventory) || !event.getSlotType().equals(InventoryType.SlotType.RESULT)) {
            return;
        }
        var location = event.getClickedInventory().getLocation();
        if (location == null) return;
        if (!(location.getBlock().getState() instanceof Furnace)) return;
        manager.clearCache(location.getBlock());

        if (!ItemUtils.isAirOrNull(inventory.getResult())) { //Make sure to only give exp if the result is actually there.
            Bukkit.getScheduler().runTask(customCrafting, () -> {
                Furnace blockState = (Furnace) location.getBlock().getState();
                PersistentDataContainer rootContainer = blockState.getPersistentDataContainer();
                PersistentDataContainer usedRecipes = rootContainer.get(FurnaceListener.RECIPES_USED_KEY, PersistentDataType.TAG_CONTAINER);
                if (usedRecipes != null) {
                    // Award the experience of all the stored recipes.
                    usedRecipes.getKeys().forEach(bukkitRecipeKey -> awardRecipeExperience(usedRecipes, bukkitRecipeKey, location));
                    rootContainer.set(FurnaceListener.RECIPES_USED_KEY, PersistentDataType.TAG_CONTAINER, rootContainer.getAdapterContext().newPersistentDataContainer());
                    // Update the furnace state, so the NBT is updated.
                    blockState.update();
                }
            });
        }
    }

    private void awardRecipeExperience(PersistentDataContainer usedRecipes, NamespacedKey bukkitRecipeKey, Location location) {
        CustomRecipe<?> recipeFurnace = customCrafting.getRegistries().getRecipes().get(me.wolfyscript.utilities.util.NamespacedKey.fromBukkit(bukkitRecipeKey));
        if (recipeFurnace instanceof CustomRecipeCooking<?, ?> furnaceRecipe) {
            if (furnaceRecipe.getExp() > 0) {
                double totalXp = (double) usedRecipes.getOrDefault(bukkitRecipeKey, PersistentDataType.INTEGER, 0) * furnaceRecipe.getExp();
                location.getWorld().spawn(location, ExperienceOrb.class, experienceOrb -> {
                    experienceOrb.setExperience((int) Math.floor(totalXp));
                });
            }
        }
    }

    private void updateRecipeExperience(Block furnace, me.wolfyscript.utilities.util.NamespacedKey recipeKey) {
        Furnace blockState = (Furnace) furnace.getState();
        PersistentDataContainer rootContainer = blockState.getPersistentDataContainer();
        PersistentDataContainer usedRecipes = rootContainer.get(FurnaceListener.RECIPES_USED_KEY, PersistentDataType.TAG_CONTAINER);
        if (usedRecipes == null) {
            usedRecipes = rootContainer.getAdapterContext().newPersistentDataContainer();
        }

        NamespacedKey bukkitKey = recipeKey.bukkit();
        int amount = usedRecipes.getOrDefault(bukkitKey, PersistentDataType.INTEGER, 0);
        usedRecipes.set(recipeKey.bukkit(), PersistentDataType.INTEGER, amount + 1);

        rootContainer.set(FurnaceListener.RECIPES_USED_KEY, PersistentDataType.TAG_CONTAINER, usedRecipes);
    }


}
