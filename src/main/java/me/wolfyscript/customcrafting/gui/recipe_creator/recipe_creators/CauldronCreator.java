package me.wolfyscript.customcrafting.gui.recipe_creator.recipe_creators;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.MainCluster;
import me.wolfyscript.customcrafting.gui.RecipeCreatorCluster;
import me.wolfyscript.customcrafting.gui.recipe_creator.buttons.ButtonRecipeIngredient;
import me.wolfyscript.customcrafting.gui.recipe_creator.buttons.ButtonRecipeResult;
import me.wolfyscript.customcrafting.recipes.types.cauldron.CauldronRecipe;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ChatInputButton;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.DummyButton;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ItemInputButton;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ToggleButton;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

public class CauldronCreator extends RecipeCreator {

    public CauldronCreator(GuiCluster<CCCache> cluster, CustomCrafting customCrafting) {
        super(cluster, "cauldron", 45, customCrafting);
    }

    @Override
    public void onInit() {
        super.onInit();

        registerButton(new DummyButton<>("cauldron", Material.CAULDRON));

        registerButton(new ButtonRecipeIngredient(0));
        registerButton(new ButtonRecipeResult());
        registerButton(new ItemInputButton<>("handItem_container", new ButtonState<>("handItem_container", Material.AIR, (cache, guiHandler, player, inventory, slot, event) -> {
            if (event instanceof InventoryClickEvent && ((InventoryClickEvent) event).getClick().equals(ClickType.SHIFT_RIGHT)) {
                Bukkit.getScheduler().runTask(customCrafting, () -> {
                    if (inventory.getItem(slot) != null && !inventory.getItem(slot).getType().equals(Material.AIR)) {
                        cache.getItems().setItem(true, CustomItem.getReferenceByItemStack(inventory.getItem(slot)));
                        cache.setApplyItem((items, cache1, customItem) -> cache1.getCauldronRecipe().setHandItem(items.getItem()));
                        guiHandler.openWindow(MainCluster.ITEM_EDITOR);
                    }
                });
                return true;
            }
            return false;
        }, (cache, guiHandler, player, inventory, itemStack, slot, event) -> {
            if (event instanceof InventoryClickEvent && ((InventoryClickEvent) event).getClick().equals(ClickType.SHIFT_RIGHT) && event.getView().getTopInventory().equals(((InventoryClickEvent) event).getClickedInventory())) {
                return;
            }
            guiHandler.getCustomCache().getCauldronRecipe().setHandItem(!ItemUtils.isAirOrNull(inventory.getItem(slot)) ? CustomItem.getReferenceByItemStack(inventory.getItem(slot)) : new CustomItem(Material.AIR));
        }, null, (hashMap, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            CustomItem customItem = guiHandler.getCustomCache().getCauldronRecipe().getHandItem();
            if (customItem != null) {
                return customItem.getItemStack();
            }
            return itemStack;
        })));

        registerButton(new ToggleButton<>("dropItems", new ButtonState<>("dropItems.enabled", Material.DROPPER, (cache, guiHandler, player, inventory, slot, event) -> {
            guiHandler.getCustomCache().getCauldronRecipe().setDropItems(false);
            return true;
        }), new ButtonState<>("dropItems.disabled", Material.CHEST, (cache, guiHandler, player, inventory, slot, event) -> {
            guiHandler.getCustomCache().getCauldronRecipe().setDropItems(true);
            return true;
        })));
        registerButton(new ToggleButton<>("fire", new ButtonState<>("fire.enabled", Material.FLINT_AND_STEEL, (cache, guiHandler, player, inventory, slot, event) -> {
            guiHandler.getCustomCache().getCauldronRecipe().setNeedsFire(false);
            return true;
        }), new ButtonState<>("fire.disabled", Material.FLINT, (cache, guiHandler, player, inventory, slot, event) -> {
            guiHandler.getCustomCache().getCauldronRecipe().setNeedsFire(true);
            return true;
        })));
        registerButton(new ToggleButton<>("water", new ButtonState<>("water.enabled", Material.WATER_BUCKET, (cache, guiHandler, player, inventory, slot, event) -> {
            guiHandler.getCustomCache().getCauldronRecipe().setNeedsWater(false);
            return true;
        }), new ButtonState<>("water.disabled", Material.BUCKET, (cache, guiHandler, player, inventory, slot, event) -> {
            guiHandler.getCustomCache().getCauldronRecipe().setNeedsWater(true);
            return true;
        })));
        registerButton(new ChatInputButton<>("xp", Material.EXPERIENCE_BOTTLE, (hashMap, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            hashMap.put("%xp%", guiHandler.getCustomCache().getCauldronRecipe().getXp());
            return itemStack;
        }, (guiHandler, player, s, args) -> {
            float xp;
            try {
                xp = Float.parseFloat(args[0]);
            } catch (NumberFormatException e) {
                api.getChat().sendKey(player, getCluster(), "valid_number");
                return true;
            }
            guiHandler.getCustomCache().getCauldronRecipe().setXp(xp);
            return false;
        }));
        registerButton(new ChatInputButton<>("cookingTime", Material.CLOCK, (hashMap, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            hashMap.put("%time%", guiHandler.getCustomCache().getCauldronRecipe().getCookingTime());
            return itemStack;
        }, (guiHandler, player, s, args) -> {
            int time;
            try {
                time = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                api.getChat().sendKey(player, getCluster(), "valid_number");
                return true;
            }
            guiHandler.getCustomCache().getCauldronRecipe().setCookingTime(time);
            return false;
        }));
        registerButton(new ChatInputButton<>("waterLevel", Material.GLASS_BOTTLE, (hashMap, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            hashMap.put("%level%", guiHandler.getCustomCache().getCauldronRecipe().getWaterLevel());
            return itemStack;
        }, (guiHandler, player, s, args) -> {
            int waterLvl;
            try {
                waterLvl = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                api.getChat().sendKey(player, getCluster(), "valid_number");
                return true;
            }
            if (waterLvl > 3) {
                waterLvl = 3;
            }
            guiHandler.getCustomCache().getCauldronRecipe().setWaterLevel(waterLvl);
            return false;
        }));
    }

    @Override
    public void onUpdateAsync(GuiUpdate<CCCache> update) {
        super.onUpdateAsync(update);
        update.setButton(0, BACK);
        CCCache cache = update.getGuiHandler().getCustomCache();
        CauldronRecipe cauldronRecipe = cache.getCauldronRecipe();
        ((ToggleButton<CCCache>) getButton("fire")).setState(update.getGuiHandler(), cauldronRecipe.needsFire());
        ((ToggleButton<CCCache>) getButton("water")).setState(update.getGuiHandler(), cauldronRecipe.needsWater());
        ((ToggleButton<CCCache>) getButton("dropItems")).setState(update.getGuiHandler(), cauldronRecipe.dropItems());
        ((ToggleButton<CCCache>) getCluster().getButton("hidden")).setState(update.getGuiHandler(), cauldronRecipe.isHidden());

        update.setButton(1, RecipeCreatorCluster.HIDDEN);
        update.setButton(3, RecipeCreatorCluster.CONDITIONS);
        update.setButton(5, RecipeCreatorCluster.PRIORITY);
        update.setButton(7, RecipeCreatorCluster.EXACT_META);
        update.setButton(11, "recipe.ingredient_0");
        update.setButton(13, "cookingTime");

        update.setButton(19, "water");
        update.setButton(20, "cauldron");
        update.setButton(21, "waterLevel");

        update.setButton(23, "xp");
        update.setButton(25, "recipe.result");

        update.setButton(29, "fire");
        update.setButton(34, "dropItems");

        if (!cauldronRecipe.dropItems()) {
            update.setButton(33, "handItem_container");
        }

        update.setButton(42, RecipeCreatorCluster.GROUP);
        if(cauldronRecipe.hasNamespacedKey()){
            update.setButton(43, RecipeCreatorCluster.SAVE);
        }
        update.setButton(44, RecipeCreatorCluster.SAVE_AS);
    }

    @Override
    public boolean validToSave(CCCache cache) {
        CauldronRecipe config = cache.getCauldronRecipe();
        return !config.getIngredient().isEmpty() && !config.getResult().isEmpty();
    }
}
