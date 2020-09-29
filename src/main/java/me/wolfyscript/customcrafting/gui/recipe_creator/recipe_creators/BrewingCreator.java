package me.wolfyscript.customcrafting.gui.recipe_creator.recipe_creators;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.gui.recipe_creator.buttons.BrewingContainerButton;
import me.wolfyscript.customcrafting.gui.recipe_creator.buttons.ExactMetaButton;
import me.wolfyscript.customcrafting.gui.recipe_creator.buttons.PriorityButton;
import me.wolfyscript.customcrafting.recipes.types.brewing.BrewingRecipe;
import me.wolfyscript.utilities.api.inventory.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.InventoryAPI;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.buttons.ActionButton;
import me.wolfyscript.utilities.api.inventory.button.buttons.ChatInputButton;
import me.wolfyscript.utilities.api.inventory.button.buttons.DummyButton;
import me.wolfyscript.utilities.api.inventory.button.buttons.ToggleButton;
import me.wolfyscript.utilities.api.utils.inventory.InventoryUtils;
import me.wolfyscript.utilities.api.utils.inventory.PlayerHeadUtils;
import org.bukkit.Color;
import org.bukkit.Material;

public class BrewingCreator extends RecipeCreator {

    public BrewingCreator(InventoryAPI inventoryAPI, CustomCrafting customCrafting) {
        super("brewing_stand", inventoryAPI, 54, customCrafting);
    }

    @Override
    public void onInit() {
        super.onInit();

        registerButton(new ExactMetaButton());
        registerButton(new PriorityButton());

        registerButton(new ActionButton("page_up", PlayerHeadUtils.getViaURL("1ad6c81f899a785ecf26be1dc48eae2bcfe777a862390f5785e95bd83bd14d"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {

            return true;
        }));
        registerButton(new ActionButton("page_down", PlayerHeadUtils.getViaURL("882faf9a584c4d676d730b23f8942bb997fa3dad46d4f65e288c39eb471ce7"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {

            return true;
        }));

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
            ((TestCache) guiHandler.getCustomCache()).getBrewingRecipe().setBrewTime(Math.min(time, 400));
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
        registerButton(new DummyButton("allowed_items", Material.POTION));

        //Initialize simple option buttons
        registerButton(new ActionButton("duration_change", Material.LINGERING_POTION, (guiHandler, player, inventory, i, event) -> {
            TestCache cache = ((TestCache) guiHandler.getCustomCache());
            BrewingRecipe brewingRecipe = cache.getBrewingRecipe();
            if (event.getClick().isRightClick()) {
                //Change Mode
                brewingRecipe.getGlobalOptions().setDurationChange(0);
                return true;
            }
            //Change Value
            openChat("duration_change", guiHandler, (guiHandler1, player1, s, strings) -> {
                try {
                    int value = Integer.parseInt(s);
                    brewingRecipe.getGlobalOptions().setDurationChange(value);
                } catch (NumberFormatException ex) {
                    api.sendPlayerMessage(player1, "recipe_creator", "valid_number");
                }
                return false;
            });
            return true;
        }, (hashMap, guiHandler, player, itemStack, i, b) -> {
            TestCache cache = ((TestCache) guiHandler.getCustomCache());
            hashMap.put("%value%", cache.getBrewingRecipe().getGlobalOptions().getDurationChange());
            return itemStack;
        }));
        registerButton(new ActionButton("amplifier_change", Material.IRON_SWORD, (guiHandler, player, inventory, i, event) -> {
            TestCache cache = ((TestCache) guiHandler.getCustomCache());
            BrewingRecipe brewingRecipe = cache.getBrewingRecipe();
            if (event.getClick().isRightClick()) {
                //Change Mode
                brewingRecipe.getGlobalOptions().setDurationChange(0);
                return true;
            }
            //Change Value
            openChat("amplifier_change", guiHandler, (guiHandler1, player1, s, strings) -> {
                try {
                    int value = Integer.parseInt(s);
                    brewingRecipe.getGlobalOptions().setAmplifierChange(value);
                } catch (NumberFormatException ex) {
                    api.sendPlayerMessage(player1, "recipe_creator", "valid_number");
                }
                return false;
            });
            return true;
        }, (hashMap, guiHandler, player, itemStack, i, b) -> {
            TestCache cache = ((TestCache) guiHandler.getCustomCache());
            hashMap.put("%value%", cache.getBrewingRecipe().getGlobalOptions().getAmplifierChange());
            return itemStack;
        }));

        registerButton(new ToggleButton("reset_effects", new ButtonState("reset_effects.enabled", Material.POTION, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).getBrewingRecipe().getGlobalOptions().setResetEffects(false);
            return true;
        }), new ButtonState("reset_effects.disabled", Material.POTION, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).getBrewingRecipe().getGlobalOptions().setResetEffects(true);
            return true;
        })));
        registerButton(new ActionButton("effect_color", Material.INK_SAC, (guiHandler, player, inventory, i, event) -> {
            TestCache cache = ((TestCache) guiHandler.getCustomCache());
            BrewingRecipe brewingRecipe = cache.getBrewingRecipe();
            if (event.getClick().isRightClick()) {
                //Change Mode
                brewingRecipe.getGlobalOptions().setEffectColor(null);
                return true;
            }
            //Change Value
            openChat("effect_color", guiHandler, (guiHandler1, player1, s, args) -> {
                if (args.length > 2) {
                    try {
                        int red = Integer.parseInt(args[0]);
                        int green = Integer.parseInt(args[1]);
                        int blue = Integer.parseInt(args[2]);
                        brewingRecipe.getGlobalOptions().setEffectColor(Color.fromRGB(red, green, blue));
                    } catch (NumberFormatException ex) {
                        api.sendPlayerMessage(player1, "recipe_creator", "valid_number");
                    }
                }
                return false;
            });
            return true;
        }, (hashMap, guiHandler, player, itemStack, i, b) -> {
            TestCache cache = ((TestCache) guiHandler.getCustomCache());
            hashMap.put("%value%", cache.getBrewingRecipe().getGlobalOptions().getEffectColor());
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

        update.setButton(9, "brewing.container_0");
        update.setButton(10, "brewing_stand");

        update.setButton(18, "brewing.container_1");
        update.setButton(19, "allowed_items");
        update.setButton(28, "fuelCost");
        update.setButton(37, "brewTime");

        update.setButton(11, "none", "glass_white");
        update.setButton(20, "none", "glass_white");
        update.setButton(29, "none", "glass_white");
        update.setButton(38, "none", "glass_white");

        //Simple Options
        update.setButton(13, "duration_change");
        update.setButton(14, "amplifier_change");
        update.setButton(15, "effect_color");
        update.setButton(16, "reset_effects");

        //requiredEffects
        //effectRemovals
        //effectAdditions
        //effectUpgrades
        //Result Items

        //Advanced Options
        //-required options
        //  -> requiredEffects
        //  -> allowedItems
        //-

        if (brewingRecipe.hasNamespacedKey()) {
            update.setButton(52, "save");
        }
        update.setButton(53, "save_as");
    }

    @Override
    public boolean validToSave(TestCache cache) {
        return !InventoryUtils.isCustomItemsListEmpty(cache.getBrewingRecipe().getIngredients());
    }
}
