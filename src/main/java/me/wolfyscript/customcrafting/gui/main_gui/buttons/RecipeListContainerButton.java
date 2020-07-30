package me.wolfyscript.customcrafting.gui.main_gui.buttons;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.types.ICustomRecipe;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.GuiHandler;
import me.wolfyscript.utilities.api.inventory.GuiWindow;
import me.wolfyscript.utilities.api.inventory.button.Button;
import me.wolfyscript.utilities.api.utils.inventory.item_builder.ItemBuilder;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.Recipe;

import java.util.HashMap;

public class RecipeListContainerButton extends Button {

    private final CustomCrafting customCrafting;
    private final HashMap<GuiHandler, Recipe> recipes = new HashMap<>();
    private final HashMap<GuiHandler, ICustomRecipe> customRecipes = new HashMap<>();

    public RecipeListContainerButton(int slot, CustomCrafting customCrafting) {
        super("recipe_list.container_" + slot, null);
        this.customCrafting = customCrafting;
    }

    @Override
    public void init(GuiWindow guiWindow) {
        //NOT NEEDED
    }

    @Override
    public void init(String s, WolfyUtilities wolfyUtilities) {
        //NOT NEEDED
    }

    @Override
    public boolean execute(GuiHandler guiHandler, Player player, Inventory inventory, int slot, InventoryClickEvent event) {
        String id;
        if (getCustomRecipe(guiHandler) != null) {
            id = (getCustomRecipe(guiHandler)).getNamespacedKey().toString();
        } else {
            id = ((Keyed) getRecipe(guiHandler)).getKey().toString();
        }
        if (!id.isEmpty() && id.contains(":")) {
            if (customCrafting.getRecipeHandler().getDisabledRecipes().contains(id)) {
                customCrafting.getRecipeHandler().getDisabledRecipes().remove(id);
            } else {
                customCrafting.getRecipeHandler().getDisabledRecipes().add(id);
                for (Player player1 : Bukkit.getOnlinePlayers()) {
                    player1.undiscoverRecipe(new NamespacedKey(id.split(":")[0], id.split(":")[1]));
                }
            }
        }
        return true;
    }

    @Override
    public void render(GuiHandler guiHandler, Player player, Inventory inventory, int slot, boolean help) {
        if (getCustomRecipe(guiHandler) != null) {
            ICustomRecipe recipe = getCustomRecipe(guiHandler);
            if (recipe != null) {
                ItemBuilder itemB = new ItemBuilder(recipe.getCustomResult().create());
                if (recipe.getCustomResult().getItemStack().getType().equals(Material.AIR)) {
                    itemB.setType(Material.STONE).addUnsafeEnchantment(Enchantment.FIRE_ASPECT, 0).addItemFlags(ItemFlag.HIDE_ENCHANTS).setDisplayName("§r§7" + recipe.getNamespacedKey().toString());
                }
                itemB.addLoreLine("§8" + recipe.getNamespacedKey().toString());
                if (customCrafting.getRecipeHandler().getDisabledRecipes().contains(recipe.getNamespacedKey().toString())) {
                    itemB.addLoreLine(ChatColor.translateAlternateColorCodes('&', CustomCrafting.getApi().getLanguageAPI().replaceKeys("$inventories.none.recipe_list.items.lores.disabled$")));
                } else {
                    itemB.addLoreLine(ChatColor.translateAlternateColorCodes('&', CustomCrafting.getApi().getLanguageAPI().replaceKeys("$inventories.none.recipe_list.items.lores.enabled$")));
                }
                inventory.setItem(slot, itemB.create());
            }
        } else {
            Recipe recipe = getRecipe(guiHandler);
            if (recipe != null) {
                ItemBuilder itemB = new ItemBuilder(recipe.getResult());
                if (recipe.getResult().getType().equals(Material.AIR)) {
                    itemB.setType(Material.STONE).addUnsafeEnchantment(Enchantment.FIRE_ASPECT, 0).addItemFlags(ItemFlag.HIDE_ENCHANTS).setDisplayName("§r§7" + ((Keyed) recipe).getKey().toString());
                }
                itemB.addLoreLine("§8" + ((Keyed) recipe).getKey().toString());
                if (customCrafting.getRecipeHandler().getDisabledRecipes().contains(((Keyed) recipe).getKey().toString())) {
                    itemB.addLoreLine(ChatColor.translateAlternateColorCodes('&', CustomCrafting.getApi().getLanguageAPI().replaceKeys("$inventories.none.recipe_list.items.lores.disabled$")));
                } else {
                    itemB.addLoreLine(ChatColor.translateAlternateColorCodes('&', CustomCrafting.getApi().getLanguageAPI().replaceKeys("$inventories.none.recipe_list.items.lores.enabled$")));
                }
                inventory.setItem(slot, itemB.create());
            }
        }
    }

    public ICustomRecipe getCustomRecipe(GuiHandler guiHandler) {
        return customRecipes.getOrDefault(guiHandler, null);
    }

    public void setCustomRecipe(GuiHandler guiHandler, ICustomRecipe recipe) {
        customRecipes.put(guiHandler, recipe);
    }

    public Recipe getRecipe(GuiHandler guiHandler) {
        return recipes.getOrDefault(guiHandler, null);
    }

    public void setRecipe(GuiHandler guiHandler, Recipe recipe) {
        recipes.put(guiHandler, recipe);
    }
}
