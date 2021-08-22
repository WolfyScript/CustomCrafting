package me.wolfyscript.customcrafting.recipes.items;

import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.custom_items.references.APIReference;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class Ingredient extends RecipeItemStack {

    private boolean replaceWithRemains = true;

    public Ingredient() {
        super();
    }

    public Ingredient(Ingredient ingredient) {
        super(ingredient);
        this.replaceWithRemains = ingredient.replaceWithRemains;
    }

    public Ingredient(Material... materials) {
        super(materials);
    }

    public Ingredient(ItemStack... items) {
        super(items);
    }

    public Ingredient(NamespacedKey... tags) {
        super(tags);
    }

    public Ingredient(APIReference... references) {
        super(references);
    }

    public Ingredient(List<APIReference> references, Set<NamespacedKey> tags) {
        super(references, tags);
    }

    public boolean isReplaceWithRemains() {
        return replaceWithRemains;
    }

    public void setReplaceWithRemains(boolean replaceWithRemains) {
        this.replaceWithRemains = replaceWithRemains;
    }

    @Override
    public Ingredient clone() {
        return new Ingredient(this);
    }

    public boolean test(ItemStack itemStack, boolean exactMatch) {
        if (itemStack == null) return false;
        return choices.stream().anyMatch(customItem -> customItem.isSimilar(itemStack, exactMatch));
    }

    public Optional<CustomItem> check(ItemStack itemStack, boolean exactMatch) {
        if (itemStack == null) return Optional.empty();
        return choices.stream().filter(customItem -> customItem.isSimilar(itemStack, exactMatch)).findFirst();
    }
}
