package me.wolfyscript.customcrafting.handlers;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.MainConfig;
import me.wolfyscript.customcrafting.recipes.types.CustomRecipe;
import me.wolfyscript.customcrafting.recipes.types.RecipeConfig;
import me.wolfyscript.customcrafting.recipes.types.anvil.AnvilConfig;
import me.wolfyscript.customcrafting.recipes.types.anvil.CustomAnvilRecipe;
import me.wolfyscript.customcrafting.recipes.types.blast_furnace.BlastingConfig;
import me.wolfyscript.customcrafting.recipes.types.blast_furnace.CustomBlastRecipe;
import me.wolfyscript.customcrafting.recipes.types.campfire.CampfireConfig;
import me.wolfyscript.customcrafting.recipes.types.campfire.CustomCampfireRecipe;
import me.wolfyscript.customcrafting.recipes.types.cauldron.CauldronConfig;
import me.wolfyscript.customcrafting.recipes.types.cauldron.CauldronRecipe;
import me.wolfyscript.customcrafting.recipes.types.elite_workbench.EliteCraftConfig;
import me.wolfyscript.customcrafting.recipes.types.elite_workbench.ShapedEliteCraftRecipe;
import me.wolfyscript.customcrafting.recipes.types.elite_workbench.ShapelessEliteCraftRecipe;
import me.wolfyscript.customcrafting.recipes.types.furnace.CustomFurnaceRecipe;
import me.wolfyscript.customcrafting.recipes.types.furnace.FurnaceConfig;
import me.wolfyscript.customcrafting.recipes.types.smoker.CustomSmokerRecipe;
import me.wolfyscript.customcrafting.recipes.types.smoker.SmokerConfig;
import me.wolfyscript.customcrafting.recipes.types.stonecutter.CustomStonecutterRecipe;
import me.wolfyscript.customcrafting.recipes.types.stonecutter.StonecutterConfig;
import me.wolfyscript.customcrafting.recipes.types.workbench.AdvancedCraftConfig;
import me.wolfyscript.customcrafting.recipes.types.workbench.ShapedCraftRecipe;
import me.wolfyscript.customcrafting.recipes.types.workbench.ShapelessCraftRecipe;
import me.wolfyscript.customcrafting.utils.ChatUtils;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.config.ConfigAPI;
import me.wolfyscript.utilities.api.custom_items.CustomItems;
import me.wolfyscript.utilities.api.custom_items.ItemConfig;
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
            api.sendConsoleMessage("- " + namespace + ":" + key);
            CustomRecipe recipe = getRecipe(namespace, key);
            if (recipe != null) {
                recipeHandler.registerRecipe(recipe);
            } else {
                api.sendConsoleMessage("Error loading recipe \"" + namespace + ":" + "\". Couldn't find recipe in DataBase!");
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
                CustomItems.setCustomItem(itemConfig);
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
            ResultSet resultSet = getRecipeData(namespace, key);
            return resultSet.isBeforeFirst();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public ResultSet getRecipeData(String namespace, String key) {
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

    public CustomRecipe getRecipe(String namespace, String key) {
        ResultSet resultSet = getRecipeData(namespace, key);
        try {
            while (resultSet.next()) {
                String type = resultSet.getString("rType");
                String data = resultSet.getString("rData");
                try {
                    switch (type) {
                        case "workbench":
                            AdvancedCraftConfig config = new AdvancedCraftConfig(data, configAPI, namespace, key);
                            if (config.isShapeless()) {
                                return new ShapelessCraftRecipe(config);
                            } else {
                                return new ShapedCraftRecipe(config);
                            }
                        case "elite_workbench":
                            EliteCraftConfig eliteCraftConfig = new EliteCraftConfig(data, configAPI, namespace, key);
                            if (eliteCraftConfig.isShapeless()) {
                                return new ShapelessEliteCraftRecipe(eliteCraftConfig);
                            } else {
                                return new ShapedEliteCraftRecipe(eliteCraftConfig);
                            }
                        case "furnace":
                            return new CustomFurnaceRecipe(new FurnaceConfig(data, configAPI, namespace, key));
                        case "anvil":
                            return new CustomAnvilRecipe(new AnvilConfig(data, configAPI, namespace, key));
                        case "blast_furnace":
                            return new CustomBlastRecipe(new BlastingConfig(data, configAPI, namespace, key));
                        case "smoker":
                            return new CustomSmokerRecipe(new SmokerConfig(data, configAPI, namespace, key));
                        case "campfire":
                            return new CustomCampfireRecipe(new CampfireConfig(data, configAPI, namespace, key));
                        case "stonecutter":
                            return new CustomStonecutterRecipe(new StonecutterConfig(data, configAPI, namespace, key));
                        case "enchant":
                            break;
                        case "cauldron":
                            return new CauldronRecipe(new CauldronConfig(data, configAPI, namespace, key));
                    }
                } catch (Exception ex) {
                    ChatUtils.sendRecipeItemLoadingError(namespace, key, type, ex);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void addRecipe(RecipeConfig data) {
        addRecipe(data, true);
    }

    public void addRecipe(RecipeConfig data, boolean async) {
        try {
            PreparedStatement pState = dataBase.getPreparedStatement("INSERT INTO customcrafting_recipes (rNamespace, rKey, rType, rData) values (?, ?, ?, ?)");
            pState.setString(1, data.getNamespace());
            pState.setString(2, data.getName());
            pState.setString(3, data.getConfigType());
            pState.setString(4, data.toString());
            if (async) {
                dataBase.executeUpdate(pState);
            } else {
                pState.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateRecipe(RecipeConfig data) {
        updateRecipe(data, true);
    }

    public void updateRecipe(RecipeConfig data, boolean async) {
        if (hasRecipe(data.getNamespace(), data.getName())) {
            try {
                PreparedStatement pState = dataBase.getPreparedStatement("UPDATE customcrafting_recipes SET rData=? WHERE rNamespace=? AND rKey=?");
                pState.setString(1, data.toString());
                pState.setString(2, data.getNamespace());
                pState.setString(3, data.getName());
                if (async) {
                    dataBase.executeUpdate(pState);
                } else {
                    pState.executeUpdate();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            addRecipe(data, async);
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
            pState.setString(1, data.getNamespace());
            pState.setString(2, data.getName());
            pState.setString(3, data.toString());
            dataBase.executeUpdate(pState);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateItem(ItemConfig data) {
        if (hasItem(data.getNamespace(), data.getName())) {
            try {
                PreparedStatement pState = dataBase.getPreparedStatement("UPDATE customcrafting_items SET rData=? WHERE rNamespace=? AND rKey=?");
                pState.setString(1, data.toString());
                pState.setString(2, data.getNamespace());
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
