package me.wolfyscript.customcrafting.gui.recipe_creator;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.PlayerCache;
import me.wolfyscript.customcrafting.gui.ExtendedGuiWindow;
import me.wolfyscript.customcrafting.gui.Setting;
import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.conditions.EliteWorkbenchCondition;
import me.wolfyscript.customcrafting.recipes.conditions.PermissionCondition;
import me.wolfyscript.customcrafting.recipes.conditions.WeatherCondition;
import me.wolfyscript.customcrafting.recipes.conditions.WorldTimeCondition;
import me.wolfyscript.customcrafting.recipes.types.RecipeConfig;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.GuiHandler;
import me.wolfyscript.utilities.api.inventory.GuiUpdateEvent;
import me.wolfyscript.utilities.api.inventory.InventoryAPI;
import me.wolfyscript.utilities.api.inventory.button.ButtonActionRender;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.buttons.ActionButton;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class ConditionsMenu extends ExtendedGuiWindow {

    public ConditionsMenu(InventoryAPI inventoryAPI) {
        super("conditions", inventoryAPI, 45);
    }

    @Override
    public void onInit() {
        registerButton(new ActionButton("back", new ButtonState("none", "back", WolfyUtilities.getCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODY0Zjc3OWE4ZTNmZmEyMzExNDNmYTY5Yjk2YjE0ZWUzNWMxNmQ2NjllMTljNzVmZDFhN2RhNGJmMzA2YyJ9fX0="), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            guiHandler.openPreviousInv();
            return true;
        })));

        registerButton(new ActionButton("conditions.world_time", new ButtonState("world_time", Material.CLOCK, new ButtonActionRender() {
            @Override
            public boolean run(GuiHandler guiHandler, Player player, Inventory inventory, int slot, InventoryClickEvent event) {
                if (event.getClick().isRightClick()) {
                    //Change Mode
                    CustomCrafting.getPlayerCache(player).getRecipeConfig().getConditions().getByID("world_time").toggleOption();
                } else {
                    //Change Value
                    openChat("world_time", guiHandler, (guiHandler1, player1, s, strings) -> {
                        try {
                            long value = Long.parseLong(s);
                            ((WorldTimeCondition) CustomCrafting.getPlayerCache(player).getRecipeConfig().getConditions().getByID("world_time")).setTime(value);
                        } catch (NumberFormatException ex) {
                            api.sendPlayerMessage(player1, "recipe_creator", "valid_number");
                        }
                        return false;
                    });
                }
                return true;
            }

            @Override
            public ItemStack render(HashMap<String, Object> hashMap, GuiHandler guiHandler, Player player, ItemStack itemStack, int slot, boolean b) {
                RecipeConfig recipeConfig = CustomCrafting.getPlayerCache(player).getRecipeConfig();
                hashMap.put("%VALUE%", ((WorldTimeCondition) recipeConfig.getConditions().getByID("world_time")).getTime());
                hashMap.put("%MODE%", recipeConfig.getConditions().getByID("world_time").getOption().getDisplayString(api));
                return itemStack;
            }
        })));

        registerButton(new ActionButton("conditions.weather", new ButtonState("weather", Material.WATER_BUCKET, new ButtonActionRender() {
            @Override
            public boolean run(GuiHandler guiHandler, Player player, Inventory inventory, int slot, InventoryClickEvent event) {
                RecipeConfig recipeConfig = CustomCrafting.getPlayerCache(player).getRecipeConfig();
                if (event.getClick().isRightClick()) {
                    //Change Mode
                    recipeConfig.getConditions().getByID("weather").toggleOption();
                } else {
                    //Change Value
                    ((WeatherCondition) recipeConfig.getConditions().getByID("weather")).toggleWeather();
                }
                return true;
            }

            @Override
            public ItemStack render(HashMap<String, Object> hashMap, GuiHandler guiHandler, Player player, ItemStack itemStack, int slot, boolean b) {
                RecipeConfig recipeConfig = CustomCrafting.getPlayerCache(player).getRecipeConfig();
                hashMap.put("%VALUE%", ((WeatherCondition) recipeConfig.getConditions().getByID("weather")).getWeather().getDisplay(api));
                hashMap.put("%MODE%", recipeConfig.getConditions().getByID("weather").getOption().getDisplayString(api));
                return itemStack;
            }
        })));

        registerButton(new ActionButton("conditions.advanced_workbench", new ButtonState("advanced_workbench", Material.CRAFTING_TABLE, new ButtonActionRender() {
            @Override
            public boolean run(GuiHandler guiHandler, Player player, Inventory inventory, int slot, InventoryClickEvent event) {
                if (event.getClick().isLeftClick()) {
                    //Change Mode
                    CustomCrafting.getPlayerCache(player).getRecipeConfig().getConditions().getByID("advanced_workbench").toggleOption();
                }
                return true;
            }

            @Override
            public ItemStack render(HashMap<String, Object> hashMap, GuiHandler guiHandler, Player player, ItemStack itemStack, int slot, boolean b) {
                hashMap.put("%MODE%", CustomCrafting.getPlayerCache(player).getRecipeConfig().getConditions().getByID("advanced_workbench").getOption().getDisplayString(api));
                return itemStack;
            }
        })));

        registerButton(new ActionButton("conditions.elite_workbench", new ButtonState("elite_workbench", Material.CRAFTING_TABLE, new ButtonActionRender() {
            @Override
            public boolean run(GuiHandler guiHandler, Player player, Inventory inventory, int slot, InventoryClickEvent event) {
                RecipeConfig recipeConfig = CustomCrafting.getPlayerCache(player).getRecipeConfig();
                if (event.getClick().isRightClick()) {
                    //Change Mode
                    recipeConfig.getConditions().getByID("elite_workbench").toggleOption();
                } else {
                    //CONFIGURE ELITE WORKBENCHES
                    openChat("elite_workbench", guiHandler, (guiHandler1, player1, s, strings) -> {
                        ((EliteWorkbenchCondition) CustomCrafting.getPlayerCache(player1).getRecipeConfig().getConditions().getByID("elite_workbench")).addEliteWorkbenches(s);
                        return false;
                    });
                }
                return true;
            }

            @Override
            public ItemStack render(HashMap<String, Object> hashMap, GuiHandler guiHandler, Player player, ItemStack itemStack, int slot, boolean b) {
                hashMap.put("%MODE%", CustomCrafting.getPlayerCache(player).getRecipeConfig().getConditions().getByID("elite_workbench").getOption().getDisplayString(api));
                return itemStack;
            }
        })));

        registerButton(new ActionButton("conditions.permission", new ButtonState("permission", Material.REDSTONE, new ButtonActionRender() {
            @Override
            public boolean run(GuiHandler guiHandler, Player player, Inventory inventory, int slot, InventoryClickEvent event) {
                RecipeConfig recipeConfig = CustomCrafting.getPlayerCache(player).getRecipeConfig();
                if (event.getClick().isRightClick()) {
                    //Change Mode
                    recipeConfig.getConditions().getByID("permission").toggleOption();
                } else {
                    //SET Custom Permission String
                    openChat("permission", guiHandler, (guiHandler1, player1, s, strings) -> {
                        ((PermissionCondition) CustomCrafting.getPlayerCache(player1).getRecipeConfig().getConditions().getByID("permission")).setPermission(s.trim());
                        return false;
                    });
                }
                return true;
            }

            @Override
            public ItemStack render(HashMap<String, Object> hashMap, GuiHandler guiHandler, Player player, ItemStack itemStack, int slot, boolean b) {
                RecipeConfig recipeConfig = CustomCrafting.getPlayerCache(player).getRecipeConfig();
                hashMap.put("%VALUE%", ((PermissionCondition) recipeConfig.getConditions().getByID("permission")).getPermission());
                hashMap.put("%MODE%", recipeConfig.getConditions().getByID("permission").getOption().getDisplayString(api));
                return itemStack;
            }
        })));

    }

    @EventHandler
    public void onUpdate(GuiUpdateEvent event) {
        if (event.verify(this)) {
            PlayerCache cache = CustomCrafting.getPlayerCache(event.getPlayer());
            event.setButton(0, "back");

            event.setButton(9, "conditions.world_time");
            event.setButton(10, "conditions.weather");
            event.setButton(11, "conditions.permission");
            if (cache.getSetting().equals(Setting.WORKBENCH)) {
                event.setButton(12, "conditions.advanced_workbench");
            } else {
                event.setButton(12, "conditions.elite_workbench");
            }
        }

    }
}
