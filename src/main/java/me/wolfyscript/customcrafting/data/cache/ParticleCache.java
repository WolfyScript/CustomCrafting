package me.wolfyscript.customcrafting.data.cache;

import me.wolfyscript.utilities.util.particles.ParticleLocation;

public class ParticleCache {

    private int page;
    private ParticleLocation action;

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

    public ParticleLocation getAction() {
        return action;
    }

    public void setAction(ParticleLocation action) {
        this.action = action;
    }
}
