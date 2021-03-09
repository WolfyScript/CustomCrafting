package me.wolfyscript.customcrafting.utils;

import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.custom_items.references.APIReference;
import me.wolfyscript.utilities.api.inventory.tags.CustomTag;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.annotation.JsonIgnore;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.Registry;
import me.wolfyscript.utilities.util.inventory.InventoryUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class RecipeItemStack {

    @JsonIgnore
    private final Set<CustomItem> choices;

    private Set<APIReference> items;
    private Set<NamespacedKey> tags;

    public RecipeItemStack() {
        this.items = new HashSet<>();
        this.tags = new HashSet<>();
        this.choices = new HashSet<>();
    }

    public RecipeItemStack(Set<APIReference> items, Set<NamespacedKey> tags) {
        this.items = items;
        this.tags = tags;
        this.choices = new HashSet<>();
    }

    public Set<NamespacedKey> getTags() {
        return tags;
    }

    public void setTags(Set<NamespacedKey> tags) {
        this.tags = tags;
    }

    public Set<APIReference> getItems() {
        return items;
    }

    public void setItems(Set<APIReference> items) {
        this.items = items;
    }

    public void buildChoices() {
        this.choices.clear();
        this.choices.addAll(items.parallelStream().map(CustomItem::of).collect(Collectors.toSet()));
        tags.stream().map(namespacedKey -> {
            if (namespacedKey.getNamespace().equals("minecraft")) {
                String[] key = namespacedKey.getKey().split("/", 2);
                if (key.length > 1 && (key[0].equals("blocks") || key[0].equals("items"))) {
                    Tag<Material> tag = Bukkit.getTag(key[0], org.bukkit.NamespacedKey.minecraft(key[1]), Material.class);
                    if (tag != null) {
                        return tag.getValues().parallelStream().map(CustomItem::new).collect(Collectors.toSet());
                    }
                }
            } else {
                CustomTag<CustomItem> tag = Registry.ITEM_TAGS.getTag(namespacedKey);
                if (tag != null) {
                    return tag.getValues();
                }
            }
            return null;
        }).filter(Objects::nonNull).distinct().forEach(this.choices::addAll);
    }

    public Set<CustomItem> getChoices() {
        return choices;
    }

    public boolean isEmpty() {
        return InventoryUtils.isCustomItemsListEmpty(new ArrayList<>(this.choices));
    }

    public boolean test(ItemStack itemStack, boolean exactMatch) {
        if (itemStack == null) return false;
        return choices.stream().anyMatch(customItem -> customItem.isSimilar(itemStack, exactMatch));
    }

    public CustomItem check(ItemStack itemStack, boolean exactMatch) {
        if (itemStack == null) return null;
        return choices.stream().filter(customItem -> customItem.isSimilar(itemStack, exactMatch)).findFirst().orElse(null);
    }


}
