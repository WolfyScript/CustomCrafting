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

package me.wolfyscript.customcrafting.handlers;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.MainConfig;
import me.wolfyscript.customcrafting.configs.custom_data.RecipeBookData;
import me.wolfyscript.customcrafting.configs.recipebook.RecipeBookConfig;
import me.wolfyscript.customcrafting.recipes.CraftingRecipeShaped;
import me.wolfyscript.customcrafting.recipes.CraftingRecipeShapeless;
import me.wolfyscript.customcrafting.recipes.items.Ingredient;
import me.wolfyscript.customcrafting.recipes.items.Result;
import me.wolfyscript.customcrafting.recipes.items.extension.*;
import me.wolfyscript.customcrafting.utils.ItemLoader;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.custom_items.ParticleContent;
import me.wolfyscript.utilities.api.inventory.custom_items.references.WolfyUtilitiesRef;
import me.wolfyscript.utilities.api.language.Language;
import me.wolfyscript.utilities.api.language.LanguageAPI;
import me.wolfyscript.utilities.util.particles.ParticleAnimation;
import me.wolfyscript.utilities.util.particles.ParticleEffect;
import me.wolfyscript.utilities.util.particles.animators.AnimatorBasic;
import me.wolfyscript.utilities.util.particles.timer.TimerLinear;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class ConfigHandler {

    private final CustomCrafting customCrafting;
    private final WolfyUtilities api;
    private final LanguageAPI languageAPI;
    private final MainConfig mainConfig;
    private RecipeBookConfig recipeBookConfig;

    public ConfigHandler(CustomCrafting customCrafting) {
        this.api = WolfyUtilities.get(customCrafting);
        this.customCrafting = customCrafting;
        var configAPI = api.getConfigAPI();
        this.languageAPI = api.getLanguageAPI();

        var oldConfigFile = new File(customCrafting.getDataFolder().getPath(), "main_config.yml");//Makes sure that if a config with the old name already exists, it's renamed to the new config name.
        if (oldConfigFile.exists() && !oldConfigFile.renameTo(new File(customCrafting.getDataFolder().getPath(), "config.yml"))) {
            customCrafting.getLogger().severe("Couldn't rename 'main_config.yml' to 'config.yml'!");
        }
        this.mainConfig = new MainConfig(configAPI, customCrafting);
        mainConfig.loadDefaults();
        configAPI.registerConfig(mainConfig);
        loadLang();
        api.getConfigAPI().setPrettyPrinting(mainConfig.isPrettyPrinting());
    }

    public void renameOldRecipesFolder() {
        if (!DataHandler.DATA_FOLDER.exists()) { //Check for the old recipes folder and rename it to the new data folder.
            var old = new File(customCrafting.getDataFolder() + File.separator + "recipes");
            if (!old.renameTo(DataHandler.DATA_FOLDER)) {
                customCrafting.getLogger().severe("Couldn't rename folder to the new required names!");
            }
        }
    }

    public void loadDefaults() {
        var enchantTableEffect = new ParticleEffect(Particle.ENCHANTMENT_TABLE, 2, new Vector(0,0,0), 0.75, null, new TimerLinear(1, 1), new AnimatorBasic());
        //Registry.PARTICLE_EFFECTS.register(CustomCrafting.ADVANCED_CRAFTING_TABLE, enchantTableEffect);

        var enchantAnimation = new ParticleAnimation(
                Material.ENCHANTING_TABLE,
                "Advanced Crafting Table",
                Arrays.asList("This is the default effect for the advanced crafting table", ""), 0, 5, -1,
                new ParticleAnimation.ParticleEffectSettings(enchantTableEffect, new Vector(0.5,1.25,0.5), 0)
        );
        //Registry.PARTICLE_ANIMATIONS.register(CustomCrafting.ADVANCED_CRAFTING_TABLE, enchantAnimation);

        if (mainConfig.resetRecipeBook()) {
            var knowledgeBook = new CustomItem(Material.KNOWLEDGE_BOOK);
            knowledgeBook.setDisplayName(me.wolfyscript.utilities.util.chat.ChatColor.convert("&6Recipe Book"));
            knowledgeBook.addLoreLine(me.wolfyscript.utilities.util.chat.ChatColor.convert("&7Contains some interesting recipes..."));
            knowledgeBook.addUnsafeEnchantment(Enchantment.DURABILITY, 5);
            knowledgeBook.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            ((RecipeBookData) knowledgeBook.getCustomData(CustomCrafting.RECIPE_BOOK)).setEnabled(true);
            ItemLoader.saveItem(CustomCrafting.RECIPE_BOOK, knowledgeBook);

            var knowledgeBookCraft = new CraftingRecipeShapeless(CustomCrafting.RECIPE_BOOK);
            knowledgeBookCraft.addIngredient(new Ingredient(Material.BOOK));
            knowledgeBookCraft.addIngredient(new Ingredient(Material.CRAFTING_TABLE));
            knowledgeBookCraft.getResult().put(0, CustomItem.with(new WolfyUtilitiesRef(NamespacedKeyUtils.fromInternal(CustomCrafting.RECIPE_BOOK))));
            knowledgeBookCraft.save();
        }

        if (mainConfig.resetAdvancedWorkbench()) {
            var advancedWorkbench = new CustomItem(Material.CRAFTING_TABLE);
            advancedWorkbench.setDisplayName(ChatColor.GOLD + "Advanced Crafting Table");
            advancedWorkbench.addLoreLine(me.wolfyscript.utilities.util.chat.ChatColor.convert("&7Crafting Table for advanced recipes"));
            advancedWorkbench.addUnsafeEnchantment(Enchantment.DURABILITY, 5);
            advancedWorkbench.addItemFlags(ItemFlag.HIDE_ENCHANTS);

            //advancedWorkbench.getParticleContent().setBlock(new ParticleContent.Settings(CustomCrafting.ADVANCED_CRAFTING_TABLE));
            advancedWorkbench.getParticleContent().setBlock(new ParticleContent.Settings(enchantAnimation));

            ItemLoader.saveItem(CustomCrafting.ADVANCED_CRAFTING_TABLE, advancedWorkbench);

            var workbenchCraft = new CraftingRecipeShaped(CustomCrafting.ADVANCED_CRAFTING_TABLE);
            workbenchCraft.setMirrorHorizontal(false);
            workbenchCraft.setShape("G", "C", "D");
            workbenchCraft.setIngredient('G', new Ingredient(Material.GOLD_INGOT));
            workbenchCraft.setIngredient('C', new Ingredient(Material.CRAFTING_TABLE));
            workbenchCraft.setIngredient('D', new Ingredient(Material.GLOWSTONE_DUST));
            Result result = workbenchCraft.getResult();
            result.put(0, CustomItem.with(new WolfyUtilitiesRef(CustomCrafting.INTERNAL_ADVANCED_CRAFTING_TABLE)));
            if (WolfyUtilities.isDevEnv()) {
                var commandExecution = new CommandResultExtension(Arrays.asList("say hi %player%", "effect give %player% minecraft:strength 100 100"), new ArrayList<>(), true, true);
                commandExecution.setExecutionType(ExecutionType.BULK);
                result.addExtension(commandExecution);
                result.addExtension(new SoundResultExtension(Sound.BLOCK_ANVIL_USE));
                result.addExtension(new MythicMobResultExtension("SkeletalKnight", 1));
                result.addExtension(new ResultExtensionAdvancement(NamespacedKey.minecraft("husbandry/tactical_fishing"), false, null, false, false));
            }
            workbenchCraft.save();
        }
    }

    public void loadRecipeBookConfig() {
        var oldRecipeBookFile = new File(customCrafting.getDataFolder(), "recipe_book_old.json");
        var recipeBookFile = new File(customCrafting.getDataFolder(), "recipe_book.json");
        if (!oldRecipeBookFile.exists() && recipeBookFile.exists() && !recipeBookFile.renameTo(oldRecipeBookFile)) {
            customCrafting.getLogger().severe("Couldn't backup old recipe_book.json! Trying to load and migrate old data!");
            customCrafting.getLogger().severe("If that fails, delete the recipe_book.json and restart the server!");
        }
        if (!recipeBookFile.exists()) {
            customCrafting.saveResource("recipe_book.json", false);
        }
        this.recipeBookConfig = new RecipeBookConfig(customCrafting);
        //Fix recipe book config if broken!
        if (recipeBookConfig.getCategories() == null) {
            customCrafting.saveResource("recipe_book.json", true);
            try {
                this.recipeBookConfig.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void loadLang() {
        var chosenLang = mainConfig.getString("language");
        customCrafting.saveResource("lang/en_US.json", true);
        customCrafting.saveResource("lang/de_DE.json", true);
        customCrafting.saveResource("lang/zh_CN.json", true);

        var fallBackLanguage = new Language(customCrafting, "en_US");
        languageAPI.registerLanguage(fallBackLanguage);
        customCrafting.getLogger().info(() -> "Loaded fallback language \"en_US\" v" + fallBackLanguage.getVersion() + " translated by " + String.join(", ", fallBackLanguage.getAuthors()));

        var file = new File(customCrafting.getDataFolder(), "lang/" + chosenLang + ".json");
        if (file.exists()) {
            var language = new Language(customCrafting, chosenLang);
            languageAPI.registerLanguage(language);
            languageAPI.setActiveLanguage(language);
            customCrafting.getLogger().info(() -> "Loaded active language \"" + chosenLang + "\" v" + language.getVersion() + " translated by " + String.join(", ", language.getAuthors()));
        }
    }

    public void save() throws IOException {
        recipeBookConfig.save(getConfig().isPrettyPrinting());
    }

    public MainConfig getConfig() {
        return mainConfig;
    }

    public RecipeBookConfig getRecipeBookConfig() {
        return recipeBookConfig;
    }
}
