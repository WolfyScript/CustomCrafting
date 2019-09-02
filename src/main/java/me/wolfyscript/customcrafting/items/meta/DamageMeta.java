package me.wolfyscript.customcrafting.items.meta;


import me.wolfyscript.customcrafting.items.Meta;
import me.wolfyscript.customcrafting.items.MetaSettings;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class DamageMeta extends Meta {

    public DamageMeta() {
        super("damage");
        setOption(MetaSettings.Option.EXACT);
        setAvailableOptions(MetaSettings.Option.EXACT, MetaSettings.Option.IGNORE, MetaSettings.Option.HIGHER, MetaSettings.Option.LOWER);
    }

    @Override
    public boolean check(ItemMeta metaOther, ItemMeta meta2) {
        switch (option) {
            case EXACT:
                return ((Damageable) metaOther).getDamage() == ((Damageable) meta2).getDamage();
            case IGNORE:
                ((Damageable) metaOther).setDamage(0);
                ((Damageable) meta2).setDamage(0);
                return true;
            case LOWER:
                return ((Damageable) metaOther).getDamage() < ((Damageable) meta2).getDamage();
            case HIGHER:
                return ((Damageable) metaOther).getDamage() > ((Damageable) meta2).getDamage();
        }
        return false;
    }
}
