package me.wolfyscript.customcrafting.utils;

import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.tags.CustomTag;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

public class RecipeItemStack {

    private final Set<CustomItem> choices;
    private Set<CustomItem> items;
    private Set<CustomTag<CustomItem>> tags;

    public RecipeItemStack(Set<CustomItem> items, Set<CustomTag<CustomItem>> tags) {
        this.items = items;
        this.tags = tags;
        this.choices = new HashSet<>();
    }

    public Set<CustomTag<CustomItem>> getTags() {
        return tags;
    }

    public void setTags(Set<CustomTag<CustomItem>> tags) {
        this.tags = tags;
    }

    public Set<CustomItem> getItems() {
        return items;
    }

    public void setItems(Set<CustomItem> items) {
        this.items = items;
    }

    public void buildChoices() {
        this.choices.clear();
        this.choices.addAll(items);
        tags.stream().map(CustomTag::getValues).distinct().forEach(customItems -> this.choices.addAll(customItems));
    }

    public Set<CustomItem> getChoices() {
        return choices;
    }

    public boolean test(ItemStack itemStack) {


        return true;
    }


}
