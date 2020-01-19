package me.wolfyscript.customcrafting.recipes.conditions;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.wolfyscript.customcrafting.recipes.Condition;
import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.types.CustomCookingRecipe;
import me.wolfyscript.customcrafting.recipes.types.CustomRecipe;
import me.wolfyscript.utilities.api.WolfyUtilities;
import org.bukkit.entity.Player;

public class PermissionCondition extends Condition {

    private String permission = "customcrafting.craft.%namespace%.%recipe_name%";

    public PermissionCondition() {
        super("permission");
        setOption(Conditions.Option.IGNORE);
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
        if (recipe instanceof CustomCookingRecipe && data.getPlayer() == null) {
            return true;
        }
        Player player = data.getPlayer();
        if (option.equals(Conditions.Option.IGNORE)) {
            return true;
        }
        String permissionString = permission.replace("%namespace%", recipe.getId().split(":")[0]).replace("%recipe_name%", recipe.getId().split(":")[1]);
        return WolfyUtilities.hasPermission(player, permissionString);
    }

    @Override
    public JsonElement toJsonElement() {
        JsonObject jsonObject = (JsonObject) super.toJsonElement();
        jsonObject.addProperty("permission", permission);
        return jsonObject;
    }

    @Override
    public void fromJsonElement(JsonElement jsonElement) {
        this.permission = ((JsonObject) jsonElement).getAsJsonPrimitive("permission").getAsString();
    }
}
