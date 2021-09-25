package me.wolfyscript.customcrafting.recipes;

import com.google.common.base.Preconditions;
import me.wolfyscript.customcrafting.CCRegistry;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.handlers.ResourceLoader;
import me.wolfyscript.customcrafting.recipes.conditions.Conditions;
import me.wolfyscript.customcrafting.recipes.items.Ingredient;
import me.wolfyscript.customcrafting.recipes.items.Result;
import me.wolfyscript.customcrafting.utils.ItemLoader;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.GuiWindow;
import me.wolfyscript.utilities.api.nms.network.MCByteBuf;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.core.JsonGenerator;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.core.type.TypeReference;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.ObjectMapper;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.SerializerProvider;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.annotation.JsonSerialize;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.ser.std.StdSerializer;
import me.wolfyscript.utilities.util.Keyed;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.json.jackson.JacksonUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@JsonSerialize(using = CustomRecipe.Serializer.class)
public abstract class CustomRecipe<C extends CustomRecipe<C>> implements Keyed {

    protected static final String KEY_RESULT = "result";
    protected static final String KEY_GROUP = "group";
    protected static final String KEY_PRIORITY = "priority";
    protected static final String KEY_EXACT_META = "exactItemMeta";
    protected static final String KEY_CONDITIONS = "conditions";
    protected static final String KEY_HIDDEN = "hidden";
    protected static final String ERROR_MSG_KEY = "Not a valid key! The key cannot be null!";

    protected final NamespacedKey namespacedKey;
    protected boolean exactMeta;
    protected boolean hidden;

    protected RecipePriority priority;
    protected Conditions conditions;
    protected String group;
    protected final WolfyUtilities api;
    protected final ObjectMapper mapper;
    protected Result result;

    protected CustomRecipe(NamespacedKey namespacedKey, JsonNode node) {
        this.namespacedKey = Objects.requireNonNull(namespacedKey, ERROR_MSG_KEY);
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
        if (node.has(KEY_RESULT) && !(this instanceof CustomRecipeStonecutter)) {
            setResult(ItemLoader.loadResult(node.path(KEY_RESULT)));
        }
    }

    protected CustomRecipe(NamespacedKey key) {
        this.namespacedKey = Objects.requireNonNull(key, ERROR_MSG_KEY);
        this.mapper = JacksonUtil.getObjectMapper();
        this.api = CustomCrafting.inst().getApi();
        this.result = new Result();

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
    protected CustomRecipe(CustomRecipe<?> customRecipe) {
        this.mapper = JacksonUtil.getObjectMapper();
        this.api = CustomCrafting.inst().getApi();
        this.namespacedKey = Objects.requireNonNull(customRecipe.namespacedKey, ERROR_MSG_KEY);

        this.group = customRecipe.group;
        this.priority = customRecipe.priority;
        this.exactMeta = customRecipe.exactMeta;
        this.conditions = customRecipe.conditions;
        this.hidden = customRecipe.hidden;
        this.result = customRecipe.result.clone();
    }

    @Override
    public abstract C clone();

    public WolfyUtilities getAPI() {
        return api;
    }

    /**
     * @return The namespaced key under which the recipe is saved.
     */
    @Override
    public @NotNull NamespacedKey getNamespacedKey() {
        return namespacedKey;
    }

    public RecipePriority getPriority() {
        return priority;
    }

    public void setPriority(RecipePriority priority) {
        this.priority = priority;
    }

    public boolean isExactMeta() {
        return exactMeta;
    }

    public void setExactMeta(boolean exactMeta) {
        this.exactMeta = exactMeta;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = Objects.requireNonNullElse(group, "");
    }

    public Conditions getConditions() {
        return conditions;
    }

    public void setConditions(Conditions conditions) {
        this.conditions = conditions;
    }

    public abstract Ingredient getIngredient(int slot);

    public Result getResult() {
        return result;
    }

    public void setResult(@NotNull Result result) {
        Objects.requireNonNull(result, "Invalid result! Result must not be null!");
        Preconditions.checkArgument(!result.isEmpty(), "Invalid result! Recipe must have a non-air result!");
        this.result = result;
    }

    public abstract RecipeType<C> getRecipeType();

    public List<CustomItem> getRecipeBookItems() {
        return getResult().getChoices();
    }

    /**
     * Checks all the conditions of the recipe against specified data.
     *
     * @param data The data to check the conditions against.
     * @return True if the conditions are met.
     */
    public boolean checkConditions(Conditions.Data data) {
        return getConditions().checkConditions(this, data);
    }

    /**
     * Checks a specific condition of this recipe against the specified data.
     * If the condition does not exist, it will return true.
     *
     * @param id   The id of the Condition.
     * @param data The data to check the condition against.
     * @return True if condition is valid or nonexistent.
     */
    public boolean checkCondition(String id, Conditions.Data data) {
        return getConditions().check(id, this, data);
    }

    public boolean isDisabled() {
        return CustomCrafting.inst().getDisableRecipesHandler().getRecipes().contains(getNamespacedKey());
    }

    public boolean findResultItem(ItemStack result) {
        return getResult().getChoices().stream().anyMatch(customItem -> customItem.create().isSimilar(result));
    }

    /**
     * This method saves the Recipe into the File or the Database.
     * It can also send a confirmation message to the player if the player is not null.
     */
    public boolean save(@Nullable Player player) {
        return save(CustomCrafting.inst().getDataHandler().getActiveLoader(), player);
    }

    /**
     * Saves the recipe using the specified {@link ResourceLoader}.<br>
     * If a {@link Player} is specified, then it will send that player a chat message if the save was successful.
     *
     * @param loader The loader to use to save the recipe.
     * @param player Optional. The player to send a save confirm message.
     * @return If the save was successful.
     */
    public boolean save(ResourceLoader loader, @Nullable Player player) {
        if (loader.save(this)) {
            getAPI().getChat().sendKey(player, "recipe_creator", "save.success");
            getAPI().getChat().sendMessage(player, String.format("§6data/%s/%s/%s/", getNamespacedKey().getNamespace(), getRecipeType().getId(), getNamespacedKey().getKey()));
            return true;
        }
        return false;
    }

    /**
     * Saves the recipe to the default {@link RecipeLoader} that is used to save data.<br>
     * Default would be the {@link me.wolfyscript.customcrafting.handlers.LocalStorageLoader}, that saves data to the data folder.
     *
     * @return If the saving was successful.
     */
    public boolean save() {
        return save(null);
    }

    public boolean delete(@Nullable Player player) {
        Bukkit.getScheduler().runTask(CustomCrafting.inst(), () -> CCRegistry.RECIPES.remove(getNamespacedKey()));
        if (CustomCrafting.inst().getDataHandler().getActiveLoader().delete(this)) {
            getAPI().getChat().sendMessage(player, "§aRecipe deleted!");
            return true;
        }
        getAPI().getChat().sendMessage(player, "&cCouldn't delete recipe file!");
        return false;
    }

    public boolean delete() {
        return delete(null);
    }

    public abstract void renderMenu(GuiWindow<CCCache> guiWindow, GuiUpdate<CCCache> event);

    public abstract void prepareMenu(GuiHandler<CCCache> guiHandler, GuiCluster<CCCache> cluster);

    public void writeToJson(JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStringField(KEY_GROUP, group);
        gen.writeBooleanField(KEY_HIDDEN, hidden);
        gen.writeStringField(KEY_PRIORITY, priority.toString());
        gen.writeBooleanField(KEY_EXACT_META, exactMeta);
        gen.writeObjectField(KEY_CONDITIONS, conditions);
    }

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

    static class Serializer extends StdSerializer<CustomRecipe<?>> {

        public Serializer() {
            super((Class<CustomRecipe<?>>) new TypeReference<>() {
            }.getType());
        }

        public Serializer(Class<CustomRecipe<?>> vc) {
            super(vc);
        }

        @Override
        public void serialize(CustomRecipe iCustomRecipe, JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
            gen.writeStartObject();
            iCustomRecipe.writeToJson(gen, serializerProvider);
            gen.writeEndObject();
        }
    }
}
