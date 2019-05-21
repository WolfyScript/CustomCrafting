package me.wolfyscript.customcrafting.items;

import com.sun.istack.internal.NotNull;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.custom_configs.items.ItemConfig;
import me.wolfyscript.utilities.api.WolfyUtilities;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class CustomItem extends ItemStack implements Cloneable{

    private ItemConfig config;
    private String id;

    private int burnTime;
    private ArrayList<Material> allowedBlocks;

    private CustomItem replacement;

    public CustomItem(ItemConfig config){
        super(config.getCustomItem());
        this.config = config;
        this.id = config.getId();
        this.burnTime = config.getBurnTime();
        this.allowedBlocks = config.getAllowedBlocks();
        this.replacement = config.getReplacementItem();
    }

    public CustomItem(ItemStack itemStack){
        super(itemStack);
        this.config = null;
        this.id = "";
        this.burnTime = 0;
        this.allowedBlocks = new ArrayList<>();
        this.replacement = null;
    }

    public CustomItem(Material material){
        this(new ItemStack(material));
    }

    public String getId() {
        return id;
    }

    public boolean hasReplacement(){
        return replacement != null;
    }

    public CustomItem getReplacement() {
        return replacement;
    }

    public void setReplacement(CustomItem replacement) {
        this.replacement = replacement;
    }

    public boolean hasID(){
        return !id.isEmpty();
    }

    public boolean hasConfig(){
        return config != null;
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

    public int getBurnTime() {
        return burnTime;
    }

    public void setBurnTime(int burnTime) {
        this.burnTime = burnTime;
    }

    public ArrayList<Material> getAllowedBlocks() {
        return allowedBlocks;
    }

    @Override
    public boolean isSimilar(ItemStack stack) {
        if (stack == null){
            return false;
        } else if (stack == this) {
            return true;
        }else{
            return getType() == stack.getType() && this.getDurability() == stack.getDurability() && this.hasItemMeta() == stack.hasItemMeta() && (!this.hasItemMeta() || Bukkit.getItemFactory().equals(this.getItemMeta(), stack.getItemMeta()));
        }
    }

    @Override
    public CustomItem clone() {
        CustomItem customItem;
        if(hasConfig()){
            customItem = new CustomItem(getConfig());
        }else{
            customItem = new CustomItem(this);
        }
        return customItem;
    }
}
