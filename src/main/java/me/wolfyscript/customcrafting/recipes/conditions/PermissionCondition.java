package me.wolfyscript.customcrafting.recipes.conditions;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.Condition;
import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.types.CustomCookingRecipe;
import me.wolfyscript.customcrafting.recipes.types.ICustomRecipe;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.core.JsonGenerator;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

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
    public boolean check(ICustomRecipe<?> recipe, Conditions.Data data) {
        if (recipe instanceof CustomCookingRecipe && data.getPlayer() == null) {
            return true;
        }
        Player player = data.getPlayer();
        if (option.equals(Conditions.Option.IGNORE)) return true;
        if (player == null) return false;
        String permissionString = permission.replace("%namespace%", recipe.getNamespacedKey().getNamespace()).replace("%recipe_name%", recipe.getNamespacedKey().getKey());
        return CustomCrafting.getApi().getPermissions().hasPermission(player, permissionString);
    }

    @Override
    public void writeJson(@NotNull JsonGenerator gen) throws IOException {
        super.writeJson(gen);
        gen.writeStringField("permission", permission);
    }

    @Override
    public void readFromJson(JsonNode node) {
        this.permission = node.get("permission").asText();
    }
}
