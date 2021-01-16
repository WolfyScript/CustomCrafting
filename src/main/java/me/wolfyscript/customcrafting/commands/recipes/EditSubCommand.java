package me.wolfyscript.customcrafting.commands.recipes;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.commands.AbstractSubCommand;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.Setting;
import me.wolfyscript.customcrafting.recipes.types.ICustomRecipe;
import me.wolfyscript.customcrafting.utils.ChatUtils;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.Pair;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class EditSubCommand extends AbstractSubCommand {

    public EditSubCommand(CustomCrafting customCrafting) {
        super("edit", new ArrayList<>(), customCrafting);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull String var3, @NotNull String[] args) {
        if (sender instanceof Player) {
            WolfyUtilities api = CustomCrafting.getApi();
            Player player = (Player) sender;
            if (ChatUtils.checkPerm(sender, "customcrafting.cmd.recipes.edit")) {
                if (args.length > 0) {
                    ICustomRecipe<?> customRecipe = customCrafting.getRecipeHandler().getRecipe(new NamespacedKey(args[0].split(":")[0], args[0].split(":")[1]));
                    if (customRecipe != null) {
                        GuiHandler<CCCache> guiHandler = api.getInventoryAPI(CCCache.class).getGuiHandler(player);
                        guiHandler.getCustomCache().setSetting(Setting.valueOf(customRecipe.getRecipeType().toString().toUpperCase(Locale.ROOT)));
                        if (customCrafting.getRecipeHandler().loadRecipeIntoCache(customRecipe, guiHandler)) {
                            Bukkit.getScheduler().runTaskLater(customCrafting, () -> api.getInventoryAPI().openGui(player, new NamespacedKey("none", "recipe_creator")), 1);
                        }
                    } else {
                        api.getChat().sendMessage((Player) sender, "$msg.gui.recipe_editor.not_existing$", new Pair<>("%RECIPE%", args[0] + ":" + args[1]));
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
        List<String> recipes = customCrafting.getRecipeHandler().getRecipes().keySet().stream().map(NamespacedKey::toString).collect(Collectors.toList());
        StringUtil.copyPartialMatches(args[args.length - 1], recipes, results);
        return results;
    }
}
