package me.wolfyscript.customcrafting.data.cache;

import me.wolfyscript.customcrafting.items.CustomItem;
import org.bukkit.Material;

import java.io.Serializable;

public class Items implements Serializable {

    private static final long serialVersionUID = 420L;

    private CustomItem item;
    private String type;
    private String id;
    private int craftSlot;
    private boolean saved;

    public Items(){
        this.item = new CustomItem(Material.AIR);
        this.type = "";
        this.id = "";
        this.saved = false;
        this.craftSlot = -1;
    }

    public void setItem(String type, CustomItem customItem){
        setItem(customItem);
        setType(type);
        if(customItem.getId().isEmpty()){
            setSaved(false);
        }else{
            setId(customItem.getId());
            setSaved(true);
        }
    }

    public void setSource(CustomItem customItem){
        setItem("source", customItem);
    }

    public void setResult(CustomItem customItem){
        setItem("result", customItem);
    }

    public void setIngredient(int slot, CustomItem customItem){
        setItem("ingredient", customItem);
        setCraftSlot(slot);
    }

    public CustomItem getItem() {
        return item;
    }

    public void setItem(CustomItem item) {
        this.item = item;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isSaved() {
        return saved;
    }

    public void setSaved(boolean saved) {
        this.saved = saved;
    }

    public int getCraftSlot() {
        return craftSlot;
    }

    public void setCraftSlot(int craftSlot) {
        this.craftSlot = craftSlot;
    }

    public enum Type{

    }
}
