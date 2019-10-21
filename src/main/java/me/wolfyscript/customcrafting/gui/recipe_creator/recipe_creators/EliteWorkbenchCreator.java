package me.wolfyscript.customcrafting.gui.recipe_creator.recipe_creators;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.PlayerCache;
import me.wolfyscript.customcrafting.data.cache.Workbench;
import me.wolfyscript.customcrafting.gui.ExtendedGuiWindow;
import me.wolfyscript.customcrafting.gui.recipe_creator.buttons.CraftingIngredientButton;
import me.wolfyscript.customcrafting.recipes.RecipePriority;
import me.wolfyscript.customcrafting.recipes.RecipeUtils;
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

    public EliteWorkbenchCreator(InventoryAPI inventoryAPI) {
        super("elite_workbench", inventoryAPI, 54);
    }

    @Override
    public void onInit() {
        registerButton(new ActionButton("back", new ButtonState("none", "back", WolfyUtilities.getCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODY0Zjc3OWE4ZTNmZmEyMzExNDNmYTY5Yjk2YjE0ZWUzNWMxNmQ2NjllMTljNzVmZDFhN2RhNGJmMzA2YyJ9fX0="), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            guiHandler.openCluster("none");
            return true;
        })));
        registerButton(new ActionButton("save", new ButtonState("save", Material.WRITABLE_BOOK, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            PlayerCache cache = CustomCrafting.getPlayerCache(player);
            if (validToSave(cache)) {
                openChat(guiHandler, "$msg.gui.none.recipe_creator.save.input$", (guiHandler1, player1, s, args) -> {
                    if (args.length > 1) {
                        String namespace = args[0].toLowerCase(Locale.ROOT).replace(" ", "_");
                        String key = args[1].toLowerCase(Locale.ROOT).replace(" ", "_");
                        if (!RecipeUtils.testNameSpaceKey(namespace, key)) {
                            api.sendPlayerMessage(player, "&cInvalid Namespace or Key! Namespaces & Keys may only contain lowercase alphanumeric characters, periods, underscores, and hyphens!");
                        }
                        EliteCraftConfig config;
                        if (CustomCrafting.hasDataBaseHandler()) {
                            config = new EliteCraftConfig("{}", api.getConfigAPI(), namespace, key);
                        } else {
                            config = new EliteCraftConfig(api.getConfigAPI(), namespace, key);
                        }
                        config.saveRecipe(6, CustomCrafting.getPlayerCache(player1));
                        api.sendPlayerMessage(player, "$msg.gui.none.recipe_creator.save.success$");
                        api.sendPlayerMessage(player, "§6recipes/" + namespace + "/workbench/" + key);

                        try {
                            EliteCraftingRecipe customRecipe;
                            if (CustomCrafting.hasDataBaseHandler()) {
                                customRecipe = (EliteCraftingRecipe) CustomCrafting.getDataBaseHandler().getRecipe(namespace, key);
                            } else {
                                if (config.isShapeless()) {
                                    customRecipe = new ShapelessEliteCraftRecipe(config);
                                } else {
                                    customRecipe = new ShapedEliteCraftRecipe(config);
                                }
                            }
                            Bukkit.getScheduler().runTaskLater(CustomCrafting.getInst(), () -> {
                                CustomCrafting.getRecipeHandler().injectRecipe(customRecipe);
                                api.sendPlayerMessage(player, "$msg.gui.none.recipe_creator.loading.success$");
                            }, 1);
                        } catch (Exception ex) {
                            api.sendPlayerMessage(player, "$msg.gui.none.recipe_creator.loading.error$", new String[]{"%REC%", config.getId()});
                            ex.printStackTrace();
                            return false;
                        }
                        Bukkit.getScheduler().runTaskLater(CustomCrafting.getInst(), () -> guiHandler.changeToInv("main_menu"), 1);
                        return false;
                    }
                    return false;
                });
            } else {
                api.sendPlayerMessage(player, "$msg.gui.none.recipe_creator.save.empty$");
            }
            return false;
        })));

        for (int i = 0; i < 37; i++) {
            registerButton(new CraftingIngredientButton(i));
        }

        registerButton(new ToggleButton("exact_meta", new ButtonState("recipe_creator", "exact_meta.enabled", Material.GREEN_CONCRETE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            CustomCrafting.getPlayerCache(player).getEliteWorkbench().setExactMeta(false);
            return true;
        }), new ButtonState("recipe_creator", "exact_meta.disabled", Material.RED_CONCRETE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            CustomCrafting.getPlayerCache(player).getWorkbench().setExactMeta(true);
            return true;
        })));
        registerButton(new ActionButton("priority", new ButtonState("priority", WolfyUtilities.getSkullViaURL("b8ea57c7551c6ab33b8fed354b43df523f1e357c4b4f551143c34ddeac5b6c8d"), new ButtonActionRender() {
            @Override
            public boolean run(GuiHandler guiHandler, Player player, Inventory inventory, int i, InventoryClickEvent inventoryClickEvent) {
                RecipePriority priority = CustomCrafting.getPlayerCache(player).getEliteWorkbench().getPriority();
                int order;
                order = priority.getOrder();
                if (order < 2) {
                    order++;
                } else {
                    order = -2;
                }
                CustomCrafting.getPlayerCache(player).getEliteWorkbench().setPriority(RecipePriority.getByOrder(order));
                return true;
            }

            @Override
            public ItemStack render(HashMap<String, Object> hashMap, GuiHandler guiHandler, Player player, ItemStack itemStack, int slot, boolean help) {
                RecipePriority priority = CustomCrafting.getPlayerCache(player).getWorkbench().getPriority();
                if (priority != null) {
                    hashMap.put("%PRI%", priority.name());
                }
                return itemStack;
            }
        })));

        registerButton(new ToggleButton("workbench.shapeless", false, new ButtonState("recipe_creator", "workbench.shapeless.enabled", WolfyUtilities.getSkullViaURL("f21d93da43863cb3759afefa9f7cc5c81f34d920ca97b7283b462f8b197f813"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            CustomCrafting.getPlayerCache(player).getEliteWorkbench().setShapeless(false);
            return true;
        }), new ButtonState("recipe_creator", "workbench.shapeless.disabled", WolfyUtilities.getSkullViaURL("1aae7e8222ddbee19d184b97e79067814b6ba3142a3bdcce8b93099a312"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            CustomCrafting.getPlayerCache(player).getEliteWorkbench().setShapeless(true);
            return true;
        })));
    }

    @EventHandler
    public void onUpdate(GuiUpdateEvent event) {
        if (event.verify(this)) {
            event.setButton(6, "back");
            PlayerCache cache = CustomCrafting.getPlayerCache(event.getPlayer());
            Workbench workbench = cache.getWorkbench();

            ((ToggleButton) event.getGuiWindow().getButton("workbench.shapeless")).setState(event.getGuiHandler(), workbench.isShapeless());
            ((ToggleButton) event.getGuiWindow().getButton("exact_meta")).setState(event.getGuiHandler(), workbench.isExactMeta());
            int slot;
            for (int i = 0; i < 36; i++) {
                slot = i + (i / 6) * 3;
                event.setButton(slot, "crafting.container_" + i);
            }
            event.setButton(25, "crafting.container_36");
            event.setButton(33, "workbench.shapeless");
            event.setButton(35, "recipe_creator", "conditions");

            event.setButton(51, "exact_meta");
            event.setButton(52, "priority");

            event.setButton(53, "save");
        }
    }

    private boolean validToSave(PlayerCache cache) {
        Workbench workbench = cache.getWorkbench();
        return workbench.getIngredients() != null && !workbench.getResult().isEmpty();
    }
}
