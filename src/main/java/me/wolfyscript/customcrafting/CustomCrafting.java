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
import me.wolfyscript.customcrafting.gui.*;
import me.wolfyscript.customcrafting.gui.item_creator.tabs.*;
import me.wolfyscript.customcrafting.handlers.ConfigHandler;
import me.wolfyscript.customcrafting.handlers.DataBaseHandler;
import me.wolfyscript.customcrafting.handlers.DataHandler;
import me.wolfyscript.customcrafting.listeners.*;
import me.wolfyscript.customcrafting.network.NetworkHandler;
import me.wolfyscript.customcrafting.placeholderapi.PlaceHolder;
import me.wolfyscript.customcrafting.recipes.conditions.*;
import me.wolfyscript.customcrafting.utils.*;
import me.wolfyscript.customcrafting.utils.recipe_item.extension.CommandResultExtension;
import me.wolfyscript.customcrafting.utils.recipe_item.extension.MythicMobResultExtension;
import me.wolfyscript.customcrafting.utils.recipe_item.extension.ResultExtension;
import me.wolfyscript.customcrafting.utils.recipe_item.extension.SoundResultExtension;
import me.wolfyscript.customcrafting.utils.recipe_item.target.MergeAdapter;
import me.wolfyscript.customcrafting.utils.recipe_item.target.adapters.DamageMergeAdapter;
import me.wolfyscript.customcrafting.utils.recipe_item.target.adapters.EnchantMergeAdapter;
import me.wolfyscript.customcrafting.utils.recipe_item.target.adapters.EnchantedBookMergeAdapter;
import me.wolfyscript.customcrafting.utils.recipe_item.target.adapters.PlaceholderAPIMergeAdapter;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.chat.Chat;
import me.wolfyscript.utilities.api.inventory.gui.InventoryAPI;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.core.type.TypeReference;
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
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;

public class CustomCrafting extends JavaPlugin {

    //Only used for displaying which version it is.
    private static final boolean PREMIUM = true;
    private static final String ENVIRONMENT = System.getProperties().getProperty("com.wolfyscript.env", "PROD");

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
    private DataBaseHandler dataBaseHandler = null;
    private Cauldrons cauldrons = null;

    private final UpdateChecker updateChecker;
    private final NetworkHandler networkHandler;

    private final boolean isPaper;

    public CustomCrafting() {
        super();
        instance = this;
        currentVersion = instance.getDescription().getVersion();
        this.version = WUVersion.parse(getDescription().getVersion());
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

    public static boolean isDevEnv() {
        return ENVIRONMENT.equalsIgnoreCase("DEV");
    }

    @Override
    public void onLoad() {
        getLogger().info("WolfyUtilities API: " + Bukkit.getPluginManager().getPlugin("WolfyUtilities"));
        getLogger().info("Environment: " + ENVIRONMENT);
        getLogger().info("Registering custom data");
        me.wolfyscript.utilities.util.Registry.CUSTOM_ITEM_DATA.register(new EliteWorkbenchData.Provider());
        me.wolfyscript.utilities.util.Registry.CUSTOM_ITEM_DATA.register(new RecipeBookData.Provider());
        me.wolfyscript.utilities.util.Registry.CUSTOM_ITEM_DATA.register(new CauldronData.Provider());

        getLogger().info("Registering Result Extensions");
        Registry.RESULT_EXTENSIONS.register(new CommandResultExtension());
        Registry.RESULT_EXTENSIONS.register(new MythicMobResultExtension());
        Registry.RESULT_EXTENSIONS.register(new SoundResultExtension());
        CustomPlayerData.register(new CCPlayerData.Provider());

        getLogger().info("Registering Result Merge Adapters");
        Registry.RESULT_MERGE_ADAPTERS.register(new EnchantMergeAdapter());
        Registry.RESULT_MERGE_ADAPTERS.register(new EnchantedBookMergeAdapter());
        Registry.RESULT_MERGE_ADAPTERS.register(new DamageMergeAdapter());
        Registry.RESULT_MERGE_ADAPTERS.register(new PlaceholderAPIMergeAdapter());

        getLogger().info("Registering Recipe Conditions");
        Condition.register(new AdvancedWorkbenchCondition(), new AdvancedWorkbenchCondition.GUIComponent());
        Condition.register(new CraftDelayCondition(), new CraftDelayCondition.GUIComponent());
        Condition.register(new CraftLimitCondition(), new CraftLimitCondition.GUIComponent());
        Condition.register(new EliteWorkbenchCondition(), new EliteWorkbenchCondition.GUIComponent());
        Condition.register(new ExperienceCondition(), new ExperienceCondition.GUIComponent());
        Condition.register(new PermissionCondition(), new PermissionCondition.GUIComponent());
        Condition.register(new WeatherCondition(), new WeatherCondition.GUIComponent());
        Condition.register(new WorldBiomeCondition(), new WorldBiomeCondition.GUIComponent());
        Condition.register(new WorldNameCondition(), new WorldNameCondition.GUIComponent());
        Condition.register(new WorldTimeCondition(), new WorldTimeCondition.GUIComponent());

        KeyedTypeIdResolver.registerTypeRegistry(ResultExtension.class, Registry.RESULT_EXTENSIONS);
        KeyedTypeIdResolver.registerTypeRegistry(MergeAdapter.class, Registry.RESULT_MERGE_ADAPTERS);
        KeyedTypeIdResolver.registerTypeRegistry((Class<Condition<?>>) new TypeReference<Condition<?>>() {
        }.getType(), Registry.RECIPE_CONDITIONS);
    }

    @Override
    public void onEnable() {
        this.api.initialize();
        writeBanner();
        writePatreonCredits();
        writeSeparator();

        configHandler = new ConfigHandler(this);
        if (configHandler.getConfig().isDatabaseEnabled()) {
            try {
                dataBaseHandler = new DataBaseHandler(api, configHandler.getConfig(), this);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        configHandler.loadDefaults();
        dataHandler = new DataHandler(this);
        craftManager = new CraftManager(this);

        writeSeparator();
        registerListeners();
        registerCommands();
        registerInventories();
        if (isDevEnv()) {
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
        Registry.ITEM_CREATOR_TABS.register(new TabArmorSlots());
        Registry.ITEM_CREATOR_TABS.register(new TabAttributes());
        Registry.ITEM_CREATOR_TABS.register(new TabConsume());
        Registry.ITEM_CREATOR_TABS.register(new TabCustomDurability());
        Registry.ITEM_CREATOR_TABS.register(new TabCustomModelData());
        Registry.ITEM_CREATOR_TABS.register(new TabDamage());
        Registry.ITEM_CREATOR_TABS.register(new TabDisplayName());
        Registry.ITEM_CREATOR_TABS.register(new TabEliteCraftingTable());
        Registry.ITEM_CREATOR_TABS.register(new TabEnchants());
        Registry.ITEM_CREATOR_TABS.register(new TabFlags());
        Registry.ITEM_CREATOR_TABS.register(new TabFuel());
        Registry.ITEM_CREATOR_TABS.register(new TabLocalizedName());
        Registry.ITEM_CREATOR_TABS.register(new TabLore());
        Registry.ITEM_CREATOR_TABS.register(new TabParticleEffects());
        Registry.ITEM_CREATOR_TABS.register(new TabPermission());
        Registry.ITEM_CREATOR_TABS.register(new TabPlayerHead());
        Registry.ITEM_CREATOR_TABS.register(new TabPotion());
        Registry.ITEM_CREATOR_TABS.register(new TabRarity());
        Registry.ITEM_CREATOR_TABS.register(new TabRecipeBook());
        Registry.ITEM_CREATOR_TABS.register(new TabRepairCost());
        Registry.ITEM_CREATOR_TABS.register(new TabVanilla());
        Registry.ITEM_CREATOR_TABS.register(new TabUnbreakable());

        invAPI.registerCluster(new MainCluster(invAPI, this));
        invAPI.registerCluster(new RecipeCreatorCluster(invAPI, this));
        invAPI.registerCluster(new RecipeBookCluster(invAPI, this));
        invAPI.registerCluster(new EliteCraftingCluster(invAPI, this));
        invAPI.registerCluster(new ItemCreatorCluster(invAPI, this));
        invAPI.registerCluster(new ParticleCreatorCluster(invAPI, this));
        invAPI.registerCluster(new PotionCreatorCluster(invAPI, this));
        invAPI.registerCluster(new RecipeBookEditorCluster(invAPI, this));

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

    /**
     * @deprecated Replaced with {@link #getDataHandler()}
     */
    @Deprecated
    public DataHandler getRecipeHandler() {
        return getDataHandler();
    }

    public boolean hasDataBaseHandler() {
        return dataBaseHandler != null;
    }

    public DataBaseHandler getDataBaseHandler() {
        return dataBaseHandler;
    }

    public CraftManager getCraftManager() {
        return craftManager;
    }

    /**
     * @deprecated Replaced with {@link #getCraftManager()}
     */
    @Deprecated
    public RecipeUtils getRecipeUtils() {
        return craftManager.getRecipeUtils();
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

    @Deprecated
    public boolean isOutdated() {
        return getUpdateChecker().isOutdated();
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
}
