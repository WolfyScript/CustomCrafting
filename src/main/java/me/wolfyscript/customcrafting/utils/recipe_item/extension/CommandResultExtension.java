package me.wolfyscript.customcrafting.utils.recipe_item.extension;

import me.clip.placeholderapi.PlaceholderAPI;
import me.wolfyscript.utilities.api.WolfyUtilities;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CommandResultExtension extends ResultExtension {

    private final List<String> commands = new ArrayList<>();
    private final List<String> consoleCommands = new ArrayList<>();
    private final List<String> playerCommands = new ArrayList<>();
    private final List<String> nearPlayers = new ArrayList<>();

    @Override
    public void onWorkstation(Block block, @Nullable Player player) {

    }

    @Override
    public void onLocation(Location location, @Nullable Player player) {
        if (!nearPlayers.isEmpty()) {
            List<Player> playersInRange = getEntitiesInRange(Player.class, location, outerRadius, innerRadius);
            playersInRange.forEach(player1 -> {
                executeCommands(playerCommands, player1);
                executeCommands(consoleCommands, player1);
            });
        }
    }

    @Override
    public void onPlayer(@NotNull Player player, Location location) {
        executeCommands(playerCommands, player);
        executeCommands(consoleCommands, player);
    }

    private void executeCommands(List<String> commands, Player player) {
        if (commands.isEmpty()) return;
        commands.forEach(s -> {
            if (WolfyUtilities.hasPlaceHolderAPI()) {
                s = PlaceholderAPI.setPlaceholders(player, s);
            } else {
                s = s.replace("%player%", player.getName());
            }
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s);
        });
    }


}
