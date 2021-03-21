package me.wolfyscript.customcrafting.gui.lists.buttons;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.Setting;
import me.wolfyscript.customcrafting.recipes.types.ICustomRecipe;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.chat.ClickData;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiWindow;
import me.wolfyscript.utilities.api.inventory.gui.button.Button;
import me.wolfyscript.utilities.api.nms.inventory.GUIInventory;
import me.wolfyscript.utilities.util.Pair;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import me.wolfyscript.utilities.util.inventory.item_builder.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import java.io.IOException;
import java.util.HashMap;

public class RecipeListContainerButton extends Button<CCCache> {

    private final CustomCrafting customCrafting;
    private final HashMap<GuiHandler<CCCache>, Recipe> recipes = new HashMap<>();
    private final HashMap<GuiHandler<CCCache>, ICustomRecipe<?,?>> customRecipes = new HashMap<>();
    private final WolfyUtilities api;

    public RecipeListContainerButton(int slot, CustomCrafting customCrafting) {
        super("recipe_list.container_" + slot, null);
        this.customCrafting = customCrafting;
        this.api = CustomCrafting.getApi();
    }

    @Override
    public void init(GuiWindow guiWindow) {
        //NOT NEEDED
    }

    @Override
    public void init(String s, WolfyUtilities wolfyUtilities) {
        //NOT NEEDED
    }

    @Override
    public void postExecute(GuiHandler<CCCache> guiHandler, Player player, GUIInventory<CCCache> inventory, ItemStack itemStack, int slot, InventoryInteractEvent event) throws IOException {

    }

    @Override
    public void preRender(GuiHandler<CCCache> guiHandler, Player player, GUIInventory<CCCache> inventory, ItemStack itemStack, int slot, boolean help) {

    }

    @Override
    public boolean execute(GuiHandler<CCCache> guiHandler, Player player, GUIInventory<CCCache> inventory, int slot, InventoryInteractEvent event) {
        CCCache cache = guiHandler.getCustomCache();
        String id = getCustomRecipe(guiHandler) != null ? getCustomRecipe(guiHandler).getNamespacedKey().toString() : me.wolfyscript.utilities.util.NamespacedKey.of(((Keyed) getRecipe(guiHandler)).getKey()).toString();
        if(event instanceof InventoryClickEvent){
            if (((InventoryClickEvent) event).isShiftClick() && getCustomRecipe(guiHandler) != null) {
                ICustomRecipe<?,?> recipe = getCustomRecipe(guiHandler);
                if (((InventoryClickEvent) event).isLeftClick()) {
                    cache.setSetting(Setting.RECIPE_CREATOR);
                    cache.setRecipeType(recipe.getRecipeType());
                    if (customCrafting.getRecipeHandler().loadRecipeIntoCache(recipe, guiHandler)) {
                        Bukkit.getScheduler().runTaskLater(customCrafting, () -> guiHandler.openWindow(new me.wolfyscript.utilities.util.NamespacedKey("recipe_creator", guiHandler.getCustomCache().getRecipeType().getCreatorID())), 1);
                    } else {
                        api.getChat().sendKey(player, new me.wolfyscript.utilities.util.NamespacedKey("none", "recipe_editor"), "invalid_recipe", new Pair<>("%recipe_type%", guiHandler.getCustomCache().getRecipeType().name()));
                    }
                } else {
                    api.getChat().sendKey(player, new me.wolfyscript.utilities.util.NamespacedKey("none", "recipe_editor"), "delete.confirm", new Pair<>("%recipe%", recipe.getNamespacedKey().toString()));
                    api.getChat().sendActionMessage(player, new ClickData("$inventories.none.recipe_editor.messages.delete.confirmed$", (wolfyUtilities, player1) -> {
                        guiHandler.openCluster();
                        Bukkit.getScheduler().runTaskAsynchronously(customCrafting, () -> recipe.delete(player1));
                    }), new ClickData("$inventories.none.recipe_editor.messages.delete.declined$", (wolfyUtilities, player2) -> guiHandler.openCluster()));
                }
            } else {
                if (!id.isEmpty() && id.contains(":")) {
                    me.wolfyscript.utilities.util.NamespacedKey namespacedKey = me.wolfyscript.utilities.util.NamespacedKey.of(id);
                    if (customCrafting.getRecipeHandler().getDisabledRecipes().contains(namespacedKey)) {
                        customCrafting.getRecipeHandler().getDisabledRecipes().remove(namespacedKey);
                    } else {
                        customCrafting.getRecipeHandler().getDisabledRecipes().add(namespacedKey);
                        for (Player player1 : Bukkit.getOnlinePlayers()) {
                            if (namespacedKey != null) {
                                player1.undiscoverRecipe(namespacedKey.toBukkit(CustomCrafting.getInst()));
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    @Override
    public void render(GuiHandler<CCCache> guiHandler, Player player, GUIInventory<CCCache> guiInventory, Inventory inventory, ItemStack itemStack, int slot, boolean help) {
        if (getCustomRecipe(guiHandler) != null) {
            ICustomRecipe<?,?> recipe = getCustomRecipe(guiHandler);
            if (recipe != null) {
                ItemBuilder itemB = new ItemBuilder(recipe.getResult().getItemStack().clone());
                if (recipe.getResult().isEmpty()) {
                    itemB.setType(Material.STONE).addUnsafeEnchantment(Enchantment.FIRE_ASPECT, 0).addItemFlags(ItemFlag.HIDE_ENCHANTS).setDisplayName("§r§7" + recipe.getNamespacedKey().toString());
                }
                itemB.addLoreLine("§8" + recipe.getNamespacedKey().toString());
                if (customCrafting.getRecipeHandler().getDisabledRecipes().contains(recipe.getNamespacedKey())) {
                    itemB.addLoreLine(ChatColor.translateAlternateColorCodes('&', CustomCrafting.getApi().getLanguageAPI().replaceKeys("$inventories.none.recipe_list.items.lores.disabled$")));
                } else {
                    itemB.addLoreLine(ChatColor.translateAlternateColorCodes('&', CustomCrafting.getApi().getLanguageAPI().replaceKeys("$inventories.none.recipe_list.items.lores.enabled$")));
                }
                itemB.addLoreLine("");
                itemB.addLoreLine("§8" + recipe.getRecipeType().name());
                itemB.addLoreLine(ChatColor.translateAlternateColorCodes('&', CustomCrafting.getApi().getLanguageAPI().replaceKeys("$inventories.none.recipe_list.items.lores.edit$")));
                itemB.addLoreLine(ChatColor.translateAlternateColorCodes('&', CustomCrafting.getApi().getLanguageAPI().replaceKeys("$inventories.none.recipe_list.items.lores.delete$")));
                inventory.setItem(slot, itemB.create());
            }
        } else {
            Recipe recipe = getRecipe(guiHandler);
            if (recipe != null) {
                ItemBuilder itemB;
                if (ItemUtils.isAirOrNull(recipe.getResult())) {
                    itemB = new ItemBuilder(Material.STONE);
                    itemB.addUnsafeEnchantment(Enchantment.FIRE_ASPECT, 0).addItemFlags(ItemFlag.HIDE_ENCHANTS).setDisplayName("§r§7" + ((Keyed) recipe).getKey().toString());
                } else {
                    itemB = new ItemBuilder(recipe.getResult());
                }
                itemB.addLoreLine("§8" + ((Keyed) recipe).getKey().toString());
                if (customCrafting.getRecipeHandler().getDisabledRecipes().contains(me.wolfyscript.utilities.util.NamespacedKey.of(((Keyed) recipe).getKey()))) {
                    itemB.addLoreLine(ChatColor.translateAlternateColorCodes('&', CustomCrafting.getApi().getLanguageAPI().replaceKeys("$inventories.none.recipe_list.items.lores.disabled$")));
                } else {
                    itemB.addLoreLine(ChatColor.translateAlternateColorCodes('&', CustomCrafting.getApi().getLanguageAPI().replaceKeys("$inventories.none.recipe_list.items.lores.enabled$")));
                }
                inventory.setItem(slot, itemB.create());
            }
        }
    }

    public ICustomRecipe<?,?> getCustomRecipe(GuiHandler<CCCache> guiHandler) {
        return customRecipes.getOrDefault(guiHandler, null);
    }

    public void setCustomRecipe(GuiHandler<CCCache> guiHandler, ICustomRecipe<?,?> recipe) {
        customRecipes.put(guiHandler, recipe);
    }

    public Recipe getRecipe(GuiHandler<CCCache> guiHandler) {
        return recipes.getOrDefault(guiHandler, null);
    }

    public void setRecipe(GuiHandler<CCCache> guiHandler, Recipe recipe) {
        recipes.put(guiHandler, recipe);
    }
}
