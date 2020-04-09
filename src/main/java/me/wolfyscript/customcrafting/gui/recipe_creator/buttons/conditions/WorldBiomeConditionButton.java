package me.wolfyscript.customcrafting.gui.recipe_creator.buttons.conditions;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.recipes.Conditions;
import me.wolfyscript.customcrafting.recipes.conditions.WorldBiomeCondition;
import me.wolfyscript.customcrafting.recipes.types.RecipeConfig;
import me.wolfyscript.utilities.api.inventory.GuiHandler;
import me.wolfyscript.utilities.api.inventory.GuiWindow;
import me.wolfyscript.utilities.api.inventory.button.ButtonActionRender;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.buttons.ActionButton;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Locale;

public class WorldBiomeConditionButton extends ActionButton {

    public WorldBiomeConditionButton() {
        super("conditions.world_biome", new ButtonState("world_biome", Material.SAND, new ButtonActionRender() {
            @Override
            public boolean run(GuiHandler guiHandler, Player player, Inventory inventory, int slot, InventoryClickEvent event) {
                GuiWindow window = guiHandler.getCurrentInv();
                RecipeConfig recipeConfig = ((TestCache) guiHandler.getCustomCache()).getRecipeConfig();
                Conditions conditions = recipeConfig.getConditions();
                if (event.getClick().isRightClick()) {
                    //Change Mode
                    conditions.getByID("world_biome").toggleOption();
                    recipeConfig.setConditions(conditions);
                } else if (!event.isShiftClick()) {
                    //CONFIGURE ELITE WORKBENCHES
                    guiHandler.getCurrentInv().openChat("world_biome", guiHandler, (guiHandler1, player1, s, args) -> {
                        if (!s.isEmpty()) {
                            try {
                                Biome biome = Biome.valueOf(s.toUpperCase(Locale.ROOT));
                                WorldBiomeCondition condition = (WorldBiomeCondition) conditions.getByID("world_biome");
                                if (condition.getBiomes().contains(biome.toString())) {
                                    window.sendMessage(player1, "already_existing");
                                    return true;
                                }
                                ((WorldBiomeCondition) conditions.getByID("world_biome")).addBiome(biome.toString());
                                recipeConfig.setConditions(conditions);
                                return false;
                            } catch (IllegalArgumentException ex) {
                                window.sendMessage(player1, "invalid_biome");
                            }
                        }
                        return true;
                    });
                } else {
                    if (((WorldBiomeCondition) conditions.getByID("world_biome")).getBiomes().size() > 0) {
                        ((WorldBiomeCondition) conditions.getByID("world_biome")).getBiomes().remove(((WorldBiomeCondition) conditions.getByID("world_biome")).getBiomes().size() - 1);
                        recipeConfig.setConditions(conditions);
                    }
                    return false;
                }
                return true;
            }

            @Override
            public ItemStack render(HashMap<String, Object> hashMap, GuiHandler guiHandler, Player player, ItemStack itemStack, int slot, boolean b) {
                WorldBiomeCondition condition = (WorldBiomeCondition) ((TestCache) guiHandler.getCustomCache()).getRecipeConfig().getConditions().getByID("world_biome");
                hashMap.put("%MODE%", condition.getOption().getDisplayString(CustomCrafting.getApi()));
                for (int i = 0; i < 4; i++) {
                    if (i < condition.getBiomes().size()) {
                        hashMap.put("%var" + i + "%", condition.getBiomes().get(i));
                    } else {
                        hashMap.put("%var" + i + "%", "...");
                    }
                }
                return itemStack;
            }
        }));
    }
}
