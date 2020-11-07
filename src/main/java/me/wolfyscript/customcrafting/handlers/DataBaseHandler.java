package me.wolfyscript.customcrafting.handlers;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.MainConfig;
import me.wolfyscript.customcrafting.recipes.types.ICustomRecipe;
import me.wolfyscript.customcrafting.recipes.types.anvil.CustomAnvilRecipe;
import me.wolfyscript.customcrafting.recipes.types.blast_furnace.CustomBlastRecipe;
import me.wolfyscript.customcrafting.recipes.types.brewing.BrewingRecipe;
import me.wolfyscript.customcrafting.recipes.types.campfire.CustomCampfireRecipe;
import me.wolfyscript.customcrafting.recipes.types.cauldron.CauldronRecipe;
import me.wolfyscript.customcrafting.recipes.types.elite_workbench.ShapedEliteCraftRecipe;
import me.wolfyscript.customcrafting.recipes.types.elite_workbench.ShapelessEliteCraftRecipe;
import me.wolfyscript.customcrafting.recipes.types.furnace.CustomFurnaceRecipe;
import me.wolfyscript.customcrafting.recipes.types.grindstone.GrindstoneRecipe;
import me.wolfyscript.customcrafting.recipes.types.smoker.CustomSmokerRecipe;
import me.wolfyscript.customcrafting.recipes.types.stonecutter.CustomStonecutterRecipe;
import me.wolfyscript.customcrafting.recipes.types.workbench.ShapedCraftRecipe;
import me.wolfyscript.customcrafting.recipes.types.workbench.ShapelessCraftRecipe;
import me.wolfyscript.customcrafting.utils.ChatUtils;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.config.ConfigAPI;
import me.wolfyscript.utilities.api.custom_items.CustomItem;
import me.wolfyscript.utilities.api.custom_items.CustomItems;
import me.wolfyscript.utilities.api.language.LanguageAPI;
import me.wolfyscript.utilities.api.utils.NamespacedKey;
import me.wolfyscript.utilities.api.utils.json.jackson.JacksonUtil;
import me.wolfyscript.utilities.api.utils.sql.SQLDataBase;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.core.JsonProcessingException;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.databind.JsonNode;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DataBaseHandler {

    private final CustomCrafting customCrafting;
    private final WolfyUtilities api;
    private final ConfigAPI configAPI;
    private final LanguageAPI languageAPI;
    private final MainConfig mainConfig;

    private final SQLDataBase dataBase;

    public DataBaseHandler(CustomCrafting customCrafting) {
        this.api = WolfyUtilities.getAPI(customCrafting);
        this.customCrafting = customCrafting;
        this.configAPI = api.getConfigAPI();
        this.languageAPI = api.getLanguageAPI();
        this.mainConfig = customCrafting.getConfigHandler().getConfig();
        this.dataBase = new SQLDataBase(api, mainConfig.getDatabankHost(), mainConfig.getDatabankDataBase(), mainConfig.getDatabankUsername(), mainConfig.getDataBankPassword(), mainConfig.getDatabankPort());
        init();
    }

    public void init() {
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
            NamespacedKey namespacedKey = new NamespacedKey(namespace, key);
            api.sendConsoleMessage("- " + namespacedKey.toString());
            ICustomRecipe recipe = getRecipe(namespacedKey);
            if (recipe != null) {
                recipeHandler.registerRecipe(recipe);
            } else {
                api.sendConsoleMessage("Error loading recipe \"" + namespacedKey.toString() + "\". Couldn't find recipe in DataBase!");
            }
        }
    }

    public void loadItems() throws SQLException {
        api.sendConsoleMessage("");
        api.sendConsoleMessage("$msg.startup.recipes.items$");
        ResultSet resultSet = getItems();
        if (resultSet == null) {
            return;
        }
        while (resultSet.next()) {
            String namespace = resultSet.getString("rNamespace");
            String key = resultSet.getString("rKey");
            String data = resultSet.getString("rData");
            if (namespace != null && key != null && data != null && !data.equals("{}")) {
                api.sendConsoleMessage("- " + namespace + ":" + key);
                try {
                    CustomItems.addCustomItem(new NamespacedKey(namespace, key), JacksonUtil.getObjectMapper().readValue(data, CustomItem.class));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            } else {
                api.sendConsoleMessage("Error loading item \"" + namespace + ":" + key + "\". Invalid namespacekey or data!");
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

    public boolean hasRecipe(NamespacedKey namespacedKey) {
        try {
            ResultSet resultSet = getRecipeData(namespacedKey);
            return resultSet.isBeforeFirst();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public ResultSet getRecipeData(NamespacedKey namespacedKey) {
        try {
            PreparedStatement pState = dataBase.getPreparedStatement("SELECT rType, rData FROM customcrafting_recipes WHERE rNamespace=? AND rKey=?");
            pState.setString(1, namespacedKey.getNamespace());
            pState.setString(2, namespacedKey.getKey());
            return pState.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ICustomRecipe getRecipe(NamespacedKey namespacedKey) {
        ResultSet resultSet = getRecipeData(namespacedKey);
        try {
            while (resultSet.next()) {
                String type = resultSet.getString("rType");
                String data = resultSet.getString("rData");
                try {
                    JsonNode node = JacksonUtil.getObjectMapper().readTree(data);
                    switch (type) {
                        case "workbench":
                            if (node.path("shapeless").asBoolean()) {
                                return new ShapelessCraftRecipe(namespacedKey, node);
                            } else {
                                return new ShapedCraftRecipe(namespacedKey, node);
                            }
                        case "elite_workbench":
                            if (node.path("shapeless").asBoolean()) {
                                return new ShapelessEliteCraftRecipe(namespacedKey, node);
                            } else {
                                return new ShapedEliteCraftRecipe(namespacedKey, node);
                            }
                        case "furnace":
                            return new CustomFurnaceRecipe(namespacedKey, node);
                        case "anvil":
                            return new CustomAnvilRecipe(namespacedKey, node);
                        case "blast_furnace":
                            return new CustomBlastRecipe(namespacedKey, node);
                        case "smoker":
                            return new CustomSmokerRecipe(namespacedKey, node);
                        case "campfire":
                            return new CustomCampfireRecipe(namespacedKey, node);
                        case "stonecutter":
                            return new CustomStonecutterRecipe(namespacedKey, node);
                        case "enchant":
                            break;
                        case "grindstone":
                            return new GrindstoneRecipe(namespacedKey, node);
                        case "cauldron":
                            return new CauldronRecipe(namespacedKey, node);
                        case "brewing":
                            return new BrewingRecipe(namespacedKey, node);
                    }
                } catch (Exception ex) {
                    ChatUtils.sendRecipeItemLoadingError(namespacedKey.getNamespace(), namespacedKey.getKey(), type, ex);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void addRecipe(ICustomRecipe data) {
        addRecipe(data, true);
    }

    public void addRecipe(ICustomRecipe data, boolean async) {
        try {
            PreparedStatement pState = dataBase.getPreparedStatement("INSERT INTO customcrafting_recipes (rNamespace, rKey, rType, rData) values (?, ?, ?, ?)");
            pState.setString(1, data.getNamespacedKey().getNamespace());
            pState.setString(2, data.getNamespacedKey().getKey());
            pState.setString(3, data.getRecipeType().getId());
            pState.setString(4, JacksonUtil.getObjectMapper().writeValueAsString(data));
            if (async) {
                dataBase.executeUpdate(pState);
            } else {
                pState.executeUpdate();
            }
        } catch (SQLException | JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public void updateRecipe(ICustomRecipe data) {
        updateRecipe(data, true);
    }

    public void updateRecipe(ICustomRecipe data, boolean async) {
        if (hasRecipe(data.getNamespacedKey())) {
            try {
                PreparedStatement pState = dataBase.getPreparedStatement("UPDATE customcrafting_recipes SET rData=? WHERE rNamespace=? AND rKey=?");
                pState.setString(1, JacksonUtil.getObjectMapper().writeValueAsString(data));
                pState.setString(2, data.getNamespacedKey().getNamespace());
                pState.setString(3, data.getNamespacedKey().getKey());
                if (async) {
                    dataBase.executeUpdate(pState);
                } else {
                    pState.executeUpdate();
                }
            } catch (SQLException | JsonProcessingException e) {
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

    public boolean hasItem(NamespacedKey namespacedKey) {
        try {
            ResultSet resultSet = getItem(namespacedKey);
            return resultSet.isBeforeFirst();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public ResultSet getItem(NamespacedKey namespacedKey) {
        try {
            PreparedStatement pState = dataBase.getPreparedStatement("SELECT rData FROM customcrafting_items WHERE rNamespace=? AND rKey=?");
            pState.setString(1, namespacedKey.getNamespace());
            pState.setString(2, namespacedKey.getKey());
            return pState.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void addItem(NamespacedKey namespacedKey, CustomItem data) {
        try {
            PreparedStatement pState = dataBase.getPreparedStatement("INSERT INTO customcrafting_items (rNamespace, rKey, rData) values (?, ?, ?)");
            pState.setString(1, namespacedKey.getNamespace());
            pState.setString(2, namespacedKey.getKey());
            pState.setString(3, JacksonUtil.getObjectMapper().writeValueAsString(data));
            dataBase.executeUpdate(pState);
        } catch (SQLException | JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public void updateItem(NamespacedKey namespacedKey, CustomItem data) {
        if (hasItem(namespacedKey)) {
            try {
                PreparedStatement pState = dataBase.getPreparedStatement("UPDATE customcrafting_items SET rData=? WHERE rNamespace=? AND rKey=?");
                pState.setString(1, JacksonUtil.getObjectMapper().writeValueAsString(data));
                pState.setString(2, namespacedKey.getNamespace());
                pState.setString(3, namespacedKey.getKey());
                dataBase.executeUpdate(pState);
            } catch (SQLException | JsonProcessingException e) {
                e.printStackTrace();
            }
        } else {
            addItem(namespacedKey, data);
        }
    }

    public void removeItem(NamespacedKey namespacedKey) {
        try {
            PreparedStatement pState = dataBase.getPreparedStatement("delete from customcrafting_items where rNamespace=? and rKey=?");
            pState.setString(1, namespacedKey.getNamespace());
            pState.setString(2, namespacedKey.getKey());
            dataBase.executeUpdate(pState);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Deprecated
    public boolean hasRecipe(String namespace, String key) {
        return hasRecipe(new NamespacedKey(namespace, key));
    }

    @Deprecated
    public ResultSet getRecipeData(String namespace, String key) {
        return getRecipeData(new NamespacedKey(namespace, key));
    }

    @Deprecated
    public ICustomRecipe getRecipe(String namespace, String key) {
        return getRecipe(new NamespacedKey(namespace, key));
    }

    @Deprecated
    public boolean hasItem(String namespace, String key) {
        return hasItem(new NamespacedKey(namespace, key));
    }

    @Deprecated
    public ResultSet getItem(String namespace, String key) {
        return getItem(new NamespacedKey(namespace, key));
    }

    @Deprecated
    public void removeItem(String namespace, String key) {
        removeItem(new NamespacedKey(namespace, key));
    }

}
