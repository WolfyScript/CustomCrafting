package me.wolfyscript.customcrafting.items.meta;


import me.wolfyscript.customcrafting.items.Meta;
import me.wolfyscript.customcrafting.items.MetaSettings;
import org.bukkit.inventory.meta.ItemMeta;

public class EnchantMeta extends Meta {

    public EnchantMeta() {
        super("enchant");
        setOption(MetaSettings.Option.EXACT);
        setAvailableOptions(MetaSettings.Option.EXACT, MetaSettings.Option.IGNORE);
    }

    @Override
    public boolean check(ItemMeta meta1, ItemMeta meta2) {
        if (option.equals(MetaSettings.Option.IGNORE)) {
            if (meta1.hasEnchants()) {
                meta1.getEnchants().keySet().forEach(meta1::removeEnchant);
            }
            if (meta2.hasEnchants()) {
                meta2.getEnchants().keySet().forEach(meta2::removeEnchant);
            }
        }
        return true;
    }
}
