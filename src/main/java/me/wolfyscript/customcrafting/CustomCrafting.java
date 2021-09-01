package me.wolfyscript.customcrafting;

import me.wolfyscript.customcrafting.commands.CommandCC;
import me.wolfyscript.customcrafting.commands.CommandRecipe;
import me.wolfyscript.customcrafting.configs.custom_data.CauldronData;
import me.wolfyscript.customcrafting.configs.custom_data.EliteWorkbenchData;
import me.wolfyscript.customcrafting.configs.custom_data.RecipeBookData;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.CCPlayerData;
import me.wolfyscript.customcrafting.data.cauldron.Cauldrons;
import me.wolfyscript.customcrafting.data.patreon.Patreon;
import me.wolfyscript.customcrafting.data.patreon.Patron;
import me.wolfyscript.customcrafting.gui.elite_crafting.EliteCraftingCluster;
import me.wolfyscript.customcrafting.gui.item_creator.ClusterItemCreator;
import me.wolfyscript.customcrafting.gui.item_creator.tabs.*;
import me.wolfyscript.customcrafting.gui.main_gui.ClusterMain;
import me.wolfyscript.customcrafting.gui.particle_creator.ClusterParticleCreator;
import me.wolfyscript.customcrafting.gui.potion_creator.ClusterPotionCreator;
import me.wolfyscript.customcrafting.gui.recipe_creator.ClusterRecipeCreator;
import me.wolfyscript.customcrafting.gui.recipebook.ClusterRecipeBook;
import me.wolfyscript.customcrafting.gui.recipebook_editor.ClusterRecipeBookEditor;
import me.wolfyscript.customcrafting.handlers.ConfigHandler;
import me.wolfyscript.customcrafting.handlers.DataHandler;
import me.wolfyscript.customcrafting.handlers.DisableRecipesHandler;
import me.wolfyscript.customcrafting.listeners.*;
import me.wolfyscript.customcrafting.network.NetworkHandler;
import me.wolfyscript.customcrafting.placeholderapi.PlaceHolder;
import me.wolfyscript.customcrafting.recipes.conditions.*;
import me.wolfyscript.customcrafting.recipes.items.extension.*;
import me.wolfyscript.customcrafting.recipes.items.target.MergeAdapter;
import me.wolfyscript.customcrafting.recipes.items.target.adapters.DamageMergeAdapter;
import me.wolfyscript.customcrafting.recipes.items.target.adapters.EnchantMergeAdapter;
import me.wolfyscript.customcrafting.recipes.items.target.adapters.EnchantedBookMergeAdapter;
import me.wolfyscript.customcrafting.recipes.items.target.adapters.PlaceholderAPIMergeAdapter;
import me.wolfyscript.customcrafting.utils.ChatUtils;
import me.wolfyscript.customcrafting.utils.CraftManager;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.customcrafting.utils.UpdateChecker;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.chat.Chat;
import me.wolfyscript.utilities.api.inventory.gui.InventoryAPI;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.Reflection;
import me.wolfyscript.utilities.util.entity.CustomPlayerData;
import me.wolfyscript.utilities.util.json.jackson.KeyedTypeIdResolver;
import me.wolfyscript.utilities.util.version.WUVersion;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

public class CustomCrafting extends JavaPlugin {

    //Only used for displaying which version it is.
    private static final boolean PREMIUM = true;

    public static final NamespacedKey ADVANCED_CRAFTING_TABLE = new NamespacedKey(NamespacedKeyUtils.NAMESPACE, "advanced_crafting_table");
    public static final NamespacedKey INTERNAL_ADVANCED_CRAFTING_TABLE = NamespacedKeyUtils.fromInternal(ADVANCED_CRAFTING_TABLE);
    public static final NamespacedKey ELITE_CRAFTING_TABLE = new NamespacedKey(NamespacedKeyUtils.NAMESPACE, "elite_crafting_table");
    public static final NamespacedKey RECIPE_BOOK = new NamespacedKey(NamespacedKeyUtils.NAMESPACE, "recipe_book");
    public static final NamespacedKey CAULDRON = new NamespacedKey(NamespacedKeyUtils.NAMESPACE, "cauldron");
    //Used for backwards compatibility
    public static final NamespacedKey ADVANCED_WORKBENCH = new NamespacedKey(NamespacedKeyUtils.NAMESPACE, "workbench");

    private static final String CONSOLE_SEPARATOR = "------------------------------------------------------------------------";

    public static final int BUKKIT_VERSION = Bukkit.getUnsafe().getDataVersion();
    public static final int CONFIG_VERSION = 4;

    //Instance Object to use when no Object was passed!
    private static CustomCrafting instance;
    //Utils
    private final String currentVersion;
    private final WUVersion version;
    private final Patreon patreon;
    private final ChatUtils chatUtils;
    //The main WolfyUtilities instance
    private final WolfyUtilities api;
    private final Chat chat;
    private CraftManager craftManager;
    //File Handlers to load, save or edit data
    private ConfigHandler configHandler;
    private DataHandler dataHandler;
    private Cauldrons cauldrons = null;

    private final UpdateChecker updateChecker;
    private final NetworkHandler networkHandler;

    private DisableRecipesHandler disableRecipesHandler;

    private final boolean isPaper;

    public CustomCrafting() {
        super();
        instance = this;
        currentVersion = getDescription().getVersion();
        this.version = WUVersion.parse(currentVersion.split("-")[0]);
        isPaper = WolfyUtilities.hasClass("com.destroystokyo.paper.utils.PaperPluginLogger");
        api = WolfyUtilities.get(this, false);
        this.chat = api.getChat();
        this.chat.setInGamePrefix("§7[§3CC§7] ");
        api.setInventoryAPI(new InventoryAPI<>(api.getPlugin(), api, CCCache.class));
        this.chatUtils = new ChatUtils(this);
        this.patreon = new Patreon();
        this.updateChecker = new UpdateChecker(this, 55883);
        this.networkHandler = new NetworkHandler(this, api);
    }

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
        getLogger().info("WolfyUtilities API: " + Bukkit.getPluginManager().getPlugin("WolfyUtilities"));
        getLogger().info("Environment: " + WolfyUtilities.getENVIRONMENT());
        getLogger().info("Registering custom data");
        me.wolfyscript.utilities.util.Registry.CUSTOM_ITEM_DATA.register(new EliteWorkbenchData.Provider());
        me.wolfyscript.utilities.util.Registry.CUSTOM_ITEM_DATA.register(new RecipeBookData.Provider());
        me.wolfyscript.utilities.util.Registry.CUSTOM_ITEM_DATA.register(new CauldronData.Provider());

        getLogger().info("Registering Result Extensions");
        CCClassRegistry.RESULT_EXTENSIONS.register(new CommandResultExtension());
        CCClassRegistry.RESULT_EXTENSIONS.register(new MythicMobResultExtension());
        CCClassRegistry.RESULT_EXTENSIONS.register(new SoundResultExtension());
        CCClassRegistry.RESULT_EXTENSIONS.register(new ResultExtensionAdvancement());

        CustomPlayerData.register(new CCPlayerData.Provider());

        getLogger().info("Registering Result Merge Adapters");
        CCClassRegistry.RESULT_MERGE_ADAPTERS.register(new EnchantMergeAdapter());
        CCClassRegistry.RESULT_MERGE_ADAPTERS.register(new EnchantedBookMergeAdapter());
        CCClassRegistry.RESULT_MERGE_ADAPTERS.register(new DamageMergeAdapter());
        CCClassRegistry.RESULT_MERGE_ADAPTERS.register(new PlaceholderAPIMergeAdapter());

        getLogger().info("Registering Recipe Conditions");
        CCClassRegistry.RECIPE_CONDITIONS.register(AdvancedWorkbenchCondition.KEY, AdvancedWorkbenchCondition.class, new AdvancedWorkbenchCondition.GUIComponent());
        CCClassRegistry.RECIPE_CONDITIONS.register(CraftDelayCondition.KEY, CraftDelayCondition.class, new CraftDelayCondition.GUIComponent());
        CCClassRegistry.RECIPE_CONDITIONS.register(CraftLimitCondition.KEY, CraftLimitCondition.class, new CraftLimitCondition.GUIComponent());
        CCClassRegistry.RECIPE_CONDITIONS.register(EliteWorkbenchCondition.KEY, EliteWorkbenchCondition.class, new EliteWorkbenchCondition.GUIComponent());
        CCClassRegistry.RECIPE_CONDITIONS.register(ExperienceCondition.KEY, ExperienceCondition.class, new ExperienceCondition.GUIComponent());
        CCClassRegistry.RECIPE_CONDITIONS.register(PermissionCondition.KEY, PermissionCondition.class, new PermissionCondition.GUIComponent());
        CCClassRegistry.RECIPE_CONDITIONS.register(WeatherCondition.KEY, WeatherCondition.class, new WeatherCondition.GUIComponent());
        CCClassRegistry.RECIPE_CONDITIONS.register(WorldBiomeCondition.KEY, WorldBiomeCondition.class, new WorldBiomeCondition.GUIComponent());
        CCClassRegistry.RECIPE_CONDITIONS.register(WorldNameCondition.KEY, WorldNameCondition.class, new WorldNameCondition.GUIComponent());
        CCClassRegistry.RECIPE_CONDITIONS.register(WorldTimeCondition.KEY, WorldTimeCondition.class, new WorldTimeCondition.GUIComponent());
        CCClassRegistry.RECIPE_CONDITIONS.register(ConditionAdvancement.KEY, ConditionAdvancement.class, new ConditionAdvancement.GUIComponent());

        KeyedTypeIdResolver.registerTypeRegistry(ResultExtension.class, CCClassRegistry.RESULT_EXTENSIONS);
        KeyedTypeIdResolver.registerTypeRegistry(MergeAdapter.class, CCClassRegistry.RESULT_MERGE_ADAPTERS);
        KeyedTypeIdResolver.registerTypeRegistry((Class<Condition<?>>) (Object) Condition.class, CCClassRegistry.RECIPE_CONDITIONS);
    }

    @Override
    public void onEnable() {
        this.api.initialize();
        writeBanner();
        writePatreonCredits();
        writeSeparator();

        configHandler = new ConfigHandler(this);
        configHandler.loadRecipeBookConfig();
        configHandler.renameOldRecipesFolder();
        dataHandler = new DataHandler(this);
        configHandler.loadDefaults();
        craftManager = new CraftManager(this);
        disableRecipesHandler = new DisableRecipesHandler(this);

        writeSeparator();
        registerListeners();
        registerCommands();
        registerInventories();
        if (WolfyUtilities.isDevEnv()) {
            this.networkHandler.registerPackets();
        }

        cauldrons = new Cauldrons(this);
        if (WolfyUtilities.hasPlugin("PlaceholderAPI")) {
            api.getConsole().info("$msg.startup.placeholder$");
            new PlaceHolder(this).register();
        }
        //This makes sure that the customItems and recipes are loaded after ItemsAdder, so that all items are loaded correctly!
        if (!WolfyUtilities.hasPlugin("ItemsAdder")) {
            dataHandler.loadRecipesAndItems();
        }
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
        cauldrons.endAutoSaveTask();
        cauldrons.save();
    }

    private void writeBanner() {
        getLogger().info("____ _  _ ____ ___ ____ _  _ ____ ____ ____ ____ ___ _ _  _ ____ ");
        getLogger().info("|    |  | [__   |  |  | |\\/| |    |__/ |__| |___  |  | |\\ | | __ ");
        getLogger().info("|___ |__| ___]  |  |__| |  | |___ |  \\ |  | |     |  | | \\| |__]");
        getLogger().info(() -> "    v" + currentVersion + " " + (PREMIUM ? "Premium" : "Free"));
        getLogger().info(" ");
    }

    public void writeSeparator() {
        getLogger().info(CONSOLE_SEPARATOR);
    }

    private void writePatreonCredits() {
        patreon.initialize();
        getLogger().info("");
        getLogger().info("Special thanks to my Patrons for supporting this project: ");
        List<Patron> patronList = patreon.getPatronList();
        int lengthColumn = 20;
        int size = patronList.size();
        for (int i = 0; i <= size; i += 2) {
            if (i < size) {
                var sB = new StringBuilder();
                String name = patronList.get(i).getName();
                sB.append("| ").append(name);
                sB.append(" ".repeat(Math.max(0, lengthColumn - name.length())));
                if (i + 1 < patronList.size()) {
                    sB.append("| ").append(patronList.get(i + 1).getName());
                }
                getLogger().log(Level.INFO, "     {0}", sB);
            }
        }
    }

    private void registerListeners() {
        var pM = Bukkit.getPluginManager();
        pM.registerEvents(new PlayerListener(this), this);
        pM.registerEvents(new CraftListener(this), this);
        pM.registerEvents(new FurnaceListener(this), this);
        pM.registerEvents(new AnvilListener(this), this);
        pM.registerEvents(new CauldronListener(this), this);
        pM.registerEvents(new EliteWorkbenchListener(api), this);
        pM.registerEvents(new GrindStoneListener(this), this);
        pM.registerEvents(new BrewingStandListener(api, this), this);
        pM.registerEvents(new RecipeBookListener(), this);
        pM.registerEvents(new SmithingListener(this), this);

        if (WolfyUtilities.hasPlugin("ItemsAdder")) {
            getLogger().info("Detected ItemsAdder! CustomItems and Recipes will be loaded after ItemsAdder is successfully loaded!");
            pM.registerEvents(new ItemsAdderListener(this), this);
        }
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
        InventoryAPI<CCCache> invAPI = this.api.getInventoryAPI(CCCache.class);
        api.getConsole().info("$msg.startup.inventories$");
        CCRegistry.ITEM_CREATOR_TABS.register(new TabArmorSlots());
        CCRegistry.ITEM_CREATOR_TABS.register(new TabAttributes());
        CCRegistry.ITEM_CREATOR_TABS.register(new TabConsume());
        CCRegistry.ITEM_CREATOR_TABS.register(new TabCustomDurability());
        CCRegistry.ITEM_CREATOR_TABS.register(new TabCustomModelData());
        CCRegistry.ITEM_CREATOR_TABS.register(new TabDamage());
        CCRegistry.ITEM_CREATOR_TABS.register(new TabDisplayName());
        CCRegistry.ITEM_CREATOR_TABS.register(new TabEliteCraftingTable());
        CCRegistry.ITEM_CREATOR_TABS.register(new TabEnchants());
        CCRegistry.ITEM_CREATOR_TABS.register(new TabFlags());
        CCRegistry.ITEM_CREATOR_TABS.register(new TabFuel());
        CCRegistry.ITEM_CREATOR_TABS.register(new TabLocalizedName());
        CCRegistry.ITEM_CREATOR_TABS.register(new TabLore());
        CCRegistry.ITEM_CREATOR_TABS.register(new TabParticleEffects());
        CCRegistry.ITEM_CREATOR_TABS.register(new TabPermission());
        CCRegistry.ITEM_CREATOR_TABS.register(new TabPlayerHead());
        CCRegistry.ITEM_CREATOR_TABS.register(new TabPotion());
        CCRegistry.ITEM_CREATOR_TABS.register(new TabRarity());
        CCRegistry.ITEM_CREATOR_TABS.register(new TabRecipeBook());
        CCRegistry.ITEM_CREATOR_TABS.register(new TabRepairCost());
        CCRegistry.ITEM_CREATOR_TABS.register(new TabVanilla());
        CCRegistry.ITEM_CREATOR_TABS.register(new TabUnbreakable());

        invAPI.registerCluster(new ClusterMain(invAPI, this));
        invAPI.registerCluster(new ClusterRecipeCreator(invAPI, this));
        invAPI.registerCluster(new ClusterRecipeBook(invAPI, this));
        invAPI.registerCluster(new EliteCraftingCluster(invAPI, this));
        invAPI.registerCluster(new ClusterItemCreator(invAPI, this));
        invAPI.registerCluster(new ClusterParticleCreator(invAPI, this));
        invAPI.registerCluster(new ClusterPotionCreator(invAPI, this));
        invAPI.registerCluster(new ClusterRecipeBookEditor(invAPI, this));
    }

    public ConfigHandler getConfigHandler() {
        return configHandler;
    }

    public WolfyUtilities getApi() {
        return api;
    }

    public void onPlayerDisconnect(Player player) {
        this.networkHandler.disconnectPlayer(player);
    }

    public DataHandler getDataHandler() {
        return dataHandler;
    }

    public CraftManager getCraftManager() {
        return craftManager;
    }

    public ChatUtils getChatUtils() {
        return chatUtils;
    }

    public Cauldrons getCauldrons() {
        return cauldrons;
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

    public WUVersion getVersion() {
        return version;
    }

    public DisableRecipesHandler getDisableRecipesHandler() {
        return disableRecipesHandler;
    }
}
