package me.wolfyscript.customcrafting.listeners;

import me.wolfyscript.utilities.api.WolfyUtilities;
import org.bukkit.event.Listener;

public class BlockListener implements Listener {

    private final WolfyUtilities api;

    public BlockListener(WolfyUtilities api) {
        this.api = api;
    }
}
