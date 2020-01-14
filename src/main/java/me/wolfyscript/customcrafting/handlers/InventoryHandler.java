package me.wolfyscript.customcrafting.handlers;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.PlayerCache;
import me.wolfyscript.customcrafting.data.cache.EliteWorkbench;
import me.wolfyscript.customcrafting.data.cache.KnowledgeBook;
import me.wolfyscript.customcrafting.gui.Setting;
import me.wolfyscript.customcrafting.gui.crafting.*;
import me.wolfyscript.customcrafting.gui.recipebook.buttons.ItemCategoryButton;
import me.wolfyscript.customcrafting.gui.item_creator.ItemCreator;
import me.wolfyscript.customcrafting.gui.main_gui.*;
import me.wolfyscript.customcrafting.gui.recipe_creator.ConditionsMenu;
import me.wolfyscript.customcrafting.gui.recipe_creator.VariantMenu;
import me.wolfyscript.customcrafting.gui.recipe_creator.recipe_creators.*;
import me.wolfyscript.customcrafting.gui.recipebook.RecipeBook;
import me.wolfyscript.customcrafting.gui.recipebook.buttons.RecipeBookContainerButton;
import me.wolfyscript.customcrafting.recipes.types.anvil.CustomAnvilRecipe;
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
import me.wolfyscript.utilities.api.utils.chat.HoverEvent;
import me.wolfyscript.utilities.api.utils.item_builder.ItemBuilder;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.CookingRecipe;
import org.bukkit.inventory.FurnaceRecipe;

public class InventoryHandler {

    private WolfyUtilities api;
    private InventoryAPI invAPI;

    public InventoryHandler(WolfyUtilities api) {
        this.api = api;
        this.invAPI = api.getInventoryAPI();
    }

    public void init() {
        api.sendConsoleMessage("$msg.startup.inventories$");
        registerInvs();
        invAPI.registerButton("none", new DummyButton("glass_gray", new ButtonState("none", "background", Material.GRAY_STAINED_GLASS_PANE, null)));
        invAPI.registerButton("none", new DummyButton("glass_black", new ButtonState("none", "background", Material.BLACK_STAINED_GLASS_PANE, null)));
        invAPI.registerButton("none", new DummyButton("glass_red", new ButtonState("none", "background", Material.RED_STAINED_GLASS_PANE, null)));
        invAPI.registerButton("none", new DummyButton("glass_white", new ButtonState("none", "background", Material.WHITE_STAINED_GLASS_PANE, null)));
        invAPI.registerButton("none", new DummyButton("glass_green", new ButtonState("none", "background", Material.GREEN_STAINED_GLASS_PANE, null)));
        invAPI.registerButton("none", new DummyButton("glass_purple", new ButtonState("none", "background", Material.PURPLE_STAINED_GLASS_PANE, null)));
        invAPI.registerButton("none", new DummyButton("glass_pink", new ButtonState("none", "background", Material.PINK_STAINED_GLASS_PANE, null)));
        invAPI.registerButton("none", new ToggleButton("gui_help", true, new ButtonState("gui_help_off", WolfyUtilities.getCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGVlZjc4ZWRkNDdhNzI1ZmJmOGMyN2JiNmE3N2Q3ZTE1ZThlYmFjZDY1Yzc3ODgxZWM5ZWJmNzY4NmY3YzgifX19"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            guiHandler.setHelpEnabled(true);
            return true;
        }), new ButtonState("gui_help_on", WolfyUtilities.getCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGVlZjc4ZWRkNDdhNzI1ZmJmOGMyN2JiNmE3N2Q3ZTE1ZThlYmFjZDY1Yzc3ODgxZWM5ZWJmNzY4NmY3YzgifX19"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            guiHandler.setHelpEnabled(false);
            return true;
        })));
        invAPI.registerButton("none", new ActionButton("back", new ButtonState("none", "back", WolfyUtilities.getCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODY0Zjc3OWE4ZTNmZmEyMzExNDNmYTY5Yjk2YjE0ZWUzNWMxNmQ2NjllMTljNzVmZDFhN2RhNGJmMzA2YyJ9fX0="), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            guiHandler.openPreviousInv();
            return true;
        })));
    }

    private void registerInvs() {
        //Main Cluster
        GuiCluster mainCluster = invAPI.getOrRegisterGuiCluster("none");
        mainCluster.registerGuiWindow(new MainMenu(invAPI));
        mainCluster.registerGuiWindow(new ItemEditor(invAPI));
        mainCluster.registerGuiWindow(new RecipeEditor(invAPI));
        mainCluster.registerGuiWindow(new RecipesList(invAPI));
        mainCluster.registerGuiWindow(new Settings(invAPI));
        mainCluster.setMainmenu("main_menu");

        mainCluster.registerButton(new ActionButton("patreon", new ButtonState("main_menu", "patreon", WolfyUtilities.getSkullViaURL("5693b66a595f78af3f51f4efa4c13375b1b958e6f4c507a47c4fe565cc275"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            api.openBook(player, false,
                    new ClickData[]{
                            new ClickData("&c&l      Patreon\n", null),
                            new ClickData("&8Special thanks to my \n&8Patrons\n", null),
                            new ClickData("&3&lApprehentice\n", null),
                            new ClickData("&3&lAlex\n", null),
                            new ClickData("&8for supporting this project!\n", null),
                            new ClickData("&8[&cBecome a Patron&8]\n", null, new HoverEvent(HoverEvent.Action.SHOW_TEXT, "§7Goto WolfyScript's Patreon"), new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.patreon.com/wolfyscript")),
                            new ClickData("\n&8Also special thanks to &8the &8&lCommunity &8and &8&lDonators &8for &8supporting my &8projects!\n", null)
                    },
                    new ClickData[]{
                            new ClickData("   &ka&8&lSocialmedia&ka\n", null),
                            new ClickData("&8Support me on other Socialmedia.\n\n", null),
                            new ClickData("&5&lInstagram\n\n", null, new HoverEvent(HoverEvent.Action.SHOW_TEXT, "§7Goto §5Instagram"), new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.instagram.com/_gunnar.h_/")),
                            new ClickData("&4&lYouTube\n\n", null, new HoverEvent(HoverEvent.Action.SHOW_TEXT, "§7Goto §4YouTube"), new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.youtube.com/channel/UCTlqRLm4PxZuAI4nVN4X74g")),
                            new ClickData("\n&8Join the Community on &8the Discord Server", null),
                            new ClickData("\n&9&lDiscord Invatation", null, new HoverEvent(HoverEvent.Action.SHOW_TEXT, "§7Get Invite Link"), new ClickEvent(ClickEvent.Action.OPEN_URL, "https://discord.gg/qGhDTSr"))
                    }
            );
            return true;
        })), invAPI.getWolfyUtilities());
        mainCluster.registerButton(new ActionButton("instagram", new ButtonState("main_menu", "instagram", WolfyUtilities.getSkullViaURL("ac88d6163fabe7c5e62450eb37a074e2e2c88611c998536dbd8429faa0819453"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            api.sendActionMessage(player, new ClickData("&7[&3Click here to go to Instagram&7]", null, new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.instagram.com/_gunnar.h_/")));
            return true;
        })), invAPI.getWolfyUtilities());
        mainCluster.registerButton(new ActionButton("youtube", new ButtonState("main_menu", "youtube", WolfyUtilities.getSkullViaURL("b4353fd0f86314353876586075b9bdf0c484aab0331b872df11bd564fcb029ed"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            api.sendActionMessage(player, new ClickData("&7[&3Click here to go to YouTube&7]", null, new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.youtube.com/channel/UCTlqRLm4PxZuAI4nVN4X74g")));
            return true;
        })), invAPI.getWolfyUtilities());
        mainCluster.registerButton(new ActionButton("discord", new ButtonState("main_menu", "discord", WolfyUtilities.getSkullViaURL("4d42337be0bdca2128097f1c5bb1109e5c633c17926af5fb6fc20000011aeb53"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            api.sendActionMessage(player, new ClickData("&7[&3Click here to join Discord&7]", null, new ClickEvent(ClickEvent.Action.OPEN_URL, "https://discord.gg/qGhDTSr")));
            return true;
        })), invAPI.getWolfyUtilities());

        GuiCluster recipeCreator = invAPI.getOrRegisterGuiCluster("recipe_creator");
        recipeCreator.registerGuiWindow(new AnvilCreator(invAPI));
        recipeCreator.registerGuiWindow(new CookingCreator(invAPI));
        recipeCreator.registerGuiWindow(new CauldronCreator(invAPI));
        recipeCreator.registerGuiWindow(new StonecutterCreator(invAPI));
        recipeCreator.registerGuiWindow(new WorkbenchCreator(invAPI));
        recipeCreator.registerGuiWindow(new EliteWorkbenchCreator(invAPI));
        recipeCreator.registerGuiWindow(new ConditionsMenu(invAPI));
        recipeCreator.registerGuiWindow(new VariantMenu(invAPI));

        recipeCreator.registerButton(new ActionButton("conditions", new ButtonState("conditions", Material.CYAN_CONCRETE_POWDER, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            guiHandler.changeToInv("conditions");
            return true;
        })), api);

        GuiCluster recipeBook = invAPI.getOrRegisterGuiCluster("recipe_book");
        recipeBook.registerGuiWindow(new RecipeBook(invAPI));
        recipeBook.registerGuiWindow(new me.wolfyscript.customcrafting.gui.recipebook.MainMenu(invAPI));
        recipeBook.setMainmenu("main_menu");
        recipeBook.registerButton(new ItemCategoryButton(), api);
        recipeBook.registerButton(new ActionButton("next_page", new ButtonState("next_page", WolfyUtilities.getSkullViaURL("c86185b1d519ade585f184c34f3f3e20bb641deb879e81378e4eaf209287"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            KnowledgeBook book = CustomCrafting.getPlayerCache(player).getKnowledgeBook();
            book.setPage(book.getPage() + 1);
            return true;
        })), api);
        recipeBook.registerButton(new ActionButton("previous_page", new ButtonState("previous_page", WolfyUtilities.getSkullViaURL("ad73cf66d31b83cd8b8644c15958c1b73c8d97323b801170c1d8864bb6a846d"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            KnowledgeBook book = CustomCrafting.getPlayerCache(player).getKnowledgeBook();
            book.setPage(book.getPage() > 0 ? book.getPage() - 1 : 0);
            return true;
        })), api);
        recipeBook.registerButton(new ToggleButton("permission", new ButtonState("permission.disabled", Material.RED_CONCRETE, (guiHandler, player, inventory, i, inventoryClickEvent) -> true), new ButtonState("permission.enabled", Material.GREEN_CONCRETE, (guiHandler, player, inventory, i, inventoryClickEvent) -> true)), api);
        recipeBook.registerButton(new MultipleChoiceButton("workbench.filter_button", new ButtonState("workbench.filter_button.all", Material.CRAFTING_TABLE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            CustomCrafting.getPlayerCache(player).getKnowledgeBook().setWorkbenchFilter(KnowledgeBook.WorkbenchFilter.ADVANCED);
            return true;
        }), new ButtonState("workbench.filter_button.advanced", new ItemBuilder(Material.CRAFTING_TABLE).addUnsafeEnchantment(Enchantment.DURABILITY, 0).create(), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            CustomCrafting.getPlayerCache(player).getKnowledgeBook().setWorkbenchFilter(KnowledgeBook.WorkbenchFilter.NORMAL);
            return true;
        }), new ButtonState("workbench.filter_button.normal", Material.CRAFTING_TABLE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            CustomCrafting.getPlayerCache(player).getKnowledgeBook().setWorkbenchFilter(KnowledgeBook.WorkbenchFilter.ALL);
            return true;
        })), api);
        recipeBook.registerButton(new DummyButton("workbench.shapeless_on", new ButtonState("workbench.shapeless_on", Material.CRAFTING_TABLE)), api);
        recipeBook.registerButton(new DummyButton("workbench.shapeless_off", new ButtonState("workbench.shapeless_off", Material.CRAFTING_TABLE)), api);

        recipeBook.registerButton(new DummyButton("anvil.durability", new ButtonState("anvil.durability", Material.ANVIL, (hashMap, guiHandler, player, itemStack, i, b) -> {
            hashMap.put("%var%", ((CustomAnvilRecipe)CustomCrafting.getPlayerCache(player).getKnowledgeBook().getCustomRecipe()).getDurability());
            return itemStack;
        })), api);
        recipeBook.registerButton(new DummyButton("anvil.result", new ButtonState("anvil.result", Material.ANVIL)), api);
        recipeBook.registerButton(new DummyButton("anvil.none", new ButtonState("anvil.none", Material.ANVIL)), api);

        recipeBook.registerButton(new DummyButton("cooking.icon", new ButtonState("cooking.icon", Material.FURNACE, (hashMap, guiHandler, player, itemStack, i, b) -> {
            KnowledgeBook knowledgeBook = CustomCrafting.getPlayerCache(player).getKnowledgeBook();
            itemStack.setType(Material.matchMaterial(knowledgeBook.getSetting().toString()));
            hashMap.put("%type%", "&7"+ StringUtils.capitalize(knowledgeBook.getSetting().getId().replace("_", " ")));
            if (WolfyUtilities.hasVillagePillageUpdate()){
                CookingRecipe cookingRecipe = (CookingRecipe) knowledgeBook.getCustomRecipe();
                hashMap.put("%time%", cookingRecipe.getCookingTime());
                hashMap.put("%xp%", cookingRecipe.getExperience());
            }else{
                FurnaceRecipe recipe = (FurnaceRecipe) knowledgeBook.getCustomRecipe();
                hashMap.put("%time%", recipe.getCookingTime());
                hashMap.put("%xp%", recipe.getExperience());
            }
            return itemStack;
        })), api);
        recipeBook.registerButton(new DummyButton("furnace", new ButtonState("furnace", Material.FURNACE, (hashMap, guiHandler, player, itemStack, i, b) -> {
            return itemStack;
        })), api);

        if(WolfyUtilities.hasVillagePillageUpdate()){
            recipeBook.registerButton(new DummyButton("stonecutter", new ButtonState("stonecutter", Material.STONECUTTER)), api);
            recipeBook.registerButton(new DummyButton("blast_furnace", new ButtonState("blast_furnace", Material.BLAST_FURNACE, (hashMap, guiHandler, player, itemStack, i, b) -> {
                return itemStack;
            })), api);
            recipeBook.registerButton(new DummyButton("campfire", new ButtonState("campire", Material.CAMPFIRE, (hashMap, guiHandler, player, itemStack, i, b) -> {
                return itemStack;
            })), api);
            recipeBook.registerButton(new DummyButton("blast_furnace", new ButtonState("blast_furnace", Material.BLAST_FURNACE, (hashMap, guiHandler, player, itemStack, i, b) -> {
                return itemStack;
            })), api);
            recipeBook.registerButton(new DummyButton("smoker", new ButtonState("smoker", Material.SMOKER, (hashMap, guiHandler, player, itemStack, i, b) -> {
                return itemStack;
            })), api);
            recipeBook.registerButton(new DummyButton("cauldron.water.disabled", new ButtonState("cauldron.water.disabled", Material.CAULDRON, (hashMap, guiHandler, player, itemStack, i, b) -> {
                return itemStack;
            })), api);
            recipeBook.registerButton(new DummyButton("cauldron.water.enabled", new ButtonState("cauldron.water.enabled", WolfyUtilities.getSkullViaURL("848a19cdf42d748b41b72fb4376ae3f63c1165d2dce0651733df263446c77ba6"), (hashMap, guiHandler, player, itemStack, i, b) -> {
                KnowledgeBook knowledgeBook = CustomCrafting.getPlayerCache(player).getKnowledgeBook();
                hashMap.put("%lvl%", ((CauldronRecipe)knowledgeBook.getCustomRecipe()).getWaterLevel());
                return itemStack;
            })), api);
            recipeBook.registerButton(new DummyButton("cauldron.fire.disabled", new ButtonState("cauldron.fire.disabled", Material.FLINT, (hashMap, guiHandler, player, itemStack, i, b) -> {
                return itemStack;
            })), api);
            recipeBook.registerButton(new DummyButton("cauldron.fire.enabled", new ButtonState("cauldron.fire.enabled", Material.FLINT_AND_STEEL, (hashMap, guiHandler, player, itemStack, i, b) -> {
                return itemStack;
            })), api);
        }

        GuiCluster craftingCluster = new GuiCluster();
        invAPI.registerCustomGuiCluster("crafting", craftingCluster);
        craftingCluster.registerGuiWindow(new CraftingWindow3(invAPI));
        craftingCluster.registerGuiWindow(new CraftingWindow4(invAPI));
        craftingCluster.registerGuiWindow(new CraftingWindow5(invAPI));
        craftingCluster.registerGuiWindow(new CraftingWindow6(invAPI));
        craftingCluster.registerGuiWindow(new CraftingRecipeBook(invAPI));
        craftingCluster.setMainmenu("crafting_3");
        craftingCluster.registerButton(new ActionButton("knowledge_book", new ButtonState("crafting", "knowledge_book", Material.KNOWLEDGE_BOOK, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            PlayerCache cache = CustomCrafting.getPlayerCache(player);
            EliteWorkbench eliteWorkbench = cache.getEliteWorkbench();
            KnowledgeBook knowledgeBook = cache.getKnowledgeBook();
            if (eliteWorkbench.getEliteWorkbenchData().isAdvancedRecipes()) {
                knowledgeBook.setSetting(Setting.WORKBENCH);
            } else {
                knowledgeBook.setSetting(Setting.ELITE_WORKBENCH);
            }
            guiHandler.changeToInv("recipe_book");
            return true;
        })), api);

        GuiCluster itemCreator = invAPI.getOrRegisterGuiCluster("item_creator");
        itemCreator.registerGuiWindow(new ItemCreator(invAPI));

    }


}
