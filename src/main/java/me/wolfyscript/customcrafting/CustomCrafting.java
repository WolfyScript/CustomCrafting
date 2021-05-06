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
import me.wolfyscript.customcrafting.handlers.ConfigHandler;
import me.wolfyscript.customcrafting.handlers.DataBaseHandler;
import me.wolfyscript.customcrafting.handlers.DataHandler;
import me.wolfyscript.customcrafting.listeners.*;
import me.wolfyscript.customcrafting.placeholderapi.PlaceHolder;
import me.wolfyscript.customcrafting.utils.ChatUtils;
import me.wolfyscript.customcrafting.utils.CraftManager;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.customcrafting.utils.recipe_item.extension.CommandResultExtension;
import me.wolfyscript.customcrafting.utils.recipe_item.extension.MythicMobResultExtension;
import me.wolfyscript.customcrafting.utils.recipe_item.extension.SoundResultExtension;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.chat.Chat;
import me.wolfyscript.utilities.api.chat.ClickData;
import me.wolfyscript.utilities.api.inventory.gui.InventoryAPI;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.Reflection;
import me.wolfyscript.utilities.util.entity.CustomPlayerData;
import me.wolfyscript.utilities.util.version.MinecraftVersions;
import me.wolfyscript.utilities.util.version.ServerVersion;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;

public class CustomCrafting extends JavaPlugin {

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

    private boolean outdated = false;

    public CustomCrafting() {
        super();
        instance = this;
        currentVersion = instance.getDescription().getVersion();

        api = WolfyUtilities.get(this, false);
        this.chat = api.getChat();
        this.chat.setInGamePrefix("§7[§3CC§7] ");
        api.setInventoryAPI(new InventoryAPI<>(api.getPlugin(), api, CCCache.class));

        this.chatUtils = new ChatUtils(this);
        this.patreon = new Patreon(this);
    }

    public static CustomCrafting inst() {
        return instance;
    }

    @Override
    public void onLoad() {
        getLogger().info("WolfyUtilities API: " + Bukkit.getPluginManager().getPlugin("WolfyUtilities"));

        getLogger().info("Registering custom data");
        me.wolfyscript.utilities.util.Registry.CUSTOM_ITEM_DATA.register(new EliteWorkbenchData.Provider());
        me.wolfyscript.utilities.util.Registry.CUSTOM_ITEM_DATA.register(new RecipeBookData.Provider());
        me.wolfyscript.utilities.util.Registry.CUSTOM_ITEM_DATA.register(new CauldronData.Provider());

        getLogger().info("Registering Result Extensions");
        Registry.RESULT_EXTENSIONS.register(new CommandResultExtension());
        Registry.RESULT_EXTENSIONS.register(new MythicMobResultExtension());
        Registry.RESULT_EXTENSIONS.register(new SoundResultExtension());
        CustomPlayerData.register(new CCPlayerData.Provider());
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
        getDataHandler().onSave();
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

        cauldrons = new Cauldrons(this);
        if (WolfyUtilities.hasPlugin("PlaceholderAPI")) {
            api.getConsole().info("$msg.startup.placeholder$");
            new PlaceHolder(this).register();
        }
        //This makes sure that the customItems and recipes are loaded after ItemsAdder, so that all items are loaded correctly!
        if (!WolfyUtilities.hasPlugin("ItemsAdder")) {
            dataHandler.loadRecipesAndItems();
        }
        //Don't check for updates when it's a Premium+ version, because there isn't a way to do so yet!
        if (!patreon.isPatreon()) {
            checkUpdate(null);
        }
        //Load Metrics
        Metrics metrics = new Metrics(this, 3211);
        metrics.addCustomChart(new Metrics.SimplePie("used_language", () -> getConfigHandler().getConfig().getString("language")));
        metrics.addCustomChart(new Metrics.SimplePie("advanced_workbench", () -> configHandler.getConfig().isAdvancedWorkbenchEnabled() ? "enabled" : "disabled"));
        writeSeparator();
    }

    private void writeBanner() {
        getLogger().info("____ _  _ ____ ___ ____ _  _ ____ ____ ____ ____ ___ _ _  _ ____ ");
        getLogger().info("|    |  | [__   |  |  | |\\/| |    |__/ |__| |___  |  | |\\ | | __ ");
        getLogger().info("|___ |__| ___]  |  |__| |  | |___ |  \\ |  | |     |  | | \\| |__]");
        getLogger().info(() -> "    v" + currentVersion + " " + (patreon.isPatreon() ? "Patreon" : "Free"));
        getLogger().info(" ");
    }

    public void writeSeparator() {
        getLogger().info(CONSOLE_SEPARATOR);
    }

    private void writePatreonCredits() {
        if (patreon.isPatreon()) {
            getLogger().info("Thanks for actively supporting this plugin on Patreon!");
        }
        patreon.initialize();
        getLogger().info("");
        getLogger().info("Special thanks to my Patrons for supporting this project: ");
        List<Patron> patronList = patreon.getPatronList();
        int lengthColumn = 20;
        int size = patronList.size();
        for (int i = 0; i <= size; i += 2) {
            if (i < size) {
                StringBuilder sB = new StringBuilder();
                String name = patronList.get(i).getName();
                sB.append("| ").append(name);
                for (int j = 0; j < lengthColumn - name.length(); j++) {
                    sB.append(" ");
                }
                if (i + 1 < patronList.size()) {
                    sB.append("| ").append(patronList.get(i + 1).getName());
                }
                getLogger().log(Level.INFO, "     {0}", sB);
            }
        }
    }

    private void registerListeners() {
        PluginManager pM = Bukkit.getPluginManager();
        pM.registerEvents(new PlayerListener(this), this);
        pM.registerEvents(new CraftListener(this), this);
        pM.registerEvents(new FurnaceListener(this), this);
        pM.registerEvents(new AnvilListener(this), this);
        pM.registerEvents(new CauldronListener(this), this);
        pM.registerEvents(new EliteWorkbenchListener(api), this);
        pM.registerEvents(new GrindStoneListener(this), this);
        pM.registerEvents(new BrewingStandListener(api, this), this);
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_16)) {
            pM.registerEvents(new SmithingListener(this), this);
        }
        if (WolfyUtilities.hasPlugin("ItemsAdder")) {
            getLogger().info("Detected ItemsAdder! CustomItems and Recipes will be loaded after ItemsAdder is successfully loaded!");
            pM.registerEvents(new ItemsAdderListener(this), this);
        }
    }

    private void registerCommands() {
        final Field serverCommandMap = Reflection.getDeclaredField(Bukkit.getServer().getClass(), "commandMap");
        serverCommandMap.setAccessible(true);
        try {
            CommandMap commandMap = (CommandMap) serverCommandMap.get(Bukkit.getServer());
            commandMap.register("customcrafting", new CommandCC(this));
            commandMap.register("recipes", "customcrafting", new CommandRecipe(this));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void registerInventories() {
        InventoryAPI<CCCache> invAPI = this.api.getInventoryAPI(CCCache.class);
        api.getConsole().info("$msg.startup.inventories$");
        invAPI.registerCluster(new MainCluster(invAPI, this));
        invAPI.registerCluster(new RecipeCreatorCluster(invAPI, this));
        invAPI.registerCluster(new RecipeBookCluster(invAPI, this));
        invAPI.registerCluster(new EliteCraftingCluster(invAPI, this));
        invAPI.registerCluster(new ItemCreatorCluster(invAPI, this));
        invAPI.registerCluster(new ParticleCreatorCluster(invAPI, this));
        invAPI.registerCluster(new PotionCreatorCluster(invAPI, this));
        invAPI.registerCluster(new RecipeBookEditorCluster(invAPI, this));
    }

    public void checkUpdate(@Nullable Player player) {
        new Thread(() -> {
            try {
                HttpURLConnection con = (HttpURLConnection) new URL("https://api.spigotmc.org/legacy/update.php?resource=55883").openConnection();
                con.setReadTimeout(2000);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String version = bufferedReader.readLine();

                String[] vNew = version.split("\\.");
                String[] vOld = currentVersion.split("\\.");

                for (int i = 0; i < vNew.length; i++) {
                    int v1 = Integer.parseInt(vNew[i]);
                    int v2 = Integer.parseInt(vOld[i]);
                    if (v2 > v1) {
                        outdated = false;
                        return;
                    } else if (v1 > v2) {
                        outdated = true;
                        api.getConsole().warn("$msg.startup.outdated$");
                        if (player != null) {
                            chat.sendMessage(player, "$msg.player.outdated.msg$");
                            chat.sendActionMessage(player, new ClickData("$msg.player.outdated.msg2$", null), new ClickData("$msg.player.outdated.link$", null, new me.wolfyscript.utilities.api.chat.ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.spigotmc.org/resources/55883/")));
                        }
                        return;
                    }
                }
            } catch (Exception ex) {
                api.getConsole().warn("$msg.startup.update_check_fail$");
            }
        }).start();
    }

    public ConfigHandler getConfigHandler() {
        return configHandler;
    }

    public WolfyUtilities getApi() {
        return api;
    }

    public DataHandler getDataHandler() {
        return dataHandler;
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

    public ChatUtils getChatUtils() {
        return chatUtils;
    }

    public Cauldrons getCauldrons() {
        return cauldrons;
    }

    public Patreon getPatreon() {
        return patreon;
    }

    public boolean isOutdated() {
        return outdated;
    }
}
