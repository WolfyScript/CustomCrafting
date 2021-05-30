package me.wolfyscript.customcrafting.utils;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.types.workbench.CraftingData;

import java.util.UUID;

/**
 * Replaced by {@link CraftManager}. Accessed using {@link CustomCrafting#getCraftManager()}
 */
@Deprecated
public class RecipeUtils {

    private final CraftManager craftManager;

    RecipeUtils(CraftManager craftManager) {
        this.craftManager = craftManager;
    }

    /**
     * @deprecated Replaced by {@link CraftManager#put(UUID, CraftingData)}
     */
    @Deprecated
    public void put(UUID uuid, CraftingData craftingData) {
        craftManager.put(uuid, craftingData);
    }

    /**
     * @deprecated Replaced by {@link CraftManager#remove(UUID)}
     */
    @Deprecated
    public void remove(UUID uuid) {
        craftManager.remove(uuid);
    }

    /**
     * @deprecated Replaced by {@link CraftManager#has(UUID)}
     */
    @Deprecated
    public boolean has(UUID uuid) {
        return craftManager.has(uuid);
    }

}
