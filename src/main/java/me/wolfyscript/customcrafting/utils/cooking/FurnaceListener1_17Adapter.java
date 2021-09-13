package me.wolfyscript.customcrafting.utils.cooking;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.Pair;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceStartSmeltEvent;

public class FurnaceListener1_17Adapter implements Listener {

    private final CustomCrafting customCrafting;
    private final CookingManager manager;

    public FurnaceListener1_17Adapter(CustomCrafting customCrafting, CookingManager manager) {
        this.customCrafting = customCrafting;
        this.manager = manager;
    }

    @EventHandler
    public void onStartSmelt(FurnaceStartSmeltEvent event) {
        var recipe = event.getRecipe();
        if (recipe.getKey().getNamespace().equals(NamespacedKeyUtils.NAMESPACE)) {
            var data = manager.getAdapter().processRecipe(event.getSource(), NamespacedKeyUtils.toInternal(NamespacedKey.fromBukkit(recipe.getKey())), event.getBlock());
            if(data.getKey() == null) {
                event.setTotalCookTime(0);
            }
            manager.cacheRecipeData(event.getBlock(), data);
        } else {
            manager.cacheRecipeData(event.getBlock(), new Pair<>(null, false));
        }
    }
}
