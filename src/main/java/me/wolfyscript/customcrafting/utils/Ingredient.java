package me.wolfyscript.customcrafting.utils;

import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.custom_items.references.APIReference;
import me.wolfyscript.utilities.api.inventory.custom_items.references.VanillaRef;
import me.wolfyscript.utilities.api.inventory.tags.CustomTag;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.annotation.JsonIgnore;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.Registry;
import me.wolfyscript.utilities.util.inventory.InventoryUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Ingredient {

    @JsonIgnore
    private final List<CustomItem> choices;

    private List<APIReference> items;
    private List<NamespacedKey> tags;

    public Ingredient() {
        this.items = new ArrayList<>();
        this.tags = new ArrayList<>();
        this.choices = new ArrayList<>();
    }

    public Ingredient(Material material) {
        this(new ItemStack(material));
    }

    public Ingredient(ItemStack itemStack) {
        this();
        this.items = Arrays.asList(new VanillaRef(itemStack));
    }

    public Ingredient(Material... materials) {
        this();
        this.items = Arrays.stream(materials).map(material -> new VanillaRef(new ItemStack(material))).collect(Collectors.toList());
    }

    public Ingredient(ItemStack... items) {
        this();
        this.items = Arrays.stream(items).map(VanillaRef::new).collect(Collectors.toList());
    }

    public Ingredient(List<Material> items, NamespacedKey... tags) {
        this();
        this.items = items.stream().map(material -> new VanillaRef(new ItemStack(material))).collect(Collectors.toList());
        this.tags = Arrays.asList(tags);
    }

    public Ingredient(List<APIReference> items) {
        this();
        this.items = items;
    }

    public Ingredient(List<APIReference> items, List<NamespacedKey> tags) {
        this();
        this.items = items;
        this.tags = tags;
    }

    public List<NamespacedKey> getTags() {
        return tags;
    }

    public void setTags(List<NamespacedKey> tags) {
        this.tags = tags;
    }

    public List<APIReference> getItems() {
        return items;
    }

    public void setItems(List<APIReference> items) {
        this.items = items;
    }

    public void put(int variantSlot, CustomItem variant) {
        if (this.items.size() > variantSlot) {
            this.items.set(variantSlot, variant.getApiReference());
        } else {
            this.items.add(variant.getApiReference());
        }
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

    public List<CustomItem> getChoices() {
        return choices;
    }

    public List<CustomItem> getChoices(Player player) {
        return getChoicesStream().filter(customItem -> !customItem.hasPermission() || player.hasPermission(customItem.getPermission())).collect(Collectors.toList());
    }

    public Stream<CustomItem> getChoicesStream(){
        return choices.stream();
    }

    public List<ItemStack> getBukkitChoices(){
        return getChoicesStream().map(CustomItem::create).collect(Collectors.toList());
    }

    public List<ItemStack> getBukkitChoices(Player player){
        return getChoicesStream().filter(customItem -> !customItem.hasPermission() || player.hasPermission(customItem.getPermission())).map(CustomItem::create).collect(Collectors.toList());
    }

    public boolean isEmpty() {
        return InventoryUtils.isCustomItemsListEmpty(new ArrayList<>(this.choices));
    }

    public boolean test(ItemStack itemStack, boolean exactMatch) {
        if (itemStack == null) return false;
        return choices.stream().anyMatch(customItem -> customItem.isSimilar(itemStack, exactMatch));
    }

    public Optional<CustomItem> check(ItemStack itemStack, boolean exactMatch) {
        if (itemStack == null) return Optional.empty();
        return choices.stream().filter(customItem -> customItem.isSimilar(itemStack, exactMatch)).findFirst();
    }


}
