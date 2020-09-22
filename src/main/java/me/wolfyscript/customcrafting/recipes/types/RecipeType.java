package me.wolfyscript.customcrafting.recipes.types;

import me.wolfyscript.customcrafting.recipes.types.anvil.CustomAnvilRecipe;
import me.wolfyscript.customcrafting.recipes.types.blast_furnace.CustomBlastRecipe;
import me.wolfyscript.customcrafting.recipes.types.brewing.BrewingRecipe;
import me.wolfyscript.customcrafting.recipes.types.campfire.CustomCampfireRecipe;
import me.wolfyscript.customcrafting.recipes.types.cauldron.CauldronRecipe;
import me.wolfyscript.customcrafting.recipes.types.elite_workbench.EliteCraftingRecipe;
import me.wolfyscript.customcrafting.recipes.types.furnace.CustomFurnaceRecipe;
import me.wolfyscript.customcrafting.recipes.types.grindstone.GrindstoneRecipe;
import me.wolfyscript.customcrafting.recipes.types.smithing.CustomSmithingRecipe;
import me.wolfyscript.customcrafting.recipes.types.smoker.CustomSmokerRecipe;
import me.wolfyscript.customcrafting.recipes.types.stonecutter.CustomStonecutterRecipe;
import me.wolfyscript.customcrafting.recipes.types.workbench.AdvancedCraftingRecipe;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class RecipeType<C extends ICustomRecipe<?>> {

    private static final Set<RecipeType<? extends ICustomRecipe<?>>> values = new HashSet<>();

    public enum Type {
        WORKBENCH, ELITE_WORKBENCH, ANVIL, FURNACE, BLAST_FURNACE, SMOKER, CAMPFIRE, STONECUTTER, CAULDRON, GRINDSTONE, BREWING_STAND, SMITHING
    }

    /**
     * Constants for recipe types. They contain the information which class the recipe is from and the creator id, etc.
     */
    public static final RecipeType<AdvancedCraftingRecipe> WORKBENCH = new RecipeType<>(Type.WORKBENCH, AdvancedCraftingRecipe.class);
    public static final RecipeType<EliteCraftingRecipe> ELITE_WORKBENCH = new RecipeType<>(Type.ELITE_WORKBENCH, EliteCraftingRecipe.class);
    public static final RecipeType<CustomAnvilRecipe> ANVIL = new RecipeType<>(Type.ANVIL, CustomAnvilRecipe.class);
    public static final RecipeType<CustomFurnaceRecipe> FURNACE = new RecipeType<>(Type.FURNACE, CustomFurnaceRecipe.class, "cooking");
    public static final RecipeType<CustomBlastRecipe> BLAST_FURNACE = new RecipeType<>(Type.BLAST_FURNACE, CustomBlastRecipe.class, "cooking");
    public static final RecipeType<CustomSmokerRecipe> SMOKER = new RecipeType<>(Type.SMOKER, CustomSmokerRecipe.class, "cooking");
    public static final RecipeType<CustomCampfireRecipe> CAMPFIRE = new RecipeType<>(Type.CAMPFIRE, CustomCampfireRecipe.class, "cooking");
    public static final RecipeType<CustomStonecutterRecipe> STONECUTTER = new RecipeType<>(Type.STONECUTTER, CustomStonecutterRecipe.class);
    public static final RecipeType<CauldronRecipe> CAULDRON = new RecipeType<>(Type.CAULDRON, CauldronRecipe.class);
    public static final RecipeType<GrindstoneRecipe> GRINDSTONE = new RecipeType<>(Type.GRINDSTONE, GrindstoneRecipe.class);
    public static final RecipeType<BrewingRecipe> BREWING_STAND = new RecipeType<>(Type.BREWING_STAND, BrewingRecipe.class);
    public static final RecipeType<CustomSmithingRecipe> SMITHING = new RecipeType<>(Type.SMITHING, CustomSmithingRecipe.class);

    /**
     * This is the RecipeType object.
     */
    private final String id;
    private final String creatorID;
    private final Class<C> clazz;
    private final RecipeType.Type type;

    public RecipeType(RecipeType.Type type, Class<C> clazz){
        this(type, clazz, type.toString().toLowerCase(Locale.ROOT));
    }

    public RecipeType(RecipeType.Type type, Class<C> clazz, String creatorID){
        this.type = type;
        this.clazz = clazz;
        this.id = type.toString().toLowerCase(Locale.ROOT);
        this.creatorID = creatorID;
        values.add(this);
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

    public RecipeType.Type getType() {
        return type;
    }

    public String name(){
        return getType().toString();
    }

    public static RecipeType<?> valueOf(String id){
        return values.stream().filter(rType -> rType.getId().equalsIgnoreCase(id)).findFirst().orElse(null);
    }

    public static RecipeType<?> valueOf(RecipeType.Type type){
        return values.stream().filter(rType -> rType.getType().equals(type)).findFirst().orElse(null);
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
