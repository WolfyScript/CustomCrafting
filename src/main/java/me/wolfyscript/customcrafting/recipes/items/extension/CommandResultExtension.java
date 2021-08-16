package me.wolfyscript.customcrafting.recipes.items.extension;

import me.clip.placeholderapi.PlaceholderAPI;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CommandResultExtension extends ResultExtension {

    private List<String> consoleCommands = new ArrayList<>();
    private List<String> playerCommands = new ArrayList<>();
    private boolean nearPlayer = false;
    private boolean nearWorkstation = false;

    public CommandResultExtension() {
        super(new NamespacedKey("customcrafting", "command"));
    }

    public CommandResultExtension(CommandResultExtension extension) {
        super(extension);
        this.consoleCommands = extension.consoleCommands;
        this.playerCommands = extension.playerCommands;
        this.nearPlayer = extension.nearPlayer;
        this.nearWorkstation = extension.nearWorkstation;
    }

    public CommandResultExtension(List<String> consoleCommands, List<String> playerCommands, boolean nearPlayer, boolean nearWorkstation) {
        this();
        this.consoleCommands = consoleCommands;
        this.playerCommands = playerCommands;
        this.nearPlayer = nearPlayer;
        this.nearWorkstation = nearWorkstation;
    }

    @Override
    public void onWorkstation(Block block, @Nullable Player player) {

    }

    @Override
    public void onLocation(Location location, @Nullable Player player) {
        if ((player != null && nearPlayer) || nearWorkstation) {
            getEntitiesInRange(Player.class, location, getOuterRadius(), getInnerRadius()).forEach(this::executeCommands);
        }
    }

    @Override
    public void onPlayer(@NotNull Player player, Location location) {
        executeCommands(player);
    }

    @Override
    public CommandResultExtension clone() {
        return new CommandResultExtension(this);
    }

    protected void executeCommands(Player player) {
        if (!consoleCommands.isEmpty()) {
            parseCommands(consoleCommands, player).forEach(s -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s));
        }
        if (!playerCommands.isEmpty()) {
            parseCommands(playerCommands, player).forEach(s -> Bukkit.dispatchCommand(player, s));
        }
    }

    protected List<String> parseCommands(List<String> commands, Player player) {
        return commands.stream().map(s -> {
            if (WolfyUtilities.hasPlaceHolderAPI()) {
                return PlaceholderAPI.setPlaceholders(player, s);
            } else {
                return s.replace("%player%", player.getName());
            }
        }).toList();
    }


}
