package me.wolfyscript.customcrafting.listeners;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.MainConfig;
import me.wolfyscript.customcrafting.data.PlayerCache;
import me.wolfyscript.customcrafting.handlers.RecipeHandler;
import me.wolfyscript.customcrafting.items.CustomItem;
import me.wolfyscript.customcrafting.listeners.customevents.CustomCraftEvent;
import me.wolfyscript.customcrafting.listeners.customevents.CustomPreCraftEvent;
import me.wolfyscript.customcrafting.recipes.CustomRecipe;
import me.wolfyscript.customcrafting.recipes.workbench.CraftingRecipe;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.utils.InventoryUtils;
import me.wolfyscript.utilities.api.utils.RandomCollection;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerRecipeDiscoverEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class CraftListener implements Listener {

    private WolfyUtilities api;

    private MainConfig config = CustomCrafting.getConfigHandler().getConfig();

    private HashMap<UUID, String> precraftedRecipes = new HashMap<>();
    private HashMap<UUID, HashMap<String, CustomItem>> precraftedItems = new HashMap<>();
    private HashMap<UUID, ItemStack[]> replacements = new HashMap<>();

    public CraftListener(WolfyUtilities api) {
        this.api = api;
    }

    @EventHandler
    public void onAdvancedWorkbench(CustomPreCraftEvent event) {
        if (!event.isCancelled() && event.getRecipe().getId().equals("customcrafting:workbench")) {
            if (!CustomCrafting.getConfigHandler().getConfig().isAdvancedWorkbenchEnabled()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCraft(InventoryClickEvent event) {
        if (event.getClickedInventory() instanceof CraftingInventory && event.getSlot() == 0) {
            CraftingInventory inventory = (CraftingInventory) event.getClickedInventory();
            ItemStack resultItem = inventory.getResult();
            if (resultItem != null && !resultItem.getType().equals(Material.AIR) && precraftedRecipes.containsKey(event.getWhoClicked().getUniqueId()) && precraftedRecipes.get(event.getWhoClicked().getUniqueId()) != null) {
                resultItem = inventory.getResult().clone();
                CraftingRecipe recipe = CustomCrafting.getRecipeHandler().getCraftingRecipe(precraftedRecipes.get(event.getWhoClicked().getUniqueId()));
                boolean small = inventory.getMatrix().length < 9;
                if (recipe != null) {
                    CustomCraftEvent customCraftEvent = new CustomCraftEvent(recipe, inventory);
                    if (!customCraftEvent.isCancelled() && event.getCurrentItem() != null) {
                        ItemStack[] matrix = inventory.getMatrix().clone();
                        List<List<ItemStack>> ingredients = CustomCrafting.getRecipeHandler().getIngredients(matrix);
                        inventory.setResult(new ItemStack(Material.AIR));
                        Player player = (Player) event.getWhoClicked();
                        {//---------COMMANDS AND STATISTICS-------------
                            PlayerCache cache = CustomCrafting.getPlayerCache(player);
                            if (config.getCommandsSuccessCrafted() != null && !config.getCommandsSuccessCrafted().isEmpty()) {
                                for (String command : config.getCommandsSuccessCrafted()) {
                                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command.replace("%P%", player.getName()).replace("%UUID%", player.getUniqueId().toString()).replace("%REC%", recipe.getId()));
                                }
                            }
                            cache.addRecipeCrafts(customCraftEvent.getRecipe().getId());
                            cache.addAmountCrafted(1);
                            if (CustomCrafting.getWorkbenches().isWorkbench(player.getTargetBlock(null, 5).getLocation())) {
                                cache.addAmountAdvancedCrafted(1);
                            } else {
                                cache.addAmountNormalCrafted(1);
                            }
                        }//----------------------------------------------

                        //CALCULATE AMOUNTS CRAFTABLE AND REMOVE THEM!
                        int amount = recipe.getAmountCraftable(ingredients);
                        List<ItemStack> replacements = new ArrayList<>();

                        List<CustomItem> results = new ArrayList<>();
                        for(CustomItem customItem : recipe.getCustomResults()){
                            if(!customItem.hasPermission() || player.hasPermission(customItem.getPermission())){
                                results.add(customItem);
                            }
                        }
                        if (results.size() < 2 && (event.getClick().equals(ClickType.SHIFT_RIGHT) || event.getClick().equals(ClickType.SHIFT_LEFT)) ) {
                            api.sendDebugMessage("SHIFT-CLICK!");
                            if (resultItem.getAmount() > 0) {
                                int possible = InventoryUtils.getInventorySpace(player, resultItem) / resultItem.getAmount();
                                if (possible > amount) {
                                    possible = amount;
                                }
                                if (possible > 0) {
                                    api.sendDebugMessage(" possible: " + possible);
                                    replacements = recipe.removeMatrix(ingredients, inventory, small, possible);
                                }
                                Random rd = new Random();
                                for (int i = 0; i < possible; i++) {
                                    player.getInventory().addItem(resultItem);
                                    if(!customCraftEvent.getResult().isEmpty()){
                                        resultItem = customCraftEvent.getResult().get(rd.nextInt(customCraftEvent.getResult().size()));
                                    }else{
                                        resultItem = new ItemStack(Material.AIR);
                                    }
                                }
                            }
                        } else if(event.getClick().equals(ClickType.LEFT) || event.getClick().equals(ClickType.RIGHT)){
                            api.sendDebugMessage("ONE-CLICK!");
                            if (event.getView().getCursor() == null || event.getView().getCursor().getType().equals(Material.AIR) || (event.getView().getCursor() != null && event.getView().getCursor().getAmount() < event.getCursor().getMaxStackSize())) {
                                HashMap<String, CustomItem> precraftedItem = precraftedItems.getOrDefault(player.getUniqueId(), new HashMap<>());
                                if (event.getView().getCursor() != null && event.getView().getCursor().isSimilar(resultItem)) {
                                    replacements = recipe.removeMatrix(ingredients, inventory, small, 1);
                                    event.getView().getCursor().setAmount(event.getView().getCursor().getAmount() + resultItem.getAmount());
                                    precraftedItem.put(recipe.getId(), null);
                                    precraftedItems.put(player.getUniqueId(), precraftedItem);
                                    precraftedRecipes.put(event.getWhoClicked().getUniqueId(), null);
                                } else if(event.getView().getCursor() == null || event.getView().getCursor().getType().equals(Material.AIR)){
                                    replacements = recipe.removeMatrix(ingredients, inventory, small, 1);
                                    event.getView().setCursor(resultItem);
                                    precraftedItem.put(recipe.getId(), null);
                                    precraftedItems.put(player.getUniqueId(), precraftedItem);
                                    precraftedRecipes.put(event.getWhoClicked().getUniqueId(), null);
                                }
                            }
                        }
                        Bukkit.getScheduler().runTaskLater(CustomCrafting.getInst(), () -> {
                            player.updateInventory();
                            if (!CustomCrafting.getRecipeHandler().getDisabledRecipes().contains(recipe.getId())) {
                                CustomPreCraftEvent customPreCraftEvent = new CustomPreCraftEvent(false, recipe, inventory, ingredients);
                                if (checkRecipe(recipe, ingredients, player, CustomCrafting.getRecipeHandler(), customPreCraftEvent)) {
                                    RandomCollection<CustomItem> items = new RandomCollection<>();
                                    for(CustomItem customItem : customPreCraftEvent.getResult()){
                                        if(!customItem.hasPermission() || player.hasPermission(customItem.getPermission())){
                                            items.add(customItem.getRarityPercentage(), customItem);
                                        }
                                    }
                                    HashMap<String, CustomItem> precraftedItem1 = precraftedItems.getOrDefault(player.getUniqueId(), new HashMap<>());
                                    CustomItem result = new CustomItem(Material.AIR);
                                    if(precraftedItem1.get(recipe.getId()) == null){
                                        if(!items.isEmpty()){
                                            result = items.next();
                                            precraftedItem1.put(recipe.getId(), result);
                                            precraftedItems.put(player.getUniqueId(), precraftedItem1);
                                        }
                                    }else{
                                        result = precraftedItem1.get(recipe.getId());
                                    }
                                    inventory.setResult(result);
                                }
                            }
                            player.updateInventory();
                        }, 1);
                        for (ItemStack itemStack : replacements) {
                            if (InventoryUtils.hasInventorySpace(player, itemStack)) {
                                player.getInventory().addItem(itemStack);
                            } else {
                                player.getLocation().getWorld().dropItemNaturally(player.getLocation(), itemStack);
                            }
                        }
                        return;
                    }
                }
            }
            precraftedRecipes.put(event.getWhoClicked().getUniqueId(), null);
        } else if (event.getClickedInventory() instanceof CraftingInventory) {
            Bukkit.getScheduler().runTaskLater(CustomCrafting.getInst(), () -> {
                PrepareItemCraftEvent event1 = new PrepareItemCraftEvent((CraftingInventory) event.getClickedInventory(), event.getView(), false);
                Bukkit.getPluginManager().callEvent(event1);
            }, 1);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCraft(CraftItemEvent event) {
        //EMPTY
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPreCraft(PrepareItemCraftEvent e) {
        Player player = (Player) e.getView().getPlayer();
        try {
            ItemStack[] matrix = e.getInventory().getMatrix();
            RecipeHandler recipeHandler = CustomCrafting.getRecipeHandler();

            List<List<ItemStack>> ingredients = recipeHandler.getIngredients(matrix);
            List<CraftingRecipe> recipesToCheck = new ArrayList<>(recipeHandler.getSimilarRecipes(ingredients));
            recipesToCheck.sort(Comparator.comparing(CustomRecipe::getPriority));

            api.sendDebugMessage("---------------------------------");
            api.sendDebugMessage("Possible Custom Recipes detected:");
            recipesToCheck.forEach(craftingRecipe -> api.sendDebugMessage(" - " + craftingRecipe.getId()));
            api.sendDebugMessage("");
            boolean allow = false;
            if (!recipesToCheck.isEmpty() && !CustomCrafting.getConfigHandler().getConfig().isLockedDown()) {
                CustomPreCraftEvent customPreCraftEvent;
                for (CraftingRecipe recipe : recipesToCheck) {
                    if (recipe != null && !recipeHandler.getDisabledRecipes().contains(recipe.getId())) {
                        customPreCraftEvent = new CustomPreCraftEvent(e.isRepair(), recipe, e.getInventory(), ingredients);
                        if (checkRecipe(recipe, ingredients, player, recipeHandler, customPreCraftEvent)) {
                            allow = true;
                            RandomCollection<CustomItem> items = new RandomCollection<>();
                            for(CustomItem customItem : customPreCraftEvent.getResult()){
                                if(!customItem.hasPermission() || player.hasPermission(customItem.getPermission())){
                                    items.add(customItem.getRarityPercentage(), customItem);
                                }
                            }
                            HashMap<String, CustomItem> precraftedItem = precraftedItems.getOrDefault(player.getUniqueId(), new HashMap<>());
                            CustomItem result = new CustomItem(Material.AIR);
                            if(precraftedItem.get(recipe.getId()) == null){
                                if(!items.isEmpty()){
                                    result = items.next();
                                    precraftedItem.put(recipe.getId(), result);
                                    precraftedItems.put(player.getUniqueId(), precraftedItem);
                                }
                            }else{
                                result = precraftedItem.get(recipe.getId());
                            }
                            e.getInventory().setResult(result);
                            break;
                        }
                    }
                }
            }
            if (!allow) {
                api.sendDebugMessage("No valid recipe!");
                precraftedRecipes.remove(player.getUniqueId());
                if (e.getRecipe() != null) {
                    if (e.getRecipe() instanceof Keyed) {
                        api.sendDebugMessage("Detected recipe: " + ((Keyed) e.getRecipe()).getKey());
                        CraftingRecipe recipe = recipeHandler.getCraftingRecipe(((Keyed) e.getRecipe()).getKey().toString());
                        if (recipeHandler.getDisabledRecipes().contains(((Keyed) e.getRecipe()).getKey().toString()) || recipe != null) {
                            //Recipe is disabled or it is a custom recipe! Due the changes, custom recipes added to Bukkit are only placeholders in the vanilla knowledge book!
                            e.getInventory().setResult(new ItemStack(Material.AIR));
                        } else {
                            api.sendDebugMessage("Use vanilla recipe output!");
                        }
                    }
                }
            }
            player.updateInventory();
        } catch (Exception ex) {
            System.out.println("WHAT HAPPENED? Please report!");
            ex.printStackTrace();
            System.out.println("WHAT HAPPENED? Please report!");
            precraftedRecipes.remove(player.getUniqueId());
            e.getInventory().setResult(new ItemStack(Material.AIR));
        }
    }

    private boolean checkRecipe(CraftingRecipe recipe, List<List<ItemStack>> ingredients, Player player, RecipeHandler recipeHandler, CustomPreCraftEvent customPreCraftEvent) {
        boolean perm = checkWorkbenchAndPerm(player, player.getTargetBlock(null, 5).getLocation(), recipe);
        boolean check = recipe.check(ingredients);
        if (!(perm && check) || recipeHandler.getDisabledRecipes().contains(recipe.getId())) {
            api.sendDebugMessage("  invalid: " + recipe.getId());
            customPreCraftEvent.setCancelled(true);
        }
        /*
         The event is still called even if the recipe is invalid! This will allow other plugins to manipulate their own recipes!
        */
        Bukkit.getPluginManager().callEvent(customPreCraftEvent);
        if (!customPreCraftEvent.isCancelled()) {
            //ALLOWED
            api.sendDebugMessage("Recipe \"" + customPreCraftEvent.getRecipe().getId() + "\" detected!");
            precraftedRecipes.put(player.getUniqueId(), customPreCraftEvent.getRecipe().getId());
            return true;
        }
        return false;
    }

    private boolean checkWorkbenchAndPerm(Player player, Location location, CraftingRecipe recipe) {
        if (!recipe.needsAdvancedWorkbench() || (location != null && CustomCrafting.getWorkbenches().isWorkbench(location))) {
            String perm = "customcrafting.craft." + recipe.getId();
            String perm2 = "customcrafting.craft." + recipe.getId().split(":")[0];

            if (recipe.needsPermission()) {
                if (!player.hasPermission("customcrafting.craft.*")) {
                    if (!player.hasPermission(perm)) {
                        return player.hasPermission(perm2);
                    }
                }
            }
            return true;
        }
        return false;
    }

    public HashMap<UUID, ItemStack[]> getReplacements() {
        return replacements;
    }
}
