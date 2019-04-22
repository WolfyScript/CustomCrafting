package me.wolfyscript.customcrafting.events;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.MainConfig;
import me.wolfyscript.customcrafting.data.PlayerCache;
import me.wolfyscript.customcrafting.events.customevents.CustomCraftEvent;
import me.wolfyscript.customcrafting.events.customevents.CustomPreCraftEvent;
import me.wolfyscript.customcrafting.handlers.RecipeHandler;
import me.wolfyscript.customcrafting.items.ItemUtils;
import me.wolfyscript.customcrafting.recipes.craftrecipes.CraftingRecipe;
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
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class CraftEvents implements Listener {

    private WolfyUtilities api;

    private MainConfig config = CustomCrafting.getConfigHandler().getConfig();

    private HashMap<UUID, String> precraftedRecipes = new HashMap<>();

    public CraftEvents(WolfyUtilities api) {
        this.api = api;
    }

    @EventHandler
    public void onAdvancedWorkbench(CustomPreCraftEvent event){
        if(!event.isCancelled() && event.getRecipe().getID().equals("customcrafting:workbench")){
            if(CustomCrafting.getConfigHandler().getConfig().isAdvancedWorkbenchEnabled()){
                String name = api.getLanguageAPI().getActiveLanguage().replaceKeys("$crafting.workbench.name$");
                List<String> lore = api.getLanguageAPI().getActiveLanguage().replaceKey("crafting.workbench.lore");
                lore.add("§c§c§_§w§o§r§k§b§e§n§c§h");
                ItemStack itemStack = event.getRecipe().getCustomResult().clone();
                ItemMeta itemMeta = itemStack.getItemMeta();
                itemMeta.setDisplayName(name);
                itemMeta.setLore(lore);
                itemStack.setItemMeta(itemMeta);
                event.setResult(itemStack);
            }else{
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDiscover(PlayerRecipeDiscoverEvent event) {
        if (CustomCrafting.getConfigHandler().getConfig().getDisabledRecipes().contains(event.getRecipe().toString())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onCraft(CraftItemEvent event) {
        if (event.getRecipe() instanceof Keyed) {
            ItemStack result = event.getInventory().getResult();
            if (result != null && !result.getType().equals(Material.AIR) && precraftedRecipes.containsKey(event.getWhoClicked().getUniqueId()) && precraftedRecipes.get(event.getWhoClicked().getUniqueId()) != null) {
                String key = precraftedRecipes.get(event.getWhoClicked().getUniqueId());
                CraftingRecipe recipe = CustomCrafting.getRecipeHandler().getCraftingRecipe(key);
                ItemStack[] matrix = event.getInventory().getMatrix();
                boolean small = matrix.length < 9;
                RecipeHandler recipeHandler = CustomCrafting.getRecipeHandler();
                List<List<ItemStack>> ingredients = recipeHandler.getIngredients(matrix);
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
                            api.sendDebugMessage("SHIFT-CLICK!");
                            //Check if player has space left in his inventory
                            int possible = ItemUtils.getInventorySpace(player, resultItem) / resultItem.getAmount() - 1;
                            api.sendDebugMessage("Inv space: " + possible);
                            api.sendDebugMessage("Possible: " + amount);
                            if (possible > amount) {
                                possible = amount;
                            }
                            api.sendDebugMessage("Possible crafts: " + possible);
                            //Remove the amount possible and give the specific items to the player
                            if (possible > 0) {
                                event.getInventory().setMatrix(recipe.removeIngredients(ingredients, matrix, small, possible).getMatrix());
                            }
                            for (int i = 0; i < possible - 1; i++) {
                                player.getInventory().addItem(resultItem);
                            }
                            event.setCurrentItem(resultItem);
                        } else {
                            api.sendDebugMessage("ONE-CLICK!");
                            event.getInventory().setMatrix(recipe.removeIngredients(ingredients, matrix, small, 1).getMatrix());
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
        Player player = (Player) e.getView().getPlayer();
        if (e.getRecipe() != null) {
            if (e.getRecipe() instanceof Keyed) {
                try {
                    api.sendDebugMessage("Detected recipe: " + ((Keyed) e.getRecipe()).getKey().toString());
                    ItemStack[] matrix = e.getInventory().getMatrix();
                    RecipeHandler recipeHandler = CustomCrafting.getRecipeHandler();
                    List<List<ItemStack>> ingredients = recipeHandler.getIngredients(matrix);
                    List<CraftingRecipe> recipesToCheck = new ArrayList<>(recipeHandler.getSimilarRecipes(ingredients));
                    api.sendDebugMessage("Similar Recipes:");
                    recipesToCheck.forEach(craftingRecipe -> api.sendDebugMessage(" - " + craftingRecipe.getID()));
                    boolean allow = false;
                    if (!recipesToCheck.isEmpty()) {
                        CustomPreCraftEvent customPreCraftEvent;
                        for (CraftingRecipe recipe : recipesToCheck) {
                            if (recipe != null && !recipeHandler.getDisabledRecipes().contains(recipe.getID())) {
                                api.sendDebugMessage("check recipe: " + recipe.getID());
                                customPreCraftEvent = new CustomPreCraftEvent(e.isRepair(), recipe, e.getRecipe(), e.getInventory(), ingredients);
                                boolean perm = checkWorkbenchAndPerm(player, e.getView().getPlayer().getTargetBlock(null, 5).getLocation(), recipe);
                                boolean check = recipe.check(ingredients);
                                api.sendDebugMessage(" " + perm);
                                api.sendDebugMessage(" " + check);
                                if (!(perm && check) || recipeHandler.getDisabledRecipes().contains(recipe.getID())) {
                                    customPreCraftEvent.setCancelled(true);
                                }
                                Bukkit.getPluginManager().callEvent(customPreCraftEvent);
                                if (!customPreCraftEvent.isCancelled()) {
                                    //ALLOW
                                    precraftedRecipes.put(player.getUniqueId(), customPreCraftEvent.getRecipe().getID());
                                    e.getInventory().setResult(customPreCraftEvent.getResult());
                                    allow = true;
                                    break;
                                }
                            }
                        }
                    }
                    if (!allow) {
                        api.sendDebugMessage("No recipes allowed!");
                        precraftedRecipes.remove(player.getUniqueId());
                        CraftingRecipe recipe = recipeHandler.getCraftingRecipe(((Keyed) e.getRecipe()).getKey().toString());
                        api.sendDebugMessage("  detected recipe: " + ((Keyed) e.getRecipe()).getKey().toString());
                        api.sendDebugMessage("  custom recipe: " + recipe);
                        if (recipeHandler.getDisabledRecipes().contains(((Keyed) e.getRecipe()).getKey().toString())) {
                            api.sendDebugMessage("      -> Disabled!");
                            e.getInventory().setResult(new ItemStack(Material.AIR));
                        } else if (recipe == null) {
                            api.sendDebugMessage("  -> Enabled -> Default output!");
                        } else {
                            e.getInventory().setResult(new ItemStack(Material.AIR));
                        }
                    }
                } catch (Exception ex) {
                    System.out.println("WHAT HAPPENED? Please report!");
                    ex.printStackTrace();
                    System.out.println("WHAT HAPPENED? Please report!");
                    precraftedRecipes.remove(player.getUniqueId());
                    e.getInventory().setResult(new ItemStack(Material.AIR));
                }
            }
        }
    }

    private boolean checkWorkbenchAndPerm(Player player, Location location, CraftingRecipe recipe) {
        if (!recipe.needsAdvancedWorkbench() || (location != null && CustomCrafting.getWorkbenches().isWorkbench(location))) {
            String perm = "customcrafting.craft."+recipe.getID();
            String perm2 = "customcrafting.craft."+recipe.getID().split(":")[0];

            if(recipe.needsPermission()){
                if(!player.hasPermission("customcrafting.craft.*")){
                    if(!player.hasPermission(perm)){
                        if(!player.hasPermission(perm2)){
                            return false;
                        }
                    }
                }
            }
            return true;
        }
        return false;
    }

}
