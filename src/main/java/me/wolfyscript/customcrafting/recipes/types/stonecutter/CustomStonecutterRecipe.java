package me.wolfyscript.customcrafting.recipes.types.stonecutter;

import me.wolfyscript.customcrafting.recipes.types.CustomRecipe;
import me.wolfyscript.customcrafting.recipes.types.ICustomVanillaRecipe;
import me.wolfyscript.customcrafting.recipes.types.RecipeType;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.custom_items.api_references.APIReference;
import me.wolfyscript.utilities.api.inventory.GuiUpdateEvent;
import me.wolfyscript.utilities.api.inventory.GuiWindow;
import me.wolfyscript.utilities.api.utils.NamespacedKey;
import me.wolfyscript.utilities.api.utils.inventory.ItemUtils;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.core.JsonGenerator;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.SerializerProvider;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.StonecuttingRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CustomStonecutterRecipe extends CustomRecipe implements ICustomVanillaRecipe<StonecuttingRecipe> {

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
        this.result = customStonecutterRecipe.getCustomResults();
        this.source = customStonecutterRecipe.getSource();
    }

    @Override
    public List<CustomItem> getCustomResults() {
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
    public RecipeType getRecipeType() {
        return RecipeType.STONECUTTER;
    }

    @Override
    public CustomStonecutterRecipe clone() {
        return new CustomStonecutterRecipe(this);
    }

    @Override
    public void renderMenu(GuiWindow guiWindow, GuiUpdateEvent event) {
        event.setButton(0, "back");
        //TODO STONECUTTER
        event.setButton(20, "recipe_book", "ingredient.container_20");
        event.setButton(24, "recipe_book", "ingredient.container_24");
        event.setButton(29, "none", "glass_green");
        event.setButton(30, "none", "glass_green");
        event.setButton(31, "recipe_book", "stonecutter");
        event.setButton(32, "none", "glass_green");
        event.setButton(33, "none", "glass_green");

        ItemStack whiteGlass = event.getInventory().getItem(53);
        ItemMeta itemMeta = whiteGlass.getItemMeta();
        itemMeta.setCustomModelData(9007);
        whiteGlass.setItemMeta(itemMeta);
        event.setItem(53, whiteGlass);
    }

    @Override
    public StonecuttingRecipe getVanillaRecipe() {
        return new StonecuttingRecipe(new org.bukkit.NamespacedKey(namespacedKey.getNamespace(), namespacedKey.getKey()), getCustomResult().create(), new RecipeChoice.ExactChoice(getSource().stream().map(CustomItem::create).collect(Collectors.toList())));
    }
}
