package me.wolfyscript.customcrafting.gui.recipe_creator;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.cache.RecipeData;
import me.wolfyscript.customcrafting.gui.ExtendedGuiWindow;
import me.wolfyscript.customcrafting.recipes.conditions.WeatherCondition;
import me.wolfyscript.customcrafting.recipes.conditions.WorldTimeCondition;
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
                if(event.getClick().isRightClick()){
                    //Change Mode
                    RecipeData recipeData = CustomCrafting.getPlayerCache(player).getRecipeData();
                    recipeData.getConditions().getByID("world_time").toggleOption();
                }else{
                    //Change Value
                    openChat(guiHandler, "$$", (guiHandler1, player1, s, strings) -> {
                        try {
                            long value = Long.parseLong(s);
                            RecipeData recipeData = CustomCrafting.getPlayerCache(player).getRecipeData();
                            ((WorldTimeCondition) recipeData.getConditions().getByID("world_time")).setTime(value);
                        }catch (NumberFormatException ex){
                            api.sendPlayerMessage(player1, "$$");
                        }
                        return false;
                    });
                }
                return true;
            }

            @Override
            public ItemStack render(HashMap<String, Object> hashMap, GuiHandler guiHandler, Player player, ItemStack itemStack, int slot, boolean b) {
                RecipeData recipeData = CustomCrafting.getPlayerCache(player).getRecipeData();
                hashMap.put("%VALUE%", ((WorldTimeCondition)recipeData.getConditions().getByID("world_time")).getTime());
                hashMap.put("%MODE%", recipeData.getConditions().getByID("world_time").getOption().getDisplayString(api));
                return itemStack;
            }
        })));

        registerButton(new ActionButton("conditions.weather", new ButtonState("weather", Material.WATER_BUCKET, new ButtonActionRender() {
            @Override
            public boolean run(GuiHandler guiHandler, Player player, Inventory inventory, int slot, InventoryClickEvent event) {
                RecipeData recipeData = CustomCrafting.getPlayerCache(player).getRecipeData();
                if(event.getClick().isRightClick()){
                    //Change Mode
                    recipeData.getConditions().getByID("weather").toggleOption();
                }else{
                    //Change Value
                    ((WeatherCondition)recipeData.getConditions().getByID("weather")).toggleWeather();
                }
                return true;
            }

            @Override
            public ItemStack render(HashMap<String, Object> hashMap, GuiHandler guiHandler, Player player, ItemStack itemStack, int slot, boolean b) {
                RecipeData recipeData = CustomCrafting.getPlayerCache(player).getRecipeData();
                hashMap.put("%VALUE%", ((WeatherCondition)recipeData.getConditions().getByID("weather")).getWeather().getDisplay(api));
                hashMap.put("%MODE%", recipeData.getConditions().getByID("weather").getOption().getDisplayString(api));
                return itemStack;
            }
        })));

    }

    @EventHandler
    public void onUpdate(GuiUpdateEvent event) {
        if(event.verify(this)){
            event.setButton(0, "back");
        }
        event.setButton(9, "conditions.world_time");
        event.setButton(10, "conditions.weather");
    }
}
