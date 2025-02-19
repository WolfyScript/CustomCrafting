/*
 *       ____ _  _ ____ ___ ____ _  _ ____ ____ ____ ____ ___ _ _  _ ____
 *       |    |  | [__   |  |  | |\/| |    |__/ |__| |___  |  | |\ | | __
 *       |___ |__| ___]  |  |__| |  | |___ |  \ |  | |     |  | | \| |__]
 *
 *       CustomCrafting Recipe creation and management tool for Minecraft
 *                      Copyright (C) 2021  WolfyScript
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.wolfyscript.customcrafting.configs.recipebook;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.*;
import me.wolfyscript.lib.com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({"categoryAlign", "variationCycle", "categories", "filters"})
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RecipeBookConfig {

    private final Map<String, Category> categoryMap = new HashMap<>();
    private final Map<String, CategoryFilter> filters = new HashMap<>();
    private List<String> sortedCategories;
    private List<String> sortedFilters;
    private CategoryAlign categoryAlign = new CategoryAlign();
    private VariationCycle variationCycle = new VariationCycle();
    @JsonIgnore
    private boolean shouldSave = true;

    public RecipeBookConfig(List<String> sortedCategories, List<String> sortedFilters) {
        this.sortedFilters = sortedFilters;
        this.sortedCategories = sortedCategories;
    }

    public RecipeBookConfig() {
        this.sortedFilters = new ArrayList<>();
        this.sortedCategories = new ArrayList<>();
    }

    public RecipeBookConfig(boolean shouldSave) {
        this();
        this.shouldSave = shouldSave;
    }

    public RecipeBookConfig(RecipeBookConfig original) {
        this.sortedCategories = new ArrayList<>(original.getSortedCategories());
        this.sortedFilters = new ArrayList<>(original.getSortedFilters());
        this.categoryMap.putAll(original.categoryMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> new Category(entry.getValue()))));
        this.filters.putAll(original.filters.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> new CategoryFilter(entry.getValue()))));
        this.categoryAlign = new CategoryAlign(original.categoryAlign);
        this.variationCycle = new VariationCycle(original.variationCycle);
    }

    @JsonIgnore
    public boolean isShouldSave() {
        return shouldSave;
    }

    public void registerCategory(String key, Category category) {
        category.setId(key);
        if (!sortedCategories.contains(key)) {
            sortedCategories.add(key);
        }
        categoryMap.put(key, category);
    }

    public void registerFilter(String key, CategoryFilter category) {
        category.setId(key);
        if (!sortedFilters.contains(key)) {
            sortedFilters.add(key);
        }
        filters.put(key, category);
    }

    public void removeFilter(String key) {
        sortedFilters.remove(key);
        filters.remove(key);
    }

    public void removeCategory(String key) {
        sortedCategories.remove(key);
        categoryMap.remove(key);
    }

    public CategoryFilter getFilter(String key) {
        return filters.get(key);
    }

    public CategoryFilter getFilter(int index) {
        if (getSortedFilters().isEmpty()) return null;
        return getFilter(getSortedFilters().get(index));
    }

    public Category getCategory(String key) {
        return categoryMap.get(key);
    }

    public Category getCategory(int index) {
        if (getSortedCategories().isEmpty()) return null;
        return getCategory(getSortedCategories().get(index));
    }

    @JsonIgnore
    public List<String> getSortedFilters() {
        return sortedFilters;
    }

    @JsonIgnore
    public void setSortedFilters(List<String> sortedSwitchCategories) {
        this.sortedFilters = sortedSwitchCategories;
    }

    @JsonIgnore
    public List<String> getSortedCategories() {
        return sortedCategories;
    }

    @JsonIgnore
    public void setSortedCategories(List<String> sortedMainCategories) {
        this.sortedCategories = sortedMainCategories;
    }

    @JsonIgnore
    public Map<String, Category> getCategories() {
        return categoryMap;
    }

    @JsonIgnore
    public Map<String, CategoryFilter> getFilters() {
        return filters;
    }

    public CategoryAlign getCategoryAlign() {
        return categoryAlign;
    }

    public void setCategoryAlign(CategoryAlign categoryAlign) {
        this.categoryAlign = categoryAlign;
    }

    public VariationCycle getVariationCycle() {
        return variationCycle;
    }

    public void setVariationCycle(VariationCycle variationCycle) {
        this.variationCycle = variationCycle;
    }

    public void index(CustomCrafting customCrafting) {
        customCrafting.getApi().getConsole().info("Indexing Recipe Book...");
        Collection<CategoryFilter> filterValues = this.filters.values();
        this.categoryMap.values().forEach(category -> category.index(customCrafting, filterValues));
        customCrafting.getApi().getConsole().info("Indexed Recipe Book!");
    }

    @Override
    public String toString() {
        return "Categories{" +
                "sortedCategories=" + sortedCategories +
                ", sortedFilters=" + sortedFilters +
                ", categories=" + categoryMap +
                ", filters=" + filters +
                '}';
    }

    @JsonGetter("categories")
    private Map<String, Object> getCategoriesSettings() {
        return getSettingsMap(getSortedCategories(), categoryMap.values());
    }

    @JsonSetter("categories")
    private void setCategoriesSettings(ObjectNode node) {
        applySettings(node, Category.class, category -> registerCategory(category.getId(), category), s -> sortedCategories.add(s));
    }

    @JsonGetter("filters")
    private Map<String, Object> getFiltersSettings() {
        return getSettingsMap(getSortedFilters(), filters.values());
    }

    @JsonSetter("filters")
    private void setFilters(ObjectNode node) {
        applySettings(node, CategoryFilter.class, filter -> registerFilter(filter.getId(), filter), s -> sortedFilters.add(s));
    }

    private <T extends CategorySettings> void applySettings(ObjectNode node, Class<T> type, Consumer<T> settings, Consumer<String> sort) {
        if (node.has("sort")) {
            node.path("sort").elements().forEachRemaining(element -> sort.accept(element.asText()));
        }
        node.path("options").elements().forEachRemaining(element -> settings.accept(CustomCrafting.inst().getApi().getJacksonMapperUtil().getGlobalMapper().convertValue(element, type)));
    }

    private <T extends CategorySettings> Map<String, Object> getSettingsMap(List<String> sortedList, Collection<T> settings) {
        return Map.of(
                "sort", sortedList,
                "options", settings
        );
    }

    public class CategoryAlign {

        private AlignItems align = AlignItems.LEFT;
        private int maxPerRow = 9;
        private int minRows = 2;
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        private Map<String, Integer> customSlots = new HashMap<>();

        public CategoryAlign() { }

        public CategoryAlign(CategoryAlign original) {
            this.align = original.getAlign();
            this.maxPerRow = original.getMaxPerRow();
            this.minRows = original.getMinRows();
            this.customSlots.putAll(original.customSlots);
        }

        @JsonIgnore
        public int getRequiredRows() {
            return Math.max(minRows, Math.min(5, (int) Math.ceil(categoryMap.size() / (double) maxPerRow)));
        }

        public AlignItems getAlign() {
            return align;
        }

        public void setAlign(AlignItems align) {
            this.align = align;
        }

        public int getMaxPerRow() {
            return maxPerRow;
        }

        public void setMaxPerRow(int maxPerRow) {
            this.maxPerRow = maxPerRow;
        }

        public int getMinRows() {
            return minRows;
        }

        public void setMinRows(int minRows) {
            this.minRows = minRows;
        }

        @JsonGetter("customSlots")
        Map<String, Integer> getCustomSlots() {
            return customSlots;
        }

        @JsonIgnore
        public Optional<Integer> getCustomSlot(String categoryId) {
            return Optional.ofNullable(customSlots.get(categoryId));
        }

        public void setCustomSlots(Map<String, Integer> customSlots) {
            this.customSlots = customSlots;
        }
    }

    public static class VariationCycle {

        private int periodIngredient = 30;
        private int periodRecipe = 30;

        public VariationCycle(VariationCycle original) {
            this.periodIngredient = original.getPeriodIngredient();
            this.periodRecipe = original.getPeriodRecipe();
        }

        public VariationCycle() {}

        public int getPeriodIngredient() {
            return periodIngredient;
        }

        public void setPeriodIngredient(int periodIngredient) {
            this.periodIngredient = periodIngredient;
        }

        public int getPeriodRecipe() {
            return periodRecipe;
        }

        public void setPeriodRecipe(int periodRecipe) {
            this.periodRecipe = periodRecipe;
        }
    }

}
