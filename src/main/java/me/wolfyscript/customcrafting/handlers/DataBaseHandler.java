package me.wolfyscript.customcrafting.handlers;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.MainConfig;
import me.wolfyscript.customcrafting.configs.custom_configs.CustomConfig;
import me.wolfyscript.customcrafting.configs.custom_configs.anvil.AnvilConfig;
import me.wolfyscript.customcrafting.configs.custom_configs.blast_furnace.BlastingConfig;
import me.wolfyscript.customcrafting.configs.custom_configs.campfire.CampfireConfig;
import me.wolfyscript.customcrafting.configs.custom_configs.furnace.FurnaceConfig;
import me.wolfyscript.customcrafting.configs.custom_configs.items.ItemConfig;
import me.wolfyscript.customcrafting.configs.custom_configs.smoker.SmokerConfig;
import me.wolfyscript.customcrafting.configs.custom_configs.stonecutter.StonecutterConfig;
import me.wolfyscript.customcrafting.configs.custom_configs.workbench.CraftConfig;
import me.wolfyscript.customcrafting.recipes.anvil.CustomAnvilRecipe;
import me.wolfyscript.customcrafting.recipes.blast_furnace.CustomBlastRecipe;
import me.wolfyscript.customcrafting.recipes.campfire.CustomCampfireRecipe;
import me.wolfyscript.customcrafting.recipes.furnace.CustomFurnaceRecipe;
import me.wolfyscript.customcrafting.recipes.smoker.CustomSmokerRecipe;
import me.wolfyscript.customcrafting.recipes.stonecutter.CustomStonecutterRecipe;
import me.wolfyscript.customcrafting.recipes.workbench.ShapedCraftRecipe;
import me.wolfyscript.customcrafting.recipes.workbench.ShapelessCraftRecipe;
import me.wolfyscript.customcrafting.utils.ChatUtils;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.config.ConfigAPI;
import me.wolfyscript.utilities.api.language.LanguageAPI;
import me.wolfyscript.utilities.api.utils.sql.SQLDataBase;
import org.bukkit.plugin.Plugin;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DataBaseHandler {

    private Plugin instance;
    private WolfyUtilities api;
    private ConfigAPI configAPI;
    private LanguageAPI languageAPI;
    private MainConfig mainConfig;

    private SQLDataBase dataBase;

    public DataBaseHandler(WolfyUtilities api) {
        this.api = api;
        this.instance = api.getPlugin();
        this.configAPI = api.getConfigAPI();
        this.languageAPI = api.getLanguageAPI();
        this.mainConfig = CustomCrafting.getConfigHandler().getConfig();
        this.dataBase = new SQLDataBase(api, mainConfig.getDatabankHost(), mainConfig.getDatabankDataBase(), mainConfig.getDatabankUsername(), mainConfig.getDataBankPassword(), mainConfig.getDatabankPort());
        this.dataBase.openConnectionOnMainThread();
        init();
    }

    public void init() {
        CustomCrafting.getConfigHandler().getConfig().setPreferredFileType("json");
        try {
            PreparedStatement itemsTable = dataBase.getPreparedStatement("CREATE TABLE IF NOT EXISTS customcrafting_items" +
                    "(" +
                    "rNamespace VARCHAR(255) null," +
                    "rKey VARCHAR(255) null," +
                    "rData LONGTEXT null," +
                    "constraint customcrafting_items_namespacekey" +
                    " UNIQUE (rNamespace, rKey)" +
                    ");");
            itemsTable.executeUpdate();
            itemsTable.closeOnCompletion();
            PreparedStatement recipesTable = dataBase.getPreparedStatement("CREATE TABLE IF NOT EXISTS customcrafting_recipes" +
                    "(" +
                    "rNamespace VARCHAR(255) null, " +
                    "rKey VARCHAR(255) null, " +
                    "rType TINYTEXT null, " +
                    "rData LONGTEXT null, " +
                    "constraint customcrafting_items_namespacekey" +
                    " UNIQUE (rNamespace, rKey)" +
                    ");");
            recipesTable.executeUpdate();
            recipesTable.closeOnCompletion();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadRecipes(RecipeHandler recipeHandler) throws SQLException {
        api.sendConsoleMessage("$msg.startup.recipes.recipes$");
        ResultSet resultSet = getRecipes();
        if (resultSet == null) {
            return;
        }
        while (resultSet.next()) {
            String namespace = resultSet.getString("rNamespace");
            String key = resultSet.getString("rKey");
            String type = resultSet.getString("rType");
            String data = resultSet.getString("rData");
            api.sendConsoleMessage("- " + namespace + ":" + key);
            try {
                switch (type) {
                    case "workbench":
                        CraftConfig config = new CraftConfig(data, configAPI, namespace, key);
                        if (config.isShapeless()) {
                            recipeHandler.registerRecipe(new ShapelessCraftRecipe(config));
                        } else {
                            recipeHandler.registerRecipe(new ShapedCraftRecipe(config));
                        }
                        break;
                    case "furnace":
                        recipeHandler.registerRecipe(new CustomFurnaceRecipe(new FurnaceConfig(data, configAPI, namespace, key)));
                        break;
                    case "anvil":
                        recipeHandler.registerRecipe(new CustomAnvilRecipe(new AnvilConfig(data, configAPI, namespace, key)));
                        break;
                    case "blast_furnace":
                        recipeHandler.registerRecipe(new CustomBlastRecipe(new BlastingConfig(data, configAPI, namespace, key)));
                        break;
                    case "smoker":
                        recipeHandler.registerRecipe(new CustomSmokerRecipe(new SmokerConfig(data, configAPI, namespace, key)));
                        break;
                    case "campfire":
                        recipeHandler.registerRecipe(new CustomCampfireRecipe(new CampfireConfig(data, configAPI, namespace, key)));
                        break;
                    case "stonecutter":
                        recipeHandler.registerRecipe(new CustomStonecutterRecipe(new StonecutterConfig(data, configAPI, namespace, key)));
                        break;
                    case "enchant":
                }
            } catch (Exception ex) {
                ChatUtils.sendRecipeItemLoadingError(namespace, key, type, ex);
            }
        }
    }

    public void loadItems(RecipeHandler recipeHandler) throws SQLException {
        api.sendConsoleMessage("");
        api.sendConsoleMessage("$msg.startup.recipes.items$");
        ResultSet resultSet = getItems();
        if (resultSet != null) {
            while (resultSet.next()) {
                String namespace = resultSet.getString("rNamespace");
                String key = resultSet.getString("rKey");
                api.sendConsoleMessage("- " + namespace + ":" + key);
                ItemConfig itemConfig = new ItemConfig(resultSet.getString("rData"), configAPI, namespace, key);
                recipeHandler.setCustomItem(itemConfig);
            }
        }
    }

    public ResultSet getRecipes() {
        try {
            PreparedStatement pState = dataBase.getPreparedStatement("SELECT * FROM customcrafting_recipes");
            return pState.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ResultSet getItems() {
        try {
            PreparedStatement pState = dataBase.getPreparedStatement("SELECT * FROM customcrafting_items");
            return pState.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean hasRecipe(String namespace, String key) {
        try {
            ResultSet resultSet = getRecipe(namespace, key);
            return resultSet.isBeforeFirst();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public ResultSet getRecipe(String namespace, String key) {
        try {
            PreparedStatement pState = dataBase.getPreparedStatement("SELECT rType, rData FROM customcrafting_recipes WHERE rNamespace=? AND rKey=?");
            pState.setString(1, namespace);
            pState.setString(2, key);
            return pState.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void addRecipe(CustomConfig data) {
        try {
            PreparedStatement pState = dataBase.getPreparedStatement("INSERT INTO customcrafting_recipes (rNamespace, rKey, rType, rData) values (?, ?, ?, ?)");
            pState.setString(1, data.getFolder());
            pState.setString(2, data.getName());
            pState.setString(3, data.getConfigType());
            pState.setString(4, data.toString());
            dataBase.executeUpdate(pState);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateRecipe(CustomConfig data) {
        if (hasRecipe(data.getFolder(), data.getName())) {
            try {
                PreparedStatement pState = dataBase.getPreparedStatement("UPDATE customcrafting_recipes SET rData=? WHERE rNamespace=? AND rKey=?");
                pState.setString(1, data.toString());
                pState.setString(2, data.getFolder());
                pState.setString(3, data.getName());
                dataBase.executeUpdate(pState);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            addRecipe(data);
        }
    }

    public void removeRecipe(String namespace, String key) {
        try {
            PreparedStatement pState = dataBase.getPreparedStatement("delete from customcrafting_recipes where rNamespace=? and rKey=?");
            pState.setString(1, namespace);
            pState.setString(2, key);
            dataBase.executeUpdate(pState);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public boolean hasItem(String namespace, String key) {
        try {
            ResultSet resultSet = getItem(namespace, key);
            return resultSet.isBeforeFirst();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public ResultSet getItem(String namespace, String key) {
        try {
            PreparedStatement pState = dataBase.getPreparedStatement("SELECT rData FROM customcrafting_items WHERE rNamespace=? AND rKey=?");
            pState.setString(1, namespace);
            pState.setString(2, key);
            return pState.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void addItem(ItemConfig data) {
        try {
            PreparedStatement pState = dataBase.getPreparedStatement("INSERT INTO customcrafting_items (rNamespace, rKey, rData) values (?, ?, ?)");
            pState.setString(1, data.getFolder());
            pState.setString(2, data.getName());
            pState.setString(3, data.toString());
            dataBase.executeUpdate(pState);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateItem(ItemConfig data) {
        if (hasItem(data.getFolder(), data.getName())) {
            try {
                PreparedStatement pState = dataBase.getPreparedStatement("UPDATE customcrafting_items SET rData=? WHERE rNamespace=? AND rKey=?");
                pState.setString(1, data.toString());
                pState.setString(2, data.getFolder());
                pState.setString(3, data.getName());
                dataBase.executeUpdate(pState);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            addItem(data);
        }
    }

    public void removeItem(String namespace, String key) {
        try {
            PreparedStatement pState = dataBase.getPreparedStatement("delete from customcrafting_items where rNamespace=? and rKey=?");
            pState.setString(1, namespace);
            pState.setString(2, key);
            dataBase.executeUpdate(pState);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
