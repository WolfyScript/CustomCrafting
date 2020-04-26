package me.wolfyscript.customcrafting.gui.recipe_creator.recipe_creators;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.gui.ExtendedGuiWindow;
import me.wolfyscript.customcrafting.gui.recipe_creator.buttons.CraftingIngredientButton;
import me.wolfyscript.customcrafting.recipes.RecipePriority;
import me.wolfyscript.customcrafting.recipes.types.elite_workbench.EliteCraftConfig;
import me.wolfyscript.customcrafting.recipes.types.elite_workbench.EliteCraftingRecipe;
import me.wolfyscript.customcrafting.recipes.types.elite_workbench.ShapedEliteCraftRecipe;
import me.wolfyscript.customcrafting.recipes.types.elite_workbench.ShapelessEliteCraftRecipe;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.GuiHandler;
import me.wolfyscript.utilities.api.inventory.GuiUpdateEvent;
import me.wolfyscript.utilities.api.inventory.InventoryAPI;
import me.wolfyscript.utilities.api.inventory.button.ButtonActionRender;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.buttons.ActionButton;
import me.wolfyscript.utilities.api.inventory.button.buttons.ToggleButton;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Locale;

public class EliteWorkbenchCreator extends ExtendedGuiWindow {

    public EliteWorkbenchCreator(InventoryAPI inventoryAPI, CustomCrafting customCrafting) {
        super("elite_workbench", inventoryAPI, 54, customCrafting);
    }

    @Override
    public void onInit() {
        registerButton(new ActionButton("back", new ButtonState("none", "back", WolfyUtilities.getCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODY0Zjc3OWE4ZTNmZmEyMzExNDNmYTY5Yjk2YjE0ZWUzNWMxNmQ2NjllMTljNzVmZDFhN2RhNGJmMzA2YyJ9fX0="), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            guiHandler.openCluster("none");
            return true;
        })));
        registerButton(new ActionButton("save", new ButtonState("recipe_creator", "save", Material.WRITABLE_BOOK, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            TestCache cache = ((TestCache) guiHandler.getCustomCache());
            if (validToSave(cache)) {
                openChat("recipe_creator", "save.input", guiHandler, (guiHandler1, player1, s, args) -> {
                    EliteCraftConfig config = cache.getEliteCraftConfig();
                    if (args.length > 1) {
                        if (!config.saveConfig(args[0], args[1], player1)) {
                            return true;
                        }
                        try {
                            EliteCraftingRecipe customRecipe;
                            if (CustomCrafting.hasDataBaseHandler()) {
                                customRecipe = (EliteCraftingRecipe) CustomCrafting.getDataBaseHandler().getRecipe(args[0].toLowerCase(Locale.ROOT).replace(" ", "_"), args[1].toLowerCase(Locale.ROOT).replace(" ", "_"));
                            } else {
                                if (config.isShapeless()) {
                                    customRecipe = new ShapelessEliteCraftRecipe(config);
                                } else {
                                    customRecipe = new ShapedEliteCraftRecipe(config);
                                }
                            }
                            Bukkit.getScheduler().runTaskLater(customCrafting, () -> {
                                customCrafting.getRecipeHandler().injectRecipe(customRecipe);
                                api.sendPlayerMessage(player, "recipe_creator", "loading.success");
                            }, 1);
                            if (customCrafting.getConfigHandler().getConfig().isResetCreatorAfterSave()) {
                                cache.resetEliteCraftConfig();
                            }
                        } catch (Exception ex) {
                            api.sendPlayerMessage(player, "recipe_creator", "loading.error", new String[]{"%REC%", config.getId()});
                            ex.printStackTrace();
                            return false;
                        }
                        Bukkit.getScheduler().runTask(customCrafting, () -> guiHandler.openCluster("none"));
                        return false;
                    }
                    return false;
                });
            } else {
                api.sendPlayerMessage(player, "recipe_creator", "save.empty");
            }
            return false;
        })));

        registerButton(new ToggleButton("hidden", new ButtonState("recipe_creator", "hidden.enabled", WolfyUtilities.getSkullViaURL("ce9d49dd09ecee2a4996965514d6d301bf12870c688acb5999b6658e1dfdff85"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).getEliteCraftConfig().setHidden(false);
            return true;
        }), new ButtonState("recipe_creator", "hidden.disabled", WolfyUtilities.getSkullViaURL("85e5bf255d5d7e521474318050ad304ab95b01a4af0bae15e5cd9c1993abcc98"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).getEliteCraftConfig().setHidden(true);
            return true;
        })));

        for (int i = 0; i < 37; i++) {
            registerButton(new CraftingIngredientButton(i, customCrafting));
        }

        registerButton(new ToggleButton("exact_meta", new ButtonState("recipe_creator", "exact_meta.enabled", Material.GREEN_CONCRETE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).getEliteCraftConfig().setExactMeta(false);
            return true;
        }), new ButtonState("recipe_creator", "exact_meta.disabled", Material.RED_CONCRETE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).getEliteCraftConfig().setExactMeta(true);
            return true;
        })));
        registerButton(new ActionButton("priority", new ButtonState("recipe_creator", "priority", WolfyUtilities.getSkullViaURL("b8ea57c7551c6ab33b8fed354b43df523f1e357c4b4f551143c34ddeac5b6c8d"), new ButtonActionRender() {
            @Override
            public boolean run(GuiHandler guiHandler, Player player, Inventory inventory, int i, InventoryClickEvent inventoryClickEvent) {
                RecipePriority priority = ((TestCache) guiHandler.getCustomCache()).getEliteCraftConfig().getPriority();
                int order;
                order = priority.getOrder();
                if (order < 2) {
                    order++;
                } else {
                    order = -2;
                }
                ((TestCache) guiHandler.getCustomCache()).getEliteCraftConfig().setPriority(RecipePriority.getByOrder(order));
                return true;
            }

            @Override
            public ItemStack render(HashMap<String, Object> hashMap, GuiHandler guiHandler, Player player, ItemStack itemStack, int slot, boolean help) {
                RecipePriority priority = ((TestCache) guiHandler.getCustomCache()).getEliteCraftConfig().getPriority();
                if (priority != null) {
                    hashMap.put("%PRI%", priority.name());
                }
                return itemStack;
            }
        })));

        registerButton(new ToggleButton("workbench.shapeless", false, new ButtonState("recipe_creator", "workbench.shapeless.enabled", WolfyUtilities.getSkullViaURL("f21d93da43863cb3759afefa9f7cc5c81f34d920ca97b7283b462f8b197f813"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).getEliteCraftConfig().setShapeless(false);
            return true;
        }), new ButtonState("recipe_creator", "workbench.shapeless.disabled", WolfyUtilities.getSkullViaURL("1aae7e8222ddbee19d184b97e79067814b6ba3142a3bdcce8b93099a312"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).getEliteCraftConfig().setShapeless(true);
            return true;
        })));

        registerButton(new ToggleButton("workbench.mirrorHorizontal", false, new ButtonState("recipe_creator", "workbench.mirrorHorizontal.enabled", WolfyUtilities.getSkullViaURL("956a3618459e43b287b22b7e235ec699594546c6fcd6dc84bfca4cf30ab9311"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).getEliteCraftConfig().setMirrorHorizontal(false);
            return true;
        }), new ButtonState("recipe_creator", "workbench.mirrorHorizontal.disabled", WolfyUtilities.getSkullViaURL("956a3618459e43b287b22b7e235ec699594546c6fcd6dc84bfca4cf30ab9311"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).getEliteCraftConfig().setMirrorHorizontal(true);
            return true;
        })));
        registerButton(new ToggleButton("workbench.mirrorVertical", false, new ButtonState("recipe_creator", "workbench.mirrorVertical.enabled", WolfyUtilities.getSkullViaURL("882faf9a584c4d676d730b23f8942bb997fa3dad46d4f65e288c39eb471ce7"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).getEliteCraftConfig().setMirrorVertical(false);
            return true;
        }), new ButtonState("recipe_creator", "workbench.mirrorVertical.disabled", WolfyUtilities.getSkullViaURL("882faf9a584c4d676d730b23f8942bb997fa3dad46d4f65e288c39eb471ce7"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).getEliteCraftConfig().setMirrorVertical(true);
            return true;
        })));
        registerButton(new ToggleButton("workbench.mirrorRotation", false, new ButtonState("recipe_creator", "workbench.mirrorRotation.enabled", WolfyUtilities.getSkullViaURL("e887cc388c8dcfcf1ba8aa5c3c102dce9cf7b1b63e786b34d4f1c3796d3e9d61"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).getEliteCraftConfig().setMirrorRotation(false);
            return true;
        }), new ButtonState("recipe_creator", "workbench.mirrorRotation.disabled", WolfyUtilities.getSkullViaURL("e887cc388c8dcfcf1ba8aa5c3c102dce9cf7b1b63e786b34d4f1c3796d3e9d61"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).getEliteCraftConfig().setMirrorRotation(true);
            return true;
        })));
    }

    @EventHandler
    public void onUpdate(GuiUpdateEvent event) {
        if (event.verify(this)) {
            event.setButton(6, "back");
            TestCache cache = (TestCache) event.getGuiHandler().getCustomCache();
            EliteCraftConfig workbench = cache.getEliteCraftConfig();
            ((ToggleButton) event.getGuiWindow().getButton("workbench.shapeless")).setState(event.getGuiHandler(), workbench.isShapeless());
            ((ToggleButton) event.getGuiWindow().getButton("exact_meta")).setState(event.getGuiHandler(), workbench.isExactMeta());
            ((ToggleButton) event.getGuiWindow().getButton("workbench.mirrorHorizontal")).setState(event.getGuiHandler(), workbench.mirrorHorizontal());
            ((ToggleButton) event.getGuiWindow().getButton("workbench.mirrorVertical")).setState(event.getGuiHandler(), workbench.mirrorVertical());
            ((ToggleButton) event.getGuiWindow().getButton("workbench.mirrorRotation")).setState(event.getGuiHandler(), workbench.mirrorRotation());
            ((ToggleButton) event.getGuiWindow().getButton("hidden")).setState(event.getGuiHandler(), workbench.isHidden());
            int slot;
            for (int i = 0; i < 36; i++) {
                slot = i + (i / 6) * 3;
                event.setButton(slot, "crafting.container_" + i);
            }
            event.setButton(25, "crafting.container_36");
            event.setButton(24, "workbench.shapeless");

            event.setButton(35, "hidden");
            if (workbench.mirrorHorizontal() && workbench.mirrorVertical()) {
                event.setButton(33, "workbench.mirrorRotation");
            }
            event.setButton(42, "workbench.mirrorHorizontal");
            event.setButton(43, "workbench.mirrorVertical");
            event.setButton(44, "recipe_creator", "conditions");

            event.setButton(51, "exact_meta");
            event.setButton(52, "priority");

            event.setButton(53, "save");
        }
    }

    private boolean validToSave(TestCache cache) {
        EliteCraftConfig workbench = cache.getEliteCraftConfig();
        return workbench.getIngredients() != null && !workbench.getResult().isEmpty();
    }
}
