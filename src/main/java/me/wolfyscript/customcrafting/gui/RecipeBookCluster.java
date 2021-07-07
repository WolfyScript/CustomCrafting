package me.wolfyscript.customcrafting.gui;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.Registry;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.recipebook.MainMenu;
import me.wolfyscript.customcrafting.gui.recipebook.RecipeBook;
import me.wolfyscript.customcrafting.gui.recipebook.buttons.IngredientContainerButton;
import me.wolfyscript.customcrafting.gui.recipebook.buttons.ItemCategoryButton;
import me.wolfyscript.customcrafting.gui.recipebook.buttons.RecipeBookContainerButton;
import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.RecipeType;
import me.wolfyscript.customcrafting.recipes.conditions.PermissionCondition;
import me.wolfyscript.customcrafting.recipes.conditions.WeatherCondition;
import me.wolfyscript.customcrafting.recipes.conditions.WorldTimeCondition;
import me.wolfyscript.customcrafting.recipes.types.CustomCookingRecipe;
import me.wolfyscript.customcrafting.recipes.types.ICustomRecipe;
import me.wolfyscript.customcrafting.recipes.types.anvil.CustomAnvilRecipe;
import me.wolfyscript.customcrafting.recipes.types.brewing.BrewingRecipe;
import me.wolfyscript.customcrafting.recipes.types.cauldron.CauldronRecipe;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.InventoryAPI;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.DummyButton;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ToggleButton;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.inventory.PlayerHeadUtils;
import me.wolfyscript.utilities.util.version.MinecraftVersions;
import me.wolfyscript.utilities.util.version.ServerVersion;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;

public class RecipeBookCluster extends CCCluster {

    public static final String KEY = "recipe_book";

    public static final NamespacedKey MAIN_MENU = new NamespacedKey(KEY, "main_menu");
    public static final NamespacedKey RECIPE_BOOK = new NamespacedKey(KEY, "recipe_book");

    public static final NamespacedKey BACK_TO_LIST = new NamespacedKey(KEY, "back_to_list");
    public static final NamespacedKey NEXT_PAGE = new NamespacedKey(KEY, "next_page");
    public static final NamespacedKey PREVIOUS_PAGE = new NamespacedKey(KEY, "previous_page");
    public static final NamespacedKey ITEM_CATEGORY = new NamespacedKey(KEY, "item_category");
    public static final NamespacedKey PERMISSION = new NamespacedKey(KEY, "permission");

    public static final NamespacedKey STONECUTTER = new NamespacedKey(RecipeBookCluster.KEY, "stonecutter");
    public static final NamespacedKey FURNACE = new NamespacedKey(RecipeBookCluster.KEY, "furnace");
    public static final NamespacedKey BLAST_FURNACE = new NamespacedKey(RecipeBookCluster.KEY, "blast_furnace");
    public static final NamespacedKey CAMPFIRE = new NamespacedKey(RecipeBookCluster.KEY, "campfire");
    public static final NamespacedKey GRINDSTONE = new NamespacedKey(RecipeBookCluster.KEY, "grindstone");
    public static final NamespacedKey SMOKER = new NamespacedKey(RecipeBookCluster.KEY, "smoker");
    public static final NamespacedKey SMITHING = new NamespacedKey(RecipeBookCluster.KEY, "smithing");

    public RecipeBookCluster(InventoryAPI<CCCache> inventoryAPI, CustomCrafting customCrafting) {
        super(inventoryAPI, KEY, customCrafting);
    }

    @Override
    public void onInit() {
        registerGuiWindow(new RecipeBook(this, customCrafting));
        registerGuiWindow(new MainMenu(this, customCrafting));
        setEntry(MAIN_MENU);

        registerButton(new ItemCategoryButton(customCrafting));
        registerButton(new ActionButton<>(NEXT_PAGE.getKey(), PlayerHeadUtils.getViaURL("c86185b1d519ade585f184c34f3f3e20bb641deb879e81378e4eaf209287"), (cache, guiHandler, player, inventory, slot, event) -> {
            RecipeBookContainerButton.resetButtons(guiHandler);
            var book = guiHandler.getCustomCache().getKnowledgeBook();
            book.setPage(book.getPage() + 1);
            return true;
        }));
        registerButton(new ActionButton<>(PREVIOUS_PAGE.getKey(), PlayerHeadUtils.getViaURL("ad73cf66d31b83cd8b8644c15958c1b73c8d97323b801170c1d8864bb6a846d"), (cache, guiHandler, player, inventory, slot, event) -> {
            RecipeBookContainerButton.resetButtons(guiHandler);
            var book = guiHandler.getCustomCache().getKnowledgeBook();
            book.setPage(book.getPage() > 0 ? book.getPage() - 1 : 0);
            return true;
        }));
        registerButton(new ActionButton<>(BACK_TO_LIST.getKey(), Material.BARRIER, (cache, guiHandler, player, inventory, slot, event) -> {
            if (event instanceof InventoryClickEvent) {
                var book = cache.getKnowledgeBook();
                IngredientContainerButton.resetButtons(guiHandler);
                if (((InventoryClickEvent) event).isLeftClick()) {
                    book.removePreviousResearchItem();
                    if (book.getSubFolder() > 0) {
                        CustomItem item = book.getResearchItem();
                        if (book.getSubFolderRecipes().isEmpty()) {
                            book.setSubFolderRecipes(item, Registry.RECIPES.get(item));
                        }
                        if (!book.getSubFolderRecipes().isEmpty()) {
                            book.applyRecipeToButtons(guiHandler, book.getSubFolderRecipes().get(0));
                        }
                        return true;
                    }
                } else {
                    book.setResearchItems(new ArrayList<>());
                }
            }
            return true;
        }));

        registerButton(new ToggleButton<>(PERMISSION.getKey(), new ButtonState<>("permission.disabled", Material.RED_CONCRETE, (cache, guiHandler, player, inventory, slot, event) -> true), new ButtonState<>("permission.enabled", Material.GREEN_CONCRETE, (cache, guiHandler, player, inventory, slot, event) -> true)));

        registerRecipeIcons();
        for (int i = 0; i < 37; i++) {
            registerButton(new IngredientContainerButton(i));
        }
        for (int i = 0; i < 45; i++) {
            registerButton(new RecipeBookContainerButton(i));
        }
        registerConditionDisplays();
    }

    private void registerRecipeIcons() {
        registerButton(new DummyButton<>("workbench.shapeless_on", Material.CRAFTING_TABLE));
        registerButton(new DummyButton<>("workbench.shapeless_off", Material.CRAFTING_TABLE));

        registerButton(new DummyButton<>("anvil.durability", Material.ANVIL, (hashMap, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            hashMap.put("%var%", ((CustomAnvilRecipe) guiHandler.getCustomCache().getKnowledgeBook().getCurrentRecipe()).getDurability());
            return itemStack;
        }));
        registerButton(new DummyButton<>("anvil.result", Material.ANVIL));
        registerButton(new DummyButton<>("anvil.none", Material.ANVIL));

        registerButton(new DummyButton<>("cooking.icon", Material.FURNACE, (hashMap, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            var knowledgeBook = cache.getKnowledgeBook();
            RecipeType<?> recipeType = knowledgeBook.getCurrentRecipe().getRecipeType();
            CustomCookingRecipe<?, ?> cookingRecipe = ((CustomCookingRecipe<?, ?>) knowledgeBook.getCurrentRecipe());
            itemStack.setType(Material.matchMaterial(recipeType.name()));
            hashMap.put("%type%", "&7" + StringUtils.capitalize(recipeType.getId().replace("_", " ")));
            hashMap.put("%time%", cookingRecipe.getCookingTime());
            hashMap.put("%xp%", cookingRecipe.getExp());
            return itemStack;
        }));
        registerButton(new DummyButton<>(FURNACE.getKey(), Material.FURNACE));
        registerButton(new DummyButton<>(STONECUTTER.getKey(), Material.STONECUTTER));
        registerButton(new DummyButton<>(BLAST_FURNACE.getKey(), Material.BLAST_FURNACE));
        registerButton(new DummyButton<>(CAMPFIRE.getKey(), Material.CAMPFIRE));
        registerButton(new DummyButton<>(GRINDSTONE.getKey(), Material.GRINDSTONE));
        registerButton(new DummyButton<>(SMOKER.getKey(), Material.SMOKER));
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_16)) {
            registerButton(new DummyButton<>(SMITHING.getKey(), Material.SMITHING_TABLE));
        }
        registerButton(new DummyButton<>("cauldron.water.disabled", Material.CAULDRON));
        registerButton(new DummyButton<>("cauldron.water.enabled", new ButtonState<>("cauldron.water.enabled", PlayerHeadUtils.getViaURL("848a19cdf42d748b41b72fb4376ae3f63c1165d2dce0651733df263446c77ba6"), (hashMap, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            var knowledgeBook = cache.getKnowledgeBook();
            hashMap.put("%lvl%", ((CauldronRecipe) knowledgeBook.getCurrentRecipe()).getWaterLevel());
            return itemStack;
        })));
        registerButton(new DummyButton<>("cauldron.fire.disabled", Material.FLINT));
        registerButton(new DummyButton<>("cauldron.fire.enabled", Material.FLINT_AND_STEEL));
        registerButton(new DummyButton<>("brewing.icon", Material.BREWING_STAND, (hashMap, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            BrewingRecipe cookingRecipe = (BrewingRecipe) (guiHandler.getCustomCache().getKnowledgeBook()).getCurrentRecipe();
            hashMap.put("%time%", cookingRecipe.getBrewTime());
            hashMap.put("%cost%", cookingRecipe.getFuelCost());
            return itemStack;
        }));
        registerButton(new DummyButton<>("brewing.potion_duration", Material.CLOCK, (hashMap, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            BrewingRecipe cookingRecipe = (BrewingRecipe) (cache.getKnowledgeBook()).getCurrentRecipe();
            hashMap.put("%value%", cookingRecipe.getDurationChange());
            return itemStack;
        }));
        registerButton(new DummyButton<>("brewing.potion_amplifier", Material.IRON_SWORD, (hashMap, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            BrewingRecipe cookingRecipe = (BrewingRecipe) (cache.getKnowledgeBook()).getCurrentRecipe();
            hashMap.put("%value%", cookingRecipe.getAmplifierChange());
            return itemStack;
        }));
    }

    private void registerConditionDisplays() {
        registerButton(new DummyButton<>("conditions.world_time", Material.CLOCK, (hashMap, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            ICustomRecipe<?, ?> recipe = (cache.getKnowledgeBook()).getCurrentRecipe();
            hashMap.put("%value%", recipe.getConditions().getByType(WorldTimeCondition.class).getTime());
            if (recipe.getConditions().getByType(WorldTimeCondition.class).getOption().equals(Conditions.Option.EXACT)) {
                hashMap.put("%mode%", "");
            } else {
                hashMap.put("%mode%", recipe.getConditions().getByType(WorldTimeCondition.class).getOption().getDisplayString(wolfyUtilities));
            }
            return itemStack;
        }));

        registerButton(new ActionButton<>("conditions.weather", Material.WATER_BUCKET, (hashMap, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            ICustomRecipe<?,?> recipe = (cache.getKnowledgeBook()).getCurrentRecipe();
            hashMap.put("%value%", recipe.getConditions().getByType(WeatherCondition.class).getWeather().getDisplay(wolfyUtilities));
            return itemStack;
        }));

        registerButton(new ActionButton<>("conditions.permission", Material.REDSTONE, (hashMap, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            ICustomRecipe<?,?> recipe = (cache.getKnowledgeBook()).getCurrentRecipe();
            hashMap.put("%value%", recipe.getConditions().getByType(PermissionCondition.class).getPermission());
            return itemStack;
        }));
    }
}
