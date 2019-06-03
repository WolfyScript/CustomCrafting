package me.wolfyscript.customcrafting.items;

import com.sun.istack.internal.NotNull;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.custom_configs.items.ItemConfig;
import me.wolfyscript.utilities.api.WolfyUtilities;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nullable;
import java.util.*;

public class CustomItem extends ItemStack implements Cloneable{

    private ItemConfig config;
    private String id;

    private int burnTime;
    private ArrayList<Material> allowedBlocks;

    private CustomItem replacement;
    private int durabilityCost;

    public CustomItem(ItemConfig config){
        super(config.getCustomItem());
        this.config = config;
        this.id = config.getId();
        this.burnTime = config.getBurnTime();
        this.allowedBlocks = config.getAllowedBlocks();
        this.replacement = config.getReplacementItem();
        this.durabilityCost = config.getDurabilityCost();
    }

    public CustomItem(ItemStack itemStack){
        super(itemStack);
        this.config = null;
        this.id = "";
        this.burnTime = 0;
        this.allowedBlocks = new ArrayList<>();
        this.replacement = null;
        this.durabilityCost = 0;
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

    @Nullable
    public CustomItem getReplacement() {
        return replacement != null ? replacement.clone() : null;
    }

    public void setReplacement(@Nullable CustomItem replacement) {
        this.replacement = replacement;
    }

    public int getDurabilityCost() {
        return durabilityCost;
    }

    public void setDurabilityCost(int durabilityCost) {
        this.durabilityCost = durabilityCost;
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

    public ItemStack getIDItem(int amount){
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
        idItem.setAmount(amount);
        return idItem;
    }

    public ItemStack getIDItem(){
        return getIDItem(1);
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
            if(getDurabilityCost() != 0 && stack.hasItemMeta()){
                if (stack.getItemMeta() instanceof Damageable) {
                    ItemStack copy = stack.clone();
                    ItemMeta copyMeta = copy.getItemMeta();
                    ((Damageable) copyMeta).setDamage(((Damageable)this.getItemMeta()).getDamage());
                    copy.setItemMeta(copyMeta);
                    return getType() == copy.getType() && this.hasItemMeta() == copy.hasItemMeta() && (!this.hasItemMeta() || Bukkit.getItemFactory().equals(this.getItemMeta(), copyMeta));
                }
            }else{
                return getType() == stack.getType() && this.hasItemMeta() == stack.hasItemMeta() && (!this.hasItemMeta() || Bukkit.getItemFactory().equals(this.getItemMeta(), stack.getItemMeta()));
            }
            return false;
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
