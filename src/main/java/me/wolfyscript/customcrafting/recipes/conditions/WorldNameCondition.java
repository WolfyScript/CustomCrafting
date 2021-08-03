package me.wolfyscript.customcrafting.recipes.conditions;

import me.wolfyscript.customcrafting.recipes.ICustomRecipe;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.annotation.JsonProperty;
import me.wolfyscript.utilities.util.NamespacedKey;

import java.util.ArrayList;
import java.util.List;

public class WorldNameCondition extends Condition {

    @JsonProperty("names")
    private final List<String> worldNames;

    public WorldNameCondition() {
        super(new NamespacedKey(NamespacedKeyUtils.NAMESPACE, "world_name"));
        setOption(Conditions.Option.IGNORE);
        setAvailableOptions(Conditions.Option.IGNORE, Conditions.Option.EXACT);
        this.worldNames = new ArrayList<>();
    }

    @Override
    public boolean check(ICustomRecipe<?> recipe, Conditions.Data data) {
        if (option.equals(Conditions.Option.IGNORE)) {
            return true;
        }
        if (data.getBlock() != null) {
            return worldNames.contains(data.getBlock().getLocation().getWorld().getName());
        }
        return false;
    }

    public void addWorldName(String worldName) {
        this.worldNames.add(worldName);
    }

    public List<String> getWorldNames() {
        return worldNames;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder(option.toString());
        stringBuilder.append(";");
        for (String eliteWorkbench : worldNames) {
            stringBuilder.append(eliteWorkbench).append(",");
        }
        return stringBuilder.toString();
    }
}
