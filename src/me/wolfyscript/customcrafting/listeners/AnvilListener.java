package me.wolfyscript.customcrafting.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;

public class AnvilListener implements Listener {

    @EventHandler
    public void onCheck(PrepareAnvilEvent event) {
        /*
        Player player = (Player) event.getView().getPlayer();
        AnvilInventory inventory = event.getInventory();
        List<CustomAnvilRecipe> recipes = CustomCrafting.getRecipeHandler().getAnvilRecipes();

        for (CustomAnvilRecipe recipe : recipes) {
            CustomPrepareAnvilEvent anvilEvent = new CustomPrepareAnvilEvent(event.getView(), event.getResult(), recipe);


            boolean left = true;
            if (recipe.hasInputLeft()) {
                if (inventory.getItem(0) != null) {



                } else {
                    left = false;
                }
            }
            boolean right = true;
            if (recipe.hasInputRight() && left) {
                if (inventory.getItem(1) != null) {

                } else {
                    right = false;
                }
            }

            if (!left || !right) {
                anvilEvent.setCancelled(true);
            }
            Bukkit.getPluginManager().callEvent(anvilEvent);

            if (anvilEvent.isCancelled()) {
                continue;
            }

            //RECIPE RESULTS!
            if (recipe.getMode() == 0) {


            }


        }
        */
    }
}
