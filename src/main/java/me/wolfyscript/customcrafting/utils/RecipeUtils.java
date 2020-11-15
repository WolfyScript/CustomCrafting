package me.wolfyscript.customcrafting.utils;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.MainConfig;
import me.wolfyscript.customcrafting.data.PlayerStatistics;
import me.wolfyscript.customcrafting.handlers.RecipeHandler;
import me.wolfyscript.customcrafting.listeners.customevents.CustomCraftEvent;
import me.wolfyscript.customcrafting.listeners.customevents.CustomPreCraftEvent;
import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.types.CraftingRecipe;
import me.wolfyscript.customcrafting.recipes.types.ICustomRecipe;
import me.wolfyscript.customcrafting.recipes.types.workbench.CraftingData;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.utils.NamespacedKey;
import me.wolfyscript.utilities.api.utils.RandomCollection;
import me.wolfyscript.utilities.api.utils.inventory.InventoryUtils;
import me.wolfyscript.utilities.api.utils.inventory.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class RecipeUtils {

    private static final WolfyUtilities api = CustomCrafting.getApi();

    private final HashMap<UUID, CraftingData> preCraftedRecipes = new HashMap<>();
    private final HashMap<UUID, HashMap<NamespacedKey, CustomItem>> precraftedItems = new HashMap<>();
    private final CustomCrafting customCrafting;

    public RecipeUtils(CustomCrafting customCrafting) {
        this.customCrafting = customCrafting;
    }

    public ItemStack preCheckRecipe(ItemStack[] matrix, Player player, boolean isRepair, Inventory inventory, boolean elite, boolean advanced) {
        preCraftedRecipes.remove(player.getUniqueId());
        RecipeHandler recipeHandler = customCrafting.getRecipeHandler();
        if (customCrafting.getConfigHandler().getConfig().isLockedDown()) return null;
        List<List<ItemStack>> ingredients = recipeHandler.getIngredients(matrix);
        CustomItem customItem = recipeHandler.getSimilarRecipesStream(ingredients, elite, advanced).sorted(Comparator.comparing(ICustomRecipe::getPriority)).map(recipe -> checkRecipe(recipe, ingredients, player, inventory, recipeHandler, isRepair, () -> recipe.check(ingredients))).filter(Objects::nonNull).findFirst().orElse(null);
        return customItem == null ? null : customItem.create();
    }

    public CustomItem checkRecipe(CraftingRecipe<?> recipe, List<List<ItemStack>> matrix, Player player, Inventory inventory, RecipeHandler recipeHandler, boolean isRepair, Supplier<CraftingData> craftingDataSupplier) {
        CustomPreCraftEvent customPreCraftEvent = new CustomPreCraftEvent(isRepair, recipe, inventory, matrix);
        customPreCraftEvent.setCancelled(true);
        CraftingData craftingData = null;
        if (!recipeHandler.getDisabledRecipes().contains(recipe.getNamespacedKey().toString()) && recipe.getConditions().checkConditions(recipe, new Conditions.Data(player, player.getTargetBlock(null, 5), player.getOpenInventory()))) {
            craftingData = craftingDataSupplier.get();
            if (craftingData != null) {
                customPreCraftEvent.setCancelled(false);
            }
        }
        /*
         The event is still called even if the recipe is invalid! This will allow other plugins to manipulate their own recipes or use their own checks!
        */
        Bukkit.getPluginManager().callEvent(customPreCraftEvent);
        if (!customPreCraftEvent.isCancelled()) {
            //ALLOWED
            //api.sendDebugMessage("Recipe \"" + customPreCraftEvent.getRecipe().getNamespacedKey().toString() + "\" detected!");
            preCraftedRecipes.put(player.getUniqueId(), craftingData);

            RandomCollection<CustomItem> items = new RandomCollection<>();
            customPreCraftEvent.getResult().stream().filter(customItem -> !customItem.hasPermission() || player.hasPermission(customItem.getPermission())).forEach(customItem -> items.add(customItem.getRarityPercentage(), customItem.clone()));
            HashMap<NamespacedKey, CustomItem> precraftedItem = precraftedItems.getOrDefault(player.getUniqueId(), new HashMap<>());
            CustomItem result = new CustomItem(Material.AIR);
            if (precraftedItem.get(recipe.getNamespacedKey()) == null) {
                if (!items.isEmpty()) {
                    result = items.next();
                    precraftedItem.put(recipe.getNamespacedKey(), result);
                    precraftedItems.put(player.getUniqueId(), precraftedItem);
                }
            } else {
                result = precraftedItem.get(recipe.getNamespacedKey());
            }
            return result;
        }
        return null;
    }

    public void consumeRecipe(ItemStack resultItem, ItemStack[] matrix, InventoryClickEvent event) {
        MainConfig config = customCrafting.getConfigHandler().getConfig();
        Inventory inventory = event.getClickedInventory();
        if (resultItem != null && !resultItem.getType().equals(Material.AIR) && getPreCraftedRecipes().containsKey(event.getWhoClicked().getUniqueId()) && getPreCraftedRecipes().get(event.getWhoClicked().getUniqueId()) != null) {
            CraftingData craftingData = getPreCraftedRecipes().get(event.getWhoClicked().getUniqueId());
            CraftingRecipe<?> recipe = craftingData.getRecipe();
            if (recipe != null) {
                CustomCraftEvent customCraftEvent = new CustomCraftEvent(recipe, inventory);
                if (!customCraftEvent.isCancelled() && event.getCurrentItem() != null) {
                    List<List<ItemStack>> ingredients = customCrafting.getRecipeHandler().getIngredients(matrix);
                    Player player = (Player) event.getWhoClicked();
                    {//---------COMMANDS AND STATISTICS-------------
                        PlayerStatistics cache = CustomCrafting.getPlayerStatistics(player);
                        if (config.getCommandsSuccessCrafted() != null && !config.getCommandsSuccessCrafted().isEmpty()) {
                            config.getCommandsSuccessCrafted().forEach(c -> Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), c.replace("%P%", player.getName()).replace("%UUID%", player.getUniqueId().toString()).replace("%REC%", recipe.getNamespacedKey().toString())));
                        }
                        cache.addRecipeCrafts(customCraftEvent.getRecipe().getNamespacedKey().toString());
                        cache.addAmountCrafted(1);
                        if (CustomCrafting.getWorkbenches().isWorkbench(player.getTargetBlock(null, 5).getLocation())) {
                            cache.addAmountAdvancedCrafted(1);
                        } else {
                            cache.addAmountNormalCrafted(1);
                        }
                    }//----------------------------------------------

                    //CALCULATE AMOUNTS CRAFTABLE AND REMOVE THEM!
                    int amount = recipe.getAmountCraftable(ingredients, craftingData);
                    List<CustomItem> results = recipe.getResults().stream().filter(customItem -> !customItem.hasPermission() || player.hasPermission(customItem.getPermission())).collect(Collectors.toList());
                    if (results.size() < 2 && (event.getClick().equals(ClickType.SHIFT_RIGHT) || event.getClick().equals(ClickType.SHIFT_LEFT))) {
                        //api.sendDebugMessage("SHIFT-CLICK!");
                        if (resultItem.getAmount() > 0) {
                            int possible = Math.min(InventoryUtils.getInventorySpace(event.getView().getBottomInventory(), resultItem) / resultItem.getAmount(), amount);
                            if (possible > 0) {
                                //api.sendDebugMessage(" possible: " + possible);
                                recipe.removeMatrix(ingredients, inventory, possible, craftingData);
                            }
                            event.setCurrentItem(new ItemStack(Material.AIR));
                            Random rd = new Random();
                            for (int i = 0; i < possible; i++) {
                                event.getView().getBottomInventory().addItem(resultItem);
                                resultItem = !results.isEmpty() ? results.get(rd.nextInt(customCraftEvent.getResult().size())).create() : new ItemStack(Material.AIR);
                            }
                        }
                    } else if (event.getClick().equals(ClickType.LEFT) || event.getClick().equals(ClickType.RIGHT)) {
                        //api.sendDebugMessage("ONE-CLICK!");
                        ItemStack cursor = event.getView().getCursor();
                        if (ItemUtils.isAirOrNull(cursor) || cursor.getAmount() < cursor.getMaxStackSize()) {
                            HashMap<NamespacedKey, CustomItem> precraftedItem = getPrecraftedItems().getOrDefault(player.getUniqueId(), new HashMap<>());
                            recipe.removeMatrix(ingredients, inventory, 1, craftingData);
                            if (resultItem.isSimilar(player.getItemOnCursor()) || player.getItemOnCursor().isSimilar(resultItem)) {
                                event.getView().getCursor().setAmount(event.getView().getCursor().getAmount() + resultItem.getAmount());
                            } else if (event.getView().getCursor() == null || event.getView().getCursor().getType().equals(Material.AIR)) {
                                event.getView().setCursor(resultItem);
                            }
                            precraftedItem.put(recipe.getNamespacedKey(), null);
                            getPrecraftedItems().put(player.getUniqueId(), precraftedItem);
                            getPreCraftedRecipes().put(event.getWhoClicked().getUniqueId(), null);
                        }
                    }
                }
            }
            preCraftedRecipes.put(event.getWhoClicked().getUniqueId(), null);
        }
    }

    public HashMap<UUID, HashMap<NamespacedKey, CustomItem>> getPrecraftedItems() {
        return precraftedItems;
    }

    public HashMap<UUID, CraftingData> getPreCraftedRecipes() {
        return preCraftedRecipes;
    }
}