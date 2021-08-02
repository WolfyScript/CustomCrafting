package me.wolfyscript.customcrafting.gui.recipe_creator.buttons.conditions;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.recipes.ICustomRecipe;
import me.wolfyscript.customcrafting.recipes.conditions.WorldBiomeCondition;
import me.wolfyscript.utilities.api.inventory.gui.GuiWindow;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Locale;

public class WorldBiomeConditionButton extends ActionButton<CCCache> {

    public WorldBiomeConditionButton() {
        super("conditions.world_biome", new ButtonState<>("world_biome", Material.SAND, (cache, guiHandler, player, inventory, slot, event) -> {
            GuiWindow<CCCache> window = guiHandler.getWindow();
            ICustomRecipe<?> recipeConfig = guiHandler.getCustomCache().getRecipe();
            var conditions = recipeConfig.getConditions();
            if(event instanceof InventoryClickEvent){
                if (((InventoryClickEvent) event).getClick().isRightClick()) {
                    //Change Mode
                    conditions.getByType(WorldBiomeCondition.class).toggleOption();
                    recipeConfig.setConditions(conditions);
                } else if (!((InventoryClickEvent) event).isShiftClick()) {
                    //CONFIGURE ELITE WORKBENCHES
                    guiHandler.getWindow().openChat("world_biome", guiHandler, (guiHandler1, player1, s, args) -> {
                        if (!s.isEmpty()) {
                            try {
                                var biome = Biome.valueOf(s.toUpperCase(Locale.ROOT));
                                WorldBiomeCondition condition = conditions.getByType(WorldBiomeCondition.class);
                                if (condition.getBiomes().contains(biome.toString())) {
                                    window.sendMessage(player1, "already_existing");
                                    return true;
                                }
                                conditions.getByType(WorldBiomeCondition.class).addBiome(biome.toString());
                                recipeConfig.setConditions(conditions);
                                return false;
                            } catch (IllegalArgumentException ex) {
                                window.sendMessage(player1, "invalid_biome");
                            }
                        }
                        return true;
                    });
                } else {
                    if (!conditions.getByType(WorldBiomeCondition.class).getBiomes().isEmpty()) {
                        conditions.getByType(WorldBiomeCondition.class).getBiomes().remove(conditions.getByType(WorldBiomeCondition.class).getBiomes().size() - 1);
                        recipeConfig.setConditions(conditions);
                    }
                }
            }

            return true;
        }, (hashMap, cache, guiHandler, player, inventory, itemStack, slot, b) -> {
            WorldBiomeCondition condition = guiHandler.getCustomCache().getRecipe().getConditions().getByType(WorldBiomeCondition.class);
            hashMap.put("%MODE%", condition.getOption().getDisplayString(CustomCrafting.inst().getApi()));
            for (int i = 0; i < 4; i++) {
                if (i < condition.getBiomes().size()) {
                    hashMap.put("%var" + i + "%", condition.getBiomes().get(i));
                } else {
                    hashMap.put("%var" + i + "%", "...");
                }
            }
            return itemStack;
        }));
    }
}
