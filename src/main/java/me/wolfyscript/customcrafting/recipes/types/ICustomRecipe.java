package me.wolfyscript.customcrafting.recipes.types;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.Registry;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.handlers.DataHandler;
import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.RecipePriority;
import me.wolfyscript.customcrafting.recipes.RecipeType;
import me.wolfyscript.customcrafting.utils.recipe_item.Ingredient;
import me.wolfyscript.customcrafting.utils.recipe_item.Result;
import me.wolfyscript.customcrafting.utils.recipe_item.target.ResultTarget;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.GuiWindow;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.core.JsonGenerator;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.SerializerProvider;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.annotation.JsonSerialize;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.ser.std.StdSerializer;
import me.wolfyscript.utilities.util.Keyed;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.json.jackson.JacksonUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.List;

@JsonSerialize(using = ICustomRecipe.Serializer.class)
public interface ICustomRecipe<C extends ICustomRecipe<?, ?>, T extends ResultTarget> extends Keyed {

    WolfyUtilities getAPI();

    boolean hasNamespacedKey();

    @Override
    NamespacedKey getNamespacedKey();

    void setNamespacedKey(NamespacedKey namespacedKey);

    RecipeType<C> getRecipeType();

    String getGroup();

    void setGroup(String group);

    Result<T> getResult();

    void setResult(Result<T> result);

    /**
     * Used to set Ingredient from cache of the RecipeCreator
     *
     * @param slot       The slot of the ingredient in the recipe.
     * @param ingredient The ingredient to set
     */
    void setIngredient(int slot, Ingredient ingredient);

    Ingredient getIngredient(int slot);

    RecipePriority getPriority();

    void setPriority(RecipePriority priority);

    boolean isExactMeta();

    void setExactMeta(boolean exactMeta);

    Conditions getConditions();

    void setConditions(Conditions conditions);

    default boolean checkConditions(Conditions.Data data) {
        return getConditions().checkConditions(this, data);
    }

    boolean isHidden();

    void setHidden(boolean hidden);

    C clone();

    /**
     * This method saves the Recipe into the File or the Database.
     * It can also send a confirmation message to the player if the player is not null.
     */
    default boolean save(@Nullable Player player) {
        if (getNamespacedKey() != null) {
            try {
                if (CustomCrafting.inst().hasDataBaseHandler()) {
                    CustomCrafting.inst().getDataBaseHandler().updateRecipe(this);
                } else {
                    File file = new File(DataHandler.DATA_FOLDER, getNamespacedKey().getNamespace() + File.separator + getRecipeType().getId() + File.separator + getNamespacedKey().getKey() + ".json");
                    file.getParentFile().mkdirs();
                    if (file.exists() || file.createNewFile()) {
                        JacksonUtil.getObjectWriter(CustomCrafting.inst().getConfigHandler().getConfig().isPrettyPrinting()).writeValue(file, this);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            if (player != null) {
                getAPI().getChat().sendKey(player, "recipe_creator", "save.success");
                getAPI().getChat().sendMessage(player, String.format("§6data/%s/%s/%s/", getNamespacedKey().getNamespace(), getRecipeType().getId(), getNamespacedKey().getKey()));
            }
            return true;
        }
        if (player != null) {
            getAPI().getChat().sendMessage(player, "&c" + "Missing NamespacedKey!");
        }
        return false;
    }

    default boolean save() {
        return save(null);
    }

    default boolean delete(@Nullable Player player) {
        if (getNamespacedKey() != null) {
            Bukkit.getScheduler().runTask(CustomCrafting.inst(), () -> Registry.RECIPES.remove(getNamespacedKey()));
            if (CustomCrafting.inst().hasDataBaseHandler()) {
                CustomCrafting.inst().getDataBaseHandler().removeRecipe(getNamespacedKey().getNamespace(), getNamespacedKey().getKey());
                player.sendMessage("§aRecipe deleted!");
                return true;
            } else {
                File file = new File(DataHandler.DATA_FOLDER, getNamespacedKey().getNamespace() + File.separator + getRecipeType().getId() + File.separator + getNamespacedKey().getKey() + ".json");
                System.gc();
                if (file.delete()) {
                    if (player != null) getAPI().getChat().sendMessage(player, "&aRecipe deleted!");
                    return true;
                } else {
                    file.deleteOnExit();
                    if (player != null)
                        getAPI().getChat().sendMessage(player, "&cCouldn't delete recipe on runtime! File is being deleted on restart!");
                }
            }
            return false;
        }
        if (player != null) {
            getAPI().getChat().sendMessage(player, "&c" + "Missing NamespacedKey!");
        }
        return false;
    }

    default boolean delete() {
        return delete(null);
    }

    void writeToJson(JsonGenerator gen, SerializerProvider serializerProvider) throws IOException;

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
