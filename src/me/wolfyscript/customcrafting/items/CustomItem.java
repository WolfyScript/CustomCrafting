package me.wolfyscript.customcrafting.items;

import me.wolfyscript.customcrafting.configs.custom_configs.ItemConfig;
import me.wolfyscript.utilities.api.WolfyUtilities;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class CustomItem extends ItemStack{

    private ItemConfig config;
    private String id;

    public CustomItem(ItemConfig config){
        super(config.getCustomItem());
        this.config = config;
        this.id = config.getId();
    }

    public CustomItem(ItemStack itemStack){
        super(itemStack);
        this.config = null;
        this.id = "";
    }

    public CustomItem(Material material){
        this(new ItemStack(material));
    }

    public String getId() {
        return id;
    }

    public boolean hasID(){
        return !id.isEmpty();
    }

    public ItemConfig getConfig() {
        return config;
    }

    public ItemStack getHiddenIDItem(){
        if(getType().equals(Material.AIR)){
            return new ItemStack(Material.AIR);
        }
        ItemStack idItem = new ItemStack(this.clone());
        ItemMeta idItemMeta = idItem.getItemMeta();
        idItemMeta.setDisplayName((idItemMeta.hasDisplayName() ? idItemMeta.getDisplayName() : WordUtils.capitalizeFully(idItem.getType().name().replace("_", " "))) + WolfyUtilities.hideString(";/id:"+getId()));
        idItem.setItemMeta(idItemMeta);
        return idItem;
    }

    public ItemStack getIDItem(){
        if(getType().equals(Material.AIR)){
           return new ItemStack(Material.AIR);
        }
        ItemStack idItem = new ItemStack(this.clone());
        if(!this.id.isEmpty()){
            ItemMeta idItemMeta = idItem.getItemMeta();
            if(idItemMeta.hasDisplayName() && !WolfyUtilities.unhideString(idItemMeta.getDisplayName()).endsWith(":id_item")){
                idItemMeta.setDisplayName(idItemMeta.getDisplayName()+ WolfyUtilities.hideString(":id_item"));
            }else{
                idItemMeta.setDisplayName(WolfyUtilities.hideString("%NO_NAME%")+"§r"+WordUtils.capitalizeFully(idItem.getType().name().replace("_", " ")) + WolfyUtilities.hideString(":id_item"));
            }
            List<String> lore = idItemMeta.hasLore() ? idItemMeta.getLore() : new ArrayList<>();
            lore.add("");
            lore.add("§7[§3§lID_ITEM§r§7]");
            lore.add("§3"+this.id);

            idItemMeta.setLore(lore);
            idItem.setItemMeta(idItemMeta);
        }
        return idItem;
    }



}
