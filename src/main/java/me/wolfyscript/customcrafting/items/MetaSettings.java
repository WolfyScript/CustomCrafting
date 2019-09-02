package me.wolfyscript.customcrafting.items;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.data.PlayerCache;
import me.wolfyscript.customcrafting.items.meta.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.*;

public class MetaSettings {

    private AttributesModifiersMeta modifiersMeta = new AttributesModifiersMeta();
    private CustomModelDataMeta customModelDataMeta = new CustomModelDataMeta();
    private DamageMeta damageMeta = new DamageMeta();
    private EnchantMeta enchantMeta = new EnchantMeta();
    private FlagsMeta flagsMeta = new FlagsMeta();
    private LoreMeta loreMeta = new LoreMeta();
    private NameMeta nameMeta = new NameMeta();
    private PlayerHeadMeta playerHeadMeta = new PlayerHeadMeta();
    private PotionMeta potionMeta = new PotionMeta();
    private RepairCostMeta repairCostMeta = new RepairCostMeta();
    private UnbreakableMeta unbreakableMeta = new UnbreakableMeta();
    private CustomDamageMeta customDamageMeta = new CustomDamageMeta();
    private CustomDurabilityMeta customDurabilityMeta = new CustomDurabilityMeta();

    private HashMap<String, Meta> metas;

    public MetaSettings(String jsonString) {
        this();
        JSONParser parser = new JSONParser();
        JSONObject obj = null;
        if (!jsonString.isEmpty()) {
            try {
                obj = (JSONObject) parser.parse(jsonString);
            } catch (ParseException e) {
                CustomCrafting.getApi().sendConsoleWarning("Error getting JSONObject from String:");
                CustomCrafting.getApi().sendConsoleWarning("" + jsonString);
            }
        }
        if (obj != null) {
            Set<String> keys = obj.keySet();
            for (String key : keys) {
                String value = (String) obj.get(key);
                getMetaByID(key).parseFromJSON(value);
            }
        }
    }

    public MetaSettings() {
        List<Meta> list = Arrays.asList(modifiersMeta, customModelDataMeta, damageMeta, enchantMeta, flagsMeta, loreMeta, nameMeta, playerHeadMeta, potionMeta, repairCostMeta, unbreakableMeta, customDamageMeta, customDurabilityMeta);
        this.metas = new HashMap<>();
        for (Meta meta : list) {
            this.metas.put(meta.getId(), meta);
        }
    }

    public Meta getMetaByID(String id) {
        return metas.get(id);
    }

    public List<String> getMetas(){
        return new ArrayList<>(metas.keySet());
    }

    public AttributesModifiersMeta getModifiersMeta() {
        return modifiersMeta;
    }

    public void setModifiersMeta(AttributesModifiersMeta modifiersMeta) {
        this.modifiersMeta = modifiersMeta;
    }

    public CustomModelDataMeta getCustomModelDataMeta() {
        return customModelDataMeta;
    }

    public void setCustomModelDataMeta(CustomModelDataMeta customModelDataMeta) {
        this.customModelDataMeta = customModelDataMeta;
    }

    public DamageMeta getDamageMeta() {
        return damageMeta;
    }

    public void setDamageMeta(DamageMeta damageMeta) {
        this.damageMeta = damageMeta;
    }

    public EnchantMeta getEnchantMeta() {
        return enchantMeta;
    }

    public void setEnchantMeta(EnchantMeta enchantMeta) {
        this.enchantMeta = enchantMeta;
    }

    public FlagsMeta getFlagsMeta() {
        return flagsMeta;
    }

    public void setFlagsMeta(FlagsMeta flagsMeta) {
        this.flagsMeta = flagsMeta;
    }

    public LoreMeta getLoreMeta() {
        return loreMeta;
    }

    public void setLoreMeta(LoreMeta loreMeta) {
        this.loreMeta = loreMeta;
    }

    public NameMeta getNameMeta() {
        return nameMeta;
    }

    public void setNameMeta(NameMeta nameMeta) {
        this.nameMeta = nameMeta;
    }

    public PlayerHeadMeta getPlayerHeadMeta() {
        return playerHeadMeta;
    }

    public void setPlayerHeadMeta(PlayerHeadMeta playerHeadMeta) {
        this.playerHeadMeta = playerHeadMeta;
    }

    public PotionMeta getPotionMeta() {
        return potionMeta;
    }

    public void setPotionMeta(PotionMeta potionMeta) {
        this.potionMeta = potionMeta;
    }

    public RepairCostMeta getRepairCostMeta() {
        return repairCostMeta;
    }

    public void setRepairCostMeta(RepairCostMeta repairCostMeta) {
        this.repairCostMeta = repairCostMeta;
    }

    public UnbreakableMeta getUnbreakableMeta() {
        return unbreakableMeta;
    }

    public void setUnbreakableMeta(UnbreakableMeta unbreakableMeta) {
        this.unbreakableMeta = unbreakableMeta;
    }

    public CustomDamageMeta getCustomDamageMeta() {
        return customDamageMeta;
    }

    public void setCustomDamageMeta(CustomDamageMeta customDamageMeta) {
        this.customDamageMeta = customDamageMeta;
    }

    public CustomDurabilityMeta getCustomDurabilityMeta() {
        return customDurabilityMeta;
    }

    public void setCustomDurabilityMeta(CustomDurabilityMeta customDurabilityMeta) {
        this.customDurabilityMeta = customDurabilityMeta;
    }

    public Meta getMetaByCache(PlayerCache cache) {
        switch (cache.getSubSetting()) {
            case "attributes_modifiers":
                return getModifiersMeta();
            case "skull_setting":
                return getPlayerHeadMeta();
            case "item_name":
                return getNameMeta();
            case "item_lore":
                return getLoreMeta();
            case "item_flags":
                return getFlagsMeta();
            case "item_enchantments":
                return getEnchantMeta();
            case "potion_effects":
                return getPotionMeta();
            case "item_damage":
                return getDamageMeta();
            case "repair_cost":
                return getRepairCostMeta();
            case "custom_model_data":
                return getCustomModelDataMeta();
        }
        return null;
    }

    public boolean checkMeta(ItemMeta input, ItemMeta customItem) {
        for (Meta meta : metas.values()) {
            if (!meta.check(input, customItem)) {
                CustomCrafting.getApi().sendDebugMessage("          Meta: " + meta.getId());
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        HashMap<String, String> map = new HashMap<>();
        for (String id : metas.keySet()) {
            map.put(id, metas.get(id).toString());
        }
        JSONObject obj = new JSONObject(map);
        return obj.toString();
    }

    public enum Option {
        EXACT, IGNORE, HIGHER, LOWER
    }

}
