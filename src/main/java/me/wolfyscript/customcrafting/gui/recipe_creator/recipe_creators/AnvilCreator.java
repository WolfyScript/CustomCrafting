package me.wolfyscript.customcrafting.gui.recipe_creator.recipe_creators;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.gui.RecipeCreatorCluster;
import me.wolfyscript.customcrafting.gui.recipe_creator.buttons.ButtonRecipeIngredient;
import me.wolfyscript.customcrafting.gui.recipe_creator.buttons.ButtonRecipeResult;
import me.wolfyscript.customcrafting.recipes.types.anvil.CustomAnvilRecipe;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ChatInputButton;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ToggleButton;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class AnvilCreator extends RecipeCreator {

    public AnvilCreator(GuiCluster<CCCache> cluster, CustomCrafting customCrafting) {
        super(cluster, "anvil", 54, customCrafting);
    }

    @Override
    public void onInit() {
        super.onInit();

        registerButton(new ButtonRecipeIngredient(0));
        registerButton(new ButtonRecipeIngredient(1));
        registerButton(new ButtonRecipeResult());

        registerButton(new ActionButton<>("mode", Material.REDSTONE, (cache, guiHandler, player, inventory, slot, event) -> {
            CustomAnvilRecipe.Mode mode = guiHandler.getCustomCache().getAnvilRecipe().getMode();
            int id = mode.getId();
            if (id < 2) {
                id++;
            } else {
                id = 0;
            }
            guiHandler.getCustomCache().getAnvilRecipe().setMode(CustomAnvilRecipe.Mode.getById(id));
            return true;
        }, (values, cache, guiHandler, player, inventory, itemStack, slot, event) -> {
            values.put("%MODE%", guiHandler.getCustomCache().getAnvilRecipe().getMode().name());
            return itemStack;
        }));
        registerButton(new ActionButton<>("repair_mode", Material.GLOWSTONE_DUST, (cache, guiHandler, player, inventory, slot, event) -> {
            int index = CustomAnvilRecipe.RepairCostMode.getModes().indexOf(guiHandler.getCustomCache().getAnvilRecipe().getRepairCostMode()) + 1;
            guiHandler.getCustomCache().getAnvilRecipe().setRepairCostMode(CustomAnvilRecipe.RepairCostMode.getModes().get(index >= CustomAnvilRecipe.RepairCostMode.getModes().size() ? 0 : index));
            return true;
        }, (values, cache, guiHandler, player, inventory, itemStack, slot, event) -> {
            values.put("%VAR%", guiHandler.getCustomCache().getAnvilRecipe().getRepairCostMode().name());
            return itemStack;
        }));
        registerButton(new ToggleButton<>("repair_apply", new ButtonState<>("repair_apply.true", Material.GREEN_CONCRETE, (cache, guiHandler, player, inventory, slot, event) -> {
            guiHandler.getCustomCache().getAnvilRecipe().setApplyRepairCost(false);
            return true;
        }), new ButtonState<>("repair_apply.false", Material.RED_CONCRETE, (cache, guiHandler, player, inventory, slot, event) -> {
            guiHandler.getCustomCache().getAnvilRecipe().setApplyRepairCost(true);
            return true;
        })));
        registerButton(new ToggleButton<>("block_repair", false, new ButtonState<>("block_repair.true", Material.IRON_SWORD, (cache, guiHandler, player, inventory, slot, event) -> {
            guiHandler.getCustomCache().getAnvilRecipe().setBlockEnchant(false);
            return true;
        }), new ButtonState<>("block_repair.false", Material.IRON_SWORD, (cache, guiHandler, player, inventory, slot, event) -> {
            guiHandler.getCustomCache().getAnvilRecipe().setBlockEnchant(true);
            return true;
        })));
        registerButton(new ToggleButton<>("block_rename", false, new ButtonState<>("block_rename.true", Material.WRITABLE_BOOK, (cache, guiHandler, player, inventory, slot, event) -> {
            guiHandler.getCustomCache().getAnvilRecipe().setBlockRename(false);
            return true;
        }), new ButtonState<>("block_rename.false", Material.WRITABLE_BOOK, (cache, guiHandler, player, inventory, slot, event) -> {
            guiHandler.getCustomCache().getAnvilRecipe().setBlockRename(true);
            return true;
        })));
        registerButton(new ToggleButton<>("block_enchant", false, new ButtonState<>("block_enchant.true", Material.ENCHANTING_TABLE, (cache, guiHandler, player, inventory, slot, event) -> {
            guiHandler.getCustomCache().getAnvilRecipe().setBlockEnchant(false);
            return true;
        }), new ButtonState<>("block_enchant.false", Material.ENCHANTING_TABLE, (cache, guiHandler, player, inventory, slot, event) -> {
            guiHandler.getCustomCache().getAnvilRecipe().setBlockEnchant(true);
            return true;
        })));
        registerButton(new ChatInputButton<>("repair_cost", Material.EXPERIENCE_BOTTLE, (values, cache, guiHandler, player, inventory, itemStack, slot, event) -> {
            values.put("%VAR%", guiHandler.getCustomCache().getAnvilRecipe().getRepairCost());
            return itemStack;
        }, (guiHandler, player, s, args) -> {
            int repairCost;
            try {
                repairCost = Math.max(1, Integer.parseInt(args[0]));
            } catch (NumberFormatException e) {
                api.getChat().sendKey(player, getCluster(), "valid_number");
                return true;
            }
            guiHandler.getCustomCache().getAnvilRecipe().setRepairCost(repairCost);
            return false;
        }));
        registerButton(new ChatInputButton<>("durability", Material.IRON_SWORD, (values, cache, guiHandler, player, inventory, itemStack, slot, event) -> {
            values.put("%VAR%", guiHandler.getCustomCache().getAnvilRecipe().getDurability());
            return itemStack;
        }, (guiHandler, player, s, args) -> {
            int durability;
            try {
                durability = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                api.getChat().sendKey(player, getCluster(), "valid_number");
                return true;
            }
            guiHandler.getCustomCache().getAnvilRecipe().setDurability(durability);
            return false;
        }));
    }

    @Override
    public void onUpdateAsync(GuiUpdate<CCCache> event) {
        super.onUpdateAsync(event);
        CCCache cache = event.getGuiHandler().getCustomCache();
        event.setButton(0, BACK);
        CustomAnvilRecipe anvilRecipe = cache.getAnvilRecipe();
        event.setButton(1, RecipeCreatorCluster.HIDDEN);
        event.setButton(3, RecipeCreatorCluster.CONDITIONS);
        event.setButton(5, RecipeCreatorCluster.PRIORITY);
        event.setButton(7, RecipeCreatorCluster.EXACT_META);
        event.setButton(19, "recipe.ingredient_0");
        event.setButton(21, "recipe.ingredient_1");
        if (anvilRecipe.getMode().equals(CustomAnvilRecipe.Mode.RESULT)) {
            event.setButton(25, "recipe.result");
        } else if (anvilRecipe.getMode().equals(CustomAnvilRecipe.Mode.DURABILITY)) {
            event.setButton(25, "durability");
        } else {
            event.setItem(25, new ItemStack(Material.BARRIER));
        }
        event.setButton(23, "mode");
        event.setButton(36, "block_enchant");
        event.setButton(37, "block_rename");
        event.setButton(38, "block_repair");
        event.setButton(40, "repair_apply");
        event.setButton(41, "repair_cost");
        event.setButton(42, "repair_mode");

        event.setButton(51, RecipeCreatorCluster.GROUP);
        if(anvilRecipe.hasNamespacedKey()){
            event.setButton(52, RecipeCreatorCluster.SAVE);
        }
        event.setButton(53, RecipeCreatorCluster.SAVE_AS);
    }

    @Override
    public boolean validToSave(CCCache cache) {
        CustomAnvilRecipe anvilRecipe = cache.getAnvilRecipe();
        if (anvilRecipe.getInputLeft().isEmpty() && anvilRecipe.getInputRight().isEmpty())
            return false;
        return !anvilRecipe.getMode().equals(CustomAnvilRecipe.Mode.RESULT) || anvilRecipe.getResult() != null && !anvilRecipe.getResult().isEmpty();
    }
}
