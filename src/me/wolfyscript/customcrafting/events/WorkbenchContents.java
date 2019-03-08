package me.wolfyscript.customcrafting.events;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.MainConfig;
import me.wolfyscript.customcrafting.data.PlayerCache;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.EulerAngle;

import java.util.*;

public class WorkbenchContents implements Listener {

    private static HashMap<Location, Boolean> openWorkbenches = new HashMap<>();

    private MainConfig config = CustomCrafting.getConfigHandler().getConfig();

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        Block block = e.getView().getPlayer().getTargetBlock(null, 5);
        if (config.saveContents() && e.getInventory() != null && e.getInventory().getType().equals(InventoryType.WORKBENCH) && isWorkbench(block)) {
            CustomCrafting.getWorkbenches().setContents(block.getLocation(), ((CraftingInventory) e.getInventory()).getMatrix());
            updateArmorStands(block.getLocation());
            Player player = (Player) e.getPlayer();
            Bukkit.getScheduler().runTaskLater(CustomCrafting.getInst(), () -> {
                ItemStack[] items = CustomCrafting.getWorkbenches().getContents(block.getLocation()).toArray(new ItemStack[0]);
                if(player.getInventory() != null){
                    player.getInventory().removeItem(items);
                }
            }, 1);
            close(block.getLocation());
        }
    }

    @EventHandler
    public void onOpen(PlayerInteractEvent event) {
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            Block block = event.getClickedBlock();
            if (block.getType().equals(Material.CRAFTING_TABLE)) {
                Location location = block.getLocation();
                if (isWorkbench(block)) {
                    if (!isOpen(location)) {
                        event.setCancelled(true);
                        InventoryView inventory = event.getPlayer().openWorkbench(block.getLocation(), false);
                        Bukkit.getScheduler().runTaskLater(CustomCrafting.getInst(), () -> {
                            ItemStack[] items = CustomCrafting.getWorkbenches().getContents(block.getLocation()).toArray(new ItemStack[0]);
                            if(items.length == 9){
                                ((CraftingInventory)inventory.getTopInventory()).setMatrix(items);
                            }
                        },1);
                        setOpen(location);
                    } else {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Block block = e.getView().getPlayer().getTargetBlock(null, 5);
        if (e.getInventory() != null && e.getInventory().getType().equals(InventoryType.WORKBENCH)) {
            if(isWorkbench(block)){
                updateContents(block.getLocation(), ((CraftingInventory) e.getInventory()).getMatrix());
            }
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent e) {
        Block block = e.getView().getPlayer().getTargetBlock(null, 5);
        if (isWorkbench(block) && e.getInventory() != null && e.getInventory().getType().equals(InventoryType.WORKBENCH)) {
            updateContents(block.getLocation(), ((CraftingInventory) e.getInventory()).getMatrix());
        }
    }

    private void updateContents(Location location, ItemStack[] matrix) {
        Bukkit.getScheduler().runTaskLater(CustomCrafting.getInst(), () -> {
            CustomCrafting.getWorkbenches().setContents(location, matrix);
        }, 1);
        updateArmorStands(location);
    }

    private void updateArmorStands(Location location){
        Bukkit.getScheduler().runTaskLater(CustomCrafting.getInst(), () -> {
            Collection<Entity> entities = location.getWorld().getNearbyEntities(BoundingBox.of(location.getBlock()), entity -> (entity instanceof ArmorStand) && !entity.getCustomName().isEmpty() && entity.getCustomName().startsWith("cc:"));
            for (Entity entity : entities){
                entity.remove();
            }
            if(config.displayContents()){
                List<ItemStack> contents = CustomCrafting.getWorkbenches().getContents(location);
                if(!contents.isEmpty()){
                    for(int i = 0; i < 9; i++){
                        ItemStack item = contents.get(i);
                        if(!item.getType().equals(Material.AIR)){
                            int j = (i - ((i/3)*3));
                            int k = i/3;
                            float x = 0.185f * j;
                            float z = 0.185f * k;
                            location.getWorld().spawn(location.clone().add(0.125 + x, 0.04, 0.45 + z), ArmorStand.class, armorStand -> {
                                armorStand.setInvulnerable(true);
                                armorStand.setVisible(false);
                                armorStand.setCustomName("cc:"+item.getType().toString());
                                armorStand.setCustomNameVisible(false);
                                armorStand.setSmall(true);
                                armorStand.setMarker(true);
                                armorStand.setAI(false);
                                armorStand.setArms(true);
                                armorStand.setLeftArmPose(new EulerAngle(3.141593,0,0));
                                armorStand.setGravity(false);
                                armorStand.getEquipment().setItemInOffHand(item);
                            });
                        }
                    }
                }
            }
        }, 1);
    }

    private boolean isWorkbench(Block block) {
        return block.getType().equals(Material.CRAFTING_TABLE) && CustomCrafting.getWorkbenches().isWorkbench(block.getLocation());
    }

    public static boolean isOpen(Location location) {
        return !openWorkbenches.isEmpty() && openWorkbenches.containsKey(location) && openWorkbenches.get(location);
    }

    public static void setOpen(Location location) {
        openWorkbenches.put(location, true);
    }

    public static void close(Location location) {
        openWorkbenches.put(location, false);
    }
}
