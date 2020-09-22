package me.wolfyscript.customcrafting.gui.recipe_creator.recipe_creators;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.gui.recipe_creator.buttons.BrewingContainerButton;
import me.wolfyscript.customcrafting.gui.recipe_creator.buttons.ExactMetaButton;
import me.wolfyscript.customcrafting.gui.recipe_creator.buttons.PriorityButton;
import me.wolfyscript.customcrafting.recipes.types.brewing.BrewingRecipe;
import me.wolfyscript.utilities.api.inventory.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.InventoryAPI;
import me.wolfyscript.utilities.api.inventory.button.buttons.ActionButton;
import me.wolfyscript.utilities.api.inventory.button.buttons.ChatInputButton;
import me.wolfyscript.utilities.api.inventory.button.buttons.DummyButton;
import me.wolfyscript.utilities.api.inventory.button.buttons.ToggleButton;
import me.wolfyscript.utilities.api.utils.inventory.InventoryUtils;
import org.bukkit.Material;

public class BrewingCreator extends RecipeCreator {

    public BrewingCreator(InventoryAPI inventoryAPI, CustomCrafting customCrafting) {
        super("brewing_stand", inventoryAPI, 45, customCrafting);
    }

    @Override
    public void onInit() {
        super.onInit();

        registerButton(new ExactMetaButton());
        registerButton(new PriorityButton());

        registerButton(new DummyButton("brewing_stand", Material.BREWING_STAND));
        registerButton(new ChatInputButton("brewTime", Material.CLOCK, (hashMap, guiHandler, player, itemStack, slot, help) -> {
            hashMap.put("%time%", ((TestCache) guiHandler.getCustomCache()).getBrewingRecipe().getBrewTime());
            return itemStack;
        }, (guiHandler, player, s, args) -> {
            int time;
            try {
                time = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                api.sendPlayerMessage(player, "recipe_creator", "valid_number");
                return true;
            }
            ((TestCache) guiHandler.getCustomCache()).getBrewingRecipe().setBrewTime(time <= 400 ? time : 400);
            return false;
        }));
        registerButton(new ChatInputButton("fuelCost", Material.BLAZE_POWDER, (hashMap, guiHandler, player, itemStack, slot, help) -> {
            hashMap.put("%cost%", ((TestCache) guiHandler.getCustomCache()).getBrewingRecipe().getFuelCost());
            return itemStack;
        }, (guiHandler, player, s, args) -> {
            int cost;
            try {
                cost = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                api.sendPlayerMessage(player, "recipe_creator", "valid_number");
                return true;
            }
            ((TestCache) guiHandler.getCustomCache()).getBrewingRecipe().setFuelCost(cost);
            return false;
        }));
        registerButton(new BrewingContainerButton(0, customCrafting));
        registerButton(new BrewingContainerButton(1, customCrafting));

        registerButton(new ActionButton("potion_duration", Material.CLOCK, (guiHandler, player, inventory, i, event) -> {
            TestCache cache = ((TestCache) guiHandler.getCustomCache());
            BrewingRecipe brewingRecipe = cache.getBrewingRecipe();
            if (event.getClick().isRightClick()) {
                //Change Mode
                brewingRecipe.setDurationChange(0);
                return true;
            } else {
                //Change Value
                openChat("potion_duration", guiHandler, (guiHandler1, player1, s, strings) -> {
                    try {
                        int value = Integer.parseInt(s);
                        brewingRecipe.setDurationChange(value);
                    } catch (NumberFormatException ex) {
                        api.sendPlayerMessage(player1, "recipe_creator", "valid_number");
                    }
                    return false;
                });
            }
            return true;
        }, (hashMap, guiHandler, player, itemStack, i, b) -> {
            TestCache cache = ((TestCache) guiHandler.getCustomCache());
            hashMap.put("%value%", cache.getBrewingRecipe().getDurationChange());
            return itemStack;
        }));

        registerButton(new ActionButton("potion_amplifier", Material.IRON_SWORD, (guiHandler, player, inventory, i, event) -> {
            TestCache cache = ((TestCache) guiHandler.getCustomCache());
            BrewingRecipe brewingRecipe = cache.getBrewingRecipe();
            if (event.getClick().isRightClick()) {
                //Change Mode
                brewingRecipe.setDurationChange(0);
                return true;
            } else {
                //Change Value
                openChat("potion_amplifier", guiHandler, (guiHandler1, player1, s, strings) -> {
                    try {
                        int value = Integer.parseInt(s);
                        brewingRecipe.setAmplifierChange(value);
                    } catch (NumberFormatException ex) {
                        api.sendPlayerMessage(player1, "recipe_creator", "valid_number");
                    }
                    return false;
                });
            }
            return true;
        }, (hashMap, guiHandler, player, itemStack, i, b) -> {
            TestCache cache = ((TestCache) guiHandler.getCustomCache());
            hashMap.put("%value%", cache.getBrewingRecipe().getAmplifierChange());
            return itemStack;
        }));
    }


    @Override
    public void onUpdateAsync(GuiUpdate update) {
        update.setButton(0, "back");
        TestCache cache = (TestCache) update.getGuiHandler().getCustomCache();
        BrewingRecipe brewingRecipe = cache.getBrewingRecipe();
        ((ToggleButton) update.getGuiWindow().getButton("hidden")).setState(update.getGuiHandler(), brewingRecipe.isHidden());

        update.setButton(1, "hidden");
        update.setButton(3, "recipe_creator", "conditions");
        update.setButton(5, "priority");
        update.setButton(7, "exact_meta");

        update.setButton(11, "brewing.container_0");

        update.setButton(20, "brewing_stand");
        update.setButton(19, "brewTime");

        update.setButton(21, "fuelCost");
        //update.setButton(29, "brewing.container_1");

        update.setButton(23, "potion_duration");
        update.setButton(25, "potion_amplifier");

        if(brewingRecipe.hasNamespacedKey()){
            update.setButton(43, "save");
        }
        update.setButton(44, "save_as");
    }

    @Override
    public boolean validToSave(TestCache cache) {
        return !InventoryUtils.isCustomItemsListEmpty(cache.getBrewingRecipe().getIngredients());
    }
}
