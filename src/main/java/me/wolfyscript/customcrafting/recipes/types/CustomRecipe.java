package me.wolfyscript.customcrafting.recipes.types;

import com.google.common.base.Preconditions;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.RecipePriority;
import me.wolfyscript.customcrafting.utils.ItemLoader;
import me.wolfyscript.customcrafting.utils.recipe_item.Result;
import me.wolfyscript.customcrafting.utils.recipe_item.target.ResultTarget;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.nms.network.MCByteBuf;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.core.JsonGenerator;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.ObjectMapper;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.SerializerProvider;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.json.jackson.JacksonUtil;

import java.io.IOException;
import java.util.Objects;

public abstract class CustomRecipe<C extends ICustomRecipe<C, T>, T extends ResultTarget> implements ICustomRecipe<C, T> {

    protected static final String KEY_RESULT = "result";
    protected static final String KEY_GROUP = "group";
    protected static final String KEY_PRIORITY = "priority";
    protected static final String KEY_EXACT_META = "exactItemMeta";
    protected static final String KEY_CONDITIONS = "conditions";
    protected static final String KEY_HIDDEN = "hidden";

    protected NamespacedKey namespacedKey;
    protected boolean exactMeta;
    protected boolean hidden;

    protected RecipePriority priority;
    protected Conditions conditions;
    protected String group;
    protected final WolfyUtilities api;
    protected final ObjectMapper mapper;
    protected Result<T> result;

    protected CustomRecipe(NamespacedKey namespacedKey, JsonNode node) {
        this.namespacedKey = Objects.requireNonNull(namespacedKey, "Not a valid key! The key cannot be null!");
        this.mapper = JacksonUtil.getObjectMapper();
        this.api = CustomCrafting.inst().getApi();
        //Get fields from JsonNode
        this.group = node.path(KEY_GROUP).asText("");
        this.priority = mapper.convertValue(node.path(KEY_PRIORITY).asText("NORMAL"), RecipePriority.class);
        this.exactMeta = node.path(KEY_EXACT_META).asBoolean(true);
        this.conditions = mapper.convertValue(node.path(KEY_CONDITIONS), Conditions.class);
        if (this.conditions == null) {
            this.conditions = new Conditions();
        }
        this.hidden = node.path(KEY_HIDDEN).asBoolean(false);

        //Sets the result of the recipe if one exists in the config
        if (node.has(KEY_RESULT) && !(this instanceof CustomStonecutterRecipe)) {
            setResult(ItemLoader.loadResult(node.path(KEY_RESULT)));
        }
    }

    protected CustomRecipe(NamespacedKey namespacedKey, boolean exactMeta, boolean hidden, String group, RecipePriority priority, Conditions conditions, Result<T> result) {
        this.mapper = JacksonUtil.getObjectMapper();
        this.api = CustomCrafting.inst().getApi();
        this.namespacedKey = Objects.requireNonNull(namespacedKey, "Not a valid key! The key cannot be null!");
        this.exactMeta = exactMeta;
        this.hidden = hidden;
        this.group = group;
        this.priority = priority;
        this.conditions = conditions;
        this.result = result;
    }

    protected CustomRecipe() {
        this.mapper = JacksonUtil.getObjectMapper();
        this.api = CustomCrafting.inst().getApi();
        this.namespacedKey = null;
        this.result = new Result<>();

        this.group = "";
        this.priority = RecipePriority.NORMAL;
        this.exactMeta = true;
        this.conditions = new Conditions();
        this.hidden = false;
    }

    /**
     * Used to copy the fields of another instance to this one.
     * The ResultTarget must be the same in order to copy it.
     *
     * @param customRecipe The other CustomRecipe. Can be from another type, but must have the same ResultTarget.
     */
    protected CustomRecipe(CustomRecipe<?, T> customRecipe) {
        this.mapper = JacksonUtil.getObjectMapper();
        this.api = CustomCrafting.inst().getApi();
        this.namespacedKey = customRecipe.namespacedKey;

        this.group = customRecipe.group;
        this.priority = customRecipe.priority;
        this.exactMeta = customRecipe.exactMeta;
        this.conditions = customRecipe.conditions;
        this.hidden = customRecipe.hidden;
        this.result = customRecipe.result.clone();
    }

    @Override
    public abstract C clone();

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
        Preconditions.checkArgument(!result.isEmpty(), "Invalid result! Recipe must have a non-air result!");
    }

    @Override
    public void writeToJson(JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        gen.writeStringField(KEY_GROUP, group);
        gen.writeBooleanField(KEY_HIDDEN, hidden);
        gen.writeStringField(KEY_PRIORITY, priority.toString());
        gen.writeBooleanField(KEY_EXACT_META, exactMeta);
        gen.writeObjectField(KEY_CONDITIONS, conditions);
    }

    @Override
    public void writeToBuf(MCByteBuf byteBuf) {
        byteBuf.writeUtf(getRecipeType().name());
        byteBuf.writeUtf(namespacedKey.toString());
        byteBuf.writeBoolean(exactMeta);
        byteBuf.writeUtf(group);
        byteBuf.writeVarInt(result.getChoices().size());
        for (CustomItem choice : result.getChoices()) {
            byteBuf.writeItemStack(choice.create());
        }
    }
}
