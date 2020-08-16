package me.wolfyscript.customcrafting.commands.cc_subcommands;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.commands.AbstractSubCommand;
import me.wolfyscript.customcrafting.handlers.InventoryHandler;
import me.wolfyscript.customcrafting.handlers.RecipeHandler;
import me.wolfyscript.customcrafting.recipes.types.ICustomVanillaRecipe;
import me.wolfyscript.customcrafting.utils.ChatUtils;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.InventoryAPI;
import me.wolfyscript.utilities.api.language.LanguageAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReloadSubCommand extends AbstractSubCommand {

    public ReloadSubCommand(CustomCrafting customCrafting) {
        super("reload", new ArrayList<>(), customCrafting);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull String var3, @NotNull String[] var4) {
        WolfyUtilities api = CustomCrafting.getApi();
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (ChatUtils.checkPerm(p, "customcrafting.cmd.reload")) {
                api.sendPlayerMessage(p, "&eReloading GUIs and Recipes!");

                if (WolfyUtilities.hasBuzzyBeesUpdate()) {
                    InventoryAPI<?> invAPI = CustomCrafting.getApi().getInventoryAPI();
                    LanguageAPI langAPI = CustomCrafting.getApi().getLanguageAPI();
                    invAPI.reset();
                    langAPI.unregisterLanguages();
                    customCrafting.getConfigHandler().getConfig().save();
                    try {
                        customCrafting.getConfigHandler().loadLang();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    new InventoryHandler(customCrafting).init();

                    //Reload Recipes
                    RecipeHandler recipeHandler = customCrafting.getRecipeHandler();
                    recipeHandler.getRecipes().forEach((namespacedKey, iCustomRecipe) -> {
                        if (iCustomRecipe instanceof ICustomVanillaRecipe) {
                            recipeHandler.unregisterVanillaRecipe(iCustomRecipe.getNamespacedKey());
                            Bukkit.addRecipe(((ICustomVanillaRecipe<?>) iCustomRecipe).getVanillaRecipe());
                        }
                    });
                    CustomCrafting.getApi().sendPlayerMessage(p, "§aReload Complete");
                    return true;
                }
                api.sendPlayerMessage(p, "&cThis command is only available in 1.15+");
            }
        }
        return true;
    }

    @Override
    protected @Nullable List<String> onTabComplete(@NotNull CommandSender var1, @NotNull String var3, @NotNull String[] var4) {
        return null;
    }
}
