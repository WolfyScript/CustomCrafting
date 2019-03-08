package me.wolfyscript.customcrafting.gui;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.GuiAction;
import me.wolfyscript.utilities.api.inventory.GuiClick;
import me.wolfyscript.utilities.api.inventory.GuiUpdateEvent;
import me.wolfyscript.utilities.api.inventory.InventoryAPI;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class RecipesList extends ExtendedGuiWindow {

    private HashMap<UUID, Integer> pages = new HashMap<>();

    public RecipesList(InventoryAPI inventoryAPI) {
        super("recipes_list", inventoryAPI, 54);
    }

    @Override
    public void onInit() {
        createItem("next_page", WolfyUtilities.getCustomHead(""));
        createItem("previous_page", WolfyUtilities.getCustomHead(""));
    }

    @EventHandler
    public void onUpdate(GuiUpdateEvent event) {
        if(event.verify(this)){
            event.setItem(47, "previous_page");
            event.setItem(51, "next_page");
            List<Recipe> recipes = CustomCrafting.getRecipeHandler().getAllRecipes();
            if(!pages.containsKey(event.getPlayer().getUniqueId())){
                pages.put(event.getPlayer().getUniqueId(), 0);
            }
            int item = -1;
            for(int i = 35 * pages.get(event.getPlayer().getUniqueId()); item < 35 && i < recipes.size(); i++){
                item++;
                Recipe recipe = recipes.get(i);
                if(recipe instanceof Keyed){
                    ItemStack itemStack = recipe.getResult().clone();
                    if(itemStack != null){
                        if(itemStack.getType().equals(Material.AIR)){
                            itemStack = new ItemStack(Material.STONE);
                            itemStack.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
                            ItemMeta itemMeta = itemStack.getItemMeta();
                            itemMeta.setDisplayName("§r§7"+((Keyed) recipe).getKey().toString());
                            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                            itemStack.setItemMeta(itemMeta);
                        }
                        ItemMeta itemMeta = itemStack.getItemMeta();
                        List<String> lore = itemMeta.hasLore() ? itemMeta.getLore() : new ArrayList<>();
                        lore.add(WolfyUtilities.hideString(((Keyed) recipe).getKey().toString()));
                        if(CustomCrafting.getRecipeHandler().getDisabledRecipes().contains(((Keyed) recipe).getKey().toString())){
                            lore.add("§7§l[§c§lDISABLED§7§l]");
                        }else{
                            lore.add("§7§l[§a§lENABLED§7§l]");
                        }
                        itemMeta.setLore(lore);
                        itemStack.setItemMeta(itemMeta);
                        event.setItem(9+item, itemStack);
                    }
                }
            }
        }
    }

    @Override
    public boolean onAction(GuiAction guiAction) {
        Player player = guiAction.getPlayer();
        UUID uuid = player.getUniqueId();
        if(!pages.containsKey(player.getUniqueId())){
            pages.put(uuid, 0);
        }
        if(guiAction.getAction().equals("previous_page")){
            if(pages.get(uuid) > 0){
                pages.put(uuid, pages.get(uuid)-1);
            }
        }else if(guiAction.getAction().equals("next_page")){
            pages.put(uuid, pages.get(uuid)+1);
        }
        return false;
    }

    @Override
    public boolean onClick(GuiClick guiClick) {
        ItemStack itemStack = guiClick.getCurrentItem();
        if(itemStack.hasItemMeta() && itemStack.getItemMeta().hasLore()){
            String id = WolfyUtilities.unhideString(itemStack.getItemMeta().getLore().get(itemStack.getItemMeta().getLore().size()-2));
            if(!id.isEmpty() && id.contains(":")){
                if(CustomCrafting.getRecipeHandler().getDisabledRecipes().contains(id)){
                    CustomCrafting.getRecipeHandler().getDisabledRecipes().remove(id);
                }else{
                    CustomCrafting.getRecipeHandler().getDisabledRecipes().add(id);
                    for(Player player : Bukkit.getOnlinePlayers()){
                        player.undiscoverRecipe(new NamespacedKey(id.split(":")[0], id.split(":")[1]));
                    }
                }
            }
        }
        update(guiClick.getGuiHandler());
        return true;
    }
}
