package me.wolfyscript.customcrafting.utils.recipe_item.extension;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public abstract class ResultExtension {


    public abstract void onLocation(Location location);

    public abstract void onPlayer(Player player);


}
