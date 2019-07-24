package me.wolfyscript.customcrafting.gui.list;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.PlayerCache;
import me.wolfyscript.customcrafting.data.cache.KnowledgeBook;
import me.wolfyscript.customcrafting.gui.ExtendedGuiWindow;
import me.wolfyscript.customcrafting.gui.Setting;
import me.wolfyscript.customcrafting.items.CustomItem;
import me.wolfyscript.customcrafting.recipes.CustomCookingRecipe;
import me.wolfyscript.customcrafting.recipes.anvil.CustomAnvilRecipe;
import me.wolfyscript.customcrafting.recipes.stonecutter.CustomStonecutterRecipe;
import me.wolfyscript.customcrafting.recipes.workbench.CraftingRecipe;
import me.wolfyscript.customcrafting.recipes.CustomRecipe;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.GuiAction;
import me.wolfyscript.utilities.api.inventory.GuiClick;
import me.wolfyscript.utilities.api.inventory.GuiUpdateEvent;
import me.wolfyscript.utilities.api.inventory.InventoryAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

public class RecipeBook extends ExtendedGuiWindow {

    public RecipeBook(InventoryAPI inventoryAPI) {
        super("recipe_book", inventoryAPI, 45);
    }

    @Override
    public void onInit() {
        createItem("next_page", WolfyUtilities.getCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzg2MTg1YjFkNTE5YWRlNTg1ZjE4NGMzNGYzZjNlMjBiYjY0MWRlYjg3OWU4MTM3OGU0ZWFmMjA5Mjg3In19fQ=="));
        createItem("previous_page", WolfyUtilities.getCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWQ3M2NmNjZkMzFiODNjZDhiODY0NGMxNTk1OGMxYjczYzhkOTczMjNiODAxMTcwYzFkODg2NGJiNmE4NDZkIn19fQ=="));

        createItem("workbench", Material.CRAFTING_TABLE);
        createItem("furnace", Material.FURNACE);
        createItem("anvil", Material.ANVIL);

        if (WolfyUtilities.hasVillagePillageUpdate()) {
            createItem("blast_furnace", Material.BLAST_FURNACE);
            createItem("smoker", Material.SMOKER);
            createItem("campfire", Material.CAMPFIRE);
            createItem("stonecutter", Material.STONECUTTER);
        }
    }

    @EventHandler
    public void onUpdate(GuiUpdateEvent event) {
        if (event.verify(this)) {
            Player player = event.getPlayer();
            PlayerCache cache = CustomCrafting.getPlayerCache(player);
            KnowledgeBook knowledgeBook = cache.getKnowledgeBook();
            for (int i = 36; i < 45; i++) {
                event.setItem(i, "none", "glass_white");
            }
            if (!knowledgeBook.getSetting().equals(Setting.MAIN_MENU)) {
                for (int i = 1; i < 9; i++) {
                    event.setItem(i, "none", "glass_white");
                }
                if (knowledgeBook.getRecipeID().isEmpty()) {
                    event.setItem(2, "previous_page");
                    event.setItem(6, "next_page");
                    List<CustomRecipe> recipes = new ArrayList<>();
                    if (knowledgeBook.getSetting().equals(Setting.WORKBENCH)) {
                        for (CraftingRecipe recipe : CustomCrafting.getRecipeHandler().getCraftingRecipes()) {
                            if (!recipe.needsPermission() || (player.hasPermission("customcrafting.craft.*") || player.hasPermission("customcrafting.craft." + recipe.getId()) || player.hasPermission("customcrafting.craft." + recipe.getId().split(":")[0]))) {
                                if(!CustomCrafting.getRecipeHandler().getDisabledRecipes().contains(recipe.getId())){
                                    recipes.add(recipe);
                                }
                            }
                        }
                    } else {
                        recipes.addAll(CustomCrafting.getRecipeHandler().getRecipes(knowledgeBook.getSetting()));
                    }
                    int maxPages = recipes.size() / 36 + (recipes.size() % 36 > 0 ? 1 : 0);
                    if (knowledgeBook.getPage() >= maxPages) {
                        knowledgeBook.setPage(1);
                    }
                    int item = 0;
                    for (int i = 36 * knowledgeBook.getPage(); item < 36 && i < recipes.size(); i++) {
                        CustomRecipe recipe = recipes.get(i);
                        CustomItem itemStack;
                        if (recipe instanceof CustomAnvilRecipe) {
                            if (!((CustomAnvilRecipe) recipe).getInputLeft().isEmpty()) {
                                itemStack = ((CustomAnvilRecipe) recipe).getInputLeft().get(0).getRealItem();
                            } else {
                                itemStack = ((CustomAnvilRecipe) recipe).getInputRight().get(0).getRealItem();
                            }
                        } else {
                            itemStack = recipe.getCustomResult().getRealItem();
                        }

                        if (itemStack.getType().equals(Material.AIR)) {
                            itemStack = new CustomItem(new ItemStack(Material.STONE));
                            itemStack.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
                            ItemMeta itemMeta = itemStack.getItemMeta();
                            itemMeta.setDisplayName("§r§7" + recipe.getId());
                            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                            itemStack.setItemMeta(itemMeta);
                        }
                        ItemMeta itemMeta = itemStack.getItemMeta();
                        List<String> lore = itemMeta.hasLore() ? itemMeta.getLore() : new ArrayList<>();
                        lore.add(ChatColor.translateAlternateColorCodes('&', api.getLanguageAPI().getActiveLanguage().replaceKeys("$items.recipe_book.lores.click$")) + WolfyUtilities.hideString(";;" + recipe.getId()));
                        itemMeta.setLore(lore);
                        itemStack.setItemMeta(itemMeta);
                        event.setItem(9 + item, itemStack);
                        item++;
                    }
                } else if (knowledgeBook.getCustomRecipe() != null) {
                    switch (knowledgeBook.getSetting()) {
                        case WORKBENCH:
                            CraftingRecipe craftingRecipe = (CraftingRecipe) knowledgeBook.getCustomRecipe();
                            if (!craftingRecipe.getIngredients().isEmpty()) {
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
                                        event.setItem(24, craftingRecipe.getCustomResult().getRealItem());
                                    }, 1, 20));
                                }
                            }
                            break;
                        case CAMPFIRE:
                        case BLAST_FURNACE:
                        case SMOKER:
                        case FURNACE:
                            CustomCookingRecipe furnaceRecipe = (CustomCookingRecipe) knowledgeBook.getCustomRecipe();
                            if (furnaceRecipe != null) {
                                if (knowledgeBook.getTimerTask() == -1) {
                                    AtomicInteger i = new AtomicInteger();
                                    knowledgeBook.setTimerTask(Bukkit.getScheduler().scheduleSyncRepeatingTask(CustomCrafting.getInst(), () -> {
                                        if (i.get() == 0) {
                                            event.setItem(23, "none", "glass_gray");
                                            event.setItem(22, "none", "glass_gray");
                                            event.setItem(21, "none", "glass_gray");
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
                                event.setItem(20, furnaceRecipe.getSource().getRealItem());
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
                                    }
                                }, 1, 20));
                            }
                            break;
                        case STONECUTTER:
                            //TODO STONECUTTER
                            CustomStonecutterRecipe stonecutterRecipe = (CustomStonecutterRecipe) knowledgeBook.getCustomRecipe();

                            event.setItem(20, stonecutterRecipe.getSource().getRealItem());
                            event.setItem(24, stonecutterRecipe.getCustomResult().getRealItem());
                            break;
                    }
                }
            } else {
                //MAINMENU
                event.setItem(0, "none", "glass_white");
                event.setItem(8, "none", "glass_white");
                event.setItem(11, "workbench");
                event.setItem(13, "furnace");
                event.setItem(15, "anvil");
                if (WolfyUtilities.hasVillagePillageUpdate()) {
                    event.setItem(19, "blast_furnace");
                    event.setItem(21, "smoker");
                    event.setItem(23, "campfire");
                    event.setItem(25, "stonecutter");
                }
            }
        }
    }

    @Override
    public boolean onClick(GuiClick guiClick) {
        Player player = guiClick.getPlayer();
        PlayerCache cache = CustomCrafting.getPlayerCache(player);
        KnowledgeBook book = cache.getKnowledgeBook();
        ItemStack itemStack = guiClick.getCurrentItem();
        if (itemStack != null && itemStack.hasItemMeta() && itemStack.getItemMeta().hasLore()) {
            List<String> lore = itemStack.getItemMeta().getLore();
            String line = lore.get(lore.size() - 1);
            if (line.startsWith(WolfyUtilities.translateColorCodes(api.getLanguageAPI().getActiveLanguage().replaceKeys("$items.recipe_book.lores.click$")))) {
                String code = WolfyUtilities.unhideString(line);
                String id = code.split(";;")[1];
                if (code.contains(";;") && id.contains(":")) {
                    book.setRecipeID(id);
                    if (!book.getRecipeID().isEmpty()) {
                        CustomRecipe recipe = CustomCrafting.getRecipeHandler().getRecipe(book.getRecipeID());
                        if (recipe != null) {
                            book.setCustomRecipe(recipe);
                        }
                    }
                }
            }
        }
        update(guiClick.getGuiHandler());
        return true;
    }

    @Override
    public boolean onAction(GuiAction guiAction) {
        PlayerCache cache = CustomCrafting.getPlayerCache(guiAction.getPlayer());
        KnowledgeBook book = cache.getKnowledgeBook();
        switch (guiAction.getAction()) {
            case "previous_page":
                book.setPage(book.getPage() > 0 ? book.getPage()-1 : 0);
                break;
            case "next_page":
                book.setPage(book.getPage()+1);
                break;
            case "back":
                book.stopTimerTask();
                if (book.getRecipeID().isEmpty()) {
                    book.setSetting(Setting.MAIN_MENU);
                } else {
                    book.setRecipeID("");
                }
                break;
            case "workbench":
            case "furnace":
            case "anvil":
            case "blast_furnace":
            case "smoker":
            case "campfire":
            case "stonecutter":
                book.setSetting(Setting.valueOf(guiAction.getAction().toUpperCase(Locale.ROOT)));
                break;
        }
        return true;
    }

}
