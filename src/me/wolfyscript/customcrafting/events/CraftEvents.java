package me.wolfyscript.customcrafting.events;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.MainConfig;
import me.wolfyscript.customcrafting.data.PlayerCache;
import me.wolfyscript.customcrafting.events.customevents.CustomCraftEvent;
import me.wolfyscript.customcrafting.events.customevents.CustomPreCraftEvent;
import me.wolfyscript.customcrafting.recipes.*;
import me.wolfyscript.utilities.api.WolfyUtilities;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerRecipeDiscoverEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

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
                precraftedRecipes.put(event.getWhoClicked().getUniqueId(), null);
                CraftingRecipe recipe = CustomCrafting.getRecipeHandler().getCraftingRecipe(key);
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
                        cache.addAmountCrafted(1);
                        if (CustomCrafting.getWorkbenches().isWorkbench(block.getLocation())) {
                            cache.addAmountAdvancedCrafted(1);
                        } else {
                            cache.addAmountNormalCrafted(1);
                        }
                        int amount = recipe.getAmountCraftable(event.getInventory().getMatrix());
                        ItemStack resultItem = recipe.getResult().clone();
                        if (event.getClick().equals(ClickType.SHIFT_RIGHT) || event.getClick().equals(ClickType.SHIFT_LEFT)) {
                            resultItem.setAmount(resultItem.getAmount() * amount);
                            event.getInventory().setMatrix(recipe.removeIngredients(event.getInventory().getMatrix(), amount).getMatrix());
                            event.setCurrentItem(resultItem);
                        } else {
                            event.getInventory().setMatrix(recipe.removeIngredients(event.getInventory().getMatrix(), 1).getMatrix());
                            event.setCurrentItem(resultItem);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPreCraft(PrepareItemCraftEvent e) {
        if (e.getRecipe() != null) {
            if (e.getRecipe() instanceof Keyed) {
                try {
                    Player player = (Player) e.getView().getPlayer();
                    final String key = ((Keyed) e.getRecipe()).getKey().toString();
                    List<CraftingRecipe> recipesToCheck = new ArrayList<>();
                    boolean disabled = false;
                    if (CustomCrafting.getRecipeHandler().getDisabledRecipes().contains(key)) {
                        disabled = true;
                        e.getInventory().setResult(new ItemStack(Material.AIR));
                    } else {
                        if (CustomCrafting.getRecipeHandler().getCraftingRecipe(key) != null)
                            recipesToCheck.add(CustomCrafting.getRecipeHandler().getCraftingRecipe(key));
                    }
                    if (CustomCrafting.getRecipeHandler().getExtendRecipes().containsKey(key)) {
                        for (String recipeKey : CustomCrafting.getRecipeHandler().getExtendRecipes().get(key)) {
                            CraftingRecipe craftingRecipe = CustomCrafting.getRecipeHandler().getCraftingRecipe(recipeKey);
                            if (craftingRecipe != null) {
                                recipesToCheck.add(craftingRecipe);
                            }
                        }
                    }
                    if (CustomCrafting.getRecipeHandler().getOverrideRecipes().containsKey(key)) {
                        recipesToCheck.clear();
                        for (String recipeKey : CustomCrafting.getRecipeHandler().getOverrideRecipes().get(key)) {
                            CraftingRecipe craftingRecipe = CustomCrafting.getRecipeHandler().getCraftingRecipe(recipeKey);
                            if (craftingRecipe != null) {
                                recipesToCheck.add(craftingRecipe);
                            }
                        }
                    }
                    if (!recipesToCheck.isEmpty()) {
                        CustomPreCraftEvent customPreCraftEvent = null;
                        for (CraftingRecipe recipe : recipesToCheck) {
                            if (recipe != null) {
                                if (recipe.check(e.getInventory().getMatrix()) && checkWorkbenchAndPerm(player, e.getView().getPlayer().getTargetBlock(null, 5).getLocation(), recipe)) {
                                    customPreCraftEvent = new CustomPreCraftEvent(e.isRepair(), recipe, e.getRecipe(), e.getInventory());
                                    Bukkit.getPluginManager().callEvent(customPreCraftEvent);
                                    break;
                                }
                            }
                        }
                        if (customPreCraftEvent != null && !customPreCraftEvent.isCancelled()) {
                            //ALLOW
                            precraftedRecipes.put(player.getUniqueId(), customPreCraftEvent.getRecipe().getID());
                            e.getInventory().setResult(customPreCraftEvent.getResult());
                        } else {
                            e.getInventory().setResult(new ItemStack(Material.AIR));
                        }
                    } else {
                        if (disabled) {
                            e.getInventory().setResult(new ItemStack(Material.AIR));
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
        CustomCrafting.getWorkbenches().setContents(e.getView().getPlayer().getTargetBlock(null, 5).getLocation(), e.getInventory().getMatrix());
    }

    private boolean checkWorkbenchAndPerm(Player player, Location location, CraftingRecipe recipe) {
        if (!recipe.needsAdvancedWorkbench() || (location != null && CustomCrafting.getWorkbenches().isWorkbench(location))) {
            return !recipe.needsPermission() || (player.hasPermission("customcrafting.craft.*") || player.hasPermission("customcrafting.craft." + recipe.getID()) || player.hasPermission("customcrafting.craft." + recipe.getID().split(":")[0]));
        }
        return false;
    }

    private CraftingRecipe getAllowedRecipe(List<CustomRecipe> group, ItemStack[] matrix) {
        if (!group.isEmpty()) {
            for (CustomRecipe groupRecipe : group) {
                if (groupRecipe instanceof CraftingRecipe && ((CraftingRecipe) groupRecipe).check(matrix)) {
                    return (CraftingRecipe) groupRecipe;
                }
            }
        }
        return null;
    }


}
