package me.wolfyscript.customcrafting;

import me.wolfyscript.customcrafting.commands.CommandCC;
import me.wolfyscript.customcrafting.commands.CommandRecipe;
import me.wolfyscript.customcrafting.configs.custom_data.CauldronData;
import me.wolfyscript.customcrafting.configs.custom_data.EliteWorkbenchData;
import me.wolfyscript.customcrafting.configs.custom_data.KnowledgeBookData;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.PlayerStatistics;
import me.wolfyscript.customcrafting.data.Workbenches;
import me.wolfyscript.customcrafting.data.cauldron.Cauldrons;
import me.wolfyscript.customcrafting.data.patreon.Patreon;
import me.wolfyscript.customcrafting.data.patreon.Patron;
import me.wolfyscript.customcrafting.handlers.ConfigHandler;
import me.wolfyscript.customcrafting.handlers.DataBaseHandler;
import me.wolfyscript.customcrafting.handlers.InventoryHandler;
import me.wolfyscript.customcrafting.handlers.RecipeHandler;
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
import me.wolfyscript.utilities.util.json.jackson.JacksonUtil;
import me.wolfyscript.utilities.util.world.WorldUtils;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class CustomCrafting extends JavaPlugin {

    /*
    « 174
    » 175
     */
    private static CustomCrafting instance;
    private static final List<PlayerStatistics> playerStatisticsList = new ArrayList<>();
    private static WolfyUtilities api;
    private static ConfigHandler configHandler;
    private static RecipeHandler recipeHandler;
    private static DataBaseHandler dataBaseHandler = null;
    private RecipeUtils recipeUtils;
    private Patreon patreon;

    //Utils
    private ChatUtils chatUtils;

    private static Workbenches workbenches = null;
    private static Cauldrons cauldrons = null;

    private static String currentVersion;

    private boolean outdated = false;

    public static CustomCrafting getInst() {
        return instance;
    }

    public static boolean hasPlayerCache(Player player) {
        return playerStatisticsList.stream().anyMatch(playerStatistics -> playerStatistics.getUuid().equals(player.getUniqueId()));
    }

    @Override
    public void onLoad() {
        getLogger().info("WolfyUtilities API: " + Bukkit.getPluginManager().getPlugin("WolfyUtilities"));
        if (Bukkit.getPluginManager().getPlugin("WolfyUtilities") != null) {
            getLogger().info("Registering custom data.");
            CustomItem.registerCustomData(new EliteWorkbenchData.Provider());
            CustomItem.registerCustomData(new KnowledgeBookData.Provider());
            CustomItem.registerCustomData(new CauldronData.Provider());
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
            System.out.println("--------------------------------------------------------------------------------------------------------");
            setEnabled(false);
            return;
        }

        api = WolfyUtilities.get(instance);
        Chat chat = api.getChat();
        chat.setIN_GAME_PREFIX("§7[§6CC§7] ");
        chat.setCONSOLE_PREFIX("§7[§3CC§7] ");

        api.setInventoryAPI(new InventoryAPI<>(api.getPlugin(), api, CCCache.class));

        if (patreon.isPatreon()) {
            System.out.println("Thanks for actively supporting this plugin on Patreon!");
        }

        patreon.initialize();

        System.out.println();
        System.out.println("Special thanks to my Patreons for supporting this project: ");
        List<Patron> patronList = patreon.getPatronList();
        int lengthColumn = 20;
        for (int i = 0; i < patronList.size(); i += 2) {
            StringBuilder sB = new StringBuilder();
            String name = patronList.get(i).getName();
            sB.append(name);
            for (int j = 0; j < lengthColumn - name.length(); j++) {
                sB.append(" ");
            }
            System.out.println("    " + sB.append(patronList.get(i + 1).getName()).toString());
        }
        System.out.println();
        System.out.println("------------------------------------------------------------------------");

        recipeUtils = new RecipeUtils(this);
        chatUtils = new ChatUtils(this);
        configHandler = new ConfigHandler(this);
        try {
            configHandler.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        recipeHandler = new RecipeHandler(this);

        if (configHandler.getConfig().isDatabankEnabled()) {
            dataBaseHandler = new DataBaseHandler(this);
        }

        InventoryHandler invHandler = new InventoryHandler(this);

        PluginManager pM = Bukkit.getPluginManager();

        pM.registerEvents(new PlayerListener(this), this);

        System.out.println("------------------------------------------------------------------------");
        pM.registerEvents(new CraftListener(this), this);
        pM.registerEvents(new BlockListener(api), this);
        pM.registerEvents(new FurnaceListener(this), this);
        pM.registerEvents(new AnvilListener(this), this);
        //getServer().getPluginManager().registerEvents(new EnchantListener(), this);
        pM.registerEvents(new CauldronListener(this), this);
        pM.registerEvents(new EliteWorkbenchListener(api), this);
        pM.registerEvents(new GrindStoneListener(this), this);
        pM.registerEvents(new BrewingStandListener(api, this), this);
        if (WolfyUtilities.hasNetherUpdate()) {
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

        loadPlayerStatistics();
        invHandler.init();
        workbenches = new Workbenches(this);

        cauldrons = new Cauldrons(this);
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            chat.sendConsoleMessage("$msg.startup.placeholder$");
            new PlaceHolder(this).register();
        }

        //This makes sure that the customItems and recipes are loaded after ItemsAdder, so that all items are loaded correctly!
        if (!WolfyUtilities.hasPlugin("ItemsAdder")) {
            loadRecipesAndItems();
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
            workbenches.endTask();
            workbenches.save();
            cauldrons.endAutoSaveTask();
            cauldrons.save();
            getRecipeHandler().onSave();
            savePlayerStatistics();
        }
    }

    public void loadRecipesAndItems() {
        recipeHandler.load();
        WorldUtils.getWorldCustomItemStore().initiateMissingBlockEffects();
    }

    private void savePlayerStatistics() {
        HashMap<UUID, HashMap<String, Object>> caches = new HashMap<>();
        playerStatisticsList.forEach(playerStatistics -> caches.put(playerStatistics.getUuid(), playerStatistics.getStats()));
        try {
            FileOutputStream fos = new FileOutputStream(new File(getDataFolder() + File.separator + "playerstats.dat"));
            BukkitObjectOutputStream oos = new BukkitObjectOutputStream(fos);
            oos.writeObject(caches);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadPlayerStatistics() {
        api.getChat().sendConsoleMessage("$msg.startup.playerstats$");
        File file = new File(getDataFolder() + File.separator + "playerstats.dat");
        if (file.exists()) {
            FileInputStream fis;
            try {
                fis = new FileInputStream(file);
                BukkitObjectInputStream ois = new BukkitObjectInputStream(fis);
                try {
                    Object object = ois.readObject();
                    if (object instanceof HashMap) {
                        ((HashMap<UUID, HashMap<String, Object>>) object).forEach((uuid, stat) -> playerStatisticsList.add(new PlayerStatistics(uuid, stat)));
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                ois.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public ConfigHandler getConfigHandler() {
        return configHandler;
    }

    public static WolfyUtilities getApi() {
        return api;
    }

    public RecipeHandler getRecipeHandler() {
        return recipeHandler;
    }

    public RecipeUtils getRecipeUtils() {
        return recipeUtils;
    }

    public ChatUtils getChatUtils() {
        return chatUtils;
    }

    public static Workbenches getWorkbenches() {
        return workbenches;
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
                            chat.sendPlayerMessage(player, "$msg.player.outdated.msg$");
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

    public static void renewPlayerStatistics(Player player) {
        if (hasPlayerCache(player)) {
            PlayerStatistics playerStatistics = getPlayerStatistics(player);
            playerStatisticsList.remove(playerStatistics);
        }
        playerStatisticsList.add(new PlayerStatistics(player.getUniqueId()));
    }

    public static PlayerStatistics getPlayerStatistics(Player player) {
        return getPlayerStatistics(player.getUniqueId());
    }

    public static PlayerStatistics getPlayerStatistics(UUID uuid) {
        PlayerStatistics playerStatistics = playerStatisticsList.stream().filter(pS -> pS.getUuid().equals(uuid)).findFirst().orElse(new PlayerStatistics(uuid));
        if (!playerStatisticsList.contains(playerStatistics)) playerStatisticsList.add(playerStatistics);
        return playerStatistics;
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
                File file = new File(getDataFolder() + "/recipes/" + namespacedKey.getNamespace() + "/items", namespacedKey.getKey() + ".json");
                file.getParentFile().mkdirs();
                if (file.exists() || file.createNewFile()) {
                    JacksonUtil.getObjectWriter(getConfigHandler().getConfig().isPrettyPrinting()).writeValue(file, customItem);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (Registry.CUSTOM_ITEMS.get(namespacedKey) != null) {
            Registry.CUSTOM_ITEMS.remove(namespacedKey);
        }
        Registry.CUSTOM_ITEMS.register(namespacedKey, customItem);
    }

    public boolean deleteItem(NamespacedKey namespacedKey, @Nullable Player player) {
        if (!Registry.CUSTOM_ITEMS.has(namespacedKey)) {
            if (player != null) getApi().getChat().sendPlayerMessage(player, "error");
            return false;
        }
        Registry.CUSTOM_ITEMS.remove(namespacedKey);
        System.gc();
        if (CustomCrafting.hasDataBaseHandler()) {
            CustomCrafting.getDataBaseHandler().removeItem(namespacedKey);
            return true;
        } else {
            File file = new File(getDataFolder() + "/recipes/" + namespacedKey.getNamespace() + "/items", namespacedKey.getKey() + ".json");
            if (file.delete()) {
                if (player != null) getApi().getChat().sendPlayerMessage(player, "&aCustomItem deleted!");
                return true;
            } else {
                file.deleteOnExit();
                if (player != null)
                    getApi().getChat().sendPlayerMessage(player, "&cCouldn't delete CustomItem on runtime! File is being deleted on restart!");
            }
        }
        return false;
    }
}
