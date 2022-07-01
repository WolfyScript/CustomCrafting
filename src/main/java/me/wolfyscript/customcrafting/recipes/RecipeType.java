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

package me.wolfyscript.customcrafting.recipes;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.settings.AdvancedRecipeSettings;
import me.wolfyscript.customcrafting.recipes.settings.CraftingRecipeSettings;
import me.wolfyscript.customcrafting.recipes.settings.EliteRecipeSettings;
import me.wolfyscript.lib.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.util.Keyed;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.json.jackson.annotations.OptionalKeyReference;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@OptionalKeyReference(field = "key")
public interface RecipeType<C extends CustomRecipe<?>> extends RecipeLoader<C>, Keyed {

    //Crafting recipes
    RecipeType<CraftingRecipeShaped> CRAFTING_SHAPED = new RecipeTypeImpl<>(Type.CRAFTING_SHAPED, CraftingRecipeShaped.class);
    RecipeType<CraftingRecipeShapeless> CRAFTING_SHAPELESS = new RecipeTypeImpl<>(Type.CRAFTING_SHAPELESS, CraftingRecipeShapeless.class);
    RecipeType<CraftingRecipeEliteShaped> ELITE_CRAFTING_SHAPED = new RecipeTypeImpl<>(Type.ELITE_CRAFTING_SHAPED, CraftingRecipeEliteShaped.class);
    RecipeType<CraftingRecipeEliteShapeless> ELITE_CRAFTING_SHAPELESS = new RecipeTypeImpl<>(Type.ELITE_CRAFTING_SHAPELESS, CraftingRecipeEliteShapeless.class);
    //Cooking recipes
    RecipeType<CustomRecipeFurnace> FURNACE = new RecipeTypeImpl<>(Type.FURNACE, CustomRecipeFurnace.class);
    RecipeType<CustomRecipeBlasting> BLAST_FURNACE = new RecipeTypeImpl<>(Type.BLAST_FURNACE, CustomRecipeBlasting.class);
    RecipeType<CustomRecipeSmoking> SMOKER = new RecipeTypeImpl<>(Type.SMOKER, CustomRecipeSmoking.class);
    RecipeType<CustomRecipeCampfire> CAMPFIRE = new RecipeTypeImpl<>(Type.CAMPFIRE, CustomRecipeCampfire.class);
    //Misc recipes
    RecipeType<CustomRecipeAnvil> ANVIL = new RecipeTypeImpl<>(Type.ANVIL, CustomRecipeAnvil.class);
    RecipeType<CustomRecipeStonecutter> STONECUTTER = new RecipeTypeImpl<>(Type.STONECUTTER, CustomRecipeStonecutter.class);
    RecipeType<CustomRecipeCauldron> CAULDRON = new RecipeTypeImpl<>(Type.CAULDRON, CustomRecipeCauldron.class);
    RecipeType<CustomRecipeGrindstone> GRINDSTONE = new RecipeTypeImpl<>(Type.GRINDSTONE, CustomRecipeGrindstone.class);
    RecipeType<CustomRecipeBrewing> BREWING_STAND = new RecipeTypeImpl<>(Type.BREWING_STAND, CustomRecipeBrewing.class);
    RecipeType<CustomRecipeSmithing> SMITHING = new RecipeTypeImpl<>(Type.SMITHING, CustomRecipeSmithing.class);

    static Set<RecipeType<? extends CustomRecipe<?>>> values() {
        return Set.copyOf(RecipeTypeImpl.values.values());
    }

    /**
     * @param id The id of the recipe type.
     * @return The recipe type of the specified id; or null if non can be found.
     */
    static RecipeType<?> valueOf(String id) {
        return RecipeTypeImpl.values.get(id.toLowerCase(Locale.ROOT));
    }

    static RecipeType<?> valueOf(Type type) {
        return RecipeTypeImpl.values.get(type.toString().toLowerCase(Locale.ROOT));
    }

    static <C extends CustomRecipe<C>> RecipeType<C> valueOfRecipe(CustomRecipe<C> type) {
        if (type == null) return null;
        return (RecipeType<C>) values().stream().filter(recipeType -> recipeType.isInstance(type)).findFirst().orElse(null);
    }

    Type getType();

    String getCreatorID();

    Container<? super C> getContainer();

    Class<C> getRecipeClass();

    boolean isInstance(CustomRecipe<?> recipe);

    /**
     * Casts the recipe to the type of this RecipeType.
     * Make sure that the recipe is actually a type of this type using {@link #isInstance(CustomRecipe)}.
     * If the recipe is not a type of this recipe type this method will throw a {@link ClassCastException}.
     *
     * @param recipe The recipe to cast.
     * @return The recipe cast to the type of this RecipeType.
     */
    C cast(CustomRecipe<?> recipe);

    String name();

    /**
     * The Container can hold multiple RecipeTypes and is used to group similar or related recipe types together.
     *
     * @param <C> The type of the {@link CustomRecipe}, that all recipes in the collection must match.
     */
    interface Container<C extends CustomRecipe<?>> {

        CraftingContainer<AdvancedRecipeSettings> CRAFTING = new CraftingContainer<>("workbench", "crafting", CRAFTING_SHAPED, CRAFTING_SHAPELESS);
        CraftingContainer<EliteRecipeSettings> ELITE_CRAFTING = new CraftingContainer<>("elite_workbench", "elite_crafting", ELITE_CRAFTING_SHAPED, ELITE_CRAFTING_SHAPELESS);
        Container<CustomRecipeCooking<?, ?>> COOKING = new ContainerImpl<>((Class<CustomRecipeCooking<?, ?>>) (Object) CustomRecipeCooking.class, "cooking", List.of(FURNACE, BLAST_FURNACE, SMOKER, CAMPFIRE));

        static Collection<Container<? extends CustomRecipe<?>>> values() {
            return ContainerImpl.values.values();
        }

        static boolean isLegacy(String id) {
            return !ContainerImpl.values.containsKey(id) && ContainerImpl.legacyValues.containsKey(id);
        }

        static Container<?> valueOf(String id) {
            if (ContainerImpl.values.containsKey(id)) {
                return ContainerImpl.values.get(id);
            }
            return ContainerImpl.legacyValues.get(id);
        }

        List<RecipeType<? extends C>> getTypes();

        String getId();

        String getLegacyID();

        boolean hasLegacy();

        String getCreatorID();

        /**
         * @param recipe The recipe to check.
         * @return if the recipe is an instance of the included types.
         */
        boolean isInstance(CustomRecipe<?> recipe);

        /**
         * @param type The type to check for.
         * @return True if the type is included in the container.
         */
        boolean has(RecipeType<?> type);

        /**
         * Casts the recipe to the type of this Container.
         * Make sure that the recipe is actually a type of this type using {@link #isInstance(CustomRecipe)}.
         * If the recipe is not a type of this container this method will throw a {@link ClassCastException}.
         *
         * @param recipe The recipe to cast.
         * @return The recipe cast to the type of this RecipeType.
         */
        C cast(CustomRecipe<?> recipe);

        final class CraftingContainer<S extends CraftingRecipeSettings<S>> extends Container.ContainerImpl<CraftingRecipe<?, S>> implements RecipeLoader<CraftingRecipe<?, S>> {

            private CraftingContainer(String legacyID, String id, RecipeType<? extends CraftingRecipe<?, S>> shaped, RecipeType<? extends CraftingRecipe<?, S>> shapeless) {
                super((Class<CraftingRecipe<?, S>>) (Object) CraftingRecipe.class, id, legacyID, id, List.of(shaped, shapeless));
            }

            @Override
            public CraftingRecipe<?, S> getInstance(NamespacedKey namespacedKey, JsonNode node) throws IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException {
                return !node.path("shapeless").asBoolean() ? types.get(0).getInstance(namespacedKey, node) : types.get(1).getInstance(namespacedKey, node);
            }

        }

        class ContainerImpl<C extends CustomRecipe<?>> implements Container<C> {

            static final Map<String, Container<?>> values = new HashMap<>();
            static final Map<String, Container<?>> legacyValues = new HashMap<>();

            protected final List<RecipeType<? extends C>> types;
            private final String id;
            private final String legacyID;
            private final String creatorID;
            private final Class<C> clazz;

            private ContainerImpl(Class<C> clazz, String id, List<RecipeType<? extends C>> types) {
                this(clazz, id, id, types);
            }

            private ContainerImpl(Class<C> clazz, String id, String creatorID, List<RecipeType<? extends C>> types) {
                this(clazz, id, null, creatorID, types);
            }

            private ContainerImpl(Class<C> clazz, String id, String legacyID, String creatorID, List<RecipeType<? extends C>> types) {
                this.types = types;
                for (RecipeType<? extends C> type : this.types) {
                    if (type instanceof RecipeTypeImpl<?> recipeTypeImpl) {
                        ((RecipeTypeImpl<C>) recipeTypeImpl).setParent(this);
                    }
                }
                this.clazz = clazz;
                this.id = id;
                this.legacyID = legacyID;
                this.creatorID = creatorID;
                values.putIfAbsent(id, this);
                legacyValues.putIfAbsent(legacyID, this);
            }

            @Override
            public List<RecipeType<? extends C>> getTypes() {
                return types;
            }

            @Override
            public String getId() {
                return id;
            }

            @Override
            public String getLegacyID() {
                return legacyID;
            }

            @Override
            public boolean hasLegacy() {
                return legacyID != null && !legacyID.isBlank();
            }

            @Override
            public String getCreatorID() {
                return creatorID;
            }

            @Override
            public boolean isInstance(CustomRecipe<?> recipe) {
                return types.get(0).isInstance(recipe) || types.get(1).isInstance(recipe);
            }

            @Override
            public boolean has(RecipeType<?> type) {
                return types.contains(type);
            }

            @Override
            public C cast(CustomRecipe<?> recipe) {
                return clazz.cast(recipe);
            }

        }
    }

    /**
     * Enum representation of all available {@link RecipeType}s.<br>
     * This allows you to make use of switches and other enum only features.<br>
     * Use {@link RecipeType#getType()} to get it's enum representation.
     */
    enum Type {
        CRAFTING_SHAPED,
        CRAFTING_SHAPELESS,
        ELITE_CRAFTING_SHAPED,
        ELITE_CRAFTING_SHAPELESS,
        ANVIL,
        FURNACE,
        BLAST_FURNACE,
        SMOKER,
        CAMPFIRE,
        STONECUTTER,
        CAULDRON,
        GRINDSTONE,
        BREWING_STAND,
        SMITHING,
        CUSTOM
    }

    class RecipeTypeImpl<C extends CustomRecipe<?>> implements RecipeType<C> {

        static final Map<String, RecipeType<? extends CustomRecipe<?>>> values = new HashMap<>();

        private final NamespacedKey key;
        private final String id;
        private final Type type;
        private final String creatorID;
        private final Class<C> clazz;
        private Container<? super C> parent;

        private RecipeTypeImpl(Type type, Class<C> clazz) {
            this(type, clazz, type.toString().toLowerCase(Locale.ROOT));
        }

        private RecipeTypeImpl(Type type, Class<C> clazz, String creatorID) {
            this.key = new NamespacedKey(CustomCrafting.inst(), creatorID);
            this.type = type;
            this.clazz = clazz;
            this.id = type.toString().toLowerCase(Locale.ROOT);
            this.creatorID = creatorID;
            this.parent = null;
            values.put(id, this);
        }

        @Override
        public Type getType() {
            return type;
        }

        @Override
        public String getId() {
            return id;
        }

        @Override
        public String getCreatorID() {
            return parent == null ? creatorID : parent.getCreatorID();
        }

        @Override
        public Class<C> getRecipeClass() {
            return clazz;
        }

        @Override
        public Container<? super C> getContainer() {
            return parent;
        }

        void setParent(Container<? super C> parent) {
            this.parent = parent;
        }

        @Override
        public boolean isInstance(CustomRecipe<?> recipe) {
            return clazz.isInstance(recipe);
        }

        @Override
        public C cast(CustomRecipe<?> recipe) {
            return clazz.cast(recipe);
        }

        @Override
        public C getInstance(NamespacedKey namespacedKey, JsonNode node) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
            return clazz.getDeclaredConstructor(NamespacedKey.class, JsonNode.class).newInstance(namespacedKey, node);
        }

        @Override
        public String name() {
            return getType().toString();
        }

        @Override
        public String toString() {
            return "RecipeType{" +
                    "id='" + id + '\'' +
                    ", creatorID='" + creatorID + '\'' +
                    ", clazz=" + clazz +
                    ", type=" + type +
                    '}';
        }

        @Override
        public NamespacedKey getNamespacedKey() {
            return key;
        }
    }
}
