package me.wolfyscript.customcrafting.recipes.types.cauldron;

import me.wolfyscript.customcrafting.recipes.Condition;
import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.RecipePriority;
import me.wolfyscript.customcrafting.recipes.types.CustomRecipe;
import me.wolfyscript.customcrafting.recipes.types.RecipeType;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.GuiUpdateEvent;
import me.wolfyscript.utilities.api.inventory.GuiWindow;
import org.bukkit.entity.Item;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CauldronRecipe implements CustomRecipe<CauldronConfig> {

    private boolean exactMeta, hidden;
    private String group;
    private Conditions conditions;

    private int cookingTime;
    private int waterLevel;
    private RecipePriority priority;
    private float xp;
    private CustomItem handItem;
    private List<CustomItem> result;
    private List<CustomItem> ingredients;
    private boolean dropItems;
    private boolean needsFire;
    private boolean needsWater;

    private String mythicMobName;
    private int mythicMobLevel;
    private Vector mythicMobMod;

    private String id;
    private CauldronConfig config;

    public CauldronRecipe(CauldronConfig config) {
        this.id = config.getId();
        this.config = config;
        this.result = config.getResult();
        this.ingredients = config.getIngredients();
        this.dropItems = config.dropItems();
        this.priority = config.getPriority();
        this.exactMeta = config.isExactMeta();
        this.group = config.getGroup();
        this.xp = config.getXP();
        this.cookingTime = config.getCookingTime();
        this.needsFire = config.needsFire();
        this.conditions = config.getConditions();
        this.waterLevel = config.getWaterLevel();
        this.needsWater = config.needsWater();
        this.handItem = config.getHandItem();
        this.mythicMobLevel = config.getMythicMobLevel();
        this.mythicMobMod = config.getMythicMobMod();
        this.mythicMobName = config.getMythicMobName();
        this.hidden = config.isHidden();
    }

    @Override
    public boolean isExactMeta() {
        return exactMeta;
    }

    public void setExactMeta(boolean exactMeta) {
        this.exactMeta = exactMeta;
    }

    public int getCookingTime() {
        return cookingTime;
    }

    public boolean needsFire() {
        return needsFire;
    }

    public int getWaterLevel() {
        return waterLevel;
    }

    public boolean needsWater() {
        return needsWater;
    }

    public float getXp() {
        return xp;
    }

    @Override
    public RecipePriority getPriority() {
        return priority;
    }

    public void setPriority(RecipePriority priority) {
        this.priority = priority;
    }

    @Override
    public List<CustomItem> getCustomResults() {
        return result;
    }

    public void setResult(List<CustomItem> result) {
        this.result = result;
    }

    public List<CustomItem> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<CustomItem> ingredients) {
        this.ingredients = ingredients;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getGroup() {
        return group;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public CauldronConfig getConfig() {
        return config;
    }

    public void setConfig(CauldronConfig config) {
        this.config = config;
    }

    public boolean dropItems() {
        return dropItems;
    }

    public void setDropItems(boolean dropItems) {
        this.dropItems = dropItems;
    }

    public List<Item> checkRecipe(List<Item> items) {
        List<Item> validItems = new ArrayList<>();
        for (CustomItem customItem : getIngredients()) {
            for (Item item : items) {
                if (customItem.isSimilar(item.getItemStack(), isExactMeta()) && customItem.getAmount() == item.getItemStack().getAmount()) {
                    validItems.add(item);
                    break;
                }
            }
        }
        if (validItems.size() >= ingredients.size()) {
            return validItems;
        }
        return null;
    }

    @Override
    public Conditions getConditions() {
        return conditions;
    }

    public CustomItem getHandItem() {
        return handItem;
    }

    public String getMythicMobName() {
        return mythicMobName;
    }

    public int getMythicMobLevel() {
        return mythicMobLevel;
    }

    public Vector getMythicMobMod() {
        return mythicMobMod;
    }

    @Override
    public boolean isHidden() {
        return hidden;
    }

    @Override
    public RecipeType getRecipeType() {
        return RecipeType.CAULDRON;
    }

    @Override
    public void renderMenu(GuiWindow guiWindow, GuiUpdateEvent event) {
        event.setButton(0, "back");
        int invSlot;
        for (int i = 0; i < 6; i++) {
            invSlot = 10 + i + (i / 3) * 6;
            event.setButton(invSlot, "recipe_book", "ingredient.container_" + invSlot);
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
        event.setButton(23, "recipe_book", needsWater() ? "cauldron.water.enabled" : "cauldron.water.disabled");
        event.setButton(32, "recipe_book", needsFire() ? "cauldron.fire.enabled" : "cauldron.fire.disabled");
        event.setButton(25, "recipe_book", "ingredient.container_25");
    }
}
