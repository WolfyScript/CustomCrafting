package me.wolfyscript.customcrafting.gui.particle_creator;

import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.data.cache.ParticleCache;
import me.wolfyscript.customcrafting.gui.ExtendedGuiWindow;
import me.wolfyscript.customcrafting.gui.particle_creator.buttons.ParticleEffectButton;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.GuiHandler;
import me.wolfyscript.utilities.api.inventory.GuiUpdateEvent;
import me.wolfyscript.utilities.api.inventory.InventoryAPI;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.buttons.ActionButton;
import me.wolfyscript.utilities.api.utils.NamespacedKey;
import me.wolfyscript.utilities.api.utils.particles.ParticleEffect;
import me.wolfyscript.utilities.api.utils.particles.ParticleEffects;
import org.bukkit.event.EventHandler;

import java.util.Collection;
import java.util.Map;

public class MainMenu extends ExtendedGuiWindow {

    public MainMenu(InventoryAPI inventoryAPI) {
        super("main_menu", inventoryAPI, 45);
    }

    @Override
    public void onInit() {

        registerButton(new ActionButton("next_page", new ButtonState("next_page", WolfyUtilities.getSkullViaURL("c86185b1d519ade585f184c34f3f3e20bb641deb879e81378e4eaf209287"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ParticleCache book = ((TestCache) guiHandler.getCustomCache()).getParticleCache();
            book.setPage(book.getPage() + 1);
            return true;
        })));
        registerButton(new ActionButton("previous_page", new ButtonState("previous_page", WolfyUtilities.getSkullViaURL("ad73cf66d31b83cd8b8644c15958c1b73c8d97323b801170c1d8864bb6a846d"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ParticleCache book = ((TestCache) guiHandler.getCustomCache()).getParticleCache();
            book.setPage(book.getPage() > 0 ? book.getPage() - 1 : 0);
            return true;
        })));
        for (int i = 0; i < 45; i++) {
            registerButton(new ParticleEffectButton(i));
        }
    }

    @EventHandler
    public void onUpdate(GuiUpdateEvent event){
        if(event.verify(this)) {
            GuiHandler<TestCache> guiHandler = event.getGuiHandler();
            TestCache cache = guiHandler.getCustomCache();
            ParticleCache particleCache = cache.getParticleCache();

            Map<NamespacedKey, ParticleEffect> effectsMap = ParticleEffects.getEffects();
            Collection<ParticleEffect> effects = effectsMap.values();

            int maxPages = effects.size() / 45 + (effects.size() % 45 > 0 ? 1 : 0);
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
            for (Map.Entry<NamespacedKey, ParticleEffect> entry : effectsMap.entrySet()) {
                int item = 9 + i;
                if (item < 45) {
                    ParticleEffectButton button = (ParticleEffectButton) getButton("particle_effect.slot" + item);
                    button.setParticleEffect(guiHandler, entry.getValue());
                    event.setButton(item, button);
                    i++;
                }
            }
        }
    }
}
