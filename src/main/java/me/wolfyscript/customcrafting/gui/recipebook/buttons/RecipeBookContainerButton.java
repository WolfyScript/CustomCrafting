package me.wolfyscript.customcrafting.gui.recipebook.buttons;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.data.cache.KnowledgeBook;
import me.wolfyscript.customcrafting.recipes.types.CustomRecipe;
import me.wolfyscript.customcrafting.recipes.types.anvil.CustomAnvilRecipe;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.GuiHandler;
import me.wolfyscript.utilities.api.inventory.GuiWindow;
import me.wolfyscript.utilities.api.inventory.button.Button;
import me.wolfyscript.utilities.api.utils.item_builder.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;

import java.util.HashMap;

public class RecipeBookContainerButton extends Button {

    private HashMap<GuiHandler, CustomRecipe> recipes = new HashMap<>();

    public RecipeBookContainerButton(int slot) {
        super("recipe_book.container_" + slot, null);
    }


    @Override
    public void init(GuiWindow guiWindow) {

    }

    @Override
    public void init(String s, WolfyUtilities wolfyUtilities) {

    }

    @Override
    public boolean execute(GuiHandler guiHandler, Player player, Inventory inventory, int slot, InventoryClickEvent event) {
        TestCache cache = (TestCache) guiHandler.getCustomCache();
        KnowledgeBook book = cache.getKnowledgeBook();
        book.setCustomRecipe(getRecipe(guiHandler));
        return true;
    }

    @Override
    public void render(GuiHandler guiHandler, Player player, Inventory inventory, int slot, boolean help) {
        CustomRecipe recipe = getRecipe(guiHandler);
        if (recipe != null) {
            ItemBuilder itemB;
            if(recipe instanceof CustomAnvilRecipe){
                if(((CustomAnvilRecipe) recipe).hasInputLeft()){
                    itemB = new ItemBuilder(((CustomAnvilRecipe) recipe).getInputLeft().get(0).getRealItem());
                }else if(((CustomAnvilRecipe) recipe).hasInputRight()){
                    itemB = new ItemBuilder(((CustomAnvilRecipe) recipe).getInputRight().get(0).getRealItem());
                }else if(((CustomAnvilRecipe) recipe).getMode().equals(CustomAnvilRecipe.Mode.RESULT)){
                    itemB = new ItemBuilder(recipe.getCustomResult().getRealItem().getRealItem());
                }else{
                    itemB = new ItemBuilder(Material.STONE);
                    itemB.addUnsafeEnchantment(Enchantment.FIRE_ASPECT, 0).addItemFlags(ItemFlag.HIDE_ENCHANTS).setDisplayName("§r§7" + recipe.getId());
                }
                itemB.addLoreLine("").addLoreLine(CustomCrafting.getApi().getLanguageAPI().replaceColoredKeys("$inventories.recipe_book.global_items.lores.click$"));
            }else{
                itemB = new ItemBuilder(recipe.getCustomResult().getRealItem());
                if (recipe.getResult().getType().equals(Material.AIR)) {
                    itemB.setType(Material.STONE).addUnsafeEnchantment(Enchantment.FIRE_ASPECT, 0).addItemFlags(ItemFlag.HIDE_ENCHANTS).setDisplayName("§r§7" + recipe.getId());
                }
                itemB.addLoreLine("").addLoreLine(CustomCrafting.getApi().getLanguageAPI().replaceColoredKeys("$inventories.recipe_book.global_items.lores.click$"));
            }
            inventory.setItem(slot, itemB.create());
        }
    }

    public CustomRecipe getRecipe(GuiHandler guiHandler) {
        return recipes.getOrDefault(guiHandler, null);
    }

    public void setRecipe(GuiHandler guiHandler, CustomRecipe recipe) {
        recipes.put(guiHandler, recipe);
    }
}
