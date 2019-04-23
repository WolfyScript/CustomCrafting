package me.wolfyscript.customcrafting.placeholderapi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.PlayerCache;
import me.wolfyscript.customcrafting.recipes.workbench.CraftingRecipe;
import me.wolfyscript.customcrafting.recipes.CustomRecipe;
import me.wolfyscript.customcrafting.recipes.furnace.FurnaceCRecipe;
import me.wolfyscript.utilities.api.WolfyUtilities;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class PlaceHolder extends PlaceholderExpansion {

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
        if(p != null){
            PlayerCache cache = CustomCrafting.getPlayerCache(p.getUniqueId());
            if(params.contains(";")){
                //Params with %ccrafting_<option>;<recipe_id>%
                String recipeID = params.split(";")[1];
                CustomRecipe recipe = CustomCrafting.getRecipeHandler().getRecipe(recipeID);
                String option = params.split(";")[0];
                switch (option){
                    case "type":
                        if(recipe instanceof CraftingRecipe){
                            return "workbench";
                        }
                        if(recipe instanceof FurnaceCRecipe){
                            return "furnace";
                        }
                        break;
                    case "crafts":
                        if(cache == null)
                            break;
                        return String.valueOf(cache.getRecipeCrafts(recipeID));
                    case "workbench":
                        if(recipe instanceof CraftingRecipe){
                            return String.valueOf(((CraftingRecipe) recipe).needsAdvancedWorkbench());
                        }
                        break;
                    case "permission":
                        if(recipe instanceof CraftingRecipe){
                            return String.valueOf(((CraftingRecipe) recipe).needsPermission());
                        }
                        break;
                    case "has_perm":
                        if(recipe instanceof  CraftingRecipe){
                            if(p.isOnline()){
                                Player player = Bukkit.getPlayer(p.getUniqueId());
                                return String.valueOf(WolfyUtilities.hasPermission(player, "customcrafting.craft."+recipeID));
                            }
                        }
                }
            }else{
                //Doesn't contain recipe ID!
                switch (params){
                    case "crafts":
                        if(cache == null)
                            break;
                        return String.valueOf(cache.getAmountCrafted());
                    case "total":
                        return String.valueOf(CustomCrafting.getRecipeHandler().getAllRecipes().size());
                    case "total_custom":
                        return String.valueOf(CustomCrafting.getRecipeHandler().getRecipes().size());
                    case "available":
                        if(p.isOnline()){
                            int i = 0;
                            Player player = Bukkit.getPlayer(p.getUniqueId());
                            for(CustomRecipe recipe : CustomCrafting.getRecipeHandler().getRecipes()){
                                if(WolfyUtilities.hasPermission(player, "customcrafting.craft."+recipe.getID())){
                                    i++;
                                }
                            }
                            return String.valueOf(i);
                        }
                        break;
                }

            }
        }
        return null;
    }
}
