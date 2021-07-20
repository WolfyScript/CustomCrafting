package me.wolfyscript.customcrafting.gui.item_creator.tabs;

import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.items.Items;
import me.wolfyscript.customcrafting.gui.item_creator.ItemCreator;
import me.wolfyscript.customcrafting.gui.item_creator.buttons.OptionButton;
import me.wolfyscript.customcrafting.gui.item_creator.buttons.ParticleEffectSelectButton;
import me.wolfyscript.customcrafting.utils.NamespacedKeyUtils;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.DummyButton;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.particles.ParticleLocation;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class TabParticleEffects extends ItemCreatorTab {

    public static final String KEY = "particle_effects";

    public TabParticleEffects() {
        super(new NamespacedKey(NamespacedKeyUtils.NAMESPACE, KEY));
    }

    @Override
    public void register(ItemCreator creator, WolfyUtilities api) {
        creator.registerButton(new OptionButton(Material.FIREWORK_ROCKET, this));
        creator.registerButton(new DummyButton<>("particle_effects.head", Material.IRON_HELMET));
        creator.registerButton(new ParticleEffectSelectButton(ParticleLocation.HEAD));
        creator.registerButton(new DummyButton<>("particle_effects.chest", Material.IRON_CHESTPLATE));
        creator.registerButton(new ParticleEffectSelectButton(ParticleLocation.CHEST));
        creator.registerButton(new DummyButton<>("particle_effects.legs", Material.IRON_LEGGINGS));
        creator.registerButton(new ParticleEffectSelectButton(ParticleLocation.LEGS));
        creator.registerButton(new DummyButton<>("particle_effects.feet", Material.IRON_BOOTS));
        creator.registerButton(new ParticleEffectSelectButton(ParticleLocation.FEET));
        creator.registerButton(new DummyButton<>("particle_effects.hand", Material.IRON_SWORD));
        creator.registerButton(new ParticleEffectSelectButton(ParticleLocation.HAND));
        creator.registerButton(new DummyButton<>("particle_effects.off_hand", Material.SHIELD));
        creator.registerButton(new ParticleEffectSelectButton(ParticleLocation.OFF_HAND));
        creator.registerButton(new DummyButton<>("particle_effects.block", Material.GRASS_BLOCK));
        creator.registerButton(new ParticleEffectSelectButton(ParticleLocation.BLOCK));
    }

    @Override
    public void render(GuiUpdate<CCCache> update, CCCache cache, Items items, CustomItem customItem, ItemStack item) {
        update.setButton(28, "particle_effects.head");
        update.setButton(29, "particle_effects.chest");
        update.setButton(30, "particle_effects.legs");
        update.setButton(31, "particle_effects.feet");
        update.setButton(32, "particle_effects.hand");
        update.setButton(33, "particle_effects.off_hand");
        update.setButton(34, "particle_effects.block");

        update.setButton(37, "particle_effects.head.input");
        update.setButton(38, "particle_effects.chest.input");
        update.setButton(39, "particle_effects.legs.input");
        update.setButton(40, "particle_effects.feet.input");
        update.setButton(41, "particle_effects.hand.input");
        update.setButton(42, "particle_effects.off_hand.input");
        update.setButton(43, "particle_effects.block.input");
    }
}
