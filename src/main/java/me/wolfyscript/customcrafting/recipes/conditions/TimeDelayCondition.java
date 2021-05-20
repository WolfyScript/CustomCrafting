package me.wolfyscript.customcrafting.recipes.conditions;

import me.wolfyscript.customcrafting.recipes.Condition;
import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.types.ICustomRecipe;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.core.JsonGenerator;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class TimeDelayCondition extends Condition {

    private final HashMap<UUID, Long> playerCraftTimeMap = new HashMap<>();
    long delay = 0;

    public TimeDelayCondition() {
        super("time_delay");
        setOption(Conditions.Option.IGNORE);
        setAvailableOptions(Conditions.Option.IGNORE, Conditions.Option.EXACT, Conditions.Option.LOWER, Conditions.Option.LOWER_EXACT, Conditions.Option.HIGHER, Conditions.Option.HIGHER_EXACT, Conditions.Option.HIGHER_LOWER);
    }

    @Override
    public boolean check(ICustomRecipe<?, ?> recipe, Conditions.Data data) {
        Player player = data.getPlayer();
        if (player != null) {
            boolean valid = checkDelay(System.currentTimeMillis() - playerCraftTimeMap.getOrDefault(player.getUniqueId(), 0L));
            if (valid) {
                playerCraftTimeMap.remove(player.getUniqueId());
            }
            return valid;
        }
        return true;
    }

    private boolean checkDelay(long timeSinceLastCraft) {
        switch (option) {
            case IGNORE:
                return true;
            case EXACT:
                return timeSinceLastCraft >= delay;
            default:
                return false;
        }
    }

    @Override
    public void readFromJson(JsonNode node) {
        this.delay = node.get("delay").asInt();
    }

    @Override
    public void writeJson(@NotNull JsonGenerator gen) throws IOException {
        gen.writeNumberField("delay", delay);
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }
}
