package me.wolfyscript.customcrafting.gui.recipebook.buttons;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.PlayerCache;
import me.wolfyscript.customcrafting.data.cache.KnowledgeBook;
import me.wolfyscript.customcrafting.recipes.types.CustomRecipe;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.GuiHandler;
import me.wolfyscript.utilities.api.inventory.GuiWindow;
import me.wolfyscript.utilities.api.inventory.button.Button;
import me.wolfyscript.utilities.api.inventory.button.ButtonType;
import me.wolfyscript.utilities.api.utils.item_builder.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;

import java.util.HashMap;

public class IngredientContainerButton extends Button {

    private HashMap<GuiHandler, CustomRecipe> recipes = new HashMap<>();

    public IngredientContainerButton(int slot) {
        super("ingredient.container_"+slot, ButtonType.DUMMY);
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
        PlayerCache cache = CustomCrafting.getPlayerCache(player);
        KnowledgeBook book = cache.getKnowledgeBook();
        book.setCustomRecipe(getRecipe(guiHandler));
        return true;
    }

    @Override
    public void render(GuiHandler guiHandler, Player player, Inventory inventory, int slot, boolean help) {
        CustomRecipe recipe = getRecipe(guiHandler);
        if(recipe != null){
            ItemBuilder itemB = new ItemBuilder(recipe.getResult());
            if (recipe.getResult().getType().equals(Material.AIR)) {
                itemB.setType(Material.STONE).addUnsafeEnchantment(Enchantment.FIRE_ASPECT, 0).addItemFlags(ItemFlag.HIDE_ENCHANTS).setDisplayName("§r§7" + recipe.getId());
            }
            itemB.addLoreLine("").addLoreLine(ChatColor.translateAlternateColorCodes('&', CustomCrafting.getApi().getLanguageAPI().getActiveLanguage().replaceKeys("$items.recipe_book.lores.click$")));
            inventory.setItem(slot, itemB.create());
        }
    }

    public CustomRecipe getRecipe(GuiHandler guiHandler){
        return recipes.getOrDefault(guiHandler, null);
    }

    public void setRecipe(GuiHandler guiHandler, CustomRecipe recipe){
        recipes.put(guiHandler, recipe);
    }

}
