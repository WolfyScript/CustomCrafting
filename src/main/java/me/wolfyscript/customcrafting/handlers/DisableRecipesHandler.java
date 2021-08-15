package me.wolfyscript.customcrafting.handlers;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.MainConfig;
import me.wolfyscript.customcrafting.recipes.ICustomRecipe;
import me.wolfyscript.customcrafting.recipes.ICustomVanillaRecipe;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Recipe;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class DisableRecipesHandler {

    private final CustomCrafting customCrafting;
    private final ConfigHandler configHandler;
    private final MainConfig config;

    private final Set<NamespacedKey> recipes = new HashSet<>();
    private final HashMap<org.bukkit.NamespacedKey, Recipe> cachedRecipes = new HashMap<>();

    public DisableRecipesHandler(CustomCrafting customCrafting) {
        this.customCrafting = customCrafting;
        this.configHandler = customCrafting.getConfigHandler();
        this.config = configHandler.getConfig();

        if (!config.getDisabledRecipes().isEmpty()) {
            recipes.addAll(config.getDisabledRecipes().parallelStream().map(NamespacedKey::of).toList());
        }
    }

    public void saveDisabledRecipes() {
        config.setDisabledRecipes(recipes);
        config.save();
    }

    /**
     * @return A list of recipes that are disabled.
     */
    public Set<NamespacedKey> getRecipes() {
        return recipes;
    }

    public void toggleRecipe(ICustomRecipe<?> recipe) {
        if (recipe.isDisabled()) {
            enableRecipe(recipe);
        } else {
            disableRecipe(recipe);
        }
    }

    /**
     * Disables the {@link ICustomRecipe} and removes it's bukkit counterpart if it is an instance of {@link ICustomVanillaRecipe}.
     *
     * @param recipe The recipe to disable.
     */
    public void disableRecipe(ICustomRecipe<?> recipe) {
        var namespacedKey = recipe.getNamespacedKey();
        recipes.add(namespacedKey);
        if (recipe instanceof ICustomVanillaRecipe<?>) {
            Bukkit.removeRecipe(namespacedKey.toBukkit(customCrafting));
        }
        saveDisabledRecipes();
    }

    /**
     * Enables the {@link me.wolfyscript.customcrafting.recipes.CustomRecipe} and adds the Bukkit recipe if it is an instance of {@link ICustomVanillaRecipe}.
     *
     * @param recipe The recipe to enable.
     */
    public void enableRecipe(ICustomRecipe<?> recipe) {
        var namespacedKey = recipe.getNamespacedKey();
        if (recipe instanceof ICustomVanillaRecipe<?> customVanillaRecipe) {
            Bukkit.addRecipe(customVanillaRecipe.getVanillaRecipe());
        }
        recipes.remove(namespacedKey);
        saveDisabledRecipes();
    }

    public boolean isBukkitRecipeDisabled(org.bukkit.NamespacedKey namespacedKey) {
        return recipes.contains(NamespacedKey.fromBukkit(namespacedKey));
    }

    public void toggleBukkitRecipe(org.bukkit.NamespacedKey namespacedKey) {
        if (isBukkitRecipeDisabled(namespacedKey)) {
            enableBukkitRecipe(namespacedKey);
        } else {
            disableBukkitRecipe(namespacedKey);
        }
    }

    /**
     * Disables the specified bukkit recipe.
     * The {@link Recipe} is cached before being removed from Bukkit, to easily add it
     * on runtime when enabled using {@link #enableBukkitRecipe(org.bukkit.NamespacedKey)}.
     *
     * @param namespacedKey The {@link org.bukkit.NamespacedKey} of the recipe to remove.
     */
    public void disableBukkitRecipe(org.bukkit.NamespacedKey namespacedKey) {
        Recipe bukkitRecipe = Bukkit.getRecipe(namespacedKey);
        if (bukkitRecipe != null) {
            for (Player player1 : Bukkit.getOnlinePlayers()) {
                player1.undiscoverRecipe(namespacedKey);
            }
            if (!namespacedKey.getNamespace().equals(NamespacedKeyUtils.NAMESPACE)) {
                recipes.add(NamespacedKey.fromBukkit(namespacedKey));
                //Cache the recipe if it is a Bukkit recipe, so we can add again at runtime, without the requirement to reload everything.
                cachedRecipes.put(namespacedKey, bukkitRecipe);
            }
            Bukkit.removeRecipe(namespacedKey);
        }
        saveDisabledRecipes();
    }

    /**
     * Enables the specified recipe again and tries to add the previously cached
     * {@link Recipe} back into Bukkit.
     *
     * @param namespacedKey The {@link org.bukkit.NamespacedKey} of the recipe to enable.
     */
    public void enableBukkitRecipe(org.bukkit.NamespacedKey namespacedKey) {
        recipes.remove(NamespacedKey.fromBukkit(namespacedKey));
        Recipe bukkitRecipe = cachedRecipes.get(namespacedKey);
        if (bukkitRecipe != null) {
            Bukkit.addRecipe(bukkitRecipe);
        }
        saveDisabledRecipes();
    }


}
