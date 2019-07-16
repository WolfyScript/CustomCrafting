package me.wolfyscript.customcrafting.items.meta;

import me.wolfyscript.customcrafting.items.Meta;
import me.wolfyscript.customcrafting.items.MetaSettings;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class NameMeta extends Meta {

    public NameMeta(){
        super("name");
        setOption(MetaSettings.Option.EXACT);
        setAvailableOptions(MetaSettings.Option.EXACT, MetaSettings.Option.IGNORE);
    }

    @Override
    public boolean check(ItemMeta meta1, ItemMeta meta2) {
        if(option.equals(MetaSettings.Option.IGNORE)){
            meta1.setDisplayName(null);
            meta2.setDisplayName(null);
        }
        return true;
    }
}
