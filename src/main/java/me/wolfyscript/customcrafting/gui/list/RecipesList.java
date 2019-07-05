package me.wolfyscript.customcrafting.gui.list;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.gui.ExtendedGuiWindow;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.GuiAction;
import me.wolfyscript.utilities.api.inventory.GuiClick;
import me.wolfyscript.utilities.api.inventory.GuiUpdateEvent;
import me.wolfyscript.utilities.api.inventory.InventoryAPI;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class RecipesList extends ExtendedGuiWindow {

    private HashMap<UUID, Integer> pages = new HashMap<>();

    public RecipesList(InventoryAPI inventoryAPI) {
        super("recipe_list", inventoryAPI, 54);
    }

    @Override
    public void onInit() {
        createItem("next_page", WolfyUtilities.getCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzg2MTg1YjFkNTE5YWRlNTg1ZjE4NGMzNGYzZjNlMjBiYjY0MWRlYjg3OWU4MTM3OGU0ZWFmMjA5Mjg3In19fQ=="));
        createItem("previous_page", WolfyUtilities.getCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWQ3M2NmNjZkMzFiODNjZDhiODY0NGMxNTk1OGMxYjczYzhkOTczMjNiODAxMTcwYzFkODg2NGJiNmE4NDZkIn19fQ=="));
    }

    @EventHandler
    public void onUpdate(GuiUpdateEvent event) {
        if (event.verify(this)) {
            event.setItem(2, "previous_page");
            event.setItem(6, "next_page");
            List<Recipe> recipes = CustomCrafting.getRecipeHandler().getAllRecipes();
            if (!pages.containsKey(event.getPlayer().getUniqueId())) {
                pages.put(event.getPlayer().getUniqueId(), 0);
            }

            int item = 0;
            for (int i = 45 * pages.get(event.getPlayer().getUniqueId()); item < 45 && i < recipes.size(); i++) {

                Recipe recipe = recipes.get(i);
                if (recipe instanceof Keyed) {
                    ItemStack itemStack = recipe.getResult();
                    if (itemStack.getType().equals(Material.AIR)) {
                        itemStack = new ItemStack(Material.STONE);
                        itemStack.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
                        ItemMeta itemMeta = itemStack.getItemMeta();
                        itemMeta.setDisplayName("§r§7" + ((Keyed) recipe).getKey().toString());
                        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                        itemStack.setItemMeta(itemMeta);
                    }
                    ItemMeta itemMeta = itemStack.getItemMeta();
                    List<String> lore = itemMeta.hasLore() ? itemMeta.getLore() : new ArrayList<>();
                    lore.add(" ");
                    lore.add("§7"+((Keyed) recipe).getKey().toString()+""+WolfyUtilities.hideString(";;"+((Keyed) recipe).getKey().toString()));
                    if (CustomCrafting.getRecipeHandler().getDisabledRecipes().contains(((Keyed) recipe).getKey().toString())) {
                        lore.add(ChatColor.translateAlternateColorCodes('&', api.getLanguageAPI().getActiveLanguage().replaceKeys("$items.recipe_list.lores.disabled$")));
                    } else {
                        lore.add(ChatColor.translateAlternateColorCodes('&', api.getLanguageAPI().getActiveLanguage().replaceKeys("$items.recipe_list.lores.enabled$")));
                    }
                    itemMeta.setLore(lore);
                    itemStack.setItemMeta(itemMeta);
                    event.setItem(9 + item, itemStack);
                }
                item++;
            }
        }
    }

    @Override
    public boolean onAction(GuiAction guiAction) {
        if(!super.onAction(guiAction)){
            if (guiAction.getAction().equals("back")) {
                guiAction.getGuiHandler().openLastInv();
            } else {
                Player player = guiAction.getPlayer();
                UUID uuid = player.getUniqueId();
                if (!pages.containsKey(player.getUniqueId())) {
                    pages.put(uuid, 0);
                }
                if (guiAction.getAction().equals("previous_page")) {
                    if (pages.get(uuid) > 0) {
                        pages.put(uuid, pages.get(uuid) - 1);
                    }
                } else if (guiAction.getAction().equals("next_page")) {
                    if (pages.get(uuid) + 1 < getMaxPages()) {
                        pages.put(uuid, pages.get(uuid) + 1);
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean onClick(GuiClick guiClick) {
        ItemStack itemStack = guiClick.getCurrentItem();
        if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasLore()) {
            String id = WolfyUtilities.unhideString(itemStack.getItemMeta().getLore().get(itemStack.getItemMeta().getLore().size() - 2)).split(";;")[1];
            if (!id.isEmpty() && id.contains(":")) {
                if (CustomCrafting.getRecipeHandler().getDisabledRecipes().contains(id)) {
                    CustomCrafting.getRecipeHandler().getDisabledRecipes().remove(id);
                } else {
                    CustomCrafting.getRecipeHandler().getDisabledRecipes().add(id);
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.undiscoverRecipe(new NamespacedKey(id.split(":")[0], id.split(":")[1]));
                    }
                }
            }
        }
        update(guiClick.getGuiHandler());
        return true;
    }

    private int getMaxPages() {
        return CustomCrafting.getRecipeHandler().getAllRecipes().size() / 45 + (CustomCrafting.getRecipeHandler().getAllRecipes().size() % 45 > 0 ? 1 : 0);
    }
}
