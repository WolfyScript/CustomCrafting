package me.wolfyscript.customcrafting.gui.main_gui;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.CCPlayerData;
import me.wolfyscript.customcrafting.gui.*;
import me.wolfyscript.customcrafting.gui.main_gui.buttons.RecipeTypeButton;
import me.wolfyscript.customcrafting.recipes.RecipeType;
import me.wolfyscript.customcrafting.utils.PlayerUtil;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.util.inventory.PlayerHeadUtils;
import me.wolfyscript.utilities.util.inventory.item_builder.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;

public class MainMenu extends CCWindow {

    private static final String CRAFTING = RecipeType.Container.CRAFTING.getId();
    private static final String FURNACE = RecipeType.FURNACE.getId();
    private static final String ANVIL = RecipeType.ANVIL.getId();
    private static final String BLAST_FURNACE = RecipeType.BLAST_FURNACE.getId();
    private static final String SMOKER = RecipeType.SMOKER.getId();
    private static final String CAMPFIRE = RecipeType.CAMPFIRE.getId();
    private static final String STONECUTTER = RecipeType.STONECUTTER.getId();
    private static final String GRINDSTONE = RecipeType.GRINDSTONE.getId();
    private static final String BREWING_STAND = RecipeType.BREWING_STAND.getId();
    private static final String ELITE_CRAFTING = RecipeType.Container.ELITE_CRAFTING.getId();
    private static final String CAULDRON = RecipeType.CAULDRON.getId();
    private static final String SMITHING = RecipeType.SMITHING.getId();

    private static final String SETTINGS = "settings";

    private static final String RECIPE_BOOK_EDITOR = "recipe_book_editor";
    private static final String ITEM_EDITOR = "item_editor";

    public MainMenu(GuiCluster<CCCache> cluster, CustomCrafting customCrafting) {
        super(cluster, "main_menu", 54, customCrafting);
    }

    @Override
    public void onInit() {
        registerButton(new RecipeTypeButton(CRAFTING, RecipeType.CRAFTING_SHAPED, Material.CRAFTING_TABLE));
        registerButton(new RecipeTypeButton(FURNACE, RecipeType.FURNACE, Material.FURNACE));
        registerButton(new RecipeTypeButton(ANVIL, RecipeType.ANVIL, Material.ANVIL));
        registerButton(new RecipeTypeButton(BLAST_FURNACE, RecipeType.BLAST_FURNACE, Material.BLAST_FURNACE));
        registerButton(new RecipeTypeButton(SMOKER, RecipeType.SMOKER, Material.SMOKER));
        registerButton(new RecipeTypeButton(CAMPFIRE, RecipeType.CAMPFIRE, Material.CAMPFIRE));
        registerButton(new RecipeTypeButton(STONECUTTER, RecipeType.STONECUTTER, Material.STONECUTTER));
        registerButton(new RecipeTypeButton(GRINDSTONE, RecipeType.GRINDSTONE, Material.GRINDSTONE));
        registerButton(new RecipeTypeButton(BREWING_STAND, RecipeType.BREWING_STAND, Material.BREWING_STAND));
        registerButton(new RecipeTypeButton(ELITE_CRAFTING, RecipeType.ELITE_CRAFTING_SHAPED, new ItemBuilder(Material.CRAFTING_TABLE).addItemFlags(ItemFlag.HIDE_ENCHANTS).addUnsafeEnchantment(Enchantment.DURABILITY, 0).create()));
        registerButton(new RecipeTypeButton(CAULDRON, RecipeType.CAULDRON, Material.CAULDRON));
        registerButton(new RecipeTypeButton(SMITHING, RecipeType.SMITHING, Material.SMITHING_TABLE));

        registerButton(new ActionButton<>(ITEM_EDITOR, Material.CHEST, (cache, guiHandler, player, inventory, slot, event) -> {
            cache.setSetting(Setting.ITEMS);
            cache.getItems().setRecipeItem(false);
            cache.getItems().setSaved(false);
            cache.getItems().setNamespacedKey(null);
            guiHandler.openCluster(ItemCreatorCluster.KEY);
            return true;
        }));

        registerButton(new ActionButton<>(SETTINGS, PlayerHeadUtils.getViaURL("b3f293ebd0911bb8133e75802890997e82854915df5d88f115de1deba628164"), (cache, guiHandler, player, inventory, slot, event) -> {
            guiHandler.openWindow(SETTINGS);
            return true;
        }));
        registerButton(new ActionButton<>(RECIPE_BOOK_EDITOR, Material.KNOWLEDGE_BOOK, (cache, guiHandler, player, inventory, slot, event) -> {
            guiHandler.openCluster(RecipeBookEditorCluster.KEY);
            return true;
        }));
    }

    @Override
    public void onUpdateAsync(GuiUpdate<CCCache> event) {
        super.onUpdateAsync(event);
        CCPlayerData data = PlayerUtil.getStore(event.getPlayer());
        event.setButton(0, SETTINGS);
        event.setButton(8, MainCluster.GUI_HELP);

        event.setButton(4, MainCluster.PATREON);
        event.setButton(48, MainCluster.GITHUB);
        event.setButton(49, MainCluster.YOUTUBE);
        event.setButton(50, MainCluster.DISCORD);

        event.setButton(10, CRAFTING);
        event.setButton(12, FURNACE);
        event.setButton(14, ANVIL);
        event.setButton(16, CAULDRON);

        event.setButton(19, BLAST_FURNACE);
        event.setButton(21, SMOKER);
        event.setButton(23, CAMPFIRE);
        event.setButton(25, STONECUTTER);
        if (customCrafting.getConfigHandler().getConfig().isBrewingRecipes()) {
            event.setButton(28, GRINDSTONE);
            event.setButton(30, BREWING_STAND);
            event.setButton(32, ELITE_CRAFTING);
            event.setButton(34, SMITHING);
        } else {
            event.setButton(29, GRINDSTONE);
            event.setButton(31, ELITE_CRAFTING);
            event.setButton(33, SMITHING);
        }

        for (int i = 37; i < 44; i++) {
            event.setButton(i, data.getLightBackground());
        }

        event.setButton(36, ITEM_EDITOR);
        event.setButton(44, MainCluster.RECIPE_LIST);
        event.setButton(45, MainCluster.ITEM_LIST);
        event.setButton(53, RECIPE_BOOK_EDITOR);
    }
}