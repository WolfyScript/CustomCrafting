package me.wolfyscript.customcrafting.recipes.conditions;

import me.wolfyscript.customcrafting.recipes.Condition;
import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.types.CraftingRecipe;
import me.wolfyscript.customcrafting.recipes.types.CustomCookingRecipe;
import me.wolfyscript.customcrafting.recipes.types.CustomRecipe;
import me.wolfyscript.utilities.api.WolfyUtilities;
import org.bukkit.entity.Player;

public class PermissionCondition extends Condition {

    private String permission = "customcrafting.craft.%namespace%.%recipe_name%";

    public PermissionCondition() {
        super("permission");
        setOption(Conditions.Option.EXACT);
        setAvailableOptions(Conditions.Option.EXACT, Conditions.Option.IGNORE);
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
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
            String permissionString = permission.replace("&namespace&", recipe.getId().split(":")[0]).replace("%recipe_name%", recipe.getId().split(":")[1]);
            return WolfyUtilities.hasPermission(player, permissionString);
        }
        return true;
    }

    @Override
    public String toString() {
        return option.toString() + ";" + permission;
    }

    @Override
    public void fromString(String value) {
        String[] args = value.split(";");
        this.option = Conditions.Option.valueOf(args[0]);
        if (args.length > 1) {
            this.permission = args[1];
        }
    }
}
