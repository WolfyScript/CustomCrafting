package me.wolfyscript.customcrafting.gui.main_gui.buttons;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.GuiHandler;
import me.wolfyscript.utilities.api.inventory.GuiWindow;
import me.wolfyscript.utilities.api.inventory.button.Button;
import me.wolfyscript.utilities.api.utils.item_builder.ItemBuilder;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.Recipe;

import java.util.HashMap;

public class RecipeListContainerButton extends Button {

    private HashMap<GuiHandler, Recipe> recipes = new HashMap<>();

    public RecipeListContainerButton(int slot) {
        super("recipe_list.container_" + slot, null);
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
        String id = ((Keyed) getRecipe(guiHandler)).getKey().toString();
        if (!id.isEmpty() && id.contains(":")) {
            if (CustomCrafting.getRecipeHandler().getDisabledRecipes().contains(id)) {
                CustomCrafting.getRecipeHandler().getDisabledRecipes().remove(id);
            } else {
                CustomCrafting.getRecipeHandler().getDisabledRecipes().add(id);
                for (Player player1 : Bukkit.getOnlinePlayers()) {
                    player1.undiscoverRecipe(new NamespacedKey(id.split(":")[0], id.split(":")[1]));
                }
            }
        }
        return true;
    }

    @Override
    public void render(GuiHandler guiHandler, Player player, Inventory inventory, int slot, boolean help) {
        Recipe recipe = getRecipe(guiHandler);
        if (recipe != null) {
            ItemBuilder itemB = new ItemBuilder(recipe.getResult());
            if (recipe.getResult().getType().equals(Material.AIR)) {
                itemB.setType(Material.STONE).addUnsafeEnchantment(Enchantment.FIRE_ASPECT, 0).addItemFlags(ItemFlag.HIDE_ENCHANTS).setDisplayName("§r§7" + ((Keyed) recipe).getKey().toString());
            }
            itemB.addLoreLine("");
            if (CustomCrafting.getRecipeHandler().getDisabledRecipes().contains(((Keyed) recipe).getKey().toString())) {
                itemB.addLoreLine(ChatColor.translateAlternateColorCodes('&', CustomCrafting.getApi().getLanguageAPI().getActiveLanguage().replaceKeys("$inventories.none.recipe_list.items.lores.disabled$")));
            } else {
                itemB.addLoreLine(ChatColor.translateAlternateColorCodes('&', CustomCrafting.getApi().getLanguageAPI().getActiveLanguage().replaceKeys("$inventories.none.recipe_list.items.lores.enabled$")));
            }
            inventory.setItem(slot, itemB.create());
        }
    }

    public Recipe getRecipe(GuiHandler guiHandler) {
        return recipes.getOrDefault(guiHandler, null);
    }

    public void setRecipe(GuiHandler guiHandler, Recipe recipe) {
        recipes.put(guiHandler, recipe);
    }
}
