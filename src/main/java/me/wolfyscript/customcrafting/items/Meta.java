package me.wolfyscript.customcrafting.items;

import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public abstract class Meta {

    protected MetaSettings.Option option;

    private String id;

    private List<MetaSettings.Option> availableOptions;

    protected Meta(String id){
        this.id = id;
    }

    public MetaSettings.Option getOption(){
        return option;
    }

    public void setOption(MetaSettings.Option option){
        this.option = option;
    }

    public boolean isExact(){
        return option.equals(MetaSettings.Option.EXACT);
    }

    public List<MetaSettings.Option> getAvailableOptions(){
        return availableOptions;
    }

    protected void setAvailableOptions(MetaSettings.Option... options){
        if(options != null){
            availableOptions = Arrays.asList(options);
        }
    }

    public abstract boolean check(ItemMeta metaOther, ItemMeta meta);

    @Override
    public String toString(){
        return option.toString();
    }

    public void parseFromJSON(String value){
        this.option = MetaSettings.Option.valueOf(value);
    }

    public String getId() {
        return id;
    }
}
