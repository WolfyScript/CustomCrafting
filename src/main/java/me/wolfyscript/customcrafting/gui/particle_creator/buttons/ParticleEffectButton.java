package me.wolfyscript.customcrafting.gui.particle_creator.buttons;

import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.ParticleCache;
import me.wolfyscript.customcrafting.data.cache.items.Items;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.Pair;
import me.wolfyscript.utilities.util.particles.ParticleEffect;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.HashMap;

public class ParticleEffectButton extends ActionButton<CCCache> {

    private final HashMap<GuiHandler<CCCache>, Pair<NamespacedKey, ParticleEffect>> particleEffects = new HashMap<>();

    public ParticleEffectButton(int slot) {
        super("particle_effect.slot" + slot, new ButtonState<>("particle_effect.select", Material.BARRIER));
        set();
    }

    private void set() {
        getState().setAction((cache, guiHandler, player, inventory, slot, event) -> {
            Items items = guiHandler.getCustomCache().getItems();
            ParticleCache particleCache = guiHandler.getCustomCache().getParticleCache();
            if (particleCache.getAction() != null && event instanceof InventoryClickEvent && ((InventoryClickEvent) event).isRightClick()) {
                Pair<NamespacedKey, ParticleEffect> particleEffect = getParticleEffect(guiHandler);
                items.getItem().getParticleContent().put(particleCache.getAction(), particleEffect.getKey());
            }
            return true;
        });
        getState().setRenderAction((hashMap, cache, guiHandler, player, inventory, itemStack, slot, help) -> {
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

    public Pair<NamespacedKey, ParticleEffect> getParticleEffect(GuiHandler<CCCache> guiHandler) {
        return particleEffects.getOrDefault(guiHandler, null);
    }

    public void setParticleEffect(GuiHandler<CCCache> guiHandler, Pair<NamespacedKey, ParticleEffect> particleEffect) {
        particleEffects.put(guiHandler, particleEffect);
    }
}
