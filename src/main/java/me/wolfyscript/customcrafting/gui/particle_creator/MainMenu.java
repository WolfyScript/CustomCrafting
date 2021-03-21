package me.wolfyscript.customcrafting.gui.particle_creator;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.CCCache;
import me.wolfyscript.customcrafting.data.cache.ParticleCache;
import me.wolfyscript.customcrafting.gui.CCWindow;
import me.wolfyscript.customcrafting.gui.particle_creator.buttons.ParticleEffectButton;
import me.wolfyscript.utilities.api.inventory.gui.GuiCluster;
import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.gui.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.Pair;
import me.wolfyscript.utilities.util.Registry;
import me.wolfyscript.utilities.util.inventory.PlayerHeadUtils;
import me.wolfyscript.utilities.util.particles.ParticleEffect;

import java.util.Collection;
import java.util.Map;

public class MainMenu extends CCWindow {

    public MainMenu(GuiCluster<CCCache> cluster, CustomCrafting customCrafting) {
        super(cluster, "main_menu", 54, customCrafting);
    }

    @Override
    public void onInit() {
        registerButton(new ActionButton<>("back", new ButtonState<>("none", "back", PlayerHeadUtils.getViaURL("864f779a8e3ffa231143fa69b96b14ee35c16d669e19c75fd1a7da4bf306c"), (cache, guiHandler, player, inventory, slot, event) -> {
            if (guiHandler.getCustomCache().getParticleCache().getAction() != null) {
                guiHandler.openCluster("item_creator");
                guiHandler.getCustomCache().getParticleCache().setAction(null);
            } else {
                guiHandler.openCluster("none");
            }
            return true;
        })));
        registerButton(new ActionButton<>("next_page", PlayerHeadUtils.getViaURL("c86185b1d519ade585f184c34f3f3e20bb641deb879e81378e4eaf209287"), (cache, guiHandler, player, inventory, slot, event) -> {
            ParticleCache book = guiHandler.getCustomCache().getParticleCache();
            book.setPage(book.getPage() + 1);
            return true;
        }));
        registerButton(new ActionButton<>("previous_page",  PlayerHeadUtils.getViaURL("ad73cf66d31b83cd8b8644c15958c1b73c8d97323b801170c1d8864bb6a846d"), (cache, guiHandler, player, inventory, slot, event) -> {
            ParticleCache book = guiHandler.getCustomCache().getParticleCache();
            book.setPage(book.getPage() > 0 ? book.getPage() - 1 : 0);
            return true;
        }));
        for (int i = 0; i < 45; i++) {
            registerButton(new ParticleEffectButton(i));
        }
    }

    @Override
    public void onUpdateSync(GuiUpdate<CCCache> guiUpdate) {

    }

    @Override
    public void onUpdateAsync(GuiUpdate<CCCache> event) {
        super.onUpdateAsync(event);
        GuiHandler<CCCache> guiHandler = event.getGuiHandler();
        CCCache cache = guiHandler.getCustomCache();
        ParticleCache particleCache = cache.getParticleCache();

        event.setButton(0, "back");

        Collection<ParticleEffect> effects = Registry.PARTICLE_EFFECTS.values();

        int maxPages = effects.size() / 54 + (effects.size() % 54 > 0 ? 1 : 0);
        if (particleCache.getPage() >= maxPages) {
            particleCache.setPage(0);
        }
        if (particleCache.getPage() != 0) {
            event.setButton(2, "previous_page");
        }
        if (particleCache.getPage() + 1 < maxPages) {
            event.setButton(6, "next_page");
        }

        int i = 0;
        for (Map.Entry<NamespacedKey, ParticleEffect> entry : Registry.PARTICLE_EFFECTS.entrySet()) {
            int item = 9 + i;
            if (item < 54) {
                ParticleEffectButton button = (ParticleEffectButton) getButton("particle_effect.slot" + item);
                button.setParticleEffect(guiHandler, new Pair<>(entry.getKey(), entry.getValue()));
                event.setButton(item, button);
                i++;
            }
        }
    }
}
