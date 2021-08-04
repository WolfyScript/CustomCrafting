package me.wolfyscript.customcrafting.recipes.conditions;

import me.wolfyscript.customcrafting.data.CCPlayerData;
import me.wolfyscript.customcrafting.recipes.ICustomRecipe;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.customcrafting.utils.PlayerUtil;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.entity.Player;

public class CraftLimitCondition extends Condition {

    public static final NamespacedKey KEY = new NamespacedKey(NamespacedKeyUtils.NAMESPACE, "craft_limit");

    private long limit = 0;

    public CraftLimitCondition() {
        super(KEY);
        setAvailableOptions(Conditions.Option.IGNORE, Conditions.Option.LOWER_EXACT, Conditions.Option.LOWER);
    }

    @Override
    public boolean check(ICustomRecipe<?> recipe, Conditions.Data data) {
        Player player = data.getPlayer();
        if (player != null) {
            CCPlayerData playerStore = PlayerUtil.getStore(player);
            if (playerStore != null) {
                long amount = playerStore.getRecipeCrafts(recipe.getNamespacedKey());
                return switch (option) {
                    case LOWER_EXACT -> amount <= limit;
                    case LOWER -> amount < limit;
                    default -> false;
                };
            }
        }
        return true;
    }

    public long getLimit() {
        return limit;
    }

    public void setLimit(long limit) {
        this.limit = limit;
    }
}
