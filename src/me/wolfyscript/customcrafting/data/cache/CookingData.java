package me.wolfyscript.customcrafting.data.cache;

import me.wolfyscript.customcrafting.items.CustomItem;
import me.wolfyscript.customcrafting.recipes.RecipePriority;
import org.bukkit.Material;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class CookingData {

    private boolean exactMeta;
    private RecipePriority priority;

    private CustomItem source;
    private CustomItem result;

    private boolean advFurnace;
    private int cookingTime;
    private float experience;

    public CookingData(){
        this.source = new CustomItem(Material.AIR);
        this.result = new CustomItem(Material.AIR);

        this.experience = 0.2f;

        this.advFurnace = true;
        this.cookingTime = 60;

        this.exactMeta = true;
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

    public boolean isExactMeta() {
        return exactMeta;
    }

    public void setExactMeta(boolean exactMeta) {
        this.exactMeta = exactMeta;
    }
}
