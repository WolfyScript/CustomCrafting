package me.wolfyscript.customcrafting.data.cache.items;

import me.wolfyscript.utilities.api.custom_items.CustomItem;
import org.bukkit.Material;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.io.Serializable;
import java.util.UUID;

public class Items implements Serializable {

    private static final long serialVersionUID = 420L;

    private int page;

    private ItemStack playerHeadSetting;

    private CustomItem item;
    private boolean recipeItem;
    private String id;
    private int craftSlot, variantSlot;
    private boolean saved;

    private EquipmentSlot attributeSlot;
    private AttributeModifier.Operation attribOperation;
    private double attribAmount;
    private String attributeUUID;
    private String attributeName;

    public Items() {
        this.page = 0;
        this.playerHeadSetting = new ItemStack(Material.AIR);

        this.item = new CustomItem(Material.AIR);
        this.recipeItem = false;
        this.id = "";
        this.saved = false;
        this.craftSlot = -1;
        this.variantSlot = -1;

        this.attributeSlot = null;
        this.attribOperation = AttributeModifier.Operation.ADD_NUMBER;
        this.attribAmount = 0.5;
        this.attributeUUID = "";
        this.attributeName = "";
    }

    public void setItem(boolean recipeItem, CustomItem customItem) {
        setItem(customItem);
        setRecipeItem(recipeItem);
        if (customItem.getId().isEmpty()) {
            setSaved(false);
        } else {
            setId(customItem.getId());
            setSaved(true);
        }
    }

    public void setVariant(int variantSlot, CustomItem customItem) {
        this.variantSlot = variantSlot;
        setItem(true, customItem);
    }

    public CustomItem getItem() {
        return item;
    }

    public void setItem(CustomItem item) {
        this.item = item;
    }

    public void setRecipeItem(boolean recipeItem) {
        this.recipeItem = recipeItem;
    }

    public boolean isRecipeItem() {
        return recipeItem;
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

    public EquipmentSlot getAttributeSlot() {
        return attributeSlot;
    }

    public boolean isAttributeSlot(EquipmentSlot equipmentSlot) {
        return getAttributeSlot() != null && getAttributeSlot().equals(equipmentSlot);
    }

    public void setAttributeSlot(EquipmentSlot attributeSlot) {
        this.attributeSlot = attributeSlot;
    }

    public AttributeModifier.Operation getAttribOperation() {
        return attribOperation;
    }

    public void setAttribOperation(AttributeModifier.Operation attribOperation) {
        this.attribOperation = attribOperation;
    }

    public double getAttribAmount() {
        return attribAmount;
    }

    public void setAttribAmount(double attribAmount) {
        this.attribAmount = attribAmount;
    }

    public String getAttributeUUID() {
        return attributeUUID;
    }

    public void setAttributeUUID(String attributeUUID) {
        this.attributeUUID = attributeUUID;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public AttributeModifier getAttributeModifier() {
        double amount = getAttribAmount();
        EquipmentSlot slot = getAttributeSlot();
        String name = getAttributeName().isEmpty() ? "customcrafting" : getAttributeName();
        UUID uuid = getAttributeUUID().isEmpty() ? UUID.randomUUID() : UUID.fromString(getAttributeUUID());
        AttributeModifier.Operation operation = getAttribOperation();
        return new AttributeModifier(uuid, name, amount, operation, slot);
    }

    public ItemStack getPlayerHeadSetting() {
        return playerHeadSetting;
    }

    public void setPlayerHeadSetting(ItemStack playerHeadSetting) {
        this.playerHeadSetting = playerHeadSetting;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getVariantSlot() {
        return variantSlot;
    }

    public void setVariantSlot(int variantSlot) {
        this.variantSlot = variantSlot;
    }
}
