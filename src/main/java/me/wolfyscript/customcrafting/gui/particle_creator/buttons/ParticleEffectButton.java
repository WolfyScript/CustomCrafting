package me.wolfyscript.customcrafting.gui.particle_creator.buttons;

import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.data.cache.ParticleCache;
import me.wolfyscript.customcrafting.data.cache.items.Items;
import me.wolfyscript.utilities.api.inventory.GuiHandler;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.buttons.ActionButton;
import me.wolfyscript.utilities.api.utils.NamespacedKey;
import me.wolfyscript.utilities.api.utils.Pair;
import me.wolfyscript.utilities.api.utils.particles.ParticleEffect;
import org.bukkit.Material;

import java.util.HashMap;

public class ParticleEffectButton extends ActionButton {

    private HashMap<GuiHandler, Pair<NamespacedKey, ParticleEffect>> particleEffects = new HashMap<>();

    public ParticleEffectButton(int slot) {
        super("particle_effect.slot" + slot, new ButtonState("particle_effect.select", Material.BARRIER));
        set();
    }

    private void set() {
        getState().setAction((guiHandler, player, inventory, i, event) -> {
            Items items = ((TestCache) guiHandler.getCustomCache()).getItems();
            ParticleCache particleCache = ((TestCache) guiHandler.getCustomCache()).getParticleCache();
            if (particleCache.getAction() != null && event.isRightClick()) {
                Pair<NamespacedKey, ParticleEffect> particleEffect = getParticleEffect(guiHandler);
                items.getItem().getParticleContent().put(particleCache.getAction(), particleEffect.getKey());
            }
            return true;
        });
        getState().setRenderAction((hashMap, guiHandler, player, itemStack, i, b) -> {
            Pair<NamespacedKey, ParticleEffect> particleEffectData = getParticleEffect(guiHandler);
            ParticleEffect particleEffect = particleEffectData.getValue();
            if (particleEffect != null) {
                itemStack.setType(particleEffect.getIcon());
                hashMap.put("%effect_name%", particleEffect.getName());
                hashMap.put("%effect_description%", particleEffect.getDescription());
            } else {
                itemStack.setType(Material.AIR);
            }
            return itemStack;
        });
    }

    public Pair<NamespacedKey, ParticleEffect> getParticleEffect(GuiHandler guiHandler) {
        return particleEffects.getOrDefault(guiHandler, null);
    }

    public void setParticleEffect(GuiHandler guiHandler, Pair<NamespacedKey, ParticleEffect> particleEffect) {
        particleEffects.put(guiHandler, particleEffect);
    }
}
