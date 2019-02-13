package me.wolfyscript.customcrafting.gui.items;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.gui.PlayerCache;
import me.wolfyscript.customcrafting.gui.Setting;
import me.wolfyscript.customcrafting.items.CustomItem;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.*;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;

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
            event.setItem(20, "create_item");
            event.setItem(22, "edit_item");
            event.setItem(24, "delete_item");
            event.setItem(31, "load_item");

        }
    }

    @Override
    public boolean onAction(GuiAction guiAction) {
        String action = guiAction.getAction();
        if(action.equals("back")){
            guiAction.getGuiHandler().openLastInv();
        }else{
            PlayerCache cache = CustomCrafting.getPlayerCache(guiAction.getPlayer());
            switch (action){
                case "edit_item":
                    if(cache.getItemTag().equals("result")){
                        cache.setCustomItem(cache.getCraftResult());
                        guiAction.getGuiHandler().changeToInv("item_creator");
                        break;
                    }else if(cache.getItemTag().startsWith("ingredient:")){
                        int slot = Integer.parseInt(cache.getItemTag().split(":")[1]);
                        cache.setCustomItem(cache.getCraftIngredients().get(slot));
                        guiAction.getGuiHandler().changeToInv("item_creator");
                        break;
                    }
                case "delete_item":
                case "load_item":
                    runChat(0, "&3Type in the folder and name of the item! &6e.g. example your_item", guiAction.getGuiHandler());
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
        if(id == 0){
            String[] args = message.split(" ");
            if(args.length > 1){
                CustomItem customItem = CustomCrafting.getRecipeHandler().getCustomItem(args[0], args[1]);
                if(customItem == null){
                    api.sendPlayerMessage(guiHandler.getPlayer(), "&cThis item does not exist or is not loaded!");
                    return true;
                }

            }else{
                api.sendPlayerMessage(guiHandler.getPlayer(), "&cPlease type in the item name!");
                return true;
            }
        }
        return false;
    }
}
