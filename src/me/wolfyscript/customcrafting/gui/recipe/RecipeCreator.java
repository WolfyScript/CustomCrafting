package me.wolfyscript.customcrafting.gui.recipe;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.custom_configs.CookingConfig;
import me.wolfyscript.customcrafting.configs.custom_configs.blast_furnace.BlastingConfig;
import me.wolfyscript.customcrafting.configs.custom_configs.campfire.CampfireConfig;
import me.wolfyscript.customcrafting.configs.custom_configs.smoker.SmokerConfig;
import me.wolfyscript.customcrafting.configs.custom_configs.stonecutter.StonecutterConfig;
import me.wolfyscript.customcrafting.configs.custom_configs.workbench.CraftConfig;
import me.wolfyscript.customcrafting.configs.custom_configs.furnace.FurnaceConfig;
import me.wolfyscript.customcrafting.data.PlayerCache;
import me.wolfyscript.customcrafting.data.cache.*;
import me.wolfyscript.customcrafting.gui.ExtendedGuiWindow;
import me.wolfyscript.customcrafting.items.CustomItem;
import me.wolfyscript.customcrafting.recipes.anvil.CustomAnvilRecipe;
import me.wolfyscript.customcrafting.utils.ItemUtils;
import me.wolfyscript.customcrafting.recipes.*;
import me.wolfyscript.customcrafting.recipes.blast_furnace.CustomBlastRecipe;
import me.wolfyscript.customcrafting.recipes.campfire.CustomCampfireRecipe;
import me.wolfyscript.customcrafting.recipes.furnace.CustomFurnaceRecipe;
import me.wolfyscript.customcrafting.recipes.smoker.CustomSmokerRecipe;
import me.wolfyscript.customcrafting.recipes.stonecutter.CustomStonecutterRecipe;
import me.wolfyscript.customcrafting.recipes.workbench.CraftingRecipe;
import me.wolfyscript.customcrafting.recipes.workbench.ShapedCraftRecipe;
import me.wolfyscript.customcrafting.recipes.workbench.ShapelessCraftRecipe;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RecipeCreator extends ExtendedGuiWindow {


    public RecipeCreator(InventoryAPI inventoryAPI) {
        super("recipe_creator", inventoryAPI, 45);
    }

    @Override
    public void onInit() {
        createItem("save", Material.WRITABLE_BOOK);

        createItem("permissions_on", Material.GREEN_CONCRETE);
        createItem("permissions_off", Material.RED_CONCRETE);

        createItem("exact_meta_on", Material.GREEN_CONCRETE);
        createItem("exact_meta_off", Material.RED_CONCRETE);
        createItem("priority", WolfyUtilities.getSkullViaURL("b8ea57c7551c6ab33b8fed354b43df523f1e357c4b4f551143c34ddeac5b6c8d"));

        ItemStack advancedWorkbench = new ItemStack(Material.CRAFTING_TABLE);
        advancedWorkbench.addUnsafeEnchantment(Enchantment.FIRE_ASPECT, 1);
        ItemMeta advWorkMeta = advancedWorkbench.getItemMeta();
        advWorkMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        advancedWorkbench.setItemMeta(advWorkMeta);

        createItem("workbench.adv_workbench_on", advancedWorkbench);
        createItem("workbench.adv_workbench_off", Material.CRAFTING_TABLE);
        createItem("workbench.shapeless_off", WolfyUtilities.getSkullByValue("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWFhZTdlODIyMmRkYmVlMTlkMTg0Yjk3ZTc5MDY3ODE0YjZiYTMxNDJhM2JkY2NlOGI5MzA5OWEzMTIifX19"));
        createItem("workbench.shapeless_on", WolfyUtilities.getSkullByValue("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjIxZDkzZGE0Mzg2M2NiMzc1OWFmZWZhOWY3Y2M1YzgxZjM0ZDkyMGNhOTdiNzI4M2I0NjJmOGIxOTdmODEzIn19fQ=="));

        createItem("furnace.adv_furnace_on", Material.GREEN_CONCRETE);
        createItem("furnace.adv_furnace_off", Material.RED_CONCRETE);
        createItem("furnace.xp", Material.EXPERIENCE_BOTTLE);
        createItem("furnace.cooking_time", Material.COAL);

        createItem("anvil.mode", Material.REDSTONE);
        createItem("anvil.repair_cost", Material.EXPERIENCE_BOTTLE);
        createItem("anvil.block_repair.true", Material.IRON_SWORD);
        createItem("anvil.block_repair.false", Material.IRON_SWORD);
        createItem("anvil.block_rename.true", Material.WRITABLE_BOOK);
        createItem("anvil.block_rename.false", Material.WRITABLE_BOOK);
        createItem("anvil.block_enchant.true", Material.ENCHANTING_TABLE);
        createItem("anvil.block_enchant.false", Material.ENCHANTING_TABLE);
        createItem("anvil.durability", Material.IRON_SWORD);
    }

    @EventHandler
    public void onUpdate(GuiUpdateEvent event) {
        if (event.verify(this)) {
            PlayerCache cache = CustomCrafting.getPlayerCache(event.getPlayer());

            Workbench workbench = cache.getWorkbench();

            switch (cache.getSetting()) {
                case CRAFT_RECIPE:
                    event.setItem(35, event.getItem("priority", "%PRI%", workbench.getPriority().name()));
                    if (!workbench.getIngredients().isEmpty()) {
                        int slot;
                        for (int i = 0; i < 9; i++) {
                            slot = 10 + i + (i / 3) * 6;
                            event.setItem(slot, workbench.getIngredients().isEmpty() ? new ItemStack(Material.AIR) : workbench.getIngredient(i) != null ? workbench.getIngredient(i).getIDItem() : new ItemStack(Material.AIR));
                        }
                    }
                    event.setItem(26, workbench.isExactMeta() ? "exact_meta_on" : "exact_meta_off");
                    event.setItem(22, workbench.isShapeless() ? "workbench.shapeless_on" : "workbench.shapeless_off");

                    event.setItem(24, workbench.getResult().getIDItem(workbench.getResultCustomAmount()));
                    event.setItem(3, workbench.isPermissions() ? "permissions_on" : "permissions_off");
                    if (workbench.isAdvWorkbench()) {
                        event.setItem(5, "workbench.adv_workbench_on");
                    } else {
                        event.setItem(5, "workbench.adv_workbench_off");
                    }
                    break;
                case ANVIL:
                    Anvil anvil = cache.getAnvil();
                    event.setItem(6, anvil.isExactMeta() ? "exact_meta_on" : "exact_meta_off");
                    event.setItem(4, event.getItem("priority", "%PRI%", anvil.getPriority().name()));
                    event.setItem(2, anvil.isPermissions() ? "permissions_on" : "permissions_off");

                    event.setItem(19, anvil.getInputLeft().isEmpty() ? new ItemStack(Material.AIR) : anvil.getInputLeft().keySet().toArray(new CustomItem[0])[0]);
                    event.setItem(21, anvil.getInputRight().isEmpty() ? new ItemStack(Material.AIR) : anvil.getInputRight().keySet().toArray(new CustomItem[0])[0]);

                    if (anvil.getMode().equals(CustomAnvilRecipe.Mode.RESULT)) {
                        event.setItem(25, anvil.getResult());
                    } else if (anvil.getMode().equals(CustomAnvilRecipe.Mode.DURABILITY)) {
                        event.setItem(25, event.getItem("anvil.durability", "%VAR%", String.valueOf(anvil.getDurability())));
                    } else {
                        event.setItem(25, new ItemStack(Material.BARRIER));
                    }

                    event.setItem(23, event.getItem("anvil.repair_cost", "%VAR%", String.valueOf(anvil.getRepairCost())));

                    event.setItem(37, anvil.isBlockEnchant() ? "anvil.block_enchant.true" : "anvil.block_enchant.false");
                    event.setItem(38, anvil.isBlockRename() ? "anvil.block_rename.true" : "anvil.block_rename.false");
                    event.setItem(39, anvil.isBlockRepair() ? "anvil.block_repair.true" : "anvil.block_repair.false");

                    event.setItem(41, event.getItem("anvil.mode", "%MODE%", anvil.getMode().name()));
                    break;
                case STONECUTTER:
                    Stonecutter stonecutter = cache.getStonecutter();
                    event.setItem(20, stonecutter.getSource().getIDItem());
                    event.setItem(24, stonecutter.getResult().getIDItem());
                    break;
                case BLAST_FURNACE:
                case SMOKER:
                case CAMPFIRE:
                case FURNACE_RECIPE:
                    CookingData cooking = cache.getCookingData();
                    event.setItem(20, "none", "glass_white");
                    event.setItem(11, cooking.getSource().getIDItem());

                    event.setItem(24, cooking.getResult().getIDItem());

                    event.setItem(37, "none", "glass_white");
                    event.setItem(39, "none", "glass_white");

                    ItemStack xp = event.getItem("furnace.xp");
                    ItemMeta xpMeta = xp.getItemMeta();
                    if (xpMeta.hasLore()) {
                        List<String> lore = xpMeta.getLore();
                        for (int i = 0; i < lore.size(); i++) {
                            lore.set(i, lore.get(i).replace("%XP%", String.valueOf(cooking.getExperience())));
                        }
                        xpMeta.setLore(lore);
                    }
                    xp.setItemMeta(xpMeta);
                    event.setItem(22, xp);

                    ItemStack time = event.getItem("furnace.cooking_time");
                    ItemMeta timeMeta = time.getItemMeta();
                    if (timeMeta.hasLore()) {
                        List<String> lore = timeMeta.getLore();
                        for (int i = 0; i < lore.size(); i++) {
                            lore.set(i, lore.get(i).replace("%TIME%", String.valueOf(cooking.getCookingTime())));
                        }
                        timeMeta.setLore(lore);
                    }
                    time.setItemMeta(timeMeta);
                    event.setItem(29, time);
                    break;
            }
            event.setItem(44, "save");
        }
    }

    @Override
    public boolean onAction(GuiAction guiAction) {
        if (!super.onAction(guiAction)) {
            String action = guiAction.getAction();
            PlayerCache cache = CustomCrafting.getPlayerCache(guiAction.getPlayer());
            updateInv(guiAction.getPlayer().getOpenInventory().getTopInventory(), cache);
            switch (cache.getSetting()) {
                case CRAFT_RECIPE:
                    Workbench workbench = cache.getWorkbench();
                    if (action.startsWith("workbench.shapeless_")) {
                        api.sendDebugMessage("  Shapeless: " + !workbench.isShapeless());
                        workbench.setShapeless(!workbench.isShapeless());
                    } else if (action.startsWith("permissions_")) {
                        api.sendDebugMessage("  Permission: " + !workbench.isPermissions());
                        workbench.setPermissions(!workbench.isPermissions());
                    } else if (action.startsWith("workbench.adv_workbench_")) {
                        api.sendDebugMessage("  Adv Workbench: " + !workbench.isAdvWorkbench());
                        workbench.setAdvWorkbench(!workbench.isAdvWorkbench());
                    } else if (action.equals("priority")) {
                        RecipePriority recipePriority = workbench.getPriority();
                        int order = recipePriority.getOrder();
                        if (order < 2) {
                            order++;
                        } else {
                            order = -2;
                        }
                        workbench.setPriority(RecipePriority.getByOrder(order));
                    } else if (action.startsWith("exact_meta_")) {
                        workbench.setExactMeta(!workbench.isExactMeta());
                    }
                    break;
                case ANVIL:
                    Anvil anvil = cache.getAnvil();
                    if (action.startsWith("anvil.")) {
                        action = action.substring("anvil.".length());
                        switch (action.split("\\.")[0]) {
                            case "block_enchant":
                                anvil.setBlockEnchant(!anvil.isBlockEnchant());
                                break;
                            case "block_repair":
                                anvil.setBlockRepair(!anvil.isBlockRepair());
                                break;
                            case "block_rename":
                                anvil.setBlockRename(!anvil.isBlockRename());
                                break;
                            case "durability":
                                runChat(40, "", guiAction.getGuiHandler());
                                break;
                            case "repair_cost":
                                runChat(30, "", guiAction.getGuiHandler());
                                break;
                            case "mode":
                                CustomAnvilRecipe.Mode mode = anvil.getMode();
                                int id = mode.getId();
                                if (id < 2) {
                                    id++;
                                } else {
                                    id = 0;
                                }
                                anvil.setMode(CustomAnvilRecipe.Mode.getById(id));
                        }
                    } else if (action.equals("priority")) {
                        RecipePriority recipePriority = anvil.getPriority();
                        int order = recipePriority.getOrder();
                        if (order < 2) {
                            order++;
                        } else {
                            order = -2;
                        }
                        anvil.setPriority(RecipePriority.getByOrder(order));
                    }
                    break;
                case BLAST_FURNACE:
                case SMOKER:
                case CAMPFIRE:
                case FURNACE_RECIPE:
                    CookingData furnace = cache.getCookingData();
                    if (action.startsWith("furnace.adv_furnace_")) {
                        furnace.setAdvFurnace(!furnace.isAdvFurnace());
                    } else if (action.equals("furnace.xp")) {
                        runChat(1, "$msg.gui.recipe_creator.furnace.xp$", guiAction.getGuiHandler());
                    } else if (action.equals("furnace.cooking_time")) {
                        runChat(11, "$msg.gui.recipe_creator.furnace.cooking_time$", guiAction.getGuiHandler());
                    }
                    break;
            }
            if (action.equals("back")) {
                guiAction.getGuiHandler().openLastInv();
            } else if (action.equals("save")) {
                if (validToSave(cache)) {
                    runChat(0, "$msg.gui.recipe_creator.save.input$", guiAction.getGuiHandler());
                } else {
                    api.sendPlayerMessage(guiAction.getPlayer(), "$msg.gui.recipe_creator.save.empty$");
                }
            }
            update(guiAction.getGuiHandler());
        }
        return true;
    }

    private boolean validToSave(PlayerCache cache) {
        switch (cache.getSetting()) {
            case CRAFT_RECIPE:
                Workbench workbench = cache.getWorkbench();
                if (!workbench.getIngredients().isEmpty() && !workbench.getResult().getType().equals(Material.AIR))
                    return true;
                break;
            case STONECUTTER:
                Stonecutter stonecutter = cache.getStonecutter();
                if (!stonecutter.getResult().getType().equals(Material.AIR) && !stonecutter.getSource().getType().equals(Material.AIR))
                    return true;
                break;
            case BLAST_FURNACE:
            case SMOKER:
            case CAMPFIRE:
            case FURNACE_RECIPE:
                CookingData furnace = cache.getCookingData();
                if (!furnace.getResult().getType().equals(Material.AIR) && !furnace.getSource().getType().equals(Material.AIR))
                    return true;
        }
        return false;
    }

    @Override
    public boolean onClick(GuiClick guiClick) {
        int slot = guiClick.getClickedSlot();
        Player player = guiClick.getPlayer();
        PlayerCache playerCache = CustomCrafting.getPlayerCache(player);
        Items items = playerCache.getItems();
        updateInv(player.getOpenInventory().getTopInventory(), playerCache);

        if (guiClick.getClickedInventory().equals(player.getOpenInventory().getTopInventory())) {
            if (guiClick.getClickType().equals(ClickType.SHIFT_RIGHT) && !guiClick.getCurrentItem().getType().equals(Material.AIR)) {
                CustomItem customItem = CustomItem.getCustomItem(guiClick.getCurrentItem());
                String id = customItem.getId();
                switch (playerCache.getSetting()) {
                    case CRAFT_RECIPE:
                        if ((slot >= 10 && slot < 13) || (slot >= 19 && slot < 22) || (slot >= 28 && slot < 31)) {
                            items.setIngredient((slot - 10) - 6 * (((slot - 10) / 4) / 2), customItem);
                            guiClick.getGuiHandler().changeToInv("item_editor");
                            return true;
                        } else {
                            items.setResult(customItem);
                            guiClick.getGuiHandler().changeToInv("item_editor");
                            return true;
                        }
                    case STONECUTTER:
                        if (slot == 20) {
                            items.setSource(customItem);
                        } else {
                            items.setResult(customItem);
                        }
                        guiClick.getGuiHandler().changeToInv("item_editor");
                        return true;
                    case BLAST_FURNACE:
                    case SMOKER:
                    case CAMPFIRE:
                    case FURNACE_RECIPE:
                        if (slot == 11) {
                            items.setSource(customItem);
                        } else {
                            items.setResult(customItem);
                        }
                        guiClick.getGuiHandler().changeToInv("item_editor");
                        return true;
                }
            }
        }
        return false;
    }

    private void updateInv(Inventory inv, PlayerCache cache) {
        switch (cache.getSetting()) {
            case CRAFT_RECIPE:
                Workbench workbench = cache.getWorkbench();
                List<ItemStack> ingredients = new ArrayList<>();
                int craftSlot;
                for (int i = 0; i < 9; i++) {
                    craftSlot = 10 + i + (i / 3) * 6;
                    ItemStack itemStack = inv.getItem(craftSlot);
                    ingredients.add(itemStack == null ? new ItemStack(Material.AIR) : itemStack);
                }
                workbench.setIngredients(ingredients);
                if (inv.getItem(24) != null) {
                    CustomItem customItem = CustomItem.getCustomItem(inv.getItem(24));
                    workbench.setResult(customItem);
                    workbench.setResultCustomAmount(customItem.getAmount());
                } else {
                    workbench.setResult(new CustomItem(Material.AIR));
                }
                break;
            case STONECUTTER:
                Stonecutter stonecutter = cache.getStonecutter();
                if (inv.getItem(24) != null) {
                    stonecutter.setResult(CustomItem.getCustomItem(inv.getItem(24)));
                } else {
                    stonecutter.setResult(new CustomItem(Material.AIR));
                }
                if (inv.getItem(20) != null) {
                    stonecutter.setSource(CustomItem.getCustomItem(inv.getItem(20)));
                } else {
                    stonecutter.setSource(new CustomItem(Material.AIR));
                }
                break;
            case BLAST_FURNACE:
            case SMOKER:
            case CAMPFIRE:
            case FURNACE_RECIPE:
                CookingData cooking = cache.getCookingData();
                if (inv.getItem(24) != null) {
                    cooking.setResult(CustomItem.getCustomItem(inv.getItem(24)));
                } else {
                    cooking.setResult(new CustomItem(Material.AIR));
                }
                if (inv.getItem(11) != null) {
                    cooking.setSource(CustomItem.getCustomItem(inv.getItem(11)));
                } else {
                    cooking.setSource(new CustomItem(Material.AIR));
                }
                break;
        }
    }

    @Override
    public boolean parseChatMessage(int id, String message, GuiHandler guiHandler) {
        Player player = guiHandler.getPlayer();
        PlayerCache cache = CustomCrafting.getPlayerCache(player);
        String[] args = message.split(" ");
        Workbench workbench = cache.getWorkbench();
        CookingData furnace = cache.getCookingData();
        Anvil anvil = cache.getAnvil();
        if (args.length > 0) {
            if (id == 0) {
                if (args.length > 1) {
                    CookingConfig cookingConfig = null;
                    switch (cache.getSetting()) {
                        case CRAFT_RECIPE:
                            CraftConfig config = new CraftConfig(api.getConfigAPI(), args[0].toLowerCase(), args[1].toLowerCase());
                            api.sendDebugMessage("Create Config:");
                            api.sendDebugMessage("  id: " + args[0].toLowerCase() + ":" + args[1].toLowerCase());
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
                            HashMap<Character, ArrayList<CustomItem>> ingredients = workbench.getIngredients();
                            api.sendDebugMessage("  Ingredients: " + ingredients);
                            String[] shape = new String[3];
                            int index = 0;
                            int row = 0;
                            for (char key : ingredients.keySet()) {
                                List<CustomItem> keyItems = ingredients.get(key);
                                if (ItemUtils.isEmpty(keyItems)) {
                                    if (shape[row] != null) {
                                        shape[row] = shape[row] + " ";
                                    } else {
                                        shape[row] = " ";
                                    }
                                } else {
                                    if (shape[row] != null) {
                                        shape[row] = shape[row] + key;
                                    } else {
                                        shape[row] = String.valueOf(key);
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
                            config.save();
                            api.sendDebugMessage("Reset GUI cache...");
                            cache.resetWorkbench();
                            api.sendPlayerMessage(player, "$msg.gui.recipe_creator.save.success$");
                            api.sendPlayerMessage(player, "§6recipes/" + args[0] + "/workbench/" + args[1]);
                            try {
                                api.sendDebugMessage("Loading Recipe...");
                                CraftingRecipe customRecipe;
                                if (config.isShapeless()) {
                                    customRecipe = new ShapelessCraftRecipe(config);
                                } else {
                                    customRecipe = new ShapedCraftRecipe(config);
                                }
                                customRecipe.load();
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

                            break;
                        case STONECUTTER:
                            Stonecutter stonecutter = cache.getStonecutter();
                            StonecutterConfig stonecutterConfig = new StonecutterConfig(api.getConfigAPI(), args[0].toLowerCase(), args[1].toLowerCase());
                            stonecutterConfig.setResult(stonecutter.getResult());
                            stonecutterConfig.setSource(stonecutter.getSource());
                            stonecutterConfig.setExactMeta(stonecutter.isExactMeta());
                            stonecutterConfig.setPriority(stonecutter.getPriority());
                            stonecutterConfig.save();
                            cache.resetStonecutter();
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
                            cookingConfig = new BlastingConfig(api.getConfigAPI(), args[0].toLowerCase(), args[1].toLowerCase());
                        case SMOKER:
                            if (cookingConfig == null) {
                                cookingConfig = new SmokerConfig(api.getConfigAPI(), args[0].toLowerCase(), args[1].toLowerCase());
                            }
                        case CAMPFIRE:
                            if (cookingConfig == null) {
                                cookingConfig = new CampfireConfig(api.getConfigAPI(), args[0].toLowerCase(), args[1].toLowerCase());
                            }
                        case FURNACE_RECIPE:
                            if (cookingConfig == null) {
                                cookingConfig = new FurnaceConfig(api.getConfigAPI(), args[0].toLowerCase(), args[1].toLowerCase());
                            }
                            //furnaceConfig.setAdvancedFurnace(furnace.isAdvFurnace());
                            cookingConfig.setCookingTime(furnace.getCookingTime());
                            cookingConfig.setXP(furnace.getExperience());
                            cookingConfig.setResult(furnace.getResult());
                            cookingConfig.setSource(furnace.getSource());
                            cookingConfig.setExactMeta(furnace.isExactMeta());
                            cookingConfig.save();
                            cookingConfig.load();
                            cache.resetCookingData();
                            api.sendPlayerMessage(player, "$msg.gui.recipe_creator.save.success$");
                            api.sendPlayerMessage(player, "§6recipes/" + args[0] + "/furnace/" + args[1]);
                            try {
                                CustomRecipe customRecipe = null;
                                switch (cache.getSetting()) {
                                    case SMOKER:
                                        customRecipe = new CustomSmokerRecipe((SmokerConfig) cookingConfig);
                                        break;
                                    case CAMPFIRE:
                                        customRecipe = new CustomCampfireRecipe((CampfireConfig) cookingConfig);
                                        break;
                                    case FURNACE_RECIPE:
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
            } else {
                switch (id) {
                    case 1:
                        float xp;
                        try {
                            xp = Float.parseFloat(args[0]);
                        } catch (NumberFormatException e) {
                            api.sendPlayerMessage(player, "$msg.gui.recipe_creator.valid_number$");
                            return true;
                        }
                        furnace.setExperience(xp);
                        break;
                    case 11:
                        int time;
                        try {
                            time = Integer.parseInt(args[0]);
                        } catch (NumberFormatException e) {
                            api.sendPlayerMessage(player, "$msg.gui.recipe_creator.valid_number$");
                            return true;
                        }
                        furnace.setCookingTime(time);
                        break;
                    case 2:
                        if (args.length > 1) {
                            workbench.setOverride(args[0] + ":" + args[1]);
                        } else {
                            workbench.setOverride("");
                            return true;
                        }
                        break;
                    case 30:
                        int repairCost;
                        try {
                            repairCost = Integer.parseInt(args[0]);
                        } catch (NumberFormatException e) {
                            api.sendPlayerMessage(player, "$msg.gui.recipe_creator.valid_number$");
                            return true;
                        }
                        anvil.setRepairCost(repairCost);
                        break;
                    case 40:
                        int durability;
                        try {
                            durability = Integer.parseInt(args[0]);
                        } catch (NumberFormatException e) {
                            api.sendPlayerMessage(player, "$msg.gui.recipe_creator.valid_number$");
                            return true;
                        }
                        anvil.setDurability(durability);
                        break;
                }
            }
        }
        return false;
    }
}
