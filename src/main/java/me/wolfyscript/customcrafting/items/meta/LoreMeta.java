package me.wolfyscript.customcrafting.items.meta;


import me.wolfyscript.customcrafting.items.Meta;
import me.wolfyscript.customcrafting.items.MetaSettings;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class LoreMeta extends Meta {

    public LoreMeta(){
        super("lore");
        setOption(MetaSettings.Option.EXACT);
        setAvailableOptions(MetaSettings.Option.EXACT, MetaSettings.Option.IGNORE);
    }

    @Override
    public boolean check(ItemMeta meta1, ItemMeta meta2) {
        if(option.equals(MetaSettings.Option.IGNORE)){
            meta1.setLore(new ArrayList<>());
            meta2.setLore(new ArrayList<>());
        }
        return true;
    }
}
