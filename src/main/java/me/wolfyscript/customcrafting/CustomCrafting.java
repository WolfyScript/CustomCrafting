package me.wolfyscript.customcrafting;

import me.wolfyscript.customcrafting.commands.CommandCC;
import me.wolfyscript.customcrafting.commands.CommandRecipe;
import me.wolfyscript.customcrafting.configs.custom_data.CauldronData;
import me.wolfyscript.customcrafting.configs.custom_data.EliteWorkbenchData;
import me.wolfyscript.customcrafting.configs.custom_data.KnowledgeBookData;
import me.wolfyscript.customcrafting.data.PlayerStatistics;
import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.data.Workbenches;
import me.wolfyscript.customcrafting.data.cauldron.Cauldrons;
import me.wolfyscript.customcrafting.handlers.ConfigHandler;
import me.wolfyscript.customcrafting.handlers.DataBaseHandler;
import me.wolfyscript.customcrafting.handlers.InventoryHandler;
import me.wolfyscript.customcrafting.handlers.RecipeHandler;
import me.wolfyscript.customcrafting.listeners.*;
import me.wolfyscript.customcrafting.placeholderapi.PlaceHolder;
import me.wolfyscript.customcrafting.utils.ChatUtils;
import me.wolfyscript.customcrafting.utils.RecipeUtils;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.custom_items.CustomItems;
import me.wolfyscript.utilities.api.inventory.InventoryAPI;
import me.wolfyscript.utilities.api.utils.NamespacedKey;
import me.wolfyscript.utilities.api.utils.chat.ClickData;
import me.wolfyscript.utilities.api.utils.json.jackson.JacksonUtil;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.jetbrains.annotations.Nullable;

import java.io.*;
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
    private RecipeUtils recipeUtils;
    private static DataBaseHandler dataBaseHandler = null;

    //Utils
    private ChatUtils chatUtils;

    private static Workbenches workbenches = null;
    private static Cauldrons cauldrons = null;

    private static String currentVersion;

    private boolean outdated = false;
    private final boolean premium = true;
    private boolean premiumPlus = false;

    @Nullable
    public static CustomCrafting getInst() {
        return instance;
    }

    @Override
    public void onLoad() {
        CustomItem.registerCustomData(new EliteWorkbenchData());
        CustomItem.registerCustomData(new KnowledgeBookData());
        CustomItem.registerCustomData(new CauldronData());
    }

    public static boolean hasPlayerCache(Player player) {
        return playerStatisticsList.stream().anyMatch(playerStatistics -> playerStatistics.getUuid().equals(player.getUniqueId()));
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
        api.sendConsoleMessage("$msg.startup.playerstats$");
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

    public void onDisable() {
        getConfigHandler().getConfig().save();
        workbenches.endTask();
        workbenches.save();
        cauldrons.endAutoSaveTask();
        cauldrons.save();
        getRecipeHandler().onSave();
        savePlayerStatistics();
    }

    public ConfigHandler getConfigHandler() {
        return configHandler;
    }

    public void onEnable() {
        instance = this;
        currentVersion = instance.getDescription().getVersion();
        api = WolfyUtilities.getOrCreateAPI(instance);
        api.setCHAT_PREFIX("§7[§6CC§7] ");
        api.setCONSOLE_PREFIX("§7[§3CC§7] ");

        InventoryAPI<TestCache> inventoryAPI = new InventoryAPI<>(api.getPlugin(), api, TestCache.class);

        api.setInventoryAPI(inventoryAPI);

        if (!currentVersion.endsWith(".0")) {
            premiumPlus = true;
        }
        System.out.println("____ _  _ ____ ___ ____ _  _ ____ ____ ____ ____ ___ _ _  _ ____ ");
        System.out.println("|    |  | [__   |  |  | |\\/| |    |__/ |__| |___  |  | |\\ | | __ ");
        System.out.println("|___ |__| ___]  |  |__| |  | |___ |  \\ |  | |     |  | | \\| |__]");
        System.out.println("    v" + instance.getDescription().getVersion() + " " + (premium ? "Premium" : (premiumPlus ? "Premium+" : "")));
        System.out.println(" ");

        if (premiumPlus) {
            System.out.println("Thanks for actively supporting this plugin on Patreon!");
        } else if (premium) {
            System.out.println("Thanks for supporting this plugin!");
        }
        System.out.println();
        System.out.println("Special thanks to my Patreons for supporting this project: ");
        System.out.println(
                "       Apprehentice        Alex            Vincent Deniau\n" +
                        "       Nat R               gizmonster      Nick coburn\n" +
                        "       TheDutchRuben       Beng701         Eli2t\n" +
                        "       르 미                 Ananass Me      Thomas Texier\n" +
                        "       Ethonion"
        );
        System.out.println();
        System.out.println("------------------------------------------------------------------------");

        File mainConfig = new File(getDataFolder(), "Main-Config.yml");
        if (mainConfig.exists()) {
            System.out.println("Found old CustomCrafting data! renaming folder...");
            if (getDataFolder().renameTo(new File(getDataFolder().getParentFile(), "CustomCrafting_old"))) {
                System.out.println("Renamed to CustomCrafting_old!");
                System.out.println("Creating new folder");
            }
        }
        recipeUtils = new RecipeUtils(this);
        chatUtils = new ChatUtils(this);
        configHandler = new ConfigHandler(this);
        configHandler.load();
        recipeHandler = new RecipeHandler(this);

        if (configHandler.getConfig().isDatabankEnabled()) {
            dataBaseHandler = new DataBaseHandler(this);
        }

        InventoryHandler invHandler = new InventoryHandler(this);

        Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this);

        System.out.println("------------------------------------------------------------------------");
        getServer().getPluginManager().registerEvents(new CraftListener(this), this);
        getServer().getPluginManager().registerEvents(new BlockListener(api), this);
        getServer().getPluginManager().registerEvents(new FurnaceListener(this), this);
        getServer().getPluginManager().registerEvents(new AnvilListener(this), this);
        //getServer().getPluginManager().registerEvents(new EnchantListener(), this);
        getServer().getPluginManager().registerEvents(new CauldronListener(this), this);
        getServer().getPluginManager().registerEvents(new EliteWorkbenchListener(api), this);
        getServer().getPluginManager().registerEvents(new GrindStoneListener(this), this);
        getServer().getPluginManager().registerEvents(new BrewingStandListener(this), this);

        CommandMap commandMap = getServer().getCommandMap();
        commandMap.register("customcrafting", new CommandCC(this));
        commandMap.register("recipes", "customcrafting", new CommandRecipe(this));

        loadPlayerStatistics();
        invHandler.init();
        workbenches = new Workbenches(this);

        cauldrons = new Cauldrons(this);
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            api.sendConsoleMessage("$msg.startup.placeholder$");
            new PlaceHolder(this).register();
        }
        if (Bukkit.getPluginManager().getPlugin("MythicMobs") != null) {
            api.sendConsoleMessage("$msg.startup.mythicmobs.detected$");
            api.sendConsoleMessage("$msg.startup.mythicmobs.register$");
        }

        recipeHandler.load();
        CustomItems.initiateMissingBlockEffects();

        //Don't check for updates when it's a Premium+ version, because there isn't a way yet to do so!
        if (!premiumPlus) {
            checkUpdate(null);
        }

        Metrics metrics = new Metrics(this, 3211);
        metrics.addCustomChart(new Metrics.SimplePie("used_language", () -> getConfigHandler().getConfig().getString("language")));
        metrics.addCustomChart(new Metrics.SimplePie("server_software", () -> {
            String version = Bukkit.getServer().getName();
            if (WolfyUtilities.hasSpigot()) {
                version = "Spigot";
            }
            if (WolfyUtilities.hasClass("com.destroystokyo.paper.Title")) {
                version = "Paper";
            }
            return version;
        }));
        metrics.addCustomChart(new Metrics.SimplePie("advanced_workbench", () -> configHandler.getConfig().isAdvancedWorkbenchEnabled() ? "enabled" : "disabled"));

        System.out.println("------------------------------------------------------------------------");
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

    public void checkUpdate(@Nullable Player player) {
        Thread updater = new Thread(() -> {
            try {
                HttpURLConnection con = (HttpURLConnection) new URL(
                        "https://api.spigotmc.org/legacy/update.php?resource=55883").openConnection();

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
                        api.sendConsoleWarning("$msg.startup.outdated$");
                        if (player != null) {
                            api.sendPlayerMessage(player, "$msg.player.outdated.msg$");
                            api.sendActionMessage(player, new ClickData("$msg.player.outdated.msg2$", null), new ClickData("$msg.player.outdated.link$", null, new me.wolfyscript.utilities.api.utils.chat.ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.spigotmc.org/resources/55883/")));
                        }
                        return;
                    }
                }
            } catch (Exception ex) {
                api.sendConsoleWarning("$msg.startup.update_check_fail$");
            }
        });
        updater.start();
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

    public boolean isPremium() {
        return premium;
    }

    public boolean isPremiumPlus() {
        return premiumPlus;
    }

    public static boolean hasDataBaseHandler() {
        return dataBaseHandler != null;
    }

    public static DataBaseHandler getDataBaseHandler() {
        return dataBaseHandler;
    }

    public void saveItem(me.wolfyscript.utilities.api.utils.NamespacedKey namespacedKey, CustomItem customItem) {
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
        if (CustomItems.getCustomItem(namespacedKey) != null) {
            CustomItems.removeCustomItem(namespacedKey);
        }
        CustomItems.addCustomItem(namespacedKey, customItem);
    }

    public boolean deleteItem(NamespacedKey namespacedKey, @Nullable Player player) {
        if (!CustomItems.hasCustomItem(namespacedKey)) {
            if (player != null) getApi().sendPlayerMessage(player, "error");
            return false;
        }
        CustomItems.removeCustomItem(namespacedKey);
        System.gc();
        if (CustomCrafting.hasDataBaseHandler()) {
            CustomCrafting.getDataBaseHandler().removeItem(namespacedKey);
            return true;
        } else {
            File file = new File(getDataFolder() + "/recipes/" + namespacedKey.getNamespace() + "/items", namespacedKey.getKey() + ".json");
            if (file.delete()) {
                if (player != null) getApi().sendPlayerMessage(player, "&aCustomItem deleted!");
                return true;
            } else {
                file.deleteOnExit();
                if (player != null)
                    getApi().sendPlayerMessage(player, "&cCouldn't delete CustomItem on runtime! File is being deleted on restart!");
            }
        }
        return false;
    }
}
