package me.wolfyscript.customcrafting.gui.recipe_creator;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.CCWindow;
import me.wolfyscript.customcrafting.gui.MainCluster;
import me.wolfyscript.customcrafting.gui.RecipeCreatorCluster;
import me.wolfyscript.customcrafting.gui.recipe_creator.buttons.ButtonContainerItemIngredient;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ToggleButton;
import me.wolfyscript.utilities.util.inventory.PlayerHeadUtils;
import org.bukkit.Material;

public class IngredientMenu extends CCWindow {

    private static final String REPLACE_WITH_REMAINS = "replace_with_remains";

    public IngredientMenu(GuiCluster<CCCache> cluster, CustomCrafting customCrafting) {
        super(cluster, "ingredient", 54, customCrafting);
        setForceSyncUpdate(true);
    }

    @Override
    public void onInit() {
        for (int i = 0; i < 36; i++) {
            registerButton(new ButtonContainerItemIngredient(i));
        }
        registerButton(new ActionButton<>("back", new ButtonState<>(MainCluster.BACK, PlayerHeadUtils.getViaURL("864f779a8e3ffa231143fa69b96b14ee35c16d669e19c75fd1a7da4bf306c"), (cache, guiHandler, player, inventory, slot, event) -> {
            cache.getRecipeCreatorCache().getRecipeCache().applyIngredientCache();
            guiHandler.openPreviousWindow();
            return true;
        })));
        registerButton(new ActionButton<>("tags", new ButtonState<>(RecipeCreatorCluster.TAGS, Material.NAME_TAG, (cache, guiHandler, player, guiInventory, i, inventoryInteractEvent) -> {
            cache.getRecipeCreatorCache().getTagSettingsCache().setRecipeItemStack(cache.getRecipeCreatorCache().getIngredientCache().getIngredient());
            guiHandler.openWindow("tag_settings");
            return true;
        })));
        registerButton(new ToggleButton<>(REPLACE_WITH_REMAINS, (cache, guiHandler, player, guiInventory, i) -> cache.getRecipeCreatorCache().getIngredientCache().getIngredient().isReplaceWithRemains(), new ButtonState<>(REPLACE_WITH_REMAINS + ".enabled", Material.BUCKET, (cache, guiHandler, player, guiInventory, i, inventoryInteractEvent) -> {
            cache.getRecipeCreatorCache().getIngredientCache().getIngredient().setReplaceWithRemains(false);
            return true;
        }), new ButtonState<>(REPLACE_WITH_REMAINS + ".disabled", Material.BUCKET, (cache, guiHandler, player, guiInventory, i, inventoryInteractEvent) -> {
            cache.getRecipeCreatorCache().getIngredientCache().getIngredient().setReplaceWithRemains(true);
            return true;
        })));
    }

    @Override
    public void onUpdateAsync(GuiUpdate<CCCache> update) {
        super.onUpdateAsync(update);
        update.setButton(0, "back");
        for (int i = 0; i < 36; i++) {
            update.setButton(9 + i, "item_container_" + i);
        }
        update.setButton(48, "tags");
        update.setButton(50, REPLACE_WITH_REMAINS);
    }
}
