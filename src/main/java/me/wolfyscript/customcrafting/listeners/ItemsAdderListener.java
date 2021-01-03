package me.wolfyscript.customcrafting.listeners;

import dev.lone.itemsadder.api.Events.ItemsAdderFirstLoadEvent;
import me.wolfyscript.customcrafting.CustomCrafting;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.io.IOException;

public class ItemsAdderListener implements Listener {

    private final CustomCrafting customCrafting;

    public ItemsAdderListener(CustomCrafting customCrafting) {
        this.customCrafting = customCrafting;
    }

    @EventHandler
    public void onLoadComplete(ItemsAdderFirstLoadEvent event) {
        System.out.println("--------------------------------------------------------------------");
        try {
            CustomCrafting.getInst().loadRecipesAndItems();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("--------------------------------------------------------------------");
    }

}
