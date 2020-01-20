package me.wolfyscript.customcrafting.gui.item_creator.buttons;

import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.data.cache.items.Items;
import me.wolfyscript.utilities.api.inventory.GuiHandler;
import me.wolfyscript.utilities.api.inventory.button.ButtonActionRender;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.buttons.ActionButton;
import me.wolfyscript.utilities.api.utils.particles.ParticleEffect;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Locale;

public class ParticleEffectSelectButton extends ActionButton {

    private ParticleEffect.Action action;

    public ParticleEffectSelectButton(ParticleEffect.Action action, ButtonState state) {
        super("particle_effects." + action.toString().toLowerCase(Locale.ROOT) + ".input", new ButtonState("particle_effects." + action.toString().toLowerCase(Locale.ROOT) + ".input", Material.AIR, new ButtonActionRender() {
            @Override
            public boolean run(GuiHandler guiHandler, Player player, Inventory inventory, int i, InventoryClickEvent inventoryClickEvent) {

                guiHandler.openCluster("particle_creator");

                return false;
            }

            @Override
            public ItemStack render(HashMap<String, Object> hashMap, GuiHandler guiHandler, Player player, ItemStack itemStack, int i, boolean b) {
                Items items = ((TestCache) guiHandler.getCustomCache()).getItems();

                items.getItem().getCustomData("particle");
                //Should I put the Particle Data directly into the CustomItem?
                //I tend to da that, because it's better for compatibility when I will ad functionality to it later on!


                return itemStack;
            }
        }));
        this.action = action;
    }
}
