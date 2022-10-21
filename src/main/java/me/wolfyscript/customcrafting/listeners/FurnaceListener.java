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

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.CustomRecipe;
import me.wolfyscript.customcrafting.recipes.CustomRecipeCooking;
import me.wolfyscript.customcrafting.recipes.data.CookingRecipeData;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.customcrafting.utils.cooking.CookingManager;
import me.wolfyscript.customcrafting.utils.cooking.FurnaceListener1_17Adapter;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.registry.RegistryCustomItem;
import me.wolfyscript.utilities.util.Pair;
import me.wolfyscript.utilities.util.inventory.InventoryUtils;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import me.wolfyscript.utilities.util.version.MinecraftVersions;
import me.wolfyscript.utilities.util.version.ServerVersion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Furnace;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

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
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_17)) {
            Bukkit.getPluginManager().registerEvents(new FurnaceListener1_17Adapter(customCrafting, manager), customCrafting);
        } else {
            customCrafting.getLogger().warning("Looks like you are using 1.16. This will impact the Cooking Recipe Performance! Please update to 1.17 and/or use Paper!");
        }
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
        for (CustomItem customItem : registryCustomItem.values()) {
            var fuelSettings = customItem.getFuelSettings();
            if (fuelSettings.getBurnTime() > 0 && customItem.isSimilar(input) && fuelSettings.getAllowedBlocks().contains(event.getBlock().getType())) {
                event.setCancelled(false);
                event.setBurning(true);
                event.setBurnTime(fuelSettings.getBurnTime());
                break;
            }
        }
    }

    @EventHandler
    public void onSmelt(FurnaceSmeltEvent event) {
        //Check for and handle the custom recipe.
        Pair<CookingRecipeData<?>, Boolean> data = manager.getCustomRecipeCache(event);
        if (data.getValue()) {
            if (data.getKey() == null) {
                event.setCancelled(true);
                return;
            }
            manager.getAdapter().applyResult(event);
            return;
        }
        //Similar to the check in the FurnaceStartSmeltEvent.
        //This is needed in 1.16 as the FurnaceStartSmeltEvent doesn't exist.
        //Check if the CustomItem is allowed in Vanilla recipes
        CustomItem customItem = CustomItem.getByItemStack(event.getSource());
        if (customItem != null && customItem.isBlockVanillaRecipes()) {
            event.setCancelled(true); //Cancel the process if it is.
        }
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

        if (!ItemUtils.isAirOrNull(inventory.getResult())) { //Make sure to only give exp if the result is actually there.
            // Keep this for backwards compatibility and handle existing custom recipe exp.
            Bukkit.getScheduler().runTask(customCrafting, () -> {
                Furnace blockState = (Furnace) location.getBlock().getState();
                PersistentDataContainer rootContainer = blockState.getPersistentDataContainer();
                PersistentDataContainer usedRecipes = rootContainer.get(FurnaceListener.RECIPES_USED_KEY, PersistentDataType.TAG_CONTAINER);
                if (usedRecipes != null) {
                    //Award the experience of all the stored recipes.
                    usedRecipes.getKeys().forEach(bukkitRecipeKey -> awardRecipeExperience(usedRecipes, bukkitRecipeKey, location));
                    rootContainer.set(FurnaceListener.RECIPES_USED_KEY, PersistentDataType.TAG_CONTAINER, rootContainer.getAdapterContext().newPersistentDataContainer());
                    //Update the furnace state, so the NBT is updated.
                    blockState.update();
                }
            });
        }
    }

    private void awardRecipeExperience(PersistentDataContainer usedRecipes, NamespacedKey bukkitRecipeKey, Location location) {
        CustomRecipe<?> recipeFurnace = customCrafting.getRegistries().getRecipes().get(me.wolfyscript.utilities.util.NamespacedKey.fromBukkit(bukkitRecipeKey));
        if (recipeFurnace instanceof CustomRecipeCooking<?, ?> furnaceRecipe) {
            if (furnaceRecipe.getExp() > 0) {
                //Calculates the amount of xp levels the Exp Orbs will get. (See net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity#createExperience)
                double totalXp = (double) usedRecipes.getOrDefault(bukkitRecipeKey, PersistentDataType.INTEGER, 0) * furnaceRecipe.getExp();
                int levels = (int) Math.floor(totalXp);
                if (levels < Integer.MAX_VALUE) {
                    double xpLeft = totalXp - levels;
                    if (xpLeft != 0.0F && Math.random() < xpLeft) {
                        ++levels;
                    }
                }
                awardExp(location, levels);
            }
        }
    }

    public static void awardExp(Location loc, int expLevels) {
        while (expLevels > 0) {
            int totalXp = getExperienceValue(expLevels);
            expLevels -= totalXp;
            loc.getWorld().spawn(loc, ExperienceOrb.class, orb -> orb.setExperience(totalXp));
        }
    }

    public static int getExperienceValue(int expLevels) {
        if (expLevels > 162670129) {
            return expLevels - 100000;
        } else if (expLevels > 81335063) {
            return 81335063;
        } else if (expLevels > 40667527) {
            return 40667527;
        } else if (expLevels > 20333759) {
            return 20333759;
        } else if (expLevels > 10166857) {
            return 10166857;
        } else if (expLevels > 5083423) {
            return 5083423;
        } else if (expLevels > 2541701) {
            return 2541701;
        } else if (expLevels > 1270849) {
            return 1270849;
        } else if (expLevels > 635413) {
            return 635413;
        } else if (expLevels > 317701) {
            return 317701;
        } else if (expLevels > 158849) {
            return 158849;
        } else if (expLevels > 79423) {
            return 79423;
        } else if (expLevels > 39709) {
            return 39709;
        } else if (expLevels > 19853) {
            return 19853;
        } else if (expLevels > 9923) {
            return 9923;
        } else if (expLevels > 4957) {
            return 4957;
        } else {
            return expLevels >= 2477 ? 2477 : (expLevels >= 1237 ? 1237 : (expLevels >= 617 ? 617 : (expLevels >= 307 ? 307 : (expLevels >= 149 ? 149 : (expLevels >= 73 ? 73 : (expLevels >= 37 ? 37 : (expLevels >= 17 ? 17 : (expLevels >= 7 ? 7 : (expLevels >= 3 ? 3 : 1)))))))));
        }
    }

}
