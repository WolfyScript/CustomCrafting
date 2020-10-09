package me.wolfyscript.customcrafting.gui.particle_creator;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.customcrafting.data.cache.ParticleCache;
import me.wolfyscript.customcrafting.gui.ExtendedGuiWindow;
import me.wolfyscript.customcrafting.gui.particle_creator.buttons.ParticleEffectButton;
import me.wolfyscript.utilities.api.inventory.GuiHandler;
import me.wolfyscript.utilities.api.inventory.GuiUpdate;
import me.wolfyscript.utilities.api.inventory.InventoryAPI;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.buttons.ActionButton;
import me.wolfyscript.utilities.api.utils.NamespacedKey;
import me.wolfyscript.utilities.api.utils.Pair;
import me.wolfyscript.utilities.api.utils.inventory.PlayerHeadUtils;
import me.wolfyscript.utilities.api.utils.particles.ParticleEffect;
import me.wolfyscript.utilities.api.utils.particles.ParticleEffects;

import java.util.Collection;
import java.util.Map;

public class MainMenu extends ExtendedGuiWindow {

    public MainMenu(InventoryAPI inventoryAPI, CustomCrafting customCrafting) {
        super("main_menu", inventoryAPI, 54, customCrafting);
    }

    @Override
    public void onInit() {
        registerButton(new ActionButton("back", new ButtonState("none", "back", PlayerHeadUtils.getViaValue("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODY0Zjc3OWE4ZTNmZmEyMzExNDNmYTY5Yjk2YjE0ZWUzNWMxNmQ2NjllMTljNzVmZDFhN2RhNGJmMzA2YyJ9fX0="), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            if (((TestCache) guiHandler.getCustomCache()).getParticleCache().getAction() != null) {
                guiHandler.openCluster("item_creator");
                ((TestCache) guiHandler.getCustomCache()).getParticleCache().setAction(null);
            } else {
                guiHandler.openCluster("none");
            }
            return true;
        })));
        registerButton(new ActionButton("next_page", PlayerHeadUtils.getViaURL("c86185b1d519ade585f184c34f3f3e20bb641deb879e81378e4eaf209287"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ParticleCache book = ((TestCache) guiHandler.getCustomCache()).getParticleCache();
            book.setPage(book.getPage() + 1);
            return true;
        }));
        registerButton(new ActionButton("previous_page",  PlayerHeadUtils.getViaURL("ad73cf66d31b83cd8b8644c15958c1b73c8d97323b801170c1d8864bb6a846d"), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ParticleCache book = ((TestCache) guiHandler.getCustomCache()).getParticleCache();
            book.setPage(book.getPage() > 0 ? book.getPage() - 1 : 0);
            return true;
        }));
        for (int i = 0; i < 45; i++) {
            registerButton(new ParticleEffectButton(i));
        }
    }

    @Override
    public void onUpdateAsync(GuiUpdate event) {
        super.onUpdateAsync(event);
        GuiHandler<TestCache> guiHandler = event.getGuiHandler(TestCache.class);
        TestCache cache = guiHandler.getCustomCache();
        ParticleCache particleCache = cache.getParticleCache();

        event.setButton(0, "back");

        Map<NamespacedKey, ParticleEffect> effectsMap = ParticleEffects.getEffects();
        Collection<ParticleEffect> effects = effectsMap.values();

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
        for (Map.Entry<NamespacedKey, ParticleEffect> entry : effectsMap.entrySet()) {
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
