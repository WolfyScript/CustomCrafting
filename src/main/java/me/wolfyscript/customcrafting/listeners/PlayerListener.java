package me.wolfyscript.customcrafting.listeners;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.custom_data.KnowledgeBookData;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.chat.ClickData;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerListener implements Listener {

    private final CustomCrafting customCrafting;

    public PlayerListener(CustomCrafting customCrafting) {
        this.customCrafting = customCrafting;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        WolfyUtilities api = CustomCrafting.getApi();
        if ((player.isOp() || player.hasPermission("customcrafting.*") || player.hasPermission("customcrafting.update_check"))) {
            if (customCrafting.isOutdated()) {
                api.getChat().sendMessage(player, "$msg.player.outdated.msg$");
                api.getChat().sendActionMessage(player, new ClickData("$msg.player.outdated.msg2$", null), new ClickData("$msg.player.outdated.link$", null, new me.wolfyscript.utilities.api.chat.ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.spigotmc.org/resources/55883/")));
            } else if (!customCrafting.getPatreon().isPatreon()) {
                customCrafting.checkUpdate(player);
            }
        }
    }

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        if (event.hasItem() && (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_AIR))) {
            Player p = event.getPlayer();
            ItemStack itemStack = event.getItem();
            CustomItem customItem = CustomItem.getByItemStack(itemStack);
            if (customItem != null) {
                KnowledgeBookData knowledgeBook = (KnowledgeBookData) customItem.getCustomData(CustomCrafting.RECIPE_BOOK);
                if (knowledgeBook.isEnabled()) {
                    event.setUseItemInHand(Event.Result.DENY);
                    event.setUseInteractedBlock(Event.Result.DENY);
                    event.getPlayer().closeInventory();
                    CustomCrafting.getApi().getInventoryAPI().openCluster(p, "recipe_book");
                }
            }
        }
    }
}
