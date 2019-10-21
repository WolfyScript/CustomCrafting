package me.wolfyscript.customcrafting.listeners;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.cauldron.Cauldron;
import me.wolfyscript.customcrafting.data.cauldron.Cauldrons;
import me.wolfyscript.customcrafting.listeners.customevents.CauldronPreCookEvent;
import me.wolfyscript.customcrafting.recipes.types.cauldron.CauldronRecipe;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Levelled;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CauldronListener implements Listener {

    private WolfyUtilities api;

    public CauldronListener(WolfyUtilities api) {
        this.api = api;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (block.getType().equals(Material.CAULDRON)) {
            if (CustomCrafting.getCauldrons().isCauldron(block.getLocation())) {
                CustomCrafting.getCauldrons().removeCauldron(block.getLocation());
            }
        } else if (WolfyUtilities.hasVillagePillageUpdate() && block.getType().equals(Material.CAMPFIRE)) {
            Location location = block.getLocation().add(0, 1, 0);
            if (CustomCrafting.getCauldrons().isCauldron(location)) {
                CustomCrafting.getCauldrons().removeCauldron(location);
            }
        } else if (block.getType().equals(Material.NETHERRACK)) {
            Location location = block.getLocation().add(0, 2, 0);
            if (CustomCrafting.getCauldrons().isCauldron(location)) {
                CustomCrafting.getCauldrons().removeCauldron(location);
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        if (block.getType().equals(Material.CAULDRON)) {
            if ((WolfyUtilities.hasVillagePillageUpdate() && block.getLocation().subtract(0, 1, 0).getBlock().getType().equals(Material.CAMPFIRE)) || block.getLocation().subtract(0, 2, 0).getBlock().getType().equals(Material.NETHERRACK)) {
                CustomCrafting.getCauldrons().addCauldron(block.getLocation());
            }
        } else if (WolfyUtilities.hasVillagePillageUpdate() && block.getType().equals(Material.CAMPFIRE)) {
            Location location = block.getLocation().add(0, 1, 0);
            if (location.getBlock().getType().equals(Material.CAULDRON)) {
                CustomCrafting.getCauldrons().addCauldron(location);
            }
        } else if (block.getType().equals(Material.NETHERRACK)) {
            Location location = block.getLocation().add(0, 2, 0);
            if (location.getBlock().getType().equals(Material.CAULDRON)) {
                CustomCrafting.getCauldrons().addCauldron(location);
            }
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        Item itemDrop = event.getItemDrop();
        Cauldrons cauldrons = CustomCrafting.getCauldrons();
        Bukkit.getScheduler().runTaskLater(api.getPlugin(), () -> {
            for (Map.Entry<Location, List<Cauldron>> cauldronEntry : cauldrons.getCauldrons().entrySet()) {
                Location loc = cauldronEntry.getKey();
                List<Cauldron> cauldronEntryValue = cauldronEntry.getValue();
                if(loc.getWorld() != itemDrop.getLocation().getWorld()){
                    continue;
                }
                double distance = loc.clone().add(0.5, 0.4, 0.5).distance(itemDrop.getLocation());
                if (distance > 0.4) {
                    continue;
                }
                List<Item> items = new ArrayList<>();
                for (Entity entity : loc.getWorld().getNearbyEntities(loc.clone().add(0.5, 0.4, 0.5), 0.5, 0.4, 0.5, entity -> entity instanceof Item)) {
                    items.add((Item) entity);
                }
                if (!items.isEmpty()) {
                    int level = ((Levelled) loc.getBlock().getBlockData()).getLevel();
                    //Check for new possible Recipes
                    List<CauldronRecipe> recipes = CustomCrafting.getRecipeHandler().getCauldronRecipes();
                    for (CauldronRecipe recipe : recipes) {
                        if (cauldronEntryValue.isEmpty() || cauldronEntryValue.get(0).getRecipe().getId().equals(recipe.getId())) {
                            if (level >= recipe.getWaterLevel() && (level == 0 || !recipe.isNoWater()) && (!recipe.needsFire() || cauldrons.isCustomCauldronLit(loc.getBlock()))) {
                                List<Item> validItems = recipe.checkRecipe(items);
                                if (validItems != null) {
                                    //Do something with the items! e.g. consume!
                                    CauldronPreCookEvent cauldronPreCookEvent = new CauldronPreCookEvent(recipe, player);
                                    Bukkit.getPluginManager().callEvent(cauldronPreCookEvent);
                                    if (!cauldronPreCookEvent.isCancelled()) {
                                        cauldronEntryValue.add(new Cauldron(cauldronPreCookEvent));

                                        for (int i = 0; i < recipe.getIngredients().size() && i < validItems.size(); i++) {
                                            Item itemEntity = validItems.get(i);
                                            ItemStack itemStack = itemEntity.getItemStack();
                                            CustomItem customItem = recipe.getIngredients().get(i);
                                            customItem.consumeItem(itemStack, customItem.getAmount(), itemEntity.getLocation().clone().add(0.0, 0.5, 0.0));
                                        }
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }, 20);
    }
}
