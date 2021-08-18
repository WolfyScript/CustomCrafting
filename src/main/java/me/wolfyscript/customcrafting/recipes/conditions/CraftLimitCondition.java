package me.wolfyscript.customcrafting.recipes.conditions;

import me.wolfyscript.customcrafting.data.CCPlayerData;
import me.wolfyscript.customcrafting.recipes.ICraftingRecipe;
import me.wolfyscript.customcrafting.recipes.ICustomRecipe;
import me.wolfyscript.customcrafting.recipes.RecipeType;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.customcrafting.utils.PlayerUtil;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ChatInputButton;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

public class CraftLimitCondition extends Condition<CraftLimitCondition> {

    public static final NamespacedKey KEY = new NamespacedKey(NamespacedKeyUtils.NAMESPACE, "craft_limit");

    private long limit = 0;

    public CraftLimitCondition() {
        super(KEY);
        setAvailableOptions(Conditions.Option.LOWER_EXACT, Conditions.Option.LOWER);
    }

    @Override
    public boolean isApplicable(ICustomRecipe<?> recipe) {
        return recipe instanceof ICraftingRecipe;
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

    public static class GUIComponent extends FunctionalGUIComponent<CraftLimitCondition> {

        public GUIComponent() {
            super(Material.BARRIER, getLangKey(KEY.getKey(), "name"), List.of(getLangKey(KEY.getKey(), "description")),
                    (menu, api) -> {
                        menu.registerButton(new ChatInputButton<>("conditions.craft_limit", Material.BARRIER, (hashMap, cache, guiHandler, player, guiInventory, itemStack, i, b) -> {
                            hashMap.put("%VALUE%", cache.getRecipeCreatorCache().getRecipeCache().getConditions().getByType(CraftLimitCondition.class).getLimit());
                            return itemStack;
                        }, (guiHandler, player, s, strings) -> {
                            var conditions = guiHandler.getCustomCache().getRecipeCreatorCache().getRecipeCache().getConditions();
                            try {
                                conditions.getByType(CraftLimitCondition.class).setLimit(Long.parseLong(s));
                            } catch (NumberFormatException ex) {
                                api.getChat().sendKey(player, "recipe_creator", "valid_number");
                            }
                            return false;
                        }));
                    },
                    (update, cache, condition, recipe) -> {

                    });
        }

        @Override
        public boolean shouldRender(RecipeType<?> type) {
            return type instanceof ICraftingRecipe;
        }
    }
}
