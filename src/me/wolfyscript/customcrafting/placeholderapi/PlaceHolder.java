package me.wolfyscript.customcrafting.placeholderapi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.PlayerCache;
import me.wolfyscript.customcrafting.recipes.CraftingRecipe;
import me.wolfyscript.customcrafting.recipes.CustomRecipe;
import me.wolfyscript.customcrafting.recipes.FurnaceCRecipe;
import org.bukkit.OfflinePlayer;

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
            if(params.contains(":")){

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
                        //TODO RECIPE TRACKER
                        break;
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
                }
            }else{
                //Doesn't contain recipe ID!
                switch (params){
                    case "crafts":
                        return String.valueOf(cache.getAmountCrafted());
                    case "total":
                        return String.valueOf(CustomCrafting.getRecipeHandler().getAllRecipes().size());
                    case "total_custom":
                        return String.valueOf(CustomCrafting.getRecipeHandler().getRecipes().size());
                    case "available":
                        int i = 0;
                        for(CustomRecipe recipe : CustomCrafting.getRecipeHandler().getRecipes()){
                            //TODO PERMISSION CHECK!
                        }
                        break;
                }

            }


            if(cache != null){
                if(params.equals("amount_crafted")){

                }
                if(params.equals("amount_advanced_crafted")){
                    return String.valueOf(cache.getAmountAdvancedCrafted());
                }
                if(params.equals("amount_normal_crafted")){
                    return String.valueOf(cache.getAmountNormalCrafted());
                }
                //SPACE FOR MORE PLACEHOLDERS

            }
        }
        return null;
    }
}
