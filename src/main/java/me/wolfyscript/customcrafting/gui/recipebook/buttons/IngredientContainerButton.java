package me.wolfyscript.customcrafting.gui.recipebook.buttons;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.data.cache.KnowledgeBook;
import me.wolfyscript.customcrafting.recipes.types.ICustomRecipe;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiWindow;
import me.wolfyscript.utilities.api.inventory.gui.button.Button;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.*;

public class IngredientContainerButton extends Button<TestCache> {

    private final CustomCrafting customCrafting;
    private final HashMap<GuiHandler<TestCache>, List<CustomItem>> variantsMap = new HashMap<>();
    private final HashMap<GuiHandler<TestCache>, Integer> timings = new HashMap<>();

    private final HashMap<GuiHandler<TestCache>, Runnable> tasks = new HashMap<>();
    private final HashMap<GuiHandler<TestCache>, Runnable> tasksQueue = new HashMap<>();

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

    public static void resetButtons(GuiHandler<TestCache> guiHandler) {
        GuiCluster<TestCache> cluster = guiHandler.getInvAPI().getGuiCluster("recipe_book");
        for (int i = 0; i < 54; i++) {
            Button btn = cluster.getButton("ingredient.container_" + i);
            if (btn != null) {
                IngredientContainerButton button = (IngredientContainerButton) btn;
                if (button.getVariantsMap(guiHandler) != null) {
                    if (button.getTask(guiHandler) != null) {
                        button.setTask(guiHandler, null);
                    }
                    button.setVariants(guiHandler, null);
                    button.setTiming(guiHandler, 0);
                }
            }
        }
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
        KnowledgeBook book = cache.getKnowledgeBook();
        if (getVariantsMap(guiHandler) != null && getTiming(guiHandler) < getVariantsMap(guiHandler).size()) {
            CustomItem customItem = getVariantsMap(guiHandler).get(getTiming(guiHandler));
            List<ICustomRecipe<?>> recipes = customCrafting.getRecipeHandler().getAvailableRecipesBySimilarResult(customItem.create(), player);
            recipes.remove(book.getCurrentRecipe());
            if (!recipes.isEmpty()) {
                GuiCluster<TestCache> cluster = guiHandler.getInvAPI().getGuiCluster("recipe_book");
                for (int i = 0; i < 54; i++) {
                    IngredientContainerButton button = (IngredientContainerButton) cluster.getButton("ingredient.container_" + i);
                    if (button.getVariantsMap(guiHandler) != null) {
                        if (button.getTask(guiHandler) != null) {
                            button.setTask(guiHandler, null);
                        }
                        button.setVariants(guiHandler, null);
                        button.setTiming(guiHandler, 0);
                    }
                }
                book.stopTimerTask();
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
    public void render(GuiHandler<TestCache> guiHandler, Player player, Inventory inventory, int slot, boolean help) {
        List<CustomItem> variants = getVariantsMap(guiHandler);
        inventory.setItem(slot, variants.isEmpty() ? new ItemStack(Material.AIR) : variants.get(getTiming(guiHandler)).create());
        if (getTask(guiHandler) == null) {
            setTask(guiHandler, () -> {
                if (player != null && slot < inventory.getSize()) {
                    if (!variants.isEmpty()) {
                        int variant = getTiming(guiHandler);
                        inventory.setItem(slot, variants.get(variant).create());
                        setTiming(guiHandler, ++variant < variants.size() ? variant : 0);
                    }
                }
            });
        }
    }

    public void setTiming(GuiHandler<TestCache> guiHandler, int timing) {
        timings.put(guiHandler, timing);
    }

    public int getTiming(GuiHandler<TestCache> guiHandler) {
        return timings.getOrDefault(guiHandler, 0);
    }

    public List<CustomItem> getVariantsMap(GuiHandler<TestCache> guiHandler) {
        return variantsMap.getOrDefault(guiHandler, new ArrayList<>());
    }

    public void setVariants(GuiHandler<TestCache> guiHandler, List<CustomItem> variants) {
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

    public void setTask(GuiHandler<TestCache> guiHandler, Runnable task) {
        tasksQueue.put(guiHandler, task);
    }

    public Runnable getTask(GuiHandler<TestCache> guiHandler) {
        return tasks.get(guiHandler);
    }

    public Collection<Runnable> getTasks(){
        return tasks.values();
    }

    public void updateTasks(){
        tasks.putAll(tasksQueue);
    }
}
