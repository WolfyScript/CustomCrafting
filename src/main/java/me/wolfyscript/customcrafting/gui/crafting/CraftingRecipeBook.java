package me.wolfyscript.customcrafting.gui.crafting;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.data.cache.EliteWorkbench;
import me.wolfyscript.customcrafting.data.cache.KnowledgeBook;
import me.wolfyscript.customcrafting.gui.ExtendedGuiWindow;
import me.wolfyscript.customcrafting.gui.Setting;
import me.wolfyscript.customcrafting.gui.recipebook.buttons.ItemCategoryButton;
import me.wolfyscript.customcrafting.gui.recipebook.buttons.RecipeBookContainerButton;
import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.conditions.EliteWorkbenchCondition;
import me.wolfyscript.customcrafting.recipes.types.CustomRecipe;
import me.wolfyscript.customcrafting.recipes.types.elite_workbench.EliteCraftingRecipe;
import me.wolfyscript.customcrafting.recipes.types.elite_workbench.ShapedEliteCraftRecipe;
import me.wolfyscript.customcrafting.recipes.types.workbench.AdvancedCraftingRecipe;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.GuiHandler;
import me.wolfyscript.utilities.api.inventory.GuiUpdateEvent;
import me.wolfyscript.utilities.api.inventory.InventoryAPI;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.buttons.ActionButton;
import me.wolfyscript.utilities.api.inventory.button.buttons.DummyButton;
import me.wolfyscript.utilities.api.utils.ItemCategory;
import me.wolfyscript.utilities.api.utils.item_builder.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class CraftingRecipeBook extends ExtendedGuiWindow {

    public CraftingRecipeBook(InventoryAPI inventoryAPI) {
        super("recipe_book", inventoryAPI, 54);
    }

    @Override
    public void onInit() {
        registerButton(new ActionButton("back", new ButtonState("none", "back", WolfyUtilities.getSkullViaURL("864f779a8e3ffa231143fa69b96b14ee35c16d669e19c75fd1a7da4bf306c"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            TestCache cache = (TestCache) guiHandler.getCustomCache();
            KnowledgeBook book = cache.getKnowledgeBook();
            book.stopTimerTask();
            if (book.getCustomRecipe() == null) {
                guiHandler.openPreviousInv();
            } else {
                book.setCustomRecipe(null);
            }
            return true;
        })));
        for (int i = 0; i < 45; i++) {
            registerButton(new RecipeBookContainerButton(i));
        }
        registerButton(new DummyButton("workbench.shapeless_on", new ButtonState("workbench.shapeless_on", WolfyUtilities.getSkullViaURL("f21d93da43863cb3759afefa9f7cc5c81f34d920ca97b7283b462f8b197f813"))));
        registerButton(new DummyButton("workbench.shapeless_off", new ButtonState("workbench.shapeless_off", WolfyUtilities.getSkullViaURL("1aae7e8222ddbee19d184b97e79067814b6ba3142a3bdcce8b93099a312"))));
    }

    @EventHandler
    public void onUpdate(GuiUpdateEvent event) {
        if (event.verify(this)) {
            event.setButton(0, "back");
            GuiHandler<TestCache> guiHandler = event.getGuiHandler();
            Player player = event.getPlayer();
            TestCache cache = guiHandler.getCustomCache();

            EliteWorkbench eliteWorkbenchData = cache.getEliteWorkbench();
            KnowledgeBook knowledgeBook = cache.getKnowledgeBook();
            ItemCategory itemCategory = knowledgeBook.getItemCategory();
            ((ItemCategoryButton) api.getInventoryAPI().getGuiCluster("recipe_book").getButton("itemCategory")).setState(event.getGuiHandler(), itemCategory);
            if (knowledgeBook.getCustomRecipe() == null) {
                event.setButton(0, "back");
                event.setButton(2, "recipe_book", "previous_page");
                event.setButton(4, "recipe_book", "itemCategory");
                event.setButton(6, "recipe_book", "next_page");
                List<CustomRecipe> recipes = new ArrayList<>();
                recipes.addAll(CustomCrafting.getRecipeHandler().getAvailableEliteCraftingRecipes(player));

                Iterator<CustomRecipe> iterator = recipes.iterator();
                while (iterator.hasNext()){
                    EliteCraftingRecipe recipe = (EliteCraftingRecipe) iterator.next();
                    if(!recipe.getConditions().getByID("elite_workbench").getOption().equals(Conditions.Option.IGNORE)){
                        if (!((EliteWorkbenchCondition) recipe.getConditions().getByID("elite_workbench")).getEliteWorkbenches().contains(eliteWorkbenchData.getEliteWorkbenchData().getId())) {
                            iterator.remove();
                            continue;
                        }
                    }
                    if(recipe.isShapeless()){
                        if(recipe.getIngredients().size() > eliteWorkbenchData.getCurrentGridSize() * eliteWorkbenchData.getCurrentGridSize()){
                            iterator.remove();
                        }
                    }else{
                        ShapedEliteCraftRecipe recipe1 = (ShapedEliteCraftRecipe) recipe;
                        if(recipe1.getShape().length > eliteWorkbenchData.getCurrentGridSize() || recipe1.getShape()[0].length() > eliteWorkbenchData.getCurrentGridSize()){
                            iterator.remove();
                        }
                    }
                }
                if (knowledgeBook.getSetting().equals(Setting.WORKBENCH)) {
                    recipes.addAll(CustomCrafting.getRecipeHandler().getAvailableAdvancedCraftingRecipes(player));
                }

                if (!itemCategory.equals(ItemCategory.SEARCH)) {
                    Iterator<CustomRecipe> recipeIterator = recipes.iterator();
                    while (recipeIterator.hasNext()) {
                        CustomRecipe recipe = recipeIterator.next();
                        List<CustomItem> customItems = recipe.getCustomResults();
                        boolean allowed = false;
                        for (CustomItem customItem : customItems) {
                            if (itemCategory.isValid(customItem.getType())) {
                                allowed = true;
                            }
                        }
                        if (!allowed) {
                            recipeIterator.remove();
                        }
                    }
                }

                int maxPages = recipes.size() / 45 + (recipes.size() % 45 > 0 ? 1 : 0);
                if (knowledgeBook.getPage() >= maxPages) {
                    knowledgeBook.setPage(0);
                }
                int item = 0;
                for (int i = 45 * knowledgeBook.getPage(); item < 45 && i < recipes.size(); i++) {
                    RecipeBookContainerButton button = (RecipeBookContainerButton) getButton("recipe_book.container_" + item);
                    button.setRecipe(event.getGuiHandler(), recipes.get(i));
                    event.setButton(9 + item, button);
                    item++;
                }
            }else{
                if(knowledgeBook.getCustomRecipe() instanceof EliteCraftingRecipe){
                    event.setButton(6, "back");
                    EliteCraftingRecipe eliteCraftingRecipe = (EliteCraftingRecipe) knowledgeBook.getCustomRecipe();
                    if (!eliteCraftingRecipe.getIngredients().isEmpty()) {
                        event.setButton(24, "recipe_book", eliteCraftingRecipe.isShapeless() ? "workbench.shapeless_on" : "workbench.shapeless_off");
                        int gridSize = 6;
                        int startSlot = 0;
                        int invSlot;
                        for (int i = 0; i < gridSize*gridSize; i++) {
                            invSlot = startSlot + i + (i / gridSize) * 3;
                            List<CustomItem> variants = eliteCraftingRecipe.getIngredients(i);
                            int variant = knowledgeBook.getTimerTimings().getOrDefault(i, 0);
                            event.setItem(invSlot, variants.isEmpty() ? new ItemStack(Material.AIR) : variants.get(variant));
                        }
                        event.setItem(25, eliteCraftingRecipe.getCustomResult());
                        if (knowledgeBook.getTimerTask() == -1) {
                            knowledgeBook.setTimerTask(Bukkit.getScheduler().scheduleSyncRepeatingTask(CustomCrafting.getInst(), () -> {
                                HashMap<Integer, Integer> variantsTimers = knowledgeBook.getTimerTimings();
                                int slot;
                                for (int i = 0; i < gridSize*gridSize; i++) {
                                    slot = startSlot + i + (i / gridSize) * 3;
                                    List<CustomItem> variants = eliteCraftingRecipe.getIngredients(i);
                                    int variant = variantsTimers.getOrDefault(i, 0);
                                    event.setItem(slot, variants.isEmpty() ? new ItemStack(Material.AIR) : variants.get(variant).getRealItem());
                                    if (++variant < variants.size()) {
                                        variantsTimers.put(i, variant);
                                    } else {
                                        variantsTimers.put(i, 0);
                                    }
                                }
                                List<ItemStack> variants = new ArrayList<>();
                                if (eliteCraftingRecipe.getCustomResults().size() > 1) {
                                    for (CustomItem customItem : eliteCraftingRecipe.getCustomResults()) {
                                        if (!customItem.hasPermission() || player.hasPermission(customItem.getPermission())) {
                                            if (customItem.getType() != Material.AIR) {
                                                ItemBuilder itemBuilder = new ItemBuilder(customItem.getRealItem());
                                                itemBuilder.addLoreLine("§7" + (customItem.getRarityPercentage() * 100) + "% possibility");
                                                variants.add(itemBuilder.create());
                                            }
                                        }
                                    }
                                    int variant = variantsTimers.getOrDefault(9, 0);
                                    event.setItem(24, variants.isEmpty() ? new ItemStack(Material.AIR) : variants.get(variant));
                                    if (++variant < variants.size()) {
                                        variantsTimers.put(9, variant);
                                    } else {
                                        variantsTimers.put(9, 0);
                                    }
                                } else {
                                    event.setItem(25, eliteCraftingRecipe.getCustomResult().getRealItem());
                                }
                            }, 1, 30));
                        }
                    }
                }else{
                    event.setButton(0, "back");
                    AdvancedCraftingRecipe craftingRecipe = (AdvancedCraftingRecipe) knowledgeBook.getCustomRecipe();
                    if (!craftingRecipe.getIngredients().isEmpty()) {
                        if (craftingRecipe.getConditions().getByID("advanced_workbench").getOption().equals(Conditions.Option.EXACT)) {
                            for (int i = 1; i < 8; i++) {
                                event.setButton(i, "none", "glass_purple");
                            }
                            for (int i = 45; i < 54; i++) {
                                event.setButton(i, "none", "glass_purple");
                            }
                        }
                        event.setButton(23, "recipe_book", craftingRecipe.isShapeless() ? "workbench.shapeless_on" : "workbench.shapeless_off");
                        int invSlot;
                        for (int i = 0; i < 9; i++) {
                            invSlot = 10 + i + (i / 3) * 6;
                            List<CustomItem> variants = craftingRecipe.getIngredients(i);
                            int variant = knowledgeBook.getTimerTimings().getOrDefault(i, 0);
                            event.setItem(invSlot, variants.isEmpty() ? new ItemStack(Material.AIR) : variants.get(variant));
                        }
                        event.setItem(25, craftingRecipe.getCustomResult());
                        if (knowledgeBook.getTimerTask() == -1) {
                            knowledgeBook.setTimerTask(Bukkit.getScheduler().scheduleSyncRepeatingTask(CustomCrafting.getInst(), () -> {
                                HashMap<Integer, Integer> variantsTimers = knowledgeBook.getTimerTimings();
                                int slot;
                                for (int i = 0; i < 9; i++) {
                                    slot = 10 + i + (i / 3) * 6;
                                    List<CustomItem> variants = craftingRecipe.getIngredients(i);
                                    int variant = variantsTimers.getOrDefault(i, 0);
                                    event.setItem(slot, variants.isEmpty() ? new ItemStack(Material.AIR) : variants.get(variant).getRealItem());
                                    if (++variant < variants.size()) {
                                        variantsTimers.put(i, variant);
                                    } else {
                                        variantsTimers.put(i, 0);
                                    }
                                }

                                List<ItemStack> variants = new ArrayList<>();
                                if (craftingRecipe.getCustomResults().size() > 1) {
                                    for (CustomItem customItem : craftingRecipe.getCustomResults()) {
                                        if (!customItem.hasPermission() || player.hasPermission(customItem.getPermission())) {
                                            if (customItem.getType() != Material.AIR) {
                                                ItemBuilder itemBuilder = new ItemBuilder(customItem.getRealItem());
                                                itemBuilder.addLoreLine("§7" + (customItem.getRarityPercentage() * 100) + "% possibility");
                                                variants.add(itemBuilder.create());
                                            }
                                        }
                                    }
                                    int variant = variantsTimers.getOrDefault(9, 0);
                                    event.setItem(24, variants.isEmpty() ? new ItemStack(Material.AIR) : variants.get(variant));
                                    if (++variant < variants.size()) {
                                        variantsTimers.put(9, variant);
                                    } else {
                                        variantsTimers.put(9, 0);
                                    }
                                } else {
                                    event.setItem(25, craftingRecipe.getCustomResult().getRealItem());
                                }
                            }, 1, 20));
                        }
                    }
                }



            }


        }
    }
}
