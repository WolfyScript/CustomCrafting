package me.wolfyscript.customcrafting.commands.recipes;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.Registry;
import me.wolfyscript.customcrafting.commands.AbstractSubCommand;
import me.wolfyscript.customcrafting.recipes.CraftingRecipe;
import me.wolfyscript.customcrafting.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ToggleSubCommand extends AbstractSubCommand {

    public ToggleSubCommand(CustomCrafting customCrafting) {
        super("toggle", new ArrayList<>(), customCrafting);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull String var3, @NotNull String[] args) {
        if (sender instanceof Player && ChatUtils.checkPerm(sender, "customcrafting.cmd.recipes.toggle") && args.length > 0) {
            String id = args[0];
            if (!id.isEmpty() && id.contains(":")) {
                var namespacedKey = me.wolfyscript.utilities.util.NamespacedKey.of(id);
                if (customCrafting.getDataHandler().getDisabledRecipes().contains(namespacedKey)) {
                    sender.sendMessage("Enabled recipe " + id);
                    customCrafting.getDataHandler().getDisabledRecipes().remove(namespacedKey);
                } else {
                    sender.sendMessage("Disabled recipe " + id);
                    customCrafting.getDataHandler().getDisabledRecipes().add(namespacedKey);
                    if (namespacedKey != null) {
                        Bukkit.getOnlinePlayers().forEach(player -> player.undiscoverRecipe(namespacedKey.toBukkit(customCrafting)));
                    }
                }
            }
        }
        return true;
    }

    @Override
    protected @Nullable
    List<String> onTabComplete(@NotNull CommandSender var1, @NotNull String var3, @NotNull String[] args) {
        List<String> results = new ArrayList<>();
        List<String> recipes = customCrafting.getDataHandler().getBukkitNamespacedKeys();
        recipes.addAll(Registry.RECIPES.get(CraftingRecipe.class).stream().map(recipe -> recipe.getNamespacedKey().toString()).collect(Collectors.toSet()));
        StringUtil.copyPartialMatches(args[args.length - 1], recipes, results);
        return results;
    }
}
