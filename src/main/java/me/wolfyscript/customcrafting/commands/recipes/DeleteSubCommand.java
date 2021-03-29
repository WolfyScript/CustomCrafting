package me.wolfyscript.customcrafting.commands.recipes;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.Registry;
import me.wolfyscript.customcrafting.commands.AbstractSubCommand;
import me.wolfyscript.customcrafting.recipes.types.ICustomRecipe;
import me.wolfyscript.customcrafting.utils.ChatUtils;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.chat.ClickData;
import me.wolfyscript.utilities.api.chat.ClickEvent;
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
import java.util.stream.Collectors;

public class DeleteSubCommand extends AbstractSubCommand {

    public DeleteSubCommand(CustomCrafting customCrafting) {
        super("delete", new ArrayList<>(), customCrafting);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull String var3, @NotNull String[] args) {
        if (sender instanceof Player) {
            WolfyUtilities api = customCrafting.getApi();
            Player player = (Player) sender;
            if (ChatUtils.checkPerm(sender, "customcrafting.cmd.recipes.delete")) {
                if (args.length > 0) {
                    ICustomRecipe<?, ?> customRecipe = Registry.RECIPES.get(new NamespacedKey(args[0].split(":")[0], args[0].split(":")[1]));
                    if (customRecipe != null) {
                        api.getChat().sendMessage(player, "$msg.gui.recipe_editor.delete.confirm$", new Pair<>("%RECIPE%", customRecipe.getNamespacedKey().toString()));
                        api.getChat().sendActionMessage(player, new ClickData("$msg.gui.recipe_editor.delete.confirmed$", (wolfyUtilities, player1) -> Bukkit.getScheduler().runTask(customCrafting, () -> customRecipe.delete(player))), new ClickData("$msg.gui.recipe_editor.delete.declined$", (wolfyUtilities, player1) -> api.getChat().sendMessage(player1, "§cCancelled"), new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/recipes delete ")));
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
        List<String> recipes = Registry.RECIPES.keySet().stream().map(NamespacedKey::toString).collect(Collectors.toList());
        StringUtil.copyPartialMatches(args[args.length - 1], recipes, results);
        return results;
    }
}
