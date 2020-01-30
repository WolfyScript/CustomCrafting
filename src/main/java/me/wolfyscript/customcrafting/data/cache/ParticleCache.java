package me.wolfyscript.customcrafting.data.cache;

import me.wolfyscript.utilities.api.utils.particles.ParticleEffect;

public class ParticleCache {

    private int page;
    private ParticleEffect.Action action;

    public ParticleCache() {
        this.page = 0;
        this.action = null;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public ParticleEffect.Action getAction() {
        return action;
    }

    public void setAction(ParticleEffect.Action action) {
        this.action = action;
    }
}
