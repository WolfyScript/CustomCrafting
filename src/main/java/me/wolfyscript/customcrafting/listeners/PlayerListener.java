package me.wolfyscript.customcrafting.listeners;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.custom_data.RecipeBookData;
import me.wolfyscript.customcrafting.utils.ItemLoader;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerListener implements Listener {

    private final CustomCrafting customCrafting;

    public PlayerListener(CustomCrafting customCrafting) {
        this.customCrafting = customCrafting;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        var player = event.getPlayer();
        if (customCrafting.getConfigHandler().getConfig().updateOldCustomItems()) {
            ItemStack[] contents = player.getInventory().getContents();
            if (contents.length > 0) {
                for (ItemStack stack : contents) {
                    ItemLoader.updateItem(stack);
                }
            }
        }
        if ((player.isOp() || player.hasPermission("customcrafting.*") || player.hasPermission("customcrafting.update_check"))) {
            customCrafting.getUpdateChecker().run(player);
        }
    }

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        if (event.hasItem() && (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_AIR))) {
            var customItem = CustomItem.getByItemStack(event.getItem());
            if (customItem != null) {
                RecipeBookData knowledgeBook = (RecipeBookData) customItem.getCustomData(CustomCrafting.RECIPE_BOOK);
                if (knowledgeBook != null && knowledgeBook.isEnabled()) {
                    event.setUseItemInHand(Event.Result.DENY);
                    event.setUseInteractedBlock(Event.Result.DENY);
                    event.getPlayer().closeInventory();
                    CustomCrafting.inst().getApi().getInventoryAPI().openCluster(event.getPlayer(), "recipe_book");
                }
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        customCrafting.onPlayerDisconnect(event.getPlayer());
    }
}
