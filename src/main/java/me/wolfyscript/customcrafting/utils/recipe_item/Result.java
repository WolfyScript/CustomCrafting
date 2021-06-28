package me.wolfyscript.customcrafting.utils.recipe_item;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.types.workbench.CraftingData;
import me.wolfyscript.customcrafting.utils.recipe_item.extension.ResultExtension;
import me.wolfyscript.customcrafting.utils.recipe_item.target.ResultTarget;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.custom_items.references.APIReference;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.annotation.JsonIgnore;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.annotation.JsonInclude;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.annotation.JsonProperty;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.RandomCollection;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Result<T extends ResultTarget> extends RecipeItemStack {

    @JsonIgnore
    private final Map<UUID, CustomItem> cachedItems = new HashMap<>();
    @JsonIgnore
    private final Map<Vector, CustomItem> cachedBlockItems = new HashMap<>();
    private T target;
    private List<ResultExtension> extensions;

    public Result() {
        super();
        this.extensions = new ArrayList<>();
    }

    public Result(Result<T> result) {
        super(result);
        this.extensions = result.extensions;
        this.target = result.target;
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

    @Override
    public Result<T> clone() {
        return new Result<>(this);
    }

    public void setTarget(T target) {
        this.target = target;
    }

    public T getTarget() {
        return target;
    }

    @JsonProperty("extensions")
    public List<ResultExtension> getExtensions() {
        return extensions;
    }

    @JsonProperty("extensions")
    private void setExtensions(List<ResultExtension> extensions) {
        this.extensions = extensions;
    }

    public void addExtension(ResultExtension extension) {
        this.extensions.add(extension);
    }

    public void removeExtension(ResultExtension extension) {
        this.extensions.remove(extension);
    }

    public void removeExtension(int index) {
        this.extensions.remove(index);
    }

    public RandomCollection<CustomItem> getRandomChoices(@Nullable Player player) {
        return (player == null ? getChoices() : getChoices(player)).stream().collect(RandomCollection.getCollector((rdmCollection, customItem) -> rdmCollection.add(customItem.getRarityPercentage(), customItem)));
    }

    public Optional<CustomItem> getItem(@Nullable Player player) {
        CustomItem item = cachedItems.computeIfAbsent(player == null ? null : player.getUniqueId(), uuid -> getRandomChoices(player).next());
        addCachedItem(player, item);
        return Optional.ofNullable(item);
    }

    public ItemStack getItem(CraftingData data, Player player) {
        return getItem(getItem(player).orElse(new CustomItem(Material.AIR)), data, player);
    }

    public ItemStack getItem(CustomItem customItem, CraftingData data, Player player) {
        if (target != null) {
            return target.mergeCraftingData(data, player, customItem, customItem.create());
        }
        return customItem.create();
    }

    public Optional<CustomItem> getItem(@NotNull Block block) {
        var vector = block.getLocation().toVector();
        var item = cachedBlockItems.computeIfAbsent(vector, uuid -> getRandomChoices(null).next());
        addCachedItem(vector, item);
        return Optional.ofNullable(item);

    }

    private void addCachedItem(Player player, CustomItem customItem) {
        if (player != null) {
            if (customItem == null) {
                cachedItems.remove(player.getUniqueId());
            } else {
                cachedItems.put(player.getUniqueId(), customItem);
            }
        }
    }

    public void removeCachedItem(Player player) {
        if (player != null) {
            cachedItems.remove(player.getUniqueId());
        }
    }

    private void addCachedItem(Vector block, CustomItem customItem) {
        if (block != null) {
            if (customItem == null) {
                cachedBlockItems.remove(block);
            } else {
                cachedBlockItems.put(block, customItem);
            }
        }
    }

    public void removeCachedItem(Block block) {
        if (block != null) {
            cachedBlockItems.remove(block.getLocation().toVector());
        }
    }

    public void executeExtensions(@NotNull Location location, boolean isWorkstation, @Nullable Player player) {
        Bukkit.getScheduler().runTaskLater(CustomCrafting.inst(), () -> extensions.forEach(resultExtension -> resultExtension.onCraft(location, isWorkstation, player)), 2);
    }
}
