package me.wolfyscript.customcrafting.utils;

import me.wolfyscript.customcrafting.data.CCPlayerData;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.entity.PlayerUtils;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlayerUtil {

    private PlayerUtil() {
    }

    public static final NamespacedKey CC_DATA = new NamespacedKey("customcrafting", "data");

    public static CCPlayerData getStore(Player player) {
        return getStore(player.getUniqueId());
    }

    public static CCPlayerData getStore(UUID uuid) {
        return PlayerUtils.getStore(uuid).getData(CC_DATA, CCPlayerData.class);
    }

}
