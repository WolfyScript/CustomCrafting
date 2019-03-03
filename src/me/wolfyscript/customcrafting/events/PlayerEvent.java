package me.wolfyscript.customcrafting.events;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.PlayerCache;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerEvent implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        if(!CustomCrafting.hasPlayerCache(player)){
            CustomCrafting.getApi().sendConsoleMessage("Initializing new cache for "+player.getDisplayName());
        }
        PlayerCache playerCache = CustomCrafting.getPlayerCache(player);

    }

}
