package me.wolfyscript.customcrafting.data.cache;

import me.wolfyscript.customcrafting.items.CustomItem;
import me.wolfyscript.customcrafting.items.ItemUtils;
import me.wolfyscript.customcrafting.recipes.RecipePriority;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.io.Serializable;
import java.util.*;

public class Workbench implements Serializable {

    private static final long serialVersionUID = 421L;
    private static final char[] LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    private HashMap<Character, ArrayList<CustomItem>> ingredients;
    private CustomItem result;
    private String extend;
    private List<String> overrides;
    private boolean advWorkbench;
    private boolean permissions;
    private boolean shapeless;
    private boolean saved;
    private String id;
    private RecipePriority priority;
    private boolean exactMeta;
    private int resultCustomAmount;
    private HashMap<Integer, Integer> ingredientsCustomAmount;

    public Workbench(){
        this.ingredients = new HashMap<>();
        setIngredients(Arrays.asList(new ItemStack(Material.AIR),new ItemStack(Material.AIR),new ItemStack(Material.AIR),new ItemStack(Material.AIR),new ItemStack(Material.AIR),new ItemStack(Material.AIR),new ItemStack(Material.AIR),new ItemStack(Material.AIR),new ItemStack(Material.AIR)));
        this.result = new CustomItem(Material.AIR);
        this.extend = "";
        this.overrides = new ArrayList<>();
        this.advWorkbench = false;
        this.permissions = true;
        this.shapeless = false;
        this.saved = false;
        this.id = "";
        this.priority = RecipePriority.NORMAL;
        this.exactMeta = true;
        this.resultCustomAmount = 1;
        this.ingredientsCustomAmount = new HashMap<>();
    }

    public HashMap<Character, ArrayList<CustomItem>> getIngredients() {
        return ingredients;
    }

    public void setIngredients(HashMap<Character, ArrayList<CustomItem>> ingredients) {
        this.ingredients = ingredients;
    }

    public ArrayList<CustomItem> getIngredients(char key){
        return getIngredients().getOrDefault(key, new ArrayList<>());
    }

    public List<CustomItem> getIngredients(int slot){
        return getIngredients().get(LETTERS[slot]);
    }

    public CustomItem getIngredient(char key){
        return getIngredients(key).get(0);
    }

    public CustomItem getIngredient(int slot){
        return getIngredients(slot).get(0);
    }

    public void setIngredients(List<ItemStack> ingredients) {
        for(int i = 0; i < ingredients.size(); i++){
            CustomItem customItem = ItemUtils.getCustomItem(ingredients.get(i));
            if(customItem.getType().equals(Material.AIR)){
                setIngredients(i, new ArrayList<>(Collections.singleton(customItem)));
            }else{
                setIngredient(i, customItem);
            }
        }
    }

    public void setIngredient(char key, CustomItem itemStack){
        setIngredient(key, 0, itemStack);
    }

    public void setIngredient(char key, int variant, CustomItem itemStack){
        ArrayList<CustomItem> ingredient = (ArrayList<CustomItem>) getIngredients(key);
        if(variant < ingredient.size())
            ingredient.set(variant, itemStack);
        else
            ingredient.add(itemStack);
        getIngredients().put(key, ingredient);
    }

    public void setIngredients(char key, ArrayList<CustomItem> ingredients){
        getIngredients().put(key, ingredients);
    }

    public void setIngredients(int slot, ArrayList<CustomItem> ingredients){
        setIngredients(LETTERS[slot], ingredients);
    }

    public void addIngredient(char key, CustomItem itemStack){
        ArrayList<CustomItem> ingredient = getIngredients(key);
        ingredient.add(itemStack);
        getIngredients().put(key, ingredient);
    }

    public void addIngredient(int slot, CustomItem itemStack){
        addIngredient(LETTERS[slot], itemStack);
    }

    public void setIngredient(int slot, CustomItem itemStack){
        setIngredient(LETTERS[slot], itemStack);
    }

    public void setResult(CustomItem result) {
        this.result = result;
    }

    public CustomItem getResult() {
        return result;
    }

    public void setAdvWorkbench(boolean advWorkbench) {
        this.advWorkbench = advWorkbench;
    }

    public boolean isAdvWorkbench() {
        return advWorkbench;
    }

    public void setPermissions(boolean permissions) {
        this.permissions = permissions;
    }

    public boolean isPermissions() {
        return permissions;
    }

    public void setShapeless(boolean shapeless) {
        this.shapeless = shapeless;
    }

    public boolean isShapeless() {
        return shapeless;
    }

    public void setSaved(boolean saved) {
        this.saved = saved;
    }

    public boolean isSaved() {
        return saved;
    }

    public void setOverride(String extend) {
        this.extend = extend;
    }

    public String getOverride() {
        return extend;
    }

    public void setOverrides(List<String> overrides) {
        this.overrides = overrides;
    }

    public List<String> getOverrides() {
        return overrides;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public RecipePriority getPriority() {
        return priority;
    }

    public void setPriority(RecipePriority priority) {
        this.priority = priority;
    }

    public boolean isExactMeta() {
        return exactMeta;
    }

    public void setExactMeta(boolean exactMeta) {
        this.exactMeta = exactMeta;
    }

    public int getResultCustomAmount() {
        return resultCustomAmount;
    }

    public void setResultCustomAmount(int resultCustomAmount) {
        this.resultCustomAmount = resultCustomAmount;
    }

    public HashMap<Integer, Integer> getIngredientsCustomAmount() {
        return ingredientsCustomAmount;
    }

    public void setIngredientsCustomAmount(HashMap<Integer, Integer> ingredientsCustomAmount) {
        this.ingredientsCustomAmount = ingredientsCustomAmount;
    }
}
