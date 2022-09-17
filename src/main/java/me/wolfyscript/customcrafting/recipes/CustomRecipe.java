/*
 *       ____ _  _ ____ ___ ____ _  _ ____ ____ ____ ____ ___ _ _  _ ____
 *       |    |  | [__   |  |  | |\/| |    |__/ |__| |___  |  | |\ | | __
 *       |___ |__| ___]  |  |__| |  | |___ |  \ |  | |     |  | | \| |__]
 *
 *       CustomCrafting Recipe creation and management tool for Minecraft
 *                      Copyright (C) 2021  WolfyScript
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.wolfyscript.customcrafting.recipes;

import com.google.common.base.Preconditions;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.handlers.ResourceLoader;
import me.wolfyscript.customcrafting.recipes.conditions.Conditions;
import me.wolfyscript.customcrafting.recipes.items.Ingredient;
import me.wolfyscript.customcrafting.recipes.items.Result;
import me.wolfyscript.customcrafting.utils.ItemLoader;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JacksonInject;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonAutoDetect;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonCreator;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonGetter;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonIgnore;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonProperty;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonPropertyOrder;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonSetter;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonTypeInfo;
import me.wolfyscript.lib.com.fasterxml.jackson.core.JsonGenerator;
import me.wolfyscript.lib.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.lib.com.fasterxml.jackson.databind.ObjectMapper;
import me.wolfyscript.lib.com.fasterxml.jackson.databind.SerializerProvider;
import me.wolfyscript.lib.com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import me.wolfyscript.lib.com.fasterxml.jackson.databind.annotation.JsonTypeResolver;
import me.wolfyscript.lib.net.kyori.adventure.text.Component;
import me.wolfyscript.lib.net.kyori.adventure.text.format.NamedTextColor;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.GuiWindow;
import me.wolfyscript.utilities.api.nms.network.MCByteBuf;
import me.wolfyscript.utilities.util.Keyed;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.json.jackson.JacksonUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@JsonTypeResolver(RecipeTypeResolver.class)
@JsonTypeIdResolver(RecipeTypeIdResolver.class)
@JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "@type")
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonPropertyOrder(value = {"@type", "group", "hidden", "vanillaBook", "priority", "checkNBT", "conditions"})
public abstract class CustomRecipe<C extends CustomRecipe<C>> implements Keyed {

    protected static final String KEY_RESULT = "result";
    protected static final String KEY_GROUP = "group";
    protected static final String KEY_VANILLA_BOOK = "vanillaBook";
    protected static final String KEY_AUTO_DISCOVER = "autoDiscover";
    protected static final String KEY_PRIORITY = "priority";
    protected static final String KEY_EXACT_META = "exactItemMeta";
    protected static final String KEY_CONDITIONS = "conditions";
    protected static final String KEY_HIDDEN = "hidden";
    protected static final String ERROR_MSG_KEY = "Not a valid key! The key cannot be null!";

    @JsonProperty("@type")
    protected RecipeType<C> type;
    @JsonIgnore
    protected final NamespacedKey namespacedKey;
    @JsonIgnore
    protected final WolfyUtilities api;
    @JsonIgnore
    protected final CustomCrafting customCrafting;
    @JsonIgnore
    protected final ObjectMapper mapper;

    @JsonIgnore
    protected boolean checkAllNBT;
    protected boolean hidden;
    protected boolean vanillaBook;
    protected boolean autoDiscover;
    protected RecipePriority priority;
    protected Conditions conditions;
    protected String group;
    protected Result result;

    /**
     * @param namespacedKey The namespaced key of the recipe.
     * @param node          The json node read from the recipe file.
     * @deprecated Used only for deserializing recipes from old json files.
     */
    protected CustomRecipe(NamespacedKey namespacedKey, JsonNode node) {
        this.type = RecipeType.valueOfRecipe(this);
        this.namespacedKey = Objects.requireNonNull(namespacedKey, ERROR_MSG_KEY);
        this.customCrafting = CustomCrafting.inst(); //TODO: Dependency Injection
        this.mapper = customCrafting.getApi().getJacksonMapperUtil().getGlobalMapper();
        this.api = this.customCrafting.getApi();
        //Get fields from JsonNode
        this.group = node.path(KEY_GROUP).asText("");
        this.priority = mapper.convertValue(node.path(KEY_PRIORITY).asText("NORMAL"), RecipePriority.class);
        this.checkAllNBT = node.path(KEY_EXACT_META).asBoolean(false);
        this.conditions = mapper.convertValue(node.path(KEY_CONDITIONS), Conditions.class);
        if (this.conditions == null) {
            this.conditions = new Conditions(customCrafting);
        }
        this.vanillaBook = node.path(KEY_VANILLA_BOOK).asBoolean(true);
        this.autoDiscover = node.path(KEY_VANILLA_BOOK).asBoolean(true);
        this.hidden = node.path(KEY_HIDDEN).asBoolean(false);
        //Sets the result of the recipe if one exists in the config
        if (node.has(KEY_RESULT) && !(this instanceof CustomRecipeStonecutter)) {
            setResult(ItemLoader.loadResult(node.path(KEY_RESULT), customCrafting));
        }
    }

    @JsonCreator
    protected CustomRecipe(@JsonProperty("key") @JacksonInject("key") NamespacedKey key, CustomCrafting customCrafting) {
        this(key, customCrafting, null);
    }

    protected CustomRecipe(NamespacedKey key, CustomCrafting customCrafting, RecipeType<C> type) {
        this.type = type == null ? RecipeType.valueOfRecipe(this) : type;
        Preconditions.checkArgument(this.type != null, "Error constructing Recipe Object \"" + getClass().getName() + "\": Missing RecipeType!");
        this.namespacedKey = Objects.requireNonNull(key, ERROR_MSG_KEY);
        this.customCrafting = customCrafting; //TODO: Dependency Injection
        this.mapper = customCrafting.getApi().getJacksonMapperUtil().getGlobalMapper();
        this.api = customCrafting.getApi();
        this.result = new Result();

        this.group = "";
        this.priority = RecipePriority.NORMAL;
        this.checkAllNBT = false;
        this.vanillaBook = true;
        this.autoDiscover = true;
        this.conditions = new Conditions(customCrafting);
        this.hidden = false;
    }

    /**
     * Used to copy the fields of another instance to this one.
     * The ResultTarget must be the same in order to copy it.
     *
     * @param customRecipe The other CustomRecipe. Can be from another type, but must have the same ResultTarget.
     */
    protected CustomRecipe(CustomRecipe<C> customRecipe) {
        this.type = customRecipe.type;
        this.customCrafting = customRecipe.customCrafting;
        this.mapper = customCrafting.getApi().getJacksonMapperUtil().getGlobalMapper();
        this.api = customCrafting.getApi();
        this.namespacedKey = Objects.requireNonNull(customRecipe.namespacedKey, ERROR_MSG_KEY);

        this.vanillaBook = customRecipe.vanillaBook;
        this.autoDiscover = customRecipe.autoDiscover;
        this.group = customRecipe.group;
        this.priority = customRecipe.priority;
        this.checkAllNBT = customRecipe.checkAllNBT;
        this.conditions = customRecipe.conditions;
        this.hidden = customRecipe.hidden;
        this.result = customRecipe.result.clone();
    }

    @Override
    public abstract C clone();

    @JsonIgnore
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

    @JsonIgnore
    @Deprecated
    public boolean isExactMeta() {
        return checkAllNBT;
    }

    @JsonIgnore
    @Deprecated
    public void setExactMeta(boolean exactMeta) {
        this.checkAllNBT = exactMeta;
    }

    @JsonSetter("checkNBT")
    public void setCheckNBT(boolean checkAllNBT) {
        this.checkAllNBT = checkAllNBT;
    }

    @JsonGetter("checkNBT")
    public boolean isCheckNBT() {
        return checkAllNBT;
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

    @JsonSetter("result")
    protected void setResult(JsonNode node) {
        setResult(ItemLoader.loadResult(node, this.customCrafting));
    }

    @JsonIgnore
    public RecipeType<C> getRecipeType() {
        return type;
    }

    @JsonGetter("@type")
    private NamespacedKey getType() {
        return type.getNamespacedKey();
    }

    @JsonIgnore
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

    @JsonIgnore
    public boolean isDisabled() {
        return customCrafting.getDisableRecipesHandler().getRecipes().contains(getNamespacedKey());
    }

    public boolean findResultItem(ItemStack result) {
        return getResult().getChoices().stream().anyMatch(customItem -> customItem.create().isSimilar(result));
    }

    /**
     * This method saves the Recipe into the File or the Database.
     * It can also send a confirmation message to the player if the player is not null.
     */
    public boolean save(@Nullable Player player) {
        return save(customCrafting.getDataHandler().getActiveLoader(), player);
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
            getAPI().getChat().sendMessage(player, String.format("§6data/%s/recipes/%s", NamespacedKeyUtils.getKeyRoot(getNamespacedKey()), NamespacedKeyUtils.getRelativeKeyObjPath(getNamespacedKey())));
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
        Bukkit.getScheduler().runTask(customCrafting, () -> customCrafting.getRegistries().getRecipes().remove(getNamespacedKey()));
        try {
            if (customCrafting.getDataHandler().getActiveLoader().delete(this)) {
                getAPI().getChat().sendMessage(player, Component.text("Recipe deleted!", NamedTextColor.GREEN));
                return true;
            }
        } catch (IOException e) {
            getAPI().getChat().sendMessage(player, Component.text("Couldn't delete recipe file! " + e.getMessage(), NamedTextColor.RED));
            getAPI().getChat().sendMessage(player, Component.text("For full error please see logs!", NamedTextColor.RED));
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete() {
        return delete(null);
    }

    public abstract void renderMenu(GuiWindow<CCCache> guiWindow, GuiUpdate<CCCache> event);

    public abstract void prepareMenu(GuiHandler<CCCache> guiHandler, GuiCluster<CCCache> cluster);

    /**
     * Writes the recipe to json using the specified generator and provider.
     *
     * @param gen      The JsonGenerator
     * @param provider The SerializerProvider
     * @throws IOException Any exception caused when writing it to json.
     * @deprecated This is no longer used. Instead, the recipe object can be written to json directly.
     */
    @Deprecated
    public void writeToJson(JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStringField(KEY_GROUP, group);
        gen.writeBooleanField(KEY_HIDDEN, hidden);
        if (vanillaBook) {
            gen.writeBooleanField(KEY_VANILLA_BOOK, true);
        }
        gen.writeStringField(KEY_PRIORITY, priority.toString());
        gen.writeBooleanField(KEY_EXACT_META, checkAllNBT);
        gen.writeObjectField(KEY_CONDITIONS, conditions);
    }

    public void writeToBuf(MCByteBuf byteBuf) {
        byteBuf.writeUtf(getRecipeType().name());
        byteBuf.writeUtf(namespacedKey.toString());
        byteBuf.writeBoolean(checkAllNBT);
        byteBuf.writeUtf(group);
        byteBuf.writeCollection(result.getChoices(), (mcByteBuf, customItem) -> mcByteBuf.writeItemStack(customItem.create()));
    }

}
