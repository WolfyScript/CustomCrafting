package me.wolfyscript.customcrafting.gui.item_creator.buttons;

import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.data.cache.items.Items;
import me.wolfyscript.utilities.api.custom_items.ParticleContent;
import me.wolfyscript.utilities.api.inventory.GuiHandler;
import me.wolfyscript.utilities.api.inventory.button.ButtonActionRender;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.buttons.ActionButton;
import me.wolfyscript.utilities.api.utils.particles.ParticleEffect;
import me.wolfyscript.utilities.api.utils.particles.ParticleEffects;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Locale;

public class ParticleEffectSelectButton extends ActionButton {

    public ParticleEffectSelectButton(ParticleEffect.Action action) {
        super("particle_effects." + action.toString().toLowerCase(Locale.ROOT) + ".input", new ButtonState("particle_effects.input", Material.BARRIER, new ButtonActionRender() {
            @Override
            public boolean run(GuiHandler guiHandler, Player player, Inventory inventory, int i, InventoryClickEvent event) {
                if (event.getClick().isShiftClick()) {
                    ((TestCache) guiHandler.getCustomCache()).getItems().getItem().getParticleContent().remove(action);
                } else {
                    ((TestCache) guiHandler.getCustomCache()).getParticleCache().setAction(action);
                    guiHandler.openCluster("particle_creator");
                }
                return true;
            }

            @Override
            public ItemStack render(HashMap<String, Object> hashMap, GuiHandler guiHandler, Player player, ItemStack itemStack, int i, boolean b) {
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
            }
        }));
    }
}
