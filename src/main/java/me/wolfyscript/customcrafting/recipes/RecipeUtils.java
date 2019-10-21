package me.wolfyscript.customcrafting.recipes;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.MainConfig;
import me.wolfyscript.customcrafting.data.PlayerCache;
import me.wolfyscript.customcrafting.handlers.RecipeHandler;
import me.wolfyscript.customcrafting.listeners.customevents.CustomCraftEvent;
import me.wolfyscript.customcrafting.listeners.customevents.CustomPreCraftEvent;
import me.wolfyscript.customcrafting.recipes.types.CraftingRecipe;
import me.wolfyscript.customcrafting.recipes.types.CustomRecipe;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.utils.InventoryUtils;
import me.wolfyscript.utilities.api.utils.RandomCollection;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class RecipeUtils {

    private static WolfyUtilities api = CustomCrafting.getApi();

    private static HashMap<UUID, String> precraftedRecipes = new HashMap<>();
    private static HashMap<UUID, HashMap<String, CustomItem>> precraftedItems = new HashMap<>();

    public static ItemStack preCheckRecipe(ItemStack[] matrix, Player player, boolean isRepair, Inventory inventory, boolean elite, boolean advanced){
        RecipeHandler recipeHandler = CustomCrafting.getRecipeHandler();
        List<List<ItemStack>> ingredients = recipeHandler.getIngredients(matrix);
        List<CraftingRecipe> recipesToCheck = new ArrayList<>(recipeHandler.getSimilarRecipes(ingredients, elite, advanced));
        recipesToCheck.sort(Comparator.comparing(CustomRecipe::getPriority));
        recipesToCheck.forEach(craftingRecipe -> api.sendDebugMessage(" - " + craftingRecipe.getId()));
        if (!recipesToCheck.isEmpty() && !CustomCrafting.getConfigHandler().getConfig().isLockedDown()) {
            CustomPreCraftEvent customPreCraftEvent;
            for (CraftingRecipe recipe : recipesToCheck) {
                if (recipe != null && !recipeHandler.getDisabledRecipes().contains(recipe.getId())) {
                    customPreCraftEvent = new CustomPreCraftEvent(isRepair, recipe, inventory, ingredients);
                    if (checkRecipe(recipe, ingredients, player, recipeHandler, customPreCraftEvent)) {
                        RandomCollection<CustomItem> items = new RandomCollection<>();
                        for(CustomItem customItem : customPreCraftEvent.getResult()){
                            if(!customItem.hasPermission() || player.hasPermission(customItem.getPermission())){
                                items.add(customItem.getRarityPercentage(), customItem.clone());
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
                        return result.getItemStack();
                    }
                }
            }
        }
        return null;
    }

    public static boolean checkRecipe(CraftingRecipe recipe, List<List<ItemStack>> ingredients, Player player, RecipeHandler recipeHandler, CustomPreCraftEvent customPreCraftEvent) {
        customPreCraftEvent.setCancelled(true);
        if(!recipeHandler.getDisabledRecipes().contains(recipe.getId())){
            if(recipe.getConditions().checkConditions(recipe, new Conditions.Data(player, player.getTargetBlock(null, 5), player.getOpenInventory()))){
                if(recipe.check(ingredients)){
                    customPreCraftEvent.setCancelled(false);
                }
            }
        }
        /*
         The event is still called even if the recipe is invalid! This will allow other plugins to manipulate their own recipes or use their own checks!
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

    public static void consumeRecipe(ItemStack resultItem, ItemStack[] matrix, InventoryClickEvent event){
        MainConfig config = CustomCrafting.getConfigHandler().getConfig();
        Inventory inventory = event.getClickedInventory();
        if (resultItem != null && !resultItem.getType().equals(Material.AIR) && RecipeUtils.getPrecraftedRecipes().containsKey(event.getWhoClicked().getUniqueId()) && RecipeUtils.getPrecraftedRecipes().get(event.getWhoClicked().getUniqueId()) != null) {
            CraftingRecipe recipe = CustomCrafting.getRecipeHandler().getCraftingRecipe(RecipeUtils.getPrecraftedRecipes().get(event.getWhoClicked().getUniqueId()));
            boolean small = matrix.length < 9;
            if (recipe != null) {
                CustomCraftEvent customCraftEvent = new CustomCraftEvent(recipe, inventory);
                if (!customCraftEvent.isCancelled() && event.getCurrentItem() != null) {
                    List<List<ItemStack>> ingredients = CustomCrafting.getRecipeHandler().getIngredients(matrix);
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
                                replacements = recipe.removeMatrix(ingredients, inventory, matrix, small, possible);
                            }
                            Random rd = new Random();
                            for (int i = 0; i < possible; i++) {
                                player.getInventory().addItem(resultItem);
                                if(!customCraftEvent.getResult().isEmpty()){
                                    resultItem = customCraftEvent.getResult().get(rd.nextInt(customCraftEvent.getResult().size())).getRealItem();
                                }else{
                                    resultItem = new ItemStack(Material.AIR);
                                }
                            }
                        }
                    } else if(event.getClick().equals(ClickType.LEFT) || event.getClick().equals(ClickType.RIGHT)){
                        api.sendDebugMessage("ONE-CLICK!");
                        if (event.getView().getCursor() == null || event.getView().getCursor().getType().equals(Material.AIR) || (event.getView().getCursor() != null && event.getView().getCursor().getAmount() < event.getCursor().getMaxStackSize())) {
                            HashMap<String, CustomItem> precraftedItem = RecipeUtils.getPrecraftedItems().getOrDefault(player.getUniqueId(), new HashMap<>());
                            replacements = recipe.removeMatrix(ingredients, inventory, matrix, small, 1);
                            if (event.getView().getCursor() != null && event.getView().getCursor().isSimilar(resultItem)) {
                                event.getView().getCursor().setAmount(event.getView().getCursor().getAmount() + resultItem.getAmount());
                            } else if(event.getView().getCursor() == null || event.getView().getCursor().getType().equals(Material.AIR)){
                                event.getView().setCursor(resultItem);
                            }
                            precraftedItem.put(recipe.getId(), null);
                            RecipeUtils.getPrecraftedItems().put(player.getUniqueId(), precraftedItem);
                            RecipeUtils.getPrecraftedRecipes().put(event.getWhoClicked().getUniqueId(), null);
                        }
                    }
                    for (ItemStack itemStack : replacements) {
                        if (InventoryUtils.hasInventorySpace(player, itemStack)) {
                            player.getInventory().addItem(itemStack);
                        } else {
                            player.getLocation().getWorld().dropItemNaturally(player.getLocation(), itemStack);
                        }
                    }
                }
            }
            precraftedRecipes.put(event.getWhoClicked().getUniqueId(), null);
        }
    }

    public static HashMap<UUID, HashMap<String, CustomItem>> getPrecraftedItems() {
        return precraftedItems;
    }

    public static HashMap<UUID, String> getPrecraftedRecipes() {
        return precraftedRecipes;
    }

    public static boolean testNameSpaceKey(String namespace, String key){
        return CustomCrafting.VALID_NAMESPACE.matcher(namespace).matches() && CustomCrafting.VALID_KEY.matcher(key).matches();
    }
}
