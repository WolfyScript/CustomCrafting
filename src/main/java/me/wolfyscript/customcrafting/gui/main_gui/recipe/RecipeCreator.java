package me.wolfyscript.customcrafting.gui.main_gui.recipe;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.custom_configs.CookingConfig;
import me.wolfyscript.customcrafting.configs.custom_configs.anvil.AnvilConfig;
import me.wolfyscript.customcrafting.configs.custom_configs.blast_furnace.BlastingConfig;
import me.wolfyscript.customcrafting.configs.custom_configs.campfire.CampfireConfig;
import me.wolfyscript.customcrafting.configs.custom_configs.furnace.FurnaceConfig;
import me.wolfyscript.customcrafting.configs.custom_configs.smoker.SmokerConfig;
import me.wolfyscript.customcrafting.configs.custom_configs.stonecutter.StonecutterConfig;
import me.wolfyscript.customcrafting.configs.custom_configs.workbench.CraftConfig;
import me.wolfyscript.customcrafting.data.PlayerCache;
import me.wolfyscript.customcrafting.data.cache.Anvil;
import me.wolfyscript.customcrafting.data.cache.CookingData;
import me.wolfyscript.customcrafting.data.cache.Stonecutter;
import me.wolfyscript.customcrafting.data.cache.Workbench;
import me.wolfyscript.customcrafting.gui.ExtendedGuiWindow;
import me.wolfyscript.customcrafting.gui.main_gui.buttons.AnvilContainerButton;
import me.wolfyscript.customcrafting.gui.main_gui.buttons.CookingContainerButton;
import me.wolfyscript.customcrafting.gui.main_gui.buttons.CraftingContainerButton;
import me.wolfyscript.customcrafting.gui.main_gui.buttons.StonecutterContainerButton;
import me.wolfyscript.customcrafting.items.CustomItem;
import me.wolfyscript.customcrafting.recipes.CustomRecipe;
import me.wolfyscript.customcrafting.recipes.RecipePriority;
import me.wolfyscript.customcrafting.recipes.anvil.CustomAnvilRecipe;
import me.wolfyscript.customcrafting.recipes.blast_furnace.CustomBlastRecipe;
import me.wolfyscript.customcrafting.recipes.campfire.CustomCampfireRecipe;
import me.wolfyscript.customcrafting.recipes.furnace.CustomFurnaceRecipe;
import me.wolfyscript.customcrafting.recipes.smoker.CustomSmokerRecipe;
import me.wolfyscript.customcrafting.recipes.stonecutter.CustomStonecutterRecipe;
import me.wolfyscript.customcrafting.recipes.workbench.CraftingRecipe;
import me.wolfyscript.customcrafting.recipes.workbench.ShapedCraftRecipe;
import me.wolfyscript.customcrafting.recipes.workbench.ShapelessCraftRecipe;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.GuiHandler;
import me.wolfyscript.utilities.api.inventory.GuiUpdateEvent;
import me.wolfyscript.utilities.api.inventory.InventoryAPI;
import me.wolfyscript.utilities.api.inventory.button.ButtonActionRender;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.buttons.ActionButton;
import me.wolfyscript.utilities.api.inventory.button.buttons.ChatInputButton;
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
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class RecipeCreator extends ExtendedGuiWindow {

    public RecipeCreator(InventoryAPI inventoryAPI) {
        super("recipe_creator", inventoryAPI, 45);
    }

    @Override
    public void onInit() {
        registerButton(new ActionButton("back", new ButtonState("none", "back", WolfyUtilities.getCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODY0Zjc3OWE4ZTNmZmEyMzExNDNmYTY5Yjk2YjE0ZWUzNWMxNmQ2NjllMTljNzVmZDFhN2RhNGJmMzA2YyJ9fX0="), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            guiHandler.openPreviousInv();
            return true;
        })));
        registerButton(new ActionButton("save", new ButtonState("save", Material.WRITABLE_BOOK, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            PlayerCache cache = CustomCrafting.getPlayerCache(player);
            if (validToSave(cache)) {
                openChat(guiHandler, "$msg.gui.recipe_creator.save.input$", (guiHandler1, player1, s, args) -> {
                    PlayerCache cache1 = CustomCrafting.getPlayerCache(player1);
                    Workbench workbench = cache1.getWorkbench();
                    CookingData furnace = cache1.getCookingData();
                    Anvil anvil = cache1.getAnvil();
                    if (args.length > 1) {
                        String namespace = args[0].toLowerCase(Locale.ROOT).replace(" ", "_");
                        String key = args[1].toLowerCase(Locale.ROOT).replace(" ", "_");
                        if (!CustomCrafting.VALID_NAMESPACE.matcher(namespace).matches()) {
                            api.sendPlayerMessage(player, "&cInvalid Namespace! Namespaces may only contain lowercase alphanumeric characters, periods, underscores, and hyphens!");
                            return true;
                        }
                        if (!CustomCrafting.VALID_KEY.matcher(key).matches()) {
                            api.sendPlayerMessage(player, "&cInvalid key! Keys may only contain lowercase alphanumeric characters, periods, underscores, and hyphens!");
                            return true;
                        }
                        CookingConfig cookingConfig = null;
                        switch (cache1.getSetting()) {
                            case WORKBENCH:
                                CraftConfig config;
                                if (CustomCrafting.hasDataBaseHandler()) {
                                    config = new CraftConfig("{}", api.getConfigAPI(), namespace, key);
                                } else {
                                    config = new CraftConfig(api.getConfigAPI(), namespace, key);
                                }
                                api.sendDebugMessage("Create Config:");
                                api.sendDebugMessage("  id: " + namespace + ":" + key);
                                api.sendDebugMessage("  Permission: " + workbench.isPermissions());
                                api.sendDebugMessage("  Adv Workbench: " + workbench.isAdvWorkbench());
                                api.sendDebugMessage("  Shapeless: " + workbench.isShapeless());
                                api.sendDebugMessage("  ExactMeta: " + workbench.isExactMeta());
                                api.sendDebugMessage("  Priority: " + workbench.getPriority());
                                api.sendDebugMessage("  Result: " + workbench.getResult());
                                config.setPermission(workbench.isPermissions());
                                config.setNeedWorkbench(workbench.isAdvWorkbench());
                                config.setShapeless(workbench.isShapeless());
                                config.setExactMeta(workbench.isExactMeta());
                                config.setPriority(workbench.getPriority());

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
                                    CustomCrafting.getDataBaseHandler().updateRecipe(config);
                                } else {
                                    config.reload(CustomCrafting.getConfigHandler().getConfig().isPrettyPrinting());
                                }
                                api.sendDebugMessage("Reset GUI cache...");
                                cache1.resetWorkbench();
                                api.sendPlayerMessage(player, "$msg.gui.recipe_creator.save.success$");
                                api.sendPlayerMessage(player, "§6recipes/" + namespace + "/workbench/" + key);
                                try {
                                    api.sendDebugMessage("Loading Recipe...");
                                    CraftingRecipe customRecipe;
                                    if (config.isShapeless()) {
                                        customRecipe = new ShapelessCraftRecipe(config);
                                    } else {
                                        customRecipe = new ShapedCraftRecipe(config);
                                    }
                                    Bukkit.getScheduler().runTaskLater(CustomCrafting.getInst(), () -> {
                                        CustomCrafting.getRecipeHandler().injectRecipe(customRecipe);
                                        api.sendPlayerMessage(player, "$msg.gui.recipe_creator.loading.success$");
                                    }, 1);
                                } catch (Exception ex) {
                                    api.sendPlayerMessage(player, "$msg.gui.recipe_creator.loading.error$", new String[]{"%REC%", config.getId()});
                                    ex.printStackTrace();
                                    return false;
                                }
                                Bukkit.getScheduler().runTaskLater(CustomCrafting.getInst(), () -> guiHandler.changeToInv("main_menu"), 1);
                                return false;
                            case ANVIL:
                                AnvilConfig anvilConfig;
                                if (CustomCrafting.hasDataBaseHandler()) {
                                    anvilConfig = new AnvilConfig("{}", api.getConfigAPI(), namespace, key);
                                } else {
                                    anvilConfig = new AnvilConfig(api.getConfigAPI(), namespace, key);
                                }
                                anvilConfig.setBlockEnchant(anvil.isBlockEnchant());
                                anvilConfig.setBlockRename(anvil.isBlockRename());
                                anvilConfig.setBlockRepairing(anvil.isBlockRepair());
                                anvilConfig.setExactMeta(anvil.isExactMeta());
                                anvilConfig.setPermission(anvil.isPermissions());
                                anvilConfig.setRepairCostMode(anvil.getRepairCostMode());
                                anvilConfig.setRepairCost(anvil.getRepairCost());
                                anvilConfig.setPriority(anvil.getPriority());
                                anvilConfig.setMode(anvil.getMode());
                                anvilConfig.setResult(anvil.getResult());
                                anvilConfig.setDurability(anvil.getDurability());
                                anvilConfig.setInputLeft(anvil.getIngredients(0));
                                anvilConfig.setInputRight(anvil.getIngredients(1));

                                if (CustomCrafting.hasDataBaseHandler()) {
                                    CustomCrafting.getDataBaseHandler().updateRecipe(anvilConfig);
                                } else {
                                    anvilConfig.reload(CustomCrafting.getConfigHandler().getConfig().isPrettyPrinting());
                                }
                                cache1.resetAnvil();

                                try {
                                    Bukkit.getScheduler().runTaskLater(CustomCrafting.getInst(), () -> {
                                        CustomCrafting.getRecipeHandler().injectRecipe(new CustomAnvilRecipe(anvilConfig));
                                        api.sendPlayerMessage(player, "$msg.gui.recipe_creator.loading.success$");
                                    }, 1);
                                } catch (Exception ex) {
                                    api.sendPlayerMessage(player, "$msg.gui.recipe_creator.error_loading$", new String[]{"%REC%", anvilConfig.getId()});
                                    ex.printStackTrace();
                                    return false;
                                }
                                Bukkit.getScheduler().runTaskLater(CustomCrafting.getInst(), () -> guiHandler.changeToInv("main_menu"), 1);
                                return false;
                            case STONECUTTER:
                                Stonecutter stonecutter = cache1.getStonecutter();
                                StonecutterConfig stonecutterConfig;
                                if (CustomCrafting.hasDataBaseHandler()) {
                                    stonecutterConfig = new StonecutterConfig("{}", api.getConfigAPI(), namespace, key);
                                } else {
                                    stonecutterConfig = new StonecutterConfig(api.getConfigAPI(), namespace, key);
                                }

                                stonecutterConfig.setResult(stonecutter.getResult());
                                stonecutterConfig.setSource(stonecutter.getSource());
                                stonecutterConfig.setExactMeta(stonecutter.isExactMeta());
                                stonecutterConfig.setPriority(stonecutter.getPriority());
                                if (CustomCrafting.hasDataBaseHandler()) {
                                    CustomCrafting.getDataBaseHandler().updateRecipe(stonecutterConfig);
                                } else {
                                    stonecutterConfig.reload(CustomCrafting.getConfigHandler().getConfig().isPrettyPrinting());
                                }
                                cache1.resetStonecutter();
                                try {
                                    Bukkit.getScheduler().runTaskLater(CustomCrafting.getInst(), () -> {
                                        CustomCrafting.getRecipeHandler().injectRecipe(new CustomStonecutterRecipe(stonecutterConfig));
                                        api.sendPlayerMessage(player, "$msg.gui.recipe_creator.loading.success$");
                                    }, 1);

                                } catch (Exception ex) {
                                    api.sendPlayerMessage(player, "$msg.gui.recipe_creator.error_loading$", new String[]{"%REC%", stonecutterConfig.getId()});
                                    ex.printStackTrace();
                                    return false;
                                }
                                Bukkit.getScheduler().runTaskLater(CustomCrafting.getInst(), () -> guiHandler.changeToInv("main_menu"), 1);
                                return false;
                            case BLAST_FURNACE:
                                cookingConfig = new BlastingConfig(api.getConfigAPI(), namespace, key);
                            case SMOKER:
                                if (cookingConfig == null) {
                                    cookingConfig = new SmokerConfig(api.getConfigAPI(), namespace, key);
                                }
                            case CAMPFIRE:
                                if (cookingConfig == null) {
                                    cookingConfig = new CampfireConfig(api.getConfigAPI(), namespace, key);
                                }
                            case FURNACE:
                                if (cookingConfig == null) {
                                    cookingConfig = new FurnaceConfig(api.getConfigAPI(), namespace, key);
                                }
                                //furnaceConfig.setAdvancedFurnace(furnace.isAdvFurnace());
                                cookingConfig.setCookingTime(furnace.getCookingTime());
                                cookingConfig.setXP(furnace.getExperience());
                                cookingConfig.setResult(furnace.getResult());
                                cookingConfig.setSource(furnace.getSource());
                                cookingConfig.setExactMeta(furnace.isExactMeta());
                                if (CustomCrafting.hasDataBaseHandler()) {
                                    CustomCrafting.getDataBaseHandler().updateRecipe(cookingConfig);
                                } else {
                                    cookingConfig.reload(CustomCrafting.getConfigHandler().getConfig().isPrettyPrinting());
                                }
                                cache1.resetCookingData();
                                api.sendPlayerMessage(player, "$msg.gui.recipe_creator.save.success$");
                                api.sendPlayerMessage(player, "§6recipes/" + namespace + "/furnace/" + key);
                                try {
                                    CustomRecipe customRecipe = null;
                                    switch (cache1.getSetting()) {
                                        case SMOKER:
                                            customRecipe = new CustomSmokerRecipe((SmokerConfig) cookingConfig);
                                            break;
                                        case CAMPFIRE:
                                            customRecipe = new CustomCampfireRecipe((CampfireConfig) cookingConfig);
                                            break;
                                        case FURNACE:
                                            customRecipe = new CustomFurnaceRecipe((FurnaceConfig) cookingConfig);
                                            break;
                                        case BLAST_FURNACE:
                                            customRecipe = new CustomBlastRecipe((BlastingConfig) cookingConfig);
                                    }
                                    if (customRecipe != null) {
                                        CustomRecipe finalCustomRecipe = customRecipe;
                                        Bukkit.getScheduler().runTaskLater(CustomCrafting.getInst(), () -> CustomCrafting.getRecipeHandler().injectRecipe(finalCustomRecipe), 1);
                                    } else {
                                        api.sendPlayerMessage(player, "$msg.gui.recipe_creator.error_loading$", new String[]{"%REC%", cookingConfig.getId()});
                                    }
                                } catch (Exception ex) {
                                    api.sendPlayerMessage(player, "$msg.gui.recipe_creator.error_loading$", new String[]{"%REC%", cookingConfig.getId()});
                                    ex.printStackTrace();
                                    return false;
                                }
                                Bukkit.getScheduler().runTaskLater(CustomCrafting.getInst(), () -> guiHandler.changeToInv("main_menu"), 1);
                                return false;
                        }
                    }
                    return false;
                });
            } else {
                api.sendPlayerMessage(player, "$msg.gui.recipe_creator.save.empty$");
            }
            return true;
        })));

        for (int i = 0; i < 10; i++) {
            registerButton(new CraftingContainerButton(i));
        }

        registerButton(new AnvilContainerButton(0));
        registerButton(new AnvilContainerButton(1));
        registerButton(new AnvilContainerButton(2));

        registerButton(new CookingContainerButton(0));
        registerButton(new CookingContainerButton(1));

        registerButton(new StonecutterContainerButton(0));
        registerButton(new StonecutterContainerButton(1));

        registerButton(new ToggleButton("permission", new ButtonState("permission.disabled", Material.RED_CONCRETE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            switch (CustomCrafting.getPlayerCache(player).getSetting()) {
                case WORKBENCH:
                    Workbench workbench = CustomCrafting.getPlayerCache(player).getWorkbench();
                    workbench.setPermissions(true);
                    break;
                case ANVIL:
                    Anvil anvil = CustomCrafting.getPlayerCache(player).getAnvil();
                    anvil.setPermissions(true);
                    break;
            }
            return true;
        }), new ButtonState("permission.enabled", Material.GREEN_CONCRETE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            switch (CustomCrafting.getPlayerCache(player).getSetting()) {
                case WORKBENCH:
                    Workbench workbench = CustomCrafting.getPlayerCache(player).getWorkbench();
                    workbench.setPermissions(false);
                    break;
                case ANVIL:
                    Anvil anvil = CustomCrafting.getPlayerCache(player).getAnvil();
                    anvil.setPermissions(false);
                    break;
            }
            return true;
        })));
        registerButton(new ToggleButton("exact_meta", new ButtonState("exact_meta_on", Material.GREEN_CONCRETE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            PlayerCache cache = CustomCrafting.getPlayerCache(player);
            switch (cache.getSetting()) {
                case WORKBENCH:
                    CustomCrafting.getPlayerCache(player).getWorkbench().setExactMeta(false);
                    break;
                case ANVIL:
                    CustomCrafting.getPlayerCache(player).getAnvil().setExactMeta(false);
            }
            return true;
        }), new ButtonState("exact_meta_off", Material.RED_CONCRETE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            PlayerCache cache = CustomCrafting.getPlayerCache(player);
            switch (cache.getSetting()) {
                case WORKBENCH:
                    CustomCrafting.getPlayerCache(player).getWorkbench().setExactMeta(true);
                    break;
                case ANVIL:
                    CustomCrafting.getPlayerCache(player).getAnvil().setExactMeta(true);
            }
            return true;
        })));
        registerButton(new ActionButton("priority", new ButtonState("priority", WolfyUtilities.getSkullViaURL("b8ea57c7551c6ab33b8fed354b43df523f1e357c4b4f551143c34ddeac5b6c8d"), new ButtonActionRender() {
            @Override
            public boolean run(GuiHandler guiHandler, Player player, Inventory inventory, int i, InventoryClickEvent inventoryClickEvent) {
                RecipePriority priority;
                int order;
                switch (CustomCrafting.getPlayerCache(player).getSetting()) {
                    case WORKBENCH:
                        priority = CustomCrafting.getPlayerCache(player).getWorkbench().getPriority();
                        order = priority.getOrder();
                        if (order < 2) {
                            order++;
                        } else {
                            order = -2;
                        }
                        CustomCrafting.getPlayerCache(player).getWorkbench().setPriority(RecipePriority.getByOrder(order));
                        break;
                    case ANVIL:
                        priority = CustomCrafting.getPlayerCache(player).getAnvil().getPriority();
                        order = priority.getOrder();
                        if (order < 2) {
                            order++;
                        } else {
                            order = -2;
                        }
                        CustomCrafting.getPlayerCache(player).getAnvil().setPriority(RecipePriority.getByOrder(order));
                }
                return true;
            }

            @Override
            public ItemStack render(HashMap<String, Object> hashMap, GuiHandler guiHandler, Player player, ItemStack itemStack, int slot, boolean help) {
                RecipePriority priority = null;
                switch (CustomCrafting.getPlayerCache(player).getSetting()) {
                    case WORKBENCH:
                        priority = CustomCrafting.getPlayerCache(player).getWorkbench().getPriority();
                        break;
                    case ANVIL:
                        priority = CustomCrafting.getPlayerCache(player).getAnvil().getPriority();
                }
                if (priority != null) {
                    hashMap.put("%PRI%", priority.name());
                }
                return itemStack;
            }
        })));
        /*

         */
        //WORKBENCH
        registerButton(new ToggleButton("workbench.adv_workbench", false, new ButtonState("workbench.adv_workbench_on", new ItemBuilder(Material.CRAFTING_TABLE).addUnsafeEnchantment(Enchantment.FIRE_ASPECT, 0).addItemFlags(ItemFlag.HIDE_ENCHANTS).create(), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            Workbench workbench = CustomCrafting.getPlayerCache(player).getWorkbench();
            workbench.setAdvWorkbench(false);
            return true;
        }), new ButtonState("workbench.adv_workbench_off", Material.CRAFTING_TABLE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            Workbench workbench = CustomCrafting.getPlayerCache(player).getWorkbench();
            workbench.setAdvWorkbench(true);
            return true;
        })));
        registerButton(new ToggleButton("workbench.shapeless", false, new ButtonState("workbench.shapeless_on", WolfyUtilities.getSkullViaURL("f21d93da43863cb3759afefa9f7cc5c81f34d920ca97b7283b462f8b197f813"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            Workbench workbench = CustomCrafting.getPlayerCache(player).getWorkbench();
            workbench.setShapeless(false);
            return true;
        }), new ButtonState("workbench.shapeless_off", WolfyUtilities.getSkullViaURL("1aae7e8222ddbee19d184b97e79067814b6ba3142a3bdcce8b93099a312"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            Workbench workbench = CustomCrafting.getPlayerCache(player).getWorkbench();
            workbench.setShapeless(true);
            return true;
        })));

        //FURNACE
        registerButton(new ToggleButton("furnace.adv_furnace", false, new ButtonState("furnace.adv_furnace_on", Material.GREEN_CONCRETE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            CustomCrafting.getPlayerCache(player).getCookingData().setAdvFurnace(false);
            return true;
        }), new ButtonState("furnace.adv_furnace_off", Material.RED_CONCRETE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            CustomCrafting.getPlayerCache(player).getCookingData().setAdvFurnace(true);
            return true;
        })));
        registerButton(new ChatInputButton("furnace.xp", new ButtonState("furnace.xp", Material.EXPERIENCE_BOTTLE, (hashMap, guiHandler, player, itemStack, slot, help) -> {
            hashMap.put("%XP%", CustomCrafting.getPlayerCache(player).getCookingData().getExperience());
            return itemStack;
        }), "$msg.gui.recipe_creator.furnace.xp$", (guiHandler, player, s, args) -> {
            float xp;
            try {
                xp = Float.parseFloat(args[0]);
            } catch (NumberFormatException e) {
                api.sendPlayerMessage(player, "$msg.gui.recipe_creator.valid_number$");
                return true;
            }
            CustomCrafting.getPlayerCache(player).getCookingData().setExperience(xp);
            return false;
        }));
        registerButton(new ChatInputButton("furnace.cooking_time", new ButtonState("furnace.cooking_time", Material.COAL, (hashMap, guiHandler, player, itemStack, slot, help) -> {
            hashMap.put("%TIME%", CustomCrafting.getPlayerCache(player).getCookingData().getCookingTime());
            return itemStack;
        }), "$msg.gui.recipe_creator.furnace.cooking_time$", (guiHandler, player, s, args) -> {
            int time;
            try {
                time = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                api.sendPlayerMessage(player, "$msg.gui.recipe_creator.valid_number$");
                return true;
            }
            CustomCrafting.getPlayerCache(player).getCookingData().setCookingTime(time);
            return false;
        }));


        //ANVIL
        registerButton(new ActionButton("anvil.mode", new ButtonState("anvil.mode", Material.REDSTONE, new ButtonActionRender() {
            @Override
            public boolean run(GuiHandler guiHandler, Player player, Inventory inventory, int i, InventoryClickEvent inventoryClickEvent) {
                CustomAnvilRecipe.Mode mode = CustomCrafting.getPlayerCache(player).getAnvil().getMode();
                int id = mode.getId();
                if (id < 2) {
                    id++;
                } else {
                    id = 0;
                }
                CustomCrafting.getPlayerCache(player).getAnvil().setMode(CustomAnvilRecipe.Mode.getById(id));
                return true;
            }

            @Override
            public ItemStack render(HashMap<String, Object> hashMap, GuiHandler guiHandler, Player player, ItemStack itemStack, int slot, boolean help) {
                hashMap.put("%MODE%", CustomCrafting.getPlayerCache(player).getAnvil().getMode().name());
                return itemStack;
            }
        })));

        registerButton(new ActionButton("anvil.repair_mode", new ButtonState("anvil.repair_mode", Material.GLOWSTONE_DUST, new ButtonActionRender() {
            @Override
            public boolean run(GuiHandler guiHandler, Player player, Inventory inventory, int i, InventoryClickEvent inventoryClickEvent) {
                int index = CustomAnvilRecipe.RepairCostMode.getModes().indexOf(CustomCrafting.getPlayerCache(player).getAnvil().getRepairCostMode()) + 1;
                CustomCrafting.getPlayerCache(player).getAnvil().setRepairCostMode(CustomAnvilRecipe.RepairCostMode.getModes().get(index >= CustomAnvilRecipe.RepairCostMode.getModes().size() ? 0 : index));
                return true;
            }

            @Override
            public ItemStack render(HashMap<String, Object> hashMap, GuiHandler guiHandler, Player player, ItemStack itemStack, int slot, boolean help) {
                hashMap.put("%VAR%", CustomCrafting.getPlayerCache(player).getAnvil().getRepairCostMode().name());
                return itemStack;
            }
        })));

        registerButton(new ToggleButton("anvil.repair_apply", new ButtonState("anvil.repair_apply.true", Material.GREEN_CONCRETE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            CustomCrafting.getPlayerCache(player).getAnvil().setApplyRepairCost(false);
            return true;
        }), new ButtonState("anvil.repair_apply.false", Material.RED_CONCRETE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            CustomCrafting.getPlayerCache(player).getAnvil().setApplyRepairCost(true);
            return true;
        })));

        registerButton(new ToggleButton("anvil.block_repair", false, new ButtonState("anvil.block_repair.true", Material.IRON_SWORD, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            CustomCrafting.getPlayerCache(player).getAnvil().setBlockEnchant(false);
            return true;
        }), new ButtonState("anvil.block_repair.false", Material.IRON_SWORD, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            CustomCrafting.getPlayerCache(player).getAnvil().setBlockEnchant(true);
            return true;
        })));

        registerButton(new ToggleButton("anvil.block_rename", false, new ButtonState("anvil.block_rename.true", Material.WRITABLE_BOOK, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            CustomCrafting.getPlayerCache(player).getAnvil().setBlockRename(false);
            return true;
        }), new ButtonState("anvil.block_rename.false", Material.WRITABLE_BOOK, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            CustomCrafting.getPlayerCache(player).getAnvil().setBlockRename(true);
            return true;
        })));

        registerButton(new ToggleButton("anvil.block_enchant", false, new ButtonState("anvil.block_enchant.true", Material.ENCHANTING_TABLE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            CustomCrafting.getPlayerCache(player).getAnvil().setBlockEnchant(false);
            return true;
        }), new ButtonState("anvil.block_enchant.false", Material.ENCHANTING_TABLE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            CustomCrafting.getPlayerCache(player).getAnvil().setBlockEnchant(true);
            return true;
        })));

        registerButton(new ChatInputButton("anvil.repair_cost", new ButtonState("anvil.repair_cost", Material.EXPERIENCE_BOTTLE), "$msg.gui.recipe_creator.anvil.repair_cost$", (guiHandler, player, s, args) -> {
            int repairCost;
            try {
                repairCost = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                api.sendPlayerMessage(player, "$msg.gui.recipe_creator.valid_number$");
                return true;
            }
            CustomCrafting.getPlayerCache(player).getAnvil().setRepairCost(repairCost);
            return false;
        }));

        registerButton(new ChatInputButton("anvil.durability", new ButtonState("anvil.durability", Material.IRON_SWORD), "$msg.gui.recipe_creator.anvil.durability$", (guiHandler, player, s, args) -> {
            int durability;
            try {
                durability = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                api.sendPlayerMessage(player, "$msg.gui.recipe_creator.valid_number$");
                return true;
            }
            CustomCrafting.getPlayerCache(player).getAnvil().setDurability(durability);
            return false;
        }));
    }

    @EventHandler
    public void onUpdate(GuiUpdateEvent event) {
        if (event.verify(this)) {
            PlayerCache cache = CustomCrafting.getPlayerCache(event.getPlayer());
            Workbench workbench = cache.getWorkbench();
            event.setButton(0, "back");
            switch (cache.getSetting()) {
                case WORKBENCH:
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
                    event.setButton(3, "permission");
                    event.setButton(5, "workbench.adv_workbench");
                    event.setButton(44, "save");
                    break;
                case ANVIL:
                    Anvil anvil = cache.getAnvil();
                    event.setButton(2, "permission");
                    event.setButton(4, "priority");
                    event.setButton(6, "exact_meta");
                    event.setButton(19, "anvil.container_0");
                    event.setButton(21, "anvil.container_1");
                    if (anvil.getMode().equals(CustomAnvilRecipe.Mode.RESULT)) {
                        event.setButton(25, "anvil.container_2");
                    } else if (anvil.getMode().equals(CustomAnvilRecipe.Mode.DURABILITY)) {
                        event.setButton(25, "anvil.durability");
                    } else {
                        event.setItem(25, new ItemStack(Material.BARRIER));
                    }
                    event.setButton(23, "anvil.mode");
                    event.setButton(36, "anvil.block_enchant");
                    event.setButton(37, "anvil.block_rename");
                    event.setButton(38, "anvil.block_repair");
                    event.setButton(40, "anvil.repair_apply");
                    event.setButton(41, "anvil.repair_cost");
                    event.setButton(42, "anvil.repair_mode");
                    event.setButton(44, "save");
                    break;
                case STONECUTTER:
                    event.setButton(20, "stonecutter.container_0");
                    event.setButton(24, "stonecutter.container_1");
                    event.setButton(44, "save");
                    break;
                case BLAST_FURNACE:
                case SMOKER:
                case CAMPFIRE:
                case FURNACE:
                    event.setButton(20, "none", "glass_white");
                    //TODO: INPUT
                    event.setButton(11, "cooking.container_0");
                    event.setButton(24, "cooking.container_1");
                    event.setButton(37, "none", "glass_white");
                    event.setButton(39, "none", "glass_white");
                    event.setButton(22, "furnace.xp");
                    event.setButton(29, "furnace.cooking_time");
                    event.setButton(44, "save");
                    break;
            }
        }
    }

    private boolean validToSave(PlayerCache cache) {
        switch (cache.getSetting()) {
            case WORKBENCH:
                Workbench workbench = cache.getWorkbench();
                if (workbench.getIngredients() != null && !workbench.getResult().isEmpty())
                    return true;
                break;
            case ANVIL:
                Anvil anvil = cache.getAnvil();
                if (!anvil.getIngredients(0).isEmpty() || !anvil.getIngredients(1).isEmpty()) {
                    if (anvil.getMode().equals(CustomAnvilRecipe.Mode.RESULT)) {
                        return anvil.getResult() != null && !anvil.getResult().isEmpty();
                    } else {
                        return true;
                    }
                } else {
                    return false;
                }
            case STONECUTTER:
                Stonecutter stonecutter = cache.getStonecutter();
                if (!stonecutter.getResult().getType().equals(Material.AIR) && !stonecutter.getSource().isEmpty())
                    return true;
                break;
            case BLAST_FURNACE:
            case SMOKER:
            case CAMPFIRE:
            case FURNACE:
                CookingData furnace = cache.getCookingData();
                if (furnace.getSource() != null && !furnace.getSource().isEmpty() && furnace.getResult() != null && !furnace.getResult().isEmpty())
                    return true;
        }
        return false;
    }
}
