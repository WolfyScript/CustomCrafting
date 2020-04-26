package me.wolfyscript.customcrafting.recipes.types;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.PlayerStatistics;
import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.data.cache.KnowledgeBook;
import me.wolfyscript.customcrafting.recipes.Condition;
import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.GuiUpdateEvent;
import me.wolfyscript.utilities.api.inventory.GuiWindow;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public interface CustomCookingRecipe<T extends CookingConfig> extends CustomRecipe<T> {

    List<CustomItem> getSource();

    @Override
    T getConfig();

    @Override
    default void renderMenu(GuiWindow guiWindow, GuiUpdateEvent event) {
        PlayerStatistics playerStatistics = CustomCrafting.getPlayerStatistics(event.getPlayer());
        KnowledgeBook book = ((TestCache) event.getGuiHandler().getCustomCache()).getKnowledgeBook();
        event.setButton(0, "back");
        List<Condition> conditions = getConditions().values().stream().filter(condition -> !condition.getOption().equals(Conditions.Option.IGNORE) && !condition.getId().equals("permission")).collect(Collectors.toList());
        int startSlot = 9 / (conditions.size() + 1);
        int slot = 0;
        for (Condition condition : conditions) {
            if (!condition.getOption().equals(Conditions.Option.IGNORE)) {
                event.setButton(36 + startSlot + slot, "recipe_book", "conditions." + condition.getId());
                slot += 2;
            }
        }
        event.setButton(13, "recipe_book", "cooking.icon");
        event.setButton(20, "none", playerStatistics.getDarkMode() ? "glass_gray" : "glass_white");
        event.setButton(11, "recipe_book", "ingredient.container_11");
        event.setButton(24, "recipe_book", "ingredient.container_24");

        if (book.getTimerTask() == -1) {
            AtomicInteger i = new AtomicInteger();
            book.setTimerTask(Bukkit.getScheduler().scheduleSyncRepeatingTask(event.getWolfyUtilities().getPlugin(), () -> {
                if (i.get() == 0) {
                    event.setButton(23, "none", playerStatistics.getDarkMode() ? "glass_black" : "glass_gray");
                    event.setButton(22, "none", playerStatistics.getDarkMode() ? "glass_black" : "glass_gray");
                    event.setButton(21, "none", playerStatistics.getDarkMode() ? "glass_black" : "glass_gray");
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
    }
}
