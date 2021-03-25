package me.wolfyscript.customcrafting.utils.recipe_item.extension;

import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.mobs.MythicMob;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class MythicMobResultExtension extends ResultExtension {

    private String mobName;
    private int mobLevel;
    private Vector offset = new Vector(0.5, 1, 0.5);

    public MythicMobResultExtension() {
        super(new NamespacedKey("customcrafting", "mythicmobs/mob_spawn"));
    }

    public MythicMobResultExtension(MythicMobResultExtension extension) {
        super(extension);
        this.mobName = extension.mobName;
        this.mobLevel = extension.mobLevel;
        this.offset = extension.offset;
    }

    public MythicMobResultExtension(String mobName, int mobLevel) {
        this();
        this.mobName = mobName;
        this.mobLevel = mobLevel;
    }

    public MythicMobResultExtension(String mobName, int mobLevel, Vector offset) {
        this();
        this.mobName = mobName;
        this.mobLevel = mobLevel;
        this.offset = offset;
    }

    @Override
    public void onWorkstation(Block block, @Nullable Player player) {
        spawnMob(block.getLocation());
    }

    @Override
    public void onLocation(Location location, @Nullable Player player) {

    }

    @Override
    public void onPlayer(@NotNull Player player, Location location) {

    }

    @Override
    public MythicMobResultExtension clone() {
        return new MythicMobResultExtension(this);
    }

    protected void spawnMob(Location origin) {
        if (WolfyUtilities.hasMythicMobs()) {
            MythicMob mythicMob = MythicMobs.inst().getMobManager().getMythicMob(mobName);
            if (mythicMob != null) {
                origin.add(offset);
                mythicMob.spawn(BukkitAdapter.adapt(origin), mobLevel);
            }
        }
    }
}
