package me.wolfyscript.customcrafting;

import com.google.gson.GsonBuilder;
import me.wolfyscript.customcrafting.commands.CommandCC;
import me.wolfyscript.customcrafting.commands.CommandRecipe;
import me.wolfyscript.customcrafting.configs.custom_data.EliteWorkbenchData;
import me.wolfyscript.customcrafting.data.PlayerStatistics;
import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.data.Workbenches;
import me.wolfyscript.customcrafting.data.cauldron.Cauldrons;
import me.wolfyscript.customcrafting.handlers.ConfigHandler;
import me.wolfyscript.customcrafting.handlers.DataBaseHandler;
import me.wolfyscript.customcrafting.handlers.InventoryHandler;
import me.wolfyscript.customcrafting.handlers.RecipeHandler;
import me.wolfyscript.customcrafting.listeners.*;
import me.wolfyscript.customcrafting.metrics.Metrics;
import me.wolfyscript.customcrafting.placeholderapi.PlaceHolder;
import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.crafting.CraftListener;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.custom_items.CustomItems;
import me.wolfyscript.utilities.api.inventory.InventoryAPI;
import me.wolfyscript.utilities.api.utils.GsonUtil;
import me.wolfyscript.utilities.api.utils.chat.ClickData;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import javax.annotation.Nullable;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

public class CustomCrafting extends JavaPlugin {

    /*
    « 174
    » 175
     */

    private static Plugin instance;
    private static List<PlayerStatistics> playerStatisticsList = new ArrayList<>();
    private static WolfyUtilities api;
    private static ConfigHandler configHandler;
    private static RecipeHandler recipeHandler;
    private static DataBaseHandler dataBaseHandler = null;

    private static Workbenches workbenches = null;
    private static Cauldrons cauldrons = null;

    private static final boolean betaVersion = false;
    private static String currentVersion;

    private static boolean outdated = false;
    private static boolean loaded = false;

    public static final Pattern VALID_NAMESPACEKEY = Pattern.compile("[a-z0-9._-]+");


    @Override
    public void onLoad() {
        CustomItem.registerCustomData(new EliteWorkbenchData());
        GsonBuilder gsonBuilder = GsonUtil.getGsonBuilder();
        gsonBuilder.registerTypeHierarchyAdapter(Conditions.class, new Conditions.Serialization());
    }

    public void onEnable() {
        instance = this;
        currentVersion = instance.getDescription().getVersion();
        api = WolfyUtilities.getOrCreateAPI(instance);
        api.setCHAT_PREFIX("§7[§6CC§7] ");
        api.setCONSOLE_PREFIX("§7[§3CC§7] ");
        InventoryAPI<TestCache> inventoryAPI = new InventoryAPI<>(api.getPlugin(), api, TestCache.class);
        api.setInventoryAPI(inventoryAPI);

        System.out.println("____ _  _ ____ ___ ____ _  _ ____ ____ ____ ____ ___ _ _  _ ____ ");
        System.out.println("|    |  | [__   |  |  | |\\/| |    |__/ |__| |___  |  | |\\ | | __ ");
        System.out.println("|___ |__| ___]  |  |__| |  | |___ |  \\ |  | |     |  | | \\| |__]");
        System.out.println("    v" + instance.getDescription().getVersion() + (betaVersion ? "-beta" : ""));
        System.out.println(" ");
        if (betaVersion || currentVersion.contains("-dev") || currentVersion.contains("-pre")) {
            System.out.println("This is a unstable build! It may contain bugs and game breaking glitches!");
            System.out.println("Do not use this version on production servers!");
        }
        System.out.println("------------------------------------------------------------------------");

        loaded = canRun();

        File mainConfig = new File(getDataFolder(), "Main-Config.yml");
        if (mainConfig.exists()) {
            System.out.println("Found old CustomCrafting data! renaming folder...");
            if (getDataFolder().renameTo(new File(getDataFolder().getParentFile(), "CustomCrafting_old"))) {
                System.out.println("Renamed to CustomCrafting_old!");
                System.out.println("Creating new folder");
            }
        }

        configHandler = new ConfigHandler(api);
        InventoryHandler invHandler = new InventoryHandler(api);
        recipeHandler = new RecipeHandler(api);
        configHandler.load();

        if (configHandler.getConfig().isDatabankEnabled()) {
            dataBaseHandler = new DataBaseHandler(api);
        }

        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);

        if (loaded) {
            System.out.println("------------------------------------------------------------------------");
            getServer().getPluginManager().registerEvents(new CraftListener(api), this);
            getServer().getPluginManager().registerEvents(new BlockListener(api), this);
            getServer().getPluginManager().registerEvents(new FurnaceListener(), this);
            getServer().getPluginManager().registerEvents(new WorkbenchContents(), this);
            getServer().getPluginManager().registerEvents(new AnvilListener(), this);
            getServer().getPluginManager().registerEvents(new EnchantListener(), this);
            getServer().getPluginManager().registerEvents(new CauldronListener(api), this);
            getServer().getPluginManager().registerEvents(new EliteWorkbenchListener(api), this);

            CommandCC commandCC = new CommandCC();
            PluginCommand command = getServer().getPluginCommand("customcrafting");
            if (!configHandler.getConfig().isCCenabled()) {
                command.setAliases(new ArrayList<>());
            }
            command.setExecutor(commandCC);
            command.setTabCompleter(commandCC);
            getCommand("recipes").setExecutor(new CommandRecipe());
            getCommand("recipes").setTabCompleter(new CommandRecipe());
            loadPlayerStatistics();
            invHandler.init();
            workbenches = new Workbenches(api);

            cauldrons = new Cauldrons(api);
            if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
                api.sendConsoleMessage("$msg.startup.placeholder$");
                new PlaceHolder().register();
            }
            if (Bukkit.getPluginManager().getPlugin("MythicMobs") != null) {
                api.sendConsoleMessage("$msg.startup.mythicmobs.detected$");
                api.sendConsoleMessage("$msg.startup.mythicmobs.register$");
            }

            recipeHandler.load();
            CustomItems.initiateMissingBlockEffects();
            checkUpdate(null);
            Metrics metrics = new Metrics(this);
            metrics.addCustomChart(new Metrics.SimplePie("used_language", () -> getConfigHandler().getConfig().getString("language")));
            metrics.addCustomChart(new Metrics.SimplePie("server_software", () -> {
                String version = Bukkit.getServer().getName();
                if (WolfyUtilities.hasSpigot()) {
                    version = "Spigot";
                }
                if (WolfyUtilities.hasClass("com.destroystokyo.paper.entity.TargetEntityInfo")) {
                    version = "Paper";
                }
                return version;
            }));
            metrics.addCustomChart(new Metrics.SimplePie("advanced_workbench", () -> configHandler.getConfig().isAdvancedWorkbenchEnabled() ? "enabled" : "disabled"));
        }
        System.out.println("------------------------------------------------------------------------");
    }

    public void onDisable() {
        if (loaded) {
            getConfigHandler().getConfig().save();
            workbenches.endTask();
            workbenches.save();
            cauldrons.endAutoSaveTask();
            cauldrons.save();
            getRecipeHandler().onSave();
            savePlayerStatistics();
        }
    }

    public static void checkUpdate(@Nullable Player player) {
        Thread updater = new Thread(() -> {
            try {
                HttpURLConnection con = (HttpURLConnection) new URL(
                        "https://api.spigotmc.org/legacy/update.php?resource=55883").openConnection();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String version = bufferedReader.readLine();
                if (!version.isEmpty() && !version.equals(currentVersion) && !currentVersion.contains("-dev") && !currentVersion.contains("-pre")) {
                    outdated = true;
                    api.sendConsoleWarning("$msg.startup.outdated$");
                    if (player != null) {
                        api.sendPlayerMessage(player, "$msg.player.outdated.msg$");
                        api.sendActionMessage(player, new ClickData("$msg.player.outdated.msg2$", null), new ClickData("$msg.player.outdated.link$", null, new me.wolfyscript.utilities.api.utils.chat.ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.spigotmc.org/resources/55883/")));
                    }
                }

                /*
                String lVersion = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
                String[] lVersionAndDev = lVersion.split("-");
                int lDev = -1;
                if(lVersionAndDev.length > 1){
                    lDev = Integer.parseInt(lVersionAndDev[1].replace("dev", ""));
                }
                String[] lVersionVars = lVersionAndDev[0].split("\\.");
                int lVersionVar = Integer.parseInt(lVersionVars[0]);
                int lFeatureLayer = Integer.parseInt(lVersionVars[1]);
                int lImprovementLayer = Integer.parseInt(lVersionVars[2]);
                int lBugfixLayer = Integer.parseInt(lVersionVars[3]);

                String[] cVersionAndDev = currentVersion.split("-");
                int cDev = -1;
                if(cVersionAndDev.length > 1){
                    cDev = Integer.parseInt(cVersionAndDev[1].replace("dev", ""));
                }
                String[] cVersionVars = cVersionAndDev[0].split("\\.");
                int cVersionVar = Integer.parseInt(cVersionVars[0]);
                int cFeatureLayer = Integer.parseInt(cVersionVars[1]);
                int cImprovementLayer = Integer.parseInt(cVersionVars[2]);
                int cBugfixLayer = Integer.parseInt(cVersionVars[3]);

                if(cVersionVar < lVersionVar || cFeatureLayer < lFeatureLayer || cImprovementLayer < lImprovementLayer || cBugfixLayer < lBugfixLayer){
                    outdated = true;
                }else if(cDev > -1){
                    if(lDev > -1){
                        if(cDev < lDev){
                            outdated = true;
                        }
                    }else{
                        outdated = true;
                    }
               }
                 */

            } catch (Exception ex) {
                ex.printStackTrace();
                api.sendConsoleWarning("$msg.startup.update_check_fail$");
            }
        });
        updater.start();
    }

    private static boolean canRun() {
        try {
            RecipeChoice.ExactChoice exactChoice = new RecipeChoice.ExactChoice(new ItemStack(Material.DEBUG_STICK));
        } catch (NoClassDefFoundError e) {
            System.out.println("You are using an outdated Spigot version!");
            System.out.println("You can get the latest Spigot version via BuildTools: ");
            System.out.println("    https://www.spigotmc.org/wiki/buildtools/");
            System.out.println("------------------------------------------------------------------------");
            return false;
        }
        return true;
    }

    public static boolean isOutdated() {
        return outdated;
    }

    public static boolean isLoaded() {
        return loaded;
    }

    public static ConfigHandler getConfigHandler() {
        return configHandler;
    }

    public static Plugin getInst() {
        return instance;
    }

    public static WolfyUtilities getApi() {
        return api;
    }

    public static RecipeHandler getRecipeHandler() {
        return recipeHandler;
    }

    public static Workbenches getWorkbenches() {
        return workbenches;
    }

    public static Cauldrons getCauldrons() {
        return cauldrons;
    }

    public static boolean hasPlayerCache(Player player) {
        for (PlayerStatistics playerStatistics : playerStatisticsList) {
            if (playerStatistics.getUuid().equals(player.getUniqueId()))
                return true;
        }
        return false;
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
        for (PlayerStatistics playerStatistics : playerStatisticsList) {
            if (playerStatistics.getUuid().equals(uuid))
                return playerStatistics;
        }
        PlayerStatistics playerStatistics = new PlayerStatistics(uuid);
        playerStatisticsList.add(playerStatistics);
        return playerStatistics;
    }

    private static void savePlayerStatistics() {
        HashMap<UUID, HashMap<String, Object>> caches = new HashMap<>();
        for (PlayerStatistics playerStatistics : playerStatisticsList) {
            caches.put(playerStatistics.getUuid(), playerStatistics.getStats());
        }
        try {
            FileOutputStream fos = new FileOutputStream(new File(CustomCrafting.getInst().getDataFolder() + File.separator + "playerstats.dat"));
            BukkitObjectOutputStream oos = new BukkitObjectOutputStream(fos);
            oos.writeObject(caches);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadPlayerStatistics() {
        api.sendConsoleMessage("$msg.startup.playerstats$");
        File file = new File(CustomCrafting.getInst().getDataFolder() + File.separator + "playerstats.dat");
        if (file.exists()) {
            FileInputStream fis;
            try {
                fis = new FileInputStream(file);
                BukkitObjectInputStream ois = new BukkitObjectInputStream(fis);
                try {
                    Object object = ois.readObject();
                    if (object instanceof HashMap) {
                        HashMap<UUID, HashMap<String, Object>> stats = (HashMap<UUID, HashMap<String, Object>>) object;
                        for (UUID uuid : stats.keySet()) {
                            playerStatisticsList.add(new PlayerStatistics(uuid, stats.get(uuid)));
                        }
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

    public static boolean hasDataBaseHandler() {
        return dataBaseHandler != null;
    }

    public static DataBaseHandler getDataBaseHandler() {
        return dataBaseHandler;
    }
}
