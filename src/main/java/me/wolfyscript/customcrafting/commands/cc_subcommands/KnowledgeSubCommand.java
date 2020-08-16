package me.wolfyscript.customcrafting.commands.cc_subcommands;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.commands.AbstractSubCommand;
import me.wolfyscript.customcrafting.configs.recipebook.Categories;
import me.wolfyscript.customcrafting.utils.ChatUtils;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.InventoryAPI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class KnowledgeSubCommand extends AbstractSubCommand {

    public KnowledgeSubCommand(CustomCrafting customCrafting) {
        super("knowledge", new ArrayList<>(), customCrafting);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull String var3, @NotNull String[] var4) {
        if (sender instanceof Player) {
            WolfyUtilities api = CustomCrafting.getApi();
            Player p = (Player) sender;
            InventoryAPI invAPI = api.getInventoryAPI();
            //TODO Check if main categories exist. if not directly open the recipe list else the main menu
            if (ChatUtils.checkPerm(p, "customcrafting.cmd.knowledge")) {
                Categories categories = customCrafting.getRecipeHandler().getCategories();
                if (categories.getSortedMainCategories().size() > 1) {
                    invAPI.openCluster(p, "recipe_book");
                } else {
                    invAPI.openGui(p, "recipe_book", "recipe_book");
                }
            }
        }
        return true;
    }

    @Override
    protected @Nullable List<String> onTabComplete(@NotNull CommandSender var1, @NotNull String var3, @NotNull String[] var4) {
        return null;
    }
}
