package me.wolfyscript.customcrafting.recipes.types.stonecutter;

import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.recipebook.buttons.IngredientContainerButton;
import me.wolfyscript.customcrafting.recipes.RecipeType;
import me.wolfyscript.customcrafting.recipes.Types;
import me.wolfyscript.customcrafting.recipes.types.CustomRecipe;
import me.wolfyscript.customcrafting.recipes.types.ICustomVanillaRecipe;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.custom_items.references.APIReference;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.GuiWindow;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.core.JsonGenerator;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.SerializerProvider;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.StonecuttingRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CustomStonecutterRecipe extends CustomRecipe<CustomStonecutterRecipe> implements ICustomVanillaRecipe<StonecuttingRecipe> {

    private List<CustomItem> result;
    private List<CustomItem> source;

    public CustomStonecutterRecipe() {
        super();
        this.result = new ArrayList<>();
        this.source = new ArrayList<>();
    }

    public CustomStonecutterRecipe(NamespacedKey namespacedKey, JsonNode node) {
        super(namespacedKey, node);
        {
            List<CustomItem> sources = new ArrayList<>();
            JsonNode sourceNode = node.path("source");
            if (sourceNode.isObject()) {
                sources.add(new CustomItem(mapper.convertValue(sourceNode, APIReference.class)));
                JsonNode variantsNode = sourceNode.path("variants");
                for (JsonNode jsonNode : variantsNode) {
                    sources.add(new CustomItem(mapper.convertValue(jsonNode, APIReference.class)));
                }
            } else {
                sourceNode.elements().forEachRemaining(n -> sources.add(new CustomItem(mapper.convertValue(n, APIReference.class))));
            }
            this.source = sources.stream().filter(customItem -> !ItemUtils.isAirOrNull(customItem)).collect(Collectors.toList());
        }
    }

    public CustomStonecutterRecipe(CustomStonecutterRecipe customStonecutterRecipe){
        super(customStonecutterRecipe);
        this.result = customStonecutterRecipe.getResults();
        this.source = customStonecutterRecipe.getSource();
    }

    @Override
    public List<CustomItem> getResults() {
        return new ArrayList<>(result);
    }

    @Override
    public void setResult(List<CustomItem> result) {
        this.result = result;
    }

    public List<CustomItem> getSource() {
        return new ArrayList<>(source);
    }

    public void setSource(List<CustomItem> source) {
        this.source = source;
    }

    public void setSource(int variant, CustomItem ingredient) {
        if (variant < source.size()) {
            source.set(variant, ingredient);
        } else {
            source.add(ingredient);
        }
    }

    @Override
    public void writeToJson(JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        super.writeToJson(gen, serializerProvider);
        gen.writeObjectField("result", result.get(0).getApiReference());
        {
            gen.writeArrayFieldStart("source");
            for (CustomItem customItem : getSource()) {
                gen.writeObject(customItem.getApiReference());
            }
            gen.writeEndArray();
        }
    }

    @Override
    public RecipeType<CustomStonecutterRecipe> getRecipeType() {
        return Types.STONECUTTER;
    }

    @Override
    public CustomStonecutterRecipe clone() {
        return new CustomStonecutterRecipe(this);
    }

    @Override
    public void prepareMenu(GuiHandler<CCCache> guiHandler, GuiCluster<CCCache> cluster) {
        ((IngredientContainerButton) cluster.getButton("ingredient.container_20")).setVariants(guiHandler, getSource());
        ((IngredientContainerButton) cluster.getButton("ingredient.container_24")).setVariants(guiHandler, Collections.singletonList(getResult()));
    }

    @Override
    public void renderMenu(GuiWindow<CCCache> guiWindow, GuiUpdate<CCCache> event) {
        //TODO STONECUTTER
        NamespacedKey glass = new NamespacedKey("none", "glass_green");
        event.setButton(20, new NamespacedKey("recipe_book", "ingredient.container_20"));
        event.setButton(24, new NamespacedKey("recipe_book", "ingredient.container_24"));
        event.setButton(29, glass);
        event.setButton(30, glass);
        event.setButton(31, new NamespacedKey("recipe_book", "stonecutter"));
        event.setButton(32, glass);
        event.setButton(33, glass);

        ItemStack whiteGlass = event.getInventory().getItem(53);
        ItemMeta itemMeta = whiteGlass.getItemMeta();
        itemMeta.setCustomModelData(9007);
        whiteGlass.setItemMeta(itemMeta);
        event.setItem(53, whiteGlass);
    }

    @Override
    public StonecuttingRecipe getVanillaRecipe() {
        RecipeChoice choice = isExactMeta() ? new RecipeChoice.ExactChoice(getSource().stream().map(CustomItem::create).collect(Collectors.toList())) : new RecipeChoice.MaterialChoice(getSource().stream().map(i -> i.create().getType()).collect(Collectors.toList()));
        return new StonecuttingRecipe(new org.bukkit.NamespacedKey(namespacedKey.getNamespace(), namespacedKey.getKey()), getResult().create(), choice);
    }
}
