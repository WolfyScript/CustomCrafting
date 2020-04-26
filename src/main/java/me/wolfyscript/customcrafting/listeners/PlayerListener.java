package me.wolfyscript.customcrafting.listeners;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.types.furnace.CustomFurnaceRecipe;
import me.wolfyscript.customcrafting.utils.ChatUtils;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.utils.chat.ClickData;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class PlayerListener implements Listener {

    private CustomCrafting customCrafting;

    public PlayerListener(CustomCrafting customCrafting) {
        this.customCrafting = customCrafting;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        WolfyUtilities api = CustomCrafting.getApi();
        if (!customCrafting.isLoaded()) {
            api.sendPlayerMessage(player, "$msg.player.error.loading.msg$");
            api.sendPlayerMessage(player, "$msg.player.error.loading.msg1$");
            api.sendPlayerMessage(player, "$msg.player.error.loading.msg2$");
            api.sendPlayerMessage(player, "$msg.player.error.loading.msg3$");
            api.sendActionMessage(player,
                    new ClickData("$msg.player.error.loading.msg4$", null, new me.wolfyscript.utilities.api.utils.chat.ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.spigotmc.org/wiki/buildtools/")),
                    new ClickData("$msg.player.error.loading.msg5$", null, new me.wolfyscript.utilities.api.utils.chat.ClickEvent(ClickEvent.Action.OPEN_URL, "https://papermc.io/downloads")));
            api.sendPlayerMessage(player, "");
            api.sendPlayerMessage(player, "$msg.player.error.loading.msg6$");
        }
        for (CustomFurnaceRecipe customFurnaceRecipe : customCrafting.getRecipeHandler().getFurnaceRecipes()) {
            player.undiscoverRecipe(new NamespacedKey(customFurnaceRecipe.getNamespacedKey().getNamespace(), customFurnaceRecipe.getNamespacedKey().getKey()));
        }
        if (!CustomCrafting.hasPlayerCache(player)) {
            CustomCrafting.getApi().sendConsoleMessage("Initializing new cache for " + player.getDisplayName());
            CustomCrafting.renewPlayerStatistics(player);
        }
        if ((player.isOp() || player.hasPermission("customcrafting.*") || player.hasPermission("customcrafting.update_check"))) {
            if (customCrafting.isOutdated()) {
                api.sendPlayerMessage(player, "$msg.player.outdated.msg$");
                api.sendActionMessage(player, new ClickData("$msg.player.outdated.msg2$", null), new ClickData("$msg.player.outdated.link$", null, new me.wolfyscript.utilities.api.utils.chat.ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.spigotmc.org/resources/55883/")));
            } else {
                customCrafting.checkUpdate(player);
            }
        }
    }

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        if (event.hasItem() && (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_AIR))) {
            ItemStack itemStack = event.getItem();
            if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasLore()) {
                List<String> lore = itemStack.getItemMeta().getLore();
                for (String line : lore) {
                    String unhidden = WolfyUtilities.unhideString(line);
                    if (unhidden.equals("cc_knowledgebook")) {
                        Player p = event.getPlayer();
                        event.setUseItemInHand(Event.Result.DENY);
                        event.setCancelled(true);
                        if (event.hasBlock()) {
                            if (event.getClickedBlock().getType().isInteractable()) {
                                return;
                            }
                        }
                        if (ChatUtils.checkPerm(event.getPlayer(), "customcrafting.item.knowledge_book")) {
                            CustomCrafting.getApi().getInventoryAPI().openCluster(p, "recipe_book");
                        }
                    }
                }
            }
        }
    }
}
