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

package me.wolfyscript.customcrafting;

import com.wolfyscript.jackson.dataformat.hocon.HoconFactory;
import com.wolfyscript.jackson.dataformat.hocon.HoconGenerator;
import com.wolfyscript.jackson.dataformat.hocon.HoconMapper;
import java.io.IOException;
import me.wolfyscript.customcrafting.commands.CommandCC;
import me.wolfyscript.customcrafting.commands.CommandRecipe;
import me.wolfyscript.customcrafting.compatibility.PluginCompatibility;
import me.wolfyscript.customcrafting.configs.MainConfig;
import me.wolfyscript.customcrafting.configs.custom_data.CauldronData;
import me.wolfyscript.customcrafting.configs.custom_data.EliteWorkbenchData;
import me.wolfyscript.customcrafting.configs.custom_data.RecipeBookData;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.CCPlayerData;
import me.wolfyscript.customcrafting.data.patreon.Patreon;
import me.wolfyscript.customcrafting.data.persistent.CauldronBlockData;
import me.wolfyscript.customcrafting.gui.cauldron.CauldronWorkstationCluster;
import me.wolfyscript.customcrafting.gui.elite_crafting.EliteCraftingCluster;
import me.wolfyscript.customcrafting.gui.item_creator.ClusterItemCreator;
import me.wolfyscript.customcrafting.gui.item_creator.tabs.TabArmorSlots;
import me.wolfyscript.customcrafting.gui.item_creator.tabs.TabAttributes;
import me.wolfyscript.customcrafting.gui.item_creator.tabs.TabConsume;
import me.wolfyscript.customcrafting.gui.item_creator.tabs.TabCustomDurability;
import me.wolfyscript.customcrafting.gui.item_creator.tabs.TabCustomModelData;
import me.wolfyscript.customcrafting.gui.item_creator.tabs.TabDamage;
import me.wolfyscript.customcrafting.gui.item_creator.tabs.TabDisplayName;
import me.wolfyscript.customcrafting.gui.item_creator.tabs.TabEliteCraftingTable;
import me.wolfyscript.customcrafting.gui.item_creator.tabs.TabEnchants;
import me.wolfyscript.customcrafting.gui.item_creator.tabs.TabFlags;
import me.wolfyscript.customcrafting.gui.item_creator.tabs.TabFuel;
import me.wolfyscript.customcrafting.gui.item_creator.tabs.TabLocalizedName;
import me.wolfyscript.customcrafting.gui.item_creator.tabs.TabLore;
import me.wolfyscript.customcrafting.gui.item_creator.tabs.TabParticleEffects;
import me.wolfyscript.customcrafting.gui.item_creator.tabs.TabPermission;
import me.wolfyscript.customcrafting.gui.item_creator.tabs.TabPlayerHead;
import me.wolfyscript.customcrafting.gui.item_creator.tabs.TabPotion;
import me.wolfyscript.customcrafting.gui.item_creator.tabs.TabRarity;
import me.wolfyscript.customcrafting.gui.item_creator.tabs.TabRecipeBook;
import me.wolfyscript.customcrafting.gui.item_creator.tabs.TabRepairCost;
import me.wolfyscript.customcrafting.gui.item_creator.tabs.TabUnbreakable;
import me.wolfyscript.customcrafting.gui.item_creator.tabs.TabVanilla;
import me.wolfyscript.customcrafting.gui.main_gui.ClusterMain;
import me.wolfyscript.customcrafting.gui.potion_creator.ClusterPotionCreator;
import me.wolfyscript.customcrafting.gui.recipe_creator.ClusterRecipeCreator;
import me.wolfyscript.customcrafting.gui.recipebook.ClusterRecipeBook;
import me.wolfyscript.customcrafting.gui.recipebook.ClusterRecipeView;
import me.wolfyscript.customcrafting.gui.recipebook_editor.ClusterRecipeBookEditor;
import me.wolfyscript.customcrafting.handlers.ConfigHandler;
import me.wolfyscript.customcrafting.handlers.DataHandler;
import me.wolfyscript.customcrafting.handlers.DisableRecipesHandler;
import me.wolfyscript.customcrafting.listeners.AnvilListener;
import me.wolfyscript.customcrafting.listeners.BrewingStandListener;
import me.wolfyscript.customcrafting.listeners.CauldronListener;
import me.wolfyscript.customcrafting.listeners.EliteWorkbenchListener;
import me.wolfyscript.customcrafting.listeners.FurnaceListener;
import me.wolfyscript.customcrafting.listeners.GrindStoneListener;
import me.wolfyscript.customcrafting.listeners.PlayerListener;
import me.wolfyscript.customcrafting.listeners.RecipeBookListener;
import me.wolfyscript.customcrafting.listeners.SmithingListener;
import me.wolfyscript.customcrafting.listeners.crafting.CraftListener;
import me.wolfyscript.customcrafting.network.NetworkHandler;
import me.wolfyscript.customcrafting.recipes.RecipeType;
import me.wolfyscript.customcrafting.recipes.anvil.RepairTask;
import me.wolfyscript.customcrafting.recipes.anvil.RepairTaskDefault;
import me.wolfyscript.customcrafting.recipes.anvil.RepairTaskDurability;
import me.wolfyscript.customcrafting.recipes.anvil.RepairTaskResult;
import me.wolfyscript.customcrafting.recipes.conditions.AdvancedWorkbenchCondition;
import me.wolfyscript.customcrafting.recipes.conditions.Condition;
import me.wolfyscript.customcrafting.recipes.conditions.ConditionAdvancement;
import me.wolfyscript.customcrafting.recipes.conditions.ConditionCustomPlayerCheck;
import me.wolfyscript.customcrafting.recipes.conditions.ConditionScoreboard;
import me.wolfyscript.customcrafting.recipes.conditions.CraftDelayCondition;
import me.wolfyscript.customcrafting.recipes.conditions.CraftLimitCondition;
import me.wolfyscript.customcrafting.recipes.conditions.EliteWorkbenchCondition;
import me.wolfyscript.customcrafting.recipes.conditions.ExperienceCondition;
import me.wolfyscript.customcrafting.recipes.conditions.PermissionCondition;
import me.wolfyscript.customcrafting.recipes.conditions.WeatherCondition;
import me.wolfyscript.customcrafting.recipes.conditions.WorldBiomeCondition;
import me.wolfyscript.customcrafting.recipes.conditions.WorldNameCondition;
import me.wolfyscript.customcrafting.recipes.conditions.WorldTimeCondition;
import me.wolfyscript.customcrafting.recipes.items.extension.CommandResultExtension;
import me.wolfyscript.customcrafting.recipes.items.extension.MythicMobResultExtension;
import me.wolfyscript.customcrafting.recipes.items.extension.ResultExtension;
import me.wolfyscript.customcrafting.recipes.items.extension.ResultExtensionAdvancement;
import me.wolfyscript.customcrafting.recipes.items.extension.SoundResultExtension;
import me.wolfyscript.customcrafting.recipes.items.target.MergeAdapter;
import me.wolfyscript.customcrafting.recipes.items.target.adapters.DamageMergeAdapter;
import me.wolfyscript.customcrafting.recipes.items.target.adapters.DisplayNameMergeAdapter;
import me.wolfyscript.customcrafting.recipes.items.target.adapters.EnchantMergeAdapter;
import me.wolfyscript.customcrafting.recipes.items.target.adapters.EnchantedBookMergeAdapter;
import me.wolfyscript.customcrafting.recipes.items.target.adapters.FireworkRocketMergeAdapter;
import me.wolfyscript.customcrafting.recipes.items.target.adapters.NBTMergeAdapter;
import me.wolfyscript.customcrafting.recipes.items.target.adapters.PlaceholderAPIMergeAdapter;
import me.wolfyscript.customcrafting.registry.CCRegistries;
import me.wolfyscript.customcrafting.utils.ChatUtils;
import me.wolfyscript.customcrafting.utils.CraftManager;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.customcrafting.utils.UpdateChecker;
import me.wolfyscript.customcrafting.utils.cooking.CookingManager;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonIncludeProperties;
import me.wolfyscript.lib.com.fasterxml.jackson.databind.SerializationFeature;
import me.wolfyscript.lib.net.kyori.adventure.text.Component;
import me.wolfyscript.utilities.api.WolfyUtilCore;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.gui.InventoryAPI;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.Reflection;
import me.wolfyscript.utilities.util.entity.CustomPlayerData;
import me.wolfyscript.utilities.util.json.jackson.KeyedTypeIdResolver;
import me.wolfyscript.utilities.util.version.ServerVersion;
import me.wolfyscript.utilities.util.version.WUVersion;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

@JsonIncludeProperties(/* Do not include properties because it is injected and there is no need to serialize this class! */)
public class CustomCrafting extends JavaPlugin {

    private static final String CONSOLE_SEPARATOR = "------------------------------------------------------------------------";
    //CustomData keys
    public static final NamespacedKey ELITE_CRAFTING_TABLE_DATA = new NamespacedKey(NamespacedKeyUtils.NAMESPACE, "elite_crafting_table");
    public static final NamespacedKey RECIPE_BOOK_DATA = new NamespacedKey(NamespacedKeyUtils.NAMESPACE, "recipe_book");
    public static final NamespacedKey CAULDRON_DATA = new NamespacedKey(NamespacedKeyUtils.NAMESPACE, "cauldron");
    //Recipes & Items keys
    public static final NamespacedKey ADVANCED_CRAFTING_TABLE = new NamespacedKey(NamespacedKeyUtils.NAMESPACE, "customcrafting/advanced_crafting_table");
    public static final NamespacedKey RECIPE_BOOK = new NamespacedKey(NamespacedKeyUtils.NAMESPACE, "customcrafting/recipe_book");
    //Used for backwards compatibility
    public static final NamespacedKey ADVANCED_WORKBENCH = new NamespacedKey(NamespacedKeyUtils.NAMESPACE, "workbench");
    public static final int BUKKIT_VERSION = Bukkit.getUnsafe().getDataVersion();
    public static final int CONFIG_VERSION = 5;
    private final Component coloredTitle;

    //Instance Object to use when no Object was passed!
    private static CustomCrafting instance;

    //Utils
    private final String currentVersion;
    private final WUVersion version;
    private final Patreon patreon;
    private final ChatUtils chatUtils;
    //The main WolfyUtilities instance
    private final WolfyUtilities api;
    private final CCRegistries registries;
    //Recipe Managers / API
    private final CraftManager craftManager;
    private final CookingManager cookingManager;
    private DisableRecipesHandler disableRecipesHandler;
    //File Handlers to load, save or edit data
    private ConfigHandler configHandler;
    private DataHandler dataHandler;
    //Network
    private final UpdateChecker updateChecker;
    private final NetworkHandler networkHandler;
    //Compatibility
    private final PluginCompatibility pluginCompatibility;
    private final boolean isPaper;

    public CustomCrafting() {
        super();
        instance = this;
        currentVersion = getDescription().getVersion();
        this.version = WUVersion.parse(currentVersion.split("-")[0]);
        this.pluginCompatibility = new PluginCompatibility(this);
        isPaper = WolfyUtilities.hasClass("com.destroystokyo.paper.utils.PaperPluginLogger");
        if (!isPaper) {
            getLogger().warning("Paper not detected! Not using performance improvements.");
        }
        api = WolfyUtilCore.getInstance().getAPI(this, false);

        HoconMapper mapper = new HoconMapper(new HoconFactory()
                .disable(HoconGenerator.Feature.ROOT_OBJECT_BRACKETS)
                .disable(HoconGenerator.Feature.ALWAYS_QUOTE_STRINGS)
                .disable(HoconGenerator.Feature.OBJECT_VALUE_SEPARATOR));
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        api.getJacksonMapperUtil().setGlobalMapper(api.getCore().applyWolfyUtilsJsonMapperModules(mapper));

        this.registries = new CCRegistries(this, api.getCore());

        var chat = api.getChat();
        chat.setChatPrefix(chat.getMiniMessage().deserialize("<gray>[<gradient:dark_aqua:aqua>CC</gradient>]</gray>"));
        this.coloredTitle = chat.getMiniMessage().deserialize("<gradient:dark_aqua:aqua><b>CustomCrafting</b></gradient>");
        api.setInventoryAPI(new InventoryAPI<>(api.getPlugin(), api, CCCache.class));
        this.chatUtils = new ChatUtils(this);
        this.patreon = new Patreon(this);
        this.updateChecker = new UpdateChecker(this, 55883);
        this.networkHandler = new NetworkHandler(this, api);

        this.craftManager = new CraftManager(this);
        this.cookingManager = new CookingManager(this);
    }

    /**
     * Gets the instance of the CustomCrafting plugin.<br>
     *
     * @return The instance of this plugin.
     */
    public static CustomCrafting inst() {
        return instance;
    }

    /**
     * @deprecated Replaced by {@link #inst()}
     */
    @Deprecated
    public static CustomCrafting getInst() {
        return inst();
    }

    @Override
    public void onLoad() {
        getLogger().info("WolfyUtils API: v" + ServerVersion.getWUVersion().getVersion());
        getLogger().info("CustomCrafting: v" + getVersion().getVersion());
        getLogger().info("Environment   : " + WolfyUtilities.getENVIRONMENT());

        api.getCore().applyWolfyUtilsJsonMapperModules(api.getJacksonMapperUtil().getGlobalMapper());

        ConfigurationSerialization.registerClass(MainConfig.DatabaseSettings.class);
        ConfigurationSerialization.registerClass(MainConfig.LocalStorageSettings.class);

        getLogger().info("Registering CustomItem Data");
        var customItemData = api.getRegistries().getCustomItemData();
        customItemData.register(new EliteWorkbenchData.Provider());
        customItemData.register(new RecipeBookData.Provider());
        customItemData.register(new CauldronData.Provider());

        getLogger().info("Registering Custom Block Data");
        var customBlockData = api.getRegistries().getCustomBlockData();
        customBlockData.register(CauldronBlockData.ID, CauldronBlockData.class);

        getLogger().info("Registering Result Extensions");
        var resultExtensions = getRegistries().getRecipeResultExtensions();
        resultExtensions.register(new CommandResultExtension(this));
        resultExtensions.register(new MythicMobResultExtension());
        resultExtensions.register(new SoundResultExtension());
        resultExtensions.register(new ResultExtensionAdvancement());

        CustomPlayerData.register(new CCPlayerData.Provider());

        getLogger().info("Registering Result Merge Adapters");
        var resultMergeAdapters = getRegistries().getRecipeMergeAdapters();
        resultMergeAdapters.register(new EnchantMergeAdapter());
        resultMergeAdapters.register(new EnchantedBookMergeAdapter());
        resultMergeAdapters.register(new DisplayNameMergeAdapter());
        resultMergeAdapters.register(new DamageMergeAdapter());
        resultMergeAdapters.register(new PlaceholderAPIMergeAdapter());
        resultMergeAdapters.register(new FireworkRocketMergeAdapter());
        if (ServerVersion.getWUVersion().isAfterOrEq(WUVersion.of(4, 16, 4, 0))) {
            resultMergeAdapters.register(new NBTMergeAdapter());
        }

        getLogger().info("Registering Recipe Conditions");
        var recipeConditions = getRegistries().getRecipeConditions();
        recipeConditions.register(AdvancedWorkbenchCondition.KEY, AdvancedWorkbenchCondition.class, new AdvancedWorkbenchCondition.GUIComponent());
        recipeConditions.register(CraftDelayCondition.KEY, CraftDelayCondition.class, new CraftDelayCondition.GUIComponent());
        recipeConditions.register(CraftLimitCondition.KEY, CraftLimitCondition.class, new CraftLimitCondition.GUIComponent());
        recipeConditions.register(EliteWorkbenchCondition.KEY, EliteWorkbenchCondition.class, new EliteWorkbenchCondition.GUIComponent());
        recipeConditions.register(ExperienceCondition.KEY, ExperienceCondition.class, new ExperienceCondition.GUIComponent());
        recipeConditions.register(PermissionCondition.KEY, PermissionCondition.class, new PermissionCondition.GUIComponent());
        recipeConditions.register(WeatherCondition.KEY, WeatherCondition.class, new WeatherCondition.GUIComponent());
        recipeConditions.register(WorldBiomeCondition.KEY, WorldBiomeCondition.class, new WorldBiomeCondition.GUIComponent());
        recipeConditions.register(WorldNameCondition.KEY, WorldNameCondition.class, new WorldNameCondition.GUIComponent());
        recipeConditions.register(WorldTimeCondition.KEY, WorldTimeCondition.class, new WorldTimeCondition.GUIComponent());
        recipeConditions.register(ConditionAdvancement.KEY, ConditionAdvancement.class, new ConditionAdvancement.GUIComponent());
        recipeConditions.register(ConditionCustomPlayerCheck.KEY, ConditionCustomPlayerCheck.class, new ConditionCustomPlayerCheck.GUIComponent());

        if (ServerVersion.getWUVersion().isAfterOrEq(WUVersion.of(3, 16, 3, 0))) {
            //Only register it when the features are available
            recipeConditions.register(ConditionScoreboard.KEY, ConditionScoreboard.class, new ConditionScoreboard.GUIComponent());
        }

        getLogger().info("Registering Recipe Types");
        var recipeTypes = getRegistries().getRecipeTypes();
        recipeTypes.register(RecipeType.CRAFTING_SHAPED);
        recipeTypes.register(RecipeType.CRAFTING_SHAPELESS);
        recipeTypes.register(RecipeType.ELITE_CRAFTING_SHAPED);
        recipeTypes.register(RecipeType.ELITE_CRAFTING_SHAPELESS);
        recipeTypes.register(RecipeType.FURNACE);
        recipeTypes.register(RecipeType.BLAST_FURNACE);
        recipeTypes.register(RecipeType.SMOKER);
        recipeTypes.register(RecipeType.CAMPFIRE);
        recipeTypes.register(RecipeType.ANVIL);
        recipeTypes.register(RecipeType.STONECUTTER);
        recipeTypes.register(RecipeType.CAULDRON);
        recipeTypes.register(RecipeType.GRINDSTONE);
        recipeTypes.register(RecipeType.BREWING_STAND);
        recipeTypes.register(RecipeType.SMITHING);

        getLogger().info("Registering Anvil Recipe Tasks");
        var anvilRecipeRepairTasks = getRegistries().getAnvilRecipeRepairTasks();
        anvilRecipeRepairTasks.register(RepairTaskDefault.KEY, RepairTaskDefault.class);
        anvilRecipeRepairTasks.register(RepairTaskResult.KEY, RepairTaskResult.class);
        anvilRecipeRepairTasks.register(RepairTaskDurability.KEY, RepairTaskDurability.class);

        getLogger().info("Registering Type Registries");
        KeyedTypeIdResolver.registerTypeRegistry(ResultExtension.class, resultExtensions);
        KeyedTypeIdResolver.registerTypeRegistry(MergeAdapter.class, resultMergeAdapters);
        KeyedTypeIdResolver.registerTypeRegistry((Class<Condition<?>>) (Object) Condition.class, recipeConditions);
        KeyedTypeIdResolver.registerTypeRegistry(RepairTask.class, anvilRecipeRepairTasks);
    }

    @Override
    public void onEnable() {
        this.api.initialize();
        writeBanner();
        this.patreon.initialize();
        this.patreon.printPatreonCredits();
        writeSeparator();

        this.configHandler = new ConfigHandler(this);
        this.configHandler.load();
        this.pluginCompatibility.init();
        this.dataHandler = new DataHandler(this);
        this.disableRecipesHandler = new DisableRecipesHandler(this);

        registerListeners();
        registerCommands();
        registerInventories();
        //Used for testing purposes, might be available for production in the future
        if (WolfyUtilities.isDevEnv()) {
            this.networkHandler.registerPackets();
        }
        if (api.getCore().getCompatibilityManager().getPlugins().isDoneLoading()) {
            dataHandler.loadRecipesAndItems();
        }
        //All data is loaded. Now test for updates.
        updateChecker.run(null);
        //Load Metrics
        var metrics = new Metrics(this, 3211);
        metrics.addCustomChart(new SimplePie("used_language", () -> getConfigHandler().getConfig().getString("language")));
        metrics.addCustomChart(new SimplePie("advanced_workbench", () -> configHandler.getConfig().isAdvancedWorkbenchEnabled() ? "enabled" : "disabled"));
        writeSeparator();
    }

    @Override
    public void onDisable() {
        try {
            configHandler.save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeBanner() {
        getLogger().info("____ _  _ ____ ___ ____ _  _ ____ ____ ____ ____ ___ _ _  _ ____ ");
        getLogger().info("|    |  | [__   |  |  | |\\/| |    |__/ |__| |___  |  | |\\ | | __ ");
        getLogger().info("|___ |__| ___]  |  |__| |  | |___ |  \\ |  | |     |  | | \\| |__]");
        getLogger().info(() -> "    Version    | v" + version.getVersion());
        getLogger().info(() -> "    WolfyUtils | v" + ServerVersion.getWUVersion().getVersion());
        getLogger().info(() -> "    Bukkit     | " + Bukkit.getVersion() + "(API: " + Bukkit.getBukkitVersion() + ")");
    }

    public void writeSeparator() {
        getLogger().info(CONSOLE_SEPARATOR);
    }

    private void registerListeners() {
        var pM = Bukkit.getPluginManager();
        pM.registerEvents(new PlayerListener(this), this);
        pM.registerEvents(new CraftListener(this), this);
        pM.registerEvents(new FurnaceListener(this, cookingManager), this);
        pM.registerEvents(new AnvilListener(this), this);
        pM.registerEvents(new CauldronListener(this), this);
        pM.registerEvents(new EliteWorkbenchListener(api), this);
        pM.registerEvents(new GrindStoneListener(this), this);
        pM.registerEvents(new BrewingStandListener(api, this), this);
        pM.registerEvents(new RecipeBookListener(), this);
        pM.registerEvents(new SmithingListener(this), this);
    }

    private void registerCommands() {
        final var serverCommandMap = Reflection.getDeclaredField(Bukkit.getServer().getClass(), "commandMap");
        serverCommandMap.setAccessible(true);
        try {
            var commandMap = (CommandMap) serverCommandMap.get(Bukkit.getServer());
            commandMap.register("customcrafting", new CommandCC(this));
            commandMap.register("recipes", "customcrafting", new CommandRecipe(this));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void registerInventories() {
        api.getConsole().info("$msg.startup.inventories$");
        InventoryAPI<CCCache> invAPI = this.api.getInventoryAPI(CCCache.class);
        getLogger().info("Register ItemCreator Tabs");
        var registry = getRegistries().getItemCreatorTabs();
        //Register tabs for the item creator
        registry.register(new TabArmorSlots());
        registry.register(new TabAttributes());
        registry.register(new TabConsume());
        registry.register(new TabCustomDurability());
        registry.register(new TabCustomModelData());
        registry.register(new TabDamage());
        registry.register(new TabDisplayName());
        registry.register(new TabEliteCraftingTable());
        registry.register(new TabEnchants());
        registry.register(new TabFlags());
        registry.register(new TabFuel());
        registry.register(new TabLocalizedName());
        registry.register(new TabLore());
        registry.register(new TabParticleEffects());
        registry.register(new TabPermission());
        registry.register(new TabPlayerHead());
        registry.register(new TabPotion());
        registry.register(new TabRarity());
        registry.register(new TabRecipeBook());
        registry.register(new TabRepairCost());
        registry.register(new TabVanilla());
        registry.register(new TabUnbreakable());
        //Register the GUIs
        invAPI.registerCluster(new ClusterMain(invAPI, this));
        invAPI.registerCluster(new ClusterRecipeCreator(invAPI, this));
        invAPI.registerCluster(new ClusterRecipeBook(invAPI, this));
        invAPI.registerCluster(new ClusterRecipeView(invAPI, this));
        invAPI.registerCluster(new EliteCraftingCluster(invAPI, this));
        invAPI.registerCluster(new ClusterItemCreator(invAPI, this));
        invAPI.registerCluster(new ClusterPotionCreator(invAPI, this));
        invAPI.registerCluster(new ClusterRecipeBookEditor(invAPI, this));
        invAPI.registerCluster(new CauldronWorkstationCluster(invAPI, this));
    }

    public ConfigHandler getConfigHandler() {
        return configHandler;
    }

    /**
     * Gets the formatted name of this plugin.
     *
     * @return The Component of the formatted plugin name.
     */
    public Component getColoredTitle() {
        return coloredTitle;
    }

    /**
     * Gets the WolfyUtilities API instance that is bound to this plugin.
     *
     * @return The WolfyUtilities instance of this plugin.
     */
    public WolfyUtilities getApi() {
        return api;
    }

    public void onPlayerDisconnect(Player player) {
        this.networkHandler.disconnectPlayer(player);
    }

    /**
     * Gets the DataHandler that loads and saves recipes and items, that are saved in the data directory.
     *
     * @return The DataHandler instance
     */
    public DataHandler getDataHandler() {
        return dataHandler;
    }

    /**
     * Gets the CraftManager that manages crafting and caches required data.<br>
     * This can be used to identify if a player has an active custom recipe for example.
     *
     * @return The CraftManager instance
     */
    public CraftManager getCraftManager() {
        return craftManager;
    }

    public CookingManager getCookingManager() {
        return cookingManager;
    }

    public ChatUtils getChatUtils() {
        return chatUtils;
    }

    public Patreon getPatreon() {
        return patreon;
    }

    public boolean isPaper() {
        return isPaper;
    }

    public UpdateChecker getUpdateChecker() {
        return updateChecker;
    }

    /**
     * Gets the version of CustomCrafting parsed to the {@link WUVersion} object.
     *
     * @return The version of CustomCrafting
     */
    public WUVersion getVersion() {
        return version;
    }

    /**
     * Gets the DisableRecipesHandler that handles toggling of vanilla and custom recipes.
     *
     * @return The DisableRecipeHandler instance
     */
    public DisableRecipesHandler getDisableRecipesHandler() {
        return disableRecipesHandler;
    }

    /**
     * Gets the Registries of CustomCrafting.<br>
     * Registries allow you to add custom content to CustomCrafting, from recipes to JSON configurable objects.
     *
     * @return The Registries of CustomCrafting
     */
    public CCRegistries getRegistries() {
        return registries;
    }
}
