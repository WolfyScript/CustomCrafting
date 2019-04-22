package me.wolfyscript.customcrafting.recipes.craftrecipes;

import java.util.HashMap;

public class CheckResult {

    private HashMap<Integer[], Integer> amounts;
    private boolean allowed;

    public CheckResult(HashMap<Integer[], Integer> amounts, boolean allowed){
        this.allowed = allowed;
        this.amounts = amounts;
    }

    public HashMap<Integer[], Integer> getAmounts() {
        return amounts;
    }

    public boolean isAllowed() {
        return allowed;
    }
}
