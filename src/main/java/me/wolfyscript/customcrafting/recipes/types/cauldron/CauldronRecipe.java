package me.wolfyscript.customcrafting.recipes.types.cauldron;

import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.recipebook.buttons.IngredientContainerButton;
import me.wolfyscript.customcrafting.recipes.Condition;
import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.RecipeType;
import me.wolfyscript.customcrafting.recipes.Types;
import me.wolfyscript.customcrafting.recipes.types.CustomRecipe;
import me.wolfyscript.customcrafting.utils.ItemLoader;
import me.wolfyscript.customcrafting.utils.recipe_item.Ingredient;
import me.wolfyscript.customcrafting.utils.recipe_item.Result;
import me.wolfyscript.customcrafting.utils.recipe_item.target.SlotResultTarget;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.GuiWindow;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.core.JsonGenerator;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.SerializerProvider;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.Material;
import org.bukkit.entity.Item;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CauldronRecipe extends CustomRecipe<CauldronRecipe, SlotResultTarget> {

    private int cookingTime;
    private int waterLevel;
    private float xp;
    private CustomItem handItem;
    private Ingredient ingredients;
    private boolean dropItems;
    private boolean needsFire;
    private boolean needsWater;

    public CauldronRecipe(NamespacedKey namespacedKey, JsonNode node) {
        super(namespacedKey, node);
        this.xp = node.path("exp").floatValue();
        this.cookingTime = node.path("cookingTime").asInt(60);
        this.waterLevel = node.path("waterLevel").asInt(1);
        this.needsWater = node.path("water").asBoolean(true);
        this.needsFire = node.path("fire").asBoolean(true);
        {
            JsonNode dropNode = node.path("dropItems");
            this.dropItems = dropNode.path("enabled").asBoolean();
            this.handItem = ItemLoader.load(dropNode.path("handItem"));
        }
        this.ingredients = ItemLoader.loadIngredient(node.path("ingredients"));
    }

    public CauldronRecipe() {
        super();
        this.result = new Result<>();
        this.ingredients = new Ingredient();
        this.dropItems = true;
        this.xp = 0;
        this.cookingTime = 80;
        this.needsFire = false;
        this.waterLevel = 0;
        this.needsWater = true;
        this.handItem = new CustomItem(Material.AIR);
    }

    public CauldronRecipe(CauldronRecipe cauldronRecipe) {
        super(cauldronRecipe);
        this.result = cauldronRecipe.getResult();
        this.ingredients = cauldronRecipe.getIngredients();
        this.dropItems = cauldronRecipe.dropItems();
        this.xp = cauldronRecipe.getXp();
        this.cookingTime = cauldronRecipe.getCookingTime();
        this.needsFire = cauldronRecipe.needsFire();
        this.waterLevel = cauldronRecipe.getWaterLevel();
        this.needsWater = cauldronRecipe.needsWater();
        this.handItem = cauldronRecipe.getHandItem();
    }

    public int getCookingTime() {
        return cookingTime;
    }

    public void setCookingTime(int cookingTime) {
        this.cookingTime = cookingTime;
    }

    public boolean needsFire() {
        return needsFire;
    }

    public void setNeedsFire(boolean needsFire) {
        this.needsFire = needsFire;
    }

    public int getWaterLevel() {
        return waterLevel;
    }

    public void setWaterLevel(int waterLevel) {
        this.waterLevel = waterLevel;
    }

    public boolean needsWater() {
        return needsWater;
    }

    public void setNeedsWater(boolean needsWater) {
        this.needsWater = needsWater;
    }

    public float getXp() {
        return xp;
    }

    public void setXp(float xp) {
        this.xp = xp;
    }

    public Ingredient getIngredients() {
        return ingredients;
    }

    public void setIngredients(Ingredient ingredients) {
        this.ingredients = ingredients;
    }

    public boolean dropItems() {
        return dropItems;
    }

    public void setDropItems(boolean dropItems) {
        this.dropItems = dropItems;
    }

    public List<Item> checkRecipe(List<Item> items) {
        List<Item> validItems = new ArrayList<>();
        for (CustomItem customItem : getIngredients().getChoices()) {
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

    public CustomItem getHandItem() {
        return handItem;
    }

    public void setHandItem(CustomItem handItem) {
        this.handItem = handItem;
    }

    @Override
    public RecipeType<CauldronRecipe> getRecipeType() {
        return Types.CAULDRON;
    }

    @Override
    public CauldronRecipe clone() {
        return new CauldronRecipe(this);
    }

    @Override
    public void writeToJson(JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        super.writeToJson(gen, serializerProvider);
        gen.writeObjectFieldStart("dropItems");
        gen.writeBooleanField("enabled", dropItems);
        gen.writeObjectField("handItem", handItem.getApiReference());
        gen.writeEndObject();
        gen.writeNumberField("exp", xp);
        gen.writeNumberField("cookingTime", cookingTime);
        gen.writeNumberField("waterLevel", waterLevel);
        gen.writeBooleanField("water", needsWater);
        gen.writeBooleanField("fire", needsFire);
        gen.writeObjectField("result", this.result);
        gen.writeObjectField("ingredients", ingredients);
    }

    @Override
    public void prepareMenu(GuiHandler<CCCache> guiHandler, GuiCluster<CCCache> cluster) {
        Ingredient ingredients = getIngredients();
        int invSlot;
        for (int i = 0; i < 6; i++) {
            invSlot = 10 + i + (i / 3) * 6;
            if (i < ingredients.size()) {
                ((IngredientContainerButton) cluster.getButton("ingredient.container_" + invSlot)).setVariants(guiHandler, Collections.singletonList(ingredients.getChoices().get(i)));
            } else {
                ((IngredientContainerButton) cluster.getButton("ingredient.container_" + invSlot)).setVariants(guiHandler, Collections.singletonList(new CustomItem(Material.AIR)));
            }
        }
        ((IngredientContainerButton) cluster.getButton("ingredient.container_25")).setVariants(guiHandler, getResult());
    }

    @Override
    public void renderMenu(GuiWindow<CCCache> guiWindow, GuiUpdate<CCCache> event) {
        int invSlot;
        for (int i = 0; i < 6; i++) {
            invSlot = 10 + i + (i / 3) * 6;
            event.setButton(invSlot, new NamespacedKey("recipe_book", "ingredient.container_" + invSlot));
        }
        List<Condition> conditions = getConditions().values().stream().filter(condition -> !condition.getOption().equals(Conditions.Option.IGNORE) && !condition.getId().equals("permission")).collect(Collectors.toList());
        int startSlot = 9 / (conditions.size() + 1);
        int slot = 0;
        for (Condition condition : conditions) {
            if (!condition.getOption().equals(Conditions.Option.IGNORE)) {
                event.setButton(36 + startSlot + slot, new NamespacedKey("recipe_book", "conditions." + condition.getId()));
                slot += 2;
            }
        }
        event.setButton(23, new NamespacedKey("recipe_book", needsWater() ? "cauldron.water.enabled" : "cauldron.water.disabled"));
        event.setButton(32, new NamespacedKey("recipe_book", needsFire() ? "cauldron.fire.enabled" : "cauldron.fire.disabled"));
        event.setButton(25, new NamespacedKey("recipe_book", "ingredient.container_25"));
    }
}
