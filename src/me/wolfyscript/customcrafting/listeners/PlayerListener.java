package me.wolfyscript.customcrafting.listeners;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.PlayerCache;
import me.wolfyscript.customcrafting.recipes.furnace.CustomFurnaceRecipe;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.InventoryAPI;
import me.wolfyscript.utilities.api.utils.chat.ClickData;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.chat.BaseComponentSerializer;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.yaml.snakeyaml.constructor.BaseConstructor;

public class PlayerListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        for(CustomFurnaceRecipe customFurnaceRecipe : CustomCrafting.getRecipeHandler().getFurnaceRecipes()){
            player.undiscoverRecipe(new NamespacedKey(customFurnaceRecipe.getId().split(":")[0], customFurnaceRecipe.getId().split(":")[1]));
        }
        if(!CustomCrafting.hasPlayerCache(player)){
            CustomCrafting.getApi().sendConsoleMessage("Initializing new cache for "+player.getDisplayName());
            CustomCrafting.renewPlayerCache(player);
        }
        InventoryAPI inventoryAPI = CustomCrafting.getApi().getInventoryAPI();
        if(player.isOp() || player.hasPermission("customcrafting.*") || player.hasPermission("customcrafting.update_check")) {
            if (CustomCrafting.isOutdated()) {
                WolfyUtilities api = CustomCrafting.getApi();
                api.sendPlayerMessage(player, "$msg.player.outdated.msg$");
                api.sendActionMessage(player, new ClickData("$msg.player.outdated.msg2$", null), new ClickData("$msg.player.outdated.link$", null, new me.wolfyscript.utilities.api.utils.chat.ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.spigotmc.org/resources/55883/")));
            } else {
                CustomCrafting.checkUpdate(player);
            }
        }
    }

}
