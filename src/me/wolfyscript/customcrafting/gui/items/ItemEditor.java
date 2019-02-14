package me.wolfyscript.customcrafting.gui.items;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.gui.PlayerCache;
import me.wolfyscript.customcrafting.items.CustomItem;
import me.wolfyscript.customcrafting.items.ItemUtils;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

public class ItemEditor extends GuiWindow {

    private WolfyUtilities api = CustomCrafting.getApi();

    public ItemEditor(InventoryAPI inventoryAPI) {
        super("item_editor", inventoryAPI, 54);
    }

    @Override
    public void onInit() {
        createItem("load_item", Material.ITEM_FRAME);
        createItem("create_item", Material.ITEM_FRAME);
        createItem("edit_item", Material.REDSTONE);
        createItem("delete_item", Material.BARRIER);
    }

    @EventHandler
    public void onUpdate(GuiUpdateEvent event) {
        if (event.verify(this)) {
            PlayerCache cache = CustomCrafting.getPlayerCache(event.getPlayer());

            if(!cache.getItemTag(0).equals("items")){
                event.setItem(20, "load_item");
                event.setItem(22, "create_item");
                event.setItem(24, "edit_item");


            }else{
                event.setItem(20, "create_item");
                event.setItem(22, "edit_item");
                event.setItem(24, "delete_item");
                event.setItem(31, "load_item");
            }
        }
    }

    @Override
    public boolean onAction(GuiAction guiAction) {
        String action = guiAction.getAction();
        if (action.equals("back")) {
            guiAction.getGuiHandler().openLastInv();
        } else {
            PlayerCache cache = CustomCrafting.getPlayerCache(guiAction.getPlayer());
            switch (action) {
                case "edit_item":
                    if(!cache.getItemTag().isEmpty()){
                        if(cache.getItemTag(1).equals("saved")){
                            cache.setCustomItem(CustomCrafting.getRecipeHandler().getCustomItem(cache.getItemTag(2)));
                        }else{
                            if (cache.getItemTag().equals("result")) {
                                cache.setCustomItem(cache.getCraftResult());
                                guiAction.getGuiHandler().changeToInv("item_creator");
                            } else if (cache.getItemTag().startsWith("ingredient:")) {
                                int slot = Integer.parseInt(cache.getItemTag(0).split(":")[1]);
                                cache.setCustomItem(cache.getCraftIngredients().get(slot));
                                guiAction.getGuiHandler().changeToInv("item_creator");
                            }
                        }
                    }else{
                        runChat(2, "&3Type in the folder and name of the item! &6e.g. example your_item", guiAction.getGuiHandler());
                    }
                    break;
                case "delete_item":
                    runChat(0, "&3Type in the folder and name of the item! &6e.g. example your_item", guiAction.getGuiHandler());
                    break;
                case "load_item":
                    runChat(1, "&3Type in the folder and name of the item! &6e.g. example your_item", guiAction.getGuiHandler());
                    break;
                case "create_item":
                    guiAction.getGuiHandler().changeToInv("item_creator");
                    break;
            }
        }
        return true;
    }

    @Override
    public boolean parseChatMessage(int id, String message, GuiHandler guiHandler) {
        String[] args = message.split(" ");
        Player player = guiHandler.getPlayer();
        PlayerCache cache = CustomCrafting.getPlayerCache(guiHandler.getPlayer());
        if (args.length > 1) {
            CustomItem customItem = CustomCrafting.getRecipeHandler().getCustomItem(args[0], args[1]);
            if (customItem == null) {
                api.sendPlayerMessage(guiHandler.getPlayer(), "&cThis item does not exist or is not loaded!");
                return true;
            }
            switch (id){
                case 0:
                    CustomCrafting.getRecipeHandler().removeCustomItem(customItem);
                    customItem.getConfig().getConfigFile().deleteOnExit();
                    break;
                case 1:
                    if(cache.getItemTag(0).equals("items")){
                        player.getInventory().addItem(customItem);
                    }else{
                        ItemStack itemStack = cache.getCustomItem();
                        ItemUtils.applyItem(itemStack, cache);
                        guiHandler.changeToInv("recipe_creator");
                    }
                    break;
                case 2:
                    cache.setCustomItem(customItem);
                    cache.setItemTag("items;saved;"+customItem.getId());
                    api.sendPlayerMessage(guiHandler.getPlayer(), "&aThis item can now be edited!");
                    Bukkit.getScheduler().runTask(api.getPlugin(), ()->guiHandler.changeToInv("item_creator"));
                    break;
            }
        } else {
            api.sendPlayerMessage(guiHandler.getPlayer(), "&cPlease type in the item name!");
            return true;
        }
        return false;
    }
}
