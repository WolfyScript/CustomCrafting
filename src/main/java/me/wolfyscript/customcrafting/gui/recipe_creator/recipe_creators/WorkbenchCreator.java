package me.wolfyscript.customcrafting.gui.recipe_creator.recipe_creators;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.PlayerCache;
import me.wolfyscript.customcrafting.data.cache.Workbench;
import me.wolfyscript.customcrafting.gui.ExtendedGuiWindow;
import me.wolfyscript.customcrafting.gui.main_gui.buttons.CraftingContainerButton;
import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.RecipePriority;
import me.wolfyscript.customcrafting.recipes.RecipeUtils;
import me.wolfyscript.customcrafting.recipes.types.workbench.AdvancedCraftConfig;
import me.wolfyscript.customcrafting.recipes.types.workbench.AdvancedCraftingRecipe;
import me.wolfyscript.customcrafting.recipes.types.workbench.ShapedCraftRecipe;
import me.wolfyscript.customcrafting.recipes.types.workbench.ShapelessCraftRecipe;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.GuiHandler;
import me.wolfyscript.utilities.api.inventory.GuiUpdateEvent;
import me.wolfyscript.utilities.api.inventory.InventoryAPI;
import me.wolfyscript.utilities.api.inventory.button.ButtonActionRender;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.buttons.ActionButton;
import me.wolfyscript.utilities.api.inventory.button.buttons.ToggleButton;
import me.wolfyscript.utilities.api.utils.InventoryUtils;
import me.wolfyscript.utilities.api.utils.item_builder.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class WorkbenchCreator extends ExtendedGuiWindow {

    public WorkbenchCreator(InventoryAPI inventoryAPI) {
        super("workbench", inventoryAPI, 45);
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
                    PlayerCache cache1 = CustomCrafting.getPlayerCache(player1);
                    Workbench workbench = cache1.getWorkbench();
                    if (args.length > 1) {
                        String namespace = args[0].toLowerCase(Locale.ROOT).replace(" ", "_");
                        String key = args[1].toLowerCase(Locale.ROOT).replace(" ", "_");
                        if (!RecipeUtils.testNameSpaceKey(namespace, key)) {
                            api.sendPlayerMessage(player, "&cInvalid Namespace or Key! Namespaces & Keys may only contain lowercase alphanumeric characters, periods, underscores, and hyphens!");
                        }
                        AdvancedCraftConfig config;
                        if (CustomCrafting.hasDataBaseHandler()) {
                            config = new AdvancedCraftConfig("{}", api.getConfigAPI(), namespace, key);
                        } else {
                            config = new AdvancedCraftConfig(api.getConfigAPI(), namespace, key);
                        }
                        api.sendDebugMessage("Create Config:");
                        api.sendDebugMessage("  id: " + namespace + ":" + key);
                        api.sendDebugMessage("  Conditions: " + workbench.getConditions().toMap());
                        api.sendDebugMessage("  Shapeless: " + workbench.isShapeless());
                        api.sendDebugMessage("  ExactMeta: " + workbench.isExactMeta());
                        api.sendDebugMessage("  Priority: " + workbench.getPriority());
                        api.sendDebugMessage("  Result: " + workbench.getResult());
                        config.setShapeless(workbench.isShapeless());
                        config.setExactMeta(workbench.isExactMeta());
                        config.setPriority(workbench.getPriority());
                        config.setConditions(workbench.getConditions());

                        config.setResult(workbench.getResult());
                        HashMap<Character, List<CustomItem>> ingredients = workbench.getIngredients();
                        api.sendDebugMessage("  Ingredients: " + ingredients);
                        String[] shape = new String[3];
                        int index = 0;
                        int row = 0;
                        for (char ingrd : ingredients.keySet()) {
                            List<CustomItem> keyItems = ingredients.get(ingrd);
                            if (InventoryUtils.isEmpty(new ArrayList<>(keyItems))) {
                                if (shape[row] != null) {
                                    shape[row] = shape[row] + " ";
                                } else {
                                    shape[row] = " ";
                                }
                            } else {
                                if (shape[row] != null) {
                                    shape[row] = shape[row] + ingrd;
                                } else {
                                    shape[row] = String.valueOf(ingrd);
                                }
                            }
                            index++;
                            if ((index % 3) == 0) {
                                row++;
                            }
                        }
                        api.sendDebugMessage("  Shape:");
                        for (String shapeRow : shape) {
                            api.sendDebugMessage("      " + shapeRow);
                        }
                        config.setShape(shape);
                        config.setIngredients(workbench.getIngredients());
                        api.sendDebugMessage("Saving...");

                        if (CustomCrafting.hasDataBaseHandler()) {
                            CustomCrafting.getDataBaseHandler().updateRecipe(config, false);
                        } else {
                            config.reload(CustomCrafting.getConfigHandler().getConfig().isPrettyPrinting());
                        }
                        api.sendDebugMessage("Reset GUI cache...");
                        cache1.resetWorkbench();
                        api.sendPlayerMessage(player, "$msg.gui.none.recipe_creator.save.success$");
                        api.sendPlayerMessage(player, "§6recipes/" + namespace + "/workbench/" + key);

                        try {
                            AdvancedCraftingRecipe customRecipe;
                            if (CustomCrafting.hasDataBaseHandler()) {
                                customRecipe = (AdvancedCraftingRecipe) CustomCrafting.getDataBaseHandler().getRecipe(namespace, key);
                            } else {
                                api.sendDebugMessage("Loading Recipe...");
                                if (config.isShapeless()) {
                                    customRecipe = new ShapelessCraftRecipe(config);
                                } else {
                                    customRecipe = new ShapedCraftRecipe(config);
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

        for (int i = 0; i < 10; i++) {
            registerButton(new CraftingContainerButton(i));
        }

        registerButton(new ToggleButton("exact_meta", new ButtonState("recipe_creator", "exact_meta.enabled", Material.GREEN_CONCRETE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            CustomCrafting.getPlayerCache(player).getWorkbench().setExactMeta(false);
            return true;
        }), new ButtonState("recipe_creator", "exact_meta.disabled", Material.RED_CONCRETE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            CustomCrafting.getPlayerCache(player).getWorkbench().setExactMeta(true);
            return true;
        })));
        registerButton(new ActionButton("priority", new ButtonState("priority", WolfyUtilities.getSkullViaURL("b8ea57c7551c6ab33b8fed354b43df523f1e357c4b4f551143c34ddeac5b6c8d"), new ButtonActionRender() {
            @Override
            public boolean run(GuiHandler guiHandler, Player player, Inventory inventory, int i, InventoryClickEvent inventoryClickEvent) {
                RecipePriority priority = CustomCrafting.getPlayerCache(player).getWorkbench().getPriority();
                int order;
                order = priority.getOrder();
                if (order < 2) {
                    order++;
                } else {
                    order = -2;
                }
                CustomCrafting.getPlayerCache(player).getWorkbench().setPriority(RecipePriority.getByOrder(order));
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
        registerButton(new ToggleButton("permission", new ButtonState("permission.disabled", Material.RED_CONCRETE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            Workbench workbench = CustomCrafting.getPlayerCache(player).getWorkbench();
            workbench.getConditions().getByID("permission").setOption(Conditions.Option.EXACT);
            return true;
        }), new ButtonState("permission.enabled", Material.GREEN_CONCRETE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            Workbench workbench = CustomCrafting.getPlayerCache(player).getWorkbench();
            workbench.getConditions().getByID("permission").setOption(Conditions.Option.IGNORE);

            return true;
        })));

        registerButton(new ToggleButton("workbench.shapeless", false, new ButtonState("workbench.shapeless.enabled", WolfyUtilities.getSkullViaURL("f21d93da43863cb3759afefa9f7cc5c81f34d920ca97b7283b462f8b197f813"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            Workbench workbench = CustomCrafting.getPlayerCache(player).getWorkbench();
            workbench.setShapeless(false);
            return true;
        }), new ButtonState("workbench.shapeless.disabled", WolfyUtilities.getSkullViaURL("1aae7e8222ddbee19d184b97e79067814b6ba3142a3bdcce8b93099a312"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            Workbench workbench = CustomCrafting.getPlayerCache(player).getWorkbench();
            workbench.setShapeless(true);
            return true;
        })));
        registerButton(new ToggleButton("workbench.adv_workbench", false, new ButtonState("workbench.adv_workbench.enabled", new ItemBuilder(Material.CRAFTING_TABLE).addUnsafeEnchantment(Enchantment.FIRE_ASPECT, 0).addItemFlags(ItemFlag.HIDE_ENCHANTS).create(), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            Workbench workbench = CustomCrafting.getPlayerCache(player).getWorkbench();
            workbench.getConditions().getByID("advanced_workbench").setOption(Conditions.Option.IGNORE);
            return true;
        }), new ButtonState("workbench.adv_workbench.disabled", Material.CRAFTING_TABLE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            Workbench workbench = CustomCrafting.getPlayerCache(player).getWorkbench();
            workbench.getConditions().getByID("advanced_workbench").setOption(Conditions.Option.EXACT);
            return true;
        })));
    }

    @EventHandler
    public void onUpdate(GuiUpdateEvent event) {
        if (event.verify(this)) {
            event.setButton(0, "back");
            PlayerCache cache = CustomCrafting.getPlayerCache(event.getPlayer());
            Workbench workbench = cache.getWorkbench();

            ((ToggleButton) event.getGuiWindow().getButton("workbench.shapeless")).setState(event.getGuiHandler(), workbench.isShapeless());
            ((ToggleButton) event.getGuiWindow().getButton("workbench.adv_workbench")).setState(event.getGuiHandler(), workbench.getConditions().getByID("advanced_workbench").getOption().equals(Conditions.Option.EXACT));
            ((ToggleButton) event.getGuiWindow().getButton("exact_meta")).setState(event.getGuiHandler(), workbench.isExactMeta());
            ((ToggleButton) event.getGuiWindow().getButton("permission")).setState(event.getGuiHandler(), !workbench.getConditions().getByID("permission").getOption().equals(Conditions.Option.EXACT));
            event.setButton(35, "priority");
            if (!workbench.getIngredients().isEmpty()) {
                int slot;
                for (int i = 0; i < 9; i++) {
                    slot = 10 + i + (i / 3) * 6;
                    event.setButton(slot, "crafting.container_" + i);
                }
            }
            event.setButton(26, "exact_meta");
            event.setButton(22, "workbench.shapeless");
            event.setButton(24, "crafting.container_9");
            event.setButton(2, "recipe_creator","conditions");
            event.setButton(4, "permission");
            event.setButton(6, "workbench.adv_workbench");
            event.setButton(44, "save");
        }
    }

    private boolean validToSave(PlayerCache cache) {
        Workbench workbench = cache.getWorkbench();
        return workbench.getIngredients() != null && !workbench.getResult().isEmpty();
    }
}
