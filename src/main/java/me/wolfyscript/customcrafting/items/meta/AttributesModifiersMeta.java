package me.wolfyscript.customcrafting.items.meta;


import com.google.common.collect.Multimap;
import me.wolfyscript.customcrafting.items.Meta;
import me.wolfyscript.customcrafting.items.MetaSettings;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.meta.ItemMeta;

public class AttributesModifiersMeta extends Meta {

    public AttributesModifiersMeta(){
        super("attributes_modifiers");
        setOption(MetaSettings.Option.EXACT);
        setAvailableOptions(MetaSettings.Option.EXACT, MetaSettings.Option.IGNORE);
    }

    @Override
    public boolean check(ItemMeta meta1, ItemMeta meta2) {
        if(option.equals(MetaSettings.Option.IGNORE)){
            if(meta1.hasAttributeModifiers()){
                Multimap<Attribute, AttributeModifier> modifiers = meta1.getAttributeModifiers();
                modifiers.keySet().forEach(meta1::removeAttributeModifier);
            }
            if(meta2.hasAttributeModifiers()){
                Multimap<Attribute, AttributeModifier> modifiers = meta2.getAttributeModifiers();
                modifiers.keySet().forEach(meta2::removeAttributeModifier);
            }
        }
        return true;
    }
}
