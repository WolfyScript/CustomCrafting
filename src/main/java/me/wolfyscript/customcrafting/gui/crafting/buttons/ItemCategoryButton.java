package me.wolfyscript.customcrafting.gui.crafting.buttons;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.utilities.api.inventory.GuiHandler;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.buttons.MultipleChoiceButton;
import me.wolfyscript.utilities.api.utils.ItemCategory;
import org.bukkit.Material;

public class ItemCategoryButton extends MultipleChoiceButton {

    public ItemCategoryButton() {
        super("itemCategory", new ButtonState("recipe_book", "brewing", Material.GLASS_BOTTLE, (guiHandler, player, inventory, i, e) -> {
            CustomCrafting.getPlayerCache(player).getKnowledgeBook().setItemCategory(e.isLeftClick() ? ItemCategory.BUILDING_BLOCKS : ItemCategory.SEARCH);
            return true;
        }), new ButtonState("recipe_book", "building_blocks", Material.BRICKS, (guiHandler, player, inventory, i, e) -> {
            CustomCrafting.getPlayerCache(player).getKnowledgeBook().setItemCategory(e.isLeftClick() ? ItemCategory.DECORATIONS : ItemCategory.BREWING);
            return true;
        }), new ButtonState("recipe_book", "decorations", Material.LILAC, (guiHandler, player, inventory, i, e) -> {
            CustomCrafting.getPlayerCache(player).getKnowledgeBook().setItemCategory(e.isLeftClick() ? ItemCategory.COMBAT : ItemCategory.BUILDING_BLOCKS);
            return true;
        }), new ButtonState("recipe_book", "combat", Material.GOLDEN_SWORD, (guiHandler, player, inventory, i, e) -> {
            CustomCrafting.getPlayerCache(player).getKnowledgeBook().setItemCategory(e.isLeftClick() ? ItemCategory.TOOLS : ItemCategory.DECORATIONS);
            return true;
        }), new ButtonState("recipe_book", "tools", Material.IRON_AXE, (guiHandler, player, inventory, i, e) -> {
            CustomCrafting.getPlayerCache(player).getKnowledgeBook().setItemCategory(e.isLeftClick() ? ItemCategory.REDSTONE : ItemCategory.COMBAT);
            return true;
        }), new ButtonState("recipe_book", "redstone", Material.REDSTONE, (guiHandler, player, inventory, i, e) -> {
            CustomCrafting.getPlayerCache(player).getKnowledgeBook().setItemCategory(e.isLeftClick() ? ItemCategory.FOOD : ItemCategory.TOOLS);
            return true;
        }), new ButtonState("recipe_book", "food", Material.APPLE, (guiHandler, player, inventory, i, e) -> {
            CustomCrafting.getPlayerCache(player).getKnowledgeBook().setItemCategory(e.isLeftClick() ? ItemCategory.TRANSPORTATION : ItemCategory.REDSTONE);
            return true;
        }), new ButtonState("recipe_book", "transportation", Material.POWERED_RAIL, (guiHandler, player, inventory, i, e) -> {
            CustomCrafting.getPlayerCache(player).getKnowledgeBook().setItemCategory(e.isLeftClick() ? ItemCategory.MISC : ItemCategory.FOOD);
            return true;
        }), new ButtonState("recipe_book", "misc", Material.LAVA_BUCKET, (guiHandler, player, inventory, i, e) -> {
            CustomCrafting.getPlayerCache(player).getKnowledgeBook().setItemCategory(e.isLeftClick() ? ItemCategory.SEARCH : ItemCategory.TRANSPORTATION);
            return true;
        }), new ButtonState("recipe_book", "search", Material.COMPASS, (guiHandler, player, inventory, i, e) -> {
            CustomCrafting.getPlayerCache(player).getKnowledgeBook().setItemCategory(e.isLeftClick() ? ItemCategory.BREWING : ItemCategory.MISC);
            return true;
        }));
    }

    public void setState(GuiHandler guiHandler, ItemCategory itemCategory){
        setState(guiHandler, itemCategory.ordinal());
    }
}
