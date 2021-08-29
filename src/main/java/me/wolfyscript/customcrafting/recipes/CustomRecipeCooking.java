package me.wolfyscript.customcrafting.recipes;

import com.google.common.base.Preconditions;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.CCPlayerData;
import me.wolfyscript.customcrafting.gui.recipebook.ButtonContainerIngredient;
import me.wolfyscript.customcrafting.recipes.conditions.Condition;
import me.wolfyscript.customcrafting.recipes.items.Ingredient;
import me.wolfyscript.customcrafting.utils.ItemLoader;
import me.wolfyscript.customcrafting.utils.PlayerUtil;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.GuiWindow;
import me.wolfyscript.utilities.api.nms.network.MCByteBuf;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.core.JsonGenerator;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.SerializerProvider;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.Material;
import org.bukkit.inventory.CookingRecipe;
import org.bukkit.inventory.RecipeChoice;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

public abstract class CustomRecipeCooking<C extends CustomRecipeCooking<C, T>, T extends CookingRecipe<?>> extends CustomRecipe<C> implements ICustomVanillaRecipe<T> {

    private Ingredient source;
    private float exp;
    private int cookingTime;

    protected CustomRecipeCooking(NamespacedKey namespacedKey, JsonNode node) {
        super(namespacedKey, node);
        this.exp = node.path("exp").floatValue();
        this.cookingTime = node.path("cooking_time").asInt();
        this.source = ItemLoader.loadIngredient(node.path("source"));
    }

    protected CustomRecipeCooking(NamespacedKey key) {
        super(key);
        this.source = new Ingredient();
        this.exp = 0;
        this.cookingTime = 80;
    }

    protected CustomRecipeCooking(CustomRecipeCooking<C, T> customRecipeCooking) {
        super(customRecipeCooking);
        this.source = customRecipeCooking.source;
        this.exp = customRecipeCooking.exp;
        this.cookingTime = customRecipeCooking.cookingTime;
    }

    @Override
    public abstract C clone();

    /**
     * @param material The type of the block.
     * @return if the recipe can be used inside that type of block!
     */
    public abstract boolean validType(Material material);

    public Ingredient getSource() {
        return this.source;
    }

    public void setSource(@NotNull Ingredient source) {
        Preconditions.checkArgument(!source.isEmpty(), "Invalid source! Recipe must have non-air source!");
        this.source = source;
    }

    @Override
    public Ingredient getIngredient(int slot) {
        return this.source;
    }

    private void setIngredient(int slot, Ingredient ingredient) {
        setSource(ingredient);
    }

    /**
     * @param ingredient The ingredient to set as the source.
     * @deprecated Replaced by {@link #setSource(Ingredient)}
     */
    @Deprecated
    public void setIngredient(Ingredient ingredient) {
        setIngredient(0, ingredient);
    }

    public void setCookingTime(int cookingTime) {
        this.cookingTime = cookingTime;
    }

    public float getExp() {
        return exp;
    }

    public int getCookingTime() {
        return cookingTime;
    }

    public void setExp(float exp) {
        this.exp = exp;
    }

    protected RecipeChoice getRecipeChoice() {
        return isExactMeta() ? new RecipeChoice.ExactChoice(getSource().getChoices().parallelStream().map(CustomItem::create).toList()) : new RecipeChoice.MaterialChoice(getSource().getChoices().parallelStream().map(i -> i.create().getType()).toList());
    }

    @Override
    public void prepareMenu(GuiHandler<CCCache> guiHandler, GuiCluster<CCCache> cluster) {
        var player = guiHandler.getPlayer();
        ((ButtonContainerIngredient) cluster.getButton(ButtonContainerIngredient.key(11))).setVariants(guiHandler, getSource().getChoices(player));
        ((ButtonContainerIngredient) cluster.getButton(ButtonContainerIngredient.key(24))).setVariants(guiHandler, this.getResult().getChoices().stream().filter(customItem -> !customItem.hasPermission() || player.hasPermission(customItem.getPermission())).toList());
    }

    @Override
    public void renderMenu(GuiWindow<CCCache> guiWindow, GuiUpdate<CCCache> event) {
        CCPlayerData data = PlayerUtil.getStore(event.getPlayer());
        List<Condition<?>> conditions = getConditions().getValues().stream().filter(condition -> !condition.getId().equals("permission")).toList();
        int startSlot = 9 / (conditions.size() + 1);
        int slot = 0;
        for (Condition<?> condition : conditions) {
            event.setButton(36 + startSlot + slot, new NamespacedKey("recipe_book", "conditions." + condition.getId()));
            slot += 2;
        }
        event.setButton(13, new NamespacedKey("recipe_book", "cooking.icon"));
        event.setButton(20, data.getLightBackground());
        event.setButton(11, ButtonContainerIngredient.namespacedKey(11));
        event.setButton(24, ButtonContainerIngredient.namespacedKey(24));
    }

    @Override
    public void writeToJson(JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        super.writeToJson(gen, serializerProvider);
        gen.writeNumberField("cooking_time", cookingTime);
        gen.writeNumberField("exp", exp);
        gen.writeObjectField("result", result);
        gen.writeObjectField("source", source);
    }

    @Override
    public void writeToBuf(MCByteBuf byteBuf) {
        super.writeToBuf(byteBuf);

        byteBuf.writeVarInt(source.size());
        for (CustomItem choice : source.getChoices()) {
            byteBuf.writeItemStack(choice.create());
        }
    }
}
