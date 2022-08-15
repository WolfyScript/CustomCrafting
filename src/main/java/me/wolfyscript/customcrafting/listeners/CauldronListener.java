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

import com.wolfyscript.utilities.bukkit.WolfyCoreBukkit;
import com.wolfyscript.utilities.bukkit.persistent.world.BlockStorage;
import com.wolfyscript.utilities.bukkit.persistent.world.WorldStorage;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.CacheCauldronWorkstation;
import me.wolfyscript.customcrafting.data.cauldron.Cauldron;
import me.wolfyscript.customcrafting.data.cauldron.Cauldrons;
import me.wolfyscript.customcrafting.data.persistent.CauldronBlockData;
import me.wolfyscript.customcrafting.gui.cauldron.CauldronWorkstationCluster;
import me.wolfyscript.customcrafting.listeners.customevents.CauldronPreCookEvent;
import me.wolfyscript.customcrafting.recipes.CustomRecipe;
import me.wolfyscript.customcrafting.recipes.CustomRecipeCauldron;
import me.wolfyscript.customcrafting.recipes.RecipeType;
import me.wolfyscript.customcrafting.utils.PlayerUtil;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.util.inventory.InventoryUtils;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Comparator;
import java.util.List;

public class CauldronListener implements Listener {

    private final CustomCrafting customCrafting;
    private final WolfyUtilities api;

    public CauldronListener(CustomCrafting customCrafting) {
        this.customCrafting = customCrafting;
        this.api = customCrafting.getApi();
    }

    @EventHandler
    public void onInteractWithCauldron(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getPlayer().isSneaking()) return;

        Block clicked = event.getClickedBlock();
        if (clicked != null && Cauldrons.isCauldron(clicked.getType())) {
            WorldStorage worldStorage = ((WolfyCoreBukkit)api.getCore()).getPersistentStorage().getOrCreateWorldStorage(clicked.getWorld());
            BlockStorage blockStorage = worldStorage.getOrCreateAndSetBlockStorage(clicked.getLocation());

            if (blockStorage.getData(CauldronBlockData.ID, CauldronBlockData.class).isEmpty()) {
                var cauldronBlockData = new CauldronBlockData(blockStorage.getPos(), blockStorage.getChunkStorage());
                blockStorage.addOrSetData(cauldronBlockData);
                cauldronBlockData.onLoad();
                blockStorage.getChunkStorage().updateBlock(blockStorage.getPos());
            }
            blockStorage.getData(CauldronBlockData.ID, CauldronBlockData.class).ifPresent(cauldronBlockData -> {
                GuiHandler<CCCache> guiHandler = api.getInventoryAPI(CCCache.class).getGuiHandler(event.getPlayer());
                CacheCauldronWorkstation cauldronWorkstation = guiHandler.getCustomCache().getCauldronWorkstation();
                cauldronWorkstation.setBlockData(cauldronBlockData);
                cauldronWorkstation.setBlock(clicked);
                guiHandler.openCluster(CauldronWorkstationCluster.KEY);
            });
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        var block = event.getBlock();
        if (Cauldrons.isCauldron(block.getType())) {
            if (customCrafting.getCauldrons().isCauldron(block.getLocation())) {
                customCrafting.getCauldrons().removeCauldron(block.getLocation());
            }
        } else if (block.getType().equals(Material.CAMPFIRE)) {
            var location = block.getLocation().add(0, 1, 0);
            if (customCrafting.getCauldrons().isCauldron(location)) {
                customCrafting.getCauldrons().removeCauldron(location);
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        var block = event.getBlock();
        if (Cauldrons.isCauldron(block.getType())) {
            if (block.getLocation().subtract(0, 1, 0).getBlock().getType().equals(Material.CAMPFIRE)) {
                customCrafting.getCauldrons().addCauldron(block.getLocation());
            }
        } else if (block.getType().equals(Material.CAMPFIRE)) {
            var location = block.getLocation().add(0, 1, 0);
            if (Cauldrons.isCauldron(location.getBlock().getType())) {
                customCrafting.getCauldrons().addCauldron(location);
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && event.getHand().equals(EquipmentSlot.HAND) && event.getClickedBlock() != null && Cauldrons.isCauldron(event.getClickedBlock().getType())) {
            var player = event.getPlayer();
            var block = event.getClickedBlock();
            var cauldrons = customCrafting.getCauldrons();
            if (cauldrons.isCauldron(block.getLocation())) {
                for (Cauldron cauldron : cauldrons.getCauldrons().get(block.getLocation())) {
                    if (cauldron.isDone() && !cauldron.dropItems()) {
                        ItemStack handItem = event.getItem();
                        CustomRecipeCauldron recipe = cauldron.getRecipe();
                        CustomItem required = recipe.getHandItem();
                        event.setUseItemInHand(Event.Result.DENY);
                        event.setUseInteractedBlock(Event.Result.DENY);
                        if (!ItemUtils.isAirOrNull(handItem)) {
                            if (!ItemUtils.isAirOrNull(required) && required.isSimilar(handItem, recipe.isCheckNBT())) {
                                handItem.setAmount(handItem.getAmount() - 1);
                            } else if (!ItemUtils.isAirOrNull(required)) {
                                return;
                            }
                        } else if (!ItemUtils.isAirOrNull(required)) {
                            return;
                        }
                        ItemStack result = cauldron.getResult().create();
                        if (InventoryUtils.hasInventorySpace(player, result)) {
                            player.getInventory().addItem(result);
                        } else {
                            player.getWorld().dropItemNaturally(player.getLocation(), result);
                        }
                        if (recipe.getXp() > 0) {
                            player.giveExp(recipe.getXp());
                        }
                        cauldron.setForRemoval(true);
                    }
                }
            }
        }
    }

    /*
    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        var player = event.getPlayer();
        var itemDrop = event.getItemDrop();
        var cauldrons = customCrafting.getCauldrons();
        Bukkit.getScheduler().runTaskLater(api.getPlugin(),
                () -> {
                    if (cauldrons != null && cauldrons.getCauldrons() != null) {
                        cauldrons.getCauldrons().entrySet().stream().filter(entry -> entry.getKey().getWorld() != null)
                                .filter(entry -> entry.getKey().getWorld().equals(itemDrop.getLocation().getWorld()) && entry.getKey().clone().add(0.5, 0.4, 0.5).distance(itemDrop.getLocation()) <= 0.4)
                                .forEach(entry -> {
                                    Location loc = entry.getKey();
                                    List<Item> items = loc.getWorld().getNearbyEntities(loc.clone().add(0.5, 0.4, 0.5), 0.5, 0.4, 0.5, Item.class::isInstance).stream().map(Item.class::cast).toList();
                                    if (!items.isEmpty()) {
                                        var cauldronBlock = loc.getBlock();
                                        int level = Cauldrons.getLevel(cauldronBlock);
                                        List<CustomRecipeCauldron> recipes = customCrafting.getRegistries().getRecipes().get(RecipeType.CAULDRON);
                                        recipes.sort(Comparator.comparing(CustomRecipe::getPriority));
                                        for (CustomRecipeCauldron recipe : recipes) {
                                            if (entry.getValue().isEmpty() || entry.getValue().get(0).getRecipe().getNamespacedKey().equals(recipe.getNamespacedKey())) {
                                                if (level >= recipe.getWaterLevel() && (level == 0 || recipe.needsWater()) && (!recipe.needsFire() || cauldrons.isCustomCauldronLit(loc.getBlock()))) {
                                                    List<Item> validItems = recipe.checkRecipe(items);
                                                    if (!validItems.isEmpty()) {
                                                        //Do something with the items! e.g. consume!
                                                        var cauldronPreCookEvent = new CauldronPreCookEvent(customCrafting, recipe, player, cauldronBlock);
                                                        Bukkit.getPluginManager().callEvent(cauldronPreCookEvent);
                                                        if (!cauldronPreCookEvent.isCancelled()) {
                                                            synchronized (cauldrons.getCauldrons()) {
                                                                entry.getValue().add(new Cauldron(cauldronPreCookEvent));
                                                            }
                                                            for (int i = 0; i < recipe.getIngredient().size() && i < validItems.size(); i++) {
                                                                var itemEntity = validItems.get(i);
                                                                var customItem = recipe.getIngredient().getChoices().get(i);
                                                                customItem.remove(itemEntity.getItemStack(), customItem.getAmount(), itemEntity.getLocation().add(0.0, 0.5, 0.0));
                                                            }
                                                        }
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                });
                    }
                }, 20
        );
    }

     */
}
