package me.wolfyscript.customcrafting.gui.main_gui;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.CCPlayerData;
import me.wolfyscript.customcrafting.gui.CCWindow;
import me.wolfyscript.customcrafting.gui.Setting;
import me.wolfyscript.customcrafting.gui.main_gui.buttons.RecipeTypeButton;
import me.wolfyscript.customcrafting.recipes.types.RecipeType;
import me.wolfyscript.customcrafting.utils.PlayerUtil;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.inventory.PlayerHeadUtils;
import me.wolfyscript.utilities.util.inventory.item_builder.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;

public class MainMenu extends CCWindow {

    public MainMenu(GuiCluster<CCCache> cluster, CustomCrafting customCrafting) {
        super(cluster, "main_menu", 54, customCrafting);
    }

    @Override
    public void onInit() {
        registerButton(new RecipeTypeButton(RecipeType.WORKBENCH, Material.CRAFTING_TABLE));
        registerButton(new RecipeTypeButton(RecipeType.FURNACE, Material.FURNACE));
        registerButton(new RecipeTypeButton(RecipeType.ANVIL, Material.ANVIL));
        registerButton(new RecipeTypeButton(RecipeType.BLAST_FURNACE, Material.BLAST_FURNACE));
        registerButton(new RecipeTypeButton(RecipeType.SMOKER, Material.SMOKER));
        registerButton(new RecipeTypeButton(RecipeType.CAMPFIRE, Material.CAMPFIRE));
        registerButton(new RecipeTypeButton(RecipeType.STONECUTTER, Material.STONECUTTER));
        registerButton(new RecipeTypeButton(RecipeType.GRINDSTONE, Material.GRINDSTONE));
        registerButton(new RecipeTypeButton(RecipeType.BREWING_STAND, Material.BREWING_STAND));
        registerButton(new RecipeTypeButton(RecipeType.ELITE_WORKBENCH, new ItemBuilder(Material.CRAFTING_TABLE).addItemFlags(ItemFlag.HIDE_ENCHANTS).addUnsafeEnchantment(Enchantment.DURABILITY, 0).create()));
        registerButton(new RecipeTypeButton(RecipeType.CAULDRON, Material.CAULDRON));
        if (WolfyUtilities.hasNetherUpdate()) {
            registerButton(new RecipeTypeButton(RecipeType.SMITHING, Material.SMITHING_TABLE));
        }

        registerButton(new ActionButton<>("item_editor", Material.CHEST, (cache, guiHandler, player, inventory, slot, event) -> {
            cache.setSetting(Setting.ITEMS);
            cache.getItems().setRecipeItem(false);
            cache.getItems().setSaved(false);
            cache.getItems().setNamespacedKey(null);
            guiHandler.openWindow("item_editor");
            return true;
        }));

        registerButton(new ActionButton<>("settings", PlayerHeadUtils.getViaURL("b3f293ebd0911bb8133e75802890997e82854915df5d88f115de1deba628164"), (cache, guiHandler, player, inventory, slot, event) -> {
            guiHandler.openWindow("settings");
            return true;
        }));
        registerButton(new ActionButton<>("recipe_book_editor", Material.KNOWLEDGE_BOOK, (cache, guiHandler, player, inventory, slot, event) -> {
            guiHandler.openCluster("recipe_book_editor");
            return true;
        }));
    }

    @Override
    public void onUpdateSync(GuiUpdate<CCCache> guiUpdate) {

    }

    @Override
    public void onUpdateAsync(GuiUpdate<CCCache> event) {
        super.onUpdateAsync(event);
        CCPlayerData data = PlayerUtil.getStore(event.getPlayer());
        event.setButton(0, "settings");
        event.setButton(8, new NamespacedKey("none", "gui_help"));

        event.setButton(4, new NamespacedKey("none", "patreon"));
        event.setButton(48, new NamespacedKey("none", "instagram"));
        event.setButton(49, new NamespacedKey("none", "youtube"));
        event.setButton(50, new NamespacedKey("none", "discord"));

        event.setButton(10, "workbench");
        event.setButton(12, "furnace");
        event.setButton(14, "anvil");
        event.setButton(16, "cauldron");

        if (WolfyUtilities.hasNetherUpdate()) {
            event.setButton(19, "blast_furnace");
            event.setButton(21, "smoker");
            event.setButton(23, "campfire");
            event.setButton(25, "stonecutter");
            event.setButton(28, "grindstone");
            event.setButton(30, "brewing_stand");
            event.setButton(32, "elite_workbench");
            event.setButton(34, "smithing");
        } else {
            event.setButton(20, "blast_furnace");
            event.setButton(22, "smoker");
            event.setButton(24, "campfire");
            event.setButton(28, "stonecutter");
            event.setButton(30, "grindstone");
            event.setButton(32, "brewing");
            event.setButton(34, "elite_workbench");
        }
        for (int i = 37; i < 44; i++) {
            event.setButton(i, new NamespacedKey("none", data.isDarkMode() ? "glass_gray" : "glass_white"));
        }

        event.setButton(36, "item_editor");
        event.setButton(44, new NamespacedKey("none", "recipe_list"));
        event.setButton(45, new NamespacedKey("none", "item_list"));
        event.setButton(53, "recipe_book_editor");
    }
}
