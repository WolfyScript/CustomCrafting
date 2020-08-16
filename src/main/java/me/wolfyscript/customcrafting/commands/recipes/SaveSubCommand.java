package me.wolfyscript.customcrafting.commands.recipes;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.commands.AbstractSubCommand;
import me.wolfyscript.customcrafting.utils.ChatUtils;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.custom_items.CustomItems;
import org.bukkit.Keyed;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class SaveSubCommand extends AbstractSubCommand {

    public SaveSubCommand(CustomCrafting customCrafting) {
        super("save", new ArrayList<>(), customCrafting);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull String var3, @NotNull String[] args) {
        WolfyUtilities api = CustomCrafting.getApi();
        if (sender instanceof Player) {
            if (ChatUtils.checkPerm(sender, "customcrafting.cmd.recipes.save")) {
                new TreeMap<>(CustomItems.getCustomItems()).forEach((namespacedKey, customItem) -> {
                    api.sendConsoleMessage("Saving item: " + namespacedKey.toString());
                    customCrafting.saveItem(namespacedKey, customItem);
                });
                customCrafting.getRecipeHandler().getRecipes().values().forEach(recipe -> {
                    api.sendConsoleMessage("Saving recipe: " + recipe.getNamespacedKey().toString());
                    recipe.save();
                });
                sender.sendMessage("§eAll recipes are resaved! See the console log for errors.");
                sender.sendMessage("§cNotice that some recipes must be recreated due incompatibility! These are: ");
                sender.sendMessage("§c- recipes that caused errors when saving (their config is corrupted from now on)");
                sender.sendMessage("§c- recipes that don't work when the server is restarted");
                sender.sendMessage("§eYou can get or ask for further information on the discord!");
            }
        }
        return true;
    }

    @Override
    protected @Nullable
    List<String> onTabComplete(@NotNull CommandSender var1, @NotNull String var3, @NotNull String[] args) {
        List<String> results = new ArrayList<>();
        List<String> recipes = customCrafting.getRecipeHandler().getVanillaRecipes().stream().filter(recipe -> recipe instanceof Keyed).map(recipe -> ((Keyed) recipe).getKey().toString()).collect(Collectors.toList());
        recipes.addAll(customCrafting.getRecipeHandler().getAdvancedCraftingRecipes().stream().map(recipe -> recipe.getNamespacedKey().toString()).collect(Collectors.toSet()));
        StringUtil.copyPartialMatches(args[args.length - 1], recipes, results);
        return results;
    }
}
