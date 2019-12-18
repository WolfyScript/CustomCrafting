package me.wolfyscript.customcrafting.gui.recipebook.buttons;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.utilities.api.inventory.GuiHandler;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.buttons.MultipleChoiceButton;
import me.wolfyscript.utilities.api.utils.ItemCategory;
import org.bukkit.Material;

public class ItemCategoryButton extends MultipleChoiceButton {

    public ItemCategoryButton() {
        super("itemCategory", new ButtonState("recipe_book", "itemCategory.brewing", Material.GLASS_BOTTLE, (guiHandler, player, inventory, i, e) -> {
            CustomCrafting.getPlayerCache(player).getKnowledgeBook().setItemCategory(e.isLeftClick() ? ItemCategory.BUILDING_BLOCKS : ItemCategory.SEARCH);
            return true;
        }), new ButtonState("recipe_book", "itemCategory.building_blocks", Material.BRICKS, (guiHandler, player, inventory, i, e) -> {
            CustomCrafting.getPlayerCache(player).getKnowledgeBook().setItemCategory(e.isLeftClick() ? ItemCategory.DECORATIONS : ItemCategory.BREWING);
            return true;
        }), new ButtonState("recipe_book", "itemCategory.decorations", Material.LILAC, (guiHandler, player, inventory, i, e) -> {
            CustomCrafting.getPlayerCache(player).getKnowledgeBook().setItemCategory(e.isLeftClick() ? ItemCategory.COMBAT : ItemCategory.BUILDING_BLOCKS);
            return true;
        }), new ButtonState("recipe_book", "itemCategory.combat", Material.GOLDEN_SWORD, (guiHandler, player, inventory, i, e) -> {
            CustomCrafting.getPlayerCache(player).getKnowledgeBook().setItemCategory(e.isLeftClick() ? ItemCategory.TOOLS : ItemCategory.DECORATIONS);
            return true;
        }), new ButtonState("recipe_book", "itemCategory.tools", Material.IRON_AXE, (guiHandler, player, inventory, i, e) -> {
            CustomCrafting.getPlayerCache(player).getKnowledgeBook().setItemCategory(e.isLeftClick() ? ItemCategory.REDSTONE : ItemCategory.COMBAT);
            return true;
        }), new ButtonState("recipe_book", "itemCategory.redstone", Material.REDSTONE, (guiHandler, player, inventory, i, e) -> {
            CustomCrafting.getPlayerCache(player).getKnowledgeBook().setItemCategory(e.isLeftClick() ? ItemCategory.FOOD : ItemCategory.TOOLS);
            return true;
        }), new ButtonState("recipe_book", "itemCategory.food", Material.APPLE, (guiHandler, player, inventory, i, e) -> {
            CustomCrafting.getPlayerCache(player).getKnowledgeBook().setItemCategory(e.isLeftClick() ? ItemCategory.TRANSPORTATION : ItemCategory.REDSTONE);
            return true;
        }), new ButtonState("recipe_book", "itemCategory.transportation", Material.POWERED_RAIL, (guiHandler, player, inventory, i, e) -> {
            CustomCrafting.getPlayerCache(player).getKnowledgeBook().setItemCategory(e.isLeftClick() ? ItemCategory.MISC : ItemCategory.FOOD);
            return true;
        }), new ButtonState("recipe_book", "itemCategory.misc", Material.LAVA_BUCKET, (guiHandler, player, inventory, i, e) -> {
            CustomCrafting.getPlayerCache(player).getKnowledgeBook().setItemCategory(e.isLeftClick() ? ItemCategory.SEARCH : ItemCategory.TRANSPORTATION);
            return true;
        }), new ButtonState("recipe_book", "itemCategory.search", Material.COMPASS, (guiHandler, player, inventory, i, e) -> {
            CustomCrafting.getPlayerCache(player).getKnowledgeBook().setItemCategory(e.isLeftClick() ? ItemCategory.BREWING : ItemCategory.MISC);
            return true;
        }));
    }

    public void setState(GuiHandler guiHandler, ItemCategory itemCategory) {
        setState(guiHandler, itemCategory.ordinal());
    }
}
