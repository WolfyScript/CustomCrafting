/*
 *       ____ _  _ ____ ___ ____ _  _ ____ ____ ____ ____ ___ _ _  _ ____
 *       |    |  | [__   |  |  | |\/| |    |__/ |__| |___  |  | |\ | | __
 *       |___ |__| ___]  |  |__| |  | |___ |  \ |  | |     |  | | \| |__]
 *
 *       CustomCrafting Recipe creation and management tool for Minecraft
 *                      Copyright (C) 2021  WolfyScript
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.wolfyscript.customcrafting.recipes.items;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.data.RecipeData;
import me.wolfyscript.customcrafting.recipes.items.extension.ExecutionType;
import me.wolfyscript.customcrafting.recipes.items.extension.ResultExtension;
import me.wolfyscript.customcrafting.recipes.items.target.ResultTarget;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.custom_items.references.APIReference;
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
import java.util.stream.Collectors;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Result extends RecipeItemStack {

    @JsonIgnore
    private final Map<UUID, CustomItem> cachedItems = new HashMap<>();
    @JsonIgnore
    private final Map<Vector, CustomItem> cachedBlockItems = new HashMap<>();
    private ResultTarget target;
    private List<ResultExtension> extensions;
    @JsonIgnore
    private List<ResultExtension> bulkExtensions = new ArrayList<>();

    public Result() {
        super();
        this.extensions = new ArrayList<>();
    }

    public Result(Result result) {
        super(result);
        setExtensions(result.extensions);
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
        this.bulkExtensions = this.extensions.stream().filter(resultExtension -> resultExtension.getExecutionType().equals(ExecutionType.BULK)).collect(Collectors.toList());
    }

    public void addExtension(ResultExtension extension) {
        this.extensions.add(extension);
        if (extension.getExecutionType().equals(ExecutionType.BULK)) {
           this.bulkExtensions.add(extension);
        }
    }

    public void removeExtension(ResultExtension extension) {
        this.extensions.remove(extension);
        this.bulkExtensions.remove(extension);
    }

    public void removeExtension(int index) {
        removeExtension(this.extensions.get(index));
    }

    public RandomCollection<CustomItem> getRandomChoices(@Nullable Player player) {
        return (player == null ? getChoices() : getChoices(player)).stream().collect(RandomCollection.getCollector((rdmCollection, customItem) -> rdmCollection.add(customItem.getWeight(), customItem)));
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
        executeExtensions(location, isWorkstation, player, 1);
    }

    public void executeExtensions(@NotNull Location location, boolean isWorkstation, @Nullable Player player, int amountOfExecutions) {
        executeExtensions(this.extensions, location, isWorkstation, player);
        if (amountOfExecutions > 1) {
            for (int i = 0; i < amountOfExecutions - 1; i++) {
                executeExtensions(this.bulkExtensions, location, isWorkstation, player);
            }
        }
    }

    private void executeExtensions(List<ResultExtension> extensions, @NotNull Location location, boolean isWorkstation, @Nullable Player player) {
        Bukkit.getScheduler().runTaskLater(CustomCrafting.inst(), () -> extensions.forEach(resultExtension -> resultExtension.onCraft(location, isWorkstation, player)), 2);
    }

}
