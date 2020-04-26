package me.wolfyscript.customcrafting.gui.recipe_creator.recipe_creators;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.PlayerStatistics;
import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.gui.ExtendedGuiWindow;
import me.wolfyscript.customcrafting.gui.recipe_creator.buttons.CookingContainerButton;
import me.wolfyscript.customcrafting.recipes.types.CookingConfig;
import me.wolfyscript.customcrafting.recipes.types.CustomRecipe;
import me.wolfyscript.customcrafting.recipes.types.blast_furnace.BlastingConfig;
import me.wolfyscript.customcrafting.recipes.types.blast_furnace.CustomBlastRecipe;
import me.wolfyscript.customcrafting.recipes.types.campfire.CampfireConfig;
import me.wolfyscript.customcrafting.recipes.types.campfire.CustomCampfireRecipe;
import me.wolfyscript.customcrafting.recipes.types.furnace.CustomFurnaceRecipe;
import me.wolfyscript.customcrafting.recipes.types.furnace.FurnaceConfig;
import me.wolfyscript.customcrafting.recipes.types.smoker.CustomSmokerRecipe;
import me.wolfyscript.customcrafting.recipes.types.smoker.SmokerConfig;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.GuiUpdateEvent;
import me.wolfyscript.utilities.api.inventory.InventoryAPI;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.buttons.ActionButton;
import me.wolfyscript.utilities.api.inventory.button.buttons.ChatInputButton;
import me.wolfyscript.utilities.api.inventory.button.buttons.ToggleButton;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;

public class CookingCreator extends ExtendedGuiWindow {

    public CookingCreator(InventoryAPI inventoryAPI, CustomCrafting customCrafting) {
        super("cooking", inventoryAPI, 45, customCrafting);
    }

    @Override
    public void onInit() {
        registerButton(new ActionButton("back", new ButtonState("none", "back", WolfyUtilities.getCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODY0Zjc3OWE4ZTNmZmEyMzExNDNmYTY5Yjk2YjE0ZWUzNWMxNmQ2NjllMTljNzVmZDFhN2RhNGJmMzA2YyJ9fX0="), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            guiHandler.openCluster("none");
            return true;
        })));
        registerButton(new ActionButton("save", new ButtonState("recipe_creator", "save", Material.WRITABLE_BOOK, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            TestCache cache = (TestCache) guiHandler.getCustomCache();
            if (validToSave(cache)) {
                openChat("recipe_creator","save.input", guiHandler, (guiHandler1, player1, s, args) -> {
                    TestCache cache1 = (TestCache)guiHandler1.getCustomCache();
                    CookingConfig cookingConfig = cache1.getCookingConfig();
                    if (args.length > 1) {
                        if (!cookingConfig.saveConfig(args[0], args[1], player1)) {
                            return true;
                        }
                        //TODO: RESET COOKING CONFIG!
                        try {
                            CustomRecipe customRecipe = null;
                            switch (cache1.getSetting()) {
                                case SMOKER:
                                    customRecipe = new CustomSmokerRecipe((SmokerConfig) cookingConfig);
                                    break;
                                case CAMPFIRE:
                                    customRecipe = new CustomCampfireRecipe((CampfireConfig) cookingConfig);
                                    break;
                                case FURNACE:
                                    customRecipe = new CustomFurnaceRecipe((FurnaceConfig) cookingConfig);
                                    break;
                                case BLAST_FURNACE:
                                    customRecipe = new CustomBlastRecipe((BlastingConfig) cookingConfig);
                            }
                            if (customRecipe != null) {
                                CustomRecipe finalCustomRecipe = customRecipe;
                                Bukkit.getScheduler().runTaskLater(customCrafting, () -> customCrafting.getRecipeHandler().injectRecipe(finalCustomRecipe), 1);
                            } else {
                                api.sendPlayerMessage(player, "recipe_creator", "loading.error", new String[]{"%REC%", cookingConfig.getNamespacedKey().toString()});
                            }
                            if (customCrafting.getConfigHandler().getConfig().isResetCreatorAfterSave()) {
                                cache.resetCookingConfig();
                            }
                        } catch (Exception ex) {
                            api.sendPlayerMessage(player, "recipe_creator", "loading.error", new String[]{"%REC%", cookingConfig.getNamespacedKey().toString()});
                            ex.printStackTrace();
                            return false;
                        }
                        Bukkit.getScheduler().runTaskLater(customCrafting, () -> guiHandler.openCluster("none"), 1);
                        return false;
                    }
                    return false;
                });
            } else {
                api.sendPlayerMessage(player, "recipe_creator", "save.empty");
            }
            return false;
        })));

        registerButton(new ToggleButton("hidden", new ButtonState("recipe_creator", "hidden.enabled", WolfyUtilities.getSkullViaURL("ce9d49dd09ecee2a4996965514d6d301bf12870c688acb5999b6658e1dfdff85"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).getCookingConfig().setHidden(false);
            return true;
        }), new ButtonState("recipe_creator", "hidden.disabled", WolfyUtilities.getSkullViaURL("85e5bf255d5d7e521474318050ad304ab95b01a4af0bae15e5cd9c1993abcc98"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).getCookingConfig().setHidden(true);
            return true;
        })));

        registerButton(new CookingContainerButton(0, customCrafting));
        registerButton(new CookingContainerButton(1, customCrafting));

        registerButton(new ChatInputButton("xp", new ButtonState("xp", Material.EXPERIENCE_BOTTLE, (hashMap, guiHandler, player, itemStack, slot, help) -> {
            hashMap.put("%XP%", ((TestCache) guiHandler.getCustomCache()).getCookingConfig().getXP());
            return itemStack;
        }), (guiHandler, player, s, args) -> {
            float xp;
            try {
                xp = Float.parseFloat(args[0]);
            } catch (NumberFormatException e) {
                api.sendPlayerMessage(player, "recipe_creator", "valid_number");
                return true;
            }
            ((TestCache) guiHandler.getCustomCache()).getCookingConfig().setXP(xp);
            return false;
        }));
        registerButton(new ChatInputButton("cooking_time", new ButtonState("cooking_time", Material.COAL, (hashMap, guiHandler, player, itemStack, slot, help) -> {
            hashMap.put("%TIME%", ((TestCache) guiHandler.getCustomCache()).getCookingConfig().getCookingTime());
            return itemStack;
        }), (guiHandler, player, s, args) -> {
            int time;
            try {
                time = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                api.sendPlayerMessage(player, "recipe_creator", "valid_number");
                return true;
            }
            ((TestCache) guiHandler.getCustomCache()).getCookingConfig().setCookingTime(time);
            return false;
        }));
    }

    @EventHandler
    public void onUpdate(GuiUpdateEvent event) {
        if (event.verify(this)) {
            event.setButton(0, "back");
            TestCache cache = (TestCache) event.getGuiHandler().getCustomCache();
            ((ToggleButton) event.getGuiWindow().getButton("hidden")).setState(event.getGuiHandler(), cache.getCookingConfig().isHidden());

            PlayerStatistics playerStatistics = CustomCrafting.getPlayerStatistics(event.getPlayer());

            event.setButton(3, "hidden");
            event.setButton(5, "recipe_creator", "conditions");
            event.setButton(20, "none", playerStatistics.getDarkMode() ? "glass_gray" : "glass_white");
            event.setButton(11, "cooking.container_0");
            event.setButton(24, "cooking.container_1");
            event.setButton(10, "none", playerStatistics.getDarkMode() ? "glass_gray" : "glass_white");
            event.setButton(12, "none", playerStatistics.getDarkMode() ? "glass_gray" : "glass_white");
            event.setButton(22, "xp");
            event.setButton(29, "cooking_time");
            event.setButton(44, "save");
        }
    }

    private boolean validToSave(TestCache cache) {
        switch (cache.getSetting()) {
            case BLAST_FURNACE:
            case SMOKER:
            case CAMPFIRE:
            case FURNACE:
                CookingConfig furnace = cache.getCookingConfig();
                if (furnace.getSource() != null && !furnace.getSource().isEmpty() && furnace.getResult() != null && !furnace.getResult().isEmpty())
                    return true;
        }
        return false;
    }
}
