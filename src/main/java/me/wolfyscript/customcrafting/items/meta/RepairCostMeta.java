package me.wolfyscript.customcrafting.items.meta;


import me.wolfyscript.customcrafting.items.Meta;
import me.wolfyscript.customcrafting.items.MetaSettings;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;

public class RepairCostMeta extends Meta {

    public RepairCostMeta(){
        super("repairCost");
        setOption(MetaSettings.Option.EXACT);
        setAvailableOptions(MetaSettings.Option.EXACT, MetaSettings.Option.IGNORE, MetaSettings.Option.HIGHER, MetaSettings.Option.LOWER);
    }

    @Override
    public boolean check(ItemMeta meta1, ItemMeta meta2) {
        if(meta1 instanceof Repairable && meta2 instanceof Repairable){
            switch (option){
                case IGNORE:
                    ((Repairable) meta1).setRepairCost(0);
                    ((Repairable) meta2).setRepairCost(0);
                    return true;
                case LOWER:
                    return ((Repairable) meta1).getRepairCost() < ((Repairable) meta2).getRepairCost();
                case HIGHER:
                    return ((Repairable) meta1).getRepairCost() > ((Repairable) meta2).getRepairCost();
            }
        }
        return true;
    }
}
