package me.wolfyscript.customcrafting.gui.recipe_creator.recipe_creators;

import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.mobs.MythicMob;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.gui.recipe_creator.buttons.CauldronContainerButton;
import me.wolfyscript.customcrafting.gui.recipe_creator.buttons.ExactMetaButton;
import me.wolfyscript.customcrafting.gui.recipe_creator.buttons.PriorityButton;
import me.wolfyscript.customcrafting.recipes.types.cauldron.CauldronRecipe;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.*;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.inventory.InventoryUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;

public class CauldronCreator extends RecipeCreator {

    public CauldronCreator(GuiCluster<TestCache> cluster, CustomCrafting customCrafting) {
        super(cluster, "cauldron", 45, customCrafting);
    }

    @Override
    public void onInit() {
        super.onInit();

        registerButton(new ExactMetaButton());
        registerButton(new PriorityButton());

        registerButton(new DummyButton("cauldron", Material.CAULDRON));

        registerButton(new CauldronContainerButton(0, customCrafting));
        registerButton(new CauldronContainerButton(1, customCrafting));
        registerButton(new ItemInputButton("handItem_container", Material.AIR, (guiHandler, player, inventory, slot, event) -> {
            if (event.getClick().equals(ClickType.SHIFT_RIGHT)) {
                Bukkit.getScheduler().runTask(customCrafting, () -> {
                    if (inventory.getItem(slot) != null && !inventory.getItem(slot).getType().equals(Material.AIR)) {
                        TestCache cache = (TestCache) guiHandler.getCustomCache();
                        cache.getItems().setItem(true, CustomItem.getReferenceByItemStack(inventory.getItem(slot)));
                        cache.setApplyItem((items, cache1, customItem) -> cache1.getCauldronRecipe().setHandItem(items.getItem()));
                        guiHandler.changeToInv(new NamespacedKey("none", "item_editor"));
                    }
                });
                return true;
            }
            Bukkit.getScheduler().runTask(customCrafting, () -> ((TestCache) guiHandler.getCustomCache()).getCauldronRecipe().setHandItem(inventory.getItem(slot) != null && !inventory.getItem(slot).getType().equals(Material.AIR) ? CustomItem.getReferenceByItemStack(inventory.getItem(slot)) : new CustomItem(Material.AIR)));
            return false;
        }, (hashMap, guiHandler, player, itemStack, i, b) -> {
            CustomItem customItem = ((TestCache) guiHandler.getCustomCache()).getCauldronRecipe().getHandItem();
            if (customItem != null) {
                return customItem.getItemStack();
            }
            return itemStack;
        }));

        registerButton(new ToggleButton("dropItems", new ButtonState("dropItems.enabled", Material.DROPPER, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).getCauldronRecipe().setDropItems(false);
            return true;
        }), new ButtonState("dropItems.disabled", Material.CHEST, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).getCauldronRecipe().setDropItems(true);
            return true;
        })));
        registerButton(new ToggleButton("fire", new ButtonState("fire.enabled", Material.FLINT_AND_STEEL, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).getCauldronRecipe().setNeedsFire(false);
            return true;
        }), new ButtonState("fire.disabled", Material.FLINT, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).getCauldronRecipe().setNeedsFire(true);
            return true;
        })));
        registerButton(new ToggleButton("water", new ButtonState("water.enabled", Material.WATER_BUCKET, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).getCauldronRecipe().setNeedsWater(false);
            return true;
        }), new ButtonState("water.disabled", Material.BUCKET, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).getCauldronRecipe().setNeedsWater(true);
            return true;
        })));
        registerButton(new ChatInputButton("xp", Material.EXPERIENCE_BOTTLE, (hashMap, guiHandler, player, itemStack, slot, help) -> {
            hashMap.put("%xp%", ((TestCache) guiHandler.getCustomCache()).getCauldronRecipe().getXp());
            return itemStack;
        }, (guiHandler, player, s, args) -> {
            float xp;
            try {
                xp = Float.parseFloat(args[0]);
            } catch (NumberFormatException e) {
                api.getChat().sendPlayerMessage(player, "recipe_creator", "valid_number");
                return true;
            }
            ((TestCache) guiHandler.getCustomCache()).getCauldronRecipe().setXp(xp);
            return false;
        }));
        registerButton(new ChatInputButton("cookingTime", Material.CLOCK, (hashMap, guiHandler, player, itemStack, slot, help) -> {
            hashMap.put("%time%", ((TestCache) guiHandler.getCustomCache()).getCauldronRecipe().getCookingTime());
            return itemStack;
        }, (guiHandler, player, s, args) -> {
            int time;
            try {
                time = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                api.getChat().sendPlayerMessage(player, "recipe_creator", "valid_number");
                return true;
            }
            ((TestCache) guiHandler.getCustomCache()).getCauldronRecipe().setCookingTime(time);
            return false;
        }));
        registerButton(new ChatInputButton("waterLevel", Material.GLASS_BOTTLE, (hashMap, guiHandler, player, itemStack, slot, help) -> {
            hashMap.put("%level%", ((TestCache) guiHandler.getCustomCache()).getCauldronRecipe().getWaterLevel());
            return itemStack;
        }, (guiHandler, player, s, args) -> {
            int waterLvl;
            try {
                waterLvl = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                api.getChat().sendPlayerMessage(player, "recipe_creator", "valid_number");
                return true;
            }
            if (waterLvl > 3) {
                waterLvl = 3;
            }
            ((TestCache) guiHandler.getCustomCache()).getCauldronRecipe().setWaterLevel(waterLvl);
            return false;
        }));

        if (WolfyUtilities.hasMythicMobs()) {
            registerButton(new ActionButton("mythicMob", Material.WITHER_SKELETON_SKULL, (guiHandler, player, inventory, i, event) -> {
                if (event.getClick().isLeftClick()) {
                    openChat("mythicMob", (GuiHandler<TestCache>) guiHandler, (guiHandler1, player1, s, args) -> {
                        if (args.length > 1) {
                            CauldronRecipe recipe = ((TestCache) guiHandler.getCustomCache()).getCauldronRecipe();
                            MythicMob mythicMob = MythicMobs.inst().getMobManager().getMythicMob(args[0]);
                            if (mythicMob != null) {
                                int level = 1;
                                try {
                                    level = Integer.parseInt(args[1]);
                                } catch (NumberFormatException e) {
                                    api.getChat().sendPlayerMessage(player, "$msg.gui.recipe_creator.valid_number$");
                                    return true;
                                }
                                double modX = recipe.getMythicMobMod().getX();
                                double modY = recipe.getMythicMobMod().getY();
                                double modZ = recipe.getMythicMobMod().getZ();
                                if (args.length >= 5) {
                                    try {
                                        modX = Double.parseDouble(args[2]);
                                        modY = Double.parseDouble(args[3]);
                                        modZ = Double.parseDouble(args[4]);
                                    } catch (NumberFormatException e) {
                                        api.getChat().sendPlayerMessage(player, "$msg.gui.recipe_creator.valid_number$");
                                        return true;
                                    }
                                }
                                recipe.setMythicMob(args[0], level, modX, modY, modZ);
                                guiHandler.openCluster();
                                return false;
                            }
                        }
                        guiHandler.openCluster();
                        return true;
                    });
                } else {
                    ((TestCache) guiHandler.getCustomCache()).getCauldronRecipe().setMythicMob("<none>", 0, 0, 0.5, 0);
                }
                return true;
            }, (hashMap, guiHandler, player, itemStack, i, b) -> {
                hashMap.put("%mob.name%", ((TestCache) guiHandler.getCustomCache()).getCauldronRecipe().getMythicMobName());
                hashMap.put("%mob.level%", ((TestCache) guiHandler.getCustomCache()).getCauldronRecipe().getMythicMobLevel());
                hashMap.put("%mob.modX%", ((TestCache) guiHandler.getCustomCache()).getCauldronRecipe().getMythicMobMod().getX());
                hashMap.put("%mob.modY%", ((TestCache) guiHandler.getCustomCache()).getCauldronRecipe().getMythicMobMod().getY());
                hashMap.put("%mob.modZ%", ((TestCache) guiHandler.getCustomCache()).getCauldronRecipe().getMythicMobMod().getZ());
                return itemStack;
            }));
        }
    }

    @Override
    public void onUpdateAsync(GuiUpdate<TestCache> update) {
        super.onUpdateAsync(update);
        update.setButton(0, "back");
        TestCache cache = update.getGuiHandler().getCustomCache();
        CauldronRecipe cauldronRecipe = cache.getCauldronRecipe();
        ((ToggleButton) getButton("fire")).setState(update.getGuiHandler(), cauldronRecipe.needsFire());
        ((ToggleButton) getButton("water")).setState(update.getGuiHandler(), cauldronRecipe.needsWater());
        ((ToggleButton) getButton("dropItems")).setState(update.getGuiHandler(), cauldronRecipe.dropItems());
        ((ToggleButton) getButton("hidden")).setState(update.getGuiHandler(), cauldronRecipe.isHidden());

        update.setButton(1, "hidden");
        update.setButton(3, "recipe_creator", "conditions");
        update.setButton(5, "priority");
        update.setButton(7, "exact_meta");
        update.setButton(11, "cauldron.container_0");
        update.setButton(13, "cookingTime");

        update.setButton(19, "water");
        update.setButton(20, "cauldron");
        update.setButton(21, "waterLevel");

        update.setButton(23, "xp");
        update.setButton(25, "cauldron.container_1");

        update.setButton(29, "fire");
        update.setButton(34, "dropItems");

        if (!cauldronRecipe.dropItems()) {
            update.setButton(33, "handItem_container");
        }
        if (WolfyUtilities.hasMythicMobs()) {
            update.setButton(14, "mythicMob");
        }

        if(cauldronRecipe.hasNamespacedKey()){
            update.setButton(43, "save");
        }
        update.setButton(44, "save_as");
    }

    @Override
    public boolean validToSave(TestCache cache) {
        CauldronRecipe config = cache.getCauldronRecipe();
        return !InventoryUtils.isCustomItemsListEmpty(config.getIngredients()) && !InventoryUtils.isCustomItemsListEmpty(config.getResults());
    }
}
