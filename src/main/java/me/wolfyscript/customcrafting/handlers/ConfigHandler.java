package me.wolfyscript.customcrafting.handlers;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.MainConfig;
import me.wolfyscript.customcrafting.configs.custom_data.RecipeBookData;
import me.wolfyscript.customcrafting.configs.recipebook.RecipeBook;
import me.wolfyscript.customcrafting.recipes.types.workbench.ShapedCraftRecipe;
import me.wolfyscript.customcrafting.recipes.types.workbench.ShapelessCraftRecipe;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.customcrafting.utils.recipe_item.Ingredient;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.config.ConfigAPI;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.custom_items.references.WolfyUtilitiesRef;
import me.wolfyscript.utilities.api.language.Language;
import me.wolfyscript.utilities.api.language.LanguageAPI;
import me.wolfyscript.utilities.util.Registry;
import me.wolfyscript.utilities.util.json.jackson.JacksonUtil;
import me.wolfyscript.utilities.util.particles.ParticleAnimation;
import me.wolfyscript.utilities.util.particles.ParticleEffect;
import me.wolfyscript.utilities.util.particles.ParticleLocation;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class ConfigHandler {

    private final CustomCrafting customCrafting;
    private final WolfyUtilities api;
    private final ConfigAPI configAPI;
    private final LanguageAPI languageAPI;
    private final MainConfig mainConfig;
    private RecipeBook recipeBook;

    public ConfigHandler(CustomCrafting customCrafting) {
        this.api = WolfyUtilities.get(customCrafting);
        this.customCrafting = customCrafting;
        this.configAPI = api.getConfigAPI();
        this.languageAPI = api.getLanguageAPI();

        File oldConfigFile = new File(customCrafting.getDataFolder().getPath(), "main_config.yml");//Makes sure that if a config with the old name already exists, it's renamed to the new config name.
        if(oldConfigFile.exists()){
            oldConfigFile.renameTo(new File(customCrafting.getDataFolder().getPath(), "config.yml"));
        }
        this.mainConfig = new MainConfig(configAPI, customCrafting);
        mainConfig.loadDefaults();
        configAPI.registerConfig(mainConfig);

        try {
            loadLang();
        } catch (IOException e) {
            e.printStackTrace();
        }
        api.getConfigAPI().setPrettyPrinting(mainConfig.isPrettyPrinting());
    }

    public void loadDefaults() throws IOException {
        if (!DataHandler.DATA_FOLDER.exists()) { //Check for the old recipes folder and rename it to the new data folder.
            File old = new File(customCrafting.getDataFolder() + File.separator + "recipes");
            if (!old.renameTo(DataHandler.DATA_FOLDER)) {
                customCrafting.getLogger().severe("Couldn't rename folder to the new required names!");
            }
        }
        ParticleAnimation enchantAnimation = new ParticleAnimation(Material.ENCHANTING_TABLE, "Advanced Crafting Table", Arrays.asList("This is the default effect for the advanced crafting table", ""), 0, 2, new ParticleEffect(Particle.ENCHANTMENT_TABLE, 10, 0.5, null, new Vector(0.5, 1.3, 0.5)));
        Registry.PARTICLE_ANIMATIONS.register(CustomCrafting.ADVANCED_CRAFTING_TABLE, enchantAnimation);

        if (mainConfig.resetRecipeBook()) {
            CustomItem knowledgeBook = new CustomItem(Material.KNOWLEDGE_BOOK);
            knowledgeBook.setDisplayName(me.wolfyscript.utilities.util.chat.ChatColor.convert("&6Recipe Book"));
            knowledgeBook.addLoreLine(me.wolfyscript.utilities.util.chat.ChatColor.convert("&7Contains some interesting recipes..."));
            knowledgeBook.addUnsafeEnchantment(Enchantment.DURABILITY, 5);
            knowledgeBook.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            ((RecipeBookData) knowledgeBook.getCustomData(CustomCrafting.RECIPE_BOOK)).setEnabled(true);
            customCrafting.saveItem(CustomCrafting.RECIPE_BOOK, knowledgeBook);

            ShapelessCraftRecipe knowledgeBookCraft = new ShapelessCraftRecipe();
            knowledgeBookCraft.setIngredients('A', new Ingredient(Material.BOOK));
            knowledgeBookCraft.setIngredients('B', new Ingredient(Material.CRAFTING_TABLE));
            knowledgeBookCraft.setResult(0, CustomItem.with(new WolfyUtilitiesRef(NamespacedKeyUtils.fromInternal(CustomCrafting.RECIPE_BOOK))));
            knowledgeBookCraft.setNamespacedKey(CustomCrafting.RECIPE_BOOK);
            knowledgeBookCraft.save();
        }
        if (mainConfig.resetAdvancedWorkbench()) {
            CustomItem advancedWorkbench = new CustomItem(Material.CRAFTING_TABLE);
            advancedWorkbench.setDisplayName(ChatColor.GOLD + "Advanced Crafting Table");
            advancedWorkbench.addLoreLine(me.wolfyscript.utilities.util.chat.ChatColor.convert("&7Crafting Table for advanced recipes"));
            advancedWorkbench.addUnsafeEnchantment(Enchantment.DURABILITY, 5);
            advancedWorkbench.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            advancedWorkbench.getParticleContent().addParticleEffect(ParticleLocation.BLOCK, CustomCrafting.ADVANCED_CRAFTING_TABLE);
            customCrafting.saveItem(CustomCrafting.ADVANCED_CRAFTING_TABLE, advancedWorkbench);

            ShapedCraftRecipe workbenchCraft = new ShapedCraftRecipe();
            workbenchCraft.setMirrorHorizontal(false);
            workbenchCraft.setIngredients('B', new Ingredient(Material.GOLD_INGOT));
            workbenchCraft.setIngredients('E', new Ingredient(Material.CRAFTING_TABLE));
            workbenchCraft.setIngredients('H', new Ingredient(Material.GLOWSTONE_DUST));
            workbenchCraft.setResult(0, CustomItem.with(new WolfyUtilitiesRef(NamespacedKeyUtils.fromInternal(CustomCrafting.ADVANCED_CRAFTING_TABLE))));
            workbenchCraft.setNamespacedKey(CustomCrafting.ADVANCED_CRAFTING_TABLE);
            workbenchCraft.save();
        }

        //Loading RecipeBook
        File recipeBookFile = new File(customCrafting.getDataFolder(), "recipe_book.json");
        if (!recipeBookFile.exists()) {
            customCrafting.saveResource("recipe_book.json", false);
        }
        this.recipeBook = new RecipeBook(customCrafting);
    }

    public void loadLang() throws IOException {
        String chosenLang = mainConfig.getString("language");
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
