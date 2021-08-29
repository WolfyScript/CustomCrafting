package me.wolfyscript.customcrafting.gui.item_creator;

import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.items.Items;
import me.wolfyscript.customcrafting.data.cache.items.ItemsButtonAction;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.util.Registry;
import me.wolfyscript.utilities.util.particles.ParticleEffect;
import me.wolfyscript.utilities.util.particles.ParticleLocation;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Locale;

public class ButtonParticleEffectSelect extends ActionButton<CCCache> {

    public ButtonParticleEffectSelect(ParticleLocation action) {
        super("particle_effects." + action.toString().toLowerCase(Locale.ROOT) + ".input", new ButtonState<>("particle_effects.input", Material.BARRIER, (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
            if (event instanceof InventoryClickEvent clickEvent) {
                if (clickEvent.getClick().isShiftClick()) {
                    items.getItem().getParticleContent().remove(action);
                } else {
                    cache.getParticleCache().setAction(action);
                    guiHandler.openCluster("particle_creator");
                }
            }
            return true;
        }, (hashMap, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
            Items items = guiHandler.getCustomCache().getItems();
            ParticleEffect particleEffect = Registry.PARTICLE_EFFECTS.get(items.getItem().getParticleContent().getParticleEffect(action));
            if (particleEffect != null) {
                itemStack.setType(particleEffect.getIcon());
                hashMap.put("%effect_name%", particleEffect.getName());
                hashMap.put("%effect_description%", particleEffect.getDescription());
            } else {
                itemStack.setType(Material.AIR);
            }
            return itemStack;
        }));
    }
}
