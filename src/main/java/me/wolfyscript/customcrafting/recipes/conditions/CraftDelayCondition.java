/*
 *       ____ _  _ ____ ___ ____ _  _ ____ ____ ____ ____ ___ _ _  _ ____
 *       |    |  | [__   |  |  | |\/| |    |__/ |__| |___  |  | |\ | | __
 *       |___ |__| ___]  |  |__| |  | |___ |  \ |  | |     |  | | \| |__]
 *
 *       CustomCrafting Recipe creation and management tool for Minecraft
 *                      Copyright (C) 2021  WolfyScript
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.wolfyscript.customcrafting.recipes.conditions;

import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.recipes.CraftingRecipe;
import me.wolfyscript.customcrafting.recipes.CustomRecipe;
import me.wolfyscript.customcrafting.recipes.RecipeType;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.lib.com.fasterxml.jackson.annotation.JsonIgnore;
import me.wolfyscript.lib.net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import me.wolfyscript.utilities.api.inventory.gui.button.CallbackButtonRender;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ChatInputButton;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class CraftDelayCondition extends Condition<CraftDelayCondition> {

    public static final NamespacedKey KEY = new NamespacedKey(NamespacedKeyUtils.NAMESPACE, "craft_delay");

    @JsonIgnore
    private final HashMap<UUID, Long> playerCraftTimeMap = new HashMap<>();

    private long delay = 0;

    public CraftDelayCondition() {
        super(KEY);
        setAvailableOptions(Conditions.Option.EXACT);
    }

    @Override
    public boolean isApplicable(CustomRecipe<?> recipe) {
        return recipe instanceof CraftingRecipe;
    }

    @Override
    public boolean check(CustomRecipe<?> recipe, Conditions.Data data) {
        Player player = data.getPlayer();
        if (player != null) {
            long timeSince = System.currentTimeMillis() - playerCraftTimeMap.getOrDefault(player.getUniqueId(), 0L);
            boolean valid = checkDelay(timeSince);
            if (valid) {
                playerCraftTimeMap.remove(player.getUniqueId());
            }
            return valid;
        }
        return true;
    }

    private boolean checkDelay(long timeSinceLastCraft) {
        return timeSinceLastCraft >= delay;
    }

    @JsonIgnore
    public void setPlayerCraftTime(Player player) {
        playerCraftTimeMap.put(player.getUniqueId(), System.currentTimeMillis());
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public static class GUIComponent extends FunctionalGUIComponent<CraftDelayCondition> {

        public GUIComponent() {
            super(Material.CLOCK, getLangKey(KEY.getKey(), "name"), getLangKey(KEY.getKey(), "description"),
                    (menu, api) -> {
                        menu.registerButton(new ChatInputButton<>("conditions.craft_delay.set", Material.CLOCK, (CallbackButtonRender<CCCache>) (cache, guiHandler, player, guiInventory, itemStack, i) -> CallbackButtonRender.UpdateResult.of(Placeholder.unparsed("value", String.valueOf(cache.getRecipeCreatorCache().getRecipeCache().getConditions().getByType(CraftDelayCondition.class).getDelay()))), (guiHandler, player, s, strings) -> {
                            var conditions = guiHandler.getCustomCache().getRecipeCreatorCache().getRecipeCache().getConditions();
                            try {
                                long value = Long.parseLong(s);
                                conditions.getByType(CraftDelayCondition.class).setDelay(value);
                            } catch (NumberFormatException ex) {
                                api.getChat().sendKey(player, "recipe_creator", "valid_number");
                            }
                            return false;
                        }));
                    },
                    (update, cache, condition, recipe) -> {
                        update.setButton(31, "conditions.craft_delay.set");
                    });
        }

        @Override
        public boolean shouldRender(RecipeType<?> type) {
            return RecipeType.Container.CRAFTING.has(type) || RecipeType.Container.ELITE_CRAFTING.has(type);
        }
    }

}
