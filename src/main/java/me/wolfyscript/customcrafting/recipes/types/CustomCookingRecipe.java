package me.wolfyscript.customcrafting.recipes.types;

import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.CCPlayerData;
import me.wolfyscript.customcrafting.data.cache.KnowledgeBook;
import me.wolfyscript.customcrafting.gui.recipebook.buttons.IngredientContainerButton;
import me.wolfyscript.customcrafting.recipes.Condition;
import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.utils.ItemLoader;
import me.wolfyscript.customcrafting.utils.PlayerUtil;
import me.wolfyscript.customcrafting.utils.recipe_item.Ingredient;
import me.wolfyscript.customcrafting.utils.recipe_item.Result;
import me.wolfyscript.customcrafting.utils.recipe_item.target.FixedResultTarget;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.GuiWindow;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.core.JsonGenerator;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.SerializerProvider;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.CookingRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public abstract class CustomCookingRecipe<C extends CustomCookingRecipe<?, ?>, T extends CookingRecipe<?>> extends CustomRecipe<C, FixedResultTarget> implements ICustomVanillaRecipe<T> {

    private Ingredient source;
    private float exp;
    private int cookingTime;

    public CustomCookingRecipe(NamespacedKey namespacedKey, JsonNode node) {
        super(namespacedKey, node);
        this.exp = node.path("exp").floatValue();
        this.cookingTime = node.path("cooking_time").asInt();
        this.source = ItemLoader.loadIngredient(node.path("source"));
    }

    public CustomCookingRecipe() {
        super();
        this.result = new Result<>();
        this.source = new Ingredient();
        this.exp = 0;
        this.cookingTime = 80;
    }

    public CustomCookingRecipe(CustomCookingRecipe<?, ?> customCookingRecipe) {
        super(customCookingRecipe);
        this.result = customCookingRecipe.getResult();
        this.source = customCookingRecipe.getSource();
        this.exp = customCookingRecipe.getExp();
        this.cookingTime = customCookingRecipe.getCookingTime();
    }

    public Ingredient getSource() {
        return this.source;
    }

    public void setSource(Ingredient source) {
        this.source = source;
    }

    public void setCookingTime(int cookingTime) {
        this.cookingTime = cookingTime;
    }

    public float getExp() {
        return exp;
    }

    public int getCookingTime() {
        return cookingTime;
    }

    public void setExp(float exp) {
        this.exp = exp;
    }

    protected RecipeChoice getRecipeChoice(){
        return isExactMeta() ? new RecipeChoice.ExactChoice(getSource().getChoices().parallelStream().map(CustomItem::create).collect(Collectors.toList())) : new RecipeChoice.MaterialChoice(getSource().getChoices().parallelStream().map(i -> i.create().getType()).collect(Collectors.toList()));
    }

    @Override
    public void prepareMenu(GuiHandler<CCCache> guiHandler, GuiCluster<CCCache> cluster) {
        Player player = guiHandler.getPlayer();
        ((IngredientContainerButton) cluster.getButton("ingredient.container_11")).setVariants(guiHandler, getSource().getChoices(player));
        ((IngredientContainerButton) cluster.getButton("ingredient.container_24")).setVariants(guiHandler, this.getResult().getChoices().stream().filter(customItem -> !customItem.hasPermission() || player.hasPermission(customItem.getPermission())).collect(Collectors.toList()));
    }

    @Override
    public void renderMenu(GuiWindow<CCCache> guiWindow, GuiUpdate<CCCache> event) {
        CCPlayerData data = PlayerUtil.getStore(event.getPlayer());
        KnowledgeBook book = event.getGuiHandler().getCustomCache().getKnowledgeBook();
        List<Condition> conditions = getConditions().values().stream().filter(condition -> !condition.getOption().equals(Conditions.Option.IGNORE) && !condition.getId().equals("permission")).collect(Collectors.toList());
        int startSlot = 9 / (conditions.size() + 1);
        int slot = 0;
        for (Condition condition : conditions) {
            if (!condition.getOption().equals(Conditions.Option.IGNORE)) {
                event.setButton(36 + startSlot + slot, new NamespacedKey("recipe_book", "conditions." + condition.getId()));
                slot += 2;
            }
        }
        event.setButton(13, new NamespacedKey("recipe_book", "cooking.icon"));
        event.setButton(20, new NamespacedKey("none", data.isDarkMode() ? "glass_gray" : "glass_white"));
        event.setButton(11, new NamespacedKey("recipe_book", "ingredient.container_11"));
        event.setButton(24, new NamespacedKey("recipe_book", "ingredient.container_24"));

        if (book.getTimerTask() == null) {
            AtomicInteger i = new AtomicInteger();
            book.setTimerTask(Bukkit.getScheduler().runTaskTimerAsynchronously(event.getGuiHandler().getApi().getPlugin(), () -> {
                if (i.get() == 0) {
                    NamespacedKey glass = new NamespacedKey("none", data.isDarkMode() ? "glass_black" : "glass_gray");
                    event.setButton(23, glass);
                    event.setButton(22, glass);
                    event.setButton(21, glass);
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

    @Override
    public void writeToJson(JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        super.writeToJson(gen, serializerProvider);
        gen.writeNumberField("cooking_time", cookingTime);
        gen.writeNumberField("exp", exp);
        gen.writeObjectField("result", result);
        {
            gen.writeArrayFieldStart("source");
            gen.writeObject(source);
            gen.writeEndArray();
        }
    }
}
