package me.wolfyscript.customcrafting.gui.recipe_creator.recipe_creators;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.gui.recipe_creator.buttons.*;
import me.wolfyscript.customcrafting.recipes.types.grindstone.GrindstoneRecipe;
import me.wolfyscript.utilities.api.inventory.GuiUpdateEvent;
import me.wolfyscript.utilities.api.inventory.InventoryAPI;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.buttons.ActionButton;
import me.wolfyscript.utilities.api.inventory.button.buttons.ChatInputButton;
import me.wolfyscript.utilities.api.inventory.button.buttons.DummyButton;
import me.wolfyscript.utilities.api.inventory.button.buttons.ToggleButton;
import me.wolfyscript.utilities.api.utils.inventory.InventoryUtils;
import me.wolfyscript.utilities.api.utils.inventory.PlayerHeadUtils;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;

public class GrindstoneCreator extends RecipeCreator {

    public GrindstoneCreator(InventoryAPI inventoryAPI, CustomCrafting customCrafting) {
        super("grindstone", inventoryAPI, 45, customCrafting);
    }

    @Override
    public void onInit() {
        registerButton(new ActionButton("back", new ButtonState("none", "back", PlayerHeadUtils.getViaValue("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODY0Zjc3OWE4ZTNmZmEyMzExNDNmYTY5Yjk2YjE0ZWUzNWMxNmQ2NjllMTljNzVmZDFhN2RhNGJmMzA2YyJ9fX0="), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            guiHandler.openCluster("none");
            return true;
        })));

        registerButton(new SaveButton());

        registerButton(new ExactMetaButton());
        registerButton(new PriorityButton());
        registerButton(new HiddenButton());

        registerButton(new GrindstoneContainerButton(0, customCrafting));
        registerButton(new GrindstoneContainerButton(1, customCrafting));
        registerButton(new GrindstoneContainerButton(2, customCrafting));

        registerButton(new DummyButton("grindstone", new ButtonState("grindstone", Material.GRINDSTONE)));

        registerButton(new ChatInputButton("xp", new ButtonState("xp", Material.EXPERIENCE_BOTTLE, (hashMap, guiHandler, player, itemStack, slot, help) -> {
            hashMap.put("%xp%", ((TestCache) guiHandler.getCustomCache()).getGrindstoneRecipe().getXp());
            return itemStack;
        }), (guiHandler, player, s, args) -> {
            float xp;
            try {
                xp = Float.parseFloat(args[0]);
            } catch (NumberFormatException e) {
                api.sendPlayerMessage(player, "recipe_creator", "valid_number");
                return true;
            }
            ((TestCache) guiHandler.getCustomCache()).getGrindstoneRecipe().setXp(xp);
            return false;
        }));
    }

    @EventHandler
    public void onUpdate(GuiUpdateEvent event) {
        if (event.verify(this)) {
            event.setButton(0, "back");
            TestCache cache = (TestCache) event.getGuiHandler().getCustomCache();
            GrindstoneRecipe grindstoneRecipe = cache.getGrindstoneRecipe();
            ((ToggleButton) event.getGuiWindow().getButton("hidden")).setState(event.getGuiHandler(), grindstoneRecipe.isHidden());

            event.setButton(1, "hidden");
            event.setButton(3, "recipe_creator", "conditions");
            event.setButton(5, "priority");
            event.setButton(7, "exact_meta");

            event.setButton(11, "grindstone.container_0");
            event.setButton(20, "grindstone");
            event.setButton(29, "grindstone.container_1");

            event.setButton(23, "xp");
            event.setButton(25, "grindstone.container_2");

            event.setButton(44, "save");
        }
    }

    @Override
    public boolean validToSave(TestCache cache) {
        GrindstoneRecipe recipe = cache.getGrindstoneRecipe();
        if (!recipe.getInputTop().isEmpty() || !recipe.getInputBottom().isEmpty()) {
            return !InventoryUtils.isCustomItemsListEmpty(recipe.getCustomResults());
        }
        return false;
    }
}
