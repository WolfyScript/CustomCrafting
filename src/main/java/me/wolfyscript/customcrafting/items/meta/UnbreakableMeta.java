package me.wolfyscript.customcrafting.items.meta;


import me.wolfyscript.customcrafting.items.Meta;
import me.wolfyscript.customcrafting.items.MetaSettings;
import org.bukkit.inventory.meta.ItemMeta;

public class UnbreakableMeta extends Meta {

    public UnbreakableMeta(){
        super("unbreakable");
        setOption(MetaSettings.Option.EXACT);
        setAvailableOptions(MetaSettings.Option.EXACT, MetaSettings.Option.IGNORE);
    }

    @Override
    public boolean check(ItemMeta meta1, ItemMeta meta2) {
        //TODO
        return true;
    }
}
