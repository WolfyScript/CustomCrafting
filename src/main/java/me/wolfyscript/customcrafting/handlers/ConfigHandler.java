package me.wolfyscript.customcrafting.handlers;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.MainConfig;
import me.wolfyscript.customcrafting.configs.custom_data.KnowledgeBookData;
import me.wolfyscript.customcrafting.configs.recipebook.RecipeBook;
import me.wolfyscript.customcrafting.recipes.types.workbench.ShapedCraftRecipe;
import me.wolfyscript.customcrafting.recipes.types.workbench.ShapelessCraftRecipe;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.config.ConfigAPI;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.custom_items.api_references.WolfyUtilitiesRef;
import me.wolfyscript.utilities.api.language.Language;
import me.wolfyscript.utilities.api.language.LanguageAPI;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.json.jackson.JacksonUtil;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;

import java.io.File;
import java.io.IOException;

public class ConfigHandler {

    private final CustomCrafting customCrafting;
    private final WolfyUtilities api;
    private final ConfigAPI configAPI;
    private final LanguageAPI languageAPI;
    private MainConfig mainConfig;
    private RecipeBook recipeBook;

    public ConfigHandler(CustomCrafting customCrafting) {
        this.api = WolfyUtilities.get(customCrafting);
        this.customCrafting = customCrafting;
        this.configAPI = api.getConfigAPI();
        this.languageAPI = api.getLanguageAPI();
    }

    public void load() throws IOException {
        //Load core config!
        //Makes sure that if a config with the old name already exists, it's renamed to the new config name.
        File oldConfigFile = new File(customCrafting.getDataFolder().getPath(), "main_config.yml");
        if(oldConfigFile.exists()){
            oldConfigFile.renameTo(new File(customCrafting.getDataFolder().getPath(), "config.yml"));
        }
        this.mainConfig = new MainConfig(configAPI, customCrafting);
        mainConfig.loadDefaults();
        configAPI.registerConfig(mainConfig);
        //

        try {
            loadLang();
        } catch (IOException e) {
            e.printStackTrace();
        }
        api.getConfigAPI().setPrettyPrinting(mainConfig.isPrettyPrinting());

        if (mainConfig.resetKnowledgeBook()) {
            //Creating the knowledgebook item and recipe
            NamespacedKey knowledgebookKey = new NamespacedKey("customcrafting", "knowledge_book");
            CustomItem knowledgeBook = new CustomItem(Material.KNOWLEDGE_BOOK);
            knowledgeBook.setDisplayName(me.wolfyscript.utilities.util.chat.ChatColor.convert("&6Knowledge Book"));
            knowledgeBook.addLoreLine(me.wolfyscript.utilities.util.chat.ChatColor.convert("&7Contains some interesting recipes..."));
            knowledgeBook.addUnsafeEnchantment(Enchantment.DURABILITY, 5);
            knowledgeBook.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            ((KnowledgeBookData) knowledgeBook.getCustomData(new NamespacedKey("customcrafting","knowledge_book"))).setEnabled(true);
            customCrafting.saveItem(knowledgebookKey, knowledgeBook);

            ShapelessCraftRecipe knowledgeBookCraft = new ShapelessCraftRecipe();
            knowledgeBookCraft.setIngredient('A', 0, new CustomItem(Material.BOOK));
            knowledgeBookCraft.setIngredient('B', 0, new CustomItem(Material.CRAFTING_TABLE));
            knowledgeBookCraft.setResult(0, new CustomItem(new WolfyUtilitiesRef(knowledgebookKey)));
            knowledgeBookCraft.setNamespacedKey(knowledgebookKey);
            knowledgeBookCraft.save();
        }
        if (mainConfig.resetAdvancedWorkbench()) {
            //Creating the advanced workbench item and recipe
            NamespacedKey workbenchKey = new NamespacedKey("customcrafting", "workbench");

            CustomItem advancedWorkbench = new CustomItem(Material.CRAFTING_TABLE);
            advancedWorkbench.setDisplayName(ChatColor.GOLD + "Advanced Workbench");
            advancedWorkbench.addLoreLine(me.wolfyscript.utilities.util.chat.ChatColor.convert("&7Workbench for advanced crafting"));
            advancedWorkbench.addUnsafeEnchantment(Enchantment.DURABILITY, 5);
            advancedWorkbench.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            customCrafting.saveItem(workbenchKey, advancedWorkbench);

            ShapedCraftRecipe workbenchCraft = new ShapedCraftRecipe();
            workbenchCraft.setMirrorHorizontal(false);
            workbenchCraft.setIngredient('B', 0, new CustomItem(Material.GOLD_INGOT));
            workbenchCraft.setIngredient('E', 0, new CustomItem(Material.CRAFTING_TABLE));
            workbenchCraft.setIngredient('H', 0, new CustomItem(Material.GLOWSTONE_DUST));
            workbenchCraft.setResult(0, new CustomItem(new WolfyUtilitiesRef(workbenchKey)));
            workbenchCraft.setNamespacedKey(workbenchKey);
            workbenchCraft.save();
        }

        //Loading RecipeBook
        customCrafting.saveResource("recipe_book.json", false);
        this.recipeBook = new RecipeBook(customCrafting);
    }

    public void loadLang() throws IOException {
        String chosenLang = customCrafting.getConfigHandler().getConfig().getString("language");
        customCrafting.saveResource("lang/en_US.json", true);
        customCrafting.saveResource("lang/de_DE.json", true);
        customCrafting.saveResource("lang/zh_CN.json", true);

        Language fallBackLanguage = new Language(customCrafting, "en_US");
        languageAPI.registerLanguage(fallBackLanguage);
        System.out.println("Loaded fallback language \"en_US\" v" + fallBackLanguage.getVersion() + " translated by " + String.join(", ", fallBackLanguage.getAuthors()));

        File file = new File(customCrafting.getDataFolder(), "lang/" + chosenLang + ".json");
        if (file.exists()) {
            Language language = new Language(customCrafting, chosenLang);
            languageAPI.registerLanguage(language);
            languageAPI.setActiveLanguage(language);
            System.out.println("Loaded active language \"" + chosenLang + "\" v" + language.getVersion() + " translated by " + String.join(", ", language.getAuthors()));
        }
    }

    public void save() throws IOException {
        JacksonUtil.getObjectWriter(getConfig().isPrettyPrinting()).writeValue(new File(customCrafting.getDataFolder(), "recipe_book.json"), recipeBook);
        getConfig().save();
    }

    public MainConfig getConfig() {
        return mainConfig;
    }

    public RecipeBook getRecipeBook() {
        return recipeBook;
    }
}
