package me.wolfyscript.customcrafting.gui.main_gui;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.PlayerCache;
import me.wolfyscript.customcrafting.gui.ExtendedGuiWindow;
import me.wolfyscript.customcrafting.gui.Setting;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.GuiUpdateEvent;
import me.wolfyscript.utilities.api.inventory.InventoryAPI;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.buttons.ActionButton;
import me.wolfyscript.utilities.api.inventory.button.buttons.ToggleButton;
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
            CustomCrafting.getPlayerCache(player).setSetting(Setting.WORKBENCH);
            guiHandler.changeToInv("recipe_editor");
            return true;
        })));
        registerButton(new ActionButton("furnace", new ButtonState("furnace", Material.FURNACE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            CustomCrafting.getPlayerCache(player).setSetting(Setting.FURNACE);
            guiHandler.changeToInv("recipe_editor");
            return true;
        })));
        registerButton(new ActionButton("anvil", new ButtonState("anvil", Material.ANVIL, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            CustomCrafting.getPlayerCache(player).setSetting(Setting.ANVIL);
            guiHandler.changeToInv("recipe_editor");
            return true;
        })));
        if (WolfyUtilities.hasVillagePillageUpdate()) {
            registerButton(new ActionButton("blast_furnace", new ButtonState("blast_furnace", Material.BLAST_FURNACE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                CustomCrafting.getPlayerCache(player).setSetting(Setting.BLAST_FURNACE);
                guiHandler.changeToInv("recipe_editor");
                return true;
            })));
            registerButton(new ActionButton("smoker", new ButtonState("smoker", Material.SMOKER, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                CustomCrafting.getPlayerCache(player).setSetting(Setting.SMOKER);
                guiHandler.changeToInv("recipe_editor");
                return true;
            })));
            registerButton(new ActionButton("campfire", new ButtonState("campfire", Material.CAMPFIRE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                CustomCrafting.getPlayerCache(player).setSetting(Setting.CAMPFIRE);
                guiHandler.changeToInv("recipe_editor");
                return true;
            })));
            registerButton(new ActionButton("stonecutter", new ButtonState("stonecutter", Material.STONECUTTER, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                CustomCrafting.getPlayerCache(player).setSetting(Setting.STONECUTTER);
                guiHandler.changeToInv("recipe_editor");
                return true;
            })));
            registerButton(new ActionButton("elite_workbench", new ButtonState("elite_workbench", new ItemBuilder(Material.CRAFTING_TABLE).addItemFlags(ItemFlag.HIDE_ENCHANTS).addUnsafeEnchantment(Enchantment.DURABILITY, 0).create(), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                CustomCrafting.getPlayerCache(player).setSetting(Setting.ELITE_WORKBENCH);
                guiHandler.changeToInv("recipe_editor");
                return true;
            })));
            registerButton(new ActionButton("cauldron", new ButtonState("cauldron", Material.CAULDRON, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                CustomCrafting.getPlayerCache(player).setSetting(Setting.CAULDRON);
                guiHandler.changeToInv("cauldron");
                return true;
            })));
        }

        registerButton(new ActionButton("item_editor", new ButtonState("item_editor", Material.CHEST, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            PlayerCache playerCache = CustomCrafting.getPlayerCache(player);
            playerCache.setSetting(Setting.ITEMS);
            playerCache.getItems().setType("items");
            playerCache.getItems().setSaved(false);
            playerCache.getItems().setId("");
            guiHandler.changeToInv("item_editor");
            return true;
        })));
        registerButton(new ActionButton("recipe_list", new ButtonState("recipe_list", Material.WRITTEN_BOOK, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            guiHandler.changeToInv("recipe_list");
            CustomCrafting.getPlayerCache(player).setSetting(Setting.RECIPE_LIST);
            return true;
        })));
        registerButton(new ToggleButton("lockdown", !CustomCrafting.getConfigHandler().getConfig().isLockedDown(), new ButtonState("lockdown.disabled", Material.BARRIER, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            CustomCrafting.getConfigHandler().getConfig().setLockDown(true);
            return true;
        }), new ButtonState("lockdown.enabled", Material.BARRIER, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            CustomCrafting.getConfigHandler().getConfig().setLockDown(false);
            return true;
        })));
    }

    @EventHandler
    public void onUpdate(GuiUpdateEvent event) {
        if (event.verify(this)) {
            event.setButton(8, "none", "gui_help");

            event.setButton(0, "none", "glass_white");
            event.setButton(10, "workbench");
            event.setButton(12, "furnace");
            event.setButton(14, "anvil");

            if (WolfyUtilities.hasVillagePillageUpdate()) {
                event.setButton(15, "blast_furnace");
                event.setButton(19, "smoker");
                event.setButton(21, "campfire");
                event.setButton(23, "stonecutter");
                event.setButton(25, "elite_workbench");
            }
            event.setButton(39, "item_editor");
            event.setButton(41, "recipe_list");

            event.setButton(0, "lockdown");
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onUpdateGuis(GuiUpdateEvent event) {
        if (event.getWolfyUtilities().equals(CustomCrafting.getApi()) && event.getGuiHandler().getCurrentInv() != null && event.getGuiHandler().getCurrentInv().equals(event.getGuiWindow())) {
            if(!event.getGuiWindow().getClusterID().equals("crafting")){
                for (int i = 0; i < 9; i++) {
                    event.setButton(i, "none", "glass_white");
                }
                for (int i = 9; i < event.getGuiHandler().getCurrentInv().getSize() - 9; i++) {
                    event.setButton(i, "none", "glass_gray");
                }
                for (int i = event.getGuiHandler().getCurrentInv().getSize() - 9; i < event.getGuiHandler().getCurrentInv().getSize(); i++) {
                    event.setButton(i, "none", "glass_white");
                }
                if (event.getGuiHandler().getCurrentInv().getSize() > 8) {
                    event.setButton(8, "none", "gui_help");
                }
            }
        }
    }
}
