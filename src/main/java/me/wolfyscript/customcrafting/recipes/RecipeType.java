package me.wolfyscript.customcrafting.recipes;

import me.wolfyscript.customcrafting.recipes.settings.AdvancedRecipeSettings;
import me.wolfyscript.customcrafting.recipes.settings.EliteRecipeSettings;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.util.NamespacedKey;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class RecipeType<C extends ICustomRecipe<?>> {

    //Crafting recipes
    public static final RecipeType<CraftingRecipeShaped> WORKBENCH_SHAPED = new RecipeType<>(Type.WORKBENCH_SHAPED, CraftingRecipeShaped.class);
    public static final RecipeType<CraftingRecipeShapeless> WORKBENCH_SHAPELESS = new RecipeType<>(Type.WORKBENCH_SHAPELESS, CraftingRecipeShapeless.class);
    public static final RecipeType<CraftingRecipeEliteShaped> ELITE_WORKBENCH_SHAPED = new RecipeType<>(Type.ELITE_WORKBENCH_SHAPED, CraftingRecipeEliteShaped.class);
    public static final RecipeType<CraftingRecipe<?, AdvancedRecipeSettings>> WORKBENCH = new RecipeType<>(Type.WORKBENCH, (Class<CraftingRecipe<?, AdvancedRecipeSettings>>) (Object) CraftingRecipe.class) {
        @Override
        public CraftingRecipe<?, AdvancedRecipeSettings> getInstance(NamespacedKey namespacedKey, JsonNode node) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
            return node.path("shapeless").asBoolean() ? WORKBENCH_SHAPELESS.getInstance(namespacedKey, node) : WORKBENCH_SHAPED.getInstance(namespacedKey, node);
        }

        @Override
        public boolean isInstance(ICustomRecipe<?> recipe) {
            return recipe instanceof CraftingRecipe<?, ?> craftingRecipe && craftingRecipe.getSettings() instanceof AdvancedRecipeSettings;
        }
    };
    public static final RecipeType<CraftingRecipeEliteShapeless> ELITE_WORKBENCH_SHAPELESS = new RecipeType<>(Type.ELITE_WORKBENCH_SHAPELESS, CraftingRecipeEliteShapeless.class);
    //Cooking recipes
    public static final RecipeType<CustomRecipeFurnace> FURNACE = new RecipeType<>(Type.FURNACE, CustomRecipeFurnace.class);
    public static final RecipeType<CraftingRecipe<?, EliteRecipeSettings>> ELITE_WORKBENCH = new RecipeType<>(Type.ELITE_WORKBENCH, (Class<CraftingRecipe<?, EliteRecipeSettings>>) (Object) CraftingRecipe.class) {
        @Override
        public CraftingRecipe<?, EliteRecipeSettings> getInstance(NamespacedKey namespacedKey, JsonNode node) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
            return node.path("shapeless").asBoolean() ? ELITE_WORKBENCH_SHAPELESS.getInstance(namespacedKey, node) : ELITE_WORKBENCH_SHAPED.getInstance(namespacedKey, node);
        }

        @Override
        public boolean isInstance(ICustomRecipe<?> recipe) {
            return recipe instanceof CraftingRecipe<?, ?> craftingRecipe && craftingRecipe.getSettings() instanceof EliteRecipeSettings;
        }
    };
    public static final RecipeType<CustomRecipeBlasting> BLAST_FURNACE = new RecipeType<>(Type.BLAST_FURNACE, CustomRecipeBlasting.class);
    public static final RecipeType<CustomRecipeSmoking> SMOKER = new RecipeType<>(Type.SMOKER, CustomRecipeSmoking.class);
    public static final RecipeType<CustomRecipeCampfire> CAMPFIRE = new RecipeType<>(Type.CAMPFIRE, CustomRecipeCampfire.class);
    //Misc recipes
    public static final RecipeType<CustomRecipeAnvil> ANVIL = new RecipeType<>(Type.ANVIL, CustomRecipeAnvil.class);
    public static final RecipeType<CustomRecipeStonecutter> STONECUTTER = new RecipeType<>(Type.STONECUTTER, CustomRecipeStonecutter.class);
    public static final RecipeType<CustomRecipeCauldron> CAULDRON = new RecipeType<>(Type.CAULDRON, CustomRecipeCauldron.class);
    public static final RecipeType<CustomRecipeGrindstone> GRINDSTONE = new RecipeType<>(Type.GRINDSTONE, CustomRecipeGrindstone.class);
    public static final RecipeType<CustomRecipeBrewing> BREWING_STAND = new RecipeType<>(Type.BREWING_STAND, CustomRecipeBrewing.class);
    public static final RecipeType<CustomRecipeSmithing> SMITHING = new RecipeType<>(Type.SMITHING, CustomRecipeSmithing.class);
    static final Set<RecipeType<? extends ICustomRecipe<?>>> values = new HashSet<>();


    private final Type type;

    protected RecipeType(Type type, Class<C> clazz) {
        this(type, clazz, type.toString().toLowerCase(Locale.ROOT));
    }

    protected RecipeType(Type type, Class<C> clazz, String creatorID) {
        this.type = type;
        this.clazz = clazz;
        this.id = type.toString().toLowerCase(Locale.ROOT);
        this.creatorID = creatorID;
        values.add(this);
    }

    public static Set<RecipeType<? extends ICustomRecipe<?>>> values() {
        return Collections.unmodifiableSet(values);
    }

    private final String id;
    private final String creatorID;
    private final Class<C> clazz;

    public static RecipeType<?> valueOf(String id) {
        return values.stream().filter(rType -> rType.getId().equalsIgnoreCase(id)).findFirst().orElse(null);
    }

    public static RecipeType<?> valueOf(Type type) {
        return values.stream().filter(rType -> rType.getType().equals(type)).findFirst().orElse(null);
    }

    public Type getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public String getCreatorID() {
        return creatorID;
    }

    public Class<C> getClazz() {
        return clazz;
    }

    public boolean isInstance(ICustomRecipe<?> recipe) {
        return clazz.isInstance(recipe);
    }

    public C cast(ICustomRecipe<?> recipe) {
        return clazz.cast(recipe);
    }

    public C getInstance(NamespacedKey namespacedKey, JsonNode node) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        return clazz.getDeclaredConstructor(NamespacedKey.class, JsonNode.class).newInstance(namespacedKey, node);
    }

    public enum Type {
        WORKBENCH,
        WORKBENCH_SHAPED,
        WORKBENCH_SHAPELESS,
        ELITE_WORKBENCH,
        ELITE_WORKBENCH_SHAPED,
        ELITE_WORKBENCH_SHAPELESS,
        ANVIL,
        FURNACE,
        BLAST_FURNACE,
        SMOKER,
        CAMPFIRE,
        STONECUTTER,
        CAULDRON,
        GRINDSTONE,
        BREWING_STAND,
        SMITHING
    }

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
}
