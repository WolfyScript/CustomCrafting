package me.wolfyscript.customcrafting.configs.recipebook;

import me.wolfyscript.utilities.api.nms.network.MCByteBuf;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.annotation.*;
import me.wolfyscript.utilities.util.inventory.CreativeModeTab;
import org.bukkit.Material;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@JsonPropertyOrder({"id", "icon", "name", "description"})
public class CategoryFilter extends CategorySettings {

    @JsonIgnore
    private final Set<Material> totalMaterials;
    protected Set<CreativeModeTab> creativeModeTabs;
    protected Set<Material> materials;

    public CategoryFilter() {
        super();
        this.creativeModeTabs = new HashSet<>();
        this.materials = new HashSet<>();
        this.totalMaterials = new HashSet<>();
    }

    public CategoryFilter(CategoryFilter category) {
        super(category);
        this.creativeModeTabs = new HashSet<>(category.creativeModeTabs);
        this.materials = new HashSet<>(category.materials);
        this.totalMaterials = new HashSet<>();
    }

    @JsonGetter
    public Set<Material> getMaterials() {
        return materials;
    }

    @JsonSetter
    public void setMaterials(Set<Material> materials) {
        this.materials = materials;
        this.totalMaterials.addAll(materials);
    }

    @JsonGetter
    public Set<CreativeModeTab> getCreativeModeTabs() {
        return creativeModeTabs;
    }

    @JsonSetter
    @JsonAlias({"itemCategories"})
    public void setCreativeModeTabs(Set<CreativeModeTab> itemCategories) {
        this.creativeModeTabs = itemCategories;
        this.totalMaterials.addAll(itemCategories.stream().flatMap(creativeModeTab -> creativeModeTab.getMaterials().stream()).collect(Collectors.toSet()));
    }

    public boolean filter(RecipeContainer container) {
        if (!groups.isEmpty() && container.getGroup() != null && !groups.contains(container.getGroup())) {
            return false;
        }
        if (container.getRecipe() != null && (!recipes.isEmpty() && !recipes.contains(container.getRecipe())) || (!namespaces.isEmpty() && !namespaces.contains(container.getRecipe().getNamespace()))) {
            return false;
        }
        return container.isValid(totalMaterials);
    }

    @Override
    public void writeToByteBuf(MCByteBuf byteBuf) {
        super.writeToByteBuf(byteBuf);
        writeData(byteBuf);
        writeStringArray(totalMaterials.stream().map(material -> material.getKey().toString()).collect(Collectors.toList()), byteBuf);

    }
}
