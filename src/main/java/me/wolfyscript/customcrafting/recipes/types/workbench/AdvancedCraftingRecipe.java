package me.wolfyscript.customcrafting.recipes.types.workbench;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.PlayerStatistics;
import me.wolfyscript.customcrafting.recipes.Condition;
import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.RecipePriority;
import me.wolfyscript.customcrafting.recipes.types.CraftingRecipe;
import me.wolfyscript.customcrafting.recipes.types.RecipeType;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.GuiUpdateEvent;
import me.wolfyscript.utilities.api.inventory.GuiWindow;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class AdvancedCraftingRecipe implements CraftingRecipe<AdvancedCraftConfig> {

    private boolean exactMeta, hidden;
    private RecipePriority priority;
    private Conditions conditions;

    private AdvancedCraftConfig config;
    private String id;
    private String group;
    private List<CustomItem> result;
    private Map<Character, List<CustomItem>> ingredients;
    private WolfyUtilities api;

    public AdvancedCraftingRecipe(AdvancedCraftConfig config) {
        this.result = config.getResult();
        this.id = config.getId();
        this.config = config;
        this.ingredients = config.getIngredients();
        this.group = config.getGroup();
        this.priority = config.getPriority();
        this.api = CustomCrafting.getApi();
        this.exactMeta = config.isExactMeta();
        this.conditions = config.getConditions();
        this.hidden = config.isHidden();
    }

    public void setIngredients(Map<Character, List<CustomItem>> ingredients) {
        this.ingredients = ingredients;
    }

    @Override
    public Map<Character, List<CustomItem>> getIngredients() {
        return ingredients;
    }

    @Override
    public List<CustomItem> getIngredients(int slot) {
        return getIngredients().getOrDefault(LETTERS[slot], new ArrayList<>());
    }

    @Override
    public CustomItem getIngredient(int slot) {
        List<CustomItem> list = getIngredients(slot);
        return list.size() > 0 ? list.get(0) : null;
    }

    @Override
    public void setResult(List<CustomItem> result) {
        this.result = result;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    @Override
    public List<CustomItem> getCustomResults() {
        return result;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public RecipeType getRecipeType() {
        return RecipeType.WORKBENCH;
    }

    public AdvancedCraftConfig getConfig() {
        return config;
    }

    @Override
    public String getGroup() {
        return group;
    }

    @Override
    public RecipePriority getPriority() {
        return priority;
    }

    @Override
    public boolean isExactMeta() {
        return exactMeta;
    }

    @Override
    public Conditions getConditions() {
        return conditions;
    }

    @Override
    public boolean isHidden() {
        return hidden;
    }

    @Override
    public void renderMenu(GuiWindow guiWindow, GuiUpdateEvent event) {
        PlayerStatistics playerStatistics = CustomCrafting.getPlayerStatistics(event.getPlayer());
        event.setButton(0, "back");
        if (!getIngredients().isEmpty()) {
            if (getConditions().getByID("advanced_workbench").getOption().equals(Conditions.Option.EXACT)) {
                for (int i = 1; i < 9; i++) {
                    event.setButton(i, "none", "glass_purple");
                }
                for (int i = 36; i < 45; i++) {
                    event.setButton(i, "none", "glass_purple");
                }
            }
            if (getConditions().getByID("permission").getOption().equals(Conditions.Option.EXACT)) {
                //TODO display for admins
            }
            List<Condition> conditions = getConditions().values().stream().filter(condition -> !condition.getOption().equals(Conditions.Option.IGNORE) && !condition.getId().equals("permission")).collect(Collectors.toList());
            int startSlot = 9 / (conditions.size() + 1);
            int slot = 0;
            for (Condition condition : conditions) {
                if (!condition.getOption().equals(Conditions.Option.IGNORE)) {
                    event.setButton(36 + startSlot + slot, "recipe_book", "conditions." + condition.getId());
                    slot += 2;
                }
            }
            event.setButton(23, "recipe_book", isShapeless() ? "workbench.shapeless_on" : "workbench.shapeless_off");
            int invSlot;
            for (int i = 0; i < 9; i++) {
                invSlot = 10 + i + (i / 3) * 6;
                event.setButton(invSlot, "recipe_book", "ingredient.container_" + invSlot);
            }
            event.setButton(25, "recipe_book", "ingredient.container_25");
        }
    }
}
