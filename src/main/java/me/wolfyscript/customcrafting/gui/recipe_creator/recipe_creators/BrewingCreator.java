package me.wolfyscript.customcrafting.gui.recipe_creator.recipe_creators;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.gui.recipe_creator.buttons.BrewingContainerButton;
import me.wolfyscript.customcrafting.gui.recipe_creator.buttons.ExactMetaButton;
import me.wolfyscript.customcrafting.gui.recipe_creator.buttons.PriorityButton;
import me.wolfyscript.customcrafting.recipes.types.brewing.BrewingRecipe;
import me.wolfyscript.utilities.api.inventory.GuiUpdateEvent;
import me.wolfyscript.utilities.api.inventory.InventoryAPI;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.buttons.ActionButton;
import me.wolfyscript.utilities.api.inventory.button.buttons.ChatInputButton;
import me.wolfyscript.utilities.api.inventory.button.buttons.DummyButton;
import me.wolfyscript.utilities.api.inventory.button.buttons.ToggleButton;
import me.wolfyscript.utilities.api.utils.inventory.InventoryUtils;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;

public class BrewingCreator extends RecipeCreator {

    public BrewingCreator(InventoryAPI inventoryAPI, CustomCrafting customCrafting) {
        super("brewing_stand", inventoryAPI, 45, customCrafting);
    }

    @Override
    public void onInit() {
        super.onInit();

        registerButton(new ExactMetaButton());
        registerButton(new PriorityButton());

        registerButton(new DummyButton("brewing_stand", new ButtonState("brewing_stand", Material.BREWING_STAND)));
        registerButton(new ChatInputButton("brewTime", new ButtonState("brewTime", Material.CLOCK, (hashMap, guiHandler, player, itemStack, slot, help) -> {
            hashMap.put("%time%", ((TestCache) guiHandler.getCustomCache()).getBrewingRecipe().getBrewTime());
            return itemStack;
        }), (guiHandler, player, s, args) -> {
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
        registerButton(new ChatInputButton("fuelCost", new ButtonState("fuelCost", Material.BLAZE_POWDER, (hashMap, guiHandler, player, itemStack, slot, help) -> {
            hashMap.put("%cost%", ((TestCache) guiHandler.getCustomCache()).getBrewingRecipe().getFuelCost());
            return itemStack;
        }), (guiHandler, player, s, args) -> {
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

        registerButton(new ActionButton("potion_duration", new ButtonState("potion_duration", Material.CLOCK, (guiHandler, player, inventory, i, event) -> {
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
        })));

        registerButton(new ActionButton("potion_amplifier", new ButtonState("potion_amplifier", Material.IRON_SWORD, (guiHandler, player, inventory, i, event) -> {
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
        })));
    }

    @EventHandler
    public void onUpdate(GuiUpdateEvent event) {
        if (event.verify(this)) {
            event.setButton(0, "back");
            TestCache cache = (TestCache) event.getGuiHandler().getCustomCache();
            BrewingRecipe brewingRecipe = cache.getBrewingRecipe();
            ((ToggleButton) event.getGuiWindow().getButton("hidden")).setState(event.getGuiHandler(), brewingRecipe.isHidden());

            event.setButton(1, "hidden");
            event.setButton(3, "recipe_creator", "conditions");
            event.setButton(5, "priority");
            event.setButton(7, "exact_meta");

            event.setButton(11, "brewing.container_0");

            event.setButton(20, "brewing_stand");
            event.setButton(19, "brewTime");

            event.setButton(21, "fuelCost");
            //event.setButton(29, "brewing.container_1");

            event.setButton(23, "potion_duration");
            event.setButton(25, "potion_amplifier");

            event.setButton(44, "save");
        }
    }

    @Override
    public boolean validToSave(TestCache cache) {
        return !InventoryUtils.isCustomItemsListEmpty(cache.getBrewingRecipe().getIngredients());
    }
}
