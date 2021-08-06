package me.wolfyscript.customcrafting.recipes.conditions;

import me.wolfyscript.customcrafting.recipes.ICraftingRecipe;
import me.wolfyscript.customcrafting.recipes.ICustomRecipe;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ChatInputButton;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.annotation.JsonIgnore;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
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
    public boolean isApplicable(ICustomRecipe<?> recipe) {
        return recipe instanceof ICraftingRecipe;
    }

    @Override
    public boolean check(ICustomRecipe<?> recipe, Conditions.Data data) {
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
            super(Material.CLOCK, "Craft Delay", List.of("Set a delay in which the recipe", "cannot be crafted/processed!"),
                    (menu, api) -> {
                        menu.registerButton(new ChatInputButton<>("conditions.craft_delay", Material.CLOCK, (hashMap, cache, guiHandler, player, guiInventory, itemStack, i, b) -> {
                            hashMap.put("%VALUE%", cache.getRecipe().getConditions().getByType(CraftDelayCondition.class).getDelay());
                            return itemStack;
                        }, (guiHandler, player, s, strings) -> {
                            var conditions = guiHandler.getCustomCache().getRecipe().getConditions();
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
                        update.setButton(31, "conditions.craft_delay");
                    });
        }
    }
}
