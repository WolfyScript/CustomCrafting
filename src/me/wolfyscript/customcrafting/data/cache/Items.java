package me.wolfyscript.customcrafting.data.cache;

import me.wolfyscript.customcrafting.items.CustomItem;
import org.bukkit.Material;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Items implements Serializable {

    private static final long serialVersionUID = 420L;

    private int page;

    private ItemStack skullSetting;

    private CustomItem item;
    private String type;
    private String id;
    private int craftSlot;
    private boolean saved;

    private EquipmentSlot attributeSlot;
    private AttributeModifier.Operation attribOperation;
    private double attribAmount;
    private String attributeUUID;
    private String attributeName;

    private CustomItem variantItem;

    public Items(){
        this.page = 0;
        this.skullSetting = new ItemStack(Material.AIR);

        this.item = new CustomItem(Material.AIR);
        this.type = "";
        this.id = "";
        this.saved = false;
        this.craftSlot = -1;

        this.attributeSlot = null;
        this.attribOperation = AttributeModifier.Operation.ADD_NUMBER;
        this.attribAmount = 0.5;
        this.attributeUUID = "";
        this.attributeName = "";

        this.variantItem = new CustomItem(Material.AIR);
    }

    public void setItem(String type, CustomItem customItem){
        setItem(customItem);
        setType(type);
        if(customItem.getId().isEmpty()){
            setSaved(false);
        }else{
            setId(customItem.getId());
            setSaved(true);
        }
    }

    public void setSource(CustomItem customItem){
        setItem("source", customItem);
    }

    public void setResult(CustomItem customItem){
        setItem("result", customItem);
    }

    public void setIngredient(int slot, CustomItem customItem){
        setItem("ingredient", customItem);
        setCraftSlot(slot);
    }

    public CustomItem getItem() {
        return item;
    }

    public void setItem(CustomItem item) {
        this.item = item;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public AttributeModifier getAttributeModifier(){
        double amount = getAttribAmount();
        EquipmentSlot slot = getAttributeSlot();
        String name = getAttributeName().isEmpty() ? "customcrafting" : getAttributeName();
        UUID uuid = getAttributeUUID().isEmpty() ? UUID.randomUUID() : UUID.fromString(getAttributeUUID());
        AttributeModifier.Operation operation = getAttribOperation();
        return new AttributeModifier(uuid, name, amount, operation, slot);

    }

    public ItemStack getSkullSetting() {
        return skullSetting;
    }

    public void setSkullSetting(ItemStack skullSetting) {
        this.skullSetting = skullSetting;
    }

    public CustomItem getVariantItem() {
        return variantItem;
    }

    public void setVariantItem(CustomItem variantItem) {
        this.variantItem = variantItem;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }
}
