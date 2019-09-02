package me.wolfyscript.customcrafting.gui.recipebook;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.custom_configs.CookingConfig;
import me.wolfyscript.customcrafting.data.PlayerCache;
import me.wolfyscript.customcrafting.data.cache.KnowledgeBook;
import me.wolfyscript.customcrafting.gui.ExtendedGuiWindow;
import me.wolfyscript.customcrafting.gui.Setting;
import me.wolfyscript.customcrafting.gui.recipebook.buttons.RecipeBookContainerButton;
import me.wolfyscript.customcrafting.items.CustomItem;
import me.wolfyscript.customcrafting.recipes.CustomCookingRecipe;
import me.wolfyscript.customcrafting.recipes.CustomRecipe;
import me.wolfyscript.customcrafting.recipes.anvil.CustomAnvilRecipe;
import me.wolfyscript.customcrafting.recipes.stonecutter.CustomStonecutterRecipe;
import me.wolfyscript.customcrafting.recipes.workbench.CraftingRecipe;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.GuiUpdateEvent;
import me.wolfyscript.utilities.api.inventory.InventoryAPI;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.buttons.ActionButton;
import me.wolfyscript.utilities.api.inventory.button.buttons.MultipleChoiceButton;
import me.wolfyscript.utilities.api.inventory.button.buttons.ToggleButton;
import me.wolfyscript.utilities.api.utils.item_builder.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RecipeBook extends ExtendedGuiWindow {

    public RecipeBook(InventoryAPI inventoryAPI) {
        super("recipe_book", inventoryAPI, 54);
    }

    @Override
    public void onInit() {
        registerButton(new ActionButton("back", new ButtonState("none", "back", WolfyUtilities.getSkullViaURL("864f779a8e3ffa231143fa69b96b14ee35c16d669e19c75fd1a7da4bf306c"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            PlayerCache cache = CustomCrafting.getPlayerCache(player);
            KnowledgeBook book = cache.getKnowledgeBook();
            book.stopTimerTask();
            if (book.getCustomRecipe() == null) {
                guiHandler.openPreviousInv();
            } else {
                book.setCustomRecipe(null);
            }
            return true;
        })));
        registerButton(new ActionButton("next_page", new ButtonState("next_page", WolfyUtilities.getSkullViaURL("c86185b1d519ade585f184c34f3f3e20bb641deb879e81378e4eaf209287"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            KnowledgeBook book = CustomCrafting.getPlayerCache(player).getKnowledgeBook();
            book.setPage(book.getPage() + 1);
            return true;
        })));
        registerButton(new ActionButton("previous_page", new ButtonState("previous_page", WolfyUtilities.getSkullViaURL("ad73cf66d31b83cd8b8644c15958c1b73c8d97323b801170c1d8864bb6a846d"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            KnowledgeBook book = CustomCrafting.getPlayerCache(player).getKnowledgeBook();
            book.setPage(book.getPage() > 0 ? book.getPage() - 1 : 0);
            return true;
        })));
        registerButton(new MultipleChoiceButton("workbench_filter_button", new ButtonState("workbench_filter_button.all", Material.CRAFTING_TABLE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            CustomCrafting.getPlayerCache(player).getKnowledgeBook().setWorkbenchFilter(KnowledgeBook.WorkbenchFilter.ADVANCED);
            return true;
        }), new ButtonState("workbench_filter_button.advanced", new ItemBuilder(Material.CRAFTING_TABLE).addUnsafeEnchantment(Enchantment.DURABILITY, 0).create(), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            CustomCrafting.getPlayerCache(player).getKnowledgeBook().setWorkbenchFilter(KnowledgeBook.WorkbenchFilter.NORMAL);
            return true;
        }), new ButtonState("workbench_filter_button.normal", Material.CRAFTING_TABLE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            CustomCrafting.getPlayerCache(player).getKnowledgeBook().setWorkbenchFilter(KnowledgeBook.WorkbenchFilter.ALL);
            return true;
        })));
        registerButton(new ToggleButton("permission", new ButtonState("permission.disabled", Material.RED_CONCRETE, (guiHandler, player, inventory, i, inventoryClickEvent) -> true), new ButtonState("permission.enabled", Material.GREEN_CONCRETE, (guiHandler, player, inventory, i, inventoryClickEvent) -> true)));
        for (int i = 0; i < 45; i++) {
            registerButton(new RecipeBookContainerButton(i));
        }
    }

    @EventHandler
    public void onUpdate(GuiUpdateEvent event) {
        if (event.verify(this)) {
            Player player = event.getPlayer();
            PlayerCache cache = CustomCrafting.getPlayerCache(player);
            KnowledgeBook knowledgeBook = cache.getKnowledgeBook();
            for (int i = 1; i < 8; i++) {
                event.setButton(i, "none", "glass_white");
            }
            event.setButton(0, "back");
            if (knowledgeBook.getCustomRecipe() == null) {
                event.setButton(2, "previous_page");
                event.setButton(6, "next_page");
                List<CustomRecipe> recipes = new ArrayList<>();
                if (knowledgeBook.getSetting().equals(Setting.WORKBENCH)) {
                    KnowledgeBook.WorkbenchFilter workbenchFilter = knowledgeBook.getWorkbenchFilter();
                    event.setButton(8, "workbench_filter_button");
                    for (CraftingRecipe recipe : CustomCrafting.getRecipeHandler().getCraftingRecipes()) {
                        if (!recipe.needsPermission() || (player.hasPermission("customcrafting.craft.*") || player.hasPermission("customcrafting.craft." + recipe.getId()) || player.hasPermission("customcrafting.craft." + recipe.getId().split(":")[0]))) {
                            if (!CustomCrafting.getRecipeHandler().getDisabledRecipes().contains(recipe.getId())) {
                                if (workbenchFilter.equals(KnowledgeBook.WorkbenchFilter.ALL) || (workbenchFilter.equals(KnowledgeBook.WorkbenchFilter.NORMAL) && !recipe.needsAdvancedWorkbench()) || (workbenchFilter.equals(KnowledgeBook.WorkbenchFilter.ADVANCED) && recipe.needsAdvancedWorkbench())) {
                                    recipes.add(recipe);
                                }
                            }
                        }
                    }
                } else {
                    recipes.addAll(CustomCrafting.getRecipeHandler().getRecipes(knowledgeBook.getSetting()));
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
            } else {
                switch (knowledgeBook.getSetting()) {
                    case WORKBENCH:
                        CraftingRecipe craftingRecipe = (CraftingRecipe) knowledgeBook.getCustomRecipe();
                        if (!craftingRecipe.getIngredients().isEmpty()) {
                            if (craftingRecipe.needsAdvancedWorkbench()) {
                                for (int i = 1; i < 8; i++) {
                                    event.setButton(i, "none", "glass_purple");
                                }
                                for (int i = 45; i < 54; i++) {
                                    event.setButton(i, "none", "glass_purple");
                                }
                            }
                            //event.setItem(22, "recipe_creator", craftingRecipe.isShapeless() ? "workbench.shapeless_on" : "workbench.shapeless_off");
                            int invSlot;
                            for (int i = 0; i < 9; i++) {
                                invSlot = 10 + i + (i / 3) * 6;
                                List<CustomItem> variants = craftingRecipe.getIngredients(i);

                                int variant = knowledgeBook.getTimerTimings().getOrDefault(i, 0);

                                event.setItem(invSlot, variants.isEmpty() ? new ItemStack(Material.AIR) : variants.get(variant));
                            }
                            event.setItem(24, craftingRecipe.getCustomResult());
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
                                    if(craftingRecipe.getCustomResults().size() > 1){
                                        for (CustomItem customItem : craftingRecipe.getCustomResults()) {
                                            if (!customItem.hasPermission() || player.hasPermission(customItem.getPermission())) {
                                                if(customItem.getType()!=Material.AIR){
                                                    ItemBuilder itemBuilder = new ItemBuilder(customItem.getRealItem());
                                                    itemBuilder.addLoreLine("§7"+(customItem.getRarityPercentage()*100)+"% possibility");
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
                                    }else{
                                        event.setItem(24, craftingRecipe.getCustomResult().getRealItem());
                                    }
                                }, 1, 20));
                            }
                        }
                        break;
                    case CAMPFIRE:
                    case BLAST_FURNACE:
                    case SMOKER:
                    case FURNACE:
                        CustomCookingRecipe<CookingConfig> furnaceRecipe = (CustomCookingRecipe<CookingConfig>) knowledgeBook.getCustomRecipe();
                        if (furnaceRecipe != null) {
                            if (knowledgeBook.getTimerTask() == -1) {
                                AtomicInteger i = new AtomicInteger();
                                knowledgeBook.setTimerTask(Bukkit.getScheduler().scheduleSyncRepeatingTask(CustomCrafting.getInst(), () -> {
                                    if (i.get() == 0) {
                                        event.setButton(23, "none", "glass_gray");
                                        event.setButton(22, "none", "glass_gray");
                                        event.setButton(21, "none", "glass_gray");
                                    } else if (i.get() == 1) {
                                        event.setItem(21, new ItemStack(Material.YELLOW_CONCRETE));
                                    } else if (i.get() == 2) {
                                        event.setItem(21, new ItemStack(Material.ORANGE_CONCRETE));
                                        event.setItem(22, new ItemStack(Material.YELLOW_CONCRETE));
                                    } else {
                                        event.setItem(21, new ItemStack(Material.RED_CONCRETE_POWDER));
                                        event.setItem(22, new ItemStack(Material.ORANGE_CONCRETE));
                                        event.setItem(23, new ItemStack(Material.YELLOW_CONCRETE));
                                    }
                                    if (i.get() < 3) {
                                        i.getAndIncrement();
                                    } else {
                                        i.set(0);
                                    }
                                }, 1, 4));
                            }
                            event.setItem(20, furnaceRecipe.getSource().get(0).getRealItem());
                            event.setItem(24, furnaceRecipe.getCustomResult().getRealItem());
                        }
                        break;
                    case ANVIL:
                        CustomAnvilRecipe customAnvilRecipe = (CustomAnvilRecipe) knowledgeBook.getCustomRecipe();
                        List<CustomItem> inputLeft = customAnvilRecipe.getInputLeft();
                        List<CustomItem> inputRight = customAnvilRecipe.getInputRight();
                        HashMap<Integer, Integer> timerTimings = knowledgeBook.getTimerTimings();
                        event.setItem(19, inputLeft.isEmpty() ? new ItemStack(Material.AIR) : inputLeft.get(timerTimings.getOrDefault(0, 0)));
                        event.setItem(21, inputRight.isEmpty() ? new ItemStack(Material.AIR) : inputRight.get(timerTimings.getOrDefault(1, 0)));
                        if (customAnvilRecipe.getMode().equals(CustomAnvilRecipe.Mode.RESULT)) {
                            event.setItem(25, customAnvilRecipe.getCustomResult());
                        }
                        if (knowledgeBook.getTimerTask() == -1) {
                            knowledgeBook.setTimerTask(Bukkit.getScheduler().scheduleSyncRepeatingTask(CustomCrafting.getInst(), () -> {
                                HashMap<Integer, Integer> timings = knowledgeBook.getTimerTimings();
                                for (int i = 0; i < 2; i++) {
                                    List<CustomItem> variants = i == 0 ? customAnvilRecipe.getInputLeft() : customAnvilRecipe.getInputRight();
                                    if (!variants.isEmpty()) {
                                        int variant = timings.getOrDefault(0, 0);
                                        event.setItem(i == 0 ? 19 : 21, variants.get(variant).getRealItem());
                                        if (++variant < variants.size()) {
                                            timings.put(i, variant);
                                        } else {
                                            timings.put(i, 0);
                                        }
                                    } else {
                                        event.setItem(i == 0 ? 19 : 21, new ItemStack(Material.AIR));
                                    }
                                }
                                if (customAnvilRecipe.getMode().equals(CustomAnvilRecipe.Mode.RESULT)) {
                                    event.setItem(25, customAnvilRecipe.getCustomResult().getRealItem());
                                    List<ItemStack> variants = new ArrayList<>();
                                    if(customAnvilRecipe.getCustomResults().size() > 1){
                                        for (CustomItem customItem : customAnvilRecipe.getCustomResults()) {
                                            if (!customItem.hasPermission() || player.hasPermission(customItem.getPermission())) {
                                                if(customItem.getType()!=Material.AIR){
                                                    ItemBuilder itemBuilder = new ItemBuilder(customItem.getRealItem());
                                                    itemBuilder.addLoreLine("§7"+(customItem.getRarityPercentage()*100)+"% possibility");
                                                    variants.add(itemBuilder.create());
                                                }
                                            }
                                        }
                                        int variant = timings.getOrDefault(9, 0);
                                        event.setItem(25, variants.isEmpty() ? new ItemStack(Material.AIR) : variants.get(variant));
                                        if (++variant < variants.size()) {
                                            timings.put(9, variant);
                                        } else {
                                            timings.put(9, 0);
                                        }
                                    }else{
                                        event.setItem(25, customAnvilRecipe.getCustomResult().getRealItem());
                                    }
                                }
                            }, 1, 20));
                        }
                        break;
                    case STONECUTTER:
                        //TODO STONECUTTER
                        CustomStonecutterRecipe stonecutterRecipe = (CustomStonecutterRecipe) knowledgeBook.getCustomRecipe();

                        //TODO: NEW RESULT SYSTEM!
                        event.setItem(20, stonecutterRecipe.getSource().get(0));
                        event.setItem(24, stonecutterRecipe.getCustomResult().getRealItem());
                        break;
                }
            }

        }
    }
}
