package me.wolfyscript.customcrafting.gui.recipe_creator.recipe_creators;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.PlayerStatistics;
import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.gui.recipe_creator.buttons.CookingContainerButton;
import me.wolfyscript.customcrafting.recipes.types.CustomCookingRecipe;
import me.wolfyscript.utilities.api.inventory.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.InventoryAPI;
import me.wolfyscript.utilities.api.inventory.button.buttons.ChatInputButton;
import me.wolfyscript.utilities.api.inventory.button.buttons.ToggleButton;
import me.wolfyscript.utilities.api.utils.inventory.InventoryUtils;
import org.bukkit.Material;

public class CookingCreator extends RecipeCreator {

    public CookingCreator(InventoryAPI inventoryAPI, CustomCrafting customCrafting) {
        super("cooking", inventoryAPI, 45, customCrafting);
    }

    @Override
    public void onInit() {
        super.onInit();

        registerButton(new CookingContainerButton(0, customCrafting));
        registerButton(new CookingContainerButton(1, customCrafting));

        registerButton(new ChatInputButton("xp", Material.EXPERIENCE_BOTTLE, (hashMap, guiHandler, player, itemStack, slot, help) -> {
            hashMap.put("%XP%", ((TestCache) guiHandler.getCustomCache()).getCookingRecipe().getExp());
            return itemStack;
        }, (guiHandler, player, s, args) -> {
            float xp;
            try {
                xp = Float.parseFloat(args[0]);
            } catch (NumberFormatException e) {
                api.sendPlayerMessage(player, "recipe_creator", "valid_number");
                return true;
            }
            ((TestCache) guiHandler.getCustomCache()).getCookingRecipe().setExp(xp);
            return false;
        }));
        registerButton(new ChatInputButton("cooking_time", Material.COAL, (hashMap, guiHandler, player, itemStack, slot, help) -> {
            hashMap.put("%TIME%", ((TestCache) guiHandler.getCustomCache()).getCookingRecipe().getCookingTime());
            return itemStack;
        }, (guiHandler, player, s, args) -> {
            int time;
            try {
                time = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                api.sendPlayerMessage(player, "recipe_creator", "valid_number");
                return true;
            }
            ((TestCache) guiHandler.getCustomCache()).getCookingRecipe().setCookingTime(time);
            return false;
        }));
    }

    @Override
    public void onUpdateAsync(GuiUpdate update) {
        super.onUpdateAsync(update);
        update.setButton(0, "back");
        TestCache cache = (TestCache) update.getGuiHandler().getCustomCache();
        ((ToggleButton) getButton("hidden")).setState(update.getGuiHandler(), cache.getCookingRecipe().isHidden());

        PlayerStatistics playerStatistics = CustomCrafting.getPlayerStatistics(update.getPlayer());

        update.setButton(3, "hidden");
        update.setButton(5, "recipe_creator", "conditions");
        update.setButton(20, "none", playerStatistics.getDarkMode() ? "glass_gray" : "glass_white");
        update.setButton(11, "cooking.container_0");
        update.setButton(24, "cooking.container_1");
        update.setButton(10, "none", playerStatistics.getDarkMode() ? "glass_gray" : "glass_white");
        update.setButton(12, "none", playerStatistics.getDarkMode() ? "glass_gray" : "glass_white");
        update.setButton(22, "xp");
        update.setButton(29, "cooking_time");

        if(cache.getCookingRecipe().hasNamespacedKey()){
            update.setButton(43, "save");
        }
        update.setButton(44, "save_as");
    }

    public boolean validToSave(TestCache cache) {
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
