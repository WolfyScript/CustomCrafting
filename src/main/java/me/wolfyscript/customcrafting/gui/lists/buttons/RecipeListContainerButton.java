package me.wolfyscript.customcrafting.gui.lists.buttons;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.gui.Setting;
import me.wolfyscript.customcrafting.recipes.types.ICustomRecipe;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.chat.ClickData;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiWindow;
import me.wolfyscript.utilities.api.inventory.gui.button.Button;
import me.wolfyscript.utilities.util.Pair;
import me.wolfyscript.utilities.util.inventory.item_builder.ItemBuilder;
import org.bukkit.*;
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

public class RecipeListContainerButton extends Button<TestCache> {

    private final CustomCrafting customCrafting;
    private final HashMap<GuiHandler<TestCache>, Recipe> recipes = new HashMap<>();
    private final HashMap<GuiHandler<TestCache>, ICustomRecipe<?>> customRecipes = new HashMap<>();
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
    public void postExecute(GuiHandler<TestCache> guiHandler, Player player, Inventory inventory, ItemStack itemStack, int i, InventoryInteractEvent inventoryInteractEvent) throws IOException {

    }

    @Override
    public void prepareRender(GuiHandler<TestCache> guiHandler, Player player, Inventory inventory, ItemStack itemStack, int i, boolean b) {

    }

    @Override
    public boolean execute(GuiHandler<TestCache> guiHandler, Player player, Inventory inventory, int slot, InventoryClickEvent event) {
        TestCache cache = guiHandler.getCustomCache();
        String id = getCustomRecipe(guiHandler) != null ? getCustomRecipe(guiHandler).getNamespacedKey().toString() : ((Keyed) getRecipe(guiHandler)).getKey().toString();

        if (event.isShiftClick() && getCustomRecipe(guiHandler) != null) {
            ICustomRecipe<?> recipe = getCustomRecipe(guiHandler);
            if (event.isLeftClick()) {
                cache.setSetting(Setting.RECIPE_CREATOR);
                cache.setRecipeType(recipe.getRecipeType());
                if (customCrafting.getRecipeHandler().loadRecipeIntoCache(recipe, guiHandler)) {
                    Bukkit.getScheduler().runTaskLater(customCrafting, () -> guiHandler.changeToInv(new me.wolfyscript.utilities.util.NamespacedKey("recipe_creator", guiHandler.getCustomCache().getRecipeType().getCreatorID())), 1);
                } else {
                    api.getChat().sendPlayerMessage(player, new me.wolfyscript.utilities.util.NamespacedKey("none", "recipe_editor"), "invalid_recipe", new Pair<>("%recipe_type%", guiHandler.getCustomCache().getRecipeType().name()));
                }
            } else {
                api.getChat().sendPlayerMessage(player, new me.wolfyscript.utilities.util.NamespacedKey("none", "recipe_editor"), "delete.confirm", new Pair<>("%recipe%", recipe.getNamespacedKey().toString()));
                api.getChat().sendActionMessage(player, new ClickData("$inventories.none.recipe_editor.messages.delete.confirmed$", (wolfyUtilities, player1) -> {
                    guiHandler.openCluster();
                    Bukkit.getScheduler().runTaskAsynchronously(customCrafting, () -> recipe.delete(player1));
                }), new ClickData("$inventories.none.recipe_editor.messages.delete.declined$", (wolfyUtilities, player2) -> guiHandler.openCluster()));
            }
        } else {
            if (!id.isEmpty() && id.contains(":")) {
                if (customCrafting.getRecipeHandler().getDisabledRecipes().contains(id)) {
                    customCrafting.getRecipeHandler().getDisabledRecipes().remove(id);
                } else {
                    customCrafting.getRecipeHandler().getDisabledRecipes().add(id);
                    for (Player player1 : Bukkit.getOnlinePlayers()) {
                        player1.undiscoverRecipe(new NamespacedKey(id.split(":")[0], id.split(":")[1]));
                    }
                }
            }
        }
        return true;
    }

    @Override
    public void render(GuiHandler<TestCache> guiHandler, Player player, Inventory inventory, int slot, boolean help) {
        if (getCustomRecipe(guiHandler) != null) {
            ICustomRecipe<?> recipe = getCustomRecipe(guiHandler);
            if (recipe != null) {
                ItemBuilder itemB = new ItemBuilder(recipe.getResult().create());
                if (recipe.getResult().getItemStack().getType().equals(Material.AIR)) {
                    itemB.setType(Material.STONE).addUnsafeEnchantment(Enchantment.FIRE_ASPECT, 0).addItemFlags(ItemFlag.HIDE_ENCHANTS).setDisplayName("§r§7" + recipe.getNamespacedKey().toString());
                }
                itemB.addLoreLine("§8" + recipe.getNamespacedKey().toString());
                if (customCrafting.getRecipeHandler().getDisabledRecipes().contains(recipe.getNamespacedKey().toString())) {
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
                ItemBuilder itemB = new ItemBuilder(recipe.getResult());
                if (recipe.getResult().getType().equals(Material.AIR)) {
                    itemB.setType(Material.STONE).addUnsafeEnchantment(Enchantment.FIRE_ASPECT, 0).addItemFlags(ItemFlag.HIDE_ENCHANTS).setDisplayName("§r§7" + ((Keyed) recipe).getKey().toString());
                }
                itemB.addLoreLine("§8" + ((Keyed) recipe).getKey().toString());
                if (customCrafting.getRecipeHandler().getDisabledRecipes().contains(((Keyed) recipe).getKey().toString())) {
                    itemB.addLoreLine(ChatColor.translateAlternateColorCodes('&', CustomCrafting.getApi().getLanguageAPI().replaceKeys("$inventories.none.recipe_list.items.lores.disabled$")));
                } else {
                    itemB.addLoreLine(ChatColor.translateAlternateColorCodes('&', CustomCrafting.getApi().getLanguageAPI().replaceKeys("$inventories.none.recipe_list.items.lores.enabled$")));
                }
                inventory.setItem(slot, itemB.create());
            }
        }
    }

    public ICustomRecipe<?> getCustomRecipe(GuiHandler<TestCache> guiHandler) {
        return customRecipes.getOrDefault(guiHandler, null);
    }

    public void setCustomRecipe(GuiHandler<TestCache> guiHandler, ICustomRecipe<?> recipe) {
        customRecipes.put(guiHandler, recipe);
    }

    public Recipe getRecipe(GuiHandler<TestCache> guiHandler) {
        return recipes.getOrDefault(guiHandler, null);
    }

    public void setRecipe(GuiHandler<TestCache> guiHandler, Recipe recipe) {
        recipes.put(guiHandler, recipe);
    }
}
