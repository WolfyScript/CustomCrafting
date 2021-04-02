package me.wolfyscript.customcrafting.commands.cc_subcommands;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.commands.AbstractSubCommand;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.handlers.DataHandler;
import me.wolfyscript.customcrafting.utils.ChatUtils;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.InventoryAPI;
import me.wolfyscript.utilities.util.version.MinecraftVersions;
import me.wolfyscript.utilities.util.version.ServerVersion;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ReloadSubCommand extends AbstractSubCommand {

    public ReloadSubCommand(CustomCrafting customCrafting) {
        super("reload", new ArrayList<>(), customCrafting);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull String var3, @NotNull String[] var4) {
        WolfyUtilities api = customCrafting.getApi();
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (ChatUtils.checkPerm(p, "customcrafting.cmd.reload")) {
                api.getChat().sendMessage(p, "&eReloading Items and Recipes!");
                if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_15)) {
                    InventoryAPI<CCCache> invAPI = api.getInventoryAPI(CCCache.class);
                    Bukkit.getOnlinePlayers().forEach(player -> {
                        GuiHandler<CCCache> guiHandler = invAPI.getGuiHandler(player);
                        guiHandler.getCustomCache().getKnowledgeBook().setCachedSubFolderRecipes(new HashMap<>());
                        guiHandler.getCustomCache().getKnowledgeBook().setResearchItems(new ArrayList<>());
                    });
                    //Reload Recipes
                    DataHandler dataHandler = customCrafting.getDataHandler();
                    dataHandler.saveData();
                    dataHandler.load(false);
                    dataHandler.getCategories().indexCategories();
                    api.getChat().sendMessage(p, "&aReload Complete");
                    return true;
                }
                api.getChat().sendMessage(p, "&cThis command is only available in 1.15+");
            }
        }
        return true;
    }

    @Override
    protected @Nullable List<String> onTabComplete(@NotNull CommandSender var1, @NotNull String var3, @NotNull String[] var4) {
        return null;
    }
}
