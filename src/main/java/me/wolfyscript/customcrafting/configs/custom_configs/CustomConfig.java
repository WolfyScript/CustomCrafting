package me.wolfyscript.customcrafting.configs.custom_configs;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.items.CustomItem;
import me.wolfyscript.customcrafting.recipes.RecipePriority;
import me.wolfyscript.utilities.api.config.Config;
import me.wolfyscript.utilities.api.config.ConfigAPI;
import org.bukkit.inventory.ItemStack;

public class CustomConfig extends Config {

    private String folder;
    private String id;
    private String type;

    public CustomConfig(ConfigAPI configAPI, String folder, String type, String name, String defaultName, String fileType) {
        this(configAPI, folder, type, name, "me/wolfyscript/customcrafting/configs/custom_configs/" + type, defaultName, false, fileType);
    }

    public CustomConfig(ConfigAPI configAPI, String folder, String type, String name, String defaultName, boolean override, String fileType) {
        this(configAPI, folder, type, name, "me/wolfyscript/customcrafting/configs/custom_configs/" + type, defaultName, override, fileType);
    }

    public CustomConfig(ConfigAPI configAPI, String folder, String type, String name, String defaultPath, String defaultName, boolean override, String fileType) {
        super(configAPI, configAPI.getApi().getPlugin().getDataFolder()+"/recipes/"+folder+"/"+type, name, defaultPath, defaultName, fileType, override);
        this.folder = folder;
        this.id = folder + ":" + name;
        this.type = type;
        if(getType().equals(Type.YAML)){
            setSaveAfterValueSet(true);
        }
        setPathSeparator('.');
    }

    @Override
    public void reload() {
        super.reload(CustomCrafting.getConfigHandler().getConfig().isPrettyPrinting());
    }

    public String getFolder() {
        return folder;
    }

    public String getId() {
        return id;
    }

    public String getConfigType() {
        return type;
    }

    public void setGroup(String group) {
        set("group", group);
    }

    public String getGroup() {
        return getString("group");
    }

    public void setExactMeta(boolean exactMeta) {
        set("exactItemMeta", exactMeta);
    }

    public boolean isExactMeta() {
        return getBoolean("exactItemMeta");
    }

    public void saveCustomItem(String path, CustomItem customItem) {
        CustomCrafting.getApi().sendDebugMessage("Saving Item: " + customItem);
        if (customItem != null) {
            if (!customItem.getId().isEmpty() && !customItem.getId().equals("NULL")) {
                set(path + ".item_key", customItem.getId());
                set(path + ".custom_amount", customItem.getAmount() != CustomCrafting.getRecipeHandler().getCustomItem(customItem.getId()).getAmount() ? customItem.getAmount() : 0);
            } else {
                setItem(path + ".item", new ItemStack(customItem));
            }
        }
    }

    public CustomItem getCustomItem(String path) {
        String id = getString(path + ".item_key");
        if (id != null && !id.isEmpty()) {
            CustomItem customItem = CustomCrafting.getRecipeHandler().getCustomItem(id);
            if (get(path + ".custom_amount") != null) {
                int i = getInt(path + ".custom_amount");
                if (i != 0) {
                    customItem.setAmount(i);
                }
            }
            return customItem;
        }
        return new CustomItem(getItem(path + ".item"));
    }

    public RecipePriority getPriority() {
        try {
            return RecipePriority.valueOf(getString("priority"));
        } catch (IllegalArgumentException e) {
            return RecipePriority.NORMAL;
        }
    }

    public void setPriority(RecipePriority recipePriority) {
        set("priority", recipePriority.name());
    }
}
