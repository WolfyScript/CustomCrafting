package me.wolfyscript.customcrafting.utils;

import me.wolfyscript.customcrafting.configs.custom_data.CauldronData;
import me.wolfyscript.customcrafting.configs.custom_data.EliteWorkbenchData;
import me.wolfyscript.customcrafting.configs.custom_data.KnowledgeBookData;
import me.wolfyscript.utilities.api.custom_items.CustomItem;

public class WolfyUtilitiesData {

    public static void initCustomData() {
        CustomItem.registerCustomData(new EliteWorkbenchData());
        CustomItem.registerCustomData(new KnowledgeBookData());
        CustomItem.registerCustomData(new CauldronData());
    }
}
