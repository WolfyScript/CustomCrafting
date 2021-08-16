package me.wolfyscript.customcrafting.recipes;

import me.wolfyscript.customcrafting.CCRegistry;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.handlers.ResourceLoader;
import me.wolfyscript.customcrafting.recipes.conditions.Conditions;
import me.wolfyscript.customcrafting.recipes.items.Ingredient;
import me.wolfyscript.customcrafting.recipes.items.Result;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.GuiWindow;
import me.wolfyscript.utilities.api.nms.network.MCByteBuf;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.core.JsonGenerator;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.SerializerProvider;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.annotation.JsonSerialize;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.ser.std.StdSerializer;
import me.wolfyscript.utilities.util.Keyed;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;

@JsonSerialize(using = ICustomRecipe.Serializer.class)
public interface ICustomRecipe<C extends ICustomRecipe<C>> extends Keyed {

    WolfyUtilities getAPI();

    @Override
    @NotNull
    NamespacedKey getNamespacedKey();

    RecipeType<C> getRecipeType();

    String getGroup();

    void setGroup(String group);

    Result getResult();

    void setResult(Result result);

    Ingredient getIngredient(int slot);

    RecipePriority getPriority();

    void setPriority(RecipePriority priority);

    boolean isExactMeta();

    void setExactMeta(boolean exactMeta);

    Conditions getConditions();

    void setConditions(Conditions conditions);

    /**
     * Checks all the conditions of the recipe against specified data.
     *
     * @param data The data to check the conditions against.
     * @return True if the conditions are met.
     */
    default boolean checkConditions(Conditions.Data data) {
        return getConditions().checkConditions(this, data);
    }

    /**
     * Checks a specific condition of this recipe against the specified data.
     * If the condition does not exist, it will return true.
     *
     * @param id   The id of the Condition.
     * @param data The data to check the condition against.
     * @return True if condition is valid or non existent.
     */
    default boolean checkCondition(String id, Conditions.Data data) {
        return getConditions().check(id, this, data);
    }

    boolean isHidden();

    void setHidden(boolean hidden);

    default boolean isDisabled() {
        return CustomCrafting.inst().getDisableRecipesHandler().getRecipes().contains(getNamespacedKey());
    }

    C clone();

    /**
     * This method saves the Recipe into the File or the Database.
     * It can also send a confirmation message to the player if the player is not null.
     */
    default boolean save(@Nullable Player player) {
        return save(CustomCrafting.inst().getDataHandler().getActiveLoader(), player);
    }

    default boolean save(ResourceLoader loader, @Nullable Player player) {
        if (loader.save(this)) {
            getAPI().getChat().sendKey(player, "recipe_creator", "save.success");
            getAPI().getChat().sendMessage(player, String.format("§6data/%s/%s/%s/", getNamespacedKey().getNamespace(), getRecipeType().getId(), getNamespacedKey().getKey()));
            return true;
        }
        return false;
    }

    default boolean save() {
        return save(null);
    }

    default boolean delete(@Nullable Player player) {
        Bukkit.getScheduler().runTask(CustomCrafting.inst(), () -> CCRegistry.RECIPES.remove(getNamespacedKey()));
        if (CustomCrafting.inst().getDataHandler().getActiveLoader().delete(this)) {
            getAPI().getChat().sendMessage(player, "§aRecipe deleted!");
            return true;
        }
        getAPI().getChat().sendMessage(player, "&cCouldn't delete recipe file!");
        return false;
    }

    default boolean delete() {
        return delete(null);
    }

    void writeToJson(JsonGenerator gen, SerializerProvider serializerProvider) throws IOException;

    void writeToBuf(MCByteBuf byteBuf);

    default boolean findResultItem(ItemStack result) {
        return getResult().getChoices().parallelStream().anyMatch(customItem -> customItem.create().isSimilar(result));
    }

    default List<CustomItem> getRecipeBookItems() {
        return getResult().getChoices();
    }

    void renderMenu(GuiWindow<CCCache> guiWindow, GuiUpdate<CCCache> event);

    void prepareMenu(GuiHandler<CCCache> guiHandler, GuiCluster<CCCache> cluster);

    class Serializer extends StdSerializer<ICustomRecipe> {

        public Serializer() {
            super(ICustomRecipe.class);
        }

        protected Serializer(Class<ICustomRecipe> vc) {
            super(vc);
        }

        @Override
        public void serialize(ICustomRecipe iCustomRecipe, JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
            gen.writeStartObject();
            iCustomRecipe.writeToJson(gen, serializerProvider);
            gen.writeEndObject();
        }
    }

}
