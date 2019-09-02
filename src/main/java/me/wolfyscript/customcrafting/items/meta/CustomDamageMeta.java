package me.wolfyscript.customcrafting.items.meta;

import me.wolfyscript.customcrafting.items.Meta;
import me.wolfyscript.customcrafting.items.MetaSettings;
import me.wolfyscript.utilities.api.utils.ItemUtils;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class CustomDamageMeta extends Meta {

    public CustomDamageMeta() {
        super("custom_damage");
        setOption(MetaSettings.Option.EXACT);
        setAvailableOptions(MetaSettings.Option.EXACT, MetaSettings.Option.IGNORE, MetaSettings.Option.HIGHER, MetaSettings.Option.LOWER);
    }

    @Override
    public boolean check(ItemMeta metaOther, ItemMeta meta) {
        boolean meta0 = ItemUtils.hasCustomDurability(meta);
        boolean meta1 = ItemUtils.hasCustomDurability(metaOther);
        if (meta0 && meta1) {
            switch (option) {
                case EXACT:
                    return ItemUtils.getDamage(metaOther) == ItemUtils.getDamage(meta);
                case IGNORE:
                    ItemUtils.setDamage(metaOther, 0);
                    ItemUtils.setDamage(meta, 0);
                    ((Damageable) metaOther).setDamage(0);
                    ((Damageable) meta).setDamage(0);
                    return true;
                case LOWER:
                    return ItemUtils.getDamage(metaOther) < ItemUtils.getDamage(meta);
                case HIGHER:
                    return ItemUtils.getDamage(metaOther) > ItemUtils.getDamage(meta);
            }
        } else return meta0 || !meta1;
        return true;
    }
}
