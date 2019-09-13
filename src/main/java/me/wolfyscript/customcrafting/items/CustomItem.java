package me.wolfyscript.customcrafting.items;

import com.sun.istack.internal.Nullable;
import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.custom_configs.items.ItemConfig;
import me.wolfyscript.customcrafting.data.PlayerCache;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.utils.ItemUtils;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class CustomItem extends ItemStack implements Cloneable {

    private ItemConfig config;
    private String id;

    private String permission;
    private double rarityPercentage;

    private int burnTime;
    private ArrayList<Material> allowedBlocks;

    private boolean consumed;
    private CustomItem replacement;

    private int durabilityCost;
    private MetaSettings metaSettings;

    public CustomItem(ItemConfig config, boolean replace) {
        super(config.getCustomItem(replace));
        this.config = config;
        this.id = config.getId();
        this.burnTime = config.getBurnTime();
        this.allowedBlocks = config.getAllowedBlocks();
        this.replacement = config.getReplacementItem();
        this.durabilityCost = config.getDurabilityCost();
        this.consumed = config.isConsumed();
        this.metaSettings = config.getMetaSettings();
        this.permission = config.getPermission();
        this.rarityPercentage = config.getRarityPercentage();
    }

    public CustomItem(ItemConfig config) {
        this(config, false);
    }

    public CustomItem(ItemStack itemStack) {
        super(itemStack);
        this.config = null;
        this.id = "";
        this.burnTime = 0;
        this.allowedBlocks = new ArrayList<>();
        this.replacement = null;
        this.durabilityCost = 0;
        this.consumed = true;
        this.metaSettings = new MetaSettings();
        this.permission = "";
        this.rarityPercentage = 1.0d;
    }

    public CustomItem(Material material) {
        this(new ItemStack(material));
    }

    public String getId() {
        return id;
    }

    public CustomItem getRealItem() {
        if (hasConfig()) {
            CustomItem customItem = new CustomItem(config, true);
            if (customItem.getType().equals(this.getType())) {
                customItem.setAmount(this.getAmount());
            }
            return customItem;
        }
        return clone();
    }

    public boolean hasReplacement() {
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

    public boolean isConsumed() {
        return consumed;
    }

    public void setConsumed(boolean consumed) {
        this.consumed = consumed;
    }

    public MetaSettings getMetaSettings() {
        return metaSettings;
    }

    public void setMetaSettings(MetaSettings metaSettings) {
        this.metaSettings = metaSettings;
    }

    public boolean hasID() {
        return !id.isEmpty();
    }

    public boolean hasConfig() {
        return config != null;
    }

    public ItemConfig getConfig() {
        return config;
    }

    public ItemStack getIDItem(int amount) {
        if (getType().equals(Material.AIR)) {
            return new ItemStack(Material.AIR);
        }
        ItemStack idItem = new ItemStack(this.clone());
        if (!this.id.isEmpty()) {
            ItemMeta idItemMeta = idItem.getItemMeta();
            if (idItemMeta.hasDisplayName() && !WolfyUtilities.unhideString(idItemMeta.getDisplayName()).endsWith(":id_item")) {
                idItemMeta.setDisplayName(idItemMeta.getDisplayName() + WolfyUtilities.hideString(":id_item"));
            } else {
                idItemMeta.setDisplayName(WolfyUtilities.hideString("%NO_NAME%") + "§r" + WordUtils.capitalizeFully(idItem.getType().name().replace("_", " ")) + WolfyUtilities.hideString(":id_item"));
            }
            List<String> lore = idItemMeta.hasLore() ? idItemMeta.getLore() : new ArrayList<>();
            lore.add("");
            lore.add("§7[§3§lID_ITEM§r§7]");
            lore.add("§3" + this.id);
            idItemMeta.setLore(lore);
            idItem.setItemMeta(idItemMeta);
        }
        idItem.setAmount(amount);
        return idItem;
    }

    public ItemStack getIDItem() {
        return getIDItem(this.getAmount());
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
        return isSimilar(stack, true);
    }

    public boolean isSimilar(ItemStack stack, boolean exactMeta) {
        CustomCrafting.getApi().sendDebugMessage("Compare: ");
        CustomCrafting.getApi().sendDebugMessage("  This: " + this.toString());
        CustomCrafting.getApi().sendDebugMessage("  Stack: " + stack);
        if (stack == null) {
            CustomCrafting.getApi().sendDebugMessage("      Shouldn't!");
            return false;
        } else if (stack == this) {
            CustomCrafting.getApi().sendDebugMessage("      Valid!");
            return true;
        } else if (stack.getType().equals(this.getType()) && stack.getAmount() >= this.getAmount()) {
            CustomCrafting.getApi().sendDebugMessage("      Check Item!");
            CustomCrafting.getApi().sendDebugMessage("      Exact: " + exactMeta + " metaData:" + this.hasItemMeta());
            if (exactMeta || this.hasItemMeta()) {
                CustomCrafting.getApi().sendDebugMessage("          Check Meta!");
                CustomCrafting.getApi().sendDebugMessage("          Meta CustomItem: " + this.getItemMeta());
                CustomCrafting.getApi().sendDebugMessage("          Meta Input: " + stack.getItemMeta());
                if (this.hasItemMeta() && !stack.hasItemMeta()) {
                    return false;
                } else if (!this.hasItemMeta() && stack.hasItemMeta()) {
                    CustomCrafting.getApi().sendDebugMessage("          No Meta");
                    return false;
                }
                ItemMeta stackMeta = stack.getItemMeta();
                ItemMeta currentMeta = this.getItemMeta();
                if (!getMetaSettings().checkMeta(stackMeta, currentMeta)) {
                    CustomCrafting.getApi().sendDebugMessage("          Wrong meta");
                    return false;
                }
                return stackMeta.equals(currentMeta);
            }
            CustomCrafting.getApi().sendDebugMessage("          Valid!");
            return true;
        }
        return false;
    }

    @Override
    public CustomItem clone() {
        CustomItem customItem;
        if (hasConfig()) {
            customItem = new CustomItem(getConfig());
        } else {
            customItem = new CustomItem(this);
        }
        return customItem;
    }

    /*
    This will call the super.clone() method to get the ItemStack.
    All CustomItem variables will get lost!
     */
    public ItemStack getAsItemStack() {
        return super.clone();
    }

    /*
    CustomItem static methods
     */
    public static CustomItem getByItemStack(ItemStack itemStack) {
        String id = "";
        ItemStack clearedItem = itemStack.clone();
        if (isIDItem(itemStack) && itemStack.getItemMeta().hasLore()) {
            ItemMeta clearedMeta = clearedItem.getItemMeta();
            List<String> clearedLore = clearedMeta.getLore();
            List<String> lore = itemStack.getItemMeta().getLore();
            for (int i = 0; i < lore.size(); i++) {
                String row = lore.get(i);
                if (row.startsWith("§7[§3§lID_ITEM§r§7]")) {
                    id = lore.get(i + 1).substring("§3".length());
                    clearedLore.remove(i - 1);
                    clearedLore.remove(i);
                    clearedLore.remove(row);
                }
            }
            clearedMeta.setLore(clearedLore);
            if (WolfyUtilities.unhideString(clearedMeta.getDisplayName()).contains("%NO_NAME%")) {
                clearedMeta.setDisplayName(null);
            } else {
                clearedMeta.setDisplayName(clearedMeta.getDisplayName().replace(WolfyUtilities.hideString(":id_item"), ""));
            }
            clearedItem.setItemMeta(clearedMeta);
            if (id.isEmpty()) {
                return new CustomItem(clearedItem);
            }
        }
        CustomItem customItem;
        if (id.isEmpty()) {
            customItem = new CustomItem(clearedItem);
        }else{
            customItem = CustomCrafting.getRecipeHandler().getCustomItem(id);
        }
        if (clearedItem.getAmount() != customItem.getAmount()) {
            customItem.setAmount(clearedItem.getAmount());
        }
        return customItem;
    }

    private static boolean isIDItem(ItemStack itemStack) {
        if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName()) {
            String name = WolfyUtilities.unhideString(itemStack.getItemMeta().getDisplayName());
            return name.endsWith(":id_item");
        }
        return false;
    }

    public static void saveItem(PlayerCache cache, String id, CustomItem customItem) {
        ItemConfig config;
        if (CustomCrafting.hasDataBaseHandler()) {
            config = new ItemConfig(CustomCrafting.getApi().getConfigAPI(), id.split(":")[0], id.split(":")[1]);
        } else {
            config = new ItemConfig(CustomCrafting.getApi().getConfigAPI(), id.split(":")[0], id.split(":")[1], "json");
        }
        config.setCustomItem(customItem);
        if (CustomCrafting.getRecipeHandler().getCustomItem(id) != null) {
            CustomCrafting.getRecipeHandler().removeCustomItem(id);
        }
        if (CustomCrafting.hasDataBaseHandler()) {
            CustomCrafting.getDataBaseHandler().updateItem(config);
        } else {
            config.reload(CustomCrafting.getConfigHandler().getConfig().isPrettyPrinting());
        }
        CustomItem customItem1 = new CustomItem(config);
        cache.getItems().setItem(customItem1);
        CustomCrafting.getRecipeHandler().addCustomItem(customItem1);
    }

    public void consumeUnstackableItem(ItemStack input) {
        if (this.hasConfig()) {
            if (this.isConsumed()) {
                input.setAmount(0);
            }
            if (this.hasReplacement()) {
                ItemStack replace = this.getReplacement();
                input.setType(replace.getType());
                input.setItemMeta(replace.getItemMeta());
                input.setData(replace.getData());
                input.setAmount(replace.getAmount());
            } else if (this.getDurabilityCost() != 0) {
                if (ItemUtils.hasCustomDurability(input)) {
                    ItemUtils.setDamage(input, ItemUtils.getDamage(input) + this.getDurabilityCost());
                } else {
                    ItemMeta itemMeta = input.getItemMeta();
                    if (itemMeta instanceof Damageable) {
                        ((Damageable) itemMeta).setDamage(((Damageable) itemMeta).getDamage() + this.getDurabilityCost());
                    }
                    input.setItemMeta(itemMeta);
                }
            }
        } else {
            if (input.getType().equals(Material.LAVA_BUCKET) || input.getType().equals(Material.WATER_BUCKET) || input.getType().equals(Material.MILK_BUCKET)) {
                input.setType(Material.BUCKET);
            } else {
                input.setAmount(0);
            }
        }
    }

    public boolean hasPermission(){
        return !permission.isEmpty();
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public double getRarityPercentage() {
        return rarityPercentage;
    }

    public void setRarityPercentage(double rarityPercentage) {
        this.rarityPercentage = rarityPercentage;
    }
}
