package me.wolfyscript.customcrafting.utils.recipe_item;

import me.wolfyscript.customcrafting.utils.ItemLoader;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class RecipeItemStack {

    @JsonIgnore
    protected final List<CustomItem> choices;

    private List<APIReference> items;
    private List<NamespacedKey> tags;

    public RecipeItemStack() {
        this(new ArrayList<>(), new ArrayList<>());
    }

    public RecipeItemStack(Material... materials) {
        this(Arrays.stream(materials).map(material -> new VanillaRef(new ItemStack(material))).collect(Collectors.toList()), new ArrayList<>());
    }

    public RecipeItemStack(ItemStack... items) {
        this(Arrays.stream(items).map(VanillaRef::new).collect(Collectors.toList()), new ArrayList<>());
    }

    public RecipeItemStack(NamespacedKey... tags) {
        this(new ArrayList<>(), Arrays.asList(tags));
    }

    public RecipeItemStack(APIReference... references) {
        this(Arrays.asList(references), new ArrayList<>());
    }

    public RecipeItemStack(List<APIReference> references, List<NamespacedKey> tags) {
        this.choices = new ArrayList<>();
        this.items = references;
        this.tags = tags;
        buildChoices();
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
        this.choices.addAll(items.stream().map(ItemLoader::load).collect(Collectors.toSet()));
        this.tags.stream().map(namespacedKey -> {
            if (namespacedKey.getNamespace().equals("minecraft")) {
                String[] key = namespacedKey.getKey().split("/", 2);
                if (key.length > 1 && (key[0].equals("blocks") || key[0].equals("items"))) {
                    Tag<Material> tag = Bukkit.getTag(key[0], org.bukkit.NamespacedKey.minecraft(key[1]), Material.class);
                    if (tag != null) {
                        return tag.getValues().stream().map(CustomItem::new).collect(Collectors.toSet());
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

    @JsonIgnore
    public List<CustomItem> getChoices() {
        return new ArrayList<>(choices);
    }

    public List<CustomItem> getChoices(Player player) {
        return getChoicesStream().filter(customItem -> !customItem.hasPermission() || player.hasPermission(customItem.getPermission())).collect(Collectors.toList());
    }

    @JsonIgnore
    public Stream<CustomItem> getChoicesStream() {
        return choices.stream();
    }

    @JsonIgnore
    public List<ItemStack> getBukkitChoices() {
        return getChoicesStream().map(CustomItem::create).collect(Collectors.toList());
    }

    @JsonIgnore
    public int size(){
        return getChoices().size();
    }

    @JsonIgnore
    public boolean isEmpty() {
        return InventoryUtils.isCustomItemsListEmpty(this.choices);
    }

    @Override
    public String toString() {
        return "RecipeItemStack{" +
                "choices=" + choices +
                ", items=" + items +
                ", tags=" + tags +
                '}';
    }
}
