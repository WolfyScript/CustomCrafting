package me.wolfyscript.customcrafting.recipes.types.cauldron;

import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.gui.recipebook.buttons.IngredientContainerButton;
import me.wolfyscript.customcrafting.recipes.Condition;
import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.types.CustomRecipe;
import me.wolfyscript.customcrafting.recipes.types.RecipeType;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.custom_items.api_references.APIReference;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.GuiWindow;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.core.JsonGenerator;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.SerializerProvider;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.util.Vector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CauldronRecipe extends CustomRecipe<CauldronRecipe> {

    private int cookingTime;
    private int waterLevel;
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
            this.handItem = new CustomItem(mapper.convertValue(dropNode.path("handItem"), APIReference.class));
        }
        {
            List<CustomItem> ingredients = new ArrayList<>();
            JsonNode ingredientNode = node.path("ingredients");
            ingredientNode.elements().forEachRemaining(n -> ingredients.add(new CustomItem(mapper.convertValue(n, APIReference.class))));
            setIngredients(ingredients.stream().filter(customItem -> !ItemUtils.isAirOrNull(customItem)).collect(Collectors.toList()));
        }
        if(result == null){
            this.result = new ArrayList<>();
        }
        {
            JsonNode mythicMobNode = node.path("mythicMob");
            this.mythicMobName = mythicMobNode.path("name").asText();
            this.mythicMobLevel = mythicMobNode.path("level").asInt();
            Vector vector = new Vector(0, 0, 0);
            vector.setX(mythicMobNode.path("modX").asDouble());
            vector.setY(mythicMobNode.path("modY").asDouble());
            vector.setZ(mythicMobNode.path("modZ").asDouble());
            this.mythicMobMod = vector;
        }
    }

    public CauldronRecipe() {
        super();
        this.result = new ArrayList<>();
        this.ingredients = new ArrayList<>();
        this.dropItems = true;
        this.xp = 0;
        this.cookingTime = 80;
        this.needsFire = false;
        this.waterLevel = 0;
        this.needsWater = true;
        this.handItem = new CustomItem(Material.AIR);
        this.mythicMobLevel = 0;
        this.mythicMobMod = new Vector();
        this.mythicMobName = "";
    }

    public CauldronRecipe(CauldronRecipe cauldronRecipe) {
        super(cauldronRecipe);
        this.result = cauldronRecipe.getResults();
        this.ingredients = cauldronRecipe.getIngredients();
        this.dropItems = cauldronRecipe.dropItems();
        this.xp = cauldronRecipe.getXp();
        this.cookingTime = cauldronRecipe.getCookingTime();
        this.needsFire = cauldronRecipe.needsFire();
        this.waterLevel = cauldronRecipe.getWaterLevel();
        this.needsWater = cauldronRecipe.needsWater();
        this.handItem = cauldronRecipe.getHandItem();
        this.mythicMobName = cauldronRecipe.getMythicMobName();
        this.mythicMobLevel = cauldronRecipe.getMythicMobLevel();
        this.mythicMobMod = cauldronRecipe.getMythicMobMod();
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

    public List<CustomItem> getIngredients() {
        return new ArrayList<>(ingredients);
    }

    public void setIngredients(List<CustomItem> ingredients) {
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

    public CustomItem getHandItem() {
        return handItem;
    }

    public void setHandItem(CustomItem handItem) {
        this.handItem = handItem;
    }

    public String getMythicMobName() {
        return mythicMobName;
    }

    public void setMythicMobName(String mythicMobName) {
        this.mythicMobName = mythicMobName;
    }

    public int getMythicMobLevel() {
        return mythicMobLevel;
    }

    public void setMythicMobLevel(int mythicMobLevel) {
        this.mythicMobLevel = mythicMobLevel;
    }

    public Vector getMythicMobMod() {
        return mythicMobMod;
    }

    public void setMythicMobMod(Vector mythicMobMod) {
        this.mythicMobMod = mythicMobMod;
    }

    @Override
    public RecipeType<CauldronRecipe> getRecipeType() {
        return RecipeType.CAULDRON;
    }

    @Override
    public List<CustomItem> getResults() {
        return new ArrayList<>(this.result);
    }

    @Override
    public void setResult(List<CustomItem> result) {
        this.result = result;
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
        {
            gen.writeArrayFieldStart("result");
            for (CustomItem customItem : getResults()) {
                gen.writeObject(customItem.getApiReference());
            }
            gen.writeEndArray();
        }
        {
            gen.writeArrayFieldStart("ingredients");
            for (CustomItem customItem : getIngredients()) {
                gen.writeObject(customItem.getApiReference());
            }
            gen.writeEndArray();
        }
        gen.writeObjectFieldStart("mythicMob");
        gen.writeStringField("name", mythicMobName);
        gen.writeNumberField("level", mythicMobLevel);
        gen.writeNumberField("modX", mythicMobMod.getX());
        gen.writeNumberField("modY", mythicMobMod.getY());
        gen.writeNumberField("modZ", mythicMobMod.getZ());
        gen.writeEndObject();
    }

    @Override
    public void prepareMenu(GuiHandler<TestCache> guiHandler, GuiCluster<TestCache> cluster) {
        List<CustomItem> ingredients = getIngredients();
        int invSlot;
        for (int i = 0; i < 6; i++) {
            invSlot = 10 + i + (i / 3) * 6;
            if (i < ingredients.size()) {
                ((IngredientContainerButton) cluster.getButton("ingredient.container_" + invSlot)).setVariants(guiHandler, Collections.singletonList(ingredients.get(i)));
            } else {
                ((IngredientContainerButton) cluster.getButton("ingredient.container_" + invSlot)).setVariants(guiHandler, Collections.singletonList(new CustomItem(Material.AIR)));
            }
        }
        ((IngredientContainerButton) cluster.getButton("ingredient.container_25")).setVariants(guiHandler, Collections.singletonList(getResult()));
    }

    @Override
    public void renderMenu(GuiWindow<TestCache> guiWindow, GuiUpdate<TestCache> event) {
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

    public void setMythicMob(String name, int level, double modX, double modY, double modZ) {
        this.mythicMobName = name;
        this.mythicMobLevel = level;
        this.mythicMobMod = new Vector(modX, modY, modZ);
    }
}
