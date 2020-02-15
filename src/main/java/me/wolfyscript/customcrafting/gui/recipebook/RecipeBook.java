package me.wolfyscript.customcrafting.gui.recipebook;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.PlayerStatistics;
import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.data.cache.KnowledgeBook;
import me.wolfyscript.customcrafting.gui.ExtendedGuiWindow;
import me.wolfyscript.customcrafting.gui.recipebook.buttons.ItemCategoryButton;
import me.wolfyscript.customcrafting.gui.recipebook.buttons.RecipeBookContainerButton;
import me.wolfyscript.customcrafting.handlers.RecipeHandler;
import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.types.CookingConfig;
import me.wolfyscript.customcrafting.recipes.types.CustomCookingRecipe;
import me.wolfyscript.customcrafting.recipes.types.CustomRecipe;
import me.wolfyscript.customcrafting.recipes.types.RecipeConfig;
import me.wolfyscript.customcrafting.recipes.types.anvil.CustomAnvilRecipe;
import me.wolfyscript.customcrafting.recipes.types.cauldron.CauldronRecipe;
import me.wolfyscript.customcrafting.recipes.types.elite_workbench.EliteCraftingRecipe;
import me.wolfyscript.customcrafting.recipes.types.grindstone.GrindstoneRecipe;
import me.wolfyscript.customcrafting.recipes.types.stonecutter.CustomStonecutterRecipe;
import me.wolfyscript.customcrafting.recipes.types.workbench.AdvancedCraftingRecipe;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.GuiUpdateEvent;
import me.wolfyscript.utilities.api.inventory.InventoryAPI;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.buttons.ActionButton;
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
import java.util.concurrent.atomic.AtomicInteger;

public class RecipeBook extends ExtendedGuiWindow {

    public RecipeBook(InventoryAPI inventoryAPI) {
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
    }

    @EventHandler
    public void onUpdate(GuiUpdateEvent event) {
        if (event.verify(this)) {
            RecipeHandler recipeHandler = CustomCrafting.getRecipeHandler();
            Player player = event.getPlayer();
            PlayerStatistics playerStatistics = CustomCrafting.getPlayerStatistics(player);
            KnowledgeBook knowledgeBook = ((TestCache) event.getGuiHandler().getCustomCache()).getKnowledgeBook();
            ((ItemCategoryButton) event.getInventoryAPI().getGuiCluster("recipe_book").getButton("itemCategory")).setState(event.getGuiHandler(), knowledgeBook.getItemCategory());
            for (int i = 1; i < 9; i++) {
                event.setButton(i, "none", playerStatistics.getDarkMode() ? "glass_gray" : "glass_white");
            }
            if (knowledgeBook.getCustomRecipe() == null) {
                event.setButton(0, "back");
                event.setButton(4, "recipe_book", "itemCategory");
                List<CustomRecipe> recipes = new ArrayList<>();
                switch (knowledgeBook.getSetting()) {
                    case WORKBENCH:
                        KnowledgeBook.WorkbenchFilter workbenchFilter = knowledgeBook.getWorkbenchFilter();
                        event.setButton(8, "recipe_book", "workbench.filter_button");
                        for (AdvancedCraftingRecipe recipe : CustomCrafting.getRecipeHandler().getAvailableAdvancedCraftingRecipes(player)) {
                            if (workbenchFilter.equals(KnowledgeBook.WorkbenchFilter.ALL) || (workbenchFilter.equals(KnowledgeBook.WorkbenchFilter.NORMAL) && !recipe.getConditions().getByID("advanced_workbench").getOption().equals(Conditions.Option.EXACT)) || (workbenchFilter.equals(KnowledgeBook.WorkbenchFilter.ADVANCED) && recipe.getConditions().getByID("advanced_workbench").getOption().equals(Conditions.Option.EXACT))) {
                                recipes.add(recipe);
                            }
                        }
                        break;
                    case ELITE_WORKBENCH:
                        recipes.addAll(recipeHandler.getAvailableEliteCraftingRecipes(player));
                        break;
                    case ANVIL:
                        recipes.addAll(recipeHandler.getAvailableAnvilRecipes(player));
                        break;
                    case STONECUTTER:
                        recipes.addAll(recipeHandler.getAvailableStonecutterRecipes());
                        break;
                    case CAULDRON:
                        recipes.addAll(recipeHandler.getAvailableCauldronRecipes());
                        break;
                    case FURNACE:
                        recipes.addAll(recipeHandler.getAvailableFurnaceRecipes());
                        break;
                    case BLAST_FURNACE:
                        recipes.addAll(recipeHandler.getAvailableBlastRecipes());
                        break;
                    case SMOKER:
                        recipes.addAll(recipeHandler.getAvailableSmokerRecipes());
                        break;
                    case CAMPFIRE:
                        recipes.addAll(recipeHandler.getAvailableCampfireRecipes());
                        break;
                    case GRINDSTONE:
                        recipes.addAll(recipeHandler.getAvailableGrindstoneRecipes(player));
                }
                if (!knowledgeBook.getItemCategory().equals(ItemCategory.SEARCH)) {
                    Iterator<CustomRecipe> recipeIterator = recipes.iterator();
                    while (recipeIterator.hasNext()) {
                        CustomRecipe customRecipe = recipeIterator.next();
                        List<CustomItem> items = new ArrayList<>();
                        if(customRecipe instanceof CustomAnvilRecipe){
                            CustomAnvilRecipe anvilRecipe = (CustomAnvilRecipe) customRecipe;
                            if(!anvilRecipe.getInputLeft().isEmpty()){
                                items.addAll(anvilRecipe.getInputLeft());
                            }else if(!anvilRecipe.getInputRight().isEmpty()){
                                items.addAll(anvilRecipe.getInputRight());
                            }else if(!anvilRecipe.getCustomResults().isEmpty()){
                                items.addAll(anvilRecipe.getCustomResults());
                            }
                        }else{
                            items.addAll(((CustomRecipe<RecipeConfig>) customRecipe).getCustomResults());
                        }

                        boolean valid = false;
                        for (CustomItem item : items) {
                            if (knowledgeBook.getItemCategory().isValid(item.getType())) {
                                valid = true;
                                break;
                            }
                        }
                        if (!valid) {
                            recipeIterator.remove();
                        }
                    }
                }

                int maxPages = recipes.size() / 45 + (recipes.size() % 45 > 0 ? 1 : 0);
                if (knowledgeBook.getPage() >= maxPages) {
                    knowledgeBook.setPage(0);
                }
                if (knowledgeBook.getPage() != 0) {
                    event.setButton(2, "recipe_book", "previous_page");
                }
                if (knowledgeBook.getPage() + 1 < maxPages) {
                    event.setButton(6, "recipe_book", "next_page");
                }
                int item = 0;
                for (int i = 45 * knowledgeBook.getPage(); item < 45 && i < recipes.size(); i++) {
                    RecipeBookContainerButton button = (RecipeBookContainerButton) getButton("recipe_book.container_" + item);
                    button.setRecipe(event.getGuiHandler(), recipes.get(i));
                    event.setButton(9 + item, button);
                    item++;
                }
            } else {
                switch (knowledgeBook.getSetting()) {
                    case WORKBENCH:
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
                        break;
                    case ELITE_WORKBENCH:
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
                        break;
                    case CAMPFIRE:
                    case BLAST_FURNACE:
                    case SMOKER:
                    case FURNACE:
                        event.setButton(0, "back");
                        CustomCookingRecipe<CookingConfig> furnaceRecipe = (CustomCookingRecipe<CookingConfig>) knowledgeBook.getCustomRecipe();
                        event.setButton(22, "recipe_book", "cooking.icon");
                        event.setButton(29, "none", playerStatistics.getDarkMode() ? "glass_gray" : "glass_white");

                        if (furnaceRecipe != null) {
                            if (knowledgeBook.getTimerTask() == -1) {
                                AtomicInteger i = new AtomicInteger();
                                List<CustomItem> variantsSource = new ArrayList<>();
                                for (CustomItem customItem : furnaceRecipe.getSource()) {
                                    if (!customItem.hasPermission() || player.hasPermission(customItem.getPermission())) {
                                        if (customItem.getType() != Material.AIR) {
                                            variantsSource.add(customItem.getRealItem());
                                        }
                                    }
                                }
                                List<ItemStack> variantsResult = new ArrayList<>();
                                for (CustomItem customItem : furnaceRecipe.getCustomResults()) {
                                    if (!customItem.hasPermission() || player.hasPermission(customItem.getPermission())) {
                                        if (customItem.getType() != Material.AIR) {
                                            variantsResult.add(new ItemBuilder(customItem.getRealItem()).addLoreLine("§8" + (customItem.getRarityPercentage() * 100) + "% possibility").create());
                                        }
                                    }
                                }
                                event.setItem(20, variantsSource.get(knowledgeBook.getTimerTimings().getOrDefault(0, 0)));
                                event.setItem(33, variantsResult.get(knowledgeBook.getTimerTimings().getOrDefault(1, 0)));

                                knowledgeBook.setTimerTask(Bukkit.getScheduler().scheduleSyncRepeatingTask(CustomCrafting.getInst(), () -> {
                                    if (i.get() == 0) {
                                        event.setButton(32, "none", playerStatistics.getDarkMode() ? "glass_black" : "glass_gray");
                                        event.setButton(31, "none", playerStatistics.getDarkMode() ? "glass_black" : "glass_gray");
                                        event.setButton(30, "none", playerStatistics.getDarkMode() ? "glass_black" : "glass_gray");
                                    } else if (i.get() == 1) {
                                        event.setItem(30, new ItemStack(Material.YELLOW_CONCRETE));
                                    } else if (i.get() == 2) {
                                        event.setItem(30, new ItemStack(Material.ORANGE_CONCRETE));
                                        event.setItem(31, new ItemStack(Material.YELLOW_CONCRETE));
                                    } else {
                                        event.setItem(30, new ItemStack(Material.RED_CONCRETE_POWDER));
                                        event.setItem(31, new ItemStack(Material.ORANGE_CONCRETE));
                                        event.setItem(32, new ItemStack(Material.YELLOW_CONCRETE));
                                    }
                                    if (i.get() < 3) {
                                        i.getAndIncrement();
                                    } else {
                                        i.set(0);
                                    }
                                    HashMap<Integer, Integer> variantsTimers = knowledgeBook.getTimerTimings();
                                    int variant = variantsTimers.getOrDefault(0, 0);
                                    if(variantsSource.size() > 1){
                                        event.setItem(20, variantsSource.get(variant));
                                        if (i.get() > 2) {
                                            if (++variant < variantsSource.size()) {
                                                variantsTimers.put(0, variant);
                                            } else {
                                                variantsTimers.put(0, 0);
                                            }
                                        }
                                    }else{
                                        event.setItem(20, variantsSource.isEmpty() ? new ItemStack(Material.AIR) : variantsSource.get(0));
                                    }
                                    if(variantsResult.size() > 1){
                                        variant = variantsTimers.getOrDefault(1, 0);
                                        event.setItem(33, variantsResult.get(variant));
                                        if (i.get() > 2) {
                                            if (++variant < variantsResult.size()) {
                                                variantsTimers.put(1, variant);
                                            } else {
                                                variantsTimers.put(1, 0);
                                            }
                                        }
                                    }else{
                                        event.setItem(33, variantsResult.isEmpty() ? new ItemStack(Material.AIR) : variantsResult.get(0));
                                    }
                                }, 1, 4));
                            }
                        }
                        break;
                    case ANVIL:
                        event.setButton(0, "back");
                        CustomAnvilRecipe customAnvilRecipe = (CustomAnvilRecipe) knowledgeBook.getCustomRecipe();
                        List<CustomItem> inputLeft = customAnvilRecipe.getInputLeft();
                        List<CustomItem> inputRight = customAnvilRecipe.getInputRight();
                        HashMap<Integer, Integer> timerTimings = knowledgeBook.getTimerTimings();
                        event.setItem(10, inputLeft.isEmpty() ? new ItemStack(Material.AIR) : inputLeft.get(timerTimings.getOrDefault(0, 0)));
                        event.setItem(13, inputRight.isEmpty() ? new ItemStack(Material.AIR) : inputRight.get(timerTimings.getOrDefault(1, 0)));

                        event.setButton(19, "none", "glass_green");
                        event.setButton(22, "none", "glass_green");
                        event.setButton(28, "none", "glass_green");
                        event.setButton(29, "none", "glass_green");
                        event.setButton(30, "none", "glass_green");
                        event.setButton(32, "none", "glass_green");
                        event.setButton(33, "none", "glass_green");

                        if (customAnvilRecipe.getMode().equals(CustomAnvilRecipe.Mode.RESULT)) {
                            event.setButton(31, "recipe_book", "anvil.result");
                            event.setItem(34, customAnvilRecipe.getCustomResult());
                        }else if(customAnvilRecipe.getMode().equals(CustomAnvilRecipe.Mode.DURABILITY)){
                            event.setButton(31, "recipe_book", "anvil.durability");
                            event.setItem(34, inputLeft.isEmpty() ? new ItemStack(Material.AIR) : inputLeft.get(timerTimings.getOrDefault(9, 0)));
                        }else{
                            event.setButton(31, "recipe_book", "anvil.none");
                            event.setItem(34, new ItemStack(Material.AIR));
                        }
                        if (knowledgeBook.getTimerTask() == -1) {
                            knowledgeBook.setTimerTask(Bukkit.getScheduler().scheduleSyncRepeatingTask(CustomCrafting.getInst(), () -> {
                                HashMap<Integer, Integer> timings = knowledgeBook.getTimerTimings();
                                for (int i = 0; i < 2; i++) {
                                    List<CustomItem> variants = new ArrayList<>(i == 0 ? customAnvilRecipe.getInputLeft() : customAnvilRecipe.getInputRight());
                                    Iterator<CustomItem> iterator = variants.iterator();
                                    while(iterator.hasNext()){
                                        CustomItem customItem = iterator.next();
                                        if(!customItem.hasPermission()){
                                            continue;
                                        }
                                        if(!player.hasPermission(customItem.getPermission())){
                                            iterator.remove();
                                        }
                                    }
                                    if (variants.size() > 1 && !variants.isEmpty()) {
                                        int variant = timings.getOrDefault(0, 0);
                                        event.setItem(i == 0 ? 10 : 13, variants.get(variant).getRealItem());
                                        if (++variant < variants.size()) {
                                            timings.put(i, variant);
                                        } else {
                                            timings.put(i, 0);
                                        }
                                    }
                                }
                                if (customAnvilRecipe.getMode().equals(CustomAnvilRecipe.Mode.RESULT)) {
                                    event.setItem(34, customAnvilRecipe.getCustomResult() != null ? customAnvilRecipe.getCustomResult().getRealItem() : new ItemStack(Material.AIR));
                                    List<ItemStack> variants = new ArrayList<>();
                                    if (customAnvilRecipe.getCustomResults().size() > 1) {
                                        for (CustomItem customItem : customAnvilRecipe.getCustomResults()) {
                                            if (!customItem.hasPermission() || player.hasPermission(customItem.getPermission())) {
                                                if (customItem.getType() != Material.AIR) {
                                                    ItemBuilder itemBuilder = new ItemBuilder(customItem.getRealItem());
                                                    itemBuilder.addLoreLine("§7" + (customItem.getRarityPercentage() * 100) + "%");
                                                    variants.add(itemBuilder.create());
                                                }
                                            }
                                        }
                                        int variant = timings.getOrDefault(9, 0);
                                        event.setItem(34, variants.isEmpty() ? new ItemStack(Material.AIR) : variants.get(variant));
                                        if (++variant < variants.size()) {
                                            timings.put(9, variant);
                                        } else {
                                            timings.put(9, 0);
                                        }
                                    }
                                }else if(customAnvilRecipe.getMode().equals(CustomAnvilRecipe.Mode.DURABILITY)){
                                    event.setItem(34, inputLeft.isEmpty() ? new ItemStack(Material.AIR) : inputLeft.get(timerTimings.getOrDefault(9, 0)));
                                    List<CustomItem> variants = new ArrayList<>();
                                    if (customAnvilRecipe.getInputLeft().size() > 1) {
                                        for (CustomItem customItem : customAnvilRecipe.getInputLeft()) {
                                            if (!customItem.hasPermission() || player.hasPermission(customItem.getPermission())) {
                                                variants.add(customItem.getRealItem());
                                            }
                                        }
                                        int variant = timings.getOrDefault(9, 0);
                                        event.setItem(34, variants.isEmpty() ? new ItemStack(Material.AIR) : variants.get(variant));
                                        if (++variant < variants.size()) {
                                            timings.put(9, variant);
                                        } else {
                                            timings.put(9, 0);
                                        }
                                    }
                                }else{
                                    event.setItem(34, new ItemStack(Material.AIR));
                                }
                            }, 1, 30));
                        }
                        break;
                    case STONECUTTER:
                        event.setButton(0, "back");
                        //TODO STONECUTTER
                        event.setButton(29, "none", "glass_green");
                        event.setButton(33, "none", "glass_green");
                        event.setButton(38, "none", "glass_green");
                        event.setButton(39, "none", "glass_green");
                        event.setButton(40, "none", "glass_green");
                        event.setButton(41, "none", "glass_green");
                        event.setButton(42, "none", "glass_green");
                        CustomStonecutterRecipe stonecutterRecipe = (CustomStonecutterRecipe) knowledgeBook.getCustomRecipe();
                        event.setItem(20, stonecutterRecipe.getSource().get(0));
                        event.setButton(31, "recipe_book", "stonecutter");
                        event.setItem(24, stonecutterRecipe.getCustomResult().getRealItem());
                        break;
                    case CAULDRON:
                        event.setButton(0, "back");
                        CauldronRecipe cauldronRecipe = (CauldronRecipe) knowledgeBook.getCustomRecipe();
                        List<CustomItem> ingredients = cauldronRecipe.getIngredients();
                        int invSlot;
                        for (int i = 0; i < 6; i++) {
                            invSlot = 10 + i + (i / 3) * 6;
                            if(i < ingredients.size()){
                                CustomItem customItem = ingredients.get(i);
                                event.setItem(invSlot, customItem == null ? new ItemStack(Material.AIR) : customItem);
                            }else{
                                event.setItem(invSlot, new ItemStack(Material.AIR));
                            }
                        }
                        event.setButton(29, "recipe_book", cauldronRecipe.needsWater() ? "cauldron.water.enabled" : "cauldron.water.disabled");
                        event.setButton(38, "recipe_book", cauldronRecipe.needsFire() ? "cauldron.fire.enabled" : "cauldron.fire.disabled");
                        event.setItem(34, cauldronRecipe.getCustomResult());

                        if (knowledgeBook.getTimerTask() == -1) {
                            knowledgeBook.setTimerTask(Bukkit.getScheduler().scheduleSyncRepeatingTask(CustomCrafting.getInst(), () -> {
                                HashMap<Integer, Integer> timings = knowledgeBook.getTimerTimings();
                                List<ItemStack> variants = new ArrayList<>();
                                if (cauldronRecipe.getCustomResults().size() > 1) {
                                    for (CustomItem customItem : cauldronRecipe.getCustomResults()) {
                                        if (!customItem.hasPermission() || player.hasPermission(customItem.getPermission())) {
                                            if (customItem.getType() != Material.AIR) {
                                                ItemBuilder itemBuilder = new ItemBuilder(customItem.getRealItem());
                                                itemBuilder.addLoreLine("§7" + (customItem.getRarityPercentage() * 100) + "%");
                                                variants.add(itemBuilder.create());
                                            }
                                        }
                                    }
                                    int variant = timings.getOrDefault(9, 0);
                                    event.setItem(34, variants.isEmpty() ? new ItemStack(Material.AIR) : variants.get(variant));
                                    if (++variant < variants.size()) {
                                        timings.put(9, variant);
                                    } else {
                                        timings.put(9, 0);
                                    }
                                }
                            }, 1, 30));
                        }
                        break;
                    case GRINDSTONE:
                        event.setButton(0, "back");
                        GrindstoneRecipe grindstoneRecipe = (GrindstoneRecipe) knowledgeBook.getCustomRecipe();

                        List<CustomItem> inputTop = grindstoneRecipe.getInputTop();
                        List<CustomItem> inputBottom = grindstoneRecipe.getInputBottom();
                        List<CustomItem> results = grindstoneRecipe.getCustomResults();
                        timerTimings = knowledgeBook.getTimerTimings();


                        event.setItem(11, inputTop.isEmpty() ? new ItemStack(Material.AIR) : inputTop.get(timerTimings.getOrDefault(0, 0)));
                        event.setButton(12, "none", "glass_green");
                        event.setButton(21, "none", "glass_green");
                        event.setButton(22, "recipe_book", "grindstone");
                        event.setButton(23, "none", "glass_green");
                        event.setItem(24, results.isEmpty() ? new ItemStack(Material.AIR) : results.get(timerTimings.getOrDefault(2, 0)));
                        event.setItem(29, inputBottom.isEmpty() ? new ItemStack(Material.AIR) : inputBottom.get(timerTimings.getOrDefault(1, 0)));
                        event.setButton(30, "none", "glass_green");


                        knowledgeBook.setTimerTask(Bukkit.getScheduler().scheduleSyncRepeatingTask(CustomCrafting.getInst(), () -> {
                            HashMap<Integer, Integer> timings = knowledgeBook.getTimerTimings();
                            for (int i = 0; i < 3; i++) {
                                List<CustomItem> variants = new ArrayList<>(i == 0 ? grindstoneRecipe.getInputTop() : i == 1 ? grindstoneRecipe.getInputBottom() : grindstoneRecipe.getCustomResults());
                                Iterator<CustomItem> iterator = variants.iterator();
                                while (iterator.hasNext()) {
                                    CustomItem customItem = iterator.next();
                                    if (!customItem.hasPermission()) {
                                        continue;
                                    }
                                    if (!player.hasPermission(customItem.getPermission())) {
                                        iterator.remove();
                                    }
                                }
                                if (variants.size() > 1 && !variants.isEmpty()) {
                                    int variant = timings.getOrDefault(0, 0);
                                    event.setItem(i == 0 ? 11 : i == 1 ? 29 : 25, variants.get(variant).getRealItem());
                                    if (++variant < variants.size()) {
                                        timings.put(i, variant);
                                    } else {
                                        timings.put(i, 0);
                                    }
                                }
                            }
                        }, 1, 30));
                        break;

                }
            }
        }
    }
}
