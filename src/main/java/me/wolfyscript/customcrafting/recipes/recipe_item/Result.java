package me.wolfyscript.customcrafting.recipes.recipe_item;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.data.RecipeData;
import me.wolfyscript.customcrafting.recipes.recipe_item.extension.ResultExtension;
import me.wolfyscript.customcrafting.recipes.recipe_item.target.ResultTarget;
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
public class Result extends RecipeItemStack {

    @JsonIgnore
    private final Map<UUID, CustomItem> cachedItems = new HashMap<>();
    @JsonIgnore
    private final Map<Vector, CustomItem> cachedBlockItems = new HashMap<>();
    private ResultTarget target;
    private List<ResultExtension> extensions;

    public Result() {
        super();
        this.extensions = new ArrayList<>();
    }

    public Result(Result result) {
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
    public Result clone() {
        return new Result(this);
    }

    public ResultTarget getTarget() {
        return target;
    }

    public void setTarget(ResultTarget target) {
        this.target = target;
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

    /**
     * @param player The player to get the result for.
     * @return The optional {@link CustomItem} for that player. This might be a cached Item if the player hasn't taken it out previously.
     */
    public Optional<CustomItem> getItem(@Nullable Player player) {
        CustomItem item = cachedItems.computeIfAbsent(player == null ? null : player.getUniqueId(), uuid -> getRandomChoices(player).next());
        addCachedItem(player, item);
        return Optional.ofNullable(item);
    }

    /**
     * @param block The {@link Block} to get the result for.
     * @return The optional {@link CustomItem} for that block. This might be a cached Item if the block failed to processed it.
     */
    public Optional<CustomItem> getItem(@NotNull Block block) {
        var vector = block.getLocation().toVector();
        var item = cachedBlockItems.computeIfAbsent(vector, uuid -> getRandomChoices(null).next());
        addCachedItem(vector, item);
        return Optional.ofNullable(item);
    }

    /**
     * Combination of {@link #getItem(Player)} and {@link #getItem(Block)}.
     * <p>
     * If the player is available it returns the item for the player.
     * <br>
     * If the player is null, but the block is available it returns the item for the block.
     * </p>
     *
     * @param player The player to get the result for.
     * @param block  The {@link Block} to get the result for.
     * @return Either the item for the player or block, depending on which one is available.
     */
    public Optional<CustomItem> getItem(@Nullable Player player, @Nullable Block block) {
        if (player != null) {
            return getItem(player);
        }
        return block != null ? getItem(block) : Optional.empty();
    }

    public ItemStack getItem(RecipeData<?> recipeData, @Nullable Player player, @Nullable Block block) {
        return getItem(recipeData, getItem(player, block).orElse(new CustomItem(Material.AIR)), player, block);
    }

    public ItemStack getItem(RecipeData<?> recipeData, CustomItem chosenItem, @Nullable Player player, @Nullable Block block) {
        if (target != null) {
            return target.merge(recipeData, player, block, chosenItem, chosenItem.create());
        }
        return chosenItem.create();
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
