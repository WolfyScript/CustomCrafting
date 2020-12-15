package me.wolfyscript.customcrafting.gui.recipe_creator.recipe_creators;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.gui.recipe_creator.buttons.AnvilContainerButton;
import me.wolfyscript.customcrafting.gui.recipe_creator.buttons.ExactMetaButton;
import me.wolfyscript.customcrafting.gui.recipe_creator.buttons.PriorityButton;
import me.wolfyscript.customcrafting.recipes.types.anvil.CustomAnvilRecipe;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ChatInputButton;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ToggleButton;
import me.wolfyscript.utilities.util.inventory.InventoryUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class AnvilCreator extends RecipeCreator {

    public AnvilCreator(GuiCluster<TestCache> cluster, CustomCrafting customCrafting) {
        super(cluster, "anvil", 45, customCrafting);
    }

    @Override
    public void onInit() {
        super.onInit();

        registerButton(new ExactMetaButton());
        registerButton(new PriorityButton());

        registerButton(new AnvilContainerButton(0, customCrafting));
        registerButton(new AnvilContainerButton(1, customCrafting));
        registerButton(new AnvilContainerButton(2, customCrafting));

        registerButton(new ActionButton("mode", Material.REDSTONE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            CustomAnvilRecipe.Mode mode = ((TestCache) guiHandler.getCustomCache()).getAnvilRecipe().getMode();
            int id = mode.getId();
            if (id < 2) {
                id++;
            } else {
                id = 0;
            }
            ((TestCache) guiHandler.getCustomCache()).getAnvilRecipe().setMode(CustomAnvilRecipe.Mode.getById(id));
            return true;
        }, (hashMap, guiHandler, player, itemStack, i, b) -> {
            hashMap.put("%MODE%", ((TestCache) guiHandler.getCustomCache()).getAnvilRecipe().getMode().name());
            return itemStack;
        }));
        registerButton(new ActionButton("repair_mode", Material.GLOWSTONE_DUST, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            int index = CustomAnvilRecipe.RepairCostMode.getModes().indexOf(((TestCache) guiHandler.getCustomCache()).getAnvilRecipe().getRepairCostMode()) + 1;
            ((TestCache) guiHandler.getCustomCache()).getAnvilRecipe().setRepairCostMode(CustomAnvilRecipe.RepairCostMode.getModes().get(index >= CustomAnvilRecipe.RepairCostMode.getModes().size() ? 0 : index));
            return true;
        }, (hashMap, guiHandler, player, itemStack, i, b) -> {
            hashMap.put("%VAR%", ((TestCache) guiHandler.getCustomCache()).getAnvilRecipe().getRepairCostMode().name());
            return itemStack;
        }));
        registerButton(new ToggleButton("repair_apply", new ButtonState("repair_apply.true", Material.GREEN_CONCRETE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).getAnvilRecipe().setApplyRepairCost(false);
            return true;
        }), new ButtonState("repair_apply.false", Material.RED_CONCRETE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).getAnvilRecipe().setApplyRepairCost(true);
            return true;
        })));
        registerButton(new ToggleButton("block_repair", false, new ButtonState("block_repair.true", Material.IRON_SWORD, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).getAnvilRecipe().setBlockEnchant(false);
            return true;
        }), new ButtonState("block_repair.false", Material.IRON_SWORD, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).getAnvilRecipe().setBlockEnchant(true);
            return true;
        })));
        registerButton(new ToggleButton("block_rename", false, new ButtonState("block_rename.true", Material.WRITABLE_BOOK, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).getAnvilRecipe().setBlockRename(false);
            return true;
        }), new ButtonState("block_rename.false", Material.WRITABLE_BOOK, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).getAnvilRecipe().setBlockRename(true);
            return true;
        })));
        registerButton(new ToggleButton("block_enchant", false, new ButtonState("block_enchant.true", Material.ENCHANTING_TABLE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).getAnvilRecipe().setBlockEnchant(false);
            return true;
        }), new ButtonState("block_enchant.false", Material.ENCHANTING_TABLE, (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).getAnvilRecipe().setBlockEnchant(true);
            return true;
        })));
        registerButton(new ChatInputButton("repair_cost", Material.EXPERIENCE_BOTTLE, (hashMap, guiHandler, player, itemStack, i, b) -> {
            hashMap.put("%VAR%", ((TestCache) guiHandler.getCustomCache()).getAnvilRecipe().getRepairCost());
            return itemStack;
        }, (guiHandler, player, s, args) -> {
            int repairCost;
            try {
                repairCost = Math.max(1, Integer.parseInt(args[0]));
            } catch (NumberFormatException e) {
                api.getChat().sendPlayerMessage(player, "recipe_creator", "valid_number");
                return true;
            }
            ((TestCache) guiHandler.getCustomCache()).getAnvilRecipe().setRepairCost(repairCost);
            return false;
        }));
        registerButton(new ChatInputButton("durability", Material.IRON_SWORD, (hashMap, guiHandler, player, itemStack, i, b) -> {
            hashMap.put("%VAR%", ((TestCache) guiHandler.getCustomCache()).getAnvilRecipe().getDurability());
            return itemStack;
        }, (guiHandler, player, s, args) -> {
            int durability;
            try {
                durability = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                api.getChat().sendPlayerMessage(player, "recipe_creator", "valid_number");
                return true;
            }
            ((TestCache) guiHandler.getCustomCache()).getAnvilRecipe().setDurability(durability);
            return false;
        }));
    }

    @Override
    public void onUpdateAsync(GuiUpdate<TestCache> event) {
        super.onUpdateAsync(event);
        TestCache cache = event.getGuiHandler().getCustomCache();
        event.setButton(0, "back");
        CustomAnvilRecipe anvilRecipe = cache.getAnvilRecipe();
        ((ToggleButton) getButton("exact_meta")).setState(event.getGuiHandler(), anvilRecipe.isExactMeta());
        ((ToggleButton) getButton("hidden")).setState(event.getGuiHandler(), anvilRecipe.isHidden());
        event.setButton(1, "hidden");
        event.setButton(3, "recipe_creator", "conditions");
        event.setButton(5, "priority");
        event.setButton(7, "exact_meta");
        event.setButton(19, "container_0");
        event.setButton(21, "container_1");
        if (anvilRecipe.getMode().equals(CustomAnvilRecipe.Mode.RESULT)) {
            event.setButton(25, "container_2");
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

        if(anvilRecipe.hasNamespacedKey()){
            event.setButton(43, "save");
        }
        event.setButton(44, "save_as");
    }

    @Override
    public boolean validToSave(TestCache cache) {
        CustomAnvilRecipe anvilRecipe = cache.getAnvilRecipe();
        if (InventoryUtils.isCustomItemsListEmpty(anvilRecipe.getInputLeft()) && InventoryUtils.isCustomItemsListEmpty(anvilRecipe.getInputRight()))
            return false;
        return !anvilRecipe.getMode().equals(CustomAnvilRecipe.Mode.RESULT) || anvilRecipe.getResults() != null && !anvilRecipe.getResults().isEmpty();
    }
}
