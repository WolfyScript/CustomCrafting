package me.wolfyscript.customcrafting.recipes;

import com.google.common.base.Preconditions;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.MainCluster;
import me.wolfyscript.customcrafting.gui.RecipeBookCluster;
import me.wolfyscript.customcrafting.gui.recipebook.buttons.IngredientContainerButton;
import me.wolfyscript.customcrafting.utils.ItemLoader;
import me.wolfyscript.customcrafting.utils.recipe_item.Ingredient;
import me.wolfyscript.customcrafting.utils.recipe_item.Result;
import me.wolfyscript.customcrafting.utils.recipe_item.target.FixedResultTarget;
import me.wolfyscript.utilities.api.inventory.custom_items.references.APIReference;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.GuiWindow;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.core.JsonGenerator;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.SerializerProvider;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.json.jackson.JacksonUtil;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.StonecuttingRecipe;

import java.io.IOException;
import java.util.stream.Collectors;

public class CustomRecipeStonecutter extends CustomRecipe<CustomRecipeStonecutter, FixedResultTarget> implements ICustomVanillaRecipe<StonecuttingRecipe> {

    private static final String KEY_SOURCE = "source";

    private Ingredient source;

    public CustomRecipeStonecutter() {
        super();
        this.result = new Result<>();
        this.source = new Ingredient();
    }

    public CustomRecipeStonecutter(NamespacedKey namespacedKey, JsonNode node) {
        super(namespacedKey, node);
        if (node.has(KEY_RESULT)) {
            //Some old config format, which saved the item directly as a reference
            setResult(node.path(KEY_RESULT).has("custom_amount") ? new Result<>(JacksonUtil.getObjectMapper().convertValue(node.path("result"), APIReference.class)) : ItemLoader.loadResult(node.path("result")));
        }
        setSource(ItemLoader.loadIngredient(node.path(KEY_SOURCE)));
    }

    public CustomRecipeStonecutter(CustomRecipeStonecutter customRecipeStonecutter) {
        super(customRecipeStonecutter);
        this.result = customRecipeStonecutter.getResult();
        this.source = customRecipeStonecutter.getSource();
    }

    public Ingredient getSource() {
        return source;
    }

    public void setSource(Ingredient source) {
        this.source = source;
        Preconditions.checkArgument(!source.isEmpty(), "Invalid source! Recipe must have non-air source!");
    }

    @Override
    public void writeToJson(JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        super.writeToJson(gen, serializerProvider);
        gen.writeObjectField(KEY_RESULT, this.result);
        gen.writeObjectField(KEY_SOURCE, this.source);
    }

    @Override
    public RecipeType<CustomRecipeStonecutter> getRecipeType() {
        return Types.STONECUTTER;
    }

    @Override
    public void setIngredient(int slot, Ingredient ingredient) {
        setSource(ingredient);
    }

    @Override
    public Ingredient getIngredient(int slot) {
        return this.source;
    }

    @Override
    public CustomRecipeStonecutter clone() {
        return new CustomRecipeStonecutter(this);
    }

    @Override
    public void prepareMenu(GuiHandler<CCCache> guiHandler, GuiCluster<CCCache> cluster) {
        ((IngredientContainerButton) cluster.getButton("ingredient.container_20")).setVariants(guiHandler, getSource());
        ((IngredientContainerButton) cluster.getButton("ingredient.container_24")).setVariants(guiHandler, getResult());
    }

    @Override
    public void renderMenu(GuiWindow<CCCache> guiWindow, GuiUpdate<CCCache> event) {
        NamespacedKey glass = MainCluster.GLASS_GREEN;
        event.setButton(20, new NamespacedKey(RecipeBookCluster.KEY, "ingredient.container_20"));
        event.setButton(24, new NamespacedKey(RecipeBookCluster.KEY, "ingredient.container_24"));
        event.setButton(29, glass);
        event.setButton(30, glass);
        event.setButton(31, RecipeBookCluster.STONECUTTER);
        event.setButton(32, glass);
        event.setButton(33, glass);

        ItemStack whiteGlass = event.getInventory().getItem(53);
        if (whiteGlass != null) {
            var itemMeta = whiteGlass.getItemMeta();
            itemMeta.setCustomModelData(9007);
            whiteGlass.setItemMeta(itemMeta);
            event.setItem(53, whiteGlass);
        }
    }

    @Override
    public StonecuttingRecipe getVanillaRecipe() {
        if (!getResult().isEmpty() && !getSource().isEmpty()) {
            RecipeChoice choice = isExactMeta() ? new RecipeChoice.ExactChoice(getSource().getBukkitChoices()) : new RecipeChoice.MaterialChoice(getSource().getBukkitChoices().stream().map(ItemStack::getType).collect(Collectors.toList()));
            return new StonecuttingRecipe(getNamespacedKey().toBukkit(CustomCrafting.inst()), getResult().getChoices().get(0).create(), choice);
        }
        return null;
    }
}
