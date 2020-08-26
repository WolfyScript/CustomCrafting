package me.wolfyscript.customcrafting.handlers;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.data.cache.KnowledgeBook;
import me.wolfyscript.customcrafting.gui.elite_crafting.*;
import me.wolfyscript.customcrafting.gui.item_creator.ItemCreator;
import me.wolfyscript.customcrafting.gui.main_gui.*;
import me.wolfyscript.customcrafting.gui.recipe_creator.ConditionsMenu;
import me.wolfyscript.customcrafting.gui.recipe_creator.VariantMenu;
import me.wolfyscript.customcrafting.gui.recipe_creator.recipe_creators.*;
import me.wolfyscript.customcrafting.gui.recipebook.RecipeBook;
import me.wolfyscript.customcrafting.gui.recipebook.buttons.IngredientContainerButton;
import me.wolfyscript.customcrafting.gui.recipebook.buttons.ItemCategoryButton;
import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.conditions.PermissionCondition;
import me.wolfyscript.customcrafting.recipes.conditions.WeatherCondition;
import me.wolfyscript.customcrafting.recipes.conditions.WorldTimeCondition;
import me.wolfyscript.customcrafting.recipes.types.CustomCookingRecipe;
import me.wolfyscript.customcrafting.recipes.types.ICustomRecipe;
import me.wolfyscript.customcrafting.recipes.types.RecipeType;
import me.wolfyscript.customcrafting.recipes.types.anvil.CustomAnvilRecipe;
import me.wolfyscript.customcrafting.recipes.types.brewing.BrewingRecipe;
import me.wolfyscript.customcrafting.recipes.types.cauldron.CauldronRecipe;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.GuiCluster;
import me.wolfyscript.utilities.api.inventory.InventoryAPI;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.buttons.ActionButton;
import me.wolfyscript.utilities.api.inventory.button.buttons.DummyButton;
import me.wolfyscript.utilities.api.inventory.button.buttons.MultipleChoiceButton;
import me.wolfyscript.utilities.api.inventory.button.buttons.ToggleButton;
import me.wolfyscript.utilities.api.utils.chat.ClickData;
import me.wolfyscript.utilities.api.utils.chat.ClickEvent;
import me.wolfyscript.utilities.api.utils.inventory.PlayerHeadUtils;
import me.wolfyscript.utilities.api.utils.inventory.item_builder.ItemBuilder;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.CookingRecipe;

import java.util.ArrayList;

public class InventoryHandler {

    private final CustomCrafting customCrafting;
    private final WolfyUtilities api;
    private final InventoryAPI invAPI;

    public InventoryHandler(CustomCrafting customCrafting) {
        this.api = WolfyUtilities.getAPI(customCrafting);
        this.invAPI = this.api.getInventoryAPI();
        this.customCrafting = customCrafting;
    }

    public void init() {
        api.sendConsoleMessage("$msg.startup.inventories$");
        registerInvs();

        invAPI.registerButton("none", new DummyButton("glass_gray", new ButtonState("none", "background", Material.GRAY_STAINED_GLASS_PANE, 8999, null)));
        invAPI.registerButton("none", new DummyButton("glass_black", new ButtonState("none", "background", Material.BLACK_STAINED_GLASS_PANE, 8999, null)));
        invAPI.registerButton("none", new DummyButton("glass_red", new ButtonState("none", "background", Material.RED_STAINED_GLASS_PANE, 8999, null)));

        invAPI.registerButton("none", new DummyButton("glass_white", new ButtonState("none", "background", Material.WHITE_STAINED_GLASS_PANE, 8999, null)));

        invAPI.registerButton("none", new DummyButton("glass_green", new ButtonState("none", "background", Material.GREEN_STAINED_GLASS_PANE, 8999, null)));
        invAPI.registerButton("none", new DummyButton("glass_purple", new ButtonState("none", "background", Material.PURPLE_STAINED_GLASS_PANE, 8999, null)));
        invAPI.registerButton("none", new DummyButton("glass_pink", new ButtonState("none", "background", Material.PINK_STAINED_GLASS_PANE, 8999, null)));

        invAPI.registerButton("none", new ToggleButton("gui_help", true, new ButtonState("gui_help_off", new ItemBuilder(Material.PLAYER_HEAD).setPlayerHeadValue("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGVlZjc4ZWRkNDdhNzI1ZmJmOGMyN2JiNmE3N2Q3ZTE1ZThlYmFjZDY1Yzc3ODgxZWM5ZWJmNzY4NmY3YzgifX19").create(), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            guiHandler.setHelpEnabled(true);
            return true;
        }), new ButtonState("gui_help_on", new ItemBuilder(Material.PLAYER_HEAD).setPlayerHeadValue("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGVlZjc4ZWRkNDdhNzI1ZmJmOGMyN2JiNmE3N2Q3ZTE1ZThlYmFjZDY1Yzc3ODgxZWM5ZWJmNzY4NmY3YzgifX19").create(), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            guiHandler.setHelpEnabled(false);
            return true;
        })));
        invAPI.registerButton("none", new ActionButton("back", new ButtonState("none", "back", new ItemBuilder(Material.PLAYER_HEAD).setPlayerHeadValue("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODY0Zjc3OWE4ZTNmZmEyMzExNDNmYTY5Yjk2YjE0ZWUzNWMxNmQ2NjllMTljNzVmZDFhN2RhNGJmMzA2YyJ9fX0=").create(), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            guiHandler.openPreviousInv();
            return true;
        })));
    }

    private void registerInvs() {
        //Main Cluster
        GuiCluster mainCluster = invAPI.getOrRegisterGuiCluster("none");
        {
            mainCluster.registerGuiWindow(new MainMenu(invAPI, customCrafting));
            mainCluster.registerGuiWindow(new ItemEditor(invAPI, customCrafting));
            mainCluster.registerGuiWindow(new RecipeEditor(invAPI, customCrafting));
            mainCluster.registerGuiWindow(new RecipesList(invAPI, customCrafting));
            mainCluster.registerGuiWindow(new Settings(invAPI, customCrafting));
            mainCluster.registerGuiWindow(new PatronsMenu(invAPI, customCrafting));
            mainCluster.setMainmenu("main_menu");

            mainCluster.registerButton(new ActionButton("patreon", new ButtonState("main_menu", "patreon", PlayerHeadUtils.getViaURL("5693b66a595f78af3f51f4efa4c13375b1b958e6f4c507a47c4fe565cc275"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                guiHandler.changeToInv("patrons_menu");
                return true;
            })), invAPI.getWolfyUtilities());
            mainCluster.registerButton(new ActionButton("instagram", new ButtonState("main_menu", "instagram", PlayerHeadUtils.getViaURL("ac88d6163fabe7c5e62450eb37a074e2e2c88611c998536dbd8429faa0819453"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                api.sendActionMessage(player, new ClickData("&7[&3Click here to go to Instagram&7]", null, new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.instagram.com/_gunnar.h_/")));
                return true;
            })), invAPI.getWolfyUtilities());
            mainCluster.registerButton(new ActionButton("youtube", new ButtonState("main_menu", "youtube", PlayerHeadUtils.getViaURL("b4353fd0f86314353876586075b9bdf0c484aab0331b872df11bd564fcb029ed"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                api.sendActionMessage(player, new ClickData("&7[&3Click here to go to YouTube&7]", null, new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.youtube.com/channel/UCTlqRLm4PxZuAI4nVN4X74g")));
                return true;
            })), invAPI.getWolfyUtilities());
            mainCluster.registerButton(new ActionButton("discord", new ButtonState("main_menu", "discord", PlayerHeadUtils.getViaURL("4d42337be0bdca2128097f1c5bb1109e5c633c17926af5fb6fc20000011aeb53"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                api.sendActionMessage(player, new ClickData("&7[&3Click here to join Discord&7]", null, new ClickEvent(ClickEvent.Action.OPEN_URL, "https://discord.gg/qGhDTSr")));
                return true;
            })), invAPI.getWolfyUtilities());
        }

        GuiCluster recipeCreator = invAPI.getOrRegisterGuiCluster("recipe_creator");
        {
            recipeCreator.registerGuiWindow(new WorkbenchCreator(invAPI, customCrafting));
            recipeCreator.registerGuiWindow(new CookingCreator(invAPI, customCrafting));
            recipeCreator.registerGuiWindow(new AnvilCreator(invAPI, customCrafting));
            recipeCreator.registerGuiWindow(new CauldronCreator(invAPI, customCrafting));
            recipeCreator.registerGuiWindow(new StonecutterCreator(invAPI, customCrafting));
            recipeCreator.registerGuiWindow(new GrindstoneCreator(invAPI, customCrafting));
            recipeCreator.registerGuiWindow(new EliteWorkbenchCreator(invAPI, customCrafting));
            recipeCreator.registerGuiWindow(new BrewingCreator(invAPI, customCrafting));
            recipeCreator.registerGuiWindow(new ConditionsMenu(invAPI, customCrafting));
            recipeCreator.registerGuiWindow(new VariantMenu(invAPI, customCrafting));

            recipeCreator.registerButton(new ActionButton("conditions", new ButtonState("conditions", Material.CYAN_CONCRETE_POWDER, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                guiHandler.changeToInv("conditions");
                return true;
            })), api);
        }

        GuiCluster recipeBook = invAPI.getOrRegisterGuiCluster("recipe_book");
        {
            recipeBook.registerGuiWindow(new RecipeBook(invAPI, customCrafting));
            recipeBook.registerGuiWindow(new me.wolfyscript.customcrafting.gui.recipebook.MainMenu(invAPI, customCrafting));
            recipeBook.setMainmenu("main_menu");
            recipeBook.registerButton(new ItemCategoryButton(customCrafting), api);
            recipeBook.registerButton(new ActionButton("next_page", new ButtonState("next_page", PlayerHeadUtils.getViaURL("c86185b1d519ade585f184c34f3f3e20bb641deb879e81378e4eaf209287"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                KnowledgeBook book = ((TestCache) guiHandler.getCustomCache()).getKnowledgeBook();
                book.setPage(book.getPage() + 1);
                return true;
            })), api);
            recipeBook.registerButton(new ActionButton("previous_page", new ButtonState("previous_page", PlayerHeadUtils.getViaURL("ad73cf66d31b83cd8b8644c15958c1b73c8d97323b801170c1d8864bb6a846d"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                KnowledgeBook book = ((TestCache) guiHandler.getCustomCache()).getKnowledgeBook();
                book.setPage(book.getPage() > 0 ? book.getPage() - 1 : 0);
                return true;
            })), api);

            recipeBook.registerButton(new ActionButton("back_to_list", new ButtonState("back_to_list", Material.BARRIER, (guiHandler, player, inventory, slot, inventoryClickEvent) -> {
                TestCache cache = (TestCache) guiHandler.getCustomCache();
                KnowledgeBook book = cache.getKnowledgeBook();
                book.stopTimerTask();
                IngredientContainerButton.resetButtons(guiHandler);
                book.setResearchItems(new ArrayList<>());
                book.setSubFolderRecipes(new ArrayList<>());
                book.setSubFolder(0);
                return true;
            })), api);

            recipeBook.registerButton(new ToggleButton("permission", new ButtonState("permission.disabled", Material.RED_CONCRETE, (guiHandler, player, inventory, i, inventoryClickEvent) -> true), new ButtonState("permission.enabled", Material.GREEN_CONCRETE, (guiHandler, player, inventory, i, inventoryClickEvent) -> true)), api);
            recipeBook.registerButton(new MultipleChoiceButton("workbench.filter_button", new ButtonState("workbench.filter_button.all", Material.CRAFTING_TABLE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                ((TestCache) guiHandler.getCustomCache()).getKnowledgeBook().setWorkbenchFilter(KnowledgeBook.WorkbenchFilter.ADVANCED);
                return true;
            }), new ButtonState("workbench.filter_button.advanced", new ItemBuilder(Material.CRAFTING_TABLE).addUnsafeEnchantment(Enchantment.DURABILITY, 0).create(), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                ((TestCache) guiHandler.getCustomCache()).getKnowledgeBook().setWorkbenchFilter(KnowledgeBook.WorkbenchFilter.NORMAL);
                return true;
            }), new ButtonState("workbench.filter_button.normal", Material.CRAFTING_TABLE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                ((TestCache) guiHandler.getCustomCache()).getKnowledgeBook().setWorkbenchFilter(KnowledgeBook.WorkbenchFilter.ALL);
                return true;
            })), api);
            recipeBook.registerButton(new DummyButton("workbench.shapeless_on", new ButtonState("workbench.shapeless_on", Material.CRAFTING_TABLE)), api);
            recipeBook.registerButton(new DummyButton("workbench.shapeless_off", new ButtonState("workbench.shapeless_off", Material.CRAFTING_TABLE)), api);

            recipeBook.registerButton(new DummyButton("anvil.durability", new ButtonState("anvil.durability", Material.ANVIL, (hashMap, guiHandler, player, itemStack, i, b) -> {
                hashMap.put("%var%", ((CustomAnvilRecipe) ((TestCache) guiHandler.getCustomCache()).getKnowledgeBook().getCurrentRecipe()).getDurability());
                return itemStack;
            })), api);
            recipeBook.registerButton(new DummyButton("anvil.result", new ButtonState("anvil.result", Material.ANVIL)), api);
            recipeBook.registerButton(new DummyButton("anvil.none", new ButtonState("anvil.none", Material.ANVIL)), api);

            recipeBook.registerButton(new DummyButton("cooking.icon", new ButtonState("cooking.icon", Material.FURNACE, (hashMap, guiHandler, player, itemStack, i, b) -> {
                KnowledgeBook knowledgeBook = ((TestCache) guiHandler.getCustomCache()).getKnowledgeBook();
                RecipeType recipeType = knowledgeBook.getCurrentRecipe().getRecipeType();
                CookingRecipe<?> cookingRecipe = ((CustomCookingRecipe<?>) knowledgeBook.getCurrentRecipe()).getVanillaRecipe();
                itemStack.setType(Material.matchMaterial(recipeType.toString()));
                hashMap.put("%type%", "&7" + StringUtils.capitalize(recipeType.getId().replace("_", " ")));
                hashMap.put("%time%", cookingRecipe.getCookingTime());
                hashMap.put("%xp%", cookingRecipe.getExperience());
                return itemStack;
            })), api);
            recipeBook.registerButton(new DummyButton("furnace", new ButtonState("furnace", Material.FURNACE)), api);
            recipeBook.registerButton(new DummyButton("stonecutter", new ButtonState("stonecutter", Material.STONECUTTER)), api);
            recipeBook.registerButton(new DummyButton("blast_furnace", new ButtonState("blast_furnace", Material.BLAST_FURNACE)), api);
            recipeBook.registerButton(new DummyButton("campfire", new ButtonState("campire", Material.CAMPFIRE)), api);
            recipeBook.registerButton(new DummyButton("blast_furnace", new ButtonState("blast_furnace", Material.BLAST_FURNACE)), api);
            recipeBook.registerButton(new DummyButton("grindstone", new ButtonState("grindstone", Material.GRINDSTONE)), api);
            recipeBook.registerButton(new DummyButton("smoker", new ButtonState("smoker", Material.SMOKER)), api);
            recipeBook.registerButton(new DummyButton("cauldron.water.disabled", new ButtonState("cauldron.water.disabled", Material.CAULDRON)), api);
            recipeBook.registerButton(new DummyButton("cauldron.water.enabled", new ButtonState("cauldron.water.enabled", PlayerHeadUtils.getViaURL("848a19cdf42d748b41b72fb4376ae3f63c1165d2dce0651733df263446c77ba6"), (hashMap, guiHandler, player, itemStack, i, b) -> {
                KnowledgeBook knowledgeBook = ((TestCache) guiHandler.getCustomCache()).getKnowledgeBook();
                hashMap.put("%lvl%", ((CauldronRecipe) knowledgeBook.getCurrentRecipe()).getWaterLevel());
                return itemStack;
            })), api);
            recipeBook.registerButton(new DummyButton("cauldron.fire.disabled", new ButtonState("cauldron.fire.disabled", Material.FLINT)), api);
            recipeBook.registerButton(new DummyButton("cauldron.fire.enabled", new ButtonState("cauldron.fire.enabled", Material.FLINT_AND_STEEL)), api);
            recipeBook.registerButton(new DummyButton("brewing.icon", new ButtonState("brewing.icon", Material.BREWING_STAND, (hashMap, guiHandler, player, itemStack, i, b) -> {
                BrewingRecipe cookingRecipe = (BrewingRecipe) (((TestCache) guiHandler.getCustomCache()).getKnowledgeBook()).getCurrentRecipe();
                hashMap.put("%time%", cookingRecipe.getBrewTime());
                hashMap.put("%cost%", cookingRecipe.getFuelCost());
                return itemStack;
            })), api);
            recipeBook.registerButton(new DummyButton("brewing.potion_duration", new ButtonState("brewing.potion_duration", Material.CLOCK, (hashMap, guiHandler, player, itemStack, i, b) -> {
                BrewingRecipe cookingRecipe = (BrewingRecipe) (((TestCache) guiHandler.getCustomCache()).getKnowledgeBook()).getCurrentRecipe();
                hashMap.put("%value%", cookingRecipe.getDurationChange());
                return itemStack;
            })), api);
            recipeBook.registerButton(new DummyButton("brewing.potion_amplifier", new ButtonState("brewing.potion_amplifier", Material.IRON_SWORD, (hashMap, guiHandler, player, itemStack, i, b) -> {
                BrewingRecipe cookingRecipe = (BrewingRecipe) (((TestCache) guiHandler.getCustomCache()).getKnowledgeBook()).getCurrentRecipe();
                hashMap.put("%value%", cookingRecipe.getAmplifierChange());
                return itemStack;
            })), api);
            for (int i = 0; i < 54; i++) {
                recipeBook.registerButton(new IngredientContainerButton(i, customCrafting), api);
            }

            recipeBook.registerButton(new DummyButton("conditions.world_time", new ButtonState("conditions.world_time", Material.CLOCK, (hashMap, guiHandler, player, itemStack, i, b) -> {
                ICustomRecipe recipe = (((TestCache) guiHandler.getCustomCache()).getKnowledgeBook()).getCurrentRecipe();
                hashMap.put("%value%", ((WorldTimeCondition) recipe.getConditions().getByID("world_time")).getTime());

                if (recipe.getConditions().getByID("world_time").getOption().equals(Conditions.Option.EXACT)) {
                    hashMap.put("%mode%", "");
                } else {
                    hashMap.put("%mode%", recipe.getConditions().getByID("world_time").getOption().getDisplayString(api));
                }
                return itemStack;
            })), api);

            recipeBook.registerButton(new ActionButton("conditions.weather", new ButtonState("conditions.weather", Material.WATER_BUCKET, (hashMap, guiHandler, player, itemStack, i, b) -> {
                ICustomRecipe recipe = (((TestCache) guiHandler.getCustomCache()).getKnowledgeBook()).getCurrentRecipe();
                hashMap.put("%value%", ((WeatherCondition) recipe.getConditions().getByID("weather")).getWeather().getDisplay(api));
                return itemStack;
            })), api);

            recipeBook.registerButton(new ActionButton("conditions.permission", new ButtonState("conditions.permission", Material.REDSTONE, (hashMap, guiHandler, player, itemStack, i, b) -> {
                ICustomRecipe recipe = (((TestCache) guiHandler.getCustomCache()).getKnowledgeBook()).getCurrentRecipe();
                hashMap.put("%value%", ((PermissionCondition) recipe.getConditions().getByID("permission")).getPermission());
                return itemStack;
            })), api);

        }

        GuiCluster craftingCluster = invAPI.getOrRegisterGuiCluster("crafting");
        {
            craftingCluster.registerGuiWindow(new CraftingWindow3(invAPI, customCrafting));
            craftingCluster.registerGuiWindow(new CraftingWindow4(invAPI, customCrafting));
            craftingCluster.registerGuiWindow(new CraftingWindow5(invAPI, customCrafting));
            craftingCluster.registerGuiWindow(new CraftingWindow6(invAPI, customCrafting));
            craftingCluster.registerGuiWindow(new CraftingRecipeBook(invAPI, customCrafting));
            craftingCluster.setMainmenu("crafting_3");
            craftingCluster.registerButton(new ActionButton("knowledge_book", new ButtonState("crafting", "knowledge_book", Material.KNOWLEDGE_BOOK, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
                TestCache cache = ((TestCache) guiHandler.getCustomCache());
                KnowledgeBook knowledgeBook = cache.getKnowledgeBook();
                knowledgeBook.setRecipeItems(new ArrayList<>());
                knowledgeBook.stopTimerTask();
                IngredientContainerButton.resetButtons(guiHandler);
                knowledgeBook.setRecipeItems(new ArrayList<>());
                guiHandler.changeToInv("recipe_book");
                return true;
            })), api);
        }

        GuiCluster itemCreator = invAPI.getOrRegisterGuiCluster("item_creator");
        itemCreator.registerGuiWindow(new ItemCreator(invAPI, customCrafting));

        GuiCluster particleCluster = invAPI.getOrRegisterGuiCluster("particle_creator");
        particleCluster.registerGuiWindow(new me.wolfyscript.customcrafting.gui.particle_creator.MainMenu(invAPI, customCrafting));


    }


}
