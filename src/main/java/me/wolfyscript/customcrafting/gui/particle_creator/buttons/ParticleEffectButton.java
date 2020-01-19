package me.wolfyscript.customcrafting.gui.particle_creator.buttons;

import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.GuiHandler;
import me.wolfyscript.utilities.api.inventory.GuiWindow;
import me.wolfyscript.utilities.api.inventory.button.Button;
import me.wolfyscript.utilities.api.inventory.button.ButtonType;
import me.wolfyscript.utilities.api.utils.particles.ParticleEffect;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class ParticleEffectButton extends Button {

    private HashMap<GuiHandler, ParticleEffect> particleEffects = new HashMap<>();

    public ParticleEffectButton(int slot) {
        super("particle_effect.slot" + slot, ButtonType.NORMAL);
    }

    @Override
    public void init(GuiWindow guiWindow) {

    }

    @Override
    public void init(String s, WolfyUtilities wolfyUtilities) {

    }

    @Override
    public boolean execute(GuiHandler guiHandler, Player player, Inventory inventory, int i, InventoryClickEvent inventoryClickEvent) {
        return true;
    }

    @Override
    public void render(GuiHandler guiHandler, Player player, Inventory inventory, int i, boolean b) {
        ParticleEffect particleEffect = getParticleEffect(guiHandler);


        if (particleEffect != null) {
            inventory.setItem(i, particleEffect.getIconItem());
        } else {
            inventory.setItem(i, new ItemStack(Material.AIR));
        }

    }

    public ParticleEffect getParticleEffect(GuiHandler guiHandler) {
        return particleEffects.getOrDefault(guiHandler, null);
    }

    public void setParticleEffect(GuiHandler guiHandler, ParticleEffect particleEffect) {
        particleEffects.put(guiHandler, particleEffect);
    }
}
