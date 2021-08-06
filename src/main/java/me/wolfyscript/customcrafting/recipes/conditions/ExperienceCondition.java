package me.wolfyscript.customcrafting.recipes.conditions;

import me.wolfyscript.customcrafting.recipes.ICustomRecipe;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ChatInputButton;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.annotation.JsonIgnore;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.annotation.JsonProperty;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.Material;

import java.util.List;

public class ExperienceCondition extends Condition<ExperienceCondition> {

    public static final NamespacedKey KEY = new NamespacedKey(NamespacedKeyUtils.NAMESPACE, "player_experience");

    @JsonProperty("experience")
    private int expLevel = 0;

    public ExperienceCondition() {
        super(KEY);
        setAvailableOptions(Conditions.Option.EXACT, Conditions.Option.LOWER, Conditions.Option.LOWER_EXACT, Conditions.Option.HIGHER, Conditions.Option.HIGHER_EXACT, Conditions.Option.HIGHER_LOWER);
    }

    @Override
    public boolean isApplicable(ICustomRecipe<?> recipe) {
        return switch (recipe.getRecipeType().getType()) {
            case WORKBENCH, ELITE_WORKBENCH, BREWING_STAND, GRINDSTONE -> true;
            default -> false;
        };
    }

    @Override
    public boolean check(ICustomRecipe<?> recipe, Conditions.Data data) {
        if (data.getPlayer() != null) {
            int currentExp = data.getPlayer().getLevel();
            return switch (option) {
                case EXACT -> currentExp == expLevel;
                case LOWER -> currentExp < expLevel;
                case LOWER_EXACT -> currentExp <= expLevel;
                case HIGHER -> currentExp > expLevel;
                case HIGHER_EXACT -> currentExp >= expLevel;
                case HIGHER_LOWER -> currentExp < expLevel || currentExp > expLevel;
                default -> true;
            };
        }
        return true;
    }

    @JsonIgnore
    public float getExpLevel() {
        return expLevel;
    }

    @JsonIgnore
    public void setExpLevel(int expLevel) {
        this.expLevel = expLevel;
    }

    public static class GUIComponent extends FunctionalGUIComponent<ExperienceCondition> {

        public GUIComponent() {
            super(Material.EXPERIENCE_BOTTLE, "Player Experience", List.of(""),
                    (menu, api) -> {
                        menu.registerButton(new ChatInputButton<>("conditions.player_experience", Material.CLOCK, (hashMap, cache, guiHandler, player, guiInventory, itemStack, i, b) -> {
                            hashMap.put("%VALUE%", cache.getRecipe().getConditions().getByType(ExperienceCondition.class).getExpLevel());
                            return itemStack;
                        }, (guiHandler, player, s, strings) -> {
                            try {
                                int value = Integer.parseInt(s);
                                guiHandler.getCustomCache().getRecipe().getConditions().getByType(ExperienceCondition.class).setExpLevel(value);
                            } catch (NumberFormatException ex) {
                                api.getChat().sendKey(player, "recipe_creator", "valid_number");
                            }
                            return false;
                        }));
                    },
                    (update, cache, condition, recipe) -> {


                    });
        }
    }
}
