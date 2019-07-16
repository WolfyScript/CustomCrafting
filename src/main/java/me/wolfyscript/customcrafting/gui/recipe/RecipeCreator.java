package me.wolfyscript.customcrafting.gui.recipe;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.custom_configs.CookingConfig;
import me.wolfyscript.customcrafting.configs.custom_configs.anvil.AnvilConfig;
import me.wolfyscript.customcrafting.configs.custom_configs.blast_furnace.BlastingConfig;
import me.wolfyscript.customcrafting.configs.custom_configs.campfire.CampfireConfig;
import me.wolfyscript.customcrafting.configs.custom_configs.smoker.SmokerConfig;
import me.wolfyscript.customcrafting.configs.custom_configs.stonecutter.StonecutterConfig;
import me.wolfyscript.customcrafting.configs.custom_configs.workbench.CraftConfig;
import me.wolfyscript.customcrafting.configs.custom_configs.furnace.FurnaceConfig;
import me.wolfyscript.customcrafting.data.PlayerCache;
import me.wolfyscript.customcrafting.data.cache.*;
import me.wolfyscript.customcrafting.gui.ExtendedGuiWindow;
import me.wolfyscript.customcrafting.gui.Setting;
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

import java.util.*;

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
        createItem("anvil.repair_mode", Material.GLOWSTONE_DUST);
        createItem("anvil.repair_apply.true", Material.GREEN_CONCRETE);
        createItem("anvil.repair_apply.false", Material.RED_CONCRETE);
        createItem("anvil.repair_cost", Material.EXPERIENCE_BOTTLE);
        createItem("anvil.block_repair.true", Material.IRON_SWORD);
        createItem("anvil.block_repair.false", Material.IRON_SWORD);
        createItem("anvil.block_rename.true", Material.WRITABLE_BOOK);
        createItem("anvil.block_rename.false", Material.WRITABLE_BOOK);
        createItem("anvil.block_enchant.true", Material.ENCHANTING_TABLE);
        createItem("anvil.block_enchant.false", Material.ENCHANTING_TABLE);
        createItem("anvil.durability", Material.IRON_SWORD);

        createItem("anvil.input.cancel", Material.BARRIER);
        createItem("anvil.input.empty", Material.BARRIER);
    }

    @EventHandler
    public void onUpdate(GuiUpdateEvent event) {
        if (event.verify(this)) {
            PlayerCache cache = CustomCrafting.getPlayerCache(event.getPlayer());

            Workbench workbench = cache.getWorkbench();
            switch (cache.getSetting()) {
                case WORKBENCH:
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
                    event.setItem(44, "save");
                    break;
                case ANVIL:
                    Anvil anvil = cache.getAnvil();

                    if(anvil.getMenu().equals(Anvil.Menu.MAINMENU)){
                        event.setItem(6, anvil.isExactMeta() ? "exact_meta_on" : "exact_meta_off");
                        event.setItem(4, event.getItem("priority", "%PRI%", anvil.getPriority().name()));
                        event.setItem(2, anvil.isPermissions() ? "permissions_on" : "permissions_off");

                        event.setItem(19, anvil.getInputLeft().isEmpty() ? event.getItem("anvil.input.empty") : anvil.getInputLeft().get(0));
                        event.setItem(21, anvil.getInputRight().isEmpty() ? event.getItem("anvil.input.empty") : anvil.getInputRight().get(0));

                        if (anvil.getMode().equals(CustomAnvilRecipe.Mode.RESULT)) {
                            event.setItem(25, anvil.getResult().getIDItem());
                        } else if (anvil.getMode().equals(CustomAnvilRecipe.Mode.DURABILITY)) {
                            event.setItem(25, event.getItem("anvil.durability", "%VAR%", String.valueOf(anvil.getDurability())));
                        } else {
                            event.setItem(25, new ItemStack(Material.BARRIER));
                        }

                        event.setItem(23, event.getItem("anvil.mode", "%MODE%", anvil.getMode().name()));

                        event.setItem(36, anvil.isBlockEnchant() ? "anvil.block_enchant.true" : "anvil.block_enchant.false");
                        event.setItem(37, anvil.isBlockRename() ? "anvil.block_rename.true" : "anvil.block_rename.false");
                        event.setItem(38, anvil.isBlockRepair() ? "anvil.block_repair.true" : "anvil.block_repair.false");

                        event.setItem(40, anvil.isApplyRepairCost() ? "anvil.repair_apply.true" : "anvil.repair_apply.false");
                        event.setItem(41, event.getItem("anvil.repair_cost", "%VAR%", String.valueOf(anvil.getRepairCost())));
                        event.setItem(42, event.getItem("anvil.repair_mode", "%VAR%", anvil.getRepairCostMode().toString()));

                        event.setItem(44, "save");
                    }else{
                        event.setItem(0, "glass_white", true);
                        List<CustomItem> input = anvil.getMenu().equals(Anvil.Menu.INPUT_LEFT) ? anvil.getInputLeft() : anvil.getInputRight();

                        for(int i = 0; i < 27; i++){
                            event.setItem(9+i, new ItemStack(Material.AIR));
                        }

                        Iterator<CustomItem> itr = input.iterator();
                        for(int i = 0; itr.hasNext(); i++){
                            CustomItem item = itr.next();
                            event.setItem(9+i, item.getIDItem());
                        }

                        event.setItem(40, "anvil.input.cancel");
                    }
                    break;
                case STONECUTTER:
                    Stonecutter stonecutter = cache.getStonecutter();
                    event.setItem(20, stonecutter.getSource().getIDItem());
                    event.setItem(24, stonecutter.getResult().getIDItem());
                    event.setItem(44, "save");
                    break;
                case BLAST_FURNACE:
                case SMOKER:
                case CAMPFIRE:
                case FURNACE:
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
                    event.setItem(44, "save");
                    break;
            }
        }
    }

    @Override
    public boolean onAction(GuiAction guiAction) {
        if (!super.onAction(guiAction)) {
            String action = guiAction.getAction();
            PlayerCache cache = CustomCrafting.getPlayerCache(guiAction.getPlayer());
            updateInv(guiAction.getPlayer().getOpenInventory().getTopInventory(), cache);
            switch (cache.getSetting()) {
                case WORKBENCH:
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
                        if(action.startsWith("input.")){
                            switch (action.substring("input.".length())){
                                case "cancel":
                                    anvil.setMenu(Anvil.Menu.MAINMENU);
                                    break;
                                case "empty":
                                    if(guiAction.getRawSlot() == 19){
                                        anvil.setMenu(Anvil.Menu.INPUT_LEFT);
                                        update(guiAction.getGuiHandler());
                                    }else{
                                        anvil.setMenu(Anvil.Menu.INPUT_RIGHT);
                                        update(guiAction.getGuiHandler());
                                    }
                                    break;
                            }
                        }else{
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
                                    runChat(40, "$msg.gui.recipe_creator.anvil.durability$", guiAction.getGuiHandler());
                                    break;
                                case "repair_cost":
                                    runChat(30, "$msg.gui.recipe_creator.anvil.repair_cost$", guiAction.getGuiHandler());
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
                                    break;
                                case "repair_apply":
                                    anvil.setApplyRepairCost(!anvil.isApplyRepairCost());
                                    break;
                                case "repair_mode":
                                    int index = CustomAnvilRecipe.RepairCostMode.getModes().indexOf(anvil.getRepairCostMode())+1;
                                    anvil.setRepairCostMode(CustomAnvilRecipe.RepairCostMode.getModes().get(index >= CustomAnvilRecipe.RepairCostMode.getModes().size() ? 0 : index));
                                    break;

                            }
                        }
                    } else if (action.startsWith("permissions_")) {
                        anvil.setPermissions(!anvil.isPermissions());
                    } else if (action.startsWith("exact_meta_")) {
                        anvil.setExactMeta(!anvil.isExactMeta());
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
                case FURNACE:
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
            case WORKBENCH:
                Workbench workbench = cache.getWorkbench();
                if (!workbench.getIngredients().isEmpty() && !workbench.getResult().getType().equals(Material.AIR))
                    return true;
                break;
            case ANVIL:
                Anvil anvil = cache.getAnvil();
                if(!anvil.getInputLeft().isEmpty() || !anvil.getInputRight().isEmpty()){
                    if(anvil.getMode().equals(CustomAnvilRecipe.Mode.RESULT)){
                        return anvil.getResult() != null && !anvil.getResult().getType().equals(Material.AIR);
                    }else{
                        return true;
                    }
                }else{
                    return false;
                }
            case STONECUTTER:
                Stonecutter stonecutter = cache.getStonecutter();
                if (!stonecutter.getResult().getType().equals(Material.AIR) && !stonecutter.getSource().getType().equals(Material.AIR))
                    return true;
                break;
            case BLAST_FURNACE:
            case SMOKER:
            case CAMPFIRE:
            case FURNACE:
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
        Anvil anvil = playerCache.getAnvil();
        updateInv(player.getOpenInventory().getTopInventory(), playerCache);
        if (guiClick.getClickedInventory().equals(player.getOpenInventory().getTopInventory())) {
            if (guiClick.getClickType().equals(ClickType.SHIFT_RIGHT)) {
                CustomItem customItem = CustomItem.getByItemStack(guiClick.getCurrentItem() == null ? new ItemStack(Material.AIR) : guiClick.getCurrentItem());
                String id = customItem.getId();
                switch (playerCache.getSetting()) {
                    case WORKBENCH:
                        if ((slot >= 10 && slot < 13) || (slot >= 19 && slot < 22) || (slot >= 28 && slot < 31)) {
                            items.setIngredient((slot - 10) - 6 * (((slot - 10) / 4) / 2), customItem);
                            guiClick.getGuiHandler().changeToInv("item_editor");
                            return true;
                        } else {
                            items.setResult(customItem);
                            guiClick.getGuiHandler().changeToInv("item_editor");
                            return true;
                        }
                    case ANVIL:
                        if(slot == 19){
                            anvil.setMenu(Anvil.Menu.INPUT_LEFT);
                            update(guiClick.getGuiHandler());
                        }else if(slot == 21){
                            anvil.setMenu(Anvil.Menu.INPUT_RIGHT);
                            update(guiClick.getGuiHandler());
                        }else if (slot == 25){
                            items.setResult(customItem);
                            guiClick.getGuiHandler().changeToInv("item_editor");
                        }
                        return true;
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
                    case FURNACE:
                        if (slot == 11) {
                            items.setSource(customItem);
                        } else {
                            items.setResult(customItem);
                        }
                        guiClick.getGuiHandler().changeToInv("item_editor");
                        return true;
                }
            }else{
                if(playerCache.getSetting().equals(Setting.ANVIL)){
                    if(anvil.getMenu().equals(Anvil.Menu.MAINMENU)){
                        if(slot == 19){
                            anvil.setMenu(Anvil.Menu.INPUT_LEFT);
                            update(guiClick.getGuiHandler());
                            return true;
                        }else if(slot == 21){
                            anvil.setMenu(Anvil.Menu.INPUT_RIGHT);
                            update(guiClick.getGuiHandler());
                            return true;
                        }
                    }else{


                    }
                }
            }
        }
        return false;
    }

    private void updateInv(Inventory inv, PlayerCache cache) {
        switch (cache.getSetting()) {
            case WORKBENCH:
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
                    CustomItem customItem = CustomItem.getByItemStack(inv.getItem(24));
                    workbench.setResult(customItem);
                    workbench.setResultCustomAmount(customItem.getAmount());
                } else {
                    workbench.setResult(new CustomItem(Material.AIR));
                }
                break;
            case ANVIL:
                Anvil anvil = cache.getAnvil();
                if(anvil.getMenu().equals(Anvil.Menu.INPUT_LEFT) || anvil.getMenu().equals(Anvil.Menu.INPUT_RIGHT)){
                    //TODO: UPDATE INPUT VARIANTS!
                    List<CustomItem> inputVariants = new ArrayList<>();
                    for(int i = 9; i < 36; i++){
                        ItemStack itemStack = inv.getItem(i);
                        if(itemStack != null && !itemStack.getType().equals(Material.AIR)){
                            CustomItem customItem = CustomItem.getByItemStack(itemStack);
                            inputVariants.add(customItem);
                        }
                    }
                    anvil.setInput(inputVariants);
                }else{
                    if(anvil.getMode().equals(CustomAnvilRecipe.Mode.RESULT)){
                        ItemStack result = inv.getItem(25);
                        if (result != null) {
                            CustomItem customItem = CustomItem.getByItemStack(result);
                            anvil.setResult(customItem);
                        } else {
                            anvil.setResult(new CustomItem(Material.AIR));
                        }
                    }
                }
                break;
            case STONECUTTER:
                Stonecutter stonecutter = cache.getStonecutter();
                if (inv.getItem(24) != null) {
                    stonecutter.setResult(CustomItem.getByItemStack(inv.getItem(24)));
                } else {
                    stonecutter.setResult(new CustomItem(Material.AIR));
                }
                if (inv.getItem(20) != null) {
                    stonecutter.setSource(CustomItem.getByItemStack(inv.getItem(20)));
                } else {
                    stonecutter.setSource(new CustomItem(Material.AIR));
                }
                break;
            case BLAST_FURNACE:
            case SMOKER:
            case CAMPFIRE:
            case FURNACE:
                CookingData cooking = cache.getCookingData();
                if (inv.getItem(24) != null) {
                    cooking.setResult(CustomItem.getByItemStack(inv.getItem(24)));
                } else {
                    cooking.setResult(new CustomItem(Material.AIR));
                }
                if (inv.getItem(11) != null) {
                    cooking.setSource(CustomItem.getByItemStack(inv.getItem(11)));
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
                    String namespace = args[0].toLowerCase(Locale.ROOT).replace(" ", "_");
                    String key = args[1].toLowerCase(Locale.ROOT).replace(" ", "_");
                    if(!CustomCrafting.VALID_NAMESPACE.matcher(namespace).matches()){
                        api.sendPlayerMessage(player, "&cInvalid Namespace! Namespaces may only contain lowercase alphanumeric characters, periods, underscores, and hyphens!");
                        return true;
                    }
                    if(!CustomCrafting.VALID_KEY.matcher(key).matches()){
                        api.sendPlayerMessage(player, "&cInvalid key! Keys may only contain lowercase alphanumeric characters, periods, underscores, and hyphens!");
                        return true;
                    }
                    CookingConfig cookingConfig = null;
                    switch (cache.getSetting()) {
                        case WORKBENCH:
                            CraftConfig config = new CraftConfig(api.getConfigAPI(), namespace, key);
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
                            HashMap<Character, ArrayList<CustomItem>> ingredients = workbench.getIngredients();
                            api.sendDebugMessage("  Ingredients: " + ingredients);
                            String[] shape = new String[3];
                            int index = 0;
                            int row = 0;
                            for (char ingrd : ingredients.keySet()) {
                                List<CustomItem> keyItems = ingredients.get(ingrd);
                                if (ItemUtils.isEmpty(keyItems)) {
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
                            config.save();
                            api.sendDebugMessage("Reset GUI cache...");
                            cache.resetWorkbench();
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
                            AnvilConfig anvilConfig = new AnvilConfig(api.getConfigAPI(), namespace, key);
                            anvilConfig.setBlockEnchant(anvil.isBlockEnchant());
                            anvilConfig.setBlockRename(anvil.isBlockRename());
                            anvilConfig.setBlockRepairing(anvil.isBlockRepair());
                            anvilConfig.setExactMeta(anvil.isExactMeta());
                            anvilConfig.setPermission(anvil.isPermissions());
                            anvilConfig.setRepairCost(anvil.getRepairCost());
                            anvilConfig.setPriority(anvil.getPriority());
                            anvilConfig.setMode(anvil.getMode());
                            anvilConfig.setResult(anvil.getResult());
                            anvilConfig.setDurability(anvil.getDurability());
                            anvilConfig.setInputLeft(anvil.getInputLeft());
                            anvilConfig.setInputRight(anvil.getInputRight());
                            anvilConfig.save();
                            cache.resetAnvil();

                            try {
                                Bukkit.getScheduler().runTaskLater(CustomCrafting.getInst(), () -> {
                                    CustomCrafting.getRecipeHandler().injectRecipe(new CustomAnvilRecipe(anvilConfig));
                                    api.sendPlayerMessage(player, "$msg.gui.recipe_creator.loading.success$");
                                }, 1);
                            }catch (Exception ex){
                                api.sendPlayerMessage(player, "$msg.gui.recipe_creator.error_loading$", new String[]{"%REC%", anvilConfig.getId()});
                                ex.printStackTrace();
                                return false;
                            }
                            Bukkit.getScheduler().runTaskLater(CustomCrafting.getInst(), () -> guiHandler.changeToInv("main_menu"), 1);
                            return false;
                        case STONECUTTER:
                            Stonecutter stonecutter = cache.getStonecutter();
                            StonecutterConfig stonecutterConfig = new StonecutterConfig(api.getConfigAPI(), namespace, key);
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
                            cookingConfig.save();
                            cookingConfig.load();
                            cache.resetCookingData();
                            api.sendPlayerMessage(player, "$msg.gui.recipe_creator.save.success$");
                            api.sendPlayerMessage(player, "§6recipes/" + namespace + "/furnace/" + key);
                            try {
                                CustomRecipe customRecipe = null;
                                switch (cache.getSetting()) {
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
