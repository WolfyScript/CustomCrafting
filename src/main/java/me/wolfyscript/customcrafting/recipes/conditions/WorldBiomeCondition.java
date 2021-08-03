package me.wolfyscript.customcrafting.recipes.conditions;

import me.wolfyscript.customcrafting.recipes.ICustomRecipe;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.utilities.util.NamespacedKey;

import java.util.ArrayList;
import java.util.List;

public class WorldBiomeCondition extends Condition {

    private final List<String> biomes;

    public WorldBiomeCondition() {
        super(new NamespacedKey(NamespacedKeyUtils.NAMESPACE, "world_biome"));
        setOption(Conditions.Option.IGNORE);
        setAvailableOptions(Conditions.Option.IGNORE, Conditions.Option.EXACT);
        this.biomes = new ArrayList<>();
    }

    @Override
    public boolean check(ICustomRecipe<?> recipe, Conditions.Data data) {
        if (option.equals(Conditions.Option.IGNORE)) {
            return true;
        }
        if (data.getBlock() != null) {
            return biomes.contains(data.getBlock().getBiome().toString());
        }
        return false;
    }

    public void addBiome(String biome) {
        this.biomes.add(biome);
    }

    public List<String> getBiomes() {
        return biomes;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder(option.toString());
        stringBuilder.append(";");
        for (String eliteWorkbench : biomes) {
            stringBuilder.append(eliteWorkbench).append(",");
        }
        return stringBuilder.toString();
    }
}
