package me.wolfyscript.customcrafting.recipes.conditions;

import me.wolfyscript.customcrafting.recipes.Condition;
import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.types.CustomCookingRecipe;
import me.wolfyscript.customcrafting.recipes.types.CustomRecipe;
import me.wolfyscript.customcrafting.recipes.types.CraftingRecipe;
import me.wolfyscript.utilities.api.WolfyUtilities;
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
            //Deprecated because of the problems with columns in the permission String!

            return WolfyUtilities.hasPermission(player, "customcrafting.craft." + recipe.getId().split(":")[0] + "." + recipe.getId().split(":")[1]) || player.hasPermission("customcrafting.craft." + recipe.getId());
        }
        return true;
    }
}
