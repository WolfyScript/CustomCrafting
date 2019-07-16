package me.wolfyscript.customcrafting.items.meta;


import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.items.Meta;
import me.wolfyscript.customcrafting.items.MetaSettings;
import me.wolfyscript.utilities.api.WolfyUtilities;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class CustomModelDataMeta extends Meta {

    public CustomModelDataMeta(){
        super("customModelData");
        setOption(MetaSettings.Option.EXACT);
        setAvailableOptions(MetaSettings.Option.EXACT, MetaSettings.Option.IGNORE, MetaSettings.Option.HIGHER, MetaSettings.Option.LOWER);
    }

    @Override
    public boolean check(ItemMeta meta1, ItemMeta meta2) {
        if(WolfyUtilities.hasVillagePillageUpdate()){
            switch (option){
                case IGNORE:
                    meta1.setCustomModelData(0);
                    meta2.setCustomModelData(0);
                    return true;
                case LOWER:
                    return meta1.getCustomModelData() < meta2.getCustomModelData();
                case HIGHER:
                    return meta1.getCustomModelData() > meta2.getCustomModelData();
            }
        }
        return true;
    }
}
