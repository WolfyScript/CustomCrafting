package me.wolfyscript.customcrafting.gui.potion_creator;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.potions.PotionEffects;
import me.wolfyscript.customcrafting.gui.CCWindow;
import me.wolfyscript.customcrafting.gui.potion_creator.buttons.PotionEffectTypeSelectButton;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.inventory.PlayerHeadUtils;
import org.bukkit.potion.PotionEffectType;

public class PotionEffectTypeSelection extends CCWindow {

    public PotionEffectTypeSelection(GuiCluster<CCCache> cluster, CustomCrafting customCrafting) {
        super(cluster, "potion_effect_type_selection", 54, customCrafting);
    }

    @Override
    public void onInit() {
        registerButton(new ActionButton<>("back", new ButtonState<>("none", "back", PlayerHeadUtils.getViaValue("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODY0Zjc3OWE4ZTNmZmEyMzExNDNmYTY5Yjk2YjE0ZWUzNWMxNmQ2NjllMTljNzVmZDFhN2RhNGJmMzA2YyJ9fX0="), (cache, guiHandler, player, inventory, slot, event) -> {
            PotionEffects potionEffectCache = guiHandler.getCustomCache().getPotionEffectCache();
            if (!potionEffectCache.getOpenedFromCluster().isEmpty()) {
                guiHandler.openWindow(new NamespacedKey(potionEffectCache.getOpenedFromCluster(), potionEffectCache.getOpenedFromWindow()));
            } else {
                guiHandler.openWindow(potionEffectCache.getOpenedFromWindow());
            }
            return true;
        })));

        for (PotionEffectType type : PotionEffectType.values()) {
            registerButton(new PotionEffectTypeSelectButton(type));
        }
    }

    @Override
    public void onUpdateSync(GuiUpdate<CCCache> guiUpdate) {

    }

    @Override
    public void onUpdateAsync(GuiUpdate<CCCache> update) {
        super.onUpdateAsync(update);
        update.setButton(0, "back");
        for (int i = 0; i < PotionEffectType.values().length; i++) {
            update.setButton(9 + i, "potion_effect_type_" + PotionEffectType.values()[i].getName().toLowerCase());
        }
    }
}
