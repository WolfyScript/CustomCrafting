package me.wolfyscript.customcrafting.recipes.types;

import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.CCPlayerData;
import me.wolfyscript.customcrafting.data.cache.KnowledgeBook;
import me.wolfyscript.customcrafting.gui.recipebook.buttons.IngredientContainerButton;
import me.wolfyscript.customcrafting.recipes.Condition;
import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.utils.PlayerUtil;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.custom_items.references.APIReference;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.GuiWindow;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.core.JsonGenerator;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.SerializerProvider;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.CookingRecipe;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public abstract class CustomCookingRecipe<C extends CustomCookingRecipe<?, ?>, T extends CookingRecipe<?>> extends CustomRecipe<C> implements ICustomVanillaRecipe<T> {

    private List<CustomItem> result;
    private List<CustomItem> source;
    private float exp;
    private int cookingTime;

    public CustomCookingRecipe(NamespacedKey namespacedKey, JsonNode node) {
        super(namespacedKey, node);
        this.exp = node.path("exp").floatValue();
        this.cookingTime = node.path("cooking_time").asInt();
        {
            List<CustomItem> results = new ArrayList<>();
            JsonNode resultNode = node.path("source");
            if (resultNode.isObject()) {
                results.add(new CustomItem(mapper.convertValue(resultNode, APIReference.class)));
                JsonNode variantsNode = resultNode.path("variants");
                for (JsonNode jsonNode : variantsNode) {
                    results.add(new CustomItem(mapper.convertValue(jsonNode, APIReference.class)));
                }
            } else {
                resultNode.elements().forEachRemaining(n -> results.add(new CustomItem(mapper.convertValue(n, APIReference.class))));
            }
            this.source = results.stream().filter(customItem -> !ItemUtils.isAirOrNull(customItem)).collect(Collectors.toList());
        }
    }

    public CustomCookingRecipe() {
        super();
        this.result = new ArrayList<>();
        this.source = new ArrayList<>();
        this.exp = 0;
        this.cookingTime = 80;
    }

    public CustomCookingRecipe(CustomCookingRecipe<?, ?> customCookingRecipe) {
        super(customCookingRecipe);
        this.result = customCookingRecipe.getResults();
        this.source = customCookingRecipe.getSource();
        this.exp = customCookingRecipe.getExp();
        this.cookingTime = customCookingRecipe.getCookingTime();
    }

    public List<CustomItem> getSource() {
        return new ArrayList<>(this.source);
    }

    public void setSource(List<CustomItem> source) {
        this.source = source;
    }

    public void setSource(int variant, CustomItem ingredient) {
        if (variant < source.size()) {
            source.set(variant, ingredient);
        } else {
            source.add(ingredient);
        }
    }

    @Override
    public List<CustomItem> getResults() {
        return new ArrayList<>(this.result);
    }

    @Override
    public void setResult(List<CustomItem> result) {
        this.result = result;
    }

    public void setResult(int variant, CustomItem ingredient) {
        if (variant < result.size()) {
            result.set(variant, ingredient);
        } else {
            result.add(ingredient);
        }
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

    @Override
    public void prepareMenu(GuiHandler<CCCache> guiHandler, GuiCluster<CCCache> cluster) {
        Player player = guiHandler.getPlayer();
        ((IngredientContainerButton) cluster.getButton("ingredient.container_11")).setVariants(guiHandler, getSource().stream().filter(customItem -> !customItem.hasPermission() || player.hasPermission(customItem.getPermission())).collect(Collectors.toList()));
        ((IngredientContainerButton) cluster.getButton("ingredient.container_24")).setVariants(guiHandler, getResults().stream().filter(customItem -> !customItem.hasPermission() || player.hasPermission(customItem.getPermission())).collect(Collectors.toList()));
    }

    @Override
    public void renderMenu(GuiWindow<CCCache> guiWindow, GuiUpdate<CCCache> event) {
        CCPlayerData data = PlayerUtil.getStore(event.getPlayer());
        KnowledgeBook book = event.getGuiHandler().getCustomCache().getKnowledgeBook();
        event.setButton(0, "back");
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
        {
            gen.writeArrayFieldStart("result");
            for (CustomItem customItem : result) {
                gen.writeObject(customItem.getApiReference());
            }
            gen.writeEndArray();
        }
        {
            gen.writeArrayFieldStart("source");
            for (CustomItem customItem : source) {
                gen.writeObject(customItem.getApiReference());
            }
            gen.writeEndArray();
        }
    }
}
