/*
 *       ____ _  _ ____ ___ ____ _  _ ____ ____ ____ ____ ___ _ _  _ ____
 *       |    |  | [__   |  |  | |\/| |    |__/ |__| |___  |  | |\ | | __
 *       |___ |__| ___]  |  |__| |  | |___ |  \ |  | |     |  | | \| |__]
 *
 *       CustomCrafting Recipe creation and management tool for Minecraft
 *                      Copyright (C) 2021  WolfyScript
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.wolfyscript.customcrafting.handlers;

import me.wolfyscript.customcrafting.CCRegistry;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.recipes.CustomRecipe;
import me.wolfyscript.customcrafting.recipes.RecipeLoader;
import me.wolfyscript.customcrafting.recipes.RecipeType;
import me.wolfyscript.customcrafting.utils.ChatUtils;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.network.database.sql.SQLDataBase;
import com.fasterxml.jackson.core.JsonProcessingException;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.Registry;
import me.wolfyscript.utilities.util.json.jackson.JacksonUtil;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLDatabaseLoader extends DatabaseLoader {

    private final SQLDataBase dataBase;

    public SQLDatabaseLoader(CustomCrafting customCrafting) {
        super(customCrafting, new NamespacedKey(customCrafting, "database_loader"));
        this.dataBase = new SQLDataBase(api, config.getDatabaseHost(), config.getDatabaseSchema(), config.getDatabaseUsername(), config.getDatabasePassword(), config.getDatabasePort());
        init();
    }

    public void init() {
        try {
            Connection connection = dataBase.open();
            dataBase.executeUpdate(connection.prepareStatement("CREATE TABLE IF NOT EXISTS customcrafting_items(rNamespace VARCHAR(255) null, rKey VARCHAR(255) null, rData LONGTEXT null, constraint customcrafting_items_namespacekey UNIQUE (rNamespace, rKey));"));
            dataBase.executeUpdate(connection.prepareStatement("CREATE TABLE IF NOT EXISTS customcrafting_recipes(rNamespace VARCHAR(255) null, rKey VARCHAR(255) null, rType TINYTEXT null, rData LONGTEXT null, constraint customcrafting_items_namespacekey UNIQUE (rNamespace, rKey));"));
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dataBase.close();
        }
    }

    @Override
    public void load() {
        api.getConsole().info("- - - - [Database Storage] - - - -");
        loadItems();
        loadRecipes();
        api.getConsole().info("");
    }

    @Override
    public void save() {

    }

    @Override
    public boolean save(CustomRecipe<?> recipe) {
        updateRecipe(recipe);
        return true;
    }

    @Override
    public boolean save(CustomItem item) {
        if (item.getNamespacedKey() != null) {
            var internalKey = NamespacedKeyUtils.toInternal(item.getNamespacedKey());
            updateItem(internalKey, item);
            return true;
        }
        return false;
    }

    @Override
    public boolean delete(CustomRecipe<?> recipe) {
        removeRecipe(recipe.getNamespacedKey().getNamespace(), recipe.getNamespacedKey().getKey());
        return true;
    }

    @Override
    public boolean delete(CustomItem item) {
        if (item != null) {
            removeItem(NamespacedKeyUtils.toInternal(item.getNamespacedKey()));
            return true;
        }
        return false;
    }

    public void loadRecipes() {
        api.getConsole().info("$msg.startup.recipes.recipes$");
        try (PreparedStatement recipesQuery = dataBase.open().prepareStatement("SELECT * FROM customcrafting_recipes")) {
            ResultSet resultSet = recipesQuery.executeQuery();
            if (resultSet == null) {
                return;
            }
            while (resultSet.next()) {
                String namespace = resultSet.getString("rNamespace");
                String key = resultSet.getString("rKey");
                NamespacedKey namespacedKey = new NamespacedKey(namespace, key);
                CustomRecipe<?> recipe = getRecipe(namespacedKey);
                if (recipe != null) {
                    CCRegistry.RECIPES.register(recipe);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            dataBase.close();
        }
    }

    public void loadItems() {
        api.getConsole().info("$msg.startup.recipes.items$");
        try (PreparedStatement itemsQuery = dataBase.open().prepareStatement("SELECT * FROM customcrafting_items")) {
            ResultSet resultSet = itemsQuery.executeQuery();
            if (resultSet == null) return;
            while (resultSet.next()) {
                String namespace = resultSet.getString("rNamespace");
                String key = resultSet.getString("rKey");
                String data = resultSet.getString("rData");
                if (namespace != null && key != null && data != null && !data.equals("{}")) {
                    try {
                        Registry.CUSTOM_ITEMS.register(new NamespacedKey(customCrafting, namespace + "/" + key), JacksonUtil.getObjectMapper().readValue(data, CustomItem.class));
                    } catch (JsonProcessingException e) {
                        api.getConsole().info("Error loading item \"" + namespace + ":" + key + "\": " + e.getMessage());
                    }
                } else {
                    api.getConsole().info("Error loading item \"" + namespace + ":" + key + "\". Invalid namespacedkey or data!");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            dataBase.close();
        }
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
            PreparedStatement pState = dataBase.open().prepareStatement("SELECT rType, rData FROM customcrafting_recipes WHERE rNamespace=? AND rKey=?");
            pState.setString(1, namespacedKey.getNamespace());
            pState.setString(2, namespacedKey.getKey());
            return dataBase.executeQuery(pState);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public CustomRecipe<?> getRecipe(NamespacedKey namespacedKey) {
        ResultSet resultSet = getRecipeData(namespacedKey);
        try {
            while (resultSet.next()) {
                String typeID = resultSet.getString("rType");
                String data = resultSet.getString("rData");
                try {
                    RecipeLoader<?> loader = RecipeType.valueOf(typeID);
                    if (loader == null && RecipeType.Container.valueOf(typeID) instanceof RecipeLoader<?> recipeLoader) {
                        loader = recipeLoader;
                    }
                    if (loader != null) {
                        return loader.getInstance(namespacedKey, JacksonUtil.getObjectMapper().readTree(data));
                    }
                } catch (JsonProcessingException | NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
                    ChatUtils.sendRecipeItemLoadingError(namespacedKey.getNamespace(), namespacedKey.getKey(), typeID, e);
                }
            }
            resultSet.getStatement().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void addRecipe(CustomRecipe<?> data) {
        try {
            PreparedStatement pState = dataBase.open().prepareStatement("INSERT INTO customcrafting_recipes (rNamespace, rKey, rType, rData) VALUES (?, ?, ?, ?)");
            pState.setString(1, data.getNamespacedKey().getNamespace());
            pState.setString(2, data.getNamespacedKey().getKey());
            pState.setString(3, data.getRecipeType().getId());
            pState.setString(4, JacksonUtil.getObjectMapper().writeValueAsString(data));
            dataBase.executeAsyncUpdate(pState);
        } catch (SQLException | JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public void updateRecipe(CustomRecipe<?> data) {
        if (hasRecipe(data.getNamespacedKey())) {
            try {
                PreparedStatement pState = dataBase.open().prepareStatement("UPDATE customcrafting_recipes SET rData=? WHERE rNamespace=? AND rKey=?");
                pState.setString(1, JacksonUtil.getObjectMapper().writeValueAsString(data));
                pState.setString(2, data.getNamespacedKey().getNamespace());
                pState.setString(3, data.getNamespacedKey().getKey());
                dataBase.executeAsyncUpdate(pState);
            } catch (SQLException | JsonProcessingException e) {
                e.printStackTrace();
            }
        } else {
            addRecipe(data);
        }
    }

    public void removeRecipe(String namespace, String key) {
        try {
            PreparedStatement pState = dataBase.open().prepareStatement("DELETE FROM customcrafting_recipes WHERE rNamespace=? AND rKey=?");
            pState.setString(1, namespace);
            pState.setString(2, key);
            dataBase.executeAsyncUpdate(pState);
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
            PreparedStatement pState = dataBase.open().prepareStatement("SELECT rData FROM customcrafting_items WHERE rNamespace=? AND rKey=?");
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
            PreparedStatement pState = dataBase.open().prepareStatement("INSERT INTO customcrafting_items (rNamespace, rKey, rData) VALUES (?, ?, ?)");
            pState.setString(1, namespacedKey.getNamespace());
            pState.setString(2, namespacedKey.getKey());
            pState.setString(3, JacksonUtil.getObjectMapper().writeValueAsString(data));
            dataBase.executeAsyncUpdate(pState);
        } catch (SQLException | JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public void updateItem(NamespacedKey namespacedKey, CustomItem data) {
        if (hasItem(namespacedKey)) {
            try {
                PreparedStatement pState = dataBase.open().prepareStatement("UPDATE customcrafting_items SET rData=? WHERE rNamespace=? AND rKey=?");
                pState.setString(1, JacksonUtil.getObjectMapper().writeValueAsString(data));
                pState.setString(2, namespacedKey.getNamespace());
                pState.setString(3, namespacedKey.getKey());
                dataBase.executeAsyncUpdate(pState);
            } catch (SQLException | JsonProcessingException e) {
                e.printStackTrace();
            }
        } else {
            addItem(namespacedKey, data);
        }
    }

    public void removeItem(NamespacedKey namespacedKey) {
        try {
            PreparedStatement pState = dataBase.open().prepareStatement("DELETE FROM customcrafting_items WHERE rNamespace=? AND rKey=?");
            pState.setString(1, namespacedKey.getNamespace());
            pState.setString(2, namespacedKey.getKey());
            dataBase.executeAsyncUpdate(pState);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
