package me.wolfyscript.customcrafting.data.cauldron;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.listeners.customevents.CauldronPreCookEvent;
import me.wolfyscript.customcrafting.recipes.types.cauldron.CauldronRecipe;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.RandomCollection;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class Cauldron implements Listener {

    private CauldronRecipe recipe;
    private final int cookingTime;
    private int passedTicks;
    private boolean done;
    private final boolean dropItems;
    private CustomItem result;

    public Cauldron(CauldronPreCookEvent event) {
        this.recipe = event.getRecipe();

        Player player = event.getPlayer();
        RandomCollection<CustomItem> items = recipe.getResults().parallelStream().filter((item) -> !item.hasPermission() || player.hasPermission(item.getPermission())).collect(RandomCollection.getCollector((rdmC, item) -> rdmC.add(item.getRarityPercentage(), item)));
        if (!items.isEmpty()) {
            this.result = items.next();
        } else {
            this.result = new CustomItem(Material.AIR);
        }

        this.dropItems = event.dropItems();
        this.cookingTime = event.getCookingTime();
        this.passedTicks = 0;
        this.done = false;
    }

    public Cauldron(CauldronRecipe recipe, int passedTicks, int cookingTime, boolean done, boolean dropItems) {
        this.recipe = recipe;
        this.passedTicks = passedTicks;
        this.done = done;
        this.dropItems = dropItems;
        this.cookingTime = cookingTime;
    }

    public static Cauldron fromString(CustomCrafting customCrafting, String data) {
        if (data == null || data.isEmpty())
            return null;
        String[] args = data.split(";");
        CauldronRecipe recipe = (CauldronRecipe) customCrafting.getRecipeHandler().getRecipe(NamespacedKey.getByString(args[0]));
        if (recipe == null) {
            return null;
        }
        return new Cauldron(recipe, Integer.parseInt(args[1]), Integer.parseInt(args[2]), Boolean.parseBoolean(args[3]), Boolean.parseBoolean(args[4]));
    }

    @Override
    public String toString() {
        return recipe.getNamespacedKey().toString() + ";" + passedTicks + ";" + cookingTime + ";" + done + ";" + dropItems;
    }

    public void increasePassedTicks() {
        this.passedTicks++;
    }

    public void decreasePassedTicks(int amount) {
        if (this.passedTicks >= amount) {
            this.passedTicks -= amount;
        } else {
            this.passedTicks = 0;
        }
    }

    public boolean isDone() {
        return done;
    }

    public CauldronRecipe getRecipe() {
        return recipe;
    }

    public void setRecipe(CauldronRecipe recipe) {
        this.recipe = recipe;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public void setPassedTicks(int passedTicks) {
        this.passedTicks = passedTicks;
    }

    public int getCookingTime() {
        return cookingTime;
    }

    public int getPassedTicks() {
        return passedTicks;
    }

    public boolean dropItems() {
        return dropItems;
    }

    public CustomItem getResult() {
        return result;
    }

    public void setResult(CustomItem result) {
        this.result = result;
    }
}
