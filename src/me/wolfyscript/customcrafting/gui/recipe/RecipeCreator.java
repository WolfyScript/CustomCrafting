package me.wolfyscript.customcrafting.gui.recipe;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.gui.PlayerCache;
import me.wolfyscript.customcrafting.gui.Setting;
import me.wolfyscript.customcrafting.items.ItemUtils;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class RecipeCreator extends GuiWindow {


    public RecipeCreator(InventoryAPI inventoryAPI) {
        super("recipe_creator", inventoryAPI, 54);
    }

    @Override
    public void onInit() {
        createItem("save", Material.WRITABLE_BOOK);
        createItem("permissions_on", Material.GREEN_CONCRETE);
        createItem("permissions_off", Material.RED_CONCRETE);
        createItem("adv_workbench_on", Material.GREEN_CONCRETE);
        createItem("adv_workbench_off", Material.RED_CONCRETE);
        createItem("shapeless_off", WolfyUtilities.getSkullByValue("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWFhZTdlODIyMmRkYmVlMTlkMTg0Yjk3ZTc5MDY3ODE0YjZiYTMxNDJhM2JkY2NlOGI5MzA5OWEzMTIifX19"));
        createItem("shapeless_on", WolfyUtilities.getSkullByValue("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjIxZDkzZGE0Mzg2M2NiMzc1OWFmZWZhOWY3Y2M1YzgxZjM0ZDkyMGNhOTdiNzI4M2I0NjJmOGIxOTdmODEzIn19fQ=="));
    }

    @EventHandler
    public void onUpdate(GuiUpdateEvent event){
        if(event.verify(this)){
            PlayerCache cache = CustomCrafting.getPlayerCache(event.getPlayer());
            switch (cache.getSetting()){
                case CRAFT_RECIPE:
                    if(!cache.getCraftIngredients().isEmpty()){
                        int slot;
                        for(int i = 0; i < 9 ; i++){
                            slot = 19 + i + (i/3)*6;
                            event.setItem(slot, cache.getCraftIngredients().isEmpty() ? new ItemStack(Material.AIR) : cache.getCraftIngredients().get(i));
                        }
                    }
                    event.setItem(31, cache.getShape() ? "shapeless_off" : "shapeless_on");
                    event.setItem(33, cache.getCraftResult());
                    event.setItem(3, cache.getPermission() ? "permissions_on" : "permissions_off");
                    event.setItem(5, cache.getWorkbench() ? "adv_workbench_on" : "adv_workbench_off");
                    break;
                case FURNACE_RECIPE:
                    event.setItem(29, cache.getFurnaceSource());
                    event.setItem(33, cache.getFurnaceResult());
                    break;
            }
            event.setItem(53, "save");
        }
    }

    @Override
    public boolean onAction(GuiAction guiAction) {
        String action = guiAction.getAction();
        PlayerCache cache = CustomCrafting.getPlayerCache(guiAction.getPlayer());
        if(action.equals("back")){
            guiAction.getGuiHandler().openLastInv();
        }else{
            switch (cache.getSetting()){
                case CRAFT_RECIPE:
                    if(action.startsWith("shapeless_")){
                        cache.setShape(!cache.getShape());
                    }else if(action.startsWith("permissions_")){
                        cache.setPermission(!cache.getPermission());
                    }else if(action.startsWith("adv_workbench_")){
                        cache.setWorkbench(!cache.getWorkbench());
                    }
                    break;
                case FURNACE_RECIPE:

                    break;
            }
        }
        update(guiAction.getGuiHandler());
        return true;
    }

    @Override
    public boolean onClick(GuiClick guiClick) {
        int slot = guiClick.getClickedSlot();
        Player player = guiClick.getPlayer();
        PlayerCache playerCache = CustomCrafting.getPlayerCache(player);
        if(playerCache.getSetting().equals(Setting.CRAFT_RECIPE)){
            updateCraftInv(player.getOpenInventory().getTopInventory(), playerCache);
            if(guiClick.getClickedInventory().equals(player.getOpenInventory().getTopInventory())){
                if(guiClick.getClickType().equals(ClickType.SHIFT_RIGHT) && !guiClick.getCurrentItem().getType().equals(Material.AIR)){
                    if((slot >= 19 && slot < 22) || (slot >= 28 && slot < 31) || (slot >= 37 && slot < 40)){
                        int craftSlot = (slot-19) - 6*(((slot-19)/4)/2);
                        String id = ItemUtils.getCustomItemID(playerCache.getCraftIngredients().get(craftSlot));
                        if(id.isEmpty()){
                            playerCache.setItemTag("ingredient:"+craftSlot+";not_saved;null");
                        }else{
                            playerCache.setItemTag("ingredient:"+craftSlot+";saved;"+id);
                        }
                        guiClick.getGuiHandler().changeToInv("item_editor");
                        return true;
                    }else{
                        //TODO: CACHE Result Tag! AND USE IT!
                        String id = ItemUtils.getCustomItemID(playerCache.getCraftResult());
                        if(id.isEmpty()){
                            playerCache.setItemTag("result"+";not_saved;null");
                        }else{
                            playerCache.setItemTag("result"+";saved;"+id);
                        }
                        guiClick.getGuiHandler().changeToInv("item_editor");
                        return true;
                    }
                }
            }


        }


        return false;
    }

    private void updateCraftInv(Inventory inv, PlayerCache playerCache){
        Bukkit.getScheduler().runTaskLater(CustomCrafting.getInst(), ()->{
            List<ItemStack> ingredients = new ArrayList<>();
            int craftSlot;
            for(int i = 0; i < 9 ; i++){
                craftSlot = 19 + i + (i/3)*6;
                ItemStack itemStack = inv.getItem(craftSlot);
                ingredients.add(itemStack == null ? new ItemStack(Material.AIR) : itemStack);
            }
            playerCache.setCraftIngredients(ingredients);
            //System.out.println("Updated List: "+playerCache.getCraftIngredients());
            ItemStack itemStack = inv.getItem(33);
            playerCache.setCraftResult(itemStack == null ? new ItemStack(Material.AIR) : itemStack);
            //System.out.println("Updated Result: "+playerCache.getCraftResult());

        },1);
    }
}
