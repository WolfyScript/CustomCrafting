package me.wolfyscript.customcrafting.recipes.types.stonecutter;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.MainCluster;
import me.wolfyscript.customcrafting.gui.RecipeBookCluster;
import me.wolfyscript.customcrafting.gui.recipebook.buttons.IngredientContainerButton;
import me.wolfyscript.customcrafting.recipes.RecipeType;
import me.wolfyscript.customcrafting.recipes.Types;
import me.wolfyscript.customcrafting.recipes.types.CustomRecipe;
import me.wolfyscript.customcrafting.recipes.types.ICustomVanillaRecipe;
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
import org.bukkit.inventory.meta.ItemMeta;

import java.io.IOException;
import java.util.stream.Collectors;

public class CustomStonecutterRecipe extends CustomRecipe<CustomStonecutterRecipe, FixedResultTarget> implements ICustomVanillaRecipe<StonecuttingRecipe> {

    private Ingredient source;

    public CustomStonecutterRecipe() {
        super();
        this.result = new Result<>();
        this.source = new Ingredient();
    }

    public CustomStonecutterRecipe(NamespacedKey namespacedKey, JsonNode node) {
        super(namespacedKey, node);
        if (node.has("result")) {
            this.result = node.path("result").has("custom_amount") ? new Result<>(JacksonUtil.getObjectMapper().convertValue(node.path("result"), APIReference.class)) : ItemLoader.loadResult(node.path("result"));
        }
        this.source = ItemLoader.loadIngredient(node.path("source"));
    }

    public CustomStonecutterRecipe(CustomStonecutterRecipe customStonecutterRecipe) {
        super(customStonecutterRecipe);
        this.result = customStonecutterRecipe.getResult();
        this.source = customStonecutterRecipe.getSource();
    }

    public Ingredient getSource() {
        return source;
    }

    public void setSource(Ingredient source) {
        this.source = source;
    }

    @Override
    public void writeToJson(JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        super.writeToJson(gen, serializerProvider);
        gen.writeObjectField("result", this.result);
        gen.writeObjectField("source", this.source);
    }

    @Override
    public RecipeType<CustomStonecutterRecipe> getRecipeType() {
        return Types.STONECUTTER;
    }

    @Override
    public void setIngredient(int slot, Ingredient ingredient) {
        this.source = ingredient;
    }

    @Override
    public Ingredient getIngredient(int slot) {
        return this.source;
    }

    @Override
    public CustomStonecutterRecipe clone() {
        return new CustomStonecutterRecipe(this);
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
            ItemMeta itemMeta = whiteGlass.getItemMeta();
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
