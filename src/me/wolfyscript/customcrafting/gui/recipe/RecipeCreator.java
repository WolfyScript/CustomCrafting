package me.wolfyscript.customcrafting.gui.recipe;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.custom_configs.CookingConfig;
import me.wolfyscript.customcrafting.configs.custom_configs.blast_furnace.BlastingConfig;
import me.wolfyscript.customcrafting.configs.custom_configs.campfire.CampfireConfig;
import me.wolfyscript.customcrafting.configs.custom_configs.smoker.SmokerConfig;
import me.wolfyscript.customcrafting.configs.custom_configs.workbench.CraftConfig;
import me.wolfyscript.customcrafting.configs.custom_configs.furnace.FurnaceConfig;
import me.wolfyscript.customcrafting.data.PlayerCache;
import me.wolfyscript.customcrafting.data.cache.CookingData;
import me.wolfyscript.customcrafting.data.cache.Items;
import me.wolfyscript.customcrafting.data.cache.Workbench;
import me.wolfyscript.customcrafting.gui.ExtendedGuiWindow;
import me.wolfyscript.customcrafting.items.CustomItem;
import me.wolfyscript.customcrafting.items.ItemUtils;
import me.wolfyscript.customcrafting.recipes.*;
import me.wolfyscript.customcrafting.recipes.blast_furnace.CustomBlastRecipe;
import me.wolfyscript.customcrafting.recipes.campfire.CustomCampfireRecipe;
import me.wolfyscript.customcrafting.recipes.furnace.CustomFurnaceRecipe;
import me.wolfyscript.customcrafting.recipes.smoker.CustomSmokerRecipe;
import me.wolfyscript.customcrafting.recipes.workbench.CraftingRecipe;
import me.wolfyscript.customcrafting.recipes.workbench.ShapedCraftRecipe;
import me.wolfyscript.customcrafting.recipes.workbench.ShapelessCraftRecipe;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RecipeCreator extends ExtendedGuiWindow {

    public RecipeCreator(InventoryAPI inventoryAPI) {
        super("recipe_creator", inventoryAPI, 54);
    }

    @Override
    public void onInit() {
        createItem("save", Material.WRITABLE_BOOK);

        createItem("permissions_on", Material.GREEN_CONCRETE);
        createItem("permissions_off", Material.RED_CONCRETE);

        createItem("priority", WolfyUtilities.getSkullViaURL("b8ea57c7551c6ab33b8fed354b43df523f1e357c4b4f551143c34ddeac5b6c8d"));

        createItem("workbench.adv_workbench_on", Material.GREEN_CONCRETE);
        createItem("workbench.adv_workbench_off", Material.RED_CONCRETE);
        createItem("workbench.shapeless_off", WolfyUtilities.getSkullByValue("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWFhZTdlODIyMmRkYmVlMTlkMTg0Yjk3ZTc5MDY3ODE0YjZiYTMxNDJhM2JkY2NlOGI5MzA5OWEzMTIifX19"));
        createItem("workbench.shapeless_on", WolfyUtilities.getSkullByValue("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjIxZDkzZGE0Mzg2M2NiMzc1OWFmZWZhOWY3Y2M1YzgxZjM0ZDkyMGNhOTdiNzI4M2I0NjJmOGIxOTdmODEzIn19fQ=="));

        createItem("furnace.adv_furnace_on", Material.GREEN_CONCRETE);
        createItem("furnace.adv_furnace_off", Material.RED_CONCRETE);
        createItem("furnace.xp", Material.EXPERIENCE_BOTTLE);
        createItem("furnace.cooking_time", Material.COAL);

    }

    @EventHandler
    public void onUpdate(GuiUpdateEvent event) {
        if (event.verify(this)) {
            PlayerCache cache = CustomCrafting.getPlayerCache(event.getPlayer());

            Workbench workbench = cache.getWorkbench();

            event.setItem(35, event.getItem("priority", "%PRI%", workbench.getPriority().name()));

            switch (cache.getSetting()) {
                case CRAFT_RECIPE:
                    if (!workbench.getIngredients().isEmpty()) {
                        int slot;
                        for (int i = 0; i < 9; i++) {
                            slot = 19 + i + (i / 3) * 6;
                            event.setItem(slot, workbench.getIngredients().isEmpty() ? new ItemStack(Material.AIR) : workbench.getIngredient(i) != null ? workbench.getIngredient(i).getIDItem() : new ItemStack(Material.AIR));
                        }
                    }
                    event.setItem(31, workbench.isShapeless() ? "workbench.shapeless_on" : "workbench.shapeless_off");
                    event.setItem(33, workbench.getResult().getIDItem());
                    event.setItem(3, workbench.isPermissions() ? "permissions_on" : "permissions_off");
                    event.setItem(5, workbench.isAdvWorkbench() ? "workbench.adv_workbench_on" : "workbench.adv_workbench_off");
                    break;
                case BLAST_FURNACE:
                case SMOKER:
                case CAMPFIRE:
                case FURNACE_RECIPE:
                    CookingData cooking = cache.getCookingData();
                    event.setItem(11, "none", "glass_white");
                    event.setItem(19, "none", "glass_white");
                    event.setItem(20, cooking.getSource().getIDItem());
                    event.setItem(21, "none", "glass_white");
                    event.setItem(29, "none", "glass_white");

                    event.setItem(24, "none", "glass_white");
                    event.setItem(32, "none", "glass_white");
                    event.setItem(33, cooking.getResult().getIDItem());
                    event.setItem(34, "none", "glass_white");
                    event.setItem(42, "none", "glass_white");

                    event.setItem(37, "none", "glass_white");
                    event.setItem(39, "none", "glass_white");
                    event.setItem(47, "none", "glass_white");

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
                    event.setItem(31, xp);

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
                    event.setItem(38, time);
                    break;
            }
            event.setItem(53, "save");
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
                        workbench.setShapeless(!workbench.isShapeless());
                    } else if (action.startsWith("permissions_")) {
                        workbench.setPermissions(!workbench.isPermissions());
                    } else if (action.startsWith("workbench.adv_workbench_")) {
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
                CustomItem customItem = ItemUtils.getCustomItem(guiClick.getCurrentItem());
                String id = customItem.getId();
                switch (playerCache.getSetting()) {
                    case CRAFT_RECIPE:
                        if ((slot >= 19 && slot < 22) || (slot >= 28 && slot < 31) || (slot >= 37 && slot < 40)) {
                            items.setIngredient((slot - 19) - 6 * (((slot - 19) / 4) / 2), customItem);
                            guiClick.getGuiHandler().changeToInv("item_editor");
                            return true;
                        } else {
                            items.setResult(customItem);
                            guiClick.getGuiHandler().changeToInv("item_editor");
                            return true;
                        }
                    case BLAST_FURNACE:
                    case SMOKER:
                    case CAMPFIRE:
                    case FURNACE_RECIPE:
                        if (slot == 20) {
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
                    craftSlot = 19 + i + (i / 3) * 6;
                    ItemStack itemStack = inv.getItem(craftSlot);
                    ingredients.add(itemStack == null ? new ItemStack(Material.AIR) : itemStack);
                }
                workbench.setIngredients(ingredients);
                if (inv.getItem(33) != null) {
                    workbench.setResult(ItemUtils.getCustomItem(inv.getItem(33)));
                } else {
                    workbench.setResult(new CustomItem(Material.AIR));
                }
                break;
            case BLAST_FURNACE:
            case SMOKER:
            case CAMPFIRE:
            case FURNACE_RECIPE:
                CookingData cooking = cache.getCookingData();
                if (inv.getItem(33) != null) {
                    cooking.setResult(ItemUtils.getCustomItem(inv.getItem(33)));
                } else {
                    cooking.setResult(new CustomItem(Material.AIR));
                }
                if (inv.getItem(20) != null) {
                    cooking.setSource(ItemUtils.getCustomItem(inv.getItem(20)));
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
        if (args.length > 0) {
            if (id == 0) {
                if (args.length > 1) {
                    CookingConfig cookingConfig = null;
                    switch (cache.getSetting()) {
                        case CRAFT_RECIPE:
                            final CraftConfig config = new CraftConfig(api.getConfigAPI(), args[0].toLowerCase(), args[1].toLowerCase());
                            config.setPermission(workbench.isPermissions());
                            config.setNeedWorkbench(workbench.isAdvWorkbench());
                            config.setShapeless(workbench.isShapeless());
                            config.setResult(workbench.getResult());
                            HashMap<Character, ArrayList<CustomItem>> ingredients = workbench.getIngredients();
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
                            config.setShape(shape);
                            config.setIngredients(workbench.getIngredients());
                            config.setPriority(workbench.getPriority());
                            config.save();
                            cache.resetWorkbench();
                            api.sendPlayerMessage(player, "$msg.gui.recipe_creator.save.success$");
                            api.sendPlayerMessage(player, "§6recipes/" + args[0] + "/workbench/" + args[1]);
                            try {
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
                            cookingConfig.save();
                            cookingConfig.load();
                            cache.resetFurnace();
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
                                if(customRecipe != null){
                                    CustomRecipe finalCustomRecipe = customRecipe;
                                    Bukkit.getScheduler().runTaskLater(CustomCrafting.getInst(), () -> CustomCrafting.getRecipeHandler().injectRecipe(finalCustomRecipe), 1);
                                }else{
                                    api.sendPlayerMessage(player, "$msg.gui.recipe_creator.error_loading$", new String[]{"%REC%", cookingConfig.getId()});
                                }
                            } catch (Exception ex) {
                                api.sendPlayerMessage(player, "$msg.gui.recipe_creator.error_loading$", new String[]{"%REC%", cookingConfig.getId()});
                                ex.printStackTrace();
                                return false;
                            }
                            return false;
                    }
                }
            } else if (id == 1) {
                float xp;
                try {
                    xp = Float.parseFloat(args[0]);
                } catch (NumberFormatException e) {
                    api.sendPlayerMessage(player, "$msg.gui.recipe_creator.valid_number$");
                    return true;
                }
                furnace.setExperience(xp);
            } else if (id == 11) {
                int time;
                try {
                    time = Integer.parseInt(args[0]);
                } catch (NumberFormatException e) {
                    api.sendPlayerMessage(player, "$msg.gui.recipe_creator.valid_number$");
                    return true;
                }
                furnace.setCookingTime(time);
            } else if (id == 2) {
                if (args.length > 1) {
                    workbench.setOverride(args[0] + ":" + args[1]);
                } else {
                    workbench.setOverride("");
                    return true;
                }
            }

        }
        return false;
    }
}
