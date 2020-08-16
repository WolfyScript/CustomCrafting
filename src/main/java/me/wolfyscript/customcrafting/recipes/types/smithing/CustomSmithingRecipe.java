package me.wolfyscript.customcrafting.recipes.types.smithing;

import com.google.common.collect.Streams;
import me.wolfyscript.customcrafting.recipes.types.CustomRecipe;
import me.wolfyscript.customcrafting.recipes.types.ICustomVanillaRecipe;
import me.wolfyscript.customcrafting.recipes.types.RecipeType;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.custom_items.api_references.APIReference;
import me.wolfyscript.utilities.api.inventory.GuiUpdateEvent;
import me.wolfyscript.utilities.api.inventory.GuiWindow;
import me.wolfyscript.utilities.api.utils.NamespacedKey;
import me.wolfyscript.utilities.api.utils.inventory.ItemUtils;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.SmithingRecipe;

import java.util.List;
import java.util.stream.Collectors;

public class CustomSmithingRecipe extends CustomRecipe implements ICustomVanillaRecipe<SmithingRecipe> {

    private List<CustomItem> base;
    private List<CustomItem> addition;
    private List<CustomItem> result;

    public CustomSmithingRecipe(NamespacedKey namespacedKey, JsonNode node) {
        super(namespacedKey, node);
        base = Streams.stream(node.path("base").elements()).map(n -> new CustomItem(mapper.convertValue(n, APIReference.class))).filter(cI -> !ItemUtils.isAirOrNull(cI)).collect(Collectors.toList());
        addition = Streams.stream(node.path("addition").elements()).map(n -> new CustomItem(mapper.convertValue(n, APIReference.class))).filter(cI -> !ItemUtils.isAirOrNull(cI)).collect(Collectors.toList());
        result = Streams.stream(node.path("result").elements()).map(n -> new CustomItem(mapper.convertValue(n, APIReference.class))).filter(cI -> !ItemUtils.isAirOrNull(cI)).collect(Collectors.toList());
    }

    public CustomSmithingRecipe() {
        super();
    }

    public CustomSmithingRecipe(CustomSmithingRecipe customSmithingRecipe) {
        super(customSmithingRecipe);
    }

    @Override
    public SmithingRecipe getVanillaRecipe() {
        RecipeChoice.ExactChoice baseItems = new RecipeChoice.ExactChoice(base.stream().map(CustomItem::create).collect(Collectors.toList()));
        RecipeChoice.ExactChoice additionItems = new RecipeChoice.ExactChoice(addition.stream().map(CustomItem::create).collect(Collectors.toList()));
        return new SmithingRecipe(new org.bukkit.NamespacedKey(namespacedKey.getNamespace(), namespacedKey.getKey()), getCustomResult().create(), baseItems, additionItems);
    }

    @Override
    public RecipeType getRecipeType() {
        return RecipeType.SMITHING;
    }

    @Override
    public List<CustomItem> getCustomResults() {
        return base;
    }

    @Override
    public void setResult(List<CustomItem> result) {
        this.result = result;
    }

    @Override
    public void renderMenu(GuiWindow guiWindow, GuiUpdateEvent event) {

    }
}
