package me.wolfyscript.customcrafting.gui;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class PlayerCache {

    private UUID uuid;

    private final HashMap<String, Object> CACHE = new HashMap<>();

    public PlayerCache(UUID uuid) {
        this.uuid = uuid;
        setSetting(Setting.MAIN_MENU);
        ItemStack placeHolder = new ItemStack(Material.AIR);

        setRecipeListSetting("");
        setSubSetting("");

        CACHE.put("craft", new HashMap<String, Object>());
        CACHE.put("furnace", new HashMap<String, Object>());
        CACHE.put("item", new HashMap<String, Object>());

        setCustomItem(placeHolder);
        setItemName(true);
        setItemTag("");

        setCraftIngredients(Arrays.asList(new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR)));
        setCraftResult(placeHolder);
        setPermission(true);
        setWorkbench(true);
        setShape(true);

        setFurnaceSource(placeHolder);
        setFurnaceResult(placeHolder);
    }

    public UUID getUuid() {
        return uuid;
    }

    public HashMap<String, Object> getCACHE() {
        return CACHE;
    }

    private Object getObject(String key, String subKey) {
        return ((HashMap<String, Object>) CACHE.getOrDefault(key, new HashMap<String, Object>())).get(subKey);
    }

    private void setObject(String key, String subKey, Object object) {
        HashMap<String, Object> map = (HashMap<String, Object>) CACHE.get(key);
        map.put(subKey, object);
        CACHE.put(key, map);
    }

    private Object getObject(String key) {
        return CACHE.get(key);
    }

    private void setObject(String key, Object object) {
        CACHE.put(key, object);
    }

    //Main Settings
    public Setting getSetting() {
        return (Setting) getObject("setting");
    }

    public void setSetting(Setting setting) {
        setObject("setting", setting);
    }

    //Sub-Settings for GUIs
    public String getSubSetting() {
        return (String) getObject("sub_setting");
    }

    public void setSubSetting(String setting) {
        setObject("sub_setting", setting);
    }

    //
    public void setRecipeListSetting(String setting) {
        CACHE.put("recipe_list_set", setting);
    }

    public String getRecipeListSetting() {
        return (String) CACHE.get("recipe_list_set");
    }

    //Craft Recipe Cache
    public List<ItemStack> getCraftIngredients() {
        return (List<ItemStack>) getObject("craft", "ingredients");
    }

    public void setCraftIngredients(List<ItemStack> ingredients) {
        setObject("craft", "ingredients", ingredients);
    }

    public ItemStack getCraftResult() {
        return (ItemStack) getObject("craft", "result");
    }

    public void setCraftResult(ItemStack result) {
        setObject("craft", "result", result);
    }

    public boolean getPermission() {
        return (boolean) getObject("craft", "perm");
    }

    public void setPermission(boolean perm) {
        setObject("craft", "perm", perm);
    }

    public boolean getWorkbench() {
        return (boolean) getObject("craft", "workbench");
    }

    public void setWorkbench(boolean workbench) {
        setObject("craft", "workbench", workbench);
    }

    public boolean getShape() {
        return (boolean) getObject("craft", "shape");
    }

    public void setShape(boolean workbench) {
        setObject("craft", "shape", workbench);
    }


    //Furnace Recipe Cache
    public ItemStack getFurnaceSource() {
        return (ItemStack) getObject("furnace", "source");
    }

    public void setFurnaceSource(ItemStack source) {
        setObject("furnace", "source", source);
    }

    public ItemStack getFurnaceResult() {
        return (ItemStack) getObject("furnace", "result");
    }

    public void setFurnaceResult(ItemStack source) {
        setObject("furnace", "result", source);
    }


    //Item Creator Cache
    public ItemStack getCustomItem() {
        return (ItemStack) getObject("item", "item");
    }

    public void setCustomItem(ItemStack item) {
        setObject("item", "item", item);
    }

    public void setItemName(boolean enabled){
        setObject("item", "name", enabled);
    }

    public boolean isItemNameEnabled(){
        return ((Boolean) getObject("item","name"));
    }

    public void setItemTag(String tag){
        setObject("item", "tag", tag);
    }

    public String getItemTag(){
        return (String) getObject("item","tag");
    }

}
