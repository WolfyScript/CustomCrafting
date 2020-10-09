package me.wolfyscript.customcrafting.gui.recipe_creator.recipe_creators.brewing;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.PlayerStatistics;
import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.data.cache.BrewingGUICache;
import me.wolfyscript.customcrafting.gui.recipe_creator.buttons.BrewingContainerButton;
import me.wolfyscript.customcrafting.gui.recipe_creator.buttons.BrewingOptionButton;
import me.wolfyscript.customcrafting.gui.recipe_creator.buttons.ExactMetaButton;
import me.wolfyscript.customcrafting.gui.recipe_creator.buttons.PriorityButton;
import me.wolfyscript.customcrafting.gui.recipe_creator.recipe_creators.RecipeCreator;
import me.wolfyscript.customcrafting.recipes.types.brewing.BrewingRecipe;
import me.wolfyscript.utilities.api.inventory.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.InventoryAPI;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.buttons.ActionButton;
import me.wolfyscript.utilities.api.inventory.button.buttons.DummyButton;
import me.wolfyscript.utilities.api.inventory.button.buttons.ToggleButton;
import me.wolfyscript.utilities.api.utils.inventory.InventoryUtils;
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

        registerButton(new DummyButton("brewing_stand", Material.BREWING_STAND));
        /*
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
         */
        registerButton(new BrewingContainerButton(0, customCrafting));
        registerButton(new BrewingContainerButton(1, customCrafting));

        registerButton(new DummyButton("allowed_items", Material.POTION));

        //Initialize simple option buttons
        registerButton(new ActionButton("duration_change", Material.LINGERING_POTION, (guiHandler, player, inventory, i, event) -> {
            TestCache cache = ((TestCache) guiHandler.getCustomCache());
            BrewingRecipe brewingRecipe = cache.getBrewingRecipe();
            if (event.getClick().isRightClick()) {
                //Change Mode
                brewingRecipe.setDurationChange(0);
                return true;
            }
            //Change Value
            openChat("duration_change", guiHandler, (guiHandler1, player1, s, strings) -> {
                try {
                    int value = Integer.parseInt(s);
                    brewingRecipe.setDurationChange(value);
                } catch (NumberFormatException ex) {
                    api.sendPlayerMessage(player1, "recipe_creator", "valid_number");
                }
                return false;
            });
            return true;
        }, (hashMap, guiHandler, player, itemStack, i, b) -> {
            TestCache cache = ((TestCache) guiHandler.getCustomCache());
            hashMap.put("%value%", cache.getBrewingRecipe().getDurationChange());
            return itemStack;
        }));
        registerButton(new ActionButton("amplifier_change", Material.IRON_SWORD, (guiHandler, player, inventory, i, event) -> {
            TestCache cache = ((TestCache) guiHandler.getCustomCache());
            BrewingRecipe brewingRecipe = cache.getBrewingRecipe();
            if (event.getClick().isRightClick()) {
                //Change Mode
                brewingRecipe.setDurationChange(0);
                return true;
            }
            //Change Value
            openChat("amplifier_change", guiHandler, (guiHandler1, player1, s, strings) -> {
                try {
                    int value = Integer.parseInt(s);
                    brewingRecipe.setAmplifierChange(value);
                } catch (NumberFormatException ex) {
                    api.sendPlayerMessage(player1, "recipe_creator", "valid_number");
                }
                return false;
            });
            return true;
        }, (hashMap, guiHandler, player, itemStack, i, b) -> {
            TestCache cache = ((TestCache) guiHandler.getCustomCache());
            hashMap.put("%value%", cache.getBrewingRecipe().getAmplifierChange());
            return itemStack;
        }));

        registerButton(new ToggleButton("reset_effects", new ButtonState("reset_effects.enabled", Material.BARRIER, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).getBrewingRecipe().setResetEffects(false);
            return true;
        }), new ButtonState("reset_effects.disabled", Material.BARRIER, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).getBrewingRecipe().setResetEffects(true);
            return true;
        })));
        registerButton(new ActionButton("effect_color", Material.RED_DYE, (guiHandler, player, inventory, i, event) -> {
            TestCache cache = ((TestCache) guiHandler.getCustomCache());
            BrewingRecipe brewingRecipe = cache.getBrewingRecipe();
            if (event.getClick().isRightClick()) {
                //Change Mode
                brewingRecipe.setEffectColor(null);
                return true;
            }
            //Change Value
            openChat("effect_color", guiHandler, (guiHandler1, player1, s, args) -> {
                if (args.length > 2) {
                    try {
                        int red = Integer.parseInt(args[0]);
                        int green = Integer.parseInt(args[1]);
                        int blue = Integer.parseInt(args[2]);
                        brewingRecipe.setEffectColor(Color.fromRGB(red, green, blue));
                    } catch (NumberFormatException ex) {
                        api.sendPlayerMessage(player1, "recipe_creator", "valid_number");
                    }
                }
                return false;
            });
            return true;
        }, (hashMap, guiHandler, player, itemStack, i, b) -> {
            TestCache cache = ((TestCache) guiHandler.getCustomCache());
            hashMap.put("%value%", cache.getBrewingRecipe().getEffectColor());
            return itemStack;
        }));

        registerButton(new BrewingOptionButton(Material.BARRIER, "effect_removals"));


        registerButton(new BrewingOptionButton(Material.ITEM_FRAME, "result"));
        registerButton(new ActionButton("result.info", Material.BOOK));
        registerButton(new BrewingContainerButton(2, customCrafting));

        registerButton(new BrewingOptionButton(Material.ANVIL, "effect_additions"));


        registerButton(new BrewingOptionButton(Material.ENCHANTED_BOOK, "effect_upgrades"));


        registerButton(new BrewingOptionButton(Material.BOOKSHELF, "required_effects"));

    }


    @Override
    public void onUpdateAsync(GuiUpdate update) {
        super.onUpdateAsync(update);
        PlayerStatistics playerStatistics = CustomCrafting.getPlayerStatistics(update.getPlayer());
        update.setButton(0, "back");
        TestCache cache = update.getGuiHandler(TestCache.class).getCustomCache();
        BrewingGUICache brewingGUICache = cache.getBrewingGUICache();
        BrewingRecipe brewingRecipe = cache.getBrewingRecipe();

        ((ToggleButton) getButton("hidden")).setState(update.getGuiHandler(), brewingRecipe.isHidden());

        update.setButton(1, "hidden");
        update.setButton(3, "recipe_creator", "conditions");
        update.setButton(5, "priority");
        update.setButton(7, "exact_meta");

        update.setButton(9, "brewing.container_0");
        update.setButton(10, "brewing_stand");

        update.setButton(36, "brewing.container_1");
        update.setButton(37, "allowed_items");

        //Simple Options
        update.setButton(11, "duration_change");
        update.setButton(20, "amplifier_change");
        update.setButton(29, "effect_color");
        update.setButton(38, "reset_effects");

        update.setButton(12, "none", playerStatistics.getDarkMode() ? "glass_gray" : "glass_white");
        update.setButton(21, "none", playerStatistics.getDarkMode() ? "glass_gray" : "glass_white");
        update.setButton(30, "none", playerStatistics.getDarkMode() ? "glass_gray" : "glass_white");
        update.setButton(39, "none", playerStatistics.getDarkMode() ? "glass_gray" : "glass_white");


        update.setButton(13, "effect_removals.option");
        update.setButton(14, "result.option");
        update.setButton(15, "effect_additions.option");
        update.setButton(16, "effect_upgrades.option");
        update.setButton(17, "required_effects.option");

        switch (brewingGUICache.getOption()) {
            case "result":
                update.setButton(32, "brewing.container_2");
                update.setButton(34, "result.info");
                break;

        }
        //requiredEffects
        //effectRemovals
        //effectAdditions
        //effectUpgrades
        //Result Items

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
