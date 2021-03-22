package me.wolfyscript.customcrafting.commands.recipes;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.Registry;
import me.wolfyscript.customcrafting.commands.AbstractSubCommand;
import me.wolfyscript.customcrafting.recipes.types.CraftingRecipe;
import me.wolfyscript.customcrafting.utils.ChatUtils;
import me.wolfyscript.customcrafting.utils.ItemLoader;
import me.wolfyscript.utilities.api.WolfyUtilities;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
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
                Registry.CUSTOM_ITEMS.entrySet().forEach(entry -> {
                    api.getChat().sendConsoleMessage("Saving item: " + entry.getKey().toString());
                    ItemLoader.saveItem(entry.getKey(), entry.getValue());
                });
                Registry.RECIPES.values().forEach(recipe -> {
                    api.getChat().sendConsoleMessage("Saving recipe: " + recipe.getNamespacedKey().toString());
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
        List<String> recipes = customCrafting.getRecipeHandler().getBukkitNamespacedKeys();
        recipes.addAll(Registry.RECIPES.get(CraftingRecipe.class).stream().map(recipe -> recipe.getNamespacedKey().toString()).collect(Collectors.toSet()));
        return StringUtil.copyPartialMatches(args[args.length - 1], recipes, new ArrayList<>());
    }
}
