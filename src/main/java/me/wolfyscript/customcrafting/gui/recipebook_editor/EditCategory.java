package me.wolfyscript.customcrafting.gui.recipebook_editor;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.RecipeBookEditor;
import me.wolfyscript.customcrafting.gui.CCWindow;
import me.wolfyscript.customcrafting.gui.recipebook_editor.buttons.SaveCategoryButton;
import me.wolfyscript.customcrafting.recipes.types.ICustomRecipe;
import me.wolfyscript.customcrafting.utils.ChatUtils;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ChatInputButton;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ItemInputButton;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.Pair;
import me.wolfyscript.utilities.util.chat.ChatColor;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import me.wolfyscript.utilities.util.inventory.PlayerHeadUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class EditCategory extends CCWindow {

    public EditCategory(GuiCluster<CCCache> cluster, CustomCrafting customCrafting) {
        super(cluster, "category", 54, customCrafting);
    }

    @Override
    public void onInit() {
        registerButton(new ActionButton<>("back", new ButtonState<>("none", "back", PlayerHeadUtils.getViaValue("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODY0Zjc3OWE4ZTNmZmEyMzExNDNmYTY5Yjk2YjE0ZWUzNWMxNmQ2NjllMTljNzVmZDFhN2RhNGJmMzA2YyJ9fX0="), (cache, guiHandler, player, inventory, slot, event) -> {
            cache.getRecipeBookEditor().setCategory(null);
            cache.getRecipeBookEditor().setCategoryID("");
            guiHandler.openPreviousWindow();
            return true;
        })));
        registerButton(new SaveCategoryButton(false, customCrafting));
        registerButton(new SaveCategoryButton(true, customCrafting));

        registerButton(new ItemInputButton<>("icon", Material.AIR, (cache, guiHandler, player, inventory, slot, event) -> {
            Bukkit.getScheduler().runTask(customCrafting, () -> {
                if (!ItemUtils.isAirOrNull(inventory.getItem(slot))) {
                    cache.getRecipeBookEditor().getCategory().setIcon(inventory.getItem(slot).getType());
                } else {
                    cache.getRecipeBookEditor().getCategory().setIcon(Material.AIR);
                }
            });
            return false;
        }, (hashMap, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            RecipeBookEditor recipeBookEditor = guiHandler.getCustomCache().getRecipeBookEditor();
            if (recipeBookEditor.getCategory() != null && recipeBookEditor.getCategory().getIcon() != null) {
                return new ItemStack(guiHandler.getCustomCache().getRecipeBookEditor().getCategory().getIcon());
            }
            return new ItemStack(Material.AIR);
        }));

        registerButton(new ChatInputButton<>("name", Material.NAME_TAG, (values, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            values.put("%name%", guiHandler.getCustomCache().getRecipeBookEditor().getCategory().getName());
            return itemStack;
        }, (guiHandler, player, s, strings) -> {
            guiHandler.getCustomCache().getRecipeBookEditor().getCategory().setName(s);
            return false;
        }));
        registerButton(new ChatInputButton<>("description.add", Material.WRITABLE_BOOK, (values, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            values.put("%description%", guiHandler.getCustomCache().getRecipeBookEditor().getCategory().getDescription());
            return itemStack;
        }, (guiHandler, player, s, strings) -> {
            guiHandler.getCustomCache().getRecipeBookEditor().getCategory().getDescription().add(s.equals("&empty") ? "" : ChatColor.convert(s));
            return false;
        }));
        registerButton(new ActionButton<>("description.remove", Material.WRITTEN_BOOK, (cache, guiHandler, player, inventory, slot, event) -> {
            ChatUtils.sendCategoryDescription(player);
            guiHandler.close();
            return true;
        }));

        registerButton(new ActionButton<>("recipes", Material.CRAFTING_TABLE, (cache, guiHandler, player, inventory, slot, event) -> {
            guiHandler.getCustomCache().getChatLists().setCurrentPageRecipes(1);
            customCrafting.getChatUtils().sendRecipeList(player, new ArrayList<>(customCrafting.getRecipeHandler().getRecipes().values()));
            if(event instanceof InventoryClickEvent){
                boolean remove = ((InventoryClickEvent) event).isRightClick();
                guiHandler.setChatInputAction((guiHandler1, player1, s, args) -> {
                    if (args.length > 1) {
                        NamespacedKey namespacedKey = new NamespacedKey(args[0], args[1]);
                        ICustomRecipe<?,?> recipe = customCrafting.getRecipeHandler().getRecipe(namespacedKey);

                        if (recipe == null) {
                            api.getChat().sendKey(player, new NamespacedKey("none", "recipe_editor"), "not_existing", new Pair<>("%recipe%", args[0] + ":" + args[1]));
                            return true;
                        }
                        if (remove) {
                            cache.getRecipeBookEditor().getCategory().getRecipes().remove(namespacedKey);
                        } else {
                            cache.getRecipeBookEditor().getCategory().getRecipes().add(namespacedKey);
                        }
                    }
                    return false;
                });
                Bukkit.getScheduler().runTask(customCrafting, guiHandler::close);
            }
            return true;
        }, (values, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            values.put("%recipes%", guiHandler.getCustomCache().getRecipeBookEditor().getCategory().getRecipes().stream().map(namespacedKey -> "&7 - " + namespacedKey.toString()).collect(Collectors.toList()));
            return itemStack;
        }));

    }

    @Override
    public void onUpdateSync(GuiUpdate<CCCache> guiUpdate) {

    }

    @Override
    public void onUpdateAsync(GuiUpdate<CCCache> update) {
        super.onUpdateAsync(update);
        GuiHandler<CCCache> guiHandler = update.getGuiHandler();
        CCCache cache = guiHandler.getCustomCache();
        RecipeBookEditor recipeBookEditor = cache.getRecipeBookEditor();
        update.setButton(0, "back");
        update.setButton(13, "icon");
        update.setButton(19, "name");
        update.setButton(24, "description.add");
        update.setButton(25, "description.remove");
        update.setButton(37, "recipes");

        if (recipeBookEditor.hasCategoryID()) {
            update.setButton(52, "save");
        }
        update.setButton(53, "save_as");

    }
}
