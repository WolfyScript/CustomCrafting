package me.wolfyscript.customcrafting.gui.recipe_creator.recipe_creators;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.PlayerCache;
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

import java.util.Locale;

public class CookingCreator extends ExtendedGuiWindow {

    public CookingCreator(InventoryAPI inventoryAPI) {
        super("cooking", inventoryAPI, 45);
    }

    @Override
    public void onInit() {
        registerButton(new ActionButton("back", new ButtonState("none", "back", WolfyUtilities.getCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODY0Zjc3OWE4ZTNmZmEyMzExNDNmYTY5Yjk2YjE0ZWUzNWMxNmQ2NjllMTljNzVmZDFhN2RhNGJmMzA2YyJ9fX0="), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            guiHandler.openCluster("none");
            return true;
        })));
        registerButton(new ActionButton("save", new ButtonState("recipe_creator","save", Material.WRITABLE_BOOK, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            PlayerCache cache = CustomCrafting.getPlayerCache(player);
            if (validToSave(cache)) {
                openChat("save.input", guiHandler, (guiHandler1, player1, s, args) -> {
                    PlayerCache cache1 = CustomCrafting.getPlayerCache(player1);
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
                                Bukkit.getScheduler().runTaskLater(CustomCrafting.getInst(), () -> CustomCrafting.getRecipeHandler().injectRecipe(finalCustomRecipe), 1);
                            } else {
                                api.sendPlayerMessage(player, "recipe_creator","error_loading", new String[]{"%REC%", cookingConfig.getId()});
                            }
                        } catch (Exception ex) {
                            api.sendPlayerMessage(player, "recipe_creator","error_loading", new String[]{"%REC%", cookingConfig.getId()});
                            ex.printStackTrace();
                            return false;
                        }
                        Bukkit.getScheduler().runTaskLater(CustomCrafting.getInst(), () -> guiHandler.changeToInv("main_menu"), 1);
                        return false;

                    }
                    return false;
                });
            } else {
                api.sendPlayerMessage(player, "recipe_creator","save.empty");
            }
            return false;
        })));

        registerButton(new CookingContainerButton(0));
        registerButton(new CookingContainerButton(1));

        registerButton(new ChatInputButton("xp", new ButtonState("xp", Material.EXPERIENCE_BOTTLE, (hashMap, guiHandler, player, itemStack, slot, help) -> {
            hashMap.put("%XP%", CustomCrafting.getPlayerCache(player).getCookingConfig().getXP());
            return itemStack;
        }), (guiHandler, player, s, args) -> {
            float xp;
            try {
                xp = Float.parseFloat(args[0]);
            } catch (NumberFormatException e) {
                api.sendPlayerMessage(player, "recipe_creator", "valid_number");
                return true;
            }
            CustomCrafting.getPlayerCache(player).getCookingConfig().setXP(xp);
            return false;
        }));
        registerButton(new ChatInputButton("cooking_time", new ButtonState("cooking_time", Material.COAL, (hashMap, guiHandler, player, itemStack, slot, help) -> {
            hashMap.put("%TIME%", CustomCrafting.getPlayerCache(player).getCookingConfig().getCookingTime());
            return itemStack;
        }), (guiHandler, player, s, args) -> {
            int time;
            try {
                time = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                api.sendPlayerMessage(player, "recipe_creator", "valid_number");
                return true;
            }
            CustomCrafting.getPlayerCache(player).getCookingConfig().setCookingTime(time);
            return false;
        }));
    }

    @EventHandler
    public void onUpdate(GuiUpdateEvent event) {
        if (event.verify(this)) {
            event.setButton(0, "back");
            PlayerCache cache = CustomCrafting.getPlayerCache(event.getPlayer());
            switch (cache.getSetting()) {
                case BLAST_FURNACE:
                case SMOKER:
                case CAMPFIRE:
                case FURNACE:
                    event.setButton(20, "none", cache.getDarkMode() ? "glass_gray" : "glass_white");
                    event.setButton(11, "cooking.container_0");
                    event.setButton(24, "cooking.container_1");
                    event.setButton(10, "none", cache.getDarkMode() ? "glass_gray" : "glass_white");
                    event.setButton(12, "none", cache.getDarkMode() ? "glass_gray" : "glass_white");
                    event.setButton(22, "xp");
                    event.setButton(29, "cooking_time");
                    event.setButton(44, "save");
                    break;
            }

        }
    }

    private boolean validToSave(PlayerCache cache) {
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
