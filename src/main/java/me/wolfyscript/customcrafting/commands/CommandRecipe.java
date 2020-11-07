package me.wolfyscript.customcrafting.commands;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.commands.recipes.DeleteSubCommand;
import me.wolfyscript.customcrafting.commands.recipes.EditSubCommand;
import me.wolfyscript.customcrafting.commands.recipes.SaveSubCommand;
import me.wolfyscript.customcrafting.commands.recipes.ToggleSubCommand;
import me.wolfyscript.customcrafting.configs.recipebook.Categories;
import me.wolfyscript.customcrafting.utils.ChatUtils;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.InventoryAPI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandRecipe extends IndexCommand {

    private final List<String> COMMANDS = Arrays.asList("toggle", "edit", "delete", "save");

    private final CustomCrafting customCrafting;

    public CommandRecipe(CustomCrafting customCrafting) {
        super("recipes", "", "/recipes <setting>", new ArrayList<>());
        this.customCrafting = customCrafting;
        registerSubCommand(new EditSubCommand(customCrafting));
        registerSubCommand(new DeleteSubCommand(customCrafting));
        registerSubCommand(new ToggleSubCommand(customCrafting));
        registerSubCommand(new SaveSubCommand(customCrafting));
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String s, @NotNull String[] args) {
        if (args.length == 0 && sender instanceof Player) {
            WolfyUtilities api = CustomCrafting.getApi();
            Player p = (Player) sender;
            InventoryAPI<?> invAPI = api.getInventoryAPI();
            if (ChatUtils.checkPerm(p, "customcrafting.cmd.recipes")) {
                Categories categories = customCrafting.getRecipeHandler().getCategories();
                if (categories.getSortedMainCategories().size() > 1) {
                    invAPI.openCluster(p, "recipe_book");
                } else {
                    invAPI.openGui(p, "recipe_book", "recipe_book");
                }
            }
        }
        return super.execute(sender, s, args);
    }

    @NotNull
    @Override
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] strings) throws IllegalArgumentException {
        return super.tabComplete(sender, alias, strings);
    }
}
