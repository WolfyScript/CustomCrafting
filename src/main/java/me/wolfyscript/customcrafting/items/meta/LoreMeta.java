package me.wolfyscript.customcrafting.items.meta;


import me.wolfyscript.customcrafting.items.Meta;
import me.wolfyscript.customcrafting.items.MetaSettings;
import me.wolfyscript.utilities.api.WolfyUtilities;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LoreMeta extends Meta {

    public LoreMeta() {
        super("lore");
        setOption(MetaSettings.Option.EXACT);
        setAvailableOptions(MetaSettings.Option.EXACT, MetaSettings.Option.IGNORE);
    }

    @Override
    public boolean check(ItemMeta meta1, ItemMeta meta2) {
        if (option.equals(MetaSettings.Option.IGNORE)) {
            meta1.setLore(new ArrayList<>());
            meta2.setLore(new ArrayList<>());
        }
        clearLore(meta1);
        clearLore(meta2);
        return true;
    }

    private ItemMeta clearLore(ItemMeta itemMeta) {
        if (itemMeta.hasLore()) {
            List<String> lore = itemMeta.getLore();
            Iterator<String> loreItr = lore.iterator();
            while (loreItr.hasNext()) {
                String line = WolfyUtilities.unhideString(loreItr.next());
                if (line.startsWith("durability_tag") || line.startsWith("itemSettings")) {
                    loreItr.remove();
                }
            }
            itemMeta.setLore(lore);
        }
        return itemMeta;
    }
}
