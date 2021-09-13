package me.wolfyscript.customcrafting.utils.cooking;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.data.CookingRecipeData;
import me.wolfyscript.utilities.util.Pair;
import org.bukkit.block.Block;
import org.bukkit.block.Furnace;
import org.bukkit.event.inventory.FurnaceSmeltEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CookingManager {

    private final CustomCrafting plugin;
    final Map<Block, Pair<CookingRecipeData<?>, Boolean>> cachedRecipeData = new ConcurrentHashMap<>();
    private final SmeltAPIAdapter smeltAdapter;

    public CookingManager(CustomCrafting plugin) {
        this.plugin = plugin;
        this.smeltAdapter = plugin.isPaper() ? new PaperSmeltAPIAdapter(plugin, this) : new BukkitSmeltAPIAdapter(plugin, this);
    }

    public SmeltAPIAdapter getAdapter() {
        return smeltAdapter;
    }

    /**
     * Checks and caches if the cooked/smelted recipe is a custom recipe.<br>
     * The cached value will be removed after a specific delay (currently 4 ticks).
     *
     * @param event The {@link FurnaceSmeltEvent}
     */
    private Pair<CookingRecipeData<?>, Boolean> checkEvent(FurnaceSmeltEvent event) {
        var block = event.getBlock();
        return smeltAdapter.process(event, block, (Furnace) block.getState());
    }

    void cacheRecipeData(Block block, Pair<CookingRecipeData<?>, Boolean> data) {
        cachedRecipeData.put(block, data);
    }

    void clearCache(Block block) {
        cachedRecipeData.remove(block);
    }

    /**
     * Checks if the cooked/smelted recipe is a custom recipe.<br>
     * The first invocation of this method will run the check.<br>
     * After that first call, invocations will use the cached value instead.<br>
     *
     * @param event The {@link FurnaceSmeltEvent}
     * @return If the recipe of the event is a custom recipe.
     */
    public boolean hasCustomRecipe(FurnaceSmeltEvent event) {
        return getCustomRecipeCache(event).getValue();
    }

    /**
     * Checks if the cooked/smelted recipe is a custom recipe.<br>
     * The first invocation of this method will run the check.<br>
     * After that first call, invocations will use the cached value instead.<br>
     *
     * @param event The {@link FurnaceSmeltEvent}
     * @return The {@link CookingRecipeData} of the custom recipe. Null if the event doesn't contain a custom recipe.
     */
    public Pair<CookingRecipeData<?>, Boolean> getCustomRecipeCache(FurnaceSmeltEvent event) {
        return cachedRecipeData.computeIfAbsent(event.getBlock(), block -> checkEvent(event));
    }

}
