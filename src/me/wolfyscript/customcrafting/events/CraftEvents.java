package me.wolfyscript.customcrafting.events;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.MainConfig;
import me.wolfyscript.customcrafting.data.PlayerCache;
import me.wolfyscript.customcrafting.events.customevents.CustomCraftEvent;
import me.wolfyscript.customcrafting.events.customevents.CustomPreCraftEvent;
import me.wolfyscript.customcrafting.handlers.RecipeHandler;
import me.wolfyscript.customcrafting.items.ItemUtils;
import me.wolfyscript.customcrafting.recipes.*;
import me.wolfyscript.utilities.api.WolfyUtilities;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerRecipeDiscoverEvent;
import org.bukkit.inventory.*;

import java.util.*;
import java.util.function.Consumer;

public class CraftEvents implements Listener {

    private WolfyUtilities api;

    private MainConfig config = CustomCrafting.getConfigHandler().getConfig();

    private HashMap<UUID, String> precraftedRecipes = new HashMap<>();

    public CraftEvents(WolfyUtilities api) {
        this.api = api;
    }

    @EventHandler
    public void onDiscover(PlayerRecipeDiscoverEvent event) {
        if (CustomCrafting.getConfigHandler().getConfig().getDisabledRecipes().contains(event.getRecipe().toString())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onCraft(CraftItemEvent event) {
        if (event.getRecipe() != null && event.getRecipe() instanceof Keyed) {
            ItemStack result = event.getInventory().getResult();
            if (result != null && !result.getType().equals(Material.AIR) && precraftedRecipes.containsKey(event.getWhoClicked().getUniqueId()) && precraftedRecipes.get(event.getWhoClicked().getUniqueId()) != null) {
                String key = precraftedRecipes.get(event.getWhoClicked().getUniqueId());
                CraftingRecipe recipe = CustomCrafting.getRecipeHandler().getCraftingRecipe(key);
                ItemStack[] matrix = event.getInventory().getMatrix();
                List<List<ItemStack>> ingredients = RecipeHandler.getIngredients(matrix);
                if (recipe != null) {
                    Player player = (Player) event.getWhoClicked();
                    Block block = player.getTargetBlock(null, 5);
                    PlayerCache cache = CustomCrafting.getPlayerCache(player);
                    CustomCraftEvent customCraftEvent = new CustomCraftEvent(recipe, event.getRecipe(), event.getInventory());
                    if (!customCraftEvent.isCancelled()) {
                        if (config.getCommandsSuccessCrafted() != null && !config.getCommandsSuccessCrafted().isEmpty()) {
                            for (String command : config.getCommandsSuccessCrafted()) {
                                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command.replace("%P%", player.getName()).replace("%UUID%", player.getUniqueId().toString()).replace("%REC%", recipe.getID()));
                            }
                        }
                        cache.addRecipeCrafts(customCraftEvent.getRecipe().getID());
                        cache.addAmountCrafted(1);
                        if (CustomCrafting.getWorkbenches().isWorkbench(block.getLocation())) {
                            cache.addAmountAdvancedCrafted(1);
                        } else {
                            cache.addAmountNormalCrafted(1);
                        }
                        int amount = recipe.getAmountCraftable(ingredients);
                        ItemStack resultItem = recipe.getCustomResult().clone();
                        if (event.getClick().equals(ClickType.SHIFT_RIGHT) || event.getClick().equals(ClickType.SHIFT_LEFT)) {
                            //Check if player has space left in his inventory
                            int possible = ItemUtils.getInventorySpace(player, resultItem) / resultItem.getAmount() - 1;
                            if(possible > amount){
                                possible = amount - 1;
                            }
                            //Remove the amount possible and give the specific items to the player
                            if(possible > 1){
                                event.getInventory().setMatrix(recipe.removeIngredients(ingredients, possible).getMatrix());
                            }
                            for(int i = 0; i < possible-1; i++){
                                player.getInventory().addItem(resultItem);
                            }
                            event.setCurrentItem(resultItem);
                        } else {
                            ItemStack[] matrixRes = recipe.removeIngredients(ingredients, 1).getMatrix();
                            //TODO: LIST TO MATRIX
                            //ItemStack[] resultMatrix =
                            /*
                            for(int i = 0; i < 3; i++){
                                for(int j = 0; j < 3; j++){

                                }
                            }
                            */
                            event.getInventory().setMatrix(matrixRes);
                            event.setCurrentItem(resultItem);
                        }
                    }
                }
            }
        }
        precraftedRecipes.put(event.getWhoClicked().getUniqueId(), null);
    }

    @EventHandler
    public void onPreCraft(PrepareItemCraftEvent e) {
        if (e.getRecipe() != null) {
            if (e.getRecipe() instanceof Keyed) {
                try {
                    //TODO! SHAPE CHECKER!!!
                    //TODO TEST IF IT WORKS!
                    ItemStack[] matrix = e.getInventory().getMatrix();
                    List<List<ItemStack>> ingredients = RecipeHandler.getIngredients(matrix);
                    RecipeHandler recipeHandler = CustomCrafting.getRecipeHandler();
                    Player player = (Player) e.getView().getPlayer();
                    List<CraftingRecipe> recipesToCheck = new ArrayList<>(recipeHandler.getSimilarRecipes(ingredients));
                    System.out.println("Recipes:");
                    recipesToCheck.forEach(craftingRecipe -> System.out.println(" - "+craftingRecipe.getID()));
                    boolean allow = false;
                    if (!recipesToCheck.isEmpty()) {
                        CustomPreCraftEvent customPreCraftEvent;
                        for (CraftingRecipe recipe : recipesToCheck) {
                            if (recipe != null && !recipeHandler.getDisabledRecipes().contains(recipe.getID())) {
                                System.out.println("check recipe: "+recipe.getID());
                                customPreCraftEvent = new CustomPreCraftEvent(e.isRepair(), recipe, e.getRecipe(), e.getInventory());
                                boolean perm = checkWorkbenchAndPerm(player, e.getView().getPlayer().getTargetBlock(null, 5).getLocation(), recipe);
                                boolean check = recipe.check(ingredients);
                                System.out.println(" "+perm);
                                System.out.println(" "+check);
                                if (!(perm && check) || recipeHandler.getDisabledRecipes().contains(recipe.getID())) {
                                    customPreCraftEvent.setCancelled(true);
                                }
                                Bukkit.getPluginManager().callEvent(customPreCraftEvent);
                                if(!customPreCraftEvent.isCancelled()){
                                    //ALLOW
                                    precraftedRecipes.put(player.getUniqueId(), customPreCraftEvent.getRecipe().getID());
                                    e.getInventory().setResult(customPreCraftEvent.getResult());
                                    allow = true;
                                    break;
                                }else{
                                    //DENIED
                                    e.getInventory().setResult(new ItemStack(Material.AIR));
                                }
                            }
                        }
                    }
                    if(!allow){
                        CraftingRecipe recipe = recipeHandler.getCraftingRecipe(((Keyed) e.getRecipe()).getKey().toString());
                        if(recipe == null && !recipeHandler.getDisabledRecipes().contains(((Keyed) e.getRecipe()).getKey().toString())){
                            e.getInventory().setResult(e.getRecipe().getResult());
                        }
                    }
                } catch (Exception ex) {
                    System.out.println("WHAT HAPPENED? Please report!");
                    ex.printStackTrace();
                    System.out.println("WHAT HAPPENED? Please report!");
                    e.getInventory().setResult(new ItemStack(Material.AIR));
                }
            }
        }
    }

    private boolean checkWorkbenchAndPerm(Player player, Location location, CraftingRecipe recipe) {
        if (!recipe.needsAdvancedWorkbench() || (location != null && CustomCrafting.getWorkbenches().isWorkbench(location))) {
            return !recipe.needsPermission() || (player.hasPermission("customcrafting.craft.*") || player.hasPermission("customcrafting.craft." + recipe.getID()) || player.hasPermission("customcrafting.craft." + recipe.getID().split(":")[0]));
        }
        return false;
    }

}
