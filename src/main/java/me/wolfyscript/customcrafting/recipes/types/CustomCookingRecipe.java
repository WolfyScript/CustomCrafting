package me.wolfyscript.customcrafting.recipes.types;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.PlayerStatistics;
import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.data.cache.KnowledgeBook;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.GuiUpdateEvent;
import me.wolfyscript.utilities.api.inventory.GuiWindow;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public interface CustomCookingRecipe<T extends CookingConfig> extends CustomRecipe<T> {

    List<CustomItem> getSource();

    @Override
    T getConfig();

    @Override
    default void renderMenu(GuiWindow guiWindow, GuiUpdateEvent event) {
        PlayerStatistics playerStatistics = CustomCrafting.getPlayerStatistics(event.getPlayer());
        KnowledgeBook book = ((TestCache) event.getGuiHandler().getCustomCache()).getKnowledgeBook();
        event.setButton(0, "back");
        event.setButton(22, "recipe_book", "cooking.icon");
        event.setButton(29, "none", playerStatistics.getDarkMode() ? "glass_gray" : "glass_white");
        event.setButton(20, "recipe_book", "ingredient.container_20");
        event.setButton(33, "recipe_book", "ingredient.container_33");
        if (book.getTimerTask() == -1) {
            AtomicInteger i = new AtomicInteger();
            book.setTimerTask(Bukkit.getScheduler().scheduleSyncRepeatingTask(CustomCrafting.getInst(), () -> {
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
            }, 1, 4));
        }
    }
}
