package me.wolfyscript.customcrafting.commands;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.commands.recipes.DeleteSubCommand;
import me.wolfyscript.customcrafting.commands.recipes.EditSubCommand;
import me.wolfyscript.customcrafting.commands.recipes.SaveSubCommand;
import me.wolfyscript.customcrafting.commands.recipes.ToggleSubCommand;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.RecipeBookCluster;
import me.wolfyscript.customcrafting.utils.ChatUtils;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.gui.InventoryAPI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class CommandRecipe extends IndexCommand {

    private final CustomCrafting customCrafting;

    public CommandRecipe(CustomCrafting customCrafting) {
        super("recipes", "", "/recipes", new ArrayList<>());
        this.customCrafting = customCrafting;
        registerSubCommand(new EditSubCommand(customCrafting));
        registerSubCommand(new DeleteSubCommand(customCrafting));
        registerSubCommand(new ToggleSubCommand(customCrafting));
        registerSubCommand(new SaveSubCommand(customCrafting));
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String s, @NotNull String[] args) {
        if (args.length == 0 && sender instanceof Player) {
            WolfyUtilities api = customCrafting.getApi();
            Player p = (Player) sender;
            InventoryAPI<CCCache> invAPI = api.getInventoryAPI(CCCache.class);
            if (ChatUtils.checkPerm(p, "customcrafting.cmd.recipes")) {
                var categories = customCrafting.getDataHandler().getCategories();
                if (categories.getSortedCategories().size() > 1) {
                    invAPI.openCluster(p, RecipeBookCluster.KEY);
                } else if (!categories.getSortedCategories().isEmpty()) {
                    invAPI.getGuiHandler(p).getCustomCache().getKnowledgeBook().setCategory(categories.getCategory(0));
                    invAPI.openGui(p, RecipeBookCluster.RECIPE_BOOK);
                }
            }
        }
        return super.execute(sender, s, args);
    }
}
