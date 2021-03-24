package me.wolfyscript.customcrafting.listeners;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.Registry;
import me.wolfyscript.customcrafting.data.cauldron.Cauldron;
import me.wolfyscript.customcrafting.data.cauldron.Cauldrons;
import me.wolfyscript.customcrafting.listeners.customevents.CauldronPreCookEvent;
import me.wolfyscript.customcrafting.recipes.Types;
import me.wolfyscript.customcrafting.recipes.types.ICustomRecipe;
import me.wolfyscript.customcrafting.recipes.types.cauldron.CauldronRecipe;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.util.inventory.InventoryUtils;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Levelled;
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

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class CauldronListener implements Listener {

    private final CustomCrafting customCrafting;
    private final WolfyUtilities api;

    public CauldronListener(CustomCrafting customCrafting) {
        this.customCrafting = customCrafting;
        this.api = WolfyUtilities.get(customCrafting);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (block.getType().equals(Material.CAULDRON)) {
            if (CustomCrafting.inst().getCauldrons().isCauldron(block.getLocation())) {
                CustomCrafting.inst().getCauldrons().removeCauldron(block.getLocation());
            }
        } else if (block.getType().equals(Material.CAMPFIRE)) {
            Location location = block.getLocation().add(0, 1, 0);
            if (CustomCrafting.inst().getCauldrons().isCauldron(location)) {
                CustomCrafting.inst().getCauldrons().removeCauldron(location);
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        if (block.getType().equals(Material.CAULDRON)) {
            if (block.getLocation().subtract(0, 1, 0).getBlock().getType().equals(Material.CAMPFIRE)) {
                CustomCrafting.inst().getCauldrons().addCauldron(block.getLocation());
            }
        } else if (block.getType().equals(Material.CAMPFIRE)) {
            Location location = block.getLocation().add(0, 1, 0);
            if (location.getBlock().getType().equals(Material.CAULDRON)) {
                CustomCrafting.inst().getCauldrons().addCauldron(location);
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && event.getHand().equals(EquipmentSlot.HAND) && event.getClickedBlock() != null && event.getClickedBlock().getType().equals(Material.CAULDRON)) {
            Player player = event.getPlayer();
            Block block = event.getClickedBlock();
            Cauldrons cauldrons = CustomCrafting.inst().getCauldrons();
            if (cauldrons.isCauldron(block.getLocation())) {
                for (Cauldron cauldron : cauldrons.getCauldrons().get(block.getLocation())) {
                    if (cauldron.isDone() && !cauldron.dropItems()) {
                        ItemStack handItem = event.getItem();
                        CustomItem required = cauldron.getRecipe().getHandItem();
                        event.setUseItemInHand(Event.Result.DENY);
                        event.setUseInteractedBlock(Event.Result.DENY);
                        if (!ItemUtils.isAirOrNull(handItem)) {
                            if (!ItemUtils.isAirOrNull(required) && required.isSimilar(handItem, cauldron.getRecipe().isExactMeta())) {
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
                        cauldron.setForRemoval(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        Item itemDrop = event.getItemDrop();
        Cauldrons cauldrons = CustomCrafting.inst().getCauldrons();
        Bukkit.getScheduler().runTaskLater(api.getPlugin(), () -> cauldrons.getCauldrons().entrySet().stream()
                .filter(entry -> entry.getKey().getWorld() != null)
                .filter(entry -> entry.getKey().getWorld().equals(itemDrop.getLocation().getWorld()) && entry.getKey().clone().add(0.5, 0.4, 0.5).distance(itemDrop.getLocation()) <= 0.4)
                .forEach(entry -> {
                    Location loc = entry.getKey();
                    List<Item> items = loc.getWorld().getNearbyEntities(loc.clone().add(0.5, 0.4, 0.5), 0.5, 0.4, 0.5, entity -> entity instanceof Item).stream().map(entity -> (Item) entity).collect(Collectors.toList());
                    if (!items.isEmpty()) {
                        int level = ((Levelled) loc.getBlock().getBlockData()).getLevel();
                        List<Cauldron> cauldronEntryValue = entry.getValue();
                        //Check for new possible Recipes
                        List<CauldronRecipe> recipes = Registry.RECIPES.get(Types.CAULDRON);
                        recipes.sort(Comparator.comparing(ICustomRecipe::getPriority));
                        for (CauldronRecipe recipe : recipes) {
                            if (cauldronEntryValue.isEmpty() || cauldronEntryValue.get(0).getRecipe().getNamespacedKey().equals(recipe.getNamespacedKey())) {
                                if (level >= recipe.getWaterLevel() && (level == 0 || recipe.needsWater()) && (!recipe.needsFire() || cauldrons.isCustomCauldronLit(loc.getBlock()))) {
                                    List<Item> validItems = recipe.checkRecipe(items);
                                    if (!validItems.isEmpty()) {
                                        //Do something with the items! e.g. consume!
                                        CauldronPreCookEvent cauldronPreCookEvent = new CauldronPreCookEvent(recipe, player);
                                        Bukkit.getPluginManager().callEvent(cauldronPreCookEvent);
                                        if (!cauldronPreCookEvent.isCancelled()) {
                                            synchronized (cauldrons.getCauldrons()) {
                                                cauldronEntryValue.add(new Cauldron(cauldronPreCookEvent));
                                            }
                                            for (int i = 0; i < recipe.getIngredient().size() && i < validItems.size(); i++) {
                                                Item itemEntity = validItems.get(i);
                                                ItemStack itemStack = itemEntity.getItemStack();
                                                CustomItem customItem = recipe.getIngredient().getChoices().get(i);
                                                customItem.consumeItem(itemStack, customItem.getAmount(), itemEntity.getLocation().clone().add(0.0, 0.5, 0.0));
                                            }
                                        }
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }), 20
        );
    }
}
