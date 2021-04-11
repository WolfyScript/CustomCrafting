package me.wolfyscript.customcrafting.commands;

import me.wolfyscript.customcrafting.CustomCrafting;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class AbstractSubCommand {

    protected final CustomCrafting customCrafting;
    private final String label;
    private final List<String> alias;

    protected AbstractSubCommand(String label, List<String> alias, CustomCrafting customCrafting) {
        this.customCrafting = customCrafting;
        this.label = label;
        this.alias = alias;
    }

    public String getLabel() {
        return label;
    }

    public List<String> getAlias() {
        return alias;
    }

    protected abstract boolean onCommand(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args);

    @Nullable
    protected abstract List<String> onTabComplete(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args);
}
