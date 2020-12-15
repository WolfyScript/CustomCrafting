package me.wolfyscript.customcrafting.placeholderapi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.PlayerStatistics;
import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.types.CraftingRecipe;
import me.wolfyscript.customcrafting.recipes.types.ICraftingRecipe;
import me.wolfyscript.customcrafting.recipes.types.ICustomRecipe;
import me.wolfyscript.customcrafting.recipes.types.furnace.CustomFurnaceRecipe;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class PlaceHolder extends PlaceholderExpansion {

    private final CustomCrafting customCrafting;

    public PlaceHolder(CustomCrafting customCrafting) {
        this.customCrafting = customCrafting;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    /**
     * The placeholder identifier should go here.
     * <br>This is what tells PlaceholderAPI to call our onRequest
     * method to obtain a value if a placeholder starts with our
     * identifier.
     * <br>This must be unique and can not contain % or _
     *
     * @return The identifier in {@code %<identifier>_<value>%} as String.
     */
    @Override
    public String getIdentifier() {
        return "ccrafting";
    }

    @Override
    public String getAuthor() {
        return "WolfyScript";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public String onRequest(OfflinePlayer p, String params) {
        if (p != null) {
            PlayerStatistics cache = CustomCrafting.getPlayerStatistics(p.getUniqueId());
            if (params.contains(";")) {
                //Params with %ccrafting_<option>;<recipe_id>%
                String recipeID = params.split(";")[1];
                ICustomRecipe recipe = customCrafting.getRecipeHandler().getRecipe(recipeID);
                String option = params.split(";")[0];
                switch (option) {
                    case "type":
                        if (recipe instanceof ICraftingRecipe) {
                            return "workbench";
                        }
                        if (recipe instanceof CustomFurnaceRecipe) {
                            return "furnace";
                        }
                        break;
                    case "crafts":
                        if (cache == null)
                            break;
                        return String.valueOf(cache.getRecipeCrafts(recipeID));
                    case "workbench":
                        if (recipe instanceof CraftingRecipe) {
                            return String.valueOf((recipe).getConditions().getByID("advanced_workbench").getOption().equals(Conditions.Option.EXACT));
                        }
                        break;
                    case "permission":
                        if (recipe instanceof CraftingRecipe) {
                            return String.valueOf((recipe).getConditions().getByID("permission").getOption().equals(Conditions.Option.EXACT));
                        }
                        break;
                    case "has_perm":
                        if (recipe instanceof CraftingRecipe) {
                            if (p.isOnline()) {
                                Player player = Bukkit.getPlayer(p.getUniqueId());
                                return String.valueOf(CustomCrafting.getApi().getPermissions().hasPermission(player, "customcrafting.craft." + recipeID));
                            }
                        }
                }
            } else {
                //Doesn't contain recipe ID!
                switch (params) {
                    case "crafts":
                        if (cache == null)
                            break;
                        return String.valueOf(cache.getAmountCrafted());
                    case "total":
                        return String.valueOf(customCrafting.getRecipeHandler().getVanillaRecipes().size());
                    case "total_custom":
                        return String.valueOf(customCrafting.getRecipeHandler().getRecipes().size());
                    case "available":
                        if (p.isOnline()) {
                            Player player = Bukkit.getPlayer(p.getUniqueId());
                            return String.valueOf(customCrafting.getRecipeHandler().getRecipes().keySet().stream().filter(namespacedKey -> CustomCrafting.getApi().getPermissions().hasPermission(player, "customcrafting.craft." + namespacedKey.getNamespace() + "." + namespacedKey.getKey())).count());
                        }
                        break;
                }

            }
        }
        return null;
    }
}
