package me.wolfyscript.customcrafting.commands.recipes;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.Registry;
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
import java.util.stream.Collectors;

public class EditSubCommand extends AbstractSubCommand {

    public EditSubCommand(CustomCrafting customCrafting) {
        super("edit", new ArrayList<>(), customCrafting);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull String var3, @NotNull String[] args) {
        if (sender instanceof Player player && ChatUtils.checkPerm(player, "customcrafting.cmd.recipes.edit") && args.length > 0) {
            WolfyUtilities api = customCrafting.getApi();
            NamespacedKey key = NamespacedKey.of(args[0]);
            if (key != null) {
                ICustomRecipe<?, ?> customRecipe = Registry.RECIPES.get(key);
                if (customRecipe != null) {
                    GuiHandler<CCCache> guiHandler = api.getInventoryAPI(CCCache.class).getGuiHandler(player);
                    guiHandler.getCustomCache().setRecipeType(customRecipe.getRecipeType());
                    guiHandler.getCustomCache().setSetting(Setting.RECIPE_CREATOR);
                    if (customCrafting.getDataHandler().loadRecipeIntoCache(customRecipe, guiHandler)) {
                        Bukkit.getScheduler().runTaskLater(customCrafting, () -> api.getInventoryAPI().openGui(player, new NamespacedKey("recipe_creator", guiHandler.getCustomCache().getRecipeType().getCreatorID())), 1);
                    }
                } else {
                    api.getChat().sendMessage((Player) sender, "$msg.gui.recipe_editor.not_existing$", new Pair<>("%RECIPE%", args[0] + ":" + args[1]));
                }
            }
        }
        return true;
    }

    @Override
    protected @Nullable
    List<String> onTabComplete(@NotNull CommandSender var1, @NotNull String var3, @NotNull String[] args) {
        List<String> results = new ArrayList<>();
        StringUtil.copyPartialMatches(args[args.length - 1], Registry.RECIPES.keySet().stream().map(NamespacedKey::toString).collect(Collectors.toList()), results);
        return results;
    }
}
