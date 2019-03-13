package me.wolfyscript.customcrafting.data.cache;

import me.wolfyscript.customcrafting.items.CustomItem;
import org.bukkit.Material;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Furnace implements Serializable {

    private static final long serialVersionUID = 422L;

    private CustomItem source;
    private CustomItem result;

    private String extend;
    private List<String> overrides;

    private boolean advFurnace;
    private int cookingTime;
    private float experience;

    public Furnace(){
        this.source = new CustomItem(Material.AIR);
        this.result = new CustomItem(Material.AIR);

        this.experience = 0.2f;
        this.extend = "";
        this.overrides = new ArrayList<>();

        this.advFurnace = true;
        this.cookingTime = 60;

    }

    public void setSource(CustomItem source) {
        this.source = source;
    }

    public CustomItem getSource() {
        return source;
    }

    public CustomItem getResult() {
        return result;
    }

    public void setResult(CustomItem result) {
        this.result = result;
    }

    public String getExtend() {
        return extend;
    }

    public void setExtend(String extend) {
        this.extend = extend;
    }

    public List<String> getOverrides() {
        return overrides;
    }

    public void setOverrides(List<String> overrides) {
        this.overrides = overrides;
    }

    public boolean isAdvFurnace() {
        return advFurnace;
    }

    public void setAdvFurnace(boolean advFurnace) {
        this.advFurnace = advFurnace;
    }

    public int getCookingTime() {
        return cookingTime;
    }

    public void setCookingTime(int cookingTime) {
        this.cookingTime = cookingTime;
    }

    public float getExperience() {
        return experience;
    }

    public void setExperience(float experience) {
        this.experience = experience;
    }
}
