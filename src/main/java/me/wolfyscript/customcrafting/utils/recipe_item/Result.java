package me.wolfyscript.customcrafting.utils.recipe_item;

import me.wolfyscript.customcrafting.utils.recipe_item.extension.ResultExtension;
import me.wolfyscript.customcrafting.utils.recipe_item.target.ResultTarget;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.custom_items.references.APIReference;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.annotation.JsonIgnore;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.RandomCollection;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Optional;

public class Result<T extends ResultTarget> extends RecipeItemStack {

    private T target;
    private List<ResultExtension> extensions;

    public Result() {
        super();
    }

    public Result(Material... materials) {
        super(materials);
    }

    public Result(ItemStack... items) {
        super(items);
    }

    public Result(NamespacedKey... tags) {
        super(tags);
    }

    public Result(APIReference... references) {
        super(references);
    }

    public Result(List<APIReference> references, List<NamespacedKey> tags) {
        super(references, tags);
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

    /**
     * @return The ItemStack of the first CustomItem in the choices or AIR if choices are empty.
     */
    @JsonIgnore
    public ItemStack getItemStack() {
        return isEmpty() ? ItemUtils.AIR : getChoices().get(0).create();
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
