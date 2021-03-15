package me.wolfyscript.customcrafting.utils.recipe_item;

import me.wolfyscript.customcrafting.utils.recipe_item.extension.ResultExtension;
import me.wolfyscript.customcrafting.utils.recipe_item.target.ResultTarget;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.custom_items.references.APIReference;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.annotation.JsonIgnore;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.RandomCollection;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class Result<T extends ResultTarget> extends RecipeItemStack {

    private T target;
    private List<ResultExtension> extensions;

    public Result() {
        super();
        this.extensions = new ArrayList<>();
    }

    public Result(Material... materials) {
        super(materials);
        this.extensions = new ArrayList<>();
    }

    public Result(ItemStack... items) {
        super(items);
        this.extensions = new ArrayList<>();
    }

    public Result(NamespacedKey... tags) {
        super(tags);
        this.extensions = new ArrayList<>();
    }

    public Result(APIReference... references) {
        super(references);
        this.extensions = new ArrayList<>();
    }

    public Result(List<APIReference> references, Set<NamespacedKey> tags) {
        super(references, tags);
        this.extensions = new ArrayList<>();
    }

    public void setTarget(T target) {
        this.target = target;
    }

    public T getTarget() {
        return target;
    }

    public void setExtensions(List<ResultExtension> extensions) {
        this.extensions = extensions;
    }

    public List<ResultExtension> getExtensions() {
        return extensions;
    }

    public RandomCollection<CustomItem> getRandomChoices(Player player) {
        return getChoices(player).parallelStream().collect(RandomCollection.getCollector((rdmCollection, customItem) -> rdmCollection.add(customItem.getRarityPercentage(), customItem.clone())));
    }

    public Optional<CustomItem> getItem(Player player) {
        RandomCollection<CustomItem> items = getRandomChoices(player);
        return Optional.ofNullable(!items.isEmpty() ? items.next() : null);
    }

    public CustomItem getCustomItem(Player player) {
        return getItem(player).orElse(new CustomItem(Material.AIR));
    }

    @JsonIgnore
    public Optional<CustomItem> getItem() {
        RandomCollection<CustomItem> items = getChoices().parallelStream().collect(RandomCollection.getCollector((rdmCollection, customItem) -> rdmCollection.add(customItem.getRarityPercentage(), customItem.clone())));
        return Optional.ofNullable(!items.isEmpty() ? items.next() : null);
    }
}
