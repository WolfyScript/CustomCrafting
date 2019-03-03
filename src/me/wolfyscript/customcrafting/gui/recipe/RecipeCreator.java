package me.wolfyscript.customcrafting.gui.recipe;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.custom_configs.CraftConfig;
import me.wolfyscript.customcrafting.configs.custom_configs.FurnaceConfig;
import me.wolfyscript.customcrafting.data.PlayerCache;
import me.wolfyscript.customcrafting.gui.ExtendedGuiWindow;
import me.wolfyscript.customcrafting.items.CustomItem;
import me.wolfyscript.customcrafting.items.ItemUtils;
import me.wolfyscript.customcrafting.recipes.CraftingRecipe;
import me.wolfyscript.customcrafting.recipes.ShapedCraftRecipe;
import me.wolfyscript.customcrafting.recipes.ShapelessCraftRecipe;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.*;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

public class RecipeCreator extends ExtendedGuiWindow {

    public RecipeCreator(InventoryAPI inventoryAPI) {
        super("recipe_creator", inventoryAPI, 54);
    }

    @Override
    public void onInit() {
        createItem("save", Material.WRITABLE_BOOK);

        createItem("permissions_on", Material.GREEN_CONCRETE);
        createItem("permissions_off", Material.RED_CONCRETE);

        createItem("workbench.adv_workbench_on", Material.GREEN_CONCRETE);
        createItem("workbench.adv_workbench_off", Material.RED_CONCRETE);
        createItem("workbench.shapeless_off", WolfyUtilities.getSkullByValue("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWFhZTdlODIyMmRkYmVlMTlkMTg0Yjk3ZTc5MDY3ODE0YjZiYTMxNDJhM2JkY2NlOGI5MzA5OWEzMTIifX19"));
        createItem("workbench.shapeless_on", WolfyUtilities.getSkullByValue("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjIxZDkzZGE0Mzg2M2NiMzc1OWFmZWZhOWY3Y2M1YzgxZjM0ZDkyMGNhOTdiNzI4M2I0NjJmOGIxOTdmODEzIn19fQ=="));

        createItem("furnace.adv_furnace_on", Material.GREEN_CONCRETE);
        createItem("furnace.adv_furnace_off", Material.RED_CONCRETE);
        createItem("furnace.xp", Material.EXPERIENCE_BOTTLE);
        createItem("furnace.cooking_time", Material.COAL);

    }

    @EventHandler
    public void onUpdate(GuiUpdateEvent event) {
        if (event.verify(this)) {
            PlayerCache cache = CustomCrafting.getPlayerCache(event.getPlayer());
            switch (cache.getSetting()) {
                case CRAFT_RECIPE:
                    if (!cache.getCraftIngredients().isEmpty()) {
                        int slot;
                        for (int i = 0; i < 9; i++) {
                            slot = 19 + i + (i / 3) * 6;
                            event.setItem(slot, cache.getCraftIngredients().isEmpty() ? new ItemStack(Material.AIR) : cache.getCraftIngredient(i).getIDItem());
                        }
                    }
                    event.setItem(31, cache.getShape() ? "workbench.shapeless_off" : "workbench.shapeless_on");
                    event.setItem(33, cache.getCraftResult().getIDItem());
                    event.setItem(3, cache.getPermission() ? "permissions_on" : "permissions_off");
                    event.setItem(5, cache.getWorkbench() ? "workbench.adv_workbench_on" : "workbench.adv_workbench_off");
                    break;
                case FURNACE_RECIPE:
                    event.setItem(11, "none", "glass_white");
                    event.setItem(19, "none", "glass_white");
                    event.setItem(20, cache.getFurnaceSource().getIDItem());
                    event.setItem(21, "none", "glass_white");
                    event.setItem(29, "none", "glass_white");


                    event.setItem(24, "none", "glass_white");
                    event.setItem(32, "none", "glass_white");
                    event.setItem(33, cache.getFurnaceResult().getIDItem());
                    event.setItem(34, "none", "glass_white");
                    event.setItem(42, "none", "glass_white");

                    event.setItem(37, "none", "glass_white");
                    event.setItem(39, "none", "glass_white");
                    event.setItem(47, "none", "glass_white");

                    ItemStack xp = event.getItem("furnace.xp");
                    ItemMeta xpMeta = xp.getItemMeta();
                    if(xpMeta.hasLore()){
                        List<String> lore = xpMeta.getLore();
                        for(int i = 0; i < lore.size(); i++){
                            lore.set(i, lore.get(i).replace("%XP%", String.valueOf(cache.getXP())));
                        }
                        xpMeta.setLore(lore);
                    }
                    xp.setItemMeta(xpMeta);
                    event.setItem(31, xp);

                    ItemStack time = event.getItem("furnace.cooking_time");
                    ItemMeta timeMeta = time.getItemMeta();
                    if(timeMeta.hasLore()){
                        List<String> lore = timeMeta.getLore();
                        for(int i = 0; i < lore.size(); i++){
                            lore.set(i, lore.get(i).replace("%TIME%", String.valueOf(cache.getCookingTime())));
                        }
                        timeMeta.setLore(lore);
                    }
                    time.setItemMeta(timeMeta);
                    event.setItem(38, time);
                    event.setItem(30, cache.needsAdvFurnace() ? "furnace.adv_furnace_on" : "furnace.adv_furnace_off");

                    break;
            }
            event.setItem(53, "save");
        }
    }

    @Override
    public boolean onAction(GuiAction guiAction) {
        String action = guiAction.getAction();
        PlayerCache cache = CustomCrafting.getPlayerCache(guiAction.getPlayer());
        updateInv(guiAction.getPlayer().getOpenInventory().getTopInventory(), cache);

        switch (cache.getSetting()) {
            case CRAFT_RECIPE:
                if (action.startsWith("workbench.shapeless_")) {
                    cache.setShape(!cache.getShape());
                } else if (action.startsWith("permissions_")) {
                    cache.setPermission(!cache.getPermission());
                } else if (action.startsWith("workbench.adv_workbench_")) {
                    cache.setWorkbench(!cache.getWorkbench());
                }
                break;
            case FURNACE_RECIPE:
                if (action.startsWith("furnace.adv_furnace_")) {
                    cache.setAdvancedFurnace(!cache.needsAdvFurnace());
                } else if (action.equals("furnace.xp")) {
                    runChat(1, "&3Type in the amount of experience. e.g. 0.5", guiAction.getGuiHandler());
                } else if (action.equals("furnace.cooking_time")) {
                    runChat(11, "&3Type in the cooking time in ticks. e.g. 20", guiAction.getGuiHandler());
                }
                break;

        }

        if (action.equals("back")) {
            guiAction.getGuiHandler().openLastInv();
        } else if (action.equals("save")) {
            if(validToSave(cache)){
                runChat(0, "&3Type in the name of the folder and item! &6e.g. example your_recipe", guiAction.getGuiHandler());
            }else{
                api.sendPlayerMessage(guiAction.getPlayer(), "§cCannot save a empty recipe! Check the Result and the Source/ingredients");
            }
        }

        update(guiAction.getGuiHandler());
        return true;
    }

    private boolean validToSave(PlayerCache cache){
        switch (cache.getSetting()){
            case CRAFT_RECIPE:
                if(!cache.getCraftIngredients().isEmpty() && !cache.getCraftResult().getType().equals(Material.AIR))
                    return true;
            case FURNACE_RECIPE:
                if(!cache.getFurnaceResult().getType().equals(Material.AIR) && !cache.getFurnaceSource().getType().equals(Material.AIR))
                    return true;
        }
        return false;
    }

    @Override
    public boolean onClick(GuiClick guiClick) {
        int slot = guiClick.getClickedSlot();
        Player player = guiClick.getPlayer();
        PlayerCache playerCache = CustomCrafting.getPlayerCache(player);
        updateInv(player.getOpenInventory().getTopInventory(), playerCache);

        if (guiClick.getClickedInventory().equals(player.getOpenInventory().getTopInventory())) {
            if (guiClick.getClickType().equals(ClickType.SHIFT_RIGHT) && !guiClick.getCurrentItem().getType().equals(Material.AIR)) {
                CustomItem customItem = ItemUtils.getCustomItem(guiClick.getCurrentItem());
                String id = customItem.getId();
                switch (playerCache.getSetting()) {
                    case CRAFT_RECIPE:
                        if ((slot >= 19 && slot < 22) || (slot >= 28 && slot < 31) || (slot >= 37 && slot < 40)) {
                            int craftSlot = (slot - 19) - 6 * (((slot - 19) / 4) / 2);
                            if (id.isEmpty() || id.equals("NULL")) {
                                playerCache.setItemTag("ingredient:" + craftSlot, "not_saved", "null");
                            } else {
                                playerCache.setItemTag("ingredient:" + craftSlot, "saved", id);
                            }
                            playerCache.setCustomItem(customItem);
                            guiClick.getGuiHandler().changeToInv("item_editor");
                            return true;
                        } else {
                            if (id.isEmpty() || id.equals("NULL")) {
                                playerCache.setItemTag("result", "not_saved", "null");
                            } else {
                                playerCache.setItemTag("result", "saved", id);
                            }
                            playerCache.setCustomItem(customItem);
                            guiClick.getGuiHandler().changeToInv("item_editor");
                            return true;
                        }
                    case FURNACE_RECIPE:
                        playerCache.setItemTag((slot == 20 ? "source" : "result"), (id.isEmpty() ? "not_saved" : "saved"), id);
                        playerCache.setCustomItem(customItem);
                        guiClick.getGuiHandler().changeToInv("item_editor");
                        return true;

                }
            }
        }
        return false;
    }

    private void updateInv(Inventory inv, PlayerCache playerCache) {
        switch (playerCache.getSetting()) {
            case CRAFT_RECIPE:
                List<ItemStack> ingredients = new ArrayList<>();
                int craftSlot;
                for (int i = 0; i < 9; i++) {
                    craftSlot = 19 + i + (i / 3) * 6;
                    ItemStack itemStack = inv.getItem(craftSlot);
                    ingredients.add(itemStack == null ? new ItemStack(Material.AIR) : itemStack);
                }
                playerCache.setCraftIngredients(ingredients);
                if (inv.getItem(33) != null) {
                    playerCache.setCraftResult(ItemUtils.getCustomItem(inv.getItem(33)));
                }
                break;
            case FURNACE_RECIPE:
                if (inv.getItem(33) != null) {
                    playerCache.setFurnaceResult(ItemUtils.getCustomItem(inv.getItem(33)));
                }
                if (inv.getItem(20) != null) {
                    playerCache.setFurnaceSource(ItemUtils.getCustomItem(inv.getItem(20)));
                }
                break;
        }


    }

    @Override
    public boolean parseChatMessage(int id, String message, GuiHandler guiHandler) {
        Player player = guiHandler.getPlayer();
        PlayerCache cache = CustomCrafting.getPlayerCache(player);
        String[] args = message.split(" ");

        if (args.length > 0) {
            if (id == 0) {
                if (args.length > 1) {
                    switch (cache.getSetting()) {
                        case CRAFT_RECIPE:
                            CraftConfig config = new CraftConfig(api.getConfigAPI(), args[0], args[1]);
                            config.setPermission(cache.getPermission());
                            config.setNeedWorkbench(cache.getWorkbench());
                            config.setShapeless(!cache.getShape());
                            config.setResult(cache.getCraftResult());
                            HashMap<Character, List<CustomItem>> ingredients = cache.getCraftIngredients();
                            String[] shape = new String[3];
                            int index = 0;
                            int row = 0;
                            for (char key : ingredients.keySet()) {
                                List<CustomItem> keyItems = ingredients.get((char) key);
                                if (ItemUtils.isEmpty(keyItems)) {
                                    if (shape[row] != null) {
                                        shape[row] = shape[row] + " ";
                                    } else {
                                        shape[row] = " ";
                                    }
                                } else {
                                    if (shape[row] != null) {
                                        shape[row] = shape[row] + key;
                                    } else {
                                        shape[row] = String.valueOf(key);
                                    }
                                }
                                index++;
                                if ((index % 3) == 0) {
                                    row++;
                                }
                            }
                            config.setShape(shape);
                            config.setIngredients(cache.getCraftIngredients());
                            config.save();
                            api.sendPlayerMessage(player, "§aRecipe successfully saved to:");
                            api.sendPlayerMessage(player, "§6recipes/" + args[0] + "/workbench/" + args[1]);
                            guiHandler.openLastInv();
                            break;
                        case FURNACE_RECIPE:
                            FurnaceConfig furnaceConfig = new FurnaceConfig(api.getConfigAPI(), args[0], args[1]);

                            furnaceConfig.setAdvancedFurnace(cache.needsAdvFurnace());
                            furnaceConfig.setCookingTime(cache.getCookingTime());
                            furnaceConfig.setXP(cache.getXP());
                            furnaceConfig.setResult(cache.getFurnaceResult());
                            furnaceConfig.setSource(cache.getFurnaceSource());

                            furnaceConfig.save();

                            api.sendPlayerMessage(player, "§aRecipe successfully saved to:");
                            api.sendPlayerMessage(player, "§6recipes/" + args[0] + "/furnace/" + args[1]);
                            guiHandler.openLastInv();
                            break;


                    }
                }
            }else if(id == 1){
                float xp = 0f;
                try {
                    xp = Float.parseFloat(args[0]);
                }catch (NumberFormatException e){
                    api.sendPlayerMessage(player, "§cType in a valid number!");
                    return true;
                }
                cache.setXP(xp);
            }else if(id == 11){
                int time = 20;
                try {
                    time = Integer.parseInt(args[0]);
                }catch (NumberFormatException e){
                    api.sendPlayerMessage(player, "§cType in a valid number!");
                    return true;
                }
                cache.setCookingTime(time);
            }

        }


        return false;
    }
}
