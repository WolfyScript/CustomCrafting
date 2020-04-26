package me.wolfyscript.customcrafting.gui.recipebook.buttons;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.data.cache.KnowledgeBook;
import me.wolfyscript.customcrafting.recipes.types.CustomRecipe;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.GuiCluster;
import me.wolfyscript.utilities.api.inventory.GuiHandler;
import me.wolfyscript.utilities.api.inventory.GuiWindow;
import me.wolfyscript.utilities.api.inventory.button.Button;
import me.wolfyscript.utilities.api.inventory.button.ButtonType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class IngredientContainerButton extends Button {

    private final CustomCrafting customCrafting;
    private HashMap<GuiHandler, List<CustomItem>> variantsMap = new HashMap<>();
    private HashMap<GuiHandler, Integer> timings = new HashMap<>();

    private HashMap<GuiHandler, BukkitTask> tasks = new HashMap<>();

    public IngredientContainerButton(int slot, CustomCrafting customCrafting) {
        super("ingredient.container_" + slot, ButtonType.DUMMY);
        this.customCrafting = customCrafting;
    }

    @Override
    public void init(GuiWindow guiWindow) {
        //NOT NEEDED
    }

    @Override
    public void init(String s, WolfyUtilities wolfyUtilities) {
        //NOT NEEDED
    }

    public static void resetButtons(GuiHandler guiHandler) {
        GuiCluster cluster = guiHandler.getApi().getInventoryAPI().getGuiCluster("recipe_book");
        for (int i = 0; i < 45; i++) {
            IngredientContainerButton button = (IngredientContainerButton) cluster.getButton("ingredient.container_" + i);
            if (button.getVariantsMap(guiHandler) != null) {
                if (button.getTask(guiHandler) != null) {
                    button.getTask(guiHandler).cancel();
                    button.setTask(guiHandler, null);
                }
                button.setVariants(guiHandler, null);
                button.setTiming(guiHandler, 0);
            }
        }
    }

    @Override
    public boolean execute(GuiHandler guiHandler, Player player, Inventory inventory, int slot, InventoryClickEvent event) {
        TestCache cache = (TestCache) guiHandler.getCustomCache();
        KnowledgeBook book = cache.getKnowledgeBook();
        if (getVariantsMap(guiHandler) != null && getTiming(guiHandler) < getVariantsMap(guiHandler).size()) {
            CustomItem customItem = getVariantsMap(guiHandler).get(getTiming(guiHandler));
            List<CustomRecipe> recipes = customCrafting.getRecipeHandler().getRecipes(customItem);
            recipes.remove(book.getCurrentRecipe());
            if (!recipes.isEmpty()) {
                GuiCluster cluster = WolfyUtilities.getAPI(customCrafting).getInventoryAPI().getGuiCluster("recipe_book");
                for (int i = 0; i < 45; i++) {
                    IngredientContainerButton button = (IngredientContainerButton) cluster.getButton("ingredient.container_" + i);
                    if (button.getVariantsMap(guiHandler) != null) {
                        if (button.getTask(guiHandler) != null) {
                            button.getTask(guiHandler).cancel();
                            button.setTask(guiHandler, null);
                        }
                        button.setVariants(guiHandler, null);
                        button.setTiming(guiHandler, 0);
                    }
                }
                book.setSubFolder(book.getSubFolder() + 1);
                book.setSubFolderPage(0);
                book.getResearchItems().add(customItem);
                book.setSubFolderRecipes(recipes);
                book.applyRecipeToButtons(guiHandler, recipes.get(0));
            }
        }
        return true;
    }

    @Override
    public void render(GuiHandler guiHandler, Player player, Inventory inventory, int slot, boolean help) {
        List<CustomItem> variants = getVariantsMap(guiHandler);
        inventory.setItem(slot, variants.isEmpty() ? new ItemStack(Material.AIR) : variants.get(getTiming(guiHandler)));
        if (getTask(guiHandler) == null) {
            setTask(guiHandler, Bukkit.getScheduler().runTaskTimerAsynchronously(guiHandler.getApi().getPlugin(), () -> {
                if (player != null && inventory != null && slot < inventory.getSize()) {
                    if (!variants.isEmpty()) {
                        int variant = getTiming(guiHandler);
                        inventory.setItem(slot, variants.isEmpty() ? new ItemStack(Material.AIR) : variants.get(variant));
                        if (++variant < variants.size()) {
                            setTiming(guiHandler, variant);
                        } else {
                            setTiming(guiHandler, 0);
                        }
                    }
                }
            }, 1, 20));
        }
    }

    public void setTiming(GuiHandler guiHandler, int timing) {
        timings.put(guiHandler, timing);
    }

    public int getTiming(GuiHandler guiHandler) {
        return timings.getOrDefault(guiHandler, 0);
    }

    public List<CustomItem> getVariantsMap(GuiHandler guiHandler) {
        return variantsMap.getOrDefault(guiHandler, new ArrayList<>());
    }

    public void setVariants(GuiHandler guiHandler, List<CustomItem> variants) {
        if (variants != null) {
            Iterator<CustomItem> iterator = variants.iterator();
            while (iterator.hasNext()) {
                CustomItem customItem = iterator.next();
                if (!customItem.hasPermission()) {
                    continue;
                }
                if (!guiHandler.getPlayer().hasPermission(customItem.getPermission())) {
                    iterator.remove();
                }
            }
        }
        this.variantsMap.put(guiHandler, variants);
    }

    public void setTask(GuiHandler guiHandler, BukkitTask task) {
        tasks.put(guiHandler, task);
    }

    public BukkitTask getTask(GuiHandler guiHandler) {
        return tasks.get(guiHandler);
    }
}
