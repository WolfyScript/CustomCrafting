package me.wolfyscript.customcrafting.recipes.types.grindstone;

import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.RecipePriority;
import me.wolfyscript.customcrafting.recipes.types.CustomRecipe;
import me.wolfyscript.customcrafting.recipes.types.RecipeType;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.GuiUpdateEvent;
import me.wolfyscript.utilities.api.inventory.GuiWindow;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class GrindstoneRecipe implements CustomRecipe<GrindstoneConfig> {

    private boolean exactMeta, hidden;

    private GrindstoneConfig config;
    private String id;
    private String group;

    private List<CustomItem> inputTop, inputBottom, result;
    private float xp;
    private RecipePriority priority;
    private Conditions conditions;

    public GrindstoneRecipe(GrindstoneConfig config) {
        this.result = config.getResult();
        this.id = config.getId();
        this.config = config;
        this.priority = config.getPriority();
        this.exactMeta = config.isExactMeta();
        this.inputTop = config.getInputTop();
        this.inputBottom = config.getInputBottom();
        this.conditions = config.getConditions();
        this.hidden = config.isHidden();
        this.group = config.getGroup();
        this.xp = config.getXP();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public RecipeType getRecipeType() {
        return RecipeType.GRINDSTONE;
    }

    @Override
    public String getGroup() {
        return group;
    }

    @Override
    public List<CustomItem> getCustomResults() {
        return result;
    }

    @Override
    public RecipePriority getPriority() {
        return priority;
    }

    @Override
    public GrindstoneConfig getConfig() {
        return config;
    }

    @Override
    public boolean isExactMeta() {
        return exactMeta;
    }

    @Override
    public Conditions getConditions() {
        return conditions;
    }

    public List<CustomItem> getInputTop() {
        return inputTop;
    }

    public List<CustomItem> getInputBottom() {
        return inputBottom;
    }

    @Override
    public boolean isHidden() {
        return hidden;
    }

    public float getXp() {
        return xp;
    }

    @Override
    public void renderMenu(GuiWindow guiWindow, GuiUpdateEvent event) {
        event.setButton(0, "back");
        event.setButton(11, "recipe_book", "ingredient.container_11");
        event.setButton(12, "none", "glass_green");
        event.setButton(21, "none", "glass_green");
        event.setButton(22, "recipe_book", "grindstone");
        event.setButton(23, "none", "glass_green");
        event.setButton(24, "recipe_book", "ingredient.container_24");
        event.setButton(29, "recipe_book", "ingredient.container_29");
        event.setButton(30, "none", "glass_green");

        ItemStack whiteGlass = event.getInventory().getItem(53);
        ItemMeta itemMeta = whiteGlass.getItemMeta();
        itemMeta.setCustomModelData(9008);
        whiteGlass.setItemMeta(itemMeta);
        event.setItem(53, whiteGlass);
    }
}
