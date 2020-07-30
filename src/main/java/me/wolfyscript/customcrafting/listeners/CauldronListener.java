package me.wolfyscript.customcrafting.listeners;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.cauldron.Cauldron;
import me.wolfyscript.customcrafting.data.cauldron.Cauldrons;
import me.wolfyscript.customcrafting.listeners.customevents.CauldronPreCookEvent;
import me.wolfyscript.customcrafting.recipes.types.ICustomRecipe;
import me.wolfyscript.customcrafting.recipes.types.cauldron.CauldronRecipe;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.utils.inventory.InventoryUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Levelled;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
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

import java.util.*;

public class CauldronListener implements Listener {

    private final CustomCrafting customCrafting;
    private final WolfyUtilities api;

    public CauldronListener(CustomCrafting customCrafting) {
        this.customCrafting = customCrafting;
        this.api = WolfyUtilities.getAPI(customCrafting);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (block.getType().equals(Material.CAULDRON)) {
            if (CustomCrafting.getCauldrons().isCauldron(block.getLocation())) {
                CustomCrafting.getCauldrons().removeCauldron(block.getLocation());
            }
        } else if (block.getType().equals(Material.CAMPFIRE)) {
            Location location = block.getLocation().add(0, 1, 0);
            if (CustomCrafting.getCauldrons().isCauldron(location)) {
                CustomCrafting.getCauldrons().removeCauldron(location);
            }
        }

    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        if (block.getType().equals(Material.CAULDRON)) {
            if (block.getLocation().subtract(0, 1, 0).getBlock().getType().equals(Material.CAMPFIRE)) {
                CustomCrafting.getCauldrons().addCauldron(block.getLocation());
            }
        } else if (block.getType().equals(Material.CAMPFIRE)) {
            Location location = block.getLocation().add(0, 1, 0);
            if (location.getBlock().getType().equals(Material.CAULDRON)) {
                CustomCrafting.getCauldrons().addCauldron(location);
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && event.getHand().equals(EquipmentSlot.HAND)) {
            Player player = event.getPlayer();
            Block block = event.getClickedBlock();
            Cauldrons cauldrons = CustomCrafting.getCauldrons();
            if (cauldrons.isCauldron(block.getLocation())) {
                Iterator<Cauldron> cauldronItr = cauldrons.getCauldrons().get(block.getLocation()).iterator();
                while (cauldronItr.hasNext()) {
                    Cauldron cauldron = cauldronItr.next();
                    if (cauldron.isDone() && !cauldron.dropItems()) {
                        ItemStack handItem = event.getItem();
                        CustomItem required = cauldron.getRecipe().getHandItem();
                        if ((handItem != null && (required == null || required.getItemStack().getType().equals(Material.AIR))) || required.isSimilar(handItem, cauldron.getRecipe().isExactMeta())) {
                            event.setUseItemInHand(Event.Result.DENY);
                            event.setUseInteractedBlock(Event.Result.DENY);
                            handItem.setAmount(handItem.getAmount() - 1);

                            ItemStack result = cauldron.getResult().create();
                            if (InventoryUtils.hasInventorySpace(player, result)) {
                                player.getInventory().addItem(result);
                            } else {
                                player.getWorld().dropItemNaturally(player.getLocation(), result);
                            }
                            cauldronItr.remove();
                        }
                    }
                }
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
                if (loc.getWorld() != itemDrop.getLocation().getWorld()) {
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
                    List<CauldronRecipe> recipes = customCrafting.getRecipeHandler().getCauldronRecipes();
                    recipes.sort(Comparator.comparing(ICustomRecipe::getPriority));
                    for (CauldronRecipe recipe : recipes) {
                        if (cauldronEntryValue.isEmpty() || cauldronEntryValue.get(0).getRecipe().getNamespacedKey().equals(recipe.getNamespacedKey())) {
                            if (level >= recipe.getWaterLevel() && (level == 0 || recipe.needsWater()) && (!recipe.needsFire() || cauldrons.isCustomCauldronLit(loc.getBlock()))) {
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
