package me.wolfyscript.customcrafting.gui.item_creator.buttons;

import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.data.cache.items.Items;
import me.wolfyscript.customcrafting.data.cache.items.ItemsButtonAction;
import me.wolfyscript.utilities.api.inventory.custom_items.ParticleContent;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.api.particles.ParticleEffect;
import me.wolfyscript.utilities.api.particles.ParticleEffects;
import org.bukkit.Material;

import java.util.Locale;

public class ParticleEffectSelectButton extends ActionButton {

    public ParticleEffectSelectButton(ParticleEffect.Action action) {
        super("particle_effects." + action.toString().toLowerCase(Locale.ROOT) + ".input", new ButtonState("particle_effects.input", Material.BARRIER, (ItemsButtonAction) (cache, items, guiHandler, player, inventory, i, event) -> {
            if (event.getClick().isShiftClick()) {
                items.getItem().getParticleContent().remove(action);
            } else {
                cache.getParticleCache().setAction(action);
                guiHandler.openCluster("particle_creator");
            }
            return true;
        }, (hashMap, guiHandler, player, itemStack, i, b) -> {
            Items items = ((TestCache) guiHandler.getCustomCache()).getItems();
            ParticleContent ParticleContent = items.getItem().getParticleContent();
            ParticleEffect particleEffect = ParticleEffects.getEffect(ParticleContent.getParticleEffect(action));
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
