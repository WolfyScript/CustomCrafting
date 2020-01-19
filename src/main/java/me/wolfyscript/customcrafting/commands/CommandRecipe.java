package me.wolfyscript.customcrafting.commands;

import com.sun.istack.internal.NotNull;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.gui.Setting;
import me.wolfyscript.customcrafting.recipes.types.CraftingRecipe;
import me.wolfyscript.customcrafting.recipes.types.CustomRecipe;
import me.wolfyscript.customcrafting.utils.ChatUtils;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.utils.chat.ClickData;
import me.wolfyscript.utilities.api.utils.chat.ClickEvent;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Recipe;
import org.bukkit.util.StringUtil;

import java.util.*;

public class CommandRecipe implements CommandExecutor, TabCompleter {

    private final List<String> COMMANDS = Arrays.asList("toggle", "edit", "delete");
    private final List<String> RECIPES = WolfyUtilities.hasVillagePillageUpdate() ? Arrays.asList("workbench", "furnace", "anvil", "blast_furnace", "smoker", "campfire", "stonecutter") : Arrays.asList("workbench", "furnace", "anvil");

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        WolfyUtilities api = CustomCrafting.getApi();
        if (args.length > 0) {
            String label = args[0];
            if (label.equalsIgnoreCase("toggle") && ChatUtils.checkPerm(sender, "customcrafting.cmd.recipes.toggle")) {
                if (args.length > 1) {
                    String id = args[1];
                    if (!id.isEmpty() && id.contains(":")) {
                        if (CustomCrafting.getRecipeHandler().getDisabledRecipes().contains(id)) {
                            sender.sendMessage("Enabled recipe " + id);
                            CustomCrafting.getRecipeHandler().getDisabledRecipes().remove(id);
                        } else {
                            sender.sendMessage("Disabled recipe " + id);
                            CustomCrafting.getRecipeHandler().getDisabledRecipes().add(id);
                            for (Player player : Bukkit.getOnlinePlayers()) {
                                player.undiscoverRecipe(new NamespacedKey(id.split(":")[0], id.split(":")[1]));
                            }
                        }
                    }
                }
            } else if ((label.equalsIgnoreCase("edit") && ChatUtils.checkPerm(sender, "customcrafting.cmd.recipes.edit")) || (label.equalsIgnoreCase("delete") && ChatUtils.checkPerm(sender, "customcrafting.cmd.recipes.delete"))) {
                if (args.length > 2) {
                    Player player = (Player) sender;
                    CustomRecipe customRecipe = CustomCrafting.getRecipeHandler().getRecipe(args[2]);
                    if (customRecipe != null) {
                        if (label.equalsIgnoreCase("edit")) {
                            Setting setting = Setting.WORKBENCH;
                            switch (args[1]) {
                                case "furnace":
                                    setting = Setting.FURNACE;
                                    break;
                                case "anvil":
                                case "blast_furnace":
                                case "smoker":
                                case "campfire":
                                case "stonecutter":
                                    setting = Setting.valueOf(args[1].toUpperCase(Locale.ROOT));
                                    break;
                            }
                            ((TestCache)api.getInventoryAPI().getGuiHandler(player).getCustomCache()).setSetting(setting);

                            if (CustomCrafting.getRecipeHandler().loadRecipeIntoCache(customRecipe, api.getInventoryAPI().getGuiHandler(player))) {
                                Bukkit.getScheduler().runTaskLater(CustomCrafting.getInst(), () -> api.getInventoryAPI().openGui(player, "none", "recipe_creator"), 1);
                            }
                        } else if (label.equalsIgnoreCase("delete")) {
                            api.sendPlayerMessage(player, "$msg.gui.recipe_editor.delete.confirm$", new String[]{"%RECIPE%", customRecipe.getId()});
                            StringBuilder command = new StringBuilder("/recipes");
                            for (int i = 0; i < args.length - 1; i++) {
                                command.append(" ").append(args[i]);
                            }
                            command.append(" ");
                            api.sendActionMessage(player, new ClickData("$msg.gui.recipe_editor.delete.confirmed$", (wolfyUtilities, player1) -> Bukkit.getScheduler().runTask(CustomCrafting.getInst(), () -> {
                                CustomCrafting.getRecipeHandler().unregisterRecipe(customRecipe);
                                if (customRecipe.getConfig().getConfigFile().delete()) {
                                    api.sendPlayerMessage(player1, "§aRecipe deleted!");
                                } else {
                                    api.sendPlayerMessage(player1, "§cCould not delete recipe!");
                                }
                            })), new ClickData("$msg.gui.recipe_editor.delete.declined$", (wolfyUtilities, player1) -> api.sendPlayerMessage(player1, "§cCancelled"), new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.SUGGEST_COMMAND, command.toString())));
                        }
                    } else {
                        api.sendPlayerMessage((Player) sender, "$msg.gui.recipe_editor.not_existing$", new String[]{"%RECIPE%", args[0] + ":" + args[1]});
                    }
                }
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String s, String[] args) {
        List<String> results = new ArrayList<>();
        if (args.length > 1) {
            if (args[0].equalsIgnoreCase("toggle")) {
                List<String> recipes = new ArrayList<>();
                for (Recipe recipe : CustomCrafting.getRecipeHandler().getVanillaRecipes()) {
                    if (recipe instanceof Keyed) {
                        recipes.add(((Keyed) recipe).getKey().toString());
                    }
                }
                for (CraftingRecipe recipe : CustomCrafting.getRecipeHandler().getAdvancedCraftingRecipes()) {
                    recipes.add(recipe.getId());
                }
                copyPartialMatches(args[args.length - 1], recipes, results);
            } else if (args[0].equalsIgnoreCase("edit") || args[0].equalsIgnoreCase("delete")) {
                if (args.length == 2) {
                    StringUtil.copyPartialMatches(args[1], RECIPES, results);
                } else if (args.length == 3) {
                    List<String> recipes = new ArrayList<>();
                    List<CustomRecipe> customRecipes = CustomCrafting.getRecipeHandler().getRecipes(args[1]);
                    for (CustomRecipe customRecipe : customRecipes) {
                        recipes.add(customRecipe.getId());
                    }
                    StringUtil.copyPartialMatches(args[2], recipes, results);
                }
            }
        } else {
            if (sender instanceof Player) {
                StringUtil.copyPartialMatches(args[0], COMMANDS, results);
            } else {
                StringUtil.copyPartialMatches(args[0], Arrays.asList("toggle", "delete"), results);
            }
        }
        Collections.sort(results);
        return results;
    }

    @NotNull
    public static <T extends Collection<? super String>> T copyPartialMatches(@NotNull String token, @NotNull Iterable<String> originals, @NotNull T collection) throws UnsupportedOperationException, IllegalArgumentException {
        Validate.notNull(token, "Search token cannot be null");
        Validate.notNull(collection, "Collection cannot be null");
        Validate.notNull(originals, "Originals cannot be null");
        Iterator var4 = originals.iterator();

        while (var4.hasNext()) {
            String string = (String) var4.next();
            if (containsIgnoreCase(string, token)) {
                collection.add(string);
            }
        }

        return collection;
    }

    public static boolean containsIgnoreCase(String string, String other) {
        return string.toLowerCase(Locale.ROOT).contains(other.toLowerCase(Locale.ROOT));
    }
}
