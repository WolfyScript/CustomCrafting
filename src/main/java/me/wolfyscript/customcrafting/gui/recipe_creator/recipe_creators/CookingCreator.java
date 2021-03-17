package me.wolfyscript.customcrafting.gui.recipe_creator.recipe_creators;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.CCPlayerData;
import me.wolfyscript.customcrafting.gui.recipe_creator.buttons.CookingContainerButton;
import me.wolfyscript.customcrafting.recipes.types.CustomCookingRecipe;
import me.wolfyscript.customcrafting.utils.PlayerUtil;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ChatInputButton;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ToggleButton;
import me.wolfyscript.utilities.util.inventory.InventoryUtils;
import org.bukkit.Material;

public class CookingCreator extends RecipeCreator {

    public CookingCreator(GuiCluster<CCCache> cluster, CustomCrafting customCrafting) {
        super(cluster, "cooking", 45, customCrafting);
    }

    @Override
    public void onInit() {
        super.onInit();

        registerButton(new CookingContainerButton(0));
        registerButton(new CookingContainerButton(1));

        registerButton(new ChatInputButton<>("xp", Material.EXPERIENCE_BOTTLE, (hashMap, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            hashMap.put("%XP%", guiHandler.getCustomCache().getCookingRecipe().getExp());
            return itemStack;
        }, (guiHandler, player, s, args) -> {
            float xp;
            try {
                xp = Float.parseFloat(args[0]);
            } catch (NumberFormatException e) {
                api.getChat().sendKey(player, getCluster(), "valid_number");
                return true;
            }
            guiHandler.getCustomCache().getCookingRecipe().setExp(xp);
            return false;
        }));
        registerButton(new ChatInputButton<>("cooking_time", Material.COAL, (hashMap, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            hashMap.put("%TIME%", guiHandler.getCustomCache().getCookingRecipe().getCookingTime());
            return itemStack;
        }, (guiHandler, player, s, args) -> {
            int time;
            try {
                time = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                api.getChat().sendKey(player, getCluster(), "valid_number");
                return true;
            }
            guiHandler.getCustomCache().getCookingRecipe().setCookingTime(time);
            return false;
        }));
    }

    @Override
    public void onUpdateSync(GuiUpdate<CCCache> guiUpdate) {

    }

    @Override
    public void onUpdateAsync(GuiUpdate update) {
        super.onUpdateAsync(update);
        update.setButton(0, "back");
        CCCache cache = (CCCache) update.getGuiHandler().getCustomCache();
        ((ToggleButton) getButton("hidden")).setState(update.getGuiHandler(), cache.getCookingRecipe().isHidden());

        CCPlayerData data = PlayerUtil.getStore(update.getPlayer());

        update.setButton(3, "hidden");
        update.setButton(5, "recipe_creator", "conditions");
        update.setButton(20, "none", data.isDarkMode() ? "glass_gray" : "glass_white");
        update.setButton(11, "cooking.container_0");
        update.setButton(24, "cooking.container_1");
        update.setButton(10, "none", data.isDarkMode() ? "glass_gray" : "glass_white");
        update.setButton(12, "none", data.isDarkMode() ? "glass_gray" : "glass_white");
        update.setButton(22, "xp");
        update.setButton(29, "cooking_time");

        if (cache.getCookingRecipe().hasNamespacedKey()) {
            update.setButton(43, "save");
        }
        update.setButton(44, "save_as");
    }

    public boolean validToSave(CCCache cache) {
        switch (cache.getRecipeType().getType()) {
            case BLAST_FURNACE:
            case SMOKER:
            case CAMPFIRE:
            case FURNACE:
                CustomCookingRecipe<?, ?> furnace = cache.getCookingRecipe();
                if (!InventoryUtils.isCustomItemsListEmpty(furnace.getSource()) && !InventoryUtils.isCustomItemsListEmpty(furnace.getResults()))
                    return true;
        }
        return false;
    }
}
