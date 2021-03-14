package me.wolfyscript.customcrafting.recipes.types;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.RecipePriority;
import me.wolfyscript.customcrafting.utils.ItemLoader;
import me.wolfyscript.customcrafting.utils.recipe_item.Result;
import me.wolfyscript.customcrafting.utils.recipe_item.target.ResultTarget;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.custom_items.references.WolfyUtilitiesRef;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.core.JsonGenerator;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.ObjectMapper;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.SerializerProvider;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.json.jackson.JacksonUtil;

import java.io.IOException;

public abstract class CustomRecipe<C extends CustomRecipe<?, ?>, T extends ResultTarget> implements ICustomRecipe<C, T> {

    protected NamespacedKey namespacedKey;
    protected boolean exactMeta, hidden;

    protected RecipePriority priority;
    protected Conditions conditions;
    protected String group;
    protected WolfyUtilities api;
    protected ObjectMapper mapper;
    protected Result<T> result;

    public CustomRecipe(NamespacedKey namespacedKey, JsonNode node) {
        this.mapper = JacksonUtil.getObjectMapper();
        this.api = CustomCrafting.getApi();
        this.namespacedKey = namespacedKey;
        //Get fields from JsonNode
        this.group = node.path("group").asText("");
        this.priority = RecipePriority.valueOf(node.path("priority").asText("NORMAL"));
        this.exactMeta = node.path("exactItemMeta").asBoolean(true);
        this.conditions = mapper.convertValue(node.path("conditions"), Conditions.class);
        if (this.conditions == null) {
            this.conditions = new Conditions();
        }
        this.hidden = node.path("hidden").asBoolean(false);

        //Sets the result of the recipe if one exists in the config
        if (node.has("result")) {
            setResult(ItemLoader.loadResult(node.path("result")));
        }
    }

    public CustomRecipe() {
        this.mapper = JacksonUtil.getObjectMapper();
        this.api = CustomCrafting.getApi();
        this.namespacedKey = null;

        this.group = "";
        this.priority = RecipePriority.NORMAL;
        this.exactMeta = true;
        this.conditions = new Conditions();
        this.hidden = false;
    }

    public CustomRecipe(CustomRecipe<?, ?> craftingRecipe) {
        this.mapper = JacksonUtil.getObjectMapper();
        this.api = CustomCrafting.getApi();
        this.namespacedKey = craftingRecipe.getNamespacedKey();

        this.group = craftingRecipe.getGroup();
        this.priority = craftingRecipe.getPriority();
        this.exactMeta = craftingRecipe.isExactMeta();
        this.conditions = craftingRecipe.getConditions();
        this.hidden = craftingRecipe.isHidden();
    }

    @Override
    public WolfyUtilities getAPI() {
        return api;
    }

    @Override
    public boolean hasNamespacedKey() {
        return namespacedKey != null;
    }

    @Override
    public NamespacedKey getNamespacedKey() {
        return namespacedKey;
    }

    @Override
    public void setNamespacedKey(NamespacedKey namespacedKey) {
        this.namespacedKey = namespacedKey;
    }

    @Override
    public RecipePriority getPriority() {
        return priority;
    }

    @Override
    public void setPriority(RecipePriority priority) {
        this.priority = priority;
    }

    @Override
    public boolean isExactMeta() {
        return exactMeta;
    }

    @Override
    public void setExactMeta(boolean exactMeta) {
        this.exactMeta = exactMeta;
    }

    @Override
    public boolean isHidden() {
        return hidden;
    }

    @Override
    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    @Override
    public String getGroup() {
        return group;
    }

    @Override
    public void setGroup(String group) {
        this.group = group;
    }

    @Override
    public Conditions getConditions() {
        return conditions;
    }

    @Override
    public void setConditions(Conditions conditions) {
        this.conditions = conditions;
    }

    @Override
    public Result<T> getResult() {
        return result;
    }

    @Override
    public void setResult(Result<T> result) {
        this.result = result;
    }

    @Override
    abstract public C clone();

    @Override
    public void writeToJson(JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        gen.writeStringField("group", group);
        gen.writeBooleanField("hidden", hidden);
        gen.writeStringField("priority", priority.toString());
        gen.writeBooleanField("exactItemMeta", exactMeta);
        gen.writeObjectField("conditions", conditions);
    }

    protected void saveCustomItem(CustomItem customItem, JsonGenerator gen) throws IOException {
        if (customItem.hasNamespacedKey()) {
            gen.writeObject(new WolfyUtilitiesRef(customItem.getNamespacedKey()));
        } else {
            gen.writeObject(customItem.getApiReference());
        }
    }
}
