package me.wolfyscript.customcrafting.gui.main_gui;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.PlayerStatistics;
import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.gui.ExtendedGuiWindow;
import me.wolfyscript.customcrafting.gui.Setting;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.GuiUpdateEvent;
import me.wolfyscript.utilities.api.inventory.InventoryAPI;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.buttons.ActionButton;
import me.wolfyscript.utilities.api.utils.item_builder.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemFlag;

public class MainMenu extends ExtendedGuiWindow {

    public MainMenu(InventoryAPI inventoryAPI) {
        super("main_menu", inventoryAPI, 45);
    }

    @Override
    public void onInit() {
        registerButton(new ActionButton("workbench", new ButtonState("workbench", Material.CRAFTING_TABLE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).setSetting(Setting.WORKBENCH);
            guiHandler.changeToInv("recipe_editor");
            return true;
        })));
        registerButton(new ActionButton("furnace", new ButtonState("furnace", Material.FURNACE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).setSetting(Setting.FURNACE);
            guiHandler.changeToInv("recipe_editor");
            return true;
        })));
        registerButton(new ActionButton("anvil", new ButtonState("anvil", Material.ANVIL, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).setSetting(Setting.ANVIL);
            guiHandler.changeToInv("recipe_editor");
            return true;
        })));
        if (WolfyUtilities.hasVillagePillageUpdate()) {
            registerButton(new ActionButton("blast_furnace", new ButtonState("blast_furnace", Material.BLAST_FURNACE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                ((TestCache) guiHandler.getCustomCache()).setSetting(Setting.BLAST_FURNACE);
                guiHandler.changeToInv("recipe_editor");
                return true;
            })));
            registerButton(new ActionButton("smoker", new ButtonState("smoker", Material.SMOKER, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                ((TestCache) guiHandler.getCustomCache()).setSetting(Setting.SMOKER);
                guiHandler.changeToInv("recipe_editor");
                return true;
            })));
            registerButton(new ActionButton("campfire", new ButtonState("campfire", Material.CAMPFIRE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                ((TestCache) guiHandler.getCustomCache()).setSetting(Setting.CAMPFIRE);
                guiHandler.changeToInv("recipe_editor");
                return true;
            })));
            registerButton(new ActionButton("stonecutter", new ButtonState("stonecutter", Material.STONECUTTER, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                ((TestCache) guiHandler.getCustomCache()).setSetting(Setting.STONECUTTER);
                guiHandler.changeToInv("recipe_editor");
                return true;
            })));
            registerButton(new ActionButton("elite_workbench", new ButtonState("elite_workbench", new ItemBuilder(Material.CRAFTING_TABLE).addItemFlags(ItemFlag.HIDE_ENCHANTS).addUnsafeEnchantment(Enchantment.DURABILITY, 0).create(), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                ((TestCache) guiHandler.getCustomCache()).setSetting(Setting.ELITE_WORKBENCH);
                guiHandler.changeToInv("recipe_editor");
                return true;
            })));
            registerButton(new ActionButton("cauldron", new ButtonState("cauldron", Material.CAULDRON, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                ((TestCache) guiHandler.getCustomCache()).setSetting(Setting.CAULDRON);
                guiHandler.changeToInv("recipe_editor");
                return true;
            })));
        }

        registerButton(new ActionButton("item_editor", new ButtonState("item_editor", Material.CHEST, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            TestCache cache = (TestCache) guiHandler.getCustomCache();
            cache.setSetting(Setting.ITEMS);
            cache.setSetting(Setting.ITEMS);
            cache.setSetting(Setting.ITEMS);
            cache.getItems().setRecipeItem(false);
            cache.getItems().setSaved(false);
            cache.getItems().setId("");
            guiHandler.changeToInv("item_editor");
            return true;
        })));
        registerButton(new ActionButton("recipe_list", new ButtonState("recipe_list", Material.WRITTEN_BOOK, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            guiHandler.changeToInv("recipe_list");
            ((TestCache) guiHandler.getCustomCache()).setSetting(Setting.RECIPE_LIST);
            return true;
        })));
        registerButton(new ActionButton("settings", new ButtonState("settings", WolfyUtilities.getSkullViaURL("b3f293ebd0911bb8133e75802890997e82854915df5d88f115de1deba628164"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            guiHandler.changeToInv("settings");
            return true;
        })));
    }

    @EventHandler
    public void onUpdate(GuiUpdateEvent event) {
        if (event.verify(this)) {
            event.setButton(0, "settings");
            event.setButton(8, "none", "gui_help");
            event.setButton(4, "none", "patreon");

            event.setButton(39, "none", "instagram");
            event.setButton(40, "none", "youtube");
            event.setButton(41, "none", "discord");

            event.setButton(10, "workbench");
            event.setButton(12, "furnace");
            event.setButton(14, "anvil");

            if (WolfyUtilities.hasVillagePillageUpdate()) {
                event.setButton(16, "cauldron");
                event.setButton(20, "blast_furnace");
                event.setButton(22, "smoker");
                event.setButton(24, "campfire");
                event.setButton(30, "stonecutter");
                event.setButton(32, "elite_workbench");
            }
            event.setButton(36, "item_editor");
            event.setButton(44, "recipe_list");
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onUpdateGuis(GuiUpdateEvent event) {
        if (event.getWolfyUtilities().equals(CustomCrafting.getApi()) && event.getGuiHandler().getCurrentInv() != null && event.getGuiHandler().getCurrentInv().equals(event.getGuiWindow())) {
            PlayerStatistics playerStatistics = CustomCrafting.getPlayerStatistics(event.getPlayer());
            if (!event.getGuiWindow().getNamespace().startsWith("crafting_grid")) {
                if (event.getGuiHandler().getCurrentInv().getSize() > 9) {
                    for (int i = 0; i < 9; i++) {
                        event.setButton(i, "none", playerStatistics.getDarkMode() ? "glass_gray" : "glass_white");
                    }
                    for (int i = 9; i < event.getGuiHandler().getCurrentInv().getSize() - 9; i++) {
                        event.setButton(i, "none", playerStatistics.getDarkMode() ? "glass_black" : "glass_gray");
                    }
                    for (int i = event.getGuiHandler().getCurrentInv().getSize() - 9; i < event.getGuiHandler().getCurrentInv().getSize(); i++) {
                        event.setButton(i, "none", playerStatistics.getDarkMode() ? "glass_gray" : "glass_white");
                    }
                    event.setButton(8, "none", "gui_help");
                } else {
                    for (int i = 0; i < 9; i++) {
                        event.setButton(i, "none", playerStatistics.getDarkMode() ? "glass_black" : "glass_gray");
                    }
                }
            }
        }
    }
}
