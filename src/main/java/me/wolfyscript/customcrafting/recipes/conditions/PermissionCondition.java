package me.wolfyscript.customcrafting.recipes.conditions;

import me.wolfyscript.customcrafting.recipes.Condition;
import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.types.CustomCookingRecipe;
import me.wolfyscript.customcrafting.recipes.types.CustomRecipe;
import me.wolfyscript.customcrafting.recipes.types.CraftingRecipe;
import org.bukkit.entity.Player;

public class PermissionCondition extends Condition {

    public PermissionCondition() {
        super("permission");
        setOption(Conditions.Option.EXACT);
        setAvailableOptions(Conditions.Option.EXACT, Conditions.Option.IGNORE);
    }

    @Override
    public boolean check(CustomRecipe recipe, Conditions.Data data) {
        if (recipe instanceof CustomCookingRecipe) {
            return true;
        } else if (recipe instanceof CraftingRecipe) {
            Player player = data.getPlayer();
            if (option.equals(Conditions.Option.IGNORE)) {
                return true;
            }
            String perm3 = "customcrafting.craft." + recipe.getId().split(":")[0] + "." + recipe.getId().split(":")[1];
            String perm2 = "customcrafting.craft." + recipe.getId().split(":")[0];
            //Deprecated because of the problems with columns in the permission String!
            @Deprecated String perm = "customcrafting.craft." + recipe.getId();

            return player.hasPermission("customcrafting.craft.*") || player.hasPermission(perm) || player.hasPermission(perm2) || player.hasPermission(perm3);
        }
        return true;
    }
}
