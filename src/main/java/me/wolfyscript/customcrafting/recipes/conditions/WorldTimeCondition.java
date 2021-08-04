package me.wolfyscript.customcrafting.recipes.conditions;

import me.wolfyscript.customcrafting.recipes.ICustomRecipe;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.utilities.util.NamespacedKey;

public class WorldTimeCondition extends Condition {

    public static final NamespacedKey KEY = new NamespacedKey(NamespacedKeyUtils.NAMESPACE, "world_time");

    private long time = 0;

    public WorldTimeCondition() {
        super(KEY);
        setAvailableOptions(Conditions.Option.IGNORE, Conditions.Option.EXACT, Conditions.Option.LOWER, Conditions.Option.LOWER_EXACT, Conditions.Option.HIGHER, Conditions.Option.HIGHER_EXACT, Conditions.Option.HIGHER_LOWER);
    }

    @Override
    public boolean check(ICustomRecipe<?> recipe, Conditions.Data data) {
        long currentTime = data.getBlock().getWorld().getTime();
        return switch (option) {
            case EXACT -> currentTime == time;
            case LOWER -> currentTime < time;
            case LOWER_EXACT -> currentTime <= time;
            case HIGHER -> currentTime > time;
            case HIGHER_EXACT -> currentTime >= time;
            case HIGHER_LOWER -> currentTime < time || currentTime > time;
            default -> true;
        };
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getTime() {
        return time;
    }
}
