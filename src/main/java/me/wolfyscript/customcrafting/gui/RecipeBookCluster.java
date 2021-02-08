package me.wolfyscript.customcrafting.gui;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.KnowledgeBook;
import me.wolfyscript.customcrafting.gui.recipebook.MainMenu;
import me.wolfyscript.customcrafting.gui.recipebook.RecipeBook;
import me.wolfyscript.customcrafting.gui.recipebook.buttons.IngredientContainerButton;
import me.wolfyscript.customcrafting.gui.recipebook.buttons.ItemCategoryButton;
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
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.MultipleChoiceButton;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ToggleButton;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.inventory.PlayerHeadUtils;
import me.wolfyscript.utilities.util.inventory.item_builder.ItemBuilder;
import me.wolfyscript.utilities.util.version.MinecraftVersions;
import me.wolfyscript.utilities.util.version.ServerVersion;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.CookingRecipe;

import java.util.ArrayList;

public class RecipeBookCluster extends CCCluster {

    public RecipeBookCluster(InventoryAPI<CCCache> inventoryAPI, CustomCrafting customCrafting) {
        super(inventoryAPI, "recipe_book", customCrafting);
    }

    @Override
    public void onInit() {
        registerGuiWindow(new RecipeBook(this, customCrafting));
        registerGuiWindow(new MainMenu(this, customCrafting));
        setEntry(new NamespacedKey("recipe_book", "main_menu"));
        registerButton(new ItemCategoryButton(customCrafting));
        registerButton(new ActionButton<>("next_page", PlayerHeadUtils.getViaURL("c86185b1d519ade585f184c34f3f3e20bb641deb879e81378e4eaf209287"), (cache, guiHandler, player, inventory, slot, event) -> {
            KnowledgeBook book = guiHandler.getCustomCache().getKnowledgeBook();
            book.setPage(book.getPage() + 1);
            return true;
        }));
        registerButton(new ActionButton<>("previous_page", PlayerHeadUtils.getViaURL("ad73cf66d31b83cd8b8644c15958c1b73c8d97323b801170c1d8864bb6a846d"), (cache, guiHandler, player, inventory, slot, event) -> {
            KnowledgeBook book = guiHandler.getCustomCache().getKnowledgeBook();
            book.setPage(book.getPage() > 0 ? book.getPage() - 1 : 0);
            return true;
        }));
        registerButton(new ActionButton<>("back_to_list", Material.BARRIER, (cache, guiHandler, player, inventory, slot, event) -> {
            if (event instanceof InventoryClickEvent) {
                KnowledgeBook book = cache.getKnowledgeBook();
                book.stopTimerTask();
                IngredientContainerButton.resetButtons(guiHandler);
                if (((InventoryClickEvent) event).isLeftClick()) {
                    book.removePreviousResearchItem();
                    if (book.getSubFolder() > 0) {
                        CustomItem item = book.getResearchItem();
                        if (book.getSubFolderRecipes().isEmpty()) {
                            book.setSubFolderRecipes(item, customCrafting.getRecipeHandler().getRecipes(item));
                        }
                        if (book.getSubFolderRecipes().size() > 0) {
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

        registerButton(new ToggleButton<>("permission", new ButtonState<>("permission.disabled", Material.RED_CONCRETE, (cache, guiHandler, player, inventory, slot, event) -> true), new ButtonState<>("permission.enabled", Material.GREEN_CONCRETE, (cache, guiHandler, player, inventory, slot, event) -> true)));
        registerButton(new MultipleChoiceButton<>("workbench.filter_button", new ButtonState<>("workbench.filter_button.all", Material.CRAFTING_TABLE, (cache, guiHandler, player, inventory, slot, event) -> {
            cache.getKnowledgeBook().setWorkbenchFilter(KnowledgeBook.WorkbenchFilter.ADVANCED);
            return true;
        }), new ButtonState<>("workbench.filter_button.advanced", new ItemBuilder(Material.CRAFTING_TABLE).addUnsafeEnchantment(Enchantment.DURABILITY, 0).create(), (cache, guiHandler, player, inventory, slot, event) -> {
            cache.getKnowledgeBook().setWorkbenchFilter(KnowledgeBook.WorkbenchFilter.NORMAL);
            return true;
        }), new ButtonState<>("workbench.filter_button.normal", Material.CRAFTING_TABLE, (cache, guiHandler, player, inventory, slot, event) -> {
            cache.getKnowledgeBook().setWorkbenchFilter(KnowledgeBook.WorkbenchFilter.ALL);
            return true;
        })));
        registerButton(new DummyButton<>("workbench.shapeless_on", Material.CRAFTING_TABLE));
        registerButton(new DummyButton<>("workbench.shapeless_off", Material.CRAFTING_TABLE));

        registerButton(new DummyButton<>("anvil.durability", Material.ANVIL, (hashMap, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            hashMap.put("%var%", ((CustomAnvilRecipe) guiHandler.getCustomCache().getKnowledgeBook().getCurrentRecipe()).getDurability());
            return itemStack;
        }));
        registerButton(new DummyButton<>("anvil.result", Material.ANVIL));
        registerButton(new DummyButton<>("anvil.none", Material.ANVIL));

        registerButton(new DummyButton<>("cooking.icon", Material.FURNACE, (hashMap, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            KnowledgeBook knowledgeBook = cache.getKnowledgeBook();
            RecipeType<?> recipeType = knowledgeBook.getCurrentRecipe().getRecipeType();
            CookingRecipe<?> cookingRecipe = ((CustomCookingRecipe<?, ?>) knowledgeBook.getCurrentRecipe()).getVanillaRecipe();
            itemStack.setType(Material.matchMaterial(recipeType.name()));
            hashMap.put("%type%", "&7" + StringUtils.capitalize(recipeType.getId().replace("_", " ")));
            hashMap.put("%time%", cookingRecipe.getCookingTime());
            hashMap.put("%xp%", cookingRecipe.getExperience());
            return itemStack;
        }));
        registerButton(new DummyButton<>("furnace", Material.FURNACE));
        registerButton(new DummyButton<>("stonecutter", Material.STONECUTTER));
        registerButton(new DummyButton<>("blast_furnace", Material.BLAST_FURNACE));
        registerButton(new DummyButton<>("campfire", Material.CAMPFIRE));
        registerButton(new DummyButton<>("blast_furnace", Material.BLAST_FURNACE));
        registerButton(new DummyButton<>("grindstone", Material.GRINDSTONE));
        registerButton(new DummyButton<>("smoker", Material.SMOKER));
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_16)) {
            registerButton(new DummyButton<>("smithing", Material.SMITHING_TABLE));
        }
        registerButton(new DummyButton<>("cauldron.water.disabled", Material.CAULDRON));
        registerButton(new DummyButton<>("cauldron.water.enabled", new ButtonState<>("cauldron.water.enabled", PlayerHeadUtils.getViaURL("848a19cdf42d748b41b72fb4376ae3f63c1165d2dce0651733df263446c77ba6"), (hashMap, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            KnowledgeBook knowledgeBook = cache.getKnowledgeBook();
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
        for (int i = 0; i < 54; i++) {
            registerButton(new IngredientContainerButton(i, customCrafting));
        }

        registerButton(new DummyButton<>("conditions.world_time", Material.CLOCK, (hashMap, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            ICustomRecipe<?> recipe = (cache.getKnowledgeBook()).getCurrentRecipe();
            hashMap.put("%value%", ((WorldTimeCondition) recipe.getConditions().getByID("world_time")).getTime());

            if (recipe.getConditions().getByID("world_time").getOption().equals(Conditions.Option.EXACT)) {
                hashMap.put("%mode%", "");
            } else {
                hashMap.put("%mode%", recipe.getConditions().getByID("world_time").getOption().getDisplayString(wolfyUtilities));
            }
            return itemStack;
        }));

        registerButton(new ActionButton<>("conditions.weather", Material.WATER_BUCKET, (hashMap, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            ICustomRecipe<?> recipe = (cache.getKnowledgeBook()).getCurrentRecipe();
            hashMap.put("%value%", ((WeatherCondition) recipe.getConditions().getByID("weather")).getWeather().getDisplay(wolfyUtilities));
            return itemStack;
        }));

        registerButton(new ActionButton<>("conditions.permission", Material.REDSTONE, (hashMap, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            ICustomRecipe<?> recipe = (cache.getKnowledgeBook()).getCurrentRecipe();
            hashMap.put("%value%", ((PermissionCondition) recipe.getConditions().getByID("permission")).getPermission());
            return itemStack;
        }));
    }
}
