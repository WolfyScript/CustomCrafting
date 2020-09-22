package me.wolfyscript.customcrafting.commands.recipes;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.commands.AbstractSubCommand;
import me.wolfyscript.customcrafting.recipes.types.CraftingRecipe;
import me.wolfyscript.customcrafting.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
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
        if (sender instanceof Player) {
            if (ChatUtils.checkPerm(sender, "customcrafting.cmd.recipes.toggle")) {
                if (args.length > 0) {
                    String id = args[0];
                    if (!id.isEmpty() && id.contains(":")) {
                        if (customCrafting.getRecipeHandler().getDisabledRecipes().contains(id)) {
                            sender.sendMessage("Enabled recipe " + id);
                            customCrafting.getRecipeHandler().getDisabledRecipes().remove(id);
                        } else {
                            sender.sendMessage("Disabled recipe " + id);
                            customCrafting.getRecipeHandler().getDisabledRecipes().add(id);
                            Bukkit.getOnlinePlayers().forEach(player -> player.undiscoverRecipe(new NamespacedKey(id.split(":")[0], id.split(":")[1])));
                        }
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
        List<String> recipes = customCrafting.getRecipeHandler().getVanillaRecipes().stream().filter(recipe -> recipe instanceof Keyed).map(recipe -> ((Keyed) recipe).getKey().toString()).collect(Collectors.toList());
        recipes.addAll(customCrafting.getRecipeHandler().getRecipes(CraftingRecipe.class).stream().map(recipe -> recipe.getNamespacedKey().toString()).collect(Collectors.toSet()));
        StringUtil.copyPartialMatches(args[args.length - 1], recipes, results);
        return results;
    }
}
