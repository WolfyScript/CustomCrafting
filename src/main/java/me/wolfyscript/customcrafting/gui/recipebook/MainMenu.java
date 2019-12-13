package me.wolfyscript.customcrafting.gui.recipebook;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.PlayerCache;
import me.wolfyscript.customcrafting.gui.ExtendedGuiWindow;
import me.wolfyscript.customcrafting.gui.Setting;
import me.wolfyscript.customcrafting.handlers.RecipeHandler;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.GuiUpdateEvent;
import me.wolfyscript.utilities.api.inventory.InventoryAPI;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.buttons.ActionButton;
import me.wolfyscript.utilities.api.utils.item_builder.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemFlag;

import java.util.ArrayList;
import java.util.List;

public class MainMenu extends ExtendedGuiWindow {

    public MainMenu(InventoryAPI inventoryAPI) {
        super("main_menu", inventoryAPI, 45);
    }

    @Override
    public void onInit() {
        registerButton(new ActionButton("workbench", new ButtonState("workbench", Material.CRAFTING_TABLE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            CustomCrafting.getPlayerCache(player).getKnowledgeBook().setSetting(Setting.WORKBENCH);
            guiHandler.changeToInv("recipe_book");
            return true;
        })));
        registerButton(new ActionButton("furnace", new ButtonState("furnace", Material.FURNACE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            CustomCrafting.getPlayerCache(player).getKnowledgeBook().setSetting(Setting.FURNACE);
            guiHandler.changeToInv("recipe_book");
            return true;
        })));
        registerButton(new ActionButton("anvil", new ButtonState("anvil", Material.ANVIL, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            CustomCrafting.getPlayerCache(player).getKnowledgeBook().setSetting(Setting.ANVIL);
            guiHandler.changeToInv("recipe_book");
            return true;
        })));
        if (WolfyUtilities.hasVillagePillageUpdate()) {
            registerButton(new ActionButton("blast_furnace", new ButtonState("blast_furnace", Material.BLAST_FURNACE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                CustomCrafting.getPlayerCache(player).getKnowledgeBook().setSetting(Setting.BLAST_FURNACE);
                guiHandler.changeToInv("recipe_book");
                return true;
            })));
            registerButton(new ActionButton("smoker", new ButtonState("smoker", Material.SMOKER, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                CustomCrafting.getPlayerCache(player).getKnowledgeBook().setSetting(Setting.SMOKER);
                guiHandler.changeToInv("recipe_book");
                return true;
            })));
            registerButton(new ActionButton("campfire", new ButtonState("campfire", Material.CAMPFIRE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                CustomCrafting.getPlayerCache(player).getKnowledgeBook().setSetting(Setting.CAMPFIRE);
                guiHandler.changeToInv("recipe_book");
                return true;
            })));
            registerButton(new ActionButton("stonecutter", new ButtonState("stonecutter", Material.STONECUTTER, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                CustomCrafting.getPlayerCache(player).getKnowledgeBook().setSetting(Setting.STONECUTTER);
                guiHandler.changeToInv("recipe_book");
                return true;
            })));
            registerButton(new ActionButton("elite_workbench", new ButtonState("elite_workbench", new ItemBuilder(Material.CRAFTING_TABLE).addItemFlags(ItemFlag.HIDE_ENCHANTS).addUnsafeEnchantment(Enchantment.DURABILITY, 0).create(), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                CustomCrafting.getPlayerCache(player).getKnowledgeBook().setSetting(Setting.ELITE_WORKBENCH);
                guiHandler.changeToInv("recipe_book");
                return true;
            })));
        }
    }

    @EventHandler
    private void onUpdate(GuiUpdateEvent event) {
        if (event.verify(this)) {
            PlayerCache cache = CustomCrafting.getPlayerCache(event.getPlayer());
            event.setButton(0, "none", cache.getDarkMode() ? "glass_gray" : "glass_white");
            event.setButton(8, "none", cache.getDarkMode() ? "glass_gray" : "glass_white");

            RecipeHandler recipeHandler = CustomCrafting.getRecipeHandler();
            List<String> availableRecipes = new ArrayList<>();
            if(!recipeHandler.getAvailableAdvancedCraftingRecipes(event.getPlayer()).isEmpty()){
                availableRecipes.add("workbench");
            }
            if(!recipeHandler.getFurnaceRecipes().isEmpty()){
                availableRecipes.add("furnace");
            }
            if(!recipeHandler.getAnvilRecipes().isEmpty()){
                availableRecipes.add("anvil");
            }
            if (WolfyUtilities.hasVillagePillageUpdate()) {
                if(!recipeHandler.getBlastRecipes().isEmpty()){
                    availableRecipes.add("blast_furnace");
                }
                if(!recipeHandler.getSmokerRecipes().isEmpty()){
                    availableRecipes.add("smoker");
                }
                if(!recipeHandler.getCampfireRecipes().isEmpty()){
                    availableRecipes.add("campfire");
                }
                if (!recipeHandler.getStonecutterRecipes().isEmpty()){
                    availableRecipes.add("stonecutter");
                }
                if(!recipeHandler.getAvailableEliteCraftingRecipes(event.getPlayer()).isEmpty()){
                    availableRecipes.add("elite_workbench");
                }
            }
            int index = 0;
            for(int i = 10; i < 45 && index < availableRecipes.size(); i+=2){
                if(i == 18){
                    i = 28;
                }
                event.setButton(i, availableRecipes.get(index++));
            }
        }
    }
}
