package me.wolfyscript.customcrafting.placeholderapi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.PlayerCache;
import org.bukkit.OfflinePlayer;

public class PlaceHolder extends PlaceholderExpansion {

    @Override
    public boolean canRegister() {
        return true;
    }

    /**
     * The placeholder identifier should go here.
     * <br>This is what tells PlaceholderAPI to call our onRequest
     * method to obtain a value if a placeholder starts with our
     * identifier.
     * <br>This must be unique and can not contain % or _
     *
     * @return The identifier in {@code %<identifier>_<value>%} as String.
     */
    @Override
    public String getIdentifier() {
        return "customcrafting";
    }

    @Override
    public String getAuthor() {
        return "WolfyScript";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public String onRequest(OfflinePlayer p, String params) {
        if(p != null){
            PlayerCache cache = CustomCrafting.getPlayerCache(p.getUniqueId());
            if(cache != null){
                if(params.equals("amount_crafted")){
                    return String.valueOf(cache.getAmountCrafted());
                }
                if(params.equals("amount_advanced_crafted")){
                    return String.valueOf(cache.getAmountAdvancedCrafted());
                }
                if(params.equals("amount_normal_crafted")){
                    return String.valueOf(cache.getAmountNormalCrafted());
                }
                //SPACE FOR MORE PLACEHOLDERS

            }
        }
        return null;
    }
}
