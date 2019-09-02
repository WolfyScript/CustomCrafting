package me.wolfyscript.customcrafting.items.meta;

import me.wolfyscript.customcrafting.items.Meta;
import me.wolfyscript.customcrafting.items.MetaSettings;
import me.wolfyscript.utilities.api.utils.ItemUtils;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class CustomDurabilityMeta extends Meta {

    public CustomDurabilityMeta() {
        super("custom_durability");
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
                    return ItemUtils.getCustomDurability(metaOther) == ItemUtils.getCustomDurability(meta);
                case IGNORE:
                    ItemUtils.setCustomDurability(metaOther, 0);
                    ItemUtils.setCustomDurability(meta, 0);
                    ((Damageable) metaOther).setDamage(0);
                    ((Damageable) meta).setDamage(0);
                    return true;
                case LOWER:
                    return ItemUtils.getCustomDurability(metaOther) < ItemUtils.getCustomDurability(meta);
                case HIGHER:
                    return ItemUtils.getCustomDurability(metaOther) > ItemUtils.getCustomDurability(meta);
            }
        } else return meta0 || !meta1;
        return true;
    }
}
