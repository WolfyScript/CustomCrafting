package me.wolfyscript.customcrafting.commands.recipes;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.commands.AbstractSubCommand;
import me.wolfyscript.customcrafting.recipes.types.ICustomRecipe;
import me.wolfyscript.customcrafting.utils.ChatUtils;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.utils.NamespacedKey;
import me.wolfyscript.utilities.api.utils.chat.ClickData;
import me.wolfyscript.utilities.api.utils.chat.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DeleteSubCommand extends AbstractSubCommand {

    public DeleteSubCommand(CustomCrafting customCrafting) {
        super("delete", new ArrayList<>(), customCrafting);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull String var3, @NotNull String[] args) {
        if (sender instanceof Player) {
            WolfyUtilities api = CustomCrafting.getApi();
            Player player = (Player) sender;
            if (ChatUtils.checkPerm(sender, "customcrafting.cmd.recipes.delete")) {
                if (args.length > 0) {
                    ICustomRecipe customRecipe = customCrafting.getRecipeHandler().getRecipe(new NamespacedKey(args[0].split(":")[0], args[0].split(":")[1]));
                    if (customRecipe != null) {
                        api.sendPlayerMessage(player, "$msg.gui.recipe_editor.delete.confirm$", new String[]{"%RECIPE%", customRecipe.getNamespacedKey().toString()});
                        api.sendActionMessage(player, new ClickData("$msg.gui.recipe_editor.delete.confirmed$", (wolfyUtilities, player1) -> Bukkit.getScheduler().runTask(customCrafting, () -> customRecipe.delete(player))), new ClickData("$msg.gui.recipe_editor.delete.declined$", (wolfyUtilities, player1) -> api.sendPlayerMessage(player1, "§cCancelled"), new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/recipes delete ")));
                    } else {
                        api.sendPlayerMessage((Player) sender, "$msg.gui.recipe_editor.not_existing$", new String[]{"%RECIPE%", args[0] + ":" + args[1]});
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
