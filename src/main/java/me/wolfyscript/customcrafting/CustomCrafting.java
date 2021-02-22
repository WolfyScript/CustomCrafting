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
import me.wolfyscript.customcrafting.handlers.ConfigHandler;
import me.wolfyscript.customcrafting.handlers.DataBaseHandler;
import me.wolfyscript.customcrafting.handlers.DataHandler;
import me.wolfyscript.customcrafting.handlers.InventoryHandler;
import me.wolfyscript.customcrafting.listeners.*;
import me.wolfyscript.customcrafting.placeholderapi.PlaceHolder;
import me.wolfyscript.customcrafting.utils.ChatUtils;
import me.wolfyscript.customcrafting.utils.RecipeUtils;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.chat.Chat;
import me.wolfyscript.utilities.api.chat.ClickData;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.InventoryAPI;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.Reflection;
import me.wolfyscript.utilities.util.Registry;
import me.wolfyscript.utilities.util.entity.CustomPlayerData;
import me.wolfyscript.utilities.util.json.jackson.JacksonUtil;
import me.wolfyscript.utilities.util.version.MinecraftVersions;
import me.wolfyscript.utilities.util.version.ServerVersion;
import me.wolfyscript.utilities.util.world.WorldUtils;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

public class CustomCrafting extends JavaPlugin {

    public static final NamespacedKey ADVANCED_CRAFTING_TABLE = new NamespacedKey("customcrafting", "advanced_crafting_table");
    public static final NamespacedKey ELITE_CRAFTING_TABLE = new NamespacedKey("customcrafting", "elite_crafting_table");
    public static final NamespacedKey RECIPE_BOOK = new NamespacedKey("customcrafting", "recipe_book");
    public static final NamespacedKey CAULDRON = new NamespacedKey("customcrafting", "cauldron");
    //Used for backwards compatibility
    public static final NamespacedKey ADVANCED_WORKBENCH = new NamespacedKey("customcrafting", "workbench");
    public static final NamespacedKey ELITE_WORKBENCH = new NamespacedKey("customcrafting", "elite_workbench");

    public static final int BUKKIT_VERSION = Bukkit.getUnsafe().getDataVersion();
    public static final int CONFIG_VERSION = 2;

    private static CustomCrafting instance;
    private static WolfyUtilities api;
    private static ConfigHandler configHandler;
    private static DataHandler dataHandler;
    private static DataBaseHandler dataBaseHandler = null;
    private InventoryHandler inventoryHandler;
    private RecipeUtils recipeUtils;
    private Patreon patreon;

    //Utils
    private ChatUtils chatUtils;
    private static Cauldrons cauldrons = null;
    private static String currentVersion;
    private boolean outdated = false;

    public static CustomCrafting getInst() {
        return instance;
    }

    @Override
    public void onLoad() {
        getLogger().info("WolfyUtilities API: " + Bukkit.getPluginManager().getPlugin("WolfyUtilities"));
        if (Bukkit.getPluginManager().getPlugin("WolfyUtilities") != null) {
            getLogger().info("Registering custom data");
            Registry.CUSTOM_ITEM_DATA.register(new EliteWorkbenchData.Provider());
            Registry.CUSTOM_ITEM_DATA.register(new RecipeBookData.Provider());
            Registry.CUSTOM_ITEM_DATA.register(new CauldronData.Provider());

            CustomPlayerData.register(new CCPlayerData.Provider());
        } else {
            getLogger().severe("Couldn't find WolfyUtilities API!");
        }
    }

    @Override
    public void onEnable() {
        instance = this;
        currentVersion = instance.getDescription().getVersion();
        patreon = new Patreon(this);
        System.out.println("____ _  _ ____ ___ ____ _  _ ____ ____ ____ ____ ___ _ _  _ ____ ");
        System.out.println("|    |  | [__   |  |  | |\\/| |    |__/ |__| |___  |  | |\\ | | __ ");
        System.out.println("|___ |__| ___]  |  |__| |  | |___ |  \\ |  | |     |  | | \\| |__]");
        System.out.println("    v" + currentVersion + " " + (patreon.isPatreon() ? "Patreon" : "Free"));
        System.out.println(" ");

        if (Bukkit.getPluginManager().getPlugin("WolfyUtilities") == null) {
            getLogger().severe("CustomCrafting requires WolfyUtilities to work! Make sure you download and install it besides CC! ");
            getLogger().severe("Download link: https://www.spigotmc.org/resources/wolfyutilities.64124/");
            getLogger().severe("--------------------------------------------------------------------------------------------------------");
            setEnabled(false);
            return;
        }

        api = WolfyUtilities.get(instance);
        Chat chat = api.getChat();
        chat.setIN_GAME_PREFIX("§7[§3CC§7] ");
        chat.setCONSOLE_PREFIX("§7[§3CC§7] ");

        api.setInventoryAPI(new InventoryAPI<>(api.getPlugin(), api, CCCache.class));
        if (patreon.isPatreon()) {
            System.out.println("Thanks for actively supporting this plugin on Patreon!");
        }
        patreon.initialize();
        System.out.println();
        System.out.println("Special thanks to my Patrons for supporting this project: ");
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
                System.out.println("    " + sB.toString());
            }
        }
        System.out.println("------------------------------------------------------------------------");

        chatUtils = new ChatUtils(this);
        configHandler = new ConfigHandler(this);
        if (configHandler.getConfig().isDatabaseEnabled()) {
            dataBaseHandler = new DataBaseHandler(api, configHandler.getConfig(), this);
        }

        try {
            configHandler.loadDefaults();
        } catch (IOException e) {
            e.printStackTrace();
        }
        dataHandler = new DataHandler(this);

        recipeUtils = new RecipeUtils(this);

        inventoryHandler = new InventoryHandler(this);

        PluginManager pM = Bukkit.getPluginManager();

        pM.registerEvents(new PlayerListener(this), this);

        System.out.println("------------------------------------------------------------------------");
        pM.registerEvents(new CraftListener(this), this);
        pM.registerEvents(new FurnaceListener(this), this);
        pM.registerEvents(new AnvilListener(this), this);
        //getServer().getPluginManager().registerEvents(new EnchantListener(), this);
        pM.registerEvents(new CauldronListener(this), this);
        pM.registerEvents(new EliteWorkbenchListener(api), this);
        pM.registerEvents(new GrindStoneListener(this), this);
        pM.registerEvents(new BrewingStandListener(api, this), this);
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_16)) {
            pM.registerEvents(new SmithingListener(this), this);
        }

        final Field serverCommandMap = Reflection.getDeclaredField(Bukkit.getServer().getClass(), "commandMap");
        serverCommandMap.setAccessible(true);
        try {
            CommandMap commandMap = (CommandMap) serverCommandMap.get(Bukkit.getServer());
            commandMap.register("customcrafting", new CommandCC(this));
            commandMap.register("recipes", "customcrafting", new CommandRecipe(this));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        inventoryHandler.init();

        cauldrons = new Cauldrons(this);
        if (WolfyUtilities.hasPlugin("PlaceholderAPI")) {
            chat.sendConsoleMessage("$msg.startup.placeholder$");
            new PlaceHolder(this).register();
        }

        //This makes sure that the customItems and recipes are loaded after ItemsAdder, so that all items are loaded correctly!
        if (!WolfyUtilities.hasPlugin("ItemsAdder")) {
            try {
                loadRecipesAndItems();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            getLogger().info("Detected ItemsAdder! CustomItems and Recipes will be loaded after ItemsAdder is successfully loaded!");
            pM.registerEvents(new ItemsAdderListener(this), this);
        }

        //Don't check for updates when it's a Premium+ version, because there isn't a way to do so yet!
        if (!patreon.isPatreon()) {
            checkUpdate(null);
        }

        //Load Metrics
        Metrics metrics = new Metrics(this, 3211);
        metrics.addCustomChart(new Metrics.SimplePie("used_language", () -> getConfigHandler().getConfig().getString("language")));
        metrics.addCustomChart(new Metrics.SimplePie("advanced_workbench", () -> configHandler.getConfig().isAdvancedWorkbenchEnabled() ? "enabled" : "disabled"));

        System.out.println("------------------------------------------------------------------------");
    }

    @Override
    public void onDisable() {
        if (Bukkit.getPluginManager().getPlugin("WolfyUtilities") != null) {
            try {
                configHandler.save();
            } catch (IOException e) {
                e.printStackTrace();
            }
            cauldrons.endAutoSaveTask();
            cauldrons.save();
            getRecipeHandler().onSave();
        }
    }

    public void loadRecipesAndItems() throws IOException {
        if (!configHandler.getConfig().getDisabledRecipes().isEmpty()) {
            dataHandler.getDisabledRecipes().addAll(configHandler.getConfig().getDisabledRecipes().parallelStream().map(NamespacedKey::of).collect(Collectors.toList()));
        }
        dataHandler.load(true);
        dataHandler.indexRecipeItems();
        WorldUtils.getWorldCustomItemStore().initiateMissingBlockEffects();
    }

    public ConfigHandler getConfigHandler() {
        return configHandler;
    }

    public static WolfyUtilities getApi() {
        return api;
    }

    public DataHandler getRecipeHandler() {
        return dataHandler;
    }

    public InventoryHandler getInventoryHandler() {
        return inventoryHandler;
    }

    public RecipeUtils getRecipeUtils() {
        return recipeUtils;
    }

    public ChatUtils getChatUtils() {
        return chatUtils;
    }

    public static Cauldrons getCauldrons() {
        return cauldrons;
    }

    public Patreon getPatreon() {
        return patreon;
    }

    public void checkUpdate(@Nullable Player player) {
        Chat chat = api.getChat();
        new Thread(() -> {
            try {
                HttpURLConnection con = (HttpURLConnection) new URL("https://api.spigotmc.org/legacy/update.php?resource=55883").openConnection();

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
                        chat.sendConsoleWarning("$msg.startup.outdated$");
                        if (player != null) {
                            chat.sendMessage(player, "$msg.player.outdated.msg$");
                            chat.sendActionMessage(player, new ClickData("$msg.player.outdated.msg2$", null), new ClickData("$msg.player.outdated.link$", null, new me.wolfyscript.utilities.api.chat.ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.spigotmc.org/resources/55883/")));
                        }
                        return;
                    }
                }
            } catch (Exception ex) {
                chat.sendConsoleWarning("$msg.startup.update_check_fail$");
            }
        }).start();
    }

    public boolean isOutdated() {
        return outdated;
    }

    public static boolean hasDataBaseHandler() {
        return dataBaseHandler != null;
    }

    public static DataBaseHandler getDataBaseHandler() {
        return dataBaseHandler;
    }

    public void saveItem(NamespacedKey namespacedKey, CustomItem customItem) {
        if (CustomCrafting.hasDataBaseHandler()) {
            CustomCrafting.getDataBaseHandler().updateItem(namespacedKey, customItem);
        } else {
            try {
                File file = new File(DataHandler.DATA_FOLDER + File.separator + namespacedKey.getNamespace() + File.separator + "items", namespacedKey.getKey() + ".json");
                file.getParentFile().mkdirs();
                if (file.exists() || file.createNewFile()) {
                    JacksonUtil.getObjectWriter(getConfigHandler().getConfig().isPrettyPrinting()).writeValue(file, customItem);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Registry.CUSTOM_ITEMS.register(namespacedKey, customItem);
    }

    public boolean deleteItem(NamespacedKey namespacedKey, @Nullable Player player) {
        if (!Registry.CUSTOM_ITEMS.has(namespacedKey)) {
            if (player != null) getApi().getChat().sendMessage(player, "error");
            return false;
        }
        Registry.CUSTOM_ITEMS.remove(namespacedKey);
        System.gc();
        if (CustomCrafting.hasDataBaseHandler()) {
            CustomCrafting.getDataBaseHandler().removeItem(namespacedKey);
            return true;
        } else {
            File file = new File(DataHandler.DATA_FOLDER + File.separator + namespacedKey.getNamespace() + File.separator + "items", namespacedKey.getKey() + ".json");
            if (file.delete()) {
                if (player != null) getApi().getChat().sendMessage(player, "&aCustomItem deleted!");
                return true;
            } else {
                file.deleteOnExit();
                if (player != null)
                    getApi().getChat().sendMessage(player, "&cCouldn't delete CustomItem on runtime! File is being deleted on restart!");
            }
        }
        return false;
    }
}
