package me.wolfyscript.customcrafting.gui.recipe_creator;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.recipes.CustomRecipeAnvil;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ChatInputButton;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ToggleButton;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class RecipeCreatorAnvil extends RecipeCreator {

    public static final String MODE = "mode";
    public static final String REPAIR_MODE = "repair_mode";
    public static final String REPAIR_APPLY = "repair_apply";
    public static final String BLOCK_REPAIR = "block_repair";
    public static final String BLOCK_RENAME = "block_rename";
    public static final String BLOCK_ENCHANT = "block_enchant";
    public static final String REPAIR_COST = "repair_cost";
    public static final String DURABILITY = "durability";

    public RecipeCreatorAnvil(GuiCluster<CCCache> cluster, CustomCrafting customCrafting) {
        super(cluster, "anvil", 54, customCrafting);
    }

    @Override
    public void onInit() {
        super.onInit();

        registerButton(new ButtonRecipeIngredient(0));
        registerButton(new ButtonRecipeIngredient(1));
        registerButton(new ButtonRecipeResult());

        registerButton(new ActionButton<>(MODE, Material.REDSTONE, (cache, guiHandler, player, inventory, slot, event) -> {
            var mode = cache.getRecipeCreatorCache().getAnvilCache().getMode();
            int id = mode.getId();
            if (id < 2) {
                id++;
            } else {
                id = 0;
            }
            cache.getRecipeCreatorCache().getAnvilCache().setMode(CustomRecipeAnvil.Mode.getById(id));
            return true;
        }, (values, cache, guiHandler, player, inventory, itemStack, slot, event) -> {
            values.put("%MODE%", cache.getRecipeCreatorCache().getAnvilCache().getMode().name());
            return itemStack;
        }));
        registerButton(new ActionButton<>(REPAIR_MODE, Material.GLOWSTONE_DUST, (cache, guiHandler, player, inventory, slot, event) -> {
            int index = CustomRecipeAnvil.RepairCostMode.getModes().indexOf(cache.getRecipeCreatorCache().getAnvilCache().getRepairCostMode()) + 1;
            cache.getRecipeCreatorCache().getAnvilCache().setRepairCostMode(CustomRecipeAnvil.RepairCostMode.getModes().get(index >= CustomRecipeAnvil.RepairCostMode.getModes().size() ? 0 : index));
            return true;
        }, (values, cache, guiHandler, player, inventory, itemStack, slot, event) -> {
            values.put("%VAR%", cache.getRecipeCreatorCache().getAnvilCache().getRepairCostMode().name());
            return itemStack;
        }));
        registerButton(new ToggleButton<>(REPAIR_APPLY, new ButtonState<>("repair_apply.true", Material.GREEN_CONCRETE, (cache, guiHandler, player, inventory, slot, event) -> {
            cache.getRecipeCreatorCache().getAnvilCache().setApplyRepairCost(false);
            return true;
        }), new ButtonState<>("repair_apply.false", Material.RED_CONCRETE, (cache, guiHandler, player, inventory, slot, event) -> {
            cache.getRecipeCreatorCache().getAnvilCache().setApplyRepairCost(true);
            return true;
        })));
        registerButton(new ToggleButton<>(BLOCK_REPAIR, false, new ButtonState<>("block_repair.true", Material.IRON_SWORD, (cache, guiHandler, player, inventory, slot, event) -> {
            cache.getRecipeCreatorCache().getAnvilCache().setBlockRepair(false);
            return true;
        }), new ButtonState<>("block_repair.false", Material.IRON_SWORD, (cache, guiHandler, player, inventory, slot, event) -> {
            cache.getRecipeCreatorCache().getAnvilCache().setBlockRepair(true);
            return true;
        })));
        registerButton(new ToggleButton<>(BLOCK_RENAME, false, new ButtonState<>("block_rename.true", Material.WRITABLE_BOOK, (cache, guiHandler, player, inventory, slot, event) -> {
            cache.getRecipeCreatorCache().getAnvilCache().setBlockRename(false);
            return true;
        }), new ButtonState<>("block_rename.false", Material.WRITABLE_BOOK, (cache, guiHandler, player, inventory, slot, event) -> {
            cache.getRecipeCreatorCache().getAnvilCache().setBlockRename(true);
            return true;
        })));
        registerButton(new ToggleButton<>(BLOCK_ENCHANT, false, new ButtonState<>("block_enchant.true", Material.ENCHANTING_TABLE, (cache, guiHandler, player, inventory, slot, event) -> {
            cache.getRecipeCreatorCache().getAnvilCache().setBlockEnchant(false);
            return true;
        }), new ButtonState<>("block_enchant.false", Material.ENCHANTING_TABLE, (cache, guiHandler, player, inventory, slot, event) -> {
            cache.getRecipeCreatorCache().getAnvilCache().setBlockEnchant(true);
            return true;
        })));
        registerButton(new ChatInputButton<>(REPAIR_COST, Material.EXPERIENCE_BOTTLE, (values, cache, guiHandler, player, inventory, itemStack, slot, event) -> {
            values.put("%VAR%", cache.getRecipeCreatorCache().getAnvilCache().getRepairCost());
            return itemStack;
        }, (guiHandler, player, s, args) -> {
            int repairCost;
            try {
                repairCost = Math.max(1, Integer.parseInt(args[0]));
            } catch (NumberFormatException e) {
                api.getChat().sendKey(player, getCluster(), "valid_number");
                return true;
            }
            guiHandler.getCustomCache().getRecipeCreatorCache().getAnvilCache().setRepairCost(repairCost);
            return false;
        }));
        registerButton(new ChatInputButton<>(DURABILITY, Material.IRON_SWORD, (values, cache, guiHandler, player, inventory, itemStack, slot, event) -> {
            values.put("%VAR%", cache.getRecipeCreatorCache().getAnvilCache().getDurability());
            return itemStack;
        }, (guiHandler, player, s, args) -> {
            int durability;
            try {
                durability = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                api.getChat().sendKey(player, getCluster(), "valid_number");
                return true;
            }
            guiHandler.getCustomCache().getRecipeCreatorCache().getAnvilCache().setDurability(durability);
            return false;
        }));
    }

    @Override
    public void onUpdateAsync(GuiUpdate<CCCache> event) {
        super.onUpdateAsync(event);
        CCCache cache = event.getGuiHandler().getCustomCache();
        event.setButton(0, BACK);
        var anvilCache = cache.getRecipeCreatorCache().getAnvilCache();
        event.setButton(1, ClusterRecipeCreator.HIDDEN);
        event.setButton(3, ClusterRecipeCreator.CONDITIONS);
        event.setButton(5, ClusterRecipeCreator.PRIORITY);
        event.setButton(7, ClusterRecipeCreator.EXACT_META);
        event.setButton(19, "recipe.ingredient_0");
        event.setButton(21, "recipe.ingredient_1");
        if (anvilCache.getMode().equals(CustomRecipeAnvil.Mode.RESULT)) {
            event.setButton(25, "recipe.result");
        } else if (anvilCache.getMode().equals(CustomRecipeAnvil.Mode.DURABILITY)) {
            event.setButton(25, DURABILITY);
        } else {
            event.setItem(25, new ItemStack(Material.BARRIER));
        }
        event.setButton(23, MODE);
        event.setButton(36, BLOCK_ENCHANT);
        event.setButton(37, BLOCK_RENAME);
        event.setButton(38, BLOCK_REPAIR);
        event.setButton(40, REPAIR_APPLY);
        event.setButton(41, REPAIR_COST);
        event.setButton(42, REPAIR_MODE);

        event.setButton(51, ClusterRecipeCreator.GROUP);
        if (anvilCache.isSaved()) {
            event.setButton(52, ClusterRecipeCreator.SAVE);
        }
        event.setButton(53, ClusterRecipeCreator.SAVE_AS);
    }

}
