package me.wolfyscript.customcrafting.data;

import me.wolfyscript.customcrafting.gui.Setting;
import me.wolfyscript.customcrafting.items.CustomItem;
import me.wolfyscript.customcrafting.items.ItemUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class PlayerCache {

    private UUID uuid;

    private static char[] letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    private HashMap<String, Object> CACHE = new HashMap<>();

    public PlayerCache(UUID uuid) {
        this.uuid = uuid;
        setSetting(Setting.MAIN_MENU);
        ItemStack placeHolder = new ItemStack(Material.AIR);

        setRecipeListSetting("");
        setSubSetting("");

        CACHE.put("craft", new HashMap<String, Object>());
        CACHE.put("furnace", new HashMap<String, Object>());
        CACHE.put("item", new HashMap<String, Object>());

        setCustomItem(new CustomItem(Material.AIR));
        setItemName(true);
        setItemTag("null","null","null");

        setCraftIngredients(new HashMap<>());
        setCraftIngredients(Arrays.asList(new ItemStack(Material.AIR),new ItemStack(Material.AIR),new ItemStack(Material.AIR),new ItemStack(Material.AIR),new ItemStack(Material.AIR),new ItemStack(Material.AIR),new ItemStack(Material.AIR),new ItemStack(Material.AIR),new ItemStack(Material.AIR)));
        setCraftResult(new CustomItem(Material.AIR));
        setPermission(true);
        setWorkbench(true);
        setShape(true);
        setRecipeTag("not_saved;null");

        setAdvancedFurnace(true);
        setCookingTime(20);
        setXP(5.0f);
        setFurnaceSource(new CustomItem(Material.AIR));
        setFurnaceResult(new CustomItem(Material.AIR));

        CACHE.put("stats", new HashMap<String, Object>());
        setAmountCrafted(0);
        setAmountAvancedCrafted(0);

    }

    public PlayerCache(UUID uuid, HashMap<String, Object> stats){
        this(uuid);
        setStats(stats);
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

    //Player Stats
    public HashMap<String, Object> getStats(){
        return (HashMap<String, Object>) getObject("stats");
    }

    public void setStats(HashMap<String, Object> stats){
        setObject("stats",stats);
    }

    public void addAmountCrafted(int amount){
        setAmountCrafted(getAmountCrafted()+amount);
    }

    public void setAmountCrafted(int amount){
        setObject("stats", "amount_crafted", amount);
    }

    public int getAmountCrafted(){
        return (int) getObject("stats","amount_crafted");
    }

    public void addAmountAdvancedCrafted(int amount){
        setAmountAvancedCrafted(getAmountAdvancedCrafted()+amount);
    }

    public void setAmountAvancedCrafted(int amount){
        setObject("stats", "amount_advanced_crafted", amount);
    }

    public int getAmountAdvancedCrafted(){
        return (int) getObject("stats","amount_advanced_crafted");
    }

    public void addAmountNormalCrafted(int amount){
        setAmountNormalCrafted(getAmountNormalCrafted()+amount);
    }

    public void setAmountNormalCrafted(int amount){
        setObject("stats", "amount_normal_crafted", amount);
    }

    public int getAmountNormalCrafted(){
        return (int) getObject("stats","amount_normal_crafted");
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

    //TAG for RECIPES <saved>;<recipe_id>
    public void setRecipeTag(String tag){
        setObject("recipe", "tag");
    }

    public String getRecipetag(){
        return (String) getObject("recipe", "tag");
    }

    public String[] getRecipeTags(){
        return getRecipetag().split(";");
    }

    public String getRecipeTag(int index){
        return getRecipeTags()[index];
    }


    //Craft Recipe Cache
    public HashMap<Character, List<CustomItem>> getCraftIngredients() {
        return (HashMap<Character, List<CustomItem>>) getObject("craft", "ingredients");
    }

    public List<CustomItem> getCraftIngredients(char key){
        return getCraftIngredients().getOrDefault(key, new ArrayList<>());
    }

    public List<CustomItem> getCraftIngredients(int slot){
        return getCraftIngredients().get(letters[slot]);
    }

    public CustomItem getCraftIngredient(char key){
        return getCraftIngredients(key).get(0);
    }

    public CustomItem getCraftIngredient(int slot){
        return getCraftIngredients(slot).get(0);
    }

    public void setCraftIngredients(HashMap<Character, List<CustomItem>> ingredients) {
        setObject("craft", "ingredients", ingredients);
    }

    public void setCraftIngredients(List<ItemStack> ingredients) {
        for(int i = 0; i < ingredients.size(); i++){
            setCraftIngredient(i, ItemUtils.getCustomItem(ingredients.get(i)));
        }
    }

    public void setCraftIngredient(char key, CustomItem itemStack){
        setCraftIngredient(key, 0, itemStack);
    }

    public void setCraftIngredient(char key, int variant, CustomItem itemStack){
        List<CustomItem> ingredient = getCraftIngredients(key);
        if(variant < ingredient.size())
            ingredient.set(variant, itemStack);
        else
            ingredient.add(itemStack);
        getCraftIngredients().put(key, ingredient);
    }

    public void addCraftIngredient(char key, CustomItem itemStack){
        List<CustomItem> ingredient = getCraftIngredients(key);
        ingredient.add(itemStack);
        getCraftIngredients().put(key, ingredient);
    }

    public void addCraftIngredient(int slot, CustomItem itemStack){
        addCraftIngredient(letters[slot], itemStack);
    }

    public void setCraftIngredient(int slot, CustomItem itemStack){
        setCraftIngredient(letters[slot], itemStack);
    }

    public CustomItem getCraftResult() {
        return (CustomItem) getObject("craft", "result");
    }

    public void setCraftResult(CustomItem result) {
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
    public void setAdvancedFurnace(boolean furnace){
        setObject("furnace","advanced_furnace", furnace);
    }

    public boolean needsAdvFurnace(){
        return (boolean) getObject("furnace","advanced_furnace");
    }

    public void setCookingTime(int time){
        setObject("furnace","cooking_time", time);
    }

    public int getCookingTime(){
        return (int) getObject("furnace","cooking_time");
    }

    public void setXP(float xp){
        setObject("furnace","exp", xp);
    }

    public float getXP(){
        return (float) getObject("furnace","exp");
    }

    public CustomItem getFurnaceSource() {
        return (CustomItem) getObject("furnace", "source");
    }

    public void setFurnaceSource(CustomItem source) {
        setObject("furnace", "source", source);
    }

    public CustomItem getFurnaceResult() {
        return (CustomItem) getObject("furnace", "result");
    }

    public void setFurnaceResult(CustomItem result) {
        setObject("furnace", "result", result);
    }

    //Item Creator Cache
    public CustomItem getCustomItem() {
        return (CustomItem) getObject("item", "item");
    }

    public void setCustomItem(CustomItem item) {
        setObject("item", "item", item);
    }

    public void setItemName(boolean enabled){
        setObject("item", "name", enabled);
    }

    public boolean isItemNameEnabled(){
        return ((Boolean) getObject("item","name"));
    }

    //TAG in which the current item that is edited is saved! <type:[slot]>;<is_saved>;<item_id/null>

    public void setItemTag(String type, String saved, String id){
        setObject("item", "tag", Arrays.asList(type, saved, id));
    }

    public List<String> getItemTag(){
        return (List<String>) getObject("item","tag");
    }

    public String getItemTag(int index){
        return getItemTag().get(index);
    }

}
